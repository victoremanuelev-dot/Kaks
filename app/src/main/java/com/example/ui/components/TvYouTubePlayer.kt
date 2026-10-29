package com.example.ui.components

import android.annotation.SuppressLint
import android.graphics.Color as AndroidColor
import android.view.SoundEffectConstants
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ui.theme.LocalCustomTheme
import kotlinx.coroutines.delay
import java.util.Locale

/**
 * TV-optimized YouTube Player component with D-pad controls, official IFrame Player API,
 * hardware acceleration, live time scrubber, and TV lounge HUD overlay.
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun TvYouTubePlayer(
    videoId: String,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    autoplay: Boolean = true,
    isFullscreen: Boolean = false,
    onToggleFullscreen: () -> Unit = {},
    onPreviousVideo: () -> Unit = {},
    onNextVideo: () -> Unit = {},
    onVideoEnded: () -> Unit = {},
    onPlaybackStateChange: (Boolean) -> Unit = {}
) {
    val theme = LocalCustomTheme.current
    val view = LocalView.current
    val cleanVideoId = remember(videoId) { extractYouTubeId(videoId) }

    var webViewRef by remember { mutableStateOf<WebView?>(null) }
    var isPlaying by remember { mutableStateOf(autoplay) }
    var currentTimeSec by remember { mutableStateOf(0f) }
    var totalDurationSec by remember { mutableStateOf(0f) }
    var isMuted by remember { mutableStateOf(false) }
    var playbackSpeed by remember { mutableStateOf(1.0f) }
    var showHud by remember { mutableStateOf(true) }
    var lastInteractionTime by remember { mutableStateOf(System.currentTimeMillis()) }
    var centerActionFeedback by remember { mutableStateOf<String?>(null) }

    val playPauseFocusRequester = remember { FocusRequester() }

    // Auto-hide HUD after 4.5 seconds of user inactivity when playing
    LaunchedEffect(showHud, lastInteractionTime, isPlaying) {
        if (showHud && isPlaying) {
            delay(4500)
            showHud = false
        }
    }

    // Dismiss transient action feedback
    LaunchedEffect(centerActionFeedback) {
        if (centerActionFeedback != null) {
            delay(800)
            centerActionFeedback = null
        }
    }

    fun markActive() {
        showHud = true
        lastInteractionTime = System.currentTimeMillis()
    }

    // Player JS command helpers
    fun sendCommand(js: String) {
        webViewRef?.post {
            webViewRef?.evaluateJavascript(js, null)
        }
    }

    fun togglePlayPause() {
        markActive()
        if (isPlaying) {
            sendCommand("pause();")
            isPlaying = false
            centerActionFeedback = "PAUSE"
            onPlaybackStateChange(false)
        } else {
            sendCommand("play();")
            isPlaying = true
            centerActionFeedback = "PLAY"
            onPlaybackStateChange(true)
        }
    }

    fun seekRelative(deltaSec: Float) {
        markActive()
        val target = (currentTimeSec + deltaSec).coerceIn(0f, totalDurationSec.coerceAtLeast(1f))
        currentTimeSec = target
        sendCommand("seekTo($target);")
        centerActionFeedback = if (deltaSec > 0) "+10s" else "-10s"
    }

    fun toggleMute() {
        markActive()
        if (isMuted) {
            sendCommand("unMute();")
            isMuted = false
        } else {
            sendCommand("mute();")
            isMuted = true
        }
    }

    fun cycleSpeed() {
        markActive()
        val nextSpeed = when (playbackSpeed) {
            1.0f -> 1.25f
            1.25f -> 1.5f
            1.5f -> 0.75f
            else -> 1.0f
        }
        playbackSpeed = nextSpeed
        sendCommand("setSpeed($nextSpeed);")
        centerActionFeedback = "${nextSpeed}x"
    }

    // Fullscreen back handling
    if (isFullscreen) {
        BackHandler {
            onToggleFullscreen()
        }
    }

    // HTML with official YouTube IFrame API and native Android bridge
    val htmlContent = remember(cleanVideoId) {
        """
        <!DOCTYPE html>
        <html>
        <head>
            <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
            <style>
                * { margin: 0; padding: 0; box-sizing: border-box; }
                html, body { width: 100%; height: 100%; background-color: #000; overflow: hidden; }
                #player { position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: 0; }
            </style>
        </head>
        <body>
            <div id="player"></div>
            <script src="https://www.youtube.com/iframe_api"></script>
            <script>
                var player;
                var pollInterval;

                function onYouTubeIframeAPIReady() {
                    player = new YT.Player('player', {
                        height: '100%',
                        width: '100%',
                        videoId: '$cleanVideoId',
                        playerVars: {
                            'autoplay': ${if (autoplay) 1 else 0},
                            'controls': 0,
                            'rel': 0,
                            'modestbranding': 1,
                            'fs': 0,
                            'playsinline': 1,
                            'iv_load_policy': 3,
                            'disablekb': 1
                        },
                        events: {
                            'onReady': onPlayerReady,
                            'onStateChange': onPlayerStateChange
                        }
                    });
                }

                function onPlayerReady(event) {
                    if (window.AndroidBridge) {
                        window.AndroidBridge.onReady();
                    }
                    if (${autoplay}) {
                        player.playVideo();
                    }
                    startPolling();
                }

                function onPlayerStateChange(event) {
                    if (window.AndroidBridge) {
                        window.AndroidBridge.postState(event.data);
                    }
                }

                function startPolling() {
                    if (pollInterval) clearInterval(pollInterval);
                    pollInterval = setInterval(function() {
                        if (player && player.getCurrentTime && player.getDuration) {
                            var cur = player.getCurrentTime() || 0;
                            var dur = player.getDuration() || 0;
                            if (window.AndroidBridge) {
                                window.AndroidBridge.postTime(cur, dur);
                            }
                        }
                    }, 500);
                }

                function play() { if (player && player.playVideo) player.playVideo(); }
                function pause() { if (player && player.pauseVideo) player.pauseVideo(); }
                function seekTo(sec) { if (player && player.seekTo) player.seekTo(sec, true); }
                function mute() { if (player && player.mute) player.mute(); }
                function unMute() { if (player && player.unMute) player.unMute(); }
                function setSpeed(rate) { if (player && player.setPlaybackRate) player.setPlaybackRate(rate); }
            </script>
        </body>
        </html>
        """.trimIndent()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("tv_youtube_player_container")
            .focusable()
            .onKeyEvent { keyEvent ->
                if (keyEvent.type == KeyEventType.KeyDown) {
                    markActive()
                    when (keyEvent.key) {
                        Key.DirectionCenter, Key.Enter, Key.NumPadEnter, Key.Spacebar, Key.ButtonA -> {
                            togglePlayPause()
                            true
                        }
                        Key.DirectionLeft -> {
                            seekRelative(-10f)
                            true
                        }
                        Key.DirectionRight -> {
                            seekRelative(10f)
                            true
                        }
                        Key.MediaPlayPause -> {
                            togglePlayPause()
                            true
                        }
                        Key.MediaPlay -> {
                            if (!isPlaying) togglePlayPause()
                            true
                        }
                        Key.MediaPause -> {
                            if (isPlaying) togglePlayPause()
                            true
                        }
                        Key.MediaFastForward -> {
                            seekRelative(15f)
                            true
                        }
                        Key.MediaRewind -> {
                            seekRelative(-15f)
                            true
                        }
                        Key.MediaNext -> {
                            onNextVideo()
                            true
                        }
                        Key.MediaPrevious -> {
                            onPreviousVideo()
                            true
                        }
                        else -> false
                    }
                } else false
            }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { markActive() }
            )
    ) {
        // EMBEDDED WEBVIEW WITH JS BRIDGE
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                WebView(context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    setBackgroundColor(AndroidColor.BLACK)
                    settings.apply {
                        javaScriptEnabled = true
                        domStorageEnabled = true
                        mediaPlaybackRequiresUserGesture = false
                        loadWithOverviewMode = true
                        useWideViewPort = true
                        mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                        cacheMode = WebSettings.LOAD_DEFAULT
                    }

                    class AndroidPlayerBridge {
                        @JavascriptInterface
                        fun onReady() {
                            post { isPlaying = autoplay }
                        }

                        @JavascriptInterface
                        fun postState(state: Int) {
                            post {
                                when (state) {
                                    0 -> { // ENDED
                                        isPlaying = false
                                        onVideoEnded()
                                    }
                                    1 -> { // PLAYING
                                        isPlaying = true
                                        onPlaybackStateChange(true)
                                    }
                                    2 -> { // PAUSED
                                        isPlaying = false
                                        onPlaybackStateChange(false)
                                    }
                                }
                            }
                        }

                        @JavascriptInterface
                        fun postTime(current: Float, duration: Float) {
                            post {
                                currentTimeSec = current
                                totalDurationSec = duration
                            }
                        }
                    }

                    addJavascriptInterface(AndroidPlayerBridge(), "AndroidBridge")
                    webChromeClient = WebChromeClient()
                    webViewClient = WebViewClient()
                    loadDataWithBaseURL("https://www.youtube.com", htmlContent, "text/html", "UTF-8", null)
                    webViewRef = this
                }
            },
            update = { wv ->
                wv.loadDataWithBaseURL("https://www.youtube.com", htmlContent, "text/html", "UTF-8", null)
            }
        )

        // TRANSIENT CENTER ACTION FEEDBACK (PLAY, PAUSE, +10s, etc.)
        AnimatedVisibility(
            visible = centerActionFeedback != null,
            enter = fadeIn(tween(150)) + scaleIn(tween(150)),
            exit = fadeOut(tween(200)) + scaleOut(tween(200)),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.75f))
                    .border(2.dp, theme.primaryNeon, CircleShape)
                    .padding(22.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = centerActionFeedback ?: "",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = theme.primaryNeon
                )
            }
        }

        // TV 10-FOOT D-PAD OVERLAY HUD
        AnimatedVisibility(
            visible = showHud,
            enter = fadeIn(tween(200)),
            exit = fadeOut(tween(300)),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.Black.copy(alpha = 0.82f),
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.90f)
                            )
                        )
                    )
                    .padding(if (isFullscreen) 24.dp else 12.dp)
            ) {
                // TOP BAR (Title, Channel, YouTube HD Badge, Fullscreen Exit)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopStart),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFFF0000))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "YOUTUBE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        }

                        Column {
                            Text(
                                text = title,
                                fontSize = if (isFullscreen) 18.sp else 14.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = subtitle,
                                fontSize = if (isFullscreen) 12.sp else 10.sp,
                                color = theme.primaryNeon,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Speed Indicator Button
                        TvFocusableIconButton(
                            onClick = { cycleSpeed() },
                            icon = Icons.Default.Speed,
                            contentDescription = "Velocidade de reprodução",
                            size = 38.dp,
                            iconSize = 18.dp,
                            tint = theme.primaryNeon,
                            glowColor = theme.primaryNeon,
                            testTag = "tv_player_speed_btn"
                        )

                        // Fullscreen / Cinema Mode Button
                        TvFocusableIconButton(
                            onClick = onToggleFullscreen,
                            icon = if (isFullscreen) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                            contentDescription = if (isFullscreen) "Sair da Tela Cheia" else "Modo Tela Cheia",
                            size = 38.dp,
                            iconSize = 20.dp,
                            tint = Color.White,
                            glowColor = theme.secondaryNeon,
                            testTag = "tv_player_fullscreen_btn"
                        )
                    }
                }

                // BOTTOM BAR (Scrubber & 10-Foot Focusable Action Buttons)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Time Scrubber & Timestamps
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = formatTime(currentTimeSec),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        val progressFraction = if (totalDurationSec > 0f) {
                            (currentTimeSec / totalDurationSec).coerceIn(0f, 1f)
                        } else 0f

                        Slider(
                            value = progressFraction,
                            onValueChange = { frac ->
                                markActive()
                                val target = frac * totalDurationSec
                                currentTimeSec = target
                                sendCommand("seekTo($target);")
                            },
                            colors = SliderDefaults.colors(
                                thumbColor = theme.primaryNeon,
                                activeTrackColor = theme.primaryNeon,
                                inactiveTrackColor = Color.White.copy(alpha = 0.25f)
                            ),
                            modifier = Modifier.weight(1f).height(24.dp)
                        )

                        Text(
                            text = formatTime(totalDurationSec),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = theme.textSecondary
                        )
                    }

                    // 10-Foot D-Pad Focusable Control Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Previous Video
                        TvFocusableIconButton(
                            onClick = {
                                markActive()
                                onPreviousVideo()
                            },
                            icon = Icons.Default.SkipPrevious,
                            contentDescription = "Vídeo Anterior",
                            size = 42.dp,
                            iconSize = 22.dp,
                            tint = Color.White,
                            glowColor = theme.primaryNeon,
                            testTag = "tv_player_prev"
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        // Rewind 10s
                        TvFocusableIconButton(
                            onClick = { seekRelative(-10f) },
                            icon = Icons.Default.Replay10,
                            contentDescription = "Voltar 10 segundos",
                            size = 42.dp,
                            iconSize = 22.dp,
                            tint = Color.White,
                            glowColor = theme.primaryNeon,
                            testTag = "tv_player_rewind_10"
                        )

                        Spacer(modifier = Modifier.width(14.dp))

                        // PRIMARY PLAY / PAUSE BUTTON (High Contrast Neon Focus)
                        TvFocusableIconButton(
                            onClick = { togglePlayPause() },
                            icon = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pausar Vídeo" else "Reproduzir Vídeo",
                            size = 54.dp,
                            iconSize = 32.dp,
                            tint = Color.Black,
                            containerColor = theme.primaryNeon,
                            glowColor = theme.primaryNeon,
                            testTag = "tv_player_play_pause"
                        )

                        Spacer(modifier = Modifier.width(14.dp))

                        // Fast Forward 10s
                        TvFocusableIconButton(
                            onClick = { seekRelative(10f) },
                            icon = Icons.Default.Forward10,
                            contentDescription = "Avançar 10 segundos",
                            size = 42.dp,
                            iconSize = 22.dp,
                            tint = Color.White,
                            glowColor = theme.primaryNeon,
                            testTag = "tv_player_forward_10"
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        // Next Video
                        TvFocusableIconButton(
                            onClick = {
                                markActive()
                                onNextVideo()
                            },
                            icon = Icons.Default.SkipNext,
                            contentDescription = "Próximo Vídeo",
                            size = 42.dp,
                            iconSize = 22.dp,
                            tint = Color.White,
                            glowColor = theme.primaryNeon,
                            testTag = "tv_player_next"
                        )

                        Spacer(modifier = Modifier.width(14.dp))

                        // Mute / Unmute Toggle
                        TvFocusableIconButton(
                            onClick = { toggleMute() },
                            icon = if (isMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                            contentDescription = if (isMuted) "Ativar Som" else "Silenciar",
                            size = 42.dp,
                            iconSize = 20.dp,
                            tint = if (isMuted) Color(0xFFFF5252) else Color.White,
                            glowColor = theme.secondaryNeon,
                            testTag = "tv_player_mute"
                        )
                    }
                }
            }
        }
    }
}

private fun formatTime(seconds: Float): String {
    val totalSec = seconds.toInt().coerceAtLeast(0)
    val hrs = totalSec / 3600
    val mins = (totalSec % 3600) / 60
    val secs = totalSec % 60
    return if (hrs > 0) {
        String.format(Locale.getDefault(), "%02d:%02d:%02d", hrs, mins, secs)
    } else {
        String.format(Locale.getDefault(), "%02d:%02d", mins, secs)
    }
}
