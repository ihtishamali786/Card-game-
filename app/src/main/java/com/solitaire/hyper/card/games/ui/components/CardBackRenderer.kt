package com.solitaire.hyper.card.games.ui.components

import android.graphics.Bitmap
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.solitaire.hyper.card.games.ui.customization.CardBackPattern
import com.solitaire.hyper.card.games.ui.customization.CardBackTheme
import kotlin.math.cos
import kotlin.math.sin

/**
 * Universal high-performance procedural renderer for Card Backs (Section 7).
 * All card backs share:
 * - 360:520 ratio (1 : 1.444).
 * - Corner radius: 12% of width.
 * - Outer white edge: 3.5% of width.
 * - Dark slate outline (#3A4052).
 * - Inner panel with crisp high-contrast vector pattern.
 * - Thin gold inner border (#F0C454) for royal, ornamental, and classic themes.
 */
object CardBackRenderer {

    fun renderCardBack(
        drawScope: DrawScope,
        cardBack: CardBackTheme,
        customPhotoBitmap: Bitmap? = null
    ) {
        with(drawScope) {
            val w = size.width
            val h = size.height
            val r = w * 0.12f // 12% width radius
            val whiteEdgeW = (w * 0.035f).coerceAtLeast(1.5f) // 3.5% white outer border
            val outlineW = (w * 0.015f).coerceAtLeast(1f)

            // If custom photo is provided for BACK_CUSTOM_PHOTO
            if (cardBack.patternType == CardBackPattern.CUSTOM_PHOTO && customPhotoBitmap != null) {
                drawCustomPhoto(this, customPhotoBitmap, w, h, r, whiteEdgeW)
                return
            }

            // 1. White outer border band
            drawRoundRect(
                color = Color.White,
                topLeft = Offset.Zero,
                size = Size(w, h),
                cornerRadius = CornerRadius(r, r)
            )

            // 2. Dark slate outer outline (#3A4052)
            drawRoundRect(
                color = Color(0xFF3A4052),
                topLeft = Offset(outlineW / 2f, outlineW / 2f),
                size = Size(w - outlineW, h - outlineW),
                cornerRadius = CornerRadius(r, r),
                style = Stroke(width = outlineW)
            )

            // 3. Inner Panel
            val innerTopLeft = Offset(whiteEdgeW, whiteEdgeW)
            val innerSize = Size(w - 2 * whiteEdgeW, h - 2 * whiteEdgeW)
            val innerR = (r - whiteEdgeW).coerceAtLeast(2f)

            drawRoundRect(
                color = cardBack.baseColor,
                topLeft = innerTopLeft,
                size = innerSize,
                cornerRadius = CornerRadius(innerR, innerR)
            )

            // Inner boundary clipping region
            val cx = w / 2f
            val cy = h / 2f
            val accent = cardBack.accentColor

            // 4. Gold inner line for classical, royal, ornamental patterns
            val hasGoldBorder = when (cardBack.patternType) {
                CardBackPattern.CARBON_FIBRE, CardBackPattern.DARK_WOOD, CardBackPattern.LEATHER_STITCH,
                CardBackPattern.MINIMALIST_MONOGRAM, CardBackPattern.CHECKERBOARD -> false
                else -> true
            }

            if (hasGoldBorder) {
                val goldPad = whiteEdgeW + w * 0.024f
                val goldR = (innerR - w * 0.024f).coerceAtLeast(2f)
                drawRoundRect(
                    color = Color(0xFFF0C454),
                    topLeft = Offset(goldPad, goldPad),
                    size = Size(w - 2 * goldPad, h - 2 * goldPad),
                    cornerRadius = CornerRadius(goldR, goldR),
                    style = Stroke(width = (w * 0.012f).coerceAtLeast(1f))
                )
            }

            // 5. Distinct Pattern Rendering
            when (cardBack.patternType) {
                CardBackPattern.LATTICE_WEAVE -> {
                    // Reference card_back.png style anchor: Navy lattice + gold spade medallion
                    drawLatticeWeave(this, w, h, accent)
                    drawCenterMedallion(this, cx, cy, w * 0.22f, accent, isSpadeCrest = true)
                }
                CardBackPattern.GEOMETRIC_DIAMONDS -> {
                    drawGeometricDiamonds(this, w, h, accent)
                    drawCenterMedallion(this, cx, cy, w * 0.20f, accent, isSpadeCrest = false)
                }
                CardBackPattern.MANDALA -> {
                    drawMandalaPattern(this, cx, cy, w * 0.38f, accent)
                }
                CardBackPattern.ARABESQUE -> {
                    drawArabesqueTessellation(this, w, h, accent)
                    drawCenterMedallion(this, cx, cy, w * 0.20f, accent, isSpadeCrest = false)
                }
                CardBackPattern.GRADIENT_BLUE -> {
                    drawGradientGuilloche(this, w, h, accent)
                }
                CardBackPattern.CITY_SKYLINE -> {
                    drawCitySkylinePattern(this, w, h, accent)
                }
                CardBackPattern.FLOWER_BLOOM -> {
                    drawFlowerBloomPattern(this, cx, cy, w * 0.36f, accent)
                }
                CardBackPattern.DRAGON -> {
                    drawDragonSunburstPattern(this, cx, cy, w * 0.38f, accent)
                }
                CardBackPattern.GALAXY_SWIRL -> {
                    drawGalaxySwirlPattern(this, cx, cy, w * 0.38f, accent)
                }
                CardBackPattern.DARK_WOOD -> {
                    drawWoodGrainPattern(this, w, h, accent)
                }
                CardBackPattern.CARBON_FIBRE -> {
                    drawCarbonFibrePattern(this, w, h, accent)
                }
                CardBackPattern.LEATHER_STITCH -> {
                    drawLeatherStitchPattern(this, w, h, whiteEdgeW)
                }
                CardBackPattern.CIRCUIT_BOARD -> {
                    drawCircuitBoardPattern(this, w, h, accent)
                }
                CardBackPattern.OCEAN_WAVES -> {
                    drawOceanWavesSeigaiha(this, w, h, accent)
                }
                CardBackPattern.MOUNTAIN_SUNRISE -> {
                    drawMountainSunrisePattern(this, w, h, accent)
                }
                CardBackPattern.SUNSET_PALMS -> {
                    drawSunsetPalmsPattern(this, w, h, accent)
                }
                CardBackPattern.PAISLEY -> {
                    drawPaisleyPattern(this, cx, cy, w * 0.36f, accent)
                }
                CardBackPattern.TRIBAL_PATTERN -> {
                    drawTribalPattern(this, w, h, accent)
                }
                CardBackPattern.ORNATE_CREST -> {
                    drawOrnateCrestPattern(this, cx, cy, w * 0.38f, accent)
                }
                CardBackPattern.CHECKERBOARD -> {
                    drawCheckerboardPattern(this, w, h, whiteEdgeW, accent)
                }
                CardBackPattern.STARRY_NIGHT -> {
                    drawStarryNightPattern(this, cx, cy, w, h, accent)
                }
                CardBackPattern.MINIMALIST_MONOGRAM -> {
                    drawMonogramPattern(this, cx, cy, w, h, accent)
                }
                CardBackPattern.ANIMAL_SILHOUETTE -> {
                    drawAnimalSilhouettePattern(this, cx, cy, w * 0.34f, accent)
                }
                CardBackPattern.VINTAGE_POSTAGE -> {
                    drawVintagePostagePattern(this, w, h, whiteEdgeW, accent)
                }
                CardBackPattern.HOLOGRAPHIC_SHIMMER -> {
                    drawHolographicPattern(this, w, h, accent)
                }
                else -> {
                    drawLatticeWeave(this, w, h, accent)
                    drawCenterMedallion(this, cx, cy, w * 0.20f, accent, isSpadeCrest = true)
                }
            }
        }
    }

    // --- Individual procedural patterns ---

    private fun drawLatticeWeave(scope: DrawScope, w: Float, h: Float, accent: Color) {
        val step = w * 0.10f
        val stroke = Stroke(width = 1.0f)
        var x = -h
        while (x < w + h) {
            scope.drawLine(color = accent.copy(alpha = 0.25f), start = Offset(x, 0f), end = Offset(x + h, h), strokeWidth = 1f)
            scope.drawLine(color = accent.copy(alpha = 0.25f), start = Offset(x + h, 0f), end = Offset(x, h), strokeWidth = 1f)
            x += step
        }
    }

    private fun drawGeometricDiamonds(scope: DrawScope, w: Float, h: Float, accent: Color) {
        val step = w * 0.09f
        var x = 0f
        while (x < w + h) {
            scope.drawLine(color = accent.copy(alpha = 0.35f), start = Offset(x, 0f), end = Offset(x - h, h), strokeWidth = 1.2f)
            scope.drawLine(color = accent.copy(alpha = 0.35f), start = Offset(x - h, 0f), end = Offset(x, h), strokeWidth = 1.2f)
            x += step
        }
    }

    private fun drawCenterMedallion(scope: DrawScope, cx: Float, cy: Float, radius: Float, accent: Color, isSpadeCrest: Boolean) {
        // Outer gold circle
        scope.drawCircle(color = Color(0xFFF0C454), radius = radius, center = Offset(cx, cy), style = Stroke(width = 1.6f))
        // Inner tinted circle
        scope.drawCircle(color = Color(0x33F0C454), radius = radius * 0.85f, center = Offset(cx, cy))
        if (isSpadeCrest) {
            val spadePath = CardFaceRenderer.createSpadePath(cx, cy, radius * 1.15f)
            scope.drawPath(path = spadePath, color = Color(0xFFF0C454))
        } else {
            scope.drawCircle(color = accent, radius = radius * 0.45f, center = Offset(cx, cy))
        }
    }

    private fun drawMandalaPattern(scope: DrawScope, cx: Float, cy: Float, r: Float, accent: Color) {
        scope.drawCircle(color = Color(0xFFF0C454), radius = r, center = Offset(cx, cy), style = Stroke(width = 1.6f))
        for (i in 0 until 8) {
            val angle = Math.toRadians(i * 45.0)
            val ox = cx + (r * 0.45f * cos(angle)).toFloat()
            val oy = cy + (r * 0.45f * sin(angle)).toFloat()
            scope.drawCircle(color = accent.copy(alpha = 0.45f), radius = r * 0.32f, center = Offset(ox, oy), style = Stroke(width = 1.2f))
        }
        scope.drawCircle(color = Color(0xFFF0C454), radius = r * 0.35f, center = Offset(cx, cy), style = Stroke(width = 1.4f))
        scope.drawCircle(color = accent, radius = r * 0.15f, center = Offset(cx, cy))
    }

    private fun drawArabesqueTessellation(scope: DrawScope, w: Float, h: Float, accent: Color) {
        val step = w * 0.12f
        var x = 0f
        while (x < w) {
            var y = 0f
            while (y < h) {
                scope.drawCircle(color = accent.copy(alpha = 0.28f), radius = step * 0.35f, center = Offset(x, y), style = Stroke(width = 1f))
                y += step
            }
            x += step
        }
    }

    private fun drawGradientGuilloche(scope: DrawScope, w: Float, h: Float, accent: Color) {
        val cx = w / 2f
        val cy = h / 2f
        val maxR = minOf(w, h) * 0.42f
        var r = maxR
        while (r > 6f) {
            scope.drawCircle(color = accent.copy(alpha = 0.22f), radius = r, center = Offset(cx, cy), style = Stroke(width = 1f))
            r -= 8f
        }
    }

    private fun drawCitySkylinePattern(scope: DrawScope, w: Float, h: Float, accent: Color) {
        // Moon
        val moonCX = w * 0.75f
        val moonCY = h * 0.32f
        scope.drawCircle(color = Color(0xFFFDE047), radius = w * 0.10f, center = Offset(moonCX, moonCY))
        // Skyline silhouette
        val path = Path().apply {
            moveTo(0f, h * 0.85f)
            lineTo(w * 0.15f, h * 0.85f)
            lineTo(w * 0.15f, h * 0.65f)
            lineTo(w * 0.30f, h * 0.65f)
            lineTo(w * 0.30f, h * 0.55f)
            lineTo(w * 0.42f, h * 0.55f)
            lineTo(w * 0.42f, h * 0.45f)
            lineTo(w * 0.55f, h * 0.45f)
            lineTo(w * 0.55f, h * 0.60f)
            lineTo(w * 0.70f, h * 0.60f)
            lineTo(w * 0.70f, h * 0.50f)
            lineTo(w * 0.85f, h * 0.50f)
            lineTo(w * 0.85f, h * 0.72f)
            lineTo(w, h * 0.72f)
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }
        scope.drawPath(path = path, color = accent.copy(alpha = 0.85f))
    }

    private fun drawFlowerBloomPattern(scope: DrawScope, cx: Float, cy: Float, r: Float, accent: Color) {
        scope.drawCircle(color = Color(0xFFF0C454), radius = r, center = Offset(cx, cy), style = Stroke(width = 1.5f))
        for (i in 0 until 12) {
            val angle = Math.toRadians(i * 30.0)
            val px = cx + (r * 0.55f * cos(angle)).toFloat()
            val py = cy + (r * 0.55f * sin(angle)).toFloat()
            scope.drawCircle(color = accent.copy(alpha = 0.55f), radius = r * 0.28f, center = Offset(px, py))
        }
        scope.drawCircle(color = Color(0xFFFDE047), radius = r * 0.25f, center = Offset(cx, cy))
    }

    private fun drawDragonSunburstPattern(scope: DrawScope, cx: Float, cy: Float, r: Float, accent: Color) {
        scope.drawCircle(color = Color(0xFFFACC15), radius = r, center = Offset(cx, cy), style = Stroke(width = 1.8f))
        for (i in 0 until 16) {
            val rad = Math.toRadians(i * 22.5)
            val ex = cx + (r * cos(rad)).toFloat()
            val ey = cy + (r * sin(rad)).toFloat()
            scope.drawLine(color = Color(0xFFFACC15).copy(alpha = 0.65f), start = Offset(cx, cy), end = Offset(ex, ey), strokeWidth = 1.4f)
        }
        scope.drawCircle(color = accent, radius = r * 0.5f, center = Offset(cx, cy))
    }

    private fun drawGalaxySwirlPattern(scope: DrawScope, cx: Float, cy: Float, r: Float, accent: Color) {
        scope.drawCircle(color = Color(0xFFC084FC), radius = r, center = Offset(cx, cy), style = Stroke(width = 1.4f))
        for (i in 0 until 24) {
            val angle = Math.toRadians(i * 15.0)
            val dist = (r * (0.2f + (i % 6) * 0.14f))
            val sx = cx + (dist * cos(angle)).toFloat()
            val sy = cy + (dist * sin(angle)).toFloat()
            scope.drawCircle(color = Color.White.copy(alpha = 0.75f), radius = 1.5f, center = Offset(sx, sy))
        }
    }

    private fun drawWoodGrainPattern(scope: DrawScope, w: Float, h: Float, accent: Color) {
        var y = 0f
        while (y < h) {
            scope.drawLine(color = accent.copy(alpha = 0.35f), start = Offset(0f, y), end = Offset(w, y), strokeWidth = 1.5f)
            y += 7f
        }
    }

    private fun drawCarbonFibrePattern(scope: DrawScope, w: Float, h: Float, accent: Color) {
        val s = w * 0.06f
        var x = 0f
        while (x < w) {
            var y = 0f
            while (y < h) {
                if (((x / s).toInt() + (y / s).toInt()) % 2 == 0) {
                    scope.drawRect(color = accent.copy(alpha = 0.30f), topLeft = Offset(x, y), size = Size(s, s))
                }
                y += s
            }
            x += s
        }
    }

    private fun drawLeatherStitchPattern(scope: DrawScope, w: Float, h: Float, edgeW: Float) {
        val pad = edgeW + w * 0.03f
        val stroke = Stroke(width = 1.4f)
        scope.drawRoundRect(
            color = Color(0xFFFCD34D),
            topLeft = Offset(pad, pad),
            size = Size(w - 2 * pad, h - 2 * pad),
            cornerRadius = CornerRadius(6f, 6f),
            style = stroke
        )
    }

    private fun drawCircuitBoardPattern(scope: DrawScope, w: Float, h: Float, accent: Color) {
        val cx = w / 2f
        var y = h * 0.15f
        while (y < h * 0.85f) {
            scope.drawLine(color = accent.copy(alpha = 0.45f), start = Offset(w * 0.15f, y), end = Offset(w * 0.85f, y), strokeWidth = 1.2f)
            scope.drawCircle(color = Color(0xFFFBBF24), radius = 2.5f, center = Offset(cx, y))
            y += h * 0.09f
        }
    }

    private fun drawOceanWavesSeigaiha(scope: DrawScope, w: Float, h: Float, accent: Color) {
        val waveR = w * 0.12f
        var y = h * 0.10f
        while (y < h * 0.95f) {
            var x = 0f
            while (x < w + waveR) {
                scope.drawArc(
                    color = accent.copy(alpha = 0.35f),
                    startAngle = 180f,
                    sweepAngle = 180f,
                    useCenter = false,
                    topLeft = Offset(x - waveR, y - waveR),
                    size = Size(2 * waveR, 2 * waveR),
                    style = Stroke(width = 1.2f)
                )
                x += waveR * 1.5f
            }
            y += waveR * 0.75f
        }
    }

    private fun drawMountainSunrisePattern(scope: DrawScope, w: Float, h: Float, accent: Color) {
        val cx = w / 2f
        // Sun
        scope.drawCircle(color = Color(0xFFF97316), radius = w * 0.14f, center = Offset(cx, h * 0.50f))
        // Mountains
        val path = Path().apply {
            moveTo(0f, h * 0.85f)
            lineTo(w * 0.35f, h * 0.55f)
            lineTo(cx, h * 0.65f)
            lineTo(w * 0.75f, h * 0.48f)
            lineTo(w, h * 0.85f)
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }
        scope.drawPath(path = path, color = accent.copy(alpha = 0.85f))
    }

    private fun drawSunsetPalmsPattern(scope: DrawScope, w: Float, h: Float, accent: Color) {
        val cx = w / 2f
        scope.drawCircle(color = Color(0xFFFDE047), radius = w * 0.16f, center = Offset(cx, h * 0.48f))
        // Palm trunk and fronds
        scope.drawLine(color = Color(0xFF1E293B), start = Offset(w * 0.40f, h * 0.88f), end = Offset(w * 0.48f, h * 0.45f), strokeWidth = 2.5f)
        scope.drawCircle(color = Color(0xFF1E293B), radius = w * 0.12f, center = Offset(w * 0.48f, h * 0.45f), style = Stroke(width = 1.5f))
    }

    private fun drawPaisleyPattern(scope: DrawScope, cx: Float, cy: Float, r: Float, accent: Color) {
        scope.drawCircle(color = Color(0xFFF0C454), radius = r, center = Offset(cx, cy), style = Stroke(width = 1.6f))
        val path = Path().apply {
            moveTo(cx, cy - r * 0.6f)
            cubicTo(cx + r * 0.6f, cy - r * 0.3f, cx + r * 0.5f, cy + r * 0.5f, cx, cy + r * 0.6f)
            cubicTo(cx - r * 0.5f, cy + r * 0.5f, cx - r * 0.6f, cy - r * 0.1f, cx, cy - r * 0.6f)
            close()
        }
        scope.drawPath(path = path, color = accent.copy(alpha = 0.65f))
        scope.drawCircle(color = Color(0xFFF0C454), radius = r * 0.22f, center = Offset(cx, cy))
    }

    private fun drawTribalPattern(scope: DrawScope, w: Float, h: Float, accent: Color) {
        val step = h * 0.08f
        var y = h * 0.15f
        while (y < h * 0.85f) {
            val path = Path().apply {
                moveTo(w * 0.15f, y)
                lineTo(w * 0.50f, y - step * 0.4f)
                lineTo(w * 0.85f, y)
            }
            scope.drawPath(path = path, color = accent.copy(alpha = 0.60f), style = Stroke(width = 1.6f))
            y += step
        }
    }

    private fun drawOrnateCrestPattern(scope: DrawScope, cx: Float, cy: Float, r: Float, accent: Color) {
        scope.drawCircle(color = Color(0xFFF0C454), radius = r, center = Offset(cx, cy), style = Stroke(width = 1.8f))
        scope.drawCircle(color = Color(0xFFF0C454).copy(alpha = 0.3f), radius = r * 0.75f, center = Offset(cx, cy))
        // Cross arms
        scope.drawLine(color = Color(0xFFF0C454), start = Offset(cx - r * 0.85f, cy), end = Offset(cx + r * 0.85f, cy), strokeWidth = 1.6f)
        scope.drawLine(color = Color(0xFFF0C454), start = Offset(cx, cy - r * 0.85f), end = Offset(cx, cy + r * 0.85f), strokeWidth = 1.6f)
        scope.drawCircle(color = Color(0xFFF0C454), radius = r * 0.28f, center = Offset(cx, cy))
    }

    private fun drawCheckerboardPattern(scope: DrawScope, w: Float, h: Float, edgeW: Float, accent: Color) {
        val tileSize = w * 0.08f
        var x = edgeW
        var row = 0
        while (x < w - edgeW) {
            var y = edgeW
            var col = 0
            while (y < h - edgeW) {
                if ((row + col) % 2 == 0) {
                    scope.drawRect(color = accent.copy(alpha = 0.35f), topLeft = Offset(x, y), size = Size(tileSize, tileSize))
                }
                y += tileSize
                col++
            }
            x += tileSize
            row++
        }
    }

    private fun drawStarryNightPattern(scope: DrawScope, cx: Float, cy: Float, w: Float, h: Float, accent: Color) {
        val r = minOf(w, h) * 0.38f
        scope.drawCircle(color = Color(0xFFF0C454), radius = r, center = Offset(cx, cy), style = Stroke(width = 1.2f))
        for (i in 0 until 18) {
            val angle = Math.toRadians(i * 20.0)
            val dist = (r * (0.15f + (i % 5) * 0.17f))
            val sx = cx + (dist * cos(angle)).toFloat()
            val sy = cy + (dist * sin(angle)).toFloat()
            scope.drawCircle(color = Color.White.copy(alpha = 0.85f), radius = 1.4f, center = Offset(sx, sy))
        }
    }

    private fun drawMonogramPattern(scope: DrawScope, cx: Float, cy: Float, w: Float, h: Float, accent: Color) {
        val r = w * 0.26f
        scope.drawCircle(color = accent, radius = r, center = Offset(cx, cy), style = Stroke(width = 1.8f))
        // Geometric H
        val hw = r * 0.55f
        val hh = r * 0.65f
        scope.drawLine(color = accent, start = Offset(cx - hw, cy - hh), end = Offset(cx - hw, cy + hh), strokeWidth = 2.4f)
        scope.drawLine(color = accent, start = Offset(cx + hw, cy - hh), end = Offset(cx + hw, cy + hh), strokeWidth = 2.4f)
        scope.drawLine(color = accent, start = Offset(cx - hw, cy), end = Offset(cx + hw, cy), strokeWidth = 2.4f)
    }

    private fun drawAnimalSilhouettePattern(scope: DrawScope, cx: Float, cy: Float, r: Float, accent: Color) {
        scope.drawCircle(color = Color(0xFFF0C454), radius = r, center = Offset(cx, cy), style = Stroke(width = 1.6f))
        // Noble crown / shield silhouette
        val path = Path().apply {
            moveTo(cx - r * 0.45f, cy + r * 0.35f)
            lineTo(cx - r * 0.45f, cy - r * 0.25f)
            lineTo(cx - r * 0.20f, cy - r * 0.05f)
            lineTo(cx, cy - r * 0.40f)
            lineTo(cx + r * 0.20f, cy - r * 0.05f)
            lineTo(cx + r * 0.45f, cy - r * 0.25f)
            lineTo(cx + r * 0.45f, cy + r * 0.35f)
            close()
        }
        scope.drawPath(path = path, color = accent.copy(alpha = 0.75f))
    }

    private fun drawVintagePostagePattern(scope: DrawScope, w: Float, h: Float, edgeW: Float, accent: Color) {
        val pad = edgeW + w * 0.02f
        val stroke = Stroke(width = 1.2f)
        scope.drawRoundRect(
            color = Color(0xFFF0C454),
            topLeft = Offset(pad, pad),
            size = Size(w - 2 * pad, h - 2 * pad),
            cornerRadius = CornerRadius(4f, 4f),
            style = stroke
        )
        // Perforations
        var x = pad
        while (x < w - pad) {
            scope.drawCircle(color = Color(0xFF713F12), radius = 1.2f, center = Offset(x, pad))
            scope.drawCircle(color = Color(0xFF713F12), radius = 1.2f, center = Offset(x, h - pad))
            x += 6f
        }
    }

    private fun drawHolographicPattern(scope: DrawScope, w: Float, h: Float, accent: Color) {
        val diag = w + h
        var x = -h
        val colors = listOf(Color(0xFFF43F5E), Color(0xFF8B5CF6), Color(0xFF38BDF8), Color(0xFF10B981))
        var idx = 0
        while (x < diag) {
            scope.drawLine(
                color = colors[idx % colors.size].copy(alpha = 0.28f),
                start = Offset(x, 0f),
                end = Offset(x + h, h),
                strokeWidth = 3f
            )
            x += 8f
            idx++
        }
    }

    private fun drawCustomPhoto(
        scope: DrawScope,
        bitmap: Bitmap,
        w: Float,
        h: Float,
        r: Float,
        whiteEdgeW: Float
    ) {
        // White border
        scope.drawRoundRect(
            color = Color.White,
            topLeft = Offset.Zero,
            size = Size(w, h),
            cornerRadius = CornerRadius(r, r)
        )
        // Center photo
        val innerW = (w - 2 * whiteEdgeW).toInt()
        val innerH = (h - 2 * whiteEdgeW).toInt()
        if (innerW > 0 && innerH > 0) {
            val scaled = Bitmap.createScaledBitmap(bitmap, innerW, innerH, true)
            scope.drawImage(
                image = scaled.asImageBitmap(),
                topLeft = Offset(whiteEdgeW, whiteEdgeW)
            )
        }
    }
}
