package com.solitaire.hyper.card.games.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solitaire.hyper.card.games.game.model.CardColor
import com.solitaire.hyper.card.games.game.model.Rank
import com.solitaire.hyper.card.games.game.model.Suit
import com.solitaire.hyper.card.games.ui.theme.SleekEmerald400
import com.solitaire.hyper.card.games.ui.theme.SleekEmerald500
import com.solitaire.hyper.card.games.ui.theme.SleekHeaderDark
import com.solitaire.hyper.card.games.ui.theme.SleekSlate300
import kotlin.random.Random

data class CascadeCard(
    val suit: Suit,
    val rank: Rank,
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    var isDone: Boolean = false
)

data class TrailStamp(
    val suit: Suit,
    val rank: Rank,
    val x: Float,
    val y: Float
)

/**
 * Iconic Windows Solitaire Winning Card Cascade Animation.
 * Cards cascade from the foundations, bouncing across the screen,
 * leaving an iconic card trail behind them!
 */
@Composable
fun WinningCascadeCanvas(
    score: Int,
    timeSeconds: Long,
    moves: Int,
    coinsEarned: Int = 200,
    hasDoubledCoins: Boolean = false,
    onWatchAdDouble: (() -> Unit)? = null,
    onPlayAgain: () -> Unit,
    onHome: () -> Unit,
    onDismiss: () -> Unit
) {
    val activeCards = remember { mutableStateListOf<CascadeCard>() }
    val trails = remember { mutableStateListOf<TrailStamp>() }
    var bannerVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        // Build card deck to launch from Kings down to Aces
        val suits = listOf(Suit.SPADES, Suit.HEARTS, Suit.CLUBS, Suit.DIAMONDS)
        val ranks = Rank.values().reversed() // KING down to ACE

        var lastLaunchTime = 0L
        var launchIndex = 0
        val totalToLaunch = suits.size * ranks.size

        // Physics loop
        while (true) {
            withFrameNanos { frameNanos ->
                val nowMs = frameNanos / 1_000_000

                // Periodically launch next card from one of the foundation origins
                if (nowMs - lastLaunchTime > 300 && launchIndex < totalToLaunch) {
                    val suitIdx = launchIndex % suits.size
                    val rankIdx = (launchIndex / suits.size).coerceIn(0, ranks.size - 1)
                    val suit = suits[suitIdx]
                    val rank = ranks[rankIdx]

                    // Initial horizontal foundation slot position
                    val startX = 60f + suitIdx * 180f
                    val startY = 80f
                    val vx = (Random.nextFloat() * 12f - 6f).let { if (Math.abs(it) < 2f) 3.5f else it }
                    val vy = -Random.nextFloat() * 6f - 3f

                    activeCards.add(
                        CascadeCard(
                            suit = suit,
                            rank = rank,
                            x = startX,
                            y = startY,
                            vx = vx,
                            vy = vy
                        )
                    )
                    launchIndex++
                    lastLaunchTime = nowMs

                    if (launchIndex >= 8) {
                        bannerVisible = true
                    }
                }

                // Update physics on all active cards
                val gravity = 0.65f
                val iterator = activeCards.iterator()
                while (iterator.hasNext()) {
                    val c = iterator.next()
                    // Stamp trail
                    if (trails.size < 400) {
                        trails.add(TrailStamp(c.suit, c.rank, c.x, c.y))
                    }

                    c.x += c.vx
                    c.vy += gravity
                    c.y += c.vy

                    // Floor bounce
                    val floorY = 1600f
                    if (c.y >= floorY) {
                        c.y = floorY
                        c.vy = -c.vy * 0.8f
                        if (Math.abs(c.vy) < 2f) {
                            c.isDone = true
                        }
                    }

                    // Remove if moved past screen edges
                    if (c.x < -150f || c.x > 1200f || c.isDone) {
                        iterator.remove()
                    }
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .testTag("winning_cascade_canvas")
    ) {
        // Canvas rendering the bouncing card cascade
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cardW = 90f
            val cardH = 130f
            val corner = CornerRadius(10f, 10f)

            // Draw trails
            for (stamp in trails) {
                drawCascadeCard(stamp.rank, stamp.suit, stamp.x, stamp.y, cardW, cardH, corner)
            }

            // Draw active moving cards
            for (card in activeCards) {
                drawCascadeCard(card.rank, card.suit, card.x, card.y, cardW, cardH, corner)
            }
        }

        // Overlay victory summary banner
        if (bannerVisible) {
            Card(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(24.dp)
                    .testTag("victory_dialog_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SleekHeaderDark.copy(alpha = 0.95f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "🎉 VICTORY!",
                        color = SleekEmerald400,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "You conquered this Solitaire deal!",
                        color = Color.White,
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )

                    // Coin reward badge
                    Surface(
                        color = Color(0xFFEAB308).copy(alpha = 0.15f),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEAB308).copy(alpha = 0.5f)),
                        modifier = Modifier.padding(vertical = 6.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("🪙", fontSize = 16.sp)
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = if (hasDoubledCoins) "+600 Coins Earned! (3x Bonus)" else "+$coinsEarned Coins Earned!",
                                color = Color(0xFFFDE047),
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Stats row
                    Row(
                        modifier = Modifier.padding(vertical = 10.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("SCORE", color = SleekSlate300, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text("$score", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                        }
                        Spacer(Modifier.width(24.dp))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("MOVES", color = SleekSlate300, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text("$moves", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                        }
                        Spacer(Modifier.width(24.dp))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("TIME", color = SleekSlate300, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            val mins = timeSeconds / 60
                            val secs = timeSeconds % 60
                            Text(String.format("%02d:%02d", mins, secs), color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                        }
                    }

                    if (!hasDoubledCoins && onWatchAdDouble != null) {
                        Button(
                            onClick = onWatchAdDouble,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(42.dp)
                                .testTag("double_coins_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("🎬 Watch Ad for +400 Coins", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                        Spacer(Modifier.height(8.dp))
                    }

                    Button(
                        onClick = onPlayAgain,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("play_again_cascade_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = SleekEmerald500),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White)
                        Spacer(Modifier.width(8.dp))
                        Text("PLAY AGAIN", color = Color.White, fontWeight = FontWeight.Bold)
                    }

                    Spacer(Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = onHome,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("home_cascade_button"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Home, contentDescription = null, tint = SleekSlate300)
                        Spacer(Modifier.width(8.dp))
                        Text("MAIN MENU", color = SleekSlate300, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

private fun DrawScope.drawCascadeCard(
    rank: Rank,
    suit: Suit,
    x: Float,
    y: Float,
    width: Float,
    height: Float,
    corner: CornerRadius
) {
    // Card background
    drawRoundRect(
        color = Color.White,
        topLeft = Offset(x, y),
        size = Size(width, height),
        cornerRadius = corner
    )
    // Card border
    drawRoundRect(
        color = Color(0x33000000),
        topLeft = Offset(x, y),
        size = Size(width, height),
        cornerRadius = corner,
        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.5f)
    )

    val isRed = suit.color == CardColor.RED
    val paintColor = if (isRed) android.graphics.Color.rgb(211, 47, 47) else android.graphics.Color.rgb(30, 33, 36)

    val paint = android.graphics.Paint().apply {
        color = paintColor
        textSize = 28f
        isAntiAlias = true
        isFakeBoldText = true
    }

    drawContext.canvas.nativeCanvas.drawText(
        rank.display,
        x + 8f,
        y + 30f,
        paint
    )

    drawContext.canvas.nativeCanvas.drawText(
        suit.symbol,
        x + 8f,
        y + 58f,
        paint
    )

    // Center symbol
    val centerPaint = android.graphics.Paint().apply {
        color = paintColor
        textSize = 42f
        isAntiAlias = true
        textAlign = android.graphics.Paint.Align.CENTER
    }
    drawContext.canvas.nativeCanvas.drawText(
        suit.symbol,
        x + width / 2f,
        y + height / 2f + 14f,
        centerPaint
    )
}
