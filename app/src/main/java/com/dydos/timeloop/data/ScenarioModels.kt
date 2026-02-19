package com.dydos.timeloop.data

import kotlinx.serialization.Serializable

@Serializable
data class ScenarioConfig(
    val loop_duration: Int,
    val events: List<ScenarioEvent>,
    val puzzles: List<PuzzleConfig>
)

@Serializable
data class ScenarioEvent(
    val time: Int,
    val type: String,
    val sender: String,
    val content: String
)

@Serializable
data class PuzzleConfig(
    val id: String,
    val solution: String,
    val location: String
)

data class SmsMessage(
    val sender: String,
    val content: String,
    val second: Int
)

data class PersistentNotebook(
    val notes: Set<String> = emptySet(),
    val flags: Set<String> = emptySet()
)

enum class PhoneApp {
    HOME,
    MESSAGES,
    NOTES,
    SETTINGS,
    SECURE_VAULT,
    GAME_OVER,
    WIN
}

data class GameUiState(
    val loopDuration: Int = 180,
    val timeLeft: Int = 180,
    val currentApp: PhoneApp = PhoneApp.HOME,
    val isLocked: Boolean = false,
    val pinInput: String = "",
    val volatileSms: List<SmsMessage> = emptyList(),
    val persistentNotebook: PersistentNotebook = PersistentNotebook(),
    val brightness: Float = 0.8f,
    val glitch: Boolean = false,
    val vaultInput: String = "",
    val vaultError: String? = null,
    val loopCount: Int = 1
)
