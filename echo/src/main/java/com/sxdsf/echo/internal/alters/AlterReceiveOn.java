package com.sxdsf.echo.internal.alters;

import com.sxdsf.echo.Caster;
import com.sxdsf.echo.Receiver;
import com.sxdsf.echo.Switcher;
import com.sxdsf.echo.Voice;

import java.lang.reflect.Method;

/**
 * com.sxdsf.echo.internal.alters.AlterReceiveOn
 *
 * @author 孙博闻
 * @date 2016/10/18 18:48
 * @desc 接收时用于线程变换的类
 */
public final class AlterReceiveOn<T extends Voice> implements Caster.Alter<T> {

    private final Caster<T> mOriginal;
    private final Switcher mSwitcher;

    public AlterReceiveOn(Caster<T> original, Switcher switcher) {
        mOriginal = original;
        mSwitcher = switcher;
    }

    @Override
    public Receiver<T> call(Receiver<T> receiver) {
        Receiver<T> result = null;
        Method createOnReceiveReceiver = findMethod(Caster.class, "createOnReceiveReceiver", Receiver.class, Switcher.Worker.class);
        if (createOnReceiveReceiver != null) {
            try {
                createOnReceiveReceiver.setAccessible(true);
                result = (Receiver<T>) createOnReceiveReceiver.invoke(mOriginal, receiver, mSwitcher.createWorker());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
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
