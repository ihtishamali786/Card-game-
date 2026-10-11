package com.solitaire.hyper.card.games.ads

import android.app.Activity
import android.content.Context

/**
 * 100% Ad-Free, Zero Tracking & Zero Data Collection Manager.
 * All Google Mobile Ads SDK / AdMob dependencies are removed.
 * Rewarded actions immediately grant bonuses without ads or network requests.
 */
object AdManager {
    const val TEST_BANNER_ID = ""
    val bannerAdUnitId: String = ""

    fun markFirstLaunchComplete() {}

    fun initialize(context: Context) {
        // Zero third-party Ad SDK initialization
    }

    fun onGameCompleted(activity: Activity, isAdFree: Boolean, onComplete: () -> Unit = {}) {
        onComplete()
    }

    fun showInterstitialIfReady(activity: Activity, isAdFree: Boolean, onDismiss: () -> Unit = {}) {
        onDismiss()
    }

    fun checkAndShowPeriodicTimeAd(activity: Activity, isAdFree: Boolean) {}

    fun preloadAppOpen(context: Context) {}

    fun showAppOpenAdIfReady(activity: Activity, isAdFree: Boolean) {}

    fun showExitAd(activity: Activity, isAdFree: Boolean, onDismiss: () -> Unit) {
        onDismiss()
    }

    /**
     * Immediately grants the reward to the player without any ad viewing or network request.
     */
    fun showRewardedAd(
        activity: Activity,
        onRewardEarned: () -> Unit,
        onAdDismissed: () -> Unit = {},
        onAdFailed: (String) -> Unit = {},
        onDismissOrFailed: () -> Unit = {}
    ) {
        onRewardEarned()
        onAdDismissed()
        onDismissOrFailed()
    }

    fun showRewardedInterstitialAd(
        activity: Activity,
        isAdFree: Boolean,
        onRewardEarned: () -> Unit,
        onAdDismissed: () -> Unit = {},
        onAdFailed: (String) -> Unit = {},
        onDismissOrFailed: () -> Unit = {}
    ) {
        onRewardEarned()
        onAdDismissed()
        onDismissOrFailed()
    }
}
