package com.solitaire.hyper.card.games.game.model

/**
 * Klondike Solitaire deal modes.
 */
enum class GameMode(val displayName: String, val cardsPerDraw: Int) {
    DRAW_1("Draw 1", 1),
    DRAW_3("Draw 3", 3);

    companion object {
        fun fromString(name: String?): GameMode {
            return entries.firstOrNull { it.name.equals(name, ignoreCase = true) } ?: DRAW_1
        }
    }
}
