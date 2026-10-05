package com.solitaire.hyper.card.games.ui.components

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solitaire.hyper.card.games.game.model.Card
import com.solitaire.hyper.card.games.ui.customization.CardBackPattern
import com.solitaire.hyper.card.games.ui.customization.CardBackTheme
import com.solitaire.hyper.card.games.ui.customization.CardFaceStyle
import com.solitaire.hyper.card.games.ui.customization.CardFaceTheme
import com.solitaire.hyper.card.games.ui.customization.CustomizationRegistry
import com.solitaire.hyper.card.games.ui.customization.SuitColorScheme
import com.solitaire.hyper.card.games.ui.customization.SuitStyleTheme

@Composable
fun CardView(
    card: Card?,
    modifier: Modifier = Modifier,
    cardBack: CardBackTheme = CustomizationRegistry.cardBacks.first(),
    cardFace: CardFaceTheme = CustomizationRegistry.cardFaces.first(),
    suitStyle: SuitStyleTheme = CustomizationRegistry.suitStyles.first(),
    suitScheme: SuitColorScheme = CustomizationRegistry.suitColorSchemes.first(),
    customCardBackUri: String? = null,
    cardCornerRadius: String = "MEDIUM",
    cardIndexSize: String = "STANDARD",
    numeralsStyle: String = "WESTERN",
    isSelected: Boolean = false,
    isValidTarget: Boolean = false,
    isHinted: Boolean = false,
    largePrint: Boolean = false,
    onClick: (() -> Unit)? = null,
    onDoubleClick: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val infiniteTransition = rememberInfiniteTransition(label = "card_pulse")
    val pulseGlow by infiniteTransition.animateFloat(
        initialValue = 0.45f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_glow"
    )

    val cornerRadiusDp = when (cardCornerRadius) {
        "SHARP" -> 3.dp
        "ROUND" -> 14.dp
        else -> 8.dp
    }
    val shape = RoundedCornerShape(cornerRadiusDp)

    val borderModifier = when {
        isSelected -> Modifier.border(3.dp, Color(0xFFFFC107), shape)
        isValidTarget -> Modifier.border(3.dp, Color(0xFF00E5FF).copy(alpha = pulseGlow), shape)
        isHinted -> Modifier.border(2.5.dp, Color(0xFF34D399).copy(alpha = pulseGlow), shape)
        card?.isFaceUp == true -> Modifier.border(0.75.dp, Color(0x22000000), shape)
        else -> Modifier.border(1.dp, Color(0x333A4052), shape)
    }

    val elevation: Dp = when {
        isSelected -> 10.dp
        isValidTarget -> 8.dp
        card?.isFaceUp == true -> 3.dp
        else -> 2.dp
    }

    val accessibilityDesc = when {
        card == null -> "Empty card slot"
        !card.isFaceUp -> "Card face down"
        else -> "${card.rank.display} of ${card.suit.name.lowercase().replaceFirstChar { it.uppercase() }}"
    }

    // Decode custom card back photo bitmap if chosen
    val customBitmap = remember(customCardBackUri) {
        if (!customCardBackUri.isNullOrBlank() && cardBack.patternType == CardBackPattern.CUSTOM_PHOTO) {
            try {
                val inputStream = context.contentResolver.openInputStream(Uri.parse(customCardBackUri))
                BitmapFactory.decodeStream(inputStream)
            } catch (_: Exception) {
                null
            }
        } else null
    }

    Surface(
        modifier = modifier
            .testTag("card_${card?.id ?: "placeholder"}")
            .semantics { contentDescription = accessibilityDesc }
            .shadow(elevation, shape)
            .then(borderModifier)
            .clip(shape)
            .then(
                if (onClick != null) {
                    Modifier.clickable(onClick = onClick)
                } else Modifier
            ),
        shape = shape,
        color = Color.Transparent
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (card == null) {
                CardSlotPlaceholder(isValidTarget = isValidTarget)
            } else if (!card.isFaceUp) {
                // Procedural master Card Back
                Canvas(modifier = Modifier.fillMaxSize()) {
                    CardBackRenderer.renderCardBack(
                        drawScope = this,
                        cardBack = cardBack,
                        customPhotoBitmap = customBitmap
                    )
                }
            } else {
                // Procedural master Card Face (Jumbo Easy-Read v2 layout)
                val tokens = remember(cardFace.style) {
                    CardFaceRenderer.getTokens(cardFace.style)
                }
                val isEffectiveLargePrint = largePrint || cardIndexSize == "EXTRA_LARGE" || cardFace.style == CardFaceStyle.LARGE_INDEX
                Canvas(modifier = Modifier.fillMaxSize()) {
                    CardFaceRenderer.renderCardFace(
                        drawScope = this,
                        card = card,
                        tokens = tokens,
                        scheme = suitScheme,
                        suitStyle = suitStyle,
                        isLargePrint = isEffectiveLargePrint,
                        numeralsStyle = numeralsStyle
                    )
                }
            }

            // Selection indicator badge
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(2.dp)
                        .background(Color(0xFFFFC107), CircleShape)
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
                        .background(Color(0xFF00E5FF).copy(alpha = 0.9f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = "DROP",
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
    label: String? = null,
    iconSymbol: String? = null
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x14FFFFFF), RoundedCornerShape(8.dp))
            .border(
                1.2.dp,
                if (isValidTarget) Color(0xFF00E5FF) else Color(0x35FFFFFF),
                RoundedCornerShape(8.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        if (!iconSymbol.isNullOrEmpty()) {
            Text(
                text = iconSymbol,
                color = Color(0x30FFFFFF),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
        if (!label.isNullOrEmpty()) {
            Text(
                text = label,
                color = if (isValidTarget) Color(0xFF00E5FF) else Color(0x55FFFFFF),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
