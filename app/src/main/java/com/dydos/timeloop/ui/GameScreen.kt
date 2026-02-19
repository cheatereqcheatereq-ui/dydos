package com.dydos.timeloop.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dydos.timeloop.data.GameUiState
import com.dydos.timeloop.data.PhoneApp

@Composable
fun GameScreen(
    state: GameUiState,
    onOpenApp: (PhoneApp) -> Unit,
    onVaultInput: (String) -> Unit,
    onVaultSubmit: () -> Unit,
    onBrightnessChange: (Float) -> Unit,
    onResetLoop: () -> Unit
) {
    val containerColor = if (state.glitch) Color(0xFF5A0000) else Color(0xFF101820)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(containerColor.copy(alpha = state.brightness.coerceIn(0.2f, 1f)))
            .padding(12.dp)
    ) {
        Text(
            text = "Loop ${state.loopCount}  |  ${formatTimer(state.timeLeft)}",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )
        Spacer(Modifier.height(8.dp))

        when (state.currentApp) {
            PhoneApp.HOME -> HomeScreen(onOpenApp)
            PhoneApp.MESSAGES -> MessagesScreen(state, onOpenApp)
            PhoneApp.NOTES -> NotesScreen(state, onOpenApp)
            PhoneApp.SETTINGS -> SettingsScreen(state, onBrightnessChange, onResetLoop, onOpenApp)
            PhoneApp.SECURE_VAULT -> SecureVaultScreen(state, onVaultInput, onVaultSubmit, onOpenApp)
            PhoneApp.GAME_OVER -> GameOverScreen()
            PhoneApp.WIN -> WinScreen(onOpenApp)
        }
    }
}

@Composable
private fun HomeScreen(onOpenApp: (PhoneApp) -> Unit) {
    Text("Fake OS Launcher", color = Color.White)
    Spacer(Modifier.height(12.dp))
    AppIcon("Messages") { onOpenApp(PhoneApp.MESSAGES) }
    AppIcon("Notes") { onOpenApp(PhoneApp.NOTES) }
    AppIcon("SecureVault") { onOpenApp(PhoneApp.SECURE_VAULT) }
    AppIcon("Settings") { onOpenApp(PhoneApp.SETTINGS) }
}

@Composable
private fun AppIcon(label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .background(Color(0xFF1E2A38), RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.size(6.dp))
        Text(label, color = Color.White)
    }
}

@Composable
private fun MessagesScreen(state: GameUiState, onOpenApp: (PhoneApp) -> Unit) {
    Header("Messages") { onOpenApp(PhoneApp.HOME) }
    if (state.volatileSms.isEmpty()) {
        Text("Brak wiadomości", color = Color.LightGray)
    } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(state.volatileSms) { sms ->
                Column(Modifier.background(Color(0xFF1C2330), RoundedCornerShape(10.dp)).padding(10.dp)) {
                    Text("${sms.sender} • t+${sms.second}s", color = Color(0xFF9BC1FF), fontWeight = FontWeight.Bold)
                    Text(sms.content, color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun NotesScreen(state: GameUiState, onOpenApp: (PhoneApp) -> Unit) {
    Header("Player Notes (persistent)") { onOpenApp(PhoneApp.HOME) }
    if (state.persistentNotebook.notes.isEmpty()) {
        Text("Brak notatek. Odkrywaj wskazówki.", color = Color.LightGray)
    } else {
        state.persistentNotebook.notes.forEach {
            Text("• $it", color = Color.White)
        }
    }
}

@Composable
private fun SettingsScreen(
    state: GameUiState,
    onBrightnessChange: (Float) -> Unit,
    onResetLoop: () -> Unit,
    onOpenApp: (PhoneApp) -> Unit
) {
    Header("Settings") { onOpenApp(PhoneApp.HOME) }
    Text("Brightness", color = Color.White)
    Slider(value = state.brightness, onValueChange = onBrightnessChange)
    Spacer(Modifier.height(8.dp))
    Button(onClick = onResetLoop) {
        Text("Manual Reset Loop")
    }
}

@Composable
private fun SecureVaultScreen(
    state: GameUiState,
    onVaultInput: (String) -> Unit,
    onVaultSubmit: () -> Unit,
    onOpenApp: (PhoneApp) -> Unit
) {
    Header("SecureVault") { onOpenApp(PhoneApp.HOME) }
    Text("Wpisz 4-cyfrowy kod", color = Color.White)
    OutlinedTextField(
        value = state.vaultInput,
        onValueChange = { onVaultInput(it.filter(Char::isDigit)) },
        label = { Text("PIN") }
    )
    state.vaultError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
    Spacer(Modifier.height(8.dp))
    Button(onClick = onVaultSubmit) { Text("Unlock") }
}

@Composable
private fun GameOverScreen() {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize()) {
        Text("GAME OVER", color = Color.Red, fontSize = 36.sp, fontWeight = FontWeight.Bold)
        Text("Wybuch. Reset pętli...", color = Color.White)
    }
}

@Composable
private fun WinScreen(onOpenApp: (PhoneApp) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize()) {
        Text("Dowody wysłane", color = Color.Green, fontSize = 30.sp, fontWeight = FontWeight.Bold)
        Text("Pętla przerwana", color = Color.White)
        Spacer(Modifier.height(10.dp))
        Button(onClick = { onOpenApp(PhoneApp.HOME) }) { Text("Wróć") }
    }
}

@Composable
private fun Header(title: String, onBack: () -> Unit) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(title, color = Color.White, fontWeight = FontWeight.Bold)
        Text("← Home", color = Color(0xFF9BC1FF), modifier = Modifier.clickable { onBack() })
    }
    Spacer(Modifier.height(8.dp))
}

private fun formatTimer(time: Int): String {
    val m = time / 60
    val s = time % 60
    return "%02d:%02d".format(m, s)
}
