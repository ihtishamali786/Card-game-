package com.solitaire.hyper.card.games.ui.home

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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solitaire.hyper.card.games.ads.BannerAdView
import com.solitaire.hyper.card.games.data.preferences.UserPreferencesRepository
import com.solitaire.hyper.card.games.data.preferences.UserSettings
import com.solitaire.hyper.card.games.ui.components.QuickTutorialDialog
import com.solitaire.hyper.card.games.ui.components.ZenFloatingLights
import com.solitaire.hyper.card.games.ui.customization.CustomizationRegistry
import com.solitaire.hyper.card.games.ui.theme.SleekEmerald400
import com.solitaire.hyper.card.games.ui.theme.SleekEmerald500
import com.solitaire.hyper.card.games.ui.theme.SleekHeaderDark
import com.solitaire.hyper.card.games.ui.theme.SleekSlate100
import com.solitaire.hyper.card.games.ui.theme.SleekSlate300
import com.solitaire.hyper.card.games.ui.theme.SleekSlate400
import kotlinx.coroutines.launch

/**
 * Zen Home Screen for Solitaire Joker: Klondike Zen.
 * Focused strictly on: Play/Continue, Daily Challenge, Statistics, Themes, Settings.
 */
@Composable
fun HomeScreen(
    hasSavedGame: Boolean,
    userSettings: UserSettings = UserSettings(),
    userPrefs: UserPreferencesRepository? = null,
    onContinueGame: () -> Unit,
    onPlayDraw1: () -> Unit = onContinueGame,
    onPlayDraw3: () -> Unit = onContinueGame,
    onDailyChallenge: () -> Unit = {},
    onStatistics: () -> Unit = {},
    onCustomization: () -> Unit = {},
    onSettings: () -> Unit = {}
) {
    val coroutineScope = rememberCoroutineScope()
    var showTutorial by remember { mutableStateOf(!userSettings.hasSeenTutorial) }

    val bgTheme = remember(userSettings.backgroundId) {
        CustomizationRegistry.getBackground(userSettings.backgroundId)
    }

    if (showTutorial) {
        QuickTutorialDialog(
            onDismiss = {
                showTutorial = false
                userPrefs?.let { prefs ->
                    coroutineScope.launch { prefs.setTutorialSeen() }
                }
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgTheme.brush)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Soft floating ambient lights (disabled when Reduce Motion is ON)
        ZenFloatingLights(
            enabled = userSettings.floatingLights,
            reduceMotion = userSettings.reduceMotion
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar: Settings Icon
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 4.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onSettings,
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color.Black.copy(alpha = 0.25f), CircleShape)
                        .border(1.dp, Color.White.copy(alpha = 0.15f), CircleShape)
                        .testTag("settings_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = SleekSlate100,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            // Scrollable Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(14.dp))

                // App Brand Badge & Public Name
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = SleekEmerald400.copy(alpha = 0.15f),
                    border = BorderStroke(1.2.dp, SleekEmerald400.copy(alpha = 0.4f)),
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(text = "🃏", fontSize = 16.sp)
                        Text(
                            text = "KLONDIKE SOLITAIRE",
                            color = SleekEmerald400,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.2.sp
                        )
                    }
                }

                Text(
                    text = "Solitaire Joker",
                    color = SleekSlate100,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Relax. Play. Win with the Joker.",
                    color = Color(0xFFFBBF24),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
                )

                // Main Play / Continue Action Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(12.dp, RoundedCornerShape(20.dp))
                        .testTag("play_game_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = SleekHeaderDark),
                    border = BorderStroke(1.5.dp, SleekEmerald400.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (hasSavedGame) {
                            Button(
                                onClick = onContinueGame,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(54.dp)
                                    .testTag("continue_game_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = SleekEmerald500),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = null,
                                        tint = Color.Black,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        text = "Continue Game",
                                        color = Color.Black,
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            }
                            Spacer(Modifier.height(14.dp))
                        }

                        // Play Draw 1 & Draw 3 Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = onPlayDraw1,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("play_draw_1_button"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (!hasSavedGame) SleekEmerald500 else Color(0xFF0F2D24)
                                ),
                                border = if (hasSavedGame) BorderStroke(1.dp, SleekEmerald400) else null,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = "Draw 1",
                                    color = if (!hasSavedGame) Color.Black else SleekEmerald400,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Button(
                                onClick = onPlayDraw3,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("play_draw_3_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F2D24)),
                                border = BorderStroke(1.dp, SleekEmerald400.copy(alpha = 0.7f)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = "Draw 3",
                                    color = SleekSlate100,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))

                // Core Navigation Buttons (Daily Challenge, Statistics, Themes)
                HomeNavRow(
                    title = "Daily Challenge",
                    subtitle = "Fresh daily puzzle with calendar trophies",
                    icon = Icons.Default.DateRange,
                    iconTint = Color(0xFFFBBF24),
                    testTag = "nav_daily_challenge",
                    onClick = onDailyChallenge
                )

                Spacer(Modifier.height(12.dp))

                HomeNavRow(
                    title = "Statistics",
                    subtitle = "Win rate, streaks & 30-day momentum",
                    icon = Icons.Default.BarChart,
                    iconTint = Color(0xFF38BDF8),
                    testTag = "nav_statistics",
                    onClick = onStatistics
                )

                Spacer(Modifier.height(12.dp))

                HomeNavRow(
                    title = "Themes & Cards",
                    subtitle = "8 Zen tables, 8 backs, Jumbo faces",
                    icon = Icons.Default.Palette,
                    iconTint = Color(0xFFA855F7),
                    testTag = "nav_themes",
                    onClick = onCustomization
                )

                Spacer(Modifier.height(20.dp))
            }

            // Small bottom Banner Ad (AdMob - automatically hidden if user bought Remove Ads)
            BannerAdView(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp),
                isAdsRemoved = userSettings.isAdsRemoved
            )
        }
    }
}

@Composable
private fun HomeNavRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconTint: Color,
    testTag: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag(testTag),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SleekHeaderDark.copy(alpha = 0.95f)),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.10f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(iconTint.copy(alpha = 0.15f), CircleShape)
                    .border(1.dp, iconTint.copy(alpha = 0.35f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = SleekSlate100,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    color = SleekSlate400,
                    fontSize = 12.sp
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = SleekSlate400,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
