package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MockMediaLibrary
import com.example.model.MainTab
import com.example.model.Track
import com.example.ui.components.AudioVisualizer
import com.example.ui.components.Tv10FootButton
import com.example.ui.components.TvFocusableCard
import com.example.ui.components.TvFocusableIconButton
import com.example.ui.components.VinylRecord
import com.example.ui.theme.LocalCustomTheme
import com.example.viewmodel.MediaCenterUiState

@Composable
fun HomeScreen(
    uiState: MediaCenterUiState,
    onTabSelected: (MainTab) -> Unit,
    onPlayTrack: (Track) -> Unit,
    onStartKaraoke: (Track) -> Unit,
    onTogglePlayPause: () -> Unit,
    onSkipNext: () -> Unit,
    onToggleShuffle: () -> Unit,
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
                    vertical = if (isCompact) 8.dp else 14.dp
                ),
            verticalArrangement = Arrangement.spacedBy(if (isCompact) 16.dp else 22.dp)
        ) {
            // TOP HERO BILLBOARD / CARROSSEL PRINCIPAL
            FeaturedHeroBanner(
                theme = theme,
                isCompact = isCompact,
                onGoKaraoke = { onTabSelected(MainTab.KARAOKE) },
                onGoJukebox = { onTabSelected(MainTab.JUKEBOX) },
                onGoGames = { onTabSelected(MainTab.GAMES) }
            )

            // RESPONSIVE SHORTCUTS & NOW PLAYING
            if (isCompact) {
                // MOBILE COMPACT STACK: Cards stacked vertically with full content visibility
                NowPlayingCard(
                    uiState = uiState,
                    theme = theme,
                    isCompact = true,
                    onGoJukebox = { onTabSelected(MainTab.JUKEBOX) },
                    onTogglePlayPause = onTogglePlayPause,
                    onSkipNext = onSkipNext,
                    onToggleShuffle = onToggleShuffle,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    KaraokeShortcutCard(
                        theme = theme,
                        singerQueueCount = uiState.singerQueue.size,
                        onClick = { onTabSelected(MainTab.KARAOKE) },
                        modifier = Modifier.weight(1f)
                    )

                    GamesShortcutCard(
                        theme = theme,
                        onClick = { onTabSelected(MainTab.GAMES) },
                        modifier = Modifier.weight(1f)
                    )
                }
            } else {
                // TV BOX WIDE SCREEN ROW: 3 Columns matching TV Lounge Mockup
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    NowPlayingCard(
                        uiState = uiState,
                        theme = theme,
                        isCompact = false,
                        onGoJukebox = { onTabSelected(MainTab.JUKEBOX) },
                        onTogglePlayPause = onTogglePlayPause,
                        onSkipNext = onSkipNext,
                        onToggleShuffle = onToggleShuffle,
                        modifier = Modifier
                            .weight(1.3f)
                            .height(220.dp)
                    )

                    KaraokeShortcutCard(
                        theme = theme,
                        singerQueueCount = uiState.singerQueue.size,
                        onClick = { onTabSelected(MainTab.KARAOKE) },
                        modifier = Modifier
                            .weight(0.9f)
                            .height(220.dp)
                    )

                    GamesShortcutCard(
                        theme = theme,
                        onClick = { onTabSelected(MainTab.GAMES) },
                        modifier = Modifier
                            .weight(0.8f)
                            .height(220.dp)
                    )
                }
            }

            // MAIS TOCADOS DO MOMENTO (JUKEBOX)
            SectionHeader(
                title = "🔥 MAIS TOCADOS DO MOMENTO",
                subtitle = "Toque ou use o controle remoto para reproduzir"
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                items(MockMediaLibrary.tracks) { track ->
                    TvFocusableCard(
                        onClick = { onPlayTrack(track) },
                        modifier = Modifier
                            .width(if (isCompact) 140.dp else 165.dp)
                            .height(if (isCompact) 180.dp else 205.dp),
                        testTag = "track_card_${track.id}"
                    ) { isFocused ->
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(10.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(if (isCompact) 95.dp else 115.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        Brush.linearGradient(
                                            listOf(
                                                Color(track.coverGradientStart),
                                                Color(track.coverGradientEnd)
                                            )
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Album,
                                    contentDescription = track.album,
                                    tint = Color.White.copy(alpha = 0.85f),
                                    modifier = Modifier.size(if (isCompact) 36.dp else 44.dp)
                                )
                                if (uiState.currentTrack.id == track.id && uiState.isPlaying) {
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.BottomEnd)
                                            .padding(4.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(theme.primaryNeon)
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "PLAY",
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color.Black
                                        )
                                    }
                                }
                            }

                            Column {
                                Text(
                                    text = track.title,
                                    fontSize = if (isCompact) 12.sp else 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = theme.textPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = track.artist,
                                    fontSize = 11.sp,
                                    color = theme.textSecondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "${track.genre} • ${track.decade}",
                                    fontSize = 9.sp,
                                    color = theme.primaryNeon
                                )
                            }
                        }
                    }
                }
            }

            // FAVORITOS PARA CANTAR (KARAOKÊ)
            SectionHeader(
                title = "🎤 FAVORITOS PARA CANTAR (Karaokê)",
                subtitle = "Letras sincronizadas e duelo com notas do Jurado IA"
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                items(MockMediaLibrary.tracks.filter { it.isKaraokeReady }) { track ->
                    TvFocusableCard(
                        onClick = { onStartKaraoke(track) },
                        modifier = Modifier
                            .width(if (isCompact) 180.dp else 210.dp)
                            .height(if (isCompact) 125.dp else 140.dp),
                        glowColorOverride = theme.secondaryNeon,
                        testTag = "karaoke_quick_${track.id}"
                    ) { isFocused ->
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(if (isCompact) 56.dp else 64.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        Brush.linearGradient(
                                            listOf(
                                                Color(track.coverGradientStart),
                                                Color(track.coverGradientEnd)
                                            )
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Mic,
                                    contentDescription = "Cantar",
                                    tint = Color.White,
                                    modifier = Modifier.size(28.dp)
                                )
                            }

                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = track.title,
                                    fontSize = if (isCompact) 13.sp else 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = theme.textPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = track.artist,
                                    fontSize = 11.sp,
                                    color = theme.textSecondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "CANTAR 🎙️",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = theme.secondaryNeon
                                )
                            }
                        }
                    }
                }
            }

            // SUGESTÕES INTELIGENTES DA IA
            Card(
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, theme.primaryNeon.copy(alpha = 0.4f)),
                colors = CardDefaults.cardColors(containerColor = theme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(theme.primaryNeon, theme.secondaryNeon)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "IA",
                            tint = Color.Black,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "SUGESTÕES INTELIGENTES DA IA",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = theme.primaryNeon
                        )
                        Text(
                            text = "Playlist sob medida para sua festa com base no histórico tocado.",
                            fontSize = 11.sp,
                            color = theme.textSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun NowPlayingCard(
    uiState: MediaCenterUiState,
    theme: com.example.ui.theme.CustomThemeColors,
    isCompact: Boolean,
    onGoJukebox: () -> Unit,
    onTogglePlayPause: () -> Unit,
    onSkipNext: () -> Unit,
    onToggleShuffle: () -> Unit,
    modifier: Modifier = Modifier
) {
    TvFocusableCard(
        onClick = onGoJukebox,
        modifier = modifier,
        testTag = "now_playing_home_card"
    ) { isFocused ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(if (isCompact) 14.dp else 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(if (isCompact) 12.dp else 18.dp)
        ) {
            VinylRecord(
                isPlaying = uiState.isPlaying,
                sizeDp = if (isCompact) 85.dp else 125.dp,
                centerGradientStart = uiState.currentTrack.coverGradientStart,
                centerGradientEnd = uiState.currentTrack.coverGradientEnd
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(theme.secondaryNeon)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (uiState.isPlaying) "TOCANDO" else "PAUSADO",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                    Text(
                        text = uiState.currentTrack.genre,
                        fontSize = 11.sp,
                        color = theme.primaryNeon,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = uiState.currentTrack.title,
                    fontSize = if (isCompact) 16.sp else 18.sp,
                    fontWeight = FontWeight.Black,
                    color = theme.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = uiState.currentTrack.artist,
                    fontSize = 12.sp,
                    color = theme.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Visualizer Waves
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(if (isCompact) 26.dp else 34.dp)
                        .padding(vertical = 2.dp)
                ) {
                    AudioVisualizer(
                        isPlaying = uiState.isPlaying,
                        barCount = if (isCompact) 14 else 20
                    )
                }

                // Playback Controls (10-Foot Focusable)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TvFocusableIconButton(
                        onClick = onTogglePlayPause,
                        icon = if (uiState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Play/Pause",
                        containerColor = theme.primaryNeon,
                        tint = Color.Black,
                        size = 38.dp,
                        iconSize = 22.dp,
                        testTag = "home_play_btn"
                    )

                    TvFocusableIconButton(
                        onClick = onSkipNext,
                        icon = Icons.Default.SkipNext,
                        contentDescription = "Próxima",
                        size = 38.dp,
                        iconSize = 22.dp,
                        testTag = "home_next_btn"
                    )

                    TvFocusableIconButton(
                        onClick = onToggleShuffle,
                        icon = Icons.Default.Shuffle,
                        contentDescription = "Shuffle",
                        tint = if (uiState.isShuffle) Color.White else theme.textSecondary,
                        containerColor = if (uiState.isShuffle) theme.secondaryNeon else theme.surfaceVariant,
                        size = 38.dp,
                        iconSize = 18.dp,
                        testTag = "home_shuffle_btn"
                    )
                }
            }
        }
    }
}

@Composable
private fun KaraokeShortcutCard(
    theme: com.example.ui.theme.CustomThemeColors,
    singerQueueCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TvFocusableCard(
        onClick = onClick,
        modifier = modifier,
        glowColorOverride = theme.secondaryNeon,
        testTag = "karaoke_home_shortcut"
    ) { isFocused ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(theme.secondaryNeon),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.MicExternalOn,
                    contentDescription = "Karaokê",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }

            Column {
                Text(
                    text = "Karaokê & IA",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    color = theme.textPrimary
                )
                Text(
                    text = "$singerQueueCount na fila",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = theme.secondaryNeon
                )
            }
        }
    }
}

@Composable
private fun GamesShortcutCard(
    theme: com.example.ui.theme.CustomThemeColors,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TvFocusableCard(
        onClick = onClick,
        modifier = modifier,
        glowColorOverride = theme.accent,
        testTag = "games_home_shortcut"
    ) { isFocused ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(theme.accent),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.SportsEsports,
                    contentDescription = "Jogos",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }

            Column {
                Text(
                    text = "Arcade Retrô",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    color = theme.textPrimary
                )
                Text(
                    text = "Quizzes & Games",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = theme.accent
                )
            }
        }
    }
}

@Composable
private fun FeaturedHeroBanner(
    theme: com.example.ui.theme.CustomThemeColors,
    isCompact: Boolean,
    onGoKaraoke: () -> Unit,
    onGoJukebox: () -> Unit,
    onGoGames: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(
                        Color(0xFF03071E),
                        Color(0xFF370617),
                        Color(0xFF6A040F)
                    )
                )
            )
            .border(1.2.dp, theme.secondaryNeon.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
            .padding(if (isCompact) 14.dp else 20.dp)
    ) {
        if (isCompact) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(theme.primaryNeon)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "DESTAQUE DA SEMANA",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.Black
                    )
                }

                Text(
                    text = "BATALHA VOCAL & JUKEBOX",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )

                Text(
                    text = "Cante com letras sincronizadas e afinador em tempo real.",
                    fontSize = 11.sp,
                    color = Color(0xFFE0E0E0)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = onGoKaraoke,
                        colors = ButtonDefaults.buttonColors(containerColor = theme.secondaryNeon),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text("KARAOKÊ 🎙️", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    OutlinedButton(
                        onClick = onGoGames,
                        border = BorderStroke(1.dp, theme.primaryNeon),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text("JOGOS 🎮", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = theme.primaryNeon)
                    }
                }
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(theme.primaryNeon)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "LANÇAMENTO & DESTAQUE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.Black
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "NOITE DE FESTA: MODO BATALHA VOCAL COM IA",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )

                    Text(
                        text = "Reúna os amigos na sala, pegue os microfones da TV Box e dispute quem tem a voz mais afinada!",
                        fontSize = 12.sp,
                        color = Color(0xFFE0E0E0)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = onGoKaraoke,
                        colors = ButtonDefaults.buttonColors(containerColor = theme.secondaryNeon),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Mic, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("INICIAR KARAOKÊ", fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    OutlinedButton(
                        onClick = onGoGames,
                        border = BorderStroke(1.5.dp, theme.primaryNeon),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.SportsEsports, contentDescription = null, tint = theme.primaryNeon)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("JOGOS RETRÔ", fontWeight = FontWeight.Bold, color = theme.primaryNeon)
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String, subtitle: String) {
    val theme = LocalCustomTheme.current
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            fontSize = 15.sp,
            fontWeight = FontWeight.Black,
            color = theme.textPrimary,
            letterSpacing = 0.5.sp
        )
        Text(
            text = subtitle,
            fontSize = 11.sp,
            color = theme.textSecondary
        )
    }
}
