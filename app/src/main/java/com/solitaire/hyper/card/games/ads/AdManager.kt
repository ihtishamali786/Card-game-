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
import com.solitaire.hyper.card.games.BuildConfig

/**
 * Manages Google AdMob ad units with policy-compliant, fail-safe lifecycles.
 * Uses official Google sample test ad unit IDs in debug builds to prevent policy violations,
 * and seamlessly switches to the user's supplied production IDs in release builds.
 */
object AdManager {
    private const val TAG = "AdManager"

    // Production Unit IDs provided by the owner
    private const val PROD_BANNER_ID = "ca-app-pub-2835586363285222/9008521623"
    private const val PROD_INTERSTITIAL_ID = "ca-app-pub-2835586363285222/6527547141"
    private const val PROD_REWARDED_ID = "ca-app-pub-2835586363285222/7068753999"
    private const val PROD_APP_OPEN_ID = "ca-app-pub-2835586363285222/4092955494"

    // Official Google Test Ad Unit IDs
    private const val TEST_BANNER_ID = "ca-app-pub-3940256099942544/6300978111"
    private const val TEST_INTERSTITIAL_ID = "ca-app-pub-3940256099942544/1033173712"
    private const val TEST_REWARDED_ID = "ca-app-pub-3940256099942544/5224354917"
    private const val TEST_APP_OPEN_ID = "ca-app-pub-3940256099942544/9257390722"

    val bannerAdUnitId: String
        get() = if (BuildConfig.DEBUG) TEST_BANNER_ID else PROD_BANNER_ID

    val interstitialAdUnitId: String
        get() = if (BuildConfig.DEBUG) TEST_INTERSTITIAL_ID else PROD_INTERSTITIAL_ID

    val rewardedAdUnitId: String
        get() = if (BuildConfig.DEBUG) TEST_REWARDED_ID else PROD_REWARDED_ID

    val appOpenAdUnitId: String
        get() = if (BuildConfig.DEBUG) TEST_APP_OPEN_ID else PROD_APP_OPEN_ID

    private var interstitialAd: InterstitialAd? = null
    private var isInterstitialLoading = false

    private var rewardedAd: RewardedAd? = null
    private var isRewardedLoading = false

    private var isInitialized = false

    fun initialize(context: Context) {
        if (isInitialized) return
        try {
            MobileAds.initialize(context) { initStatus ->
                Log.d(TAG, "MobileAds initialized: $initStatus")
                isInitialized = true
                preloadInterstitial(context)
            }
        } catch (e: Exception) {
            Log.w(TAG, "AdMob initialization skipped/failed: ${e.message}")
        }
    }

    fun preloadInterstitial(context: Context) {
        if (interstitialAd != null || isInterstitialLoading) return
        isInterstitialLoading = true

        try {
            val adRequest = AdRequest.Builder().build()
            InterstitialAd.load(
                context,
                interstitialAdUnitId,
                adRequest,
                object : InterstitialAdLoadCallback() {
                    override fun onAdLoaded(ad: InterstitialAd) {
                        interstitialAd = ad
                        isInterstitialLoading = false
                        Log.d(TAG, "Interstitial loaded")
                    }

                    override fun onAdFailedToLoad(error: LoadAdError) {
                        interstitialAd = null
                        isInterstitialLoading = false
                        Log.w(TAG, "Interstitial failed to load: ${error.message}")
                    }
                }
            )
        } catch (e: Exception) {
            isInterstitialLoading = false
        }
    }

    fun showInterstitialIfReady(activity: Activity, onDismiss: () -> Unit) {
        val ad = interstitialAd
        if (ad != null) {
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    interstitialAd = null
                    preloadInterstitial(activity)
                    onDismiss()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    interstitialAd = null
                    preloadInterstitial(activity)
                    onDismiss()
                }
            }
            ad.show(activity)
        } else {
            preloadInterstitial(activity)
            onDismiss()
        }
    }

    fun preloadRewarded(context: Context) {
        if (rewardedAd != null || isRewardedLoading) return
        isRewardedLoading = true

        try {
            val adRequest = AdRequest.Builder().build()
            RewardedAd.load(
                context,
                rewardedAdUnitId,
                adRequest,
                object : RewardedAdLoadCallback() {
                    override fun onAdLoaded(ad: RewardedAd) {
                        rewardedAd = ad
                        isRewardedLoading = false
                        Log.d(TAG, "Rewarded ad loaded")
                    }

                    override fun onAdFailedToLoad(error: LoadAdError) {
                        rewardedAd = null
                        isRewardedLoading = false
                        Log.w(TAG, "Rewarded ad failed to load: ${error.message}")
                    }
                }
            )
        } catch (e: Exception) {
            isRewardedLoading = false
        }
    }

    fun showRewardedAd(
        activity: Activity,
        onRewardEarned: () -> Unit,
        onDismissOrFailed: () -> Unit
    ) {
        val ad = rewardedAd
        if (ad != null) {
            var rewardEarned = false
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    rewardedAd = null
                    preloadRewarded(activity)
                    if (rewardEarned) {
                        onRewardEarned()
                    } else {
                        onDismissOrFailed()
                    }
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    rewardedAd = null
                    preloadRewarded(activity)
                    onDismissOrFailed()
                }
            }
            ad.show(activity) { _ ->
                rewardEarned = true
            }
        } else {
            preloadRewarded(activity)
            onDismissOrFailed()
        }
    }
}
