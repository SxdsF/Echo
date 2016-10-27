package com.sxdsf.echo;

import com.sxdsf.echo.internal.utils.CastList;

/**
 * com.sxdsf.echo.AcceptorWrapper
 *
 * @author 孙博闻
 * @date 2016/10/9 17:15
 * @desc 包装Receiver的Acceptor，用于将实现者包装，让其在核心库内使用的是Acceptor
 */
public abstract class AcceptorWrapper<T extends Voice, R extends Receiver<T>> extends Acceptor<T> {

    /**
     * 被包装者
     */
    private final R mWrapped;

    public AcceptorWrapper(R wrapped, boolean isOverride, boolean isMerge) {
        if (wrapped instanceof Acceptor) {
            mWrapped = isOverride ? wrapped : null;
            mCastList = isMerge ? ((Acceptor) wrapped).mCastList : new CastList();
        } else {
            mWrapped = wrapped;
            mCastList = new CastList();
        }
    }

    /**
     * 获取被包装者
     */
    protected R getWrapped() {
        return mWrapped;
    }
}
