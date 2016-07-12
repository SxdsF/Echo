package com.sxdsf.echo;

/**
 * com.sxdsf.echo.Caster
 *
 * @author 孙博闻
 * @date 2016/7/12 13:58
 * @desc 发声者
 */
public class Caster<T extends Voice> {

    protected OnCast<T> mOnCast;

    public void cast(Receiver<T> receiver) {
        mOnCast.call(receiver);
    }

    public Caster<T> receiveOn(Switcher switcher) {
        mOnCast = new OnReceiveSwitch<>(mOnCast, new SwitchAlter<T>(switcher));
        return this;
    }

    public <R extends Voice> Caster<R> convert(Converter<T, R> converter) {
        return converter.call(this);
    }

    protected Caster(OnCast<T> onCast) {
        mOnCast = onCast;
    }

    public static <T extends Voice> Caster create(OnCast<T> onCast) {
        return new Caster<>(onCast);
    }

    private static class SwitchAlter<T extends Voice> implements Alter<T> {

        private Switcher mSwitcher;

        public SwitchAlter(Switcher switcher) {
            mSwitcher = switcher;
        }

        @Override
        public Receiver<T> call(Receiver<T> tReceiver) {
            return mSwitcher.switches(tReceiver);
        }
    }

    private static class OnReceiveSwitch<T extends Voice> implements OnCast<T> {

        private OnCast<T> mParent;
        private Alter<T> mAlter;

        public OnReceiveSwitch(OnCast<T> parent, Alter<T> alter) {
            mParent = parent;
            mAlter = alter;
        }

        @Override
        public void call(Receiver<T> tReceiver) {
            mParent.call(mAlter.call(tReceiver));
        }
    }
}
