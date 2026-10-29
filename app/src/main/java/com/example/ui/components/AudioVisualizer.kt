package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.ui.theme.LocalCustomTheme
import kotlin.math.sin

@Composable
fun AudioVisualizer(
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
    barCount: Int = 24,
    primaryColor: Color? = null,
    secondaryColor: Color? = null
) {
    val theme = LocalCustomTheme.current
    val color1 = primaryColor ?: theme.primaryNeon
    val color2 = secondaryColor ?: theme.secondaryNeon

    val infiniteTransition = rememberInfiniteTransition(label = "audio_bars_anim")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val totalWidth = size.width
        val maxHeight = size.height
        val gap = 4.dp.toPx()
        val barWidth = (totalWidth - (barCount - 1) * gap) / barCount

        for (i in 0 until barCount) {
            val normalizedIndex = i.toFloat() / barCount
            val dynamicFactor = if (isPlaying) {
                val wave1 = (sin((phase + normalizedIndex * 4f).toDouble()) + 1) / 2
                val wave2 = (sin((phase * 1.5f + normalizedIndex * 8f).toDouble()) + 1) / 2
                ((wave1 * 0.6 + wave2 * 0.4) * 0.85 + 0.15).toFloat()
            } else {
                0.12f
            }

            val barHeight = maxHeight * dynamicFactor.coerceIn(0.08f, 1.0f)
            val left = i * (barWidth + gap)
            val top = maxHeight - barHeight

            val brush = Brush.verticalGradient(
                colors = listOf(color2, color1),
                startY = top,
                endY = maxHeight
            )

            drawRoundRect(
                brush = brush,
                topLeft = Offset(left, top),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
            )
        }
    }
}
