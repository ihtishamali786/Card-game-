package com.solitaire.hyper.card.games.ui.customization

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solitaire.hyper.card.games.data.preferences.UserPreferencesRepository
import com.solitaire.hyper.card.games.data.preferences.UserSettings
import com.solitaire.hyper.card.games.game.model.Card
import com.solitaire.hyper.card.games.game.model.Rank
import com.solitaire.hyper.card.games.game.model.Suit
import com.solitaire.hyper.card.games.ui.components.CardView
import com.solitaire.hyper.card.games.ui.theme.SleekBgDark
import com.solitaire.hyper.card.games.ui.theme.SleekEmerald400
import com.solitaire.hyper.card.games.ui.theme.SleekHeaderDark
import com.solitaire.hyper.card.games.ui.theme.SleekSlate100
import com.solitaire.hyper.card.games.ui.theme.SleekSlate300
import com.solitaire.hyper.card.games.ui.theme.SleekSlate400
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomizationScreen(
    userPrefs: UserPreferencesRepository,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val settings by userPrefs.userSettingsFlow.collectAsState(initial = UserSettings())

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("Backgrounds", "Card Backs", "Card Faces")

    val currentBg = remember(settings.backgroundId) {
        CustomizationRegistry.getBackground(settings.backgroundId)
    }
    val currentCardBack = remember(settings.cardBackId) {
        CustomizationRegistry.getCardBack(settings.cardBackId)
    }
    val currentCardFace = remember(settings.cardFaceId) {
        CustomizationRegistry.getCardFace(settings.cardFaceId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Themes & Customization",
                        color = SleekSlate100,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = SleekSlate100
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SleekHeaderDark)
            )
        },
        containerColor = SleekBgDark
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Live Card Preview Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .background(currentBg.brush)
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Face Up Card Preview
                    CardView(
                        card = Card(id = 1, suit = Suit.HEARTS, rank = Rank.ACE, isFaceUp = true),
                        cardBack = currentCardBack,
                        cardFace = currentCardFace,
                        largePrint = settings.largePrintMode,
                        modifier = Modifier
                            .width(66.dp)
                            .height(96.dp)
                    )

                    // Face Up King Preview
                    CardView(
                        card = Card(id = 2, suit = Suit.SPADES, rank = Rank.KING, isFaceUp = true),
                        cardBack = currentCardBack,
                        cardFace = currentCardFace,
                        largePrint = settings.largePrintMode,
                        modifier = Modifier
                            .width(66.dp)
                            .height(96.dp)
                    )

                    // Card Back Preview
                    CardView(
                        card = Card(id = 3, suit = Suit.DIAMONDS, rank = Rank.QUEEN, isFaceUp = false),
                        cardBack = currentCardBack,
                        cardFace = currentCardFace,
                        largePrint = settings.largePrintMode,
                        modifier = Modifier
                            .width(66.dp)
                            .height(96.dp)
                    )
                }
            }

            // Tabs: Backgrounds, Card Backs, Card Faces
            PrimaryTabRow(
                selectedTabIndex = selectedTab,
                containerColor = SleekHeaderDark,
                contentColor = SleekEmerald400
            ) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                color = if (selectedTab == index) SleekEmerald400 else SleekSlate400,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 13.5.sp
                            )
                        }
                    )
                }
            }

            // Tab Content
            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                when (selectedTab) {
                    0 -> BackgroundsGrid(
                        currentBgId = settings.backgroundId,
                        onSelect = { bgId ->
                            scope.launch { userPrefs.updateBackground(bgId) }
                        }
                    )
                    1 -> CardBacksGrid(
                        currentBackId = settings.cardBackId,
                        cardFace = currentCardFace,
                        onSelect = { backId ->
                            scope.launch { userPrefs.updateCardBack(backId) }
                        }
                    )
                    2 -> CardFacesGrid(
                        currentFaceId = settings.cardFaceId,
                        cardBack = currentCardBack,
                        isLargePrint = settings.largePrintMode,
                        onSelect = { faceId ->
                            scope.launch { userPrefs.updateCardFace(faceId) }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun BackgroundsGrid(
    currentBgId: String,
    onSelect: (String) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize().testTag("backgrounds_grid"),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(CustomizationRegistry.BACKGROUNDS) { bg ->
            val isSelected = bg.id == currentBgId
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(115.dp)
                    .clickable { onSelect(bg.id) },
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(
                    if (isSelected) 2.5.dp else 1.dp,
                    if (isSelected) SleekEmerald400 else Color.White.copy(alpha = 0.12f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 8.dp else 2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(bg.brush)
                        .padding(12.dp)
                ) {
                    Column(
                        modifier = Modifier.align(Alignment.BottomStart)
                    ) {
                        Text(
                            text = bg.name,
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = bg.subtitle,
                            color = Color.White.copy(alpha = 0.75f),
                            fontSize = 10.5.sp
                        )
                    }

                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .size(24.dp)
                                .background(SleekEmerald400, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CardBacksGrid(
    currentBackId: String,
    cardFace: CardFaceTheme,
    onSelect: (String) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize().testTag("card_backs_grid"),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        items(CustomizationRegistry.CARD_BACKS) { back ->
            val isSelected = back.id == currentBackId
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelect(back.id) },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SleekHeaderDark),
                border = BorderStroke(
                    if (isSelected) 2.dp else 1.dp,
                    if (isSelected) SleekEmerald400 else Color.White.copy(alpha = 0.08f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CardView(
                        card = Card(id = 99, suit = Suit.SPADES, rank = Rank.ACE, isFaceUp = false),
                        cardBack = back,
                        cardFace = cardFace,
                        modifier = Modifier
                            .width(62.dp)
                            .height(90.dp)
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = back.name,
                        color = if (isSelected) SleekEmerald400 else SleekSlate100,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )

                    if (isSelected) {
                        Text(
                            text = "Selected",
                            color = SleekEmerald400,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CardFacesGrid(
    currentFaceId: String,
    cardBack: CardBackTheme,
    isLargePrint: Boolean,
    onSelect: (String) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(1),
        modifier = Modifier.fillMaxSize().testTag("card_faces_grid"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(CustomizationRegistry.CARD_FACES) { face ->
            val isSelected = face.id == currentFaceId
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelect(face.id) },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SleekHeaderDark),
                border = BorderStroke(
                    if (isSelected) 2.dp else 1.dp,
                    if (isSelected) SleekEmerald400 else Color.White.copy(alpha = 0.08f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CardView(
                        card = Card(id = 88, suit = Suit.HEARTS, rank = Rank.QUEEN, isFaceUp = true),
                        cardBack = cardBack,
                        cardFace = face,
                        largePrint = isLargePrint,
                        modifier = Modifier
                            .width(58.dp)
                            .height(84.dp)
                    )

                    Spacer(Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = face.name,
                            color = if (isSelected) SleekEmerald400 else SleekSlate100,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = face.description,
                            color = SleekSlate400,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                    }

                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(SleekEmerald400, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
