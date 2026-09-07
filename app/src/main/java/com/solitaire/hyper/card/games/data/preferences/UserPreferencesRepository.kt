package com.solitaire.hyper.card.games.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_settings")

data class UserSettings(
    val soundEnabled: Boolean = true,
    val musicEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val leftHandedMode: Boolean = false,
    val largePrintMode: Boolean = false,
    val vegasScoring: Boolean = false,
    val defaultDrawMode: String = "DRAW_1",
    val backgroundId: String = "CLASSIC_FELT",
    val cardBackId: String = "ROYAL_CREST",
    val cardFaceId: String = "CLASSIC",
    val autoCompleteEnabled: Boolean = true,
    val notificationsEnabled: Boolean = true,
    val showTimer: Boolean = true,
    val savedGameJson: String? = null,
    val coins: Int = 500,
    val unlockedItems: String = "",
    val adFreeUntilTimestamp: Long = 0L,
    val vipUntilTimestamp: Long = 0L,
    val rewardedAdsWatchedForVip: Int = 0
) {
    fun isAdFreeActive(): Boolean = System.currentTimeMillis() < adFreeUntilTimestamp
    fun isVipActive(): Boolean = System.currentTimeMillis() < vipUntilTimestamp

    fun isItemUnlocked(itemId: String): Boolean {
        if (isVipActive()) return true
        val set = unlockedItems.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        return set.contains(itemId)
    }

    fun getAdFreeRemainingMinutes(): Int {
        val diff = adFreeUntilTimestamp - System.currentTimeMillis()
        return if (diff > 0) ((diff + 59999) / 60000).toInt() else 0
    }

    fun getVipRemainingMinutes(): Int {
        val diff = vipUntilTimestamp - System.currentTimeMillis()
        return if (diff > 0) ((diff + 59999) / 60000).toInt() else 0
    }
}

class UserPreferencesRepository(private val context: Context) {

    private object PreferencesKeys {
        val KEY_SOUND = booleanPreferencesKey("sound_enabled")
        val KEY_MUSIC = booleanPreferencesKey("music_enabled")
        val KEY_VIBRATION = booleanPreferencesKey("vibration_enabled")
        val KEY_LEFT_HANDED = booleanPreferencesKey("left_handed_mode")
        val KEY_LARGE_PRINT = booleanPreferencesKey("large_print_mode")
        val KEY_VEGAS_SCORING = booleanPreferencesKey("vegas_scoring")
        val KEY_DRAW_MODE = stringPreferencesKey("default_draw_mode")
        val KEY_BACKGROUND = stringPreferencesKey("background_id")
        val KEY_CARD_BACK = stringPreferencesKey("card_back_id")
        val KEY_CARD_FACE = stringPreferencesKey("card_face_id")
        val KEY_AUTO_COMPLETE = booleanPreferencesKey("auto_complete_enabled")
        val KEY_NOTIFICATIONS = booleanPreferencesKey("notifications_enabled")
        val KEY_SHOW_TIMER = booleanPreferencesKey("show_timer")
        val KEY_SAVED_GAME = stringPreferencesKey("saved_game_json")
        val KEY_COINS = intPreferencesKey("coins_balance")
        val KEY_UNLOCKED_ITEMS = stringPreferencesKey("unlocked_items_csv")
        val KEY_AD_FREE_UNTIL = longPreferencesKey("ad_free_until_timestamp")
        val KEY_VIP_UNTIL = longPreferencesKey("vip_until_timestamp")
        val KEY_REWARDED_ADS_WATCHED_VIP = intPreferencesKey("rewarded_ads_watched_vip")
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
                largePrintMode = prefs[PreferencesKeys.KEY_LARGE_PRINT] ?: false,
                vegasScoring = prefs[PreferencesKeys.KEY_VEGAS_SCORING] ?: false,
                defaultDrawMode = prefs[PreferencesKeys.KEY_DRAW_MODE] ?: "DRAW_1",
                backgroundId = prefs[PreferencesKeys.KEY_BACKGROUND] ?: "CLASSIC_FELT",
                cardBackId = prefs[PreferencesKeys.KEY_CARD_BACK] ?: "ROYAL_CREST",
                cardFaceId = prefs[PreferencesKeys.KEY_CARD_FACE] ?: "CLASSIC",
                autoCompleteEnabled = prefs[PreferencesKeys.KEY_AUTO_COMPLETE] ?: true,
                notificationsEnabled = prefs[PreferencesKeys.KEY_NOTIFICATIONS] ?: true,
                showTimer = prefs[PreferencesKeys.KEY_SHOW_TIMER] ?: true,
                savedGameJson = prefs[PreferencesKeys.KEY_SAVED_GAME],
                coins = prefs[PreferencesKeys.KEY_COINS] ?: 500,
                unlockedItems = prefs[PreferencesKeys.KEY_UNLOCKED_ITEMS] ?: "",
                adFreeUntilTimestamp = prefs[PreferencesKeys.KEY_AD_FREE_UNTIL] ?: 0L,
                vipUntilTimestamp = prefs[PreferencesKeys.KEY_VIP_UNTIL] ?: 0L,
                rewardedAdsWatchedForVip = prefs[PreferencesKeys.KEY_REWARDED_ADS_WATCHED_VIP] ?: 0
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

    suspend fun updateLargePrint(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.KEY_LARGE_PRINT] = enabled }
    }

    suspend fun updateVegasScoring(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.KEY_VEGAS_SCORING] = enabled }
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

    suspend fun addCoins(amount: Int) {
        if (amount <= 0) return
        context.dataStore.edit { prefs ->
            val current = prefs[PreferencesKeys.KEY_COINS] ?: 500
            prefs[PreferencesKeys.KEY_COINS] = current + amount
        }
    }

    suspend fun spendCoins(amount: Int): Boolean {
        if (amount <= 0) return true
        var success = false
        context.dataStore.edit { prefs ->
            val current = prefs[PreferencesKeys.KEY_COINS] ?: 500
            if (current >= amount) {
                prefs[PreferencesKeys.KEY_COINS] = current - amount
                success = true
            }
        }
        return success
    }

    suspend fun unlockItem(itemId: String, cost: Int): Boolean {
        var success = false
        context.dataStore.edit { prefs ->
            val current = prefs[PreferencesKeys.KEY_COINS] ?: 500
            if (current >= cost) {
                prefs[PreferencesKeys.KEY_COINS] = current - cost
                val unlocked = prefs[PreferencesKeys.KEY_UNLOCKED_ITEMS] ?: ""
                val list = unlocked.split(",").map { it.trim() }.filter { it.isNotEmpty() }.toMutableSet()
                list.add(itemId)
                prefs[PreferencesKeys.KEY_UNLOCKED_ITEMS] = list.joinToString(",")
                success = true
            }
        }
        return success
    }

    /**
     * Deducts 1,000 coins and disables ads for 1 hour (3600 seconds)
     */
    suspend fun activateAdFreeOneHour(): Boolean {
        var success = false
        context.dataStore.edit { prefs ->
            val current = prefs[PreferencesKeys.KEY_COINS] ?: 500
            if (current >= 1000) {
                prefs[PreferencesKeys.KEY_COINS] = current - 1000
                val now = System.currentTimeMillis()
                val currentUntil = prefs[PreferencesKeys.KEY_AD_FREE_UNTIL] ?: 0L
                val base = if (currentUntil > now) currentUntil else now
                prefs[PreferencesKeys.KEY_AD_FREE_UNTIL] = base + 3600 * 1000L
                success = true
            }
        }
        return success
    }

    /**
     * Activates 1 hour VIP access (unlocks all 3D & premium themes for 1 hour)
     */
    suspend fun activateVipOneHour() {
        context.dataStore.edit { prefs ->
            val now = System.currentTimeMillis()
            val currentUntil = prefs[PreferencesKeys.KEY_VIP_UNTIL] ?: 0L
            val base = if (currentUntil > now) currentUntil else now
            prefs[PreferencesKeys.KEY_VIP_UNTIL] = base + 3600 * 1000L
            prefs[PreferencesKeys.KEY_REWARDED_ADS_WATCHED_VIP] = 0
        }
    }

    /**
     * Records a rewarded ad watched towards the 5-ads VIP pass.
     * Returns true if 5 ads threshold reached and VIP pass was activated!
     */
    suspend fun recordRewardedAdForVip(): Boolean {
        var vipUnlocked = false
        context.dataStore.edit { prefs ->
            val current = prefs[PreferencesKeys.KEY_REWARDED_ADS_WATCHED_VIP] ?: 0
            val next = current + 1
            if (next >= 5) {
                val now = System.currentTimeMillis()
                val currentUntil = prefs[PreferencesKeys.KEY_VIP_UNTIL] ?: 0L
                val base = if (currentUntil > now) currentUntil else now
                prefs[PreferencesKeys.KEY_VIP_UNTIL] = base + 3600 * 1000L
                prefs[PreferencesKeys.KEY_REWARDED_ADS_WATCHED_VIP] = 0
                vipUnlocked = true
            } else {
                prefs[PreferencesKeys.KEY_REWARDED_ADS_WATCHED_VIP] = next
            }
        }
        return vipUnlocked
    }
}
