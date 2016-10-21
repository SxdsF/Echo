package com.sxdsf.echo;

/**
 * com.sxdsf.echo.OnCastLift
 *
 * @author 孙博闻
 * @date 2016/10/18 18:42
 * @desc 用于变换的类
 */

final class OnCastLift<T extends Voice> implements OnCast<T> {

    private final OnCast<T> mParent;
    private final Alter<T> mAlter;

    OnCastLift(OnCast<T> parent, Alter<T> alter) {
        mParent = parent;
        mAlter = alter;
    }

    @Override
    public void call(Receiver<T> receiver) {
        mParent.call(mAlter.call(receiver));
    }
}
