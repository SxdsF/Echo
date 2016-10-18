package com.sxdsf.echo.sample;

import com.sxdsf.echo.AcceptorWrapper;
import com.sxdsf.echo.AcceptorWrapperFactory;
import com.sxdsf.echo.Cast;
import com.sxdsf.echo.Caster;
import com.sxdsf.echo.OnCast;
import com.sxdsf.echo.Switcher;

/**
 * com.sxdsf.echo.sample.Call
 *
 * @author 孙博闻
 * @date 2016/10/10 15:13
 * @desc 文件描述
 */

public class Call extends Caster<Response, Callback> {

    private Call(OnCall onCall, AcceptorWrapperFactory<Response, Callback> acceptorWrapperFactory) {
        super(onCall, acceptorWrapperFactory);
    }

    private Call(OnCast<Response> onCast, AcceptorWrapperFactory<Response, Callback> acceptorWrapperFactory) {
        super(onCast, acceptorWrapperFactory);
    }

    public Cast execute(Callback callback) {
        return super.cast(callback);
    }

    public static Call create(OnCall onCall) {
        return new Call(onCall, new AcceptorWrapperFactory<Response, Callback>() {
            @Override
            public AcceptorWrapper<Response, Callback> createWrapper(Callback receiver) {
                return new CallbackWrapper(receiver);
            }
        });
    }

    public static Call create(OnCast<Response> onCall) {
        return new Call(onCall, new AcceptorWrapperFactory<Response, Callback>() {
            @Override
            public AcceptorWrapper<Response, Callback> createWrapper(Callback receiver) {
                return new CallbackWrapper(receiver);
            }
        });
    }

    public Call callOn(Switcher<Response, Callback> switcher) {
        return (Call) super.castOn(switcher);
    }

    public Call callbackOn(Switcher<Response, Callback> switcher) {
        return (Call) super.receiveOn(switcher);
    }

    public Call unify(Converter converter) {
        return (Call) super.convert(converter);
    }
}
