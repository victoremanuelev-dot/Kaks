package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.MainTab
import com.example.ui.theme.LocalCustomTheme
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TopMenuBar(
    selectedTab: MainTab,
    onTabSelected: (MainTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val theme = LocalCustomTheme.current
    var currentTimeString by remember { mutableStateOf("10:09 PM") }

    LaunchedEffect(Unit) {
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        while (true) {
            currentTimeString = sdf.format(Date())
            kotlinx.coroutines.delay(30000)
        }
    }

    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val isCompact = maxWidth < 720.dp

        if (isCompact) {
            // MOBILE COMPACT RESPONSIVE TOP BAR
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                theme.background,
                                theme.background.copy(alpha = 0.90f)
                            )
                        )
                    )
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Top header row: Brand logo + Time & Mic
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    Brush.linearGradient(
                                        listOf(theme.primaryNeon, theme.secondaryNeon)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Tv,
                                contentDescription = "Media Center",
                                tint = Color.Black,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Column {
                            Text(
                                text = "MEDIA CENTER",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black,
                                color = theme.primaryNeon,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "TV & MOBILE",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = theme.textSecondary
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(theme.surface)
                                .border(1.dp, theme.cardBorder, RoundedCornerShape(12.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF00F5D4))
                            )
                            Text(
                                text = "MIC ON",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = theme.textPrimary
                            )
                        }

                        Text(
                            text = currentTimeString,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = theme.textPrimary
                        )
                    }
                }

                // Scrollable Nav Tabs Row (Fluid on Mobile)
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 2.dp, vertical = 2.dp)
                ) {
                    items(MainTab.values()) { tab ->
                        NavTabItem(
                            tab = tab,
                            isSelected = tab == selectedTab,
                            isCompact = true,
                            onClick = { onTabSelected(tab) }
                        )
                    }
                }
            }
        } else {
            // WIDE SCREEN / TV BOX 10-FOOT TOP BAR
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                theme.background.copy(alpha = 0.95f),
                                theme.background.copy(alpha = 0.60f)
                            )
                        )
                    )
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // App Brand
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(theme.primaryNeon, theme.secondaryNeon)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tv,
                            contentDescription = "Media Center TV",
                            tint = Color.Black,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Column {
                        Text(
                            text = "MEDIA CENTER",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = theme.primaryNeon,
                            letterSpacing = 1.2.sp
                        )
                        Text(
                            text = "TV BOX ENTERTAINMENT",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = theme.textSecondary,
                            letterSpacing = 1.sp
                        )
                    }
                }

                // Center Tabs
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MainTab.values().forEach { tab ->
                        NavTabItem(
                            tab = tab,
                            isSelected = tab == selectedTab,
                            isCompact = false,
                            onClick = { onTabSelected(tab) }
                        )
                    }
                }

                // Right Status
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(theme.surface)
                            .border(1.dp, theme.cardBorder, RoundedCornerShape(20.dp))
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF00F5D4))
                        )
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Mic Pronto",
                            tint = theme.primaryNeon,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "MIC ATIVO",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = theme.textPrimary
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.Wifi,
                        contentDescription = "Wi-Fi",
                        tint = theme.primaryNeon,
                        modifier = Modifier.size(20.dp)
                    )

                    Text(
                        text = currentTimeString,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = theme.textPrimary
                    )
                }
            }
        }
    }
}

@Composable
private fun NavTabItem(
    tab: MainTab,
    isSelected: Boolean,
    isCompact: Boolean = false,
    onClick: () -> Unit
) {
    val theme = LocalCustomTheme.current
    var isFocused by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.06f else if (isSelected) 1.02f else 1.0f,
        animationSpec = tween(120),
        label = "tab_scale"
    )

    val bgColor by animateColorAsState(
        targetValue = when {
            isFocused -> theme.primaryNeon
            isSelected -> theme.surfaceVariant
            else -> Color.Transparent
        },
        label = "tab_bg"
    )

    val contentColor by animateColorAsState(
        targetValue = when {
            isFocused -> Color.Black
            isSelected -> theme.primaryNeon
            else -> theme.textSecondary
        },
        label = "tab_content_color"
    )

    val icon = when (tab) {
        MainTab.HOME -> Icons.Default.Home
        MainTab.JUKEBOX -> Icons.Default.MusicNote
        MainTab.KARAOKE -> Icons.Default.MicExternalOn
        MainTab.VIDEOS -> Icons.Default.PlayCircle
        MainTab.GAMES -> Icons.Default.SportsEsports
        MainTab.SETTINGS -> Icons.Default.Settings
    }

    Box(
        modifier = Modifier
            .testTag("nav_tab_${tab.name.lowercase()}")
            .scale(scale)
            .clip(RoundedCornerShape(10.dp))
            .background(bgColor)
            .border(
                border = if (isFocused) {
                    BorderStroke(2.dp, Color.White)
                } else if (isSelected) {
                    BorderStroke(1.5.dp, theme.primaryNeon)
                } else {
                    BorderStroke(0.dp, Color.Transparent)
                },
                shape = RoundedCornerShape(10.dp)
            )
            .onFocusChanged { isFocused = it.isFocused }
            .focusable()
            .onKeyEvent { keyEvent ->
                if (keyEvent.type == KeyEventType.KeyDown &&
                    (keyEvent.key == Key.DirectionCenter ||
                     keyEvent.key == Key.Enter ||
                     keyEvent.key == Key.NumPadEnter)
                ) {
                    onClick()
                    true
                } else false
            }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(
                horizontal = if (isCompact) 12.dp else 16.dp,
                vertical = if (isCompact) 6.dp else 9.dp
            ),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = tab.title,
                tint = contentColor,
                modifier = Modifier.size(if (isCompact) 16.dp else 18.dp)
            )
            Text(
                text = tab.title,
                fontSize = if (isCompact) 13.sp else 14.sp,
                fontWeight = if (isSelected || isFocused) FontWeight.Bold else FontWeight.Medium,
                color = contentColor
            )
        }
    }
}
