package com.solitaire.hyper.card.games.ui.components

import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import com.solitaire.hyper.card.games.game.model.Card
import com.solitaire.hyper.card.games.game.model.Rank
import com.solitaire.hyper.card.games.game.model.Suit
import com.solitaire.hyper.card.games.ui.customization.CardFaceStyle
import com.solitaire.hyper.card.games.ui.customization.CardFaceTheme

/**
 * Procedural renderer for REAL-LOOKING playing cards (no images, no fonts to download).
 *
 * STANDARD / MODERN_CLEAN : casino-style card
 *   - corner index (rank + small suit) top-left and the same index rotated 180° bottom-right
 *   - A  = one big suit in the center
 *   - 2-10 = true pip layouts, lower-half pips rotated 180° like a printed deck
 *   - J/Q/K = framed court card with a mirrored figure (crown / tiara / cap) and a small suit
 * JUMBO_EASY_READ (v2) : white card, huge rank letters in the corners, ONE big suit in the center,
 *   no small corner suits. Also used when Large-Print mode is on.
 *
 * Card aspect ratio expected: height = width * 1.4
 */
object CardFaceRenderer {

    private val RED = Color(0xFFD32F2F)
    private val BLACK = Color(0xFF111827)
    private val GOLD = Color(0xFFF5B301)
    private val OUTLINE = Color(0xFF1F2937)
    private val SKIN = Color(0xFFFFD9B3)
    private val BLUE = Color(0xFF1E3A8A)
    private val DARK_RED = Color(0xFFB91C1C)
    private val COURT_BG = Color(0xFFFFFBEB)

    private val textPaint = Paint().apply {
        isAntiAlias = true
        textAlign = Paint.Align.CENTER
    }
    private val bounds = Rect()

    // ------------------------------------------------------------------ public entry point

    fun renderCardFace(
        drawScope: DrawScope,
        card: Card,
        cardFace: CardFaceTheme,
        isLargePrint: Boolean = false
    ) {
        with(drawScope) {
            val w = size.width
            val h = size.height
            val corner = w * 0.09f
            val outlineW = (w * 0.012f).coerceAtLeast(1f)

            // 1. Card surface + thin printed edge
            drawRoundRect(
                color = cardFace.surfaceColor,
                size = Size(w, h),
                cornerRadius = CornerRadius(corner, corner)
            )
            drawRoundRect(
                color = Color(0xFF6B7280).copy(alpha = 0.45f),
                topLeft = Offset(outlineW / 2f, outlineW / 2f),
                size = Size(w - outlineW, h - outlineW),
                cornerRadius = CornerRadius(corner, corner),
                style = Stroke(width = outlineW)
            )

            val suitColor = if (card.suit.isRed) RED else BLACK
            val rank = card.rank
            val rankStr = rank.symbol
            val isJumbo = cardFace.style == CardFaceStyle.JUMBO_EASY_READ || isLargePrint
            val sans = cardFace.style == CardFaceStyle.MODERN_CLEAN

            if (isJumbo) {
                drawJumbo(card, suitColor, w, h)
                return
            }

            // 2. Corner index (top-left) and identical index rotated 180 degrees (bottom-right)
            drawCornerIndex(rankStr, card.suit, suitColor, w, h, sans)
            rotate(180f, Offset(w / 2f, h / 2f)) {
                drawCornerIndex(rankStr, card.suit, suitColor, w, h, sans)
            }

            // 3. Center artwork
            when {
                rank == Rank.ACE -> {
                    val big = if (card.suit == Suit.SPADES) w * 0.34f else w * 0.30f
                    drawSuit(this, card.suit, suitColor, w * 0.5f, h * 0.5f, big)
                }
                rank.value in 2..10 -> drawPips(card.suit, suitColor, rank.value, w, h)
                else -> drawCourtCard(card, suitColor, w, h)
            }
        }
    }

    // ------------------------------------------------------------------ corner index & jumbo

    private fun DrawScope.drawCornerIndex(
        rankStr: String,
        suit: Suit,
        color: Color,
        w: Float,
        h: Float,
        sans: Boolean
    ) {
        val fontPx = if (rankStr.length > 1) w * 0.25f else w * 0.28f
        drawRankText(rankStr, color, w * 0.16f, h * 0.04f, fontPx, sans)
        // small suit sits under the rank, inside the top 30% so it stays visible in tableau stacks
        drawSuit(this, suit, color, w * 0.16f, h * 0.235f, w * 0.068f)
    }

    private fun DrawScope.drawJumbo(card: Card, suitColor: Color, w: Float, h: Float) {
        val rankStr = card.rank.symbol
        val fontPx = if (rankStr.length > 1) w * 0.34f else w * 0.40f
        drawRankText(rankStr, suitColor, w * 0.21f, h * 0.035f, fontPx, false)
        rotate(180f, Offset(w / 2f, h / 2f)) {
            drawRankText(rankStr, suitColor, w * 0.21f, h * 0.035f, fontPx, false)
        }
        drawSuit(this, card.suit, suitColor, w * 0.5f, h * 0.55f, w * 0.34f)
    }

    private fun DrawScope.drawRankText(
        text: String,
        color: Color,
        cx: Float,
        topY: Float,
        sizePx: Float,
        sans: Boolean
    ) {
        val p = textPaint
        p.color = color.toArgb()
        p.textSize = sizePx
        p.typeface = Typeface.create(if (sans) Typeface.SANS_SERIF else Typeface.SERIF, Typeface.BOLD)
        p.textScaleX = if (text.length > 1) 0.82f else 1f // keep "10" narrow so it never touches the edge
        p.getTextBounds(text, 0, text.length, bounds)
        drawIntoCanvas { it.nativeCanvas.drawText(text, cx, topY - bounds.top, p) }
    }

    // ------------------------------------------------------------------ pips 2..10

    private const val L = 0.36f
    private const val C = 0.50f
    private const val R = 0.64f

    private fun pipLayout(v: Int): List<Pair<Float, Float>> {
        val six = listOf(L to 0.22f, R to 0.22f, L to 0.50f, R to 0.50f, L to 0.78f, R to 0.78f)
        val four = listOf(L to 0.22f, R to 0.22f, L to 0.407f, R to 0.407f, L to 0.593f, R to 0.593f, L to 0.78f, R to 0.78f)
        return when (v) {
            2 -> listOf(C to 0.22f, C to 0.78f)
            3 -> listOf(C to 0.22f, C to 0.50f, C to 0.78f)
            4 -> listOf(L to 0.22f, R to 0.22f, L to 0.78f, R to 0.78f)
            5 -> listOf(L to 0.22f, R to 0.22f, C to 0.50f, L to 0.78f, R to 0.78f)
            6 -> six
            7 -> six + (C to 0.36f)
            8 -> six + (C to 0.36f) + (C to 0.64f)
            9 -> four + (C to 0.50f)
            else -> four + (C to 0.315f) + (C to 0.685f)
        }
    }

    private fun DrawScope.drawPips(suit: Suit, color: Color, value: Int, w: Float, h: Float) {
        val s = w * 0.115f
        for ((fx, fy) in pipLayout(value)) {
            val cx = w * fx
            val cy = h * fy
            if (fy > 0.5f) {
                rotate(180f, Offset(cx, cy)) { drawSuit(this, suit, color, cx, cy, s) }
            } else {
                drawSuit(this, suit, color, cx, cy, s)
            }
        }
    }

    // ------------------------------------------------------------------ court cards J Q K

    private fun DrawScope.drawCourtCard(card: Card, suitColor: Color, w: Float, h: Float) {
        val fx0 = w * 0.25f
        val fx1 = w * 0.75f
        val fy0 = h * 0.11f
        val fy1 = h * 0.89f
        val sw = (w * 0.010f).coerceAtLeast(0.7f)

        drawRect(COURT_BG, Offset(fx0, fy0), Size(fx1 - fx0, fy1 - fy0))
        drawRect(OUTLINE.copy(alpha = 0.55f), Offset(fx0, fy0), Size(fx1 - fx0, fy1 - fy0), style = Stroke(sw))

        val halfH = (fy1 - fy0) / 2f
        drawCourtHalf(card.rank, card.suit, suitColor, fx0, fy0, fx1 - fx0, halfH, sw)
        rotate(180f, Offset(w / 2f, h / 2f)) {
            drawCourtHalf(card.rank, card.suit, suitColor, fx0, fy0, fx1 - fx0, halfH, sw)
        }
        drawLine(OUTLINE.copy(alpha = 0.55f), Offset(fx0, h / 2f), Offset(fx1, h / 2f), sw)
    }

    /** Draws the upper half of a court figure inside the box (x, y, fw, fh). The lower half is this rotated 180°. */
    private fun DrawScope.drawCourtHalf(
        rank: Rank,
        suit: Suit,
        suitColor: Color,
        x: Float,
        y: Float,
        fw: Float,
        fh: Float,
        sw: Float
    ) {
        val cx = x + fw / 2f
        val robe = if (suit.isRed) DARK_RED else BLUE
        val accent = if (suit.isRed) BLUE else DARK_RED

        // Robe
        val body = Path().apply {
            moveTo(x + fw * 0.06f, y + fh)
            lineTo(x + fw * 0.14f, y + fh * 0.66f)
            quadraticTo(cx, y + fh * 0.50f, x + fw * 0.86f, y + fh * 0.66f)
            lineTo(x + fw * 0.94f, y + fh)
            close()
        }
        drawPath(body, robe)
        drawPath(body, OUTLINE, style = Stroke(sw))
        // Gold sash and collar
        drawLine(GOLD, Offset(x + fw * 0.24f, y + fh * 0.68f), Offset(x + fw * 0.72f, y + fh * 0.98f), fw * 0.09f)
        drawLine(accent, Offset(x + fw * 0.30f, y + fh * 0.66f), Offset(x + fw * 0.78f, y + fh * 0.95f), fw * 0.03f)

        // Head
        val hr = fw * 0.17f
        val hy = y + fh * 0.38f
        drawCircle(SKIN, hr, Offset(cx, hy))
        drawCircle(OUTLINE, hr, Offset(cx, hy), style = Stroke(sw))
        val eye = (fw * 0.014f).coerceAtLeast(0.6f)
        drawCircle(OUTLINE, eye, Offset(cx - hr * 0.38f, hy - hr * 0.05f))
        drawCircle(OUTLINE, eye, Offset(cx + hr * 0.38f, hy - hr * 0.05f))
        drawLine(DARK_RED, Offset(cx - hr * 0.22f, hy + hr * 0.42f), Offset(cx + hr * 0.22f, hy + hr * 0.42f), sw)

        when (rank) {
            Rank.KING -> {
                val beard = Path().apply {
                    moveTo(cx - hr * 0.95f, hy + hr * 0.15f)
                    quadraticTo(cx - hr * 0.9f, hy + hr * 1.9f, cx, hy + hr * 1.75f)
                    quadraticTo(cx + hr * 0.9f, hy + hr * 1.9f, cx + hr * 0.95f, hy + hr * 0.15f)
                    quadraticTo(cx, hy + hr * 0.75f, cx - hr * 0.95f, hy + hr * 0.15f)
                    close()
                }
                drawPath(beard, Color(0xFFE5E7EB))
                drawPath(beard, OUTLINE, style = Stroke(sw))
                drawCrown(cx, hy - hr * 0.85f, hr * 1.1f, hr * 0.55f, 3, sw)
            }
            Rank.QUEEN -> {
                // long hair on both sides
                val hair = Color(0xFF7C4A1E)
                drawOval(hair, Offset(cx - hr * 1.55f, hy - hr * 0.2f), Size(hr * 0.8f, hr * 2.0f))
                drawOval(hair, Offset(cx + hr * 0.75f, hy - hr * 0.2f), Size(hr * 0.8f, hr * 2.0f))
                drawCircle(SKIN, hr, Offset(cx, hy))
                drawCircle(OUTLINE, hr, Offset(cx, hy), style = Stroke(sw))
                drawCircle(OUTLINE, eye, Offset(cx - hr * 0.38f, hy - hr * 0.05f))
                drawCircle(OUTLINE, eye, Offset(cx + hr * 0.38f, hy - hr * 0.05f))
                drawLine(DARK_RED, Offset(cx - hr * 0.22f, hy + hr * 0.42f), Offset(cx + hr * 0.22f, hy + hr * 0.42f), sw)
                drawCrown(cx, hy - hr * 0.85f, hr * 1.0f, hr * 0.5f, 5, sw)
            }
            else -> { // JACK
                val cap = Path().apply {
                    moveTo(cx - hr * 1.05f, hy - hr * 0.15f)
                    quadraticTo(cx, hy - hr * 2.0f, cx + hr * 1.05f, hy - hr * 0.15f)
                    close()
                }
                drawPath(cap, accent)
                drawPath(cap, OUTLINE, style = Stroke(sw))
                drawLine(GOLD, Offset(cx + hr * 0.45f, hy - hr * 1.25f), Offset(cx + hr * 1.5f, hy - hr * 2.05f), sw * 2f)
                drawCircle(Color(0xFF7C4A1E), hr * 0.28f, Offset(cx - hr * 1.0f, hy + hr * 0.1f))
                drawCircle(Color(0xFF7C4A1E), hr * 0.28f, Offset(cx + hr * 1.0f, hy + hr * 0.1f))
            }
        }

        // Small suit mark in the frame corner so the suit is obvious at a glance
        drawSuit(this, suit, suitColor, x + fw * 0.16f, y + fh * 0.16f, fw * 0.09f)
    }

    private fun DrawScope.drawCrown(cx: Float, baseY: Float, halfW: Float, height: Float, spikes: Int, sw: Float) {
        val path = Path().apply {
            moveTo(cx - halfW, baseY)
            val step = (halfW * 2f) / spikes
            for (i in 0 until spikes) {
                val x0 = cx - halfW + step * i
                lineTo(x0 + step * 0.5f, baseY - height)
                lineTo(x0 + step, baseY)
            }
            close()
        }
        drawPath(path, GOLD)
        drawPath(path, OUTLINE, style = Stroke(sw))
        drawRect(DARK_RED, Offset(cx - halfW, baseY), Size(halfW * 2f, height * 0.28f))
    }

    // ------------------------------------------------------------------ suit symbols (also used by WinningCascadeCanvas)

    /** radius = half the symbol width. Symbol is centered on (cx, cy). */
    fun drawSuit(
        scope: DrawScope,
        suit: Suit,
        color: Color,
        cx: Float,
        cy: Float,
        radius: Float
    ) {
        val s = radius
        fun p(x: Float, y: Float) = Offset(cx + x * s, cy + y * s)
        with(scope) {
            when (suit) {
                Suit.HEARTS -> {
                    val oy = -0.07f
                    val path = Path().apply {
                        moveTo(cx, cy + (-0.45f + oy) * s)
                        cubicTo(p(0f, -0.95f + oy).x, p(0f, -0.95f + oy).y, p(-1f, -0.95f + oy).x, p(-1f, -0.95f + oy).y, p(-1f, -0.35f + oy).x, p(-1f, -0.35f + oy).y)
                        cubicTo(p(-1f, 0.15f + oy).x, p(-1f, 0.15f + oy).y, p(-0.4f, 0.5f + oy).x, p(-0.4f, 0.5f + oy).y, p(0f, 0.95f + oy).x, p(0f, 0.95f + oy).y)
                        cubicTo(p(0.4f, 0.5f + oy).x, p(0.4f, 0.5f + oy).y, p(1f, 0.15f + oy).x, p(1f, 0.15f + oy).y, p(1f, -0.35f + oy).x, p(1f, -0.35f + oy).y)
                        cubicTo(p(1f, -0.95f + oy).x, p(1f, -0.95f + oy).y, p(0f, -0.95f + oy).x, p(0f, -0.95f + oy).y, p(0f, -0.45f + oy).x, p(0f, -0.45f + oy).y)
                        close()
                    }
                    drawPath(path, color)
                }
                Suit.DIAMONDS -> {
                    val path = Path().apply {
                        moveTo(cx, cy - s * 1.1f)
                        lineTo(cx + s * 0.72f, cy)
                        lineTo(cx, cy + s * 1.1f)
                        lineTo(cx - s * 0.72f, cy)
                        close()
                    }
                    drawPath(path, color)
                }
                Suit.SPADES -> {
                    val oy = -0.02f
                    val path = Path().apply {
                        moveTo(cx, cy + (-1.05f + oy) * s)
                        cubicTo(p(0.25f, -0.6f + oy).x, p(0.25f, -0.6f + oy).y, p(1f, -0.25f + oy).x, p(1f, -0.25f + oy).y, p(1f, 0.3f + oy).x, p(1f, 0.3f + oy).y)
                        cubicTo(p(1f, 0.75f + oy).x, p(1f, 0.75f + oy).y, p(0.45f, 0.9f + oy).x, p(0.45f, 0.9f + oy).y, p(0.12f, 0.55f + oy).x, p(0.12f, 0.55f + oy).y)
                        cubicTo(p(0.15f, 0.8f + oy).x, p(0.15f, 0.8f + oy).y, p(0.25f, 0.95f + oy).x, p(0.25f, 0.95f + oy).y, p(0.45f, 1.05f + oy).x, p(0.45f, 1.05f + oy).y)
                        lineTo(cx - 0.45f * s, cy + (1.05f + oy) * s)
                        cubicTo(p(-0.25f, 0.95f + oy).x, p(-0.25f, 0.95f + oy).y, p(-0.15f, 0.8f + oy).x, p(-0.15f, 0.8f + oy).y, p(-0.12f, 0.55f + oy).x, p(-0.12f, 0.55f + oy).y)
                        cubicTo(p(-0.45f, 0.9f + oy).x, p(-0.45f, 0.9f + oy).y, p(-1f, 0.75f + oy).x, p(-1f, 0.75f + oy).y, p(-1f, 0.3f + oy).x, p(-1f, 0.3f + oy).y)
                        cubicTo(p(-1f, -0.25f + oy).x, p(-1f, -0.25f + oy).y, p(-0.25f, -0.6f + oy).x, p(-0.25f, -0.6f + oy).y, cx, cy + (-1.05f + oy) * s)
                        close()
                    }
                    drawPath(path, color)
                }
                Suit.CLUBS -> {
                    val r = s * 0.42f
                    drawCircle(color, r, p(0f, -0.52f))
                    drawCircle(color, r, p(-0.55f, 0.22f))
                    drawCircle(color, r, p(0.55f, 0.22f))
                    drawCircle(color, s * 0.30f, p(0f, 0.02f))
                    val stem = Path().apply {
                        moveTo(cx, cy + 0.05f * s)
                        cubicTo(p(0.05f, 0.6f).x, p(0.05f, 0.6f).y, p(0.15f, 0.85f).x, p(0.15f, 0.85f).y, p(0.4f, 1.05f).x, p(0.4f, 1.05f).y)
                        lineTo(cx - 0.4f * s, cy + 1.05f * s)
                        cubicTo(p(-0.15f, 0.85f).x, p(-0.15f, 0.85f).y, p(-0.05f, 0.6f).x, p(-0.05f, 0.6f).y, cx, cy + 0.05f * s)
                        close()
                    }
                    drawPath(stem, color)
                }
            }
        }
    }
}
