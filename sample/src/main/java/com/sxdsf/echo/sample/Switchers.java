package com.sxdsf.echo.sample;

import android.os.Looper;

import com.sxdsf.echo.Action0;
import com.sxdsf.echo.Caster;
import com.sxdsf.echo.EchoHandler;
import com.sxdsf.echo.OnCast;
import com.sxdsf.echo.Receiver;
import com.sxdsf.echo.Switcher;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * com.sxdsf.whew.Switchers
 *
 * @author 孙博闻
 * @date 2016/7/14 15:02
 * @desc Switcher的集合
 */
class Switchers {
    /**
     * 主线程的切换者
     */
    private final Switcher<Response, Callback> MAIN;
    /**
     * 异步线程的切换者
     */
    private final Switcher<Response, Callback> ASYNC;

    /**
     * 单一实例
     */
    private static final Switchers INSTANCE = new Switchers();

    private static final String TAG = "Switchers";

    private Switchers() {
        MAIN = new MainThreadSwitcher();
        //用于在后台和异步执行的线程池
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        ASYNC = new AsyncThreadSwitcher(executorService);
    }

    /**
     * 返回一个主线程的切换者
     *
     * @return
     */
    static Switcher<Response, Callback> mainThread() {
        return INSTANCE.MAIN;
    }

    /**
     * 返回一个异步线程的切换者
     *
     * @return
     */
    static Switcher<Response, Callback> asyncThread() {
        return INSTANCE.ASYNC;
    }

    /**
     * 主线程的切换类
     */
    private static class MainThreadSwitcher extends Switcher<Response, Callback> {
        private final EchoHandler mCallbackHandler;

        private MainThreadSwitcher() {
            mCallbackHandler = new EchoHandler(Looper.getMainLooper());
        }

        @Override
        public Worker createWorker() {
            return new MainThreadWorker(mCallbackHandler);
        }

        @Override
        public Callback createOnCastReceiver(final Receiver<Response> responseReceiver) {
            return new CallbackWrapper((Callback) responseReceiver);
        }

        @Override
        public Callback createOnReceiveReceiver(Receiver<Response> responseReceiver) {
            return new CallbackWrapper(new MainSwitchCallback(responseReceiver, mCallbackHandler));
        }

        @Override
        protected Caster<Response, Callback> createOnCastCaster(OnCast<Response> onCast) {
            return Call.create(onCast);
        }
    }

    /**
     * 异步线程的切换者
     */
    private static class AsyncThreadSwitcher extends Switcher<Response, Callback> {

        private final ExecutorService mExecutorService;

        private AsyncThreadSwitcher(ExecutorService executorService) {
            mExecutorService = executorService;
        }

        @Override
        public Worker createWorker() {
            return new AsyncThreadWorker(mExecutorService);
        }

        @Override
        public Callback createOnCastReceiver(final Receiver<Response> responseReceiver) {
            return new CallbackWrapper((Callback) responseReceiver);
        }

        @Override
        public Callback createOnReceiveReceiver(Receiver<Response> responseReceiver) {
            return new CallbackWrapper(new AsyncSwitchCallback(responseReceiver, mExecutorService));
        }

        @Override
        protected Caster<Response, Callback> createOnCastCaster(OnCast<Response> onCast) {
            return Call.create(onCast);
        }
    }

    /**
     * 线程变换基本处理的类
     */
    private static abstract class SwitchCallback implements Callback {

        Callback mRawCallback;

        private SwitchCallback(Receiver<Response> rawCallback) {
            mRawCallback = (Callback) rawCallback;
        }
    }

    /**
     * 主线程的处理
     */
    private static class MainSwitchCallback extends SwitchCallback {

        private EchoHandler mCallbackHandler;

        private MainSwitchCallback(Receiver<Response> rawCallback, EchoHandler messageHandler) {
            super(rawCallback);
            mCallbackHandler = messageHandler;
        }

        @Override
        public void onStart() {
            if (Looper.getMainLooper() == Looper.myLooper()) {
                mRawCallback.onStart();
            } else {
                Action0 temp = new Action0() {
                    @Override
                    public void call() {
                        mRawCallback.onStart();
                    }
                };
                enQueue(temp);
            }
        }

        @Override
        public void onError(final Throwable t) {
            if (Looper.getMainLooper() == Looper.myLooper()) {
                mRawCallback.onError(t);
            } else {
                Action0 temp = new Action0() {
                    @Override
                    public void call() {
                        mRawCallback.onError(t);
                    }
                };
                enQueue(temp);
            }
        }

        @Override
        public void onSuccess(final Response response) {
            if (Looper.getMainLooper() == Looper.myLooper()) {
                mRawCallback.onSuccess(response);
            } else {
                Action0 temp = new Action0() {
                    @Override
                    public void call() {
                        mRawCallback.onSuccess(response);
                    }
                };
                enQueue(temp);
            }
        }

        @Override
        public void onCancel() {
            if (Looper.getMainLooper() == Looper.myLooper()) {
                mRawCallback.onCancel();
            } else {
                Action0 temp = new Action0() {
                    @Override
                    public void call() {
                        mRawCallback.onCancel();
                    }
                };
                enQueue(temp);
            }
        }

        private void enQueue(Action0 action0) {
            mCallbackHandler.enQueue(action0);
            if (!mCallbackHandler.isRunning()) {
                mCallbackHandler.sendMessage(mCallbackHandler.obtainMessage());
            }
        }
    }

    /**
     * 异步线程的处理
     */
    private static class AsyncSwitchCallback extends SwitchCallback {

        private final ExecutorService mExecutorService;

        private AsyncSwitchCallback(Receiver<Response> rawCallback, ExecutorService executorService) {
            super(rawCallback);
            mExecutorService = executorService;
        }

        @Override
        public void onStart() {
            mExecutorService.execute(new Runnable() {
                @Override
                public void run() {
                    mRawCallback.onStart();
                }
            });
        }

        @Override
        public void onError(final Throwable t) {
            mExecutorService.execute(new Runnable() {
                @Override
                public void run() {
                    mRawCallback.onError(t);
                }
            });
        }

        @Override
        public void onSuccess(final Response response) {
            mExecutorService.execute(new Runnable() {
                @Override
                public void run() {
                    mRawCallback.onSuccess(response);
                }
            });
        }

        @Override
        public void onCancel() {
            mExecutorService.execute(new Runnable() {
                @Override
                public void run() {
                    mRawCallback.onCancel();
                }
            });
        }
    }
}
