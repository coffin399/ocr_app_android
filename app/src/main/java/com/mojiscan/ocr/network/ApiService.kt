package com.mojiscan.ocr.network

import com.mojiscan.ocr.data.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @Multipart
    @POST("api/v1/process")
    suspend fun processFile(
        @Part file: MultipartBody.Part,
        @Part("process_type") processType: RequestBody?
    ): Response<ProcessResponse>

    @Multipart
    @POST("api/v1/ocr")
    suspend fun processOCR(
        @Part file: MultipartBody.Part
    ): Response<OCRResponse>

    @Multipart
    @POST("api/v1/transcribe")
    suspend fun processTranscribe(
        @Part file: MultipartBody.Part,
        @Part("language") language: RequestBody?,
        @Part("model") model: RequestBody?
    ): Response<TranscribeResponse>

    @GET("api/health")
    suspend fun healthCheck(): Response<HealthResponse>
}

