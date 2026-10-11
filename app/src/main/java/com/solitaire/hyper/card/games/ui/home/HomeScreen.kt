package com.solitaire.hyper.card.games.ui.home

import android.app.Activity
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.draw.shadow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.solitaire.hyper.card.games.ads.BannerAdView
import com.solitaire.hyper.card.games.data.preferences.UserPreferencesRepository
import com.solitaire.hyper.card.games.data.preferences.UserSettings
import com.solitaire.hyper.card.games.ui.components.DailyLoginBonusDialog
import com.solitaire.hyper.card.games.ui.components.RulesDialog
import com.solitaire.hyper.card.games.ui.shop.ShopDialog
import com.solitaire.hyper.card.games.ui.theme.SleekEmerald400
import com.solitaire.hyper.card.games.ui.theme.SleekEmerald500
import com.solitaire.hyper.card.games.ui.theme.SleekGradientBottom
import com.solitaire.hyper.card.games.ui.theme.SleekGradientTop
import com.solitaire.hyper.card.games.ui.theme.SleekHeaderDark
import com.solitaire.hyper.card.games.ui.theme.SleekSlate100
import com.solitaire.hyper.card.games.ui.theme.SleekSlate200
import com.solitaire.hyper.card.games.ui.theme.SleekSlate300
import com.solitaire.hyper.card.games.ui.theme.SleekSlate400
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class HomeGameMode {
    CLASSIC_SOLITAIRE,
    CALL_BREAK
}

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
    onSettings: () -> Unit,
    onPlayFourPlayerTable: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var showRulesDialog by remember { mutableStateOf(false) }
    var showShopDialog by remember { mutableStateOf(false) }
    var showDailyBonusDialog by remember { mutableStateOf(false) }
    var dailyBonusInfo by remember { mutableStateOf<UserPreferencesRepository.DailyBonusInfo?>(null) }

    // Toggle between Classic Solitaire and Call Break
    var selectedGameMode by rememberSaveable { mutableStateOf(HomeGameMode.CLASSIC_SOLITAIRE) }

    // Check Daily Login Bonus on arrival
    val todayDateFormatted = remember { SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date()) }
    LaunchedEffect(userPrefs, todayDateFormatted) {
        if (userPrefs != null) {
            val status = userPrefs.getDailyBonusStatus(todayDateFormatted)
            dailyBonusInfo = status
            if (status.isAvailableToday) {
                showDailyBonusDialog = true
            }
        }
    }

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
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(14.dp))

            // Top Status Bar: Coins, Round "No Ads" Corner Button & VIP Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Coins Pill (opens Shop)
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = SleekHeaderDark,
                    border = BorderStroke(1.dp, Color(0xFFFFD700).copy(alpha = 0.5f)),
                    modifier = Modifier
                        .clickable { showShopDialog = true }
                        .testTag("coins_pill")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🪙", fontSize = 14.sp)
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "${userSettings.coins}",
                            color = Color(0xFFFFD700),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(Modifier.width(4.dp))
                        Text("+", color = SleekEmerald400, fontSize = 14.sp, fontWeight = FontWeight.Black)
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Ad-Free status / Shop trigger
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isAdFreeActive) SleekEmerald500.copy(alpha = 0.25f) else Color(0xFF6B21A8).copy(alpha = 0.35f),
                        border = BorderStroke(1.dp, if (isAdFreeActive) SleekEmerald400 else Color(0xFFA855F7)),
                        modifier = Modifier
                            .clickable {
                                if (userSettings.adFreeTokens > 0) {
                                    scope.launch {
                                        userPrefs?.useAdFreeToken()
                                        Toast.makeText(context, "⚡ Activated 30-min Ad-Free!", Toast.LENGTH_SHORT).show()
                                    }
                                } else if (userSettings.coins >= 1000) {
                                    scope.launch {
                                        val ok = userPrefs?.activateAdFreeOneHour() ?: false
                                        if (ok) {
                                            Toast.makeText(context, "1-Hour Ad-Free Activated!", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                } else {
                                    showShopDialog = true
                                }
                            }
                            .testTag("no_ads_round_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(if (isAdFreeActive) "🛡️" else "🚫", fontSize = 12.sp)
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = if (isAdFreeActive) "${userSettings.getAdFreeRemainingMinutes()}m" else "No Ads",
                                color = if (isAdFreeActive) SleekEmerald400 else Color(0xFFF3E8FF),
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.ExtraBold
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

            Spacer(modifier = Modifier.height(16.dp))

            // Branding Crest & App Title
            Box(
                modifier = Modifier
                    .size(62.dp)
                    .background(SleekEmerald400, CircleShape)
                    .border(3.dp, SleekEmerald500.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Star,
                    contentDescription = "Crown",
                    tint = SleekHeaderDark,
                    modifier = Modifier.size(38.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "SOLITAIRE & CALL BREAK",
                color = SleekEmerald400,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 2.sp
            )
            Text(
                text = "Premier Card Game Collection",
                color = SleekSlate300,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(18.dp))

            // CARD-BASED SELECTION MENU (Toggle between Classic Solitaire & Call Break)
            Text(
                text = "SELECT GAME MODE",
                color = SleekSlate300,
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.2.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 4.dp)
            )

            // TWO DISTINCT HERO GAME CARDS WITH PROMINENT LOGOS ABOVE AND TITLES BELOW
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // 1. Solitaire Card Game Hero Card with Distinct Logo Above & Title Below
                GameHeroSelectionCard(
                    title = "Solitaire",
                    subtitle = "Klondike Solo",
                    badge = "KLONDIKE",
                    isCallBreak = false,
                    isSelected = selectedGameMode == HomeGameMode.CLASSIC_SOLITAIRE,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("mode_toggle_solitaire"),
                    onClick = { selectedGameMode = HomeGameMode.CLASSIC_SOLITAIRE }
                )

                // 2. Call Break Card Game Hero Card with Distinct Logo Above & Title Below
                GameHeroSelectionCard(
                    title = "Call Break",
                    subtitle = "4-Player Table",
                    badge = "4-PLAYERS",
                    isCallBreak = true,
                    isSelected = selectedGameMode == HomeGameMode.CALL_BREAK,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("mode_toggle_call_break"),
                    onClick = { selectedGameMode = HomeGameMode.CALL_BREAK }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // DYNAMIC CONTENT BASED ON SELECTED GAME MODE
            AnimatedContent(
                targetState = selectedGameMode,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "GameModeContent"
            ) { mode ->
                when (mode) {
                    HomeGameMode.CLASSIC_SOLITAIRE -> {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            // Primary Action Buttons for Solitaire
                            if (hasSavedGame) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Button(
                                        onClick = onContinueGame,
                                        modifier = Modifier
                                            .weight(1.3f)
                                            .height(54.dp)
                                            .clip(RoundedCornerShape(14.dp))
                                            .testTag("continue_game_button"),
                                        colors = ButtonDefaults.buttonColors(containerColor = SleekEmerald500),
                                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
                                    ) {
                                        Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                                        Spacer(Modifier.width(6.dp))
                                        Text(
                                            text = "CONTINUE",
                                            color = Color.White,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            letterSpacing = 1.sp
                                        )
                                    }

                                    OutlinedButton(
                                        onClick = onPlayDraw1,
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(54.dp)
                                            .clip(RoundedCornerShape(14.dp))
                                            .testTag("new_deal_button"),
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            containerColor = Color.White.copy(alpha = 0.06f)
                                        ),
                                        border = BorderStroke(1.dp, SleekEmerald400.copy(alpha = 0.5f))
                                    ) {
                                        Text(
                                            text = "NEW DEAL",
                                            color = SleekEmerald400,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            } else {
                                Button(
                                    onClick = onPlayDraw1,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(56.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .testTag("continue_game_button"),
                                    colors = ButtonDefaults.buttonColors(containerColor = SleekEmerald500),
                                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
                                ) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(26.dp))
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        text = "PLAY KLONDIKE",
                                        color = Color.White,
                                        fontSize = 15.5.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        letterSpacing = 1.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Solitaire Mode Quick Selectors (Draw 1 & Draw 3 & Winnable Deal)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .clickable(onClick = onPlayDraw1)
                                        .testTag("deal_draw1_pill"),
                                    color = SleekHeaderDark,
                                    border = BorderStroke(1.dp, SleekEmerald400.copy(alpha = 0.4f)),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text("Draw 1", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        Text("Relaxed", color = SleekSlate300, fontSize = 10.sp)
                                    }
                                }

                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .clickable(onClick = onPlayDraw3)
                                        .testTag("deal_draw3_pill"),
                                    color = SleekHeaderDark,
                                    border = BorderStroke(1.dp, SleekEmerald400.copy(alpha = 0.4f)),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text("Draw 3", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        Text("Mastery", color = SleekSlate300, fontSize = 10.sp)
                                    }
                                }

                                Surface(
                                    modifier = Modifier
                                        .weight(1.2f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .clickable(onClick = onPlayWinnable)
                                        .testTag("deal_winnable_pill"),
                                    color = SleekHeaderDark,
                                    border = BorderStroke(1.dp, Color(0xFFFFD700).copy(alpha = 0.5f)),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text("🌟 Winnable", color = Color(0xFFFFD700), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        Text("100% Solvable", color = SleekSlate300, fontSize = 10.sp)
                                    }
                                }
                            }
                        }
                    }

                    HomeGameMode.CALL_BREAK -> {
                        // Call Break Action & Showcase Card
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(18.dp))
                                .clickable(onClick = onPlayFourPlayerTable)
                                .testTag("call_break_showcase_card"),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF0A2417)),
                            border = BorderStroke(1.8.dp, Color(0xFFFFD700).copy(alpha = 0.8f))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(
                                            shape = CircleShape,
                                            color = Color(0xFFFFD700).copy(alpha = 0.2f),
                                            border = BorderStroke(1.dp, Color(0xFFFFD700)),
                                            modifier = Modifier.size(38.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text("🎴", fontSize = 18.sp)
                                            }
                                        }
                                        Spacer(Modifier.width(10.dp))
                                        Column {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = "CALL BREAK TABLE",
                                                    color = Color(0xFFFFD700),
                                                    fontWeight = FontWeight.Black,
                                                    fontSize = 14.sp,
                                                    letterSpacing = 0.6.sp
                                                )
                                                Spacer(Modifier.width(6.dp))
                                                Surface(
                                                    color = SleekEmerald500,
                                                    shape = RoundedCornerShape(4.dp)
                                                ) {
                                                    Text(
                                                        text = "4-PLAYER",
                                                        color = Color.White,
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.Black,
                                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                                    )
                                                }
                                            }
                                            Text(
                                                text = "You vs 3 World Celebrities • 13 Cards Each",
                                                color = SleekSlate300,
                                                fontSize = 11.5.sp
                                            )
                                        }
                                    }
                                }

                                Spacer(Modifier.height(10.dp))

                                // Feature Badges Row
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    FeatureBadge(text = "🔄 Landscape Table")
                                    FeatureBadge(text = "♠ Trump Spades")
                                    FeatureBadge(text = "🎴 3D Deck Deal")
                                }

                                Spacer(Modifier.height(12.dp))

                                Button(
                                    onClick = onPlayFourPlayerTable,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .testTag("play_call_break_btn"),
                                    colors = ButtonDefaults.buttonColors(containerColor = SleekEmerald500),
                                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
                                ) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                                    Spacer(Modifier.width(6.dp))
                                    Text(
                                        text = "PLAY CALL BREAK NOW",
                                        color = Color.White,
                                        fontSize = 13.5.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        letterSpacing = 0.5.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // VIP Rewards & Store Card (Clean, luxurious layout)
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
                            Text("Ad-Free tokens, VIP crowns & magic wands", color = SleekSlate300, fontSize = 11.sp, maxLines = 1)
                        }
                    }
                    Text("SHOP ›", color = Color(0xFFFFD700), fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Navigation Grid: Themes, Daily Challenge, Statistics, Settings
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                HomeMenuItem(
                    modifier = Modifier.weight(1f),
                    title = "Themes",
                    icon = Icons.Default.Palette,
                    testTag = "nav_themes",
                    onClick = onCustomization
                )
                HomeMenuItem(
                    modifier = Modifier.weight(1f),
                    title = "Daily Deal",
                    icon = Icons.Default.DateRange,
                    testTag = "daily_challenge_card",
                    onClick = onDailyChallenge
                )
                HomeMenuItem(
                    modifier = Modifier.weight(1f),
                    title = "Stats",
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

        // Daily Login Bonus Dialog
        if (showDailyBonusDialog && userPrefs != null) {
            DailyLoginBonusDialog(
                currentStreakDay = dailyBonusInfo?.currentStreakDay ?: 1,
                isAvailableToClaim = dailyBonusInfo?.isAvailableToday ?: true,
                onClaimReward = {
                    scope.launch {
                        val (coins, extraDesc) = userPrefs.claimDailyLoginBonus(todayDateFormatted)
                        dailyBonusInfo = userPrefs.getDailyBonusStatus(todayDateFormatted)
                        val extraMsg = if (extraDesc != null) " + $extraDesc" else ""
                        Toast.makeText(context, "🎉 Claimed +$coins Coins$extraMsg!", Toast.LENGTH_LONG).show()
                    }
                },
                onDismiss = { showDailyBonusDialog = false }
            )
        }
    }
}

/**
 * Custom 3D-styled Solitaire Card Game Logo Emblem.
 * Shows golden ace/king cards fanned with glowing emerald spade crest.
 */
@Composable
fun SolitaireLogoEmblem(
    modifier: Modifier = Modifier,
    isSelected: Boolean = false
) {
    Box(
        modifier = modifier
            .size(68.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(
                Brush.radialGradient(
                    colors = if (isSelected) listOf(
                        Color(0xFF1E824C),
                        Color(0xFF0F4D2A),
                        Color(0xFF072615)
                    ) else listOf(
                        Color(0xFF264332),
                        Color(0xFF162A1F),
                        Color(0xFF0A140F)
                    )
                )
            )
            .border(
                width = if (isSelected) 2.dp else 1.2.dp,
                brush = Brush.linearGradient(
                    listOf(
                        if (isSelected) Color(0xFFFFD700) else Color(0xFF6EE7B7),
                        if (isSelected) Color(0xFFB8860B) else Color(0xFF10B981)
                    )
                ),
                shape = RoundedCornerShape(18.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        // Fanned mini playing card stack
        Box(
            modifier = Modifier.size(46.dp),
            contentAlignment = Alignment.Center
        ) {
            // Background card (King of Hearts silhouette)
            Surface(
                modifier = Modifier
                    .offset(x = (-8).dp, y = (-2).dp)
                    .size(26.dp, 36.dp),
                shape = RoundedCornerShape(4.dp),
                color = Color(0xFFFFFBEB),
                border = BorderStroke(0.8.dp, Color(0xFFDC2626)),
                shadowElevation = 4.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("♥", color = Color(0xFFDC2626), fontSize = 16.sp, fontWeight = FontWeight.Black)
                }
            }

            // Foreground card (Ace of Spades - Gold / Emerald)
            Surface(
                modifier = Modifier
                    .offset(x = 6.dp, y = 2.dp)
                    .size(28.dp, 38.dp),
                shape = RoundedCornerShape(4.dp),
                color = Color(0xFFF8FAFC),
                border = BorderStroke(1.dp, Color(0xFFFFD700)),
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(1.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("♠", color = Color(0xFF0F172A), fontSize = 18.sp, fontWeight = FontWeight.Black)
                    Text("A", color = Color(0xFF0F172A), fontSize = 9.sp, fontWeight = FontWeight.ExtraBold)
                }
            }

            // Golden Solitaire Crown badge on top
            Surface(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 4.dp, y = (-4).dp)
                    .size(16.dp),
                shape = CircleShape,
                color = Color(0xFFFFD700),
                border = BorderStroke(0.6.dp, Color.White),
                shadowElevation = 3.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("👑", fontSize = 9.sp)
                }
            }
        }
    }
}

/**
 * Custom 3D-styled Call Break Card Game Logo Emblem.
 * Shows a 4-Player casino table cross emblem with 4 cards and central golden trump badge.
 */
@Composable
fun CallBreakLogoEmblem(
    modifier: Modifier = Modifier,
    isSelected: Boolean = false
) {
    Box(
        modifier = modifier
            .size(68.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(
                Brush.radialGradient(
                    colors = if (isSelected) listOf(
                        Color(0xFF8B1832),
                        Color(0xFF4A0E1B),
                        Color(0xFF1E040A)
                    ) else listOf(
                        Color(0xFF3F1A24),
                        Color(0xFF240E14),
                        Color(0xFF0F0407)
                    )
                )
            )
            .border(
                width = if (isSelected) 2.dp else 1.2.dp,
                brush = Brush.linearGradient(
                    listOf(
                        if (isSelected) Color(0xFFFFD700) else Color(0xFFF472B6),
                        if (isSelected) Color(0xFFD97706) else Color(0xFFBE185D)
                    )
                ),
                shape = RoundedCornerShape(18.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        // 4-Player Table Cross Card Formation
        Box(
            modifier = Modifier.size(50.dp),
            contentAlignment = Alignment.Center
        ) {
            // North mini card
            Surface(
                modifier = Modifier
                    .offset(y = (-11).dp)
                    .size(14.dp, 20.dp),
                shape = RoundedCornerShape(2.dp),
                color = Color(0xFF1E293B),
                border = BorderStroke(0.5.dp, Color(0xFFFFD700).copy(alpha = 0.6f))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("♠", color = Color(0xFFFFD700), fontSize = 9.sp)
                }
            }

            // West mini card
            Surface(
                modifier = Modifier
                    .offset(x = (-13).dp)
                    .size(14.dp, 20.dp),
                shape = RoundedCornerShape(2.dp),
                color = Color(0xFF1E293B),
                border = BorderStroke(0.5.dp, Color(0xFFEF4444).copy(alpha = 0.6f))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("♥", color = Color(0xFFEF4444), fontSize = 9.sp)
                }
            }

            // East mini card
            Surface(
                modifier = Modifier
                    .offset(x = 13.dp)
                    .size(14.dp, 20.dp),
                shape = RoundedCornerShape(2.dp),
                color = Color(0xFF1E293B),
                border = BorderStroke(0.5.dp, Color(0xFF38BDF8).copy(alpha = 0.6f))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("♦", color = Color(0xFF38BDF8), fontSize = 9.sp)
                }
            }

            // South mini card
            Surface(
                modifier = Modifier
                    .offset(y = 11.dp)
                    .size(14.dp, 20.dp),
                shape = RoundedCornerShape(2.dp),
                color = Color(0xFFFFFBEB),
                border = BorderStroke(0.5.dp, Color(0xFFFFD700)),
                shadowElevation = 4.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("♣", color = Color(0xFF0F172A), fontSize = 9.sp)
                }
            }

            // Central Trump / 4P Trophy Ring
            Surface(
                modifier = Modifier.size(20.dp),
                shape = CircleShape,
                color = Color(0xFFFFD700),
                border = BorderStroke(1.dp, Color(0xFFD97706)),
                shadowElevation = 6.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("4P", color = Color(0xFF1E040A), fontSize = 9.sp, fontWeight = FontWeight.Black)
                }
            }
        }
    }
}

/**
 * Game Hero Selection Card with prominent logo on top, title and badge below.
 * Gives instant visual clarity so users immediately know which game they are choosing.
 */
@Composable
fun GameHeroSelectionCard(
    title: String,
    subtitle: String,
    badge: String,
    isCallBreak: Boolean,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) {
        if (isCallBreak) Color(0xFFFFD700) else SleekEmerald400
    } else {
        Color.White.copy(alpha = 0.12f)
    }
    val borderWidth = if (isSelected) 2.dp else 1.dp
    val bgColor = if (isSelected) {
        if (isCallBreak) Color(0xFF200911) else Color(0xFF0A291A)
    } else {
        SleekHeaderDark
    }

    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick),
        color = bgColor,
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(borderWidth, borderColor),
        shadowElevation = if (isSelected) 8.dp else 2.dp
    ) {
        Column(
            modifier = Modifier.padding(vertical = 14.dp, horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Indicator Pill + Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    color = if (isSelected) {
                        if (isCallBreak) Color(0xFFFFD700).copy(alpha = 0.25f) else SleekEmerald500.copy(alpha = 0.3f)
                    } else Color.White.copy(alpha = 0.06f),
                    shape = RoundedCornerShape(6.dp),
                    border = BorderStroke(
                        0.6.dp,
                        if (isSelected) (if (isCallBreak) Color(0xFFFFD700) else SleekEmerald400) else Color.White.copy(alpha = 0.1f)
                    )
                ) {
                    Text(
                        text = badge,
                        color = if (isSelected) (if (isCallBreak) Color(0xFFFFD700) else SleekEmerald400) else SleekSlate300,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                // Radio Check Circle
                Surface(
                    shape = CircleShape,
                    color = if (isSelected) (if (isCallBreak) Color(0xFFFFD700) else SleekEmerald400) else Color.Transparent,
                    border = BorderStroke(1.5.dp, if (isSelected) (if (isCallBreak) Color(0xFFFFD700) else SleekEmerald400) else SleekSlate400),
                    modifier = Modifier.size(16.dp)
                ) {
                    if (isSelected) {
                        Box(contentAlignment = Alignment.Center) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(Color(0xFF04120C), CircleShape)
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            // PROMINENT GAME LOGO ON TOP
            if (isCallBreak) {
                CallBreakLogoEmblem(isSelected = isSelected)
            } else {
                SolitaireLogoEmblem(isSelected = isSelected)
            }

            Spacer(Modifier.height(10.dp))

            // GAME TITLE BELOW THE LOGO
            Text(
                text = title,
                color = if (isSelected) Color.White else SleekSlate100,
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.5.sp
            )

            Text(
                text = subtitle,
                color = if (isSelected) (if (isCallBreak) Color(0xFFFFD700).copy(alpha = 0.9f) else SleekEmerald400) else SleekSlate300,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun FeatureBadge(text: String) {
    Surface(
        color = Color.White.copy(alpha = 0.07f),
        shape = RoundedCornerShape(6.dp),
        border = BorderStroke(0.6.dp, Color.White.copy(alpha = 0.12f))
    ) {
        Text(
            text = text,
            color = SleekSlate200,
            fontSize = 9.5.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
        )
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
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = title, tint = SleekEmerald400, modifier = Modifier.size(22.dp))
            Spacer(Modifier.height(4.dp))
            Text(title, color = SleekSlate100, fontSize = 11.5.sp, fontWeight = FontWeight.Medium)
        }
    }
}
