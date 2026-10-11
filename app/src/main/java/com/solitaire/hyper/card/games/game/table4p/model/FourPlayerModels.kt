package com.solitaire.hyper.card.games.game.table4p.model

import com.solitaire.hyper.card.games.game.model.Card
import com.solitaire.hyper.card.games.game.model.Suit

enum class PlayerPosition(val displayName: String, val defaultAvatar: String) {
    SOUTH("You", "👤"),
    WEST("Player 2", "🌟"),
    NORTH("Player 3", "👑"),
    EAST("Player 4", "⚡");

    fun nextClockwise(): PlayerPosition = when (this) {
        SOUTH -> WEST
        WEST -> NORTH
        NORTH -> EAST
        EAST -> SOUTH
    }
}

data class CelebrityProfile(
    val name: String,
    val avatar: String,
    val country: String,
    val flag: String
)

val CELEBRITY_PROFILES = listOf(
    // India
    CelebrityProfile("Shah Rukh Khan", "🌟", "India", "🇮🇳"),
    CelebrityProfile("Virat Kohli", "👑", "India", "🇮🇳"),
    CelebrityProfile("Salman Khan", "🦁", "India", "🇮🇳"),
    CelebrityProfile("Rohit Sharma", "🏏", "India", "🇮🇳"),
    CelebrityProfile("Ranbir Kapoor", "🎬", "India", "🇮🇳"),
    CelebrityProfile("Diljit Dosanjh", "🎵", "India", "🇮🇳"),
    CelebrityProfile("Amitabh Bachchan", "🎩", "India", "🇮🇳"),
    CelebrityProfile("Hrithik Roshan", "⚡", "India", "🇮🇳"),

    // Pakistan
    CelebrityProfile("Babar Azam", "🏏", "Pakistan", "🇵🇰"),
    CelebrityProfile("Atif Aslam", "🎵", "Pakistan", "🇵🇰"),
    CelebrityProfile("Fawad Khan", "🌟", "Pakistan", "🇵🇰"),
    CelebrityProfile("Shaheen Afridi", "🦅", "Pakistan", "🇵🇰"),
    CelebrityProfile("Naseem Shah", "⚡", "Pakistan", "🇵🇰"),
    CelebrityProfile("Ali Zafar", "🎤", "Pakistan", "🇵🇰"),
    CelebrityProfile("Wasim Akram", "👑", "Pakistan", "🇵🇰"),
    CelebrityProfile("Mahira Khan", "✨", "Pakistan", "🇵🇰"),

    // USA / America
    CelebrityProfile("Tom Cruise", "🎬", "USA", "🇺🇸"),
    CelebrityProfile("Leonardo DiCaprio", "🎭", "USA", "🇺🇸"),
    CelebrityProfile("Brad Pitt", "⭐", "USA", "🇺🇸"),
    CelebrityProfile("Will Smith", "🔥", "USA", "🇺🇸"),
    CelebrityProfile("Robert Downey Jr", "🤖", "USA", "🇺🇸"),
    CelebrityProfile("Chris Evans", "🛡️", "USA", "🇺🇸"),
    CelebrityProfile("Dwayne Johnson", "💪", "USA", "🇺🇸"),
    CelebrityProfile("Johnny Depp", "🏴‍☠️", "USA", "🇺🇸"),

    // Canada
    CelebrityProfile("Keanu Reeves", "🕶️", "Canada", "🇨🇦"),
    CelebrityProfile("Ryan Reynolds", "🎯", "Canada", "🇨🇦"),
    CelebrityProfile("Drake", "🎧", "Canada", "🇨🇦"),
    CelebrityProfile("Justin Bieber", "🎶", "Canada", "🇨🇦"),
    CelebrityProfile("Jim Carrey", "😄", "Canada", "🇨🇦"),

    // UK & Ireland
    CelebrityProfile("Henry Cavill", "⚔️", "UK", "🇬🇧"),
    CelebrityProfile("David Beckham", "⚽", "UK", "🇬🇧"),
    CelebrityProfile("Zayn Malik", "🎤", "UK", "🇬🇧"),
    CelebrityProfile("Benedict Cumberbatch", "🕵️", "UK", "🇬🇧"),
    CelebrityProfile("Tom Holland", "🕷️", "UK", "🇬🇧"),
    CelebrityProfile("Cillian Murphy", "🎩", "Ireland", "🇮🇪"),

    // Worldwide Sports Icons
    CelebrityProfile("Cristiano Ronaldo", "⚽", "Portugal", "🇵🇹"),
    CelebrityProfile("Lionel Messi", "🏆", "Argentina", "🇦🇷"),
    CelebrityProfile("Roger Federer", "🎾", "Switzerland", "🇨🇭"),
    CelebrityProfile("Lewis Hamilton", "🏎️", "UK", "🇬🇧")
)

// Legacy alias for compatibility
val CELEBRITY_NAMES: List<Pair<String, String>> = CELEBRITY_PROFILES.map { it.name to it.avatar }

enum class GamePhase {
    DEALING,
    TRUMP_SELECTION,
    BIDDING,
    PLAYING,
    TRICK_RESOLVING,
    ROUND_OVER
}

data class PlayerState(
    val position: PlayerPosition,
    val name: String,
    val avatar: String,
    val country: String = "",
    val flag: String = "",
    val hand: List<Card> = emptyList(),
    val bid: Int = 1,
    val tricksWon: Int = 0,
    val totalScore: Double = 0.0,
    val roundScoreDelta: Double = 0.0,
    val hasBid: Boolean = false
)

data class PlayedCard(
    val player: PlayerPosition,
    val card: Card
)

data class FourPlayerGameState(
    val roundNumber: Int = 1,
    val maxRounds: Int = 5,
    val trickNumber: Int = 1,
    val players: Map<PlayerPosition, PlayerState> = emptyMap(),
    val currentTurn: PlayerPosition = PlayerPosition.SOUTH,
    val phase: GamePhase = GamePhase.DEALING,
    val currentTrick: List<PlayedCard> = emptyList(),
    val leadSuit: Suit? = null,
    val trumpSuit: Suit = Suit.SPADES,
    val lastTrickWinner: PlayerPosition? = null,
    val message: String = "Dealing Call Break cards from deck...",
    val isFastSpeed: Boolean = false,
    val dealtCardsProgress: Int = 0, // 0 to 52 for dealing animation
    val dealingTo: PlayerPosition? = null, // currently dealing card target
    val isTableRotated: Boolean = true, // visual rotation state
    val totalTableBids: Int = 0,
    val canRedeal: Boolean = false
)
