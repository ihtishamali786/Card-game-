package com.solitaire.hyper.card.games.ui.shop

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.solitaire.hyper.card.games.ads.AdManager
import com.solitaire.hyper.card.games.data.preferences.UserPreferencesRepository
import com.solitaire.hyper.card.games.data.preferences.UserSettings
import com.solitaire.hyper.card.games.ui.theme.SleekEmerald400
import com.solitaire.hyper.card.games.ui.theme.SleekEmerald500
import com.solitaire.hyper.card.games.ui.theme.SleekHeaderDark
import com.solitaire.hyper.card.games.ui.theme.SleekSlate100
import com.solitaire.hyper.card.games.ui.theme.SleekSlate300
import com.solitaire.hyper.card.games.ui.theme.SleekSlate400
import kotlinx.coroutines.launch

@Composable
fun ShopDialog(
    userSettings: UserSettings,
    userPrefs: UserPreferencesRepository,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val scope = rememberCoroutineScope()

    val isVipActive = userSettings.isVipActive()
    val isAdFreeActive = userSettings.isAdFreeActive()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.90f)
                .testTag("shop_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = SleekHeaderDark),
            border = BorderStroke(1.5.dp, Brush.linearGradient(listOf(Color(0xFFFFD700), Color(0xFF10B981))))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp)
            ) {
                // Top Header: Title, Coins, Close
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🪙", fontSize = 22.sp)
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Solitaire Store & VIP",
                                color = SleekSlate100,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = "Ad-Free Tokens, VIP Pass & Rewards",
                                color = SleekSlate400,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Current Coins Badge
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0xFF2B2103),
                            border = BorderStroke(1.dp, Color(0xFFFFD700).copy(alpha = 0.5f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("🪙", fontSize = 13.sp)
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    text = "${userSettings.coins}",
                                    color = Color(0xFFFFD700),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 13.sp
                                )
                            }
                        }

                        IconButton(onClick = onDismiss, modifier = Modifier.size(36.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = SleekSlate300)
                        }
                    }
                }

                Spacer(Modifier.height(14.dp))

                // Scrollable Store Inventory
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // SECTION 1: AD-FREE TOKENS & TIME PASSES
                    ShopSectionHeader(title = "AD-FREE TOKENS & PASSES", icon = Icons.Default.Shield)

                    // Current Token Inventory Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF132A22)),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, SleekEmerald400.copy(alpha = 0.4f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Your Ad-Free Tokens: ${userSettings.adFreeTokens}",
                                        color = SleekEmerald400,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                    Text(
                                        text = if (isAdFreeActive) "Status: ${userSettings.getAdFreeRemainingMinutes()}m Ad-Free Active"
                                        else "Each token provides 30 mins 100% ad-free",
                                        color = SleekSlate300,
                                        fontSize = 11.5.sp
                                    )
                                }

                                Button(
                                    onClick = {
                                        scope.launch {
                                            val ok = userPrefs.useAdFreeToken()
                                            if (ok) {
                                                Toast.makeText(context, "30m Ad-Free Activated!", Toast.LENGTH_SHORT).show()
                                            } else {
                                                Toast.makeText(context, "No tokens left! Buy or watch ad.", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    },
                                    enabled = userSettings.adFreeTokens > 0,
                                    colors = ButtonDefaults.buttonColors(containerColor = SleekEmerald500),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.height(34.dp)
                                ) {
                                    Text("USE 1 TOKEN", fontSize = 11.sp, fontWeight = FontWeight.Black)
                                }
                            }

                            Spacer(Modifier.height(8.dp))

                            // Watch ad for 1 free token
                            if (activity != null) {
                                OutlinedButton(
                                    onClick = {
                                        AdManager.showRewardedAd(
                                            activity = activity,
                                            onRewardEarned = {
                                                scope.launch {
                                                    userPrefs.addAdFreeTokens(1)
                                                    Toast.makeText(context, "+1 Ad-Free Token Earned!", Toast.LENGTH_SHORT).show()
                                                }
                                            },
                                            onDismissOrFailed = {
                                                // No token awarded if ad failed to load, was blocked by DNS, or was skipped
                                            }
                                        )
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp),
                                    border = BorderStroke(1.dp, SleekEmerald400.copy(alpha = 0.5f))
                                ) {
                                    Text("🎬 Watch Video Ad for +1 Ad-Free Token (FREE)", color = SleekEmerald400, fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    // Buy Token Bundles
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        ShopBuyCard(
                            modifier = Modifier.weight(1f),
                            title = "1 Token",
                            subtitle = "30m Ad-Free",
                            priceText = "400 🪙",
                            canAfford = userSettings.coins >= 400,
                            onBuy = {
                                scope.launch {
                                    val ok = userPrefs.buyAdFreeTokens(1, 400)
                                    if (ok) Toast.makeText(context, "1 Token Purchased!", Toast.LENGTH_SHORT).show()
                                    else Toast.makeText(context, "Need 400 Coins!", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )

                        ShopBuyCard(
                            modifier = Modifier.weight(1f),
                            title = "3 Tokens",
                            subtitle = "90m Ad-Free (Save 200)",
                            priceText = "1,000 🪙",
                            isPopular = true,
                            canAfford = userSettings.coins >= 1000,
                            onBuy = {
                                scope.launch {
                                    val ok = userPrefs.buyAdFreeTokens(3, 1000)
                                    if (ok) Toast.makeText(context, "3 Tokens Purchased!", Toast.LENGTH_SHORT).show()
                                    else Toast.makeText(context, "Need 1,000 Coins!", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                    }

                    // Direct Time Passes
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        ShopBuyCard(
                            modifier = Modifier.weight(1f),
                            title = "1-Hour Pass",
                            subtitle = "Direct Ad-Free",
                            priceText = "1,000 🪙",
                            canAfford = userSettings.coins >= 1000,
                            onBuy = {
                                scope.launch {
                                    val ok = userPrefs.activateAdFreePass(1, 1000)
                                    if (ok) Toast.makeText(context, "1-Hour Ad-Free Pass Activated!", Toast.LENGTH_SHORT).show()
                                    else Toast.makeText(context, "Need 1,000 Coins!", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )

                        ShopBuyCard(
                            modifier = Modifier.weight(1f),
                            title = "24-Hour Pass",
                            subtitle = "Full Day Ad-Free",
                            priceText = "3,500 🪙",
                            canAfford = userSettings.coins >= 3500,
                            onBuy = {
                                scope.launch {
                                    val ok = userPrefs.activateAdFreePass(24, 3500)
                                    if (ok) Toast.makeText(context, "24-Hour Ad-Free Pass Activated!", Toast.LENGTH_SHORT).show()
                                    else Toast.makeText(context, "Need 3,500 Coins!", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                    }

                    // SECTION 2: VIP PREMIUM CROWN ACCESS
                    ShopSectionHeader(title = "VIP PREMIUM ACCESS", icon = Icons.Default.WorkspacePremium)

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF332305)),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.5.dp, Color(0xFFFFD700).copy(alpha = 0.6f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("👑", fontSize = 20.sp)
                                    Spacer(Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = if (userSettings.isVipPermanent) "PERMANENT VIP ACTIVE"
                                            else if (isVipActive) "VIP ACTIVE (${userSettings.getVipRemainingMinutes()}m left)"
                                            else "VIP Crown Pass",
                                            color = Color(0xFFFFD700),
                                            fontWeight = FontWeight.Black,
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            text = "All 3D Themes + Zero Ads + Infinite AI & Wands",
                                            color = SleekSlate300,
                                            fontSize = 10.5.sp
                                        )
                                    }
                                }
                            }

                            Spacer(Modifier.height(10.dp))

                            // Watch 5 Ads for 1-Hour VIP
                            if (!isVipActive && activity != null) {
                                Text(
                                    text = "Watch 5 video ads to unlock 1-Hour VIP (${userSettings.rewardedAdsWatchedForVip}/5):",
                                    color = SleekSlate300,
                                    fontSize = 11.sp
                                )
                                Spacer(Modifier.height(4.dp))
                                LinearProgressIndicator(
                                    progress = { userSettings.rewardedAdsWatchedForVip / 5f },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(5.dp)
                                        .clip(RoundedCornerShape(3.dp)),
                                    color = Color(0xFFFFD700),
                                    trackColor = Color.White.copy(alpha = 0.1f)
                                )
                                Spacer(Modifier.height(6.dp))
                                OutlinedButton(
                                    onClick = {
                                        AdManager.showRewardedAd(
                                            activity = activity,
                                            onRewardEarned = {
                                                scope.launch {
                                                    val vipUnlocked = userPrefs.recordRewardedAdForVip()
                                                    if (vipUnlocked) Toast.makeText(context, "🎉 VIP Crown Pass Unlocked!", Toast.LENGTH_LONG).show()
                                                    else Toast.makeText(context, "Watched ad towards VIP!", Toast.LENGTH_SHORT).show()
                                                }
                                            },
                                            onDismissOrFailed = {
                                                // No VIP progress if video ad was not completed or was blocked
                                            }
                                        )
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp),
                                    border = BorderStroke(1.dp, Color(0xFFFFD700).copy(alpha = 0.6f))
                                ) {
                                    Text("🎬 Watch Video Ad for VIP Progress", color = Color(0xFFFFD700), fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                ShopBuyCard(
                                    modifier = Modifier.weight(1f),
                                    title = "24H VIP",
                                    subtitle = "Full Day Access",
                                    priceText = "2,500 🪙",
                                    canAfford = userSettings.coins >= 2500,
                                    onBuy = {
                                        scope.launch {
                                            val ok = userPrefs.activateVipDayPass(2500)
                                            if (ok) Toast.makeText(context, "24-Hour VIP Pass Activated!", Toast.LENGTH_SHORT).show()
                                            else Toast.makeText(context, "Need 2,500 Coins!", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                )

                                ShopBuyCard(
                                    modifier = Modifier.weight(1f),
                                    title = "Permanent VIP",
                                    subtitle = "Lifetime Access",
                                    priceText = "10,000 🪙",
                                    isPopular = true,
                                    canAfford = userSettings.coins >= 10000,
                                    onBuy = {
                                        scope.launch {
                                            val ok = userPrefs.activatePermanentVip(10000)
                                            if (ok) Toast.makeText(context, "👑 Permanent VIP Crown Activated Forever!", Toast.LENGTH_LONG).show()
                                            else Toast.makeText(context, "Need 10,000 Coins!", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                )
                            }
                        }
                    }

                    // SECTION 3: AI COACH & MAGIC WANDS
                    ShopSectionHeader(title = "AI COACH & MAGIC WANDS", icon = Icons.Default.SmartToy)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // AI Tokens Card
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1B4B)),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, Color(0xFF6366F1).copy(alpha = 0.4f))
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("🤖 AI Coach Tokens", color = Color(0xFFC7D2FE), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text("Available: ${userSettings.aiTokens}", color = SleekSlate300, fontSize = 11.sp)
                                Spacer(Modifier.height(6.dp))
                                Button(
                                    onClick = {
                                        scope.launch {
                                            val ok = userPrefs.buyAiTokens(5, 350)
                                            if (ok) Toast.makeText(context, "+5 AI Tokens Purchased!", Toast.LENGTH_SHORT).show()
                                            else Toast.makeText(context, "Need 350 Coins!", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F46E5)),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("5 Tokens: 350🪙", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        // Magic Wands Card
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF2C2208)),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, Color(0xFFFFD54F).copy(alpha = 0.4f))
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("🪄 Magic Wands", color = Color(0xFFFFE082), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text("Available: ${userSettings.magicWands}", color = SleekSlate300, fontSize = 11.sp)
                                Spacer(Modifier.height(6.dp))
                                Button(
                                    onClick = {
                                        scope.launch {
                                            val ok = userPrefs.buyMagicWands(3, 750)
                                            if (ok) Toast.makeText(context, "+3 Magic Wands Purchased!", Toast.LENGTH_SHORT).show()
                                            else Toast.makeText(context, "Need 750 Coins!", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB45309)),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("3 Wands: 750🪙", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    // SECTION 4: UNLIMITED FREE COINS VIA REWARDED ADS
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1B2332)),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.4f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("🪙", fontSize = 20.sp)
                                Spacer(Modifier.width(8.dp))
                                Column {
                                    Text("Need More Coins?", color = SleekSlate100, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("Watch a video ad to earn +250 Coins", color = SleekSlate400, fontSize = 11.sp)
                                }
                            }

                            if (activity != null) {
                                Button(
                                    onClick = {
                                        AdManager.showRewardedAd(
                                            activity = activity,
                                            onRewardEarned = {
                                                scope.launch {
                                                    userPrefs.addCoins(250)
                                                    userPrefs.incrementRewardedAdsWatched()
                                                    Toast.makeText(context, "+250 Coins Earned!", Toast.LENGTH_SHORT).show()
                                                }
                                            },
                                            onDismissOrFailed = {
                                                // No coins awarded if video ad was not watched or was blocked
                                            }
                                        )
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.height(34.dp)
                                ) {
                                    Text("🎬 +250 🪙", color = Color.White, fontSize = 11.5.sp, fontWeight = FontWeight.Black)
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun ShopSectionHeader(title: String, icon: ImageVector) {
    Row(
        modifier = Modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = SleekEmerald400, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(6.dp))
        Text(
            text = title,
            color = SleekEmerald400,
            fontSize = 11.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 1.sp
        )
    }
}

@Composable
fun ShopBuyCard(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    priceText: String,
    isPopular: Boolean = false,
    canAfford: Boolean = true,
    onBuy: () -> Unit
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF1E2530),
        border = BorderStroke(1.dp, if (isPopular) Color(0xFFFFD700) else Color.White.copy(alpha = 0.08f))
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isPopular) {
                Text(
                    text = "BEST VALUE",
                    color = Color(0xFFFFD700),
                    fontSize = 8.5.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
            }
            Text(title, color = SleekSlate100, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Text(subtitle, color = SleekSlate400, fontSize = 10.sp)
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = onBuy,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(32.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (canAfford) SleekEmerald500 else Color(0xFF374151)
                )
            ) {
                Text(priceText, fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color.White)
            }
        }
    }
}
