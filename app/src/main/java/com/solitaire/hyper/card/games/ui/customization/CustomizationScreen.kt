package com.solitaire.hyper.card.games.ui.customization

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
import com.solitaire.hyper.card.games.ui.theme.SleekBorderSubtle
import com.solitaire.hyper.card.games.ui.theme.SleekEmerald400
import com.solitaire.hyper.card.games.ui.theme.SleekEmerald500
import com.solitaire.hyper.card.games.ui.theme.SleekHeaderDark
import com.solitaire.hyper.card.games.ui.theme.SleekSlate100
import com.solitaire.hyper.card.games.ui.theme.SleekSlate300
import com.solitaire.hyper.card.games.ui.theme.SleekSlate400
import kotlinx.coroutines.launch
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomizationScreen(
    userPrefs: UserPreferencesRepository,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val settings by userPrefs.userSettingsFlow.collectAsState(initial = UserSettings())

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Card Backs (25)", "Table Themes (56)")

    var selectedCategory by remember { mutableStateOf<TableCategory?>(null) }
    var pickingFor by remember { mutableStateOf("BACKGROUND") }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            scope.launch {
                if (pickingFor == "BACKGROUND") {
                    userPrefs.updateCustomBackgroundUri(uri.toString())
                    userPrefs.updateBackground("THEME_CUSTOM_PHOTO")
                    Toast.makeText(context, "Custom Table background applied!", Toast.LENGTH_SHORT).show()
                } else {
                    userPrefs.updateCustomCardBackUri(uri.toString())
                    userPrefs.updateCardBack("BACK_CUSTOM_PHOTO")
                    Toast.makeText(context, "Custom Card Back applied!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    val currentBg = CustomizationRegistry.getBackground(settings.backgroundId)
    val currentBack = CustomizationRegistry.getCardBack(settings.cardBackId)
    val currentFace = CustomizationRegistry.getCardFace(settings.cardFaceId)
    val currentSuit = CustomizationRegistry.getSuitStyle(settings.suitStyleId)
    val currentScheme = CustomizationRegistry.getSuitColorScheme(settings.suitColorSchemeId)

    val sampleCard1 = remember { Card(id = 1, suit = Suit.SPADES, rank = Rank.ACE, isFaceUp = true) }
    val sampleCard2 = remember { Card(id = 2, suit = Suit.HEARTS, rank = Rank.QUEEN, isFaceUp = true) }
    val sampleCardDown = remember { Card(id = 3, suit = Suit.SPADES, rank = Rank.KING, isFaceUp = false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Card & Table Customization",
                        color = SleekSlate100,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
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
                actions = {
                    IconButton(onClick = {
                        scope.launch {
                            val rBack = CustomizationRegistry.cardBacks.filter { it.id != "BACK_CUSTOM_PHOTO" }.random(Random).id
                            val rBg = CustomizationRegistry.backgrounds.filter { it.id != "THEME_CUSTOM_PHOTO" }.random(Random).id
                            userPrefs.updateCardBack(rBack)
                            userPrefs.updateBackground(rBg)
                            Toast.makeText(context, "Surprise back & table randomized!", Toast.LENGTH_SHORT).show()
                        }
                    }) {
                        Icon(Icons.Default.Shuffle, contentDescription = "Randomize", tint = SleekEmerald400)
                    }
                    IconButton(onClick = {
                        scope.launch {
                            userPrefs.resetCustomizationToDefault()
                            Toast.makeText(context, "Reset to Classic Default", Toast.LENGTH_SHORT).show()
                        }
                    }) {
                        Icon(Icons.Default.RestartAlt, contentDescription = "Reset Default", tint = SleekSlate300)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SleekHeaderDark)
            )
        },
        containerColor = SleekBgDark
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // ==========================================
            // LIVE INTERACTIVE PREVIEW AREA
            // ==========================================
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                color = SleekHeaderDark,
                border = androidx.compose.foundation.BorderStroke(1.dp, SleekBorderSubtle)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(138.dp)
                        .background(currentBg.brush)
                ) {
                    // Dim overlay
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = settings.backgroundDim))
                            .blur(settings.backgroundBlur.dp)
                    )

                    // Cards preview row (Official Solitaire Hyper Card Deck faces + Selected Back)
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CardView(
                            card = sampleCard1,
                            modifier = Modifier.size(width = 68.dp, height = 98.dp),
                            cardBack = currentBack,
                            cardFace = currentFace,
                            suitStyle = currentSuit,
                            suitScheme = currentScheme,
                            cardCornerRadius = settings.cardCornerRadius,
                            cardIndexSize = settings.cardIndexSize,
                            numeralsStyle = settings.numeralsStyle
                        )
                        CardView(
                            card = sampleCard2,
                            modifier = Modifier.size(width = 68.dp, height = 98.dp),
                            cardBack = currentBack,
                            cardFace = currentFace,
                            suitStyle = currentSuit,
                            suitScheme = currentScheme,
                            cardCornerRadius = settings.cardCornerRadius,
                            cardIndexSize = settings.cardIndexSize,
                            numeralsStyle = settings.numeralsStyle
                        )
                        CardView(
                            card = sampleCardDown,
                            modifier = Modifier.size(width = 68.dp, height = 98.dp),
                            cardBack = currentBack,
                            cardFace = currentFace,
                            customCardBackUri = settings.customCardBackUri,
                            cardCornerRadius = settings.cardCornerRadius
                        )
                    }

                    // Active theme badge
                    Surface(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(8.dp),
                        shape = RoundedCornerShape(8.dp),
                        color = Color.Black.copy(alpha = 0.65f)
                    ) {
                        Text(
                            text = "${currentBack.name} • ${currentBg.name}",
                            color = SleekEmerald400,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            // ==========================================
            // PRIMARY TABS (Card Backs & Table Themes)
            // ==========================================
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
                                fontSize = 13.sp,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == index) SleekEmerald400 else SleekSlate400
                            )
                        }
                    )
                }
            }

            // ==========================================
            // TAB CONTENT
            // ==========================================
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                when (selectedTab) {
                    0 -> CardBacksTab(
                        settings = settings,
                        onSelectBack = { scope.launch { userPrefs.updateCardBack(it.id) } },
                        onPickPhoto = {
                            pickingFor = "CARD_BACK"
                            photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        }
                    )
                    1 -> TableThemesTab(
                        settings = settings,
                        selectedCategory = selectedCategory,
                        onCategorySelect = { selectedCategory = it },
                        onSelectTheme = { scope.launch { userPrefs.updateBackground(it.id) } },
                        onPickPhoto = {
                            pickingFor = "BACKGROUND"
                            photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        },
                        onDimChange = { scope.launch { userPrefs.updateBackgroundDim(it) } },
                        onBlurChange = { scope.launch { userPrefs.updateBackgroundBlur(it) } }
                    )
                }
            }
        }
    }
}

@Composable
fun TableThemesTab(
    settings: UserSettings,
    selectedCategory: TableCategory?,
    onCategorySelect: (TableCategory?) -> Unit,
    onSelectTheme: (BackgroundTheme) -> Unit,
    onPickPhoto: () -> Unit,
    onDimChange: (Float) -> Unit,
    onBlurChange: (Float) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Dim & Blur sliders row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                Text(text = "Dim: ${(settings.backgroundDim * 100).toInt()}%", color = SleekSlate300, fontSize = 11.sp)
                Slider(
                    value = settings.backgroundDim,
                    onValueChange = onDimChange,
                    valueRange = 0.0f..0.8f,
                    colors = SliderDefaults.colors(thumbColor = SleekEmerald400, activeTrackColor = SleekEmerald500)
                )
            }
            Column(modifier = Modifier.weight(1f).padding(start = 8.dp)) {
                Text(text = "Blur: ${settings.backgroundBlur.toInt()}dp", color = SleekSlate300, fontSize = 11.sp)
                Slider(
                    value = settings.backgroundBlur,
                    onValueChange = onBlurChange,
                    valueRange = 0.0f..15.0f,
                    colors = SliderDefaults.colors(thumbColor = SleekEmerald400, activeTrackColor = SleekEmerald500)
                )
            }
        }

        // Category filter chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            FilterChip(
                selected = selectedCategory == null,
                onClick = { onCategorySelect(null) },
                label = { Text("All (56)") },
                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = SleekEmerald500)
            )
            TableCategory.values().forEach { cat ->
                FilterChip(
                    selected = selectedCategory == cat,
                    onClick = { onCategorySelect(cat) },
                    label = { Text(cat.displayName) },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = SleekEmerald500)
                )
            }
            Button(
                onClick = onPickPhoto,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("My Photo", fontSize = 11.sp)
            }
        }

        // Grid of Themes
        val filtered = remember(selectedCategory) {
            if (selectedCategory == null) CustomizationRegistry.backgrounds
            else CustomizationRegistry.backgrounds.filter { it.category == selectedCategory }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filtered) { bg ->
                val isSelected = settings.backgroundId == bg.id
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp)
                        .clickable { onSelectTheme(bg) },
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        if (isSelected) 2.5.dp else 1.dp,
                        if (isSelected) SleekEmerald400 else SleekBorderSubtle
                    )
                ) {
                    Box(modifier = Modifier.fillMaxSize().background(bg.brush).padding(8.dp)) {
                        Column(modifier = Modifier.align(Alignment.BottomStart)) {
                            Text(text = bg.name, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text(text = bg.subtitle, color = Color.White.copy(alpha = 0.7f), fontSize = 9.sp, maxLines = 1)
                        }
                        if (isSelected) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = null,
                                tint = SleekEmerald400,
                                modifier = Modifier.align(Alignment.TopEnd).size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CardBacksTab(
    settings: UserSettings,
    onSelectBack: (CardBackTheme) -> Unit,
    onPickPhoto: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "25 Handcrafted Back Designs", color = SleekSlate300, fontSize = 12.sp)
            OutlinedButton(
                onClick = onPickPhoto,
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("Use My Photo", fontSize = 11.sp)
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(CustomizationRegistry.cardBacks) { back ->
                val isSelected = settings.cardBackId == back.id
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(115.dp)
                        .clickable { onSelectBack(back) },
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        if (isSelected) 2.5.dp else 1.dp,
                        if (isSelected) SleekEmerald400 else SleekBorderSubtle
                    )
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        CardView(
                            card = Card(id = 0, suit = Suit.SPADES, rank = Rank.ACE, isFaceUp = false),
                            modifier = Modifier.fillMaxSize(),
                            cardBack = back,
                            customCardBackUri = if (back.id == "BACK_CUSTOM_PHOTO") settings.customCardBackUri else null
                        )
                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(4.dp)
                                    .background(SleekEmerald500, CircleShape)
                                    .size(18.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}
