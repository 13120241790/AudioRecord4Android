package com.zxmdly.record4android;

import android.media.AudioFormat;

/**
 * @author zhouxuming
 * @date 2022/3/3 2:37 下午
 */
public interface IRecorder {

  /**
   *
   * @param sampleRateInHz the sample rate expressed in Hertz.
   *  {@link AudioFormat#SAMPLE_RATE_UNSPECIFIED} is not permitted.
   * @param channelConfig  describes the configuration of the audio channels.
   *      *   See {@link AudioFormat#CHANNEL_IN_MONO} and
   *      *   {@link AudioFormat#CHANNEL_IN_STEREO}
   * @param audioFormat the format in which the audio data is represented.
   *      *   See {@link AudioFormat#ENCODING_PCM_16BIT}.
   */
  void init(int sampleRateInHz, int channelConfig, int audioFormat);//初始化
  void start();//开始录制

  /**
   * Reads audio data from the audio hardware for recording into a byte array.
   * The format specified in the AudioRecord constructor should be
   * {@link AudioFormat#ENCODING_PCM_8BIT} to correspond to the data in the array.
   * @param audioData the array to which the recorded audio data is written.
   * @param offsetInBytes index in audioData from which the data is written expressed in bytes.
   * @param sizeInBytes the number of requested bytes.
   * @return zero or the positive number of bytes that were read, or one of the following
   *    error codes. The number of bytes will not exceed sizeInBytes.
   */
  int readByByte(byte[] audioData, int offsetInBytes, int sizeInBytes);//读取一批数据
  void stop();//停止录制
  void release();//释放录制资源
  boolean isRecording();//是否正在录制
}
