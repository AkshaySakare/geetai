package com.aps.geetai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aps.geetai.model.ChatMessage
import com.aps.geetai.model.Mood
import com.aps.geetai.model.Sender
import com.aps.geetai.repository.ChatRepository
import com.aps.geetai.viewmodel.ChatUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * VIEWMODEL LAYER
 *
 * Rules enforced here:
 *  ✅ Depends only on ChatRepository (interface), never Firebase directly
 *  ✅ No Android/Compose UI imports (no Color, Modifier, Context, etc.)
 *  ✅ Exposes a single StateFlow<ChatUiState> — one source of truth
 *  ✅ All business logic (mood prompts, message formatting) lives here
 *  ✅ Survives configuration changes automatically
 */
@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repository: ChatRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    // -----------------------------------------------------------------------
    // Mood → Prompt mapping  (business logic, belongs in ViewModel not UI)
    // -----------------------------------------------------------------------
    private val moodPromptMap = mapOf(
        Mood.HAPPY     to "Share a joyful and uplifting Bhagavad Gita shloka.",
        Mood.SAD       to "Share a comforting verse from the Bhagavad Gita for someone feeling sad.",
        Mood.ANGRY     to "Give a shloka that teaches how to control anger.",
        Mood.ANXIOUS   to "I'm feeling anxious. Suggest a verse that brings peace.",
        Mood.CONFUSED  to "I need guidance. Share a Gita shloka about decision-making.",
        Mood.PEACEFUL  to "Give a shloka about peace and spiritual calm.",
        Mood.MOTIVATED to "Share a powerful shloka to stay focused and strong.",
        Mood.NEUTRAL   to "Share a meaningful shloka from the Bhagavad Gita."
    )

    // -----------------------------------------------------------------------
    // Public actions — called by the UI layer
    // -----------------------------------------------------------------------

    /** Called when the user taps Send on a typed message. */
    fun onUserMessage(input: String) {
        if (input.isBlank()) return
        appendMessage(ChatMessage(text = input, sender = Sender.USER))
        executeAiCall { repository.sendMessage(input) }
    }

    /** Called when the user taps a mood chip. */
    fun onMoodSelected(mood: Mood) {
        val prompt = moodPromptMap[mood] ?: "Share a shloka from the Gita."
        _uiState.update { it.copy(selectedMood = mood) }
        appendMessage(
            ChatMessage(
                text = "${mood.chipLabel} — ${mood.displayName}",
                sender = Sender.USER
            )
        )
        executeAiCall { repository.sendMoodPrompt(prompt) }
    }

    /** Called when the user dismisses the error snackbar. */
    fun dismissError() {
        _uiState.update { it.copy(error = null) }
    }

    // -----------------------------------------------------------------------
    // Private helpers
    // -----------------------------------------------------------------------

    private fun appendMessage(message: ChatMessage) {
        _uiState.update { state ->
            state.copy(messages = state.messages + message)
        }
    }

    /**
     * Generic coroutine launcher for any repository call.
     * Sets isLoading, appends the reply on success, stores error on failure.
     */
    private fun executeAiCall(apiCall: suspend () -> Result<String>) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            apiCall()
                .onSuccess { reply ->
                    appendMessage(ChatMessage(text = reply, sender = Sender.BOT))
                }
                .onFailure { throwable ->
                    _uiState.update { it.copy(error = throwable.localizedMessage ?: "Unknown error") }
                }

            _uiState.update { it.copy(isLoading = false) }
        }
    }
}