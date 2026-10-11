package com.solitaire.hyper.card.games.game.table4p.engine

import com.solitaire.hyper.card.games.game.model.Card
import com.solitaire.hyper.card.games.game.model.Rank
import com.solitaire.hyper.card.games.game.model.Suit
import com.solitaire.hyper.card.games.game.table4p.model.CELEBRITY_PROFILES
import com.solitaire.hyper.card.games.game.table4p.model.CelebrityProfile
import com.solitaire.hyper.card.games.game.table4p.model.FourPlayerGameState
import com.solitaire.hyper.card.games.game.table4p.model.GamePhase
import com.solitaire.hyper.card.games.game.table4p.model.PlayedCard
import com.solitaire.hyper.card.games.game.table4p.model.PlayerPosition
import com.solitaire.hyper.card.games.game.table4p.model.PlayerState
import java.util.Random

object FourPlayerEngine {

    /**
     * In Call Break / Spades trick-taking games, Ace is strictly the highest card
     * (value 14), followed by King (13), Queen (12), Jack (11), down to Two (2).
     */
    fun rankPower(rank: Rank): Int = if (rank == Rank.ACE) 14 else rank.value

    /**
     * Creates a standard 52-card deck strictly without duplicates.
     */
    fun createDeck(): List<Card> {
        val deck = ArrayList<Card>(52)
        var id = 1
        for (suit in Suit.entries) {
            for (rank in Rank.entries) {
                deck.add(Card(id = id++, suit = suit, rank = rank, isFaceUp = true))
            }
        }
        return deck
    }

    /**
     * Shuffles a standard 52-card deck with the given random seed.
     */
    fun shuffleDeck(seed: Long = System.currentTimeMillis()): List<Card> {
        val random = Random(seed)
        return createDeck().shuffled(random)
    }

    /**
     * Distributes 52 cards into 4 hands of 13 cards each for South, West, North, East.
     */
    fun dealHands(deck: List<Card>): Map<PlayerPosition, List<Card>> {
        require(deck.size == 52) { "Deck must have exactly 52 cards, had: ${deck.size}" }
        val hands = mutableMapOf<PlayerPosition, List<Card>>()
        val positions = PlayerPosition.entries.toTypedArray()
        for (i in 0 until 4) {
            val playerHand = deck.subList(i * 13, (i + 1) * 13)
            hands[positions[i]] = playerHand
        }
        return hands
    }

    /**
     * In Call Break, a redeal / misdeal is declared if any player is dealt 0 cards of the trump suit.
     */
    fun checkRedealCondition(hands: Map<PlayerPosition, List<Card>>, trumpSuit: Suit = Suit.SPADES): Boolean {
        return hands.values.any { hand -> hand.none { it.suit == trumpSuit } }
    }

    /**
     * Deals a fresh round with 13 cards per player, sorted by trump suit first and rank descending.
     * Randomly assigns worldwide famous celebrities (India, Pakistan, USA, Canada, UK, etc.)
     * as opponents with their names, avatars, and countries.
     */
    fun dealNewRound(
        roundNumber: Int = 1,
        existingScores: Map<PlayerPosition, Double> = emptyMap(),
        isFastSpeed: Boolean = false,
        seed: Long = System.currentTimeMillis(),
        trumpSuit: Suit = Suit.SPADES
    ): FourPlayerGameState {
        val random = Random(seed)
        val deck = shuffleDeck(seed)
        val rawHands = dealHands(deck)

        val positions = PlayerPosition.entries.toTypedArray()
        val sortedHands = positions.associateWith { pos ->
            sortHand(rawHands[pos] ?: emptyList(), trumpSuit)
        }

        // Pick 3 unique celebrities randomly from worldwide famous roster
        val celebritiesShuffled = CELEBRITY_PROFILES.shuffled(random)
        val opponentCelebrities: Map<PlayerPosition, CelebrityProfile> = mapOf(
            PlayerPosition.WEST to celebritiesShuffled[0],
            PlayerPosition.NORTH to celebritiesShuffled[1],
            PlayerPosition.EAST to celebritiesShuffled[2]
        )

        val canRedeal = checkRedealCondition(sortedHands, trumpSuit)

        val players = positions.associateWith { pos ->
            val hand = sortedHands[pos] ?: emptyList()
            val autoBid = calculateEstimatedBid(hand, trumpSuit)
            val celeb = opponentCelebrities[pos]
            val (pName, pAvatar, pCountry, pFlag) = if (pos == PlayerPosition.SOUTH) {
                Quadruple("You", "👤", "My Country", "🌍")
            } else {
                Quadruple(
                    celeb?.name ?: "Player",
                    celeb?.avatar ?: "🌟",
                    celeb?.country ?: "Global",
                    celeb?.flag ?: "⭐"
                )
            }

            PlayerState(
                position = pos,
                name = pName,
                avatar = pAvatar,
                country = pCountry,
                flag = pFlag,
                hand = hand,
                bid = autoBid,
                tricksWon = 0,
                totalScore = existingScores[pos] ?: 0.0,
                roundScoreDelta = 0.0,
                hasBid = pos != PlayerPosition.SOUTH // South bids interactively
            )
        }

        val totalBids = players.values.sumOf { it.bid }

        return FourPlayerGameState(
            roundNumber = roundNumber,
            maxRounds = 5,
            trickNumber = 1,
            players = players,
            currentTurn = PlayerPosition.SOUTH,
            phase = GamePhase.DEALING,
            currentTrick = emptyList(),
            leadSuit = null,
            trumpSuit = trumpSuit,
            lastTrickWinner = null,
            message = "Dealing cards to 4 players from the central deck...",
            isFastSpeed = isFastSpeed,
            dealtCardsProgress = 0,
            dealingTo = PlayerPosition.SOUTH,
            totalTableBids = totalBids,
            canRedeal = canRedeal
        )
    }

    private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

    /**
     * Sorts cards by trump suit first, then other suits (Spades, Hearts, Clubs, Diamonds),
     * and descending rank power (Ace = 14 high down to 2).
     */
    fun sortHand(hand: List<Card>, trumpSuit: Suit = Suit.SPADES): List<Card> {
        return hand.sortedWith(
            compareBy<Card> { card ->
                if (card.suit == trumpSuit) 0
                else when (card.suit) {
                    Suit.SPADES -> 1
                    Suit.HEARTS -> 2
                    Suit.CLUBS -> 3
                    Suit.DIAMONDS -> 4
                }
            }.thenByDescending { rankPower(it.rank) }
        )
    }

    /**
     * Estimates bid (1 to 8) based on high cards (Aces, Kings) and trump cards.
     */
    fun calculateEstimatedBid(hand: List<Card>, trumpSuit: Suit): Int {
        var count = 0
        val trumpCards = hand.filter { it.suit == trumpSuit }
        val nonTrumpCards = hand.filter { it.suit != trumpSuit }

        // 1. High trumps (Ace, King, Queen, Jack)
        for (card in trumpCards) {
            val power = rankPower(card.rank)
            if (power >= 12) { // Ace or King or Queen
                count++
            } else if (power >= 10 && trumpCards.size >= 4) {
                count++
            }
        }
        // Extra tricks if long trump suit (5 or more trumps)
        if (trumpCards.size >= 5) count++

        // 2. Non-trump high cards
        val suits = listOf(Suit.SPADES, Suit.HEARTS, Suit.CLUBS, Suit.DIAMONDS).filter { it != trumpSuit }
        for (suit in suits) {
            val suitCards = nonTrumpCards.filter { it.suit == suit }
            if (suitCards.any { it.rank == Rank.ACE }) count++
            if (suitCards.size >= 2 && suitCards.any { it.rank == Rank.KING }) count++
        }

        return count.coerceIn(1, 8)
    }

    /**
     * Checks if [card] is legally playable given [hand] and the [leadSuit].
     * Rule: Player MUST follow lead suit if they hold any card of that suit.
     * If void in lead suit, player may play any card (trump or discard).
     */
    fun isLegalPlay(card: Card, hand: List<Card>, leadSuit: Suit?): Boolean {
        if (leadSuit == null) return true // Leading player can play any card
        val hasLeadSuit = hand.any { it.suit == leadSuit }
        return if (hasLeadSuit) {
            card.suit == leadSuit
        } else {
            true // Player is void in lead suit; may play any card (including trump)
        }
    }

    /**
     * Determines which player won the 4-card trick according to Call Break rules:
     * - Highest card of the trump suit wins.
     * - If no trump was played, highest card of the lead suit wins.
     */
    fun evaluateTrickWinner(trick: List<PlayedCard>, trumpSuit: Suit): PlayerPosition {
        if (trick.isEmpty()) return PlayerPosition.SOUTH
        val leadSuit = trick.first().card.suit

        val trumpCards = trick.filter { it.card.suit == trumpSuit }
        return if (trumpCards.isNotEmpty()) {
            trumpCards.maxByOrNull { rankPower(it.card.rank) }!!.player
        } else {
            val leadSuitCards = trick.filter { it.card.suit == leadSuit }
            leadSuitCards.maxByOrNull { rankPower(it.card.rank) }!!.player
        }
    }

    /**
     * Smart, quick AI decision for celebrity opponents (West, North, East).
     */
    fun chooseAiCard(
        hand: List<Card>,
        currentTrick: List<PlayedCard>,
        trumpSuit: Suit
    ): Card {
        val leadSuit = currentTrick.firstOrNull()?.card?.suit

        // 1. If leading:
        if (leadSuit == null) {
            // Play an Ace of any non-trump suit if held, else high card or lowest card
            val nonTrumpAces = hand.filter { it.suit != trumpSuit && it.rank == Rank.ACE }
            if (nonTrumpAces.isNotEmpty()) return nonTrumpAces.random()
            val kings = hand.filter { it.rank == Rank.KING }
            if (kings.isNotEmpty()) return kings.random()
            return hand.minByOrNull { rankPower(it.rank) } ?: hand.first()
        }

        // 2. If must follow lead suit:
        val suitCards = hand.filter { it.suit == leadSuit }
        if (suitCards.isNotEmpty()) {
            val currentHighestLeadPower = currentTrick
                .filter { it.card.suit == leadSuit }
                .maxOfOrNull { rankPower(it.card.rank) } ?: 0
            val winningCards = suitCards.filter { rankPower(it.rank) > currentHighestLeadPower }
            return if (winningCards.isNotEmpty()) {
                // Play lowest winning card of lead suit to conserve higher cards
                winningCards.minByOrNull { rankPower(it.rank) } ?: suitCards.last()
            } else {
                // Cannot win lead suit; play lowest card of lead suit
                suitCards.minByOrNull { rankPower(it.rank) } ?: suitCards.first()
            }
        }

        // 3. Void in lead suit:
        val trumps = hand.filter { it.suit == trumpSuit }
        val trickHasTrump = currentTrick.any { it.card.suit == trumpSuit }
        if (trumps.isNotEmpty() && !trickHasTrump) {
            // Cut with lowest trump
            return trumps.minByOrNull { rankPower(it.rank) } ?: trumps.first()
        } else if (trumps.isNotEmpty() && trickHasTrump) {
            val highestTrumpPower = currentTrick
                .filter { it.card.suit == trumpSuit }
                .maxOfOrNull { rankPower(it.card.rank) } ?: 0
            val winningTrumps = trumps.filter { rankPower(it.rank) > highestTrumpPower }
            if (winningTrumps.isNotEmpty()) {
                return winningTrumps.minByOrNull { rankPower(it.rank) } ?: trumps.first()
            }
        }

        // Slough off lowest off-suit card
        return hand.minByOrNull { rankPower(it.rank) } ?: hand.first()
    }

    /**
     * Calculates round scores when all 13 tricks have been played.
     */
    fun calculateRoundScores(players: Map<PlayerPosition, PlayerState>): Map<PlayerPosition, PlayerState> {
        return players.mapValues { (_, state) ->
            val bid = state.bid
            val won = state.tricksWon
            val roundDelta = if (won >= bid) {
                bid.toDouble() + (won - bid) * 0.1
            } else {
                -bid.toDouble()
            }
            state.copy(
                roundScoreDelta = roundDelta,
                totalScore = state.totalScore + roundDelta
            )
        }
    }
}
