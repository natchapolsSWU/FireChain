package com.example.russianroulette.ui.screens

import android.view.MotionEvent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.russianroulette.ui.components.ActionButton
import com.example.russianroulette.ui.components.CylinderView
import com.example.russianroulette.ui.components.GameButton
import com.example.russianroulette.ui.theme.*
import com.example.russianroulette.viewmodel.PartyModeViewModel

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PartyModeScreen(
    onBack: () -> Unit,
    viewModel: PartyModeViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    
    // Setup dialog shows if no players are configured
    val showSetup = state.players.isEmpty()


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Black)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            GameButton(text = "Exit", onClick = onBack)
            Text(
                "PARTY STRATEGY",
                color = Gold,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black
            )
            GameButton(text = "Reset", onClick = { viewModel.reset() })
        }

        // Player Info
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            itemsIndexed(state.players) { index, player ->
                val isCurrent = index == state.currentPlayerIndex
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(if (player.isAlive) DarkGrey else Color.DarkGray)
                            .border(
                                2.dp,
                                if (isCurrent) Gold else if (player.isAlive) Color.Gray else CrimsonRed,
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(player.id.toString(), color = if (isCurrent) Gold else White)
                    }
                }
            }
        }

        // Pending Shots / Status
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                state.gameMessage,
                color = if (state.gameMessage.contains("BOOM")) CrimsonRed else Gold,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            if (state.skipCount > 0) {
                Text(
                    "PENDING SHOTS: ${state.skipCount}",
                    color = CrimsonRed,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black
                )
            }
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

        // Strategic Actions
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
                if (state.isShootingSequence) {
                    // Manual Trigger Button during shooting sequence
                    val targetName = state.manualTargetIndex?.let { state.players[it].name } ?: "Target"
                    ActionButton(
                        text = "PULL TRIGGER: $targetName",
                        onClick = { viewModel.triggerManualShot() },
                        isDanger = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (state.canPass) {
                            ActionButton(
                                text = "PASS",
                                onClick = { viewModel.pass() },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    ActionButton(
                        text = "FIRE CHAIN",
                        onClick = { viewModel.fire() },
                        isDanger = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

            }
        }

    // Immediate Peek Pop-up (Before Turn Change)
    if (state.showPeekPopup) {
        AlertDialog(
            onDismissRequest = { viewModel.onPeekDismissed() },
            containerColor = Black,
            title = { Text("PEEK GRANTED!", color = Gold, fontWeight = FontWeight.Black) },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text("You have 1 chance to see the cylinder.", color = White)
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    if (state.isRevealingInPopup) {
                        // Show cylinder map
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            state.chamberStates.forEachIndexed { index, hasBullet ->
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(if (hasBullet) CrimsonRed else Color.DarkGray)
                                        .border(1.dp, if (index == 0) Gold else Color.Transparent, CircleShape)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("INDEX 0 IS NEXT", color = Gold, fontSize = 10.sp)
                    } else {
                        // Reveal Button
                        Box(
                            modifier = Modifier
                                .height(60.dp)
                                .fillMaxWidth()
                                .clip(androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
                                .background(DarkGold)
                                .pointerInteropFilter {
                                    when (it.action) {
                                        MotionEvent.ACTION_DOWN -> { viewModel.setPeekingInPopup(true); true }
                                        MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> { viewModel.setPeekingInPopup(false); true }
                                        else -> false
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text("HOLD TO REVEAL", color = Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.onPeekDismissed() },
                    colors = ButtonDefaults.buttonColors(containerColor = Gold, contentColor = Black)
                ) {
                    Text("END TURN")
                }
            }
        )
    }



    // Peek Overlay
    if (state.isPeeking) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.9f)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("CYLINDER MAP", color = Gold, fontSize = 24.sp, fontWeight = FontWeight.Black)
                Spacer(modifier = Modifier.height(20.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    state.chamberStates.forEachIndexed { index, hasBullet ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(index.toString(), color = Color.Gray, fontSize = 10.sp)
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(if (hasBullet) CrimsonRed else Color.DarkGray)
                                    .border(2.dp, if (index == 0) Gold else Color.Transparent, CircleShape)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text("INDEX 0 IS NEXT", color = Gold, fontSize = 12.sp)
            }
        }
    }

    if (showSetup) {
        PartySetupDialog(
            onConfirm = { p, c, b ->
                viewModel.setupGame(p, c, b)
            },
            onDismiss = onBack
        )
    }
}

@Composable
fun PartySetupDialog(
    onConfirm: (Int, Int, Int) -> Unit,
    onDismiss: () -> Unit
) {
    var players by remember { mutableStateOf("4") }
    var chambers by remember { mutableStateOf("6") }
    var bullets by remember { mutableStateOf("1") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Black,
        title = { Text("PARTY SETUP", color = Gold, fontWeight = FontWeight.Black) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Players Input
                OutlinedTextField(
                    value = players,
                    onValueChange = { if (it.length <= 1) players = it.filter { c -> c.isDigit() } },
                    label = { Text("Players (2-8)", color = Gold) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Gold,
                        unfocusedBorderColor = Color.Gray,
                        focusedLabelColor = Gold,
                        unfocusedLabelColor = Color.Gray,
                        focusedTextColor = White,
                        unfocusedTextColor = White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                // Chambers Input
                OutlinedTextField(
                    value = chambers,
                    onValueChange = { if (it.length <= 2) chambers = it.filter { c -> c.isDigit() } },
                    label = { Text("Total Slots", color = Gold) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Gold,
                        unfocusedBorderColor = Color.Gray,
                        focusedTextColor = White,
                        unfocusedTextColor = White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                // Bullets Input
                OutlinedTextField(
                    value = bullets,
                    onValueChange = { if (it.length <= 2) bullets = it.filter { c -> c.isDigit() } },
                    label = { Text("Bullets (Grouped)", color = Gold) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CrimsonRed,
                        unfocusedBorderColor = Color.Gray,
                        focusedTextColor = White,
                        unfocusedTextColor = White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { 
                    val p = players.toIntOrNull() ?: 2
                    val c = chambers.toIntOrNull() ?: 6
                    val b = bullets.toIntOrNull() ?: 1
                    onConfirm(p.coerceIn(2, 8), c.coerceIn(2, 20), b.coerceIn(1, c - 1)) 
                },
                colors = ButtonDefaults.buttonColors(containerColor = Gold, contentColor = Black)
            ) {
                Text("LOAD CYLINDER", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("BACK", color = Gold)
            }
        }
    )
}

