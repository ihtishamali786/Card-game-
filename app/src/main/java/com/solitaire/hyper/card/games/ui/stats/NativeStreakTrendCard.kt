package com.solitaire.hyper.card.games.ui.stats

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solitaire.hyper.card.games.data.DailyStreakPoint
import com.solitaire.hyper.card.games.data.GameStatistics
import com.solitaire.hyper.card.games.ui.theme.SleekEmerald400
import com.solitaire.hyper.card.games.ui.theme.SleekHeaderDark
import com.solitaire.hyper.card.games.ui.theme.SleekSlate100
import com.solitaire.hyper.card.games.ui.theme.SleekSlate300
import com.solitaire.hyper.card.games.ui.theme.SleekSlate400

/**
 * 100% Native Jetpack Compose Data Visualization for the 30-Day Winning Streak Trend.
 * Completely replaces heavy WebView / JavaScript charts with a lightweight, 60fps Canvas curve.
 */
@Composable
fun NativeStreakTrendCard(
    stats: GameStatistics,
    modifier: Modifier = Modifier
) {
    val streakData = stats.streakTrend30Days

    val peakStreakIn30Days = remember(streakData) {
        streakData.maxOfOrNull { it.winStreak } ?: stats.bestStreak
    }
    val currentActiveStreak = remember(streakData, stats) {
        streakData.lastOrNull()?.endOfDayStreak ?: stats.currentStreak
    }
    val total30DayWins = remember(streakData) {
        streakData.sumOf { it.gamesWon }
    }
    val total30DayPlayed = remember(streakData) {
        streakData.sumOf { it.gamesPlayed }
    }

    var selectedPointIndex by remember { mutableIntStateOf(-1) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("native_streak_trend_card"),
        colors = CardDefaults.cardColors(containerColor = SleekHeaderDark),
        border = BorderStroke(1.dp, SleekEmerald400.copy(alpha = 0.35f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .background(
                                Brush.radialGradient(
                                    listOf(
                                        Color(0xFF10B981).copy(alpha = 0.3f),
                                        Color(0xFF047857).copy(alpha = 0.1f)
                                    )
                                ),
                                CircleShape
                            )
                            .border(1.dp, SleekEmerald400.copy(alpha = 0.4f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoGraph,
                            contentDescription = null,
                            tint = SleekEmerald400,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "30-Day Streak Trend",
                            color = SleekSlate100,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Native Zen Momentum Tracker",
                            color = SleekSlate400,
                            fontSize = 11.sp
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = SleekEmerald400.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, SleekEmerald400.copy(alpha = 0.35f))
                ) {
                    Text(
                        text = "30 DAYS",
                        color = SleekEmerald400,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            // Metric Summary Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetricChip(
                    modifier = Modifier.weight(1f),
                    label = "30D PEAK",
                    value = "$peakStreakIn30Days",
                    unit = if (peakStreakIn30Days == 1) "Win" else "Wins",
                    color = Color(0xFFFBBF24),
                    icon = Icons.Default.Whatshot
                )
                MetricChip(
                    modifier = Modifier.weight(1f),
                    label = "CURRENT",
                    value = "$currentActiveStreak",
                    unit = if (currentActiveStreak == 1) "Win" else "Wins",
                    color = SleekEmerald400,
                    icon = Icons.AutoMirrored.Filled.TrendingUp
                )
                MetricChip(
                    modifier = Modifier.weight(1f),
                    label = "30D WINS",
                    value = "$total30DayWins",
                    unit = "of $total30DayPlayed",
                    color = Color(0xFF38BDF8),
                    icon = Icons.Default.AutoGraph
                )
            }

            // Interactive Day Inspector Banner
            val inspectedPoint = if (selectedPointIndex in streakData.indices) streakData[selectedPointIndex] else null
            AnimatedVisibility(visible = inspectedPoint != null) {
                if (inspectedPoint != null) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF0F172A),
                        border = BorderStroke(1.dp, SleekEmerald400.copy(alpha = 0.45f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = inspectedPoint.fullDate,
                                    color = SleekSlate400,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "🔥 Win Streak: ${inspectedPoint.winStreak} ${if (inspectedPoint.winStreak == 1) "win" else "wins"}",
                                    color = SleekEmerald400,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "Played: ${inspectedPoint.gamesPlayed} | Won: ${inspectedPoint.gamesWon}",
                                    color = SleekSlate300,
                                    fontSize = 11.sp
                                )
                                Text(
                                    text = "Day ${inspectedPoint.dayNumber} of 30",
                                    color = SleekSlate400,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }

            // 100% Native Compose Canvas Chart
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF0B1713))
                    .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
            ) {
                NativeCanvasRenderer(
                    streakData = streakData,
                    selectedPointIndex = selectedPointIndex,
                    onSelectPoint = { selectedPointIndex = it },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
private fun NativeCanvasRenderer(
    streakData: List<DailyStreakPoint>,
    selectedPointIndex: Int,
    onSelectPoint: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    if (streakData.isEmpty()) {
        Box(modifier = modifier, contentAlignment = Alignment.Center) {
            Text(text = "Play your first game to start your 30-day streak!", color = SleekSlate400, fontSize = 12.sp)
        }
        return
    }

    val maxVal = remember(streakData) {
        val peak = streakData.maxOfOrNull { it.winStreak } ?: 0
        maxOf(peak + 1, 4)
    }

    Canvas(
        modifier = modifier
            .padding(top = 16.dp, bottom = 20.dp, start = 14.dp, end = 14.dp)
            .pointerInput(streakData) {
                detectTapGestures { offset ->
                    val pointWidth = size.width / (streakData.size - 1).coerceAtLeast(1)
                    val clickedIdx = ((offset.x / pointWidth) + 0.5f).toInt().coerceIn(0, streakData.lastIndex)
                    onSelectPoint(clickedIdx)
                }
            }
            .pointerInput(streakData) {
                detectDragGestures { change, _ ->
                    val pointWidth = size.width / (streakData.size - 1).coerceAtLeast(1)
                    val clickedIdx = ((change.position.x / pointWidth) + 0.5f).toInt().coerceIn(0, streakData.lastIndex)
                    onSelectPoint(clickedIdx)
                }
            }
    ) {
        val width = size.width
        val height = size.height

        // 4 Horizontal Gridlines
        val gridLines = 4
        for (i in 0..gridLines) {
            val y = height * (i.toFloat() / gridLines)
            drawLine(
                color = Color.White.copy(alpha = 0.06f),
                start = Offset(0f, y),
                end = Offset(width, y),
                strokeWidth = 1.dp.toPx()
            )
        }

        // Build smoothed path for win streaks
        val path = Path()
        val fillPath = Path()
        val points = mutableListOf<Offset>()

        streakData.forEachIndexed { i, pt ->
            val x = i * (width / (streakData.size - 1).coerceAtLeast(1))
            val normalizedVal = pt.winStreak.toFloat() / maxVal.toFloat()
            val y = height - (normalizedVal * height)
            points.add(Offset(x, y))
        }

        if (points.isNotEmpty()) {
            path.moveTo(points[0].x, points[0].y)
            fillPath.moveTo(points[0].x, height)
            fillPath.lineTo(points[0].x, points[0].y)

            for (i in 0 until points.size - 1) {
                val p0 = points[i]
                val p1 = points[i + 1]
                val controlX1 = p0.x + (p1.x - p0.x) / 2f
                val controlY1 = p0.y
                val controlX2 = p0.x + (p1.x - p0.x) / 2f
                val controlY2 = p1.y

                path.cubicTo(controlX1, controlY1, controlX2, controlY2, p1.x, p1.y)
                fillPath.cubicTo(controlX1, controlY1, controlX2, controlY2, p1.x, p1.y)
            }

            fillPath.lineTo(points.last().x, height)
            fillPath.close()

            // Area Fill Gradient
            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF10B981).copy(alpha = 0.45f),
                        Color(0xFF10B981).copy(alpha = 0.05f),
                        Color.Transparent
                    )
                )
            )

            // Main Curve Stroke
            drawPath(
                path = path,
                color = Color(0xFF34D399),
                style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
            )

            // Data Point Highlights
            points.forEachIndexed { idx, pt ->
                val streak = streakData[idx].winStreak
                val isSelected = idx == selectedPointIndex
                if (streak > 0 || isSelected) {
                    val radius = if (isSelected) 6.dp.toPx() else 3.5.dp.toPx()
                    drawCircle(
                        color = if (isSelected) Color(0xFFFBBF24) else Color(0xFF10B981),
                        radius = radius,
                        center = pt
                    )
                    drawCircle(
                        color = Color.White,
                        radius = radius * 0.45f,
                        center = pt
                    )
                }
            }
        }
    }
}

@Composable
private fun MetricChip(
    label: String,
    value: String,
    unit: String,
    color: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFF0F1E19),
        border = BorderStroke(1.dp, color.copy(alpha = 0.25f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(12.dp)
                )
                Text(
                    text = label,
                    color = SleekSlate400,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.height(3.dp))
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text(
                    text = value,
                    color = color,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = unit,
                    color = SleekSlate300,
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
