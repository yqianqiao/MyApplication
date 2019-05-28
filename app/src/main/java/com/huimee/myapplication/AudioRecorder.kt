package com.huimee.myapplication

import android.media.AudioFormat.CHANNEL_IN_MONO
import android.media.AudioFormat.ENCODING_PCM_16BIT
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log


/**
 *   Created by YX on 2019/5/27 15:20.
 */
class AudioRecorder {
    private val TAG = "AudioRecorder"

    interface OnAudioDataArrivedListener {
        fun onAudioDataArrived(audioData: ByteArray)
    }

    companion object{
        //声源
        private const val DEFAULT_AUDIO_SOURCE = MediaRecorder.AudioSource.MIC
        //采样率，现在能够保证在所有设备上使用的采样率是44100Hz, 但是其他的采样率（22050, 16000, 11025）在一些设备上也可以使用。
        private const val DEFAULT_SAMPLE_RATE_INHZ = 44100
        //声道数。CHANNEL_IN_MONO and CHANNEL_IN_STEREO. 其中CHANNEL_IN_MONO是可以保证在所有设备能够使用的。
        private const val DEFAULT_CHANNEL_CONFIG = CHANNEL_IN_MONO
        //返回的音频数据的格式。 ENCODING_PCM_8BIT, ENCODING_PCM_16BIT, and ENCODING_PCM_FLOAT.
        private const val DEFAULT_AUDIO_FORMAT = ENCODING_PCM_16BIT
    }


    //内部缓冲区大小
    private var minBufferSize = 0
    //是否已启动录音
    private var isStarted = false
    //是否可以从缓冲区中读取数据
    private var canReadDataFromBuffer = true
    //从缓冲区中读取数据的回调方法
    public var onAudioDataArrivedListener: OnAudioDataArrivedListener? = null

    private var audioRecord: AudioRecord? = null


    fun startRecord(): Boolean {
        return startRecord(
            DEFAULT_AUDIO_SOURCE,
            DEFAULT_SAMPLE_RATE_INHZ,
            DEFAULT_CHANNEL_CONFIG,
            DEFAULT_AUDIO_FORMAT
        )
    }

    private fun startRecord(audioSource: Int, sampleRate: Int, channel: Int, audioFormat: Int): Boolean {
        if (isStarted) {
            Log.e(TAG, "startRecord: AudioRecorder has been already started")
            return false
        }

        //获取内部缓冲区最小size
        minBufferSize = AudioRecord.getMinBufferSize(sampleRate, channel, audioFormat)
        if (minBufferSize == AudioRecord.ERROR_BAD_VALUE) {
            Log.e(TAG, "startRecord: minBufferSize is error_bad_value")
            return false
        }
        Log.d(TAG, "startRecord: minBufferSize = " + minBufferSize + "bytes")

        //初始化 audioRecord
        audioRecord = AudioRecord(audioSource, sampleRate, channel, audioFormat, minBufferSize)
        if (audioRecord!!.state == AudioRecord.STATE_UNINITIALIZED) {
            Log.e(TAG, "startRecord: audioRecord is uninitialized")
            return false
        }

        //启动录制
        audioRecord!!.startRecording()

        //可以从内部缓冲区中读取数据
        canReadDataFromBuffer = true

        //启动子线程
        Thread(Runnable {
            while (canReadDataFromBuffer) {
                //初始化缓冲区数据接收数组
                val data = ByteArray(minBufferSize)

                //读取内部缓冲区中读取数据
                when (val result = audioRecord!!.read(data, 0, minBufferSize)) {
                    AudioRecord.ERROR_BAD_VALUE -> Log.e(TAG, "run: audioRecord.read result is ERROR_BAD_VALUE")
                    AudioRecord.ERROR_INVALID_OPERATION -> Log.e(TAG, "run: audioRecord.read result is ERROR_INVALID_OPERATION")
                    else -> {
                        if (onAudioDataArrivedListener != null) {
                            //调用读取数据回调方法
                            onAudioDataArrivedListener!!.onAudioDataArrived(data)
                        }
                        Log.d(TAG, "run: audioRecord read " + result + "bytes")
                    }
                }
            }
        }).start()

        //设置录音已启动
        isStarted = true
        Log.d(TAG, "startRecord: audioRecorder has been already started")
        return true
    }

    fun stopRecord() {
        //如果录音尚未启动，直接返回
        if (!isStarted) return
        //设置内部缓冲区数据不可读取
        canReadDataFromBuffer = false
        //停止录音
        if (audioRecord!!.recordingState == AudioRecord.RECORDSTATE_RECORDING) {
            audioRecord!!.stop()
        }
        //释放资源
        audioRecord!!.release()
        //设置录音未启动
        isStarted = false
        //回调置为空
        onAudioDataArrivedListener = null
    }
}