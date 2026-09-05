package com.solitaire.hyper.card.games.game.model

/**
 * Encapsulates the complete delta required to perform an exact undo.
 */
sealed class GameMove {
    data class CardMove(
        val from: CardLocation,
        val to: CardLocation,
        val cards: List<Card>,
        val previousSourceTopCardWasFlipped: Boolean = false,
        val scoreDelta: Int = 0
    ) : GameMove()

    data class StockDraw(
        val drawnCards: List<Card>,
        val scoreDelta: Int = 0
    ) : GameMove()

    data class StockRecycle(
        val recycledCards: List<Card>, // cards that were in waste and put back to stock
        val scoreDelta: Int = 0
    ) : GameMove()
}
