package com.solitaire.hyper.card.games.ai

import com.solitaire.hyper.card.games.BuildConfig
import com.solitaire.hyper.card.games.game.engine.SolitaireEngine
import com.solitaire.hyper.card.games.game.model.CardLocation
import com.solitaire.hyper.card.games.game.model.GameState
import com.solitaire.hyper.card.games.game.model.Hint
import com.solitaire.hyper.card.games.game.model.Rank
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class AiAnalysisResult(
    val solvabilityRating: Int, // 0 - 100%
    val recommendationTitle: String,
    val recommendationDetail: String,
    val bestMoveDescription: String?,
    val hiddenCardsCount: Int,
    val cardsInFoundationCount: Int,
    val strategyTip: String,
    val isGeminiPowered: Boolean
)

object SolitaireAiEngine {

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(8, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    /**
     * Performs a comprehensive AI analysis of the given Solitaire game state.
     * Uses Gemini AI if GEMINI_API_KEY is configured in BuildConfig, otherwise uses
     * the built-in heuristic grandmaster solver engine.
     */
    suspend fun analyzeGame(gameState: GameState): AiAnalysisResult = withContext(Dispatchers.Default) {
        val totalFoundationCards = gameState.foundations.sumOf { it.size }
        val totalHiddenCards = gameState.tableau.sumOf { col -> col.count { !it.isFaceUp } }
        val emptyCols = gameState.tableau.count { it.isEmpty() }
        val hint = SolitaireEngine.findHint(gameState)

        // Calculate heuristic solvability rating (40% - 98%)
        val foundationFactor = (totalFoundationCards * 1.5f).toInt()
        val revealedFactor = ((21 - totalHiddenCards) * 1.8f).toInt()
        val emptyColFactor = emptyCols * 4
        val rawRating = (35 + foundationFactor + revealedFactor + emptyColFactor).coerceIn(40, 98)

        // Basic move description
        val bestMoveDesc = hint?.let { h ->
            val fromName = when (val from = h.from) {
                is CardLocation.Waste -> "Waste"
                is CardLocation.Tableau -> "Column ${from.columnIndex + 1}"
                is CardLocation.Foundation -> "Foundation ${from.index + 1}"
                else -> "Board"
            }
            val toName = when (val to = h.to) {
                is CardLocation.Foundation -> "Foundation ${to.index + 1}"
                is CardLocation.Tableau -> "Column ${to.columnIndex + 1}"
                else -> "Board"
            }
            "${h.description} ($fromName → $toName)"
        }

        val strategyTip = when {
            totalHiddenCards > 12 -> "Focus on digging into columns with the deepest hidden cards first."
            emptyCols > 0 && gameState.tableau.any { col -> col.firstOrNull { it.isFaceUp }?.rank != Rank.KING } ->
                "Reserve empty columns exclusively for Kings to unlock new tableau branches."
            totalFoundationCards < 8 -> "Prioritize finding and advancing Aces & Twos to open up space."
            totalHiddenCards == 0 -> "All hidden cards revealed! Tap Auto-Win or move cards up to Foundation."
            else -> "Alternate card colors evenly and avoid premature moves to foundation if you need that card to hold another."
        }

        // Check if Gemini API key exists
        val apiKey = try {
            val field = BuildConfig::class.java.getField("GEMINI_API_KEY")
            field.get(null) as? String ?: ""
        } catch (_: Exception) {
            ""
        }

        if (apiKey.isNotBlank() && apiKey != "DEFAULT_KEY" && apiKey != "YOUR_GEMINI_API_KEY") {
            try {
                val geminiAnalysis = fetchGeminiAdvice(apiKey, gameState, rawRating, bestMoveDesc)
                if (geminiAnalysis != null) {
                    return@withContext AiAnalysisResult(
                        solvabilityRating = rawRating,
                        recommendationTitle = geminiAnalysis.first,
                        recommendationDetail = geminiAnalysis.second,
                        bestMoveDescription = bestMoveDesc,
                        hiddenCardsCount = totalHiddenCards,
                        cardsInFoundationCount = totalFoundationCards,
                        strategyTip = strategyTip,
                        isGeminiPowered = true
                    )
                }
            } catch (_: Exception) {
                // Graceful fallback to heuristic analysis
            }
        }

        // High-quality on-device heuristic AI coach
        val title = when {
            totalHiddenCards == 0 -> "Winning State Imminent"
            rawRating >= 80 -> "High Solvability Position"
            rawRating >= 60 -> "Strong Tactical Deal"
            else -> "Challenging Layout"
        }

        val detail = if (bestMoveDesc != null) {
            "Optimal tactic: $bestMoveDesc. This move frees up tableau flexibility and exposes hidden opportunities."
        } else {
            "Current position requires careful stock cycling. Look for cross-column transfers to unblock buried ranks."
        }

        AiAnalysisResult(
            solvabilityRating = rawRating,
            recommendationTitle = title,
            recommendationDetail = detail,
            bestMoveDescription = bestMoveDesc,
            hiddenCardsCount = totalHiddenCards,
            cardsInFoundationCount = totalFoundationCards,
            strategyTip = strategyTip,
            isGeminiPowered = false
        )
    }

    private fun fetchGeminiAdvice(
        apiKey: String,
        gameState: GameState,
        rating: Int,
        bestMove: String?
    ): Pair<String, String>? {
        val prompt = buildString {
            append("You are an expert Solitaire Grandmaster. ")
            append("Analyze this Klondike Solitaire state: ")
            append("Score: ${gameState.score}, Moves: ${gameState.moveCount}, ")
            append("Cards in Foundation: ${gameState.foundations.sumOf { it.size }}/52, ")
            append("Face-down hidden cards: ${gameState.tableau.sumOf { col -> col.count { !it.isFaceUp } }}. ")
            if (bestMove != null) append("Suggested move: $bestMove. ")
            append("Provide exactly 2 short sentences: first a concise tactical evaluation title, second the key strategic advice.")
        }

        val jsonBody = JSONObject().apply {
            val contents = JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", prompt)
                        })
                    })
                })
            }
            put("contents", contents)
            put("generationConfig", JSONObject().apply {
                put("maxOutputTokens", 80)
                put("temperature", 0.4)
            })
        }

        val request = Request.Builder()
            .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey")
            .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
            .build()

        val response = httpClient.newCall(request).execute()
        if (!response.isSuccessful) return null

        val responseBody = response.body?.string() ?: return null
        val resJson = JSONObject(responseBody)
        val candidateText = resJson
            .getJSONArray("candidates")
            .getJSONObject(0)
            .getJSONObject("content")
            .getJSONArray("parts")
            .getJSONObject(0)
            .getString("text")
            .trim()

        val lines = candidateText.split("\n", ".").filter { it.isNotBlank() }
        val title = lines.getOrNull(0)?.trim()?.take(40) ?: "Grandmaster AI Analysis"
        val detail = lines.drop(1).joinToString(". ") { it.trim() }.take(180).ifBlank {
            bestMove ?: "Maintain balance between foundation transfers and tableau mobility."
        }

        return Pair(title, detail)
    }
}
