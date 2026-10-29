package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.model.MainTab
import com.example.ui.components.TopMenuBar
import com.example.ui.screens.*
import com.example.ui.theme.LocalCustomTheme
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.getCustomThemeColors
import com.example.viewmodel.MediaCenterViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: MediaCenterViewModel by viewModels()

    override fun onKeyDown(keyCode: Int, event: android.view.KeyEvent?): Boolean {
        return when (keyCode) {
            android.view.KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE -> {
                viewModel.togglePlayPause()
                true
            }
            android.view.KeyEvent.KEYCODE_MEDIA_PLAY -> {
                viewModel.playTrack(viewModel.uiState.value.currentTrack)
                true
            }
            android.view.KeyEvent.KEYCODE_MEDIA_PAUSE -> {
                viewModel.pauseTrack()
                true
            }
            android.view.KeyEvent.KEYCODE_MEDIA_NEXT -> {
                viewModel.skipNextTrack()
                true
            }
            android.view.KeyEvent.KEYCODE_MEDIA_PREVIOUS -> {
                viewModel.skipPreviousTrack()
                true
            }
            else -> super.onKeyDown(keyCode, event)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val uiState by viewModel.uiState.collectAsState()
            val customTheme = getCustomThemeColors(uiState.themeMode)

            CompositionLocalProvider(LocalCustomTheme provides customTheme) {
                MyApplicationTheme(darkTheme = true) {
                    BackHandler(enabled = uiState.isKaraokeFullscreen) {
                        viewModel.toggleKaraokeFullscreen(false)
                    }
                    BackHandler(enabled = uiState.isVideoFullscreen) {
                        viewModel.toggleVideoFullscreen(false)
                    }
                    BackHandler(enabled = !uiState.isKaraokeFullscreen && !uiState.isVideoFullscreen && uiState.selectedTab != MainTab.HOME) {
                        viewModel.selectTab(MainTab.HOME)
                    }

                    val isAnyFullscreen = uiState.isKaraokeFullscreen || uiState.isVideoFullscreen

                    Scaffold(
                        topBar = {
                            if (!isAnyFullscreen) {
                                TopMenuBar(
                                    selectedTab = uiState.selectedTab,
                                    onTabSelected = { viewModel.selectTab(it) }
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .background(customTheme.background)
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(customTheme.background)
                                .padding(if (isAnyFullscreen) androidx.compose.foundation.layout.PaddingValues(0.dp) else innerPadding)
                        ) {
                            Crossfade(
                                targetState = uiState.selectedTab,
                                label = "tab_crossfade"
                            ) { tab ->
                                when (tab) {
                                    MainTab.HOME -> HomeScreen(
                                        uiState = uiState,
                                        onTabSelected = { viewModel.selectTab(it) },
                                        onPlayTrack = { viewModel.playTrack(it) },
                                        onStartKaraoke = { viewModel.startKaraoke(it) },
                                        onTogglePlayPause = { viewModel.togglePlayPause() },
                                        onSkipNext = { viewModel.skipNextTrack() },
                                        onToggleShuffle = { viewModel.toggleShuffle() }
                                    )
                                    MainTab.JUKEBOX -> JukeboxScreen(
                                        uiState = uiState,
                                        onPlayTrack = { viewModel.playTrack(it) },
                                        onTogglePlayPause = { viewModel.togglePlayPause() },
                                        onSkipNext = { viewModel.skipNextTrack() },
                                        onSkipPrevious = { viewModel.skipPreviousTrack() },
                                        onToggleShuffle = { viewModel.toggleShuffle() },
                                        onToggleRepeat = { viewModel.toggleRepeat() },
                                        onGenreFilter = { viewModel.setGenreFilter(it) },
                                        onDecadeFilter = { viewModel.setDecadeFilter(it) },
                                        onSearchQueryChange = { viewModel.setSearchQuery(it) }
                                    )
                                    MainTab.KARAOKE -> KaraokeScreen(
                                        uiState = uiState,
                                        onStartKaraoke = { track, singer -> viewModel.startKaraoke(track, singer) },
                                        onFinishKaraoke = { viewModel.finishKaraokePerformance() },
                                        onDismissEvaluation = { viewModel.dismissEvaluation() },
                                        onAddSinger = { name, track -> viewModel.addSingerToQueue(name, track) },
                                        onRemoveSinger = { viewModel.removeSingerFromQueue(it) },
                                        onPlayNextSinger = { viewModel.playNextSingerInQueue() },
                                        onToggleDuet = { viewModel.toggleDuetMode() },
                                        onToggleDuel = { viewModel.toggleDuelMode() },
                                        onSetGuideVocalVolume = { viewModel.setGuideVocalVolume(it) },
                                        onSetMicVolume = { viewModel.setMicVolume(it) },
                                        onSetReverbLevel = { viewModel.setReverbLevel(it) },
                                        onToggleFullscreen = { viewModel.toggleKaraokeFullscreen() },
                                        onToggleDisplayMode = { viewModel.toggleKaraokeDisplayMode() }
                                    )
                                    MainTab.VIDEOS -> VideosScreen(
                                        uiState = uiState,
                                        onSelectVideo = { viewModel.selectVideo(it) },
                                        onTogglePlay = { viewModel.toggleVideoPlay() },
                                        onCategorySelected = { viewModel.setVideoCategory(it) },
                                        onNextVideo = { viewModel.playNextVideo() },
                                        onPreviousVideo = { viewModel.playPreviousVideo() },
                                        onToggleFullscreen = { viewModel.toggleVideoFullscreen() }
                                    )
                                    MainTab.GAMES -> GamesScreen(
                                        uiState = uiState,
                                        onSelectGame = { viewModel.selectGameMode(it) },
                                        onAnswerQuiz = { viewModel.answerQuizOption(it) },
                                        onRestartQuiz = { viewModel.restartQuiz() },
                                        onArcadeAction = { viewModel.triggerArcadeAction(it) }
                                    )
                                    MainTab.SETTINGS -> SettingsScreen(
                                        uiState = uiState,
                                        onSetTheme = { viewModel.setThemeMode(it) },
                                        onCleanCache = { viewModel.cleanCache() },
                                        onToggleParentalLock = { viewModel.toggleParentalLock() }
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
