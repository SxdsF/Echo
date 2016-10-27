package com.sxdsf.echo.internal.alters;

import com.sxdsf.echo.Caster;
import com.sxdsf.echo.Receiver;
import com.sxdsf.echo.Voice;

/**
 * com.sxdsf.echo.internal.alters.OnCastLift
 *
 * @author 孙博闻
 * @date 2016/10/18 18:42
 * @desc 用于变换的类
 */

public final class OnCastLift<T extends Voice> implements Caster.OnCast<T> {

    private final Caster.OnCast<T> mParent;
    private final Caster.Alter<T> mAlter;

    public OnCastLift(Caster.OnCast<T> parent, Caster.Alter<T> alter) {
        mParent = parent;
        mAlter = alter;
    }

    @Override
    public void call(Receiver<T> receiver) {
        mParent.call(mAlter.call(receiver));
    }
}
