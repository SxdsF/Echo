package com.sxdsf.echo.switchers;

import android.os.Looper;

import com.sxdsf.echo.Switcher;
import com.sxdsf.echo.internal.switchers.EchoThreadFactory;
import com.sxdsf.echo.internal.switchers.SwitcherLifecycle;
import com.sxdsf.echo.internal.switchers.android.LooperSwitcher;
import com.sxdsf.echo.internal.switchers.computation.ComputationSwitcher;
import com.sxdsf.echo.internal.switchers.executor.ExecutorSwitcher;
import com.sxdsf.echo.internal.switchers.io.IOSwitcher;
import com.sxdsf.echo.internal.switchers.newthread.NewThreadSwitcher;
import com.sxdsf.echo.internal.switchers.queue.TaskQueueSwitcher;

import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReference;

/**
 * com.sxdsf.echo.switchers.Switchers
 *
 * @author 孙博闻
 * @date 2016/10/26 17:35
 * @desc 线程切换者的集合
 */

public class Switchers {

    /**
     * 主线程切换者
     */
    private final Switcher mMain;
    /**
     * 每次创建新线程的线程切换者
     */
    private final Switcher mNewThread;
    /**
     * 任务队列的线程切换者
     */
    private final Switcher mTaskQueue;
    /**
     * 计算线程的线程切换者
     */
    private final Switcher mComputation;
    /**
     * IO线程的线程切换者
     */
    private final Switcher mIO;

    private static final AtomicReference<Switchers> INSTANCE = new AtomicReference<>();

    private static Switchers getInstance() {
        for (; ; ) {
            Switchers current = INSTANCE.get();
            if (current != null) {
                return current;
            }
            current = new Switchers();
            if (INSTANCE.compareAndSet(null, current)) {
                return current;
            } else {
                current.shutdownInstance();
            }
        }
    }

    private Switchers() {
        mMain = new LooperSwitcher(Looper.getMainLooper());
        mNewThread = new NewThreadSwitcher(new EchoThreadFactory("echo-new-thread-"));
        mTaskQueue = new TaskQueueSwitcher(new EchoThreadFactory("echo-task-queue-"));
        mComputation = new ComputationSwitcher(new EchoThreadFactory("echo-computation-"));
        mIO = new IOSwitcher(new EchoThreadFactory("echo-io-"));
    }

    /**
     * 返回Android主线程切换者
     *
     * @return
     */
    public static Switcher main() {
        return getInstance().mMain;
    }

    /**
     * 返回Android指定Looper线程切换者
     *
     * @param looper 指定的looper
     * @return
     */
    public static Switcher from(Looper looper) {
        return new LooperSwitcher(looper);
    }

    /**
     * 返回一个新线程切换者
     *
     * @return
     */
    public static Switcher newThread() {
        return getInstance().mNewThread;
    }

    /**
     * 返回指定线程池线程切换者
     *
     * @param executor 指定的线程池
     * @return
     */
    public static Switcher from(Executor executor) {
        return new ExecutorSwitcher(executor);
    }

    /**
     * 返回IO线程切换者
     *
     * @return
     */
    public static Switcher io() {
        return getInstance().mIO;
    }


    /**
     * 返回计算线程切换者
     *
     * @return
     */
    public static Switcher computation() {
        return getInstance().mComputation;
    }


    /**
     * 返回任务队列线程切换者
     *
     * @return
     */
    public static Switcher taskQueue() {
        return getInstance().mTaskQueue;
    }

    private void shutdownInstance() {
        if (mIO instanceof SwitcherLifecycle) {
            ((SwitcherLifecycle) mIO).shutdown();
        }

        if (mComputation instanceof SwitcherLifecycle) {
            ((SwitcherLifecycle) mComputation).shutdown();
        }

        if (mTaskQueue instanceof SwitcherLifecycle) {
            ((SwitcherLifecycle) mTaskQueue).shutdown();
        }
    }
}
