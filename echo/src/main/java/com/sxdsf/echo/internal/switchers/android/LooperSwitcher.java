package com.sxdsf.echo.internal.switchers.android;

import android.os.Looper;

import com.sxdsf.echo.Switcher;

/**
 * com.sxdsf.echo.internal.switchers.android.LooperSwitcher
 *
 * @author 孙博闻
 * @date 2016/10/26 18:11
 * @desc Android中使用Looper的线程切换者
 */

public class LooperSwitcher extends Switcher {

    private final EchoHandler mHandler;

    public LooperSwitcher(Looper looper) {
        mHandler = new EchoHandler(looper);
    }

    @Override
    public Worker createWorker() {
        return new EchoHandlerWorker(mHandler);
    }
}
