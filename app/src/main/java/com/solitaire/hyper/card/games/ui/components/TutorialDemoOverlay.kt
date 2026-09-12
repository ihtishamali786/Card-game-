package com.solitaire.hyper.card.games.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.solitaire.hyper.card.games.game.model.Hint
import com.solitaire.hyper.card.games.ui.customization.CardBackTheme
import com.solitaire.hyper.card.games.ui.customization.CardFaceTheme
import com.solitaire.hyper.card.games.ui.theme.SleekEmerald400
import com.solitaire.hyper.card.games.ui.theme.SleekEmerald500
import com.solitaire.hyper.card.games.ui.theme.SleekHeaderDark
import com.solitaire.hyper.card.games.ui.theme.SleekSlate200
import com.solitaire.hyper.card.games.ui.theme.SleekSlate300
import kotlin.math.roundToInt

/**
 * Animated visual tutorial overlay that demonstrates to a new user
 * how to pick up and move a card to its destination pile.
 * Activates automatically after 12-15 seconds of inactivity or via guide menu.
 */
@Composable
fun TutorialDemoOverlay(
    hint: Hint,
    fromOffset: Offset,
    toOffset: Offset,
    cardWidth: Dp,
    cardHeight: Dp,
    cardBack: CardBackTheme,
    cardFace: CardFaceTheme,
    onApplyMove: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val infiniteTransition = rememberInfiniteTransition(label = "tutorial_demo_loop")

    // Progress moves from 0f (source) to 1f (destination) and resets smoothly
    val animProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "demo_card_travel"
    )

    // Pulse glow for source and destination targets
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "target_pulse"
    )

    // Compute animated card position (flying arc from start to end)
    val flyProgress = (animProgress * 1.25f).coerceIn(0f, 1f)
    val curX = fromOffset.x + (toOffset.x - fromOffset.x) * flyProgress
    // Add an upward arc so the card feels picked up
    val arcHeightPx = with(density) { 36.dp.toPx() }
    val arcOffset = 4f * flyProgress * (1f - flyProgress) * arcHeightPx
    val curY = fromOffset.y + (toOffset.y - fromOffset.y) * flyProgress - arcOffset

    val cardScale = 1f + 0.10f * (4f * flyProgress * (1f - flyProgress))
    val cardAlpha = if (animProgress > 0.92f) (1f - ((animProgress - 0.92f) / 0.08f)).coerceIn(0f, 1f) else 0.94f

    Box(
        modifier = modifier
            .fillMaxSize()
            .zIndex(100f)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onDismiss
            )
    ) {
        // 1. Trajectory path line between from and to
        Canvas(modifier = Modifier.fillMaxSize()) {
            if (fromOffset != Offset.Zero && toOffset != Offset.Zero) {
                // Dashed curved trajectory guide
                val path = androidx.compose.ui.graphics.Path().apply {
                    moveTo(fromOffset.x + size.width * 0f, fromOffset.y)
                    val controlX = (fromOffset.x + toOffset.x) / 2f
                    val controlY = (fromOffset.y + toOffset.y) / 2f - arcHeightPx * 1.5f
                    quadraticTo(controlX, controlY, toOffset.x, toOffset.y)
                }

                drawPath(
                    path = path,
                    color = Color(0xFFFFD700).copy(alpha = 0.45f),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(
                        width = 2.5.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(14f, 10f), animProgress * 40f)
                    )
                )

                // Source highlight circle
                drawCircle(
                    color = Color(0xFFFFD700).copy(alpha = pulseAlpha * 0.35f),
                    radius = with(density) { 26.dp.toPx() },
                    center = fromOffset
                )

                // Destination target ring
                drawCircle(
                    color = SleekEmerald400.copy(alpha = pulseAlpha * 0.5f),
                    radius = with(density) { 28.dp.toPx() },
                    center = toOffset,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3.dp.toPx())
                )
            }
        }

        // 2. Animated Ghost Card floating along the trajectory
        if (fromOffset != Offset.Zero) {
            val cardWidthPx = with(density) { cardWidth.toPx() }
            val cardHeightPx = with(density) { cardHeight.toPx() }

            Box(
                modifier = Modifier
                    .offset {
                        IntOffset(
                            (curX - cardWidthPx / 2).roundToInt(),
                            (curY - cardHeightPx / 2).roundToInt()
                        )
                    }
                    .size(cardWidth, cardHeight)
                    .scale(cardScale)
                    .alpha(cardAlpha)
                    .shadow(16.dp, RoundedCornerShape(8.dp))
                    .border(2.5.dp, Color(0xFFFFD700), RoundedCornerShape(8.dp))
            ) {
                CardView(
                    card = hint.card,
                    cardBack = cardBack,
                    cardFace = cardFace,
                    isSelected = true,
                    largePrint = false,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // 3. Animated Guiding Finger Pointer (👆 / 👉)
            val fingerOffsetPx = with(density) { 14.dp.toPx() }
            Box(
                modifier = Modifier
                    .offset {
                        IntOffset(
                            (curX - with(density) { 16.dp.toPx() }).roundToInt(),
                            (curY + cardHeightPx / 2 - fingerOffsetPx).roundToInt()
                        )
                    }
                    .size(36.dp)
                    .alpha(cardAlpha)
                    .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                    .border(1.5.dp, Color(0xFFFFD700), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "👆",
                    fontSize = 20.sp
                )
            }
        }
    }
}
