package com.solitaire.hyper.card.games.game.model

/**
 * The four standard playing card suits.
 */
enum class Suit(val symbol: String, val color: CardColor, val displayName: String) {
    HEARTS("♥", CardColor.RED, "Hearts"),
    DIAMONDS("♦", CardColor.RED, "Diamonds"),
    CLUBS("♣", CardColor.BLACK, "Clubs"),
    SPADES("♠", CardColor.BLACK, "Spades")
}

/**
 * The color of a playing card suit.
 */
enum class CardColor {
    RED,
    BLACK
}

/**
 * Standard playing card ranks from Ace (1) to King (13).
 */
enum class Rank(val value: Int, val display: String) {
    ACE(1, "A"),
    TWO(2, "2"),
    THREE(3, "3"),
    FOUR(4, "4"),
    FIVE(5, "5"),
    SIX(6, "6"),
    SEVEN(7, "7"),
    EIGHT(8, "8"),
    NINE(9, "9"),
    TEN(10, "10"),
    JACK(11, "J"),
    QUEEN(12, "Q"),
    KING(13, "K");

    companion object {
        fun fromValue(value: Int): Rank = entries.firstOrNull { it.value == value } ?: ACE
    }
}

/**
 * Immutable representation of a single card.
 */
data class Card(
    val id: Int,
    val suit: Suit,
    val rank: Rank,
    val isFaceUp: Boolean = false
) {
    val color: CardColor get() = suit.color

    fun toDisplayString(): String = if (isFaceUp) "${rank.display}${suit.symbol}" else "[?]"
}

/**
 * Explicit representation of where a card or card stack is located on the board.
 */
sealed class CardLocation {
    data object Stock : CardLocation()
    data object Waste : CardLocation()
    data class Foundation(val index: Int) : CardLocation() // 0..3
    data class Tableau(val columnIndex: Int, val cardIndex: Int = -1) : CardLocation() // col 0..6
}
