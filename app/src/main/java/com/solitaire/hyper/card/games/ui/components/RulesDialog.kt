package com.solitaire.hyper.card.games.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.solitaire.hyper.card.games.ui.theme.SleekBorderSubtle
import com.solitaire.hyper.card.games.ui.theme.SleekEmerald400
import com.solitaire.hyper.card.games.ui.theme.SleekEmerald500
import com.solitaire.hyper.card.games.ui.theme.SleekHeaderDark
import com.solitaire.hyper.card.games.ui.theme.SleekSlate300
import com.solitaire.hyper.card.games.ui.theme.SleekSlate400

/**
 * How to Play & Game Rules dialog in clean English.
 */
@Composable
fun RulesDialog(
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("rules_dialog"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SleekHeaderDark),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(SleekEmerald500.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.MenuBook,
                            contentDescription = null,
                            tint = SleekEmerald400,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "HOW TO PLAY",
                            color = SleekEmerald400,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "Klondike Solitaire Guide",
                            color = SleekSlate300,
                            fontSize = 12.sp
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_rules_button")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = SleekSlate300)
                    }
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = SleekBorderSubtle
                )

                // Rule Section 1: Objective
                RuleItem(
                    icon = "🎯",
                    title = "Objective",
                    description = "Build four Foundation piles by suit (♠ ♥ ♦ ♣), starting from Ace (A) up to King (K)."
                )

                // Rule Section 2: Tableau Stacking
                RuleItem(
                    icon = "🎴",
                    title = "Tableau Stacking",
                    description = "Stack cards in descending order (K, Q, J, 10... A) with alternating colors (Red on Black, Black on Red). Only Kings can fill empty columns."
                )

                // Rule Section 3: Stock & Waste
                RuleItem(
                    icon = "🔄",
                    title = "Stock & Waste Pile",
                    description = "When you have no legal moves on the board, tap the Stock deck to draw new cards to the Waste pile."
                )

                // Rule Section 4: Easy Controls & Boosters
                RuleItem(
                    icon = "⚡",
                    title = "Fast 1-Tap & Drag Moves",
                    description = "Tap any playable card to instantly auto-move it to the foundation or best column. Or drag and drop to your desired spot!"
                )

                // Rule Section 5: Magic Wand Booster
                RuleItem(
                    icon = "🪄",
                    title = "Magic Wand Booster",
                    description = "Stuck with no moves? Tap the Magic Wand button to automatically unlock a hidden face-down card or make the smartest move!"
                )

                // Rule Section 6: Coins & 3D Themes
                RuleItem(
                    icon = "🪙",
                    title = "Coins, Ad-Free & 3D VIP",
                    description = "Win games or watch video ads to earn Coins! Spend 1,000 Coins to disable ads for 1 hour, or watch 5 video ads to unlock all 3D VIP themes for 1 hour."
                )

                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("rules_got_it_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = SleekEmerald500),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("GOT IT", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
private fun RuleItem(
    icon: String,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(text = icon, fontSize = 20.sp, modifier = Modifier.padding(top = 2.dp))
        Spacer(Modifier.width(10.dp))
        Column {
            Text(
                text = title,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = description,
                color = SleekSlate300,
                fontSize = 12.sp,
                lineHeight = 16.sp
            )
        }
    }
}
