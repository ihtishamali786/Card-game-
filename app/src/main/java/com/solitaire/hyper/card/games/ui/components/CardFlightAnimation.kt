package com.solitaire.hyper.card.games.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.solitaire.hyper.card.games.game.model.Card
import com.solitaire.hyper.card.games.game.model.CardLocation
import com.solitaire.hyper.card.games.ui.customization.CardBackTheme
import com.solitaire.hyper.card.games.ui.customization.CardFaceTheme
import kotlin.math.PI
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * Event describing a card flying between two locations.
 */
data class CardFlightEvent(
    val id: String = java.util.UUID.randomUUID().toString(),
    val card: Card,
    val from: CardLocation,
    val to: CardLocation,
    val durationMs: Int = 220
)

/**
 * Active card flight with resolved pixel coordinates.
 */
data class ActiveCardFlight(
    val id: String,
    val card: Card,
    val startOffset: Offset,
    val targetOffset: Offset,
    val durationMs: Int = 220
)

/**
 * Tracks the screen coordinates of cards and slots across the solitaire table.
 */
class CardPositionRegistry {
    val positions = mutableStateMapOf<String, Offset>()

    fun register(key: String, offset: Offset) {
        positions[key] = offset
    }

    fun getPosition(location: CardLocation): Offset? {
        val directKey = when (location) {
            is CardLocation.Stock -> "stock"
            is CardLocation.Waste -> "waste"
            is CardLocation.Foundation -> "foundation_${location.index}"
            is CardLocation.Tableau -> "tableau_${location.columnIndex}_${location.cardIndex}"
        }
        positions[directKey]?.let { return it }

        // Fallback for Tableau column
        if (location is CardLocation.Tableau) {
            positions["tableau_${location.columnIndex}_top"]?.let { return it }
            positions["tableau_${location.columnIndex}"]?.let { return it }
        }
        return null
    }
}

/**
 * Smooth card interpolation overlay.
 * Renders cards flying seamlessly along an arc between stacks with natural
 * elevation, tilt, and easing curves.
 */
@Composable
fun CardFlightOverlay(
    flights: List<ActiveCardFlight>,
    cardWidth: Dp,
    cardHeight: Dp,
    cardBack: CardBackTheme,
    cardFace: CardFaceTheme,
    largePrint: Boolean,
    onFlightFinished: (String) -> Unit
) {
    if (flights.isEmpty()) return

    Box(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(600f)
    ) {
        for (flight in flights) {
            key(flight.id) {
                SingleCardFlight(
                    flight = flight,
                    cardWidth = cardWidth,
                    cardHeight = cardHeight,
                    cardBack = cardBack,
                    cardFace = cardFace,
                    largePrint = largePrint,
                    onFinished = { onFlightFinished(flight.id) }
                )
            }
        }
    }
}

@Composable
private fun SingleCardFlight(
    flight: ActiveCardFlight,
    cardWidth: Dp,
    cardHeight: Dp,
    cardBack: CardBackTheme,
    cardFace: CardFaceTheme,
    largePrint: Boolean,
    onFinished: () -> Unit
) {
    val progress = remember { Animatable(0f) }

    LaunchedEffect(flight.id) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = flight.durationMs,
                easing = FastOutSlowInEasing
            )
        )
        onFinished()
    }

    val t = progress.value
    // Parabolic arc lift to simulate card picking up off the felt table
    val arcHeight = 30f
    val arcLift = sin(t * PI.toFloat()) * arcHeight

    val currentX = flight.startOffset.x + (flight.targetOffset.x - flight.startOffset.x) * t
    val currentY = flight.startOffset.y + (flight.targetOffset.y - flight.startOffset.y) * t - arcLift

    // Subtle tilt angle in the direction of flight
    val horizontalDelta = flight.targetOffset.x - flight.startOffset.x
    val maxTilt = (horizontalDelta / 35f).coerceIn(-12f, 12f)
    val tilt = maxTilt * sin(t * PI.toFloat())

    // 3D scale enlargement while airborne with elevation shadow
    val scale = 1f + sin(t * PI.toFloat()) * 0.08f
    val shadowElevationDp = (14f * sin(t * PI.toFloat())).coerceAtLeast(0f)

    Box(
        modifier = Modifier
            .offset { IntOffset(currentX.roundToInt(), currentY.roundToInt()) }
            .size(cardWidth, cardHeight)
            .graphicsLayer {
                rotationZ = tilt
                scaleX = scale
                scaleY = scale
                shadowElevation = shadowElevationDp
            }
    ) {
        CardView(
            card = flight.card,
            cardBack = cardBack,
            cardFace = cardFace,
            largePrint = largePrint,
            modifier = Modifier.fillMaxSize()
        )
    }
}
