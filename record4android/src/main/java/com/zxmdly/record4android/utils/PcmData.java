package com.zxmdly.record4android.utils;

import android.media.AudioFormat;

/**
 * Created by chengkai on 18-1-26.
 */

public class PcmData {

    private byte[] bytesStereo = null;
    private byte[] rightByts = null;
    private byte[] leftByts = null;
    private int bytesStereoLen = 0;

    public byte[] reSampleBuffer = null;

    public PcmData() {
        bytesStereoLen = 1;
        this.rightByts = new byte[bytesStereoLen];
        this.leftByts = new byte[bytesStereoLen];
    }

    private int samplesRate = 0;
    private int samplesBits = 0;
    private int channels = 0;

    public boolean load(byte[] buffer, int len, int samplesRate, int samplesBits, int channels) {
        if (buffer == null || buffer.length < len)
            return false;
        this.samplesRate = samplesRate;
        this.samplesBits = samplesBits;
        this.channels = channels;
        switch (channels) {
            case AudioFormat.CHANNEL_IN_STEREO: {
                //拆分左右声道
                bytesStereo = buffer;
                bytesStereoLen = len;
                split(bytesStereo, len, samplesBits);
                break;
            }
            case AudioFormat.CHANNEL_IN_MONO: {
                bytesStereo = null;
                bytesStereoLen = len * 2;
                this.rightByts = buffer;
                this.leftByts = buffer;
                break;
            }
            default:
                break;
        }
        return true;
    }


    private void split(byte[] buffer, int len, int samplesBits) {
        if (buffer == null)
            return;
        if (rightByts == null || leftByts == null ||
                rightByts.length < len / 2 || leftByts.length < len / 2) {
            rightByts = new byte[len / 2];
            leftByts = new byte[len / 2];
        }

        int i = 0;
        switch (samplesBits) {
            case AudioFormat.ENCODING_PCM_16BIT:
                for (i = 0; i < len; i += 4) {
                    if ((i + 3) < buffer.length) {
                        //通过固件类型分别从相应声道读取音频流。
                        if (false) { // ArchManager.isQuanZhiA40() 如果是 全志 a40 芯片
                            rightByts[i / 2 + 0] = buffer[i + 0];
                            rightByts[i / 2 + 1] = buffer[i + 1];
                            leftByts[i / 2 + 0] = buffer[i + 2];
                            leftByts[i / 2 + 1] = buffer[i + 3];
                        } else {
                            leftByts[i / 2 + 0] = buffer[i + 0];
                            leftByts[i / 2 + 1] = buffer[i + 1];
                            rightByts[i / 2 + 0] = buffer[i + 2];
                            rightByts[i / 2 + 1] = buffer[i + 3];
                        }
                    }
                }
                break;
            default:
                return;
        }
    }

    //辗转相除获取分子分母
    private int GetMaxCommonDivisor(int maxnum, int minnum) {
        int temp = 0;
        /*使得max中存放较大的数,min存放较小的数*/
        if (maxnum < minnum) {
            temp = minnum;
            minnum = maxnum;
            maxnum = temp;
        }
        while (maxnum % minnum != 0) {
            temp = minnum;
            minnum = maxnum % minnum;
            maxnum = temp;
        }
        return minnum;
    }

    public int reSample(int needSampleRate, int needChannel) {
        int dstPos = 0;
        int bits = 0;
        byte[] dealBuffer = null;
        int dealBufferLen = 0;
        switch (needChannel) {
            case AudioFormat.CHANNEL_IN_STEREO: {
                bits = 4;
                dealBufferLen = bytesStereoLen;
                dealBuffer = bytesStereo;
                break;
            }
            case AudioFormat.CHANNEL_IN_LEFT: {
                bits = 2;
                dealBufferLen = bytesStereoLen / 2;
                dealBuffer = leftByts;
                break;
            }
            case AudioFormat.CHANNEL_IN_MONO:
            case AudioFormat.CHANNEL_IN_RIGHT: {
                bits = 2;
                dealBufferLen = bytesStereoLen / 2;
                dealBuffer = rightByts;
                break;
            }
            default: {
                break;
            }
        }
        if (dealBufferLen == 0 || dealBuffer == null)
            return 0;
        if (reSampleBuffer == null || reSampleBuffer.length < dealBufferLen) {
            reSampleBuffer = new byte[dealBufferLen];
        }
        int maxCommonDivisor = GetMaxCommonDivisor(samplesRate, needSampleRate);
        int rate_out = needSampleRate / maxCommonDivisor;//倍率  分子
        int rate_in = samplesRate / maxCommonDivisor;//倍率  分母
        if (rate_out >= rate_in) {
            System.arraycopy(dealBuffer, 0, reSampleBuffer, 0, dealBufferLen);
            return dealBufferLen;
        }
        for (int i = 0; i < dealBufferLen; i += (bits * rate_in)) {
            if ((i + rate_out * bits) < dealBufferLen) {
                System.arraycopy(dealBuffer, i, reSampleBuffer, dstPos, rate_out * bits);
                dstPos += rate_out * bits;
            }
        }
        return dstPos;
    }

    public static void ChangePCMVolume(byte[] data, float gain) { //音量调整需替换为 Java 方法 KtvRecorder Native 弃用
        if (gain == 1)
            return;
//        KtvRecorder.ChangePCMVolume(data, 0, data.length,gain);
    }

    /**
     * 应用增益
     * @param data
     * @param offset 初始偏移byte数
     * @param length 应用长度，byte数
     * @param gain 线性增益值
     */
    public static void ChangePCMVolume(byte[] data, int offset, int length, float gain) //音量调整需替换为 Java 方法 KtvRecorder Native 弃用
    {
        if(gain == 1)
            return;
//        KtvRecorder.ChangePCMVolume(data, offset, data.length,gain);
    }
}
