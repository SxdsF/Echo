package com.sxdsf.echo;

/**
 * com.sxdsf.echo.AlterCastOn
 *
 * @author 孙博闻
 * @date 2016/10/19 11:54
 * @desc 发送时用于线程变换的类
 */
final class AlterCastOn<T extends Voice> implements OnCast<T> {

    private final Caster<T> mOriginal;
    private final Switcher<T> mSwitcher;

    AlterCastOn(Caster<T> original, Switcher<T> switcher) {
        mOriginal = original;
        mSwitcher = switcher;
    }

    @Override
    public void call(final Receiver<T> receiver) {
        if (receiver instanceof Acceptor) {
            final Acceptor<T> acceptor = (Acceptor<T>) receiver;
            Switcher.Worker worker = mSwitcher.createWorker();
            acceptor.add(worker);
            worker.switches(new Action0() {
                @Override
                public void call() {
                    mOriginal.cast(mOriginal.wrap(acceptor, true, true));
                }
            });
        }
    }
}
