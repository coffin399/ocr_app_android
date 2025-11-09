package com.mojiscan.ocr.ui.viewmodel;

import android.app.Application;
import android.net.Uri;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.LiveData;
import com.google.gson.Gson;
import com.mojiscan.ocr.data.entity.TranscriptionEntity;
import com.mojiscan.ocr.data.model.ErrorResponse;
import com.mojiscan.ocr.data.model.OCRResponse;
import com.mojiscan.ocr.data.model.ProcessResponse;
import com.mojiscan.ocr.data.model.TranscribeResponse;
import com.mojiscan.ocr.network.ApiService;
import com.mojiscan.ocr.network.RetrofitClient;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ApiViewModel extends AndroidViewModel {
    private ApiService apiService;
    private ExecutorService executorService;
    private MutableLiveData<Boolean> isProcessing = new MutableLiveData<>(false);
    private MutableLiveData<String> processingProgress = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public ApiViewModel(Application application) {
        super(application);
        apiService = RetrofitClient.getInstance().getApiService();
        executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<Boolean> getIsProcessing() {
        return isProcessing;
    }

    public LiveData<String> getProcessingProgress() {
        return processingProgress;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public interface ProcessCallback {
        void onSuccess(TranscriptionEntity transcription);
        void onError(String error);
    }

    public void processFile(Uri fileUri, File file, String processType, ProcessCallback callback) {
        executorService.execute(() -> {
            try {
                isProcessing.postValue(true);
                processingProgress.postValue("ファイルを読み込み中...");
                errorMessage.postValue(null);

                File targetFile = file;
                if (targetFile == null && fileUri != null) {
                    targetFile = uriToFile(fileUri);
                }

                if (targetFile == null || !targetFile.exists()) {
                    String error = "ファイルの読み込みに失敗しました";
                    errorMessage.postValue(error);
                    isProcessing.postValue(false);
                    if (callback != null) callback.onError(error);
                    return;
                }

                String mimeType;
                if (fileUri != null) {
                    mimeType = getMediaType(fileUri);
                } else {
                    String fileName = targetFile.getName().toLowerCase();
                    if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
                        mimeType = "image/jpeg";
                    } else if (fileName.endsWith(".png")) {
                        mimeType = "image/png";
                    } else if (fileName.endsWith(".pdf")) {
                        mimeType = "application/pdf";
                    } else {
                        mimeType = "image/jpeg";
                    }
                }

                MediaType mediaType = MediaType.parse(mimeType);
                RequestBody requestFile = RequestBody.create(mediaType, targetFile);
                MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", targetFile.getName(), requestFile);
                RequestBody processTypePart = processType != null ? 
                    RequestBody.create(MediaType.parse("text/plain"), processType) : null;

                processingProgress.postValue("処理中...");

                apiService.processFile(filePart, processTypePart).enqueue(new Callback<ProcessResponse>() {
                    @Override
                    public void onResponse(Call<ProcessResponse> call, Response<ProcessResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            ProcessResponse result = response.body();
                            TranscriptionEntity transcription = new TranscriptionEntity(
                                extractTitle(result.getText()),
                                result.getText(),
                                result.getFilename(),
                                result.getProcessType() != null ? result.getProcessType() : "auto",
                                result.getModel(),
                                System.currentTimeMillis(),
                                result.getProcessingTime()
                            );
                            processingProgress.postValue("完了");
                            isProcessing.postValue(false);
                            if (callback != null) callback.onSuccess(transcription);
                        } else {
                            String errorMsg = "エラーが発生しました: " + response.code();
                            try {
                                if (response.errorBody() != null) {
                                    String errorBody = response.errorBody().string();
                                    ErrorResponse errorResponse = new Gson().fromJson(errorBody, ErrorResponse.class);
                                    if (errorResponse != null && errorResponse.getDetail() != null) {
                                        errorMsg = errorResponse.getDetail();
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            errorMessage.postValue(errorMsg);
                            isProcessing.postValue(false);
                            if (callback != null) callback.onError(errorMsg);
                        }
                    }

                    @Override
                    public void onFailure(Call<ProcessResponse> call, Throwable t) {
                        String errorMsg = "エラーが発生しました: " + (t.getMessage() != null ? t.getMessage() : "Unknown error");
                        errorMessage.postValue(errorMsg);
                        isProcessing.postValue(false);
                        if (callback != null) callback.onError(errorMsg);
                    }
                });
            } catch (Exception e) {
                String errorMsg = "エラーが発生しました: " + e.getMessage();
                errorMessage.postValue(errorMsg);
                isProcessing.postValue(false);
                if (callback != null) callback.onError(errorMsg);
            }
        });
    }

    public void processAudioFile(File file, String language, String model, ProcessCallback callback) {
        executorService.execute(() -> {
            try {
                isProcessing.postValue(true);
                processingProgress.postValue("音声ファイルを読み込み中...");
                errorMessage.postValue(null);

                if (file == null || !file.exists()) {
                    String error = "ファイルの読み込みに失敗しました";
                    errorMessage.postValue(error);
                    isProcessing.postValue(false);
                    if (callback != null) callback.onError(error);
                    return;
                }

                String mimeType;
                String fileName = file.getName().toLowerCase();
                if (fileName.endsWith(".m4a")) {
                    mimeType = "audio/mp4";
                } else if (fileName.endsWith(".wav")) {
                    mimeType = "audio/wav";
                } else if (fileName.endsWith(".mp3")) {
                    mimeType = "audio/mpeg";
                } else {
                    mimeType = "audio/m4a";
                }

                MediaType mediaType = MediaType.parse(mimeType);
                RequestBody requestFile = RequestBody.create(mediaType, file);
                MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
                RequestBody languagePart = RequestBody.create(MediaType.parse("text/plain"), language != null ? language : "ja");
                RequestBody modelPart = RequestBody.create(MediaType.parse("text/plain"), model != null ? model : "whisper");

                processingProgress.postValue("文字起こし処理中...");

                apiService.processTranscribe(filePart, languagePart, modelPart).enqueue(new Callback<TranscribeResponse>() {
                    @Override
                    public void onResponse(Call<TranscribeResponse> call, Response<TranscribeResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            TranscribeResponse result = response.body();
                            TranscriptionEntity transcription = new TranscriptionEntity(
                                extractTitle(result.getText()),
                                result.getText(),
                                result.getFilename(),
                                "transcribe",
                                result.getModel(),
                                System.currentTimeMillis(),
                                result.getProcessingTime()
                            );
                            processingProgress.postValue("完了");
                            isProcessing.postValue(false);
                            if (callback != null) callback.onSuccess(transcription);
                        } else {
                            String errorMsg = "エラーが発生しました: " + response.code();
                            try {
                                if (response.errorBody() != null) {
                                    String errorBody = response.errorBody().string();
                                    ErrorResponse errorResponse = new Gson().fromJson(errorBody, ErrorResponse.class);
                                    if (errorResponse != null && errorResponse.getDetail() != null) {
                                        errorMsg = errorResponse.getDetail();
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            errorMessage.postValue(errorMsg);
                            isProcessing.postValue(false);
                            if (callback != null) callback.onError(errorMsg);
                        }
                    }

                    @Override
                    public void onFailure(Call<TranscribeResponse> call, Throwable t) {
                        String errorMsg = "エラーが発生しました: " + (t.getMessage() != null ? t.getMessage() : "Unknown error");
                        errorMessage.postValue(errorMsg);
                        isProcessing.postValue(false);
                        if (callback != null) callback.onError(errorMsg);
                    }
                });
            } catch (Exception e) {
                String errorMsg = "エラーが発生しました: " + e.getMessage();
                errorMessage.postValue(errorMsg);
                isProcessing.postValue(false);
                if (callback != null) callback.onError(errorMsg);
            }
        });
    }

    private File uriToFile(Uri uri) {
        try {
            InputStream inputStream = getApplication().getContentResolver().openInputStream(uri);
            if (inputStream == null) return null;

            File tempFile = new File(getApplication().getCacheDir(), "temp_" + System.currentTimeMillis());
            FileOutputStream outputStream = new FileOutputStream(tempFile);
            
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            
            inputStream.close();
            outputStream.close();
            return tempFile;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getMediaType(Uri uri) {
        String mimeType = getApplication().getContentResolver().getType(uri);
        return mimeType != null ? mimeType : "image/jpeg";
    }

    private String extractTitle(String text) {
        if (text == null || text.isEmpty()) {
            return "無題";
        }
        String[] lines = text.split("\n");
        for (String line : lines) {
            if (line != null && !line.trim().isEmpty()) {
                String firstLine = line.trim();
                return firstLine.length() > 50 ? firstLine.substring(0, 50) + "..." : firstLine;
            }
        }
        return "無題";
    }

    public void clearError() {
        errorMessage.setValue(null);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}

