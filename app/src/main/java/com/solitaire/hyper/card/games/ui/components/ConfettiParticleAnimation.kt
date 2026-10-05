package com.solitaire.hyper.card.games.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import com.solitaire.hyper.card.games.sound.SoundManager
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

enum class ConfettiShape {
    RECTANGLE,
    CIRCLE,
    STAR,
    DIAMOND
}

data class ConfettiParticle(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    var rotation: Float,
    var rotationSpeed: Float,
    var wobble: Float,
    var wobbleSpeed: Float,
    val width: Float,
    val height: Float,
    val color: Color,
    val shape: ConfettiShape,
    var alpha: Float = 1f,
    var life: Float = 1f,
    val decay: Float = Random.nextFloat() * 0.003f + 0.002f
)

private val ConfettiColors = listOf(
    Color(0xFFFFD700), // Gold
    Color(0xFFFF3B30), // Bright Red
    Color(0xFF34C759), // Emerald Green
    Color(0xFF007AFF), // Vibrant Blue
    Color(0xFFAF52DE), // Royal Purple
    Color(0xFFFF9500), // Amber Orange
    Color(0xFFFF2D55), // Hot Pink
    Color(0xFF5AC8FA), // Sky Blue
    Color(0xFFFFCC00)  // Golden Yellow
)

/**
 * High-performance, celebratory Confetti and Particle effect.
 * Features dual cannon bursts from lower corners, floating celebratory shimmer,
 * 3D tumbling paper physics, and interactive tap-to-burst particles.
 */
@Composable
fun ConfettiParticleAnimation(
    modifier: Modifier = Modifier,
    soundManager: SoundManager? = null,
    enableTapBurst: Boolean = true
) {
    val particles = remember { mutableStateListOf<ConfettiParticle>() }

    fun spawnBurst(originX: Float, originY: Float, count: Int, upwardForce: Float = 22f, angleSpread: Float = 60f, baseAngle: Float = 270f) {
        val angleRad = baseAngle * (PI / 180.0)
        val spreadRad = angleSpread * (PI / 180.0)

        for (i in 0 until count) {
            val a = angleRad + (Random.nextDouble() - 0.5) * spreadRad
            val speed = Random.nextFloat() * upwardForce + (upwardForce * 0.4f)
            val vx = (cos(a) * speed).toFloat()
            val vy = (sin(a) * speed).toFloat()

            val shape = when (Random.nextInt(5)) {
                0, 1 -> ConfettiShape.RECTANGLE
                2 -> ConfettiShape.STAR
                3 -> ConfettiShape.CIRCLE
                else -> ConfettiShape.DIAMOND
            }

            val sizeBase = Random.nextFloat() * 10f + 8f
            val width = sizeBase
            val height = if (shape == ConfettiShape.RECTANGLE) sizeBase * (Random.nextFloat() * 1.5f + 1.2f) else sizeBase

            particles.add(
                ConfettiParticle(
                    x = originX,
                    y = originY,
                    vx = vx,
                    vy = vy,
                    rotation = Random.nextFloat() * 360f,
                    rotationSpeed = (Random.nextFloat() - 0.5f) * 12f,
                    wobble = Random.nextFloat() * (2 * PI.toFloat()),
                    wobbleSpeed = Random.nextFloat() * 0.15f + 0.05f,
                    width = width,
                    height = height,
                    color = ConfettiColors.random(),
                    shape = shape
                )
            )
        }
    }

    fun spawnTopShower(screenWidth: Float, count: Int) {
        for (i in 0 until count) {
            val shape = when (Random.nextInt(4)) {
                0, 1 -> ConfettiShape.RECTANGLE
                2 -> ConfettiShape.STAR
                else -> ConfettiShape.CIRCLE
            }
            val sizeBase = Random.nextFloat() * 8f + 6f
            particles.add(
                ConfettiParticle(
                    x = Random.nextFloat() * screenWidth,
                    y = -Random.nextFloat() * 200f,
                    vx = (Random.nextFloat() - 0.5f) * 3f,
                    vy = Random.nextFloat() * 4f + 3f,
                    rotation = Random.nextFloat() * 360f,
                    rotationSpeed = (Random.nextFloat() - 0.5f) * 8f,
                    wobble = Random.nextFloat() * (2 * PI.toFloat()),
                    wobbleSpeed = Random.nextFloat() * 0.12f + 0.04f,
                    width = sizeBase,
                    height = if (shape == ConfettiShape.RECTANGLE) sizeBase * 1.8f else sizeBase,
                    color = ConfettiColors.random(),
                    shape = shape,
                    decay = 0.0015f
                )
            )
        }
    }

    LaunchedEffect(Unit) {
        soundManager?.playVictory()

        var initialized = false
        var lastShowerTime = 0L

        while (true) {
            withFrameNanos { frameNanos ->
                val nowMs = frameNanos / 1_000_000

                // Trigger dual-cannons on first frame
                if (!initialized) {
                    initialized = true
                    // Left Cannon: upward and toward center-right (approx 305 deg)
                    spawnBurst(originX = 80f, originY = 1700f, count = 70, upwardForce = 26f, angleSpread = 35f, baseAngle = 295f)
                    // Right Cannon: upward and toward center-left (approx 235 deg)
                    spawnBurst(originX = 1000f, originY = 1700f, count = 70, upwardForce = 26f, angleSpread = 35f, baseAngle = 245f)
                    // Top shower
                    spawnTopShower(1080f, 40)
                    lastShowerTime = nowMs
                }

                // Periodic gentle rain of shimmer for festive ambiance
                if (nowMs - lastShowerTime > 800 && particles.size < 220) {
                    spawnTopShower(1080f, 15)
                    lastShowerTime = nowMs
                }

                // Physics update
                val gravity = 0.38f
                val drag = 0.985f
                val iterator = particles.iterator()

                while (iterator.hasNext()) {
                    val p = iterator.next()
                    p.vx *= drag
                    p.vy = (p.vy + gravity) * drag

                    // Subtle sinusoidal wind drift
                    p.x += p.vx + sin(p.wobble) * 0.75f
                    p.y += p.vy

                    p.rotation += p.rotationSpeed
                    p.wobble += p.wobbleSpeed
                    p.life -= p.decay

                    if (p.life <= 0.3f) {
                        p.alpha = (p.life / 0.3f).coerceIn(0f, 1f)
                    }

                    if (p.life <= 0f || p.y > 2200f || p.x < -100f || p.x > 1200f) {
                        iterator.remove()
                    }
                }
            }
        }
    }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .testTag("confetti_particle_canvas")
            .pointerInput(enableTapBurst) {
                if (enableTapBurst) {
                    detectTapGestures { offset ->
                        // Interactive tap explosion!
                        spawnBurst(
                            originX = offset.x,
                            originY = offset.y,
                            count = 35,
                            upwardForce = 16f,
                            angleSpread = 360f,
                            baseAngle = 270f
                        )
                        soundManager?.playFoundationSnap()
                    }
                }
            }
    ) {
        val starPath = Path()

        for (p in particles) {
            val scaleX = cos(p.wobble) // 3D tumbling paper effect
            val effectiveAlpha = p.alpha.coerceIn(0f, 1f)

            rotate(degrees = p.rotation, pivot = Offset(p.x, p.y)) {
                scale(scaleX = scaleX, scaleY = 1f, pivot = Offset(p.x, p.y)) {
                    when (p.shape) {
                        ConfettiShape.RECTANGLE -> {
                            drawRect(
                                color = p.color.copy(alpha = effectiveAlpha),
                                topLeft = Offset(p.x - p.width / 2, p.y - p.height / 2),
                                size = Size(p.width, p.height)
                            )
                        }
                        ConfettiShape.CIRCLE -> {
                            drawCircle(
                                color = p.color.copy(alpha = effectiveAlpha),
                                radius = p.width / 2,
                                center = Offset(p.x, p.y)
                            )
                        }
                        ConfettiShape.STAR -> {
                            drawStar(
                                path = starPath,
                                centerX = p.x,
                                centerY = p.y,
                                size = p.width * 1.3f,
                                color = p.color.copy(alpha = effectiveAlpha)
                            )
                        }
                        ConfettiShape.DIAMOND -> {
                            drawDiamond(
                                centerX = p.x,
                                centerY = p.y,
                                width = p.width,
                                height = p.height,
                                color = p.color.copy(alpha = effectiveAlpha)
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun DrawScope.drawStar(
    path: Path,
    centerX: Float,
    centerY: Float,
    size: Float,
    color: Color
) {
    path.reset()
    val half = size / 2
    val inner = size * 0.25f

    // 4-pointed sparkle star
    path.moveTo(centerX, centerY - half)
    path.lineTo(centerX + inner, centerY - inner)
    path.lineTo(centerX + half, centerY)
    path.lineTo(centerX + inner, centerY + inner)
    path.lineTo(centerX, centerY + half)
    path.lineTo(centerX - inner, centerY + inner)
    path.lineTo(centerX - half, centerY)
    path.lineTo(centerX - inner, centerY - inner)
    path.close()

    drawPath(path = path, color = color)
}

private fun DrawScope.drawDiamond(
    centerX: Float,
    centerY: Float,
    width: Float,
    height: Float,
    color: Color
) {
    val path = Path().apply {
        moveTo(centerX, centerY - height / 2)
        lineTo(centerX + width / 2, centerY)
        lineTo(centerX, centerY + height / 2)
        lineTo(centerX - width / 2, centerY)
        close()
    }
    drawPath(path = path, color = color)
}
