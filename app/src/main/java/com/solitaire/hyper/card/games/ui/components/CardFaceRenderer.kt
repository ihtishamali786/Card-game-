package com.solitaire.hyper.card.games.ui.components

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import com.solitaire.hyper.card.games.game.model.Card
import com.solitaire.hyper.card.games.game.model.Rank
import com.solitaire.hyper.card.games.game.model.Suit
import com.solitaire.hyper.card.games.ui.customization.CardFaceStyle
import com.solitaire.hyper.card.games.ui.customization.CardFaceTheme
import com.solitaire.hyper.card.games.ui.customization.SuitColorScheme
import com.solitaire.hyper.card.games.ui.customization.SuitIconStyle
import com.solitaire.hyper.card.games.ui.customization.SuitStyleTheme
import java.util.concurrent.ConcurrentHashMap

/**
 * Data tokens defining the visual styling for a CardFaceTheme skin.
 * All skins share the EXACT same master layout ("Jumbo Easy-Read v2"):
 * - Ratio 360:520 (1 : 1.444).
 * - Corner radius: 12% of card width.
 * - Outer edge: ~2% of card width.
 * - ONE big suit icon only, centered, 55% of width at (50% W, 55% H).
 * - Bold rank only in top-left corner (center at 20% W, 11.5% H).
 * - Rotated 180° rank only in bottom-right (center at 80% W, 88.5% H).
 * - No small corner suits, no pips, no portraits.
 */
data class CardFaceTokens(
    val backgroundBrush: Brush,
    val edgeColor: Color,
    val edgeWidthFraction: Float = 0.022f,
    val innerFrameColor: Color? = null,
    val innerFrameWidthFraction: Float = 0.009f,
    val innerFramePaddingFraction: Float = 0.052f,
    val redColor: Color,
    val blackColor: Color,
    val diamondColor: Color? = null,
    val clubColor: Color? = null,
    val typeface: Typeface = Typeface.DEFAULT_BOLD,
    val hasGloss: Boolean = false,
    val hasGlow: Boolean = false,
    val glowColor: Color? = null,
    val isOutlineSuit: Boolean = false,
    val isPixel: Boolean = false,
    val cornerFanDecorations: Boolean = false,
    val scanlines: Boolean = false,
    val halftoneDots: Boolean = false,
    val floralAccent: Boolean = false,
    val arabesqueBorder: Boolean = false,
    val stars: Boolean = false,
    val marbleVeins: Boolean = false,
    val woodGrain: Boolean = false,
    val paperGrain: Boolean = false,
    val specularStreak: Boolean = false,
    val isDarkSurface: Boolean = false
)

object CardFaceRenderer {

    // Thread-safe in-memory pre-render cache (cardKey -> ImageBitmap)
    private val renderCache = ConcurrentHashMap<String, ImageBitmap>()

    val MASTER_TOKENS = CardFaceTokens(
        backgroundBrush = Brush.verticalGradient(listOf(Color(0xFFFFFFFF), Color(0xFFF3F5F9))),
        edgeColor = Color(0xFF3A4052),
        edgeWidthFraction = 0.022f,
        redColor = Color(0xFFE21C34),
        blackColor = Color(0xFF0E1018),
        hasGloss = true
    )

    fun clearCache() {
        renderCache.clear()
    }

    /**
     * Resolves token set for any CardFaceStyle.
     * All cards throughout the game exclusively use the official Solitaire Hyper Card Deck tokens.
     */
    fun getTokens(style: CardFaceStyle = CardFaceStyle.CLASSIC_STANDARD): CardFaceTokens = MASTER_TOKENS

    /**
     * Resolves effective suit color considering active scheme overrides.
     */
    fun resolveSuitColor(
        suit: Suit,
        tokens: CardFaceTokens = MASTER_TOKENS,
        scheme: SuitColorScheme? = null,
        isCustomSchemeActive: Boolean = false
    ): Color {
        return when (suit) {
            Suit.HEARTS, Suit.DIAMONDS -> tokens.redColor
            Suit.CLUBS, Suit.SPADES -> tokens.blackColor
        }
    }

    /**
     * Core renderer implementing the exact Section 2 layout:
     * - Height = Width * 1.444f
     * - Corner radius = 12% of width
     * - Edge = 2% of width
     * - Center suit: exactly 1 big icon centered at (50% W, 55% H), width = 55% of W
     * - Top-left rank: center at (20% W, 11.5% H), size = 33% of W (27% for "10", 40%/32% large print)
     * - Bottom-right rank: rotated 180° at (80% W, 88.5% H)
     */
    fun renderCardFace(
        drawScope: DrawScope,
        card: Card,
        tokens: CardFaceTokens,
        scheme: SuitColorScheme,
        suitStyle: SuitStyleTheme,
        isLargePrint: Boolean,
        numeralsStyle: String
    ) {
        with(drawScope) {
            val w = size.width
            val h = size.height
            val r = w * 0.12f // 12% width corner radius
            val edgeW = (w * tokens.edgeWidthFraction).coerceAtLeast(1f)

            // 1. Fill card background
            drawRoundRect(
                brush = tokens.backgroundBrush,
                topLeft = Offset.Zero,
                size = Size(w, h),
                cornerRadius = CornerRadius(r, r)
            )

            // 2. Subtle background textures (stars, marble, wood, scanlines, halftone)
            if (tokens.stars) {
                drawStars(this, w, h)
            } else if (tokens.marbleVeins) {
                drawMarbleVeins(this, w, h)
            } else if (tokens.woodGrain) {
                drawWoodGrain(this, w, h)
            } else if (tokens.scanlines) {
                drawScanlines(this, w, h)
            } else if (tokens.halftoneDots) {
                drawHalftoneDots(this, w, h)
            } else if (tokens.specularStreak) {
                drawSpecularStreak(this, w, h)
            }

            // 3. Optional inner decorative frame (e.g. Royal Gold, Casino, Mughal, Arabesque)
            tokens.innerFrameColor?.let { frameColor ->
                val pad = w * tokens.innerFramePaddingFraction
                val innerR = (r - pad).coerceAtLeast(2f)
                drawRoundRect(
                    color = frameColor,
                    topLeft = Offset(pad, pad),
                    size = Size(w - 2 * pad, h - 2 * pad),
                    cornerRadius = CornerRadius(innerR, innerR),
                    style = Stroke(width = (w * tokens.innerFrameWidthFraction).coerceAtLeast(0.8f))
                )
            }

            if (tokens.cornerFanDecorations) {
                drawCornerFanDecorations(this, w, h, tokens.edgeColor)
            }

            if (tokens.floralAccent) {
                drawFloralAccent(this, w, h, tokens.blackColor)
            }

            // 4. Outer edge stroke
            drawRoundRect(
                color = tokens.edgeColor,
                topLeft = Offset(edgeW / 2f, edgeW / 2f),
                size = Size(w - edgeW, h - edgeW),
                cornerRadius = CornerRadius(r, r),
                style = Stroke(width = edgeW)
            )

            // Resolve effective suit color
            val isCustomScheme = scheme.id != "SCHEME_STANDARD"
            val suitColor = resolveSuitColor(card.suit, tokens, scheme, isCustomScheme)

            // 5. ONE BIG CENTER SUIT ICON (Section 2: about 55% width, centered at 55% height)
            val suitCenter = Offset(w * 0.50f, h * 0.55f)
            val suitSize = w * 0.55f
            val isOutline = tokens.isOutlineSuit || suitStyle.style == SuitIconStyle.OUTLINE
            drawSuitIcon(
                drawScope = this,
                suit = card.suit,
                center = suitCenter,
                size = suitSize,
                color = suitColor,
                tokens = tokens,
                suitStyle = suitStyle.style,
                isOutline = isOutline
            )

            // 6. TOP-LEFT BOLD RANK ONLY (Center at 20% width, 11.5% height)
            val rankText = formatRank(card.rank, numeralsStyle)
            val isTen = card.rank == Rank.TEN
            val rankSizePx = when {
                isLargePrint && isTen -> w * 0.32f
                isLargePrint -> w * 0.40f
                isTen -> w * 0.27f
                else -> w * 0.33f
            }

            val rankCenterTopLeft = Offset(w * 0.20f, h * 0.115f)
            drawRankText(
                drawScope = this,
                text = rankText,
                center = rankCenterTopLeft,
                sizePx = rankSizePx,
                color = suitColor,
                typeface = tokens.typeface,
                rotationDegrees = 0f
            )

            // 7. BOTTOM-RIGHT BOLD RANK ONLY (Rotated 180°, center at 80% width, 88.5% height)
            val rankCenterBottomRight = Offset(w * 0.80f, h * 0.885f)
            drawRankText(
                drawScope = this,
                text = rankText,
                center = rankCenterBottomRight,
                sizePx = rankSizePx,
                color = suitColor,
                typeface = tokens.typeface,
                rotationDegrees = 180f
            )
        }
    }

    private fun drawRankText(
        drawScope: DrawScope,
        text: String,
        center: Offset,
        sizePx: Float,
        color: Color,
        typeface: Typeface,
        rotationDegrees: Float
    ) {
        drawScope.drawIntoCanvas { canvas ->
            val nativeCanvas = canvas.nativeCanvas
            val paint = Paint().apply {
                this.color = android.graphics.Color.argb(
                    (color.alpha * 255).toInt(),
                    (color.red * 255).toInt(),
                    (color.green * 255).toInt(),
                    (color.blue * 255).toInt()
                )
                this.textSize = sizePx
                this.typeface = typeface
                this.isAntiAlias = true
                this.isFakeBoldText = true
                this.textAlign = Paint.Align.CENTER
            }

            // Vertical metric centering
            val fontMetrics = paint.fontMetrics
            val baselineOffset = (fontMetrics.descent + fontMetrics.ascent) / 2f

            nativeCanvas.save()
            nativeCanvas.translate(center.x, center.y)
            if (rotationDegrees != 0f) {
                nativeCanvas.rotate(rotationDegrees)
            }
            nativeCanvas.drawText(text, 0f, -baselineOffset, paint)
            nativeCanvas.restore()
        }
    }

    private fun drawSuitIcon(
        drawScope: DrawScope,
        suit: Suit,
        center: Offset,
        size: Float,
        color: Color,
        tokens: CardFaceTokens,
        suitStyle: SuitIconStyle,
        isOutline: Boolean
    ) {
        val cx = center.x
        val cy = center.y
        val path = when (suit) {
            Suit.SPADES -> createSpadePath(cx, cy, size)
            Suit.HEARTS -> createHeartPath(cx, cy, size)
            Suit.DIAMONDS -> createDiamondPath(cx, cy, size)
            Suit.CLUBS -> createClubPath(cx, cy, size)
        }

        // Glow pass if enabled
        if (tokens.hasGlow && tokens.glowColor != null) {
            drawScope.drawPath(
                path = path,
                color = tokens.glowColor,
                style = Stroke(width = size * 0.12f)
            )
        }

        // Gradient / Solid brush calculation
        val suitBrush = when (suitStyle) {
            SuitIconStyle.GRADIENT -> Brush.verticalGradient(
                listOf(color, color.copy(alpha = 0.65f)),
                startY = cy - size * 0.5f,
                endY = cy + size * 0.5f
            )
            SuitIconStyle.GOLD_FOIL -> Brush.linearGradient(
                listOf(Color(0xFFFFDF7A), Color(0xFFD4AF37), Color(0xFFAA7C11), Color(0xFFFFDF7A)),
                start = Offset(cx - size * 0.4f, cy - size * 0.4f),
                end = Offset(cx + size * 0.4f, cy + size * 0.4f)
            )
            else -> Brush.verticalGradient(
                listOf(color, color.copy(alpha = 0.88f)),
                startY = cy - size * 0.5f,
                endY = cy + size * 0.5f
            )
        }

        if (isOutline) {
            drawScope.drawPath(
                path = path,
                color = color,
                style = Stroke(width = size * 0.065f) // Exactly 6% outline width
            )
        } else {
            // Fill
            drawScope.drawPath(path = path, brush = suitBrush)

            // Bevel for 3D embossed
            if (suitStyle == SuitIconStyle.THREE_D_EMBOSSED) {
                drawScope.drawPath(
                    path = path,
                    color = Color.White.copy(alpha = 0.35f),
                    style = Stroke(width = size * 0.03f)
                )
            }

            // Soft gloss highlight (matches master prompt reference cards)
            if (tokens.hasGloss && suitStyle != SuitIconStyle.GRADIENT) {
                val glossPath = createGlossPath(suit, cx, cy, size)
                drawScope.drawPath(
                    path = glossPath,
                    color = Color.White.copy(alpha = 0.28f)
                )
            }
        }
    }

    fun createHeartPath(cx: Float, cy: Float, size: Float): Path {
        val path = Path()
        val topCleftY = cy - size * 0.22f
        val bottomPointY = cy + size * 0.48f

        path.moveTo(cx, topCleftY)
        // Left lobe
        path.cubicTo(
            cx - size * 0.12f, cy - size * 0.48f,
            cx - size * 0.50f, cy - size * 0.40f,
            cx - size * 0.50f, cy - size * 0.08f
        )
        path.cubicTo(
            cx - size * 0.50f, cy + size * 0.18f,
            cx - size * 0.25f, cy + size * 0.34f,
            cx, bottomPointY
        )
        // Right lobe
        path.cubicTo(
            cx + size * 0.25f, cy + size * 0.34f,
            cx + size * 0.50f, cy + size * 0.18f,
            cx + size * 0.50f, cy - size * 0.08f
        )
        path.cubicTo(
            cx + size * 0.50f, cy - size * 0.40f,
            cx + size * 0.12f, cy - size * 0.48f,
            cx, topCleftY
        )
        path.close()
        return path
    }

    fun createSpadePath(cx: Float, cy: Float, size: Float): Path {
        val path = Path()
        val topApexY = cy - size * 0.48f
        val stemBottomY = cy + size * 0.48f
        val stemWidth = size * 0.34f
        val stemWaistY = cy + size * 0.20f

        path.moveTo(cx, topApexY)
        // Left shoulder down to lobe
        path.cubicTo(
            cx - size * 0.08f, cy - size * 0.28f,
            cx - size * 0.50f, cy - size * 0.04f,
            cx - size * 0.50f, cy + size * 0.12f
        )
        // Left bottom lobe curve in towards stem
        path.cubicTo(
            cx - size * 0.50f, cy + size * 0.30f,
            cx - size * 0.24f, cy + size * 0.30f,
            cx - size * 0.05f, stemWaistY
        )
        // Stem left flare
        path.cubicTo(
            cx - size * 0.06f, cy + size * 0.32f,
            cx - stemWidth * 0.5f, cy + size * 0.42f,
            cx - stemWidth * 0.5f, stemBottomY
        )
        // Stem bottom
        path.lineTo(cx + stemWidth * 0.5f, stemBottomY)
        // Stem right flare up
        path.cubicTo(
            cx + stemWidth * 0.5f, cy + size * 0.42f,
            cx + size * 0.06f, cy + size * 0.32f,
            cx + size * 0.05f, stemWaistY
        )
        // Right bottom lobe
        path.cubicTo(
            cx + size * 0.24f, cy + size * 0.30f,
            cx + size * 0.50f, cy + size * 0.30f,
            cx + size * 0.50f, cy + size * 0.12f
        )
        // Right shoulder up to apex
        path.cubicTo(
            cx + size * 0.50f, cy - size * 0.04f,
            cx + size * 0.08f, cy - size * 0.28f,
            cx, topApexY
        )
        path.close()
        return path
    }

    fun createDiamondPath(cx: Float, cy: Float, size: Float): Path {
        val path = Path()
        val topY = cy - size * 0.48f
        val bottomY = cy + size * 0.48f
        val rightX = cx + size * 0.38f
        val leftX = cx - size * 0.38f

        val curve = size * 0.035f
        path.moveTo(cx, topY)
        path.quadraticTo(cx + size * 0.19f - curve, cy - size * 0.24f + curve, rightX, cy)
        path.quadraticTo(cx + size * 0.19f - curve, cy + size * 0.24f - curve, cx, bottomY)
        path.quadraticTo(cx - size * 0.19f + curve, cy + size * 0.24f - curve, leftX, cy)
        path.quadraticTo(cx - size * 0.19f + curve, cy - size * 0.24f + curve, cx, topY)
        path.close()
        return path
    }

    fun createClubPath(cx: Float, cy: Float, size: Float): Path {
        val path = Path()
        val lobeR = size * 0.21f
        val stemBottomY = cy + size * 0.48f
        val stemWidth = size * 0.34f
        val stemWaistY = cy + size * 0.16f

        val topLobeCY = cy - size * 0.19f
        val leftLobeCX = cx - size * 0.22f
        val leftLobeCY = cy + size * 0.08f
        val rightLobeCX = cx + size * 0.22f
        val rightLobeCY = cy + size * 0.08f

        path.moveTo(cx, topLobeCY - lobeR)
        // Top lobe left
        path.cubicTo(
            cx - lobeR * 0.55f, topLobeCY - lobeR,
            cx - lobeR, topLobeCY - lobeR * 0.55f,
            cx - lobeR, topLobeCY
        )
        path.cubicTo(
            cx - lobeR, topLobeCY + lobeR * 0.65f,
            cx - size * 0.06f, cy - size * 0.02f,
            cx - size * 0.06f, cy - size * 0.02f
        )
        // Left lobe
        path.cubicTo(
            leftLobeCX + lobeR * 0.2f, leftLobeCY - lobeR,
            leftLobeCX - lobeR, leftLobeCY - lobeR * 0.7f,
            leftLobeCX - lobeR, leftLobeCY
        )
        path.cubicTo(
            leftLobeCX - lobeR, leftLobeCY + lobeR,
            leftLobeCX + lobeR * 0.3f, leftLobeCY + lobeR,
            cx - size * 0.06f, stemWaistY
        )
        // Stem left flare
        path.cubicTo(
            cx - size * 0.07f, cy + size * 0.30f,
            cx - stemWidth * 0.5f, cy + size * 0.42f,
            cx - stemWidth * 0.5f, stemBottomY
        )
        // Stem base
        path.lineTo(cx + stemWidth * 0.5f, stemBottomY)
        // Stem right flare up
        path.cubicTo(
            cx + stemWidth * 0.5f, cy + size * 0.42f,
            cx + size * 0.07f, cy + size * 0.30f,
            cx + size * 0.06f, stemWaistY
        )
        // Right lobe
        path.cubicTo(
            rightLobeCX - lobeR * 0.3f, rightLobeCY + lobeR,
            rightLobeCX + lobeR, rightLobeCY + lobeR,
            rightLobeCX + lobeR, rightLobeCY
        )
        path.cubicTo(
            rightLobeCX + lobeR, rightLobeCY - lobeR * 0.7f,
            rightLobeCX - lobeR * 0.2f, rightLobeCY - lobeR,
            cx + size * 0.06f, cy - size * 0.02f
        )
        // Top lobe right
        path.cubicTo(
            cx + size * 0.06f, cy - size * 0.02f,
            cx + lobeR, topLobeCY + lobeR * 0.65f,
            cx + lobeR, topLobeCY
        )
        path.cubicTo(
            cx + lobeR, topLobeCY - lobeR * 0.55f,
            cx + lobeR * 0.55f, topLobeCY - lobeR,
            cx, topLobeCY - lobeR
        )
        path.close()
        return path
    }

    private fun createGlossPath(suit: Suit, cx: Float, cy: Float, size: Float): Path {
        val path = Path()
        when (suit) {
            Suit.HEARTS -> {
                path.addOval(Rect(cx - size * 0.38f, cy - size * 0.38f, cx - size * 0.14f, cy - size * 0.16f))
            }
            Suit.SPADES -> {
                path.moveTo(cx - size * 0.08f, cy - size * 0.36f)
                path.cubicTo(cx - size * 0.28f, cy - size * 0.18f, cx - size * 0.36f, cy + size * 0.05f, cx - size * 0.22f, cy + size * 0.18f)
                path.cubicTo(cx - size * 0.30f, cy + size * 0.05f, cx - size * 0.22f, cy - size * 0.15f, cx - size * 0.08f, cy - size * 0.36f)
                path.close()
            }
            Suit.DIAMONDS -> {
                path.moveTo(cx, cy - size * 0.40f)
                path.lineTo(cx - size * 0.28f, cy)
                path.lineTo(cx - size * 0.14f, cy - size * 0.10f)
                path.lineTo(cx, cy - size * 0.22f)
                path.close()
            }
            Suit.CLUBS -> {
                val topLobeCY = cy - size * 0.19f
                path.addOval(Rect(cx - size * 0.16f, topLobeCY - size * 0.16f, cx - size * 0.02f, topLobeCY - size * 0.04f))
            }
        }
        return path
    }

    // --- Texture drawing helpers ---

    private fun drawStars(scope: DrawScope, w: Float, h: Float) {
        val starPaint = Color.White.copy(alpha = 0.55f)
        val offsets = listOf(
            Offset(w * 0.3f, h * 0.3f), Offset(w * 0.7f, h * 0.25f),
            Offset(w * 0.2f, h * 0.7f), Offset(w * 0.8f, h * 0.65f),
            Offset(w * 0.45f, h * 0.82f), Offset(w * 0.6f, h * 0.4f)
        )
        for (offset in offsets) {
            scope.drawCircle(color = starPaint, radius = 1.2f, center = offset)
        }
    }

    private fun drawMarbleVeins(scope: DrawScope, w: Float, h: Float) {
        val veinColor = Color(0x18000000)
        val path = Path().apply {
            moveTo(w * 0.1f, 0f)
            cubicTo(w * 0.3f, h * 0.35f, w * 0.2f, h * 0.65f, w * 0.5f, h)
            moveTo(w * 0.7f, 0f)
            cubicTo(w * 0.85f, h * 0.45f, w * 0.65f, h * 0.75f, w * 0.9f, h)
        }
        scope.drawPath(path = path, color = veinColor, style = Stroke(width = 0.8f))
    }

    private fun drawWoodGrain(scope: DrawScope, w: Float, h: Float) {
        val grainColor = Color(0x12000000)
        var y = h * 0.1f
        while (y < h * 0.95f) {
            scope.drawLine(color = grainColor, start = Offset(0f, y), end = Offset(w, y), strokeWidth = 1f)
            y += h * 0.07f
        }
    }

    private fun drawScanlines(scope: DrawScope, w: Float, h: Float) {
        val lineCol = Color(0x1000E5BE)
        var y = 0f
        while (y < h) {
            scope.drawLine(color = lineCol, start = Offset(0f, y), end = Offset(w, y), strokeWidth = 0.8f)
            y += 4f
        }
    }

    private fun drawHalftoneDots(scope: DrawScope, w: Float, h: Float) {
        val dotCol = Color(0x12000000)
        var x = w * 0.15f
        while (x < w * 0.85f) {
            var y = h * 0.15f
            while (y < h * 0.85f) {
                scope.drawCircle(color = dotCol, radius = 1f, center = Offset(x, y))
                y += 9f
            }
            x += 9f
        }
    }

    private fun drawSpecularStreak(scope: DrawScope, w: Float, h: Float) {
        val streakBrush = Brush.linearGradient(
            listOf(Color.Transparent, Color(0x28FFFFFF), Color.Transparent),
            start = Offset(0f, 0f),
            end = Offset(w, h * 0.6f)
        )
        scope.drawRect(brush = streakBrush)
    }

    private fun drawCornerFanDecorations(scope: DrawScope, w: Float, h: Float, goldColor: Color) {
        val fanR = w * 0.14f
        val stroke = Stroke(width = 0.8f)
        // Top right fan
        scope.drawArc(color = goldColor.copy(alpha = 0.35f), startAngle = 90f, sweepAngle = 90f, useCenter = false, topLeft = Offset(w - 2 * fanR, 0f), size = Size(2 * fanR, 2 * fanR), style = stroke)
        // Bottom left fan
        scope.drawArc(color = goldColor.copy(alpha = 0.35f), startAngle = 270f, sweepAngle = 90f, useCenter = false, topLeft = Offset(0f, h - 2 * fanR), size = Size(2 * fanR, 2 * fanR), style = stroke)
    }

    private fun drawFloralAccent(scope: DrawScope, w: Float, h: Float, accentColor: Color) {
        val leafY = h * 0.94f
        val leafX = w * 0.50f
        scope.drawCircle(color = accentColor.copy(alpha = 0.4f), radius = 2.5f, center = Offset(leafX - 6f, leafY))
        scope.drawCircle(color = accentColor.copy(alpha = 0.4f), radius = 2.5f, center = Offset(leafX + 6f, leafY))
        scope.drawCircle(color = accentColor.copy(alpha = 0.4f), radius = 3.0f, center = Offset(leafX, leafY - 4f))
    }

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
}
