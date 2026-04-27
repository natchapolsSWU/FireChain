package com.example.russianroulette.viewmodel

import androidx.lifecycle.ViewModel
import com.example.russianroulette.logic.RussianRouletteEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class NormalModeState(
    val chambers: Int = 6,
    val bullets: Int = 1,
    val bulletsRemaining: Int = 1,
    val rotation: Float = 0f,
    val isGameOver: Boolean = false,
    val lastResult: GameResult? = null,
    val hasSpunThisTurn: Boolean = false
)

sealed class GameResult {
    object Safe : GameResult()
    object Hit : GameResult()
}

class NormalModeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(NormalModeState())
    val uiState: StateFlow<NormalModeState> = _uiState.asStateFlow()

    private var engine = RussianRouletteEngine(6, 1)

    fun updateSettings(chambers: Int, bullets: Int) {
        engine = RussianRouletteEngine(chambers, bullets)
        _uiState.update { 
            it.copy(
                chambers = chambers, 
                bullets = bullets, 
                bulletsRemaining = bullets,
                isGameOver = false, 
                lastResult = null,
                hasSpunThisTurn = false
            ) 
        }
    }

    fun onManualSpin(direction: Int) {
        if (_uiState.value.isGameOver || _uiState.value.hasSpunThisTurn) return
        engine.rotate(direction)
        _uiState.update { 
            it.copy(
                rotation = it.rotation + (direction * (360f / it.chambers)),
                hasSpunThisTurn = true
            ) 
        }
    }

    fun randomizeSpin() {
        if (_uiState.value.isGameOver) return
        engine.randomSpin()
        _uiState.update { 
            it.copy(
                rotation = it.rotation + (360f * 2),
                hasSpunThisTurn = false // Normal mode allows randomizing anytime or logic varies
            ) 
        }
    }

    fun fire() {
        if (_uiState.value.isGameOver) return

        val isHit = engine.fire()
        val remaining = engine.getRemainingBulletsCount()
        
        _uiState.update { 
            it.copy(
                lastResult = if (isHit) GameResult.Hit else GameResult.Safe,
                bulletsRemaining = remaining,
                isGameOver = remaining == 0,
                rotation = it.rotation + (360f / it.chambers),
                hasSpunThisTurn = false
            )
        }
    }

    fun reset() {
        engine.reset()
        _uiState.update { 
            it.copy(
                isGameOver = false, 
                lastResult = null, 
                rotation = 0f,
                bulletsRemaining = engine.getRemainingBulletsCount(),
                hasSpunThisTurn = false
            ) 
        }
    }
}
