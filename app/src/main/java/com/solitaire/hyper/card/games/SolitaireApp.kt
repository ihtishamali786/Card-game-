package com.solitaire.hyper.card.games

import android.app.Application
import android.util.Log
import com.solitaire.hyper.card.games.ads.AdManager
import com.solitaire.hyper.card.games.data.database.AppDatabase
import com.solitaire.hyper.card.games.notifications.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SolitaireApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Log.i("SolitaireApp", "Initializing Solitaire-Hyper Card Games...")

        // 1. Initialize AdMob Ads safely
        AdManager.initialize(this)

        // 2. Initialize Notification Channels & Push
        NotificationHelper.initialize(this)

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
