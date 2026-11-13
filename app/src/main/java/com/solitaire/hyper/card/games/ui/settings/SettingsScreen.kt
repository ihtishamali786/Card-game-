package com.solitaire.hyper.card.games.ui.settings

import android.app.Activity
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DoNotDisturb
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PanTool
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Style
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.solitaire.hyper.card.games.billing.BillingManager
import com.solitaire.hyper.card.games.data.preferences.UserPreferencesRepository
import com.solitaire.hyper.card.games.data.preferences.UserSettings
import com.solitaire.hyper.card.games.notifications.NotificationScheduler
import com.solitaire.hyper.card.games.ui.components.QuickTutorialDialog
import com.solitaire.hyper.card.games.ui.components.RulesDialog
import com.solitaire.hyper.card.games.ui.theme.SleekBgDark
import com.solitaire.hyper.card.games.ui.theme.SleekEmerald400
import com.solitaire.hyper.card.games.ui.theme.SleekEmerald500
import com.solitaire.hyper.card.games.ui.theme.SleekHeaderDark
import com.solitaire.hyper.card.games.ui.theme.SleekSlate100
import com.solitaire.hyper.card.games.ui.theme.SleekSlate300
import com.solitaire.hyper.card.games.ui.theme.SleekSlate400
import kotlinx.coroutines.launch
import java.util.Locale

// -------------------------------------------------------------------------------------------------
// PRIVACY POLICY URL (Replace with your live privacy policy URL hosted for Google Play Console)
// -------------------------------------------------------------------------------------------------
const val PRIVACY_POLICY_URL = "https://policies.google.com/privacy"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    userPrefs: UserPreferencesRepository,
    billingManager: BillingManager? = null,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val scope = rememberCoroutineScope()
    val settings by userPrefs.userSettingsFlow.collectAsState(initial = UserSettings())

    var showRulesDialog by remember { mutableStateOf(false) }
    var showTutorialDialog by remember { mutableStateOf(false) }

    // Runtime permission launcher for notifications (Android 13+)
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            scope.launch {
                userPrefs.updateDailyReminder(true, settings.dailyReminderHour, settings.dailyReminderMinute)
                NotificationScheduler.scheduleDailyReminder(context, settings.dailyReminderHour, settings.dailyReminderMinute)
                Toast.makeText(context, "Daily reminder enabled for ${formatTime(settings.dailyReminderHour, settings.dailyReminderMinute)}", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Notification permission is required to enable daily reminders", Toast.LENGTH_LONG).show()
        }
    }

    if (showRulesDialog) {
        RulesDialog(onDismiss = { showRulesDialog = false })
    }

    if (showTutorialDialog) {
        QuickTutorialDialog(onDismiss = { showTutorialDialog = false })
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings",
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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Section 1: Gameplay Controls
            SettingsSectionHeader(title = "GAMEPLAY")

            SettingsCard {
                // Draw Mode Selection
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Default Draw Mode", color = SleekSlate100, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                        Text(text = "Cards turned from stock each tap", color = SleekSlate400, fontSize = 12.sp)
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        val isDraw1 = settings.defaultDrawMode == "DRAW_1"
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isDraw1) SleekEmerald500 else Color(0xFF1E293B),
                            modifier = Modifier.clickable { scope.launch { userPrefs.updateDefaultDrawMode("DRAW_1") } }
                        ) {
                            Text(
                                text = "Draw 1",
                                color = if (isDraw1) Color.Black else SleekSlate300,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (!isDraw1) SleekEmerald500 else Color(0xFF1E293B),
                            modifier = Modifier.clickable { scope.launch { userPrefs.updateDefaultDrawMode("DRAW_3") } }
                        ) {
                            Text(
                                text = "Draw 3",
                                color = if (!isDraw1) Color.Black else SleekSlate300,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                SettingsDivider()

                // Winnable / Easy Deals Switch (Default ON)
                SettingsToggleRow(
                    title = "Easy / Winnable Deals",
                    subtitle = "Ensures dealt hands have solvable winning paths",
                    icon = Icons.Default.Lightbulb,
                    checked = settings.winnableDealsOnly,
                    onCheckedChange = { checked ->
                        scope.launch { userPrefs.updateWinnableDealsOnly(checked) }
                    }
                )

                SettingsDivider()

                // Left-Handed Mode
                SettingsToggleRow(
                    title = "Left-Handed Mode",
                    subtitle = "Mirrors stock and foundation piles for easy thumb reach",
                    icon = Icons.Default.PanTool,
                    checked = settings.leftHandedMode,
                    onCheckedChange = { checked ->
                        scope.launch { userPrefs.updateLeftHanded(checked) }
                    }
                )

                SettingsDivider()

                // Large-Print Mode
                SettingsToggleRow(
                    title = "Large-Print Cards",
                    subtitle = "Enlarges rank and suit fonts for maximum readability",
                    icon = Icons.Default.TextFields,
                    checked = settings.largePrintMode,
                    onCheckedChange = { checked ->
                        scope.launch { userPrefs.updateLargePrint(checked) }
                    }
                )
            }

            // Section 2: Audio, Haptics & Relaxing Ambience
            SettingsSectionHeader(title = "AUDIO & AMBIENCE")

            SettingsCard {
                SettingsToggleRow(
                    title = "Sound Effects",
                    subtitle = "Card dealing, clicks, and victory fanfare",
                    icon = Icons.AutoMirrored.Filled.VolumeUp,
                    checked = settings.soundEnabled,
                    onCheckedChange = { checked ->
                        scope.launch { userPrefs.updateSound(checked) }
                    }
                )

                SettingsDivider()

                SettingsToggleRow(
                    title = "Haptic Vibration",
                    subtitle = "Tactile feedback on card placement and cascade",
                    icon = Icons.Default.Vibration,
                    checked = settings.hapticsEnabled,
                    onCheckedChange = { checked ->
                        scope.launch { userPrefs.updateHaptics(checked) }
                    }
                )

                SettingsDivider()

                SettingsToggleRow(
                    title = "Zen Ambient Lights",
                    subtitle = "Gentle floating orbs on table background",
                    icon = Icons.Default.Style,
                    checked = settings.floatingLights,
                    onCheckedChange = { checked ->
                        scope.launch { userPrefs.updateFloatingLights(checked) }
                    }
                )

                SettingsDivider()

                SettingsToggleRow(
                    title = "Reduce Motion",
                    subtitle = "Disables background ambient motion and particles",
                    icon = Icons.Default.DoNotDisturb,
                    checked = settings.reduceMotion,
                    onCheckedChange = { checked ->
                        scope.launch { userPrefs.updateReduceMotion(checked) }
                    }
                )
            }

            // Section 3: Daily Zen Reminder (Single, user-controlled)
            SettingsSectionHeader(title = "DAILY ZEN REMINDER")

            SettingsCard {
                SettingsToggleRow(
                    title = "Daily Reminder",
                    subtitle = if (settings.dailyReminderEnabled)
                        "Scheduled at ${formatTime(settings.dailyReminderHour, settings.dailyReminderMinute)}"
                    else "Disabled — tap to schedule a daily pause",
                    icon = Icons.Default.Alarm,
                    checked = settings.dailyReminderEnabled,
                    onCheckedChange = { checked ->
                        if (checked) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                                ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
                            ) {
                                notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                            } else {
                                scope.launch {
                                    userPrefs.updateDailyReminder(true, settings.dailyReminderHour, settings.dailyReminderMinute)
                                    NotificationScheduler.scheduleDailyReminder(context, settings.dailyReminderHour, settings.dailyReminderMinute)
                                }
                            }
                        } else {
                            scope.launch {
                                userPrefs.updateDailyReminder(false, settings.dailyReminderHour, settings.dailyReminderMinute)
                                NotificationScheduler.cancelDailyReminder(context)
                                Toast.makeText(context, "Daily reminder disabled", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                )

                if (settings.dailyReminderEnabled) {
                    SettingsDivider()
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                TimePickerDialog(
                                    context,
                                    { _, hour, minute ->
                                        scope.launch {
                                            userPrefs.updateDailyReminder(true, hour, minute)
                                            NotificationScheduler.scheduleDailyReminder(context, hour, minute)
                                        }
                                    },
                                    settings.dailyReminderHour,
                                    settings.dailyReminderMinute,
                                    false
                                ).show()
                            }
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Change Reminder Time", color = SleekSlate300, fontSize = 14.sp)
                        Text(
                            text = formatTime(settings.dailyReminderHour, settings.dailyReminderMinute),
                            color = SleekEmerald400,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            // Section 4: Premium & Ads Removal
            SettingsSectionHeader(title = "IN-APP PURCHASE")

            SettingsCard {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(Color(0xFF0F251E), CircleShape)
                            .border(1.dp, SleekEmerald400.copy(alpha = 0.5f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingBag,
                            contentDescription = null,
                            tint = SleekEmerald400,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (settings.isAdsRemoved) "Ads Removed (Active)" else "Remove Ads",
                            color = SleekSlate100,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (settings.isAdsRemoved)
                                "All banners and interstitials are permanently removed"
                            else "One-time purchase to permanently remove banner and interstitial ads",
                            color = SleekSlate400,
                            fontSize = 12.sp
                        )
                    }
                }

                if (!settings.isAdsRemoved) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                activity?.let { act ->
                                    billingManager?.launchPurchaseFlow(act)
                                }
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = SleekEmerald500),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(text = "Purchase", color = Color.Black, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = {
                                billingManager?.queryPurchases { hasRestored ->
                                    Toast.makeText(
                                        context,
                                        if (hasRestored) "Purchases restored!" else "No existing purchase found",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, SleekSlate400.copy(alpha = 0.4f))
                        ) {
                            Text(text = "Restore", color = SleekSlate100)
                        }
                    }
                }
            }

            // Section 5: Help & Privacy
            SettingsSectionHeader(title = "HELP & LEGAL")

            SettingsCard {
                SettingsActionRow(
                    title = "How to Play",
                    subtitle = "Complete rules, scoring, and card interaction guide",
                    icon = Icons.AutoMirrored.Filled.MenuBook,
                    onClick = { showRulesDialog = true }
                )

                SettingsDivider()

                SettingsActionRow(
                    title = "Quick Tutorial",
                    subtitle = "Replay the 3-screen beginner walkthrough",
                    icon = Icons.Default.Check,
                    onClick = { showTutorialDialog = true }
                )

                SettingsDivider()

                SettingsActionRow(
                    title = "Privacy Policy",
                    subtitle = "Read our data safety and privacy commitment",
                    icon = Icons.Default.Policy,
                    onClick = {
                        openWebUrl(context, PRIVACY_POLICY_URL)
                    }
                )

                SettingsDivider()

                SettingsActionRow(
                    title = "Share Solitaire Joker",
                    subtitle = "Share this relaxing Zen game with friends",
                    icon = Icons.Default.Share,
                    onClick = {
                        shareApp(context)
                    }
                )
            }

            // About Footer Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SleekHeaderDark.copy(alpha = 0.6f)),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Solitaire Joker: Klondike Zen",
                        color = SleekSlate100,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Relax. Play. Win with the Joker.",
                        color = Color(0xFFFBBF24),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                    Text(
                        text = "Version 18.0.0 (Build 18) • 100% Offline Capable",
                        color = SleekSlate400,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        color = SleekEmerald400,
        fontSize = 11.5.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.1.sp,
        modifier = Modifier.padding(start = 4.dp, top = 4.dp)
    )
}

@Composable
private fun SettingsCard(content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SleekHeaderDark),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            content()
        }
    }
}

@Composable
private fun SettingsToggleRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(Color.White.copy(alpha = 0.06f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = SleekSlate100,
                modifier = Modifier.size(19.dp)
            )
        }

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = SleekSlate100, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            Text(text = subtitle, color = SleekSlate400, fontSize = 11.5.sp)
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.Black,
                checkedTrackColor = SleekEmerald400,
                uncheckedThumbColor = SleekSlate400,
                uncheckedTrackColor = Color(0xFF1E293B)
            )
        )
    }
}

@Composable
private fun SettingsActionRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(Color.White.copy(alpha = 0.06f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = SleekSlate100,
                modifier = Modifier.size(19.dp)
            )
        }

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = SleekSlate100, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            Text(text = subtitle, color = SleekSlate400, fontSize = 11.5.sp)
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.OpenInNew,
            contentDescription = null,
            tint = SleekSlate400,
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
private fun SettingsDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(0.6.dp)
            .background(Color.White.copy(alpha = 0.06f))
    )
}

private fun formatTime(hour: Int, minute: Int): String {
    val amPm = if (hour < 12) "AM" else "PM"
    val h = if (hour == 0) 12 else if (hour > 12) hour - 12 else hour
    return String.format(Locale.US, "%d:%02d %s", h, minute, amPm)
}

private fun openWebUrl(context: Context, url: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    } catch (_: Exception) {
        Toast.makeText(context, "Could not open browser", Toast.LENGTH_SHORT).show()
    }
}

private fun shareApp(context: Context) {
    try {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(
                Intent.EXTRA_TEXT,
                "Relax. Play. Win with the Joker in Solitaire Joker: Klondike Zen!\nhttps://play.google.com/store/apps/details?id=${context.packageName}"
            )
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share Solitaire Joker"))
    } catch (_: Exception) {}
}
