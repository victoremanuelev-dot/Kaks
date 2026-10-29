package com.example.ui.components

import android.view.SoundEffectConstants
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LocalCustomTheme

/**
 * 10-Foot UI D-Pad focus modifier for Android TV / TV Box and Gamepads.
 * Provides high-contrast glowing neon outline, hardware-accelerated scale,
 * audio click feedback, and complete keyboard/remote key interception.
 */
fun Modifier.tvDpadFocusable(
    onClick: () -> Unit,
    glowColor: Color? = null,
    scaleFactor: Float = 1.05f,
    cornerRadius: Dp = 12.dp,
    borderWidth: Dp = 2.5.dp,
    focusRequester: FocusRequester? = null,
    testTag: String = "tv_dpad_item"
): Modifier = composed {
    val theme = LocalCustomTheme.current
    val view = LocalView.current
    var isFocused by remember { mutableStateOf(false) }

    val activeGlow = glowColor ?: theme.primaryNeon

    val scale by animateFloatAsState(
        targetValue = if (isFocused) scaleFactor else 1.0f,
        animationSpec = tween(120),
        label = "tv_dpad_scale"
    )

    this
        .testTag(testTag)
        .then(if (focusRequester != null) Modifier.focusRequester(focusRequester) else Modifier)
        .scale(scale)
        .onFocusChanged { state ->
            if (state.isFocused && !isFocused) {
                // Subtle sound click when remote moves onto item
                try {
                    view.playSoundEffect(SoundEffectConstants.CLICK)
                } catch (_: Exception) {}
            }
            isFocused = state.isFocused
        }
        .focusable()
        .onKeyEvent { keyEvent ->
            if (keyEvent.type == KeyEventType.KeyDown) {
                when (keyEvent.key) {
                    Key.DirectionCenter,
                    Key.Enter,
                    Key.NumPadEnter,
                    Key.Spacebar,
                    Key.ButtonA -> {
                        try {
                            view.playSoundEffect(SoundEffectConstants.CLICK)
                        } catch (_: Exception) {}
                        onClick()
                        true
                    }
                    else -> false
                }
            } else {
                false
            }
        }
        .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = {
                try {
                    view.playSoundEffect(SoundEffectConstants.CLICK)
                } catch (_: Exception) {}
                onClick()
            }
        )
        .drawBehind {
            if (isFocused) {
                // Outer glowing neon aura
                drawRoundRect(
                    color = activeGlow.copy(alpha = 0.28f),
                    size = size,
                    cornerRadius = CornerRadius((cornerRadius + 4.dp).toPx())
                )
                // Sharp glowing focus ring
                drawRoundRect(
                    color = activeGlow,
                    size = size,
                    cornerRadius = CornerRadius(cornerRadius.toPx()),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = borderWidth.toPx())
                )
            }
        }
}

/**
 * TV 10-foot optimized button with prominent D-pad focus, scale, and sound.
 */
@Composable
fun Tv10FootButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    glowColor: Color? = null,
    containerColor: Color? = null,
    contentColor: Color? = null,
    testTag: String = "tv_button"
) {
    val theme = LocalCustomTheme.current
    var isFocused by remember { mutableStateOf(false) }
    val view = LocalView.current

    val activeGlow = glowColor ?: theme.primaryNeon
    val bg = containerColor ?: theme.primaryNeon
    val fg = contentColor ?: Color.Black

    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.06f else 1.0f,
        animationSpec = tween(120),
        label = "btn_scale"
    )

    Box(
        modifier = modifier
            .testTag(testTag)
            .scale(scale)
            .clip(RoundedCornerShape(10.dp))
            .background(if (isFocused) activeGlow else bg)
            .border(
                width = if (isFocused) 2.5.dp else 1.dp,
                color = if (isFocused) Color.White else Color.Transparent,
                shape = RoundedCornerShape(10.dp)
            )
            .onFocusChanged {
                if (it.isFocused && !isFocused) {
                    try { view.playSoundEffect(SoundEffectConstants.CLICK) } catch (_: Exception) {}
                }
                isFocused = it.isFocused
            }
            .focusable()
            .onKeyEvent { keyEvent ->
                if (keyEvent.type == KeyEventType.KeyDown &&
                    (keyEvent.key == Key.DirectionCenter ||
                     keyEvent.key == Key.Enter ||
                     keyEvent.key == Key.NumPadEnter ||
                     keyEvent.key == Key.ButtonA)
                ) {
                    try { view.playSoundEffect(SoundEffectConstants.CLICK) } catch (_: Exception) {}
                    onClick()
                    true
                } else false
            }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {
                    try { view.playSoundEffect(SoundEffectConstants.CLICK) } catch (_: Exception) {}
                    onClick()
                }
            )
            .padding(horizontal = 14.dp, vertical = 9.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isFocused) Color.Black else fg,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = text,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (isFocused) Color.Black else fg
            )
        }
    }
}

/**
 * 10-foot TV D-Pad focusable icon button for remote controls and keyboards.
 */
@Composable
fun TvFocusableIconButton(
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color? = null,
    containerColor: Color? = null,
    size: Dp = 44.dp,
    iconSize: Dp = 24.dp,
    glowColor: Color? = null,
    testTag: String = "tv_icon_btn"
) {
    val theme = LocalCustomTheme.current
    var isFocused by remember { mutableStateOf(false) }
    val view = LocalView.current

    val activeGlow = glowColor ?: theme.primaryNeon
    val bg = containerColor ?: theme.surfaceVariant
    val iconColor = tint ?: theme.textPrimary

    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.12f else 1.0f,
        animationSpec = tween(120),
        label = "icon_btn_scale"
    )

    Box(
        modifier = modifier
            .testTag(testTag)
            .size(size)
            .scale(scale)
            .clip(CircleShape)
            .background(if (isFocused) activeGlow else bg)
            .border(
                width = if (isFocused) 2.5.dp else 1.dp,
                color = if (isFocused) Color.White else Color.Transparent,
                shape = CircleShape
            )
            .onFocusChanged {
                if (it.isFocused && !isFocused) {
                    try { view.playSoundEffect(SoundEffectConstants.CLICK) } catch (_: Exception) {}
                }
                isFocused = it.isFocused
            }
            .focusable()
            .onKeyEvent { keyEvent ->
                if (keyEvent.type == KeyEventType.KeyDown &&
                    (keyEvent.key == Key.DirectionCenter ||
                     keyEvent.key == Key.Enter ||
                     keyEvent.key == Key.NumPadEnter ||
                     keyEvent.key == Key.ButtonA ||
                     keyEvent.key == Key.Spacebar)
                ) {
                    try { view.playSoundEffect(SoundEffectConstants.CLICK) } catch (_: Exception) {}
                    onClick()
                    true
                } else false
            }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {
                    try { view.playSoundEffect(SoundEffectConstants.CLICK) } catch (_: Exception) {}
                    onClick()
                }
            )
            .drawBehind {
                if (isFocused) {
                    drawCircle(
                        color = activeGlow.copy(alpha = 0.35f),
                        radius = size.toPx() / 2 + 4.dp.toPx()
                    )
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = if (isFocused) Color.Black else iconColor,
            modifier = Modifier.size(iconSize)
        )
    }
}

