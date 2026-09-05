package com.solitaire.hyper.card.games.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        GameRecordEntity::class,
        DailyChallengeEntity::class,
        AchievementEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun gameRecordDao(): GameRecordDao
    abstract fun dailyChallengeDao(): DailyChallengeDao
    abstract fun achievementDao(): AchievementDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "solitaire_hyper.db"
                ).addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Seed initial achievements
                        CoroutineScope(Dispatchers.IO).launch {
                            INSTANCE?.achievementDao()?.insertAll(initialAchievements())
                        }
                    }
                }).fallbackToDestructiveMigration(dropAllTables = false)
                    .build()
                INSTANCE = instance
                instance
            }
        }

        fun initialAchievements(): List<AchievementEntity> = listOf(
            AchievementEntity(
                id = "FIRST_VICTORY",
                title = "First Victory",
                description = "Win your very first game of Solitaire",
                targetProgress = 1
            ),
            AchievementEntity(
                id = "VICTORIES_10",
                title = "Ten Victories",
                description = "Win 10 Solitaire games",
                targetProgress = 10
            ),
            AchievementEntity(
                id = "VICTORIES_50",
                title = "Fifty Victories",
                description = "Win 50 Solitaire games",
                targetProgress = 50
            ),
            AchievementEntity(
                id = "VICTORIES_100",
                title = "Hundred Victories",
                description = "Master of cards: Win 100 Solitaire games",
                targetProgress = 100
            ),
            AchievementEntity(
                id = "SPEED_DEMON",
                title = "Speed Victory",
                description = "Win a game in under 3 minutes (180 seconds)",
                targetProgress = 1
            ),
            AchievementEntity(
                id = "PERFECT_GAME",
                title = "Perfect Game",
                description = "Win a game without using a single hint",
                targetProgress = 1
            ),
            AchievementEntity(
                id = "STREAK_3",
                title = "Winning Streak",
                description = "Win 3 games in a row",
                targetProgress = 3
            ),
            AchievementEntity(
                id = "DAILY_MASTER",
                title = "Daily Challenger",
                description = "Complete 7 Daily Challenges",
                targetProgress = 7
            ),
            AchievementEntity(
                id = "DRAW_3_CHAMPION",
                title = "Draw 3 Master",
                description = "Win 5 games playing Draw 3 mode",
                targetProgress = 5
            )
        )
    }
}
