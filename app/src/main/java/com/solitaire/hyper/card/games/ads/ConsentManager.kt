package com.solitaire.hyper.card.games.ads

import android.app.Activity
import android.util.Log
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform

/**
 * Manages GDPR and privacy consent via Google's User Messaging Platform (UMP) SDK.
 */
object ConsentManager {

    private const val TAG = "ConsentManager"

    fun requestConsentAndInitAds(activity: Activity, onConsentCompleted: () -> Unit) {
        val params = ConsentRequestParameters.Builder()
            .setTagForUnderAgeOfConsent(false)
            .build()

        val consentInformation = UserMessagingPlatform.getConsentInformation(activity)

        consentInformation.requestConsentInfoUpdate(
            activity,
            params,
            {
                UserMessagingPlatform.loadAndShowConsentFormIfRequired(activity) { loadAndShowError ->
                    if (loadAndShowError != null) {
                        Log.w(TAG, "Consent form error: ${loadAndShowError.message}")
                    }
                    if (consentInformation.canRequestAds()) {
                        AdManager.initialize(activity.applicationContext)
                    }
                    onConsentCompleted()
                }
            },
            { requestConsentError ->
                Log.w(TAG, "Consent info update error: ${requestConsentError.message}")
                if (consentInformation.canRequestAds()) {
                    AdManager.initialize(activity.applicationContext)
                }
                onConsentCompleted()
            }
        )

        // Check if ads can already be requested immediately
        if (consentInformation.canRequestAds()) {
            AdManager.initialize(activity.applicationContext)
        }
    }

    fun canRequestAds(activity: Activity): Boolean {
        return UserMessagingPlatform.getConsentInformation(activity).canRequestAds()
    }
}
