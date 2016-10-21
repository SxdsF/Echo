package com.sxdsf.echo.sample;

import android.os.Looper;

import com.sxdsf.echo.Acceptor;
import com.sxdsf.echo.Action0;
import com.sxdsf.echo.EchoHandler;
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
    private final Switcher<Response> MAIN;
    /**
     * 异步线程的切换者
     */
    private final Switcher<Response> ASYNC;

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
    static Switcher<Response> mainThread() {
        return INSTANCE.MAIN;
    }

    /**
     * 返回一个异步线程的切换者
     *
     * @return
     */
    static Switcher<Response> asyncThread() {
        return INSTANCE.ASYNC;
    }

    /**
     * 主线程的切换类
     */
    private static class MainThreadSwitcher extends Switcher<Response> {
        private final EchoHandler mCallbackHandler;

        private MainThreadSwitcher() {
            mCallbackHandler = new EchoHandler(Looper.getMainLooper());
        }

        @Override
        public Worker createWorker() {
            return new MainThreadWorker(mCallbackHandler);
        }

        @Override
        protected Acceptor<Response> createOnReceiveReceiver(Receiver<Response> responseReceiver, Worker worker) {
            CallbackOnCallback callbackOnCallback = new CallbackOnCallback((Callback) responseReceiver, true, false, worker);
            callbackOnCallback.init();
            return callbackOnCallback;
        }
    }

    /**
     * 异步线程的切换者
     */
    private static class AsyncThreadSwitcher extends Switcher<Response> {

        private final ExecutorService mExecutorService;

        private AsyncThreadSwitcher(ExecutorService executorService) {
            mExecutorService = executorService;
        }

        @Override
        public Worker createWorker() {
            return new AsyncThreadWorker(mExecutorService);
        }

        @Override
        protected Acceptor<Response> createOnReceiveReceiver(Receiver<Response> responseReceiver, Worker worker) {
            CallbackOnCallback callbackOnCallback = new CallbackOnCallback((Callback) responseReceiver, true, false, worker);
            callbackOnCallback.init();
            return callbackOnCallback;
        }
    }

    private static class CallbackOnCallback extends CallbackWrapper {

        private final Switcher.Worker mWorker;

        CallbackOnCallback(Callback wrapped, boolean isOverride, boolean isMerge, Switcher.Worker worker) {
            super(wrapped, isOverride, isMerge);
            mWorker = worker;

        }

        private void init() {
            if (getWrapped() != null && getWrapped() instanceof Acceptor) {
                ((Acceptor) getWrapped()).add(mWorker);
                ((Acceptor) getWrapped()).add(this);
            }
        }

        @Override
        public void onStart() {
            if (isUnReceived()) {
                return;
            }
            mWorker.switches(new Action0() {
                @Override
                public void call() {
                    if (getWrapped() != null) {
                        if (getWrapped() instanceof Acceptor && ((Acceptor) getWrapped()).isUnReceived()) {
                            return;
                        }
                        getWrapped().onStart();
                    }
                }
            });
        }

        @Override
        public void onError(final Throwable t) {
            if (isUnReceived()) {
                return;
            }
            mWorker.switches(new Action0() {
                @Override
                public void call() {
                    if (getWrapped() != null) {
                        if (getWrapped() instanceof Acceptor && ((Acceptor) getWrapped()).isUnReceived()) {
                            return;
                        }
                        getWrapped().onError(t);
                    }
                }
            });
        }

        @Override
        public void onSuccess(final Response response) {
            if (isUnReceived()) {
                return;
            }
            mWorker.switches(new Action0() {
                @Override
                public void call() {
                    if (getWrapped() != null) {
                        if (getWrapped() instanceof Acceptor && ((Acceptor) getWrapped()).isUnReceived()) {
                            return;
                        }
                        getWrapped().onSuccess(response);
                    }
                }
            });
        }

        @Override
        public void onCancel() {
            if (isUnReceived()) {
                return;
            }
            mWorker.switches(new Action0() {
                @Override
                public void call() {
                    if (getWrapped() != null) {
                        if (getWrapped() instanceof Acceptor && ((Acceptor) getWrapped()).isUnReceived()) {
                            return;
                        }
                        getWrapped().onCancel();
                    }
                }
            });
        }
    }
}
