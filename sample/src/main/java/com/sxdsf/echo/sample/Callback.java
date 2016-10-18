package com.sxdsf.echo.sample;

import com.sxdsf.echo.Receiver;

/**
 * com.sxdsf.echo.sample.Callback
 *
 * @author 孙博闻
 * @date 2016/8/19 17:40
 * @desc 文件描述
 */
public interface Callback extends Receiver<Response> {

    /**
     * 在任务开始时回调
     */
    void onStart();

    /**
     * 在任务错误时回调
     *
     * @param t 异常
     */
    void onError(Throwable t);

    /**
     * 在任务成功时回调
     *
     * @param response 成功时回调的内容
     */
    void onSuccess(Response response);

    /**
     * 在任务取消时回调
     */
    void onCancel();
}
