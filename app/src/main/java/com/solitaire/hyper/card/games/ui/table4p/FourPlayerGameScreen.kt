package com.solitaire.hyper.card.games.ui.table4p

import androidx.compose.runtime.Composable
import com.solitaire.hyper.card.games.ui.callbreak.CallBreakScreen

/**
 * Backward compatibility wrapper for CallBreakScreen.
 */
@Composable
fun FourPlayerGameScreen(
    viewModel: FourPlayerGameViewModel,
    onNavigateBack: () -> Unit
) {
    CallBreakScreen(
        viewModel = viewModel,
        onNavigateBack = onNavigateBack
    )
}
