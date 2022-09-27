package com.tuner.android;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class InputHelper {
    public static final String TAG = "InputHelper";

    // 采样率，现在能够保证在所有设备上使用的采样率是44100Hz, 但是其他的采样率（22050, 16000, 11025）在一些设备上也可以使用。
    public static final int SAMPLE_RATE_INHZ = 44100;

    // 声道数。CHANNEL_IN_MONO and CHANNEL_IN_STEREO. 其中CHANNEL_IN_MONO是可以保证在所有设备能够使用的。
    public static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_STEREO;

    // 返回的音频数据的格式。 ENCODING_PCM_8BIT, ENCODING_PCM_16BIT, and ENCODING_PCM_FLOAT.
    public static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;

    private AudioRecord audioRecord;

    /**
     * 录音的工作线程
     */
    private Thread recordingAudioThread;
    private boolean isRecording = false;//mark if is recording
    private final Context context;

    public InputHelper(Context context) {
        this.context = context;
    }

    public String startRecordAudio() {
        String audioCacheFilePath = context.getExternalFilesDir(Environment.DIRECTORY_MUSIC).getAbsolutePath() + "/" + "jerboa_audio_cache.pcm";
        try {
            int minBufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE_INHZ, CHANNEL_CONFIG, AUDIO_FORMAT);
            this.audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE_INHZ, CHANNEL_CONFIG, AUDIO_FORMAT, minBufferSize);

            this.isRecording = true;
            audioRecord.startRecording();

            this.recordingAudioThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    File file = new File(audioCacheFilePath);
                    Log.i(TAG, "audio cache pcm file path:" + audioCacheFilePath);

                    if (file.exists()) {
                        file.delete();
                    }

                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    FileOutputStream fos = null;
                    try {
                        fos = new FileOutputStream(file);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        Log.e(TAG, "临时缓存文件未找到");
                    }
                    if (fos == null) {
                        return;
                    }

                    byte[] data = new byte[minBufferSize];
                    int read;
                    if (fos != null) {
                        while (isRecording && !recordingAudioThread.isInterrupted()) {
                            read = audioRecord.read(data, 0, minBufferSize);
                            if (AudioRecord.ERROR_INVALID_OPERATION != read) {
                                try {
                                    fos.write(data);
                                    Log.i(TAG, "写录音数据->" + read);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }

                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            this.recordingAudioThread.start();
        } catch (IllegalStateException e) {
            Log.w(TAG,"需要获取录音权限！");
            e.printStackTrace();
        } catch (SecurityException e) {
            Log.w(TAG,"需要获取录音权限！");
            e.printStackTrace();
        }

        return audioCacheFilePath;
    }

    /**
     * 停止录音
     */
    protected void stopRecordAudio(){
        try {
            this.isRecording = false;
            if (this.audioRecord != null) {
                this.audioRecord.stop();
                this.audioRecord.release();
                this.audioRecord = null;
                this.recordingAudioThread.interrupt();
                this.recordingAudioThread = null;
            }
        }
        catch (Exception e){
            Log.w(TAG,e.getLocalizedMessage());
        }
    }

}
