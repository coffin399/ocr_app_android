package com.mojiscan.ocr.ui.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mojiscan.ocr.data.entity.TranscriptionEntity
import com.mojiscan.ocr.repository.TranscriptionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch

class TranscriptionViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TranscriptionRepository

    val transcriptions: StateFlow<List<TranscriptionEntity>>
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        val db = com.mojiscan.ocr.data.database.AppDatabase.getDatabase(application)
        repository = TranscriptionRepository(db.transcriptionDao())
        
        transcriptions = combine(
            _searchQuery,
            repository.getAllTranscriptions()
        ) { query, allItems ->
            if (query.isBlank()) {
                allItems
            } else {
                allItems.filter {
                    it.title.contains(query, ignoreCase = true) ||
                    it.text.contains(query, ignoreCase = true)
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    fun addTranscription(transcription: TranscriptionEntity) {
        viewModelScope.launch {
            try {
                repository.insertTranscription(transcription)
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }

    fun deleteTranscription(id: Long) {
        viewModelScope.launch {
            try {
                repository.deleteTranscriptionById(id)
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun setLoading(loading: Boolean) {
        _isLoading.value = loading
    }

    suspend fun getTranscriptionById(id: Long): TranscriptionEntity? {
        return repository.getTranscriptionById(id)
    }
}

