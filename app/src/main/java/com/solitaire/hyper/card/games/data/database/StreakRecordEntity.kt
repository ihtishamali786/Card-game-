package com.solitaire.hyper.card.games.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Tracks current and historical consecutive win and loss streaks per game mode.
 */
@Entity(tableName = "streak_records")
data class StreakRecordEntity(
    @PrimaryKey
    val mode: String, // "ALL", "DRAW_1", "DRAW_3", "DAILY"
    val currentWinStreak: Int = 0,
    val bestWinStreak: Int = 0,
    val currentLossStreak: Int = 0,
    val worstLossStreak: Int = 0,
    val lastOutcomeWasWin: Boolean? = null,
    val totalWins: Int = 0,
    val totalLosses: Int = 0,
    val lastUpdatedMillis: Long = System.currentTimeMillis()
)
