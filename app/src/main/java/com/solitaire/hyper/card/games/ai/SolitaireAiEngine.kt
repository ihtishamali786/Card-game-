package com.solitaire.hyper.card.games.ai

import com.solitaire.hyper.card.games.BuildConfig
import com.solitaire.hyper.card.games.game.engine.SolitaireEngine
import com.solitaire.hyper.card.games.game.model.CardLocation
import com.solitaire.hyper.card.games.game.model.GameState
import com.solitaire.hyper.card.games.game.model.Hint
import com.solitaire.hyper.card.games.game.model.Rank
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

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

/**
 * Intelligent Solitaire AI Engine with Gemini AI Integration & On-Device Fallback.
 * The Gemini API key is securely accessed via BuildConfig without exposing it to the UI or logs.
 */
object SolitaireAiEngine {

    /**
     * Performs a comprehensive AI analysis of the given Solitaire game state.
     * Uses Gemini when available and seamlessly falls back to on-device heuristic analysis.
     */
    suspend fun analyzeGame(gameState: GameState): AiAnalysisResult = withContext(Dispatchers.Default) {
        val totalFoundationCards = gameState.foundations.sumOf { it.size }
        val totalHiddenCards = gameState.tableau.sumOf { col -> col.count { !it.isFaceUp } }
        val emptyCols = gameState.tableau.count { it.isEmpty() }
        val hint = SolitaireEngine.findHint(gameState)

        // Baseline heuristic calculations
        val foundationFactor = (totalFoundationCards * 1.5f).toInt()
        val revealedFactor = ((21 - totalHiddenCards) * 1.8f).toInt()
        val emptyColFactor = emptyCols * 4
        val rawRating = (35 + foundationFactor + revealedFactor + emptyColFactor).coerceIn(40, 98)

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

        // Try Gemini AI enhancement if key is provided in BuildConfig
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isNotBlank() && !apiKey.contains("MY_GEMINI_API_KEY")) {
            val geminiResult = withContext(Dispatchers.IO) {
                fetchGeminiAnalysis(
                    apiKey = apiKey,
                    totalFoundationCards = totalFoundationCards,
                    totalHiddenCards = totalHiddenCards,
                    emptyCols = emptyCols,
                    bestMoveDesc = bestMoveDesc,
                    rawRating = rawRating
                )
            }
            if (geminiResult != null) {
                return@withContext geminiResult
            }
        }

        // Offline Heuristic Grandmaster Fallback
        val strategyTip = when {
            totalHiddenCards > 12 -> "Focus on digging into columns with the deepest hidden cards first."
            emptyCols > 0 && gameState.tableau.any { col -> col.firstOrNull { it.isFaceUp }?.rank != Rank.KING } ->
                "Reserve empty columns exclusively for Kings to unlock new tableau branches."
            totalFoundationCards < 8 -> "Prioritize finding and advancing Aces & Twos to open up space."
            totalHiddenCards == 0 -> "All hidden cards revealed! Tap Auto-Win or move cards up to Foundation."
            else -> "Alternate card colors evenly and avoid premature moves to foundation if you need that card to hold another."
        }

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

    private fun fetchGeminiAnalysis(
        apiKey: String,
        totalFoundationCards: Int,
        totalHiddenCards: Int,
        emptyCols: Int,
        bestMoveDesc: String?,
        rawRating: Int
    ): AiAnalysisResult? {
        return try {
            val endpoint = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
            val url = URL(endpoint)
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json")
            conn.connectTimeout = 4000
            conn.readTimeout = 4000
            conn.doOutput = true

            val promptText = "You are a master Solitaire Klondike Grandmaster AI. Analyze this deal: Foundations=$totalFoundationCards/52, HiddenCards=$totalHiddenCards, EmptyColumns=$emptyCols, SuggestedMove=${bestMoveDesc ?: "Cycle Stock"}. Output strictly a JSON object with keys: solvabilityRating (integer between 40 and 99), recommendationTitle (string, short), recommendationDetail (string, 1-2 sentences), strategyTip (string, 1 sentence)."

            val requestJson = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", promptText)
                            })
                        })
                    })
                })
                put("generationConfig", JSONObject().apply {
                    put("responseMimeType", "application/json")
                    put("temperature", 0.3)
                })
            }

            OutputStreamWriter(conn.outputStream).use { writer ->
                writer.write(requestJson.toString())
                writer.flush()
            }

            if (conn.responseCode == 200) {
                val responseStr = BufferedReader(InputStreamReader(conn.inputStream)).use { it.readText() }
                val root = JSONObject(responseStr)
                val candidates = root.optJSONArray("candidates")
                val firstCandidate = candidates?.optJSONObject(0)
                val content = firstCandidate?.optJSONObject("content")
                val parts = content?.optJSONArray("parts")
                val textPart = parts?.optJSONObject(0)?.optString("text")

                if (!textPart.isNullOrBlank()) {
                    val analysisJson = JSONObject(textPart)
                    val rating = analysisJson.optInt("solvabilityRating", rawRating).coerceIn(40, 99)
                    val recTitle = analysisJson.optString("recommendationTitle", "Grandmaster Tactical Analysis")
                    val recDetail = analysisJson.optString("recommendationDetail", "Optimal play analyzed by Gemini AI.")
                    val tip = analysisJson.optString("strategyTip", "Balance tableau columns carefully.")

                    return AiAnalysisResult(
                        solvabilityRating = rating,
                        recommendationTitle = recTitle,
                        recommendationDetail = recDetail,
                        bestMoveDescription = bestMoveDesc,
                        hiddenCardsCount = totalHiddenCards,
                        cardsInFoundationCount = totalFoundationCards,
                        strategyTip = tip,
                        isGeminiPowered = true
                    )
                }
            }
            null
        } catch (_: Exception) {
            null
        }
    }
}
