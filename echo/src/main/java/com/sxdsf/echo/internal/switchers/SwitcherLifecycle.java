package com.sxdsf.echo.internal.switchers;

/**
 * com.sxdsf.echo.internal.switchers.SwitcherLifecycle
 *
 * @author 孙博闻
 * @date 2016/10/25 18:26
 * @desc 线程切换者的生命周期接口
 */

public interface SwitcherLifecycle {

    /**
     * 生命周期开始
     */
    void start();

    /**
     * 生命周期结束
     */
    void shutdown();
}
