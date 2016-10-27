package com.sxdsf.echo.internal.switchers.newthread;

import com.sxdsf.echo.Switcher;

import java.util.concurrent.ThreadFactory;

/**
 * com.sxdsf.echo.internal.switchers.newthread.NewThreadSwitcher
 *
 * @author 孙博闻
 * @date 2016/10/27 18:15
 * @desc 每次创建一个新线程的线程切换者
 */

public class NewThreadSwitcher extends Switcher {

    private final ThreadFactory mThreadFactory;

    public NewThreadSwitcher(ThreadFactory threadFactory) {
        mThreadFactory = threadFactory;
    }

    @Override
    public Worker createWorker() {
        return new NewThreadWorker(mThreadFactory);
    }
}
