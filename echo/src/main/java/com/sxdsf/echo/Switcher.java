package com.sxdsf.echo;

import com.sxdsf.echo.functions.Action0;

/**
 * com.sxdsf.echo.Switcher
 *
 * @author 孙博闻
 * @date 2016/7/12 15:06
 * @desc 用于线程切换
 */
public abstract class Switcher {

    /**
     * 创建一个用于线程切换的worker
     */
    public abstract Worker createWorker();

    /**
     * 真正执行线程切换的worker
     */
    public static abstract class Worker implements Cast {
        public abstract Cast switches(Action0 action0);
    }
}
