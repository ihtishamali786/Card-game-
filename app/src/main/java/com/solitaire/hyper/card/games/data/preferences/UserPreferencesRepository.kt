package com.solitaire.hyper.card.games.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_settings")

/**
 * Clean, lightweight user settings for Solitaire Joker: Klondike Zen.
 */
data class UserSettings(
    val soundEnabled: Boolean = true,
    val musicEnabled: Boolean = true,
    val hapticsEnabled: Boolean = true,
    val animationsEnabled: Boolean = true,
    val reduceMotion: Boolean = false,
    val floatingLights: Boolean = true,
    val leftHandedMode: Boolean = false,
    val largePrintMode: Boolean = false,
    val defaultDrawMode: String = "DRAW_1",
    val winnableDealsOnly: Boolean = true,
    val autoCompleteEnabled: Boolean = true,
    val showTimer: Boolean = true,
    val showMoves: Boolean = true,
    val backgroundId: String = "BG_CLASSIC_GREEN",
    val cardBackId: String = "BACK_ROYAL_BLUE",
    val cardFaceId: String = "FACE_STANDARD",
    val savedGameJson: String? = null,
    val isAdsRemoved: Boolean = false,
    val dailyReminderEnabled: Boolean = false,
    val dailyReminderHour: Int = 20,
    val dailyReminderMinute: Int = 0,
    val jokersToday: Int = 3,
    val lastJokerDate: String = "",
    val extraJokers: Int = 0,
    val hasSeenTutorial: Boolean = false,
    val hasRatedApp: Boolean = false,
    val winsCountForRating: Int = 0
) {
    /**
     * Effective available jokers right now (3 free daily + any rewarded extras).
     */
    fun totalAvailableJokers(todayDate: String): Int {
        val dailyFree = if (lastJokerDate == todayDate) jokersToday else 3
        return (dailyFree + extraJokers).coerceAtLeast(0)
    }
}

class UserPreferencesRepository(private val context: Context) {

    private object Keys {
        val KEY_SOUND = booleanPreferencesKey("sound_enabled")
        val KEY_MUSIC = booleanPreferencesKey("music_enabled")
        val KEY_HAPTICS = booleanPreferencesKey("vibration_enabled")
        val KEY_ANIMATIONS = booleanPreferencesKey("animations_enabled")
        val KEY_REDUCE_MOTION = booleanPreferencesKey("reduce_motion")
        val KEY_FLOATING_LIGHTS = booleanPreferencesKey("floating_lights_enabled")
        val KEY_LEFT_HANDED = booleanPreferencesKey("left_handed_mode")
        val KEY_LARGE_PRINT = booleanPreferencesKey("large_print_mode")
        val KEY_DRAW_MODE = stringPreferencesKey("default_draw_mode")
        val KEY_WINNABLE_DEALS = booleanPreferencesKey("winnable_deals_only")
        val KEY_AUTO_COMPLETE = booleanPreferencesKey("auto_complete_enabled")
        val KEY_SHOW_TIMER = booleanPreferencesKey("show_timer")
        val KEY_SHOW_MOVES = booleanPreferencesKey("show_moves")
        val KEY_BACKGROUND = stringPreferencesKey("background_id")
        val KEY_CARD_BACK = stringPreferencesKey("card_back_id")
        val KEY_CARD_FACE = stringPreferencesKey("card_face_id")
        val KEY_SAVED_GAME = stringPreferencesKey("saved_game_json")
        val KEY_IS_ADS_REMOVED = booleanPreferencesKey("is_ads_removed")
        val KEY_DAILY_REMINDER = booleanPreferencesKey("daily_reminder_enabled")
        val KEY_REMINDER_HOUR = intPreferencesKey("daily_reminder_hour")
        val KEY_REMINDER_MINUTE = intPreferencesKey("daily_reminder_minute")
        val KEY_JOKERS_TODAY = intPreferencesKey("jokers_today_count")
        val KEY_LAST_JOKER_DATE = stringPreferencesKey("last_joker_date")
        val KEY_EXTRA_JOKERS = intPreferencesKey("extra_jokers_balance")
        val KEY_SEEN_TUTORIAL = booleanPreferencesKey("has_seen_tutorial")
        val KEY_RATED_APP = booleanPreferencesKey("has_rated_app")
        val KEY_WINS_FOR_RATING = intPreferencesKey("wins_count_for_rating")
    }

    val userSettingsFlow: Flow<UserSettings> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { prefs ->
            UserSettings(
                soundEnabled = prefs[Keys.KEY_SOUND] ?: true,
                musicEnabled = prefs[Keys.KEY_MUSIC] ?: true,
                hapticsEnabled = prefs[Keys.KEY_HAPTICS] ?: true,
                animationsEnabled = prefs[Keys.KEY_ANIMATIONS] ?: true,
                reduceMotion = prefs[Keys.KEY_REDUCE_MOTION] ?: false,
                floatingLights = prefs[Keys.KEY_FLOATING_LIGHTS] ?: true,
                leftHandedMode = prefs[Keys.KEY_LEFT_HANDED] ?: false,
                largePrintMode = prefs[Keys.KEY_LARGE_PRINT] ?: false,
                defaultDrawMode = prefs[Keys.KEY_DRAW_MODE] ?: "DRAW_1",
                winnableDealsOnly = prefs[Keys.KEY_WINNABLE_DEALS] ?: true,
                autoCompleteEnabled = prefs[Keys.KEY_AUTO_COMPLETE] ?: true,
                showTimer = prefs[Keys.KEY_SHOW_TIMER] ?: true,
                showMoves = prefs[Keys.KEY_SHOW_MOVES] ?: true,
                backgroundId = prefs[Keys.KEY_BACKGROUND] ?: "BG_CLASSIC_GREEN",
                cardBackId = prefs[Keys.KEY_CARD_BACK] ?: "BACK_ROYAL_BLUE",
                cardFaceId = prefs[Keys.KEY_CARD_FACE] ?: "FACE_STANDARD",
                savedGameJson = prefs[Keys.KEY_SAVED_GAME],
                isAdsRemoved = prefs[Keys.KEY_IS_ADS_REMOVED] ?: false,
                dailyReminderEnabled = prefs[Keys.KEY_DAILY_REMINDER] ?: false,
                dailyReminderHour = prefs[Keys.KEY_REMINDER_HOUR] ?: 20,
                dailyReminderMinute = prefs[Keys.KEY_REMINDER_MINUTE] ?: 0,
                jokersToday = prefs[Keys.KEY_JOKERS_TODAY] ?: 3,
                lastJokerDate = prefs[Keys.KEY_LAST_JOKER_DATE] ?: "",
                extraJokers = prefs[Keys.KEY_EXTRA_JOKERS] ?: 0,
                hasSeenTutorial = prefs[Keys.KEY_SEEN_TUTORIAL] ?: false,
                hasRatedApp = prefs[Keys.KEY_RATED_APP] ?: false,
                winsCountForRating = prefs[Keys.KEY_WINS_FOR_RATING] ?: 0
            )
        }

    suspend fun updateSound(enabled: Boolean) {
        context.dataStore.edit { it[Keys.KEY_SOUND] = enabled }
    }

    suspend fun updateMusic(enabled: Boolean) {
        context.dataStore.edit { it[Keys.KEY_MUSIC] = enabled }
    }

    suspend fun updateHaptics(enabled: Boolean) {
        context.dataStore.edit { it[Keys.KEY_HAPTICS] = enabled }
    }

    suspend fun updateAnimations(enabled: Boolean) {
        context.dataStore.edit { it[Keys.KEY_ANIMATIONS] = enabled }
    }

    suspend fun updateReduceMotion(enabled: Boolean) {
        context.dataStore.edit { it[Keys.KEY_REDUCE_MOTION] = enabled }
    }

    suspend fun updateFloatingLights(enabled: Boolean) {
        context.dataStore.edit { it[Keys.KEY_FLOATING_LIGHTS] = enabled }
    }

    suspend fun updateLeftHanded(enabled: Boolean) {
        context.dataStore.edit { it[Keys.KEY_LEFT_HANDED] = enabled }
    }

    suspend fun updateLargePrint(enabled: Boolean) {
        context.dataStore.edit { it[Keys.KEY_LARGE_PRINT] = enabled }
    }

    suspend fun updateDefaultDrawMode(drawMode: String) {
        context.dataStore.edit { it[Keys.KEY_DRAW_MODE] = drawMode }
    }

    suspend fun updateWinnableDealsOnly(enabled: Boolean) {
        context.dataStore.edit { it[Keys.KEY_WINNABLE_DEALS] = enabled }
    }

    suspend fun updateAutoCompleteEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.KEY_AUTO_COMPLETE] = enabled }
    }

    suspend fun updateShowTimer(show: Boolean) {
        context.dataStore.edit { it[Keys.KEY_SHOW_TIMER] = show }
    }

    suspend fun updateShowMoves(show: Boolean) {
        context.dataStore.edit { it[Keys.KEY_SHOW_MOVES] = show }
    }

    suspend fun updateBackground(backgroundId: String) {
        context.dataStore.edit { it[Keys.KEY_BACKGROUND] = backgroundId }
    }

    suspend fun updateCardBack(cardBackId: String) {
        context.dataStore.edit { it[Keys.KEY_CARD_BACK] = cardBackId }
    }

    suspend fun updateCardFace(cardFaceId: String) {
        context.dataStore.edit { it[Keys.KEY_CARD_FACE] = cardFaceId }
    }

    suspend fun saveGame(json: String?) {
        context.dataStore.edit {
            if (json == null) it.remove(Keys.KEY_SAVED_GAME)
            else it[Keys.KEY_SAVED_GAME] = json
        }
    }

    suspend fun clearSavedGame() {
        context.dataStore.edit { it.remove(Keys.KEY_SAVED_GAME) }
    }

    suspend fun setAdsRemoved(removed: Boolean) {
        context.dataStore.edit { it[Keys.KEY_IS_ADS_REMOVED] = removed }
    }

    suspend fun updateDailyReminder(enabled: Boolean, hour: Int, minute: Int) {
        context.dataStore.edit {
            it[Keys.KEY_DAILY_REMINDER] = enabled
            it[Keys.KEY_REMINDER_HOUR] = hour
            it[Keys.KEY_REMINDER_MINUTE] = minute
        }
    }

    suspend fun setTutorialSeen() {
        context.dataStore.edit { it[Keys.KEY_SEEN_TUTORIAL] = true }
    }

    suspend fun setRatedApp() {
        context.dataStore.edit { it[Keys.KEY_RATED_APP] = true }
    }

    suspend fun incrementWinCountForRating(): Int {
        var newCount = 0
        context.dataStore.edit {
            val cur = it[Keys.KEY_WINS_FOR_RATING] ?: 0
            newCount = cur + 1
            it[Keys.KEY_WINS_FOR_RATING] = newCount
        }
        return newCount
    }

    /**
     * Decrements a Joker if available today, or returns false if none remaining.
     */
    suspend fun consumeJoker(todayDate: String): Boolean {
        var success = false
        context.dataStore.edit { prefs ->
            val lastDate = prefs[Keys.KEY_LAST_JOKER_DATE] ?: ""
            var dailyCount = if (lastDate == todayDate) (prefs[Keys.KEY_JOKERS_TODAY] ?: 3) else 3
            var extra = prefs[Keys.KEY_EXTRA_JOKERS] ?: 0

            if (dailyCount > 0) {
                dailyCount -= 1
                prefs[Keys.KEY_JOKERS_TODAY] = dailyCount
                prefs[Keys.KEY_LAST_JOKER_DATE] = todayDate
                success = true
            } else if (extra > 0) {
                extra -= 1
                prefs[Keys.KEY_EXTRA_JOKERS] = extra
                prefs[Keys.KEY_LAST_JOKER_DATE] = todayDate
                success = true
            }
        }
        return success
    }

    suspend fun addExtraJoker(amount: Int = 1) {
        context.dataStore.edit { prefs ->
            val cur = prefs[Keys.KEY_EXTRA_JOKERS] ?: 0
            prefs[Keys.KEY_EXTRA_JOKERS] = cur + amount
        }
    }

    suspend fun getSnapshot(): UserSettings = userSettingsFlow.first()
}
