package com.solitaire.hyper.card.games.game.model

/**
 * Intelligent hint suggestion provided by the Solitaire engine.
 */
data class Hint(
    val from: CardLocation,
    val to: CardLocation,
    val card: Card,
    val description: String
)
