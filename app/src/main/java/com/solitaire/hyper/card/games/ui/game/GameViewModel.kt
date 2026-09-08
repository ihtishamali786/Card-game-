package com.solitaire.hyper.card.games.ui.game

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.solitaire.hyper.card.games.data.GameRepository
import com.solitaire.hyper.card.games.data.GameStateSerializer
import com.solitaire.hyper.card.games.data.preferences.UserPreferencesRepository
import com.solitaire.hyper.card.games.data.preferences.UserSettings
import com.solitaire.hyper.card.games.game.engine.SolitaireEngine
import com.solitaire.hyper.card.games.game.model.Card
import com.solitaire.hyper.card.games.game.model.CardLocation
import com.solitaire.hyper.card.games.game.model.GameMode
import com.solitaire.hyper.card.games.game.model.GameMove
import com.solitaire.hyper.card.games.game.model.GameState
import com.solitaire.hyper.card.games.game.model.Hint
import com.solitaire.hyper.card.games.sound.SoundManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class GameViewModel(application: Application) : AndroidViewModel(application) {

    val userPrefs = UserPreferencesRepository(application)
    private val repository = GameRepository(application)
    val soundManager = SoundManager(application)

    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    private val _userSettings = MutableStateFlow(UserSettings())
    val userSettings: StateFlow<UserSettings> = _userSettings.asStateFlow()

    private val _selectedLocation = MutableStateFlow<CardLocation?>(null)
    val selectedLocation: StateFlow<CardLocation?> = _selectedLocation.asStateFlow()

    private val _activeHint = MutableStateFlow<Hint?>(null)
    val activeHint: StateFlow<Hint?> = _activeHint.asStateFlow()

    private val _isAutoCompleting = MutableStateFlow(false)
    val isAutoCompleting: StateFlow<Boolean> = _isAutoCompleting.asStateFlow()

    private val _showWinDialog = MutableStateFlow(false)
    val showWinDialog: StateFlow<Boolean> = _showWinDialog.asStateFlow()

    private val _validTargets = MutableStateFlow<Set<CardLocation>>(emptySet())
    val validTargets: StateFlow<Set<CardLocation>> = _validTargets.asStateFlow()

    private val _statusMessage = MutableStateFlow<String?>("Tap any face-up card to select")
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    private val _aiAnalysis = MutableStateFlow<com.solitaire.hyper.card.games.ai.AiAnalysisResult?>(null)
    val aiAnalysis: StateFlow<com.solitaire.hyper.card.games.ai.AiAnalysisResult?> = _aiAnalysis.asStateFlow()

    private val _isAiAnalyzing = MutableStateFlow(false)
    val isAiAnalyzing: StateFlow<Boolean> = _isAiAnalyzing.asStateFlow()

    private val undoStack = mutableListOf<GameMove>()
    private var timerJob: Job? = null
    private var usedHints = false

    init {
        viewModelScope.launch {
            userPrefs.userSettingsFlow.collectLatest { settings ->
                _userSettings.value = settings
                soundManager.soundEnabled = settings.soundEnabled
                soundManager.vibrationEnabled = settings.vibrationEnabled
            }
        }
    }

    fun startNewGame(
        mode: GameMode = GameMode.fromString(_userSettings.value.defaultDrawMode),
        isDailyChallenge: Boolean = false,
        challengeDate: String? = null,
        isWinnableDeal: Boolean = false,
        isVegasScoring: Boolean = _userSettings.value.vegasScoring
    ) {
        timerJob?.cancel()
        undoStack.clear()
        _selectedLocation.value = null
        _validTargets.value = emptySet()
        _statusMessage.value = "Tap or drag card to play"
        _activeHint.value = null
        _isAutoCompleting.value = false
        _showWinDialog.value = false
        usedHints = false

        val seed = if (isDailyChallenge && challengeDate != null) {
            SolitaireEngine.seedForDate(challengeDate)
        } else {
            System.currentTimeMillis()
        }

        val freshState = SolitaireEngine.newGame(
            gameMode = mode,
            seed = seed,
            isDailyChallenge = isDailyChallenge,
            challengeDate = challengeDate,
            isWinnableDeal = isWinnableDeal,
            isVegasScoring = isVegasScoring
        )

        _gameState.value = freshState
        saveActiveGame()
        startTimer()
        soundManager.playShuffle()
    }

    fun restoreSavedGameOrNew(defaultMode: GameMode = GameMode.DRAW_1) {
        viewModelScope.launch {
            val savedJson = _userSettings.value.savedGameJson
            val restored = GameStateSerializer.deserialize(savedJson)
            if (restored != null && !restored.isGameWon) {
                undoStack.clear()
                _selectedLocation.value = null
                _validTargets.value = emptySet()
                _statusMessage.value = "Tap any face-up card to select"
                _activeHint.value = null
                _isAutoCompleting.value = false
                _showWinDialog.value = false
                _gameState.value = restored
                startTimer()
            } else {
                startNewGame(mode = defaultMode)
            }
        }
    }

    fun onStockClicked() {
        if (_gameState.value.isGameWon || _isAutoCompleting.value) return
        _selectedLocation.value = null
        _validTargets.value = emptySet()
        _activeHint.value = null

        val result = SolitaireEngine.drawCards(_gameState.value)
        if (result != null) {
            _gameState.value = result.first
            undoStack.add(result.second)
            _statusMessage.value = "Drew card from stock"
            soundManager.playCardFlip()
            saveActiveGame()
        } else {
            soundManager.playInvalidMove()
        }
    }

    fun getCardAtLocation(location: CardLocation): Card? {
        val state = _gameState.value
        return when (location) {
            is CardLocation.Waste -> state.waste.lastOrNull()
            is CardLocation.Foundation -> state.foundations.getOrNull(location.index)?.lastOrNull()
            is CardLocation.Tableau -> {
                val col = state.tableau.getOrNull(location.columnIndex) ?: return null
                val idx = if (location.cardIndex >= 0) location.cardIndex else col.lastIndex
                col.getOrNull(idx)
            }
            else -> null
        }
    }

    fun calculateValidTargets(location: CardLocation): Set<CardLocation> {
        val card = getCardAtLocation(location) ?: return emptySet()
        val targets = mutableSetOf<CardLocation>()
        val state = _gameState.value

        // Check Foundation targets (only single cards can be matched to foundation)
        val isSingleCard = when (location) {
            is CardLocation.Waste -> true
            is CardLocation.Foundation -> true
            is CardLocation.Tableau -> {
                val col = state.tableau.getOrNull(location.columnIndex)
                col != null && location.cardIndex == col.lastIndex
            }
            else -> false
        }

        if (isSingleCard) {
            for (fIdx in 0 until 4) {
                if (SolitaireEngine.canMoveToFoundation(card, state.foundations[fIdx])) {
                    targets.add(CardLocation.Foundation(fIdx))
                }
            }
        }

        // Check Tableau column targets
        for (colIdx in 0 until 7) {
            if (location is CardLocation.Tableau && location.columnIndex == colIdx) continue
            val targetCol = state.tableau.getOrNull(colIdx) ?: continue
            if (SolitaireEngine.canMoveToTableau(card, targetCol)) {
                targets.add(CardLocation.Tableau(colIdx))
            }
        }

        return targets
    }

    fun onCardClicked(location: CardLocation) {
        if (_gameState.value.isGameWon || _isAutoCompleting.value) return
        _activeHint.value = null

        val currentSelected = _selectedLocation.value
        if (currentSelected == null) {
            if (isLocationSelectable(location)) {
                // First try direct 1-tap auto move for seamless easy play
                val quickMove = SolitaireEngine.tapToMove(_gameState.value, location)
                if (quickMove != null) {
                    val card = getCardAtLocation(location)
                    val cardName = if (card != null) "${card.rank.display}${card.suit.symbol}" else "Card"
                    _gameState.value = quickMove.first
                    undoStack.add(quickMove.second)
                    _selectedLocation.value = null
                    _validTargets.value = emptySet()
                    _statusMessage.value = "✓ Placed $cardName"
                    soundManager.playCardMove()
                    checkWinOrSave()
                } else {
                    // No quick move available - select and highlight valid targets
                    _selectedLocation.value = location
                    val targets = calculateValidTargets(location)
                    _validTargets.value = targets
                    val card = getCardAtLocation(location)
                    val cardName = if (card != null) "${card.rank.display}${card.suit.symbol}" else "Card"
                    _statusMessage.value = if (targets.isNotEmpty()) {
                        "Selected $cardName — Tap glowing match slot or column"
                    } else {
                        "Selected $cardName (No moves available)"
                    }
                    soundManager.playCardMove()
                }
            } else {
                soundManager.playInvalidMove()
            }
        } else {
            if (currentSelected == location) {
                // Tapped same card again: deselect
                _selectedLocation.value = null
                _validTargets.value = emptySet()
                _statusMessage.value = "Deselected"
            } else {
                // Attempt to move from currentSelected to this location
                val moveResult = SolitaireEngine.moveCards(_gameState.value, currentSelected, location)
                if (moveResult != null) {
                    val movedCard = getCardAtLocation(currentSelected)
                    val cardName = if (movedCard != null) "${movedCard.rank.display}${movedCard.suit.symbol}" else "Card"
                    _gameState.value = moveResult.first
                    undoStack.add(moveResult.second)
                    _selectedLocation.value = null
                    _validTargets.value = emptySet()
                    _statusMessage.value = "✓ Moved $cardName"
                    soundManager.playCardMove()
                    checkWinOrSave()
                } else {
                    // Could not move selected card here; check if user wants to play or select the new card
                    if (isLocationSelectable(location)) {
                        val quickMove = SolitaireEngine.tapToMove(_gameState.value, location)
                        if (quickMove != null) {
                            val card = getCardAtLocation(location)
                            val cardName = if (card != null) "${card.rank.display}${card.suit.symbol}" else "Card"
                            _gameState.value = quickMove.first
                            undoStack.add(quickMove.second)
                            _selectedLocation.value = null
                            _validTargets.value = emptySet()
                            _statusMessage.value = "✓ Placed $cardName"
                            soundManager.playCardMove()
                            checkWinOrSave()
                        } else {
                            _selectedLocation.value = location
                            val targets = calculateValidTargets(location)
                            _validTargets.value = targets
                            val newCard = getCardAtLocation(location)
                            val cardName = if (newCard != null) "${newCard.rank.display}${newCard.suit.symbol}" else "Card"
                            _statusMessage.value = if (targets.isNotEmpty()) {
                                "Selected $cardName — Tap glowing target to place"
                            } else {
                                "Selected $cardName (No moves available)"
                            }
                            soundManager.playCardMove()
                        }
                    } else {
                        soundManager.playInvalidMove()
                    }
                }
            }
        }
    }

    fun onFoundationClicked(foundationIndex: Int) {
        if (_gameState.value.isGameWon || _isAutoCompleting.value) return
        val selected = _selectedLocation.value
        val target = CardLocation.Foundation(foundationIndex)

        if (selected != null) {
            val moveResult = SolitaireEngine.moveCards(_gameState.value, selected, target)
            if (moveResult != null) {
                _gameState.value = moveResult.first
                undoStack.add(moveResult.second)
                val card = getCardAtLocation(selected)
                val cardName = if (card != null) "${card.rank.display}${card.suit.symbol}" else "Card"
                _selectedLocation.value = null
                _validTargets.value = emptySet()
                _statusMessage.value = "✓ Matched $cardName to Foundation!"
                soundManager.playFoundationSnap()
                checkWinOrSave()
            } else {
                soundManager.playInvalidMove()
                _statusMessage.value = "Cannot place this card in this foundation slot"
            }
        } else {
            // If foundation has a card, allow selecting it to move down
            val foundationPile = _gameState.value.foundations.getOrNull(foundationIndex)
            if (!foundationPile.isNullOrEmpty()) {
                _selectedLocation.value = target
                val targets = calculateValidTargets(target)
                _validTargets.value = targets
                val card = foundationPile.last()
                _statusMessage.value = "Selected ${card.rank.display}${card.suit.symbol} from Foundation"
                soundManager.playCardMove()
            }
        }
    }

    fun onCardDoubleClicked(location: CardLocation) {
        if (_gameState.value.isGameWon || _isAutoCompleting.value) return
        _activeHint.value = null
        _selectedLocation.value = null
        _validTargets.value = emptySet()

        val result = SolitaireEngine.doubleTapToMove(_gameState.value, location)
        if (result != null) {
            _gameState.value = result.first
            undoStack.add(result.second)
            _statusMessage.value = "✓ Matched to Foundation!"
            soundManager.playFoundationSnap()
            checkWinOrSave()
        }
    }

    fun onEmptyColumnClicked(colIndex: Int) {
        val selected = _selectedLocation.value ?: return
        val target = CardLocation.Tableau(colIndex)
        val moveResult = SolitaireEngine.moveCards(_gameState.value, selected, target)
        if (moveResult != null) {
            _gameState.value = moveResult.first
            undoStack.add(moveResult.second)
            _selectedLocation.value = null
            _validTargets.value = emptySet()
            _statusMessage.value = "✓ Placed on empty column"
            soundManager.playCardMove()
            checkWinOrSave()
        } else {
            soundManager.playInvalidMove()
            _statusMessage.value = "Only a King can be placed on an empty column"
        }
    }

    fun clearSelection() {
        _selectedLocation.value = null
        _validTargets.value = emptySet()
        _statusMessage.value = "Tap any face-up card to select"
    }

    fun undo() {
        if (undoStack.isEmpty() || _gameState.value.isGameWon || _isAutoCompleting.value) return
        val lastMove = undoStack.removeAt(undoStack.lastIndex)
        val restored = SolitaireEngine.undo(_gameState.value, lastMove)
        _gameState.value = restored
        _selectedLocation.value = null
        _validTargets.value = emptySet()
        _statusMessage.value = "Undid move"
        _activeHint.value = null
        soundManager.playCardMove()
        saveActiveGame()
    }

    fun requestHint() {
        if (_gameState.value.isGameWon || _isAutoCompleting.value) return
        usedHints = true
        val hint = SolitaireEngine.findHint(_gameState.value)
        _activeHint.value = hint
        if (hint != null) {
            _statusMessage.value = "💡 Hint: ${hint.description}"
            soundManager.playCardMove()
        } else {
            _statusMessage.value = "No direct moves found"
            soundManager.playInvalidMove()
        }
    }

    fun useMagicWand(onOutOfWands: (() -> Unit)? = null) {
        if (_gameState.value.isGameWon || _isAutoCompleting.value) return
        val isVip = _userSettings.value.isVipActive()
        if (!isVip && _userSettings.value.magicWands <= 0) {
            onOutOfWands?.invoke()
            _statusMessage.value = "Need Magic Wand! Get from store or watch video."
            soundManager.playInvalidMove()
            return
        }

        val result = SolitaireEngine.magicWand(_gameState.value)
        if (result != null) {
            if (!isVip) {
                viewModelScope.launch { userPrefs.useMagicWand(isVip = false) }
            }
            _gameState.value = result.first
            _selectedLocation.value = null
            _validTargets.value = emptySet()
            _activeHint.value = null
            _statusMessage.value = result.second
            soundManager.playMagicWand()
            checkWinOrSave()
        } else {
            soundManager.playInvalidMove()
            _statusMessage.value = "No cards can be moved right now"
        }
    }

    fun requestAiCoachAnalysis(onOutOfTokens: (() -> Unit)? = null) {
        if (_gameState.value.isGameWon || _isAutoCompleting.value) return
        val isVip = _userSettings.value.isVipActive()
        if (!isVip && _userSettings.value.aiTokens <= 0) {
            onOutOfTokens?.invoke()
            _statusMessage.value = "Need AI Token! Get from store or watch video."
            soundManager.playInvalidMove()
            return
        }

        _isAiAnalyzing.value = true
        viewModelScope.launch {
            if (!isVip) {
                userPrefs.useAiToken(isVip = false)
            }
            val result = com.solitaire.hyper.card.games.ai.SolitaireAiEngine.analyzeGame(_gameState.value)
            _aiAnalysis.value = result
            _isAiAnalyzing.value = false
            soundManager.playMagicWand()
        }
    }

    fun dismissAiCoach() {
        _aiAnalysis.value = null
    }

    fun applyAiRecommendedMove() {
        val hint = SolitaireEngine.findHint(_gameState.value)
        if (hint != null) {
            _activeHint.value = hint
            _statusMessage.value = "AI Recommended: ${hint.description}"
            soundManager.playCardMove()
        }
    }

    fun onCardDropped(from: CardLocation, to: CardLocation) {
        if (_gameState.value.isGameWon || _isAutoCompleting.value) return
        val moveResult = SolitaireEngine.moveCards(_gameState.value, from, to)
        if (moveResult != null) {
            _gameState.value = moveResult.first
            undoStack.add(moveResult.second)
            _selectedLocation.value = null
            _validTargets.value = emptySet()
            _activeHint.value = null
            val isFoundation = to is CardLocation.Foundation
            if (isFoundation) {
                soundManager.playFoundationSnap()
                _statusMessage.value = "✓ Matched to Foundation!"
            } else {
                soundManager.playCardMove()
                _statusMessage.value = "✓ Moved card"
            }
            checkWinOrSave()
        } else {
            soundManager.playInvalidMove()
        }
    }

    fun triggerAutoComplete() {
        if (!_gameState.value.canAutoComplete || _isAutoCompleting.value || _gameState.value.isGameWon) return
        _isAutoCompleting.value = true
        _selectedLocation.value = null
        _activeHint.value = null

        viewModelScope.launch {
            while (_gameState.value.canAutoComplete && !_gameState.value.isGameWon) {
                val step = SolitaireEngine.stepAutoComplete(_gameState.value)
                if (step != null) {
                    _gameState.value = step.first
                    soundManager.playFoundationSnap()
                    delay(120)
                } else {
                    break
                }
            }
            _isAutoCompleting.value = false
            if (_gameState.value.hasWon) {
                handleVictory()
            }
        }
    }

    private fun checkWinOrSave() {
        if (_gameState.value.hasWon) {
            handleVictory()
        } else {
            saveActiveGame()
        }
    }

    private fun handleVictory() {
        timerJob?.cancel()
        _gameState.update { it.copy(isGameWon = true) }
        _showWinDialog.value = true
        soundManager.playVictory()

        viewModelScope.launch {
            repository.recordGameFinished(_gameState.value, isWin = true, usedHints = usedHints)
            userPrefs.addCoins(200) // Award 200 coins on victory
            userPrefs.saveGameJson(null) // Clear active game
        }
    }

    fun awardDoubleWinCoins() {
        viewModelScope.launch {
            userPrefs.addCoins(400)
        }
    }

    fun dismissWinDialog() {
        _showWinDialog.value = false
    }

    private fun isLocationSelectable(location: CardLocation): Boolean {
        val state = _gameState.value
        return when (location) {
            is CardLocation.Waste -> state.waste.isNotEmpty()
            is CardLocation.Tableau -> {
                val col = state.tableau.getOrNull(location.columnIndex) ?: return false
                val idx = if (location.cardIndex >= 0) location.cardIndex else col.lastIndex
                col.getOrNull(idx)?.isFaceUp == true
            }
            is CardLocation.Foundation -> {
                val found = state.foundations.getOrNull(location.index) ?: return false
                found.isNotEmpty()
            }
            else -> false
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (!_gameState.value.isGameWon) {
                delay(1000)
                _gameState.update { it.copy(elapsedTimeSeconds = it.elapsedTimeSeconds + 1) }
            }
        }
    }

    private fun saveActiveGame() {
        if (_gameState.value.isGameWon) return
        viewModelScope.launch {
            val json = GameStateSerializer.serialize(_gameState.value)
            userPrefs.saveGameJson(json)
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
