package com.example.russianroulette.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.russianroulette.ui.theme.*

@Composable
fun GameButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = Gold,
    enabled: Boolean = true
) {
    val backgroundColor = if (enabled) Black else DarkGrey
    val contentColor = if (enabled) color else Color.Gray
    val borderColor = if (enabled) color else Color.Gray

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .border(2.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable(enabled = enabled) { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text.uppercase(),
            color = contentColor,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            maxLines = 1,
            softWrap = false
        )
    }
}

@Composable
fun ActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isDanger: Boolean = false,
    enabled: Boolean = true
) {
    val brush = if (isDanger) {
        Brush.horizontalGradient(listOf(CrimsonRed, BloodRed))
    } else {
        Brush.horizontalGradient(listOf(Gold, DarkGold))
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(brush)
            .clickable(enabled = enabled) { onClick() }
            .padding(horizontal = 32.dp, vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (isDanger) White else Black,
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}
