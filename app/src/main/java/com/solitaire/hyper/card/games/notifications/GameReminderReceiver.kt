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
 * BroadcastReceiver triggered by AlarmManager at the user's scheduled daily reminder time.
 */
class GameReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        Log.d(TAG, "Daily reminder alarm fired")
        NotificationHelper.showDailyReminderNotification(context)

        // Reschedule for next day if enabled
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
    }

    companion object {
        private const val TAG = "GameReminderReceiver"
    }
}
