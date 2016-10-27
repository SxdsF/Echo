package com.sxdsf.echo.internal.switchers.computation;

import com.sxdsf.echo.Switcher;
import com.sxdsf.echo.internal.switchers.EchoThreadFactory;
import com.sxdsf.echo.internal.switchers.SwitcherLifecycle;
import com.sxdsf.echo.internal.switchers.newthread.NewThreadWorker;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicReference;

/**
 * com.sxdsf.echo.internal.switchers.computation.ComputationSwitcher
 *
 * @author 孙博闻
 * @date 2016/10/27 22:29
 * @desc 计算线程的线程切换者
 */

public class ComputationSwitcher extends Switcher implements SwitcherLifecycle {

    /**
     * 最大线程数，为cpu核心数
     */
    private static final int MAX_THREADS = Runtime.getRuntime().availableProcessors();
    private static final PoolWorker SHUTDOWN_WORKER;

    static {
        SHUTDOWN_WORKER = new PoolWorker(EchoThreadFactory.NONE);
        SHUTDOWN_WORKER.unReceive();
    }

    private static final ComputationThreadPool NONE = new ComputationThreadPool(null, 0);

    private final ThreadFactory mThreadFactory;

    private final AtomicReference<ComputationThreadPool> mPool;

    public ComputationSwitcher(ThreadFactory threadFactory) {
        mThreadFactory = threadFactory;
        mPool = new AtomicReference<>(NONE);
        start();
    }


    @Override
    public Worker createWorker() {
        return new ComputationWorker(mPool.get().getEventLoop());
    }

    private static final class ComputationThreadPool {
        private final int mCores;
        private final PoolWorker[] mEventLoops;
        private long mN;

        private ComputationThreadPool(ThreadFactory threadFactory, int maxThreads) {
            mCores = maxThreads;
            mEventLoops = new PoolWorker[maxThreads];
            for (int i = 0; i < maxThreads; i++) {
                mEventLoops[i] = new PoolWorker(threadFactory);
            }
        }

        private PoolWorker getEventLoop() {
            int c = mCores;
            if (c == 0) {
                return SHUTDOWN_WORKER;
            }
            return mEventLoops[(int) (mN++ % c)];
        }

        private void shutdown() {
            for (PoolWorker w : mEventLoops) {
                w.unReceive();
            }
        }
    }

    @Override
    public void start() {
        ComputationThreadPool update = new ComputationThreadPool(mThreadFactory, MAX_THREADS);
        if (!mPool.compareAndSet(NONE, update)) {
            update.shutdown();
        }
    }

    @Override
    public void shutdown() {
        for (; ; ) {
            ComputationThreadPool curr = mPool.get();
            if (curr == NONE) {
                return;
            }
            if (mPool.compareAndSet(curr, NONE)) {
                curr.shutdown();
                return;
            }
        }
    }

    static final class PoolWorker extends NewThreadWorker {
        PoolWorker(ThreadFactory threadFactory) {
            super(threadFactory);
        }
    }
}
