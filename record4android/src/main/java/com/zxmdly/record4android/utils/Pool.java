package com.zxmdly.record4android.utils;

/**
 * Created by wow-wxh on 2019/5/8.
 */

public class Pool<T> {
    private final Object[] mPool;
    private int mPoolSize;

    public Pool(int maxPoolSize) {
        if (maxPoolSize <= 0) {
            throw new IllegalArgumentException("The max pool size must be > 0");
        }
        mPool = new Object[maxPoolSize];
    }

    public synchronized T acquire() {
        if (mPoolSize > 0) {
            final int lastPooledIndex = mPoolSize - 1;
            T instance = (T) mPool[lastPooledIndex];
            mPool[lastPooledIndex] = null;
            mPoolSize--;
            return instance;
        }
        return null;
    }


    public synchronized boolean release(T instance) {
        if (isInPool(instance)) {
            throw new IllegalStateException("Already in the pool!");
        }
        if (mPoolSize < mPool.length) {
            mPool[mPoolSize] = instance;
            mPoolSize++;
            return true;
        }
        return false;
    }

    private synchronized boolean isInPool(T instance) {
        for (int i = 0; i < mPoolSize; i++) {
            if (mPool[i] == instance) {
                return true;
            }
        }
        return false;
    }
}