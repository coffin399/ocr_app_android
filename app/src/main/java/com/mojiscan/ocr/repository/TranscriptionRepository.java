package com.mojiscan.ocr.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.mojiscan.ocr.data.dao.TranscriptionDao;
import com.mojiscan.ocr.data.database.AppDatabase;
import com.mojiscan.ocr.data.entity.TranscriptionEntity;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TranscriptionRepository {
    private TranscriptionDao transcriptionDao;
    private ExecutorService executorService;

    public TranscriptionRepository(Application application) {
        AppDatabase database = AppDatabase.getDatabase(application);
        transcriptionDao = database.transcriptionDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<TranscriptionEntity>> getAllTranscriptions() {
        return transcriptionDao.getAllTranscriptions();
    }

    public LiveData<List<TranscriptionEntity>> searchTranscriptions(String query) {
        return transcriptionDao.searchTranscriptions(query);
    }

    public TranscriptionEntity getTranscriptionById(long id) {
        return transcriptionDao.getTranscriptionById(id);
    }

    public void insertTranscription(TranscriptionEntity transcription) {
        executorService.execute(() -> transcriptionDao.insertTranscription(transcription));
    }

    public void updateTranscription(TranscriptionEntity transcription) {
        executorService.execute(() -> transcriptionDao.updateTranscription(transcription));
    }

    public void deleteTranscription(TranscriptionEntity transcription) {
        executorService.execute(() -> transcriptionDao.deleteTranscription(transcription));
    }

    public void deleteTranscriptionById(long id) {
        executorService.execute(() -> transcriptionDao.deleteTranscriptionById(id));
    }
}

