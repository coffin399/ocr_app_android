package com.mojiscan.ocr.network;

import com.mojiscan.ocr.data.model.ErrorResponse;
import com.mojiscan.ocr.data.model.HealthResponse;
import com.mojiscan.ocr.data.model.OCRResponse;
import com.mojiscan.ocr.data.model.ProcessResponse;
import com.mojiscan.ocr.data.model.TranscribeResponse;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiService {
    @Multipart
    @POST("api/v1/process")
    Call<ProcessResponse> processFile(
            @Part MultipartBody.Part file,
            @Part("process_type") RequestBody processType
    );

    @Multipart
    @POST("api/v1/ocr")
    Call<OCRResponse> processOCR(
            @Part MultipartBody.Part file
    );

    @Multipart
    @POST("api/v1/transcribe")
    Call<TranscribeResponse> processTranscribe(
            @Part MultipartBody.Part file,
            @Part("language") RequestBody language,
            @Part("model") RequestBody model
    );

    @GET("api/health")
    Call<HealthResponse> healthCheck();
}

