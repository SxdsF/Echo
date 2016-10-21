package com.sxdsf.echo;

/**
 * com.sxdsf.echo.Switcher
 *
 * @author 孙博闻
 * @date 2016/7/12 15:06
 * @desc 用于线程切换
 */
public abstract class Switcher<T extends Voice> {

    /**
     * 创建一个用于线程切换的worker
     */
    protected abstract Worker createWorker();

    /**
     * 创建一个接收时线程切换的接收者
     *
     * @param tReceiver 接收者
     */
    protected abstract Acceptor<T> createOnReceiveReceiver(Receiver<T> tReceiver, Worker worker);

    /**
     * 真正执行线程切换的worker
     */
    public static abstract class Worker implements Cast {
        public abstract Cast switches(Action0 action0);
    }
}
