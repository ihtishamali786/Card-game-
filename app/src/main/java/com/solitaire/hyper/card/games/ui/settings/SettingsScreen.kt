package com.solitaire.hyper.card.games.ui.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solitaire.hyper.card.games.ads.BannerAdView
import com.solitaire.hyper.card.games.data.preferences.UserPreferencesRepository
import com.solitaire.hyper.card.games.data.preferences.UserSettings
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
fun SettingsScreen(
    userPrefs: UserPreferencesRepository,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val settings by userPrefs.userSettingsFlow.collectAsState(initial = UserSettings())

    var showAboutDialog by remember { mutableStateOf(false) }

    val privacyUrl = "https://hypercardgames.blogspot.com/p/privacy-policy.html"
    val termsUrl = "https://hypercardgames.blogspot.com/p/terms-and-conditions.html"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.Bold) },
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Audio & Haptics
            SettingsSectionHeader("Audio & Vibration")
            Card(
                colors = CardDefaults.cardColors(containerColor = SleekHeaderDark),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column {
                    SettingsSwitchRow(
                        title = "Sound Effects",
                        subtitle = "Audio feedback for card movement and snaps",
                        checked = settings.soundEnabled,
                        onCheckedChange = { scope.launch { userPrefs.updateSound(it) } }
                    )
                    SettingsSwitchRow(
                        title = "Haptic Vibration",
                        subtitle = "Subtle tactile feedback when playing cards",
                        checked = settings.vibrationEnabled,
                        onCheckedChange = { scope.launch { userPrefs.updateVibration(it) } }
                    )
                }
            }

            // Gameplay & Controls
            SettingsSectionHeader("Gameplay & Controls")
            Card(
                colors = CardDefaults.cardColors(containerColor = SleekHeaderDark),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column {
                    SettingsSwitchRow(
                        title = "Left-Handed Mode",
                        subtitle = "Flips stock and waste piles for left-hand reach",
                        checked = settings.leftHandedMode,
                        onCheckedChange = { scope.launch { userPrefs.updateLeftHanded(it) } }
                    )
                    SettingsSwitchRow(
                        title = "Auto-Complete Helper",
                        subtitle = "Display quick win button when cards are solved",
                        checked = settings.autoCompleteEnabled,
                        onCheckedChange = { scope.launch { userPrefs.updateAutoComplete(it) } }
                    )
                    SettingsSwitchRow(
                        title = "Daily Challenge Alerts",
                        subtitle = "Remind me when new daily puzzles become available",
                        checked = settings.notificationsEnabled,
                        onCheckedChange = { scope.launch { userPrefs.updateNotifications(it) } }
                    )
                    SettingsSwitchRow(
                        title = "Game Timer & Moves",
                        subtitle = "Track active play time and move counter on table",
                        checked = settings.showTimer,
                        onCheckedChange = { scope.launch { userPrefs.updateShowTimer(it) } }
                    )
                }
            }

            // Legal & About
            SettingsSectionHeader("About & Legal")
            Card(
                colors = CardDefaults.cardColors(containerColor = SleekHeaderDark),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column {
                    SettingsActionRow(
                        title = "Privacy Policy",
                        icon = Icons.Default.OpenInNew,
                        onClick = { openBrowserUrl(context, privacyUrl) }
                    )
                    SettingsActionRow(
                        title = "Terms & Conditions",
                        icon = Icons.Default.OpenInNew,
                        onClick = { openBrowserUrl(context, termsUrl) }
                    )
                    SettingsActionRow(
                        title = "Share Solitaire",
                        icon = Icons.Default.Share,
                        onClick = { shareApp(context) }
                    )
                    SettingsActionRow(
                        title = "Rate App",
                        icon = Icons.Default.Star,
                        onClick = { rateApp(context) }
                    )
                    SettingsActionRow(
                        title = "About Application",
                        icon = Icons.Default.Info,
                        onClick = { showAboutDialog = true }
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
        }

        if (showAboutDialog) {
            AlertDialog(
                onDismissRequest = { showAboutDialog = false },
                title = { Text("Solitaire-Hyper Card Games") },
                text = {
                    Column {
                        Text("Version 2.0.0 (Play Console Release)")
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "A master-crafted Klondike Solitaire card experience with daily challenges, " +
                                    "offline play, rich customization themes, timer controls, and full statistics tracking.",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(8.dp))
                        Text("Package: com.solitaire.hyper.card.games", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showAboutDialog = false }) {
                        Text("OK")
                    }
                }
            )
        }
    }
}

@Composable
fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        color = SleekEmerald400,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(start = 4.dp)
    )
}

@Composable
fun SettingsSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = SleekSlate100, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            Text(subtitle, color = SleekSlate400, fontSize = 12.sp)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = SleekEmerald500
            )
        )
    }
}

@Composable
fun SettingsActionRow(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(title, color = Color.White, fontSize = 14.sp)
        Icon(icon, contentDescription = null, tint = Color.White.copy(alpha = 0.6f))
    }
}

private fun openBrowserUrl(context: Context, url: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    } catch (e: Exception) {
        // Safe fallback
    }
}

private fun shareApp(context: Context) {
    try {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "Play Solitaire-Hyper Card Games! Download on Google Play: https://play.google.com/store/apps/details?id=${context.packageName}")
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Share Solitaire Hyper")
        context.startActivity(shareIntent)
    } catch (e: Exception) {
        // Safe fallback
    }
}

private fun rateApp(context: Context) {
    try {
        val uri = Uri.parse("market://details?id=${context.packageName}")
        val goToMarket = Intent(Intent.ACTION_VIEW, uri)
        context.startActivity(goToMarket)
    } catch (e: Exception) {
        openBrowserUrl(context, "https://play.google.com/store/apps/details?id=${context.packageName}")
    }
}
