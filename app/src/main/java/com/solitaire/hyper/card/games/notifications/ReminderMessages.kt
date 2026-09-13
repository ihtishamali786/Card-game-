package com.solitaire.hyper.card.games.notifications

import android.content.Context

data class ReminderMessage(
    val title: String,
    val body: String
)

object ReminderMessages {
    val messages = listOf(
        ReminderMessage(
            title = "🃏 Ready for a Winning Deal?",
            body = "Your Solitaire table is freshly shuffled! Jump in and clear the deck in under 3 minutes."
        ),
        ReminderMessage(
            title = "🪙 Bonus Coins & Lucky Spin Waiting!",
            body = "Your hourly rewards are ready to collect! Spin the wheel and unlock luxury card themes."
        ),
        ReminderMessage(
            title = "👑 Daily Challenge Awaits You!",
            body = "Can you master today's puzzle? Solve it now to earn your golden trophy crown."
        ),
        ReminderMessage(
            title = "⚡ Relax & Sharpen Your Mind!",
            body = "Take a quick 2-minute break with a smooth, relaxing game of Klondike Solitaire."
        ),
        ReminderMessage(
            title = "🔥 Keep Your Winning Streak Alive!",
            body = "You're on a roll! Come back to beat your best score and claim victory."
        ),
        ReminderMessage(
            title = "💎 New 3D Velvet Tables Ready!",
            body = "Emerald Royale & Obsidian Palace tables are open. Your lucky cards are dealt!"
        ),
        ReminderMessage(
            title = "🎯 Can You Clear the Cards?",
            body = "A fresh, guaranteed winnable deal is ready. Tap to test your skills now!"
        )
    )

    fun getNextMessage(context: Context): ReminderMessage {
        val index = NotificationScheduler.getNextMessageIndex(context, messages.size)
        return messages[index]
    }
}
