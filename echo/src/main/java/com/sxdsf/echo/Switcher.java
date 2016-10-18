package com.sxdsf.echo;

/**
 * com.sxdsf.echo.Switcher
 *
 * @author 孙博闻
 * @date 2016/7/12 15:06
 * @desc 用于线程切换
 */
public abstract class Switcher<T extends Voice, R extends Receiver<T>> {

    /**
     * 发声时的线程切换
     *
     * @param caster 被切换的发声者
     */
    Caster<T, R> castOn(Caster<T, R> caster) {
        return createOnCastCaster(new OnCastSwitch<>(caster, this));
    }

    /**
     * 接收时的线程切换
     *
     * @param caster 被切换的发声者
     */
    Caster<T, R> receiveOn(Caster<T, R> caster) {
        caster.mOnCast = new OnReceiveSwitch<>(caster.mOnCast, new SwitchAlter<>(this));
        return caster;
    }

    /**
     * 创建一个用于线程切换的worker
     */
    protected abstract Worker createWorker();

    /**
     * 创建一个发声时线程切换的接收者
     *
     * @param tReceiver 接收者
     */
    protected abstract R createOnCastReceiver(Receiver<T> tReceiver);

    /**
     * 创建一个接收时线程切换的接收者
     *
     * @param tReceiver 接收者
     */
    protected abstract R createOnReceiveReceiver(Receiver<T> tReceiver);

    /**
     * 创建一个发声时的线程切换的发声者
     *
     * @param onCast 在发声时
     */
    protected abstract Caster<T, R> createOnCastCaster(OnCast<T> onCast);

    /**
     * 真正执行线程切换的worker
     */
    public static abstract class Worker implements Cast {
        public abstract Cast switches(Action0 action0);
    }

    /**
     * 执行线程变换的alter
     */
    private static class SwitchAlter<T extends Voice, R extends Receiver<T>> implements Alter<T> {

        private final Switcher<T, R> mSwitcher;

        private SwitchAlter(Switcher<T, R> switcher) {
            mSwitcher = switcher;
        }

        @Override
        public Receiver<T> call(Receiver<T> tReceiver) {
            return mSwitcher.createOnReceiveReceiver(tReceiver);
        }
    }

    /**
     * 当接收时的线程切换
     */
    private static class OnReceiveSwitch<T extends Voice> implements OnCast<T> {

        private final OnCast<T> mParent;
        private final Alter<T> mAlter;

        private OnReceiveSwitch(OnCast<T> parent, Alter<T> alter) {
            mParent = parent;
            mAlter = alter;
        }

        @Override
        public void call(Receiver<T> receiver) {
            mParent.call(mAlter.call(receiver));
        }
    }

    /**
     * 当发送时的线程切换
     */
    private static class OnCastSwitch<T extends Voice, R extends Receiver<T>> implements OnCast<T> {

        private final Caster<T, R> mOriginal;
        private final Switcher<T, R> mSwitcher;

        private OnCastSwitch(Caster<T, R> original, Switcher<T, R> switcher) {
            mOriginal = original;
            mSwitcher = switcher;
        }

        @Override
        public void call(final Receiver<T> receiver) {
            if (receiver instanceof Acceptor) {
                Acceptor<T> acceptor = (Acceptor<T>) receiver;
                Worker worker = mSwitcher.createWorker();
                acceptor.add(worker);
                worker.switches(new Action0() {
                    @Override
                    public void call() {
                        mOriginal.cast(mSwitcher.createOnCastReceiver(receiver));
                    }
                });
            }
        }
    }
}
