package com.solitaire.hyper.card.games.notifications

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.solitaire.hyper.card.games.MainActivity
import com.solitaire.hyper.card.games.R

/**
 * Triggered periodically by AlarmManager every hour when the user has not played.
 * Runs 100% offline and online without requiring any internet connection.
 */
class GameReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        Log.d(TAG, "GameReminderReceiver alarm fired")

        try {
            // Check how long it has been since the user was active in the game
            val prefs = context.getSharedPreferences(NotificationScheduler.PREFS_NAME, Context.MODE_PRIVATE)
            val lastPlayTime = prefs.getLong(NotificationScheduler.KEY_LAST_PLAY_TIMESTAMP, 0L)
            val now = System.currentTimeMillis()
            val timeSinceLastPlay = now - lastPlayTime

            // If the user hasn't played in at least 50 minutes (approx. 1 hour), show the notification
            val minIdleTimeMs = 50 * 60 * 1000L
            if (lastPlayTime == 0L || timeSinceLastPlay >= minIdleTimeMs) {
                showHourlyGameReminder(context)
            } else {
                Log.d(TAG, "User was active recently ($timeSinceLastPlay ms ago); skipping notification for now.")
            }
        } catch (e: Exception) {
            Log.w(TAG, "Error checking user activity: ${e.message}")
        } finally {
            // Always schedule the next check for 1 hour from now to ensure continuous hourly reminders
            NotificationScheduler.scheduleNextHourlyAlarm(context, delayMs = NotificationScheduler.ONE_HOUR_MS)
        }
    }

    private fun showHourlyGameReminder(context: Context) {
        // Android 13+ runtime permission check
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.i(TAG, "POST_NOTIFICATIONS permission not granted; skipping notification")
                return
            }
        }

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            ?: return

        // Make sure notification channel exists
        NotificationHelper.initialize(context)

        // Get an enticing rotating message
        val reminder = ReminderMessages.getNextMessage(context)

        val launchIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("from_hourly_notification", true)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            NotificationHelper.NOTIFICATION_ID_HOURLY,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, NotificationHelper.CHANNEL_HOURLY_REMINDERS)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(reminder.title)
            .setContentText(reminder.body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(reminder.body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .addAction(
                android.R.drawable.ic_media_play,
                "Play Now",
                pendingIntent
            )
            .build()

        try {
            notificationManager.notify(NotificationHelper.NOTIFICATION_ID_HOURLY, notification)
            Log.d(TAG, "Posted hourly game notification: ${reminder.title}")
        } catch (e: Exception) {
            Log.w(TAG, "Failed posting hourly game notification: ${e.message}")
        }
    }

    companion object {
        private const val TAG = "GameReminderReceiver"
    }
}
