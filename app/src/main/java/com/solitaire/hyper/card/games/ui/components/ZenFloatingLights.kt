package com.solitaire.hyper.card.games.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import kotlin.math.sin

/**
 * Gentle, relaxing animated table background with soft floating zen lights.
 * Automatically disabled if the user has enabled "Reduce Motion".
 */
@Composable
fun ZenFloatingLights(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    reduceMotion: Boolean = false
) {
    if (!enabled || reduceMotion) return

    val infiniteTransition = rememberInfiniteTransition(label = "zen_lights")

    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 16000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // Orb 1: Emerald glow
        val x1 = w * 0.25f + sin(phase) * (w * 0.12f)
        val y1 = h * 0.30f + sin(phase * 1.3f) * (h * 0.08f)
        val r1 = w * 0.35f
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFF34D399).copy(alpha = 0.08f), Color.Transparent),
                center = Offset(x1, y1),
                radius = r1
            ),
            center = Offset(x1, y1),
            radius = r1
        )

        // Orb 2: Warm amber / gold glow
        val x2 = w * 0.75f - sin(phase * 0.8f) * (w * 0.10f)
        val y2 = h * 0.65f + sin(phase * 1.1f) * (h * 0.10f)
        val r2 = w * 0.40f
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFFFBBF24).copy(alpha = 0.06f), Color.Transparent),
                center = Offset(x2, y2),
                radius = r2
            ),
            center = Offset(x2, y2),
            radius = r2
        )

        // Orb 3: Cyan zen light
        val x3 = w * 0.50f + sin(phase * 1.5f) * (w * 0.15f)
        val y3 = h * 0.85f - sin(phase * 0.9f) * (h * 0.06f)
        val r3 = w * 0.30f
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFF38BDF8).copy(alpha = 0.06f), Color.Transparent),
                center = Offset(x3, y3),
                radius = r3
            ),
            center = Offset(x3, y3),
            radius = r3
        )
    }
}
