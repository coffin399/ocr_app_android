package com.mojiscan.ocr.ui.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import com.google.android.material.card.MaterialCardView;
import com.mojiscan.ocr.R;
import com.mojiscan.ocr.data.entity.TranscriptionEntity;
import com.mojiscan.ocr.ui.viewmodel.ApiViewModel;
import com.mojiscan.ocr.ui.viewmodel.TranscriptionViewModel;
import com.mojiscan.ocr.util.AudioRecorder;

import java.io.File;

public class AddTranscriptionFragment extends Fragment {
    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final int FILE_PICKER_REQUEST = 1;
    private static final int AUDIO_FILE_PICKER_REQUEST = 2;

    private TranscriptionViewModel transcriptionViewModel;
    private ApiViewModel apiViewModel;
    private AudioRecorder audioRecorder;
    
    private Button selectFileButton;
    private Button selectAudioButton;
    private Button recordAudioButton;
    private Button stopRecordingButton;
    private MaterialCardView errorCard;
    private TextView errorTextView;
    private MaterialCardView processingCard;
    private TextView processingTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_transcription, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        transcriptionViewModel = new ViewModelProvider(requireActivity()).get(TranscriptionViewModel.class);
        apiViewModel = new ViewModelProvider(requireActivity()).get(ApiViewModel.class);
        audioRecorder = new AudioRecorder(requireContext());

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        if (getActivity() != null) {
            ((androidx.appcompat.app.AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            toolbar.setNavigationOnClickListener(v -> Navigation.findNavController(view).popBackStack());
        }

        selectFileButton = view.findViewById(R.id.selectFileButton);
        selectAudioButton = view.findViewById(R.id.selectAudioButton);
        recordAudioButton = view.findViewById(R.id.recordAudioButton);
        stopRecordingButton = view.findViewById(R.id.stopRecordingButton);
        errorCard = view.findViewById(R.id.errorCard);
        errorTextView = view.findViewById(R.id.errorTextView);
        processingCard = view.findViewById(R.id.processingCard);
        processingTextView = view.findViewById(R.id.processingTextView);

        selectFileButton.setOnClickListener(v -> openFilePicker());
        selectAudioButton.setOnClickListener(v -> openAudioFilePicker());
        recordAudioButton.setOnClickListener(v -> startRecording());
        stopRecordingButton.setOnClickListener(v -> stopRecording());

        apiViewModel.getIsProcessing().observe(getViewLifecycleOwner(), isProcessing -> {
            if (isProcessing) {
                processingCard.setVisibility(View.VISIBLE);
                selectFileButton.setEnabled(false);
                selectAudioButton.setEnabled(false);
                recordAudioButton.setEnabled(false);
            } else {
                processingCard.setVisibility(View.GONE);
                selectFileButton.setEnabled(true);
                selectAudioButton.setEnabled(true);
                recordAudioButton.setEnabled(true);
            }
        });

        apiViewModel.getProcessingProgress().observe(getViewLifecycleOwner(), progress -> {
            if (progress != null) {
                processingTextView.setText(progress);
            }
        });

        apiViewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                errorCard.setVisibility(View.VISIBLE);
                errorTextView.setText(error);
            } else {
                errorCard.setVisibility(View.GONE);
            }
        });

        audioRecorder.getIsRecording().observe(getViewLifecycleOwner(), isRecording -> {
            if (isRecording) {
                recordAudioButton.setVisibility(View.GONE);
                stopRecordingButton.setVisibility(View.VISIBLE);
            } else {
                recordAudioButton.setVisibility(View.VISIBLE);
                stopRecordingButton.setVisibility(View.GONE);
            }
        });
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        String[] mimeTypes = {"image/*", "application/pdf"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        startActivityForResult(Intent.createChooser(intent, "ファイルを選択"), FILE_PICKER_REQUEST);
    }

    private void openAudioFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "音声ファイルを選択"), AUDIO_FILE_PICKER_REQUEST);
    }

    private void startRecording() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO) 
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), 
                new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSION_REQUEST_CODE);
            return;
        }
        audioRecorder.startRecording();
    }

    private void stopRecording() {
        File recordingFile = audioRecorder.stopRecording();
        if (recordingFile != null && recordingFile.exists()) {
            processAudioFile(recordingFile);
        }
    }

    private void processFile(Uri uri) {
        apiViewModel.processFile(uri, null, "auto", new ApiViewModel.ProcessCallback() {
            @Override
            public void onSuccess(TranscriptionEntity transcription) {
                transcriptionViewModel.addTranscription(transcription);
                if (getView() != null) {
                    Navigation.findNavController(getView()).popBackStack();
                }
            }

            @Override
            public void onError(String error) {
                // Error is handled by ViewModel
            }
        });
    }

    private void processAudioFile(File file) {
        apiViewModel.processAudioFile(file, "ja", "whisper", new ApiViewModel.ProcessCallback() {
            @Override
            public void onSuccess(TranscriptionEntity transcription) {
                transcriptionViewModel.addTranscription(transcription);
                if (getView() != null) {
                    Navigation.findNavController(getView()).popBackStack();
                }
            }

            @Override
            public void onError(String error) {
                // Error is handled by ViewModel
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && data.getData() != null) {
            Uri uri = data.getData();
            if (requestCode == FILE_PICKER_REQUEST) {
                processFile(uri);
            } else if (requestCode == AUDIO_FILE_PICKER_REQUEST) {
                processFile(uri);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startRecording();
            }
        }
    }
}

