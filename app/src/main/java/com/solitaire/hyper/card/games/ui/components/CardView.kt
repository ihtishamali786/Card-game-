package com.solitaire.hyper.card.games.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solitaire.hyper.card.games.game.model.Card
import com.solitaire.hyper.card.games.ui.customization.CardBackTheme
import com.solitaire.hyper.card.games.ui.customization.CardFaceTheme
import com.solitaire.hyper.card.games.ui.customization.CustomizationRegistry

/**
 * Material 3 Card Component with double-tap to foundation and smooth visual states.
 */
@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun CardView(
    card: Card?,
    modifier: Modifier = Modifier,
    cardBack: CardBackTheme = CustomizationRegistry.CARD_BACKS.first(),
    cardFace: CardFaceTheme = CustomizationRegistry.CARD_FACES.first(),
    isSelected: Boolean = false,
    isValidTarget: Boolean = false,
    isHinted: Boolean = false,
    largePrint: Boolean = false,
    onClick: (() -> Unit)? = null,
    onDoubleClick: (() -> Unit)? = null
) {
    val infiniteTransition = rememberInfiniteTransition(label = "card_glow")
    val pulseGlow by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_glow"
    )

    val shape = RoundedCornerShape(8.dp)

    val borderModifier = when {
        isSelected -> Modifier.border(2.5.dp, Color(0xFFFBBF24), shape)
        isValidTarget -> Modifier.border(2.5.dp, Color(0xFF38BDF8).copy(alpha = pulseGlow), shape)
        isHinted -> Modifier.border(2.5.dp, Color(0xFF34D399).copy(alpha = pulseGlow), shape)
        card?.isFaceUp == true -> Modifier.border(0.8.dp, Color.Black.copy(alpha = 0.15f), shape)
        else -> Modifier.border(0.8.dp, Color.White.copy(alpha = 0.2f), shape)
    }

    val elevation: Dp = when {
        isSelected -> 10.dp
        isValidTarget -> 8.dp
        card?.isFaceUp == true -> 3.dp
        else -> 2.dp
    }

    val accessibilityDesc = when {
        card == null -> "Empty card slot"
        !card.isFaceUp -> "Face down card"
        else -> "${card.rank.symbol} of ${card.suit.name.lowercase()}"
    }

    val clickableModifier = if (onClick != null || onDoubleClick != null) {
        Modifier.combinedClickable(
            onClick = { onClick?.invoke() },
            onDoubleClick = { onDoubleClick?.invoke() }
        )
    } else {
        Modifier
    }

    Surface(
        modifier = modifier
            .shadow(elevation, shape, ambientColor = Color.Black.copy(alpha = 0.35f))
            .then(borderModifier)
            .clip(shape)
            .then(clickableModifier)
            .semantics { contentDescription = accessibilityDesc }
            .testTag(if (card != null) "card_${card.id}" else "card_slot_empty"),
        shape = shape,
        color = Color.Transparent
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (card == null) {
                CardSlotPlaceholder(isValidTarget = isValidTarget)
            } else if (!card.isFaceUp) {
                // Procedural Card Back
                Canvas(modifier = Modifier.fillMaxSize()) {
                    CardBackRenderer.renderCardBack(
                        drawScope = this,
                        cardBack = cardBack
                    )
                }
            } else {
                // Procedural Card Face
                Canvas(modifier = Modifier.fillMaxSize()) {
                    CardFaceRenderer.renderCardFace(
                        drawScope = this,
                        card = card,
                        cardFace = cardFace,
                        isLargePrint = largePrint
                    )
                }
            }

            // Selection indicator badge
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(2.dp)
                        .background(Color(0xFFFBBF24), CircleShape)
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = "✓",
                        color = Color.Black,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            // Valid target indicator
            if (isValidTarget && card != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 2.dp)
                        .background(Color(0xFF38BDF8).copy(alpha = 0.9f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = "MOVE",
                        color = Color.Black,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }
}

@Composable
fun CardSlotPlaceholder(
    isValidTarget: Boolean = false,
    iconSymbol: String? = null,
    label: String? = null
) {
    val shape = RoundedCornerShape(8.dp)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                if (isValidTarget) Color(0xFF38BDF8).copy(alpha = 0.15f)
                else Color.Black.copy(alpha = 0.15f)
            )
            .border(
                1.dp,
                if (isValidTarget) Color(0xFF38BDF8).copy(alpha = 0.6f)
                else Color.White.copy(alpha = 0.15f),
                shape
            ),
        contentAlignment = Alignment.Center
    ) {
        if (!iconSymbol.isNullOrBlank()) {
            Text(
                text = iconSymbol,
                color = if (isValidTarget) Color(0xFF38BDF8) else Color.White.copy(alpha = 0.25f),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }
    }
}
