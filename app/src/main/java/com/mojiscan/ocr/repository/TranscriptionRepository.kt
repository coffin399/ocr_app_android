package com.mojiscan.ocr.repository

import com.mojiscan.ocr.data.dao.TranscriptionDao
import com.mojiscan.ocr.data.entity.TranscriptionEntity
import kotlinx.coroutines.flow.Flow

class TranscriptionRepository(private val transcriptionDao: TranscriptionDao) {
    fun getAllTranscriptions(): Flow<List<TranscriptionEntity>> {
        return transcriptionDao.getAllTranscriptions()
    }

    fun searchTranscriptions(query: String): Flow<List<TranscriptionEntity>> {
        return transcriptionDao.searchTranscriptions(query)
    }

    suspend fun getTranscriptionById(id: Long): TranscriptionEntity? {
        return transcriptionDao.getTranscriptionById(id)
    }

    suspend fun insertTranscription(transcription: TranscriptionEntity): Long {
        return transcriptionDao.insertTranscription(transcription)
    }

    suspend fun updateTranscription(transcription: TranscriptionEntity) {
        transcriptionDao.updateTranscription(transcription)
    }

    suspend fun deleteTranscription(transcription: TranscriptionEntity) {
        transcriptionDao.deleteTranscription(transcription)
    }

    suspend fun deleteTranscriptionById(id: Long) {
        transcriptionDao.deleteTranscriptionById(id)
    }
}

