package com.mojiscan.ocr.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "transcriptions")
data class TranscriptionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val text: String,
    val filename: String,
    val processType: String, // "ocr" or "transcribe"
    val model: String,
    val timestamp: Long = System.currentTimeMillis(),
    val processingTime: Double = 0.0
)

