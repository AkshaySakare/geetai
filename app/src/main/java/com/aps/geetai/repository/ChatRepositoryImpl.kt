package com.aps.geetai.repository

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import javax.inject.Inject
import javax.inject.Singleton

/**
 * REPOSITORY LAYER — Implementation
 *
 * ALL Firebase / Gemini API calls live here.
 * The ViewModel never imports Firebase — it only calls this interface.
 *
 * Annotated @Singleton so Hilt shares one instance across the app.
 */
@Singleton
class ChatRepositoryImpl @Inject constructor() : ChatRepository {

    // Lazily create the generative model once
    private val generativeModel by lazy {
        Firebase.ai(backend = GenerativeBackend.googleAI())
            .generativeModel("gemini-2.5-flash")
    }

    override suspend fun sendMessage(userText: String): Result<String> {
        return runCatching {
            val response = generativeModel.generateContent(userText)
            response.text ?: "No response received."
        }.onFailure { error ->
            Log.e("ChatRepository", "sendMessage error: $error")
        }
    }

    override suspend fun sendMoodPrompt(moodPrompt: String): Result<String> {
        return runCatching {
            val response = generativeModel.generateContent(moodPrompt)
            response.text ?: "No response received."
        }.onFailure { error ->
            Log.e("ChatRepository", "sendMoodPrompt error: $error")
        }
    }
}
