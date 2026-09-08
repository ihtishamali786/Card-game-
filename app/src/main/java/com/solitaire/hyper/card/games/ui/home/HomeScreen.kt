package com.solitaire.hyper.card.games.ui.home

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import com.solitaire.hyper.card.games.ui.shop.ShopDialog
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solitaire.hyper.card.games.ads.AdManager
import com.solitaire.hyper.card.games.ads.BannerAdView
import com.solitaire.hyper.card.games.data.preferences.UserPreferencesRepository
import com.solitaire.hyper.card.games.data.preferences.UserSettings
import com.solitaire.hyper.card.games.ui.components.RulesDialog
import com.solitaire.hyper.card.games.ui.theme.SleekBorderSubtle
import com.solitaire.hyper.card.games.ui.theme.SleekEmerald400
import com.solitaire.hyper.card.games.ui.theme.SleekEmerald500
import com.solitaire.hyper.card.games.ui.theme.SleekGradientBottom
import com.solitaire.hyper.card.games.ui.theme.SleekGradientTop
import com.solitaire.hyper.card.games.ui.theme.SleekHeaderDark
import com.solitaire.hyper.card.games.ui.theme.SleekSlate100
import com.solitaire.hyper.card.games.ui.theme.SleekSlate300
import com.solitaire.hyper.card.games.ui.theme.SleekSlate400
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    hasSavedGame: Boolean,
    userSettings: UserSettings = UserSettings(),
    userPrefs: UserPreferencesRepository? = null,
    onContinueGame: () -> Unit,
    onPlayDraw1: () -> Unit = onContinueGame,
    onPlayDraw3: () -> Unit = onContinueGame,
    onPlayWinnable: () -> Unit = onContinueGame,
    onDailyChallenge: () -> Unit,
    onStatistics: () -> Unit,
    onCustomization: () -> Unit,
    onAchievements: () -> Unit,
    onSettings: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val scope = rememberCoroutineScope()
    val currentDateStr = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()).format(Date())
    var showRulesDialog by remember { mutableStateOf(false) }
    var showShopDialog by remember { mutableStateOf(false) }

    val isAdFreeActive = userSettings.isAdFreeActive()
    val isVipActive = userSettings.isVipActive()

    if (showRulesDialog) {
        RulesDialog(onDismiss = { showRulesDialog = false })
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        SleekGradientTop,
                        SleekGradientBottom
                    )
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Top Status Bar: Coins & Active VIP Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Coins Pill (opens Shop)
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = SleekHeaderDark,
                    border = BorderStroke(1.dp, Color(0xFFFFD700).copy(alpha = 0.5f)),
                    modifier = Modifier.clickable {
                        showShopDialog = true
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🪙", fontSize = 15.sp)
                        Spacer(Modifier.width(5.dp))
                        Text(
                            text = "${userSettings.coins}",
                            color = Color(0xFFFFD700),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 13.5.sp
                        )
                        Spacer(Modifier.width(6.dp))
                        Text("+", color = SleekEmerald400, fontWeight = FontWeight.Black, fontSize = 13.5.sp)
                    }
                }

                // Ad-Free Tokens & VIP status badges
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Ad-Free Tokens Quick Pill
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFF0F291E),
                        border = BorderStroke(1.dp, SleekEmerald400.copy(alpha = 0.5f)),
                        modifier = Modifier.clickable {
                            if (userSettings.adFreeTokens > 0) {
                                scope.launch {
                                    userPrefs?.useAdFreeToken()
                                    Toast.makeText(context, "⚡ Activated 30-min Ad-Free!", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                showShopDialog = true
                            }
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("⚡", fontSize = 12.sp)
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = "${userSettings.adFreeTokens} Token${if (userSettings.adFreeTokens != 1) "s" else ""}",
                                color = SleekEmerald400,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    if (isAdFreeActive) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF143026),
                            border = BorderStroke(1.dp, SleekEmerald400)
                        ) {
                            Text(
                                text = "🛡️ ${userSettings.getAdFreeRemainingMinutes()}m",
                                color = SleekEmerald400,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp)
                            )
                        }
                    }

                    if (isVipActive) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF3B2A06),
                            border = BorderStroke(1.dp, Color(0xFFFFD700))
                        ) {
                            Text(
                                text = "👑 VIP",
                                color = Color(0xFFFFD700),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Branding Crest & App Title
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .background(SleekEmerald400, CircleShape)
                    .border(3.dp, SleekEmerald500.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Star,
                    contentDescription = "Crown",
                    tint = SleekHeaderDark,
                    modifier = Modifier.size(42.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "SOLITAIRE",
                color = SleekEmerald400,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 4.sp
            )
            Text(
                text = "Classic Klondike Card Game",
                color = SleekSlate300,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ONLY ONE MAIN ACTION BUTTON: CONTINUE GAME (Strictly as user requested)
            Button(
                onClick = onContinueGame,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .testTag("continue_game_button"),
                colors = ButtonDefaults.buttonColors(containerColor = SleekEmerald500),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
                Spacer(Modifier.width(10.dp))
                Text(
                    text = if (hasSavedGame) "CONTINUE GAME" else "CONTINUE GAME",
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 1-Hour Ad-Free & VIP Rewards Hub
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SleekHeaderDark),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🛡️", fontSize = 16.sp)
                            Spacer(Modifier.width(8.dp))
                            Column {
                                Text("Remove Ads for 1 Hour", color = SleekSlate100, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Text("Cost: 1,000 Coins", color = SleekSlate400, fontSize = 11.sp)
                            }
                        }

                        Button(
                            onClick = {
                                if (userPrefs != null) {
                                    scope.launch {
                                        if (userSettings.coins >= 1000) {
                                            val ok = userPrefs.activateAdFreeOneHour()
                                            if (ok) {
                                                Toast.makeText(context, "1-Hour Ad-Free Activated!", Toast.LENGTH_SHORT).show()
                                            }
                                        } else {
                                            Toast.makeText(context, "Need 1,000 Coins! Watch videos to earn coins.", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isAdFreeActive) Color(0xFF143026) else Color(0xFF2A1C40)
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Text(
                                if (isAdFreeActive) "Active" else "Pay 1000🪙",
                                color = if (isAdFreeActive) SleekEmerald400 else Color(0xFFCE93D8),
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(Modifier.height(10.dp))

                    // 5 Video Ads for VIP Pass
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("👑 1-Hour 3D VIP Pass", color = Color(0xFFFFD700), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text(
                                if (isVipActive) "All 3D themes unlocked (${userSettings.getVipRemainingMinutes()}m left)"
                                else "Watch 5 video ads to unlock (${userSettings.rewardedAdsWatchedForVip}/5 watched)",
                                color = SleekSlate300,
                                fontSize = 11.sp
                            )
                            Spacer(Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { if (isVipActive) 1f else (userSettings.rewardedAdsWatchedForVip / 5f) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp)),
                                color = Color(0xFFFFD700),
                                trackColor = Color.White.copy(alpha = 0.1f)
                            )
                        }

                        Spacer(Modifier.width(10.dp))

                        if (!isVipActive) {
                            Button(
                                onClick = {
                                    if (activity != null && userPrefs != null) {
                                        AdManager.showRewardedAd(
                                            activity = activity,
                                            onRewardEarned = {
                                                scope.launch {
                                                    val vipUnlocked = userPrefs.recordRewardedAdForVip()
                                                    if (vipUnlocked) {
                                                        Toast.makeText(context, "🎉 1-Hour 3D VIP Pass Unlocked!", Toast.LENGTH_LONG).show()
                                                    } else {
                                                        Toast.makeText(context, "Watched ad towards VIP pass!", Toast.LENGTH_SHORT).show()
                                                    }
                                                }
                                            },
                                            onDismissOrFailed = {
                                                scope.launch {
                                                    val vipUnlocked = userPrefs.recordRewardedAdForVip()
                                                    if (vipUnlocked) {
                                                        Toast.makeText(context, "🎉 1-Hour 3D VIP Pass Unlocked!", Toast.LENGTH_LONG).show()
                                                    }
                                                }
                                            }
                                        )
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5C4708)),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.height(34.dp)
                            ) {
                                Text("Watch Ad", color = Color(0xFFFFD700), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Store & VIP Featured Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .clickable(onClick = { showShopDialog = true })
                    .testTag("store_vip_card"),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF231908)),
                border = BorderStroke(1.2.dp, Color(0xFFFFD700).copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .background(Color(0xFFFFD700), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("👑", fontSize = 20.sp)
                        }
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Store & VIP Pass", color = Color(0xFFFFD700), fontWeight = FontWeight.Bold, fontSize = 14.5.sp)
                                Spacer(Modifier.width(6.dp))
                                Surface(
                                    color = Color(0xFFFFD700).copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text("HOT", color = Color(0xFFFFD700), fontSize = 9.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                                }
                            }
                            Spacer(Modifier.height(2.dp))
                            Text("Ad-Free tokens, VIP crowns, magic wands & AI tokens", color = SleekSlate300, fontSize = 11.sp, maxLines = 1)
                        }
                    }
                    Text("SHOP ›", color = Color(0xFFFFD700), fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Daily Challenge Featured Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .clickable(onClick = onDailyChallenge)
                    .testTag("daily_challenge_card"),
                colors = CardDefaults.cardColors(containerColor = SleekHeaderDark),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .background(SleekEmerald400, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.DateRange, contentDescription = null, tint = SleekHeaderDark)
                        }
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text("Daily Challenge", color = SleekSlate100, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(currentDateStr, color = SleekSlate400, fontSize = 12.sp)
                        }
                    }
                    Text("PLAY ›", color = SleekEmerald400, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // How to Play Quick Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .clickable(onClick = { showRulesDialog = true })
                    .testTag("rules_card"),
                colors = CardDefaults.cardColors(containerColor = SleekHeaderDark),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color(0xFF3B82F6).copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.MenuBook, contentDescription = null, tint = Color(0xFF60A5FA), modifier = Modifier.size(20.dp))
                        }
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text("Rules & How to Play", color = SleekSlate100, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Moves, gestures, scoring & boosters", color = SleekSlate400, fontSize = 11.sp)
                        }
                    }
                    Text("VIEW ›", color = Color(0xFF60A5FA), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Navigation Grid: Themes, Statistics, Settings
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                HomeMenuItem(
                    modifier = Modifier.weight(1f),
                    title = "3D Themes",
                    icon = Icons.Default.Palette,
                    testTag = "nav_themes",
                    onClick = onCustomization
                )
                HomeMenuItem(
                    modifier = Modifier.weight(1f),
                    title = "Statistics",
                    icon = Icons.Default.BarChart,
                    testTag = "nav_statistics",
                    onClick = onStatistics
                )
                HomeMenuItem(
                    modifier = Modifier.weight(1f),
                    title = "Settings",
                    icon = Icons.Default.Settings,
                    testTag = "nav_settings",
                    onClick = onSettings
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Non-intrusive Ad Banner (Hidden if 1-Hour Ad-Free is active)
            BannerAdView(isAdFree = isAdFreeActive)

            Spacer(modifier = Modifier.height(16.dp))
        }

        // In-App Store & VIP Pass Dialog
        if (showShopDialog && userPrefs != null) {
            ShopDialog(
                userSettings = userSettings,
                userPrefs = userPrefs,
                onDismiss = { showShopDialog = false }
            )
        }
    }
}

@Composable
fun HomeMenuItem(
    modifier: Modifier = Modifier,
    title: String,
    icon: ImageVector,
    testTag: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .testTag(testTag),
        color = SleekHeaderDark,
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 14.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = title, tint = SleekEmerald400, modifier = Modifier.size(24.dp))
            Spacer(Modifier.height(6.dp))
            Text(title, color = SleekSlate100, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        }
    }
}
