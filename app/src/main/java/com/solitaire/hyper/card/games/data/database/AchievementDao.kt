package com.solitaire.hyper.card.games.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AchievementDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(achievements: List<AchievementEntity>)

    @Query("SELECT * FROM achievements ORDER BY isUnlocked DESC, id ASC")
    fun getAllAchievements(): Flow<List<AchievementEntity>>

    @Query("SELECT * FROM achievements WHERE id = :id LIMIT 1")
    suspend fun getAchievement(id: String): AchievementEntity?

    @Query("UPDATE achievements SET currentProgress = :progress, isUnlocked = :unlocked, unlockedDateMillis = :dateMillis WHERE id = :id")
    suspend fun updateProgress(id: String, progress: Int, unlocked: Boolean, dateMillis: Long)

    @Query("SELECT COUNT(*) FROM achievements WHERE isUnlocked = 1")
    suspend fun countUnlocked(): Int
}
