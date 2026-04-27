package com.example.russianroulette.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.russianroulette.ui.theme.DarkGold
import com.example.russianroulette.ui.theme.Gold
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun CylinderView(
    modifier: Modifier = Modifier,
    chamberCount: Int = 6,
    currentRotation: Float = 0f,
    onSpin: (Float) -> Unit = {}
) {
    val rotationAnimatable = remember { Animatable(currentRotation) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(currentRotation) {
        rotationAnimatable.animateTo(
            targetValue = currentRotation,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDrag = { change, dragAmount ->
                        change.consume()
                        // Calculate rotation based on drag
                        // Simple horizontal drag to spin
                        val rotationDelta = dragAmount.x * 0.5f
                        onSpin(rotationDelta)
                    }
                )
            }
            .graphicsLayer(rotationZ = rotationAnimatable.value)
    ) {
        Canvas(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = size.minDimension / 2
            val chamberRadius = radius * 0.25f
            val distanceToChamber = radius * 0.65f

            // Draw outer cylinder
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Gold, DarkGold, Color.Black),
                    center = center,
                    radius = radius
                ),
                radius = radius,
                center = center
            )

            // Draw outer stroke
            drawCircle(
                color = Gold,
                radius = radius,
                center = center,
                style = Stroke(width = 4.dp.toPx())
            )

            // Draw chambers
            for (i in 0 until chamberCount) {
                val angle = (i * 2 * PI / chamberCount).toFloat()
                val chamberCenter = Offset(
                    x = center.x + distanceToChamber * cos(angle),
                    y = center.y + distanceToChamber * sin(angle)
                )

                // Chamber hole
                drawCircle(
                    color = Color.Black,
                    radius = chamberRadius,
                    center = chamberCenter
                )
                
                // Chamber rim
                drawCircle(
                    color = DarkGold,
                    radius = chamberRadius,
                    center = chamberCenter,
                    style = Stroke(width = 2.dp.toPx())
                )
            }
            
            // Central axis
            drawCircle(
                color = DarkGold,
                radius = chamberRadius * 0.5f,
                center = center
            )
        }
    }
}
