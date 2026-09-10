package com.solitaire.hyper.card.games.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log

/**
 * Handles notification channels, daily challenge alerts, and OneSignal integration.
 */
object NotificationHelper {
    private const val TAG = "NotificationHelper"
    const val ONESIGNAL_APP_ID = "7e82df7b-2f28-47c2-a528-c95f6fe625eb"

    const val CHANNEL_DAILY_CHALLENGES = "channel_daily_challenges"
    const val CHANNEL_UPDATES = "channel_updates"
    const val NOTIFICATION_ID_UPDATE = 7701
    const val NOTIFICATION_ID_DAILY = 7702

    fun initialize(context: Context) {
        createNotificationChannels(context)
        initOneSignal(context)
    }

    private fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
                ?: return

            val dailyChannel = NotificationChannel(
                CHANNEL_DAILY_CHALLENGES,
                "Daily Challenge Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Daily notifications for new Solitaire puzzle challenges"
                enableVibration(true)
            }

            val updatesChannel = NotificationChannel(
                CHANNEL_UPDATES,
                "App Updates & New Releases",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Alerts when a new version or update is available on Google Play"
                enableVibration(true)
                setShowBadge(true)
            }

            notificationManager.createNotificationChannel(dailyChannel)
            notificationManager.createNotificationChannel(updatesChannel)
        }
    }

    /**
     * Posts a status-bar notification informing the player to update their app.
     * Tapping the notification takes the user directly to the Google Play Store update page.
     */
    fun showAppUpdateNotification(
        context: Context,
        versionName: String? = null,
        updateNotes: String? = null
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (androidx.core.content.ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != android.content.pm.PackageManager.PERMISSION_GRANTED
            ) {
                Log.i(TAG, "POST_NOTIFICATIONS permission not yet granted; skipping status bar notification")
                return
            }
        }

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            ?: return

        val packageName = context.packageName
        val playStoreIntent = try {
            android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse("market://details?id=$packageName")).apply {
                addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        } catch (e: Exception) {
            android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse("https://play.google.com/store/apps/details?id=$packageName")).apply {
                addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        }

        val pendingIntent = android.app.PendingIntent.getActivity(
            context,
            NOTIFICATION_ID_UPDATE,
            playStoreIntent,
            android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
        )

        val title = if (!versionName.isNullOrBlank()) {
            "Update Available ($versionName): Please update your app"
        } else {
            "Update Available: Please update your app"
        }

        val content = updateNotes ?: "A new update for Solitaire Hyper Card Games is available on Google Play. Please update your app to the latest version for new card themes and enhancements!"

        val notification = androidx.core.app.NotificationCompat.Builder(context, CHANNEL_UPDATES)
            .setSmallIcon(com.solitaire.hyper.card.games.R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(content)
            .setStyle(androidx.core.app.NotificationCompat.BigTextStyle().bigText(content))
            .setPriority(androidx.core.app.NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .addAction(
                android.R.drawable.stat_sys_download_done,
                "Update Now",
                pendingIntent
            )
            .build()

        try {
            notificationManager.notify(NOTIFICATION_ID_UPDATE, notification)
            Log.d(TAG, "App update notification posted successfully")
        } catch (e: Exception) {
            Log.w(TAG, "Failed posting app update notification: ${e.message}")
        }
    }

    private fun initOneSignal(context: Context) {
        try {
            // Reflection-based safe invocation to ensure zero crash if SDK binary is dynamically updated
            val oneSignalClass = Class.forName("com.onesignal.OneSignal")
            val initWithContextMethod = oneSignalClass.getMethod("initWithContext", Context::class.java, String::class.java)
            initWithContextMethod.invoke(null, context, ONESIGNAL_APP_ID)
            Log.d(TAG, "OneSignal successfully initialized with App ID: $ONESIGNAL_APP_ID")
        } catch (e: ClassNotFoundException) {
            Log.i(TAG, "OneSignal SDK optional module not embedded; local notification channels active.")
        } catch (e: Exception) {
            Log.w(TAG, "OneSignal init safely caught: ${e.message}")
        }
    }
}
