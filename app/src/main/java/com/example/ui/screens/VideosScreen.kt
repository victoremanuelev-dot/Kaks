package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.MockMediaLibrary
import com.example.model.VideoCategory
import com.example.model.VideoItem
import com.example.ui.components.TvFocusableCard
import com.example.ui.components.TvYouTubePlayer
import com.example.ui.components.YouTubePlayerView
import com.example.ui.components.extractYouTubeId
import com.example.ui.theme.LocalCustomTheme
import com.example.viewmodel.MediaCenterUiState

@Composable
fun VideosScreen(
    uiState: MediaCenterUiState,
    onSelectVideo: (VideoItem) -> Unit,
    onTogglePlay: () -> Unit,
    onCategorySelected: (VideoCategory) -> Unit,
    onNextVideo: () -> Unit = {},
    onPreviousVideo: () -> Unit = {},
    onToggleFullscreen: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val theme = LocalCustomTheme.current
    var customSearchQuery by remember { mutableStateOf("") }
    var customVideoItem by remember { mutableStateOf<VideoItem?>(null) }

    val currentVideo = customVideoItem ?: uiState.activeVideo

    val filteredVideos = remember(uiState.selectedVideoCategory, customSearchQuery) {
        val baseList = MockMediaLibrary.videoItems.filter { it.category == uiState.selectedVideoCategory }
        if (customSearchQuery.isBlank()) {
            baseList
        } else {
            baseList.filter {
                it.title.contains(customSearchQuery, ignoreCase = true) ||
                it.artistOrChannel.contains(customSearchQuery, ignoreCase = true)
            }
        }
    }

    // FULLSCREEN TV PLAYER MODE
    if (uiState.isVideoFullscreen) {
        TvYouTubePlayer(
            videoId = currentVideo.youtubeVideoId.ifEmpty { "A22oy8dFjqc" },
            title = currentVideo.title,
            subtitle = "${currentVideo.artistOrChannel} • ${currentVideo.category.displayName}",
            autoplay = true,
            isFullscreen = true,
            onToggleFullscreen = onToggleFullscreen,
            onPreviousVideo = onPreviousVideo,
            onNextVideo = onNextVideo,
            onVideoEnded = onNextVideo,
            modifier = Modifier.fillMaxSize()
        )
        return
    }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isCompact = maxWidth < 720.dp

        if (isCompact) {
            // MOBILE COMPACT RESPONSIVE YOUTUBE VIDEOS LAYOUT
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // REAL YOUTUBE TV PLAYER WITH D-PAD CONTROLS
                Card(
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.5.dp, theme.primaryNeon),
                    colors = CardDefaults.cardColors(containerColor = Color.Black),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(230.dp)
                ) {
                    TvYouTubePlayer(
                        videoId = currentVideo.youtubeVideoId.ifEmpty { "A22oy8dFjqc" },
                        title = currentVideo.title,
                        subtitle = currentVideo.artistOrChannel,
                        autoplay = true,
                        isFullscreen = false,
                        onToggleFullscreen = onToggleFullscreen,
                        onPreviousVideo = onPreviousVideo,
                        onNextVideo = onNextVideo,
                        onVideoEnded = onNextVideo,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Search & Custom YouTube Link Row
                OutlinedTextField(
                    value = customSearchQuery,
                    onValueChange = { query ->
                        customSearchQuery = query
                        // If user pastes full YouTube URL or 11-char ID
                        if (query.contains("youtube.com") || query.contains("youtu.be")) {
                            val extractedId = extractYouTubeId(query)
                            if (extractedId.isNotBlank()) {
                                customVideoItem = VideoItem(
                                    id = "custom_${System.currentTimeMillis()}",
                                    title = "Vídeo do YouTube Personalizado",
                                    artistOrChannel = "Link Personalizado",
                                    duration = "YouTube",
                                    category = uiState.selectedVideoCategory,
                                    bgGradientStart = 0xFF5A189A,
                                    bgGradientEnd = 0xFF03045E,
                                    description = "Vídeo carregado direto da URL do YouTube.",
                                    youtubeVideoId = extractedId
                                )
                            }
                        }
                    },
                    placeholder = { Text("Buscar ou colar link do YouTube...", fontSize = 11.sp, color = theme.textSecondary) },
                    leadingIcon = {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.Red, modifier = Modifier.size(18.dp))
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
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                )

                // Category Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    VideoCategory.values().forEach { category ->
                        val isSelected = uiState.selectedVideoCategory == category
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) theme.primaryNeon.copy(alpha = 0.25f) else theme.surface)
                                .border(1.dp, if (isSelected) theme.primaryNeon else theme.cardBorder, RoundedCornerShape(8.dp))
                                .clickable {
                                    customVideoItem = null
                                    onCategorySelected(category)
                                }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = category.displayName.split(" ").first(),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) theme.primaryNeon else theme.textSecondary
                            )
                        }
                    }
                }

                // Video List with YouTube Thumbnails
                LazyVerticalGrid(
                    columns = GridCells.Fixed(1),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredVideos) { video ->
                        val isCurrent = currentVideo.id == video.id
                        TvFocusableCard(
                            onClick = {
                                customVideoItem = null
                                onSelectVideo(video)
                            },
                            modifier = Modifier.fillMaxWidth().height(84.dp),
                            glowColorOverride = if (isCurrent) theme.secondaryNeon else theme.primaryNeon,
                            testTag = "video_card_${video.id}"
                        ) { isFocused ->
                            Row(
                                modifier = Modifier.fillMaxSize().padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                // YouTube Thumbnail
                                Box(
                                    modifier = Modifier
                                        .size(90.dp, 66.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color.Black),
                                    contentAlignment = Alignment.Center
                                ) {
                                    AsyncImage(
                                        model = "https://img.youtube.com/vi/${video.youtubeVideoId}/hqdefault.jpg",
                                        contentDescription = video.title,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(Color.Black.copy(alpha = 0.6f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.PlayArrow,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = video.title,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = theme.textPrimary,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = video.artistOrChannel,
                                            fontSize = 10.sp,
                                            color = theme.textSecondary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = video.duration,
                                            fontSize = 9.sp,
                                            color = theme.primaryNeon
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // TV BOX WIDE SCREEN 10-FOOT LAYOUT
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // LEFT: LARGE YOUTUBE STAGE (65% width)
                Card(
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(1.5.dp, theme.primaryNeon),
                    colors = CardDefaults.cardColors(containerColor = Color.Black),
                    modifier = Modifier.weight(1.35f).fillMaxHeight()
                ) {
                    TvYouTubePlayer(
                        videoId = currentVideo.youtubeVideoId.ifEmpty { "A22oy8dFjqc" },
                        title = currentVideo.title,
                        subtitle = "${currentVideo.artistOrChannel} • ${currentVideo.category.displayName}",
                        autoplay = true,
                        isFullscreen = false,
                        onToggleFullscreen = onToggleFullscreen,
                        onPreviousVideo = onPreviousVideo,
                        onNextVideo = onNextVideo,
                        onVideoEnded = onNextVideo,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // RIGHT: YOUTUBE CATALOG & CUSTOM SEARCH (35% width)
                Column(
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "VÍDEOS DIRETO DO YOUTUBE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = theme.primaryNeon,
                        letterSpacing = 1.sp
                    )

                    // Search / Paste URL Input
                    OutlinedTextField(
                        value = customSearchQuery,
                        onValueChange = { query ->
                            customSearchQuery = query
                            if (query.contains("youtube.com") || query.contains("youtu.be")) {
                                val extractedId = extractYouTubeId(query)
                                if (extractedId.isNotBlank()) {
                                    customVideoItem = VideoItem(
                                        id = "custom_${System.currentTimeMillis()}",
                                        title = "Vídeo Carregado do YouTube",
                                        artistOrChannel = "URL Externa",
                                        duration = "YouTube",
                                        category = uiState.selectedVideoCategory,
                                        bgGradientStart = 0xFF5A189A,
                                        bgGradientEnd = 0xFF03045E,
                                        description = "Vídeo do YouTube reproduzido diretamente.",
                                        youtubeVideoId = extractedId
                                    )
                                }
                            }
                        },
                        placeholder = { Text("Buscar ou colar URL do YouTube...", fontSize = 11.sp, color = theme.textSecondary) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = theme.primaryNeon) },
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

                    // Category Filter Pills
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        VideoCategory.values().forEach { category ->
                            val isSelected = uiState.selectedVideoCategory == category
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) theme.primaryNeon.copy(alpha = 0.25f) else theme.surface)
                                    .border(1.dp, if (isSelected) theme.primaryNeon else theme.cardBorder, RoundedCornerShape(8.dp))
                                    .clickable {
                                        customVideoItem = null
                                        onCategorySelected(category)
                                    }
                                    .padding(vertical = 7.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = category.displayName.split(" ").first(),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) theme.primaryNeon else theme.textSecondary
                                )
                            }
                        }
                    }

                    // Video Cards with Official YouTube Thumbnails
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(1),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(filteredVideos) { video ->
                            val isCurrent = currentVideo.id == video.id
                            TvFocusableCard(
                                onClick = {
                                    customVideoItem = null
                                    onSelectVideo(video)
                                },
                                modifier = Modifier.fillMaxWidth().height(92.dp),
                                glowColorOverride = if (isCurrent) theme.secondaryNeon else theme.primaryNeon,
                                testTag = "video_card_${video.id}"
                            ) { isFocused ->
                                Row(
                                    modifier = Modifier.fillMaxSize().padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(95.dp, 72.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Color.Black),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        AsyncImage(
                                            model = "https://img.youtube.com/vi/${video.youtubeVideoId}/hqdefault.jpg",
                                            contentDescription = video.title,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                        Box(
                                            modifier = Modifier
                                                .size(26.dp)
                                                .clip(CircleShape)
                                                .background(Color.Black.copy(alpha = 0.65f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.PlayArrow,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }

                                    Column(
                                        modifier = Modifier.weight(1f),
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            text = video.title,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = theme.textPrimary,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = video.artistOrChannel,
                                                fontSize = 10.sp,
                                                color = theme.textSecondary,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = video.duration,
                                                fontSize = 9.sp,
                                                color = theme.primaryNeon
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
