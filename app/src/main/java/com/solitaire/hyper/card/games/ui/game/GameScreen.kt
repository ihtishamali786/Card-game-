package com.solitaire.hyper.card.games.ui.game

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solitaire.hyper.card.games.ads.AdManager
import com.solitaire.hyper.card.games.ads.BannerAdView
import com.solitaire.hyper.card.games.game.model.Card
import com.solitaire.hyper.card.games.game.model.CardLocation
import com.solitaire.hyper.card.games.game.model.GameState
import com.solitaire.hyper.card.games.ui.components.CardSlotPlaceholder
import com.solitaire.hyper.card.games.ui.components.CardView
import com.solitaire.hyper.card.games.ui.customization.CustomizationRegistry
import com.solitaire.hyper.card.games.ui.theme.SleekBorderSubtle
import com.solitaire.hyper.card.games.ui.theme.SleekEmerald400
import com.solitaire.hyper.card.games.ui.theme.SleekEmerald500
import com.solitaire.hyper.card.games.ui.theme.SleekFooterBg
import com.solitaire.hyper.card.games.ui.theme.SleekHeaderDark
import com.solitaire.hyper.card.games.ui.theme.SleekSlate100
import com.solitaire.hyper.card.games.ui.theme.SleekSlate200
import com.solitaire.hyper.card.games.ui.theme.SleekSlate300
import com.solitaire.hyper.card.games.ui.theme.SleekSlate400
import com.solitaire.hyper.card.games.ui.theme.SleekStatLabelStyle
import com.solitaire.hyper.card.games.ui.theme.SleekStatValueStyle

@Composable
fun GameScreen(
    viewModel: GameViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity

    val gameState by viewModel.gameState.collectAsState()
    val userSettings by viewModel.userSettings.collectAsState()
    val selectedLocation by viewModel.selectedLocation.collectAsState()
    val validTargets by viewModel.validTargets.collectAsState()
    val statusMessage by viewModel.statusMessage.collectAsState()
    val activeHint by viewModel.activeHint.collectAsState()
    val isAutoCompleting by viewModel.isAutoCompleting.collectAsState()
    val showWinDialog by viewModel.showWinDialog.collectAsState()

    var showRestartConfirm by remember { mutableStateOf(false) }

    val currentBackground = CustomizationRegistry.getBackground(userSettings.backgroundId)
    val currentCardBack = CustomizationRegistry.getCardBack(userSettings.cardBackId)
    val currentCardFace = CustomizationRegistry.getCardFace(userSettings.cardFaceId)

    BackHandler {
        onNavigateBack()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(currentBackground.brush)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 6.dp, vertical = 4.dp)
        ) {
            // 1. Top Header: Back Button, Stats (Time, Moves, Score, Cancel), Restart
            PortraitGameTopHeader(
                gameState = gameState,
                selectedLocation = selectedLocation,
                showTimer = userSettings.showTimer,
                onBack = onNavigateBack,
                onCancelSelection = { viewModel.clearSelection() },
                onRestart = { showRestartConfirm = true }
            )

            Spacer(modifier = Modifier.height(4.dp))

            // 2. Top Cards Row: 7 columns matching the 7 Tableau columns exactly
            // [Stock] [Waste] [Spacer] [♠] [♥] [♦] [♣]
            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                val availableWidth = maxWidth
                val colSpacing = 4.dp
                val cardWidth = ((availableWidth - (colSpacing * 8)) / 7).coerceIn(38.dp, 58.dp)
                val cardHeight = cardWidth * 1.40f

                PortraitTopCardRow(
                    gameState = gameState,
                    cardWidth = cardWidth,
                    cardHeight = cardHeight,
                    colSpacing = colSpacing,
                    cardBack = currentCardBack,
                    cardFace = currentCardFace,
                    selectedLocation = selectedLocation,
                    validTargets = validTargets,
                    activeHint = activeHint,
                    onStockClick = { viewModel.onStockClicked() },
                    onWasteClick = { viewModel.onCardClicked(CardLocation.Waste) },
                    onWasteDoubleClick = { viewModel.onCardDoubleClicked(CardLocation.Waste) },
                    onFoundationClick = { index -> viewModel.onFoundationClicked(index) }
                )
            }

            // 3. Status or Active Hint Message ribbon
            if (activeHint != null || (!statusMessage.isNullOrBlank() && statusMessage != "Tap any face-up card to select")) {
                Spacer(modifier = Modifier.height(3.dp))
                Surface(
                    color = Color.Black.copy(alpha = 0.45f),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(0.5.dp, SleekEmerald400.copy(alpha = 0.35f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            if (activeHint != null) Icons.Default.Lightbulb else Icons.Default.Info,
                            contentDescription = null,
                            tint = if (activeHint != null) SleekEmerald400 else SleekSlate200,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = activeHint?.description ?: statusMessage.orEmpty(),
                            color = if (activeHint != null) SleekEmerald400 else SleekSlate200,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // 4. Main Tableau Board (Full Width and Maximum Height)
            BoxWithConstraints(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                val availableWidth = maxWidth
                val colSpacing = 4.dp
                val cardWidth = ((availableWidth - (colSpacing * 8)) / 7).coerceIn(38.dp, 58.dp)
                val cardHeight = cardWidth * 1.40f

                TableauArea(
                    gameState = gameState,
                    cardWidth = cardWidth,
                    cardHeight = cardHeight,
                    colSpacing = colSpacing,
                    cardBack = currentCardBack,
                    cardFace = currentCardFace,
                    selectedLocation = selectedLocation,
                    validTargets = validTargets,
                    activeHint = activeHint,
                    onCardClick = { colIdx, cardIdx ->
                        viewModel.onCardClicked(CardLocation.Tableau(colIdx, cardIdx))
                    },
                    onCardDoubleClick = { colIdx, cardIdx ->
                        viewModel.onCardDoubleClicked(CardLocation.Tableau(colIdx, cardIdx))
                    },
                    onEmptyColumnClick = { colIdx ->
                        viewModel.onEmptyColumnClicked(colIdx)
                    }
                )
            }

            Spacer(modifier = Modifier.height(3.dp))

            // 5. Compact Bottom Bar: Restart, Undo, Hint, Auto Win
            CompactGameBottomBar(
                gameState = gameState,
                activeHint = activeHint,
                isAutoCompleting = isAutoCompleting,
                onRestart = { showRestartConfirm = true },
                onUndo = { viewModel.undo() },
                onHint = { viewModel.requestHint() },
                onAutoComplete = { viewModel.triggerAutoComplete() }
            )
        }

        // Win Dialog
        if (showWinDialog) {
            WinCelebrationDialog(
                gameState = gameState,
                onNewGame = {
                    viewModel.dismissWinDialog()
                    if (activity != null) {
                        AdManager.showInterstitialIfReady(activity) {
                            viewModel.startNewGame(mode = gameState.gameMode)
                        }
                    } else {
                        viewModel.startNewGame(mode = gameState.gameMode)
                    }
                },
                onHome = {
                    viewModel.dismissWinDialog()
                    onNavigateBack()
                }
            )
        }

        // Restart Confirmation Dialog
        if (showRestartConfirm) {
            AlertDialog(
                onDismissRequest = { showRestartConfirm = false },
                title = { Text("Restart Game?") },
                text = { Text("Are you sure you want to start a new game? Current progress will be lost.") },
                confirmButton = {
                    Button(
                        onClick = {
                            showRestartConfirm = false
                            viewModel.startNewGame(mode = gameState.gameMode)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Restart")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showRestartConfirm = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun PortraitGameTopHeader(
    gameState: GameState,
    selectedLocation: CardLocation?,
    showTimer: Boolean = true,
    onBack: () -> Unit,
    onCancelSelection: () -> Unit,
    onRestart: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Back Button
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .size(34.dp)
                .background(Color.Black.copy(alpha = 0.35f), CircleShape)
                .testTag("back_button")
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = SleekSlate100,
                modifier = Modifier.size(18.dp)
            )
        }

        // Center Stats Capsule: Time, Moves, Score
        Surface(
            color = Color.Black.copy(alpha = 0.45f),
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(0.5.dp, Color.White.copy(alpha = 0.10f))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Time
                if (showTimer) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = "TIME",
                            color = SleekSlate400,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = formatTime(gameState.elapsedTimeSeconds),
                            color = SleekSlate100,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Moves
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "MOVES",
                        color = SleekSlate400,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${gameState.moveCount}",
                        color = SleekSlate100,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Score
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "SCORE",
                        color = SleekSlate400,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${gameState.score}",
                        color = SleekEmerald400,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (selectedLocation != null) {
                    Surface(
                        onClick = onCancelSelection,
                        color = Color(0xFFFFC107).copy(alpha = 0.25f),
                        border = BorderStroke(0.5.dp, Color(0xFFFFC107).copy(alpha = 0.6f)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Cancel ✕",
                            color = Color(0xFFFFE082),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                        )
                    }
                }
            }
        }

        // Restart Quick Button
        IconButton(
            onClick = onRestart,
            modifier = Modifier
                .size(34.dp)
                .background(Color.Black.copy(alpha = 0.35f), CircleShape)
                .testTag("restart_header_button")
        ) {
            Icon(
                Icons.Default.Refresh,
                contentDescription = "Restart",
                tint = SleekSlate200,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun PortraitTopCardRow(
    gameState: GameState,
    cardWidth: Dp,
    cardHeight: Dp,
    colSpacing: Dp,
    cardBack: com.solitaire.hyper.card.games.ui.customization.CardBackTheme,
    cardFace: com.solitaire.hyper.card.games.ui.customization.CardFaceTheme,
    selectedLocation: CardLocation?,
    validTargets: Set<CardLocation>,
    activeHint: com.solitaire.hyper.card.games.game.model.Hint?,
    onStockClick: () -> Unit,
    onWasteClick: () -> Unit,
    onWasteDoubleClick: () -> Unit,
    onFoundationClick: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = colSpacing),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Col 0: Stock Pile
        Box(
            modifier = Modifier
                .size(cardWidth, cardHeight)
                .clickable(onClick = onStockClick)
                .testTag("stock_pile")
        ) {
            if (gameState.stock.isNotEmpty()) {
                CardView(
                    card = gameState.stock.last().copy(isFaceUp = false),
                    cardBack = cardBack,
                    cardFace = cardFace,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(2.dp)
                        .background(Color.Black.copy(alpha = 0.8f), RoundedCornerShape(3.dp))
                        .padding(horizontal = 3.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = "${gameState.stock.size}",
                        color = Color.White,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                CardSlotPlaceholder(iconSymbol = "↺", label = "RESET")
            }
        }

        // Col 1: Waste Pile
        Box(
            modifier = Modifier
                .size(cardWidth, cardHeight)
                .testTag("waste_pile")
        ) {
            val wasteTop = gameState.waste.lastOrNull()
            if (wasteTop != null) {
                val isSelected = selectedLocation is CardLocation.Waste
                val isHinted = activeHint?.from is CardLocation.Waste
                CardView(
                    card = wasteTop,
                    cardBack = cardBack,
                    cardFace = cardFace,
                    isSelected = isSelected,
                    isHinted = isHinted,
                    onClick = onWasteClick,
                    onDoubleClick = onWasteDoubleClick,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                CardSlotPlaceholder(label = "WASTE")
            }
        }

        // Col 2: Spacer to maintain the exact 7-column grid matching Tableau
        Box(modifier = Modifier.size(cardWidth, cardHeight))

        // Cols 3, 4, 5, 6: The 4 Foundation Suit slots (♠, ♥, ♦, ♣)
        val symbols = listOf("♠", "♥", "♦", "♣")
        for (i in 0 until 4) {
            val pile = gameState.foundations.getOrNull(i) ?: emptyList()
            val topCard = pile.lastOrNull()
            val isSelected = (selectedLocation as? CardLocation.Foundation)?.index == i
            val isValidTarget = validTargets.contains(CardLocation.Foundation(i))
            val isHinted = (activeHint?.to as? CardLocation.Foundation)?.index == i ||
                    (activeHint?.from as? CardLocation.Foundation)?.index == i

            Box(
                modifier = Modifier
                    .size(cardWidth, cardHeight)
                    .clickable { onFoundationClick(i) }
                    .testTag("foundation_pile_$i")
            ) {
                if (topCard != null) {
                    CardView(
                        card = topCard,
                        cardBack = cardBack,
                        cardFace = cardFace,
                        isSelected = isSelected,
                        isValidTarget = isValidTarget,
                        isHinted = isHinted,
                        onClick = { onFoundationClick(i) },
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    CardSlotPlaceholder(
                        iconSymbol = symbols[i],
                        label = if (isValidTarget) "MATCH" else null,
                        isValidTarget = isValidTarget
                    )
                }
            }
        }
    }
}

@Composable
fun TableauArea(
    gameState: GameState,
    cardWidth: Dp,
    cardHeight: Dp,
    colSpacing: Dp,
    cardBack: com.solitaire.hyper.card.games.ui.customization.CardBackTheme,
    cardFace: com.solitaire.hyper.card.games.ui.customization.CardFaceTheme,
    selectedLocation: CardLocation?,
    validTargets: Set<CardLocation>,
    activeHint: com.solitaire.hyper.card.games.game.model.Hint?,
    onCardClick: (colIdx: Int, cardIdx: Int) -> Unit,
    onCardDoubleClick: (colIdx: Int, cardIdx: Int) -> Unit,
    onEmptyColumnClick: (colIdx: Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = colSpacing),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        for (colIdx in 0 until 7) {
            val column = gameState.tableau.getOrNull(colIdx) ?: emptyList()
            TableauColumnView(
                cards = column,
                colIndex = colIdx,
                cardWidth = cardWidth,
                cardHeight = cardHeight,
                cardBack = cardBack,
                cardFace = cardFace,
                selectedLocation = selectedLocation,
                validTargets = validTargets,
                activeHint = activeHint,
                onCardClick = { cardIdx -> onCardClick(colIdx, cardIdx) },
                onCardDoubleClick = { cardIdx -> onCardDoubleClick(colIdx, cardIdx) },
                onEmptyClick = { onEmptyColumnClick(colIdx) }
            )
        }
    }
}

@Composable
fun TableauColumnView(
    cards: List<Card>,
    colIndex: Int,
    cardWidth: Dp,
    cardHeight: Dp,
    cardBack: com.solitaire.hyper.card.games.ui.customization.CardBackTheme,
    cardFace: com.solitaire.hyper.card.games.ui.customization.CardFaceTheme,
    selectedLocation: CardLocation?,
    validTargets: Set<CardLocation>,
    activeHint: com.solitaire.hyper.card.games.game.model.Hint?,
    onCardClick: (cardIdx: Int) -> Unit,
    onCardDoubleClick: (cardIdx: Int) -> Unit,
    onEmptyClick: () -> Unit
) {
    val isColumnValidTarget = validTargets.contains(CardLocation.Tableau(colIndex))

    Box(
        modifier = Modifier
            .width(cardWidth)
            .fillMaxSize()
    ) {
        if (cards.isEmpty()) {
            Box(
                modifier = Modifier
                    .size(cardWidth, cardHeight)
                    .clickable(onClick = onEmptyClick)
            ) {
                CardSlotPlaceholder(
                    iconSymbol = "K",
                    label = if (isColumnValidTarget) "KING" else null,
                    isValidTarget = isColumnValidTarget
                )
            }
        } else {
            var yOffset = 0.dp
            for (cardIdx in cards.indices) {
                val card = cards[cardIdx]
                val isSelected = selectedLocation is CardLocation.Tableau &&
                        selectedLocation.columnIndex == colIndex &&
                        cardIdx >= selectedLocation.cardIndex
                val isTarget = isColumnValidTarget && cardIdx == cards.lastIndex && !isSelected
                val isHinted = activeHint?.from is CardLocation.Tableau &&
                        (activeHint.from as CardLocation.Tableau).columnIndex == colIndex &&
                        (activeHint.from as CardLocation.Tableau).cardIndex == cardIdx

                CardView(
                    card = card,
                    cardBack = cardBack,
                    cardFace = cardFace,
                    isSelected = isSelected,
                    isValidTarget = isTarget,
                    isHinted = isHinted,
                    onClick = { onCardClick(cardIdx) },
                    onDoubleClick = { onCardDoubleClick(cardIdx) },
                    modifier = Modifier
                        .offset(y = yOffset)
                        .size(cardWidth, cardHeight)
                )

                // Dynamically offset next card: face-down cards closer together, face-up spaced out
                yOffset += if (card.isFaceUp) 18.dp else 10.dp
            }
        }
    }
}

@Composable
fun CompactGameBottomBar(
    gameState: GameState,
    activeHint: com.solitaire.hyper.card.games.game.model.Hint?,
    isAutoCompleting: Boolean,
    onRestart: () -> Unit,
    onUndo: () -> Unit,
    onHint: () -> Unit,
    onAutoComplete: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(38.dp),
        color = Color.Black.copy(alpha = 0.45f),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(0.5.dp, Color.White.copy(alpha = 0.08f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Restart Button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable(onClick = onRestart)
                    .padding(horizontal = 6.dp, vertical = 4.dp)
                    .testTag("restart_button")
            ) {
                Icon(
                    Icons.Default.Refresh,
                    contentDescription = "Restart",
                    tint = SleekSlate300,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "Restart",
                    color = SleekSlate300,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Center: Hint message if active
            if (activeHint != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        Icons.Default.Lightbulb,
                        contentDescription = null,
                        tint = SleekEmerald400,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = activeHint.description,
                        color = SleekEmerald400,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1
                    )
                }
            } else {
                Spacer(Modifier.width(1.dp))
            }

            // Right: Undo, Hint, Auto Win
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Undo
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable(onClick = onUndo)
                        .padding(horizontal = 6.dp, vertical = 4.dp)
                        .testTag("undo_button")
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.Undo,
                        contentDescription = "Undo",
                        tint = SleekSlate100,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "Undo",
                        color = SleekSlate100,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Hint
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable(onClick = onHint)
                        .padding(horizontal = 6.dp, vertical = 4.dp)
                        .testTag("hint_button")
                ) {
                    Icon(
                        Icons.Default.Lightbulb,
                        contentDescription = "Hint",
                        tint = SleekEmerald400,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "Hint",
                        color = SleekEmerald400,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Auto Win
                if (gameState.canAutoComplete) {
                    Button(
                        onClick = onAutoComplete,
                        enabled = !isAutoCompleting,
                        colors = ButtonDefaults.buttonColors(containerColor = SleekEmerald500),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier
                            .height(28.dp)
                            .testTag("autocomplete_button")
                    ) {
                        Text(
                            text = if (isAutoCompleting) "Finishing..." else "AUTO WIN",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WinCelebrationDialog(
    gameState: GameState,
    onNewGame: () -> Unit,
    onHome: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {},
        confirmButton = {
            Button(
                onClick = onNewGame,
                colors = ButtonDefaults.buttonColors(containerColor = SleekEmerald500),
                modifier = Modifier.testTag("win_new_game_button")
            ) {
                Text("Play Again", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onHome,
                modifier = Modifier.testTag("win_home_button")
            ) {
                Text("Main Menu", color = SleekSlate300)
            }
        },
        title = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .background(SleekEmerald400, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = "Crown",
                        tint = SleekHeaderDark,
                        modifier = Modifier.size(34.dp)
                    )
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "VICTORY!",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = SleekEmerald400
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Congratulations! You conquered this Solitaire deal!",
                    fontSize = 14.sp,
                    color = SleekSlate300
                )
                Spacer(Modifier.height(14.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.White.copy(alpha = 0.05f),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Time", fontSize = 11.sp, color = SleekEmerald400)
                            Text(formatTime(gameState.elapsedTimeSeconds), fontWeight = FontWeight.Bold, color = SleekSlate100)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Moves", fontSize = 11.sp, color = SleekEmerald400)
                            Text("${gameState.moveCount}", fontWeight = FontWeight.Bold, color = SleekSlate100)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Score", fontSize = 11.sp, color = SleekEmerald400)
                            Text("${gameState.score}", fontWeight = FontWeight.Bold, color = SleekSlate100)
                        }
                    }
                }
            }
        }
    )
}

fun formatTime(seconds: Long): String {
    val m = seconds / 60
    val s = seconds % 60
    return String.format("%02d:%02d", m, s)
}
