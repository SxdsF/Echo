package com.sxdsf.echo;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * com.sxdsf.echo.CastList
 *
 * @author 孙博闻
 * @date 2016/10/14 17:52
 * @desc 声音发出者和接收者关系的list
 */
public class CastList implements Cast {

    private List<Cast> mCasts;
    private volatile boolean mUnReceived;

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
        // call after leaving the synchronized block so we're not holding a lock while executing this
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
                // if we removed successfully we then need to call unsubscribe on it (outside of the lock)
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

    /* perf support */
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

    /**
     * Returns true if this composite is not mUnReceived and contains mCastList.
     *
     * @return {@code true} if this composite is not mUnReceived and contains mCastList.
     */
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
            // we will only get here once
            unReceiveFromAll(list);
        }
    }
}
