package com.solitaire.hyper.card.games.ui.customization

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solitaire.hyper.card.games.ads.BannerAdView
import com.solitaire.hyper.card.games.data.preferences.UserPreferencesRepository
import com.solitaire.hyper.card.games.data.preferences.UserSettings
import com.solitaire.hyper.card.games.game.model.Card
import com.solitaire.hyper.card.games.game.model.Rank
import com.solitaire.hyper.card.games.game.model.Suit
import com.solitaire.hyper.card.games.ui.components.CardView
import com.solitaire.hyper.card.games.ui.theme.SleekBgDark
import com.solitaire.hyper.card.games.ui.theme.SleekBorderSubtle
import com.solitaire.hyper.card.games.ui.theme.SleekEmerald400
import com.solitaire.hyper.card.games.ui.theme.SleekEmerald500
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
    val tabs = listOf("Table Felt", "Card Back", "Card Face")

    val currentBg = CustomizationRegistry.getBackground(settings.backgroundId)
    val currentBack = CustomizationRegistry.getCardBack(settings.cardBackId)
    val currentFace = CustomizationRegistry.getCardFace(settings.cardFaceId)

    val sampleFaceUpCard = remember { Card(id = 99, suit = Suit.SPADES, rank = Rank.ACE, isFaceUp = true) }
    val sampleFaceDownCard = remember { Card(id = 98, suit = Suit.HEARTS, rank = Rank.KING, isFaceUp = false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Themes & Customization", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("back_button")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SleekHeaderDark,
                    titleContentColor = SleekSlate100,
                    navigationIconContentColor = SleekSlate300
                )
            )
        },
        bottomBar = {
            BannerAdView()
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(SleekBgDark)
        ) {
            // Live Interactive Preview Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .background(currentBg.brush),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CardView(
                        card = sampleFaceUpCard,
                        cardBack = currentBack,
                        cardFace = currentFace,
                        modifier = Modifier.size(70.dp, 100.dp)
                    )
                    CardView(
                        card = sampleFaceDownCard,
                        cardBack = currentBack,
                        cardFace = currentFace,
                        modifier = Modifier.size(70.dp, 100.dp)
                    )
                }
            }

            // Tab Row
            PrimaryTabRow(
                selectedTabIndex = selectedTab,
                containerColor = SleekHeaderDark,
                contentColor = SleekEmerald400
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                color = if (selectedTab == index) SleekEmerald400 else SleekSlate400,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            // Content Grid according to selected tab
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                when (selectedTab) {
                    0 -> BackgroundsGrid(
                        currentId = settings.backgroundId,
                        onSelect = { id -> scope.launch { userPrefs.updateBackground(id) } }
                    )
                    1 -> CardBacksGrid(
                        currentId = settings.cardBackId,
                        onSelect = { id -> scope.launch { userPrefs.updateCardBack(id) } }
                    )
                    2 -> CardFacesGrid(
                        currentId = settings.cardFaceId,
                        onSelect = { id -> scope.launch { userPrefs.updateCardFace(id) } }
                    )
                }
            }
        }
    }
}

@Composable
fun BackgroundsGrid(currentId: String, onSelect: (String) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(CustomizationRegistry.backgrounds) { bg ->
            val isSelected = bg.id == currentId
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(84.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onSelect(bg.id) }
                    .then(
                        if (isSelected) Modifier.border(2.5.dp, SleekEmerald400, RoundedCornerShape(12.dp))
                        else Modifier
                    )
                    .testTag("bg_${bg.id}")
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(bg.brush)
                        .padding(10.dp)
                ) {
                    Text(
                        text = bg.name,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        modifier = Modifier.align(Alignment.BottomStart)
                    )
                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(SleekEmerald400, CircleShape)
                                .align(Alignment.TopEnd),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = SleekBgDark, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CardBacksGrid(currentId: String, onSelect: (String) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(CustomizationRegistry.cardBacks) { back ->
            val isSelected = back.id == currentId
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onSelect(back.id) }
                    .then(
                        if (isSelected) Modifier.border(2.5.dp, SleekEmerald400, RoundedCornerShape(12.dp))
                        else Modifier
                    )
                    .testTag("cardback_${back.id}")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0x30FFFFFF))
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CardView(
                        card = Card(id = 1, suit = Suit.SPADES, rank = Rank.ACE, isFaceUp = false),
                        cardBack = back,
                        modifier = Modifier.size(42.dp, 60.dp)
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = back.name,
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}

@Composable
fun CardFacesGrid(currentId: String, onSelect: (String) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(CustomizationRegistry.cardFaces) { face ->
            val isSelected = face.id == currentId
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onSelect(face.id) }
                    .then(
                        if (isSelected) Modifier.border(2.5.dp, SleekEmerald400, RoundedCornerShape(12.dp))
                        else Modifier
                    )
                    .testTag("cardface_${face.id}")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0x30FFFFFF))
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CardView(
                        card = Card(id = 1, suit = Suit.HEARTS, rank = Rank.QUEEN, isFaceUp = true),
                        cardFace = face,
                        modifier = Modifier.size(42.dp, 60.dp)
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = face.name,
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}
