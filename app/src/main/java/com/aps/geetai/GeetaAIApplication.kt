package com.aps.geetai

import android.app.Application
import com.google.firebase.Firebase
import com.google.firebase.initialize
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class — initialises Firebase and enables Hilt DI.
 * No changes needed from your original.
 */
@HiltAndroidApp
class GeetaAIApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Firebase.initialize(this)
    }
}
