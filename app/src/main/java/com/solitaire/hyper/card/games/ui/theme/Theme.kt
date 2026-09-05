package com.solitaire.hyper.card.games.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = SleekEmerald400,
    onPrimary = SleekBgDark,
    primaryContainer = SleekHeaderDark,
    onPrimaryContainer = SleekEmerald400,
    secondary = SleekEmerald500,
    onSecondary = SleekSlate100,
    background = SleekBgDark,
    onBackground = SleekSlate100,
    surface = SleekHeaderDark,
    onSurface = SleekSlate100,
    surfaceVariant = SleekGradientTop,
    onSurfaceVariant = SleekSlate300
)

@Composable
fun SolitaireHyperTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = SleekHeaderDark.toArgb()
                window.navigationBarColor = SleekFooterBg.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
