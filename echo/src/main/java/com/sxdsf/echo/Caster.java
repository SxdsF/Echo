package com.sxdsf.echo;

import android.support.annotation.NonNull;

import com.sxdsf.echo.annotations.NeedRewrite;
import com.sxdsf.echo.functions.Action1;
import com.sxdsf.echo.functions.Action2;
import com.sxdsf.echo.internal.alters.AlterCastOn;
import com.sxdsf.echo.internal.alters.AlterReceiveOn;
import com.sxdsf.echo.internal.alters.OnCastLift;

/**
 * com.sxdsf.echo.Caster
 *
 * @author 孙博闻
 * @date 2016/7/12 13:58
 * @desc 发声者，子类必须继承此类并且依照业务场景，重新定义或命名相应的方法
 */
public abstract class Caster<T extends Voice> {

    private final OnCast<T> mOnCast;

    /**
     * 发送方法
     *
     * @param receiver 接收者
     */
    @NeedRewrite
    protected Cast cast(Receiver<T> receiver) {
        if (receiver instanceof Acceptor) {
            return Caster.cast((Acceptor<T>) receiver, this);
        }
        return Caster.cast(wrap(receiver, true, false), this);
    }

    /**
     * 指定发送者的线程
     *
     * @param switcher 执行的线程切换
     */
    @NeedRewrite
    protected Caster<T> castOn(@NonNull Switcher switcher) {
        return create(new AlterCastOn<>(this, switcher));
    }

    /**
     * 指定接收者的线程
     *
     * @param switcher 执行的线程切换
     */
    @NeedRewrite
    protected Caster<T> receiveOn(@NonNull Switcher switcher) {
        return lift(new AlterReceiveOn<>(this, switcher));
    }

    /**
     * 转换方法
     *
     * @param converter 执行转换方法的转化者
     */
    @NeedRewrite
    protected <R extends Voice> Caster<R> convert(@NonNull Converter<T, R> converter) {
        return converter.call(this);
    }

    /**
     * 类型转换方法
     *
     * @param cls 要转换的类型
     */
    @NeedRewrite
    protected <K extends Caster<? extends Voice>> K classCast(@NonNull Class<K> cls) {
        return cls.cast(this);
    }

    /**
     * 用于变化的方法
     *
     * @param alter 执行变换的对象
     */
    private Caster<T> lift(Alter<T> alter) {
        return create(new OnCastLift<>(mOnCast, alter));
    }

    /**
     * 创建一个新的Caster
     *
     * @param onCast 当发声时的对象
     */
    protected abstract Caster<T> create(OnCast<T> onCast);

    /**
     * 包装接收者
     *
     * @param receiver 接收者
     */
    protected abstract Acceptor<T> wrap(Receiver<T> receiver, boolean isOverride, boolean isMerge);

    /**
     * 创建一个接收时线程切换的接收者
     *
     * @param tReceiver 接收者
     */
    protected abstract Acceptor<T> createOnReceiveReceiver(Receiver<T> tReceiver, Switcher.Worker worker);

    protected Caster(OnCast<T> onCast) {
        mOnCast = onCast;
    }

    /**
     * 静态的发送方法
     *
     * @param acceptor 接收者
     * @param caster   发声者
     */
    private static <T extends Voice> Cast cast(Acceptor<T> acceptor, Caster<T> caster) {
        caster.mOnCast.call(acceptor);
        return acceptor;
    }

    public interface OnCast<T extends Voice> extends Action1<Receiver<T>> {
    }

    public interface Alter<T extends Voice> extends Action2<Receiver<T>, Receiver<T>> {
    }

    public interface Converter<T extends Voice, R extends Voice> extends Action2<Caster<T>, Caster<R>> {
    }
}
