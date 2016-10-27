package com.sxdsf.echo.internal.switchers.executor;

import com.sxdsf.echo.Cast;
import com.sxdsf.echo.Switcher;
import com.sxdsf.echo.casts.Casts;
import com.sxdsf.echo.casts.CompositeCast;
import com.sxdsf.echo.functions.Action0;
import com.sxdsf.echo.internal.switchers.SwitchedAction;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * com.sxdsf.echo.internal.switchers.executor.ExecutorWorker
 *
 * @author 孙博闻
 * @date 2016/10/27 18:51
 * @desc 指定线程池线程切换者的Worker
 */

public class ExecutorWorker extends Switcher.Worker implements Runnable {

    private final Executor mExecutor;
    private final CompositeCast mCast;
    private final ConcurrentLinkedQueue<SwitchedAction> mQueue;
    private final AtomicBoolean mIsRunning;

    public ExecutorWorker(Executor executor) {
        mExecutor = executor;
        mCast = new CompositeCast();
        mQueue = new ConcurrentLinkedQueue<>();
        mIsRunning = new AtomicBoolean(false);
    }

    @Override
    public Cast switches(Action0 action0) {
        if (isUnReceived()) {
            return Casts.unReceived();
        }
        SwitchedAction ea = new SwitchedAction(action0, mCast);
        mCast.add(ea);
        mQueue.offer(ea);
        if (mIsRunning.compareAndSet(false, true)) {
            try {
                mExecutor.execute(this);
            } catch (RejectedExecutionException t) {
                mCast.remove(ea);
                mIsRunning.compareAndSet(true, false);
            }
        }
        return ea;
    }

    @Override
    public boolean isUnReceived() {
        return mCast.isUnReceived();
    }

    @Override
    public void unReceive() {
        mCast.unReceive();
        mQueue.clear();
    }

    @Override
    public void run() {
        while (mIsRunning.get()) {
            if (mCast.isUnReceived()) {
                mQueue.clear();
                mIsRunning.set(false);
                return;
            }
            SwitchedAction sa = mQueue.poll();
            if (sa == null) {
                mIsRunning.set(false);
                return;
            }
            if (!sa.isUnReceived()) {
                if (!mCast.isUnReceived()) {
                    sa.run();
                } else {
                    mQueue.clear();
                    mIsRunning.set(false);
                    return;
                }
            }
        }
    }
}
