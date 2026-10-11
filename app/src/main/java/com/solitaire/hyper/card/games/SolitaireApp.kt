package com.solitaire.hyper.card.games

import android.app.Application
import android.util.Log
import com.solitaire.hyper.card.games.data.database.AppDatabase
import com.solitaire.hyper.card.games.notifications.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SolitaireApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Log.i("SolitaireApp", "Initializing Solitaire-Hyper Card Games...")

        // 1. Initialize Local Notification Channels & Schedule Reminders
        NotificationHelper.initialize(this)
        com.solitaire.hyper.card.games.notifications.NotificationScheduler.recordUserActivity(this)

        // 3. Pre-warm database
        CoroutineScope(Dispatchers.IO).launch {
            try {
                AppDatabase.getDatabase(this@SolitaireApp)
            } catch (e: Exception) {
                Log.w("SolitaireApp", "Database warm-up: ${e.message}")
            }
        }
    }
}
