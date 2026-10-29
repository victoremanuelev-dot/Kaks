package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.LocalCustomTheme

@Composable
fun VinylRecord(
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
    sizeDp: Dp = 120.dp,
    centerGradientStart: Long = 0xFFF72585,
    centerGradientEnd: Long = 0xFF7209B7
) {
    val theme = LocalCustomTheme.current
    val infiniteTransition = rememberInfiniteTransition(label = "vinyl_spin")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    val currentRotation = if (isPlaying) rotation else 0f

    Box(
        modifier = modifier
            .size(sizeDp)
            .graphicsLayer {
                rotationZ = currentRotation
            },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = size.minDimension / 2

            // Outer edge shadow
            drawCircle(
                color = Color(0xFF0A0A0E),
                radius = radius,
                center = center
            )

            // Vinyl body
            val vinylBrush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF222228),
                    Color(0xFF141418),
                    Color(0xFF08080C)
                ),
                center = center,
                radius = radius
            )
            drawCircle(
                brush = vinylBrush,
                radius = radius * 0.96f,
                center = center
            )

            // Grooves
            val grooveSteps = 6
            for (i in 1..grooveSteps) {
                val grooveRadius = radius * (0.42f + (i.toFloat() / grooveSteps) * 0.50f)
                drawCircle(
                    color = Color.White.copy(alpha = 0.08f),
                    radius = grooveRadius,
                    center = center,
                    style = Stroke(width = 1.2.dp.toPx())
                )
            }

            // Center Label
            val labelRadius = radius * 0.38f
            drawCircle(
                brush = Brush.linearGradient(
                    colors = listOf(Color(centerGradientStart), Color(centerGradientEnd))
                ),
                radius = labelRadius,
                center = center
            )

            // Center Ring
            drawCircle(
                color = theme.primaryNeon,
                radius = labelRadius,
                center = center,
                style = Stroke(width = 2.dp.toPx())
            )

            // Center Spindle
            drawCircle(
                color = Color(0xFF080C15),
                radius = radius * 0.08f,
                center = center
            )
        }
    }
}
