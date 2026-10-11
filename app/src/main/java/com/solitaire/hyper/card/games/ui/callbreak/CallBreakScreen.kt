package com.solitaire.hyper.card.games.ui.callbreak

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ScreenRotation
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solitaire.hyper.card.games.game.model.Card
import com.solitaire.hyper.card.games.game.model.Suit
import com.solitaire.hyper.card.games.game.table4p.engine.FourPlayerEngine
import com.solitaire.hyper.card.games.game.table4p.model.FourPlayerGameState
import com.solitaire.hyper.card.games.game.table4p.model.GamePhase
import com.solitaire.hyper.card.games.game.table4p.model.PlayedCard
import com.solitaire.hyper.card.games.game.table4p.model.PlayerPosition
import com.solitaire.hyper.card.games.game.table4p.model.PlayerState
import com.solitaire.hyper.card.games.ui.components.CardView
import com.solitaire.hyper.card.games.ui.table4p.FourPlayerGameViewModel
import com.solitaire.hyper.card.games.ui.theme.SleekEmerald400
import com.solitaire.hyper.card.games.ui.theme.SleekEmerald500
import com.solitaire.hyper.card.games.ui.theme.SleekHeaderDark
import com.solitaire.hyper.card.games.ui.theme.SleekSlate100
import com.solitaire.hyper.card.games.ui.theme.SleekSlate200
import com.solitaire.hyper.card.games.ui.customization.CardBackTheme
import com.solitaire.hyper.card.games.ui.customization.CustomizationRegistry
import com.solitaire.hyper.card.games.ui.theme.SleekSlate300
import com.solitaire.hyper.card.games.ui.theme.SleekSlate400
import kotlin.math.roundToInt

/**
 * Dedicated CallBreak Screen UI supporting rotated (landscape) orientation,
 * 4 player slots with dynamically injected famous celebrity names,
 * and an interactive casino felt game table with sequential card dealing animations.
 */
@Composable
fun CallBreakScreen(
    viewModel: FourPlayerGameViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity

    // Automatically rotate screen to SENSOR_LANDSCAPE for wide casino table view; restores PORTRAIT on exit
    DisposableEffect(Unit) {
        val prevOrientation = activity?.requestedOrientation ?: ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        onDispose {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    val state by viewModel.gameState.collectAsState()
    var showScoreboardDialog by remember { mutableStateOf(false) }
    var showExitDialog by remember { mutableStateOf(false) }
    var showCustomizationDialog by remember { mutableStateOf(false) }

    // Call Break 3D Table Theme & Card Back state (with Auto-Change on new game support)
    var currentTableTheme by remember { mutableStateOf(CallBreakTableTheme.EMERALD_CASINO_3D) }
    var currentCardBack by remember { mutableStateOf<CardBackTheme>(CustomizationRegistry.cardBacks.first()) }
    var autoChangeOnNewGame by remember { mutableStateOf(true) }

    // When round changes and auto-change is enabled, switch to new fresh 3D theme & cards
    LaunchedEffect(state.roundNumber) {
        if (autoChangeOnNewGame && state.roundNumber > 1) {
            currentTableTheme = CallBreakTableTheme.getRandom(except = currentTableTheme)
            currentCardBack = CustomizationRegistry.cardBacks.random()
        }
    }

    BackHandler {
        showExitDialog = true
    }

    if (showCustomizationDialog) {
        CallBreakCustomizationDialog(
            currentTheme = currentTableTheme,
            currentCardBack = currentCardBack,
            autoChangeOnNewGame = autoChangeOnNewGame,
            onThemeSelected = { currentTableTheme = it },
            onCardBackSelected = { currentCardBack = it },
            onToggleAutoChange = { autoChangeOnNewGame = it },
            onDismiss = { showCustomizationDialog = false }
        )
    }

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = {
                Text(
                    text = "Leave Call Break Match?",
                    color = SleekSlate100,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Do you want to return to the main menu? Current match progress will be lost.",
                    color = SleekSlate300
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showExitDialog = false
                        onNavigateBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SleekEmerald500)
                ) {
                    Text("Leave Match", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) {
                    Text("Stay & Play", color = SleekSlate300)
                }
            }
        )
    }

    if (showScoreboardDialog) {
        CallBreakScoreboardDialog(
            players = state.players,
            roundNumber = state.roundNumber,
            maxRounds = state.maxRounds,
            trumpSuit = state.trumpSuit,
            onDismiss = { showScoreboardDialog = false }
        )
    }

    if (state.phase == GamePhase.ROUND_OVER) {
        CallBreakRoundCompleteDialog(
            state = state,
            onNextRound = { viewModel.startNextRound() },
            onNewGame = { viewModel.startNewGame() },
            onLeave = onNavigateBack
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF04120C),
                        Color(0xFF020906)
                    )
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Slim Top Bar with Controls
            CallBreakTopBar(
                round = state.roundNumber,
                maxRounds = state.maxRounds,
                trick = state.trickNumber,
                trumpSuit = state.trumpSuit,
                isFast = state.isFastSpeed,
                onBack = { showExitDialog = true },
                onToggleSpeed = { viewModel.toggleSpeed() },
                onToggleRotate = {
                    activity?.let { act ->
                        act.requestedOrientation = if (act.requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE) {
                            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                        } else {
                            ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                        }
                    }
                },
                onShowScoreboard = { showScoreboardDialog = true },
                onOpenCustomization = { showCustomizationDialog = true }
            )

            // 2. Main 3D Model Felt Table with Central Deck Dealing Animation & Celebrity Seats (Fits full screen)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 2.dp),
                contentAlignment = Alignment.Center
            ) {
                CallBreakFeltTable(
                    state = state,
                    theme = currentTableTheme,
                    cardBack = currentCardBack,
                    onSkipDeal = { viewModel.skipDealing() },
                    onCardPlayed = { card -> viewModel.onUserPlayCard(card) }
                )
            }

            // 3. Bottom Tray: Trump Selection, Bidding Tray, or South Playing Hand
            when (state.phase) {
                GamePhase.TRUMP_SELECTION -> {
                    CallBreakTrumpSelectionTray(
                        currentTrump = state.trumpSuit,
                        playerHand = state.players[PlayerPosition.SOUTH]?.hand ?: emptyList(),
                        canRedeal = state.canRedeal,
                        onSelectTrump = { suit -> viewModel.selectTrumpSuit(suit) },
                        onRedeal = { viewModel.redealRound() }
                    )
                }
                GamePhase.BIDDING -> {
                    CallBreakBiddingTray(
                        currentBid = state.players[PlayerPosition.SOUTH]?.bid ?: 3,
                        trumpSuit = state.trumpSuit,
                        players = state.players,
                        totalBids = state.totalTableBids,
                        playerHand = state.players[PlayerPosition.SOUTH]?.hand ?: emptyList(),
                        onConfirmBid = { bid -> viewModel.setUserBid(bid) }
                    )
                }
                else -> {
                    CallBreakSouthHandArea(
                        playerState = state.players[PlayerPosition.SOUTH],
                        leadSuit = state.leadSuit,
                        trumpSuit = state.trumpSuit,
                        isMyTurn = state.currentTurn == PlayerPosition.SOUTH && state.phase == GamePhase.PLAYING,
                        isDealing = state.phase == GamePhase.DEALING,
                        message = state.message,
                        onCardClick = { card -> viewModel.onUserPlayCard(card) }
                    )
                }
            }
        }
    }
}

@Composable
fun CallBreakTopBar(
    round: Int,
    maxRounds: Int,
    trick: Int,
    trumpSuit: Suit,
    isFast: Boolean,
    onBack: () -> Unit,
    onToggleSpeed: () -> Unit,
    onToggleRotate: () -> Unit,
    onShowScoreboard: () -> Unit,
    onOpenCustomization: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(42.dp)
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.size(34.dp).testTag("call_break_back_btn")
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = SleekSlate200,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(Modifier.width(4.dp))
            Text(
                text = "SOLITAIRE CALL BREAK",
                color = SleekEmerald400,
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.1.sp
            )
            Spacer(Modifier.width(8.dp))
            Surface(
                color = SleekHeaderDark,
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(0.8.dp, Color(0xFFFFD700).copy(alpha = 0.4f))
            ) {
                Text(
                    text = "Round $round/$maxRounds • Trick $trick/13",
                    color = Color(0xFFFFD700),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Trump Indicator
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFF1E293B),
                border = BorderStroke(0.8.dp, Color(0xFFFFD700).copy(alpha = 0.6f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val trumpColor = if (trumpSuit == Suit.HEARTS || trumpSuit == Suit.DIAMONDS) Color(0xFFEF4444) else Color(0xFFFFD700)
                    Text("${trumpSuit.symbol} ${trumpSuit.displayName}", color = trumpColor, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
                }
            }

            // Change 3D Table Theme & Cards Button
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = SleekEmerald500.copy(alpha = 0.35f),
                border = BorderStroke(1.dp, SleekEmerald400),
                modifier = Modifier
                    .clickable(onClick = onOpenCustomization)
                    .testTag("call_break_customize_btn")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🎨", fontSize = 11.sp)
                    Spacer(Modifier.width(3.dp))
                    Text(
                        text = "Theme",
                        color = Color.White,
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            // Speed toggle
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isFast) SleekEmerald500.copy(alpha = 0.35f) else Color.White.copy(alpha = 0.08f),
                border = BorderStroke(1.dp, if (isFast) SleekEmerald400 else Color.White.copy(alpha = 0.15f)),
                modifier = Modifier.clickable(onClick = onToggleSpeed).testTag("call_break_speed_btn")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.FlashOn,
                        contentDescription = "Speed",
                        tint = if (isFast) Color(0xFFFFD700) else SleekSlate300,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(2.dp))
                    Text(
                        text = if (isFast) "2x" else "1x",
                        color = if (isFast) Color(0xFFFFD700) else SleekSlate300,
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Rotation toggle button
            IconButton(
                onClick = onToggleRotate,
                modifier = Modifier.size(34.dp).testTag("call_break_rotate_btn")
            ) {
                Icon(
                    Icons.Default.ScreenRotation,
                    contentDescription = "Rotate Screen",
                    tint = SleekSlate200,
                    modifier = Modifier.size(18.dp)
                )
            }

            // Scoreboard button
            IconButton(
                onClick = onShowScoreboard,
                modifier = Modifier.size(34.dp).testTag("call_break_score_btn")
            ) {
                Icon(
                    Icons.Default.BarChart,
                    contentDescription = "Scoreboard",
                    tint = Color(0xFFFFD700),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun CallBreakFeltTable(
    state: FourPlayerGameState,
    theme: CallBreakTableTheme = CallBreakTableTheme.EMERALD_CASINO_3D,
    cardBack: com.solitaire.hyper.card.games.ui.customization.CardBackTheme? = null,
    onSkipDeal: () -> Unit,
    onCardPlayed: (Card) -> Unit
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            // 3D Model Outer Cushion Rail (Beveled mahogany/walnut wood)
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        theme.railOuterColor,
                        theme.railHighlightColor,
                        theme.railOuterColor,
                        theme.railInnerColor
                    )
                ),
                shape = RoundedCornerShape(22.dp)
            )
            .padding(7.dp)
            // 3D Metallic Gold Inlay Rim
            .border(
                width = 2.dp,
                brush = Brush.linearGradient(
                    listOf(
                        theme.accentTrimColor,
                        Color(0xFF8C7126),
                        theme.accentTrimColor
                    )
                ),
                shape = RoundedCornerShape(18.dp)
            )
            .padding(3.dp)
            // Recessed 3D Felt Cavity with Deep Radial Depth
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        theme.feltCenterColor,
                        theme.feltCenterColor.copy(alpha = 0.95f),
                        theme.feltEdgeColor,
                        Color.Black.copy(alpha = 0.85f)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Table 3D subtle felt watermark
        Text(
            text = "SOLITAIRE CALL BREAK",
            color = Color.White.copy(alpha = 0.05f),
            fontSize = 26.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 4.sp
        )

        // 1. Top Opponent (North Celebrity Slot)
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 4.dp)
        ) {
            CallBreakCelebritySeat(
                playerState = state.players[PlayerPosition.NORTH],
                isTurn = state.currentTurn == PlayerPosition.NORTH && state.phase == GamePhase.PLAYING,
                isBeingDealt = state.phase == GamePhase.DEALING && state.dealingTo == PlayerPosition.NORTH,
                isDealing = state.phase == GamePhase.DEALING
            )
        }

        // 2. Left Opponent (West Celebrity Slot)
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 10.dp)
        ) {
            CallBreakCelebritySeat(
                playerState = state.players[PlayerPosition.WEST],
                isTurn = state.currentTurn == PlayerPosition.WEST && state.phase == GamePhase.PLAYING,
                isBeingDealt = state.phase == GamePhase.DEALING && state.dealingTo == PlayerPosition.WEST,
                isDealing = state.phase == GamePhase.DEALING
            )
        }

        // 3. Right Opponent (East Celebrity Slot)
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 10.dp)
        ) {
            CallBreakCelebritySeat(
                playerState = state.players[PlayerPosition.EAST],
                isTurn = state.currentTurn == PlayerPosition.EAST && state.phase == GamePhase.PLAYING,
                isBeingDealt = state.phase == GamePhase.DEALING && state.dealingTo == PlayerPosition.EAST,
                isDealing = state.phase == GamePhase.DEALING
            )
        }

        // 4. Center Area: Central Deck Dealing Animation OR Trick Play Cross
        if (state.phase == GamePhase.DEALING) {
            CallBreakSequentialCenterDeckDealing(
                progress = state.dealtCardsProgress,
                dealingTo = state.dealingTo,
                targetPlayerName = state.dealingTo?.let { state.players[it]?.name } ?: "",
                onSkipDeal = onSkipDeal
            )
        } else {
            CallBreakCenterTrickCross(
                currentTrick = state.currentTrick,
                players = state.players,
                lastWinner = state.lastTrickWinner
            )
        }
    }
}

/**
 * Sequential card dealing animation where cards fly from the central deck
 * one-by-one sequentially towards South, West, North, East.
 */
@Composable
fun CallBreakSequentialCenterDeckDealing(
    progress: Int,
    dealingTo: PlayerPosition?,
    targetPlayerName: String,
    onSkipDeal: () -> Unit
) {
    // Dealing card flight animation towards target player
    val animOffset = remember { Animatable(0f) }

    LaunchedEffect(progress) {
        animOffset.snapTo(0f)
        animOffset.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 110, easing = LinearEasing)
        )
    }

    val flightFraction = animOffset.value

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier.size(160.dp, 110.dp),
            contentAlignment = Alignment.Center
        ) {
            // Deck stack visual layers
            for (i in 3 downTo 1) {
                Box(
                    modifier = Modifier
                        .offset(x = (i * 1.5).dp, y = (i * 1.5).dp)
                        .size(44.dp, 62.dp)
                        .background(Color(0xFF5A0C0C), RoundedCornerShape(6.dp))
                        .border(0.5.dp, Color(0xFFFFD700).copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                )
            }

            // Central Deck Top Card
            Surface(
                modifier = Modifier
                    .size(46.dp, 64.dp)
                    .shadow(10.dp, RoundedCornerShape(6.dp)),
                shape = RoundedCornerShape(6.dp),
                color = Color(0xFF8B0000),
                border = BorderStroke(1.5.dp, Color(0xFFFFD700))
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🎴", fontSize = 22.sp)
                        Text(
                            text = "${52 - progress}",
                            color = Color(0xFFFFD700),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }

            // Flying card traveling from deck towards destination player
            if (dealingTo != null && progress > 0) {
                val distance = 68.dp * flightFraction
                val (offsetX, offsetY, rotation) = when (dealingTo) {
                    PlayerPosition.SOUTH -> Triple(0.dp, distance, 0f)
                    PlayerPosition.NORTH -> Triple(0.dp, -distance, 0f)
                    PlayerPosition.WEST -> Triple(-distance * 1.5f, 0.dp, -45f * flightFraction)
                    PlayerPosition.EAST -> Triple(distance * 1.5f, 0.dp, 45f * flightFraction)
                }

                Box(
                    modifier = Modifier
                        .offset(x = offsetX, y = offsetY)
                        .size(32.dp, 45.dp)
                        .graphicsLayer {
                            rotationZ = rotation
                            alpha = (1f - (flightFraction * 0.3f)).coerceIn(0.2f, 1f)
                        }
                        .background(Color(0xFF9E1010), RoundedCornerShape(4.dp))
                        .border(1.dp, Color(0xFFFFD700), RoundedCornerShape(4.dp))
                ) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("♠", color = Color(0xFFFFD700), fontSize = 12.sp)
                    }
                }
            }
        }

        Spacer(Modifier.height(4.dp))

        // Dealing status badge with Skip Deal button
        Surface(
            color = Color.Black.copy(alpha = 0.75f),
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(1.dp, Color(0xFFFFD700).copy(alpha = 0.8f))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🎴 Dealing card $progress/52 to $targetPlayerName",
                    color = Color(0xFFFFD700),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.width(8.dp))
                Surface(
                    color = SleekEmerald500.copy(alpha = 0.35f),
                    shape = RoundedCornerShape(6.dp),
                    border = BorderStroke(0.8.dp, SleekEmerald400),
                    modifier = Modifier.clickable(onClick = onSkipDeal).testTag("call_break_skip_deal")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.FastForward,
                            contentDescription = "Skip",
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(Modifier.width(2.dp))
                        Text("Skip", color = Color.White, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

/**
 * Celebrity Player Slot showing Celebrity Avatar, Name, Flag, Bid/Tricks, and Active Turn.
 */
@Composable
fun CallBreakCelebritySeat(
    playerState: PlayerState?,
    isTurn: Boolean,
    isBeingDealt: Boolean,
    isDealing: Boolean
) {
    if (playerState == null) return

    val borderColor = when {
        isBeingDealt -> Color(0xFFFFD700)
        isTurn -> Color(0xFFFFD700)
        else -> Color.White.copy(alpha = 0.15f)
    }

    val bgColor = when {
        isBeingDealt -> Color(0xFFFFD700).copy(alpha = 0.35f)
        isTurn -> Color(0xFFFFD700).copy(alpha = 0.22f)
        else -> Color.Black.copy(alpha = 0.55f)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(bgColor, RoundedCornerShape(12.dp))
            .border(1.5.dp, borderColor, RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                shape = CircleShape,
                color = if (isTurn || isBeingDealt) Color(0xFFFFD700).copy(alpha = 0.35f) else Color.White.copy(alpha = 0.12f),
                modifier = Modifier.size(28.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(playerState.avatar, fontSize = 15.sp)
                }
            }
            Spacer(Modifier.width(6.dp))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = playerState.name,
                        color = if (isTurn) Color(0xFFFFD700) else Color.White,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (playerState.flag.isNotEmpty()) {
                        Spacer(Modifier.width(3.dp))
                        Text(text = playerState.flag, fontSize = 10.sp)
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    if (isDealing) {
                        Text(
                            text = if (isBeingDealt) "Receiving..." else "🎴 ${playerState.hand.size}",
                            color = Color(0xFFFFD700),
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    } else {
                        Text(
                            text = "Call: ${playerState.bid}",
                            color = Color(0xFFFFD700),
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Won: ${playerState.tricksWon}",
                            color = SleekEmerald400,
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

/**
 * Trick cross on the table where players put down cards in current trick.
 */
@Composable
fun CallBreakCenterTrickCross(
    currentTrick: List<PlayedCard>,
    players: Map<PlayerPosition, PlayerState>,
    lastWinner: PlayerPosition?
) {
    Box(
        modifier = Modifier
            .size(190.dp, 130.dp)
            .background(Color.Black.copy(alpha = 0.32f), RoundedCornerShape(16.dp))
            .border(1.2.dp, Color.White.copy(alpha = 0.14f), RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center
    ) {
        val cardWidth = 42.dp
        val cardHeight = 60.dp

        // North played card
        val northCard = currentTrick.firstOrNull { it.player == PlayerPosition.NORTH }
        if (northCard != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = 2.dp)
            ) {
                CardView(card = northCard.card, modifier = Modifier.size(cardWidth, cardHeight))
            }
        }

        // South played card
        val southCard = currentTrick.firstOrNull { it.player == PlayerPosition.SOUTH }
        if (southCard != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = (-2).dp)
            ) {
                CardView(card = southCard.card, modifier = Modifier.size(cardWidth, cardHeight))
            }
        }

        // West played card
        val westCard = currentTrick.firstOrNull { it.player == PlayerPosition.WEST }
        if (westCard != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .offset(x = 6.dp)
            ) {
                CardView(card = westCard.card, modifier = Modifier.size(cardWidth, cardHeight))
            }
        }

        // East played card
        val eastCard = currentTrick.firstOrNull { it.player == PlayerPosition.EAST }
        if (eastCard != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .offset(x = (-6).dp)
            ) {
                CardView(card = eastCard.card, modifier = Modifier.size(cardWidth, cardHeight))
            }
        }

        if (currentTrick.isEmpty()) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "TRICK TABLE",
                    color = Color.White.copy(alpha = 0.22f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center
                )
                if (lastWinner != null) {
                    val winnerName = players[lastWinner]?.name ?: lastWinner.displayName
                    Text(
                        text = "Last: $winnerName 🏆",
                        color = Color(0xFFFFD700).copy(alpha = 0.45f),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

/**
 * Interactive Trump Selection Tray where South player confirms ♠ Spades (classic)
 * or selects another suit for the round, or re-deals if desired.
 */
@Composable
fun CallBreakTrumpSelectionTray(
    currentTrump: Suit,
    playerHand: List<Card>,
    canRedeal: Boolean,
    onSelectTrump: (Suit) -> Unit,
    onRedeal: () -> Unit
) {
    var selectedSuit by remember { mutableStateOf(currentTrump) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 2.dp),
        color = Color(0xFF04180E),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.5.dp, Color(0xFFFFD700))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "♠ SELECT TRUMP SUIT",
                            color = Color(0xFFFFD700),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black
                        )
                        Spacer(Modifier.width(6.dp))
                        Surface(
                            color = Color(0xFFFFD700).copy(alpha = 0.2f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "♠ Classic Spades is Call Break Default",
                                color = Color(0xFFFFD700),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }
                    Text(
                        text = "13 cards dealt. Inspect your hand & pick round trump:",
                        color = SleekSlate300,
                        fontSize = 9.5.sp
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedButton(
                        onClick = onRedeal,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF8C00)),
                        border = BorderStroke(1.dp, Color(0xFFFF8C00)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.height(28.dp).testTag("redeal_deck_btn")
                    ) {
                        Text("🔄 Re-shuffle", fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { onSelectTrump(selectedSuit) },
                        colors = ButtonDefaults.buttonColors(containerColor = SleekEmerald500),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                        modifier = Modifier.height(28.dp).testTag("confirm_trump_btn")
                    ) {
                        Text(
                            "Confirm ${selectedSuit.symbol} Trump",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 10.sp
                        )
                    }
                }
            }

            Spacer(Modifier.height(3.dp))

            // 4 Suits selector chips & hand preview
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // 4 Suits chips
                Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    for (suit in Suit.entries) {
                        val isSelected = suit == selectedSuit
                        val suitColor = if (suit == Suit.HEARTS || suit == Suit.DIAMONDS) Color(0xFFEF4444) else Color(0xFFE2E8F0)
                        Surface(
                            modifier = Modifier
                                .height(30.dp)
                                .clickable { selectedSuit = suit }
                                .testTag("select_trump_${suit.name}"),
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) Color(0xFFFFD700).copy(alpha = 0.35f) else Color.White.copy(alpha = 0.08f),
                            border = BorderStroke(
                                1.5.dp,
                                if (isSelected) Color(0xFFFFD700) else Color.White.copy(alpha = 0.2f)
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 7.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(suit.symbol, color = suitColor, fontSize = 14.sp, fontWeight = FontWeight.Black)
                                Spacer(Modifier.width(3.dp))
                                Text(
                                    text = suit.displayName,
                                    color = if (isSelected) Color(0xFFFFD700) else Color.White,
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                if (suit == Suit.SPADES) {
                                    Spacer(Modifier.width(2.dp))
                                    Text("★", color = Color(0xFFFFD700), fontSize = 9.sp)
                                }
                            }
                        }
                    }
                }

                // Mini preview of player's 13 cards
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy((-18).dp)
                ) {
                    for (card in playerHand) {
                        CardView(
                            card = card,
                            isSelected = card.suit == selectedSuit,
                            modifier = Modifier.size(24.dp, 34.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Interactive Bidding Selector Tray where South player selects their Call (1 to 8 tricks).
 */
@Composable
fun CallBreakBiddingTray(
    currentBid: Int,
    trumpSuit: Suit,
    players: Map<PlayerPosition, PlayerState>,
    totalBids: Int,
    playerHand: List<Card>,
    onConfirmBid: (Int) -> Unit
) {
    var selectedBid by remember { mutableIntStateOf(currentBid.coerceIn(1, 8)) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 2.dp),
        color = Color(0xFF04180E),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.5.dp, Color(0xFFFFD700))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "🎯 YOUR CALL BREAK BID",
                            color = Color(0xFFFFD700),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black
                        )
                        Spacer(Modifier.width(6.dp))
                        Surface(
                            color = Color(0xFFFFD700).copy(alpha = 0.2f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "Trump: ${trumpSuit.symbol} ${trumpSuit.displayName}",
                                color = Color(0xFFFFD700),
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }
                    val opponentBids = listOf(PlayerPosition.WEST, PlayerPosition.NORTH, PlayerPosition.EAST)
                        .mapNotNull { pos -> players[pos]?.let { "${it.name.split(" ").first()}: ${it.bid}" } }
                        .joinToString(" • ")
                    Text(
                        text = "Opponents Called: $opponentBids (Table Total: $totalBids)",
                        color = SleekSlate300,
                        fontSize = 9.5.sp
                    )
                }

                Button(
                    onClick = { onConfirmBid(selectedBid) },
                    colors = ButtonDefaults.buttonColors(containerColor = SleekEmerald500),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                    modifier = Modifier.height(28.dp).testTag("call_break_confirm_bid")
                ) {
                    Text(
                        text = "Confirm Call: $selectedBid",
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 10.5.sp
                    )
                }
            }

            Spacer(Modifier.height(3.dp))

            // Chip selector for 1 to 8 + Mini preview of player's hand
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    for (b in 1..8) {
                        val isSelected = b == selectedBid
                        Surface(
                            modifier = Modifier
                                .size(28.dp)
                                .clickable { selectedBid = b }
                                .testTag("bid_chip_$b"),
                            shape = CircleShape,
                            color = if (isSelected) Color(0xFFFFD700) else Color.White.copy(alpha = 0.1f),
                            border = BorderStroke(
                                1.5.dp,
                                if (isSelected) Color(0xFFD4AF37) else Color.White.copy(alpha = 0.2f)
                            )
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "$b",
                                    color = if (isSelected) Color(0xFF04120C) else Color.White,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 11.5.sp
                                )
                            }
                        }
                    }
                }

                // Mini preview of player's 13 cards
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy((-18).dp)
                ) {
                    for (card in playerHand) {
                        CardView(
                            card = card,
                            isSelected = card.suit == trumpSuit,
                            modifier = Modifier.size(24.dp, 34.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * South Hand Area (Landscape wide horizontal scrollable cards fanned out).
 */
@Composable
fun CallBreakSouthHandArea(
    playerState: PlayerState?,
    leadSuit: Suit?,
    trumpSuit: Suit,
    isMyTurn: Boolean,
    isDealing: Boolean,
    message: String,
    onCardClick: (Card) -> Unit
) {
    if (playerState == null) return

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF030C07))
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        // Stats bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "👤 You",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(Modifier.width(8.dp))
                Surface(
                    color = Color(0xFFFFD700).copy(alpha = 0.2f),
                    shape = RoundedCornerShape(6.dp),
                    border = BorderStroke(0.8.dp, Color(0xFFFFD700).copy(alpha = 0.5f))
                ) {
                    Text(
                        text = "Tricks Won: ${playerState.tricksWon}/${playerState.bid}",
                        color = Color(0xFFFFD700),
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                    )
                }
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "Score: ${"%.1f".format(playerState.totalScore)}",
                    color = SleekEmerald400,
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = message,
                color = if (isMyTurn) Color(0xFFFFD700) else SleekSlate300,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Landscape Hand of Cards (Wide horizontal fanned row)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 2.dp),
            horizontalArrangement = Arrangement.spacedBy((-14).dp)
        ) {
            for (card in playerState.hand) {
                val isLegal = isMyTurn && !isDealing && FourPlayerEngine.isLegalPlay(card, playerState.hand, leadSuit)
                val cardWidth = 50.dp
                val cardHeight = 72.dp

                Box(
                    modifier = Modifier
                        .offset(y = if (isLegal) (-6).dp else 0.dp)
                        .clickable(enabled = isLegal) { onCardClick(card) }
                        .testTag("south_card_${card.id}")
                ) {
                    CardView(
                        card = card,
                        isSelected = isLegal || (card.suit == trumpSuit),
                        modifier = Modifier.size(cardWidth, cardHeight)
                    )
                }
            }
        }
    }
}

@Composable
fun CallBreakScoreboardDialog(
    players: Map<PlayerPosition, PlayerState>,
    roundNumber: Int,
    maxRounds: Int,
    trumpSuit: Suit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "📊 Call Break Match Scoreboard",
                fontWeight = FontWeight.ExtraBold,
                color = SleekSlate100
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Round $roundNumber of $maxRounds • ${trumpSuit.symbol} ${trumpSuit.displayName} Trump",
                    color = SleekSlate300,
                    fontSize = 12.sp
                )
                for (pos in PlayerPosition.entries) {
                    val p = players[pos] ?: continue
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (pos == PlayerPosition.SOUTH) SleekEmerald500.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.05f),
                                RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(p.avatar, fontSize = 16.sp)
                            Spacer(Modifier.width(6.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = p.name,
                                        color = if (pos == PlayerPosition.SOUTH) Color(0xFFFFD700) else Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                    if (p.flag.isNotEmpty()) {
                                        Spacer(Modifier.width(4.dp))
                                        Text(p.flag, fontSize = 10.sp)
                                    }
                                }
                                Text(
                                    text = "Call: ${p.bid} • Tricks: ${p.tricksWon}",
                                    color = SleekSlate300,
                                    fontSize = 10.sp
                                )
                            }
                        }
                        Text(
                            text = "${"%.1f".format(p.totalScore)} pts",
                            color = if (p.totalScore >= 0) SleekEmerald400 else Color(0xFFFF5252),
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = SleekEmerald500)
            ) {
                Text("Close", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Composable
fun CallBreakRoundCompleteDialog(
    state: FourPlayerGameState,
    onNextRound: () -> Unit,
    onNewGame: () -> Unit,
    onLeave: () -> Unit
) {
    val isFinalRound = state.roundNumber >= state.maxRounds
    val winnerEntry = state.players.maxByOrNull { it.value.totalScore }

    AlertDialog(
        onDismissRequest = {},
        title = {
            Text(
                text = if (isFinalRound) "🏆 Match Grand Finale!" else "🎉 Round ${state.roundNumber} Complete!",
                fontWeight = FontWeight.Black,
                color = Color(0xFFFFD700),
                fontSize = 18.sp
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (isFinalRound && winnerEntry != null) {
                    val p = winnerEntry.value
                    Surface(
                        color = Color(0xFFFFD700).copy(alpha = 0.2f),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, Color(0xFFFFD700))
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(p.avatar, fontSize = 24.sp)
                            Spacer(Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "TOURNAMENT CHAMPION",
                                    color = Color(0xFFFFD700),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Text(
                                    text = "${p.name} (${p.country})",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Final Score: ${"%.1f".format(p.totalScore)} pts",
                                    color = SleekEmerald400,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Text(
                    text = "Round Results & Scores:",
                    color = SleekSlate300,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )

                for (pos in PlayerPosition.entries) {
                    val p = state.players[pos] ?: continue
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (pos == PlayerPosition.SOUTH) SleekEmerald500.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.05f),
                                RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(p.avatar, fontSize = 14.sp)
                            Spacer(Modifier.width(6.dp))
                            Column {
                                Text(p.name, color = Color.White, fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                                Text("Bid: ${p.bid} | Won: ${p.tricksWon}", color = SleekSlate300, fontSize = 9.5.sp)
                            }
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = (if (p.roundScoreDelta >= 0) "+" else "") + "${"%.1f".format(p.roundScoreDelta)}",
                                color = if (p.roundScoreDelta >= 0) SleekEmerald400 else Color(0xFFFF5252),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Total: ${"%.1f".format(p.totalScore)}",
                                color = Color(0xFFFFD700),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (isFinalRound) {
                Button(
                    onClick = onNewGame,
                    colors = ButtonDefaults.buttonColors(containerColor = SleekEmerald500)
                ) {
                    Text("New Match", color = Color.White, fontWeight = FontWeight.Bold)
                }
            } else {
                Button(
                    onClick = onNextRound,
                    colors = ButtonDefaults.buttonColors(containerColor = SleekEmerald500)
                ) {
                    Text("Next Round ${state.roundNumber + 1}", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onLeave) {
                Text("Exit to Menu", color = SleekSlate300)
            }
        }
    )
}
