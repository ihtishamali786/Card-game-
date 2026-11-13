package com.solitaire.hyper.card.games.data

import android.content.Context
import com.solitaire.hyper.card.games.data.database.AchievementEntity
import com.solitaire.hyper.card.games.data.database.AppDatabase
import com.solitaire.hyper.card.games.data.database.DailyChallengeEntity
import com.solitaire.hyper.card.games.data.database.GameRecordEntity
import com.solitaire.hyper.card.games.data.database.StreakRecordEntity
import com.solitaire.hyper.card.games.game.model.GameMode
import com.solitaire.hyper.card.games.game.model.GameState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

data class DailyStreakPoint(
    val dayNumber: Int,         // 1 to 30
    val dateKey: String,        // e.g. "Oct 15"
    val fullDate: String,       // e.g. "2026-10-15"
    val winStreak: Int,         // Peak consecutive win streak reached on this day
    val endOfDayStreak: Int,    // Streak at end of day
    val gamesPlayed: Int,       // Games played on this day
    val gamesWon: Int,          // Wins on this day
    val gamesLost: Int          // Losses on this day
)

data class GameStatistics(
    val gamesPlayed: Int = 0,
    val gamesWon: Int = 0,
    val gamesLost: Int = 0,
    val winPercentage: Int = 0,
    val currentStreak: Int = 0, // Current active consecutive wins
    val bestStreak: Int = 0, // Best all-time consecutive wins
    val currentLossStreak: Int = 0, // Current active consecutive losses
    val worstLossStreak: Int = 0, // Worst all-time consecutive losses
    val bestScore: Int = 0,
    val bestTimeSeconds: Long = 0L,
    val averageTimeSeconds: Long = 0L,
    val averageMoves: Int = 0,
    val streakRecord: StreakRecordEntity? = null,
    val recentMatches: List<GameRecordEntity> = emptyList(),
    val streakTrend30Days: List<DailyStreakPoint> = emptyList()
)

class GameRepository(private val database: AppDatabase) {
    constructor(context: Context) : this(AppDatabase.getDatabase(context))

    private val gameRecordDao = database.gameRecordDao()
    private val dailyChallengeDao = database.dailyChallengeDao()
    private val achievementDao = database.achievementDao()
    private val streakDao = database.streakDao()

    fun getAllRecords(): Flow<List<GameRecordEntity>> = gameRecordDao.getAllRecords()

    fun getRecordsByMode(mode: String): Flow<List<GameRecordEntity>> = gameRecordDao.getRecordsByMode(mode)

    fun getCompletedDailyChallenges(): Flow<List<DailyChallengeEntity>> = dailyChallengeDao.getAllCompleted()

    fun getAchievements(): Flow<List<AchievementEntity>> = achievementDao.getAllAchievements()

    fun getStreakFlow(mode: String): Flow<StreakRecordEntity?> = streakDao.getStreakFlowByMode(mode)

    suspend fun getStatistics(mode: String): GameStatistics = withContext(Dispatchers.IO) {
        val records = if (mode == "ALL") {
            gameRecordDao.getAllRecordsList()
        } else {
            gameRecordDao.getRecordsListByMode(mode)
        }
        if (records.isEmpty()) {
            return@withContext GameStatistics(
                streakTrend30Days = compute30DaysStreakTrend(emptyList())
            )
        }

        val played = records.size
        val wonList = records.filter { it.isWin }
        val won = wonList.size
        val lost = played - won
        val winPercentage = if (played > 0) ((won.toDouble() / played) * 100).toInt() else 0

        // Calculate all-time best win streak and worst loss streak
        var bestWinStreak = 0
        var tempWinStreak = 0
        var worstLossStreak = 0
        var tempLossStreak = 0

        for (rec in records) {
            if (rec.isWin) {
                tempWinStreak++
                tempLossStreak = 0
                if (tempWinStreak > bestWinStreak) bestWinStreak = tempWinStreak
            } else {
                tempLossStreak++
                tempWinStreak = 0
                if (tempLossStreak > worstLossStreak) worstLossStreak = tempLossStreak
            }
        }

        // Current active streaks from the most recent records
        var currentWinStreak = 0
        var currentLossStreak = 0
        for (rec in records.reversed()) {
            if (rec.isWin) {
                if (currentLossStreak == 0) {
                    currentWinStreak++
                } else {
                    break
                }
            } else {
                if (currentWinStreak == 0) {
                    currentLossStreak++
                } else {
                    break
                }
            }
        }

        val storedStreak = streakDao.getStreakByMode(mode)
        val finalCurrentWin = maxOf(currentWinStreak, storedStreak?.currentWinStreak ?: 0)
        val finalBestWin = maxOf(bestWinStreak, storedStreak?.bestWinStreak ?: 0)
        val finalCurrentLoss = maxOf(currentLossStreak, storedStreak?.currentLossStreak ?: 0)
        val finalWorstLoss = maxOf(worstLossStreak, storedStreak?.worstLossStreak ?: 0)

        val bestScore = wonList.maxOfOrNull { it.score } ?: 0
        val bestTime = wonList.minOfOrNull { it.timeSeconds } ?: 0L
        val avgTime = if (wonList.isNotEmpty()) (wonList.sumOf { it.timeSeconds } / wonList.size) else 0L
        val avgMoves = if (wonList.isNotEmpty()) (wonList.sumOf { it.moves } / wonList.size) else 0
        val recent = records.takeLast(10).reversed()
        val streakTrend = compute30DaysStreakTrend(records)

        GameStatistics(
            gamesPlayed = played,
            gamesWon = won,
            gamesLost = lost,
            winPercentage = winPercentage,
            currentStreak = finalCurrentWin,
            bestStreak = finalBestWin,
            currentLossStreak = finalCurrentLoss,
            worstLossStreak = finalWorstLoss,
            bestScore = bestScore,
            bestTimeSeconds = bestTime,
            averageTimeSeconds = avgTime,
            averageMoves = avgMoves,
            streakRecord = storedStreak,
            recentMatches = recent,
            streakTrend30Days = streakTrend
        )
    }

    private fun compute30DaysStreakTrend(records: List<GameRecordEntity>): List<DailyStreakPoint> {
        val result = mutableListOf<DailyStreakPoint>()
        val calendar = java.util.Calendar.getInstance().apply {
            set(java.util.Calendar.HOUR_OF_DAY, 23)
            set(java.util.Calendar.MINUTE, 59)
            set(java.util.Calendar.SECOND, 59)
            set(java.util.Calendar.MILLISECOND, 999)
        }

        val dateFormat = java.text.SimpleDateFormat("MMM d", java.util.Locale.US)
        val fullDateFormat = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)

        val daysList = (29 downTo 0).map { daysAgo ->
            val cal = java.util.Calendar.getInstance().apply {
                timeInMillis = calendar.timeInMillis
                add(java.util.Calendar.DAY_OF_YEAR, -daysAgo)
            }
            val endOfDay = cal.timeInMillis
            cal.set(java.util.Calendar.HOUR_OF_DAY, 0)
            cal.set(java.util.Calendar.MINUTE, 0)
            cal.set(java.util.Calendar.SECOND, 0)
            cal.set(java.util.Calendar.MILLISECOND, 0)
            val startOfDay = cal.timeInMillis
            Triple(startOfDay, endOfDay, dateFormat.format(cal.time) to fullDateFormat.format(cal.time))
        }

        var runningStreak = 0
        var recordIndex = 0
        val sortedRecords = records.sortedBy { it.dateMillis }

        daysList.forEachIndexed { idx, (startOfDay, endOfDay, dateStrings) ->
            val (label, fullDate) = dateStrings
            var playedToday = 0
            var wonToday = 0
            var lostToday = 0
            var maxStreakToday = runningStreak

            while (recordIndex < sortedRecords.size && sortedRecords[recordIndex].dateMillis <= endOfDay) {
                val rec = sortedRecords[recordIndex]
                if (rec.dateMillis >= startOfDay) {
                    playedToday++
                    if (rec.isWin) {
                        wonToday++
                        runningStreak++
                        if (runningStreak > maxStreakToday) {
                            maxStreakToday = runningStreak
                        }
                    } else {
                        lostToday++
                        runningStreak = 0
                    }
                } else {
                    if (rec.isWin) {
                        runningStreak++
                    } else {
                        runningStreak = 0
                    }
                    maxStreakToday = runningStreak
                }
                recordIndex++
            }

            result.add(
                DailyStreakPoint(
                    dayNumber = idx + 1,
                    dateKey = label,
                    fullDate = fullDate,
                    winStreak = maxStreakToday,
                    endOfDayStreak = runningStreak,
                    gamesPlayed = playedToday,
                    gamesWon = wonToday,
                    gamesLost = lostToday
                )
            )
        }
        return result
    }

    suspend fun recordGameFinished(state: GameState, isWin: Boolean, usedHints: Boolean = false) = withContext(Dispatchers.IO) {
        val modeStr = if (state.isDailyChallenge) "DAILY" else state.gameMode.name
        
        // Retrieve prior streaks for this mode & ALL from Room
        val priorModeStreak = streakDao.getStreakByMode(modeStr)
        val priorAllStreak = streakDao.getStreakByMode("ALL")

        val newModeWinStreak = if (isWin) (priorModeStreak?.currentWinStreak ?: 0) + 1 else 0
        val newModeLossStreak = if (!isWin) (priorModeStreak?.currentLossStreak ?: 0) + 1 else 0
        val bestModeWinStreak = maxOf(newModeWinStreak, priorModeStreak?.bestWinStreak ?: 0)
        val worstModeLossStreak = maxOf(newModeLossStreak, priorModeStreak?.worstLossStreak ?: 0)

        val newAllWinStreak = if (isWin) (priorAllStreak?.currentWinStreak ?: 0) + 1 else 0
        val newAllLossStreak = if (!isWin) (priorAllStreak?.currentLossStreak ?: 0) + 1 else 0
        val bestAllWinStreak = maxOf(newAllWinStreak, priorAllStreak?.bestWinStreak ?: 0)
        val worstAllLossStreak = maxOf(newAllLossStreak, priorAllStreak?.worstLossStreak ?: 0)

        val record = GameRecordEntity(
            mode = modeStr,
            isWin = isWin,
            moves = state.moveCount,
            timeSeconds = state.elapsedTimeSeconds,
            score = state.score,
            dateMillis = System.currentTimeMillis(),
            consecutiveWins = newModeWinStreak,
            consecutiveLosses = newModeLossStreak
        )
        gameRecordDao.insertRecord(record)

        // Persist mode-specific streak entity in Room
        streakDao.insertOrUpdate(
            StreakRecordEntity(
                mode = modeStr,
                currentWinStreak = newModeWinStreak,
                bestWinStreak = bestModeWinStreak,
                currentLossStreak = newModeLossStreak,
                worstLossStreak = worstModeLossStreak,
                lastOutcomeWasWin = isWin,
                totalWins = (priorModeStreak?.totalWins ?: 0) + if (isWin) 1 else 0,
                totalLosses = (priorModeStreak?.totalLosses ?: 0) + if (!isWin) 1 else 0,
                lastUpdatedMillis = System.currentTimeMillis()
            )
        )

        // Persist global "ALL" streak entity in Room
        streakDao.insertOrUpdate(
            StreakRecordEntity(
                mode = "ALL",
                currentWinStreak = newAllWinStreak,
                bestWinStreak = bestAllWinStreak,
                currentLossStreak = newAllLossStreak,
                worstLossStreak = worstAllLossStreak,
                lastOutcomeWasWin = isWin,
                totalWins = (priorAllStreak?.totalWins ?: 0) + if (isWin) 1 else 0,
                totalLosses = (priorAllStreak?.totalLosses ?: 0) + if (!isWin) 1 else 0,
                lastUpdatedMillis = System.currentTimeMillis()
            )
        )

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
            evaluateAchievements(state, usedHints, newAllWinStreak)
        }
    }

    suspend fun recordCustomGame(
        mode: String,
        isWin: Boolean,
        moves: Int,
        timeSeconds: Long,
        score: Int
    ) = withContext(Dispatchers.IO) {
        val priorStreak = streakDao.getStreakByMode(mode)
        val newWinStreak = if (isWin) (priorStreak?.currentWinStreak ?: 0) + 1 else 0
        val newLossStreak = if (!isWin) (priorStreak?.currentLossStreak ?: 0) + 1 else 0
        val record = GameRecordEntity(
            mode = mode,
            isWin = isWin,
            moves = moves,
            timeSeconds = timeSeconds,
            score = score,
            dateMillis = System.currentTimeMillis(),
            consecutiveWins = newWinStreak,
            consecutiveLosses = newLossStreak
        )
        gameRecordDao.insertRecord(record)
    }

    private suspend fun evaluateAchievements(state: GameState, usedHints: Boolean, currentWinStreak: Int) {
        val totalWins = gameRecordDao.countWon(state.gameMode.name) + gameRecordDao.countWon("DAILY")

        // First Victory
        updateAchievementProgress("FIRST_VICTORY", 1)

        // 10, 50, 100 Victories
        updateAchievementProgress("VICTORIES_10", totalWins)
        updateAchievementProgress("VICTORIES_50", totalWins)
        updateAchievementProgress("VICTORIES_100", totalWins)

        // Winning streak 3+
        if (currentWinStreak >= 3) {
            updateAchievementProgress("STREAK_3", currentWinStreak)
        }

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
        val wasUnlocked = achievement.isUnlocked
        achievementDao.updateProgress(
            id = id,
            progress = newProgress,
            unlocked = isNowUnlocked || wasUnlocked,
            dateMillis = if (isNowUnlocked && !wasUnlocked) System.currentTimeMillis() else achievement.unlockedDateMillis
        )
    }

    suspend fun clearAllStats() = withContext(Dispatchers.IO) {
        gameRecordDao.clearAll()
        dailyChallengeDao.clearAll()
        streakDao.clearAll()
    }
}
