package com.solitaire.hyper.card.games

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.solitaire.hyper.card.games.data.database.AppDatabase
import com.solitaire.hyper.card.games.data.database.GameRecordEntity
import com.solitaire.hyper.card.games.data.database.StreakRecordEntity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class StreakTrackingDatabaseTest {

    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun testInsertAndRetrieveGameRecordsWithConsecutiveStreaks() = runBlocking {
        val record1 = GameRecordEntity(
            mode = "DRAW_1",
            isWin = true,
            moves = 75,
            timeSeconds = 120,
            score = 650,
            consecutiveWins = 1,
            consecutiveLosses = 0
        )
        val record2 = GameRecordEntity(
            mode = "DRAW_1",
            isWin = true,
            moves = 68,
            timeSeconds = 110,
            score = 720,
            consecutiveWins = 2,
            consecutiveLosses = 0
        )
        val record3 = GameRecordEntity(
            mode = "DRAW_1",
            isWin = false,
            moves = 30,
            timeSeconds = 50,
            score = 120,
            consecutiveWins = 0,
            consecutiveLosses = 1
        )

        db.gameRecordDao().insertRecord(record1)
        db.gameRecordDao().insertRecord(record2)
        db.gameRecordDao().insertRecord(record3)

        val records = db.gameRecordDao().getAllRecordsList()
        assertEquals(3, records.size)
        assertEquals(1, records[0].consecutiveWins)
        assertEquals(2, records[1].consecutiveWins)
        assertEquals(1, records[2].consecutiveLosses)
    }

    @Test
    fun testStreakRecordEntityPersistence() = runBlocking {
        val streak = StreakRecordEntity(
            mode = "DRAW_1",
            currentWinStreak = 4,
            bestWinStreak = 7,
            currentLossStreak = 0,
            worstLossStreak = 2,
            lastOutcomeWasWin = true,
            totalWins = 15,
            totalLosses = 5
        )

        db.streakDao().insertOrUpdate(streak)

        val retrieved = db.streakDao().getStreakByMode("DRAW_1")
        assertNotNull(retrieved)
        assertEquals(4, retrieved?.currentWinStreak)
        assertEquals(7, retrieved?.bestWinStreak)
        assertEquals(0, retrieved?.currentLossStreak)
        assertEquals(2, retrieved?.worstLossStreak)
    }

    @Test
    fun testRecharts30DayStreakTrendComputation() = runBlocking {
        val now = System.currentTimeMillis()
        val oneDayMillis = 24 * 60 * 60 * 1000L

        // Day -3: Win 1
        db.gameRecordDao().insertRecord(
            GameRecordEntity(
                mode = "ALL",
                isWin = true,
                moves = 80,
                timeSeconds = 120,
                score = 600,
                dateMillis = now - 3 * oneDayMillis,
                consecutiveWins = 1,
                consecutiveLosses = 0
            )
        )
        // Day -3: Win 2
        db.gameRecordDao().insertRecord(
            GameRecordEntity(
                mode = "ALL",
                isWin = true,
                moves = 70,
                timeSeconds = 110,
                score = 700,
                dateMillis = now - 3 * oneDayMillis + 1000,
                consecutiveWins = 2,
                consecutiveLosses = 0
            )
        )
        // Day -1: Win 3
        db.gameRecordDao().insertRecord(
            GameRecordEntity(
                mode = "ALL",
                isWin = true,
                moves = 65,
                timeSeconds = 95,
                score = 800,
                dateMillis = now - 1 * oneDayMillis,
                consecutiveWins = 3,
                consecutiveLosses = 0
            )
        )

        val repository = com.solitaire.hyper.card.games.data.GameRepository(db)
        val stats = repository.getStatistics("ALL")

        assertEquals(30, stats.streakTrend30Days.size)
        val activeDays = stats.streakTrend30Days.filter { it.winStreak > 0 }
        org.junit.Assert.assertTrue(activeDays.isNotEmpty())
    }
}

