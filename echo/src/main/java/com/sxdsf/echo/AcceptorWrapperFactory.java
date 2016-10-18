package com.sxdsf.echo;

/**
 * com.sxdsf.echo.AcceptorWrapperFactory
 *
 * @author 孙博闻
 * @date 2016/10/9 20:40
 * @desc 接收者包装类工厂
 */
public interface AcceptorWrapperFactory<T extends Voice, R extends Receiver<T>> {

    AcceptorWrapper<T, R> createWrapper(R receiver);
}
