package com.zxmdly.record4android;

import android.media.AudioRecord;
import android.media.MediaRecorder.AudioSource;
import android.util.Log;

/**
 * @author zhouxuming
 * @date 2022/3/3 2:40 下午
 * 基于 Android SDK AudioRecord 包装音频录制能力
 * AudioRecord 提供了 3 个方法来控制音频数据的读取，分别是开始录制startRecording()、读一批音频数据read()、停止录制stop()，这 3 个方法通常用下面的模板来组合：
 * audioRecord.startRecording()
 * while(是否继续录制){ audioRecord.read() }
 * audioRecord.stop()
 *
 */
public class AudioRecorder implements IRecorder{

  public static final String TAG = AudioRecorder.class.getSimpleName();

  private volatile static AudioRecorder instance;

  private AudioRecorder(){}

  public static AudioRecorder getInstance(){
    if (instance == null) {
      synchronized (AudioRecorder.class){
        if (instance == null) {
          instance = new AudioRecorder();
        }
      }
    }
    return instance;
  }

  private AudioRecord audioRecord;

  @Override
  public void init(int sampleRateInHz, int channelConfig, int audioFormat) {
    Log.e(TAG,"init params sampleRateInHz : " + sampleRateInHz);
    Log.e(TAG,"init params channelConfig : " + channelConfig);
    Log.e(TAG,"init params audioFormat : " + audioFormat);
    int minBufferSize = AudioRecord.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat);
    // AudioRecord.getMinBufferSize() 来获取最小一帧的buffer 大小，这样我们能保证每一帧都能被录制

    try {
      audioRecord = new AudioRecord(
          AudioSource.DEFAULT, //采集来源  0 默认 1 麦克风
          sampleRateInHz, channelConfig, audioFormat, //采样率 声道 量化精度
          minBufferSize * 2);
    }catch (Exception e){
      e.printStackTrace();
    }

    if (audioRecord == null) {
      Log.e(TAG,"AudioRecord init instance is empty ~! ");
      return;
    }

    if (audioRecord.getState() == AudioRecord.STATE_UNINITIALIZED) {//未初始化
      Log.e(TAG,"AudioRecord init state is uninit ~! ");
    }
    //如果初始化失败有可能是硬件资源被申请了，但是没有释放导致该进程初始化失败
  }

  @Override
  public void start() {
    if (checkState()) {
      if (audioRecord.getRecordingState() == AudioRecord.RECORDSTATE_STOPPED) {
        audioRecord.startRecording();//https://www.apiref.com/android-zh/android/media/MediaSyncEvent.html
        if (audioRecord.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING) {
          Log.e(TAG,"AudioRecord startRecording ~! ");
        }
      }
    }
  }

  @Override
  public int readByByte(byte[] audioData, int offsetInBytes, int sizeInBytes) {
    if (checkState()) {
     return audioRecord.read(audioData, offsetInBytes, sizeInBytes);//audioRecord 存在 5 个 read 重载方法可用
    }
    return 0;//为0或者为负数读取错误
  }

  @Override
  public void stop() {
    if (checkState()) {
      if (audioRecord.getRecordingState() != AudioRecord.RECORDSTATE_STOPPED) {
        audioRecord.stop();
      }
    }
  }

  @Override
  public void release() {
    if (checkState()) {
      audioRecord.release();
    }
  }

  @Override
  public boolean isRecording() {
    return audioRecord.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING;
  }

  private boolean checkState() {
    boolean bool = audioRecord != null && audioRecord.getState() == AudioRecord.STATE_INITIALIZED;
    Log.e(TAG, "checkState : " + bool);
    return bool;
  }
}
