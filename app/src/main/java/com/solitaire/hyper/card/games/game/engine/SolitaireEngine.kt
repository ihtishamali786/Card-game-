package com.solitaire.hyper.card.games.game.engine

import com.solitaire.hyper.card.games.game.model.Card
import com.solitaire.hyper.card.games.game.model.CardLocation
import com.solitaire.hyper.card.games.game.model.GameMode
import com.solitaire.hyper.card.games.game.model.GameMove
import com.solitaire.hyper.card.games.game.model.GameState
import com.solitaire.hyper.card.games.game.model.Hint
import com.solitaire.hyper.card.games.game.model.Rank
import com.solitaire.hyper.card.games.game.model.Suit
import java.util.Random

/**
 * Pure, isolated Klondike Solitaire game engine.
 * Contains no Android UI or Composable dependencies.
 */
object SolitaireEngine {

    /**
     * Creates a standard 52-card deck.
     */
    fun createDeck(): List<Card> {
        val deck = ArrayList<Card>(52)
        var id = 1
        for (suit in Suit.entries) {
            for (rank in Rank.entries) {
                deck.add(Card(id = id++, suit = suit, rank = rank, isFaceUp = false))
            }
        }
        return deck
    }

    /**
     * Initializes a brand new Klondike deal.
     */
    fun newGame(
        gameMode: GameMode = GameMode.DRAW_1,
        seed: Long = System.currentTimeMillis(),
        isDailyChallenge: Boolean = false,
        challengeDate: String? = null
    ): GameState {
        val random = Random(seed)
        val deck = createDeck().shuffled(random)

        val tableau = List(7) { ArrayList<Card>() }
        var deckIndex = 0

        // Deal 7 columns: col i has i+1 cards, top card is face-up
        for (col in 0 until 7) {
            for (row in 0..col) {
                val card = deck[deckIndex++]
                val isTop = (row == col)
                tableau[col].add(card.copy(isFaceUp = isTop))
            }
        }

        // Remaining 24 cards into stock, all face down
        val stock = ArrayList<Card>(24)
        while (deckIndex < deck.size) {
            stock.add(deck[deckIndex++].copy(isFaceUp = false))
        }

        val foundations = List(4) { emptyList<Card>() }

        return GameState(
            stock = stock,
            waste = emptyList(),
            foundations = foundations,
            tableau = tableau.map { it.toList() },
            moveCount = 0,
            score = 0,
            elapsedTimeSeconds = 0L,
            isGameWon = false,
            isAutoCompleteAvailable = false,
            gameMode = gameMode,
            isDailyChallenge = isDailyChallenge,
            challengeDate = challengeDate,
            seed = seed
        )
    }

    /**
     * Draws cards from stock to waste, or recycles waste to stock when stock is empty.
     */
    fun drawCards(state: GameState): Pair<GameState, GameMove>? {
        if (state.isGameWon) return null

        if (state.stock.isNotEmpty()) {
            val countToDraw = minOf(state.gameMode.cardsPerDraw, state.stock.size)
            // Stock top is at the end of the list
            val drawnRaw = state.stock.takeLast(countToDraw)
            val newStock = state.stock.dropLast(countToDraw)
            val drawnFlipped = drawnRaw.map { it.copy(isFaceUp = true) }
            val newWaste = state.waste + drawnFlipped

            val move = GameMove.StockDraw(drawnRaw)
            val updated = state.copy(
                stock = newStock,
                waste = newWaste,
                moveCount = state.moveCount + 1,
                isAutoCompleteAvailable = checkAutoCompleteCondition(newStock, newWaste, state.tableau)
            )
            return Pair(updated, move)
        } else if (state.waste.isNotEmpty()) {
            // Recycle waste into stock: cards flipped face down and reversed
            val recycled = state.waste.reversed().map { it.copy(isFaceUp = false) }
            val move = GameMove.StockRecycle(state.waste)
            val updated = state.copy(
                stock = recycled,
                waste = emptyList(),
                moveCount = state.moveCount + 1,
                isAutoCompleteAvailable = checkAutoCompleteCondition(recycled, emptyList(), state.tableau)
            )
            return Pair(updated, move)
        }
        return null
    }

    /**
     * Validates if moving [movingCard] onto [targetColumn] is legal in Tableau.
     */
    fun canMoveToTableau(movingCard: Card, targetColumn: List<Card>): Boolean {
        if (targetColumn.isEmpty()) {
            return movingCard.rank == Rank.KING
        }
        val topCard = targetColumn.last()
        if (!topCard.isFaceUp) return false
        return (movingCard.color != topCard.color) && (movingCard.rank.value == topCard.rank.value - 1)
    }

    /**
     * Validates if moving [movingCard] onto [foundation] is legal.
     */
    fun canMoveToFoundation(movingCard: Card, foundation: List<Card>): Boolean {
        if (foundation.isEmpty()) {
            return movingCard.rank == Rank.ACE
        }
        val topCard = foundation.last()
        return (movingCard.suit == topCard.suit) && (movingCard.rank.value == topCard.rank.value + 1)
    }

    /**
     * Performs a validated move of a card or card stack from [from] to [to].
     */
    fun moveCards(
        state: GameState,
        from: CardLocation,
        to: CardLocation
    ): Pair<GameState, GameMove>? {
        if (state.isGameWon) return null

        val cardsToMove: List<Card>
        val stateWithoutCards: GameState
        var previousTopWasFlipped = false
        var scoreDelta = 0

        when (from) {
            is CardLocation.Waste -> {
                if (state.waste.isEmpty()) return null
                val card = state.waste.last()
                cardsToMove = listOf(card)
                stateWithoutCards = state.copy(waste = state.waste.dropLast(1))
            }
            is CardLocation.Tableau -> {
                val col = state.tableau.getOrNull(from.columnIndex) ?: return null
                val index = if (from.cardIndex >= 0) from.cardIndex else col.lastIndex
                if (index !in col.indices) return null
                val movingStack = col.subList(index, col.size)
                if (movingStack.any { !it.isFaceUp }) return null // Can only move face-up cards
                cardsToMove = movingStack.toList()

                val remainingCol = col.subList(0, index).toMutableList()
                if (remainingCol.isNotEmpty() && !remainingCol.last().isFaceUp) {
                    remainingCol[remainingCol.lastIndex] = remainingCol.last().copy(isFaceUp = true)
                    previousTopWasFlipped = true
                    scoreDelta += 5 // +5 points for flipping tableau card
                }
                val newTableau = state.tableau.toMutableList()
                newTableau[from.columnIndex] = remainingCol
                stateWithoutCards = state.copy(tableau = newTableau)
            }
            is CardLocation.Foundation -> {
                val found = state.foundations.getOrNull(from.index) ?: return null
                if (found.isEmpty()) return null
                cardsToMove = listOf(found.last())
                val newFoundations = state.foundations.toMutableList()
                newFoundations[from.index] = found.dropLast(1)
                stateWithoutCards = state.copy(foundations = newFoundations)
                scoreDelta -= 15 // Deduct points if moving back from foundation
            }
            else -> return null
        }

        val primaryCard = cardsToMove.first()

        val finalState: GameState
        when (to) {
            is CardLocation.Tableau -> {
                val targetCol = stateWithoutCards.tableau.getOrNull(to.columnIndex) ?: return null
                if (!canMoveToTableau(primaryCard, targetCol)) return null

                val newTableau = stateWithoutCards.tableau.toMutableList()
                newTableau[to.columnIndex] = targetCol + cardsToMove
                if (from is CardLocation.Waste) scoreDelta += 5

                finalState = stateWithoutCards.copy(tableau = newTableau)
            }
            is CardLocation.Foundation -> {
                if (cardsToMove.size != 1) return null
                val targetFound = stateWithoutCards.foundations.getOrNull(to.index) ?: return null
                if (!canMoveToFoundation(primaryCard, targetFound)) return null

                val newFoundations = stateWithoutCards.foundations.toMutableList()
                newFoundations[to.index] = targetFound + cardsToMove
                scoreDelta += 10

                finalState = stateWithoutCards.copy(foundations = newFoundations)
            }
            else -> return null
        }

        val won = finalState.totalFoundationCards == 52
        val autoEligible = finalState.canAutoComplete

        val move = GameMove.CardMove(
            from = from,
            to = to,
            cards = cardsToMove,
            previousSourceTopCardWasFlipped = previousTopWasFlipped,
            scoreDelta = scoreDelta
        )

        val updated = finalState.copy(
            score = maxOf(0, state.score + scoreDelta),
            moveCount = state.moveCount + 1,
            isGameWon = won,
            isAutoCompleteAvailable = autoEligible
        )

        return Pair(updated, move)
    }

    /**
     * Executes tap-to-move for an tapped card at [location].
     * Priority: Foundation first, then best legal Tableau column.
     */
    fun tapToMove(state: GameState, location: CardLocation): Pair<GameState, GameMove>? {
        if (state.isGameWon) return null

        val card: Card? = when (location) {
            is CardLocation.Waste -> state.waste.lastOrNull()
            is CardLocation.Tableau -> {
                val col = state.tableau.getOrNull(location.columnIndex) ?: return null
                val idx = if (location.cardIndex >= 0) location.cardIndex else col.lastIndex
                col.getOrNull(idx)
            }
            else -> null
        }

        if (card == null || !card.isFaceUp) return null

        // 1. Try Foundation (only if single top card)
        val isTopCard = when (location) {
            is CardLocation.Waste -> true
            is CardLocation.Tableau -> {
                val col = state.tableau[location.columnIndex]
                location.cardIndex == col.lastIndex
            }
            else -> false
        }

        if (isTopCard) {
            for (fIndex in 0 until 4) {
                if (canMoveToFoundation(card, state.foundations[fIndex])) {
                    val result = moveCards(state, location, CardLocation.Foundation(fIndex))
                    if (result != null) return result
                }
            }
        }

        // 2. Try Tableau
        // Prefer non-empty columns first
        for (colIndex in 0 until 7) {
            if (location is CardLocation.Tableau && location.columnIndex == colIndex) continue
            val col = state.tableau[colIndex]
            if (col.isNotEmpty() && canMoveToTableau(card, col)) {
                val result = moveCards(state, location, CardLocation.Tableau(colIndex))
                if (result != null) return result
            }
        }
        // Then try empty columns (for Kings)
        if (card.rank == Rank.KING) {
            for (colIndex in 0 until 7) {
                if (location is CardLocation.Tableau && location.columnIndex == colIndex) continue
                val col = state.tableau[colIndex]
                if (col.isEmpty()) {
                    // Don't move a King to empty column if it's already at the root of its own column
                    if (location is CardLocation.Tableau && location.cardIndex == 0) continue
                    val result = moveCards(state, location, CardLocation.Tableau(colIndex))
                    if (result != null) return result
                }
            }
        }

        return null
    }

    /**
     * Executes double tap: moves to foundation directly if legal.
     */
    fun doubleTapToMove(state: GameState, location: CardLocation): Pair<GameState, GameMove>? {
        if (state.isGameWon) return null
        val card = when (location) {
            is CardLocation.Waste -> state.waste.lastOrNull()
            is CardLocation.Tableau -> {
                val col = state.tableau.getOrNull(location.columnIndex) ?: return null
                if (location.cardIndex != col.lastIndex) return null
                col.lastOrNull()
            }
            else -> null
        } ?: return null

        if (!card.isFaceUp) return null

        for (fIndex in 0 until 4) {
            if (canMoveToFoundation(card, state.foundations[fIndex])) {
                return moveCards(state, location, CardLocation.Foundation(fIndex))
            }
        }
        return null
    }

    /**
     * Undoes the last move with complete fidelity.
     */
    fun undo(state: GameState, move: GameMove): GameState {
        return when (move) {
            is GameMove.CardMove -> {
                val cards = move.cards
                // Remove cards from destination
                val stateWithoutDest: GameState = when (val to = move.to) {
                    is CardLocation.Tableau -> {
                        val col = state.tableau[to.columnIndex]
                        val newCol = col.dropLast(cards.size)
                        val newTableau = state.tableau.toMutableList()
                        newTableau[to.columnIndex] = newCol
                        state.copy(tableau = newTableau)
                    }
                    is CardLocation.Foundation -> {
                        val found = state.foundations[to.index]
                        val newFound = found.dropLast(cards.size)
                        val newFoundations = state.foundations.toMutableList()
                        newFoundations[to.index] = newFound
                        state.copy(foundations = newFoundations)
                    }
                    else -> state
                }

                // Restore cards to source
                val restoredState: GameState = when (val from = move.from) {
                    is CardLocation.Waste -> {
                        stateWithoutDest.copy(waste = stateWithoutDest.waste + cards)
                    }
                    is CardLocation.Tableau -> {
                        val col = stateWithoutDest.tableau[from.columnIndex].toMutableList()
                        if (move.previousSourceTopCardWasFlipped && col.isNotEmpty()) {
                            col[col.lastIndex] = col.last().copy(isFaceUp = false)
                        }
                        col.addAll(cards)
                        val newTableau = stateWithoutDest.tableau.toMutableList()
                        newTableau[from.columnIndex] = col
                        stateWithoutDest.copy(tableau = newTableau)
                    }
                    is CardLocation.Foundation -> {
                        val found = stateWithoutDest.foundations[from.index]
                        val newFoundations = stateWithoutDest.foundations.toMutableList()
                        newFoundations[from.index] = found + cards
                        stateWithoutDest.copy(foundations = newFoundations)
                    }
                    else -> stateWithoutDest
                }

                restoredState.copy(
                    score = maxOf(0, state.score - move.scoreDelta),
                    moveCount = maxOf(0, state.moveCount - 1),
                    isGameWon = false,
                    isAutoCompleteAvailable = restoredState.canAutoComplete
                )
            }
            is GameMove.StockDraw -> {
                val drawnCount = move.drawnCards.size
                val newWaste = state.waste.dropLast(drawnCount)
                val returnedToStock = move.drawnCards.map { it.copy(isFaceUp = false) }
                val newStock = state.stock + returnedToStock
                state.copy(
                    stock = newStock,
                    waste = newWaste,
                    moveCount = maxOf(0, state.moveCount - 1),
                    isAutoCompleteAvailable = checkAutoCompleteCondition(newStock, newWaste, state.tableau)
                )
            }
            is GameMove.StockRecycle -> {
                // Return stock cards back into waste
                val wasteRestored = move.recycledCards.map { it.copy(isFaceUp = true) }
                state.copy(
                    stock = emptyList(),
                    waste = wasteRestored,
                    moveCount = maxOf(0, state.moveCount - 1),
                    isAutoCompleteAvailable = checkAutoCompleteCondition(emptyList(), wasteRestored, state.tableau)
                )
            }
        }
    }

    /**
     * Checks whether the game can be safely auto-completed.
     */
    fun checkAutoCompleteCondition(
        stock: List<Card>,
        waste: List<Card>,
        tableau: List<List<Card>>
    ): Boolean {
        if (stock.isNotEmpty() || waste.isNotEmpty()) return false
        return tableau.all { col -> col.all { it.isFaceUp } }
    }

    /**
     * Executes one single step of auto-complete by moving a valid card to foundation.
     */
    fun stepAutoComplete(state: GameState): Pair<GameState, GameMove>? {
        if (state.hasWon) return null

        for (colIndex in 0 until 7) {
            val col = state.tableau[colIndex]
            if (col.isNotEmpty()) {
                val topCard = col.last()
                for (fIndex in 0 until 4) {
                    if (canMoveToFoundation(topCard, state.foundations[fIndex])) {
                        return moveCards(state, CardLocation.Tableau(colIndex, col.lastIndex), CardLocation.Foundation(fIndex))
                    }
                }
            }
        }
        return null
    }

    /**
     * Computes the smartest legal hint for the current game position.
     */
    fun findHint(state: GameState): Hint? {
        if (state.isGameWon) return null

        // 1. Move from Tableau to Foundation
        for (colIndex in 0 until 7) {
            val col = state.tableau[colIndex]
            if (col.isNotEmpty()) {
                val card = col.last()
                for (fIndex in 0 until 4) {
                    if (canMoveToFoundation(card, state.foundations[fIndex])) {
                        return Hint(
                            from = CardLocation.Tableau(colIndex, col.lastIndex),
                            to = CardLocation.Foundation(fIndex),
                            card = card,
                            description = "Move ${card.toDisplayString()} to foundation"
                        )
                    }
                }
            }
        }

        // 2. Move from Waste to Foundation
        if (state.waste.isNotEmpty()) {
            val wasteCard = state.waste.last()
            for (fIndex in 0 until 4) {
                if (canMoveToFoundation(wasteCard, state.foundations[fIndex])) {
                    return Hint(
                        from = CardLocation.Waste,
                        to = CardLocation.Foundation(fIndex),
                        card = wasteCard,
                        description = "Move ${wasteCard.toDisplayString()} to foundation"
                    )
                }
            }
        }

        // 3. Move Tableau to Tableau to reveal a hidden card
        for (colIndex in 0 until 7) {
            val col = state.tableau[colIndex]
            val firstFaceUpIndex = col.indexOfFirst { it.isFaceUp }
            if (firstFaceUpIndex > 0) { // reveals a face down card!
                val movingCard = col[firstFaceUpIndex]
                for (targetColIndex in 0 until 7) {
                    if (colIndex == targetColIndex) continue
                    val targetCol = state.tableau[targetColIndex]
                    if (canMoveToTableau(movingCard, targetCol)) {
                        return Hint(
                            from = CardLocation.Tableau(colIndex, firstFaceUpIndex),
                            to = CardLocation.Tableau(targetColIndex),
                            card = movingCard,
                            description = "Move ${movingCard.toDisplayString()} stack to reveal hidden card"
                        )
                    }
                }
            }
        }

        // 4. Move Waste to Tableau
        if (state.waste.isNotEmpty()) {
            val wasteCard = state.waste.last()
            for (targetColIndex in 0 until 7) {
                val targetCol = state.tableau[targetColIndex]
                if (canMoveToTableau(wasteCard, targetCol)) {
                    return Hint(
                        from = CardLocation.Waste,
                        to = CardLocation.Tableau(targetColIndex),
                        card = wasteCard,
                        description = "Move ${wasteCard.toDisplayString()} from waste to column ${targetColIndex + 1}"
                    )
                }
            }
        }

        // 5. Move any King to empty column if it reveals a card or clears space
        for (colIndex in 0 until 7) {
            val col = state.tableau[colIndex]
            val firstFaceUpIndex = col.indexOfFirst { it.isFaceUp }
            if (firstFaceUpIndex > 0 && col[firstFaceUpIndex].rank == Rank.KING) {
                val kingCard = col[firstFaceUpIndex]
                for (targetColIndex in 0 until 7) {
                    if (targetColIndex == colIndex) continue
                    if (state.tableau[targetColIndex].isEmpty()) {
                        return Hint(
                            from = CardLocation.Tableau(colIndex, firstFaceUpIndex),
                            to = CardLocation.Tableau(targetColIndex),
                            card = kingCard,
                            description = "Move King ${kingCard.toDisplayString()} to empty space"
                        )
                    }
                }
            }
        }

        // 6. Draw from Stock
        if (state.stock.isNotEmpty() || state.waste.isNotEmpty()) {
            val card = state.stock.lastOrNull() ?: state.waste.first()
            return Hint(
                from = CardLocation.Stock,
                to = CardLocation.Waste,
                card = card,
                description = if (state.stock.isNotEmpty()) "Draw cards from the stock pile" else "Recycle waste to stock"
            )
        }

        return null
    }

    /**
     * Deterministic seed generator for Daily Challenge date string "YYYY-MM-DD".
     */
    fun seedForDate(dateStr: String): Long {
        var hash = 0L
        for (ch in dateStr) {
            hash = 31L * hash + ch.code.toLong()
        }
        return hash
    }
}
