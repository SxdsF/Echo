package com.sxdsf.echo.sample;

import com.sxdsf.echo.AcceptorWrapper;

/**
 * com.sxdsf.echo.sample.CallbackWrapper
 *
 * @author 孙博闻
 * @date 2016/10/9 21:15
 * @desc 文件描述
 */

class CallbackWrapper extends AcceptorWrapper<Response, Callback> implements Callback {

    CallbackWrapper(Callback wrapped, boolean isOverride, boolean isMerge) {
        super(wrapped, isOverride, isMerge);
    }

    @Override
    public void onStart() {
        if (getWrapped() == null) {
            return;
        }
        getWrapped().onStart();
    }

    @Override
    public void onError(Throwable t) {
        if (getWrapped() == null) {
            return;
        }
        getWrapped().onError(t);
    }

    @Override
    public void onSuccess(Response response) {
        if (getWrapped() == null) {
            return;
        }
        getWrapped().onSuccess(response);
    }

    @Override
    public void onCancel() {
        if (getWrapped() == null) {
            return;
        }
        getWrapped().onCancel();
    }
}
