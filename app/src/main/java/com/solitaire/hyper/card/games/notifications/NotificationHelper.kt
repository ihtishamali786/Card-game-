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
                "Game Updates & Events",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Special solitaire events and rewards"
            }

            notificationManager.createNotificationChannel(dailyChannel)
            notificationManager.createNotificationChannel(updatesChannel)
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
