package com.sxdsf.echo.internal.switchers.queue;

/**
 * com.sxdsf.echo.internal.switchers.queue.Message
 *
 * @author 孙博闻
 * @date 2016/10/27 19:20
 * @desc 在消息队列中传递的消息
 */

public class Message {

    final TaskQueueWorker mWorker;

    public Message(TaskQueueWorker worker) {
        mWorker = worker;
    }
}
