package com.dydos.timeloop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ProcessLifecycleOwner
import com.dydos.timeloop.data.ScenarioRepository
import com.dydos.timeloop.ui.GameScreen
import com.dydos.timeloop.ui.LockScreen
import com.dydos.timeloop.vm.GameViewModel

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: GameViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val scenario = ScenarioRepository(applicationContext).loadScenario()
        viewModel = GameViewModel(scenario)

        ProcessLifecycleOwner.get().lifecycle.addObserver(LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_STOP -> viewModel.pauseTimer()
                Lifecycle.Event.ON_START -> viewModel.resumeTimer()
                else -> Unit
            }
        })

        setContent {
            val state by viewModel.uiState.collectAsState()
            LaunchedEffect(Unit) {
                viewModel.startLoop()
            }

            if (state.isLocked) {
                LockScreen(
                    timeLeft = state.timeLeft,
                    pin = state.pinInput,
                    onPinChange = viewModel::updatePinInput,
                    onSubmit = viewModel::submitPin
                )
            } else {
                GameScreen(
                    state = state,
                    onOpenApp = viewModel::openApp,
                    onVaultInput = viewModel::updateVaultInput,
                    onVaultSubmit = viewModel::submitVaultCode,
                    onBrightnessChange = viewModel::onBrightnessChange,
                    onResetLoop = viewModel::manualReset
                )
            }
        }
    }
}
