package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppThemeMode
import com.example.ui.components.TvFocusableCard
import com.example.ui.theme.LocalCustomTheme
import com.example.viewmodel.MediaCenterUiState

@Composable
fun SettingsScreen(
    uiState: MediaCenterUiState,
    onSetTheme: (AppThemeMode) -> Unit,
    onCleanCache: () -> Unit,
    onToggleParentalLock: () -> Unit,
    modifier: Modifier = Modifier
) {
    val theme = LocalCustomTheme.current
    val scrollState = rememberScrollState()

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isCompact = maxWidth < 720.dp

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(
                    horizontal = if (isCompact) 14.dp else 24.dp,
                    vertical = if (isCompact) 10.dp else 14.dp
                ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // TOP HEADER
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "⚙️ CONFIGURAÇÕES & SISTEMA",
                        fontSize = if (isCompact) 17.sp else 20.sp,
                        fontWeight = FontWeight.Black,
                        color = theme.textPrimary
                    )
                    Text(
                        text = "Personalização de temas, limpeza e periféricos",
                        fontSize = 11.sp,
                        color = theme.textSecondary
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(theme.surfaceVariant)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Android TV & Mobile",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = theme.primaryNeon
                    )
                }
            }

            // 1. SELETOR DE TEMAS E TEMPLATES VISUAIS
            Card(
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, theme.primaryNeon.copy(alpha = 0.5f)),
                colors = CardDefaults.cardColors(containerColor = theme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "🎨 PERSONALIZAÇÃO & TROCA DE TEMPLATE",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = theme.primaryNeon,
                        letterSpacing = 1.sp
                    )

                    if (isCompact) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            AppThemeMode.values().forEach { mode ->
                                val isSelected = uiState.themeMode == mode
                                TvFocusableCard(
                                    onClick = { onSetTheme(mode) },
                                    modifier = Modifier.fillMaxWidth().height(75.dp),
                                    glowColorOverride = if (isSelected) theme.primaryNeon else theme.cardBorder,
                                    testTag = "theme_option_${mode.name.lowercase()}"
                                ) { isFocused ->
                                    Row(
                                        modifier = Modifier.fillMaxSize().padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = mode.displayName,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) theme.primaryNeon else theme.textPrimary
                                            )
                                            Text(
                                                text = mode.description,
                                                fontSize = 10.sp,
                                                color = theme.textSecondary,
                                                maxLines = 1
                                            )
                                        }

                                        if (isSelected) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = "Ativo",
                                                tint = theme.primaryNeon,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            AppThemeMode.values().forEach { mode ->
                                val isSelected = uiState.themeMode == mode
                                TvFocusableCard(
                                    onClick = { onSetTheme(mode) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(115.dp),
                                    glowColorOverride = if (isSelected) theme.primaryNeon else theme.cardBorder,
                                    testTag = "theme_option_${mode.name.lowercase()}"
                                ) { isFocused ->
                                    Column(
                                        modifier = Modifier.fillMaxSize().padding(12.dp),
                                        verticalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = mode.displayName,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) theme.primaryNeon else theme.textPrimary
                                            )

                                            if (isSelected) {
                                                Icon(
                                                    imageVector = Icons.Default.CheckCircle,
                                                    contentDescription = "Ativo",
                                                    tint = theme.primaryNeon,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }

                                        Text(
                                            text = mode.description,
                                            fontSize = 10.sp,
                                            color = theme.textSecondary,
                                            lineHeight = 14.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 2. GERENCIAMENTO DE CACHE E DESEMPENHO
            Card(
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, theme.cardBorder),
                colors = CardDefaults.cardColors(containerColor = theme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "🧹 GERENCIAMENTO DE CACHE",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = theme.primaryNeon
                        )
                        Text(
                            text = "Espaço ocupado: ${uiState.cacheSizeMb}",
                            fontSize = 11.sp,
                            color = if (uiState.cacheCleanedToast) Color(0xFF00F5D4) else theme.textSecondary
                        )
                    }

                    Button(
                        onClick = onCleanCache,
                        colors = ButtonDefaults.buttonColors(containerColor = theme.primaryNeon),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = if (uiState.cacheCleanedToast) "LIMPO! ✓" else "LIMPAR",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }
            }

            // 3. STATUS DE PERIFÉRICOS SEM FIO & MICROFONES
            Card(
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, theme.cardBorder),
                colors = CardDefaults.cardColors(containerColor = theme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "🎙️ PERIFÉRICOS & MICROFONES BLUETOOTH",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = theme.primaryNeon
                    )

                    if (isCompact) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            CompactPeriphericRow("Microfone Sem Fio 1", "Conectado • Bateria 90%", true)
                            CompactPeriphericRow("Microfone Sem Fio 2", "Conectado • Bateria 85%", true)
                            CompactPeriphericRow("Controle Remoto / Gamepad", "Pareado via Bluetooth", true)
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            PeriphericStatusPill("Microfone 1", "Conectado • 90%", true)
                            PeriphericStatusPill("Microfone 2", "Conectado • 85%", true)
                            PeriphericStatusPill("Gamepad / TV Box", "Pareado • Pronto", true)
                        }
                    }
                }
            }

            // 4. CONTROLE PARENTAL
            Card(
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, theme.cardBorder),
                colors = CardDefaults.cardColors(containerColor = theme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "🛡️ CONTROLE PARENTAL",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = theme.primaryNeon
                        )
                        Text(
                            text = "Filtro de faixas com conteúdo explícito para festas familiares.",
                            fontSize = 11.sp,
                            color = theme.textSecondary
                        )
                    }

                    Switch(
                        checked = uiState.parentalLockEnabled,
                        onCheckedChange = { onToggleParentalLock() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = theme.primaryNeon
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun CompactPeriphericRow(name: String, status: String, isOnline: Boolean) {
    val theme = LocalCustomTheme.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(theme.surfaceVariant)
            .padding(horizontal = 10.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(if (isOnline) Color(0xFF00F5D4) else Color(0xFFD62828))
            )
            Text(text = name, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = theme.textPrimary)
        }
        Text(text = status, fontSize = 10.sp, color = theme.textSecondary)
    }
}

@Composable
private fun RowScope.PeriphericStatusPill(
    name: String,
    status: String,
    isOnline: Boolean
) {
    val theme = LocalCustomTheme.current
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = theme.surfaceVariant),
        modifier = Modifier.weight(1f)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(if (isOnline) Color(0xFF00F5D4) else Color(0xFFD62828))
                )
                Text(
                    text = name,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = theme.textPrimary
                )
            }
            Text(
                text = status,
                fontSize = 10.sp,
                color = theme.textSecondary
            )
        }
    }
}
