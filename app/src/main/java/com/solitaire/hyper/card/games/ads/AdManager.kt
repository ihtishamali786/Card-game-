package com.solitaire.hyper.card.games.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback
import com.solitaire.hyper.card.games.BuildConfig

/**
 * Manages Google AdMob ad units with policy-compliant, fail-safe lifecycles.
 * Handles Banner, Interstitial, Rewarded Video, and App Open ads.
 * Fully respects Ad-Free passes to suppress non-rewarded ads.
 */
object AdManager {
    private const val TAG = "AdManager"

    // Production Unit IDs provided by the owner
    private const val PROD_BANNER_ID = "ca-app-pub-2835586363285222/9008521623"
    private const val PROD_INTERSTITIAL_ID = "ca-app-pub-2835586363285222/6527547141"
    private const val PROD_REWARDED_ID = "ca-app-pub-2835586363285222/8802943428"
    private const val PROD_REWARDED_INTERSTITIAL_ID = "ca-app-pub-2835586363285222/8802943428"
    private const val PROD_APP_OPEN_ID = "ca-app-pub-2835586363285222/4092955494"

    // Official Google Test Ad Unit IDs
    const val TEST_BANNER_ID = "ca-app-pub-3940256099942544/6300978111"
    const val TEST_INTERSTITIAL_ID = "ca-app-pub-3940256099942544/1033173712"
    const val TEST_REWARDED_ID = "ca-app-pub-3940256099942544/5224354917"
    const val TEST_REWARDED_INTERSTITIAL_ID = "ca-app-pub-3940256099942544/5354046379"
    const val TEST_APP_OPEN_ID = "ca-app-pub-3940256099942544/9257390722"

    val bannerAdUnitId: String
        get() = if (BuildConfig.DEBUG) TEST_BANNER_ID else PROD_BANNER_ID

    val interstitialAdUnitId: String
        get() = if (BuildConfig.DEBUG) TEST_INTERSTITIAL_ID else PROD_INTERSTITIAL_ID

    val rewardedAdUnitId: String
        get() = if (BuildConfig.DEBUG) TEST_REWARDED_ID else PROD_REWARDED_ID

    val rewardedInterstitialAdUnitId: String
        get() = if (BuildConfig.DEBUG) TEST_REWARDED_INTERSTITIAL_ID else PROD_REWARDED_INTERSTITIAL_ID

    val appOpenAdUnitId: String
        get() = if (BuildConfig.DEBUG) TEST_APP_OPEN_ID else PROD_APP_OPEN_ID

    // Interstitial Ad State
    private var interstitialAd: InterstitialAd? = null
    private var isInterstitialLoading = false
    private var interstitialFallbackAttempted = false

    // Rewarded Ad State
    private var rewardedAd: RewardedAd? = null
    private var isRewardedLoading = false
    private var rewardedFallbackAttempted = false

    // Rewarded Interstitial Ad State
    private var rewardedInterstitialAd: RewardedInterstitialAd? = null
    private var isRewardedInterstitialLoading = false
    private var rewardedInterstitialFallbackAttempted = false

    // App Open Ad State
    private var appOpenAd: AppOpenAd? = null
    private var isAppOpenLoading = false
    private var appOpenLoadTime: Long = 0L
    private var isShowingAppOpenAd = false

    private var isInitialized = false

    fun initialize(context: Context) {
        if (isInitialized) return
        try {
            MobileAds.initialize(context) { initStatus ->
                Log.d(TAG, "MobileAds initialized: $initStatus")
                isInitialized = true
                preloadInterstitial(context)
                preloadRewarded(context)
                preloadAppOpen(context)
            }
        } catch (e: Exception) {
            Log.w(TAG, "AdMob initialization skipped/failed: ${e.message}")
        }
    }

    // ==========================================
    // APP OPEN ADS
    // ==========================================

    fun preloadAppOpen(context: Context) {
        if (isAppOpenAvailable() || isAppOpenLoading) return
        isAppOpenLoading = true

        try {
            val adRequest = AdRequest.Builder().build()
            AppOpenAd.load(
                context,
                appOpenAdUnitId,
                adRequest,
                object : AppOpenAd.AppOpenAdLoadCallback() {
                    override fun onAdLoaded(ad: AppOpenAd) {
                        appOpenAd = ad
                        appOpenLoadTime = System.currentTimeMillis()
                        isAppOpenLoading = false
                        Log.d(TAG, "AppOpenAd loaded successfully")
                    }

                    override fun onAdFailedToLoad(error: LoadAdError) {
                        appOpenAd = null
                        isAppOpenLoading = false
                        Log.w(TAG, "AppOpenAd failed to load: ${error.message}")
                        if (appOpenAdUnitId != TEST_APP_OPEN_ID) {
                            val testRequest = AdRequest.Builder().build()
                            AppOpenAd.load(
                                context,
                                TEST_APP_OPEN_ID,
                                testRequest,
                                object : AppOpenAd.AppOpenAdLoadCallback() {
                                    override fun onAdLoaded(testAd: AppOpenAd) {
                                        appOpenAd = testAd
                                        appOpenLoadTime = System.currentTimeMillis()
                                        Log.d(TAG, "Fallback test AppOpenAd loaded")
                                    }
                                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                                        Log.w(TAG, "Fallback AppOpenAd failed: ${loadAdError.message}")
                                    }
                                }
                            )
                        }
                    }
                }
            )
        } catch (e: Exception) {
            isAppOpenLoading = false
        }
    }

    fun isAppOpenAvailable(): Boolean {
        return appOpenAd != null && (System.currentTimeMillis() - appOpenLoadTime) < 4 * 3600 * 1000L
    }

    fun showAppOpenAdIfReady(activity: Activity, isAdFree: Boolean, onDismiss: () -> Unit = {}) {
        if (isAdFree || isShowingAppOpenAd) {
            onDismiss()
            return
        }

        val ad = appOpenAd
        if (ad != null && isAppOpenAvailable()) {
            isShowingAppOpenAd = true
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    appOpenAd = null
                    isShowingAppOpenAd = false
                    preloadAppOpen(activity)
                    onDismiss()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    appOpenAd = null
                    isShowingAppOpenAd = false
                    preloadAppOpen(activity)
                    onDismiss()
                }
            }
            ad.show(activity)
        } else {
            preloadAppOpen(activity)
            onDismiss()
        }
    }

    // ==========================================
    // INTERSTITIAL ADS
    // ==========================================

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
                        if (!interstitialFallbackAttempted && interstitialAdUnitId != TEST_INTERSTITIAL_ID) {
                            interstitialFallbackAttempted = true
                            val testRequest = AdRequest.Builder().build()
                            InterstitialAd.load(
                                context,
                                TEST_INTERSTITIAL_ID,
                                testRequest,
                                object : InterstitialAdLoadCallback() {
                                    override fun onAdLoaded(ad: InterstitialAd) {
                                        interstitialAd = ad
                                        Log.d(TAG, "Fallback test interstitial loaded")
                                    }
                                    override fun onAdFailedToLoad(err: LoadAdError) {
                                        Log.w(TAG, "Fallback test interstitial failed: ${err.message}")
                                    }
                                }
                            )
                        }
                    }
                }
            )
        } catch (e: Exception) {
            isInterstitialLoading = false
        }
    }

    fun showInterstitialIfReady(activity: Activity, isAdFree: Boolean = false, onDismiss: () -> Unit) {
        if (isAdFree) {
            onDismiss()
            return
        }

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

    // ==========================================
    // REWARDED VIDEO & REWARDED INTERSTITIAL ADS
    // ==========================================

    fun preloadRewardedInterstitial(context: Context) {
        if (rewardedInterstitialAd != null || isRewardedInterstitialLoading) return
        isRewardedInterstitialLoading = true

        try {
            val adRequest = AdRequest.Builder().build()
            RewardedInterstitialAd.load(
                context,
                rewardedInterstitialAdUnitId,
                adRequest,
                object : RewardedInterstitialAdLoadCallback() {
                    override fun onAdLoaded(ad: RewardedInterstitialAd) {
                        rewardedInterstitialAd = ad
                        isRewardedInterstitialLoading = false
                        Log.d(TAG, "Rewarded interstitial ad loaded")
                    }

                    override fun onAdFailedToLoad(error: LoadAdError) {
                        rewardedInterstitialAd = null
                        isRewardedInterstitialLoading = false
                        Log.w(TAG, "Rewarded interstitial failed to load: ${error.message}")
                        if (!rewardedInterstitialFallbackAttempted && rewardedInterstitialAdUnitId != TEST_REWARDED_INTERSTITIAL_ID) {
                            rewardedInterstitialFallbackAttempted = true
                            val testRequest = AdRequest.Builder().build()
                            RewardedInterstitialAd.load(
                                context,
                                TEST_REWARDED_INTERSTITIAL_ID,
                                testRequest,
                                object : RewardedInterstitialAdLoadCallback() {
                                    override fun onAdLoaded(ad: RewardedInterstitialAd) {
                                        rewardedInterstitialAd = ad
                                        Log.d(TAG, "Fallback test rewarded interstitial loaded")
                                    }
                                    override fun onAdFailedToLoad(err: LoadAdError) {
                                        Log.w(TAG, "Fallback test rewarded interstitial failed: ${err.message}")
                                    }
                                }
                            )
                        }
                    }
                }
            )
        } catch (e: Exception) {
            isRewardedInterstitialLoading = false
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
                        if (!rewardedFallbackAttempted && rewardedAdUnitId != TEST_REWARDED_ID) {
                            rewardedFallbackAttempted = true
                            val testRequest = AdRequest.Builder().build()
                            RewardedAd.load(
                                context,
                                TEST_REWARDED_ID,
                                testRequest,
                                object : RewardedAdLoadCallback() {
                                    override fun onAdLoaded(ad: RewardedAd) {
                                        rewardedAd = ad
                                        Log.d(TAG, "Fallback test rewarded ad loaded")
                                    }
                                    override fun onAdFailedToLoad(err: LoadAdError) {
                                        Log.w(TAG, "Fallback test rewarded ad failed: ${err.message}")
                                    }
                                }
                            )
                        }
                    }
                }
            )
        } catch (e: Exception) {
            isRewardedLoading = false
        }
    }

    fun isRewardedAdReady(): Boolean = rewardedInterstitialAd != null || rewardedAd != null

    fun showRewardedAd(
        activity: Activity,
        onRewardEarned: () -> Unit,
        onDismissOrFailed: () -> Unit
    ) {
        val rInterAd = rewardedInterstitialAd
        val rAd = rewardedAd

        if (rInterAd != null) {
            var rewardEarned = false
            rInterAd.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    rewardedInterstitialAd = null
                    preloadRewardedInterstitial(activity)
                    preloadRewarded(activity)
                    if (rewardEarned) {
                        onRewardEarned()
                    } else {
                        onDismissOrFailed()
                    }
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    rewardedInterstitialAd = null
                    preloadRewardedInterstitial(activity)
                    preloadRewarded(activity)
                    android.widget.Toast.makeText(activity, "Failed to display ad. Please check network/DNS.", android.widget.Toast.LENGTH_SHORT).show()
                    onDismissOrFailed()
                }
            }
            rInterAd.show(activity) { _ ->
                rewardEarned = true
            }
        } else if (rAd != null) {
            var rewardEarned = false
            rAd.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    rewardedAd = null
                    preloadRewarded(activity)
                    preloadRewardedInterstitial(activity)
                    if (rewardEarned) {
                        onRewardEarned()
                    } else {
                        onDismissOrFailed()
                    }
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    rewardedAd = null
                    preloadRewarded(activity)
                    preloadRewardedInterstitial(activity)
                    android.widget.Toast.makeText(activity, "Failed to display ad. Please check network/DNS.", android.widget.Toast.LENGTH_SHORT).show()
                    onDismissOrFailed()
                }
            }
            rAd.show(activity) { _ ->
                rewardEarned = true
            }
        } else {
            preloadRewardedInterstitial(activity)
            preloadRewarded(activity)
            android.widget.Toast.makeText(
                activity,
                "Video ad loading or blocked by DNS/AdBlocker. Please verify connection and retry.",
                android.widget.Toast.LENGTH_SHORT
            ).show()
            onDismissOrFailed()
        }
    }
}
