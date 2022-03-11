# AudioRecord4Android
基于 Android AudioRecord 音频采集与分发(PCM)

# 使用场景
 - 小爱、小度语言类 SDK 的音频流输出
 - 录音相关功能
 - 变声等语音相关应用
 
# PCM (Pulse Code Modulation)
PCM 为无损的脉冲数据格式，例如对音频进行一些增益修改都必须先获取 PCM 流。

# 采集与分发
AudioRecord4Android 采用双线程和双队列的形式(生产和消费队列)，采集 pcm 一个专门的线程处理，dispatch 分发来做一个线程

# 关于录音保存 mp3
建议使用 Lame 库(c) 进行 pcm to mp3 的编码转化