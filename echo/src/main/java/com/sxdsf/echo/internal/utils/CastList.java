package com.sxdsf.echo.internal.utils;

import com.sxdsf.echo.Cast;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * com.sxdsf.echo.internal.utils.CastList
 *
 * @author 孙博闻
 * @date 2016/10/14 17:52
 * @desc 声音发出者和接收者关系的list
 */
public final class CastList implements Cast {

    private List<Cast> mCasts;
    private volatile boolean mUnReceived;

    public CastList() {
    }

    public CastList(final Cast... subscriptions) {
        this.mCasts = new LinkedList<>(Arrays.asList(subscriptions));
    }

    public CastList(Cast s) {
        this.mCasts = new LinkedList<>();
        this.mCasts.add(s);
    }

    public void add(final Cast s) {
        if (s.isUnReceived()) {
            return;
        }
        if (!mUnReceived) {
            synchronized (this) {
                if (!mUnReceived) {
                    List<Cast> subs = mCasts;
                    if (subs == null) {
                        subs = new LinkedList<>();
                        mCasts = subs;
                    }
                    subs.add(s);
                    return;
                }
            }
        }
        s.unReceive();
    }

    public void remove(final Cast s) {
        if (!mUnReceived) {
            boolean unReceived;
            synchronized (this) {
                List<Cast> subs = mCasts;
                if (mUnReceived || subs == null) {
                    return;
                }
                unReceived = subs.remove(s);
            }
            if (unReceived) {
                s.unReceive();
            }
        }
    }

    private static void unReceiveFromAll(Collection<Cast> casts) {
        if (casts == null) {
            return;
        }
        for (Cast s : casts) {
            s.unReceive();
        }
    }

    public void clear() {
        if (!mUnReceived) {
            List<Cast> list;
            synchronized (this) {
                list = mCasts;
                mCasts = null;
            }
            unReceiveFromAll(list);
        }
    }

    public boolean hasCasts() {
        if (!mUnReceived) {
            synchronized (this) {
                return !mUnReceived && mCasts != null && !mCasts.isEmpty();
            }
        }
        return false;
    }

    @Override
    public boolean isUnReceived() {
        return mUnReceived;
    }

    @Override
    public void unReceive() {
        if (!mUnReceived) {
            List<Cast> list;
            synchronized (this) {
                if (mUnReceived) {
                    return;
                }
                mUnReceived = true;
                list = mCasts;
                mCasts = null;
            }
            unReceiveFromAll(list);
        }
    }
}
