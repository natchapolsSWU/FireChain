package com.example.russianroulette.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.russianroulette.ui.components.ActionButton
import com.example.russianroulette.ui.theme.Black
import com.example.russianroulette.ui.theme.DarkGold
import com.example.russianroulette.ui.theme.Gold

@Composable
fun HomeScreen(
    onStartNormal: () -> Unit,
    onStartParty: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Black)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Title
        Text(
            text = "FIRECHAIN",
            color = Gold,
            fontSize = 48.sp,
            fontWeight = FontWeight.Black,
            lineHeight = 56.sp,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "HIGH STAKES PARTY GAME",
            color = Color.Gray,
            fontSize = 14.sp,
            letterSpacing = 4.sp
        )

        Spacer(modifier = Modifier.height(80.dp))

        // Menu Buttons
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ActionButton(
                text = "NORMAL MODE",
                onClick = onStartNormal,
                modifier = Modifier.fillMaxWidth()
            )
            
            ActionButton(
                text = "PARTY MODE",
                onClick = onStartParty,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(40.dp))
        
        Text(
            text = "SWIPE TO SPIN THE CYLINDER",
            color = DarkGold,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
