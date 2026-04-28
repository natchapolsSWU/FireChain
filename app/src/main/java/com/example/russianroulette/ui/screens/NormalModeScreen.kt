package com.example.russianroulette.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.russianroulette.ui.components.ActionButton
import com.example.russianroulette.ui.components.CylinderView
import com.example.russianroulette.ui.components.GameButton
import com.example.russianroulette.ui.theme.Black
import com.example.russianroulette.ui.theme.CrimsonRed
import com.example.russianroulette.ui.theme.Gold
import com.example.russianroulette.viewmodel.GameResult
import com.example.russianroulette.viewmodel.NormalModeViewModel

@Composable
fun NormalModeScreen(
    onBack: () -> Unit,
    viewModel: NormalModeViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var showSettings by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Black)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            GameButton(text = "Back", onClick = onBack)
            Text(
                "NORMAL MODE",
                color = Gold,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                maxLines = 1
            )
            GameButton(text = "Reset", onClick = { showSettings = true })
        }

        // Status Text
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = when {
                    state.isGameOver -> "ALL BULLETS SPENT"
                    state.lastResult == GameResult.Hit -> "BOOM! HIT"
                    state.lastResult == GameResult.Safe -> "CLICK! SAFE"
                    else -> "READY TO FIRE"
                },
                color = when {
                    state.isGameOver -> CrimsonRed
                    state.lastResult == GameResult.Hit -> CrimsonRed
                    state.lastResult == GameResult.Safe -> Color.Green
                    else -> Color.White
                },

                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                "Chambers: ${state.chambers} | Bullets Left: ${state.bulletsRemaining}",
                color = Color.Gray,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

        }

        // Cylinder & Manual Rotate
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            GameButton(
                text = "<",
                onClick = { viewModel.onManualSpin(-1) },
                enabled = !state.hasSpunThisTurn && !state.isGameOver
            )

            CylinderView(
                modifier = Modifier.size(240.dp),
                chamberCount = state.chambers,
                currentRotation = state.rotation
            )

            GameButton(
                text = ">",
                onClick = { viewModel.onManualSpin(1) },
                enabled = !state.hasSpunThisTurn && !state.isGameOver
            )
        }

        // Actions
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (state.isGameOver) {
                ActionButton(
                    text = "RETRY",
                    onClick = { viewModel.reset() },
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ActionButton(
                        text = "SPIN",
                        onClick = { viewModel.randomizeSpin() },
                        modifier = Modifier.weight(1f)
                    )
                    ActionButton(
                        text = "FIRE",
                        onClick = { viewModel.fire() },
                        isDanger = true,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

    }

    if (showSettings) {
        SettingsDialog(
            currentChambers = state.chambers,
            currentBullets = state.bullets,
            onDismiss = { showSettings = false },
            onConfirm = { chambers, bullets ->
                viewModel.updateSettings(chambers, bullets)
                showSettings = false
            }
        )
    }
}

@Composable
fun SettingsDialog(
    currentChambers: Int,
    currentBullets: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int, Int) -> Unit
) {
    var chambers by remember { mutableStateOf(currentChambers.toFloat()) }
    var bullets by remember { mutableStateOf(currentBullets.toFloat()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Black,
        title = { Text("GAME SETTINGS", color = Gold, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Column {
                    Text("Chambers: ${chambers.toInt()}", color = Color.White)
                    Slider(
                        value = chambers,
                        onValueChange = { chambers = it },
                        valueRange = 2f..12f,
                        steps = 10,
                        colors = SliderDefaults.colors(thumbColor = Gold, activeTrackColor = Gold)
                    )
                }
                Column {
                    Text("Bullets: ${bullets.toInt()}", color = Color.White)
                    Slider(
                        value = bullets,
                        onValueChange = { bullets = it },
                        valueRange = 1f..(chambers - 1),
                        steps = (chambers - 2).toInt().coerceAtLeast(0),
                        colors = SliderDefaults.colors(thumbColor = CrimsonRed, activeTrackColor = CrimsonRed)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(chambers.toInt(), bullets.toInt()) },
                colors = ButtonDefaults.buttonColors(containerColor = Gold, contentColor = Black)
            ) {
                Text("APPLY")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCEL", color = Gold)
            }
        }
    )
}
