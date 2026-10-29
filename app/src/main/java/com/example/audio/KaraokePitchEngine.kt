package com.example.audio

import com.example.model.KaraokeEvaluation
import kotlin.random.Random

object KaraokePitchEngine {

    private val juradoPersonas = listOf(
        Pair("Maestro Carlinhos", listOf(
            "Sensacional! A afinação nas notas agudas foi de arrepiar a galera na sala!",
            "Muito bom! Você segurou o tempo perfeitamente no refrão!",
            "Bela performance! Com um tiquinho mais de ar no peito, você vira estrela nacional!",
            "Corajoso! Faltou um pouquinho de afinação no final, mas a energia compensou tudo!"
        )),
        Pair("Diva Roberta", listOf(
            "Que voz! Parece que você nasceu cantando esse hit no chuveiro e nos palcos!",
            "Excelente dicção e presença! O duelo vocal aqui foi digno de festival!",
            "Afinação cristalina! O público da sala ficou impressionado!",
            "Muito carisma e pegada! O vibrato no final levantou a nota geral!"
        )),
        Pair("Rockstar Rex", listOf(
            "Isso sim é atitude de palco! A batida foi seguida com precisão cirúrgica!",
            "Explosão vocal! O afinador quase não acreditou na estabilidade desse refrão!",
            "Mandou muito bem! Valeu cada nota arriscada!",
            "Nota 10 no carisma e 9 na afinação! A festa ganhou um novo astro!"
        ))
    )

    fun evaluatePerformance(
        pitchMatchHits: Int,
        totalCheckedSamples: Int,
        singerName: String,
        songTitle: String
    ): KaraokeEvaluation {
        val total = if (totalCheckedSamples <= 0) 1 else totalCheckedSamples
        val rawAccuracy = (pitchMatchHits.toFloat() / total * 100).toInt().coerceIn(45, 99)
        val timingAccuracy = (rawAccuracy + Random.nextInt(-4, 6)).coerceIn(50, 100)
        val totalScore = ((rawAccuracy * 0.65f) + (timingAccuracy * 0.35f)).toInt().coerceIn(50, 100)

        val (judgeName, comments) = juradoPersonas.random()
        val baseComment = comments.random()

        val trophy = when {
            totalScore >= 90 -> "OURO 🏆"
            totalScore >= 75 -> "PRATA 🥈"
            else -> "BRONZE 🥉"
        }

        val fullFeedback = "$baseComment ($singerName em '$songTitle')"

        return KaraokeEvaluation(
            totalScore = totalScore,
            pitchAccuracy = rawAccuracy,
            timingAccuracy = timingAccuracy,
            judgeName = judgeName,
            judgeComment = fullFeedback,
            trophy = trophy
        )
    }

    fun generateAISuggestions(historyGenre: String): List<String> {
        return when (historyGenre.lowercase()) {
            "rock" -> listOf(
                "Queen - We Are The Champions",
                "Bon Jovi - Livin' on a Prayer",
                "Capital Inicial - Primeiros Erros",
                "Raul Seixas - Metamorfose Ambulante"
            )
            "sertanejo" -> listOf(
                "Jorge & Mateus - Amo Noite e Dia",
                "Leandro & Leonardo - Pense em Mim",
                "Marília Mendonça - Infiel",
                "Zezé Di Camargo - É o Amor"
            )
            "pop" -> listOf(
                "Dua Lipa - Don't Start Now",
                "The Weeknd - Blinding Lights",
                "Anitta - Envolver",
                "Iza - Pesadão"
            )
            else -> listOf(
                "Chitãozinho & Xororó - Evidências",
                "Tim Maia - Gostava Tanto de Você",
                "Legião Urbana - Pais e Filhos",
                "A-ha - Take On Me"
            )
        }
    }
}
