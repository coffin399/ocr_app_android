package com.mojiscan.ocr.util;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Build;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.LiveData;
import java.io.File;
import java.io.IOException;

public class AudioRecorder {
    private MediaRecorder mediaRecorder;
    private File outputFile;
    private Context context;
    private MutableLiveData<Boolean> isRecording = new MutableLiveData<>(false);
    private MutableLiveData<Long> recordingDuration = new MutableLiveData<>(0L);

    public AudioRecorder(Context context) {
        this.context = context;
    }

    public LiveData<Boolean> getIsRecording() {
        return isRecording;
    }

    public LiveData<Long> getRecordingDuration() {
        return recordingDuration;
    }

    public File startRecording() {
        try {
            outputFile = new File(context.getCacheDir(), "recording_" + System.currentTimeMillis() + ".m4a");
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                mediaRecorder = new MediaRecorder(context);
            } else {
                mediaRecorder = new MediaRecorder();
            }
            
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mediaRecorder.setOutputFile(outputFile.getAbsolutePath());
            mediaRecorder.prepare();
            mediaRecorder.start();

            isRecording.postValue(true);
            return outputFile;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public File stopRecording() {
        try {
            if (mediaRecorder != null) {
                mediaRecorder.stop();
                mediaRecorder.release();
                mediaRecorder = null;
            }
            isRecording.postValue(false);
            return outputFile;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void cancelRecording() {
        stopRecording();
        if (outputFile != null && outputFile.exists()) {
            outputFile.delete();
        }
        outputFile = null;
    }
}

