package com.solitaire.hyper.card.games.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

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
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
