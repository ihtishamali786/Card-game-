package com.solitaire.hyper.card.games.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "achievements")
data class AchievementEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String,
    val currentProgress: Int = 0,
    val targetProgress: Int = 1,
    val isUnlocked: Boolean = false,
    val unlockedDateMillis: Long = 0L
)
