package com.solitaire.hyper.card.games.ads

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

/**
 * Standard AdMob Banner component.
 * Automatically hidden if the player has purchased "Remove Ads".
 */
@Composable
fun BannerAdView(
    modifier: Modifier = Modifier,
    isAdsRemoved: Boolean = false
) {
    if (isAdsRemoved) {
        return
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .testTag("home_banner_ad"),
        contentAlignment = Alignment.Center
    ) {
        AndroidView(
            modifier = Modifier.fillMaxWidth(),
            factory = { context ->
                AdView(context).apply {
                    setAdSize(AdSize.BANNER)
                    adUnitId = AdManager.BANNER_AD_UNIT_ID
                    loadAd(AdRequest.Builder().build())
                }
            }
        )
    }
}
