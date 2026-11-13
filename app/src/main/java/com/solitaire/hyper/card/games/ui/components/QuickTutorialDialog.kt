package com.solitaire.hyper.card.games.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.border
import androidx.compose.ui.window.Dialog
import com.solitaire.hyper.card.games.ui.theme.SleekEmerald400
import com.solitaire.hyper.card.games.ui.theme.SleekEmerald500
import com.solitaire.hyper.card.games.ui.theme.SleekHeaderDark
import com.solitaire.hyper.card.games.ui.theme.SleekSlate100
import com.solitaire.hyper.card.games.ui.theme.SleekSlate300
import com.solitaire.hyper.card.games.ui.theme.SleekSlate400

data class TutorialStep(
    val stepNumber: Int,
    val icon: ImageVector,
    val title: String,
    val description: String,
    val highlightPill: String
)

/**
 * First-launch 3-screen quick tutorial matching Klondike Zen standards.
 */
@Composable
fun QuickTutorialDialog(
    onDismiss: () -> Unit
) {
    var currentStep by remember { mutableIntStateOf(0) }

    val steps = listOf(
        TutorialStep(
            stepNumber = 1,
            icon = Icons.Default.EmojiEvents,
            title = "Build the 4 Foundations",
            description = "Move all 52 cards up into the 4 foundation piles at the top right. Build each suit from Ace up to King (A → 2 → 3 ... → K).",
            highlightPill = "Step 1 of 3: The Objective"
        ),
        TutorialStep(
            stepNumber = 2,
            icon = Icons.Default.Layers,
            title = "Alternate Colors in Tableau",
            description = "On the board, stack cards in descending order (King down to Ace), alternating between red and black suits. Empty spaces can only hold Kings.",
            highlightPill = "Step 2 of 3: Card Stacking"
        ),
        TutorialStep(
            stepNumber = 3,
            icon = Icons.Default.Star,
            title = "Win with the Joker",
            description = "Feeling stuck? Tap the Joker! He makes the optimal move or flips open a buried card. You get 3 free Jokers every day, with extra available anytime.",
            highlightPill = "Step 3 of 3: The Joker Helper"
        )
    )

    val step = steps[currentStep]

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("quick_tutorial_dialog"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SleekHeaderDark),
            border = BorderStroke(1.2.dp, SleekEmerald400.copy(alpha = 0.4f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = SleekEmerald400.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, SleekEmerald400.copy(alpha = 0.35f))
                ) {
                    Text(
                        text = step.highlightPill,
                        color = SleekEmerald400,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                Spacer(Modifier.height(18.dp))

                AnimatedContent(targetState = step, label = "tutorial_step") { curStep ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Big Round Icon
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(Color(0xFF0F251E), CircleShape)
                                .border(1.5.dp, SleekEmerald400, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = curStep.icon,
                                contentDescription = null,
                                tint = SleekEmerald400,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Spacer(Modifier.height(16.dp))

                        Text(
                            text = curStep.title,
                            color = SleekSlate100,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center
                        )

                        Spacer(Modifier.height(10.dp))

                        Text(
                            text = curStep.description,
                            color = SleekSlate300,
                            fontSize = 13.5.sp,
                            lineHeight = 20.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(Modifier.height(22.dp))

                // Dots indicator
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(3) { idx ->
                        Box(
                            modifier = Modifier
                                .size(if (idx == currentStep) 18.dp else 8.dp, 8.dp)
                                .background(
                                    if (idx == currentStep) SleekEmerald400 else SleekSlate400.copy(alpha = 0.3f),
                                    RoundedCornerShape(4.dp)
                                )
                        )
                    }
                }

                Spacer(Modifier.height(22.dp))

                // Bottom Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (currentStep > 0) {
                        TextButton(onClick = { currentStep -= 1 }) {
                            Text("Back", color = SleekSlate400, fontSize = 13.sp)
                        }
                    } else {
                        TextButton(onClick = onDismiss) {
                            Text("Skip", color = SleekSlate400, fontSize = 13.sp)
                        }
                    }

                    Button(
                        onClick = {
                            if (currentStep < 2) {
                                currentStep += 1
                            } else {
                                onDismiss()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SleekEmerald500),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (currentStep == 2) "Start Playing" else "Next",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.5.sp
                            )
                            Spacer(Modifier.width(4.dp))
                            Icon(
                                imageVector = if (currentStep == 2) Icons.Default.Check else Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
