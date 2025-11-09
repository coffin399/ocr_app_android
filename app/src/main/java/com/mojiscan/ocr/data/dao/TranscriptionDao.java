package com.mojiscan.ocr.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.mojiscan.ocr.data.entity.TranscriptionEntity;

import java.util.List;

@Dao
public interface TranscriptionDao {
    @Query("SELECT * FROM transcriptions ORDER BY timestamp DESC")
    LiveData<List<TranscriptionEntity>> getAllTranscriptions();

    @Query("SELECT * FROM transcriptions WHERE title LIKE '%' || :query || '%' OR text LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    LiveData<List<TranscriptionEntity>> searchTranscriptions(String query);

    @Query("SELECT * FROM transcriptions WHERE id = :id")
    LiveData<TranscriptionEntity> getTranscriptionById(long id);

    @Insert
    long insertTranscription(TranscriptionEntity transcription);

    @Update
    void updateTranscription(TranscriptionEntity transcription);

    @Delete
    void deleteTranscription(TranscriptionEntity transcription);

    @Query("DELETE FROM transcriptions WHERE id = :id")
    void deleteTranscriptionById(long id);
}

