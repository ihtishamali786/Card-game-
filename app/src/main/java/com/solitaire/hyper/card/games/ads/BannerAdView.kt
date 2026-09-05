package com.solitaire.hyper.card.games.ads

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError

@Composable
fun BannerAdView(
    modifier: Modifier = Modifier
) {
    val containerShape = RoundedCornerShape(6.dp)

    if (LocalInspectionMode.current) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(containerShape)
                .background(Color.White.copy(alpha = 0.05f))
                .border(1.dp, Color.White.copy(alpha = 0.10f), containerShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "SPONSORED ADVERTISEMENT",
                color = Color.White.copy(alpha = 0.30f),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp
            )
        }
        return
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(Color(0xFF0A0F0D)),
        contentAlignment = Alignment.Center
    ) {
        AndroidView(
            modifier = Modifier.fillMaxWidth().height(50.dp),
            factory = { context ->
                AdView(context).apply {
                    setAdSize(AdSize.BANNER)
                    adUnitId = AdManager.bannerAdUnitId
                    var fallbackTriggered = false
                    adListener = object : AdListener() {
                        override fun onAdLoaded() {
                            Log.d("BannerAdView", "Banner ad loaded successfully")
                        }

                        override fun onAdFailedToLoad(error: LoadAdError) {
                            Log.w("BannerAdView", "Banner failed to load: ${error.message}")
                            if (!fallbackTriggered && adUnitId != AdManager.TEST_BANNER_ID) {
                                fallbackTriggered = true
                                Log.d("BannerAdView", "Falling back to test banner ad")
                                post {
                                    try {
                                        adUnitId = AdManager.TEST_BANNER_ID
                                        loadAd(AdRequest.Builder().build())
                                    } catch (e: Exception) {
                                        Log.w("BannerAdView", "Fallback load failed: ${e.message}")
                                    }
                                }
                            }
                        }
                    }
                    try {
                        loadAd(AdRequest.Builder().build())
                    } catch (e: Exception) {
                        // Fail silently
                    }
                }
            },
            onRelease = { adView ->
                try {
                    adView.destroy()
                } catch (e: Exception) {
                    // Ignore release errors
                }
            }
        )
    }
}

