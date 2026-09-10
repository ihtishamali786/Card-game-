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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
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
import com.solitaire.hyper.card.games.game.model.Suit
import com.solitaire.hyper.card.games.ui.customization.CardBackTheme
import com.solitaire.hyper.card.games.ui.customization.CardFaceStyle
import com.solitaire.hyper.card.games.ui.customization.CardFaceTheme
import com.solitaire.hyper.card.games.ui.customization.CustomizationRegistry
import kotlin.math.cos
import kotlin.math.sin

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
        else -> Modifier.border(1.dp, Color(0x33B71C1C), shape)
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

/**
 * Renders the face of the playing card with custom themes matching user photos.
 */
@Composable
fun CardFaceView(
    card: Card,
    cardFace: CardFaceTheme,
    largePrint: Boolean = false
) {
    val style = cardFace.style

    // 1. Theme-specific colors
    val textColor = when (style) {
        CardFaceStyle.CRIMSON_ANVIL -> {
            if (card.color == CardColor.RED) Color(0xFF8B0000) else Color(0xFF141414)
        }
        CardFaceStyle.SILVER_DRAGON -> {
            if (card.color == CardColor.RED) Color(0xFFB91C1C) else Color(0xFF111827)
        }
        CardFaceStyle.GOLDEN_HEARTS -> {
            if (card.color == CardColor.RED) Color(0xFFD50000) else Color(0xFF3E2723)
        }
        CardFaceStyle.VINTAGE_TAVERN -> {
            if (card.color == CardColor.RED) Color(0xFF781824) else Color(0xFF1A1A1A)
        }
        CardFaceStyle.BAROQUE_ACES -> {
            if (card.color == CardColor.RED) Color(0xFF991B1B) else Color(0xFF1F2937)
        }
        CardFaceStyle.AZTEC_MANDALA -> {
            if (card.color == CardColor.RED) Color(0xFFC62828) else Color(0xFF121212)
        }
        CardFaceStyle.MODERN_POPART -> {
            if (card.color == CardColor.RED) Color(0xFFFF1744) else Color(0xFF0F172A)
        }
        CardFaceStyle.STANDARD -> {
            if (card.color == CardColor.RED) Color(0xFFD32F2F) else Color(0xFF1E2124)
        }
    }

    val fontFamily = when (style) {
        CardFaceStyle.CRIMSON_ANVIL, CardFaceStyle.VINTAGE_TAVERN, CardFaceStyle.BAROQUE_ACES -> FontFamily.Serif
        else -> FontFamily.SansSerif
    }

    // Indices sizing
    val cornerRankSize = if (largePrint) 16.sp else 13.5.sp
    val cornerRankLineHeight = if (largePrint) 16.sp else 13.5.sp
    val cornerSuitSize = if (largePrint) 14.sp else 12.sp
    val cornerSuitLineHeight = if (largePrint) 14.sp else 12.sp

    // Background modifier
    val bgModifier = when (style) {
        CardFaceStyle.CRIMSON_ANVIL -> Modifier.background(Color(0xFFFAF7F2))
        CardFaceStyle.SILVER_DRAGON -> Modifier.background(
            Brush.linearGradient(listOf(Color(0xFFE8E8EC), Color(0xFFFFFFFF), Color(0xFFD1D5DB), Color(0xFFE5E7EB)))
        )
        CardFaceStyle.GOLDEN_HEARTS -> Modifier.background(
            Brush.linearGradient(listOf(Color(0xFFFFDF73), Color(0xFFE5B83B), Color(0xFFFFF0A6), Color(0xFFCF9C22)))
        )
        CardFaceStyle.VINTAGE_TAVERN -> Modifier.background(Color(0xFFF3EBDA))
        CardFaceStyle.BAROQUE_ACES -> Modifier.background(Color(0xFFFAF7EE))
        CardFaceStyle.AZTEC_MANDALA -> Modifier.background(Color(0xFFFCFCFC))
        CardFaceStyle.MODERN_POPART -> Modifier.background(Color(0xFFFFFFFF))
        CardFaceStyle.STANDARD -> Modifier.background(Color(0xFFFCFCFC))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .then(bgModifier)
            .padding(2.dp)
    ) {
        // Theme framing line & ornaments
        Canvas(modifier = Modifier.fillMaxSize()) {
            val pad = 1.5.dp.toPx()
            val w = size.width - 2 * pad
            val h = size.height - 2 * pad

            when (style) {
                CardFaceStyle.CRIMSON_ANVIL -> {
                    // Photo 3: Sharp black outer border with crimson inner hairline
                    drawRoundRect(
                        color = Color(0xFF141414),
                        topLeft = Offset(pad, pad),
                        size = Size(w, h),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx(), 4.dp.toPx()),
                        style = Stroke(width = 0.9.dp.toPx())
                    )
                    val inPad = pad + 2.dp.toPx()
                    drawRoundRect(
                        color = Color(0xFF8B0000).copy(alpha = 0.55f),
                        topLeft = Offset(inPad, inPad),
                        size = Size(size.width - 2 * inPad, size.height - 2 * inPad),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(3.dp.toPx(), 3.dp.toPx()),
                        style = Stroke(width = 0.5.dp.toPx())
                    )
                }
                CardFaceStyle.SILVER_DRAGON -> {
                    drawRoundRect(
                        color = Color(0xFF9CA3AF),
                        topLeft = Offset(pad, pad),
                        size = Size(w, h),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx(), 4.dp.toPx()),
                        style = Stroke(width = 0.7.dp.toPx())
                    )
                }
                CardFaceStyle.GOLDEN_HEARTS -> {
                    drawRoundRect(
                        color = Color(0xFFB45309).copy(alpha = 0.7f),
                        topLeft = Offset(pad, pad),
                        size = Size(w, h),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx(), 4.dp.toPx()),
                        style = Stroke(width = 0.8.dp.toPx())
                    )
                }
                CardFaceStyle.VINTAGE_TAVERN -> {
                    drawRoundRect(
                        color = Color(0xFF781824).copy(alpha = 0.45f),
                        topLeft = Offset(pad, pad),
                        size = Size(w, h),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(3.dp.toPx(), 3.dp.toPx()),
                        style = Stroke(width = 0.8.dp.toPx())
                    )
                }
                CardFaceStyle.BAROQUE_ACES -> {
                    drawRoundRect(
                        color = Color(0xFFB8860B).copy(alpha = 0.65f),
                        topLeft = Offset(pad, pad),
                        size = Size(w, h),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx(), 4.dp.toPx()),
                        style = Stroke(width = 0.9.dp.toPx())
                    )
                }
                CardFaceStyle.AZTEC_MANDALA -> {
                    drawRoundRect(
                        color = Color(0xFF1F2937).copy(alpha = 0.4f),
                        topLeft = Offset(pad, pad),
                        size = Size(w, h),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx(), 4.dp.toPx()),
                        style = Stroke(width = 0.6.dp.toPx())
                    )
                }
                CardFaceStyle.MODERN_POPART -> {
                    drawRoundRect(
                        color = Color(0xFF00E5FF).copy(alpha = 0.5f),
                        topLeft = Offset(pad, pad),
                        size = Size(w, h),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx(), 4.dp.toPx()),
                        style = Stroke(width = 0.7.dp.toPx())
                    )
                }
                CardFaceStyle.STANDARD -> {
                    drawRoundRect(
                        color = Color(0x14000000),
                        topLeft = Offset(pad, pad),
                        size = Size(w, h),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx(), 4.dp.toPx()),
                        style = Stroke(width = 0.5.dp.toPx())
                    )
                }
            }
        }

        // Top-Left Index (Rank only - top suit sign removed per user instruction)
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 2.5.dp, top = 2.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = card.rank.display,
                color = textColor,
                fontSize = cornerRankSize,
                fontWeight = FontWeight.Black,
                fontFamily = fontFamily,
                lineHeight = cornerRankLineHeight
            )
        }

        // Center Illustration / Emblems (The ONLY suit sign on the entire card, bold and enlarged)
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when {
                // Royalty Court Cards (King, Queen, Jack)
                card.rank in listOf(Rank.JACK, Rank.QUEEN, Rank.KING) -> {
                    CourtCardCenterView(card = card, cardFace = cardFace, textColor = textColor, largePrint = largePrint)
                }
                // Ace Cards
                card.rank == Rank.ACE -> {
                    AceCardCenterView(card = card, cardFace = cardFace, textColor = textColor, largePrint = largePrint)
                }
                // Number Cards
                else -> {
                    NumberCardCenterView(card = card, cardFace = cardFace, textColor = textColor, largePrint = largePrint)
                }
            }
        }

        // Bottom-Right Index (Rank only - bottom suit sign removed per user instruction)
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 2.5.dp, bottom = 2.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = card.rank.display,
                color = textColor,
                fontSize = cornerRankSize,
                fontWeight = FontWeight.Black,
                fontFamily = fontFamily,
                lineHeight = cornerRankLineHeight
            )
        }
    }
}

/**
 * Center graphics for Court Cards (K, Q, J) reflecting the user's specific decks.
 */
@Composable
private fun CourtCardCenterView(
    card: Card,
    cardFace: CardFaceTheme,
    textColor: Color,
    largePrint: Boolean
) {
    val style = cardFace.style

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        when (style) {
            CardFaceStyle.CRIMSON_ANVIL -> {
                // ⭐ Photo 3: Medieval blacksmith court monarch crown & bold enlarged center suit sign
                val emblem = when (card.rank) {
                    Rank.KING -> "♔" // King of Hearts with royal crown
                    Rank.QUEEN -> "♕" // Queen of Hearts with floral veil
                    else -> "⚔" // Jack of Hearts with broadsword
                }
                Text(
                    text = emblem,
                    color = textColor,
                    fontSize = if (largePrint) 22.sp else 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = card.suit.symbol,
                    color = textColor,
                    fontSize = if (largePrint) 30.sp else 25.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center
                )
            }
            CardFaceStyle.SILVER_DRAGON -> {
                // Photo 1: Metallic chrome armored royalty
                Text(
                    text = when (card.rank) {
                        Rank.KING -> "♚"
                        Rank.QUEEN -> "♛"
                        else -> "♞"
                    },
                    color = textColor,
                    fontSize = if (largePrint) 22.sp else 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = card.suit.symbol,
                    color = textColor,
                    fontSize = if (largePrint) 28.sp else 24.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center
                )
            }
            CardFaceStyle.GOLDEN_HEARTS -> {
                // Photo 2: 24K pure gold court with ruby gem
                Text(
                    text = when (card.rank) {
                        Rank.KING -> "👑"
                        Rank.QUEEN -> "👸"
                        else -> "⚔️"
                    },
                    fontSize = if (largePrint) 20.sp else 17.sp,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = card.suit.symbol,
                    color = textColor,
                    fontSize = if (largePrint) 28.sp else 24.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center
                )
            }
            CardFaceStyle.VINTAGE_TAVERN -> {
                // Photo 4: Renaissance woodblock King
                Text(
                    text = when (card.rank) {
                        Rank.KING -> "👑"
                        Rank.QUEEN -> "♕"
                        else -> "🗡"
                    },
                    color = textColor,
                    fontSize = if (largePrint) 22.sp else 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = card.suit.symbol,
                    color = textColor,
                    fontSize = if (largePrint) 28.sp else 24.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center
                )
            }
            CardFaceStyle.BAROQUE_ACES -> {
                // Photo 5: Illuminated renaissance court
                Text(
                    text = when (card.rank) {
                        Rank.KING -> "♔"
                        Rank.QUEEN -> "♕"
                        else -> "⚜"
                    },
                    color = Color(0xFFB8860B),
                    fontSize = if (largePrint) 22.sp else 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = card.suit.symbol,
                    color = textColor,
                    fontSize = if (largePrint) 28.sp else 24.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center
                )
            }
            CardFaceStyle.AZTEC_MANDALA -> {
                // Photo 6: Sacred Aztec tribal deity
                Text(
                    text = when (card.rank) {
                        Rank.KING -> "☀️"
                        Rank.QUEEN -> "🌙"
                        else -> "⚡"
                    },
                    fontSize = if (largePrint) 20.sp else 17.sp,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = card.suit.symbol,
                    color = textColor,
                    fontSize = if (largePrint) 28.sp else 24.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center
                )
            }
            CardFaceStyle.MODERN_POPART -> {
                // Photo 7: Pop art designer character
                Text(
                    text = when (card.rank) {
                        Rank.KING -> "👾"
                        Rank.QUEEN -> "💎"
                        else -> "⚡"
                    },
                    fontSize = if (largePrint) 20.sp else 17.sp,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = card.suit.symbol,
                    color = textColor,
                    fontSize = if (largePrint) 28.sp else 24.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center
                )
            }
            CardFaceStyle.STANDARD -> {
                Text(
                    text = when (card.rank) {
                        Rank.KING -> "♔"
                        Rank.QUEEN -> "♕"
                        else -> "⚔"
                    },
                    color = textColor.copy(alpha = 0.90f),
                    fontSize = if (largePrint) 22.sp else 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = card.suit.symbol,
                    color = textColor,
                    fontSize = if (largePrint) 28.sp else 24.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

/**
 * Center graphics for Ace cards.
 */
@Composable
private fun AceCardCenterView(
    card: Card,
    cardFace: CardFaceTheme,
    textColor: Color,
    largePrint: Boolean
) {
    val style = cardFace.style

    when (style) {
        CardFaceStyle.CRIMSON_ANVIL -> {
            // ⭐ Photo 3: Dramatic bold enlarged suit sign with anvil diamond
            Text(
                text = card.suit.symbol,
                color = textColor,
                fontSize = if (largePrint) 44.sp else 38.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center
            )
        }
        CardFaceStyle.BAROQUE_ACES -> {
            Text(
                text = card.suit.symbol,
                color = textColor,
                fontSize = if (largePrint) 44.sp else 38.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center
            )
        }
        CardFaceStyle.AZTEC_MANDALA -> {
            Text(
                text = card.suit.symbol,
                color = textColor,
                fontSize = if (largePrint) 44.sp else 38.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center
            )
        }
        CardFaceStyle.MODERN_POPART -> {
            Text(
                text = if (card.suit == Suit.HEARTS) "💜" else card.suit.symbol,
                color = textColor,
                fontSize = if (largePrint) 42.sp else 36.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center
            )
        }
        else -> {
            Text(
                text = card.suit.symbol,
                color = textColor,
                fontSize = if (largePrint) 44.sp else 38.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Center graphics for Number cards (2 to 10).
 */
@Composable
private fun NumberCardCenterView(
    card: Card,
    cardFace: CardFaceTheme,
    textColor: Color,
    largePrint: Boolean
) {
    val style = cardFace.style

    when (style) {
        CardFaceStyle.MODERN_POPART -> {
            Text(
                text = if (card.suit == Suit.HEARTS) "❤️" else card.suit.symbol,
                color = textColor,
                fontSize = if (largePrint) 38.sp else 32.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center
            )
        }
        else -> {
            Text(
                text = card.suit.symbol,
                color = textColor,
                fontSize = if (largePrint) 38.sp else 32.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Renders the Card Back with custom geometry and artwork matching user photos.
 */
@Composable
fun CardBackView(cardBack: CardBackTheme) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // Outer base background
        drawRect(
            color = cardBack.baseColor,
            size = size
        )

        // Inner margin frame
        val margin = 3.dp.toPx()
        drawRoundRect(
            color = cardBack.accentColor.copy(alpha = 0.45f),
            topLeft = Offset(margin, margin),
            size = Size(w - 2 * margin, h - 2 * margin),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx(), 4.dp.toPx()),
            style = Stroke(width = 1.2.dp.toPx())
        )

        val cx = w / 2
        val cy = h / 2

        when (cardBack.patternType) {
            // ⭐ 1. PHOTO 3: Crimson Anvil Royal Forge (Main Board Default Back)
            CardBackTheme.PatternType.CRIMSON_ANVIL -> {
                val r = minOf(w, h) * 0.38f
                // Crimson hearth fire glow ring
                drawCircle(
                    color = Color(0xFFB71C1C).copy(alpha = 0.35f),
                    radius = r,
                    center = Offset(cx, cy)
                )
                drawCircle(
                    color = Color(0xFFD4AF37), // Forge gold trim
                    radius = r * 0.72f,
                    center = Offset(cx, cy),
                    style = Stroke(width = 1.6.dp.toPx())
                )
                // Forged Anvil silhouette in steel & gold
                val anvilW = r * 0.75f
                val anvilH = r * 0.35f
                drawRect(
                    color = Color(0xFFD4AF37),
                    topLeft = Offset(cx - anvilW / 2, cy - anvilH / 2),
                    size = Size(anvilW, anvilH)
                )
                // Anvil horn (left)
                val hornPath = Path().apply {
                    moveTo(cx - anvilW / 2, cy - anvilH / 2)
                    lineTo(cx - anvilW * 0.75f, cy - anvilH * 0.2f)
                    lineTo(cx - anvilW / 2, cy + anvilH / 2)
                    close()
                }
                drawPath(hornPath, color = Color(0xFFD4AF37))
                // Central crimson flame ruby
                drawCircle(
                    color = Color(0xFFD50000),
                    radius = 3.5.dp.toPx(),
                    center = Offset(cx, cy)
                )
                // Crossed forge swords
                drawLine(
                    color = Color(0xFFFAF7F2),
                    start = Offset(cx - r * 0.5f, cy - r * 0.5f),
                    end = Offset(cx + r * 0.5f, cy + r * 0.5f),
                    strokeWidth = 1.2.dp.toPx()
                )
                drawLine(
                    color = Color(0xFFFAF7F2),
                    start = Offset(cx + r * 0.5f, cy - r * 0.5f),
                    end = Offset(cx - r * 0.5f, cy + r * 0.5f),
                    strokeWidth = 1.2.dp.toPx()
                )
            }

            // 2. PHOTO 1: Silver Dragon Waterproof
            CardBackTheme.PatternType.SILVER_DRAGON -> {
                val r = minOf(w, h) * 0.40f
                // Metallic burst rays radiating from dragon
                for (angle in 0 until 16) {
                    val rad = Math.toRadians(angle * 22.5)
                    val ex = cx + (r * cos(rad)).toFloat()
                    val ey = cy + (r * sin(rad)).toFloat()
                    drawLine(
                        color = Color(0xFFD4D4D8).copy(alpha = 0.35f),
                        start = Offset(cx, cy),
                        end = Offset(ex, ey),
                        strokeWidth = 1.dp.toPx()
                    )
                }
                // Coiled dragon circular crest
                drawCircle(
                    color = Color(0xFFD4D4D8),
                    radius = r * 0.65f,
                    center = Offset(cx, cy),
                    style = Stroke(width = 2.dp.toPx())
                )
                drawCircle(
                    color = Color(0xFFFFFFFF),
                    radius = r * 0.35f,
                    center = Offset(cx, cy),
                    style = Stroke(width = 1.5.dp.toPx())
                )
                drawCircle(
                    color = Color(0xFFE4E4E7),
                    radius = 3.5.dp.toPx(),
                    center = Offset(cx, cy)
                )
            }

            // 3. PHOTO 2: 24K Golden Hearts Luxury
            CardBackTheme.PatternType.GOLDEN_HEARTS -> {
                val r = minOf(w, h) * 0.38f
                // 24K Diamond lattice weave
                val step = 8.dp.toPx()
                var x = 0f
                while (x < w + h) {
                    drawLine(
                        color = Color(0xFFFFD700).copy(alpha = 0.3f),
                        start = Offset(x, 0f),
                        end = Offset(x - h, h),
                        strokeWidth = 1.dp.toPx()
                    )
                    drawLine(
                        color = Color(0xFFFFD700).copy(alpha = 0.3f),
                        start = Offset(x - h, 0f),
                        end = Offset(x, h),
                        strokeWidth = 1.dp.toPx()
                    )
                    x += step
                }
                // Center luxury bullion seal
                drawCircle(
                    color = Color(0xFFFFD700),
                    radius = r * 0.6f,
                    center = Offset(cx, cy),
                    style = Stroke(width = 2.dp.toPx())
                )
                drawCircle(
                    color = Color(0xFFD50000), // Ruby core
                    radius = r * 0.25f,
                    center = Offset(cx, cy)
                )
            }

            // 4. PHOTO 4: Vintage Tavern Kings
            CardBackTheme.PatternType.VINTAGE_TAVERN -> {
                val r = minOf(w, h) * 0.35f
                // Tavern woodcut Celtic compass seal
                drawCircle(
                    color = Color(0xFFC8963E),
                    radius = r,
                    center = Offset(cx, cy),
                    style = Stroke(width = 1.8.dp.toPx())
                )
                drawCircle(
                    color = Color(0xFF781824).copy(alpha = 0.5f),
                    radius = r * 0.7f,
                    center = Offset(cx, cy)
                )
                // 4 compass arms
                val arm = r * 0.85f
                drawLine(color = Color(0xFFC8963E), start = Offset(cx - arm, cy), end = Offset(cx + arm, cy), strokeWidth = 1.5.dp.toPx())
                drawLine(color = Color(0xFFC8963E), start = Offset(cx, cy - arm), end = Offset(cx, cy + arm), strokeWidth = 1.5.dp.toPx())
            }

            // 5. PHOTO 5: Reformation Baroque Aces
            CardBackTheme.PatternType.BAROQUE_ACES -> {
                val r = minOf(w, h) * 0.36f
                // Double baroque gold rosettes & cross seal
                drawCircle(
                    color = Color(0xFFB8860B),
                    radius = r,
                    center = Offset(cx, cy),
                    style = Stroke(width = 1.5.dp.toPx())
                )
                drawCircle(
                    color = Color(0xFFB8860B).copy(alpha = 0.35f),
                    radius = r * 0.65f,
                    center = Offset(cx, cy)
                )
                // Cross seal
                val arm = r * 0.45f
                drawLine(color = Color(0xFFFFFFFF), start = Offset(cx - arm, cy), end = Offset(cx + arm, cy), strokeWidth = 2.dp.toPx())
                drawLine(color = Color(0xFFFFFFFF), start = Offset(cx, cy - arm), end = Offset(cx, cy + arm), strokeWidth = 2.dp.toPx())
            }

            // 6. PHOTO 6: Aztec Tribal Mandala
            CardBackTheme.PatternType.AZTEC_MANDALA -> {
                val r = minOf(w, h) * 0.38f
                // Concentric Aztec sunstone wheel
                drawCircle(
                    color = Color(0xFFD32F2F),
                    radius = r,
                    center = Offset(cx, cy),
                    style = Stroke(width = 2.dp.toPx())
                )
                drawCircle(
                    color = Color(0xFFFFB300),
                    radius = r * 0.68f,
                    center = Offset(cx, cy),
                    style = Stroke(width = 1.5.dp.toPx())
                )
                drawCircle(
                    color = Color(0xFFD32F2F),
                    radius = r * 0.35f,
                    center = Offset(cx, cy)
                )
            }

            // 7. PHOTO 7: Modern Pop Art Avant-Garde
            CardBackTheme.PatternType.MODERN_POPART -> {
                val r = minOf(w, h) * 0.38f
                // Pop art neon diagonal chevron & neon heart
                val step = 7.dp.toPx()
                var y = 0f
                while (y < h) {
                    drawLine(
                        color = Color(0xFF00E5FF).copy(alpha = 0.25f),
                        start = Offset(0f, y),
                        end = Offset(w, y),
                        strokeWidth = 0.8.dp.toPx()
                    )
                    y += step
                }
                drawCircle(
                    color = Color(0xFFFF007F),
                    radius = r * 0.6f,
                    center = Offset(cx, cy),
                    style = Stroke(width = 2.dp.toPx())
                )
                drawCircle(
                    color = Color(0xFF00E5FF),
                    radius = r * 0.25f,
                    center = Offset(cx, cy)
                )
            }

            CardBackTheme.PatternType.SLEEK_EMERALD -> {
                val r = minOf(w, h) * 0.36f
                drawCircle(color = cardBack.accentColor.copy(alpha = 0.22f), radius = r, center = Offset(cx, cy))
                drawCircle(color = cardBack.accentColor.copy(alpha = 0.5f), radius = r * 0.65f, center = Offset(cx, cy), style = Stroke(width = 1.2.dp.toPx()))
                val arm = 5.dp.toPx()
                drawLine(color = cardBack.accentColor, start = Offset(cx - arm, cy), end = Offset(cx + arm, cy), strokeWidth = 1.6.dp.toPx())
                drawLine(color = cardBack.accentColor, start = Offset(cx, cy - arm), end = Offset(cx, cy + arm), strokeWidth = 1.6.dp.toPx())
            }

            CardBackTheme.PatternType.ROYAL_CREST -> {
                val r = minOf(w, h) * 0.32f
                drawCircle(color = cardBack.accentColor.copy(alpha = 0.3f), radius = r, center = Offset(cx, cy), style = Stroke(width = 1.dp.toPx()))
                drawRect(color = cardBack.accentColor.copy(alpha = 0.5f), topLeft = Offset(cx - r * 0.4f, cy - r * 0.4f), size = Size(r * 0.8f, r * 0.8f))
            }

            CardBackTheme.PatternType.DIAMOND_GEOMETRY -> {
                val step = 8.dp.toPx()
                var x = 0f
                while (x < w + h) {
                    drawLine(color = cardBack.accentColor.copy(alpha = 0.25f), start = Offset(x, 0f), end = Offset(x - h, h), strokeWidth = 1.dp.toPx())
                    drawLine(color = cardBack.accentColor.copy(alpha = 0.25f), start = Offset(x - h, 0f), end = Offset(x, h), strokeWidth = 1.dp.toPx())
                    x += step
                }
            }

            CardBackTheme.PatternType.CLASSIC_TARTAN -> {
                val step = 6.dp.toPx()
                var x = 0f
                while (x < w) {
                    drawLine(color = cardBack.accentColor.copy(alpha = 0.2f), start = Offset(x, 0f), end = Offset(x, h), strokeWidth = 1.dp.toPx())
                    x += step
                }
                var y = 0f
                while (y < h) {
                    drawLine(color = cardBack.accentColor.copy(alpha = 0.2f), start = Offset(0f, y), end = Offset(w, y), strokeWidth = 1.dp.toPx())
                    y += step
                }
            }

            CardBackTheme.PatternType.HOLOGRAPHIC_3D -> {
                val r = minOf(w, h) * 0.42f
                drawCircle(color = Color(0xFF00E5FF).copy(alpha = 0.35f), radius = r, center = Offset(cx, cy))
                drawCircle(color = Color(0xFFFF007F).copy(alpha = 0.4f), radius = r * 0.72f, center = Offset(cx, cy), style = Stroke(width = 2.dp.toPx()))
                drawCircle(color = Color(0xFF76FF03).copy(alpha = 0.6f), radius = r * 0.45f, center = Offset(cx, cy), style = Stroke(width = 1.5.dp.toPx()))
                drawCircle(color = Color.White.copy(alpha = 0.85f), radius = r * 0.2f, center = Offset(cx, cy))
            }

            CardBackTheme.PatternType.GOLD_FOIL_3D -> {
                val r = minOf(w, h) * 0.38f
                drawCircle(color = Color(0xFFFFD700).copy(alpha = 0.6f), radius = r, center = Offset(cx, cy), style = Stroke(width = 2.dp.toPx()))
                drawCircle(color = Color(0xFFFFE082).copy(alpha = 0.4f), radius = r * 0.78f, center = Offset(cx, cy), style = Stroke(width = 1.2.dp.toPx()))
                val dSize = r * 0.5f
                drawLine(color = Color(0xFFFFD700), start = Offset(cx - dSize, cy), end = Offset(cx, cy - dSize), strokeWidth = 2.dp.toPx())
                drawLine(color = Color(0xFFFFD700), start = Offset(cx, cy - dSize), end = Offset(cx + dSize, cy), strokeWidth = 2.dp.toPx())
                drawLine(color = Color(0xFFFFD700), start = Offset(cx + dSize, cy), end = Offset(cx, cy + dSize), strokeWidth = 2.dp.toPx())
                drawLine(color = Color(0xFFFFD700), start = Offset(cx, cy + dSize), end = Offset(cx - dSize, cy), strokeWidth = 2.dp.toPx())
            }

            CardBackTheme.PatternType.DRAGON_3D -> {
                val r = minOf(w, h) * 0.38f
                drawCircle(color = Color(0xFFFFB300).copy(alpha = 0.5f), radius = r, center = Offset(cx, cy))
                drawCircle(color = Color(0xFFD50000).copy(alpha = 0.7f), radius = r * 0.65f, center = Offset(cx, cy), style = Stroke(width = 2.5.dp.toPx()))
                val arm = r * 0.4f
                drawLine(color = Color(0xFFFFD700), start = Offset(cx - arm, cy - arm), end = Offset(cx + arm, cy + arm), strokeWidth = 2.dp.toPx())
                drawLine(color = Color(0xFFFFD700), start = Offset(cx + arm, cy - arm), end = Offset(cx - arm, cy + arm), strokeWidth = 2.dp.toPx())
            }

            CardBackTheme.PatternType.CYBERPUNK_3D -> {
                val step = 6.dp.toPx()
                var x = 0f
                while (x < w) {
                    drawLine(color = Color(0xFF00E5FF).copy(alpha = 0.2f), start = Offset(x, 0f), end = Offset(x, h), strokeWidth = 0.8.dp.toPx())
                    x += step
                }
                drawRect(color = Color(0xFFFF007F).copy(alpha = 0.7f), topLeft = Offset(cx - 10.dp.toPx(), cy - 10.dp.toPx()), size = Size(20.dp.toPx(), 20.dp.toPx()), style = Stroke(width = 1.5.dp.toPx()))
            }

            CardBackTheme.PatternType.CRYSTAL_3D -> {
                val r = minOf(w, h) * 0.36f
                for (angle in 0 until 8) {
                    val rad = Math.toRadians(angle * 45.0)
                    val ex = cx + (r * cos(rad)).toFloat()
                    val ey = cy + (r * sin(rad)).toFloat()
                    drawLine(color = Color(0xFF80D8FF).copy(alpha = 0.6f), start = Offset(cx, cy), end = Offset(ex, ey), strokeWidth = 1.2.dp.toPx())
                }
                drawCircle(color = Color.White.copy(alpha = 0.8f), radius = 3.dp.toPx(), center = Offset(cx, cy))
            }

            else -> {
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
