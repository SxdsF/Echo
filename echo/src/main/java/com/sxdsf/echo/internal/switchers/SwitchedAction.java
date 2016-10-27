package com.sxdsf.echo.internal.switchers;

import com.sxdsf.echo.Cast;
import com.sxdsf.echo.casts.CompositeCast;
import com.sxdsf.echo.functions.Action0;
import com.sxdsf.echo.internal.utils.CastList;

import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * com.sxdsf.echo.internal.switchers.SwitchedAction
 *
 * @author 孙博闻
 * @date 2016/10/25 15:25
 * @desc 放到线程池里的实际类，其他的类都会用此类包装放到线程池里去执行
 */

public final class SwitchedAction implements Runnable, Cast {
    private final CastList mCancel;
    private final Action0 mAction;

    public SwitchedAction(Action0 action) {
        mAction = action;
        mCancel = new CastList();
    }

    public SwitchedAction(Action0 action, CompositeCast compositeCast) {
        mAction = action;
        mCancel = new CastList(new Remover(this, compositeCast));
    }

    public SwitchedAction(Action0 action, CastList castList) {
        mAction = action;
        mCancel = new CastList(new Remover2(this, castList));
    }

    @Override
    public void run() {
        mAction.call();
    }

    public void add(final Future<?> f) {
        mCancel.add(new FutureCompleter(f));
    }

    public void addParent(CompositeCast parent) {
        mCancel.add(new Remover(this, parent));
    }

    @Override
    public boolean isUnReceived() {
        return mCancel.isUnReceived();
    }

    @Override
    public void unReceive() {
        if (!mCancel.isUnReceived()) {
            mCancel.unReceive();
        }
    }

    private final class FutureCompleter implements Cast {
        private final Future<?> f;

        FutureCompleter(Future<?> f) {
            this.f = f;
        }

        @Override
        public boolean isUnReceived() {
            return f.isCancelled();
        }

        @Override
        public void unReceive() {
            f.cancel(true);
        }
    }

    private static final class Remover extends AtomicBoolean implements Cast {

        final SwitchedAction s;
        final CompositeCast parent;

        Remover(SwitchedAction s, CompositeCast parent) {
            this.s = s;
            this.parent = parent;
        }

        @Override
        public boolean isUnReceived() {
            return s.isUnReceived();
        }

        @Override
        public void unReceive() {
            if (compareAndSet(false, true)) {
                parent.remove(s);
            }
        }

    }

    private static final class Remover2 extends AtomicBoolean implements Cast {

        final SwitchedAction s;
        final CastList parent;

        Remover2(SwitchedAction s, CastList parent) {
            this.s = s;
            this.parent = parent;
        }

        @Override
        public boolean isUnReceived() {
            return s.isUnReceived();
        }

        @Override
        public void unReceive() {
            if (compareAndSet(false, true)) {
                parent.remove(s);
            }
        }

    }
}
