package com.solitaire.hyper.card.games.data

import android.content.Context
import com.solitaire.hyper.card.games.data.database.AchievementEntity
import com.solitaire.hyper.card.games.data.database.AppDatabase
import com.solitaire.hyper.card.games.data.database.DailyChallengeEntity
import com.solitaire.hyper.card.games.data.database.GameRecordEntity
import com.solitaire.hyper.card.games.game.model.GameMode
import com.solitaire.hyper.card.games.game.model.GameState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

data class GameStatistics(
    val gamesPlayed: Int = 0,
    val gamesWon: Int = 0,
    val winPercentage: Int = 0,
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val bestScore: Int = 0,
    val bestTimeSeconds: Long = 0L,
    val averageTimeSeconds: Long = 0L,
    val averageMoves: Int = 0
)

class GameRepository(context: Context) {
    private val database = AppDatabase.getDatabase(context)
    private val gameRecordDao = database.gameRecordDao()
    private val dailyChallengeDao = database.dailyChallengeDao()
    private val achievementDao = database.achievementDao()

    fun getAllRecords(): Flow<List<GameRecordEntity>> = gameRecordDao.getAllRecords()

    fun getRecordsByMode(mode: String): Flow<List<GameRecordEntity>> = gameRecordDao.getRecordsByMode(mode)

    fun getCompletedDailyChallenges(): Flow<List<DailyChallengeEntity>> = dailyChallengeDao.getAllCompleted()

    fun getAchievements(): Flow<List<AchievementEntity>> = achievementDao.getAllAchievements()

    suspend fun getStatistics(mode: String): GameStatistics = withContext(Dispatchers.IO) {
        val records = gameRecordDao.getRecordsListByMode(mode)
        if (records.isEmpty()) return@withContext GameStatistics()

        val played = records.size
        val wonList = records.filter { it.isWin }
        val won = wonList.size
        val winPercentage = if (played > 0) ((won.toDouble() / played) * 100).toInt() else 0

        // Calculate current and best streak
        var currentStreak = 0
        var bestStreak = 0
        var tempStreak = 0
        for (rec in records) {
            if (rec.isWin) {
                tempStreak++
                if (tempStreak > bestStreak) bestStreak = tempStreak
            } else {
                tempStreak = 0
            }
        }
        // Current streak is from the most recent records
        for (rec in records.reversed()) {
            if (rec.isWin) {
                currentStreak++
            } else {
                break
            }
        }

        val bestScore = wonList.maxOfOrNull { it.score } ?: 0
        val bestTime = wonList.minOfOrNull { it.timeSeconds } ?: 0L
        val avgTime = if (wonList.isNotEmpty()) (wonList.sumOf { it.timeSeconds } / wonList.size) else 0L
        val avgMoves = if (wonList.isNotEmpty()) (wonList.sumOf { it.moves } / wonList.size) else 0

        GameStatistics(
            gamesPlayed = played,
            gamesWon = won,
            winPercentage = winPercentage,
            currentStreak = currentStreak,
            bestStreak = bestStreak,
            bestScore = bestScore,
            bestTimeSeconds = bestTime,
            averageTimeSeconds = avgTime,
            averageMoves = avgMoves
        )
    }

    suspend fun recordGameFinished(state: GameState, isWin: Boolean, usedHints: Boolean = false) = withContext(Dispatchers.IO) {
        val modeStr = if (state.isDailyChallenge) "DAILY" else state.gameMode.name
        val record = GameRecordEntity(
            mode = modeStr,
            isWin = isWin,
            moves = state.moveCount,
            timeSeconds = state.elapsedTimeSeconds,
            score = state.score,
            dateMillis = System.currentTimeMillis()
        )
        gameRecordDao.insertRecord(record)

        if (state.isDailyChallenge && isWin && state.challengeDate != null) {
            dailyChallengeDao.insertOrUpdate(
                DailyChallengeEntity(
                    date = state.challengeDate,
                    isCompleted = true,
                    moves = state.moveCount,
                    timeSeconds = state.elapsedTimeSeconds,
                    score = state.score,
                    completedDateMillis = System.currentTimeMillis()
                )
            )
        }

        if (isWin) {
            evaluateAchievements(state, usedHints)
        }
    }

    private suspend fun evaluateAchievements(state: GameState, usedHints: Boolean) {
        val totalWins = gameRecordDao.countWon(state.gameMode.name) + gameRecordDao.countWon("DAILY")

        // First Victory
        updateAchievementProgress("FIRST_VICTORY", 1)

        // 10, 50, 100 Victories
        updateAchievementProgress("VICTORIES_10", totalWins)
        updateAchievementProgress("VICTORIES_50", totalWins)
        updateAchievementProgress("VICTORIES_100", totalWins)

        // Speed Demon: under 180 seconds
        if (state.elapsedTimeSeconds in 1..180) {
            updateAchievementProgress("SPEED_DEMON", 1)
        }

        // Perfect game without hints
        if (!usedHints) {
            updateAchievementProgress("PERFECT_GAME", 1)
        }

        // Draw 3 Master
        if (state.gameMode == GameMode.DRAW_3 && !state.isDailyChallenge) {
            val draw3Wins = gameRecordDao.countWon(GameMode.DRAW_3.name)
            updateAchievementProgress("DRAW_3_CHAMPION", draw3Wins)
        }

        // Daily Master
        val completedDaily = dailyChallengeDao.countCompleted()
        updateAchievementProgress("DAILY_MASTER", completedDaily)
    }

    private suspend fun updateAchievementProgress(id: String, progress: Int) {
        val achievement = achievementDao.getAchievement(id) ?: return
        val newProgress = maxOf(achievement.currentProgress, progress)
        val isNowUnlocked = newProgress >= achievement.targetProgress
        achievementDao.updateProgress(
            id = id,
            progress = newProgress,
            unlocked = isNowUnlocked || achievement.isUnlocked,
            dateMillis = if (isNowUnlocked && !achievement.isUnlocked) System.currentTimeMillis() else achievement.unlockedDateMillis
        )
    }

    suspend fun clearAllStats() = withContext(Dispatchers.IO) {
        gameRecordDao.clearAll()
        dailyChallengeDao.clearAll()
    }
}
