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
    val tabs = listOf("Table (56)", "Backs (25)", "Faces (25)", "Suits & Colors", "Combos")

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

    val sampleCard1 = remember { Card(id = 1, suit = Suit.HEARTS, rank = Rank.ACE, isFaceUp = true) }
    val sampleCard2 = remember { Card(id = 2, suit = Suit.SPADES, rank = Rank.KING, isFaceUp = true) }
    val sampleCardDown = remember { Card(id = 3, suit = Suit.DIAMONDS, rank = Rank.JACK, isFaceUp = false) }

    // Contrast check
    val contrastWarning = remember(currentFace, currentScheme, currentBg) {
        val bgRed = currentBg.primaryColor.red > 0.5f && currentBg.primaryColor.green < 0.3f
        if (bgRed && currentScheme.id == "SCHEME_STANDARD" && currentFace.isDarkSurface) {
            "Low contrast notice: Red suits on red table. Consider Four-Colour or High Contrast scheme."
        } else null
    }

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
                            val rFace = CustomizationRegistry.cardFaces.random(Random).id
                            val rSuit = CustomizationRegistry.suitStyles.random(Random).id
                            val rScheme = CustomizationRegistry.suitColorSchemes.random(Random).id
                            val rBack = CustomizationRegistry.cardBacks.filter { it.id != "BACK_CUSTOM_PHOTO" }.random(Random).id
                            val rBg = CustomizationRegistry.backgrounds.filter { it.id != "THEME_CUSTOM_PHOTO" }.random(Random).id
                            userPrefs.applyPresetCombo(rFace, rSuit, rScheme, rBack, rBg)
                            Toast.makeText(context, "Surprise style randomized!", Toast.LENGTH_SHORT).show()
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

                    // Cards preview row
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
                            text = "${currentBg.name} • ${currentFace.name}",
                            color = SleekEmerald400,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            // Contrast warning banner
            if (contrastWarning != null) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 2.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF78350F).copy(alpha = 0.35f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF59E0B))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(text = contrastWarning, color = Color(0xFFFDE68A), fontSize = 11.sp)
                    }
                }
            }

            // ==========================================
            // PRIMARY TABS
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
                                fontSize = 12.sp,
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
                    0 -> TableThemesTab(
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
                    1 -> CardBacksTab(
                        settings = settings,
                        onSelectBack = { scope.launch { userPrefs.updateCardBack(it.id) } },
                        onPickPhoto = {
                            pickingFor = "CARD_BACK"
                            photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        }
                    )
                    2 -> CardFacesTab(
                        settings = settings,
                        onSelectFace = { scope.launch { userPrefs.updateCardFace(it.id) } },
                        onIndexSizeChange = { scope.launch { userPrefs.updateCardIndexSize(it) } },
                        onCornerRadiusChange = { scope.launch { userPrefs.updateCardCornerRadius(it) } },
                        onNumeralsChange = { scope.launch { userPrefs.updateNumeralsStyle(it) } }
                    )
                    3 -> SuitsAndColorsTab(
                        settings = settings,
                        onSelectSuit = { scope.launch { userPrefs.updateSuitStyle(it.id) } },
                        onSelectScheme = { scope.launch { userPrefs.updateSuitColorScheme(it.id) } }
                    )
                    4 -> PresetsTab(
                        settings = settings,
                        onSelectCombo = {
                            scope.launch {
                                userPrefs.applyPresetCombo(
                                    it.cardFaceId,
                                    it.suitStyleId,
                                    it.suitSchemeId,
                                    it.cardBackId,
                                    it.backgroundId
                                )
                                Toast.makeText(context, "Applied ${it.name} Combo!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        onDailySurpriseToggle = { scope.launch { userPrefs.updateDailySurpriseTheme(it) } }
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

@Composable
fun CardFacesTab(
    settings: UserSettings,
    onSelectFace: (CardFaceTheme) -> Unit,
    onIndexSizeChange: (String) -> Unit,
    onCornerRadiusChange: (String) -> Unit,
    onNumeralsChange: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Ergonomics controls: Index Size, Corner Radius, Numerals
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Index size
            listOf("STANDARD" to "Std Index", "LARGE" to "Large Index", "EXTRA_LARGE" to "Senior XL").forEach { (id, label) ->
                FilterChip(
                    selected = settings.cardIndexSize == id,
                    onClick = { onIndexSizeChange(id) },
                    label = { Text(label, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = SleekEmerald500)
                )
            }
            // Numerals
            listOf("WESTERN" to "1 2 3", "EASTERN_ARABIC" to "١ ٢ ٣").forEach { (id, label) ->
                FilterChip(
                    selected = settings.numeralsStyle == id,
                    onClick = { onNumeralsChange(id) },
                    label = { Text(label, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFF0284C7))
                )
            }
            // Corner radius
            listOf("SHARP" to "Sharp Edge", "MEDIUM" to "Med Radius", "ROUND" to "Round Edge").forEach { (id, label) ->
                FilterChip(
                    selected = settings.cardCornerRadius == id,
                    onClick = { onCornerRadiusChange(id) },
                    label = { Text(label, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFF7C3AED))
                )
            }
        }

        // 25 Card Faces Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(CustomizationRegistry.cardFaces) { face ->
                val isSelected = settings.cardFaceId == face.id
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(95.dp)
                        .clickable { onSelectFace(face) },
                    shape = RoundedCornerShape(12.dp),
                    color = SleekHeaderDark,
                    border = androidx.compose.foundation.BorderStroke(
                        if (isSelected) 2.5.dp else 1.dp,
                        if (isSelected) SleekEmerald400 else SleekBorderSubtle
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CardView(
                            card = Card(id = 0, suit = Suit.HEARTS, rank = Rank.ACE, isFaceUp = true),
                            modifier = Modifier.size(width = 46.dp, height = 66.dp),
                            cardFace = face,
                            cardCornerRadius = settings.cardCornerRadius,
                            numeralsStyle = settings.numeralsStyle
                        )
                        Spacer(Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = face.name, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text(text = face.description, color = SleekSlate400, fontSize = 9.sp, maxLines = 2)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SuitsAndColorsTab(
    settings: UserSettings,
    onSelectSuit: (SuitStyleTheme) -> Unit,
    onSelectScheme: (SuitColorScheme) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Suit Color Schemes (4 options)
        Text(text = "Suit Colour Schemes (4 Options)", color = SleekEmerald400, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            CustomizationRegistry.suitColorSchemes.forEach { scheme ->
                val isSelected = settings.suitColorSchemeId == scheme.id
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onSelectScheme(scheme) },
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSelected) SleekEmerald500.copy(alpha = 0.25f) else SleekHeaderDark,
                    border = androidx.compose.foundation.BorderStroke(
                        if (isSelected) 2.dp else 1.dp,
                        if (isSelected) SleekEmerald400 else SleekBorderSubtle
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                            Text("♥", color = scheme.heartsColor, fontSize = 12.sp)
                            Text("♦", color = scheme.diamondsColor, fontSize = 12.sp)
                            Text("♣", color = scheme.clubsColor, fontSize = 12.sp)
                            Text("♠", color = scheme.spadesColor, fontSize = 12.sp)
                        }
                        Spacer(Modifier.height(2.dp))
                        Text(text = scheme.name.split(" ").first(), color = Color.White, fontSize = 9.5.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        Spacer(Modifier.height(8.dp))
        Text(text = "Suit Icon Styles (12 Styles)", color = SleekEmerald400, fontSize = 12.sp, fontWeight = FontWeight.Bold)

        // 12 Suit Icon Styles Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(CustomizationRegistry.suitStyles) { suit ->
                val isSelected = settings.suitStyleId == suit.id
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp)
                        .clickable { onSelectSuit(suit) },
                    shape = RoundedCornerShape(10.dp),
                    color = SleekHeaderDark,
                    border = androidx.compose.foundation.BorderStroke(
                        if (isSelected) 2.dp else 1.dp,
                        if (isSelected) SleekEmerald400 else SleekBorderSubtle
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "♠ ♥", fontSize = 18.sp, color = SleekEmerald400)
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Text(text = suit.name, color = Color.White, fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                            Text(text = suit.description, color = SleekSlate400, fontSize = 8.5.sp, maxLines = 1)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PresetsTab(
    settings: UserSettings,
    onSelectCombo: (PresetCombo) -> Unit,
    onDailySurpriseToggle: (Boolean) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            shape = RoundedCornerShape(12.dp),
            color = SleekHeaderDark,
            border = androidx.compose.foundation.BorderStroke(1.dp, SleekBorderSubtle)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Daily Surprise Theme", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text(text = "Wake up each morning to a brand new curated theme combo!", color = SleekSlate400, fontSize = 11.sp)
                }
                androidx.compose.material3.Switch(
                    checked = settings.dailySurpriseTheme,
                    onCheckedChange = onDailySurpriseToggle,
                    colors = androidx.compose.material3.SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = SleekEmerald500
                    )
                )
            }
        }

        Text(text = "One-Tap Preset Combos", color = SleekEmerald400, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 6.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(1),
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(CustomizationRegistry.presetCombos) { combo ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectCombo(combo) },
                    shape = RoundedCornerShape(12.dp),
                    color = SleekHeaderDark,
                    border = androidx.compose.foundation.BorderStroke(1.dp, SleekBorderSubtle)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = SleekEmerald400, modifier = Modifier.size(24.dp))
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = combo.name, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text(text = combo.description, color = SleekSlate300, fontSize = 11.sp)
                        }
                        Button(
                            onClick = { onSelectCombo(combo) },
                            colors = ButtonDefaults.buttonColors(containerColor = SleekEmerald500),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Apply", fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}
