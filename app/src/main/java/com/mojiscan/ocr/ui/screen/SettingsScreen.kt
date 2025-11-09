package com.mojiscan.ocr.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mojiscan.ocr.R
import com.mojiscan.ocr.ui.navigation.Screen
import android.content.Intent
import android.net.Uri

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    val context = LocalContext.current
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings)) },
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
                .verticalScroll(rememberScrollState())
        ) {
            SettingsItem(
                title = stringResource(R.string.update_info),
                icon = Icons.Default.Info,
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(stringResource(R.string.github_url)))
                    context.startActivity(intent)
                }
            )

            Divider()

            SettingsItem(
                title = stringResource(R.string.feedback),
                icon = Icons.Default.QuestionAnswer,
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(stringResource(R.string.feedback_url)))
                    context.startActivity(intent)
                }
            )

            Divider()

            SettingsItem(
                title = stringResource(R.string.donation),
                icon = Icons.Default.Favorite,
                onClick = {
                    navController.navigate(Screen.Donation.route)
                }
            )

            Divider()

            SettingsItem(
                title = stringResource(R.string.data_handling),
                icon = Icons.Default.Policy,
                onClick = {
                    navController.navigate(Screen.DataHandling.route)
                }
            )

            Divider()

            SettingsItem(
                title = stringResource(R.string.terms_of_service),
                icon = Icons.Default.Policy,
                onClick = {
                    navController.navigate(Screen.TermsOfService.route)
                }
            )

            Divider()

            SettingsItem(
                title = stringResource(R.string.privacy_policy),
                icon = Icons.Default.Policy,
                onClick = {
                    navController.navigate(Screen.PrivacyPolicy.route)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "${stringResource(R.string.version)} 1.0.0",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun SettingsItem(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    ListItem(
        headlineContent = { Text(title) },
        leadingContent = {
            Icon(icon, contentDescription = null)
        },
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    )
}

