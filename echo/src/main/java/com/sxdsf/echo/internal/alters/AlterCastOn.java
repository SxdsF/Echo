package com.sxdsf.echo.internal.alters;

import com.sxdsf.echo.Acceptor;
import com.sxdsf.echo.Caster;
import com.sxdsf.echo.Receiver;
import com.sxdsf.echo.Switcher;
import com.sxdsf.echo.Voice;
import com.sxdsf.echo.functions.Action0;

import java.lang.reflect.Method;

/**
 * com.sxdsf.echo.internal.alters.AlterCastOn
 *
 * @author 孙博闻
 * @date 2016/10/19 11:54
 * @desc 发送时用于线程变换的类
 */
public final class AlterCastOn<T extends Voice> implements Caster.OnCast<T> {

    private final Caster<T> mOriginal;
    private final Switcher mSwitcher;

    public AlterCastOn(Caster<T> original, Switcher switcher) {
        mOriginal = original;
        mSwitcher = switcher;
    }

    @Override
    public void call(Receiver<T> receiver) {
        if (receiver instanceof Acceptor) {
            final Acceptor<T> acceptor = (Acceptor<T>) receiver;
            Switcher.Worker worker = mSwitcher.createWorker();
            acceptor.add(worker);
            worker.switches(new Action0() {
                @Override
                public void call() {
                    Method cast = findMethod(Caster.class, "cast", Receiver.class);
                    Method wrap = findMethod(Caster.class, "wrap", Receiver.class, boolean.class, boolean.class);
                    if (cast != null && wrap != null) {
                        try {
                            cast.setAccessible(true);
                            wrap.setAccessible(true);
                            cast.invoke(mOriginal, wrap.invoke(mOriginal, acceptor, true, true));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }

    private Method findMethod(Class<Caster> cls, String methodName, Class<?>... classes) {
        Method method = null;
        if (cls != null && methodName != null) {
            try {
                method = cls.getDeclaredMethod(methodName, classes);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        return method;
    }
}
