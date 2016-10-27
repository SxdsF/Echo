package com.sxdsf.echo.internal.switchers.queue;

import com.sxdsf.echo.Switcher;
import com.sxdsf.echo.internal.switchers.EchoThreadFactory;
import com.sxdsf.echo.internal.switchers.SwitcherLifecycle;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicReference;

/**
 * com.sxdsf.echo.internal.switchers.queue.TaskQueueSwitcher
 *
 * @author 孙博闻
 * @date 2016/10/27 18:54
 * @desc 任务队列的线程切换者
 */

public class TaskQueueSwitcher extends Switcher implements SwitcherLifecycle {

    private final ThreadFactory mThreadFactory;

    private static final TaskQueuePool NONE = new TaskQueuePool(EchoThreadFactory.NONE);

    private final AtomicReference<TaskQueuePool> mPool;

    public TaskQueueSwitcher(ThreadFactory threadFactory) {
        mThreadFactory = threadFactory;
        mPool = new AtomicReference<>(NONE);
        start();
    }

    @Override
    public Worker createWorker() {
        return new TaskQueueWorker(mPool.get().getMessageQueue());
    }

    @Override
    public void start() {
        TaskQueuePool update = new TaskQueuePool(mThreadFactory);
        if (!mPool.compareAndSet(NONE, update)) {
            update.shutdown();
        }
    }

    @Override
    public void shutdown() {
        for (; ; ) {
            TaskQueuePool curr = mPool.get();
            if (curr == NONE) {
                return;
            }
            if (mPool.compareAndSet(curr, NONE)) {
                curr.shutdown();
                return;
            }
        }
    }

    private static class TaskQueuePool {

        private final BlockingQueue<Message> mMessageQueue;
        private final ExecutorService mExecutor;
        private final ThreadFactory mThreadFactory;

        private TaskQueuePool(ThreadFactory threadFactory) {
            mMessageQueue = new LinkedBlockingQueue<>();
            mThreadFactory = threadFactory;
            mExecutor = Executors.newSingleThreadExecutor(threadFactory);
            if (threadFactory != EchoThreadFactory.NONE) {
                mExecutor.execute(new Task(mMessageQueue));
            }
        }

        private void shutdown() {
            mExecutor.shutdownNow();
        }

        private BlockingQueue<Message> getMessageQueue() {
            if (mThreadFactory == EchoThreadFactory.NONE) {
                return null;
            }
            return mMessageQueue;
        }
    }

    private static class Task implements Runnable {

        private final BlockingQueue<Message> mQueue;

        private Task(BlockingQueue<Message> queue) {
            mQueue = queue;
        }

        @Override
        public void run() {
            for (; ; ) {
                Message message = mQueue.poll();
                if (message != null && message.mWorker != null && !message.mWorker.isUnReceived()) {
                    try {
                        message.mWorker.handle();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
