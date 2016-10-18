package com.sxdsf.echo;

/**
 * com.sxdsf.echo.Casts
 *
 * @author 孙博闻
 * @date 2016/10/14 15:28
 * @desc Cast的集合
 */
public final class Casts {

    private static final UnReceived UN_RECEIVED = new UnReceived();

    private Casts() {
        throw new IllegalStateException("No instances!");
    }

    public static Cast unReceived() {
        return UN_RECEIVED;
    }

    private static final class UnReceived implements Cast {

        @Override
        public boolean isUnReceived() {
            return true;
        }

        @Override
        public void unReceive() {

        }
    }
}
