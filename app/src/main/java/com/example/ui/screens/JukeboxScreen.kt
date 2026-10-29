package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MockMediaLibrary
import com.example.model.Track
import com.example.ui.components.AudioVisualizer
import com.example.ui.components.TvFocusableCard
import com.example.ui.components.TvFocusableIconButton
import com.example.ui.components.VinylRecord
import com.example.ui.theme.LocalCustomTheme
import com.example.viewmodel.MediaCenterUiState

@Composable
fun JukeboxScreen(
    uiState: MediaCenterUiState,
    onPlayTrack: (Track) -> Unit,
    onTogglePlayPause: () -> Unit,
    onSkipNext: () -> Unit,
    onSkipPrevious: () -> Unit,
    onToggleShuffle: () -> Unit,
    onToggleRepeat: () -> Unit,
    onGenreFilter: (String) -> Unit,
    onDecadeFilter: (String) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val theme = LocalCustomTheme.current

    val filteredTracks = remember(uiState.selectedGenreFilter, uiState.selectedDecadeFilter, uiState.searchQuery) {
        MockMediaLibrary.tracks.filter { track ->
            val matchGenre = uiState.selectedGenreFilter == "Todos" || track.genre.contains(uiState.selectedGenreFilter, ignoreCase = true)
            val matchDecade = uiState.selectedDecadeFilter == "Todas" || track.decade == uiState.selectedDecadeFilter
            val matchSearch = uiState.searchQuery.isBlank() ||
                    track.title.contains(uiState.searchQuery, ignoreCase = true) ||
                    track.artist.contains(uiState.searchQuery, ignoreCase = true)
            matchGenre && matchDecade && matchSearch
        }
    }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isCompact = maxWidth < 720.dp

        if (isCompact) {
            // MOBILE COMPACT RESPONSIVE JUKEBOX (Vertical flow)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Compact Active Player Card at Top
                Card(
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.2.dp, theme.primaryNeon),
                    colors = CardDefaults.cardColors(containerColor = theme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            VinylRecord(
                                isPlaying = uiState.isPlaying,
                                sizeDp = 70.dp,
                                centerGradientStart = uiState.currentTrack.coverGradientStart,
                                centerGradientEnd = uiState.currentTrack.coverGradientEnd
                            )

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = uiState.currentTrack.title,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Black,
                                    color = theme.textPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "${uiState.currentTrack.artist} • ${uiState.currentTrack.genre}",
                                    fontSize = 11.sp,
                                    color = theme.textSecondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                // Visualizer
                                Box(modifier = Modifier.fillMaxWidth().height(18.dp)) {
                                    AudioVisualizer(isPlaying = uiState.isPlaying, barCount = 16)
                                }
                            }
                        }

                        // Compact 10-Foot Focusable Controls
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TvFocusableIconButton(
                                onClick = onToggleShuffle,
                                icon = Icons.Default.Shuffle,
                                contentDescription = "Shuffle",
                                tint = if (uiState.isShuffle) theme.primaryNeon else theme.textSecondary,
                                size = 36.dp,
                                iconSize = 18.dp,
                                testTag = "btn_shuffle_compact"
                            )
                            TvFocusableIconButton(
                                onClick = onSkipPrevious,
                                icon = Icons.Default.SkipPrevious,
                                contentDescription = "Anterior",
                                size = 38.dp,
                                iconSize = 22.dp,
                                testTag = "btn_prev_compact"
                            )
                            TvFocusableIconButton(
                                onClick = onTogglePlayPause,
                                icon = if (uiState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = "Play/Pause",
                                containerColor = theme.primaryNeon,
                                tint = Color.Black,
                                size = 48.dp,
                                iconSize = 28.dp,
                                testTag = "btn_play_compact"
                            )
                            TvFocusableIconButton(
                                onClick = onSkipNext,
                                icon = Icons.Default.SkipNext,
                                contentDescription = "Próxima",
                                size = 38.dp,
                                iconSize = 22.dp,
                                testTag = "btn_next_compact"
                            )
                            TvFocusableIconButton(
                                onClick = onToggleRepeat,
                                icon = Icons.Default.Repeat,
                                contentDescription = "Repetir",
                                tint = if (uiState.isRepeat) theme.secondaryNeon else theme.textSecondary,
                                size = 36.dp,
                                iconSize = 18.dp,
                                testTag = "btn_repeat_compact"
                            )
                        }
                    }
                }

                // Search & Filter
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = onSearchQueryChange,
                    placeholder = { Text("Buscar artista, música...", fontSize = 12.sp, color = theme.textSecondary) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = theme.primaryNeon, modifier = Modifier.size(18.dp)) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = theme.primaryNeon,
                        unfocusedBorderColor = theme.cardBorder,
                        focusedTextColor = theme.textPrimary,
                        unfocusedTextColor = theme.textPrimary,
                        focusedContainerColor = theme.surface,
                        unfocusedContainerColor = theme.surface
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                )

                // Genre Pills
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    contentPadding = PaddingValues(vertical = 2.dp)
                ) {
                    items(MockMediaLibrary.genres) { genre ->
                        FilterPill(
                            text = genre,
                            isSelected = uiState.selectedGenreFilter == genre,
                            onClick = { onGenreFilter(genre) },
                            testTag = "filter_genre_$genre"
                        )
                    }
                }

                // Catalog Grid
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 140.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredTracks) { track ->
                        val isCurrent = uiState.currentTrack.id == track.id
                        TvFocusableCard(
                            onClick = { onPlayTrack(track) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp),
                            glowColorOverride = if (isCurrent) theme.secondaryNeon else theme.primaryNeon,
                            testTag = "jukebox_track_${track.id}"
                        ) { isFocused ->
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(8.dp),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(85.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(
                                            Brush.linearGradient(
                                                listOf(Color(track.coverGradientStart), Color(track.coverGradientEnd))
                                            )
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.MusicNote,
                                        contentDescription = null,
                                        tint = Color.White.copy(alpha = 0.85f),
                                        modifier = Modifier.size(28.dp)
                                    )
                                    if (isCurrent && uiState.isPlaying) {
                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.BottomEnd)
                                                .padding(3.dp)
                                                .clip(RoundedCornerShape(3.dp))
                                                .background(theme.secondaryNeon)
                                                .padding(horizontal = 4.dp, vertical = 1.dp)
                                        ) {
                                            Text("TOCANDO", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                        }
                                    }
                                }

                                Column {
                                    Text(
                                        text = track.title,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = theme.textPrimary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = track.artist,
                                        fontSize = 10.sp,
                                        color = theme.textSecondary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // TV BOX WIDE SCREEN LAYOUT (Two Columns side-by-side)
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // LEFT COLUMN: FILTER CATEGORIES & CATALOG GRID (60% width)
                Column(
                    modifier = Modifier
                        .weight(1.3f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = uiState.searchQuery,
                        onValueChange = onSearchQueryChange,
                        placeholder = { Text("Buscar na Jukebox (artista, música, banda)...", color = theme.textSecondary) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar", tint = theme.primaryNeon) },
                        trailingIcon = {
                            if (uiState.searchQuery.isNotEmpty()) {
                                IconButton(onClick = { onSearchQueryChange("") }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Limpar", tint = theme.textSecondary)
                                }
                            }
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = theme.primaryNeon,
                            unfocusedBorderColor = theme.cardBorder,
                            focusedTextColor = theme.textPrimary,
                            unfocusedTextColor = theme.textPrimary,
                            focusedContainerColor = theme.surface,
                            unfocusedContainerColor = theme.surface
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("jukebox_search_input")
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(vertical = 2.dp)
                    ) {
                        items(MockMediaLibrary.genres) { genre ->
                            FilterPill(
                                text = genre,
                                isSelected = uiState.selectedGenreFilter == genre,
                                onClick = { onGenreFilter(genre) },
                                testTag = "filter_genre_$genre"
                            )
                        }
                    }

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(vertical = 2.dp)
                    ) {
                        items(MockMediaLibrary.decades) { decade ->
                            FilterPill(
                                text = decade,
                                isSelected = uiState.selectedDecadeFilter == decade,
                                onClick = { onDecadeFilter(decade) },
                                testTag = "filter_decade_$decade"
                            )
                        }
                    }

                    Text(
                        text = "CATÁLOGO DE MÚSICAS (${filteredTracks.size})",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = theme.textSecondary,
                        letterSpacing = 1.sp
                    )

                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 150.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(filteredTracks) { track ->
                            val isCurrent = uiState.currentTrack.id == track.id
                            TvFocusableCard(
                                onClick = { onPlayTrack(track) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(170.dp),
                                glowColorOverride = if (isCurrent) theme.secondaryNeon else theme.primaryNeon,
                                testTag = "jukebox_track_${track.id}"
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
                                            .height(90.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(
                                                Brush.linearGradient(
                                                    listOf(Color(track.coverGradientStart), Color(track.coverGradientEnd))
                                                )
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.MusicNote,
                                            contentDescription = track.title,
                                            tint = Color.White.copy(alpha = 0.85f),
                                            modifier = Modifier.size(32.dp)
                                        )

                                        if (isCurrent && uiState.isPlaying) {
                                            Box(
                                                modifier = Modifier
                                                    .align(Alignment.BottomEnd)
                                                    .padding(4.dp)
                                                    .clip(RoundedCornerShape(4.dp))
                                                .background(theme.secondaryNeon)
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = "PLAYING",
                                                    fontSize = 8.sp,
                                                    fontWeight = FontWeight.Black,
                                                    color = Color.White
                                                )
                                            }
                                        }
                                    }

                                    Column {
                                        Text(
                                            text = track.title,
                                            fontSize = 12.sp,
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
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = track.genre,
                                                fontSize = 9.sp,
                                                color = theme.primaryNeon
                                            )
                                            Text(
                                                text = "${track.durationSeconds / 60}:${(track.durationSeconds % 60).toString().padStart(2, '0')}",
                                                fontSize = 9.sp,
                                                color = theme.textSecondary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // RIGHT COLUMN: ACTIVE JUKEBOX PLAYER & QUEUE (40% width)
                Card(
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(1.2.dp, theme.primaryNeon.copy(alpha = 0.6f)),
                    colors = CardDefaults.cardColors(containerColor = theme.surface),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(18.dp),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "JUKEBOX PLAYER",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = theme.primaryNeon,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "${uiState.queue.size} faixas",
                                fontSize = 11.sp,
                                color = theme.textSecondary
                            )
                        }

                        VinylRecord(
                            isPlaying = uiState.isPlaying,
                            sizeDp = 130.dp,
                            centerGradientStart = uiState.currentTrack.coverGradientStart,
                            centerGradientEnd = uiState.currentTrack.coverGradientEnd
                        )

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = uiState.currentTrack.title,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = theme.textPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${uiState.currentTrack.artist} • ${uiState.currentTrack.album}",
                                fontSize = 12.sp,
                                color = theme.textSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(38.dp)
                                .padding(horizontal = 6.dp)
                        ) {
                            AudioVisualizer(
                                isPlaying = uiState.isPlaying,
                                barCount = 22,
                                primaryColor = theme.primaryNeon,
                                secondaryColor = theme.secondaryNeon
                            )
                        }

                        // Player Controls (10-Foot Focusable)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TvFocusableIconButton(
                                onClick = onToggleShuffle,
                                icon = Icons.Default.Shuffle,
                                contentDescription = "Shuffle",
                                tint = if (uiState.isShuffle) theme.primaryNeon else theme.textSecondary,
                                size = 42.dp,
                                iconSize = 20.dp,
                                testTag = "btn_shuffle_wide"
                            )
                            TvFocusableIconButton(
                                onClick = onSkipPrevious,
                                icon = Icons.Default.SkipPrevious,
                                contentDescription = "Anterior",
                                size = 46.dp,
                                iconSize = 26.dp,
                                testTag = "btn_prev_wide"
                            )
                            TvFocusableIconButton(
                                onClick = onTogglePlayPause,
                                icon = if (uiState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = "Play/Pause",
                                containerColor = theme.primaryNeon,
                                tint = Color.Black,
                                size = 58.dp,
                                iconSize = 34.dp,
                                testTag = "btn_play_wide"
                            )
                            TvFocusableIconButton(
                                onClick = onSkipNext,
                                icon = Icons.Default.SkipNext,
                                contentDescription = "Próxima",
                                size = 46.dp,
                                iconSize = 26.dp,
                                testTag = "btn_next_wide"
                            )
                            TvFocusableIconButton(
                                onClick = onToggleRepeat,
                                icon = Icons.Default.Repeat,
                                contentDescription = "Repetir",
                                tint = if (uiState.isRepeat) theme.secondaryNeon else theme.textSecondary,
                                size = 42.dp,
                                iconSize = 20.dp,
                                testTag = "btn_repeat_wide"
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterPill(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    val theme = LocalCustomTheme.current
    var isFocused by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .testTag(testTag)
            .clip(RoundedCornerShape(16.dp))
            .background(
                when {
                    isFocused -> theme.primaryNeon
                    isSelected -> theme.primaryNeon.copy(alpha = 0.25f)
                    else -> theme.surface
                }
            )
            .border(
                width = if (isFocused) 1.5.dp else if (isSelected) 1.dp else 0.8.dp,
                color = if (isFocused) Color.White else if (isSelected) theme.primaryNeon else theme.cardBorder,
                shape = RoundedCornerShape(16.dp)
            )
            .onFocusChanged { isFocused = it.isFocused }
            .focusable()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            fontSize = 11.sp,
            fontWeight = if (isSelected || isFocused) FontWeight.Bold else FontWeight.Medium,
            color = if (isFocused) Color.Black else if (isSelected) theme.primaryNeon else theme.textSecondary
        )
    }
}
