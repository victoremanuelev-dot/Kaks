package com.example.ui.components

import android.annotation.SuppressLint
import android.graphics.Color as AndroidColor
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun YouTubePlayerView(
    videoId: String,
    modifier: Modifier = Modifier,
    autoplay: Boolean = true
) {
    val cleanVideoId = remember(videoId) { extractYouTubeId(videoId) }

    val htmlData = remember(cleanVideoId, autoplay) {
        """
        <!DOCTYPE html>
        <html>
        <head>
            <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
            <style>
                * { margin: 0; padding: 0; box-sizing: border-box; }
                html, body { width: 100%; height: 100%; background-color: #000; overflow: hidden; }
                .video-wrapper { position: relative; width: 100%; height: 100%; }
                iframe { width: 100%; height: 100%; border: 0; }
            </style>
        </head>
        <body>
            <div class="video-wrapper">
                <iframe 
                    src="https://www.youtube-nocookie.com/embed/$cleanVideoId?autoplay=${if (autoplay) 1 else 0}&controls=1&rel=0&modestbranding=1&fs=1&enablejsapi=1"
                    allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share"
                    allowfullscreen>
                </iframe>
            </div>
        </body>
        </html>
        """.trimIndent()
    }

    Box(modifier = modifier.fillMaxSize().background(Color.Black)) {
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
                    webChromeClient = WebChromeClient()
                    webViewClient = WebViewClient()
                    loadDataWithBaseURL("https://www.youtube.com", htmlData, "text/html", "UTF-8", null)
                }
            },
            update = { webView ->
                webView.loadDataWithBaseURL("https://www.youtube.com", htmlData, "text/html", "UTF-8", null)
            }
        )
    }
}

/**
 * Extracts YouTube video ID from various formats:
 * - standard ID: "A22oy8dFjqc"
 * - watch URL: "https://www.youtube.com/watch?v=A22oy8dFjqc"
 * - share URL: "https://youtu.be/A22oy8dFjqc"
 * - embed URL: "https://www.youtube.com/embed/A22oy8dFjqc"
 */
fun extractYouTubeId(urlOrId: String): String {
    val trimmed = urlOrId.trim()
    return when {
        trimmed.contains("watch?v=") -> {
            trimmed.substringAfter("watch?v=").substringBefore("&")
        }
        trimmed.contains("youtu.be/") -> {
            trimmed.substringAfter("youtu.be/").substringBefore("?")
        }
        trimmed.contains("embed/") -> {
            trimmed.substringAfter("embed/").substringBefore("?")
        }
        else -> trimmed
    }
}
