package com.solitaire.hyper.card.games.ui.table4p

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.solitaire.hyper.card.games.game.model.Card
import com.solitaire.hyper.card.games.game.model.Suit
import com.solitaire.hyper.card.games.game.table4p.engine.FourPlayerEngine
import com.solitaire.hyper.card.games.game.table4p.model.FourPlayerGameState
import com.solitaire.hyper.card.games.game.table4p.model.GamePhase
import com.solitaire.hyper.card.games.game.table4p.model.PlayedCard
import com.solitaire.hyper.card.games.game.table4p.model.PlayerPosition
import com.solitaire.hyper.card.games.sound.SoundManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FourPlayerGameViewModel(application: Application) : AndroidViewModel(application) {

    private val soundManager = SoundManager(application)
    private val repository = com.solitaire.hyper.card.games.data.GameRepository(application)
    private val _gameState = MutableStateFlow(FourPlayerEngine.dealNewRound())
    val gameState: StateFlow<FourPlayerGameState> = _gameState.asStateFlow()

    private var dealingJob: Job? = null
    private var aiTurnJob: Job? = null

    init {
        runDealingAnimation()
    }

    fun startNewGame() {
        startNewRoundInternal(round = 1, scores = emptyMap())
    }

    fun startNextRound() {
        val current = _gameState.value
        val nextRound = current.roundNumber + 1
        val scores = current.players.mapValues { it.value.totalScore }
        startNewRoundInternal(round = nextRound, scores = scores)
    }

    fun redealRound() {
        val current = _gameState.value
        val scores = current.players.mapValues { it.value.totalScore }
        startNewRoundInternal(round = current.roundNumber, scores = scores)
    }

    private fun startNewRoundInternal(round: Int, scores: Map<PlayerPosition, Double> = emptyMap()) {
        dealingJob?.cancel()
        aiTurnJob?.cancel()
        _gameState.value = FourPlayerEngine.dealNewRound(
            roundNumber = round,
            existingScores = scores,
            isFastSpeed = _gameState.value.isFastSpeed,
            trumpSuit = Suit.SPADES
        )
        runDealingAnimation()
    }

    /**
     * Sequential dealing animation: Deals 52 cards sequentially from the center deck,
     * one-by-one to South, West, North, East (13 cards to each player).
     */
    private fun runDealingAnimation() {
        dealingJob?.cancel()
        dealingJob = viewModelScope.launch {
            soundManager.playShuffle()
            val sequence = listOf(PlayerPosition.SOUTH, PlayerPosition.WEST, PlayerPosition.NORTH, PlayerPosition.EAST)
            val stepDelay = if (_gameState.value.isFastSpeed) 16L else 30L

            for (cardIndex in 1..52) {
                val targetPlayer = sequence[(cardIndex - 1) % 4]
                _gameState.value = _gameState.value.copy(
                    dealtCardsProgress = cardIndex,
                    dealingTo = targetPlayer,
                    message = "Dealing card $cardIndex of 52 to ${getPlayerName(targetPlayer)}..."
                )
                if (cardIndex % 2 == 0) {
                    soundManager.playCardDeal()
                }
                delay(stepDelay)
            }

            delay(120L)
            // Dealing finished! Move to Trump Selection phase
            _gameState.value = _gameState.value.copy(
                phase = GamePhase.TRUMP_SELECTION,
                dealtCardsProgress = 52,
                dealingTo = null,
                message = "🃏 13 cards dealt to each player! Confirm or select Trump Suit."
            )
        }
    }

    fun skipDealing() {
        dealingJob?.cancel()
        _gameState.value = _gameState.value.copy(
            phase = GamePhase.TRUMP_SELECTION,
            dealtCardsProgress = 52,
            dealingTo = null,
            message = "🃏 13 cards dealt to each player! Confirm or select Trump Suit."
        )
    }

    /**
     * Confirms or selects the round's Trump Suit (Spades ♠ is Call Break classic).
     * Re-sorts all hands so the trump suit is grouped first and estimates bids.
     */
    fun selectTrumpSuit(suit: Suit) {
        val state = _gameState.value
        if (state.phase != GamePhase.TRUMP_SELECTION && state.phase != GamePhase.BIDDING) return

        val sortedPlayers = state.players.mapValues { (_, playerState) ->
            val sortedHand = FourPlayerEngine.sortHand(playerState.hand, suit)
            val estimatedBid = FourPlayerEngine.calculateEstimatedBid(sortedHand, suit)
            playerState.copy(
                hand = sortedHand,
                bid = estimatedBid
            )
        }

        soundManager.playTrumpSelected()
        val totalBids = sortedPlayers.values.sumOf { it.bid }

        _gameState.value = state.copy(
            trumpSuit = suit,
            players = sortedPlayers,
            phase = GamePhase.BIDDING,
            totalTableBids = totalBids,
            message = "${suit.symbol} ${suit.displayName} Trump selected! Make your Call (Bid 1 to 8 tricks)."
        )
    }

    /**
     * South player submits their bid/call (1 to 8).
     * Bids for all players are finalized and gameplay begins!
     */
    fun setUserBid(bid: Int) {
        val state = _gameState.value
        if (state.phase != GamePhase.BIDDING) return

        val southPlayer = state.players[PlayerPosition.SOUTH] ?: return
        val updatedPlayers = state.players.toMutableMap()
        updatedPlayers[PlayerPosition.SOUTH] = southPlayer.copy(bid = bid, hasBid = true)

        soundManager.playCardMove()
        val totalBids = updatedPlayers.values.sumOf { it.bid }

        _gameState.value = state.copy(
            players = updatedPlayers,
            phase = GamePhase.PLAYING,
            currentTurn = PlayerPosition.SOUTH,
            totalTableBids = totalBids,
            message = "Round ${state.roundNumber}: You called $bid tricks (${state.trumpSuit.symbol} Trump)! Lead your first card."
        )
    }

    fun toggleSpeed() {
        _gameState.value = _gameState.value.copy(
            isFastSpeed = !_gameState.value.isFastSpeed
        )
    }

    private fun getPlayerName(pos: PlayerPosition): String {
        return _gameState.value.players[pos]?.name ?: pos.displayName
    }

    fun onUserPlayCard(card: Card) {
        val state = _gameState.value
        if (state.phase != GamePhase.PLAYING || state.currentTurn != PlayerPosition.SOUTH) return

        val southPlayer = state.players[PlayerPosition.SOUTH] ?: return
        val isLegal = FourPlayerEngine.isLegalPlay(card, southPlayer.hand, state.leadSuit)
        if (!isLegal) {
            soundManager.playInvalidMove()
            val leadSuitName = state.leadSuit?.displayName ?: "lead"
            _gameState.value = state.copy(message = "⚠️ Must follow lead suit: $leadSuitName")
            return
        }

        executePlay(PlayerPosition.SOUTH, card)
    }

    private fun executePlay(player: PlayerPosition, card: Card) {
        val state = _gameState.value
        val playerState = state.players[player] ?: return
        val newHand = playerState.hand.filter { it.id != card.id }
        val updatedPlayers = state.players.toMutableMap()
        updatedPlayers[player] = playerState.copy(hand = newHand)

        val newTrick = state.currentTrick + PlayedCard(player, card)
        val newLeadSuit = state.leadSuit ?: card.suit
        soundManager.playCardFlip()

        if (newTrick.size == 4) {
            // Trick is complete! Resolve winner
            val winner = FourPlayerEngine.evaluateTrickWinner(newTrick, state.trumpSuit)
            val winnerName = updatedPlayers[winner]?.name ?: winner.displayName
            _gameState.value = state.copy(
                players = updatedPlayers,
                currentTrick = newTrick,
                leadSuit = newLeadSuit,
                phase = GamePhase.TRICK_RESOLVING,
                lastTrickWinner = winner,
                message = "🏆 $winnerName won the trick!"
            )
            viewModelScope.launch {
                val delayTime = if (state.isFastSpeed) 450L else 800L
                delay(delayTime)
                resolveCompletedTrick(winner)
            }
        } else {
            val nextTurn = player.nextClockwise()
            val nextName = updatedPlayers[nextTurn]?.name ?: nextTurn.displayName
            val msg = if (nextTurn == PlayerPosition.SOUTH) "Your turn to play!" else "$nextName is thinking..."
            _gameState.value = state.copy(
                players = updatedPlayers,
                currentTrick = newTrick,
                leadSuit = newLeadSuit,
                currentTurn = nextTurn,
                message = msg
            )
            triggerAiTurnIfNeeded()
        }
    }

    private fun resolveCompletedTrick(winner: PlayerPosition) {
        val state = _gameState.value
        val winnerPlayer = state.players[winner] ?: return
        val updatedPlayers = state.players.toMutableMap()
        updatedPlayers[winner] = winnerPlayer.copy(tricksWon = winnerPlayer.tricksWon + 1)

        soundManager.playTrickWin()

        val nextTrickNum = state.trickNumber + 1
        if (nextTrickNum > 13) {
            // Round complete! Calculate round scores
            soundManager.playVictory()
            val scoredPlayers = FourPlayerEngine.calculateRoundScores(updatedPlayers)
            val southPlayer = scoredPlayers[PlayerPosition.SOUTH]
            val isWin = southPlayer != null && southPlayer.tricksWon >= southPlayer.bid
            viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
                repository.recordCustomGame(
                    mode = "CALL_BREAK",
                    isWin = isWin,
                    moves = southPlayer?.tricksWon ?: 0,
                    timeSeconds = 90L,
                    score = ((southPlayer?.roundScoreDelta ?: 0.0) * 100).toInt()
                )
            }
            _gameState.value = state.copy(
                trickNumber = 13,
                players = scoredPlayers,
                currentTrick = emptyList(),
                leadSuit = null,
                phase = GamePhase.ROUND_OVER,
                message = if (isWin) "🎉 Bid Success! Won ${southPlayer?.tricksWon} tricks." else "💔 Bid Failed. Won ${southPlayer?.tricksWon}/${southPlayer?.bid} tricks."
            )
        } else {
            // Start next trick led by trick winner
            val winnerName = updatedPlayers[winner]?.name ?: winner.displayName
            val msg = if (winner == PlayerPosition.SOUTH) "You won the trick! Lead next card." else "$winnerName won and leads next."
            _gameState.value = state.copy(
                trickNumber = nextTrickNum,
                players = updatedPlayers,
                currentTrick = emptyList(),
                leadSuit = null,
                currentTurn = winner,
                phase = GamePhase.PLAYING,
                message = msg
            )
            triggerAiTurnIfNeeded()
        }
    }

    private fun triggerAiTurnIfNeeded() {
        val state = _gameState.value
        if (state.phase != GamePhase.PLAYING) return
        val turn = state.currentTurn
        if (turn == PlayerPosition.SOUTH) return

        aiTurnJob?.cancel()
        aiTurnJob = viewModelScope.launch {
            val delayTime = if (state.isFastSpeed) 300L else 550L
            delay(delayTime)
            val currentState = _gameState.value
            if (currentState.phase == GamePhase.PLAYING && currentState.currentTurn == turn) {
                val aiPlayer = currentState.players[turn]
                if (aiPlayer != null && aiPlayer.hand.isNotEmpty()) {
                    val cardToPlay = FourPlayerEngine.chooseAiCard(
                        hand = aiPlayer.hand,
                        currentTrick = currentState.currentTrick,
                        trumpSuit = currentState.trumpSuit
                    )
                    executePlay(turn, cardToPlay)
                }
            }
        }
    }
}
