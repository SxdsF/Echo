package com.sxdsf.echo;

/**
 * com.sxdsf.echo.AlterReceiveOn
 *
 * @author 孙博闻
 * @date 2016/10/18 18:48
 * @desc 接收时用于线程变换的类
 */
final class AlterReceiveOn<T extends Voice> implements Alter<T> {

    private final Switcher<T> mSwitcher;

    AlterReceiveOn(Switcher<T> switcher) {
        mSwitcher = switcher;
    }

    @Override
    public Receiver<T> call(Receiver<T> receiver) {
        return mSwitcher.createOnReceiveReceiver(receiver, mSwitcher.createWorker());
    }
}
