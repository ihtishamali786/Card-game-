package com.solitaire.hyper.card.games.update

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed class UpdateStatus {
    object Idle : UpdateStatus()
    object Checking : UpdateStatus()
    data class Available(
        val availableVersionCode: Int,
        val availableVersionName: String,
        val updateNotes: String
    ) : UpdateStatus()
    object UpToDate : UpdateStatus()
    data class Error(val message: String) : UpdateStatus()
}

/**
 * Handles App Update links directly via Android Market / Google Play Store intents.
 * Zero proprietary SDK dependencies, zero data collection.
 */
object AppUpdateHelper {
    const val REQUEST_CODE_IN_APP_UPDATE = 8821

    private val _updateStatus = MutableStateFlow<UpdateStatus>(UpdateStatus.Idle)
    val updateStatus: StateFlow<UpdateStatus> = _updateStatus.asStateFlow()

    fun checkForAppUpdate(
        context: Context,
        isUserInitiated: Boolean = false,
        onResult: ((UpdateStatus) -> Unit)? = null
    ) {
        val status = UpdateStatus.UpToDate
        _updateStatus.value = status
        onResult?.invoke(status)
    }

    fun startUpdateFlow(activity: Activity) {
        openPlayStore(activity)
    }

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

    fun dismissUpdatePrompt() {
        _updateStatus.value = UpdateStatus.Idle
    }

    fun simulateNewUpdateForTesting(context: Context) {
        _updateStatus.value = UpdateStatus.Idle
    }
}
