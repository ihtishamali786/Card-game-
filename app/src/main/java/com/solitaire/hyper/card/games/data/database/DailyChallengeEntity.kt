package com.solitaire.hyper.card.games.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_challenges")
data class DailyChallengeEntity(
    @PrimaryKey
    val date: String, // "YYYY-MM-DD"
    val isCompleted: Boolean,
    val moves: Int,
    val timeSeconds: Long,
    val score: Int,
    val completedDateMillis: Long = System.currentTimeMillis()
)
