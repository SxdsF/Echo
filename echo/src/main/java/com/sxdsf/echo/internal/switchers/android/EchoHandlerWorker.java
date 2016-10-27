package com.sxdsf.echo.internal.switchers.android;

import com.sxdsf.echo.Cast;
import com.sxdsf.echo.casts.Casts;
import com.sxdsf.echo.functions.Action0;

/**
 * com.sxdsf.echo.internal.switchers.android.EchoHandlerWorker
 *
 * @author 孙博闻
 * @date 2016/10/14 16:42
 * @desc 使用EchoHandler自定义的处理方式处理
 */

public class EchoHandlerWorker extends HandlerWorker {

    public EchoHandlerWorker(EchoHandler handler) {
        super(handler);
    }

    @Override
    public Cast switches(Action0 action0) {
        if (mUnReceived) {
            return Casts.unReceived();
        }

        if (!(mHandler instanceof EchoHandler)) {
            throw new ClassCastException("need EchoHandler");
        }

        EchoHandler handler = (EchoHandler) mHandler;

        SwitchedAction switchedAction = new SwitchedAction(action0, handler);
        handler.enQueue(switchedAction);
        if (!handler.isRunning()) {
            handler.sendMessage(handler.obtainMessage());
        }

        if (mUnReceived) {
            handler.clear();
            return Casts.unReceived();
        }

        return switchedAction;
    }

    @Override
    public void unReceive() {
        mUnReceived = true;
        if (!(mHandler instanceof EchoHandler)) {
            throw new ClassCastException("need EchoHandler");
        }

        EchoHandler handler = (EchoHandler) mHandler;
        handler.clear();
    }

    private static class SwitchedAction implements Action0, Cast {

        private final Action0 mAction;
        private final EchoHandler mHandler;
        private volatile boolean mUnReceived;

        @Override
        public boolean isUnReceived() {
            return mUnReceived;
        }

        @Override
        public void unReceive() {
            mUnReceived = true;
            mHandler.clear();
        }

        private SwitchedAction(Action0 action, EchoHandler handler) {
            this.mAction = action;
            this.mHandler = handler;
        }

        @Override
        public void call() {
            mAction.call();
        }
    }
}
