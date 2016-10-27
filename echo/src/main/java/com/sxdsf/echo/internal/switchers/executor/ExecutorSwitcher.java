package com.sxdsf.echo.internal.switchers.executor;

import com.sxdsf.echo.Switcher;

import java.util.concurrent.Executor;

/**
 * com.sxdsf.echo.internal.switchers.executor.ExecutorSwitcher
 *
 * @author 孙博闻
 * @date 2016/10/27 18:49
 * @desc 指定线程池的线程切换者
 */

public class ExecutorSwitcher extends Switcher {

    private final Executor mExecutor;

    public ExecutorSwitcher(Executor executor) {
        mExecutor = executor;
    }

    @Override
    public Worker createWorker() {
        return new ExecutorWorker(mExecutor);
    }
}
