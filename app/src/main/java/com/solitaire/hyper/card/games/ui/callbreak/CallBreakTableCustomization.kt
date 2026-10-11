package com.solitaire.hyper.card.games.ui.callbreak

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solitaire.hyper.card.games.ui.customization.CardBackTheme
import com.solitaire.hyper.card.games.ui.customization.CustomizationRegistry
import com.solitaire.hyper.card.games.ui.theme.SleekEmerald400
import com.solitaire.hyper.card.games.ui.theme.SleekEmerald500
import com.solitaire.hyper.card.games.ui.theme.SleekHeaderDark
import com.solitaire.hyper.card.games.ui.theme.SleekSlate100
import com.solitaire.hyper.card.games.ui.theme.SleekSlate300
import com.solitaire.hyper.card.games.ui.theme.SleekSlate400

enum class CallBreakTableTheme(
    val displayName: String,
    val feltCenterColor: Color,
    val feltEdgeColor: Color,
    val railOuterColor: Color,
    val railHighlightColor: Color,
    val railInnerColor: Color,
    val accentTrimColor: Color,
    val subtitle: String
) {
    EMERALD_CASINO_3D(
        displayName = "Emerald Casino 3D",
        feltCenterColor = Color(0xFF1E754A),
        feltEdgeColor = Color(0xFF093620),
        railOuterColor = Color(0xFF26140A),
        railHighlightColor = Color(0xFF5D381B),
        railInnerColor = Color(0xFF140A05),
        accentTrimColor = Color(0xFFFFD700),
        subtitle = "Classic Vegas Green Felt & Polished Mahogany"
    ),
    SAPPHIRE_ROYALE_3D(
        displayName = "Sapphire Royale 3D",
        feltCenterColor = Color(0xFF194C8F),
        feltEdgeColor = Color(0xFF081F3E),
        railOuterColor = Color(0xFF0E182A),
        railHighlightColor = Color(0xFF3A5882),
        railInnerColor = Color(0xFF060B14),
        accentTrimColor = Color(0xFF38BDF8),
        subtitle = "Monte Carlo Ocean Blue with Platinum Rail"
    ),
    RUBY_VELVET_3D(
        displayName = "Ruby Velvet 3D",
        feltCenterColor = Color(0xFF8B1832),
        feltEdgeColor = Color(0xFF3E0814),
        railOuterColor = Color(0xFF280B12),
        railHighlightColor = Color(0xFF6B2636),
        railInnerColor = Color(0xFF120407),
        accentTrimColor = Color(0xFFFFD700),
        subtitle = "High-Roller Deep Velvet Crimson & 24K Trim"
    ),
    OBSIDIAN_GOLD_3D(
        displayName = "Obsidian Gold 3D",
        feltCenterColor = Color(0xFF2D2F3A),
        feltEdgeColor = Color(0xFF111218),
        railOuterColor = Color(0xFF16161A),
        railHighlightColor = Color(0xFF454134),
        railInnerColor = Color(0xFF09090C),
        accentTrimColor = Color(0xFFFFC107),
        subtitle = "VIP Midnight Slate with Pure Gold Trim"
    ),
    AMETHYST_LUXURY_3D(
        displayName = "Amethyst Luxury 3D",
        feltCenterColor = Color(0xFF6B1D8C),
        feltEdgeColor = Color(0xFF2E073D),
        railOuterColor = Color(0xFF1F0B2A),
        railHighlightColor = Color(0xFF582A70),
        railInnerColor = Color(0xFF0E0414),
        accentTrimColor = Color(0xFFF472B6),
        subtitle = "Royal Purple Velvet with Rose Gold Accents"
    );

    companion object {
        fun getRandom(except: CallBreakTableTheme? = null): CallBreakTableTheme {
            val pool = entries.filter { it != except }
            return pool.randomOrNull() ?: entries.first()
        }
    }
}

/**
 * Dialog allowing user to change Call Break 3D table felt background, card backs,
 * and toggle auto-change on every new game/round.
 */
@Composable
fun CallBreakCustomizationDialog(
    currentTheme: CallBreakTableTheme,
    currentCardBack: CardBackTheme,
    autoChangeOnNewGame: Boolean,
    onThemeSelected: (CallBreakTableTheme) -> Unit,
    onCardBackSelected: (CardBackTheme) -> Unit,
    onToggleAutoChange: (Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    val cardBacksPool = CustomizationRegistry.cardBacks.take(12)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Palette,
                        contentDescription = null,
                        tint = SleekEmerald400,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Customize Table & Cards",
                        color = SleekSlate100,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = SleekSlate400)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                // Auto change option
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = SleekHeaderDark,
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Color(0xFFFFD700),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Auto-Change Each Game",
                                    color = SleekSlate100,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "New background & cards every game",
                                    color = SleekSlate300,
                                    fontSize = 11.sp
                                )
                            }
                        }
                        Switch(
                            checked = autoChangeOnNewGame,
                            onCheckedChange = onToggleAutoChange,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = SleekEmerald500
                            )
                        )
                    }
                }

                Spacer(Modifier.height(14.dp))

                // Table 3D Backgrounds Selector
                Text(
                    text = "3D TABLE BACKGROUND",
                    color = SleekEmerald400,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
                Spacer(Modifier.height(8.dp))

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(CallBreakTableTheme.entries) { theme ->
                        val isSelected = theme == currentTheme
                        Surface(
                            modifier = Modifier
                                .width(120.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { onThemeSelected(theme) }
                                .testTag("theme_${theme.name}"),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(
                                if (isSelected) 2.dp else 1.dp,
                                if (isSelected) theme.accentTrimColor else Color.White.copy(alpha = 0.12f)
                            ),
                            color = SleekHeaderDark
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                // 3D Felt preview circle
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(
                                            Brush.radialGradient(
                                                listOf(theme.feltCenterColor, theme.feltEdgeColor, theme.railOuterColor)
                                            )
                                        )
                                        .border(1.5.dp, theme.accentTrimColor, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isSelected) {
                                        Icon(
                                            Icons.Default.Check,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                                Spacer(Modifier.height(6.dp))
                                Text(
                                    text = theme.displayName,
                                    color = if (isSelected) Color.White else SleekSlate300,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(14.dp))

                // Card Back Selector
                Text(
                    text = "CARD DECK STYLE",
                    color = Color(0xFFFFD700),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
                Spacer(Modifier.height(8.dp))

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(cardBacksPool) { cardBack ->
                        val isSelected = cardBack.id == currentCardBack.id
                        Surface(
                            modifier = Modifier
                                .width(85.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { onCardBackSelected(cardBack) }
                                .testTag("card_back_${cardBack.id}"),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(
                                if (isSelected) 2.dp else 1.dp,
                                if (isSelected) Color(0xFFFFD700) else Color.White.copy(alpha = 0.12f)
                            ),
                            color = SleekHeaderDark
                        ) {
                            Column(
                                modifier = Modifier.padding(6.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(width = 40.dp, height = 56.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(
                                            Brush.linearGradient(
                                                listOf(cardBack.baseColor, cardBack.accentColor)
                                            )
                                        )
                                        .border(1.dp, Color.White.copy(alpha = 0.4f), RoundedCornerShape(6.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isSelected) {
                                        Icon(
                                            Icons.Default.Check,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = cardBack.name,
                                    color = if (isSelected) Color.White else SleekSlate300,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = SleekEmerald500)
            ) {
                Text("Apply & Play", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    )
}
