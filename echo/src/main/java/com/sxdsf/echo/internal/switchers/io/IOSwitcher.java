package com.sxdsf.echo.internal.switchers.io;

import com.sxdsf.echo.Switcher;
import com.sxdsf.echo.casts.CompositeCast;
import com.sxdsf.echo.internal.switchers.EchoThreadFactory;
import com.sxdsf.echo.internal.switchers.SwitcherLifecycle;
import com.sxdsf.echo.internal.switchers.newthread.NewThreadWorker;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * com.sxdsf.echo.internal.switchers.io.IOSwitcher
 *
 * @author 孙博闻
 * @date 2016/10/27 23:48
 * @desc IO线程的线程切换者
 */

public class IOSwitcher extends Switcher implements SwitcherLifecycle {

    private final ThreadFactory mThreadFactory;

    private static final long KEEP_ALIVE_TIME = 60;
    private static final TimeUnit KEEP_ALIVE_UNIT = TimeUnit.SECONDS;

    private static final ThreadWorker SHUTDOWN_THREAD_WORKER;

    private static final CachedWorkerPool NONE;

    private final AtomicReference<CachedWorkerPool> mPool;

    static {
        SHUTDOWN_THREAD_WORKER = new ThreadWorker(EchoThreadFactory.NONE);
        SHUTDOWN_THREAD_WORKER.unReceive();

        NONE = new CachedWorkerPool(null, 0, null);
        NONE.shutdown();
    }

    public IOSwitcher(ThreadFactory threadFactory) {
        mThreadFactory = threadFactory;
        mPool = new AtomicReference<>(NONE);
        start();
    }

    @Override
    public Worker createWorker() {
        return new IOWorker(mPool.get());
    }

    static final class CachedWorkerPool {
        private final ThreadFactory mThreadFactory;
        private final long mKeepAliveTime;
        private final ConcurrentLinkedQueue<ThreadWorker> mExpiringWorkerQueue;
        private final CompositeCast mAllWorkers;
        private final ScheduledExecutorService mEvictorService;
        private final Future<?> mEvictorTask;

        CachedWorkerPool(final ThreadFactory threadFactory, long keepAliveTime, TimeUnit unit) {
            mThreadFactory = threadFactory;
            mKeepAliveTime = unit != null ? unit.toNanos(keepAliveTime) : 0L;
            mExpiringWorkerQueue = new ConcurrentLinkedQueue<>();
            mAllWorkers = new CompositeCast();

            ScheduledExecutorService evictor = null;
            Future<?> task = null;
            if (unit != null) {
                evictor = Executors.newScheduledThreadPool(1, new ThreadFactory() {
                    @Override
                    public Thread newThread(Runnable r) {
                        Thread thread = threadFactory.newThread(r);
                        thread.setName(thread.getName() + " (Evictor)");
                        return thread;
                    }
                });
                task = evictor.scheduleWithFixedDelay(
                        new Runnable() {
                            @Override
                            public void run() {
                                evictExpiredWorkers();
                            }
                        }, this.mKeepAliveTime, this.mKeepAliveTime, TimeUnit.NANOSECONDS
                );
            }
            mEvictorService = evictor;
            mEvictorTask = task;
        }

        ThreadWorker get() {
            if (mAllWorkers.isUnReceived()) {
                return SHUTDOWN_THREAD_WORKER;
            }
            while (!mExpiringWorkerQueue.isEmpty()) {
                ThreadWorker threadWorker = mExpiringWorkerQueue.poll();
                if (threadWorker != null) {
                    return threadWorker;
                }
            }
            ThreadWorker w = new ThreadWorker(mThreadFactory);
            mAllWorkers.add(w);
            return w;
        }

        void release(ThreadWorker threadWorker) {
            threadWorker.setExpirationTime(now() + mKeepAliveTime);
            mExpiringWorkerQueue.offer(threadWorker);
        }

        void evictExpiredWorkers() {
            if (!mExpiringWorkerQueue.isEmpty()) {
                long currentTimestamp = now();

                for (ThreadWorker threadWorker : mExpiringWorkerQueue) {
                    if (threadWorker.getExpirationTime() <= currentTimestamp) {
                        if (mExpiringWorkerQueue.remove(threadWorker)) {
                            mAllWorkers.remove(threadWorker);
                        }
                    } else {
                        break;
                    }
                }
            }
        }

        long now() {
            return System.nanoTime();
        }

        void shutdown() {
            try {
                if (mEvictorTask != null) {
                    mEvictorTask.cancel(true);
                }
                if (mEvictorService != null) {
                    mEvictorService.shutdownNow();
                }
            } finally {
                mAllWorkers.unReceive();
            }
        }
    }

    @Override
    public void start() {
        CachedWorkerPool update =
                new CachedWorkerPool(mThreadFactory, KEEP_ALIVE_TIME, KEEP_ALIVE_UNIT);
        if (!mPool.compareAndSet(NONE, update)) {
            update.shutdown();
        }
    }

    @Override
    public void shutdown() {
        for (; ; ) {
            CachedWorkerPool curr = mPool.get();
            if (curr == NONE) {
                return;
            }
            if (mPool.compareAndSet(curr, NONE)) {
                curr.shutdown();
                return;
            }
        }
    }

    static final class ThreadWorker extends NewThreadWorker {
        private long expirationTime;

        ThreadWorker(ThreadFactory threadFactory) {
            super(threadFactory);
            this.expirationTime = 0L;
        }

        long getExpirationTime() {
            return expirationTime;
        }

        void setExpirationTime(long expirationTime) {
            this.expirationTime = expirationTime;
        }

    }
}
