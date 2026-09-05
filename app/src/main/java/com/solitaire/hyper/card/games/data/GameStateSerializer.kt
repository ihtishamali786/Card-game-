package com.solitaire.hyper.card.games.data

import android.util.Log
import com.solitaire.hyper.card.games.game.model.Card
import com.solitaire.hyper.card.games.game.model.GameMode
import com.solitaire.hyper.card.games.game.model.GameState
import com.solitaire.hyper.card.games.game.model.Rank
import com.solitaire.hyper.card.games.game.model.Suit
import org.json.JSONArray
import org.json.JSONObject

/**
 * Fault-tolerant JSON serialization for game state persistence.
 * Guaranteed to catch any corrupted state and return null gracefully.
 */
object GameStateSerializer {
    private const val TAG = "GameStateSerializer"

    fun serialize(state: GameState): String {
        return try {
            val root = JSONObject()
            root.put("score", state.score)
            root.put("moveCount", state.moveCount)
            root.put("elapsedTimeSeconds", state.elapsedTimeSeconds)
            root.put("isGameWon", state.isGameWon)
            root.put("gameMode", state.gameMode.name)
            root.put("isDailyChallenge", state.isDailyChallenge)
            root.put("challengeDate", state.challengeDate ?: "")
            root.put("seed", state.seed)

            // Stock
            val stockArray = JSONArray()
            state.stock.forEach { stockArray.put(serializeCard(it)) }
            root.put("stock", stockArray)

            // Waste
            val wasteArray = JSONArray()
            state.waste.forEach { wasteArray.put(serializeCard(it)) }
            root.put("waste", wasteArray)

            // Foundations
            val foundationsArray = JSONArray()
            state.foundations.forEach { pile ->
                val pileArray = JSONArray()
                pile.forEach { pileArray.put(serializeCard(it)) }
                foundationsArray.put(pileArray)
            }
            root.put("foundations", foundationsArray)

            // Tableau
            val tableauArray = JSONArray()
            state.tableau.forEach { col ->
                val colArray = JSONArray()
                col.forEach { colArray.put(serializeCard(it)) }
                tableauArray.put(colArray)
            }
            root.put("tableau", tableauArray)

            root.toString()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to serialize game state", e)
            ""
        }
    }

    fun deserialize(jsonString: String?): GameState? {
        if (jsonString.isNullOrBlank()) return null
        return try {
            val root = JSONObject(jsonString)
            val score = root.optInt("score", 0)
            val moveCount = root.optInt("moveCount", 0)
            val elapsedTimeSeconds = root.optLong("elapsedTimeSeconds", 0L)
            val isGameWon = root.optBoolean("isGameWon", false)
            val gameMode = GameMode.fromString(root.optString("gameMode", GameMode.DRAW_1.name))
            val isDailyChallenge = root.optBoolean("isDailyChallenge", false)
            val challengeDate = root.optString("challengeDate", "").takeIf { it.isNotEmpty() }
            val seed = root.optLong("seed", System.currentTimeMillis())

            val stock = deserializeCardList(root.optJSONArray("stock"))
            val waste = deserializeCardList(root.optJSONArray("waste"))

            val foundations = ArrayList<List<Card>>()
            val foundationsArray = root.optJSONArray("foundations")
            for (i in 0 until 4) {
                if (foundationsArray != null && i < foundationsArray.length()) {
                    foundations.add(deserializeCardList(foundationsArray.optJSONArray(i)))
                } else {
                    foundations.add(emptyList())
                }
            }

            val tableau = ArrayList<List<Card>>()
            val tableauArray = root.optJSONArray("tableau")
            for (i in 0 until 7) {
                if (tableauArray != null && i < tableauArray.length()) {
                    tableau.add(deserializeCardList(tableauArray.optJSONArray(i)))
                } else {
                    tableau.add(emptyList())
                }
            }

            GameState(
                stock = stock,
                waste = waste,
                foundations = foundations,
                tableau = tableau,
                moveCount = moveCount,
                score = score,
                elapsedTimeSeconds = elapsedTimeSeconds,
                isGameWon = isGameWon,
                isAutoCompleteAvailable = false,
                gameMode = gameMode,
                isDailyChallenge = isDailyChallenge,
                challengeDate = challengeDate,
                seed = seed
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to deserialize game state from saved JSON. Corrupted data handled safely.", e)
            null
        }
    }

    private fun serializeCard(card: Card): JSONObject {
        val obj = JSONObject()
        obj.put("id", card.id)
        obj.put("suit", card.suit.name)
        obj.put("rank", card.rank.name)
        obj.put("faceUp", card.isFaceUp)
        return obj
    }

    private fun deserializeCard(obj: JSONObject?): Card? {
        if (obj == null) return null
        return try {
            val id = obj.getInt("id")
            val suit = Suit.valueOf(obj.getString("suit"))
            val rank = Rank.valueOf(obj.getString("rank"))
            val faceUp = obj.optBoolean("faceUp", false)
            Card(id = id, suit = suit, rank = rank, isFaceUp = faceUp)
        } catch (e: Exception) {
            null
        }
    }

    private fun deserializeCardList(array: JSONArray?): List<Card> {
        if (array == null) return emptyList()
        val list = ArrayList<Card>()
        for (i in 0 until array.length()) {
            val card = deserializeCard(array.optJSONObject(i))
            if (card != null) list.add(card)
        }
        return list
    }
}
