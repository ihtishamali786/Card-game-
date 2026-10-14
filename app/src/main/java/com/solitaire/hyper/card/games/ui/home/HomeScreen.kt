package com.solitaire.hyper.card.games.ui.home

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solitaire.hyper.card.games.data.preferences.UserPreferencesRepository
import com.solitaire.hyper.card.games.data.preferences.UserSettings
import com.solitaire.hyper.card.games.ui.theme.SleekEmerald400
import com.solitaire.hyper.card.games.ui.theme.SleekEmerald500
import com.solitaire.hyper.card.games.ui.theme.SleekGradientBottom
import com.solitaire.hyper.card.games.ui.theme.SleekGradientTop
import com.solitaire.hyper.card.games.ui.theme.SleekHeaderDark
import com.solitaire.hyper.card.games.ui.theme.SleekSlate100
import com.solitaire.hyper.card.games.ui.theme.SleekSlate200
import com.solitaire.hyper.card.games.ui.theme.SleekSlate300
import com.solitaire.hyper.card.games.ui.theme.SleekSlate400

/**
 * Clean, premium Home Screen dedicated 100% to Solitaire (Classic Klondike).
 * Features direct single-tap play, mode selectors (Draw 1, Draw 3, Winnable Deals, Daily Challenge),
 * and quick access to global Settings.
 */
@Composable
fun HomeScreen(
    hasSavedGame: Boolean,
    userSettings: UserSettings = UserSettings(),
    userPrefs: UserPreferencesRepository? = null,
    onContinueGame: () -> Unit,
    onPlayDraw1: () -> Unit = onContinueGame,
    onPlayDraw3: () -> Unit = onContinueGame,
    onPlayWinnable: () -> Unit = onContinueGame,
    onDailyChallenge: () -> Unit = {},
    onStatistics: () -> Unit = {},
    onCustomization: () -> Unit = {},
    onAchievements: () -> Unit = {},
    onBioscienceSkills: () -> Unit = {},
    onSettings: () -> Unit = {}
) {
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
            // Top Bar: Discreet Settings Menu button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 4.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onSettings,
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.White.copy(alpha = 0.08f), CircleShape)
                        .border(1.dp, Color.White.copy(alpha = 0.15f), CircleShape)
                        .testTag("home_settings_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = SleekSlate200,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Branding Crest & App Title: SOLITAIRE KING (Real Game Icon)
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .shadow(16.dp, RoundedCornerShape(22.dp))
                    .clip(RoundedCornerShape(22.dp))
                    .border(
                        2.5.dp,
                        Brush.linearGradient(
                            listOf(
                                Color(0xFFFFE082),
                                Color(0xFFFFD700),
                                SleekEmerald400
                            )
                        ),
                        RoundedCornerShape(22.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = androidx.compose.ui.res.painterResource(id = com.solitaire.hyper.card.games.R.drawable.solitaire_game_icon_1791958844060),
                    contentDescription = "Solitaire King Game Icon",
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "SOLITAIRE KING",
                color = SleekEmerald400,
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 2.5.sp
            )
            Text(
                text = "Classic Klondike Solitaire • Royal Edition",
                color = SleekSlate300,
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Central Solitaire Hero Card
            SolitaireHeroGameCard(
                hasSavedGame = hasSavedGame,
                onPlay = {
                    if (hasSavedGame) onContinueGame() else onPlayDraw1()
                },
                onPlayDraw1 = onPlayDraw1,
                onPlayDraw3 = onPlayDraw3,
                onPlayWinnable = onPlayWinnable,
                onDailyChallenge = onDailyChallenge
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Modern Quick Navigation Strip
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ModernHomePill(
                    modifier = Modifier.weight(1f),
                    title = "Stats",
                    subtitle = "Win Rates",
                    icon = Icons.Default.BarChart,
                    testTag = "home_stats_pill",
                    onClick = onStatistics
                )
                ModernHomePill(
                    modifier = Modifier.weight(1f),
                    title = "Themes",
                    subtitle = "Card Decks",
                    icon = Icons.Default.Palette,
                    testTag = "home_themes_pill",
                    onClick = onCustomization
                )
                ModernHomePill(
                    modifier = Modifier.weight(1f),
                    title = "Trophies",
                    subtitle = "Badges",
                    icon = Icons.Default.EmojiEvents,
                    testTag = "home_achievements_pill",
                    onClick = onAchievements
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Bioscience & Cognitive Skills Feature Card
            Surface(
                onClick = onBioscienceSkills,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .testTag("home_bioscience_skills_btn"),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF042017),
                border = BorderStroke(1.2.dp, Brush.horizontalGradient(listOf(Color(0xFF10B981), Color(0xFF06B6D4))))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF10B981).copy(alpha = 0.2f),
                            border = BorderStroke(1.dp, Color(0xFF10B981)),
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("🧬", fontSize = 18.sp)
                            }
                        }
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Bioscience Skills Lab",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color(0xFF06B6D4).copy(alpha = 0.25f)
                                ) {
                                    Text(
                                        text = "NEW",
                                        color = Color(0xFF38BDF8),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Cognitive Neuro-Tracker & Memory Science",
                                color = SleekSlate300,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.Science,
                        contentDescription = "Bioscience Skills",
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

/**
 * Prominent Hero Game Card for Solitaire (Classic Klondike).
 */
@Composable
fun SolitaireHeroGameCard(
    hasSavedGame: Boolean,
    onPlay: () -> Unit,
    onPlayDraw1: () -> Unit,
    onPlayDraw3: () -> Unit,
    onPlayWinnable: () -> Unit,
    onDailyChallenge: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .testTag("mode_solitaire_card"),
        shape = RoundedCornerShape(22.dp),
        color = Color(0xFF0A291A),
        border = BorderStroke(2.dp, SleekEmerald400),
        shadowElevation = 10.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF0F3D27),
                            Color(0xFF071F14),
                            Color(0xFF03120B)
                        )
                    )
                )
                .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = SleekEmerald500.copy(alpha = 0.35f),
                    border = BorderStroke(1.dp, SleekEmerald400)
                ) {
                    Text(
                        text = "ROYAL CLASSIC",
                        color = SleekEmerald400,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.White.copy(alpha = 0.08f)
                ) {
                    Text(
                        text = "SINGLE PLAYER",
                        color = SleekSlate300,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            // 5D Solitaire Royal Logo Emblem
            Solitaire5DLogoEmblem()

            Spacer(Modifier.height(14.dp))

            // Game Title & Subtitle
            Text(
                text = "Solitaire",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )
            Spacer(Modifier.height(3.dp))
            Text(
                text = "Classic Klondike Solitaire",
                color = SleekEmerald400,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(18.dp))

            // Big CTA Play Button
            Button(
                onClick = onPlay,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .testTag("play_solitaire_btn"),
                colors = ButtonDefaults.buttonColors(containerColor = SleekEmerald500),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                Spacer(Modifier.width(8.dp))
                Text(
                    text = if (hasSavedGame) "CONTINUE SOLITAIRE" else "PLAY SOLITAIRE",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                )
            }

            Spacer(Modifier.height(14.dp))

            // Quick Mode Selectors
            Text(
                text = "QUICK DEAL MODES",
                color = SleekSlate400,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Draw 1
                QuickModeButton(
                    modifier = Modifier.weight(1f),
                    title = "Draw 1",
                    subtitle = "Turn 1 card",
                    testTag = "btn_draw_1",
                    onClick = onPlayDraw1
                )

                // Draw 3
                QuickModeButton(
                    modifier = Modifier.weight(1f),
                    title = "Draw 3",
                    subtitle = "Turn 3 cards",
                    testTag = "btn_draw_3",
                    onClick = onPlayDraw3
                )
            }

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Winnable Deal
                QuickModeButton(
                    modifier = Modifier.weight(1f),
                    title = "🏆 Winnable",
                    subtitle = "Solvable deal",
                    testTag = "btn_winnable",
                    onClick = onPlayWinnable
                )

                // Daily Challenge
                QuickModeButton(
                    modifier = Modifier.weight(1f),
                    title = "📅 Daily",
                    subtitle = "Challenge puzzle",
                    testTag = "btn_daily",
                    onClick = onDailyChallenge
                )
            }
        }
    }
}

@Composable
fun QuickModeButton(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    testTag: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .testTag(testTag),
        shape = RoundedCornerShape(10.dp),
        color = Color.White.copy(alpha = 0.06f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                color = SleekSlate100,
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = subtitle,
                color = SleekSlate400,
                fontSize = 10.sp,
                fontWeight = FontWeight.Normal
            )
        }
    }
}

@Composable
fun ModernHomePill(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    testTag: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .testTag(testTag),
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFF0F261B),
        border = BorderStroke(1.dp, SleekEmerald400.copy(alpha = 0.35f))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Color(0xFFFFD700),
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = title,
                color = SleekSlate100,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = subtitle,
                color = SleekSlate400,
                fontSize = 10.sp,
                fontWeight = FontWeight.Normal
            )
        }
    }
}

/**
 * 5D Royal Solitaire Logo Emblem featuring high-resolution 3D artwork.
 */
@Composable
fun Solitaire5DLogoEmblem(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(76.dp)
            .shadow(12.dp, RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .border(
                2.dp,
                Brush.linearGradient(
                    listOf(
                        Color(0xFFFFE082),
                        Color(0xFFFFD700),
                        SleekEmerald400
                    )
                ),
                RoundedCornerShape(20.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = androidx.compose.ui.res.painterResource(id = com.solitaire.hyper.card.games.R.drawable.solitaire_game_icon_1791958844060),
            contentDescription = "Royal Solitaire Emblem",
            contentScale = androidx.compose.ui.layout.ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

