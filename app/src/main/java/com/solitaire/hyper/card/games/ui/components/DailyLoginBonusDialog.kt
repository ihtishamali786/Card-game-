package com.solitaire.hyper.card.games.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Stars
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.solitaire.hyper.card.games.ui.theme.SleekEmerald400
import com.solitaire.hyper.card.games.ui.theme.SleekEmerald500
import com.solitaire.hyper.card.games.ui.theme.SleekHeaderDark
import com.solitaire.hyper.card.games.ui.theme.SleekSlate100
import com.solitaire.hyper.card.games.ui.theme.SleekSlate300
import com.solitaire.hyper.card.games.ui.theme.SleekSlate400

data class DayRewardItem(
    val day: Int,
    val coins: Int,
    val extraDescription: String? = null,
    val iconEmoji: String = "🪙"
)

private val WeekRewards = listOf(
    DayRewardItem(1, 150, null, "🪙"),
    DayRewardItem(2, 250, null, "🪙"),
    DayRewardItem(3, 350, "+1 Magic Wand", "🪄"),
    DayRewardItem(4, 450, null, "🪙"),
    DayRewardItem(5, 600, "+1 Ad-Free", "🛡️"),
    DayRewardItem(6, 750, "+2 AI Tokens", "💡"),
    DayRewardItem(7, 1000, "+2 Wands & Token", "👑")
)

@Composable
fun DailyLoginBonusDialog(
    currentStreakDay: Int, // 1 to 7
    isAvailableToClaim: Boolean,
    onClaimReward: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = true)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF1E293B),
                            Color(0xFF0F172A),
                            Color(0xFF090D16)
                        )
                    )
                )
                .border(1.5.dp, SleekEmerald400.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                .testTag("daily_login_bonus_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header with close button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = SleekEmerald400.copy(alpha = 0.2f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("🎁", fontSize = 18.sp)
                            }
                        }
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "DAILY LOGIN REWARD",
                                color = SleekEmerald400,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Day $currentStreakDay of 7 Streak",
                                color = SleekSlate300,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = SleekSlate300)
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Streak Banner
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = SleekHeaderDark,
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = if (isAvailableToClaim) "🎉 Today's Bonus Ready!" else "✓ Claimed Today!",
                                color = if (isAvailableToClaim) Color(0xFFFFD700) else SleekEmerald400,
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Come back tomorrow to keep your streak!",
                                color = SleekSlate400,
                                fontSize = 11.sp
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFFFD700).copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, Color(0xFFFFD700).copy(alpha = 0.4f))
                        ) {
                            Text(
                                text = "🔥 $currentStreakDay DAY",
                                color = Color(0xFFFFD700),
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Days 1-6 Grid (2 rows of 3)
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        for (i in 0 until 3) {
                            val item = WeekRewards[i]
                            val isCompleted = item.day < currentStreakDay || (item.day == currentStreakDay && !isAvailableToClaim)
                            val isToday = item.day == currentStreakDay
                            DailyRewardDayCard(
                                item = item,
                                isToday = isToday,
                                isCompleted = isCompleted,
                                isClaimable = isToday && isAvailableToClaim,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        for (i in 3 until 6) {
                            val item = WeekRewards[i]
                            val isCompleted = item.day < currentStreakDay || (item.day == currentStreakDay && !isAvailableToClaim)
                            val isToday = item.day == currentStreakDay
                            DailyRewardDayCard(
                                item = item,
                                isToday = isToday,
                                isCompleted = isCompleted,
                                isClaimable = isToday && isAvailableToClaim,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    // Day 7 Mega Vault Card
                    val day7 = WeekRewards[6]
                    val isDay7Completed = day7.day < currentStreakDay || (day7.day == currentStreakDay && !isAvailableToClaim)
                    val isDay7Today = day7.day == currentStreakDay
                    MegaDay7VaultCard(
                        item = day7,
                        isToday = isDay7Today,
                        isCompleted = isDay7Completed,
                        isClaimable = isDay7Today && isAvailableToClaim
                    )
                }

                Spacer(Modifier.height(18.dp))

                // Action button: Claim or Claimed state
                if (isAvailableToClaim) {
                    val todayItem = WeekRewards.getOrNull(currentStreakDay - 1) ?: WeekRewards[0]
                    Button(
                        onClick = onClaimReward,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .testTag("claim_daily_bonus_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = SleekEmerald500),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "CLAIM ${todayItem.coins} COINS NOW",
                            color = Color.White,
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp
                        )
                    }
                } else {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .testTag("dismiss_daily_bonus_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = SleekHeaderDark),
                        border = BorderStroke(1.dp, SleekEmerald400.copy(alpha = 0.5f))
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = SleekEmerald400, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "CLAIMED FOR TODAY — PLAY SOLITAIRE",
                            color = SleekEmerald400,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DailyRewardDayCard(
    item: DayRewardItem,
    isToday: Boolean,
    isCompleted: Boolean,
    isClaimable: Boolean,
    modifier: Modifier = Modifier
) {
    val containerColor = when {
        isClaimable -> Color(0xFF143026)
        isCompleted -> SleekHeaderDark.copy(alpha = 0.6f)
        isToday -> Color(0xFF1E293B)
        else -> SleekHeaderDark
    }

    val borderColor = when {
        isClaimable -> SleekEmerald400
        isToday -> Color(0xFFFFD700).copy(alpha = 0.7f)
        isCompleted -> SleekEmerald500.copy(alpha = 0.4f)
        else -> Color.White.copy(alpha = 0.08f)
    }

    Card(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = BorderStroke(if (isClaimable || isToday) 1.5.dp else 1.dp, borderColor),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Day ${item.day}",
                    color = if (isToday) SleekEmerald400 else SleekSlate300,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold
                )
                if (isCompleted) {
                    Spacer(Modifier.width(3.dp))
                    Icon(
                        Icons.Default.Check,
                        contentDescription = "Claimed",
                        tint = SleekEmerald400,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }

            Spacer(Modifier.height(4.dp))

            Text(text = item.iconEmoji, fontSize = 22.sp)

            Spacer(Modifier.height(4.dp))

            Text(
                text = "${item.coins}",
                color = Color(0xFFFFD700),
                fontWeight = FontWeight.Black,
                fontSize = 13.sp
            )

            if (item.extraDescription != null) {
                Text(
                    text = item.extraDescription,
                    color = SleekEmerald400,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun MegaDay7VaultCard(
    item: DayRewardItem,
    isToday: Boolean,
    isCompleted: Boolean,
    isClaimable: Boolean
) {
    val containerColor = when {
        isClaimable -> Color(0xFF281C03)
        isCompleted -> SleekHeaderDark.copy(alpha = 0.6f)
        else -> Color(0xFF1F1705)
    }

    val borderColor = when {
        isClaimable -> Color(0xFFFFD700)
        isToday -> Color(0xFFFFD700)
        isCompleted -> SleekEmerald500.copy(alpha = 0.4f)
        else -> Color(0xFFFFD700).copy(alpha = 0.4f)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp)),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = BorderStroke(1.5.dp, borderColor),
        shape = RoundedCornerShape(14.dp)
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
                        .size(42.dp)
                        .background(Color(0xFFFFD700).copy(alpha = 0.2f), CircleShape)
                        .border(1.dp, Color(0xFFFFD700), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("👑", fontSize = 22.sp)
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "DAY 7: MEGA VAULT",
                            color = Color(0xFFFFD700),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black
                        )
                        if (isCompleted) {
                            Spacer(Modifier.width(6.dp))
                            Icon(Icons.Default.Check, contentDescription = "Done", tint = SleekEmerald400, modifier = Modifier.size(14.dp))
                        }
                    }
                    Text(
                        text = "1,000 Coins + 2 Magic Wands + 1 Ad-Free",
                        color = SleekSlate300,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFFFFD700),
                contentColor = Color.Black
            ) {
                Text(
                    text = "1,000 🪙",
                    color = Color.Black,
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}
