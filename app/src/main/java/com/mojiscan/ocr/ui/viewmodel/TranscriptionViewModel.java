package com.mojiscan.ocr.ui.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import com.mojiscan.ocr.data.entity.TranscriptionEntity;
import com.mojiscan.ocr.repository.TranscriptionRepository;

import java.util.ArrayList;
import java.util.List;

public class TranscriptionViewModel extends AndroidViewModel {
    private TranscriptionRepository repository;
    private MutableLiveData<String> searchQuery = new MutableLiveData<>("");
    private LiveData<List<TranscriptionEntity>> allTranscriptions;
    private LiveData<List<TranscriptionEntity>> transcriptions;
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public TranscriptionViewModel(Application application) {
        super(application);
        repository = new TranscriptionRepository(application);
        allTranscriptions = repository.getAllTranscriptions();
        
        transcriptions = Transformations.switchMap(searchQuery, query -> {
            if (query == null || query.trim().isEmpty()) {
                return allTranscriptions;
            } else {
                return Transformations.map(allTranscriptions, items -> {
                    if (items == null) return new ArrayList<>();
                    List<TranscriptionEntity> filtered = new ArrayList<>();
                    String lowerQuery = query.toLowerCase();
                    for (TranscriptionEntity item : items) {
                        if (item.getTitle() != null && item.getTitle().toLowerCase().contains(lowerQuery) ||
                            item.getText() != null && item.getText().toLowerCase().contains(lowerQuery)) {
                            filtered.add(item);
                        }
                    }
                    return filtered;
                });
            }
        });
    }

    public LiveData<List<TranscriptionEntity>> getTranscriptions() {
        return transcriptions;
    }

    public LiveData<String> getSearchQuery() {
        return searchQuery;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void setSearchQuery(String query) {
        searchQuery.setValue(query);
    }

    public void addTranscription(TranscriptionEntity transcription) {
        repository.insertTranscription(transcription);
    }

    public void deleteTranscription(long id) {
        TranscriptionEntity transcription = repository.getTranscriptionById(id);
        if (transcription != null) {
            repository.deleteTranscription(transcription);
        }
    }

    public TranscriptionEntity getTranscriptionById(long id) {
        return repository.getTranscriptionById(id);
    }

    public void clearError() {
        errorMessage.setValue(null);
    }

    public void setLoading(boolean loading) {
        isLoading.setValue(loading);
    }
}

