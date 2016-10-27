package com.sxdsf.echo.internal.switchers;

import android.support.annotation.NonNull;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

/**
 * com.sxdsf.echo.internal.switchers.EchoThreadFactory
 *
 * @author 孙博闻
 * @date 2016/10/24 10:49
 * @desc Echo这个库的线程工厂
 */

public final class EchoThreadFactory extends AtomicLong implements ThreadFactory {

    public static final ThreadFactory NONE = new ThreadFactory() {
        @Override
        public Thread newThread(@NonNull Runnable r) {
            throw new AssertionError("No threads allowed.");
        }
    };

    private final String prefix;

    public EchoThreadFactory(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public Thread newThread(@NonNull Runnable r) {
        Thread t = new Thread(r, prefix + incrementAndGet());
        t.setDaemon(true);
        return t;
    }
}
