package com.sxdsf.echo;

/**
 * com.sxdsf.echo.Converter
 *
 * @author 孙博闻
 * @date 2016/7/12 15:16
 * @desc caster的整体变化
 */
public interface Converter<T extends Voice, R extends Voice, K extends Receiver<T>, V extends Receiver<R>> extends Action2<Caster<T, K>, Caster<R, V>> {
}
