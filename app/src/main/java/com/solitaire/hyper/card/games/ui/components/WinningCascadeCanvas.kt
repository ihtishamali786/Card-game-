package com.solitaire.hyper.card.games.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
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
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solitaire.hyper.card.games.game.model.CardColor
import com.solitaire.hyper.card.games.game.model.Rank
import com.solitaire.hyper.card.games.game.model.Suit
import com.solitaire.hyper.card.games.sound.SoundManager
import com.solitaire.hyper.card.games.ui.theme.LuxuryGoldPrimary
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
    soundManager: SoundManager? = null,
    onPlayAgain: () -> Unit,
    onHome: () -> Unit,
    onDismiss: () -> Unit
) {
    val activeCards = remember { mutableStateListOf<CascadeCard>() }
    val trails = remember { mutableStateListOf<TrailStamp>() }
    var bannerVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        soundManager?.playVictory()

        val suits = listOf(Suit.SPADES, Suit.HEARTS, Suit.CLUBS, Suit.DIAMONDS)
        val ranks = Rank.values().reversed() // KING down to ACE

        var lastLaunchTime = 0L
        var launchIndex = 0
        val totalToLaunch = suits.size * ranks.size

        while (true) {
            withFrameNanos { frameNanos ->
                val nowMs = frameNanos / 1_000_000

                if (nowMs - lastLaunchTime > 260 && launchIndex < totalToLaunch) {
                    val sIdx = launchIndex % suits.size
                    val rIdx = launchIndex / suits.size
                    val suit = suits[sIdx]
                    val rank = ranks[rIdx]

                    val startX = 60f + (sIdx * 180f)
                    val startY = 80f
                    val vx = (Random.nextFloat() * 10f - 5f).let { if (it in -1f..1f) 3.5f else it }
                    val vy = -(Random.nextFloat() * 6f + 3f)

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
                    soundManager?.playCardMove()
                    lastLaunchTime = nowMs
                    launchIndex++

                    if (launchIndex >= 8 && !bannerVisible) {
                        bannerVisible = true
                    }
                }

                val gravity = 0.85f
                val bounceDamping = 0.82f
                val screenBottom = 1800f
                val screenWidth = 1080f

                val iterator = activeCards.listIterator()
                while (iterator.hasNext()) {
                    val card = iterator.next()
                    if (card.isDone) continue

                    trails.add(TrailStamp(card.suit, card.rank, card.x, card.y))
                    if (trails.size > 2200) {
                        trails.removeRange(0, 50)
                    }

                    card.x += card.vx
                    card.vy += gravity
                    card.y += card.vy

                    if (card.y >= screenBottom) {
                        card.y = screenBottom
                        card.vy = -card.vy * bounceDamping
                        soundManager?.playFoundationSnap()
                    }

                    if (card.x < -200f || card.x > screenWidth + 200f) {
                        card.isDone = true
                    }
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .testTag("winning_cascade_container")
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cardW = size.width * 0.18f
            val cardH = cardW * 1.4f
            val corner = CornerRadius(cardW * 0.1f, cardW * 0.1f)

            trails.forEach { stamp ->
                drawCascadeCard(stamp.rank, stamp.suit, stamp.x, stamp.y, cardW, cardH, corner)
            }

            activeCards.forEach { card ->
                if (!card.isDone) {
                    drawCascadeCard(card.rank, card.suit, card.x, card.y, cardW, cardH, corner)
                }
            }
        }

        if (bannerVisible) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.55f))
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.92f)
                        .testTag("victory_dialog_card"),
                    colors = CardDefaults.cardColors(containerColor = SleekHeaderDark),
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 24.dp),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, LuxuryGoldPrimary.copy(alpha = 0.65f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(22.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Golden Trophy Icon
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(
                                    Brush.radialGradient(listOf(Color(0xFFFDE047), Color(0xFFD97706))),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = null,
                                tint = Color(0xFF1E1602),
                                modifier = Modifier.size(38.dp)
                            )
                        }

                        Spacer(Modifier.height(14.dp))

                        Text(
                            text = "VICTORY!",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp,
                            color = LuxuryGoldPrimary
                        )

                        Text(
                            text = "Relax. Play. Win with the Joker.",
                            fontSize = 13.sp,
                            color = SleekSlate300,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 4.dp)
                        )

                        Spacer(Modifier.height(16.dp))

                        // Stats Summary Box
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0xFF131D19),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp, horizontal = 16.dp),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("SCORE", color = SleekSlate300, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    Text("$score", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("MOVES", color = SleekSlate300, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    Text("$moves", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("TIME", color = SleekSlate300, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    val mins = timeSeconds / 60
                                    val secs = timeSeconds % 60
                                    Text(String.format("%02d:%02d", mins, secs), color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                                }
                            }
                        }

                        Spacer(Modifier.height(20.dp))

                        Button(
                            onClick = onPlayAgain,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("play_again_cascade_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = SleekEmerald500),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White)
                            Spacer(Modifier.width(8.dp))
                            Text("PLAY AGAIN", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }

                        Spacer(Modifier.height(10.dp))

                        OutlinedButton(
                            onClick = onHome,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .testTag("home_cascade_button"),
                            shape = RoundedCornerShape(12.dp)
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
    val r = width * 0.12f
    val cardCorner = CornerRadius(r, r)

    drawRoundRect(
        color = Color(0xFFFFFFFF),
        topLeft = Offset(x, y),
        size = Size(width, height),
        cornerRadius = cardCorner
    )
    val edgeW = (width * 0.022f).coerceAtLeast(1f)
    drawRoundRect(
        color = Color(0xFF3A4052),
        topLeft = Offset(x + edgeW / 2f, y + edgeW / 2f),
        size = Size(width - edgeW, height - edgeW),
        cornerRadius = cardCorner,
        style = androidx.compose.ui.graphics.drawscope.Stroke(width = edgeW)
    )

    val isRed = suit.color == CardColor.RED
    val suitColor = if (isRed) Color(0xFFE21C34) else Color(0xFF0E1018)
    val paintColor = if (isRed) android.graphics.Color.rgb(226, 28, 52) else android.graphics.Color.rgb(14, 16, 24)

    val suitSize = width * 0.58f
    val suitCenter = Offset(x + width * 0.50f, y + height * 0.55f)
    CardFaceRenderer.drawSuit(this, suit, suitColor, suitCenter.x, suitCenter.y, suitSize * 0.45f)

    val rankSizePx = if (rank == Rank.TEN) width * 0.31f else width * 0.38f
    val paint = android.graphics.Paint().apply {
        color = paintColor
        textSize = rankSizePx
        isAntiAlias = true
        isFakeBoldText = true
        typeface = android.graphics.Typeface.DEFAULT_BOLD
        style = android.graphics.Paint.Style.FILL_AND_STROKE
        strokeWidth = (rankSizePx * 0.05f).coerceAtLeast(1.2f)
        textAlign = android.graphics.Paint.Align.CENTER
    }
    val fontMetrics = paint.fontMetrics
    val baselineOffset = (fontMetrics.descent + fontMetrics.ascent) / 2f

    val nativeCanvas = drawContext.canvas.nativeCanvas
    nativeCanvas.save()
    nativeCanvas.translate(x + width * 0.21f, y + height * 0.12f)
    nativeCanvas.drawText(rank.display, 0f, -baselineOffset, paint)
    nativeCanvas.restore()

    nativeCanvas.save()
    nativeCanvas.translate(x + width * 0.79f, y + height * 0.88f)
    nativeCanvas.rotate(180f)
    nativeCanvas.drawText(rank.display, 0f, -baselineOffset, paint)
    nativeCanvas.restore()
}
