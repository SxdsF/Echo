package com.sxdsf.echo.sample;

import com.sxdsf.echo.Acceptor;
import com.sxdsf.echo.Cast;
import com.sxdsf.echo.Caster;
import com.sxdsf.echo.Receiver;
import com.sxdsf.echo.Switcher;
import com.sxdsf.echo.functions.Action0;

/**
 * com.sxdsf.echo.sample.Call
 *
 * @author 孙博闻
 * @date 2016/10/10 15:13
 * @desc 文件描述
 */

public class Call extends Caster<Response> {

    private Call(OnCast<Response> onCast) {
        super(onCast);
    }

    public Cast execute(Callback callback) {
        return super.cast(callback);
    }

    public static Call create(OnCall onCall) {
        return new Call(onCall);
    }

    @Override
    protected Caster<Response> create(OnCast<Response> onCast) {
        return new Call(onCast);
    }

    @Override
    protected Acceptor<Response> wrap(Receiver<Response> receiver, boolean isOverride, boolean isMerge) {
        return new CallbackWrapper((Callback) receiver, isOverride, isMerge);
    }

    @Override
    public Acceptor<Response> createOnReceiveReceiver(Receiver<Response> responseReceiver, Switcher.Worker worker) {
        CallbackOnCallback callbackOnCallback = new CallbackOnCallback((Callback) responseReceiver, true, false, worker);
        callbackOnCallback.init();
        return callbackOnCallback;
    }

    public Call callOn(Switcher switcher) {
        return (Call) super.castOn(switcher);
    }

    public Call callbackOn(Switcher switcher) {
        return (Call) super.receiveOn(switcher);
    }

    public Call unify(Converter converter) {
        return (Call) super.convert(converter);
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
