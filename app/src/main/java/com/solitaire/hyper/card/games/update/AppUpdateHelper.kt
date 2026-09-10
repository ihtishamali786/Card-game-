package com.solitaire.hyper.card.games.update

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.solitaire.hyper.card.games.notifications.NotificationHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed class UpdateStatus {
    object Idle : UpdateStatus()
    object Checking : UpdateStatus()
    data class Available(
        val availableVersionCode: Int,
        val availableVersionName: String,
        val updateNotes: String,
        val appUpdateInfo: AppUpdateInfo? = null
    ) : UpdateStatus()
    object UpToDate : UpdateStatus()
    data class Error(val message: String) : UpdateStatus()
}

/**
 * Manages Google Play In-App Updates, checks for new releases,
 * presents the "Please update your app" dialog, and fires system notifications.
 */
object AppUpdateHelper {
    private const val TAG = "AppUpdateHelper"
    const val REQUEST_CODE_IN_APP_UPDATE = 8821

    private var appUpdateManager: AppUpdateManager? = null
    private var cachedAppUpdateInfo: AppUpdateInfo? = null

    private val _updateStatus = MutableStateFlow<UpdateStatus>(UpdateStatus.Idle)
    val updateStatus: StateFlow<UpdateStatus> = _updateStatus.asStateFlow()

    /**
     * Checks Google Play for any published app updates.
     * When an update is detected, it updates [updateStatus] and triggers [NotificationHelper.showAppUpdateNotification].
     */
    fun checkForAppUpdate(
        context: Context,
        isUserInitiated: Boolean = false,
        onResult: ((UpdateStatus) -> Unit)? = null
    ) {
        _updateStatus.value = UpdateStatus.Checking
        try {
            val manager = appUpdateManager ?: AppUpdateManagerFactory.create(context).also {
                appUpdateManager = it
            }

            val appUpdateInfoTask = manager.appUpdateInfo
            appUpdateInfoTask.addOnSuccessListener { info ->
                cachedAppUpdateInfo = info
                if (info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                    val availCode = info.availableVersionCode()
                    val availName = "v$availCode"
                    val notes = "A new update for Solitaire Hyper Card Games is available on Google Play! Please update your app to enjoy brand new card themes, puzzles, and performance improvements."
                    val status = UpdateStatus.Available(
                        availableVersionCode = availCode,
                        availableVersionName = availName,
                        updateNotes = notes,
                        appUpdateInfo = info
                    )
                    _updateStatus.value = status
                    onResult?.invoke(status)

                    // Post system notification
                    NotificationHelper.showAppUpdateNotification(
                        context = context,
                        versionName = availName,
                        updateNotes = notes
                    )
                } else {
                    val status = UpdateStatus.UpToDate
                    _updateStatus.value = status
                    onResult?.invoke(status)
                }
            }.addOnFailureListener { error ->
                Log.w(TAG, "Play Store in-app update check response: ${error.message}")
                val status = if (isUserInitiated) {
                    UpdateStatus.UpToDate
                } else {
                    UpdateStatus.Idle
                }
                _updateStatus.value = status
                onResult?.invoke(status)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error initiating app update check: ${e.message}")
            val status = UpdateStatus.Error(e.message ?: "Unknown error")
            _updateStatus.value = status
            onResult?.invoke(status)
        }
    }

    /**
     * Triggers the update flow: attempts Google Play In-App Flexible Update first,
     * or opens the Google Play Store directly to the app details page.
     */
    fun startUpdateFlow(activity: Activity) {
        val info = cachedAppUpdateInfo
        val manager = appUpdateManager
        if (manager != null && info != null && info.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
            try {
                manager.startUpdateFlowForResult(
                    info,
                    AppUpdateType.FLEXIBLE,
                    activity,
                    REQUEST_CODE_IN_APP_UPDATE
                )
                return
            } catch (e: Exception) {
                Log.w(TAG, "Failed starting flexible update flow, falling back to Play Store: ${e.message}")
            }
        }
        openPlayStore(activity)
    }

    /**
     * Directly opens the app's Google Play Store listing page.
     */
    fun openPlayStore(context: Context) {
        val packageName = context.packageName
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName")).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(webIntent)
        }
    }

    /**
     * Dismisses the in-app update dialog prompt.
     */
    fun dismissUpdatePrompt() {
        _updateStatus.value = UpdateStatus.Idle
    }

    /**
     * Testing hook to preview the notification & in-app dialog directly on device.
     */
    fun simulateNewUpdateForTesting(context: Context) {
        val notes = "New Update Available! Please update your app to enjoy fresh card styles, AI coach advice, and faster gameplay."
        val status = UpdateStatus.Available(
            availableVersionCode = 99,
            availableVersionName = "v7.0",
            updateNotes = notes,
            appUpdateInfo = null
        )
        _updateStatus.value = status
        NotificationHelper.showAppUpdateNotification(context, "v7.0", notes)
    }
}
