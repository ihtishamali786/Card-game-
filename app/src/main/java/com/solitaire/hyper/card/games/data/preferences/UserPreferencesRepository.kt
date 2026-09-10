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
    val cardBackId: String = "BACK_CRIMSON_ANVIL",
    val cardFaceId: String = "FACE_CRIMSON_ANVIL",
    val autoCompleteEnabled: Boolean = true,
    val notificationsEnabled: Boolean = true,
    val showTimer: Boolean = true,
    val savedGameJson: String? = null,
    val coins: Int = 500,
    val unlockedItems: String = "",
    val adFreeUntilTimestamp: Long = 0L,
    val vipUntilTimestamp: Long = 0L,
    val rewardedAdsWatchedForVip: Int = 0,
    val adFreeTokens: Int = 3,
    val magicWands: Int = 5,
    val aiTokens: Int = 10,
    val isVipPermanent: Boolean = false,
    val rewardedAdsWatchedTotal: Int = 0
) {
    fun isVipActive(): Boolean = isVipPermanent || System.currentTimeMillis() < vipUntilTimestamp
    fun isAdFreeActive(): Boolean = isVipActive() || System.currentTimeMillis() < adFreeUntilTimestamp

    fun isItemUnlocked(itemId: String): Boolean {
        if (isVipActive()) return true
        val set = unlockedItems.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        return set.contains(itemId)
    }

    fun getAdFreeRemainingMinutes(): Int {
        if (isVipActive()) return 9999
        val diff = adFreeUntilTimestamp - System.currentTimeMillis()
        return if (diff > 0) ((diff + 59999) / 60000).toInt() else 0
    }

    fun getVipRemainingMinutes(): Int {
        if (isVipPermanent) return 9999
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
        val KEY_AD_FREE_TOKENS = intPreferencesKey("ad_free_tokens_balance")
        val KEY_MAGIC_WANDS = intPreferencesKey("magic_wands_inventory")
        val KEY_AI_TOKENS = intPreferencesKey("ai_tokens_inventory")
        val KEY_VIP_PERMANENT = booleanPreferencesKey("vip_permanent_unlocked")
        val KEY_REWARDED_ADS_TOTAL = intPreferencesKey("rewarded_ads_total_watched")
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
                cardBackId = prefs[PreferencesKeys.KEY_CARD_BACK] ?: "BACK_CRIMSON_ANVIL",
                cardFaceId = prefs[PreferencesKeys.KEY_CARD_FACE] ?: "FACE_CRIMSON_ANVIL",
                autoCompleteEnabled = prefs[PreferencesKeys.KEY_AUTO_COMPLETE] ?: true,
                notificationsEnabled = prefs[PreferencesKeys.KEY_NOTIFICATIONS] ?: true,
                showTimer = prefs[PreferencesKeys.KEY_SHOW_TIMER] ?: true,
                savedGameJson = prefs[PreferencesKeys.KEY_SAVED_GAME],
                coins = prefs[PreferencesKeys.KEY_COINS] ?: 500,
                unlockedItems = prefs[PreferencesKeys.KEY_UNLOCKED_ITEMS] ?: "",
                adFreeUntilTimestamp = prefs[PreferencesKeys.KEY_AD_FREE_UNTIL] ?: 0L,
                vipUntilTimestamp = prefs[PreferencesKeys.KEY_VIP_UNTIL] ?: 0L,
                rewardedAdsWatchedForVip = prefs[PreferencesKeys.KEY_REWARDED_ADS_WATCHED_VIP] ?: 0,
                adFreeTokens = prefs[PreferencesKeys.KEY_AD_FREE_TOKENS] ?: 3,
                magicWands = prefs[PreferencesKeys.KEY_MAGIC_WANDS] ?: 5,
                aiTokens = prefs[PreferencesKeys.KEY_AI_TOKENS] ?: 10,
                isVipPermanent = prefs[PreferencesKeys.KEY_VIP_PERMANENT] ?: false,
                rewardedAdsWatchedTotal = prefs[PreferencesKeys.KEY_REWARDED_ADS_TOTAL] ?: 0
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

    /**
     * Consumes 1 Ad-Free Token from inventory and extends ad-free status by 30 minutes.
     * Returns true if a token was successfully used.
     */
    suspend fun useAdFreeToken(): Boolean {
        var used = false
        context.dataStore.edit { prefs ->
            val tokens = prefs[PreferencesKeys.KEY_AD_FREE_TOKENS] ?: 3
            if (tokens > 0) {
                prefs[PreferencesKeys.KEY_AD_FREE_TOKENS] = tokens - 1
                val now = System.currentTimeMillis()
                val currentUntil = prefs[PreferencesKeys.KEY_AD_FREE_UNTIL] ?: 0L
                val base = if (currentUntil > now) currentUntil else now
                prefs[PreferencesKeys.KEY_AD_FREE_UNTIL] = base + 30 * 60 * 1000L // 30 mins
                used = true
            }
        }
        return used
    }

    /**
     * Adds ad-free tokens to the user's inventory.
     */
    suspend fun addAdFreeTokens(count: Int) {
        if (count <= 0) return
        context.dataStore.edit { prefs ->
            val current = prefs[PreferencesKeys.KEY_AD_FREE_TOKENS] ?: 3
            prefs[PreferencesKeys.KEY_AD_FREE_TOKENS] = current + count
        }
    }

    /**
     * Buys ad-free tokens using in-game coins.
     */
    suspend fun buyAdFreeTokens(tokenCount: Int, coinCost: Int): Boolean {
        var success = false
        context.dataStore.edit { prefs ->
            val currentCoins = prefs[PreferencesKeys.KEY_COINS] ?: 500
            if (currentCoins >= coinCost) {
                prefs[PreferencesKeys.KEY_COINS] = currentCoins - coinCost
                val currentTokens = prefs[PreferencesKeys.KEY_AD_FREE_TOKENS] ?: 3
                prefs[PreferencesKeys.KEY_AD_FREE_TOKENS] = currentTokens + tokenCount
                success = true
            }
        }
        return success
    }

    /**
     * Directly activates Ad-Free for given hours using coins.
     */
    suspend fun activateAdFreePass(hours: Int, coinCost: Int): Boolean {
        var success = false
        context.dataStore.edit { prefs ->
            val currentCoins = prefs[PreferencesKeys.KEY_COINS] ?: 500
            if (currentCoins >= coinCost) {
                prefs[PreferencesKeys.KEY_COINS] = currentCoins - coinCost
                val now = System.currentTimeMillis()
                val currentUntil = prefs[PreferencesKeys.KEY_AD_FREE_UNTIL] ?: 0L
                val base = if (currentUntil > now) currentUntil else now
                prefs[PreferencesKeys.KEY_AD_FREE_UNTIL] = base + hours * 3600 * 1000L
                success = true
            }
        }
        return success
    }

    /**
     * Activates 24-Hour VIP Access using coins.
     */
    suspend fun activateVipDayPass(coinCost: Int): Boolean {
        var success = false
        context.dataStore.edit { prefs ->
            val currentCoins = prefs[PreferencesKeys.KEY_COINS] ?: 500
            if (currentCoins >= coinCost) {
                prefs[PreferencesKeys.KEY_COINS] = currentCoins - coinCost
                val now = System.currentTimeMillis()
                val currentUntil = prefs[PreferencesKeys.KEY_VIP_UNTIL] ?: 0L
                val base = if (currentUntil > now) currentUntil else now
                prefs[PreferencesKeys.KEY_VIP_UNTIL] = base + 24 * 3600 * 1000L
                success = true
            }
        }
        return success
    }

    /**
     * Permanently unlocks VIP Crown tier (no ads, all themes, unlimited perks).
     */
    suspend fun activatePermanentVip(coinCost: Int): Boolean {
        var success = false
        context.dataStore.edit { prefs ->
            val currentCoins = prefs[PreferencesKeys.KEY_COINS] ?: 500
            if (currentCoins >= coinCost) {
                prefs[PreferencesKeys.KEY_COINS] = currentCoins - coinCost
                prefs[PreferencesKeys.KEY_VIP_PERMANENT] = true
                success = true
            }
        }
        return success
    }

    /**
     * Consumes 1 magic wand or returns true if VIP
     */
    suspend fun useMagicWand(isVip: Boolean = false): Boolean {
        if (isVip) return true
        var used = false
        context.dataStore.edit { prefs ->
            val wands = prefs[PreferencesKeys.KEY_MAGIC_WANDS] ?: 5
            if (wands > 0) {
                prefs[PreferencesKeys.KEY_MAGIC_WANDS] = wands - 1
                used = true
            }
        }
        return used
    }

    suspend fun addMagicWands(count: Int) {
        if (count <= 0) return
        context.dataStore.edit { prefs ->
            val current = prefs[PreferencesKeys.KEY_MAGIC_WANDS] ?: 5
            prefs[PreferencesKeys.KEY_MAGIC_WANDS] = current + count
        }
    }

    suspend fun buyMagicWands(count: Int, coinCost: Int): Boolean {
        var success = false
        context.dataStore.edit { prefs ->
            val currentCoins = prefs[PreferencesKeys.KEY_COINS] ?: 500
            if (currentCoins >= coinCost) {
                prefs[PreferencesKeys.KEY_COINS] = currentCoins - coinCost
                val current = prefs[PreferencesKeys.KEY_MAGIC_WANDS] ?: 5
                prefs[PreferencesKeys.KEY_MAGIC_WANDS] = current + count
                success = true
            }
        }
        return success
    }

    /**
     * Consumes 1 AI analysis token or returns true if VIP
     */
    suspend fun useAiToken(isVip: Boolean = false): Boolean {
        if (isVip) return true
        var used = false
        context.dataStore.edit { prefs ->
            val tokens = prefs[PreferencesKeys.KEY_AI_TOKENS] ?: 10
            if (tokens > 0) {
                prefs[PreferencesKeys.KEY_AI_TOKENS] = tokens - 1
                used = true
            }
        }
        return used
    }

    suspend fun addAiTokens(count: Int) {
        if (count <= 0) return
        context.dataStore.edit { prefs ->
            val current = prefs[PreferencesKeys.KEY_AI_TOKENS] ?: 10
            prefs[PreferencesKeys.KEY_AI_TOKENS] = current + count
        }
    }

    suspend fun buyAiTokens(count: Int, coinCost: Int): Boolean {
        var success = false
        context.dataStore.edit { prefs ->
            val currentCoins = prefs[PreferencesKeys.KEY_COINS] ?: 500
            if (currentCoins >= coinCost) {
                prefs[PreferencesKeys.KEY_COINS] = currentCoins - coinCost
                val current = prefs[PreferencesKeys.KEY_AI_TOKENS] ?: 10
                prefs[PreferencesKeys.KEY_AI_TOKENS] = current + count
                success = true
            }
        }
        return success
    }

    /**
     * Increments general rewarded ad count
     */
    suspend fun incrementRewardedAdsWatched() {
        context.dataStore.edit { prefs ->
            val current = prefs[PreferencesKeys.KEY_REWARDED_ADS_TOTAL] ?: 0
            prefs[PreferencesKeys.KEY_REWARDED_ADS_TOTAL] = current + 1
        }
    }
}
