package com.sxdsf.echo;

import android.support.annotation.NonNull;

/**
 * com.sxdsf.echo.Caster
 *
 * @author 孙博闻
 * @date 2016/7/12 13:58
 * @desc 发声者，子类必须继承此类并且依照业务场景，重新定义或命名相应的方法
 */
public abstract class Caster<T extends Voice, R extends Receiver<T>> {

    OnCast<T> mOnCast;
    private final AcceptorWrapperFactory<T, R> mAcceptorWrapperFactory;

    /**
     * 发送方法
     *
     * @param receiver 接收者
     */
    @NeedRewrite
    protected Cast cast(R receiver) {
        if (receiver instanceof Acceptor) {
            return Caster.cast((Acceptor<T>) receiver, this);
        }
        if (mAcceptorWrapperFactory == null) {
            return Caster.cast(new AcceptorWrapper<T, Receiver<T>>(receiver) {
            }, this);
        }
        return Caster.cast(mAcceptorWrapperFactory.createWrapper(receiver), this);
    }

    /**
     * 指定发送者的线程
     *
     * @param switcher 执行的线程切换
     */
    @NeedRewrite
    protected Caster<T, R> castOn(@NonNull Switcher<T, R> switcher) {
        return switcher.castOn(this);
    }

    /**
     * 指定接收者的线程
     *
     * @param switcher 执行的线程切换
     */
    @NeedRewrite
    protected Caster<T, R> receiveOn(@NonNull Switcher<T, R> switcher) {
        return switcher.receiveOn(this);
    }

    /**
     * 转换方法
     *
     * @param converter 执行转换方法的转化者
     */
    @NeedRewrite
    protected <K extends Voice, V extends Receiver<K>> Caster<K, V> convert(@NonNull Converter<T, K, R, V> converter) {
        return converter.call(this);
    }

    /**
     * 类型转换方法
     *
     * @param cls 要转换的类型
     */
    @NeedRewrite
    protected <K extends Caster<? extends Voice, ? extends Receiver<T>>> K classCast(@NonNull Class<K> cls) {
        return cls.cast(this);
    }

    protected Caster(OnCast<T> onCast, AcceptorWrapperFactory<T, R> acceptorWrapperFactory) {
        mOnCast = onCast;
        mAcceptorWrapperFactory = acceptorWrapperFactory;
    }

    /**
     * 静态的发送方法
     *
     * @param acceptor 接收者
     * @param caster   发声者
     */
    private static <T extends Voice, R extends Receiver<T>> Cast cast(Acceptor<T> acceptor, Caster<T, R> caster) {
        if (!acceptor.isUnReceived()) {
            caster.mOnCast.call(acceptor);
        }
        return acceptor;
    }
}
