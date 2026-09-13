package com.solitaire.hyper.card.games.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

/**
 * Reschedules hourly game reminders upon device reboot or app update.
 */
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        val action = intent?.action
        Log.d(TAG, "Device rebooted or app updated. Restoring hourly notification schedule: $action")

        try {
            NotificationHelper.initialize(context)
            NotificationScheduler.scheduleNextHourlyAlarm(context, delayMs = NotificationScheduler.ONE_HOUR_MS)
        } catch (e: Exception) {
            Log.w(TAG, "Error restoring hourly reminder schedule on boot: ${e.message}")
        }
    }

    companion object {
        private const val TAG = "BootReceiver"
    }
}
