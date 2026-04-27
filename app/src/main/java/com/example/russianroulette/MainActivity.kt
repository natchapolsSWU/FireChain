package com.example.russianroulette

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.russianroulette.ui.screens.HomeScreen
import com.example.russianroulette.ui.screens.NormalModeScreen
import com.example.russianroulette.ui.screens.PartyModeScreen
import com.example.russianroulette.ui.theme.RussianRouletteTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RussianRouletteTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    RussianRouletteApp()
                }
            }
        }
    }
}

enum class Screen { Home, NormalMode, PartyMode }

@Composable
fun RussianRouletteApp() {
    var currentScreen by remember { mutableStateOf(Screen.Home) }

    when (currentScreen) {
        Screen.Home -> HomeScreen(
            onStartNormal = { currentScreen = Screen.NormalMode },
            onStartParty = { currentScreen = Screen.PartyMode }
        )
        Screen.NormalMode -> NormalModeScreen(
            onBack = { currentScreen = Screen.Home }
        )
        Screen.PartyMode -> PartyModeScreen(
            onBack = { currentScreen = Screen.Home }
        )
    }
}