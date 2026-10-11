package com.solitaire.hyper.card.games.ui.stats

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solitaire.hyper.card.games.data.GameRepository
import com.solitaire.hyper.card.games.data.GameStatistics
import com.solitaire.hyper.card.games.data.database.GameRecordEntity
import com.solitaire.hyper.card.games.ui.game.formatTime
import com.solitaire.hyper.card.games.ui.theme.SleekBgDark
import com.solitaire.hyper.card.games.ui.theme.SleekEmerald400
import com.solitaire.hyper.card.games.ui.theme.SleekEmerald500
import com.solitaire.hyper.card.games.ui.theme.SleekHeaderDark
import com.solitaire.hyper.card.games.ui.theme.SleekSlate100
import com.solitaire.hyper.card.games.ui.theme.SleekSlate300
import com.solitaire.hyper.card.games.ui.theme.SleekSlate400
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    repository: GameRepository,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabModes = listOf("ALL", "DRAW_1", "DRAW_3", "DAILY", "CALL_BREAK")
    val tabLabels = listOf("All Games", "Draw 1", "Draw 3", "Daily", "Call Break")

    var stats by remember { mutableStateOf(GameStatistics()) }
    var showClearDialog by remember { mutableStateOf(false) }

    fun refreshStats() {
        scope.launch {
            val mode = tabModes.getOrElse(selectedTab) { "ALL" }
            stats = repository.getStatistics(mode)
        }
    }

    LaunchedEffect(selectedTab) {
        refreshStats()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Game Statistics", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("back_button")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showClearDialog = true },
                        modifier = Modifier.testTag("clear_stats_button")
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Reset Stats")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SleekHeaderDark,
                    titleContentColor = SleekSlate100,
                    navigationIconContentColor = SleekSlate300,
                    actionIconContentColor = SleekSlate300
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(SleekBgDark)
        ) {
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = SleekHeaderDark,
                contentColor = SleekEmerald400,
                edgePadding = 12.dp
            ) {
                tabLabels.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                color = if (selectedTab == index) SleekEmerald400 else SleekSlate400,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 13.sp
                            )
                        }
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // High-Level Summary Card: Played, Won, Lost, Win Rate
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = SleekHeaderDark),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 18.dp, horizontal = 12.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        StatBigBlock(label = "PLAYED", value = "${stats.gamesPlayed}", color = SleekSlate100)
                        StatBigBlock(label = "WON", value = "${stats.gamesWon}", color = SleekEmerald400)
                        StatBigBlock(label = "LOST", value = "${stats.gamesLost}", color = Color(0xFFF43F5E))
                        StatBigBlock(label = "WIN RATE", value = "${stats.winPercentage}%", color = Color(0xFF38BDF8))
                    }
                }

                // Streaks Section
                Text(
                    text = "Win Streaks & Consistency",
                    color = SleekSlate100,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StreakMetricCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Whatshot,
                        iconTint = Color(0xFFFF9800),
                        label = "Current Streak",
                        value = "${stats.currentStreak} Wins",
                        subtitle = if (stats.currentStreak > 0) "Active run!" else "Streak reset"
                    )
                    StreakMetricCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.EmojiEvents,
                        iconTint = Color(0xFFFFD700),
                        label = "Best Streak",
                        value = "${stats.bestStreak} Wins",
                        subtitle = "All-time personal record"
                    )
                }

                // Detailed Records
                Text(
                    text = "Performance Records",
                    color = SleekSlate100,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatMetricCard(modifier = Modifier.weight(1f), label = "Best Score", value = "${stats.bestScore}")
                    StatMetricCard(modifier = Modifier.weight(1f), label = "Best Time", value = formatTime(stats.bestTimeSeconds))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatMetricCard(modifier = Modifier.weight(1f), label = "Avg Moves / Tricks", value = "${stats.averageMoves}")
                    StatMetricCard(modifier = Modifier.weight(1f), label = "Avg Time", value = formatTime(stats.averageTimeSeconds))
                }

                // Recent Matches History
                Text(
                    text = "Recent Match History (Offline SQLite / Room)",
                    color = SleekSlate100,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )

                if (stats.recentMatches.isEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = SleekHeaderDark),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.06f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No matches recorded yet. Play a game to start logging stats!",
                                color = SleekSlate400,
                                fontSize = 13.sp
                            )
                        }
                    }
                } else {
                    stats.recentMatches.forEach { match ->
                        RecentMatchItem(match = match)
                    }
                }

                // Zero Data Collection Privacy Notice
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = SleekHeaderDark.copy(alpha = 0.6f)),
                    border = BorderStroke(1.dp, SleekEmerald500.copy(alpha = 0.25f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(SleekEmerald500.copy(alpha = 0.15f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Lock,
                                contentDescription = null,
                                tint = SleekEmerald400,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "100% Offline Local Storage (Room DB)",
                                color = SleekSlate100,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "All wins, losses, and streaks stay exclusively on your device. Zero cloud sync, zero telemetry.",
                                color = SleekSlate400,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))
            }
        }

        if (showClearDialog) {
            AlertDialog(
                onDismissRequest = { showClearDialog = false },
                title = { Text("Reset Statistics?") },
                text = { Text("Are you sure you want to clear all game history, win/loss records, and streak stats? This cannot be undone.") },
                confirmButton = {
                    Button(
                        onClick = {
                            showClearDialog = false
                            scope.launch {
                                repository.clearAllStats()
                                refreshStats()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Reset All")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showClearDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
private fun RecentMatchItem(match: GameRecordEntity) {
    val isWin = match.isWin
    val outcomeColor = if (isWin) SleekEmerald400 else Color(0xFFF43F5E)
    val outcomeBg = if (isWin) SleekEmerald500.copy(alpha = 0.15f) else Color(0xFFF43F5E).copy(alpha = 0.15f)
    val formattedDate = remember(match.dateMillis) {
        val sdf = SimpleDateFormat("MMM d, HH:mm", Locale.getDefault())
        sdf.format(Date(match.dateMillis))
    }
    val friendlyMode = when (match.mode) {
        "DRAW_1" -> "Draw 1"
        "DRAW_3" -> "Draw 3"
        "DAILY" -> "Daily Puzzle"
        "CALL_BREAK" -> "Call Break"
        else -> match.mode
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SleekHeaderDark),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.06f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .background(outcomeBg, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isWin) Icons.Default.CheckCircle else Icons.Default.Close,
                        contentDescription = null,
                        tint = outcomeColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(Modifier.width(10.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (isWin) "WON" else "LOST",
                            color = outcomeColor,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "• $friendlyMode",
                            color = SleekSlate300,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Text(
                        text = formattedDate,
                        color = SleekSlate400,
                        fontSize = 11.sp
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${match.score} pts",
                    color = SleekSlate100,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${match.moves} moves • ${formatTime(match.timeSeconds)}",
                    color = SleekSlate400,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
fun StatBigBlock(label: String, value: String, color: Color = SleekEmerald400) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, color = color, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(Modifier.height(2.dp))
        Text(text = label, color = SleekSlate400, fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun StreakMetricCard(
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    label: String,
    value: String,
    subtitle: String
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = SleekHeaderDark),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text(text = label, color = SleekSlate400, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }
            Spacer(Modifier.height(6.dp))
            Text(text = value, color = SleekSlate100, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(Modifier.height(2.dp))
            Text(text = subtitle, color = SleekSlate400, fontSize = 10.sp)
        }
    }
}

@Composable
fun StatMetricCard(modifier: Modifier = Modifier, label: String, value: String) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = SleekHeaderDark),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Text(text = label, color = SleekSlate400, fontSize = 12.sp)
            Spacer(Modifier.height(4.dp))
            Text(text = value, color = SleekSlate100, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}
