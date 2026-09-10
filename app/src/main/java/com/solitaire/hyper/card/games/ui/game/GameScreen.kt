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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.ShoppingCart
import com.solitaire.hyper.card.games.ui.components.AiCoachDialog
import com.solitaire.hyper.card.games.ui.shop.ShopDialog
import com.solitaire.hyper.card.games.ui.components.TutorialDemoOverlay
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.zIndex
import kotlin.math.roundToInt
import com.solitaire.hyper.card.games.ui.components.WinningCascadeCanvas
import com.solitaire.hyper.card.games.ui.components.RulesDialog
import com.solitaire.hyper.card.games.ads.AdManager
import com.solitaire.hyper.card.games.ads.BannerAdView
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solitaire.hyper.card.games.game.model.Card
import com.solitaire.hyper.card.games.game.model.CardLocation
import com.solitaire.hyper.card.games.game.model.GameMode
import com.solitaire.hyper.card.games.game.model.GameState
import com.solitaire.hyper.card.games.ui.components.CardSlotPlaceholder
import com.solitaire.hyper.card.games.ui.components.CardView
import com.solitaire.hyper.card.games.ui.customization.CustomizationRegistry
import com.solitaire.hyper.card.games.ui.theme.SleekEmerald400
import com.solitaire.hyper.card.games.ui.theme.SleekEmerald500
import com.solitaire.hyper.card.games.ui.theme.SleekHeaderDark
import com.solitaire.hyper.card.games.ui.theme.SleekSlate100
import com.solitaire.hyper.card.games.ui.theme.SleekSlate200
import com.solitaire.hyper.card.games.ui.theme.SleekSlate300
import com.solitaire.hyper.card.games.ui.theme.SleekSlate400

@Composable
fun GameScreen(
    viewModel: GameViewModel,
    onNavigateBack: () -> Unit,
    onOpenSettings: () -> Unit = {},
    onOpenDaily: () -> Unit = {},
    onOpenThemes: () -> Unit = {},
    onOpenStats: () -> Unit = {},
    onOpenAchievements: () -> Unit = {}
) {
    val context = LocalContext.current
    val activity = context as? Activity

    val gameState by viewModel.gameState.collectAsState()
    val userSettings by viewModel.userSettings.collectAsState()
    val selectedLocation by viewModel.selectedLocation.collectAsState()
    val validTargets by viewModel.validTargets.collectAsState()
    val statusMessage by viewModel.statusMessage.collectAsState()
    val activeHint by viewModel.activeHint.collectAsState()
    val tutorialHint by viewModel.tutorialHint.collectAsState()
    val isTutorialActive by viewModel.isTutorialActive.collectAsState()
    val isAutoCompleting by viewModel.isAutoCompleting.collectAsState()
    val showWinDialog by viewModel.showWinDialog.collectAsState()

    val aiAnalysis by viewModel.aiAnalysis.collectAsState()
    val isAiAnalyzing by viewModel.isAiAnalyzing.collectAsState()
    var showAiCoachDialog by remember { mutableStateOf(false) }
    var showShopDialog by remember { mutableStateOf(false) }

    var showExitConfirm by remember { mutableStateOf(false) }
    var showRestartConfirm by remember { mutableStateOf(false) }
    var showPlayDialog by remember { mutableStateOf(false) }
    var showMenuDialog by remember { mutableStateOf(false) }
    var showRulesDialog by remember { mutableStateOf(false) }
    var hasDoubledCoins by remember { mutableStateOf(false) }

    val currentBackground = CustomizationRegistry.getBackground(userSettings.backgroundId)
    val currentCardBack = CustomizationRegistry.getCardBack(userSettings.cardBackId)
    val currentCardFace = CustomizationRegistry.getCardFace(userSettings.cardFaceId)

    if (showRulesDialog) {
        RulesDialog(
            onDismiss = { showRulesDialog = false },
            onShowDemo = { viewModel.showTutorialNow() }
        )
    }

    // User requested: prompt confirmation when user tries to go back
    BackHandler {
        showExitConfirm = true
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(currentBackground.brush)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        val availableWidth = maxWidth
        val colSpacing = 4.dp
        val cardWidth = ((availableWidth - (colSpacing * 8)) / 7).coerceIn(36.dp, 56.dp)
        val cardHeight = cardWidth * 1.38f
        val density = androidx.compose.ui.platform.LocalDensity.current
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            // 1. Top Header: Menu (left), Score - Time - Moves (center), Theme/Palette (right)
            PortraitGameTopHeader(
                gameState = gameState,
                showTimer = userSettings.showTimer,
                onMenuClick = { showMenuDialog = true },
                onThemesClick = onOpenThemes,
                onAiCoachClick = {
                    viewModel.requestAiCoachAnalysis(onOutOfTokens = { showShopDialog = true })
                    showAiCoachDialog = true
                },
                selectedLocation = selectedLocation,
                onCancelSelection = { viewModel.clearSelection() }
            )

            Spacer(modifier = Modifier.height(3.dp))

            // 2. Upper Card Row: Foundations 0..3 on LEFT, Gap, Waste, Stock on RIGHT (or reversed if left-handed)
            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                val availableWidth = maxWidth
                val colSpacing = 4.dp
                val cardWidth = ((availableWidth - (colSpacing * 8)) / 7).coerceIn(36.dp, 56.dp)
                val cardHeight = cardWidth * 1.38f

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
                    largePrint = userSettings.largePrintMode,
                    leftHanded = userSettings.leftHandedMode,
                    onStockClick = { viewModel.onStockClicked() },
                    onWasteClick = { viewModel.onCardClicked(CardLocation.Waste) },
                    onWasteDoubleClick = { viewModel.onCardDoubleClicked(CardLocation.Waste) },
                    onFoundationClick = { index -> viewModel.onFoundationClicked(index) },
                    onCardDrop = { from, to -> viewModel.onCardDropped(from, to) }
                )
            }

            // 3. Status or Active Hint banner
            if (activeHint != null || (!statusMessage.isNullOrBlank() && statusMessage != "Tap any face-up card to select")) {
                Spacer(modifier = Modifier.height(2.dp))
                Surface(
                    color = Color.Black.copy(alpha = 0.50f),
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

            Spacer(modifier = Modifier.height(3.dp))

            // 4. Main Tableau Area (dynamically budgeted to fill exact available vertical space)
            BoxWithConstraints(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                val availableWidth = maxWidth
                val colSpacing = 4.dp
                val cardWidth = ((availableWidth - (colSpacing * 8)) / 7).coerceIn(36.dp, 56.dp)
                val cardHeight = cardWidth * 1.38f

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
                    largePrint = userSettings.largePrintMode,
                    onCardClick = { colIdx, cardIdx ->
                        viewModel.onCardClicked(CardLocation.Tableau(colIdx, cardIdx))
                    },
                    onCardDoubleClick = { colIdx, cardIdx ->
                        viewModel.onCardDoubleClicked(CardLocation.Tableau(colIdx, cardIdx))
                    },
                    onEmptyColumnClick = { colIdx ->
                        viewModel.onEmptyColumnClicked(colIdx)
                    },
                    onCardDrop = { from, to ->
                        viewModel.onCardDropped(from, to)
                    }
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            // 5. Floating Bottom Navigation Bar: Settings, Magic Wand, Play, Hint, Undo & Solitaire emblem
            CompactGameBottomBar(
                gameState = gameState,
                isAutoCompleting = isAutoCompleting,
                onSettings = onOpenSettings,
                onMagicWand = { viewModel.useMagicWand(onOutOfWands = { showShopDialog = true }) },
                onPlay = { showPlayDialog = true },
                onHint = { viewModel.requestHint() },
                onUndo = { viewModel.undo() },
                onAutoComplete = { viewModel.triggerAutoComplete() }
            )

            // Bottom Banner Ad
            BannerAdView(
                isAdFree = userSettings.isAdFreeActive(),
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Win Celebratory Waterfall (Iconic Cascade Animation)
        if (showWinDialog) {
            WinningCascadeCanvas(
                score = gameState.score,
                timeSeconds = gameState.elapsedTimeSeconds,
                moves = gameState.moveCount,
                coinsEarned = 200,
                hasDoubledCoins = hasDoubledCoins,
                onWatchAdDouble = {
                    if (activity != null) {
                        AdManager.showRewardedAd(
                            activity = activity,
                            onRewardEarned = {
                                viewModel.awardDoubleWinCoins()
                                hasDoubledCoins = true
                            },
                            onDismissOrFailed = {}
                        )
                    }
                },
                onPlayAgain = {
                    viewModel.dismissWinDialog()
                    hasDoubledCoins = false
                    if (activity != null) {
                        AdManager.showInterstitialIfReady(activity, isAdFree = userSettings.isAdFreeActive()) {
                            viewModel.startNewGame(mode = gameState.gameMode)
                        }
                    } else {
                        viewModel.startNewGame(mode = gameState.gameMode)
                    }
                },
                onHome = {
                    viewModel.dismissWinDialog()
                    hasDoubledCoins = false
                    if (activity != null) {
                        AdManager.showInterstitialIfReady(activity, isAdFree = userSettings.isAdFreeActive()) {
                            onNavigateBack()
                        }
                    } else {
                        onNavigateBack()
                    }
                },
                onDismiss = {
                    viewModel.dismissWinDialog()
                    hasDoubledCoins = false
                }
            )
        }

        // Exit Game Confirmation Dialog
        if (showExitConfirm) {
            AlertDialog(
                onDismissRequest = { showExitConfirm = false },
                title = {
                    Text(
                        text = "Exit Game?",
                        fontWeight = FontWeight.Bold,
                        color = SleekSlate100
                    )
                },
                text = {
                    Text(
                        text = "Your current game progress will be automatically saved. Are you sure you want to return to the home screen?",
                        color = SleekSlate300,
                        fontSize = 13.5.sp
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showExitConfirm = false
                            onNavigateBack()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SleekEmerald500),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Exit to Menu", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showExitConfirm = false }) {
                        Text("Resume", color = SleekSlate300)
                    }
                }
            )
        }

        // Restart Confirmation Dialog
        if (showRestartConfirm) {
            AlertDialog(
                onDismissRequest = { showRestartConfirm = false },
                title = { Text("Restart Game?", fontWeight = FontWeight.Bold, color = SleekSlate100) },
                text = { Text("Are you sure you want to restart this deal? Current moves and timer will reset.", color = SleekSlate300, fontSize = 13.5.sp) },
                confirmButton = {
                    Button(
                        onClick = {
                            showRestartConfirm = false
                            viewModel.startNewGame(mode = gameState.gameMode)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SleekEmerald500),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Restart", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showRestartConfirm = false }) {
                        Text("Cancel", color = SleekSlate300)
                    }
                }
            )
        }

        // Play / New Game Dialog
        if (showPlayDialog) {
            var isWinnableChecked by remember { mutableStateOf(false) }
            var isVegasChecked by remember { mutableStateOf(false) }

            AlertDialog(
                onDismissRequest = { showPlayDialog = false },
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, tint = SleekEmerald400)
                        Text("New Deal", fontWeight = FontWeight.Bold, color = SleekSlate100)
                    }
                },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Game Modes & Deals:",
                            color = SleekSlate300,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        // Winnable Deal Toggle Card
                        Surface(
                            onClick = { isWinnableChecked = !isWinnableChecked },
                            color = if (isWinnableChecked) Color(0xFF1B3D2F) else Color.White.copy(alpha = 0.05f),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, if (isWinnableChecked) SleekEmerald400 else Color.White.copy(alpha = 0.15f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "🏆 Winnable Deal",
                                    color = if (isWinnableChecked) SleekEmerald400 else SleekSlate200,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    if (isWinnableChecked) "ON ✓" else "OFF",
                                    color = if (isWinnableChecked) SleekEmerald400 else SleekSlate400,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Vegas Scoring Toggle Card
                        Surface(
                            onClick = { isVegasChecked = !isVegasChecked },
                            color = if (isVegasChecked) Color(0xFF2C1E3D) else Color.White.copy(alpha = 0.05f),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, if (isVegasChecked) Color(0xFFC084FC) else Color.White.copy(alpha = 0.15f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "🎰 Vegas Mode",
                                    color = if (isVegasChecked) Color(0xFFC084FC) else SleekSlate200,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    if (isVegasChecked) "ON ✓" else "OFF",
                                    color = if (isVegasChecked) Color(0xFFC084FC) else SleekSlate400,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(Modifier.height(4.dp))

                        Button(
                            onClick = {
                                showPlayDialog = false
                                viewModel.startNewGame(
                                    mode = GameMode.DRAW_1,
                                    isWinnableDeal = isWinnableChecked,
                                    isVegasScoring = isVegasChecked
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SleekEmerald500),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Draw 1 Card", fontWeight = FontWeight.Bold, color = Color.White)
                        }

                        Button(
                            onClick = {
                                showPlayDialog = false
                                viewModel.startNewGame(
                                    mode = GameMode.DRAW_3,
                                    isWinnableDeal = isWinnableChecked,
                                    isVegasScoring = isVegasChecked
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E3A2F)),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Draw 3 Cards", fontWeight = FontWeight.Bold, color = SleekSlate100)
                        }

                        OutlinedButton(
                            onClick = {
                                showPlayDialog = false
                                showRestartConfirm = true
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, SleekEmerald400.copy(alpha = 0.4f))
                        ) {
                            Text("Replay This Deal", color = SleekEmerald400, fontWeight = FontWeight.SemiBold)
                        }
                    }
                },
                confirmButton = {},
                dismissButton = {
                    TextButton(onClick = { showPlayDialog = false }) {
                        Text("Cancel", color = SleekSlate300)
                    }
                }
            )
        }

        // ☰ In-Game Hamburger Menu Dialog
        if (showMenuDialog) {
            AlertDialog(
                onDismissRequest = { showMenuDialog = false },
                title = {
                    Text("Solitaire Menu", fontWeight = FontWeight.Bold, color = SleekSlate100)
                },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        GameMenuRow(icon = Icons.Default.Home, title = "Main Menu") {
                            showMenuDialog = false
                            showExitConfirm = true
                        }
                        GameMenuRow(icon = Icons.Default.PlayArrow, title = "New Deal") {
                            showMenuDialog = false
                            showPlayDialog = true
                        }
                        GameMenuRow(icon = Icons.Default.Refresh, title = "Replay Deal") {
                            showMenuDialog = false
                            showRestartConfirm = true
                        }
                        GameMenuRow(icon = Icons.Default.MenuBook, title = "Rules & How to Play") {
                            showMenuDialog = false
                            showRulesDialog = true
                        }
                        GameMenuRow(icon = Icons.Default.School, title = "🎓 Visual Tutorial (Show Demo)") {
                            showMenuDialog = false
                            viewModel.showTutorialNow()
                        }
                        GameMenuRow(icon = Icons.Default.DateRange, title = "Daily Challenges") {
                            showMenuDialog = false
                            onOpenDaily()
                        }
                        GameMenuRow(icon = Icons.Default.BarChart, title = "Statistics") {
                            showMenuDialog = false
                            onOpenStats()
                        }
                        GameMenuRow(icon = Icons.Default.EmojiEvents, title = "Achievements") {
                            showMenuDialog = false
                            onOpenAchievements()
                        }
                        GameMenuRow(icon = Icons.Default.SmartToy, title = "🤖 AI Solitaire Coach") {
                            showMenuDialog = false
                            viewModel.requestAiCoachAnalysis(onOutOfTokens = { showShopDialog = true })
                            showAiCoachDialog = true
                        }
                        GameMenuRow(icon = Icons.Default.ShoppingCart, title = "🪙 Shop & VIP Store") {
                            showMenuDialog = false
                            showShopDialog = true
                        }
                        GameMenuRow(icon = Icons.Default.Palette, title = "Themes & Customization") {
                            showMenuDialog = false
                            onOpenThemes()
                        }
                        GameMenuRow(icon = Icons.Default.Settings, title = "Settings") {
                            showMenuDialog = false
                            onOpenSettings()
                        }
                        GameMenuRow(icon = Icons.Default.ExitToApp, title = "Exit Game", isDestructive = true) {
                            showMenuDialog = false
                            showExitConfirm = true
                        }
                    }
                },
                confirmButton = {},
                dismissButton = {
                    TextButton(onClick = { showMenuDialog = false }) {
                        Text("Close", color = SleekSlate300)
                    }
                }
            )
        }

        // AI Solitaire Coach Dialog
        if (showAiCoachDialog || aiAnalysis != null) {
            AiCoachDialog(
                analysis = aiAnalysis,
                isLoading = isAiAnalyzing,
                userSettings = userSettings,
                userPrefs = viewModel.userPrefs,
                onApplyMove = { viewModel.applyAiRecommendedMove() },
                onDismiss = {
                    showAiCoachDialog = false
                    viewModel.dismissAiCoach()
                }
            )
        }

        // In-App Store & VIP Dialog
        if (showShopDialog) {
            ShopDialog(
                userSettings = userSettings,
                userPrefs = viewModel.userPrefs,
                onDismiss = { showShopDialog = false }
            )
        }

        // 6. Interactive Animated Tutorial Demo Overlay (Auto Guide for beginners)
        val tHint = tutorialHint
        if (isTutorialActive && tHint != null) {
            val fromOffset = remember(tHint, availableWidth, userSettings.leftHandedMode) {
                calculateTutorialCardCenter(
                    location = tHint.from,
                    gameState = gameState,
                    screenWidth = availableWidth,
                    cardWidth = cardWidth,
                    cardHeight = cardHeight,
                    colSpacing = colSpacing,
                    leftHanded = userSettings.leftHandedMode,
                    density = density
                )
            }
            val toOffset = remember(tHint, availableWidth, userSettings.leftHandedMode) {
                calculateTutorialCardCenter(
                    location = tHint.to,
                    gameState = gameState,
                    screenWidth = availableWidth,
                    cardWidth = cardWidth,
                    cardHeight = cardHeight,
                    colSpacing = colSpacing,
                    leftHanded = userSettings.leftHandedMode,
                    density = density
                )
            }

            TutorialDemoOverlay(
                hint = tHint,
                fromOffset = fromOffset,
                toOffset = toOffset,
                cardWidth = cardWidth,
                cardHeight = cardHeight,
                cardBack = currentCardBack,
                cardFace = currentCardFace,
                onApplyMove = { viewModel.applyTutorialMove() },
                onDismiss = { viewModel.dismissTutorial() }
            )
        }
    }
}

private fun calculateTutorialCardCenter(
    location: CardLocation,
    gameState: GameState,
    screenWidth: Dp,
    cardWidth: Dp,
    cardHeight: Dp,
    colSpacing: Dp,
    leftHanded: Boolean,
    density: androidx.compose.ui.unit.Density
): Offset {
    return with(density) {
        val totalPaddingHorizontal = 12.dp + (colSpacing * 2)
        val rowWidth = screenWidth - totalPaddingHorizontal
        val spaceBetweenCols = ((rowWidth - cardWidth * 7) / 6).coerceAtLeast(0.dp)
        val colStep = cardWidth + spaceBetweenCols

        fun colCenterX(col: Int): Float {
            val left = 6.dp + colSpacing + (colStep * col) + (cardWidth / 2)
            return left.toPx()
        }

        val topRowY = 46.dp + 3.dp + (cardHeight / 2)
        val tableauTopY = 46.dp + 3.dp + cardHeight + 8.dp

        when (location) {
            is CardLocation.Stock -> {
                val c = if (leftHanded) 0 else 6
                Offset(colCenterX(c), topRowY.toPx())
            }
            is CardLocation.Waste -> {
                val c = if (leftHanded) 1 else 5
                Offset(colCenterX(c), topRowY.toPx())
            }
            is CardLocation.Foundation -> {
                val c = if (leftHanded) 3 + location.index else location.index
                Offset(colCenterX(c), topRowY.toPx())
            }
            is CardLocation.Tableau -> {
                val c = location.columnIndex.coerceIn(0, 6)
                val pile = gameState.tableau.getOrNull(c) ?: emptyList()
                val cardIdx = location.cardIndex ?: maxOf(0, pile.size - 1)
                val clampedIdx = cardIdx.coerceIn(0, maxOf(0, pile.size - 1))
                val yStep = 18.dp
                val yOffset = tableauTopY + (yStep * clampedIdx) + (cardHeight / 2)
                Offset(colCenterX(c), yOffset.toPx())
            }
        }
    }
}

@Composable
fun GameMenuRow(
    icon: ImageVector,
    title: String,
    isDestructive: Boolean = false,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = Color.Transparent,
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isDestructive) MaterialTheme.colorScheme.error else SleekEmerald400,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = title,
                color = if (isDestructive) MaterialTheme.colorScheme.error else SleekSlate100,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

/**
 * Top App Bar matching the screenshot:
 * Left: Circular button with ☰ Menu
 * Center: SCORE, TIME, MOVES vertical columns in crisp dark green capsule
 * Right: Circular button with 🎨 Paint Palette with notification dot
 */
@Composable
fun PortraitGameTopHeader(
    gameState: GameState,
    showTimer: Boolean = true,
    onMenuClick: () -> Unit,
    onThemesClick: () -> Unit,
    onAiCoachClick: () -> Unit,
    selectedLocation: CardLocation?,
    onCancelSelection: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 2.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left: ☰ Menu Circular Button
        IconButton(
            onClick = onMenuClick,
            modifier = Modifier
                .size(36.dp)
                .background(Color.Black.copy(alpha = 0.35f), CircleShape)
                .border(1.dp, Color.White.copy(alpha = 0.12f), CircleShape)
                .testTag("menu_button")
        ) {
            Icon(
                Icons.Default.Menu,
                contentDescription = "Menu",
                tint = SleekSlate100,
                modifier = Modifier.size(20.dp)
            )
        }

        // Center: Three neat stats columns: SCORE | TIME | MOVES
        Surface(
            color = Color.Black.copy(alpha = 0.40f),
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(0.5.dp, Color.White.copy(alpha = 0.12f))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // SCORE
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "SCORE",
                        color = SleekSlate400,
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "${gameState.score}",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // TIME
                if (showTimer) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "TIME",
                            color = SleekSlate400,
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = formatTime(gameState.elapsedTimeSeconds),
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // MOVES
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "MOVES",
                        color = SleekSlate400,
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "${gameState.moveCount}",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Selection cancel pill if active
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
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }

        // Right: AI Coach & Themes Palette Buttons
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 🤖 AI Solitaire Coach Button
            IconButton(
                onClick = onAiCoachClick,
                modifier = Modifier
                    .size(36.dp)
                    .background(Color(0xFF2E1065).copy(alpha = 0.85f), CircleShape)
                    .border(1.2.dp, Color(0xFF8B5CF6), CircleShape)
                    .testTag("ai_coach_button")
            ) {
                Icon(
                    Icons.Default.SmartToy,
                    contentDescription = "AI Coach",
                    tint = Color(0xFFDDD6FE),
                    modifier = Modifier.size(19.dp)
                )
            }

            // 🎨 Themes Palette Button with Notification Dot
            Box(
                modifier = Modifier.size(36.dp)
            ) {
                IconButton(
                    onClick = onThemesClick,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.35f), CircleShape)
                        .border(1.dp, Color.White.copy(alpha = 0.12f), CircleShape)
                        .testTag("themes_button")
                ) {
                    Icon(
                        Icons.Default.Palette,
                        contentDescription = "Themes",
                        tint = SleekSlate100,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Small red badge dot
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(Color(0xFFE53935), CircleShape)
                        .border(1.dp, Color.White, CircleShape)
                        .align(Alignment.TopEnd)
                )
            }
        }
    }
}

/**
 * Upper Card Area exactly matching the screenshot:
 * - Left 4 columns (Cols 0, 1, 2, 3): 4 Foundation slots with faint "A" watermark inside!
 * - Column 4: Empty space (aligns with Tableau Col 4)
 * - Column 5: Waste Pile (aligns with Tableau Col 5)
 * - Column 6: Stock Pile on FAR RIGHT (with red card back and remaining count badge "24")
 */
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
    largePrint: Boolean = false,
    leftHanded: Boolean = false,
    onStockClick: () -> Unit,
    onWasteClick: () -> Unit,
    onWasteDoubleClick: () -> Unit,
    onFoundationClick: (Int) -> Unit,
    onCardDrop: ((from: CardLocation, to: CardLocation) -> Unit)? = null
) {
    val density = androidx.compose.ui.platform.LocalDensity.current

    // Foundation Piles composable block
    val foundationPilesContent: @Composable () -> Unit = {
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
                        largePrint = largePrint,
                        onClick = { onFoundationClick(i) },
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    CardSlotPlaceholder(
                        iconSymbol = "A",
                        label = if (isValidTarget) "MATCH" else null,
                        isValidTarget = isValidTarget
                    )
                }
            }
        }
    }

    // Waste Pile with Drag-and-Drop capability
    var wasteDragOffset by remember { mutableStateOf(Offset.Zero) }
    var isDraggingWaste by remember { mutableStateOf(false) }

    val wastePileContent: @Composable () -> Unit = {
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
                    largePrint = largePrint,
                    onClick = if (!isDraggingWaste) onWasteClick else null,
                    onDoubleClick = onWasteDoubleClick,
                    modifier = Modifier
                        .fillMaxSize()
                        .offset {
                            if (isDraggingWaste) {
                                IntOffset(wasteDragOffset.x.roundToInt(), wasteDragOffset.y.roundToInt())
                            } else {
                                IntOffset.Zero
                            }
                        }
                        .zIndex(if (isDraggingWaste) 200f else 1f)
                        .pointerInput(wasteTop.id) {
                            detectDragGestures(
                                onDragStart = {
                                    isDraggingWaste = true
                                    wasteDragOffset = Offset.Zero
                                },
                                onDrag = { change, dragAmount ->
                                    change.consume()
                                    wasteDragOffset += dragAmount
                                },
                                onDragEnd = {
                                    val dist = wasteDragOffset.getDistance()
                                    if (dist < 14f) {
                                        onWasteClick()
                                    } else if (onCardDrop != null) {
                                        val colStepPx = with(density) { (cardWidth + colSpacing).toPx() }
                                        val wasteColIndex = if (leftHanded) 1 else 5
                                        val colDelta = (wasteDragOffset.x / colStepPx).roundToInt()
                                        val targetColIdx = (wasteColIndex + colDelta).coerceIn(0, 6)

                                        val dragDownThreshold = with(density) { 40.dp.toPx() }
                                        if (wasteDragOffset.y > dragDownThreshold) {
                                            // Dropped into tableau
                                            onCardDrop(CardLocation.Waste, CardLocation.Tableau(targetColIdx))
                                        } else {
                                            // Dropped towards foundation
                                            val targetFIdx = if (leftHanded) {
                                                (colDelta + 1 - 3).coerceIn(0, 3)
                                            } else {
                                                (5 + colDelta).coerceIn(0, 3)
                                            }
                                            onCardDrop(CardLocation.Waste, CardLocation.Foundation(targetFIdx))
                                        }
                                    }
                                    isDraggingWaste = false
                                    wasteDragOffset = Offset.Zero
                                },
                                onDragCancel = {
                                    isDraggingWaste = false
                                    wasteDragOffset = Offset.Zero
                                }
                            )
                        }
                )
            } else {
                CardSlotPlaceholder()
            }
        }
    }

    // Stock Pile block
    val stockPileContent: @Composable () -> Unit = {
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
                    largePrint = largePrint,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(2.dp)
                        .background(Color.Black.copy(alpha = 0.85f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = "${gameState.stock.size}",
                        color = Color.White,
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                CardSlotPlaceholder(iconSymbol = "↺", label = "DEAL")
            }
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = colSpacing),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (leftHanded) {
            // Left-Handed Mode: Stock & Waste on left, Foundations on right
            stockPileContent()
            wastePileContent()
            Box(modifier = Modifier.size(cardWidth, cardHeight)) // Spacer
            foundationPilesContent()
        } else {
            // Standard Mode: Foundations on left, Waste & Stock on right
            foundationPilesContent()
            Box(modifier = Modifier.size(cardWidth, cardHeight)) // Spacer
            wastePileContent()
            stockPileContent()
        }
    }
}

/**
 * Tableau Area containing the 7 columns:
 * Supports interactive drag-and-drop of cards and stacks, dynamic compression, and 1-tap moves.
 */
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
    largePrint: Boolean = false,
    onCardClick: (colIdx: Int, cardIdx: Int) -> Unit,
    onCardDoubleClick: (colIdx: Int, cardIdx: Int) -> Unit,
    onEmptyColumnClick: (colIdx: Int) -> Unit,
    onCardDrop: (from: CardLocation, to: CardLocation) -> Unit
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
                colSpacing = colSpacing,
                cardBack = cardBack,
                cardFace = cardFace,
                selectedLocation = selectedLocation,
                validTargets = validTargets,
                activeHint = activeHint,
                largePrint = largePrint,
                onCardClick = { cardIdx -> onCardClick(colIdx, cardIdx) },
                onCardDoubleClick = { cardIdx -> onCardDoubleClick(colIdx, cardIdx) },
                onEmptyClick = { onEmptyColumnClick(colIdx) },
                onCardDrop = onCardDrop
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
    colSpacing: Dp,
    cardBack: com.solitaire.hyper.card.games.ui.customization.CardBackTheme,
    cardFace: com.solitaire.hyper.card.games.ui.customization.CardFaceTheme,
    selectedLocation: CardLocation?,
    validTargets: Set<CardLocation>,
    activeHint: com.solitaire.hyper.card.games.game.model.Hint?,
    largePrint: Boolean = false,
    onCardClick: (cardIdx: Int) -> Unit,
    onCardDoubleClick: (cardIdx: Int) -> Unit,
    onEmptyClick: () -> Unit,
    onCardDrop: (from: CardLocation, to: CardLocation) -> Unit
) {
    val isColumnValidTarget = validTargets.contains(CardLocation.Tableau(colIndex))
    val density = androidx.compose.ui.platform.LocalDensity.current

    var dragOffset by remember { mutableStateOf(Offset.Zero) }
    var isDragging by remember { mutableStateOf(false) }
    var draggedFromCardIdx by remember { mutableStateOf<Int?>(null) }

    BoxWithConstraints(
        modifier = Modifier
            .width(cardWidth)
            .fillMaxSize()
    ) {
        val maxAvailH = maxHeight
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
            val totalCount = cards.size
            val faceDownCount = cards.count { !it.isFaceUp }
            val faceUpCount = maxOf(0, totalCount - faceDownCount)

            // Dynamic compression so cards always stay inside bounds
            val baseDownStep = 10.dp
            val baseUpStep = 18.dp
            val nominalTotalH = cardHeight + (baseDownStep * faceDownCount) + (baseUpStep * maxOf(0, faceUpCount - 1))

            val compression = if (nominalTotalH > maxAvailH && nominalTotalH > cardHeight) {
                ((maxAvailH - cardHeight) / (nominalTotalH - cardHeight)).coerceIn(0.45f, 1.0f)
            } else 1.0f

            val downStep = (baseDownStep * compression).coerceAtLeast(6.dp)
            val upStep = (baseUpStep * compression).coerceAtLeast(10.dp)

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

                val isThisCardInDrag = isDragging && draggedFromCardIdx != null && cardIdx >= draggedFromCardIdx!!
                val currentCardOffset = if (isThisCardInDrag) {
                    IntOffset(dragOffset.x.roundToInt(), dragOffset.y.roundToInt())
                } else {
                    IntOffset.Zero
                }
                val zIndex = if (isThisCardInDrag) 150f + cardIdx else 1f + cardIdx

                val baseModifier = Modifier
                    .offset(y = yOffset)
                    .offset { currentCardOffset }
                    .zIndex(zIndex)
                    .size(cardWidth, cardHeight)

                val gestureModifier = if (card.isFaceUp) {
                    baseModifier.pointerInput(card.id) {
                        detectDragGestures(
                            onDragStart = {
                                isDragging = true
                                draggedFromCardIdx = cardIdx
                                dragOffset = Offset.Zero
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                dragOffset += dragAmount
                            },
                            onDragEnd = {
                                val dist = dragOffset.getDistance()
                                if (dist < 14f) {
                                    onCardClick(cardIdx)
                                } else {
                                    val colStepPx = with(density) { (cardWidth + colSpacing).toPx() }
                                    val colShift = (dragOffset.x / colStepPx).roundToInt()
                                    val targetColIdx = (colIndex + colShift).coerceIn(0, 6)

                                    val dragUpPx = with(density) { 55.dp.toPx() }
                                    if (dragOffset.y < -dragUpPx && cardIdx == cards.lastIndex) {
                                        // Dragged upwards to Foundation row
                                        val targetFIdx = (colIndex + colShift).coerceIn(0, 3)
                                        onCardDrop(
                                            CardLocation.Tableau(colIndex, cardIdx),
                                            CardLocation.Foundation(targetFIdx)
                                        )
                                    } else if (targetColIdx != colIndex) {
                                        onCardDrop(
                                            CardLocation.Tableau(colIndex, cardIdx),
                                            CardLocation.Tableau(targetColIdx)
                                        )
                                    }
                                }
                                isDragging = false
                                draggedFromCardIdx = null
                                dragOffset = Offset.Zero
                            },
                            onDragCancel = {
                                isDragging = false
                                draggedFromCardIdx = null
                                dragOffset = Offset.Zero
                            }
                        )
                    }
                } else {
                    baseModifier
                }

                CardView(
                    card = card,
                    cardBack = cardBack,
                    cardFace = cardFace,
                    isSelected = isSelected,
                    isValidTarget = isTarget,
                    isHinted = isHinted,
                    largePrint = largePrint,
                    onClick = if (!card.isFaceUp) { { onCardClick(cardIdx) } } else null,
                    onDoubleClick = if (card.isFaceUp) { { onCardDoubleClick(cardIdx) } } else null,
                    modifier = gestureModifier
                )

                yOffset += if (card.isFaceUp) upStep else downStep
            }
        }
    }
}

/**
 * Floating Bottom Bar matching the screenshot:
 * - Dark rounded pill container with:
 *   ↩ Undo | 💡 Hint | 🎴 Play | 🪄 Magic Wand | ⚙ Settings
 * - Followed by decorative: ❧ Solitaire ❧
 */
@Composable
fun CompactGameBottomBar(
    gameState: GameState,
    isAutoCompleting: Boolean,
    onSettings: () -> Unit,
    onMagicWand: () -> Unit,
    onPlay: () -> Unit,
    onHint: () -> Unit,
    onUndo: () -> Unit,
    onAutoComplete: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Auto Win button when ready
        if (gameState.canAutoComplete) {
            Button(
                onClick = onAutoComplete,
                enabled = !isAutoCompleting,
                colors = ButtonDefaults.buttonColors(containerColor = SleekEmerald500),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(32.dp)
                    .padding(bottom = 2.dp)
                    .testTag("autocomplete_button")
            ) {
                Text(
                    text = if (isAutoCompleting) "Finishing..." else "✨ AUTO WIN ✨",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }

        // Floating rounded pill
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            color = Color.Black.copy(alpha = 0.55f),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(0.75.dp, Color.White.copy(alpha = 0.12f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Undo
                BottomBarButton(
                    icon = Icons.AutoMirrored.Filled.Undo,
                    label = "Undo",
                    testTag = "bottom_undo",
                    onClick = onUndo
                )

                // Hint
                BottomBarButton(
                    icon = Icons.Default.Lightbulb,
                    label = "Hint",
                    testTag = "bottom_hint",
                    onClick = onHint
                )

                // Play (Center main deal action)
                BottomBarButton(
                    icon = Icons.Default.PlayArrow,
                    label = "Play",
                    tint = SleekEmerald400,
                    testTag = "bottom_play",
                    onClick = onPlay
                )

                // Magic Wand
                BottomBarButton(
                    icon = Icons.Default.AutoAwesome,
                    label = "Magic",
                    tint = Color(0xFFFFD54F),
                    testTag = "bottom_magic",
                    onClick = onMagicWand
                )

                // Settings
                BottomBarButton(
                    icon = Icons.Default.Settings,
                    label = "Settings",
                    testTag = "bottom_settings",
                    onClick = onSettings
                )
            }
        }

        // Subtle decorative brand title at bottom
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = "❧  SOLITAIRE  ❧",
            color = Color.White.copy(alpha = 0.35f),
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = FontFamily.Serif,
            letterSpacing = 1.sp
        )
    }
}

@Composable
fun BottomBarButton(
    icon: ImageVector,
    label: String,
    tint: Color = SleekSlate200,
    testTag: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 2.dp)
            .testTag(testTag),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = tint,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.height(1.dp))
        Text(
            text = label,
            color = tint,
            fontSize = 9.5.sp,
            fontWeight = FontWeight.SemiBold
        )
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
                shape = RoundedCornerShape(8.dp),
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
                        contentDescription = "Victory",
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
                    color = SleekSlate300,
                    textAlign = TextAlign.Center
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
