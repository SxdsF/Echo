package com.sxdsf.echo.internal.switchers.newthread;

import com.sxdsf.echo.Cast;
import com.sxdsf.echo.Switcher;
import com.sxdsf.echo.casts.Casts;
import com.sxdsf.echo.functions.Action0;
import com.sxdsf.echo.internal.switchers.SwitchedAction;
import com.sxdsf.echo.internal.utils.CastList;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

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

    public NewThreadWorker(ThreadFactory threadFactory) {
        mExecutorService = Executors.newSingleThreadExecutor(threadFactory);
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
    }
}
