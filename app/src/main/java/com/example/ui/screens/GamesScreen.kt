package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MockMediaLibrary
import com.example.model.RetroGameMode
import com.example.ui.components.TvFocusableCard
import com.example.ui.theme.LocalCustomTheme
import com.example.viewmodel.MediaCenterUiState

@Composable
fun GamesScreen(
    uiState: MediaCenterUiState,
    onSelectGame: (RetroGameMode) -> Unit,
    onAnswerQuiz: (Int) -> Unit,
    onRestartQuiz: () -> Unit,
    onArcadeAction: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val theme = LocalCustomTheme.current

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isCompact = maxWidth < 720.dp

        if (isCompact) {
            // MOBILE COMPACT RESPONSIVE GAMES SCREEN
            val mobileScrollState = rememberScrollState()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(mobileScrollState)
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Game Mode Selector Pills
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(RetroGameMode.values()) { mode ->
                        val isSelected = uiState.activeGame == mode
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) theme.accent else theme.surface)
                                .border(1.dp, if (isSelected) Color.White else theme.cardBorder, RoundedCornerShape(10.dp))
                                .clickable { onSelectGame(mode) }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = mode.title,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else theme.textSecondary
                            )
                        }
                    }
                }

                // Game Arena Stage Card
                Card(
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.2.dp, theme.accent),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF04060A)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = uiState.activeGame.title.uppercase(),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = theme.accent
                            )

                            val scoreText = when (uiState.activeGame) {
                                RetroGameMode.MUSIC_QUIZ -> "${uiState.quizScore} PTS"
                                RetroGameMode.RHYTHM_BEAT -> "${uiState.rhythmScore} PTS (${uiState.rhythmCombo}x)"
                                else -> "${uiState.arcadeScore} PTS"
                            }
                            Text(
                                text = scoreText,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                color = theme.primaryNeon
                            )
                        }

                        // Center Canvas
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(if (uiState.activeGame == RetroGameMode.MUSIC_QUIZ) 320.dp else 220.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF070B14))
                                .border(1.dp, theme.cardBorder, RoundedCornerShape(10.dp))
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            when (uiState.activeGame) {
                                RetroGameMode.MUSIC_QUIZ -> {
                                    MusicQuizGameStage(uiState = uiState, isCompact = true, onAnswer = onAnswerQuiz, onRestart = onRestartQuiz)
                                }
                                RetroGameMode.RHYTHM_BEAT -> {
                                    RhythmBeatStage(combo = uiState.rhythmCombo, onHit = { onArcadeAction("TAP_BEAT") })
                                }
                                else -> {
                                    NeonArcadeStage(score = uiState.arcadeScore, onShoot = { onArcadeAction("SHOOT") })
                                }
                            }
                        }

                        // Controller action button on mobile
                        if (uiState.activeGame != RetroGameMode.MUSIC_QUIZ) {
                            Button(
                                onClick = {
                                    if (uiState.activeGame == RetroGameMode.RHYTHM_BEAT) {
                                        onArcadeAction("TAP_BEAT")
                                    } else {
                                        onArcadeAction("SHOOT")
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = theme.secondaryNeon),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth().height(42.dp)
                            ) {
                                Text("AÇÃO / DISPARO ⚡", fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        } else {
            // TV BOX WIDE SCREEN LAYOUT
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // LEFT: GAME ARENA (70%)
                Card(
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(1.5.dp, theme.accent),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF04060A)),
                    modifier = Modifier.weight(1.4f).fillMaxHeight()
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(18.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = uiState.activeGame.title.uppercase(),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = theme.accent
                            )
                            val scoreText = when (uiState.activeGame) {
                                RetroGameMode.MUSIC_QUIZ -> "${uiState.quizScore} PONTOS"
                                RetroGameMode.RHYTHM_BEAT -> "${uiState.rhythmScore} PTS (COMBO: ${uiState.rhythmCombo}x)"
                                else -> "${uiState.arcadeScore} PONTOS"
                            }
                            Text(text = scoreText, fontSize = 12.sp, fontWeight = FontWeight.Black, color = theme.primaryNeon)
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF070B14))
                                .border(1.dp, theme.cardBorder, RoundedCornerShape(12.dp))
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            when (uiState.activeGame) {
                                RetroGameMode.MUSIC_QUIZ -> {
                                    MusicQuizGameStage(uiState = uiState, isCompact = false, onAnswer = onAnswerQuiz, onRestart = onRestartQuiz)
                                }
                                RetroGameMode.RHYTHM_BEAT -> {
                                    RhythmBeatStage(combo = uiState.rhythmCombo, onHit = { onArcadeAction("TAP_BEAT") })
                                }
                                else -> {
                                    NeonArcadeStage(score = uiState.arcadeScore, onShoot = { onArcadeAction("SHOOT") })
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Comandos: D-Pad e Botão OK do Controle Remoto",
                                fontSize = 11.sp,
                                color = theme.textSecondary
                            )

                            if (uiState.activeGame != RetroGameMode.MUSIC_QUIZ) {
                                Button(
                                    onClick = {
                                        if (uiState.activeGame == RetroGameMode.RHYTHM_BEAT) {
                                            onArcadeAction("TAP_BEAT")
                                        } else {
                                            onArcadeAction("SHOOT")
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = theme.secondaryNeon),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("DISPARAR ⚡", fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }
                    }
                }

                // RIGHT: GAME SELECTOR (30%)
                Column(
                    modifier = Modifier.weight(0.9f).fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "ESCOLHA SEU JOGO",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = theme.primaryNeon
                    )

                    RetroGameMode.values().forEach { mode ->
                        val isSelected = uiState.activeGame == mode
                        TvFocusableCard(
                            onClick = { onSelectGame(mode) },
                            modifier = Modifier.fillMaxWidth().height(85.dp),
                            glowColorOverride = if (isSelected) theme.accent else theme.primaryNeon,
                            testTag = "game_mode_${mode.name.lowercase()}"
                        ) { isFocused ->
                            Row(
                                modifier = Modifier.fillMaxSize().padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) theme.accent else theme.surfaceVariant),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = when (mode) {
                                            RetroGameMode.MUSIC_QUIZ -> Icons.Default.Quiz
                                            RetroGameMode.RHYTHM_BEAT -> Icons.Default.MusicNote
                                            RetroGameMode.SPACE_DEFENDER -> Icons.Default.RocketLaunch
                                            RetroGameMode.SNAKE_NEON -> Icons.Default.Games
                                        },
                                        contentDescription = mode.title,
                                        tint = if (isSelected) Color.White else theme.textSecondary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(mode.title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = theme.textPrimary)
                                    Text(mode.subtitle, fontSize = 10.sp, color = theme.textSecondary, maxLines = 1)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MusicQuizGameStage(
    uiState: MediaCenterUiState,
    isCompact: Boolean,
    onAnswer: (Int) -> Unit,
    onRestart: () -> Unit
) {
    val theme = LocalCustomTheme.current

    if (uiState.quizFinished) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = theme.primaryNeon, modifier = Modifier.size(48.dp))
            Text("QUIZ CONCLUÍDO!", fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color.White)
            Text("${uiState.quizScore} PONTOS!", fontSize = 16.sp, color = theme.primaryNeon)
            Button(onClick = onRestart, colors = ButtonDefaults.buttonColors(containerColor = theme.secondaryNeon)) {
                Text("JOGAR DE NOVO 🔄", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    } else {
        val q = MockMediaLibrary.quizQuestions[uiState.quizCurrentIndex]
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Q ${uiState.quizCurrentIndex + 1}/${MockMediaLibrary.quizQuestions.size}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = theme.primaryNeon
                )
                Text(text = q.genre, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = theme.secondaryNeon)
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = q.question,
                    fontSize = if (isCompact) 14.sp else 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Dica: ${q.hint}",
                    fontSize = 11.sp,
                    color = theme.textSecondary,
                    textAlign = TextAlign.Center
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                q.options.forEachIndexed { idx, opt ->
                    val isSelected = uiState.quizSelectedOption == idx
                    val isCorrect = idx == q.correctIndex
                    val optionBg = when {
                        isSelected && isCorrect -> Color(0xFF00F5D4)
                        isSelected && !isCorrect -> Color(0xFFD62828)
                        else -> theme.surfaceVariant
                    }
                    val optionText = if (isSelected) Color.Black else theme.textPrimary

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(optionBg)
                            .clickable { onAnswer(idx) }
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = "${('A' + idx)}. $opt",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = optionText
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RhythmBeatStage(combo: Int, onHit: () -> Unit) {
    val theme = LocalCustomTheme.current
    val infiniteTransition = rememberInfiniteTransition(label = "rhythm_anim")
    val noteY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "note_y"
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("♪ ACERTE NO ALVO NEON ♪", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = theme.primaryNeon)

        Canvas(
            modifier = Modifier.fillMaxWidth().weight(1f).padding(vertical = 6.dp)
        ) {
            val w = size.width
            val h = size.height
            val laneWidth = w / 3
            val targetY = h * 0.85f

            drawLine(color = theme.primaryNeon, start = Offset(0f, targetY), end = Offset(w, targetY), strokeWidth = 3.dp.toPx())

            val currentY = noteY * h
            drawRoundRect(
                color = theme.secondaryNeon,
                topLeft = Offset(laneWidth * 0.2f, currentY),
                size = Size(laneWidth * 0.6f, 16.dp.toPx()),
                cornerRadius = CornerRadius(4.dp.toPx())
            )
            drawRoundRect(
                color = theme.primaryNeon,
                topLeft = Offset(laneWidth * 1.2f, (currentY + h * 0.4f) % h),
                size = Size(laneWidth * 0.6f, 16.dp.toPx()),
                cornerRadius = CornerRadius(4.dp.toPx())
            )
        }

        Text("COMBO: ${combo}x", fontSize = 13.sp, fontWeight = FontWeight.Black, color = theme.secondaryNeon)
    }
}

@Composable
private fun NeonArcadeStage(score: Int, onShoot: () -> Unit) {
    val theme = LocalCustomTheme.current
    val infiniteTransition = rememberInfiniteTransition(label = "invader_anim")
    val alienX by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alien_x"
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("🚀 NEON SPACE DEFENDER", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = theme.accent)

        Canvas(
            modifier = Modifier.fillMaxWidth().weight(1f).padding(vertical = 6.dp)
        ) {
            val w = size.width
            val h = size.height

            val ax = alienX * w
            drawRoundRect(
                color = theme.secondaryNeon,
                topLeft = Offset(ax - 20.dp.toPx(), h * 0.2f),
                size = Size(40.dp.toPx(), 18.dp.toPx()),
                cornerRadius = CornerRadius(4.dp.toPx())
            )

            drawCircle(color = theme.primaryNeon, radius = 12.dp.toPx(), center = Offset(w / 2, h * 0.88f))
        }

        Text("PONTOS: $score", fontSize = 11.sp, color = theme.textSecondary)
    }
}
