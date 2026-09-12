package com.solitaire.hyper.card.games

import android.app.Activity
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.solitaire.hyper.card.games.data.GameRepository
import com.solitaire.hyper.card.games.data.preferences.UserPreferencesRepository
import com.solitaire.hyper.card.games.game.model.GameMode
import com.solitaire.hyper.card.games.ui.achievements.AchievementsScreen
import com.solitaire.hyper.card.games.ui.components.UpdateNoticeDialog
import com.solitaire.hyper.card.games.ui.customization.CustomizationScreen
import com.solitaire.hyper.card.games.ui.daily.DailyChallengeScreen
import com.solitaire.hyper.card.games.ui.game.GameScreen
import com.solitaire.hyper.card.games.ui.game.GameViewModel
import com.solitaire.hyper.card.games.ui.home.HomeScreen
import com.solitaire.hyper.card.games.ui.settings.SettingsScreen
import com.solitaire.hyper.card.games.ui.stats.StatisticsScreen
import com.solitaire.hyper.card.games.ui.theme.SolitaireHyperTheme
import com.solitaire.hyper.card.games.ads.AdManager
import com.solitaire.hyper.card.games.update.AppUpdateHelper
import com.solitaire.hyper.card.games.update.UpdateStatus

import androidx.activity.compose.BackHandler
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.solitaire.hyper.card.games.ui.theme.SleekEmerald500
import com.solitaire.hyper.card.games.ui.theme.SleekSlate100
import com.solitaire.hyper.card.games.ui.theme.SleekSlate300

enum class Screen {
    HOME,
    GAME,
    DAILY_CHALLENGES,
    STATISTICS,
    CUSTOMIZATION,
    ACHIEVEMENTS,
    SETTINGS
}

class MainActivity : ComponentActivity() {

    private val gameViewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val userPrefs = UserPreferencesRepository(applicationContext)
        val gameRepository = GameRepository(applicationContext)

        setContent {
            SolitaireHyperTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    SolitaireAppNavigation(
                        viewModel = gameViewModel,
                        userPrefs = userPrefs,
                        repository = gameRepository
                    )
                }
            }
        }
    }
}

@Composable
fun SolitaireAppNavigation(
    viewModel: GameViewModel,
    userPrefs: UserPreferencesRepository,
    repository: GameRepository
) {
    val context = LocalContext.current
    val activity = context as? Activity
    var currentScreen by remember { mutableStateOf(Screen.HOME) }
    var previousScreen by remember { mutableStateOf<Screen?>(null) }
    var showAppExitDialog by remember { mutableStateOf(false) }

    val userSettings by viewModel.userSettings.collectAsState()
    val hasSavedGame = !userSettings.savedGameJson.isNullOrBlank()
    val updateStatus by AppUpdateHelper.updateStatus.collectAsState()

    // Request notification permission on Android 13+ (Tiramisu)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val permissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { _ -> }

        LaunchedEffect(Unit) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    LaunchedEffect(Unit) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        activity?.let {
            AdManager.preloadAppOpen(it)
            AdManager.showAppOpenAdIfReady(it, userSettings.isAdFreeActive())
        }
        // Check for Google Play Store updates on app startup
        AppUpdateHelper.checkForAppUpdate(context)
    }

    // In-App Update Notice Dialog if new version is available
    val availableUpdate = updateStatus as? UpdateStatus.Available
    if (availableUpdate != null) {
        UpdateNoticeDialog(
            newVersionName = availableUpdate.availableVersionName,
            updateNotes = availableUpdate.updateNotes,
            onUpdateNow = {
                activity?.let { AppUpdateHelper.startUpdateFlow(it) }
            },
            onDismiss = {
                AppUpdateHelper.dismissUpdatePrompt()
            }
        )
    }

    // When on HOME screen and user presses back, ask for confirmation
    if (currentScreen == Screen.HOME) {
        BackHandler {
            showAppExitDialog = true
        }
    }

    if (showAppExitDialog) {
        AlertDialog(
            onDismissRequest = { showAppExitDialog = false },
            title = {
                Text("Exit Solitaire?", fontWeight = FontWeight.Bold, color = SleekSlate100)
            },
            text = {
                Text("Are you sure you want to close the game?", color = SleekSlate300, fontSize = 14.sp)
            },
            confirmButton = {
                Button(
                    onClick = {
                        showAppExitDialog = false
                        if (activity != null) {
                            AdManager.showExitAd(activity, userSettings.isAdFreeActive()) {
                                activity.finish()
                            }
                        } else {
                            activity?.finish()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SleekEmerald500)
                ) {
                    Text("Exit", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAppExitDialog = false }) {
                    Text("Cancel", color = SleekSlate300)
                }
            }
        )
    }

    fun navigateTo(screen: Screen) {
        previousScreen = currentScreen
        currentScreen = screen
    }

    fun navigateBackFromSubScreen() {
        currentScreen = previousScreen ?: Screen.HOME
    }

    Crossfade(targetState = currentScreen, label = "ScreenTransition") { screen ->
        when (screen) {
            Screen.HOME -> HomeScreen(
                hasSavedGame = hasSavedGame,
                userSettings = userSettings,
                userPrefs = userPrefs,
                onContinueGame = {
                    viewModel.restoreSavedGameOrNew()
                    navigateTo(Screen.GAME)
                },
                onPlayDraw1 = {
                    viewModel.startNewGame(mode = GameMode.DRAW_1)
                    navigateTo(Screen.GAME)
                },
                onPlayDraw3 = {
                    viewModel.startNewGame(mode = GameMode.DRAW_3)
                    navigateTo(Screen.GAME)
                },
                onPlayWinnable = {
                    viewModel.startNewGame(mode = GameMode.DRAW_1, isWinnableDeal = true)
                    navigateTo(Screen.GAME)
                },
                onDailyChallenge = {
                    navigateTo(Screen.DAILY_CHALLENGES)
                },
                onStatistics = {
                    navigateTo(Screen.STATISTICS)
                },
                onCustomization = {
                    navigateTo(Screen.CUSTOMIZATION)
                },
                onAchievements = {
                    navigateTo(Screen.ACHIEVEMENTS)
                },
                onSettings = {
                    navigateTo(Screen.SETTINGS)
                }
            )
            Screen.GAME -> GameScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    currentScreen = Screen.HOME
                },
                onOpenSettings = {
                    navigateTo(Screen.SETTINGS)
                },
                onOpenDaily = {
                    navigateTo(Screen.DAILY_CHALLENGES)
                },
                onOpenThemes = {
                    navigateTo(Screen.CUSTOMIZATION)
                },
                onOpenStats = {
                    navigateTo(Screen.STATISTICS)
                },
                onOpenAchievements = {
                    navigateTo(Screen.ACHIEVEMENTS)
                }
            )
            Screen.DAILY_CHALLENGES -> DailyChallengeScreen(
                completedChallengesFlow = repository.getCompletedDailyChallenges(),
                onPlayDateChallenge = { dateStr ->
                    viewModel.startNewGame(
                        mode = GameMode.DRAW_1,
                        isDailyChallenge = true,
                        challengeDate = dateStr
                    )
                    navigateTo(Screen.GAME)
                },
                onBack = { navigateBackFromSubScreen() }
            )
            Screen.STATISTICS -> StatisticsScreen(
                repository = repository,
                onBack = { navigateBackFromSubScreen() }
            )
            Screen.CUSTOMIZATION -> CustomizationScreen(
                userPrefs = userPrefs,
                onBack = { navigateBackFromSubScreen() }
            )
            Screen.ACHIEVEMENTS -> AchievementsScreen(
                achievementsFlow = repository.getAchievements(),
                onBack = { navigateBackFromSubScreen() }
            )
            Screen.SETTINGS -> SettingsScreen(
                userPrefs = userPrefs,
                onBack = { navigateBackFromSubScreen() }
            )
        }
    }
}
