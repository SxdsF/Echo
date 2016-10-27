package com.sxdsf.echo.internal.switchers.computation;

import com.sxdsf.echo.Cast;
import com.sxdsf.echo.Switcher;
import com.sxdsf.echo.casts.Casts;
import com.sxdsf.echo.functions.Action0;
import com.sxdsf.echo.internal.utils.CastList;

/**
 * com.sxdsf.echo.internal.switchers.computation.ComputationWorker
 *
 * @author 孙博闻
 * @date 2016/10/27 22:30
 * @desc 计算线程的线程切换者的Worker
 */

public class ComputationWorker extends Switcher.Worker {

    private final CastList mCastList = new CastList();
    private final ComputationSwitcher.PoolWorker mPoolWorker;

    public ComputationWorker(ComputationSwitcher.PoolWorker poolWorker) {
        mPoolWorker = poolWorker;

    }

    @Override
    public Cast switches(final Action0 action0) {
        if (isUnReceived()) {
            return Casts.unReceived();
        }

        return mPoolWorker.switchesActual(new Action0() {
            @Override
            public void call() {
                if (isUnReceived()) {
                    return;
                }
                action0.call();
            }
        }, mCastList);
    }

    @Override
    public boolean isUnReceived() {
        return mCastList.isUnReceived();
    }

    @Override
    public void unReceive() {
        mCastList.unReceive();
    }
}
