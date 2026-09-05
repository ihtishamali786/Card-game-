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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
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
import com.solitaire.hyper.card.games.ads.BannerAdView
import com.solitaire.hyper.card.games.data.GameRepository
import com.solitaire.hyper.card.games.data.GameStatistics
import com.solitaire.hyper.card.games.ui.game.formatTime
import com.solitaire.hyper.card.games.ui.theme.SleekBgDark
import com.solitaire.hyper.card.games.ui.theme.SleekBorderSubtle
import com.solitaire.hyper.card.games.ui.theme.SleekEmerald400
import com.solitaire.hyper.card.games.ui.theme.SleekEmerald500
import com.solitaire.hyper.card.games.ui.theme.SleekHeaderDark
import com.solitaire.hyper.card.games.ui.theme.SleekSlate100
import com.solitaire.hyper.card.games.ui.theme.SleekSlate300
import com.solitaire.hyper.card.games.ui.theme.SleekSlate400
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    repository: GameRepository,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Draw 1", "Draw 3", "Daily")

    var stats by remember { mutableStateOf(GameStatistics()) }
    var showClearDialog by remember { mutableStateOf(false) }

    fun refreshStats() {
        scope.launch {
            val mode = when (selectedTab) {
                0 -> "DRAW_1"
                1 -> "DRAW_3"
                else -> "DAILY"
            }
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
        },
        bottomBar = {
            BannerAdView()
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(SleekBgDark)
        ) {
            PrimaryTabRow(
                selectedTabIndex = selectedTab,
                containerColor = SleekHeaderDark,
                contentColor = SleekEmerald400
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                color = if (selectedTab == index) SleekEmerald400 else SleekSlate400,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Main High-Level Summary Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = SleekHeaderDark),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        StatBigBlock(label = "PLAYED", value = "${stats.gamesPlayed}")
                        StatBigBlock(label = "WON", value = "${stats.gamesWon}")
                        StatBigBlock(label = "WIN RATE", value = "${stats.winPercentage}%")
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Detailed Metrics Grid
                Text(
                    text = "Performance Records",
                    color = SleekSlate100,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatMetricCard(modifier = Modifier.weight(1f), label = "Current Streak", value = "${stats.currentStreak}")
                    StatMetricCard(modifier = Modifier.weight(1f), label = "Best Streak", value = "${stats.bestStreak}")
                }

                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatMetricCard(modifier = Modifier.weight(1f), label = "Best Score", value = "${stats.bestScore}")
                    StatMetricCard(modifier = Modifier.weight(1f), label = "Best Time", value = formatTime(stats.bestTimeSeconds))
                }

                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatMetricCard(modifier = Modifier.weight(1f), label = "Avg Moves", value = "${stats.averageMoves}")
                    StatMetricCard(modifier = Modifier.weight(1f), label = "Avg Time", value = formatTime(stats.averageTimeSeconds))
                }
            }
        }

        if (showClearDialog) {
            AlertDialog(
                onDismissRequest = { showClearDialog = false },
                title = { Text("Reset Statistics?") },
                text = { Text("Are you sure you want to clear all game history and records? This cannot be undone.") },
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
fun StatBigBlock(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, color = SleekEmerald400, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
        Text(text = label, color = SleekSlate400, fontSize = 11.sp, fontWeight = FontWeight.Medium)
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
