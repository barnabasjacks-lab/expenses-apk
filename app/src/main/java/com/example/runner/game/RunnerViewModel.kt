package com.example.runner.game

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.random.Random

data class RunnerUiState(
    val gameState: GameState = GameState.READY,
    val distanceMeters: Int = 0,
    val coins: Int = 0,
    val recordMeters: Int = 0,
    val playerLane: Lane = Lane.CENTER,
    val playerAction: PlayerAction = PlayerAction.RUNNING,
    val obstacles: List<Obstacle> = emptyList(),
    val coinsOnRoad: List<CoinItem> = emptyList(),
    val currentSpeedMultiplier: Float = 1.0f,
    val lastHitReason: String = ""
)

class RunnerViewModel(application: Application) : AndroidViewModel(application) {
    private val prefs = application.getSharedPreferences("dar_runner_prefs", Context.MODE_PRIVATE)

    private val _uiState = MutableStateFlow(
        RunnerUiState(
            recordMeters = prefs.getInt("high_score_record", 0)
        )
    )
    val uiState: StateFlow<RunnerUiState> = _uiState.asStateFlow()

    private var gameLoopJob: Job? = null
    private var actionResetJob: Job? = null
    private var nextObstacleTick = 0
    private var nextCoinTick = 0
    private var idCounter = 0L

    fun startGame() {
        gameLoopJob?.cancel()
        _uiState.value = _uiState.value.copy(
            gameState = GameState.PLAYING,
            distanceMeters = 0,
            coins = 0,
            playerLane = Lane.CENTER,
            playerAction = PlayerAction.RUNNING,
            obstacles = emptyList(),
            coinsOnRoad = emptyList(),
            currentSpeedMultiplier = 1.0f,
            lastHitReason = ""
        )

        gameLoopJob = viewModelScope.launch {
            var lastSpeedCheckDistance = 0
            while (isActive && _uiState.value.gameState == GameState.PLAYING) {
                delay(33) // ~30 FPS kwa ajili ya smooth game ticks
                tickGame()

                // Ongeza speed kadri umbali unavyozidi
                val currentDist = _uiState.value.distanceMeters
                if (currentDist - lastSpeedCheckDistance >= 150) {
                    lastSpeedCheckDistance = currentDist
                    val newSpeed = (_uiState.value.currentSpeedMultiplier + 0.12f).coerceAtMost(2.5f)
                    _uiState.value = _uiState.value.copy(currentSpeedMultiplier = newSpeed)
                }
            }
        }
    }

    private fun tickGame() {
        val state = _uiState.value
        val speed = 0.025f * state.currentSpeedMultiplier
        val newDistance = state.distanceMeters + (1 * state.currentSpeedMultiplier).toInt().coerceAtLeast(1)

        // Spawn obstacles
        val currentObstacles = state.obstacles.toMutableList()
        nextObstacleTick++
        if (nextObstacleTick > (50 / state.currentSpeedMultiplier).toInt().coerceAtLeast(25)) {
            nextObstacleTick = 0
            val lane = Lane.entries.random()
            val type = ObstacleType.entries.random()
            currentObstacles.add(Obstacle(id = ++idCounter, lane = lane, yProgress = 0f, type = type))
        }

        // Spawn coins
        val currentCoins = state.coinsOnRoad.toMutableList()
        nextCoinTick++
        if (nextCoinTick > 35) {
            nextCoinTick = 0
            val lane = Lane.entries.random()
            // Tengeneza sarafu 3 mfululizo kwenye njia hiyo
            currentCoins.add(CoinItem(id = ++idCounter, lane = lane, yProgress = 0f))
            currentCoins.add(CoinItem(id = ++idCounter, lane = lane, yProgress = -0.06f))
            currentCoins.add(CoinItem(id = ++idCounter, lane = lane, yProgress = -0.12f))
        }

        // Move obstacles down the road
        val updatedObstacles = mutableListOf<Obstacle>()
        var hitObstacle: Obstacle? = null

        for (obs in currentObstacles) {
            val newProgress = obs.yProgress + speed
            if (newProgress < 1.2f) {
                obs.yProgress = newProgress
                updatedObstacles.add(obs)

                // Collision Check karibu na mchezaji (yProgress kati ya 0.80f hadi 0.95f)
                if (obs.lane == state.playerLane && newProgress in 0.82f..0.96f) {
                    val avoided = when (obs.type) {
                        ObstacleType.POTHOLE -> state.playerAction == PlayerAction.JUMPING
                        ObstacleType.BODABODA -> state.playerAction == PlayerAction.JUMPING
                        ObstacleType.BARRIER -> state.playerAction == PlayerAction.SLIDING
                        ObstacleType.DALADALA -> false // Daladala ni kubwa huwezi kuruka wala kupita chini! Lazima ukwepe njia!
                    }
                    if (!avoided) {
                        hitObstacle = obs
                    }
                }
            }
        }

        // Move and collect coins
        var coinsCollectedThisTick = 0
        val updatedCoins = mutableListOf<CoinItem>()
        for (coin in currentCoins) {
            val newProgress = coin.yProgress + speed
            if (newProgress < 1.2f) {
                coin.yProgress = newProgress
                if (!coin.collected && coin.lane == state.playerLane && newProgress in 0.80f..0.98f) {
                    coin.collected = true
                    coinsCollectedThisTick++
                } else if (!coin.collected) {
                    updatedCoins.add(coin)
                }
            }
        }

        // Check Game Over
        if (hitObstacle != null) {
            val reason = when (hitObstacle.type) {
                ObstacleType.DALADALA -> "Uligongana na Daladala! 🚌"
                ObstacleType.BODABODA -> "Uligongana na Bodaboda! 🏍️"
                ObstacleType.POTHOLE -> "Ulitumbukia kwenye Shimo la Barabara! 🕳️"
                ObstacleType.BARRIER -> "Uligonga Kizuizi cha Barabara! 🚧"
            }
            val record = maxOf(state.recordMeters, newDistance)
            prefs.edit().putInt("high_score_record", record).apply()

            _uiState.value = state.copy(
                gameState = GameState.GAME_OVER,
                distanceMeters = newDistance,
                recordMeters = record,
                lastHitReason = reason
            )
            gameLoopJob?.cancel()
            return
        }

        _uiState.value = state.copy(
            distanceMeters = newDistance,
            coins = state.coins + coinsCollectedThisTick,
            obstacles = updatedObstacles,
            coinsOnRoad = updatedCoins
        )
    }

    // CONTROLS
    fun moveLeft() {
        if (_uiState.value.gameState != GameState.PLAYING) return
        val current = _uiState.value.playerLane
        val newLane = when (current) {
            Lane.RIGHT -> Lane.CENTER
            Lane.CENTER -> Lane.LEFT
            Lane.LEFT -> Lane.LEFT
        }
        _uiState.value = _uiState.value.copy(playerLane = newLane)
    }

    fun moveRight() {
        if (_uiState.value.gameState != GameState.PLAYING) return
        val current = _uiState.value.playerLane
        val newLane = when (current) {
            Lane.LEFT -> Lane.CENTER
            Lane.CENTER -> Lane.RIGHT
            Lane.RIGHT -> Lane.RIGHT
        }
        _uiState.value = _uiState.value.copy(playerLane = newLane)
    }

    fun jump() {
        if (_uiState.value.gameState != GameState.PLAYING) return
        if (_uiState.value.playerAction != PlayerAction.RUNNING) return

        _uiState.value = _uiState.value.copy(playerAction = PlayerAction.JUMPING)
        actionResetJob?.cancel()
        actionResetJob = viewModelScope.launch {
            delay(550) // Duration ya kuruka
            if (_uiState.value.playerAction == PlayerAction.JUMPING) {
                _uiState.value = _uiState.value.copy(playerAction = PlayerAction.RUNNING)
            }
        }
    }

    fun slide() {
        if (_uiState.value.gameState != GameState.PLAYING) return
        if (_uiState.value.playerAction != PlayerAction.RUNNING) return

        _uiState.value = _uiState.value.copy(playerAction = PlayerAction.SLIDING)
        actionResetJob?.cancel()
        actionResetJob = viewModelScope.launch {
            delay(550) // Duration ya kuteleza chini
            if (_uiState.value.playerAction == PlayerAction.SLIDING) {
                _uiState.value = _uiState.value.copy(playerAction = PlayerAction.RUNNING)
            }
        }
    }
}
