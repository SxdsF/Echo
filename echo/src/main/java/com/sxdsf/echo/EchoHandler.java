package com.sxdsf.echo;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * com.sxdsf.echo.EchoHandler
 *
 * @author 孙博闻
 * @date 2016/10/14 16:37
 * @desc 在此库中使用的Handler
 */

public class EchoHandler extends Handler {
    /**
     * handler里等待锁的时间，同时也是在一次handler里处理的最大时间
     */
    private static final int MAX_HANDLE_TIME = 10;
    /**
     * 内部实现的一个阻塞的消息队列
     */
    private final Queue<Action0> mQueue = new LinkedList<>();
    /**
     * 用于实现阻塞消息队列的锁
     */
    private final Lock mLock = new ReentrantLock(true);
    /**
     * handleMessage是否在运行的标识
     */
    private volatile boolean mIsRunning = false;

    private static final String TAG = "MessageHandler";

    public EchoHandler(Looper looper) {
        super(looper);
    }

    /**
     * 返回handleMessage是否在运行
     *
     * @return
     */
    public boolean isRunning() {
        return mIsRunning;
    }

    /**
     * 将一个消息入队，会阻塞的
     *
     * @param temp 要入队的
     */
    public void enQueue(Action0 temp) {
        mLock.lock();
        try {
            mQueue.offer(temp);
        } finally {
            mLock.unlock();
        }
    }

    /**
     * 将所有的消息移除，会阻塞
     */
    public void clear() {
        mLock.lock();
        try {
            if (!mQueue.isEmpty()) {
                mQueue.clear();
            }
        } finally {
            mLock.unlock();
        }
    }

    /**
     * handleMessage从阻塞的队列里拿消息，默认会等待一个最大时长
     *
     * @return
     */
    private Action0 poll() {
        Action0 temp = null;
        try {
            if (mLock.tryLock(MAX_HANDLE_TIME, TimeUnit.MILLISECONDS)) {
                try {
                    temp = mQueue.poll();
                } finally {
                    mLock.unlock();
                }
            }
        } catch (InterruptedException e) {
            Log.v(TAG, e.getMessage());
        }
        return temp;
    }

    @Override
    public void handleMessage(android.os.Message msg) {
        // TODO Auto-generated method stub
        //这里做的处理是，只要handleMessage一执行，就将标识位置为true，
        //并且循环的从阻塞队列里取消息出来执行，如果取出来的是null，
        //那就跳出这个循环，并将标识位置为false，如果handleMessage运行
        //超过了最大时长，就跳出，并发一个消息到looper以保证此handleMessage
        //继续执行
        mIsRunning = true;
        long startTime = SystemClock.uptimeMillis();
        while (true) {
            Action0 temp = poll();
            if (temp == null) {
                mIsRunning = false;
                return;
            }
            try {
                temp.call();
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
            if (SystemClock.uptimeMillis() - startTime >= MAX_HANDLE_TIME) {
                sendMessage(obtainMessage());
                mIsRunning = false;
                return;
            }
        }
    }
}
