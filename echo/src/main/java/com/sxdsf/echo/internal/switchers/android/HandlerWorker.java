package com.sxdsf.echo.internal.switchers.android;

import android.os.Handler;
import android.os.Message;

import com.sxdsf.echo.Cast;
import com.sxdsf.echo.Switcher;
import com.sxdsf.echo.casts.Casts;
import com.sxdsf.echo.functions.Action0;

/**
 * com.sxdsf.echo.internal.switchers.android.HandlerWorker
 *
 * @author 孙博闻
 * @date 2016/10/14 15:22
 * @desc 内部使用Handler处理的Worker
 */

public class HandlerWorker extends Switcher.Worker {

    protected final Handler mHandler;
    protected volatile boolean mUnReceived;

    public HandlerWorker(Handler handler) {
        this.mHandler = handler;
    }

    @Override
    public Cast switches(Action0 action0) {
        if (mUnReceived) {
            return Casts.unReceived();
        }

        SwitchedAction switchedAction = new SwitchedAction(action0, mHandler);
        Message message = Message.obtain(mHandler, switchedAction);
        message.obj = this;//token
        mHandler.sendMessage(message);

        if (mUnReceived) {
            mHandler.removeCallbacks(switchedAction);
            return Casts.unReceived();
        }

        return switchedAction;
    }

    @Override
    public boolean isUnReceived() {
        return mUnReceived;
    }

    @Override
    public void unReceive() {
        mUnReceived = true;
        mHandler.removeCallbacksAndMessages(this /* token */);
    }

    private static class SwitchedAction implements Runnable, Cast {

        private final Action0 mAction;
        private final Handler mHandler;
        private volatile boolean mUnReceived;

        @Override
        public boolean isUnReceived() {
            return mUnReceived;
        }

        @Override
        public void unReceive() {
            mUnReceived = true;
            mHandler.removeCallbacks(this);
        }

        private SwitchedAction(Action0 action, Handler handler) {
            this.mAction = action;
            this.mHandler = handler;
        }

        @Override
        public void run() {
            mAction.call();
        }
    }
}
