package com.example.runner.game

import kotlin.random.Random

enum class Lane(val index: Int) {
    LEFT(0),
    CENTER(1),
    RIGHT(2)
}

enum class ObstacleType {
    DALADALA,   // Minibus kubwa - lazima ukwepe upande au kuruka kama ni ndogo
    BODABODA,   // Pikipiki yenye mwendo kasi
    POTHOLE,    // Shimo la barabarani - unaruka juu
    BARRIER     // Kizuizi cha barabara cha juu - unainama chini (slide)
}

data class Obstacle(
    val id: Long,
    val lane: Lane,
    var yProgress: Float, // 0.0f (mbali sana) hadi 1.0f (karibu na mchezaji)
    val type: ObstacleType
)

data class CoinItem(
    val id: Long,
    val lane: Lane,
    var yProgress: Float,
    var collected: Boolean = false
)

enum class PlayerAction {
    RUNNING,
    JUMPING,
    SLIDING
}

enum class GameState {
    READY,
    PLAYING,
    GAME_OVER
}
