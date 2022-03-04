package com.zxmdly.record4android;

import android.media.AudioFormat;
import android.util.Log;
import java.util.Arrays;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author zhouxuming
 * @date 2022/3/3 3:54 下午 负责音频采集与分发
 */
public class AudioRecordManager extends Thread {

  public static final String TAG = AudioRecordManager.class.getSimpleName();

  private volatile static AudioRecordManager instance;

  private AudioRecordManager() {
    audioRecorder = AudioRecorder.getInstance();
  }

  public static AudioRecordManager getInstance() {
    if (instance == null) {
      synchronized (AudioRecordManager.class) {
        if (instance == null) {
          instance = new AudioRecordManager();
        }
      }
    }
    return instance;
  }


  private final IRecorder audioRecorder;
  private static final int defaultSamplesRate = 48000; //默认采样率
  private static final int defaultSamplesBits = AudioFormat.ENCODING_PCM_16BIT; //默认比特码值
  private static final int defaultChannels = AudioFormat.CHANNEL_IN_STEREO;//默认立体声声道

  private boolean isCollecting = true;

  // put 入队
  // offer 入队 offer仅仅对put方法改动了一点点，当队列没有可用元素的时候，不同于put方法的阻塞等待，offer方法直接方法false。
  // take 出队  队列为空，阻塞等待  队列不为空，从队首获取并移除一个元素
  // poll 出队
  private LinkedBlockingQueue<byte[]> producerQueue = new LinkedBlockingQueue<>();
  private LinkedBlockingQueue<byte[]> consumerQueue = new LinkedBlockingQueue<>();

  public void init() {
    audioRecorder.init(defaultSamplesRate, defaultChannels, defaultSamplesBits);
  }
//
//  public void init(int sampleRateInHz, int channelConfig, int audioFormat) {
//    audioRecorder.init(sampleRateInHz, channelConfig, audioFormat);
//  }

  public void startRecording() {
    start();
  }

  public void pauseProduce(){
    isCollecting = false;
  }

  public void resumeProduce(){
    isCollecting = true;
  }

  @Override
  public void run() {
    audioRecorder.init(defaultSamplesRate, defaultChannels, defaultSamplesBits);


    final int bufferLength = 10 * 1024;
    producerQueue.offer(new byte[bufferLength]);
    producerQueue.offer(new byte[bufferLength]);
    producerQueue.offer(new byte[bufferLength]);

//    while (true) {//采集线程一直循环采集音频数据
      try {
        Thread.sleep(500);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      audioRecorder.start();

      Thread dispatchThread = new Thread(new Runnable() {//分发线程
        @Override
        public void run() {
          while (true) {
//            try {
//              Thread.sleep(50);
//            } catch (InterruptedException e) {
//              e.printStackTrace();
//            }
            try {
              byte[] bytes = consumerQueue.take();
              try {
                  //TODO 外界分发
//                Log.e(TAG,"dispatch : bytes.length " + bytes.length + " obj : " + bytes.toString() + " array : " + Arrays.toString(bytes));
                Log.e(TAG,"dispatch : bytes.length " + bytes.length + " obj : " + bytes.toString());
              } catch (Exception e) {
                e.printStackTrace();
              } finally {
                producerQueue.offer(bytes);
              }
            } catch (InterruptedException e) {
              e.printStackTrace();
              break;
            }
          }
        }
      });
      dispatchThread.start();
      Log.e(TAG, "start record ~");
      while (isCollecting) {//暂停队列采集
//        try {
//          Thread.sleep(50);
//        } catch (InterruptedException e) {
//          e.printStackTrace();
//        }
        try {
          byte[] bytes = producerQueue.take();//从生产队列里面拿出一个 bytes
          int length = audioRecorder.readByByte(bytes, 0, bytes.length);
          if (length <= 0) {
            Log.e(TAG, "Once collecting failed ~");
          }
          consumerQueue.put(bytes);//将可能已经获取到的 pcm 音频字节码入队消费队列
        } catch (Exception e) {
          e.printStackTrace();
        }
      }

      audioRecorder.stop();
      Log.e(TAG, "stop record ~");
      dispatchThread.interrupt();
      try {
        dispatchThread.join();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      byte[] data;
      while ((data = consumerQueue.poll()) != null) {
        producerQueue.offer(data);
      }
    }
//  }
}
