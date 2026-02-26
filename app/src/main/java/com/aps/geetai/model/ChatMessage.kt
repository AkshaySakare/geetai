package com.aps.geetai.model

import java.util.UUID

/**
 * MODEL LAYER
 *
 * Represents a single chat message.
 * Using a proper data class instead of raw formatted strings
 * keeps the model clean and testable.
 */
data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val sender: Sender
)

enum class Sender {
    USER,   // messages sent by the human
    BOT     // responses from Gemini / Firebase AI
}
