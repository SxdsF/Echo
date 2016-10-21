package com.sxdsf.echo;

/**
 * com.sxdsf.echo.Acceptor
 *
 * @author 孙博闻
 * @date 2016/8/22 11:42
 * @desc 接收者，在核心库内使用的都是此类或子类
 */
public class Acceptor<T extends Voice> implements Receiver<T>, Cast {

    CastList mCastList;

    @Override
    public boolean isUnReceived() {
        return mCastList.isUnReceived();
    }

    @Override
    public void unReceive() {
        mCastList.unReceive();
    }

    public final void add(Cast cast) {
        mCastList.add(cast);
    }
}
