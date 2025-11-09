package com.mojiscan.ocr.ui.screen

import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.accompanist.permissions.rememberPermissionState
import com.mojiscan.ocr.R
import com.mojiscan.ocr.ui.navigation.Screen
import com.mojiscan.ocr.ui.viewmodel.ApiViewModel
import com.mojiscan.ocr.ui.viewmodel.TranscriptionViewModel
import com.mojiscan.ocr.util.AudioRecorder
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTranscriptionScreen(
    transcriptionViewModel: TranscriptionViewModel,
    apiViewModel: ApiViewModel,
    navController: NavController
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var audioRecorder by remember { mutableStateOf<AudioRecorder?>(null) }
    var recordingFile by remember { mutableStateOf<File?>(null) }
    val isProcessing by apiViewModel.isProcessing.collectAsStateWithLifecycle()
    val processingProgress by apiViewModel.processingProgress.collectAsStateWithLifecycle()
    val errorMessage by apiViewModel.errorMessage.collectAsStateWithLifecycle()

    val recordAudioPermissionState = rememberPermissionState(Manifest.permission.RECORD_AUDIO)
    val readMediaPermissionState = rememberPermissionState(Manifest.permission.READ_MEDIA_IMAGES)

    // File picker for images
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            scope.launch {
                apiViewModel.processFile(
                    fileUri = it,
                    file = null,
                    processType = "auto",
                    onSuccess = { transcription ->
                        transcriptionViewModel.addTranscription(transcription)
                        navController.popBackStack()
                    },
                    onError = { error ->
                        // Error is handled by ViewModel
                    }
                )
            }
        }
    }

    // Audio file picker
    val audioFilePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            scope.launch {
                apiViewModel.processFile(
                    fileUri = it,
                    file = null,
                    processType = "transcribe",
                    onSuccess = { transcription ->
                        transcriptionViewModel.addTranscription(transcription)
                        navController.popBackStack()
                    },
                    onError = { error ->
                        // Error is handled by ViewModel
                    }
                )
            }
        }
    }

    LaunchedEffect(Unit) {
        audioRecorder = AudioRecorder(context)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.add_transcription)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "戻る")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Error message
            errorMessage?.let { error ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = error,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }

            // Processing indicator
            if (isProcessing) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = processingProgress ?: stringResource(R.string.processing),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // File selection button
            Button(
                onClick = {
                    if (readMediaPermissionState.hasPermission) {
                        filePickerLauncher.launch("*/*")
                    } else {
                        readMediaPermissionState.launchPermissionRequest()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isProcessing
            ) {
                Text(stringResource(R.string.add_file))
            }

            // Audio file selection button
            Button(
                onClick = {
                    audioFilePickerLauncher.launch("audio/*")
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isProcessing
            ) {
                Text("音声ファイルを選択")
            }

            // Audio recording
            audioRecorder?.let { recorder ->
                val isRecording by recorder.isRecording.collectAsStateWithLifecycle()

                if (isRecording) {
                    Button(
                        onClick = {
                            recordingFile = recorder.stopRecording()
                            recordingFile?.let { file ->
                                scope.launch {
                                    apiViewModel.processAudioFile(
                                        file = file,
                                        onSuccess = { transcription ->
                                            transcriptionViewModel.addTranscription(transcription)
                                            navController.popBackStack()
                                        },
                                        onError = { error ->
                                            // Error is handled by ViewModel
                                        }
                                    )
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Icon(Icons.Default.Stop, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.stop_recording))
                    }
                } else {
                    Button(
                        onClick = {
                            if (recordAudioPermissionState.hasPermission) {
                                recorder.startRecording()
                            } else {
                                recordAudioPermissionState.launchPermissionRequest()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isProcessing
                    ) {
                        Icon(Icons.Default.Mic, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.record_audio))
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

