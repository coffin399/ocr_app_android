package com.mojiscan.ocr.ui.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mojiscan.ocr.data.entity.TranscriptionEntity
import com.mojiscan.ocr.data.model.*
import com.mojiscan.ocr.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class ApiViewModel(application: Application) : AndroidViewModel(application) {
    private val apiService = RetrofitClient.apiService

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing.asStateFlow()

    private val _processingProgress = MutableStateFlow<String?>(null)
    val processingProgress: StateFlow<String?> = _processingProgress.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun processFile(
        fileUri: Uri? = null,
        file: File? = null,
        processType: String? = null,
        onSuccess: (TranscriptionEntity) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                _isProcessing.value = true
                _processingProgress.value = "ファイルを読み込み中..."
                _errorMessage.value = null

                val targetFile = file ?: (fileUri?.let { uriToFile(it) })
                if (targetFile == null || !targetFile.exists()) {
                    _errorMessage.value = "ファイルの読み込みに失敗しました"
                    onError("ファイルの読み込みに失敗しました")
                    _isProcessing.value = false
                    return@launch
                }

                val mimeType = if (fileUri != null) {
                    getMediaType(fileUri)
                } else {
                    when {
                        targetFile.name.endsWith(".jpg", ignoreCase = true) || targetFile.name.endsWith(".jpeg", ignoreCase = true) -> "image/jpeg"
                        targetFile.name.endsWith(".png", ignoreCase = true) -> "image/png"
                        targetFile.name.endsWith(".pdf", ignoreCase = true) -> "application/pdf"
                        else -> "image/jpeg"
                    }
                }
                val requestFile = targetFile.asRequestBody(mimeType.toMediaType())
                val filePart = MultipartBody.Part.createFormData("file", targetFile.name, requestFile)
                val processTypePart = processType?.toRequestBody("text/plain".toMediaType())

                _processingProgress.value = "処理中..."

                val response = apiService.processFile(filePart, processTypePart)
                
                if (response.isSuccessful) {
                    val result = response.body()!!
                    val transcription = TranscriptionEntity(
                        title = extractTitle(result.text),
                        text = result.text,
                        filename = result.filename,
                        processType = result.processType ?: "auto",
                        model = result.model,
                        processingTime = result.processingTime,
                        timestamp = System.currentTimeMillis()
                    )
                    onSuccess(transcription)
                    _processingProgress.value = "完了"
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMsg = try {
                        val errorResponse = com.google.gson.Gson().fromJson(errorBody, ErrorResponse::class.java)
                        errorResponse.detail
                    } catch (e: Exception) {
                        "エラーが発生しました: ${response.code()}"
                    }
                    _errorMessage.value = errorMsg
                    onError(errorMsg)
                }
            } catch (e: Exception) {
                val errorMsg = "エラーが発生しました: ${e.message}"
                _errorMessage.value = errorMsg
                onError(errorMsg)
            } finally {
                _isProcessing.value = false
                _processingProgress.value = null
            }
        }
    }

    fun processAudioFile(
        file: File?,
        language: String = "ja",
        model: String = "whisper",
        onSuccess: (TranscriptionEntity) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                _isProcessing.value = true
                _processingProgress.value = "音声ファイルを読み込み中..."
                _errorMessage.value = null

                if (file == null || !file.exists()) {
                    _errorMessage.value = "ファイルの読み込みに失敗しました"
                    onError("ファイルの読み込みに失敗しました")
                    _isProcessing.value = false
                    return@launch
                }

                val mimeType = when {
                    file.name.endsWith(".m4a", ignoreCase = true) -> "audio/mp4"
                    file.name.endsWith(".wav", ignoreCase = true) -> "audio/wav"
                    file.name.endsWith(".mp3", ignoreCase = true) -> "audio/mpeg"
                    else -> "audio/m4a"
                }
                val requestFile = file.asRequestBody(mimeType.toMediaType())
                val filePart = MultipartBody.Part.createFormData("file", file.name, requestFile)
                val languagePart = language.toRequestBody("text/plain".toMediaType())
                val modelPart = model.toRequestBody("text/plain".toMediaType())

                _processingProgress.value = "文字起こし処理中..."

                val response = apiService.processTranscribe(filePart, languagePart, modelPart)
                
                if (response.isSuccessful) {
                    val result = response.body()!!
                    val transcription = TranscriptionEntity(
                        title = extractTitle(result.text),
                        text = result.text,
                        filename = result.filename,
                        processType = "transcribe",
                        model = result.model,
                        processingTime = result.processingTime,
                        timestamp = System.currentTimeMillis()
                    )
                    onSuccess(transcription)
                    _processingProgress.value = "完了"
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMsg = try {
                        val errorResponse = com.google.gson.Gson().fromJson(errorBody, ErrorResponse::class.java)
                        errorResponse.detail
                    } catch (e: Exception) {
                        "エラーが発生しました: ${response.code()}"
                    }
                    _errorMessage.value = errorMsg
                    onError(errorMsg)
                }
            } catch (e: Exception) {
                val errorMsg = "エラーが発生しました: ${e.message}"
                _errorMessage.value = errorMsg
                onError(errorMsg)
            } finally {
                _isProcessing.value = false
                _processingProgress.value = null
            }
        }
    }

    private fun uriToFile(uri: Uri): File? {
        return try {
            val inputStream: InputStream? = getApplication<Application>().contentResolver.openInputStream(uri)
            if (inputStream == null) return null

            val tempFile = File(getApplication<Application>().cacheDir, "temp_${System.currentTimeMillis()}")
            val outputStream = FileOutputStream(tempFile)
            inputStream.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            tempFile
        } catch (e: Exception) {
            null
        }
    }

    private fun getMediaType(uri: Uri): String {
        val mimeType = getApplication<Application>().contentResolver.getType(uri)
        return mimeType ?: "image/jpeg"
    }

    private fun getAudioMediaType(uri: Uri): String {
        val mimeType = getApplication<Application>().contentResolver.getType(uri)
        return mimeType ?: "audio/wav"
    }

    private fun extractTitle(text: String): String {
        val firstLine = text.lines().firstOrNull { it.isNotBlank() } ?: ""
        return if (firstLine.length > 50) {
            firstLine.take(50) + "..."
        } else {
            firstLine.ifBlank { "無題" }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}

