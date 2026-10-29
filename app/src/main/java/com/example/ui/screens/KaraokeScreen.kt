package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.MockMediaLibrary
import com.example.model.SingerInQueue
import com.example.model.Track
import com.example.ui.components.Tv10FootButton
import androidx.activity.compose.BackHandler
import com.example.model.KaraokeDisplayMode
import com.example.ui.components.TvFocusableCard
import com.example.ui.components.TvFocusableIconButton
import com.example.ui.components.YouTubePlayerView
import com.example.ui.theme.LocalCustomTheme
import com.example.viewmodel.MediaCenterUiState

@Composable
fun KaraokeScreen(
    uiState: MediaCenterUiState,
    onStartKaraoke: (Track, SingerInQueue?) -> Unit,
    onFinishKaraoke: () -> Unit,
    onDismissEvaluation: () -> Unit,
    onAddSinger: (String, Track) -> Unit,
    onRemoveSinger: (String) -> Unit,
    onPlayNextSinger: () -> Unit,
    onToggleDuet: () -> Unit,
    onToggleDuel: () -> Unit,
    onSetGuideVocalVolume: (Float) -> Unit,
    onSetMicVolume: (Float) -> Unit,
    onSetReverbLevel: (Float) -> Unit,
    onToggleFullscreen: () -> Unit = {},
    onToggleDisplayMode: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val theme = LocalCustomTheme.current
    var showAddSingerDialog by remember { mutableStateOf(false) }
    var newSingerName by remember { mutableStateOf("") }
    var selectedTrackForSinger by remember { mutableStateOf(MockMediaLibrary.tracks.first()) }

    // FULLSCREEN KARAOKE MODE
    if (uiState.isKaraokeFullscreen) {
        FullscreenKaraokeStage(
            uiState = uiState,
            theme = theme,
            onToggleFullscreen = onToggleFullscreen,
            onToggleDisplayMode = onToggleDisplayMode,
            onStartKaraoke = { onStartKaraoke(uiState.karaokeTrack, null) },
            onFinishKaraoke = onFinishKaraoke,
            onToggleDuet = onToggleDuet
        )
        return
    }

    // Evaluation Results Modal (Avaliador IA)
    if (uiState.evaluation != null) {
        Dialog(onDismissRequest = onDismissEvaluation) {
            Card(
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(2.dp, theme.secondaryNeon),
                colors = CardDefaults.cardColors(containerColor = theme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(theme.primaryNeon, theme.secondaryNeon)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = "Troféu",
                            tint = Color.Black,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Text(
                        text = "JURADO IA: ${uiState.evaluation.judgeName.uppercase()}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = theme.secondaryNeon
                    )

                    Text(
                        text = "${uiState.evaluation.totalScore} PONTOS!",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = theme.primaryNeon
                    )

                    Text(
                        text = "TROFÉU ${uiState.evaluation.trophy}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = theme.textPrimary
                    )

                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = theme.surfaceVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "\"${uiState.evaluation.judgeComment}\"",
                            fontSize = 12.sp,
                            color = theme.textPrimary,
                            modifier = Modifier.padding(12.dp)
                        )
                    }

                    Tv10FootButton(
                        text = "CONTINUAR FESTA 🎤",
                        onClick = onDismissEvaluation,
                        containerColor = theme.secondaryNeon,
                        contentColor = Color.White,
                        glowColor = theme.secondaryNeon,
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "dismiss_evaluation_btn"
                    )
                }
            }
        }
    }

    // Add Singer Dialog
    if (showAddSingerDialog) {
        Dialog(onDismissRequest = { showAddSingerDialog = false }) {
            Card(
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.5.dp, theme.primaryNeon),
                colors = CardDefaults.cardColors(containerColor = theme.surface),
                modifier = Modifier.fillMaxWidth().padding(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "ADICIONAR CANTOR NA FILA",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        color = theme.primaryNeon
                    )

                    OutlinedTextField(
                        value = newSingerName,
                        onValueChange = { newSingerName = it },
                        placeholder = { Text("Nome ou Apelido do Convidado", fontSize = 12.sp, color = theme.textSecondary) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = theme.primaryNeon,
                            unfocusedBorderColor = theme.cardBorder,
                            focusedTextColor = theme.textPrimary,
                            unfocusedTextColor = theme.textPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        text = "Música: ${selectedTrackForSinger.title} - ${selectedTrackForSinger.artist}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = theme.secondaryNeon
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Tv10FootButton(
                            text = "Cancelar",
                            onClick = { showAddSingerDialog = false },
                            containerColor = theme.surfaceVariant,
                            contentColor = theme.textSecondary,
                            glowColor = theme.cardBorder,
                            modifier = Modifier.weight(1f),
                            testTag = "cancel_singer_btn"
                        )
                        Tv10FootButton(
                            text = "Entrar",
                            onClick = {
                                if (newSingerName.isNotBlank()) {
                                    onAddSinger(newSingerName, selectedTrackForSinger)
                                    newSingerName = ""
                                    showAddSingerDialog = false
                                }
                            },
                            containerColor = theme.primaryNeon,
                            contentColor = Color.Black,
                            glowColor = theme.primaryNeon,
                            modifier = Modifier.weight(1f),
                            testTag = "confirm_singer_btn"
                        )
                    }
                }
            }
        }
    }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isCompact = maxWidth < 720.dp

        if (isCompact) {
            // MOBILE SCROLLABLE VERTICAL LAYOUT
            val mobileScrollState = rememberScrollState()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(mobileScrollState)
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // VIDEOKÊ LYRICS CARD
                LyricsStageCard(
                    uiState = uiState,
                    theme = theme,
                    isCompact = true,
                    onStartKaraoke = { onStartKaraoke(uiState.karaokeTrack, null) },
                    onFinishKaraoke = onFinishKaraoke,
                    onToggleDuet = onToggleDuet,
                    onToggleFullscreen = onToggleFullscreen,
                    onToggleDisplayMode = onToggleDisplayMode
                )

                // SINGER QUEUE
                SingerQueueCard(
                    uiState = uiState,
                    theme = theme,
                    onAddClick = {
                        selectedTrackForSinger = uiState.karaokeTrack
                        showAddSingerDialog = true
                    },
                    onStartKaraoke = onStartKaraoke,
                    onRemoveSinger = onRemoveSinger,
                    onPlayNextSinger = onPlayNextSinger,
                    modifier = Modifier.fillMaxWidth().heightIn(max = 240.dp)
                )

                // AUDIO MIXER
                AudioMixerCard(
                    uiState = uiState,
                    theme = theme,
                    onSetGuideVocalVolume = onSetGuideVocalVolume,
                    onSetMicVolume = onSetMicVolume,
                    onSetReverbLevel = onSetReverbLevel,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        } else {
            // TV BOX WIDE SCREEN (Side-by-side)
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // LEFT: LYRICS STAGE
                LyricsStageCard(
                    uiState = uiState,
                    theme = theme,
                    isCompact = false,
                    onStartKaraoke = { onStartKaraoke(uiState.karaokeTrack, null) },
                    onFinishKaraoke = onFinishKaraoke,
                    onToggleDuet = onToggleDuet,
                    onToggleFullscreen = onToggleFullscreen,
                    onToggleDisplayMode = onToggleDisplayMode,
                    modifier = Modifier.weight(1.3f).fillMaxHeight()
                )

                // RIGHT: SINGER QUEUE & AUDIO MIXER
                Column(
                    modifier = Modifier.weight(0.9f).fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    SingerQueueCard(
                        uiState = uiState,
                        theme = theme,
                        onAddClick = {
                            selectedTrackForSinger = uiState.karaokeTrack
                            showAddSingerDialog = true
                        },
                        onStartKaraoke = onStartKaraoke,
                        onRemoveSinger = onRemoveSinger,
                        onPlayNextSinger = onPlayNextSinger,
                        modifier = Modifier.weight(1f)
                    )

                    AudioMixerCard(
                        uiState = uiState,
                        theme = theme,
                        onSetGuideVocalVolume = onSetGuideVocalVolume,
                        onSetMicVolume = onSetMicVolume,
                        onSetReverbLevel = onSetReverbLevel,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun LyricsStageCard(
    uiState: MediaCenterUiState,
    theme: com.example.ui.theme.CustomThemeColors,
    isCompact: Boolean,
    onStartKaraoke: () -> Unit,
    onFinishKaraoke: () -> Unit,
    onToggleDuet: () -> Unit,
    onToggleFullscreen: () -> Unit,
    onToggleDisplayMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.5.dp, if (uiState.isKaraokeActive) theme.secondaryNeon else theme.cardBorder),
        colors = CardDefaults.cardColors(containerColor = theme.surface),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(if (isCompact) 14.dp else 18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(theme.secondaryNeon),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Mic, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    }

                    Column {
                        Text(
                            text = "MODO VIDEOKÊ",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = theme.secondaryNeon
                        )
                        Text(
                            text = "${uiState.karaokeTrack.title} - ${uiState.karaokeTrack.artist}",
                            fontSize = if (isCompact) 13.sp else 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = theme.textPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Display Mode toggle button (Vídeo de fundo vs Só Letras)
                    TvFocusableIconButton(
                        onClick = onToggleDisplayMode,
                        icon = if (uiState.karaokeDisplayMode == KaraokeDisplayMode.WITH_VIDEO) Icons.Default.Movie else Icons.Default.TextFields,
                        contentDescription = "Alternar Modo: Vídeo ou Só Letras",
                        size = 32.dp,
                        iconSize = 16.dp,
                        tint = theme.primaryNeon,
                        glowColor = theme.primaryNeon,
                        testTag = "karaoke_toggle_bg_mode_btn"
                    )

                    // Fullscreen toggle button
                    TvFocusableIconButton(
                        onClick = onToggleFullscreen,
                        icon = Icons.Default.Fullscreen,
                        contentDescription = "Tela Cheia",
                        size = 32.dp,
                        iconSize = 18.dp,
                        tint = Color.White,
                        glowColor = theme.secondaryNeon,
                        testTag = "karaoke_open_fullscreen_btn"
                    )

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(theme.primaryNeon)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${uiState.liveScore} PTS",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.Black
                        )
                    }
                }
            }

            // Lyrics Stage Center (With optional YouTube background video)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (isCompact) 160.dp else 230.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF04060A))
                    .border(1.dp, theme.cardBorder, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                // Background YouTube video when WITH_VIDEO is active
                if (uiState.karaokeDisplayMode == KaraokeDisplayMode.WITH_VIDEO) {
                    YouTubePlayerView(
                        videoId = uiState.karaokeTrack.youtubeVideoId.ifEmpty { "ePjtnSPFWK8" },
                        autoplay = true,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        Color.Black.copy(alpha = 0.65f),
                                        Color.Black.copy(alpha = 0.50f),
                                        Color.Black.copy(alpha = 0.75f)
                                    )
                                )
                            )
                    )
                }

                val currentPos = uiState.playbackPositionMs
                val lyrics = uiState.karaokeTrack.lyrics
                val currentIndex = lyrics.indexOfLast { it.timeMs <= currentPos }.coerceAtLeast(0)
                val currentLine = lyrics.getOrNull(currentIndex)
                val nextLine = lyrics.getOrNull(currentIndex + 1)

                Column(
                    modifier = Modifier.padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (currentLine != null) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(theme.primaryNeon)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = currentLine.singerRole,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.Black
                            )
                        }

                        Text(
                            text = currentLine.text,
                            fontSize = if (isCompact) 17.sp else 21.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            lineHeight = if (isCompact) 23.sp else 28.sp
                        )
                    }

                    if (nextLine != null) {
                        Text(
                            text = "A seguir: ${nextLine.text}",
                            fontSize = 12.sp,
                            color = theme.textSecondary.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            // Real-time Pitch Visualizer Bar
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("AFINADOR EM TEMPO REAL", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = theme.primaryNeon)
                    Text(if (uiState.isKaraokeActive) "AFINADO 🎯" else "PRONTO", fontSize = 10.sp, color = theme.textSecondary)
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(20.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(theme.surfaceVariant)
                        .padding(horizontal = 6.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Box(modifier = Modifier.fillMaxWidth().height(2.dp).background(theme.cardBorder))
                    val userOffset = (uiState.userPitch * 100).coerceIn(5f, 95f)
                    Box(
                        modifier = Modifier
                            .offset(x = (userOffset * 2.8).dp)
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(theme.secondaryNeon)
                    )
                }
            }

            // Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Tv10FootButton(
                    text = if (uiState.isKaraokeActive) "AVALIAR IA 🏁" else "CANTAR 🎙️",
                    onClick = {
                        if (uiState.isKaraokeActive) onFinishKaraoke() else onStartKaraoke()
                    },
                    icon = if (uiState.isKaraokeActive) Icons.Default.EmojiEvents else Icons.Default.Mic,
                    containerColor = if (uiState.isKaraokeActive) Color(0xFFD62828) else theme.secondaryNeon,
                    contentColor = Color.White,
                    glowColor = if (uiState.isKaraokeActive) Color(0xFFFF5252) else theme.secondaryNeon,
                    modifier = Modifier.weight(1f),
                    testTag = "karaoke_sing_btn"
                )

                Tv10FootButton(
                    text = if (uiState.isDuetMode) "DUETO: ON 👥" else "DUETO: OFF",
                    onClick = onToggleDuet,
                    icon = Icons.Default.Group,
                    containerColor = if (uiState.isDuetMode) theme.surfaceVariant else theme.surfaceVariant.copy(alpha = 0.5f),
                    contentColor = if (uiState.isDuetMode) theme.primaryNeon else theme.textSecondary,
                    glowColor = theme.primaryNeon,
                    modifier = Modifier.weight(1f),
                    testTag = "karaoke_duet_btn"
                )
            }
        }
    }
}

@Composable
private fun SingerQueueCard(
    uiState: MediaCenterUiState,
    theme: com.example.ui.theme.CustomThemeColors,
    onAddClick: () -> Unit,
    onStartKaraoke: (Track, SingerInQueue?) -> Unit,
    onRemoveSinger: (String) -> Unit,
    onPlayNextSinger: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, theme.primaryNeon.copy(alpha = 0.4f)),
        colors = CardDefaults.cardColors(containerColor = theme.surface),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "FILA DE CANTORES (${uiState.singerQueue.size})",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = theme.primaryNeon
                )
                TvFocusableIconButton(
                    onClick = onAddClick,
                    icon = Icons.Default.PersonAdd,
                    contentDescription = "Adicionar Cantor",
                    size = 34.dp,
                    iconSize = 18.dp,
                    tint = theme.primaryNeon,
                    glowColor = theme.primaryNeon,
                    testTag = "add_singer_btn"
                )
            }

            LazyColumn(
                modifier = Modifier.weight(1f).padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(uiState.singerQueue) { singer ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(theme.surfaceVariant)
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(singer.singerName, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = theme.textPrimary, maxLines = 1)
                            Text(singer.track.title, fontSize = 10.sp, color = theme.textSecondary, maxLines = 1)
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            TvFocusableIconButton(
                                onClick = { onStartKaraoke(singer.track, singer) },
                                icon = Icons.Default.PlayArrow,
                                contentDescription = "Cantar agora",
                                size = 32.dp,
                                iconSize = 18.dp,
                                tint = theme.primaryNeon,
                                glowColor = theme.primaryNeon,
                                testTag = "sing_now_${singer.id}"
                            )
                            TvFocusableIconButton(
                                onClick = { onRemoveSinger(singer.id) },
                                icon = Icons.Default.Close,
                                contentDescription = "Remover da fila",
                                size = 32.dp,
                                iconSize = 14.dp,
                                tint = theme.textSecondary,
                                glowColor = Color(0xFFFF5252),
                                testTag = "remove_singer_${singer.id}"
                            )
                        }
                    }
                }
            }

            Tv10FootButton(
                text = "PRÓXIMO CANTOR ⏭️",
                onClick = onPlayNextSinger,
                icon = Icons.Default.SkipNext,
                containerColor = theme.primaryNeon,
                contentColor = Color.Black,
                glowColor = theme.primaryNeon,
                modifier = Modifier.fillMaxWidth(),
                testTag = "next_singer_btn"
            )
        }
    }
}

@Composable
private fun AudioMixerCard(
    uiState: MediaCenterUiState,
    theme: com.example.ui.theme.CustomThemeColors,
    onSetGuideVocalVolume: (Float) -> Unit,
    onSetMicVolume: (Float) -> Unit,
    onSetReverbLevel: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, theme.cardBorder),
        colors = CardDefaults.cardColors(containerColor = theme.surface),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("🎛️ MIXER DE ÁUDIO (CONTROLE REMOTO)", fontSize = 11.sp, fontWeight = FontWeight.Black, color = theme.primaryNeon)

            // Vocal Guia with stepper
            AudioMixerControlRow(
                title = "Vocal Guia",
                value = uiState.guideVocalVolume,
                theme = theme,
                accentColor = theme.primaryNeon,
                onValueChange = onSetGuideVocalVolume,
                testTag = "guide_vocal"
            )

            // Mic TV Box with stepper
            AudioMixerControlRow(
                title = "Mic TV Box",
                value = uiState.micVolume,
                theme = theme,
                accentColor = theme.secondaryNeon,
                onValueChange = onSetMicVolume,
                testTag = "mic_volume"
            )

            // Eco / Reverb with stepper
            AudioMixerControlRow(
                title = "Eco / Reverb",
                value = uiState.reverbLevel,
                theme = theme,
                accentColor = theme.accent,
                onValueChange = onSetReverbLevel,
                testTag = "reverb_level"
            )
        }
    }
}

@Composable
private fun AudioMixerControlRow(
    title: String,
    value: Float,
    theme: com.example.ui.theme.CustomThemeColors,
    accentColor: Color,
    onValueChange: (Float) -> Unit,
    testTag: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(title, fontSize = 11.sp, color = theme.textSecondary)
            Text("${(value * 100).toInt()}%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = accentColor)
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            TvFocusableIconButton(
                onClick = { onValueChange((value - 0.1f).coerceIn(0f, 1f)) },
                icon = Icons.Default.Remove,
                contentDescription = "Diminuir $title",
                size = 30.dp,
                iconSize = 16.dp,
                tint = theme.textPrimary,
                glowColor = accentColor,
                testTag = "${testTag}_dec"
            )

            Slider(
                value = value,
                onValueChange = onValueChange,
                colors = SliderDefaults.colors(thumbColor = accentColor, activeTrackColor = accentColor),
                modifier = Modifier.weight(1f).height(24.dp)
            )

            TvFocusableIconButton(
                onClick = { onValueChange((value + 0.1f).coerceIn(0f, 1f)) },
                icon = Icons.Default.Add,
                contentDescription = "Aumentar $title",
                size = 30.dp,
                iconSize = 16.dp,
                tint = theme.textPrimary,
                glowColor = accentColor,
                testTag = "${testTag}_inc"
            )
        }
    }
}

/**
 * Fullscreen Karaoke Stage for Smart TV / TV Box and Mobile.
 * Supports "Com Vídeo de Fundo" (YouTube video with cyber scrim) or "Só Letras Neon" mode,
 * large lyrics with singer role badges, live score HUD, and 10-foot D-pad controls.
 */
@Composable
fun FullscreenKaraokeStage(
    uiState: MediaCenterUiState,
    theme: com.example.ui.theme.CustomThemeColors,
    onToggleFullscreen: () -> Unit,
    onToggleDisplayMode: () -> Unit,
    onStartKaraoke: () -> Unit,
    onFinishKaraoke: () -> Unit,
    onToggleDuet: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler {
        onToggleFullscreen()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("fullscreen_karaoke_stage")
    ) {
        // BACKGROUND LAYER: EITHER YOUTUBE VIDEO OR CYBER NEON AMBIENCE
        if (uiState.karaokeDisplayMode == KaraokeDisplayMode.WITH_VIDEO) {
            YouTubePlayerView(
                videoId = uiState.karaokeTrack.youtubeVideoId.ifEmpty { "ePjtnSPFWK8" },
                autoplay = true,
                modifier = Modifier.fillMaxSize()
            )
            // Cyber dark scrim overlay for crisp lyrics readability
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.Black.copy(alpha = 0.72f),
                                Color.Black.copy(alpha = 0.52f),
                                Color.Black.copy(alpha = 0.85f)
                            )
                        )
                    )
            )
        } else {
            // SÓ LETRAS NEON AMBIENCE
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            listOf(
                                theme.surfaceVariant,
                                Color(0xFF070B14),
                                Color.Black
                            )
                        )
                    )
            )
        }

        // CONTENT OVERLAY
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // TOP BAR: SINGER TAG, SONG TITLE, LIVE SCORE & CLOSE FULLSCREEN
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(theme.secondaryNeon)
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "🎤 ${uiState.activeSinger?.singerName ?: "Vocalista da Sala"}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }

                    Column {
                        Text(
                            text = uiState.karaokeTrack.title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Text(
                            text = "${uiState.karaokeTrack.artist} • ${uiState.karaokeDisplayMode.displayName}",
                            fontSize = 11.sp,
                            color = theme.primaryNeon
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // LIVE SCORE HUD
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(theme.primaryNeon)
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "${uiState.liveScore} PTS 🏆",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.Black
                        )
                    }

                    // Sair da Tela Cheia Button
                    TvFocusableIconButton(
                        onClick = onToggleFullscreen,
                        icon = Icons.Default.FullscreenExit,
                        contentDescription = "Sair da Tela Cheia",
                        size = 42.dp,
                        iconSize = 24.dp,
                        tint = Color.White,
                        glowColor = theme.secondaryNeon,
                        testTag = "exit_karaoke_fullscreen"
                    )
                }
            }

            // CENTER: HUGE GLOWING VIDEOKÊ LYRICS
            val currentPos = uiState.playbackPositionMs
            val lyrics = uiState.karaokeTrack.lyrics
            val currentIndex = lyrics.indexOfLast { it.timeMs <= currentPos }.coerceAtLeast(0)
            val currentLine = lyrics.getOrNull(currentIndex)
            val nextLine = lyrics.getOrNull(currentIndex + 1)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                if (currentLine != null) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(theme.primaryNeon)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = currentLine.singerRole,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.Black
                        )
                    }

                    Text(
                        text = currentLine.text,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        lineHeight = 42.sp,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }

                if (nextLine != null) {
                    Text(
                        text = "A seguir: ${nextLine.text}",
                        fontSize = 15.sp,
                        color = theme.textSecondary.copy(alpha = 0.85f),
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Real-Time Pitch Tuner Gauge across fullscreen
                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .padding(top = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("AFINADOR EM TEMPO REAL", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = theme.primaryNeon)
                        Text(if (uiState.isKaraokeActive) "AFINADO 🎯" else "PRONTO", fontSize = 11.sp, color = Color.White)
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(24.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Black.copy(alpha = 0.6f))
                            .border(1.dp, theme.cardBorder, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Box(modifier = Modifier.fillMaxWidth().height(2.dp).background(theme.cardBorder))
                        val userOffset = (uiState.userPitch * 100).coerceIn(5f, 95f)
                        Box(
                            modifier = Modifier
                                .offset(x = (userOffset * 3.5).dp)
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(theme.secondaryNeon)
                        )
                    }
                }
            }

            // BOTTOM CONTROL BAR (10-Foot D-Pad Focusable Buttons)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Tv10FootButton(
                    text = "⛶ SAIR DA TELA CHEIA",
                    onClick = onToggleFullscreen,
                    icon = Icons.Default.FullscreenExit,
                    containerColor = theme.surfaceVariant,
                    contentColor = Color.White,
                    glowColor = theme.primaryNeon,
                    testTag = "karaoke_fs_exit_btn"
                )

                Spacer(modifier = Modifier.width(12.dp))

                Tv10FootButton(
                    text = if (uiState.karaokeDisplayMode == KaraokeDisplayMode.WITH_VIDEO) "ALTERNAR: SÓ LETRAS 📜" else "ALTERNAR: COM VÍDEO 🎬",
                    onClick = onToggleDisplayMode,
                    icon = if (uiState.karaokeDisplayMode == KaraokeDisplayMode.WITH_VIDEO) Icons.Default.TextFields else Icons.Default.Movie,
                    containerColor = theme.surfaceVariant,
                    contentColor = theme.primaryNeon,
                    glowColor = theme.primaryNeon,
                    testTag = "karaoke_fs_display_mode_btn"
                )

                Spacer(modifier = Modifier.width(12.dp))

                Tv10FootButton(
                    text = if (uiState.isKaraokeActive) "AVALIAR IA 🏁" else "CANTAR 🎙️",
                    onClick = {
                        if (uiState.isKaraokeActive) onFinishKaraoke() else onStartKaraoke()
                    },
                    icon = if (uiState.isKaraokeActive) Icons.Default.EmojiEvents else Icons.Default.Mic,
                    containerColor = if (uiState.isKaraokeActive) Color(0xFFD62828) else theme.secondaryNeon,
                    contentColor = Color.White,
                    glowColor = if (uiState.isKaraokeActive) Color(0xFFFF5252) else theme.secondaryNeon,
                    testTag = "karaoke_fs_sing_btn"
                )

                Spacer(modifier = Modifier.width(12.dp))

                Tv10FootButton(
                    text = if (uiState.isDuetMode) "DUETO: ON 👥" else "DUETO: OFF",
                    onClick = onToggleDuet,
                    icon = Icons.Default.Group,
                    containerColor = if (uiState.isDuetMode) theme.surfaceVariant else theme.surfaceVariant.copy(alpha = 0.5f),
                    contentColor = if (uiState.isDuetMode) theme.primaryNeon else theme.textSecondary,
                    glowColor = theme.primaryNeon,
                    testTag = "karaoke_fs_duet_btn"
                )
            }
        }
    }
}
