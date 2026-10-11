package com.solitaire.hyper.card.games.ai

import com.solitaire.hyper.card.games.game.engine.SolitaireEngine
import com.solitaire.hyper.card.games.game.model.CardLocation
import com.solitaire.hyper.card.games.game.model.GameState
import com.solitaire.hyper.card.games.game.model.Hint
import com.solitaire.hyper.card.games.game.model.Rank
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class AiAnalysisResult(
    val solvabilityRating: Int, // 0 - 100%
    val recommendationTitle: String,
    val recommendationDetail: String,
    val bestMoveDescription: String?,
    val hiddenCardsCount: Int,
    val cardsInFoundationCount: Int,
    val strategyTip: String,
    val isGeminiPowered: Boolean
)

/**
 * 100% On-Device Grandmaster Heuristic AI Coach.
 * Zero network traffic, zero external SDKs, zero data collection.
 */
object SolitaireAiEngine {

    /**
     * Performs a comprehensive AI analysis of the given Solitaire game state
     * entirely on-device using heuristic grandmaster evaluation algorithms.
     */
    suspend fun analyzeGame(gameState: GameState): AiAnalysisResult = withContext(Dispatchers.Default) {
        val totalFoundationCards = gameState.foundations.sumOf { it.size }
        val totalHiddenCards = gameState.tableau.sumOf { col -> col.count { !it.isFaceUp } }
        val emptyCols = gameState.tableau.count { it.isEmpty() }
        val hint = SolitaireEngine.findHint(gameState)

        // Calculate heuristic solvability rating (40% - 98%)
        val foundationFactor = (totalFoundationCards * 1.5f).toInt()
        val revealedFactor = ((21 - totalHiddenCards) * 1.8f).toInt()
        val emptyColFactor = emptyCols * 4
        val rawRating = (35 + foundationFactor + revealedFactor + emptyColFactor).coerceIn(40, 98)

        // Basic move description
        val bestMoveDesc = hint?.let { h ->
            val fromName = when (val from = h.from) {
                is CardLocation.Waste -> "Waste"
                is CardLocation.Tableau -> "Column ${from.columnIndex + 1}"
                is CardLocation.Foundation -> "Foundation ${from.index + 1}"
                else -> "Board"
            }
            val toName = when (val to = h.to) {
                is CardLocation.Foundation -> "Foundation ${to.index + 1}"
                is CardLocation.Tableau -> "Column ${to.columnIndex + 1}"
                else -> "Board"
            }
            "${h.description} ($fromName → $toName)"
        }

        val strategyTip = when {
            totalHiddenCards > 12 -> "Focus on digging into columns with the deepest hidden cards first."
            emptyCols > 0 && gameState.tableau.any { col -> col.firstOrNull { it.isFaceUp }?.rank != Rank.KING } ->
                "Reserve empty columns exclusively for Kings to unlock new tableau branches."
            totalFoundationCards < 8 -> "Prioritize finding and advancing Aces & Twos to open up space."
            totalHiddenCards == 0 -> "All hidden cards revealed! Tap Auto-Win or move cards up to Foundation."
            else -> "Alternate card colors evenly and avoid premature moves to foundation if you need that card to hold another."
        }

        val title = when {
            totalHiddenCards == 0 -> "Winning State Imminent"
            rawRating >= 80 -> "High Solvability Position"
            rawRating >= 60 -> "Strong Tactical Deal"
            else -> "Challenging Layout"
        }

        val detail = if (bestMoveDesc != null) {
            "Optimal tactic: $bestMoveDesc. This move frees up tableau flexibility and exposes hidden opportunities."
        } else {
            "Current position requires careful stock cycling. Look for cross-column transfers to unblock buried ranks."
        }

        AiAnalysisResult(
            solvabilityRating = rawRating,
            recommendationTitle = title,
            recommendationDetail = detail,
            bestMoveDescription = bestMoveDesc,
            hiddenCardsCount = totalHiddenCards,
            cardsInFoundationCount = totalFoundationCards,
            strategyTip = strategyTip,
            isGeminiPowered = false
        )
    }
}
