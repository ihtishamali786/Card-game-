package com.solitaire.hyper.card.games.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyChallengeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(challenge: DailyChallengeEntity)

    @Query("SELECT * FROM daily_challenges WHERE date = :date LIMIT 1")
    suspend fun getChallengeByDate(date: String): DailyChallengeEntity?

    @Query("SELECT * FROM daily_challenges WHERE date = :date LIMIT 1")
    fun observeChallengeByDate(date: String): Flow<DailyChallengeEntity?>

    @Query("SELECT * FROM daily_challenges WHERE isCompleted = 1 ORDER BY date DESC")
    fun getAllCompleted(): Flow<List<DailyChallengeEntity>>

    @Query("SELECT COUNT(*) FROM daily_challenges WHERE isCompleted = 1")
    suspend fun countCompleted(): Int

    @Query("SELECT * FROM daily_challenges WHERE isCompleted = 1 ORDER BY date ASC")
    suspend fun getCompletedListAsc(): List<DailyChallengeEntity>

    @Query("DELETE FROM daily_challenges")
    suspend fun clearAll()
}
