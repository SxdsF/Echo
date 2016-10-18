package com.sxdsf.echo.sample;

import android.os.Looper;

import com.sxdsf.echo.Action0;
import com.sxdsf.echo.Cast;
import com.sxdsf.echo.Casts;
import com.sxdsf.echo.EchoHandler;
import com.sxdsf.echo.EchoHandlerWorker;

/**
 * com.sxdsf.echo.sample.MainThreadWorker
 *
 * @author 孙博闻
 * @date 2016/10/14 15:43
 * @desc 文件描述
 */

public class MainThreadWorker extends EchoHandlerWorker {

    public MainThreadWorker(EchoHandler handler) {
        super(handler);
        if (Looper.getMainLooper() != handler.getLooper()) {
            throw new IllegalArgumentException("not main looper");
        }
    }

    @Override
    public Cast switches(Action0 action0) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            action0.call();
            return Casts.unReceived();
        }
        return super.switches(action0);
    }
}
