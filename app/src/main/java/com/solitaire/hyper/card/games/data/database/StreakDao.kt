package com.solitaire.hyper.card.games.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface StreakDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(streak: StreakRecordEntity)

    @Query("SELECT * FROM streak_records WHERE mode = :mode")
    suspend fun getStreakByMode(mode: String): StreakRecordEntity?

    @Query("SELECT * FROM streak_records WHERE mode = :mode")
    fun getStreakFlowByMode(mode: String): Flow<StreakRecordEntity?>

    @Query("SELECT * FROM streak_records")
    fun getAllStreaks(): Flow<List<StreakRecordEntity>>

    @Query("DELETE FROM streak_records")
    suspend fun clearAll()
}
