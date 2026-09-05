package com.solitaire.hyper.card.games

import com.solitaire.hyper.card.games.game.engine.SolitaireEngine
import com.solitaire.hyper.card.games.game.model.Card
import com.solitaire.hyper.card.games.game.model.CardLocation
import com.solitaire.hyper.card.games.game.model.GameMode
import com.solitaire.hyper.card.games.game.model.Rank
import com.solitaire.hyper.card.games.game.model.Suit
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SolitaireEngineTest {

    @Test
    fun `createDeck generates standard 52 unique cards`() {
        val deck = SolitaireEngine.createDeck()
        assertEquals(52, deck.size)
        assertEquals(52, deck.map { it.id }.distinct().size)
        assertEquals(4, deck.map { it.suit }.distinct().size)
        assertEquals(13, deck.map { it.rank }.distinct().size)
    }

    @Test
    fun `newGame deals correct number of cards`() {
        val state = SolitaireEngine.newGame(gameMode = GameMode.DRAW_1, seed = 12345L)
        assertEquals(24, state.stock.size)
        assertEquals(0, state.waste.size)
        assertEquals(4, state.foundations.size)
        assertTrue(state.foundations.all { it.isEmpty() })
        assertEquals(7, state.tableau.size)

        for (i in 0 until 7) {
            val col = state.tableau[i]
            assertEquals(i + 1, col.size)
            assertTrue(col.last().isFaceUp)
            if (col.size > 1) {
                assertTrue(col.subList(0, col.size - 1).all { !it.isFaceUp })
            }
        }
    }

    @Test
    fun `tableau move rules are strictly enforced`() {
        val redFive = Card(1, Suit.HEARTS, Rank.FIVE, isFaceUp = true)
        val blackSix = Card(2, Suit.SPADES, Rank.SIX, isFaceUp = true)
        val redSix = Card(3, Suit.DIAMONDS, Rank.SIX, isFaceUp = true)
        val king = Card(4, Suit.CLUBS, Rank.KING, isFaceUp = true)
        val ace = Card(5, Suit.SPADES, Rank.ACE, isFaceUp = true)

        // Red 5 onto Black 6: valid
        assertTrue(SolitaireEngine.canMoveToTableau(redFive, listOf(blackSix)))

        // Red 5 onto Red 6: invalid (same color)
        assertFalse(SolitaireEngine.canMoveToTableau(redFive, listOf(redSix)))

        // King onto empty column: valid
        assertTrue(SolitaireEngine.canMoveToTableau(king, emptyList()))

        // Ace onto empty column: invalid
        assertFalse(SolitaireEngine.canMoveToTableau(ace, emptyList()))
    }

    @Test
    fun `foundation move rules are strictly enforced`() {
        val aceOfSpades = Card(1, Suit.SPADES, Rank.ACE, isFaceUp = true)
        val twoOfSpades = Card(2, Suit.SPADES, Rank.TWO, isFaceUp = true)
        val twoOfHearts = Card(3, Suit.HEARTS, Rank.TWO, isFaceUp = true)

        // Ace onto empty foundation: valid
        assertTrue(SolitaireEngine.canMoveToFoundation(aceOfSpades, emptyList()))

        // Two onto empty foundation: invalid
        assertFalse(SolitaireEngine.canMoveToFoundation(twoOfSpades, emptyList()))

        // 2 of Spades onto Ace of Spades: valid
        assertTrue(SolitaireEngine.canMoveToFoundation(twoOfSpades, listOf(aceOfSpades)))

        // 2 of Hearts onto Ace of Spades: invalid (wrong suit)
        assertFalse(SolitaireEngine.canMoveToFoundation(twoOfHearts, listOf(aceOfSpades)))
    }

    @Test
    fun `stock draw and undo maintain exact card counts`() {
        val state = SolitaireEngine.newGame(gameMode = GameMode.DRAW_1, seed = 42L)
        val drawResult = SolitaireEngine.drawCards(state)
        assertNotNull(drawResult)
        val (afterDraw, move) = drawResult!!

        assertEquals(23, afterDraw.stock.size)
        assertEquals(1, afterDraw.waste.size)

        val restored = SolitaireEngine.undo(afterDraw, move)
        assertEquals(24, restored.stock.size)
        assertEquals(0, restored.waste.size)
    }
}
