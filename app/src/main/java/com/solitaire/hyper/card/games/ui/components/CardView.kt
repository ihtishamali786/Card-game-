package com.solitaire.hyper.card.games.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solitaire.hyper.card.games.game.model.Card
import com.solitaire.hyper.card.games.game.model.CardColor
import com.solitaire.hyper.card.games.game.model.Rank
import com.solitaire.hyper.card.games.ui.customization.CardBackTheme
import com.solitaire.hyper.card.games.ui.customization.CardFaceTheme
import com.solitaire.hyper.card.games.ui.customization.CustomizationRegistry

val CardCornerRadius = 8.dp

@Composable
fun CardView(
    card: Card?,
    modifier: Modifier = Modifier,
    cardBack: CardBackTheme = CustomizationRegistry.cardBacks.first(),
    cardFace: CardFaceTheme = CustomizationRegistry.cardFaces.first(),
    isSelected: Boolean = false,
    isValidTarget: Boolean = false,
    isHinted: Boolean = false,
    largePrint: Boolean = false,
    onClick: (() -> Unit)? = null,
    onDoubleClick: (() -> Unit)? = null
) {
    val infiniteTransition = rememberInfiniteTransition(label = "card_pulse")
    val pulseGlow by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_glow"
    )

    val shape = RoundedCornerShape(CardCornerRadius)

    val borderModifier = when {
        isSelected -> Modifier.border(3.dp, Color(0xFFFFC107), shape)
        isValidTarget -> Modifier.border(3.dp, Color(0xFF00E5FF).copy(alpha = pulseGlow), shape)
        isHinted -> Modifier.border(2.5.dp, Color(0xFF34D399).copy(alpha = pulseGlow), shape)
        card?.isFaceUp == true -> Modifier.border(0.75.dp, Color(0x22000000), shape)
        else -> Modifier.border(1.dp, Color(0x3310B981), shape)
    }

    val elevation: Dp = when {
        isSelected -> 12.dp
        isValidTarget -> 8.dp
        card?.isFaceUp == true -> 4.dp
        else -> 2.dp
    }

    Surface(
        modifier = modifier
            .testTag("card_${card?.id ?: "placeholder"}")
            .shadow(elevation, shape)
            .then(borderModifier)
            .clip(shape)
            .then(
                if (onClick != null) {
                    Modifier.clickable(onClick = onClick)
                } else Modifier
            ),
        shape = shape,
        color = if (card == null) Color.Transparent else if (card.isFaceUp) Color.White else cardBack.baseColor
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (card == null) {
                // Empty placeholder slot
                CardSlotPlaceholder(isValidTarget = isValidTarget)
            } else if (!card.isFaceUp) {
                // Card back
                CardBackView(cardBack = cardBack)
            } else {
                // Face-up card
                CardFaceView(card = card, cardFace = cardFace, largePrint = largePrint)
            }

            // High-visibility selection indicator
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

            // High-visibility valid target badge
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
fun CardFaceView(
    card: Card,
    cardFace: CardFaceTheme,
    largePrint: Boolean = false
) {
    val textColor = if (card.color == CardColor.RED) Color(0xFFD32F2F) else Color(0xFF1E2124)
    val fontFamily = if (cardFace.id == "VINTAGE" || cardFace.id == "ELEGANT") FontFamily.Serif else FontFamily.SansSerif

    val cornerRankSize = if (largePrint) 14.sp else 11.sp
    val cornerRankLineHeight = if (largePrint) 14.sp else 11.sp
    val cornerSuitSize = if (largePrint) 12.sp else 10.sp
    val cornerSuitLineHeight = if (largePrint) 12.sp else 10.sp

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(2.dp)
    ) {
        // Top-Left Index
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 2.dp, top = 1.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = card.rank.display,
                color = textColor,
                fontSize = cornerRankSize,
                fontWeight = FontWeight.Bold,
                fontFamily = fontFamily,
                lineHeight = cornerRankLineHeight
            )
            Text(
                text = card.suit.symbol,
                color = textColor,
                fontSize = cornerSuitSize,
                lineHeight = cornerSuitLineHeight
            )
        }

        // Center emblem / illustration
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (card.rank in listOf(Rank.JACK, Rank.QUEEN, Rank.KING)) {
                // Royalty badge
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = when (card.rank) {
                            Rank.KING -> "♔"
                            Rank.QUEEN -> "♕"
                            else -> "⚔"
                        },
                        color = textColor.copy(alpha = 0.85f),
                        fontSize = if (largePrint) 22.sp else 18.sp,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = card.suit.symbol,
                        color = textColor.copy(alpha = 0.5f),
                        fontSize = if (largePrint) 12.sp else 10.sp
                    )
                }
            } else if (card.rank == Rank.ACE) {
                Text(
                    text = card.suit.symbol,
                    color = textColor,
                    fontSize = if (largePrint) 26.sp else 22.sp,
                    textAlign = TextAlign.Center
                )
            } else {
                Text(
                    text = card.suit.symbol,
                    color = textColor.copy(alpha = 0.8f),
                    fontSize = if (largePrint) 18.sp else 15.sp,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Bottom-Right Index (inverted)
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 2.dp, bottom = 1.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = card.suit.symbol,
                color = textColor,
                fontSize = if (largePrint) 11.sp else 9.sp,
                lineHeight = if (largePrint) 11.sp else 9.sp
            )
            Text(
                text = card.rank.display,
                color = textColor,
                fontSize = if (largePrint) 12.sp else 10.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = fontFamily,
                lineHeight = if (largePrint) 12.sp else 10.sp
            )
        }
    }
}

@Composable
fun CardBackView(cardBack: CardBackTheme) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // Outer border
        drawRect(
            color = cardBack.baseColor,
            size = size
        )

        // Inner margin frame
        val margin = 3.dp.toPx()
        drawRoundRect(
            color = cardBack.accentColor.copy(alpha = 0.4f),
            topLeft = Offset(margin, margin),
            size = Size(w - 2 * margin, h - 2 * margin),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx(), 4.dp.toPx()),
            style = Stroke(width = 1.2.dp.toPx())
        )

        when (cardBack.patternType) {
            CardBackTheme.PatternType.SLEEK_EMERALD -> {
                val cx = w / 2
                val cy = h / 2
                val r = minOf(w, h) * 0.36f
                // Radial emerald glow ring
                drawCircle(
                    color = cardBack.accentColor.copy(alpha = 0.22f),
                    radius = r,
                    center = Offset(cx, cy)
                )
                drawCircle(
                    color = cardBack.accentColor.copy(alpha = 0.5f),
                    radius = r * 0.65f,
                    center = Offset(cx, cy),
                    style = Stroke(width = 1.2.dp.toPx())
                )
                // Sleek plus / diamond symbol
                val arm = 5.dp.toPx()
                drawLine(
                    color = cardBack.accentColor,
                    start = Offset(cx - arm, cy),
                    end = Offset(cx + arm, cy),
                    strokeWidth = 1.6.dp.toPx()
                )
                drawLine(
                    color = cardBack.accentColor,
                    start = Offset(cx, cy - arm),
                    end = Offset(cx, cy + arm),
                    strokeWidth = 1.6.dp.toPx()
                )
            }
            CardBackTheme.PatternType.ROYAL_CREST -> {
                // Central diamond and crown ornament
                val cx = w / 2
                val cy = h / 2
                val r = minOf(w, h) * 0.32f
                drawCircle(
                    color = cardBack.accentColor.copy(alpha = 0.3f),
                    radius = r,
                    center = Offset(cx, cy),
                    style = Stroke(width = 1.dp.toPx())
                )
                // Center diamond
                drawRect(
                    color = cardBack.accentColor.copy(alpha = 0.5f),
                    topLeft = Offset(cx - r * 0.4f, cy - r * 0.4f),
                    size = Size(r * 0.8f, r * 0.8f)
                )
            }
            CardBackTheme.PatternType.DIAMOND_GEOMETRY -> {
                // Cross geometric grid
                val step = 8.dp.toPx()
                var x = 0f
                while (x < w + h) {
                    drawLine(
                        color = cardBack.accentColor.copy(alpha = 0.25f),
                        start = Offset(x, 0f),
                        end = Offset(x - h, h),
                        strokeWidth = 1.dp.toPx()
                    )
                    drawLine(
                        color = cardBack.accentColor.copy(alpha = 0.25f),
                        start = Offset(x - h, 0f),
                        end = Offset(x, h),
                        strokeWidth = 1.dp.toPx()
                    )
                    x += step
                }
            }
            CardBackTheme.PatternType.CLASSIC_TARTAN -> {
                val step = 6.dp.toPx()
                var x = 0f
                while (x < w) {
                    drawLine(
                        color = cardBack.accentColor.copy(alpha = 0.2f),
                        start = Offset(x, 0f),
                        end = Offset(x, h),
                        strokeWidth = 1.dp.toPx()
                    )
                    x += step
                }
                var y = 0f
                while (y < h) {
                    drawLine(
                        color = cardBack.accentColor.copy(alpha = 0.2f),
                        start = Offset(0f, y),
                        end = Offset(w, y),
                        strokeWidth = 1.dp.toPx()
                    )
                    y += step
                }
            }
            CardBackTheme.PatternType.HYPER_GRID -> {
                // Futuristic concentric rectangles
                for (i in 1..4) {
                    val inset = (i * 4).dp.toPx()
                    if (w > inset * 2 && h > inset * 2) {
                        drawRect(
                            color = cardBack.accentColor.copy(alpha = 0.15f * i),
                            topLeft = Offset(inset, inset),
                            size = Size(w - inset * 2, h - inset * 2),
                            style = Stroke(width = 0.8.dp.toPx())
                        )
                    }
                }
            }
            CardBackTheme.PatternType.HOLOGRAPHIC_3D -> {
                val cx = w / 2
                val cy = h / 2
                val r = minOf(w, h) * 0.42f
                // 3D holographic iridescent concentric rings
                drawCircle(
                    color = Color(0xFF00E5FF).copy(alpha = 0.35f),
                    radius = r,
                    center = Offset(cx, cy)
                )
                drawCircle(
                    color = Color(0xFFFF007F).copy(alpha = 0.4f),
                    radius = r * 0.72f,
                    center = Offset(cx, cy),
                    style = Stroke(width = 2.dp.toPx())
                )
                drawCircle(
                    color = Color(0xFF76FF03).copy(alpha = 0.6f),
                    radius = r * 0.45f,
                    center = Offset(cx, cy),
                    style = Stroke(width = 1.5.dp.toPx())
                )
                drawCircle(
                    color = Color.White.copy(alpha = 0.85f),
                    radius = r * 0.2f,
                    center = Offset(cx, cy)
                )
            }
            CardBackTheme.PatternType.GOLD_FOIL_3D -> {
                val cx = w / 2
                val cy = h / 2
                val r = minOf(w, h) * 0.38f
                // Double luxury 24K gold frame
                drawCircle(
                    color = Color(0xFFFFD700).copy(alpha = 0.6f),
                    radius = r,
                    center = Offset(cx, cy),
                    style = Stroke(width = 2.dp.toPx())
                )
                drawCircle(
                    color = Color(0xFFFFE082).copy(alpha = 0.4f),
                    radius = r * 0.78f,
                    center = Offset(cx, cy),
                    style = Stroke(width = 1.2.dp.toPx())
                )
                // Center 3D embossed diamond
                val dSize = r * 0.5f
                drawLine(
                    color = Color(0xFFFFD700),
                    start = Offset(cx - dSize, cy),
                    end = Offset(cx, cy - dSize),
                    strokeWidth = 2.dp.toPx()
                )
                drawLine(
                    color = Color(0xFFFFD700),
                    start = Offset(cx, cy - dSize),
                    end = Offset(cx + dSize, cy),
                    strokeWidth = 2.dp.toPx()
                )
                drawLine(
                    color = Color(0xFFFFD700),
                    start = Offset(cx + dSize, cy),
                    end = Offset(cx, cy + dSize),
                    strokeWidth = 2.dp.toPx()
                )
                drawLine(
                    color = Color(0xFFFFD700),
                    start = Offset(cx, cy + dSize),
                    end = Offset(cx - dSize, cy),
                    strokeWidth = 2.dp.toPx()
                )
            }
            CardBackTheme.PatternType.DRAGON_3D -> {
                val cx = w / 2
                val cy = h / 2
                val r = minOf(w, h) * 0.38f
                // Imperial dragon crimson and gold motif
                drawCircle(
                    color = Color(0xFFFFB300).copy(alpha = 0.5f),
                    radius = r,
                    center = Offset(cx, cy)
                )
                drawCircle(
                    color = Color(0xFFD50000).copy(alpha = 0.7f),
                    radius = r * 0.65f,
                    center = Offset(cx, cy),
                    style = Stroke(width = 2.5.dp.toPx())
                )
                // Dragon star emblem
                val arm = r * 0.4f
                drawLine(color = Color(0xFFFFD700), start = Offset(cx - arm, cy - arm), end = Offset(cx + arm, cy + arm), strokeWidth = 2.dp.toPx())
                drawLine(color = Color(0xFFFFD700), start = Offset(cx + arm, cy - arm), end = Offset(cx - arm, cy + arm), strokeWidth = 2.dp.toPx())
            }
            CardBackTheme.PatternType.CYBERPUNK_3D -> {
                val cx = w / 2
                val cy = h / 2
                // Cyberpunk neon neon glow matrix
                val step = 6.dp.toPx()
                var x = 0f
                while (x < w) {
                    drawLine(color = Color(0xFF00E5FF).copy(alpha = 0.2f), start = Offset(x, 0f), end = Offset(x, h), strokeWidth = 0.8.dp.toPx())
                    x += step
                }
                drawRect(
                    color = Color(0xFFFF007F).copy(alpha = 0.7f),
                    topLeft = Offset(cx - 10.dp.toPx(), cy - 10.dp.toPx()),
                    size = Size(20.dp.toPx(), 20.dp.toPx()),
                    style = Stroke(width = 1.5.dp.toPx())
                )
            }
            CardBackTheme.PatternType.CRYSTAL_3D -> {
                val cx = w / 2
                val cy = h / 2
                val r = minOf(w, h) * 0.36f
                // Faceted crystal refractive rays
                for (angle in 0 until 8) {
                    val rad = Math.toRadians(angle * 45.0)
                    val ex = cx + (r * Math.cos(rad)).toFloat()
                    val ey = cy + (r * Math.sin(rad)).toFloat()
                    drawLine(
                        color = Color(0xFF80D8FF).copy(alpha = 0.6f),
                        start = Offset(cx, cy),
                        end = Offset(ex, ey),
                        strokeWidth = 1.2.dp.toPx()
                    )
                }
                drawCircle(color = Color.White.copy(alpha = 0.8f), radius = 3.dp.toPx(), center = Offset(cx, cy))
            }
            else -> {
                // Minimal decorative ring
                drawCircle(
                    color = cardBack.accentColor.copy(alpha = 0.35f),
                    radius = minOf(w, h) * 0.25f,
                    center = Offset(w / 2, h / 2),
                    style = Stroke(width = 1.5.dp.toPx())
                )
            }
        }
    }
}

@Composable
fun CardSlotPlaceholder(
    modifier: Modifier = Modifier,
    iconSymbol: String? = null,
    label: String? = null,
    isValidTarget: Boolean = false
) {
    val infiniteTransition = rememberInfiniteTransition(label = "slot_pulse")
    val pulseGlow by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "slot_pulse_glow"
    )

    val shape = RoundedCornerShape(CardCornerRadius)
    val borderColor = if (isValidTarget) {
        Color(0xFF00E5FF).copy(alpha = pulseGlow)
    } else {
        Color.White.copy(alpha = 0.15f)
    }
    val borderWidth = if (isValidTarget) 2.5.dp else 1.5.dp
    val bgColor = if (isValidTarget) {
        Color(0xFF00E5FF).copy(alpha = 0.12f)
    } else {
        Color.Black.copy(alpha = 0.20f)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .border(width = borderWidth, color = borderColor, shape = shape)
            .background(color = bgColor, shape = shape),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (iconSymbol != null) {
                Text(
                    text = iconSymbol,
                    color = if (isValidTarget) Color(0xFF00E5FF) else Color.White.copy(alpha = 0.35f),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            if (isValidTarget) {
                Text(
                    text = label ?: "TAP",
                    color = Color(0xFF00E5FF),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            } else if (label != null) {
                Text(
                    text = label,
                    color = Color.White.copy(alpha = 0.25f),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
