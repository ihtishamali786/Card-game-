package com.solitaire.hyper.card.games.game.model

/**
 * Immutable snapshot of the entire Solitaire game board state.
 */
data class GameState(
    val stock: List<Card> = emptyList(),
    val waste: List<Card> = emptyList(),
    val foundations: List<List<Card>> = List(4) { emptyList() },
    val tableau: List<List<Card>> = List(7) { emptyList() },
    val moveCount: Int = 0,
    val score: Int = 0,
    val elapsedTimeSeconds: Long = 0L,
    val isGameWon: Boolean = false,
    val isAutoCompleteAvailable: Boolean = false,
    val gameMode: GameMode = GameMode.DRAW_1,
    val isDailyChallenge: Boolean = false,
    val challengeDate: String? = null,
    val seed: Long = System.currentTimeMillis()
) {
    /**
     * Total cards placed in the four foundations. 52 means won.
     */
    val totalFoundationCards: Int
        get() = foundations.sumOf { it.size }

    /**
     * Checks if all 52 cards are on foundations.
     */
    val hasWon: Boolean
        get() = totalFoundationCards == 52

    /**
     * Checks if all remaining tableau cards are face-up and stock/waste are clear.
     */
    val canAutoComplete: Boolean
        get() {
            if (hasWon) return false
            val tableauHasFaceDown = tableau.any { col -> col.any { !it.isFaceUp } }
            return !tableauHasFaceDown && stock.isEmpty() && waste.isEmpty()
        }
}
