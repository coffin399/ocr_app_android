package com.mojiscan.ocr.data.dao

import androidx.room.*
import com.mojiscan.ocr.data.entity.TranscriptionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TranscriptionDao {
    @Query("SELECT * FROM transcriptions ORDER BY timestamp DESC")
    fun getAllTranscriptions(): Flow<List<TranscriptionEntity>>

    @Query("SELECT * FROM transcriptions WHERE title LIKE '%' || :query || '%' OR text LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    fun searchTranscriptions(query: String): Flow<List<TranscriptionEntity>>

    @Query("SELECT * FROM transcriptions WHERE id = :id")
    suspend fun getTranscriptionById(id: Long): TranscriptionEntity?

    @Insert
    suspend fun insertTranscription(transcription: TranscriptionEntity): Long

    @Update
    suspend fun updateTranscription(transcription: TranscriptionEntity)

    @Delete
    suspend fun deleteTranscription(transcription: TranscriptionEntity)

    @Query("DELETE FROM transcriptions WHERE id = :id")
    suspend fun deleteTranscriptionById(id: Long)
}

