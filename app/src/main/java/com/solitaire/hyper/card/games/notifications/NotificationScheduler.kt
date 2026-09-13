package com.solitaire.hyper.card.games.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log

object NotificationScheduler {
    private const val TAG = "NotificationScheduler"
    const val PREFS_NAME = "solitaire_notification_prefs"
    const val KEY_LAST_PLAY_TIMESTAMP = "key_last_play_timestamp"
    const val KEY_NEXT_ALARM_TIMESTAMP = "key_next_alarm_timestamp"
    private const val KEY_MESSAGE_INDEX = "key_message_index"
    private const val REQUEST_CODE_ALARM = 8801

    // 1 hour in milliseconds
    const val ONE_HOUR_MS = 60 * 60 * 1000L

    /**
     * Records that the user was active in the game right now.
     * Reschedules the next hourly reminder to trigger 1 hour after this moment.
     */
    fun recordUserActivity(context: Context) {
        try {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit().putLong(KEY_LAST_PLAY_TIMESTAMP, System.currentTimeMillis()).apply()
            scheduleNextHourlyAlarm(context, delayMs = ONE_HOUR_MS)
            Log.d(TAG, "User activity recorded. Next hourly alarm set for 1 hour from now.")
        } catch (e: Exception) {
            Log.w(TAG, "Error recording user activity: ${e.message}")
        }
    }

    /**
     * Schedules the next hourly alarm.
     * Operates completely offline and online via Android AlarmManager.
     */
    fun scheduleNextHourlyAlarm(context: Context, delayMs: Long = ONE_HOUR_MS) {
        try {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
            val intent = Intent(context, GameReminderReceiver::class.java)

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                REQUEST_CODE_ALARM,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val triggerAtMillis = System.currentTimeMillis() + delayMs

            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit().putLong(KEY_NEXT_ALARM_TIMESTAMP, triggerAtMillis).apply()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
            } else {
                alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
            }
            Log.d(TAG, "Successfully scheduled hourly notification in ${delayMs / 1000}s (Offline & Online capable)")
        } catch (e: Exception) {
            Log.w(TAG, "Error scheduling hourly alarm: ${e.message}")
        }
    }

    fun getNextMessageIndex(context: Context, totalCount: Int): Int {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val currentIndex = prefs.getInt(KEY_MESSAGE_INDEX, 0)
        val nextIndex = (currentIndex + 1) % totalCount
        prefs.edit().putInt(KEY_MESSAGE_INDEX, nextIndex).apply()
        return currentIndex
    }

    fun getRemainingTimeMs(context: Context): Long {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val nextAlarm = prefs.getLong(KEY_NEXT_ALARM_TIMESTAMP, 0L)
        val remaining = nextAlarm - System.currentTimeMillis()
        return if (remaining > 0) remaining else 0L
    }

    fun formatRemainingTime(remainingMs: Long): String {
        if (remainingMs <= 0) return "Ready"
        val totalSecs = (remainingMs + 999) / 1000
        val mins = totalSecs / 60
        val secs = totalSecs % 60
        return if (mins >= 60) {
            val hours = mins / 60
            val remMins = mins % 60
            String.format(java.util.Locale.US, "%dh %02dm", hours, remMins)
        } else {
            String.format(java.util.Locale.US, "%02d:%02d", mins, secs)
        }
    }
}
