package com.example.russianroulette.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Gold,
    secondary = CrimsonRed,
    tertiary = DarkGold,
    background = Black,
    surface = DarkGrey,
    onPrimary = Black,
    onSecondary = White,
    onTertiary = Black,
    onBackground = White,
    onSurface = White,
    error = BloodRed
)

private val LightColorScheme = DarkColorScheme // Keep it premium even in light mode

@Composable
fun RussianRouletteTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is disabled to maintain the premium brand identity
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else DarkColorScheme // Force dark for now or keep same


    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}