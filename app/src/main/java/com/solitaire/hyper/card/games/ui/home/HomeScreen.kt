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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solitaire.hyper.card.games.ads.BannerAdView
import com.solitaire.hyper.card.games.ui.theme.SleekBorderSubtle
import com.solitaire.hyper.card.games.ui.theme.SleekEmerald400
import com.solitaire.hyper.card.games.ui.theme.SleekEmerald500
import com.solitaire.hyper.card.games.ui.theme.SleekGradientBottom
import com.solitaire.hyper.card.games.ui.theme.SleekGradientTop
import com.solitaire.hyper.card.games.ui.theme.SleekHeaderDark
import com.solitaire.hyper.card.games.ui.theme.SleekSlate100
import com.solitaire.hyper.card.games.ui.theme.SleekSlate300
import com.solitaire.hyper.card.games.ui.theme.SleekSlate400
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    hasSavedGame: Boolean,
    onContinueGame: () -> Unit,
    onPlayDraw1: () -> Unit,
    onPlayDraw3: () -> Unit,
    onDailyChallenge: () -> Unit,
    onStatistics: () -> Unit,
    onCustomization: () -> Unit,
    onAchievements: () -> Unit,
    onSettings: () -> Unit
) {
    val currentDateStr = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()).format(Date())

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
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Branding Crest & App Title
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .background(SleekEmerald400, CircleShape)
                    .border(3.dp, SleekEmerald500.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Star,
                    contentDescription = "Hyper Crown",
                    tint = SleekHeaderDark,
                    modifier = Modifier.size(44.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "SOLITAIRE",
                color = SleekEmerald400,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 4.sp
            )
            Text(
                text = "HYPER CARD GAMES",
                color = SleekSlate300,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(26.dp))

            // Continue Game Card (if available)
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
                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "CONTINUE GAME",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Play Draw 1 Button
            Button(
                onClick = onPlayDraw1,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("play_draw_1_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (hasSavedGame) SleekHeaderDark else SleekEmerald500
                ),
                border = if (hasSavedGame) BorderStroke(1.dp, SleekBorderSubtle) else null,
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    "PLAY DRAW 1",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Play Draw 3 Button
            OutlinedButton(
                onClick = onPlayDraw3,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("play_draw_3_button"),
                border = BorderStroke(1.5.dp, Color.White.copy(alpha = 0.2f)),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White.copy(alpha = 0.03f)),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("PLAY DRAW 3", color = SleekSlate100, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(18.dp))

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
                            Text("Daily Challenge", color = SleekSlate100, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text(currentDateStr, color = SleekSlate400, fontSize = 12.sp)
                        }
                    }
                    Text("PLAY ›", color = SleekEmerald400, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Navigation Grid: Stats, Themes, Achievements, Settings
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                HomeMenuItem(
                    modifier = Modifier.weight(1f),
                    title = "Statistics",
                    icon = Icons.Default.BarChart,
                    testTag = "nav_statistics",
                    onClick = onStatistics
                )
                HomeMenuItem(
                    modifier = Modifier.weight(1f),
                    title = "Themes",
                    icon = Icons.Default.Palette,
                    testTag = "nav_themes",
                    onClick = onCustomization
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                HomeMenuItem(
                    modifier = Modifier.weight(1f),
                    title = "Achievements",
                    icon = Icons.Default.EmojiEvents,
                    testTag = "nav_achievements",
                    onClick = onAchievements
                )
                HomeMenuItem(
                    modifier = Modifier.weight(1f),
                    title = "Settings",
                    icon = Icons.Default.Settings,
                    testTag = "nav_settings",
                    onClick = onSettings
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Non-intrusive Ad
            BannerAdView()

            Spacer(modifier = Modifier.height(16.dp))
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
            modifier = Modifier.padding(vertical = 16.dp, horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = title, tint = SleekEmerald400, modifier = Modifier.size(26.dp))
            Spacer(Modifier.height(8.dp))
            Text(title, color = SleekSlate100, fontSize = 13.sp, fontWeight = FontWeight.Medium)
        }
    }
}

