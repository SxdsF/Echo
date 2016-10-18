package com.sxdsf.echo;

/**
 * com.sxdsf.echo.EchoHandlerWorker
 *
 * @author 孙博闻
 * @date 2016/10/14 16:42
 * @desc 使用EchoHandler自定义的处理方式处理
 */

public class EchoHandlerWorker extends HandlerWorker {

    private final EchoHandler mEchoHandler;

    public EchoHandlerWorker(EchoHandler handler) {
        super(handler);
        mEchoHandler = handler;
    }

    @Override
    public Cast switches(Action0 action0) {
        if (mUnReceived) {
            return Casts.unReceived();
        }

        SwitchedAction switchedAction = new SwitchedAction(action0, mEchoHandler);
        mEchoHandler.enQueue(switchedAction);
        if (!mEchoHandler.isRunning()) {
            mEchoHandler.sendMessage(mEchoHandler.obtainMessage());
        }

        if (mUnReceived) {
            mEchoHandler.clear();
            return Casts.unReceived();
        }

        return switchedAction;
    }

    @Override
    public void unReceive() {
        mUnReceived = true;
        mEchoHandler.clear();
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
