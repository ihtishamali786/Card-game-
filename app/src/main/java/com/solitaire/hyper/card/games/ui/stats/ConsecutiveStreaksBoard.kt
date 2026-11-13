package com.solitaire.hyper.card.games.ui.stats

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import com.solitaire.hyper.card.games.data.GameStatistics
import com.solitaire.hyper.card.games.data.database.GameRecordEntity
import com.solitaire.hyper.card.games.ui.theme.SleekEmerald400
import com.solitaire.hyper.card.games.ui.theme.SleekEmerald500
import com.solitaire.hyper.card.games.ui.theme.SleekHeaderDark
import com.solitaire.hyper.card.games.ui.theme.SleekSlate100
import com.solitaire.hyper.card.games.ui.theme.SleekSlate300
import com.solitaire.hyper.card.games.ui.theme.SleekSlate400

/**
 * Dedicated Room Database-backed UI component that displays comprehensive
 * consecutive win and loss streaks, current momentum, and historical streaks.
 */
@Composable
fun ConsecutiveStreaksBoard(
    stats: GameStatistics,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("consecutive_streaks_board"),
        colors = CardDefaults.cardColors(containerColor = SleekHeaderDark),
        border = BorderStroke(1.2.dp, SleekEmerald400.copy(alpha = 0.35f)),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .background(Color(0xFFEA580C).copy(alpha = 0.2f), CircleShape)
                            .border(1.dp, Color(0xFFEA580C).copy(alpha = 0.4f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Whatshot,
                            contentDescription = null,
                            tint = Color(0xFFFB923C),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Consecutive Streaks",
                            color = SleekSlate100,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Tracked via Room Database",
                            color = SleekSlate400,
                            fontSize = 11.sp
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFF10B981).copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.3f))
                ) {
                    Text(
                        text = "ROOM DB",
                        color = SleekEmerald400,
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            // Momentum Status Banner (Wins vs Losses)
            StreakMomentumBanner(
                currentWinStreak = stats.currentStreak,
                currentLossStreak = stats.currentLossStreak
            )

            // 4 Core Streak Metrics in 2x2 Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StreakGridCard(
                    modifier = Modifier.weight(1f),
                    title = "Current Wins",
                    count = stats.currentStreak,
                    unit = if (stats.currentStreak == 1) "Win" else "Wins",
                    subtitle = if (stats.currentStreak > 0) "Active streak!" else "None active",
                    icon = Icons.AutoMirrored.Filled.TrendingUp,
                    accentColor = SleekEmerald400,
                    testTag = "metric_current_win_streak"
                )
                StreakGridCard(
                    modifier = Modifier.weight(1f),
                    title = "Best Win Run",
                    count = stats.bestStreak,
                    unit = if (stats.bestStreak == 1) "Win" else "Wins",
                    subtitle = "All-time record",
                    icon = Icons.Default.EmojiEvents,
                    accentColor = Color(0xFFFFD700),
                    testTag = "metric_best_win_streak"
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StreakGridCard(
                    modifier = Modifier.weight(1f),
                    title = "Current Losses",
                    count = stats.currentLossStreak,
                    unit = if (stats.currentLossStreak == 1) "Loss" else "Losses",
                    subtitle = if (stats.currentLossStreak > 0) "Next win resets" else "No active losses",
                    icon = Icons.AutoMirrored.Filled.TrendingDown,
                    accentColor = Color(0xFF38BDF8),
                    testTag = "metric_current_loss_streak"
                )
                StreakGridCard(
                    modifier = Modifier.weight(1f),
                    title = "Max Loss Run",
                    count = stats.worstLossStreak,
                    unit = if (stats.worstLossStreak == 1) "Loss" else "Losses",
                    subtitle = "Highest consecutive",
                    icon = Icons.Default.Shield,
                    accentColor = Color(0xFFF43F5E),
                    testTag = "metric_worst_loss_streak"
                )
            }

            // Consecutive Match Momentum Beads (Last 10 Matches)
            if (stats.recentMatches.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Recent Streak Sequence",
                            color = SleekSlate300,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Newest → Oldest",
                            color = SleekSlate400,
                            fontSize = 10.sp
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF0F172A).copy(alpha = 0.5f))
                            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(10.dp))
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        stats.recentMatches.take(10).forEach { match ->
                            MatchStreakBead(match = match)
                        }
                    }
                }
            }

            // Streak Ratio Bar (Best Win Streak vs Worst Loss Streak)
            if (stats.bestStreak > 0 || stats.worstLossStreak > 0) {
                StreakRatioVisualizer(
                    bestWinStreak = stats.bestStreak,
                    worstLossStreak = stats.worstLossStreak
                )
            }
        }
    }
}

@Composable
fun StreakMomentumBanner(
    currentWinStreak: Int,
    currentLossStreak: Int
) {
    when {
        currentWinStreak > 0 -> {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF06331E),
                border = BorderStroke(1.dp, SleekEmerald500.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "🔥", fontSize = 22.sp)
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "$currentWinStreak Consecutive Wins!",
                            color = SleekEmerald400,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "You're on a hot streak! Keep your concentration sharp.",
                            color = SleekSlate300,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
        currentLossStreak > 0 -> {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF1E293B),
                border = BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.35f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "🧊", fontSize = 22.sp)
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "$currentLossStreak Consecutive Losses",
                            color = Color(0xFF38BDF8),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "Next victory will snap the streak and reset your momentum!",
                            color = SleekSlate300,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
        else -> {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF111827),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "🎯", fontSize = 20.sp)
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = "Play your next game to establish an active win streak!",
                        color = SleekSlate300,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
fun StreakGridCard(
    modifier: Modifier = Modifier,
    title: String,
    count: Int,
    unit: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    testTag: String
) {
    Card(
        modifier = modifier.testTag(testTag),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1E19)),
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.25f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    color = SleekSlate400,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = "$count",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = unit,
                    color = accentColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
            }

            Spacer(Modifier.height(2.dp))

            Text(
                text = subtitle,
                color = SleekSlate400,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
fun MatchStreakBead(match: GameRecordEntity) {
    val isWin = match.isWin
    val bg = if (isWin) Color(0xFF166534) else Color(0xFF991B1B)
    val text = if (isWin) "W" else "L"
    val streakText = if (isWin && match.consecutiveWins > 1) {
        "${match.consecutiveWins}"
    } else if (!isWin && match.consecutiveLosses > 1) {
        "${match.consecutiveLosses}"
    } else ""

    Box(
        modifier = Modifier
            .size(28.dp)
            .clip(CircleShape)
            .background(bg)
            .border(1.dp, Color.White.copy(alpha = 0.2f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = text,
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Black
            )
            if (streakText.isNotEmpty()) {
                Text(
                    text = streakText,
                    color = if (isWin) Color(0xFFFFD700) else Color(0xFFFCA5A5),
                    fontSize = 8.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}

@Composable
fun StreakRatioVisualizer(
    bestWinStreak: Int,
    worstLossStreak: Int
) {
    val total = (bestWinStreak + worstLossStreak).coerceAtLeast(1)
    val winRatio = bestWinStreak.toFloat() / total

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Peak Wins ($bestWinStreak)",
                color = SleekEmerald400,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Peak Losses ($worstLossStreak)",
                color = Color(0xFFF43F5E),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }

        LinearProgressIndicator(
            progress = { winRatio },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = SleekEmerald400,
            trackColor = Color(0xFFF43F5E).copy(alpha = 0.5f)
        )
    }
}
