package com.solitaire.hyper.card.games

import android.app.Activity
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import com.solitaire.hyper.card.games.data.GameRepository
import com.solitaire.hyper.card.games.data.preferences.UserPreferencesRepository
import com.solitaire.hyper.card.games.game.model.GameMode
import com.solitaire.hyper.card.games.ui.achievements.AchievementsScreen
import com.solitaire.hyper.card.games.ui.customization.CustomizationScreen
import com.solitaire.hyper.card.games.ui.daily.DailyChallengeScreen
import com.solitaire.hyper.card.games.ui.game.GameScreen
import com.solitaire.hyper.card.games.ui.game.GameViewModel
import com.solitaire.hyper.card.games.ui.home.HomeScreen
import com.solitaire.hyper.card.games.ui.settings.SettingsScreen
import com.solitaire.hyper.card.games.ui.stats.StatisticsScreen
import com.solitaire.hyper.card.games.ui.theme.SolitaireHyperTheme

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
    val userSettings by viewModel.userSettings.collectAsState()
    val hasSavedGame = !userSettings.savedGameJson.isNullOrBlank()

    LaunchedEffect(Unit) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    Crossfade(targetState = currentScreen, label = "ScreenTransition") { screen ->
        when (screen) {
            Screen.HOME -> HomeScreen(
                hasSavedGame = hasSavedGame,
                onContinueGame = {
                    viewModel.restoreSavedGameOrNew()
                    currentScreen = Screen.GAME
                },
                onPlayDraw1 = {
                    viewModel.startNewGame(mode = GameMode.DRAW_1)
                    currentScreen = Screen.GAME
                },
                onPlayDraw3 = {
                    viewModel.startNewGame(mode = GameMode.DRAW_3)
                    currentScreen = Screen.GAME
                },
                onDailyChallenge = {
                    currentScreen = Screen.DAILY_CHALLENGES
                },
                onStatistics = {
                    currentScreen = Screen.STATISTICS
                },
                onCustomization = {
                    currentScreen = Screen.CUSTOMIZATION
                },
                onAchievements = {
                    currentScreen = Screen.ACHIEVEMENTS
                },
                onSettings = {
                    currentScreen = Screen.SETTINGS
                }
            )
            Screen.GAME -> GameScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    currentScreen = Screen.HOME
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
                    currentScreen = Screen.GAME
                },
                onBack = { currentScreen = Screen.HOME }
            )
            Screen.STATISTICS -> StatisticsScreen(
                repository = repository,
                onBack = { currentScreen = Screen.HOME }
            )
            Screen.CUSTOMIZATION -> CustomizationScreen(
                userPrefs = userPrefs,
                onBack = { currentScreen = Screen.HOME }
            )
            Screen.ACHIEVEMENTS -> AchievementsScreen(
                achievementsFlow = repository.getAchievements(),
                onBack = { currentScreen = Screen.HOME }
            )
            Screen.SETTINGS -> SettingsScreen(
                userPrefs = userPrefs,
                onBack = { currentScreen = Screen.HOME }
            )
        }
    }
}
