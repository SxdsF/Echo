package com.sxdsf.echo.sample;

import com.sxdsf.echo.Caster;

/**
 * com.sxdsf.echo.sample.Converter
 *
 * @author 孙博闻
 * @date 2016/10/14 15:03
 * @desc 文件描述
 */

public abstract class Converter implements com.sxdsf.echo.Converter<Response, Response> {
    @Override
    public Caster<Response> call(Caster<Response> responseCallbackCaster) {
        return convert((Call) responseCallbackCaster);
    }

    public abstract Call convert(Call call);
}
