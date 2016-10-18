package com.sxdsf.echo.sample;

import com.sxdsf.echo.AcceptorWrapper;

/**
 * com.sxdsf.echo.sample.CallbackWrapper
 *
 * @author 孙博闻
 * @date 2016/10/9 21:15
 * @desc 文件描述
 */

public class CallbackWrapper extends AcceptorWrapper<Response, Callback> implements Callback {

    public CallbackWrapper(Callback wrapped) {
        super(wrapped);
    }

    @Override
    public void onStart() {
        getWrapped().onStart();
    }

    @Override
    public void onError(Throwable t) {
        getWrapped().onError(t);
    }

    @Override
    public void onSuccess(Response response) {
        getWrapped().onSuccess(response);
    }

    @Override
    public void onCancel() {
        getWrapped().onCancel();
    }
}
