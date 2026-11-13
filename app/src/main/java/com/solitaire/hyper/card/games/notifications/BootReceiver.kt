package com.solitaire.hyper.card.games.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.solitaire.hyper.card.games.data.preferences.UserPreferencesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Reschedules the daily reminder upon device reboot if enabled.
 */
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        val action = intent?.action
        Log.d(TAG, "Device rebooted ($action). Checking daily reminder preferences...")

        try {
            NotificationHelper.initialize(context)
            val repo = UserPreferencesRepository(context)
            CoroutineScope(Dispatchers.IO).launch {
                val settings = repo.getSnapshot()
                if (settings.dailyReminderEnabled) {
                    NotificationScheduler.scheduleDailyReminder(
                        context,
                        settings.dailyReminderHour,
                        settings.dailyReminderMinute
                    )
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Error restoring daily reminder on boot: ${e.message}")
        }
    }

    companion object {
        private const val TAG = "BootReceiver"
    }
}
