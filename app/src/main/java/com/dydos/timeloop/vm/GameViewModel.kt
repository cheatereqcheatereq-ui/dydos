package com.dydos.timeloop.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dydos.timeloop.data.GameUiState
import com.dydos.timeloop.data.PersistentNotebook
import com.dydos.timeloop.data.PhoneApp
import com.dydos.timeloop.data.PuzzleConfig
import com.dydos.timeloop.data.ScenarioConfig
import com.dydos.timeloop.data.ScenarioEvent
import com.dydos.timeloop.data.SmsMessage
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GameViewModel(
    private val scenario: ScenarioConfig
) : ViewModel() {
    private val phonePin = "1984"

    private val _uiState = MutableStateFlow(GameUiState(loopDuration = scenario.loop_duration, timeLeft = scenario.loop_duration))
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var isPaused = false

    fun startLoop() {
        if (timerJob != null) return
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                if (isPaused) continue
                tick()
            }
        }
    }

    private fun tick() {
        val state = _uiState.value
        val elapsed = state.loopDuration - state.timeLeft + 1
        deliverEventsAt(elapsed)

        if (state.timeLeft <= 1) {
            onLoopTimeout()
            return
        }

        _uiState.value = state.copy(timeLeft = state.timeLeft - 1)
    }

    private fun deliverEventsAt(second: Int) {
        scenario.events.filter { it.time == second && it.type == "incoming_sms" }.forEach { event ->
            appendMessage(event)
            if (event.content.contains("1984") || event.content.contains("Orwella")) {
                addPersistentNote("Wskazówka: Kod sejfu może być związany z rokiem urodzenia Orwella (1984).")
                unlockFlag("hasDiscoveredCodeHint")
            }
        }
    }

    private fun appendMessage(event: ScenarioEvent) {
        val current = _uiState.value
        _uiState.value = current.copy(
            volatileSms = current.volatileSms + SmsMessage(
                sender = event.sender,
                content = event.content,
                second = event.time
            )
        )
    }

    fun openApp(app: PhoneApp) {
        _uiState.value = _uiState.value.copy(currentApp = app)
    }

    fun updatePinInput(input: String) {
        _uiState.value = _uiState.value.copy(pinInput = input.take(4))
    }

    fun submitPin() {
        if (_uiState.value.pinInput == phonePin) {
            _uiState.value = _uiState.value.copy(isLocked = false, pinInput = "")
        }
    }

    fun pauseTimer() {
        isPaused = true
    }

    fun resumeTimer() {
        isPaused = false
    }

    private fun onLoopTimeout() {
        _uiState.value = _uiState.value.copy(currentApp = PhoneApp.GAME_OVER, glitch = true)
        viewModelScope.launch {
            delay(1200)
            resetLoop()
        }
    }

    fun manualReset() {
        _uiState.value = _uiState.value.copy(glitch = true)
        viewModelScope.launch {
            delay(600)
            resetLoop()
        }
    }

    private fun resetLoop() {
        val persistent = _uiState.value.persistentNotebook
        val nextCount = _uiState.value.loopCount + 1
        _uiState.value = GameUiState(
            loopDuration = scenario.loop_duration,
            timeLeft = scenario.loop_duration,
            persistentNotebook = persistent,
            loopCount = nextCount
        )
    }

    fun onBrightnessChange(value: Float) {
        _uiState.value = _uiState.value.copy(brightness = value)
    }

    fun updateVaultInput(input: String) {
        _uiState.value = _uiState.value.copy(vaultInput = input.take(4), vaultError = null)
    }

    fun submitVaultCode() {
        val puzzle: PuzzleConfig = scenario.puzzles.firstOrNull { it.id == "door_code" } ?: return
        if (_uiState.value.vaultInput == puzzle.solution) {
            unlockFlag("unlockedVault")
            addPersistentNote("Kod 1984 zadziałał. Dowody wysłane.")
            _uiState.value = _uiState.value.copy(currentApp = PhoneApp.WIN)
        } else {
            _uiState.value = _uiState.value.copy(vaultError = "Błędny kod")
        }
    }

    private fun addPersistentNote(note: String) {
        val state = _uiState.value
        _uiState.value = state.copy(
            persistentNotebook = state.persistentNotebook.copy(notes = state.persistentNotebook.notes + note)
        )
    }

    private fun unlockFlag(flag: String) {
        val state = _uiState.value
        _uiState.value = state.copy(
            persistentNotebook = state.persistentNotebook.copy(flags = state.persistentNotebook.flags + flag)
        )
    }
}
