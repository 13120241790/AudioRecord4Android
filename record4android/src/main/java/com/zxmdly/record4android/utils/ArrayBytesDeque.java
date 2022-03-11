package com.zxmdly.record4android.utils;

/**
 * Created by chengkai on 18-1-26.
 */

public class ArrayBytesDeque{

    public int max = 0;
    private byte[] cache = null; // 扩容byte，用来存储语音数据
    private byte[] tmpCopy = null;
    private byte[] wakeUpTempCopy = null;
    private int cacheLen = 0;

    public ArrayBytesDeque(int max) {
        this.max = max;
    }
    public boolean isEmpty() {
        return cacheLen == 0;
    }

    public synchronized void add(byte[] buffer, int offset, int len) {
        if(cache == null){
            if(len > max){
                len = max;
            }
            if(len == 0)
                return;
            cache = new byte[len];
            System.arraycopy(buffer,offset,cache,0,len);
            cacheLen = len;
        }else{
            if(cache.length - cacheLen >= len){
                System.arraycopy(buffer,offset,cache,cacheLen,len);
                cacheLen+=len;
            }else{
                if(len + cacheLen > max){
                    len = max - cacheLen;
                }
                byte[] tmp = new byte[len + cacheLen];
                System.arraycopy(cache,0,tmp,0,cacheLen);
                System.arraycopy(buffer,offset,tmp,cacheLen,len);
                cache = tmp;
                cacheLen = len + cacheLen;
            }
        }
    }

    /**
     * buffer大小超过限制，保留最新的buffer数据，清空就数据
     * @param limitSize buffer的最大长度  即：全局变量cache
     */
    public synchronized void splitCacheByLimit(int limitSize){
        if(cacheLen > limitSize){
            if(wakeUpTempCopy == null || wakeUpTempCopy.length < limitSize){
                wakeUpTempCopy = new byte[limitSize];
            }
            System.arraycopy(cache,cacheLen - limitSize,wakeUpTempCopy,0,limitSize);
            System.arraycopy(wakeUpTempCopy,0,cache,0,limitSize);
            cacheLen = limitSize;
        }
    }

    public synchronized int pop(byte[] buffer, int off, int len) {
        if(cache == null || cacheLen == 0)
            return 0;
        int readLen = 0;
        if(len <= cacheLen){
            readLen = len;
            System.arraycopy(cache,0,buffer,off,readLen);
            if(tmpCopy == null || tmpCopy.length < cacheLen - readLen){
                tmpCopy = new byte[cacheLen - readLen];
            }
            System.arraycopy(cache,readLen,tmpCopy,0,cacheLen - readLen);
            System.arraycopy(tmpCopy,0,cache,0,cacheLen - readLen);
            cacheLen -=readLen;
        }else {
            readLen = cacheLen;
            System.arraycopy(cache,0,buffer,off,readLen);
            cacheLen = 0;
        }
        return readLen;
    }

    public synchronized byte[] popAll() {
        if(cache == null || cacheLen == 0)
            return null;
        byte[] retByte = new byte[cacheLen];
        int ret = pop(retByte,0,retByte.length);
        if(ret == retByte.length){
            return retByte;
        }
        return null;
    }

    public synchronized void clear() {
        if(cache == null)
            return;
        cacheLen = 0;
        cache = null;
        tmpCopy = null;
    }

    public synchronized byte[] getCache() {
        return cache;
    }

    public synchronized int getCacheLen()
    {
        return cacheLen;
    }
}
