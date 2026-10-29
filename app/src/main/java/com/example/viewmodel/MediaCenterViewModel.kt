package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.KaraokePitchEngine
import com.example.data.MockMediaLibrary
import com.example.model.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.random.Random

data class MediaCenterUiState(
    val selectedTab: MainTab = MainTab.HOME,
    val themeMode: AppThemeMode = AppThemeMode.NEON_CYBER,
    
    // Jukebox
    val currentTrack: Track = MockMediaLibrary.tracks.first(),
    val isPlaying: Boolean = false,
    val playbackPositionMs: Long = 0L,
    val queue: List<Track> = MockMediaLibrary.tracks,
    val isShuffle: Boolean = false,
    val isRepeat: Boolean = false,
    val selectedGenreFilter: String = "Todos",
    val selectedDecadeFilter: String = "Todas",
    val searchQuery: String = "",
    
    // Karaoke / Videokê
    val isKaraokeActive: Boolean = false,
    val karaokeTrack: Track = MockMediaLibrary.tracks.first(),
    val isKaraokeFullscreen: Boolean = false,
    val karaokeDisplayMode: KaraokeDisplayMode = KaraokeDisplayMode.WITH_VIDEO,
    val singerQueue: List<SingerInQueue> = listOf(
        SingerInQueue("sq1", "Carlos (Aniversariante)", MockMediaLibrary.tracks[0]),
        SingerInQueue("sq2", "Mariana & Bia (Dueto)", MockMediaLibrary.tracks[4]),
        SingerInQueue("sq3", "Rodrigo Rocker", MockMediaLibrary.tracks[1])
    ),
    val activeSinger: SingerInQueue? = null,
    val isDuetMode: Boolean = false,
    val isDuelMode: Boolean = true,
    val guideVocalVolume: Float = 0.5f,
    val micVolume: Float = 0.85f,
    val reverbLevel: Float = 0.6f,
    val userPitch: Float = 0.5f,
    val targetPitch: Float = 0.5f,
    val liveScore: Int = 85,
    val evaluation: KaraokeEvaluation? = null,
    val isEvaluating: Boolean = false,
    
    // Video Player
    val activeVideo: VideoItem = MockMediaLibrary.videoItems.first(),
    val isVideoPlaying: Boolean = true,
    val isVideoFullscreen: Boolean = false,
    val selectedVideoCategory: VideoCategory = VideoCategory.SHOW,
    
    // Retro Games & Quiz
    val activeGame: RetroGameMode = RetroGameMode.MUSIC_QUIZ,
    val quizCurrentIndex: Int = 0,
    val quizSelectedOption: Int? = null,
    val quizScore: Int = 0,
    val quizFinished: Boolean = false,
    val arcadeScore: Int = 0,
    val arcadeLives: Int = 3,
    val arcadeGameOver: Boolean = false,
    val rhythmCombo: Int = 0,
    val rhythmScore: Int = 0,
    
    // Settings & System
    val cacheSizeMb: String = "142.8 MB",
    val cacheCleanedToast: Boolean = false,
    val bluetoothMicsConnected: Int = 2,
    val parentalLockEnabled: Boolean = false,
    val aiRecommendations: List<String> = listOf(
        "Chitãozinho & Xororó - Evidências (Recomendado pela IA)",
        "Queen - Bohemian Rhapsody (Alta pontuação em grupo)",
        "A-ha - Take On Me (Destaque anos 80)"
    )
)

class MediaCenterViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(MediaCenterUiState())
    val uiState: StateFlow<MediaCenterUiState> = _uiState.asStateFlow()

    private var playbackJob: Job? = null
    private var pitchSimulationJob: Job? = null
    private var pitchMatchCount = 0
    private var pitchTotalSamples = 0

    init {
        startPitchEngineSimulation()
    }

    fun selectTab(tab: MainTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun setThemeMode(mode: AppThemeMode) {
        _uiState.update { it.copy(themeMode = mode) }
    }

    // --- JUKEBOX CONTROLS ---

    fun togglePlayPause() {
        val currentlyPlaying = _uiState.value.isPlaying
        if (currentlyPlaying) {
            pauseTrack()
        } else {
            playTrack(_uiState.value.currentTrack)
        }
    }

    fun playTrack(track: Track) {
        playbackJob?.cancel()
        _uiState.update {
            it.copy(
                currentTrack = track,
                isPlaying = true,
                playbackPositionMs = 0L,
                aiRecommendations = KaraokePitchEngine.generateAISuggestions(track.genre)
            )
        }

        playbackJob = viewModelScope.launch {
            while (_uiState.value.isPlaying && _uiState.value.playbackPositionMs < track.durationSeconds * 1000L) {
                delay(500)
                _uiState.update { it.copy(playbackPositionMs = it.playbackPositionMs + 500) }
            }
            if (_uiState.value.isRepeat) {
                playTrack(track)
            } else {
                skipNextTrack()
            }
        }
    }

    fun pauseTrack() {
        playbackJob?.cancel()
        _uiState.update { it.copy(isPlaying = false) }
    }

    fun skipNextTrack() {
        val current = _uiState.value.currentTrack
        val queue = _uiState.value.queue
        val currentIndex = queue.indexOfFirst { it.id == current.id }
        val nextIndex = if (_uiState.value.isShuffle) {
            queue.indices.random()
        } else {
            (currentIndex + 1) % queue.size
        }
        playTrack(queue[nextIndex])
    }

    fun skipPreviousTrack() {
        val current = _uiState.value.currentTrack
        val queue = _uiState.value.queue
        val currentIndex = queue.indexOfFirst { it.id == current.id }
        val prevIndex = if (currentIndex - 1 < 0) queue.size - 1 else currentIndex - 1
        playTrack(queue[prevIndex])
    }

    fun toggleShuffle() {
        _uiState.update { it.copy(isShuffle = !it.isShuffle) }
    }

    fun toggleRepeat() {
        _uiState.update { it.copy(isRepeat = !it.isRepeat) }
    }

    fun setGenreFilter(genre: String) {
        _uiState.update { it.copy(selectedGenreFilter = genre) }
    }

    fun setDecadeFilter(decade: String) {
        _uiState.update { it.copy(selectedDecadeFilter = decade) }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    // --- KARAOKÊ / VIDEOKÊ ---

    fun startKaraoke(track: Track, singer: SingerInQueue? = null) {
        playbackJob?.cancel()
        pitchMatchCount = 0
        pitchTotalSamples = 0
        _uiState.update {
            it.copy(
                isKaraokeActive = true,
                karaokeTrack = track,
                currentTrack = track,
                isPlaying = true,
                playbackPositionMs = 0L,
                activeSinger = singer,
                evaluation = null,
                liveScore = 80
            )
        }

        playbackJob = viewModelScope.launch {
            while (_uiState.value.isKaraokeActive && _uiState.value.playbackPositionMs < track.durationSeconds * 1000L) {
                delay(500)
                _uiState.update { it.copy(playbackPositionMs = it.playbackPositionMs + 500) }
            }
            finishKaraokePerformance()
        }
    }

    fun finishKaraokePerformance() {
        playbackJob?.cancel()
        val singerName = _uiState.value.activeSinger?.singerName ?: "Cantor(a) Convidado(a)"
        val songTitle = _uiState.value.karaokeTrack.title
        val eval = KaraokePitchEngine.evaluatePerformance(
            pitchMatchCount,
            pitchTotalSamples,
            singerName,
            songTitle
        )
        _uiState.update {
            it.copy(
                evaluation = eval,
                isPlaying = false
            )
        }
    }

    fun dismissEvaluation() {
        _uiState.update {
            it.copy(
                evaluation = null,
                isKaraokeActive = false
            )
        }
    }

    fun addSingerToQueue(name: String, track: Track) {
        if (name.isBlank()) return
        val newSinger = SingerInQueue(
            id = "sq_${System.currentTimeMillis()}",
            singerName = name.trim(),
            track = track
        )
        _uiState.update { it.copy(singerQueue = it.singerQueue + newSinger) }
    }

    fun removeSingerFromQueue(singerId: String) {
        _uiState.update {
            it.copy(singerQueue = it.singerQueue.filterNot { s -> s.id == singerId })
        }
    }

    fun playNextSingerInQueue() {
        val next = _uiState.value.singerQueue.firstOrNull { !it.isCompleted } ?: return
        startKaraoke(next.track, next)
    }

    fun toggleDuetMode() {
        _uiState.update { it.copy(isDuetMode = !it.isDuetMode) }
    }

    fun toggleDuelMode() {
        _uiState.update { it.copy(isDuelMode = !it.isDuelMode) }
    }

    fun setGuideVocalVolume(vol: Float) {
        _uiState.update { it.copy(guideVocalVolume = vol) }
    }

    fun setMicVolume(vol: Float) {
        _uiState.update { it.copy(micVolume = vol) }
    }

    fun setReverbLevel(level: Float) {
        _uiState.update { it.copy(reverbLevel = level) }
    }

    fun toggleKaraokeFullscreen(enabled: Boolean? = null) {
        _uiState.update { it.copy(isKaraokeFullscreen = enabled ?: !it.isKaraokeFullscreen) }
    }

    fun setKaraokeDisplayMode(mode: KaraokeDisplayMode) {
        _uiState.update { it.copy(karaokeDisplayMode = mode) }
    }

    fun toggleKaraokeDisplayMode() {
        _uiState.update {
            val nextMode = if (it.karaokeDisplayMode == KaraokeDisplayMode.WITH_VIDEO) {
                KaraokeDisplayMode.LYRICS_ONLY
            } else {
                KaraokeDisplayMode.WITH_VIDEO
            }
            it.copy(karaokeDisplayMode = nextMode)
        }
    }

    private fun startPitchEngineSimulation() {
        pitchSimulationJob?.cancel()
        pitchSimulationJob = viewModelScope.launch {
            while (true) {
                delay(350)
                if (_uiState.value.isKaraokeActive && _uiState.value.isPlaying) {
                    val track = _uiState.value.karaokeTrack
                    val pos = _uiState.value.playbackPositionMs
                    val currentLyric = track.lyrics.lastOrNull { it.timeMs <= pos }
                    val target = currentLyric?.targetPitch ?: 0.5f

                    // Simulate slight human pitch variance
                    val jitter = (Random.nextFloat() - 0.5f) * 0.16f
                    val userPitch = (target + jitter).coerceIn(0.1f, 0.95f)

                    val isClose = abs(userPitch - target) < 0.14f
                    pitchTotalSamples++
                    if (isClose) pitchMatchCount++

                    val targetScore = if (isClose) {
                        (_uiState.value.liveScore + 1).coerceAtMost(99)
                    } else {
                        (_uiState.value.liveScore - 1).coerceAtLeast(60)
                    }

                    _uiState.update {
                        it.copy(
                            userPitch = userPitch,
                            targetPitch = target,
                            liveScore = targetScore
                        )
                    }
                }
            }
        }
    }

    // --- VIDEO PLAYER ---

    fun selectVideo(video: VideoItem) {
        _uiState.update {
            it.copy(
                activeVideo = video,
                isVideoPlaying = true
            )
        }
    }

    fun toggleVideoPlay() {
        _uiState.update { it.copy(isVideoPlaying = !it.isVideoPlaying) }
    }

    fun setVideoPlaying(isPlaying: Boolean) {
        _uiState.update { it.copy(isVideoPlaying = isPlaying) }
    }

    fun toggleVideoFullscreen(enabled: Boolean? = null) {
        _uiState.update { it.copy(isVideoFullscreen = enabled ?: !it.isVideoFullscreen) }
    }

    fun playNextVideo() {
        val list = MockMediaLibrary.videoItems.filter { it.category == _uiState.value.selectedVideoCategory }
        if (list.isNotEmpty()) {
            val currentIndex = list.indexOfFirst { it.id == _uiState.value.activeVideo.id }
            val nextIndex = if (currentIndex < 0) 0 else (currentIndex + 1) % list.size
            _uiState.update { it.copy(activeVideo = list[nextIndex], isVideoPlaying = true) }
        }
    }

    fun playPreviousVideo() {
        val list = MockMediaLibrary.videoItems.filter { it.category == _uiState.value.selectedVideoCategory }
        if (list.isNotEmpty()) {
            val currentIndex = list.indexOfFirst { it.id == _uiState.value.activeVideo.id }
            val prevIndex = if (currentIndex <= 0) list.size - 1 else currentIndex - 1
            _uiState.update { it.copy(activeVideo = list[prevIndex], isVideoPlaying = true) }
        }
    }

    fun setVideoCategory(category: VideoCategory) {
        _uiState.update { it.copy(selectedVideoCategory = category) }
    }

    // --- RETRO GAMES & TRIVIA QUIZ ---

    fun selectGameMode(mode: RetroGameMode) {
        _uiState.update {
            it.copy(
                activeGame = mode,
                arcadeGameOver = false,
                quizSelectedOption = null
            )
        }
    }

    fun answerQuizOption(optionIndex: Int) {
        val currentQ = MockMediaLibrary.quizQuestions[_uiState.value.quizCurrentIndex]
        val isCorrect = optionIndex == currentQ.correctIndex
        val bonus = if (isCorrect) 100 else 0
        _uiState.update {
            it.copy(
                quizSelectedOption = optionIndex,
                quizScore = it.quizScore + bonus
            )
        }

        viewModelScope.launch {
            delay(1200)
            val nextIndex = _uiState.value.quizCurrentIndex + 1
            if (nextIndex < MockMediaLibrary.quizQuestions.size) {
                _uiState.update {
                    it.copy(
                        quizCurrentIndex = nextIndex,
                        quizSelectedOption = null
                    )
                }
            } else {
                _uiState.update {
                    it.copy(quizFinished = true)
                }
            }
        }
    }

    fun restartQuiz() {
        _uiState.update {
            it.copy(
                quizCurrentIndex = 0,
                quizSelectedOption = null,
                quizScore = 0,
                quizFinished = false
            )
        }
    }

    fun triggerArcadeAction(action: String) {
        when (action) {
            "TAP_BEAT" -> {
                val hit = Random.nextBoolean()
                _uiState.update {
                    if (hit) {
                        it.copy(
                            rhythmScore = it.rhythmScore + 50,
                            rhythmCombo = it.rhythmCombo + 1
                        )
                    } else {
                        it.copy(rhythmCombo = 0)
                    }
                }
            }
            "SHOOT" -> {
                _uiState.update {
                    it.copy(arcadeScore = it.arcadeScore + 25)
                }
            }
            "RESTART_ARCADE" -> {
                _uiState.update {
                    it.copy(
                        arcadeScore = 0,
                        arcadeLives = 3,
                        arcadeGameOver = false,
                        rhythmScore = 0,
                        rhythmCombo = 0
                    )
                }
            }
        }
    }

    // --- SETTINGS ---

    fun cleanCache() {
        viewModelScope.launch {
            _uiState.update { it.copy(cacheSizeMb = "0.0 KB", cacheCleanedToast = true) }
            delay(2500)
            _uiState.update { it.copy(cacheCleanedToast = false) }
        }
    }

    fun toggleParentalLock() {
        _uiState.update { it.copy(parentalLockEnabled = !it.parentalLockEnabled) }
    }
}
