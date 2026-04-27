package com.example.russianroulette.viewmodel

import androidx.lifecycle.ViewModel
import com.example.russianroulette.logic.RussianRouletteEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class Player(
    val id: Int,
    val name: String,
    val isAlive: Boolean = true
)

data class PartyModeState(
    val players: List<Player> = emptyList(),
    val currentPlayerIndex: Int = 0,
    val chambers: Int = 6,
    val bullets: Int = 1,
    val rotation: Float = 0f,
    val skipCount: Int = 0,
    val firstPasserIndex: Int? = null,
    val gameMessage: String = "Player 1's Turn",
    val isGameOver: Boolean = false,
    val isPeeking: Boolean = false, // Not used much now, replaced by popup logic
    val showPeekPopup: Boolean = false,
    val isRevealingInPopup: Boolean = false, // New: reveal state inside the popup
    val canPeekThisTurn: Boolean = false,
    val hasPeekedThisRound: Boolean = false,
    val chamberStates: List<Boolean> = emptyList(),
    val canPass: Boolean = true,
    val hasSpunThisTurn: Boolean = false,
    val isShootingSequence: Boolean = false,
    val shotsRemainingInSequence: Int = 0,
    val manualTargetIndex: Int? = null,
    val pendingNextPlayerAfterPeek: Int? = null
)

class PartyModeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(PartyModeState())
    val uiState: StateFlow<PartyModeState> = _uiState.asStateFlow()

    private var engine = RussianRouletteEngine(6, 1)

    fun setupGame(playerCount: Int, chambers: Int, bullets: Int) {
        val players = (1..playerCount).map { Player(it, "Player $it") }
        engine = RussianRouletteEngine(chambers, bullets)
        _uiState.update {
            it.copy(
                players = players,
                chambers = chambers,
                bullets = bullets,
                currentPlayerIndex = 0,
                skipCount = 0,
                firstPasserIndex = null,
                gameMessage = "Player 1's Turn",
                isGameOver = false,
                isPeeking = false,
                showPeekPopup = false,
                canPeekThisTurn = false,
                hasPeekedThisRound = false,
                chamberStates = emptyList(),
                canPass = true,
                hasSpunThisTurn = false,
                isShootingSequence = false,
                shotsRemainingInSequence = 0,
                manualTargetIndex = null,
                pendingNextPlayerAfterPeek = null
            )
        }
    }

    fun onManualSpin(direction: Int) {
        val state = _uiState.value
        if (state.isGameOver || state.isShootingSequence || state.hasSpunThisTurn) return
        
        engine.rotate(direction)
        _uiState.update { 
            it.copy(
                rotation = it.rotation + (direction * (360f / it.chambers)),
                hasSpunThisTurn = true,
                gameMessage = "Cylinder Rotated"
            ) 
        }
    }

    fun randomSpin() {
        if (_uiState.value.isGameOver || _uiState.value.isShootingSequence) return
        engine.randomSpin()
        _uiState.update { 
            it.copy(
                rotation = it.rotation + (360f * 2), // Visual spin
                gameMessage = "Cylinder Spun"
            ) 
        }
    }

    fun setPeekingInPopup(isRevealing: Boolean) {
        _uiState.update { it.copy(isRevealingInPopup = isRevealing, chamberStates = if (isRevealing) engine.getChamberStates() else emptyList()) }
    }

    fun pass() {
        val state = _uiState.value
        if (!state.canPass || state.isShootingSequence || state.showPeekPopup) return

        val newSkipCount = state.skipCount + 1
        val newFirstPasser = state.firstPasserIndex ?: state.currentPlayerIndex
        
        var nextPlayerIndex = (state.currentPlayerIndex + 1) % state.players.size
        while (!state.players[nextPlayerIndex].isAlive) {
            nextPlayerIndex = (nextPlayerIndex + 1) % state.players.size
        }

        // Peek Probability Logic: 1 in 3 (33%) chance
        val peekSucceeded = !state.hasPeekedThisRound && (kotlin.random.Random.nextInt(3) == 0)
        
        if (peekSucceeded) {
            // Show popup for current player BEFORE changing turn
            _uiState.update {
                it.copy(
                    skipCount = newSkipCount,
                    firstPasserIndex = newFirstPasser,
                    showPeekPopup = true,
                    canPeekThisTurn = true,
                    hasPeekedThisRound = true,
                    pendingNextPlayerAfterPeek = nextPlayerIndex,
                    gameMessage = "PEEK GRANTED! Look closely..."
                )
            }
        } else {
            // Change turn immediately
            _uiState.update {
                it.copy(
                    skipCount = newSkipCount,
                    firstPasserIndex = newFirstPasser,
                    currentPlayerIndex = nextPlayerIndex,
                    gameMessage = "Player ${nextPlayerIndex + 1}'s Turn",
                    hasSpunThisTurn = false,
                    canPass = nextPlayerIndex != newFirstPasser
                )
            }
        }
    }

    fun onPeekDismissed() {
        val state = _uiState.value
        val nextIdx = state.pendingNextPlayerAfterPeek ?: return
        _uiState.update {
            it.copy(
                showPeekPopup = false,
                isRevealingInPopup = false,
                canPeekThisTurn = false,
                currentPlayerIndex = nextIdx,
                gameMessage = "Player ${nextIdx + 1}'s Turn",
                hasSpunThisTurn = false,
                canPass = nextIdx != state.firstPasserIndex,
                pendingNextPlayerAfterPeek = null
            )
        }
    }

    fun fire() {
        val state = _uiState.value
        if (state.isGameOver || state.isShootingSequence || state.showPeekPopup) return
        
        _uiState.update {
            it.copy(
                isShootingSequence = true,
                shotsRemainingInSequence = state.skipCount + 1,
                manualTargetIndex = state.currentPlayerIndex,
                gameMessage = "MANUAL FIRE: Shoot Yourself First!"
            )
        }
    }

    fun triggerManualShot() {
        val state = _uiState.value
        if (!state.isShootingSequence || state.isGameOver) return
        
        val targetIdx = state.manualTargetIndex ?: return
        val isHit = engine.fire()
        
        if (isHit) {
            val updatedPlayers = state.players.map { p -> 
                if (p.id == state.players[targetIdx].id) p.copy(isAlive = false) else p 
            }
            _uiState.update { it.copy(isShootingSequence = false, manualTargetIndex = null) }
            updateResult(updatedPlayers, "BOOM! ${state.players[targetIdx].name} ELIMINATED")
            checkGameOver(updatedPlayers)
        } else {
            val remaining = state.shotsRemainingInSequence - 1
            if (remaining == 0) {
                // Sequence finished, everyone survived
                val nextIdx = findNextAlive(state.currentPlayerIndex, state.players)
                _uiState.update {
                    it.copy(
                        skipCount = 0,
                        firstPasserIndex = null,
                        currentPlayerIndex = nextIdx,
                        gameMessage = "CLICK! Everyone Survived. Player ${nextIdx + 1}'s Turn",
                        canPass = true,
                        hasSpunThisTurn = false,
                        isShootingSequence = false,
                        canPeekThisTurn = false,
                        hasPeekedThisRound = false,
                        manualTargetIndex = null,
                        shotsRemainingInSequence = 0
                    )
                }
            } else {
                // Move to next target in reverse sequence
                var nextTarget = (targetIdx - 1).mod(state.players.size)
                while (!state.players[nextTarget].isAlive) {
                    nextTarget = (nextTarget - 1).mod(state.players.size)
                }
                _uiState.update {
                    it.copy(
                        shotsRemainingInSequence = remaining,
                        manualTargetIndex = nextTarget,
                        gameMessage = "CLICK! Safe. Next Target: ${state.players[nextTarget].name}",
                        rotation = it.rotation + (360f / it.chambers)
                    )
                }
            }
        }
    }


    private fun updateResult(players: List<Player>, msg: String) {
        _uiState.update { 
            it.copy(
                players = players, 
                gameMessage = msg, 
                skipCount = 0, 
                firstPasserIndex = null,
                canPeekThisTurn = false,
                hasPeekedThisRound = false // Reset on hit too
            ) 
        }
    }

    private fun checkGameOver(players: List<Player>) {
        val remainingBullets = engine.getRemainingBulletsCount()
        val aliveCount = players.count { it.isAlive }
        
        if (remainingBullets == 0 || aliveCount <= 1) {
            val winner = players.find { it.isAlive }
            _uiState.update { 
                it.copy(
                    isGameOver = true, 
                    gameMessage = if (remainingBullets == 0) "ALL BULLETS SPENT!" else "GAME OVER! ${winner?.name} WINS!",
                    isShootingSequence = false
                ) 
            }
        } else {
            // Game continues, move to next alive player
            val nextIdx = findNextAlive(_uiState.value.currentPlayerIndex, players)
            _uiState.update { it.copy(currentPlayerIndex = nextIdx, isShootingSequence = false, hasSpunThisTurn = false, canPass = true) }
        }
    }

    private fun findNextAlive(current: Int, players: List<Player>): Int {
        var next = (current + 1) % players.size
        while (!players[next].isAlive) {
            next = (next + 1) % players.size
        }
        return next
    }

    fun reset() {
        _uiState.update { it.copy(players = emptyList()) } // Clearing players triggers setup screen
    }
}
