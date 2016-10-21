package com.sxdsf.echo.sample;

import com.sxdsf.echo.Acceptor;
import com.sxdsf.echo.Cast;
import com.sxdsf.echo.Caster;
import com.sxdsf.echo.OnCast;
import com.sxdsf.echo.Receiver;
import com.sxdsf.echo.Switcher;

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

    public Call callOn(Switcher<Response> switcher) {
        return (Call) super.castOn(switcher);
    }

    public Call callbackOn(Switcher<Response> switcher) {
        return (Call) super.receiveOn(switcher);
    }

    public Call unify(Converter converter) {
        return (Call) super.convert(converter);
    }
}
