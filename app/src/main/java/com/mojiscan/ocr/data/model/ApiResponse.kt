package com.mojiscan.ocr.data.model

import com.google.gson.annotations.SerializedName

data class ProcessResponse(
    val success: Boolean,
    val text: String,
    val filename: String,
    @SerializedName("process_type") val processType: String?,
    val model: String,
    @SerializedName("processing_time") val processingTime: Double,
    val timestamp: String
)

data class OCRResponse(
    val success: Boolean,
    val text: String,
    val filename: String,
    val model: String,
    @SerializedName("processing_time") val processingTime: Double,
    val confidence: Double,
    val timestamp: String,
    val pages: Int?
)

data class TranscribeResponse(
    val success: Boolean,
    val text: String,
    val filename: String,
    val model: String,
    val language: String?,
    @SerializedName("processing_time") val processingTime: Double,
    val timestamp: String,
    val segments: List<TranscribeSegment>?
)

data class TranscribeSegment(
    val start: Double,
    val end: Double,
    val text: String
)

data class ErrorResponse(
    val detail: String
)

data class HealthResponse(
    val status: String,
    val service: String,
    @SerializedName("models_available") val modelsAvailable: Int
)

