package com.example.model

enum class MainTab(val title: String, val iconName: String) {
    HOME("Home", "home"),
    JUKEBOX("Jukebox", "music_note"),
    KARAOKE("Karaokê", "mic"),
    VIDEOS("Vídeos", "movie"),
    GAMES("Jogos", "sports_esports"),
    SETTINGS("Config", "settings")
}

enum class AppThemeMode(val displayName: String, val description: String) {
    NEON_CYBER("Neon Cyber (Padrão)", "Visual escuro futurista com ciano e magenta brilhante"),
    RETRO_JUKEBOX("Retrô Jukebox", "Nostalgia anos 70/80 com madeira nobre e luzes douradas"),
    MODERN_MINIMAL("Modern TV", "Interface limpa, sóbria e minimalista estilo Google/Apple TV")
}

data class LyricLine(
    val timeMs: Long,
    val text: String,
    val singerRole: String = "ALL", // "LEAD", "BACKING", "DUET_1", "DUET_2", "ALL"
    val targetPitch: Float = 0.5f // 0.0f to 1.0f relative pitch
)

enum class KaraokeDisplayMode(val displayName: String) {
    WITH_VIDEO("Com Vídeo de Fundo 🎬"),
    LYRICS_ONLY("Só Letras Neon 📜")
}

data class Track(
    val id: String,
    val title: String,
    val artist: String,
    val album: String,
    val genre: String,
    val decade: String,
    val durationSeconds: Int,
    val coverGradientStart: Long,
    val coverGradientEnd: Long,
    val lyrics: List<LyricLine> = emptyList(),
    val isKaraokeReady: Boolean = true,
    val playsCount: Int = 1200,
    val youtubeVideoId: String = ""
)

data class SingerInQueue(
    val id: String,
    val singerName: String,
    val track: Track,
    val timestamp: Long = System.currentTimeMillis(),
    val isCompleted: Boolean = false,
    val score: Int? = null
)

enum class VideoCategory(val displayName: String) {
    SHOW("Shows Completos"),
    CLIPE("Clipes Musicais"),
    AMBIENTE("Visuais de Fundo (Festa)")
}

data class VideoItem(
    val id: String,
    val title: String,
    val artistOrChannel: String,
    val duration: String,
    val category: VideoCategory,
    val bgGradientStart: Long,
    val bgGradientEnd: Long,
    val description: String = "",
    val youtubeVideoId: String = ""
)

data class QuizQuestion(
    val id: Int,
    val question: String,
    val hint: String,
    val options: List<String>,
    val correctIndex: Int,
    val genre: String
)

data class KaraokeEvaluation(
    val totalScore: Int,
    val pitchAccuracy: Int, // 0 - 100
    val timingAccuracy: Int, // 0 - 100
    val judgeName: String,
    val judgeComment: String,
    val trophy: String // "OURO 🏆", "PRATA 🥈", "BRONZE 🥉"
)

enum class RetroGameMode(val title: String, val subtitle: String) {
    SPACE_DEFENDER("Neon Space Invader", "Defenda a galáxia com o D-pad e tiro"),
    RHYTHM_BEAT("Rhythm Beat Tapper", "Acerte as notas neon no ritmo da música"),
    SNAKE_NEON("Cyber Snake 2099", "A clássica cobrinha em estilo synthwave"),
    MUSIC_QUIZ("Quiz Musical da Galera", "Desafio de adivinhar quem canta ou o nome do hit")
}
