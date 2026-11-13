package com.solitaire.hyper.card.games.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.solitaire.hyper.card.games.MainActivity
import com.solitaire.hyper.card.games.R

/**
 * Handles notification channels and daily reminder notifications for Solitaire Joker: Klondike Zen.
 */
object NotificationHelper {
    private const val TAG = "NotificationHelper"
    const val CHANNEL_DAILY_REMINDER = "channel_daily_reminder"
    const val NOTIFICATION_ID_DAILY = 8801

    fun initialize(context: Context) {
        createNotificationChannels(context)
    }

    private fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
                ?: return

            val dailyChannel = NotificationChannel(
                CHANNEL_DAILY_REMINDER,
                "Daily Zen Reminder",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Gentle daily reminder to take a relaxing Solitaire pause"
                enableVibration(true)
            }

            notificationManager.createNotificationChannel(dailyChannel)
        }
    }

    fun showDailyReminderNotification(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (androidx.core.content.ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != android.content.pm.PackageManager.PERMISSION_GRANTED
            ) {
                Log.i(TAG, "POST_NOTIFICATIONS permission not granted; skipping notification")
                return
            }
        }

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            ?: return

        val launchIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            NOTIFICATION_ID_DAILY,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = "Solitaire Joker: Klondike Zen"
        val message = "Relax. Play. Win with the Joker. Your daily card break is ready!"

        val notification = NotificationCompat.Builder(context, CHANNEL_DAILY_REMINDER)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        try {
            notificationManager.notify(NOTIFICATION_ID_DAILY, notification)
            Log.d(TAG, "Daily reminder notification posted successfully")
        } catch (e: Exception) {
            Log.w(TAG, "Failed posting reminder notification: ${e.message}")
        }
    }
}
