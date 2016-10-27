package com.sxdsf.echo.internal.switchers.io;

import com.sxdsf.echo.Cast;
import com.sxdsf.echo.Switcher;
import com.sxdsf.echo.casts.Casts;
import com.sxdsf.echo.casts.CompositeCast;
import com.sxdsf.echo.functions.Action0;
import com.sxdsf.echo.internal.switchers.SwitchedAction;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * com.sxdsf.echo.internal.switchers.io.IOWorker
 *
 * @author 孙博闻
 * @date 2016/10/27 23:48
 * @desc IO线程的线程切换者的Worker
 */

public class IOWorker extends Switcher.Worker implements Action0 {
    private final CompositeCast mCasts = new CompositeCast();
    private final IOSwitcher.CachedWorkerPool mPool;
    private final IOSwitcher.ThreadWorker mThreadWorker;
    private final AtomicBoolean mOnce;

    public IOWorker(IOSwitcher.CachedWorkerPool pool) {
        mPool = pool;
        mOnce = new AtomicBoolean();
        mThreadWorker = pool.get();
    }

    @Override
    public void call() {
        mPool.release(mThreadWorker);
    }

    @Override
    public Cast switches(final Action0 action0) {
        if (mCasts.isUnReceived()) {
            return Casts.unReceived();
        }

        SwitchedAction s = mThreadWorker.switchesActual(new Action0() {
            @Override
            public void call() {
                if (isUnReceived()) {
                    return;
                }
                action0.call();
            }
        });
        mCasts.add(s);
        s.addParent(mCasts);
        return s;
    }

    @Override
    public boolean isUnReceived() {
        return mCasts.isUnReceived();
    }

    @Override
    public void unReceive() {
        if (mOnce.compareAndSet(false, true)) {
            mThreadWorker.switches(this);
        }
        mCasts.unReceive();
    }
}
