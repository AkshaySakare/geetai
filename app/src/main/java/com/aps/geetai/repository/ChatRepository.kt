package com.aps.geetai.repository


interface ChatRepository {

    /**
     * Send a free-text user message and return the AI reply.
     */
    suspend fun sendMessage(userText: String): Result<String>

    /**
     * Send a mood-based prompt and return the AI reply.
     * @param moodPrompt  The expanded Gita prompt string for that mood.
     */
    suspend fun sendMoodPrompt(moodPrompt: String): Result<String>
}
