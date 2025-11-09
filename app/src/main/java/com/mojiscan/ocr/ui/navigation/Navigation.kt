package com.mojiscan.ocr.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mojiscan.ocr.ui.screen.*
import com.mojiscan.ocr.ui.viewmodel.ApiViewModel
import com.mojiscan.ocr.ui.viewmodel.TranscriptionViewModel

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object AddTranscription : Screen("add_transcription")
    object Settings : Screen("settings")
    object Detail : Screen("detail/{id}") {
        fun createRoute(id: Long) = "detail/$id"
    }
    object DataHandling : Screen("data_handling")
    object TermsOfService : Screen("terms_of_service")
    object PrivacyPolicy : Screen("privacy_policy")
}

@Composable
fun Navigation(
    transcriptionViewModel: TranscriptionViewModel,
    apiViewModel: ApiViewModel,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                transcriptionViewModel = transcriptionViewModel,
                apiViewModel = apiViewModel,
                navController = navController
            )
        }
        composable(Screen.AddTranscription.route) {
            AddTranscriptionScreen(
                transcriptionViewModel = transcriptionViewModel,
                apiViewModel = apiViewModel,
                navController = navController
            )
        }
        composable(Screen.Settings.route) {
            SettingsScreen(navController = navController)
        }
        composable(Screen.Detail.route) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")?.toLongOrNull() ?: 0L
            DetailScreen(
                id = id,
                transcriptionViewModel = transcriptionViewModel,
                navController = navController
            )
        }
        composable(Screen.DataHandling.route) {
            DataHandlingScreen(navController = navController)
        }
        composable(Screen.TermsOfService.route) {
            TermsOfServiceScreen(navController = navController)
        }
        composable(Screen.PrivacyPolicy.route) {
            PrivacyPolicyScreen(navController = navController)
        }
    }
}

