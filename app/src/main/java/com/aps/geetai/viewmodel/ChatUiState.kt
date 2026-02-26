package com.aps.geetai.viewmodel

import com.aps.geetai.model.ChatMessage
import com.aps.geetai.model.Mood
import com.aps.geetai.model.Sender

/**
 * VIEWMODEL LAYER — UI State
 *
 * A single data class that fully describes what the ChatScreen should render.
 * The composable maps this state to pixels — nothing more.
 *
 * Replaces the old OutputTextState sealed class + raw mutableStateListOf<String>
 * with one unified, predictable state object.
 */
data class ChatUiState(
    val messages: List<ChatMessage> = listOf(
        ChatMessage(text = "Welcome to GeetaChat! 🙏", sender = Sender.BOT),
        ChatMessage(text = "Choose a mood or type your message.", sender = Sender.BOT)
    ),
    val selectedMood: Mood = Mood.NEUTRAL,
    val isLoading: Boolean = false,
    val error: String? = null
)