package com.sxdsf.echo.internal.switchers.newthread;

import com.sxdsf.echo.Cast;
import com.sxdsf.echo.Switcher;
import com.sxdsf.echo.casts.Casts;
import com.sxdsf.echo.functions.Action0;
import com.sxdsf.echo.internal.switchers.EchoThreadFactory;
import com.sxdsf.echo.internal.switchers.SwitchedAction;
import com.sxdsf.echo.internal.utils.CastList;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * com.sxdsf.echo.internal.switchers.newthread.NewThreadWorker
 *
 * @author 孙博闻
 * @date 2016/10/27 18:19
 * @desc 每次创建新线程的线程切换者的Worker
 */

public class NewThreadWorker extends Switcher.Worker {

    private final ExecutorService mExecutorService;
    private volatile boolean mUnReceived;
    private static final ConcurrentHashMap<ExecutorService, ExecutorService> EXECUTORS = new ConcurrentHashMap<>();
    private static final AtomicReference<ScheduledExecutorService> PURGE = new AtomicReference<>();
    private static final int PURGE_FREQUENCY = 1000;
    private static final String PURGE_THREAD_PREFIX = "echo-purge-";

    public NewThreadWorker(ThreadFactory threadFactory) {
        mExecutorService = Executors.newSingleThreadExecutor(threadFactory);
        registerExecutor(mExecutorService);
    }

    @Override
    public Cast switches(Action0 action0) {
        if (mUnReceived) {
            return Casts.unReceived();
        }
        return switchesActual(action0);
    }

    public SwitchedAction switchesActual(Action0 action0) {
        SwitchedAction run = new SwitchedAction(action0);
        Future<?> f;
        f = mExecutorService.submit(run);
        run.add(f);

        return run;
    }

    public SwitchedAction switchesActual(Action0 action0, CastList castList) {
        SwitchedAction run = new SwitchedAction(action0);
        castList.add(run);
        Future<?> f;
        f = mExecutorService.submit(run);
        run.add(f);

        return run;
    }

    @Override
    public boolean isUnReceived() {
        return mUnReceived;
    }

    @Override
    public void unReceive() {
        mUnReceived = true;
        mExecutorService.shutdownNow();
        deregisterExecutor(mExecutorService);
    }

    private static void registerExecutor(ExecutorService service) {
        do {
            ScheduledExecutorService exec = PURGE.get();
            if (exec != null) {
                break;
            }
            exec = Executors.newScheduledThreadPool(1, new EchoThreadFactory(PURGE_THREAD_PREFIX));
            if (PURGE.compareAndSet(null, exec)) {
                exec.scheduleAtFixedRate(new Runnable() {
                    @Override
                    public void run() {
                        purgeExecutors();
                    }
                }, PURGE_FREQUENCY, PURGE_FREQUENCY, TimeUnit.MILLISECONDS);

                break;
            } else {
                exec.shutdownNow();
            }
        } while (true);

        EXECUTORS.putIfAbsent(service, service);
    }

    private static void deregisterExecutor(ExecutorService service) {
        EXECUTORS.remove(service);
    }

    private static void purgeExecutors() {
        try {
            Iterator<ExecutorService> it = EXECUTORS.keySet().iterator();
            while (it.hasNext()) {
                ExecutorService exec = it.next();
                if (!exec.isShutdown()) {
                    if (exec instanceof ThreadPoolExecutor) {
                        ((ThreadPoolExecutor) exec).purge();
                    }
                } else {
                    it.remove();
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
