package com.mojiscan.ocr

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.mojiscan.ocr.ui.navigation.Navigation
import com.mojiscan.ocr.ui.theme.OCRAppTheme
import com.mojiscan.ocr.ui.viewmodel.ApiViewModel
import com.mojiscan.ocr.ui.viewmodel.TranscriptionViewModel

class MainActivity : ComponentActivity() {
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Request permissions
        requestPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO,
                Manifest.permission.READ_MEDIA_AUDIO
            )
        )

        setContent {
            OCRAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val transcriptionViewModel: TranscriptionViewModel = viewModel()
                    val apiViewModel: ApiViewModel = viewModel()
                    Navigation(
                        transcriptionViewModel = transcriptionViewModel,
                        apiViewModel = apiViewModel
                    )
                }
            }
        }
    }
}

