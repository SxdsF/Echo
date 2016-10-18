package com.sxdsf.echo;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * com.sxdsf.echo.NeedRewrite
 *
 * @author 孙博闻
 * @date 2016/10/10 15:29
 * @desc 标志着需要被重写的方法（子类需要重新命名一个方法，来调用此方法）
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface NeedRewrite {
}
