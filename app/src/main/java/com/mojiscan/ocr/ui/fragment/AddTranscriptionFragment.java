package com.mojiscan.ocr.ui.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.google.android.material.card.MaterialCardView;
import com.mojiscan.ocr.R;
import com.mojiscan.ocr.data.entity.TranscriptionEntity;
import com.mojiscan.ocr.ui.viewmodel.ApiViewModel;
import com.mojiscan.ocr.ui.viewmodel.TranscriptionViewModel;
import com.mojiscan.ocr.util.AudioRecorder;

import java.io.File;

public class AddTranscriptionFragment extends Fragment {
    private TranscriptionViewModel transcriptionViewModel;
    private ApiViewModel apiViewModel;
    private AudioRecorder audioRecorder;
    private NavController navController;
    
            private View selectFileButton;
            private View selectAudioButton;
            private View recordAudioButton;
            private Button stopRecordingButton;
            private com.google.android.material.card.MaterialCardView selectFileCard;
            private com.google.android.material.card.MaterialCardView selectAudioCard;
            private com.google.android.material.card.MaterialCardView recordAudioCard;
    private MaterialCardView errorCard;
    private TextView errorTextView;
    private MaterialCardView processingCard;
    private TextView processingTextView;

    private ActivityResultLauncher<Intent> filePickerLauncher;
    private ActivityResultLauncher<String> audioPickerLauncher;
    private ActivityResultLauncher<String> requestPermissionLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        filePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getData() != null && result.getData().getData() != null) {
                    Uri uri = result.getData().getData();
                    processFile(uri);
                }
            }
        );

        audioPickerLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    processFile(uri);
                }
            }
        );

        requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    startRecording();
                }
            }
        );
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_transcription, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);
        transcriptionViewModel = new ViewModelProvider(requireActivity()).get(TranscriptionViewModel.class);
        apiViewModel = new ViewModelProvider(requireActivity()).get(ApiViewModel.class);
        audioRecorder = new AudioRecorder(requireContext());

        // Back icon click listener
        View backIcon = view.findViewById(R.id.backIcon);
        if (backIcon != null) {
            backIcon.setOnClickListener(v -> {
                if (navController != null) {
                    navController.popBackStack();
                }
            });
        }

        selectFileButton = view.findViewById(R.id.selectFileButton);
        selectAudioButton = view.findViewById(R.id.selectAudioButton);
        recordAudioButton = view.findViewById(R.id.recordAudioButton);
        stopRecordingButton = view.findViewById(R.id.stopRecordingButton);
        errorCard = view.findViewById(R.id.errorCard);
        errorTextView = view.findViewById(R.id.errorTextView);
        processingCard = view.findViewById(R.id.processingCard);
        processingTextView = view.findViewById(R.id.processingTextView);

        // Find cards directly by ID
        selectFileCard = view.findViewById(R.id.selectFileCard);
        selectAudioCard = view.findViewById(R.id.selectAudioCard);
        recordAudioCard = view.findViewById(R.id.recordAudioCard);

        if (selectFileCard != null) {
            selectFileCard.setOnClickListener(v -> openFilePicker());
        }
        if (selectAudioCard != null) {
            selectAudioCard.setOnClickListener(v -> openAudioFilePicker());
        }
        if (recordAudioCard != null) {
            recordAudioCard.setOnClickListener(v -> checkAndStartRecording());
        }
        if (stopRecordingButton != null) {
            stopRecordingButton.setOnClickListener(v -> stopRecording());
        }

        apiViewModel.getIsProcessing().observe(getViewLifecycleOwner(), isProcessing -> {
            if (isProcessing) {
                processingCard.setVisibility(View.VISIBLE);
                if (selectFileCard != null) {
                    selectFileCard.setClickable(false);
                    selectFileCard.setAlpha(0.5f);
                }
                if (selectAudioCard != null) {
                    selectAudioCard.setClickable(false);
                    selectAudioCard.setAlpha(0.5f);
                }
                if (recordAudioCard != null) {
                    recordAudioCard.setClickable(false);
                    recordAudioCard.setAlpha(0.5f);
                }
            } else {
                processingCard.setVisibility(View.GONE);
                if (selectFileCard != null) {
                    selectFileCard.setClickable(true);
                    selectFileCard.setAlpha(1.0f);
                }
                if (selectAudioCard != null) {
                    selectAudioCard.setClickable(true);
                    selectAudioCard.setAlpha(1.0f);
                }
                if (recordAudioCard != null) {
                    recordAudioCard.setClickable(true);
                    recordAudioCard.setAlpha(1.0f);
                }
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
                if (recordAudioCard != null) recordAudioCard.setVisibility(View.GONE);
                stopRecordingButton.setVisibility(View.VISIBLE);
            } else {
                if (recordAudioCard != null) recordAudioCard.setVisibility(View.VISIBLE);
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
        filePickerLauncher.launch(Intent.createChooser(intent, "ファイルを選択"));
    }

    private void openAudioFilePicker() {
        audioPickerLauncher.launch("audio/*");
    }

    private void checkAndStartRecording() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO) 
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO);
            return;
        }
        startRecording();
    }

    private void startRecording() {
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
                navController.popBackStack();
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
                navController.popBackStack();
            }

            @Override
            public void onError(String error) {
                // Error is handled by ViewModel
            }
        });
    }
}
