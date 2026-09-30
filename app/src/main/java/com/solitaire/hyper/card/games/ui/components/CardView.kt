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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solitaire.hyper.card.games.game.model.Card
import com.solitaire.hyper.card.games.game.model.Rank
import com.solitaire.hyper.card.games.game.model.Suit
import com.solitaire.hyper.card.games.ui.customization.CardBackPattern
import com.solitaire.hyper.card.games.ui.customization.CardBackTheme
import com.solitaire.hyper.card.games.ui.customization.CardFaceStyle
import com.solitaire.hyper.card.games.ui.customization.CardFaceTheme
import com.solitaire.hyper.card.games.ui.customization.CustomizationRegistry
import com.solitaire.hyper.card.games.ui.customization.SuitColorScheme
import com.solitaire.hyper.card.games.ui.customization.SuitIconStyle
import com.solitaire.hyper.card.games.ui.customization.SuitStyleTheme
import kotlin.math.cos
import kotlin.math.sin

/**
 * Maps standard ranks to Western or Eastern Arabic numerals.
 */
fun formatRank(rank: Rank, numeralsStyle: String): String {
    if (numeralsStyle != "EASTERN_ARABIC") return rank.display
    return when (rank) {
        Rank.ACE -> "A"
        Rank.TWO -> "٢"
        Rank.THREE -> "٣"
        Rank.FOUR -> "٤"
        Rank.FIVE -> "٥"
        Rank.SIX -> "٦"
        Rank.SEVEN -> "٧"
        Rank.EIGHT -> "٨"
        Rank.NINE -> "٩"
        Rank.TEN -> "١٠"
        Rank.JACK -> "J"
        Rank.QUEEN -> "Q"
        Rank.KING -> "K"
    }
}

/**
 * Returns the effective color for the suit based on the chosen scheme and card face tone.
 */
fun getSuitColor(suit: Suit, scheme: SuitColorScheme, isDarkFace: Boolean): Color {
    val base = when (suit) {
        Suit.HEARTS -> scheme.heartsColor
        Suit.DIAMONDS -> scheme.diamondsColor
        Suit.CLUBS -> scheme.clubsColor
        Suit.SPADES -> scheme.spadesColor
    }
    if (isDarkFace && (base == Color(0xFF0F172A) || base == Color(0xFF1E293B) || base == Color(0xFF000000))) {
        return Color(0xFFE2E8F0)
    }
    return base
}

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

    val cornerRadiusDp = when (cardCornerRadius) {
        "SHARP" -> 2.dp
        "ROUND" -> 14.dp
        else -> 8.dp
    }
    val shape = RoundedCornerShape(cornerRadiusDp)

    val borderModifier = when {
        isSelected -> Modifier.border(3.dp, Color(0xFFFFC107), shape)
        isValidTarget -> Modifier.border(3.dp, Color(0xFF00E5FF).copy(alpha = pulseGlow), shape)
        isHinted -> Modifier.border(2.5.dp, Color(0xFF34D399).copy(alpha = pulseGlow), shape)
        card?.isFaceUp == true -> Modifier.border(0.75.dp, Color(0x22000000), shape)
        else -> Modifier.border(1.dp, cardBack.accentColor.copy(alpha = 0.4f), shape)
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
        color = if (card == null) Color.Transparent else if (card.isFaceUp) cardFace.surfaceColor else cardBack.baseColor
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (card == null) {
                CardSlotPlaceholder(isValidTarget = isValidTarget)
            } else if (!card.isFaceUp) {
                CardBackView(cardBack = cardBack, customPhotoUri = customCardBackUri)
            } else {
                CardFaceView(
                    card = card,
                    cardFace = cardFace,
                    suitStyle = suitStyle,
                    suitScheme = suitScheme,
                    cardIndexSize = if (largePrint) "EXTRA_LARGE" else cardIndexSize,
                    numeralsStyle = numeralsStyle
                )
            }

            if (isSelected) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(2.dp)
                        .background(Color(0xFFFFC107), CircleShape)
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                    Text(text = "✓", color = Color.Black, fontSize = 9.sp, fontWeight = FontWeight.Black)
                }
            }

            if (isValidTarget && card != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 2.dp)
                        .background(Color(0xFF00E5FF).copy(alpha = 0.9f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                    Text(text = "DROP", color = Color.Black, fontSize = 8.sp, fontWeight = FontWeight.ExtraBold)
                }
            }
        }
    }
}

@Composable
fun CardFaceView(
    card: Card,
    cardFace: CardFaceTheme,
    suitStyle: SuitStyleTheme = CustomizationRegistry.suitStyles.first(),
    suitScheme: SuitColorScheme = CustomizationRegistry.suitColorSchemes.first(),
    cardIndexSize: String = "STANDARD",
    numeralsStyle: String = "WESTERN",
    largePrint: Boolean = false
) {
    val style = cardFace.style
    val isDark = cardFace.isDarkSurface
    val suitColor = getSuitColor(card.suit, suitScheme, isDark)

    val fontFamily = when (style) {
        CardFaceStyle.VINTAGE_IVORY, CardFaceStyle.CARVED_WOOD, CardFaceStyle.MUGHAL_MINIATURE -> FontFamily.Serif
        CardFaceStyle.PIXEL_RETRO -> FontFamily.Monospace
        else -> FontFamily.SansSerif
    }

    val (rankSize, rankLineHeight, suitSize) = when (cardIndexSize) {
        "EXTRA_LARGE" -> Triple(22.sp, 23.sp, 15.sp)
        "LARGE" -> Triple(18.5.sp, 19.5.sp, 13.sp)
        else -> Triple(15.sp, 16.sp, 11.sp)
    }

    val formattedRank = formatRank(card.rank, numeralsStyle)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(cardFace.surfaceColor)
            .padding(2.dp)
    ) {
        // Decorative edge border
        Canvas(modifier = Modifier.fillMaxSize()) {
            val pad = 1.2.dp.toPx()
            val w = size.width - 2 * pad
            val h = size.height - 2 * pad
            val frameColor = if (isDark) Color(0x33FFFFFF) else Color(0x18000000)
            drawRoundRect(
                color = frameColor,
                topLeft = Offset(pad, pad),
                size = Size(w, h),
                cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx()),
                style = Stroke(width = 0.6.dp.toPx())
            )
        }

        // Top-Left Index (Rank & small Suit Icon)
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 2.5.dp, top = 2.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = formattedRank,
                color = suitColor,
                fontSize = rankSize,
                fontWeight = FontWeight.Black,
                fontFamily = fontFamily,
                lineHeight = rankLineHeight
            )
            SuitIconDisplay(
                suit = card.suit,
                style = suitStyle.style,
                color = suitColor,
                size = suitSize
            )
        }

        // Center Area (Illustrated Ace, Number pips, or Court Art)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            when {
                card.rank == Rank.ACE -> {
                    SuitIconDisplay(
                        suit = card.suit,
                        style = suitStyle.style,
                        color = suitColor,
                        size = 34.sp
                    )
                }
                card.rank in listOf(Rank.JACK, Rank.QUEEN, Rank.KING) -> {
                    CourtCardCenterView(card = card, cardFace = cardFace, suitColor = suitColor)
                }
                else -> {
                    NumberCardPipLayout(card = card, suitStyle = suitStyle.style, suitColor = suitColor)
                }
            }
        }

        // Bottom-Right Index (Rank & small Suit Icon rotated 180 degrees)
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .rotate(180f)
                .padding(start = 2.5.dp, top = 2.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = formattedRank,
                color = suitColor,
                fontSize = rankSize * 0.85f,
                fontWeight = FontWeight.Black,
                fontFamily = fontFamily,
                lineHeight = rankLineHeight * 0.85f
            )
            SuitIconDisplay(
                suit = card.suit,
                style = suitStyle.style,
                color = suitColor,
                size = suitSize * 0.85f
            )
        }
    }
}

@Composable
fun SuitIconDisplay(
    suit: Suit,
    style: SuitIconStyle,
    color: Color,
    size: androidx.compose.ui.unit.TextUnit
) {
    val symbol = suit.symbol
    when (style) {
        SuitIconStyle.OUTLINE -> {
            Text(
                text = symbol,
                color = color,
                fontSize = size,
                fontWeight = FontWeight.Light,
                textAlign = TextAlign.Center
            )
        }
        SuitIconStyle.THREE_D_EMBOSSED, SuitIconStyle.GOLD_FOIL -> {
            Text(
                text = symbol,
                color = color,
                fontSize = size,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )
        }
        SuitIconStyle.PIXEL -> {
            Text(
                text = symbol,
                color = color,
                fontSize = size,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center
            )
        }
        else -> {
            Text(
                text = symbol,
                color = color,
                fontSize = size,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun CourtCardCenterView(
    card: Card,
    cardFace: CardFaceTheme,
    suitColor: Color
) {
    val letter = when (card.rank) {
        Rank.JACK -> "J"
        Rank.QUEEN -> "Q"
        Rank.KING -> "K"
        else -> ""
    }
    val crownIcon = when (card.rank) {
        Rank.KING -> "👑"
        Rank.QUEEN -> "👸"
        Rank.JACK -> "⚔️"
        else -> ""
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = crownIcon, fontSize = 14.sp)
        Text(
            text = "$letter ${card.suit.symbol}",
            color = suitColor,
            fontSize = 18.sp,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun NumberCardPipLayout(
    card: Card,
    suitStyle: SuitIconStyle,
    suitColor: Color
) {
    val count = card.rank.value
    // Compact elegant pip distribution
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val rows = when {
            count <= 3 -> count
            count <= 6 -> 2
            count <= 8 -> 3
            else -> 4
        }
        val pipsPerRow = if (count <= 3) 1 else 2

        repeat(rows) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                repeat(pipsPerRow) {
                    SuitIconDisplay(
                        suit = card.suit,
                        style = suitStyle,
                        color = suitColor,
                        size = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun CardBackView(
    cardBack: CardBackTheme,
    customPhotoUri: String? = null
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(cardBack.baseColor)
            .padding(3.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val cx = w / 2f
            val cy = h / 2f

            // Frame border
            drawRoundRect(
                color = cardBack.accentColor.copy(alpha = 0.5f),
                topLeft = Offset(0f, 0f),
                size = Size(w, h),
                cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx()),
                style = Stroke(width = 1.2.dp.toPx())
            )

            when (cardBack.patternType) {
                CardBackPattern.GEOMETRIC_DIAMONDS -> {
                    val step = 8.dp.toPx()
                    var x = 0f
                    while (x < w + h) {
                        drawLine(color = cardBack.accentColor.copy(alpha = 0.25f), start = Offset(x, 0f), end = Offset(x - h, h), strokeWidth = 1.dp.toPx())
                        drawLine(color = cardBack.accentColor.copy(alpha = 0.25f), start = Offset(x - h, 0f), end = Offset(x, h), strokeWidth = 1.dp.toPx())
                        x += step
                    }
                    drawCircle(color = cardBack.accentColor, radius = minOf(w, h) * 0.2f, center = Offset(cx, cy), style = Stroke(width = 1.5.dp.toPx()))
                }
                CardBackPattern.MANDALA, CardBackPattern.ARABESQUE -> {
                    val r = minOf(w, h) * 0.35f
                    for (i in 0 until 8) {
                        val angle = Math.toRadians(i * 45.0)
                        val ox = cx + (r * 0.4f * cos(angle)).toFloat()
                        val oy = cy + (r * 0.4f * sin(angle)).toFloat()
                        drawCircle(color = cardBack.accentColor.copy(alpha = 0.35f), radius = r * 0.3f, center = Offset(ox, oy), style = Stroke(width = 1.dp.toPx()))
                    }
                    drawCircle(color = cardBack.accentColor, radius = r * 0.6f, center = Offset(cx, cy), style = Stroke(width = 1.5.dp.toPx()))
                }
                CardBackPattern.DRAGON -> {
                    val r = minOf(w, h) * 0.38f
                    for (angle in 0 until 12) {
                        val rad = Math.toRadians(angle * 30.0)
                        val ex = cx + (r * cos(rad)).toFloat()
                        val ey = cy + (r * sin(rad)).toFloat()
                        drawLine(color = cardBack.accentColor.copy(alpha = 0.4f), start = Offset(cx, cy), end = Offset(ex, ey), strokeWidth = 1.2.dp.toPx())
                    }
                    drawCircle(color = cardBack.accentColor, radius = r * 0.6f, center = Offset(cx, cy), style = Stroke(width = 2.dp.toPx()))
                }
                CardBackPattern.CIRCUIT_BOARD -> {
                    val step = 10.dp.toPx()
                    var y = step
                    while (y < h) {
                        drawLine(color = cardBack.accentColor.copy(alpha = 0.3f), start = Offset(0f, y), end = Offset(w, y), strokeWidth = 1.dp.toPx())
                        drawCircle(color = cardBack.accentColor, radius = 2.5.dp.toPx(), center = Offset(cx, y))
                        y += step
                    }
                }
                CardBackPattern.CHECKERBOARD -> {
                    val tileSize = 7.dp.toPx()
                    var x = 0f
                    var row = 0
                    while (x < w) {
                        var y = 0f
                        var col = 0
                        while (y < h) {
                            if ((row + col) % 2 == 0) {
                                drawRect(color = cardBack.accentColor.copy(alpha = 0.25f), topLeft = Offset(x, y), size = Size(tileSize, tileSize))
                            }
                            y += tileSize
                            col++
                        }
                        x += tileSize
                        row++
                    }
                }
                CardBackPattern.STARRY_NIGHT, CardBackPattern.GALAXY_SWIRL -> {
                    val r = minOf(w, h) * 0.35f
                    for (i in 0 until 16) {
                        val angle = Math.toRadians(i * 22.5)
                        val dist = (r * (0.2f + (i % 4) * 0.2f))
                        val sx = cx + (dist * cos(angle)).toFloat()
                        val sy = cy + (dist * sin(angle)).toFloat()
                        drawCircle(color = Color.White.copy(alpha = 0.8f), radius = 1.8.dp.toPx(), center = Offset(sx, sy))
                    }
                    drawCircle(color = cardBack.accentColor, radius = r * 0.5f, center = Offset(cx, cy), style = Stroke(width = 1.dp.toPx()))
                }
                CardBackPattern.ORNATE_CREST -> {
                    val r = minOf(w, h) * 0.35f
                    drawCircle(color = cardBack.accentColor, radius = r, center = Offset(cx, cy), style = Stroke(width = 1.6.dp.toPx()))
                    drawCircle(color = cardBack.accentColor.copy(alpha = 0.4f), radius = r * 0.7f, center = Offset(cx, cy))
                    drawLine(color = cardBack.accentColor, start = Offset(cx - r, cy), end = Offset(cx + r, cy), strokeWidth = 1.5.dp.toPx())
                    drawLine(color = cardBack.accentColor, start = Offset(cx, cy - r), end = Offset(cx, cy + r), strokeWidth = 1.5.dp.toPx())
                }
                else -> {
                    // Universal luxury lattice & central medallion for other patterns
                    val r = minOf(w, h) * 0.32f
                    drawCircle(color = cardBack.accentColor.copy(alpha = 0.35f), radius = r, center = Offset(cx, cy), style = Stroke(width = 1.4.dp.toPx()))
                    drawCircle(color = cardBack.accentColor.copy(alpha = 0.15f), radius = r * 0.6f, center = Offset(cx, cy))
                    drawCircle(color = cardBack.accentColor, radius = 3.dp.toPx(), center = Offset(cx, cy))
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
            .background(Color(0x12FFFFFF), RoundedCornerShape(8.dp))
            .border(
                1.dp,
                if (isValidTarget) Color(0xFF00E5FF) else Color(0x30FFFFFF),
                RoundedCornerShape(8.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (!iconSymbol.isNullOrEmpty()) {
                Text(
                    text = iconSymbol,
                    color = Color(0x40FFFFFF),
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
}
