package com.solitaire.hyper.card.games

import com.solitaire.hyper.card.games.game.model.Card
import com.solitaire.hyper.card.games.game.model.Rank
import com.solitaire.hyper.card.games.game.model.Suit
import com.solitaire.hyper.card.games.game.table4p.engine.FourPlayerEngine
import com.solitaire.hyper.card.games.game.table4p.model.PlayedCard
import com.solitaire.hyper.card.games.game.table4p.model.PlayerPosition
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FourPlayerEngineTest {

    @Test
    fun testDeckCreation_hasExactly52UniqueCards() {
        val deck = FourPlayerEngine.createDeck()
        assertEquals(52, deck.size)
        val distinctIds = deck.map { it.id }.distinct()
        assertEquals(52, distinctIds.size)

        // 13 cards of each suit
        for (suit in Suit.entries) {
            assertEquals(13, deck.count { it.suit == suit })
        }
    }

    @Test
    fun testShuffleDeck_maintains52CardsAndRandomizes() {
        val deck1 = FourPlayerEngine.shuffleDeck(seed = 12345L)
        val deck2 = FourPlayerEngine.shuffleDeck(seed = 67890L)
        assertEquals(52, deck1.size)
        assertEquals(52, deck2.size)
        // Two different seeds should produce different card sequences
        assertFalse(deck1 == deck2)
    }

    @Test
    fun testDealNewRound_distributes13CardsEachWithZeroDuplicates() {
        val state = FourPlayerEngine.dealNewRound()
        assertEquals(4, state.players.size)

        val allDealtCards = mutableListOf<Card>()
        for (pos in PlayerPosition.entries) {
            val player = state.players[pos]!!
            assertEquals(13, player.hand.size)
            allDealtCards.addAll(player.hand)

            if (pos != PlayerPosition.SOUTH) {
                // Opponents must have non-empty celebrity names and flags
                assertFalse("Opponent name must not be blank", player.name.isBlank())
                assertFalse("Opponent must not be named 'bot'", player.name.lowercase().contains("bot"))
                assertTrue("Opponent must have flag", player.flag.isNotEmpty())
            }
        }

        // Exactly 52 total dealt cards, all distinct IDs
        assertEquals(52, allDealtCards.size)
        assertEquals(52, allDealtCards.map { it.id }.distinct().size)
    }

    @Test
    fun testRankPower_aceIsHighestInTrickTakingRules() {
        assertTrue(FourPlayerEngine.rankPower(Rank.ACE) > FourPlayerEngine.rankPower(Rank.KING))
        assertTrue(FourPlayerEngine.rankPower(Rank.KING) > FourPlayerEngine.rankPower(Rank.QUEEN))
        assertTrue(FourPlayerEngine.rankPower(Rank.QUEEN) > FourPlayerEngine.rankPower(Rank.JACK))
        assertTrue(FourPlayerEngine.rankPower(Rank.JACK) > FourPlayerEngine.rankPower(Rank.TEN))
        assertTrue(FourPlayerEngine.rankPower(Rank.TWO) < FourPlayerEngine.rankPower(Rank.THREE))
        assertEquals(14, FourPlayerEngine.rankPower(Rank.ACE))
    }

    @Test
    fun testEvaluateTrickWinner_aceBeatsKingOfSameSuit() {
        val trick = listOf(
            PlayedCard(PlayerPosition.SOUTH, Card(1, Suit.HEARTS, Rank.KING)),
            PlayedCard(PlayerPosition.WEST, Card(2, Suit.HEARTS, Rank.ACE)), // Ace should beat King
            PlayedCard(PlayerPosition.NORTH, Card(3, Suit.HEARTS, Rank.JACK)),
            PlayedCard(PlayerPosition.EAST, Card(4, Suit.HEARTS, Rank.QUEEN))
        )
        val winner = FourPlayerEngine.evaluateTrickWinner(trick, Suit.SPADES)
        assertEquals(PlayerPosition.WEST, winner)
    }

    @Test
    fun testEvaluateTrickWinner_highestTrumpWins() {
        val trick = listOf(
            PlayedCard(PlayerPosition.SOUTH, Card(1, Suit.HEARTS, Rank.ACE)),
            PlayedCard(PlayerPosition.WEST, Card(2, Suit.HEARTS, Rank.KING)),
            PlayedCard(PlayerPosition.NORTH, Card(3, Suit.SPADES, Rank.TWO)), // Ruff with low trump spade
            PlayedCard(PlayerPosition.EAST, Card(4, Suit.HEARTS, Rank.TEN))
        )
        val winner = FourPlayerEngine.evaluateTrickWinner(trick, Suit.SPADES)
        assertEquals(PlayerPosition.NORTH, winner)
    }

    @Test
    fun testEvaluateTrickWinner_customTrumpSuitHearts() {
        // Test custom trump selection: HEARTS is trump!
        val trick = listOf(
            PlayedCard(PlayerPosition.SOUTH, Card(1, Suit.CLUBS, Rank.ACE)),
            PlayedCard(PlayerPosition.WEST, Card(2, Suit.CLUBS, Rank.KING)),
            PlayedCard(PlayerPosition.NORTH, Card(3, Suit.HEARTS, Rank.TWO)), // Ruff with Heart trump
            PlayedCard(PlayerPosition.EAST, Card(4, Suit.CLUBS, Rank.QUEEN))
        )
        val winner = FourPlayerEngine.evaluateTrickWinner(trick, Suit.HEARTS)
        assertEquals(PlayerPosition.NORTH, winner)
    }

    @Test
    fun testSortHand_placesTrumpSuitFirstAndRanksDescending() {
        val unsortedHand = listOf(
            Card(1, Suit.CLUBS, Rank.TWO),
            Card(2, Suit.SPADES, Rank.ACE),
            Card(3, Suit.HEARTS, Rank.KING),
            Card(4, Suit.SPADES, Rank.KING)
        )
        val sortedWithSpades = FourPlayerEngine.sortHand(unsortedHand, Suit.SPADES)
        assertEquals(Suit.SPADES, sortedWithSpades[0].suit)
        assertEquals(Rank.ACE, sortedWithSpades[0].rank)
        assertEquals(Suit.SPADES, sortedWithSpades[1].suit)
        assertEquals(Rank.KING, sortedWithSpades[1].rank)

        // When HEARTS is trump, Hearts must come first
        val sortedWithHearts = FourPlayerEngine.sortHand(unsortedHand, Suit.HEARTS)
        assertEquals(Suit.HEARTS, sortedWithHearts[0].suit)
        assertEquals(Rank.KING, sortedWithHearts[0].rank)
    }

    @Test
    fun testLegalPlayRules_mustFollowLeadIfHeld() {
        val hand = listOf(
            Card(1, Suit.HEARTS, Rank.TEN),
            Card(2, Suit.SPADES, Rank.ACE),
            Card(3, Suit.CLUBS, Rank.FIVE)
        )
        // Lead is HEARTS, player holds Hearts: must play Hearts
        assertTrue(FourPlayerEngine.isLegalPlay(hand[0], hand, Suit.HEARTS))
        assertFalse(FourPlayerEngine.isLegalPlay(hand[1], hand, Suit.HEARTS))
        assertFalse(FourPlayerEngine.isLegalPlay(hand[2], hand, Suit.HEARTS))

        // Lead is DIAMONDS, player holds NO Diamonds: may play any card
        assertTrue(FourPlayerEngine.isLegalPlay(hand[0], hand, Suit.DIAMONDS))
        assertTrue(FourPlayerEngine.isLegalPlay(hand[1], hand, Suit.DIAMONDS))
        assertTrue(FourPlayerEngine.isLegalPlay(hand[2], hand, Suit.DIAMONDS))
    }

    @Test
    fun testCalculateEstimatedBid_heuristics() {
        val strongHand = listOf(
            Card(1, Suit.SPADES, Rank.ACE),
            Card(2, Suit.SPADES, Rank.KING),
            Card(3, Suit.SPADES, Rank.QUEEN),
            Card(4, Suit.SPADES, Rank.TEN),
            Card(5, Suit.HEARTS, Rank.ACE),
            Card(6, Suit.CLUBS, Rank.ACE)
        )
        val bid = FourPlayerEngine.calculateEstimatedBid(strongHand, Suit.SPADES)
        assertTrue("Strong hand should bid at least 4", bid >= 4)
    }

    @Test
    fun testCalculateRoundScores_callBreakRules() {
        val initialPlayers = FourPlayerEngine.dealNewRound().players.toMutableMap()
        // South bid 3, won 4 -> score = 3 + 0.1 = 3.1
        initialPlayers[PlayerPosition.SOUTH] = initialPlayers[PlayerPosition.SOUTH]!!.copy(bid = 3, tricksWon = 4)
        // West bid 4, won 3 -> failed bid, score = -4.0
        initialPlayers[PlayerPosition.WEST] = initialPlayers[PlayerPosition.WEST]!!.copy(bid = 4, tricksWon = 3)

        val scored = FourPlayerEngine.calculateRoundScores(initialPlayers)
        assertEquals(3.1, scored[PlayerPosition.SOUTH]!!.roundScoreDelta, 0.001)
        assertEquals(-4.0, scored[PlayerPosition.WEST]!!.roundScoreDelta, 0.001)
    }

    @Test
    fun testCheckRedealCondition() {
        val normalHands = mapOf(
            PlayerPosition.SOUTH to listOf(Card(1, Suit.SPADES, Rank.TWO)),
            PlayerPosition.WEST to listOf(Card(2, Suit.SPADES, Rank.THREE)),
            PlayerPosition.NORTH to listOf(Card(3, Suit.SPADES, Rank.FOUR)),
            PlayerPosition.EAST to listOf(Card(4, Suit.SPADES, Rank.FIVE))
        )
        assertFalse(FourPlayerEngine.checkRedealCondition(normalHands, Suit.SPADES))

        val voidHand = mapOf(
            PlayerPosition.SOUTH to listOf(Card(1, Suit.HEARTS, Rank.TWO)), // Zero spades
            PlayerPosition.WEST to listOf(Card(2, Suit.SPADES, Rank.THREE)),
            PlayerPosition.NORTH to listOf(Card(3, Suit.SPADES, Rank.FOUR)),
            PlayerPosition.EAST to listOf(Card(4, Suit.SPADES, Rank.FIVE))
        )
        assertTrue(FourPlayerEngine.checkRedealCondition(voidHand, Suit.SPADES))
    }
}
