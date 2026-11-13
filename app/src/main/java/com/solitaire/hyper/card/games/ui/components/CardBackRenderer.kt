package com.solitaire.hyper.card.games.ui.components

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.solitaire.hyper.card.games.ui.customization.CardBackPattern
import com.solitaire.hyper.card.games.ui.customization.CardBackTheme
import kotlin.math.cos
import kotlin.math.sin

/**
 * High-performance vector renderer for Solitaire card backs.
 */
object CardBackRenderer {

    fun renderCardBack(
        drawScope: DrawScope,
        cardBack: CardBackTheme
    ) {
        with(drawScope) {
            val w = size.width
            val h = size.height
            val r = w * 0.12f
            val whiteEdgeW = (w * 0.038f).coerceAtLeast(1.5f)
            val outlineW = (w * 0.015f).coerceAtLeast(1f)

            // 1. Crisp white outer card edge
            drawRoundRect(
                color = Color.White,
                topLeft = Offset.Zero,
                size = Size(w, h),
                cornerRadius = CornerRadius(r, r)
            )

            // 2. Subtle slate card border
            drawRoundRect(
                color = Color(0xFF334155),
                topLeft = Offset(outlineW / 2f, outlineW / 2f),
                size = Size(w - outlineW, h - outlineW),
                cornerRadius = CornerRadius(r, r),
                style = Stroke(width = outlineW)
            )

            // 3. Inner base card color
            val innerTopLeft = Offset(whiteEdgeW, whiteEdgeW)
            val innerSize = Size(w - 2 * whiteEdgeW, h - 2 * whiteEdgeW)
            val innerR = (r - whiteEdgeW).coerceAtLeast(2f)

            drawRoundRect(
                color = cardBack.baseColor,
                topLeft = innerTopLeft,
                size = innerSize,
                cornerRadius = CornerRadius(innerR, innerR)
            )

            // 4. Gold / accent inner filigree frame
            val framePad = whiteEdgeW + w * 0.03f
            val frameR = (innerR - w * 0.03f).coerceAtLeast(2f)
            drawRoundRect(
                color = cardBack.accentColor.copy(alpha = 0.75f),
                topLeft = Offset(framePad, framePad),
                size = Size(w - 2 * framePad, h - 2 * framePad),
                cornerRadius = CornerRadius(frameR, frameR),
                style = Stroke(width = (w * 0.015f).coerceAtLeast(1f))
            )

            val cx = w / 2f
            val cy = h / 2f
            val accent = cardBack.accentColor

            // 5. Pattern Rendering
            when (cardBack.patternType) {
                CardBackPattern.ROYAL_SAPPHIRE -> {
                    drawDiamondsPattern(this, w, h, accent)
                    drawCenterMedallion(this, cx, cy, w * 0.22f, accent, isSpade = true)
                }
                CardBackPattern.IMPERIAL_CRIMSON -> {
                    drawLattice(this, w, h, accent)
                    drawCenterMedallion(this, cx, cy, w * 0.20f, accent, isSpade = false)
                }
                CardBackPattern.ZEN_EMERALD -> {
                    drawZenMandala(this, cx, cy, w * 0.32f, accent)
                }
                CardBackPattern.MIDNIGHT_GOLD -> {
                    drawGeometricLines(this, w, h, accent)
                    drawCenterMedallion(this, cx, cy, w * 0.22f, accent, isSpade = true)
                }
                CardBackPattern.SAKURA_BLOSSOM -> {
                    drawFloralWaves(this, w, h, accent)
                    drawCenterBlossom(this, cx, cy, w * 0.24f, accent)
                }
                CardBackPattern.CELESTIAL_STARS -> {
                    drawStarsGrid(this, w, h, accent)
                    drawCenterStar(this, cx, cy, w * 0.22f, accent)
                }
                CardBackPattern.HERITAGE_WOOD -> {
                    drawWoodgrain(this, w, h, accent)
                    drawCenterMedallion(this, cx, cy, w * 0.20f, accent, isSpade = false)
                }
                CardBackPattern.JOKER_AMETHYST -> {
                    drawDiamondsPattern(this, w, h, accent)
                    drawJokerHat(this, cx, cy, w * 0.26f, accent)
                }
            }
        }
    }

    private fun drawDiamondsPattern(scope: DrawScope, w: Float, h: Float, color: Color) {
        val step = w * 0.16f
        var y = step / 2f
        while (y < h) {
            var x = step / 2f
            while (x < w) {
                val path = Path().apply {
                    moveTo(x, y - step * 0.35f)
                    lineTo(x + step * 0.25f, y)
                    lineTo(x, y + step * 0.35f)
                    lineTo(x - step * 0.25f, y)
                    close()
                }
                scope.drawPath(path, color.copy(alpha = 0.22f), style = Stroke(width = 1f))
                x += step
            }
            y += step
        }
    }

    private fun drawLattice(scope: DrawScope, w: Float, h: Float, color: Color) {
        val step = w * 0.18f
        var x = 0f
        while (x < w + h) {
            scope.drawLine(
                color = color.copy(alpha = 0.18f),
                start = Offset(x, 0f),
                end = Offset(x - h, h),
                strokeWidth = 1f
            )
            scope.drawLine(
                color = color.copy(alpha = 0.18f),
                start = Offset(x - h, 0f),
                end = Offset(x, h),
                strokeWidth = 1f
            )
            x += step
        }
    }

    private fun drawZenMandala(scope: DrawScope, cx: Float, cy: Float, radius: Float, color: Color) {
        scope.drawCircle(color = color.copy(alpha = 0.25f), radius = radius, center = Offset(cx, cy), style = Stroke(width = 1.5f))
        scope.drawCircle(color = color.copy(alpha = 0.35f), radius = radius * 0.65f, center = Offset(cx, cy), style = Stroke(width = 1.5f))
        val petals = 8
        for (i in 0 until petals) {
            val angle = (i * (2 * Math.PI / petals)).toFloat()
            val px = cx + cos(angle) * (radius * 0.5f)
            val py = cy + sin(angle) * (radius * 0.5f)
            scope.drawCircle(color = color.copy(alpha = 0.35f), radius = radius * 0.35f, center = Offset(px, py), style = Stroke(width = 1.2f))
        }
    }

    private fun drawGeometricLines(scope: DrawScope, w: Float, h: Float, color: Color) {
        val step = w * 0.14f
        var y = 0f
        while (y < h) {
            scope.drawLine(
                color = color.copy(alpha = 0.2f),
                start = Offset(w * 0.1f, y),
                end = Offset(w * 0.9f, y),
                strokeWidth = 1f
            )
            y += step
        }
    }

    private fun drawFloralWaves(scope: DrawScope, w: Float, h: Float, color: Color) {
        val step = w * 0.2f
        var y = step
        while (y < h - step) {
            scope.drawCircle(
                color = color.copy(alpha = 0.22f),
                radius = step * 0.4f,
                center = Offset(w * 0.3f, y),
                style = Stroke(width = 1.2f)
            )
            scope.drawCircle(
                color = color.copy(alpha = 0.22f),
                radius = step * 0.4f,
                center = Offset(w * 0.7f, y),
                style = Stroke(width = 1.2f)
            )
            y += step
        }
    }

    private fun drawStarsGrid(scope: DrawScope, w: Float, h: Float, color: Color) {
        val step = w * 0.22f
        var y = step
        while (y < h - step) {
            var x = step
            while (x < w - step) {
                scope.drawCircle(color = color.copy(alpha = 0.35f), radius = 2f, center = Offset(x, y))
                x += step
            }
            y += step
        }
    }

    private fun drawWoodgrain(scope: DrawScope, w: Float, h: Float, color: Color) {
        val step = w * 0.12f
        var y = 0f
        while (y < h) {
            scope.drawLine(
                color = color.copy(alpha = 0.16f),
                start = Offset(0f, y),
                end = Offset(w, y + step * 0.5f),
                strokeWidth = 1.5f
            )
            y += step
        }
    }

    private fun drawCenterMedallion(scope: DrawScope, cx: Float, cy: Float, radius: Float, color: Color, isSpade: Boolean) {
        scope.drawCircle(color = color.copy(alpha = 0.2f), radius = radius, center = Offset(cx, cy))
        scope.drawCircle(color = color, radius = radius, center = Offset(cx, cy), style = Stroke(width = 1.8f))
        scope.drawCircle(color = color.copy(alpha = 0.5f), radius = radius * 0.75f, center = Offset(cx, cy), style = Stroke(width = 1f))

        if (isSpade) {
            // Stylized spade outline
            val path = Path().apply {
                val s = radius * 0.45f
                moveTo(cx, cy - s)
                cubicTo(cx + s, cy - s * 0.2f, cx + s * 0.8f, cy + s * 0.6f, cx, cy + s * 0.5f)
                cubicTo(cx - s * 0.8f, cy + s * 0.6f, cx - s, cy - s * 0.2f, cx, cy - s)
                close()
            }
            scope.drawPath(path, color)
        }
    }

    private fun drawCenterBlossom(scope: DrawScope, cx: Float, cy: Float, radius: Float, color: Color) {
        scope.drawCircle(color = color.copy(alpha = 0.25f), radius = radius, center = Offset(cx, cy))
        val petals = 5
        for (i in 0 until petals) {
            val angle = (i * (2 * Math.PI / petals) - Math.PI / 2).toFloat()
            val px = cx + cos(angle) * (radius * 0.5f)
            val py = cy + sin(angle) * (radius * 0.5f)
            scope.drawCircle(color = color, radius = radius * 0.28f, center = Offset(px, py))
        }
        scope.drawCircle(color = Color.White, radius = radius * 0.16f, center = Offset(cx, cy))
    }

    private fun drawCenterStar(scope: DrawScope, cx: Float, cy: Float, radius: Float, color: Color) {
        val points = 8
        val path = Path()
        for (i in 0 until points * 2) {
            val r = if (i % 2 == 0) radius else radius * 0.45f
            val angle = (i * Math.PI / points - Math.PI / 2).toFloat()
            val px = cx + cos(angle) * r
            val py = cy + sin(angle) * r
            if (i == 0) path.moveTo(px, py) else path.lineTo(px, py)
        }
        path.close()
        scope.drawPath(path, color)
    }

    private fun drawJokerHat(scope: DrawScope, cx: Float, cy: Float, size: Float, color: Color) {
        val path = Path().apply {
            moveTo(cx - size * 0.6f, cy + size * 0.4f)
            cubicTo(cx - size * 0.5f, cy - size * 0.3f, cx - size * 0.2f, cy - size * 0.5f, cx, cy - size * 0.1f)
            cubicTo(cx + size * 0.2f, cy - size * 0.5f, cx + size * 0.5f, cy - size * 0.3f, cx + size * 0.6f, cy + size * 0.4f)
            lineTo(cx - size * 0.6f, cy + size * 0.4f)
            close()
        }
        scope.drawPath(path, color)
        // Little bell circles
        scope.drawCircle(color = Color(0xFFFBBF24), radius = size * 0.12f, center = Offset(cx - size * 0.55f, cy - size * 0.25f))
        scope.drawCircle(color = Color(0xFFFBBF24), radius = size * 0.12f, center = Offset(cx + size * 0.55f, cy - size * 0.25f))
        scope.drawCircle(color = Color(0xFFFBBF24), radius = size * 0.12f, center = Offset(cx, cy - size * 0.5f))
    }
}
