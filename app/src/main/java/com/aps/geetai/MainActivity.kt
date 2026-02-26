package com.aps.geetai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

import com.aps.geetai.ui.ChatScreen
import com.aps.geetai.ui.theme.SmartAITheme

import com.aps.geetai.viewmodel.ChatViewModel
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.UnstableApi

/**
 * LAYER — Activity
 *Responsibilities (and ONLY these):
 *   Obtain the Hilt-injected ViewModel via `by viewModels()`
 *   Collect the single StateFlow into Compose state
 *   Pass state + event lambdas into ChatScreen
 * No business logic, no Firebase calls, no string formatting here.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // Hilt injects ChatRepositoryImpl into ChatViewModel automatically
    private val viewModel: ChatViewModel by viewModels()

    @UnstableApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            SmartAITheme {
                // Observe the single source-of-truth state
                val uiState by viewModel.uiState.collectAsState()

                ChatScreen(
                    uiState = uiState,
                    onUserMessage = viewModel::onUserMessage,
                    onMoodSelected = viewModel::onMoodSelected,
                    onDismissError = viewModel::dismissError,
                    onFinish = ::finish
                )
            }
        }
    }
}