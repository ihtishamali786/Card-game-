package com.solitaire.hyper.card.games

import com.solitaire.hyper.card.games.data.GameStateSerializer
import com.solitaire.hyper.card.games.game.engine.SolitaireEngine
import com.solitaire.hyper.card.games.game.model.GameMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class GameSerializationTest {

    @Test
    fun `serialize and deserialize round-trip preserves state`() {
        val original = SolitaireEngine.newGame(gameMode = GameMode.DRAW_3, seed = 999L)
        val json = GameStateSerializer.serialize(original)
        val restored = GameStateSerializer.deserialize(json)

        assertNotNull(restored)
        assertEquals(original.stock.size, restored!!.stock.size)
        assertEquals(original.waste.size, restored.waste.size)
        assertEquals(original.tableau.size, restored.tableau.size)
        assertEquals(original.foundations.size, restored.foundations.size)
        assertEquals(original.gameMode, restored.gameMode)
    }

    @Test
    fun `corrupted json returns null gracefully without crash`() {
        val corrupted = "{ invalid: json ]"
        val result = GameStateSerializer.deserialize(corrupted)
        assertNull(result)
    }
}
