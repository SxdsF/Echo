package com.sxdsf.echo.casts;

import com.sxdsf.echo.Cast;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * com.sxdsf.echo.casts.CompositeCast
 *
 * @author 孙博闻
 * @date 2016/10/25 15:30
 * @desc 混合的Cast的集合
 */

public final class CompositeCast implements Cast {

    private Set<Cast> mCasts;
    private volatile boolean mUnReceived;

    public CompositeCast() {
    }

    public CompositeCast(final Cast... mCasts) {
        this.mCasts = new HashSet<>(Arrays.asList(mCasts));
    }

    public void add(final Cast s) {
        if (s.isUnReceived()) {
            return;
        }
        if (!mUnReceived) {
            synchronized (this) {
                if (!mUnReceived) {
                    if (mCasts == null) {
                        mCasts = new HashSet<>(4);
                    }
                    mCasts.add(s);
                    return;
                }
            }
        }
        s.unReceive();
    }

    public void addAll(final Cast... casts) {
        if (!mUnReceived) {
            synchronized (this) {
                if (!mUnReceived) {
                    if (this.mCasts == null) {
                        this.mCasts = new HashSet<>(casts.length);
                    }

                    for (Cast s : casts) {
                        if (!s.isUnReceived()) {
                            this.mCasts.add(s);
                        }
                    }
                    return;
                }
            }
        }

        for (Cast s : casts) {
            s.unReceive();
        }
    }

    public void remove(final Cast s) {
        if (!mUnReceived) {
            boolean unReceive;
            synchronized (this) {
                if (mUnReceived || mCasts == null) {
                    return;
                }
                unReceive = mCasts.remove(s);
            }
            if (unReceive) {
                s.unReceive();
            }
        }
    }

    public void clear() {
        if (!mUnReceived) {
            Collection<Cast> unReceive;
            synchronized (this) {
                if (mUnReceived || mCasts == null) {
                    return;
                } else {
                    unReceive = mCasts;
                    mCasts = null;
                }
            }
            unReceiveFromAll(unReceive);
        }
    }

    private static void unReceiveFromAll(Collection<Cast> casts) {
        if (casts == null) {
            return;
        }
        for (Cast s : casts) {
            try {
                s.unReceive();
            } catch (Throwable e) {
                e.printStackTrace();
            }
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
            Collection<Cast> unReceive;
            synchronized (this) {
                if (mUnReceived) {
                    return;
                }
                mUnReceived = true;
                unReceive = mCasts;
                mCasts = null;
            }
            unReceiveFromAll(unReceive);
        }
    }
}
