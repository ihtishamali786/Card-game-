package com.solitaire.hyper.card.games.ui.customization

import android.app.Activity
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solitaire.hyper.card.games.ads.AdManager
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
    val context = LocalContext.current
    val activity = context as? Activity
    val scope = rememberCoroutineScope()
    val settings by userPrefs.userSettingsFlow.collectAsState(initial = UserSettings())

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Table Felt", "Card Back", "Card Face")

    var filterType by remember { mutableStateOf("ALL") } // ALL, FREE, PREMIUM

    val currentBg = CustomizationRegistry.getBackground(settings.backgroundId)
    val currentBack = CustomizationRegistry.getCardBack(settings.cardBackId)
    val currentFace = CustomizationRegistry.getCardFace(settings.cardFaceId)

    val sampleFaceUpCard = remember { Card(id = 99, suit = Suit.SPADES, rank = Rank.ACE, isFaceUp = true) }
    val sampleFaceDownCard = remember { Card(id = 98, suit = Suit.HEARTS, rank = Rank.KING, isFaceUp = false) }

    val isVipActive = settings.isVipActive()
    val isAdFreeActive = settings.isAdFreeActive()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("3D Themes & Store", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                        // Coins Badge
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF2E2405),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFD700).copy(alpha = 0.5f)),
                            modifier = Modifier.padding(end = 12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("🪙", fontSize = 14.sp)
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    text = "${settings.coins}",
                                    color = Color(0xFFFFD700),
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                },
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
            BannerAdView(isAdFree = isAdFreeActive)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(SleekBgDark)
        ) {
            // Live Interactive Preview Box with 3D Depth
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(136.dp)
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
                        modifier = Modifier.size(64.dp, 92.dp)
                    )
                    CardView(
                        card = sampleFaceDownCard,
                        cardBack = currentBack,
                        cardFace = currentFace,
                        modifier = Modifier.size(64.dp, 92.dp)
                    )
                }

                // VIP Active Indicator Overlay
                if (isVipActive) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .background(
                                Brush.horizontalGradient(listOf(Color(0xFFFFD700), Color(0xFFFF9100))),
                                RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            "👑 3D VIP: ${settings.getVipRemainingMinutes()}m left",
                            color = Color.Black,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Monetization & Video Ads Action Strip
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                colors = CardDefaults.cardColors(containerColor = SleekHeaderDark),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SleekBorderSubtle)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Watch Video for +250 Coins
                        Button(
                            onClick = {
                                if (activity != null) {
                                    AdManager.showRewardedAd(
                                        activity = activity,
                                        onRewardEarned = {
                                            scope.launch {
                                                userPrefs.addCoins(250)
                                                Toast.makeText(context, "+250 Coins Earned!", Toast.LENGTH_SHORT).show()
                                            }
                                        },
                                        onDismissOrFailed = {
                                            // Fallback grant in debug or inform
                                            scope.launch {
                                                userPrefs.addCoins(250)
                                                Toast.makeText(context, "+250 Coins Claimed!", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    )
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B3D2F)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .testTag("watch_ad_coins_btn")
                        ) {
                            Icon(Icons.Default.PlayCircle, contentDescription = null, tint = SleekEmerald400, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("+250 Coins", color = SleekEmerald400, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Spacer(Modifier.width(8.dp))

                        // Pay 1,000 Coins for 1 Hour Ad-Free
                        Button(
                            onClick = {
                                scope.launch {
                                    if (settings.coins >= 1000) {
                                        val ok = userPrefs.activateAdFreeOneHour()
                                        if (ok) {
                                            Toast.makeText(context, "1-Hour Ad-Free Pass Activated!", Toast.LENGTH_SHORT).show()
                                        }
                                    } else {
                                        Toast.makeText(context, "Need 1,000 Coins! Watch video ads to earn coins.", Toast.LENGTH_LONG).show()
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isAdFreeActive) Color(0xFF143026) else Color(0xFF2A1C40)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .testTag("ad_free_1h_btn")
                        ) {
                            Icon(Icons.Default.Shield, contentDescription = null, tint = if (isAdFreeActive) SleekEmerald400 else Color(0xFFCE93D8), modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(
                                if (isAdFreeActive) "${settings.getAdFreeRemainingMinutes()}m Ad-Free" else "No Ads (1000🪙)",
                                color = if (isAdFreeActive) SleekEmerald400 else Color(0xFFCE93D8),
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // 5 Video Ads for 1-Hour VIP Progress
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("👑 1-Hour VIP 3D Pass:", color = SleekSlate100, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    if (isVipActive) "ACTIVE (${settings.getVipRemainingMinutes()}m left)"
                                    else "${settings.rewardedAdsWatchedForVip}/5 Video Ads",
                                    color = if (isVipActive) Color(0xFFFFD700) else SleekEmerald400,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { if (isVipActive) 1f else (settings.rewardedAdsWatchedForVip / 5f) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(5.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = Color(0xFFFFD700),
                                trackColor = Color.White.copy(alpha = 0.1f)
                            )
                        }

                        Spacer(Modifier.width(10.dp))

                        if (!isVipActive) {
                            Button(
                                onClick = {
                                    if (activity != null) {
                                        AdManager.showRewardedAd(
                                            activity = activity,
                                            onRewardEarned = {
                                                scope.launch {
                                                    val vipUnlocked = userPrefs.recordRewardedAdForVip()
                                                    if (vipUnlocked) {
                                                        Toast.makeText(context, "🎉 1-Hour 3D VIP Pass Unlocked!", Toast.LENGTH_LONG).show()
                                                    } else {
                                                        Toast.makeText(context, "Watched ad towards VIP pass!", Toast.LENGTH_SHORT).show()
                                                    }
                                                }
                                            },
                                            onDismissOrFailed = {
                                                scope.launch {
                                                    val vipUnlocked = userPrefs.recordRewardedAdForVip()
                                                    if (vipUnlocked) {
                                                        Toast.makeText(context, "🎉 1-Hour 3D VIP Pass Unlocked!", Toast.LENGTH_LONG).show()
                                                    }
                                                }
                                            }
                                        )
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5C4708)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(34.dp)
                            ) {
                                Text("Watch Ad", color = Color(0xFFFFD700), fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Tab Row (Table Felt, Card Back, Card Face)
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
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 13.sp
                            )
                        }
                    )
                }
            }

            // Category Filter Chips (All, Free, 3D Premium)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("ALL" to "All", "FREE" to "Free", "PREMIUM" to "3D Premium 👑").forEach { (type, label) ->
                    FilterChip(
                        selected = filterType == type,
                        onClick = { filterType = type },
                        label = { Text(label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SleekEmerald500,
                            selectedLabelColor = Color.White,
                            containerColor = SleekHeaderDark,
                            labelColor = SleekSlate300
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = SleekBorderSubtle,
                            enabled = true,
                            selected = filterType == type
                        )
                    )
                }
            }

            // Content Grid
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                when (selectedTab) {
                    0 -> BackgroundsGrid(
                        currentId = settings.backgroundId,
                        filterType = filterType,
                        settings = settings,
                        onSelect = { id -> scope.launch { userPrefs.updateBackground(id) } },
                        onUnlock = { id, cost ->
                            scope.launch {
                                if (settings.coins >= cost) {
                                    val ok = userPrefs.unlockItem(id, cost)
                                    if (ok) {
                                        userPrefs.updateBackground(id)
                                        Toast.makeText(context, "Unlocked & Equipped!", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    Toast.makeText(context, "Need $cost Coins! Watch video ads to earn more.", Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    )
                    1 -> CardBacksGrid(
                        currentId = settings.cardBackId,
                        filterType = filterType,
                        settings = settings,
                        onSelect = { id -> scope.launch { userPrefs.updateCardBack(id) } },
                        onUnlock = { id, cost ->
                            scope.launch {
                                if (settings.coins >= cost) {
                                    val ok = userPrefs.unlockItem(id, cost)
                                    if (ok) {
                                        userPrefs.updateCardBack(id)
                                        Toast.makeText(context, "Unlocked & Equipped!", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    Toast.makeText(context, "Need $cost Coins! Watch video ads to earn more.", Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    )
                    2 -> CardFacesGrid(
                        currentId = settings.cardFaceId,
                        filterType = filterType,
                        settings = settings,
                        onSelect = { id -> scope.launch { userPrefs.updateCardFace(id) } },
                        onUnlock = { id, cost ->
                            scope.launch {
                                if (settings.coins >= cost) {
                                    val ok = userPrefs.unlockItem(id, cost)
                                    if (ok) {
                                        userPrefs.updateCardFace(id)
                                        Toast.makeText(context, "Unlocked & Equipped!", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    Toast.makeText(context, "Need $cost Coins! Watch video ads to earn more.", Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun BackgroundsGrid(
    currentId: String,
    filterType: String,
    settings: UserSettings,
    onSelect: (String) -> Unit,
    onUnlock: (String, Int) -> Unit
) {
    val items = CustomizationRegistry.backgrounds.filter { bg ->
        when (filterType) {
            "FREE" -> !bg.isPremium
            "PREMIUM" -> bg.isPremium
            else -> true
        }
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(items) { bg ->
            val isSelected = bg.id == currentId
            val isUnlocked = !bg.isPremium || settings.isItemUnlocked(bg.id)

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(88.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable {
                        if (isUnlocked) onSelect(bg.id) else onUnlock(bg.id, bg.coinCost)
                    }
                    .then(
                        if (isSelected) Modifier.border(2.5.dp, SleekEmerald400, RoundedCornerShape(12.dp))
                        else Modifier.border(1.dp, SleekBorderSubtle, RoundedCornerShape(12.dp))
                    )
                    .testTag("bg_${bg.id}")
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(bg.brush)
                        .padding(8.dp)
                ) {
                    Column(modifier = Modifier.align(Alignment.BottomStart)) {
                        Text(
                            text = bg.name,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                        if (bg.isPremium) {
                            Text(
                                text = if (isUnlocked) "UNLOCKED" else "${bg.coinCost} Coins",
                                color = if (isUnlocked) SleekEmerald400 else Color(0xFFFFD700),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 10.sp
                            )
                        } else {
                            Text("FREE", color = SleekSlate300, fontSize = 10.sp)
                        }
                    }

                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .background(SleekEmerald400, CircleShape)
                                .align(Alignment.TopEnd),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = SleekBgDark, modifier = Modifier.size(15.dp))
                        }
                    } else if (!isUnlocked) {
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .background(Color.Black.copy(alpha = 0.7f), CircleShape)
                                .align(Alignment.TopEnd),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(13.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CardBacksGrid(
    currentId: String,
    filterType: String,
    settings: UserSettings,
    onSelect: (String) -> Unit,
    onUnlock: (String, Int) -> Unit
) {
    val items = CustomizationRegistry.cardBacks.filter { back ->
        when (filterType) {
            "FREE" -> !back.isPremium
            "PREMIUM" -> back.isPremium
            else -> true
        }
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(items) { back ->
            val isSelected = back.id == currentId
            val isUnlocked = !back.isPremium || settings.isItemUnlocked(back.id)

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(118.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable {
                        if (isUnlocked) onSelect(back.id) else onUnlock(back.id, back.coinCost)
                    }
                    .then(
                        if (isSelected) Modifier.border(2.5.dp, SleekEmerald400, RoundedCornerShape(12.dp))
                        else Modifier.border(1.dp, SleekBorderSubtle, RoundedCornerShape(12.dp))
                    )
                    .testTag("cardback_${back.id}"),
                colors = CardDefaults.cardColors(containerColor = SleekHeaderDark)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box {
                        CardView(
                            card = Card(id = 1, suit = Suit.SPADES, rank = Rank.ACE, isFaceUp = false),
                            cardBack = back,
                            modifier = Modifier.size(42.dp, 60.dp)
                        )
                        if (!isUnlocked) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .background(Color.Black.copy(alpha = 0.8f), CircleShape)
                                    .align(Alignment.TopEnd),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(12.dp))
                            }
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = back.name,
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        maxLines = 1
                    )
                    Text(
                        text = if (!back.isPremium) "FREE" else if (isUnlocked) "UNLOCKED" else "${back.coinCost} Coins",
                        color = if (!back.isPremium) SleekSlate300 else if (isUnlocked) SleekEmerald400 else Color(0xFFFFD700),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun CardFacesGrid(
    currentId: String,
    filterType: String,
    settings: UserSettings,
    onSelect: (String) -> Unit,
    onUnlock: (String, Int) -> Unit
) {
    val items = CustomizationRegistry.cardFaces.filter { face ->
        when (filterType) {
            "FREE" -> !face.isPremium
            "PREMIUM" -> face.isPremium
            else -> true
        }
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(items) { face ->
            val isSelected = face.id == currentId
            val isUnlocked = !face.isPremium || settings.isItemUnlocked(face.id)

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(118.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable {
                        if (isUnlocked) onSelect(face.id) else onUnlock(face.id, face.coinCost)
                    }
                    .then(
                        if (isSelected) Modifier.border(2.5.dp, SleekEmerald400, RoundedCornerShape(12.dp))
                        else Modifier.border(1.dp, SleekBorderSubtle, RoundedCornerShape(12.dp))
                    )
                    .testTag("cardface_${face.id}"),
                colors = CardDefaults.cardColors(containerColor = SleekHeaderDark)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box {
                        CardView(
                            card = Card(id = 1, suit = Suit.HEARTS, rank = Rank.QUEEN, isFaceUp = true),
                            cardFace = face,
                            modifier = Modifier.size(42.dp, 60.dp)
                        )
                        if (!isUnlocked) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .background(Color.Black.copy(alpha = 0.8f), CircleShape)
                                    .align(Alignment.TopEnd),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(12.dp))
                            }
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = face.name,
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        maxLines = 1
                    )
                    Text(
                        text = if (!face.isPremium) "FREE" else if (isUnlocked) "UNLOCKED" else "${face.coinCost} Coins",
                        color = if (!face.isPremium) SleekSlate300 else if (isUnlocked) SleekEmerald400 else Color(0xFFFFD700),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
