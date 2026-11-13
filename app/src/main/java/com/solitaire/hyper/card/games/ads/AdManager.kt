package com.solitaire.hyper.card.games.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

/**
 * Responsible Google AdMob integration for Solitaire Joker: Klondike Zen.
 *
 * RULES APPLIED:
 * 1. Banner: Small, unobtrusive banner on Home Screen (suppressed if Remove Ads purchased).
 * 2. Interstitial: Shown ONLY after a game is finished (never mid-game), max once every 3 minutes (180s). Suppressed if Remove Ads purchased.
 * 3. Rewarded Video: Voluntary only, watched by player to receive extra Joker helper cards. Remains available even with Remove Ads.
 */
object AdManager {

    private const val TAG = "AdManager"

    // -----------------------------------------------------------------------------------------
    // AD UNIT IDS: Standard Google AdMob Test IDs (Replace with your production IDs for release)
    // -----------------------------------------------------------------------------------------
    const val BANNER_AD_UNIT_ID = "ca-app-pub-3940256099942544/6300978111" // TEST BANNER ID
    const val INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712" // TEST INTERSTITIAL ID
    const val REWARDED_AD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917" // TEST REWARDED ID

    // Minimum time between interstitials: 3 minutes (180 seconds)
    private const val MIN_INTERSTITIAL_INTERVAL_MS = 180_000L
    private var lastInterstitialTimeMs: Long = 0L

    private var interstitialAd: InterstitialAd? = null
    private var isInterstitialLoading: Boolean = false

    private var rewardedAd: RewardedAd? = null
    private var isRewardedLoading: Boolean = false

    private var isInitialized = false

    fun initialize(context: Context) {
        if (isInitialized) return
        try {
            MobileAds.initialize(context) { status ->
                Log.d(TAG, "Google Mobile Ads initialized: $status")
                isInitialized = true
                preloadInterstitial(context)
                preloadRewarded(context)
            }
        } catch (e: Exception) {
            Log.w(TAG, "AdMob initialization error: ${e.message}")
        }
    }

    fun preloadInterstitial(context: Context) {
        if (interstitialAd != null || isInterstitialLoading) return
        isInterstitialLoading = true

        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            context,
            INTERSTITIAL_AD_UNIT_ID,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    isInterstitialLoading = false
                    Log.d(TAG, "Interstitial ad successfully loaded")
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    interstitialAd = null
                    isInterstitialLoading = false
                    Log.w(TAG, "Interstitial ad failed to load: ${error.message}")
                }
            }
        )
    }

    /**
     * Shows an interstitial ad ONLY after a completed game.
     * Respects isAdsRemoved and strictly enforces the 3-minute rate limit.
     */
    fun onGameCompleted(activity: Activity, isAdsRemoved: Boolean, onComplete: () -> Unit = {}) {
        if (isAdsRemoved) {
            Log.d(TAG, "Ads removed by user; skipping post-game interstitial.")
            onComplete()
            return
        }

        val now = System.currentTimeMillis()
        if (now - lastInterstitialTimeMs < MIN_INTERSTITIAL_INTERVAL_MS) {
            Log.d(TAG, "Interstitial skipped: 3-minute cooldown active.")
            onComplete()
            return
        }

        val ad = interstitialAd
        if (ad != null) {
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    interstitialAd = null
                    lastInterstitialTimeMs = System.currentTimeMillis()
                    preloadInterstitial(activity)
                    onComplete()
                }

                override fun onAdFailedToShowFullScreenContent(error: AdError) {
                    interstitialAd = null
                    preloadInterstitial(activity)
                    onComplete()
                }
            }
            ad.show(activity)
        } else {
            preloadInterstitial(activity)
            onComplete()
        }
    }

    fun preloadRewarded(context: Context) {
        if (rewardedAd != null || isRewardedLoading) return
        isRewardedLoading = true

        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(
            context,
            REWARDED_AD_UNIT_ID,
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    rewardedAd = ad
                    isRewardedLoading = false
                    Log.d(TAG, "Rewarded video ad successfully loaded")
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    rewardedAd = null
                    isRewardedLoading = false
                    Log.w(TAG, "Rewarded video ad failed to load: ${error.message}")
                }
            }
        )
    }

    /**
     * Shows rewarded video to earn extra Joker cards.
     */
    fun showRewardedAd(
        activity: Activity,
        onRewardEarned: () -> Unit,
        onDismissOrFailed: () -> Unit = {}
    ) {
        val ad = rewardedAd
        if (ad != null) {
            var rewardGranted = false
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    rewardedAd = null
                    preloadRewarded(activity)
                    if (!rewardGranted) {
                        onDismissOrFailed()
                    }
                }

                override fun onAdFailedToShowFullScreenContent(error: AdError) {
                    rewardedAd = null
                    preloadRewarded(activity)
                    onDismissOrFailed()
                }
            }
            ad.show(activity) { _ ->
                rewardGranted = true
                onRewardEarned()
                onDismissOrFailed()
            }
        } else {
            // If ad is not ready in test environment, allow gentle fallback reward so user testing is not blocked
            preloadRewarded(activity)
            onRewardEarned()
            onDismissOrFailed()
        }
    }
}
