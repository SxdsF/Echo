package com.sxdsf.echo.sample;

import com.sxdsf.echo.Action0;
import com.sxdsf.echo.Cast;
import com.sxdsf.echo.CastList;
import com.sxdsf.echo.Casts;
import com.sxdsf.echo.Switcher;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * com.sxdsf.echo.sample.AsyncThreadWorker
 *
 * @author 孙博闻
 * @date 2016/10/14 15:58
 * @desc 文件描述
 */

public class AsyncThreadWorker extends Switcher.Worker {

    private final ExecutorService mExecutorService;
    private volatile boolean mUnReceived;

    public AsyncThreadWorker(ExecutorService executorService) {
        mExecutorService = executorService;
    }

    @Override
    public Cast switches(Action0 action0) {
        if (mUnReceived) {
            return Casts.unReceived();
        }
        ScheduledAction run = new ScheduledAction(action0);
        Future<?> f;
        f = mExecutorService.submit(run);
        run.add(f);

        return run;
    }

    @Override
    public boolean isUnReceived() {
        return mUnReceived;
    }

    @Override
    public void unReceive() {
        mUnReceived = true;
        mExecutorService.shutdownNow();
    }

    private static class ScheduledAction implements Runnable, Cast {
        final CastList cancel;
        final Action0 action;

        public ScheduledAction(Action0 action) {
            this.action = action;
            this.cancel = new CastList();
        }

        @Override
        public void run() {
            action.call();
        }

        public void add(final Future<?> f) {
            cancel.add(new FutureCompleter(f));
        }

        @Override
        public boolean isUnReceived() {
            return cancel.isUnReceived();
        }

        @Override
        public void unReceive() {
            if (!cancel.isUnReceived()) {
                cancel.unReceive();
            }
        }

        final class FutureCompleter implements Cast {
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
    }
}
