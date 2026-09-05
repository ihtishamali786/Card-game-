package com.solitaire.hyper.card.games.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_records")
data class GameRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val mode: String, // "DRAW_1", "DRAW_3", "DAILY"
    val isWin: Boolean,
    val moves: Int,
    val timeSeconds: Long,
    val score: Int,
    val dateMillis: Long = System.currentTimeMillis()
)
