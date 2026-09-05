package com.solitaire.hyper.card.games.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

data class ModeStats(
    val totalPlayed: Int,
    val totalWon: Int,
    val bestScore: Int,
    val bestTimeSeconds: Long,
    val avgMoves: Double,
    val avgTimeSeconds: Double
)

@Dao
interface GameRecordDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: GameRecordEntity): Long

    @Query("SELECT * FROM game_records ORDER BY dateMillis DESC")
    fun getAllRecords(): Flow<List<GameRecordEntity>>

    @Query("SELECT * FROM game_records WHERE mode = :mode ORDER BY dateMillis DESC")
    fun getRecordsByMode(mode: String): Flow<List<GameRecordEntity>>

    @Query("SELECT * FROM game_records WHERE mode = :mode ORDER BY dateMillis ASC")
    suspend fun getRecordsListByMode(mode: String): List<GameRecordEntity>

    @Query("SELECT COUNT(*) FROM game_records WHERE mode = :mode")
    suspend fun countPlayed(mode: String): Int

    @Query("SELECT COUNT(*) FROM game_records WHERE mode = :mode AND isWin = 1")
    suspend fun countWon(mode: String): Int

    @Query("SELECT MAX(score) FROM game_records WHERE mode = :mode AND isWin = 1")
    suspend fun getBestScore(mode: String): Int?

    @Query("SELECT MIN(timeSeconds) FROM game_records WHERE mode = :mode AND isWin = 1")
    suspend fun getBestTimeSeconds(mode: String): Long?

    @Query("SELECT AVG(moves) FROM game_records WHERE mode = :mode AND isWin = 1")
    suspend fun getAvgMoves(mode: String): Double?

    @Query("SELECT AVG(timeSeconds) FROM game_records WHERE mode = :mode AND isWin = 1")
    suspend fun getAvgTimeSeconds(mode: String): Double?

    @Query("DELETE FROM game_records")
    suspend fun clearAll()
}
