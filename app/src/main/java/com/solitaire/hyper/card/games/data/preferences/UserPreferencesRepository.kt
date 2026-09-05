package com.solitaire.hyper.card.games.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_settings")

data class UserSettings(
    val soundEnabled: Boolean = true,
    val musicEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val leftHandedMode: Boolean = false,
    val defaultDrawMode: String = "DRAW_1",
    val backgroundId: String = "CLASSIC_FELT",
    val cardBackId: String = "ROYAL_CREST",
    val cardFaceId: String = "CLASSIC",
    val autoCompleteEnabled: Boolean = true,
    val notificationsEnabled: Boolean = true,
    val showTimer: Boolean = true,
    val savedGameJson: String? = null
)

class UserPreferencesRepository(private val context: Context) {

    private object PreferencesKeys {
        val KEY_SOUND = booleanPreferencesKey("sound_enabled")
        val KEY_MUSIC = booleanPreferencesKey("music_enabled")
        val KEY_VIBRATION = booleanPreferencesKey("vibration_enabled")
        val KEY_LEFT_HANDED = booleanPreferencesKey("left_handed_mode")
        val KEY_DRAW_MODE = stringPreferencesKey("default_draw_mode")
        val KEY_BACKGROUND = stringPreferencesKey("background_id")
        val KEY_CARD_BACK = stringPreferencesKey("card_back_id")
        val KEY_CARD_FACE = stringPreferencesKey("card_face_id")
        val KEY_AUTO_COMPLETE = booleanPreferencesKey("auto_complete_enabled")
        val KEY_NOTIFICATIONS = booleanPreferencesKey("notifications_enabled")
        val KEY_SHOW_TIMER = booleanPreferencesKey("show_timer")
        val KEY_SAVED_GAME = stringPreferencesKey("saved_game_json")
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
                soundEnabled = prefs[PreferencesKeys.KEY_SOUND] ?: true,
                musicEnabled = prefs[PreferencesKeys.KEY_MUSIC] ?: true,
                vibrationEnabled = prefs[PreferencesKeys.KEY_VIBRATION] ?: true,
                leftHandedMode = prefs[PreferencesKeys.KEY_LEFT_HANDED] ?: false,
                defaultDrawMode = prefs[PreferencesKeys.KEY_DRAW_MODE] ?: "DRAW_1",
                backgroundId = prefs[PreferencesKeys.KEY_BACKGROUND] ?: "CLASSIC_FELT",
                cardBackId = prefs[PreferencesKeys.KEY_CARD_BACK] ?: "ROYAL_CREST",
                cardFaceId = prefs[PreferencesKeys.KEY_CARD_FACE] ?: "CLASSIC",
                autoCompleteEnabled = prefs[PreferencesKeys.KEY_AUTO_COMPLETE] ?: true,
                notificationsEnabled = prefs[PreferencesKeys.KEY_NOTIFICATIONS] ?: true,
                showTimer = prefs[PreferencesKeys.KEY_SHOW_TIMER] ?: true,
                savedGameJson = prefs[PreferencesKeys.KEY_SAVED_GAME]
            )
        }

    suspend fun updateSound(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.KEY_SOUND] = enabled }
    }

    suspend fun updateMusic(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.KEY_MUSIC] = enabled }
    }

    suspend fun updateVibration(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.KEY_VIBRATION] = enabled }
    }

    suspend fun updateLeftHanded(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.KEY_LEFT_HANDED] = enabled }
    }

    suspend fun updateDrawMode(mode: String) {
        context.dataStore.edit { it[PreferencesKeys.KEY_DRAW_MODE] = mode }
    }

    suspend fun updateBackground(id: String) {
        context.dataStore.edit { it[PreferencesKeys.KEY_BACKGROUND] = id }
    }

    suspend fun updateCardBack(id: String) {
        context.dataStore.edit { it[PreferencesKeys.KEY_CARD_BACK] = id }
    }

    suspend fun updateCardFace(id: String) {
        context.dataStore.edit { it[PreferencesKeys.KEY_CARD_FACE] = id }
    }

    suspend fun updateAutoComplete(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.KEY_AUTO_COMPLETE] = enabled }
    }

    suspend fun updateNotifications(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.KEY_NOTIFICATIONS] = enabled }
    }

    suspend fun updateShowTimer(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.KEY_SHOW_TIMER] = enabled }
    }

    suspend fun saveGameJson(json: String?) {
        context.dataStore.edit { prefs ->
            if (json != null) {
                prefs[PreferencesKeys.KEY_SAVED_GAME] = json
            } else {
                prefs.remove(PreferencesKeys.KEY_SAVED_GAME)
            }
        }
    }
}
