package com.solitaire.hyper.card.games.ui.daily

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solitaire.hyper.card.games.ads.BannerAdView
import com.solitaire.hyper.card.games.data.database.DailyChallengeEntity
import com.solitaire.hyper.card.games.ui.theme.SleekBgDark
import com.solitaire.hyper.card.games.ui.theme.SleekBorderSubtle
import com.solitaire.hyper.card.games.ui.theme.SleekEmerald400
import com.solitaire.hyper.card.games.ui.theme.SleekEmerald500
import com.solitaire.hyper.card.games.ui.theme.SleekHeaderDark
import com.solitaire.hyper.card.games.ui.theme.SleekSlate100
import com.solitaire.hyper.card.games.ui.theme.SleekSlate300
import com.solitaire.hyper.card.games.ui.theme.SleekSlate400
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyChallengeScreen(
    completedChallengesFlow: Flow<List<DailyChallengeEntity>>,
    onPlayDateChallenge: (dateStr: String) -> Unit,
    onBack: () -> Unit
) {
    val completedList by completedChallengesFlow.collectAsState(initial = emptyList())
    val completedDates = completedList.map { it.date }.toSet()

    val todayCalendar = Calendar.getInstance()
    val todayDateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(todayCalendar.time)
    val monthTitle = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(todayCalendar.time)
    val daysInMonth = todayCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    val currentDay = todayCalendar.get(Calendar.DAY_OF_MONTH)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Daily Challenges", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("back_button")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SleekHeaderDark,
                    titleContentColor = SleekSlate100,
                    navigationIconContentColor = SleekSlate300
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
                .padding(16.dp)
        ) {
            // Trophy Summary Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SleekHeaderDark),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.EmojiEvents,
                            contentDescription = null,
                            tint = SleekEmerald400,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(Modifier.height(4.dp))
                        Text("Crowns Won", color = SleekSlate400, fontSize = 12.sp)
                        Text("${completedDates.size}", color = SleekSlate100, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    }

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(48.dp)
                            .background(Color.White.copy(alpha = 0.1f))
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (completedDates.contains(todayDateStr)) "TODAY'S CROWN" else "TODAY'S PUZZLE",
                            color = SleekEmerald400,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(6.dp))
                        Button(
                            onClick = { onPlayDateChallenge(todayDateStr) },
                            colors = ButtonDefaults.buttonColors(containerColor = SleekEmerald500),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.testTag("play_today_challenge")
                        ) {
                            Text(
                                text = if (completedDates.contains(todayDateStr)) "Replay" else "Play Now",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Month Title
            Text(
                text = monthTitle,
                color = SleekSlate100,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 4.dp, bottom = 12.dp)
            )

            // Calendar Grid of Days
            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(daysInMonth) { index ->
                    val dayNum = index + 1
                    val dateStr = String.format(Locale.US, "%04d-%02d-%02d",
                        todayCalendar.get(Calendar.YEAR),
                        todayCalendar.get(Calendar.MONTH) + 1,
                        dayNum
                    )
                    val isCompleted = completedDates.contains(dateStr)
                    val isToday = dayNum == currentDay
                    val isFuture = dayNum > currentDay

                    val border = when {
                        isToday -> androidx.compose.foundation.BorderStroke(2.dp, SleekEmerald400)
                        else -> null
                    }

                    Surface(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .then(
                                if (!isFuture) {
                                    Modifier.clickable { onPlayDateChallenge(dateStr) }
                                } else Modifier
                            )
                            .testTag("day_$dayNum"),
                        color = when {
                            isCompleted -> SleekEmerald500.copy(alpha = 0.35f)
                            isFuture -> Color.White.copy(alpha = 0.04f)
                            else -> SleekHeaderDark
                        },
                        shape = RoundedCornerShape(8.dp),
                        border = border
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            if (isCompleted) {
                                Icon(
                                    Icons.Default.Star,
                                    contentDescription = "Completed",
                                    tint = SleekEmerald400,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Text(
                                text = "$dayNum",
                                color = if (isFuture) SleekSlate400.copy(alpha = 0.4f) else SleekSlate100,
                                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
