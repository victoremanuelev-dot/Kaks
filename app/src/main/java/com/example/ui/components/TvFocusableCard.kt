package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.LocalCustomTheme

@Composable
fun TvFocusableCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(16.dp),
    glowColorOverride: Color? = null,
    borderWidth: Dp = 2.5.dp,
    testTag: String = "tv_card",
    content: @Composable BoxScope.(Boolean) -> Unit
) {
    val theme = LocalCustomTheme.current
    var isFocused by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.04f else 1.0f,
        animationSpec = tween(durationMillis = 180),
        label = "tv_card_scale"
    )

    val activeGlowColor = glowColorOverride ?: theme.primaryNeon

    val border = if (isFocused) {
        BorderStroke(borderWidth, activeGlowColor)
    } else {
        BorderStroke(1.dp, theme.cardBorder)
    }

    val cardColor = if (isFocused) {
        theme.surfaceVariant
    } else {
        theme.surface
    }

    Card(
        shape = shape,
        border = border,
        colors = CardDefaults.cardColors(containerColor = cardColor),
        modifier = modifier
            .testTag(testTag)
            .scale(scale)
            .onFocusChanged { focusState ->
                isFocused = focusState.isFocused
            }
            .focusable()
            .onKeyEvent { keyEvent ->
                if (keyEvent.type == KeyEventType.KeyDown &&
                    (keyEvent.key == Key.DirectionCenter ||
                     keyEvent.key == Key.Enter ||
                     keyEvent.key == Key.NumPadEnter)
                ) {
                    onClick()
                    true
                } else {
                    false
                }
            }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .drawBehind {
                if (isFocused) {
                    // Outer neon aura glow
                    drawRoundRect(
                        color = activeGlowColor.copy(alpha = 0.22f),
                        size = size,
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(18.dp.toPx())
                    )
                }
            }
    ) {
        Box(content = { content(isFocused) })
    }
}
