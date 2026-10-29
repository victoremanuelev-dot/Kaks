package com.example.data

import com.example.model.*

object MockMediaLibrary {

    val tracks: List<Track> = listOf(
        Track(
            id = "t1",
            title = "Evidências",
            artist = "Chitãozinho & Xororó",
            album = "Cowboy do Asfalto",
            genre = "Sertanejo",
            decade = "Anos 90",
            durationSeconds = 278,
            coverGradientStart = 0xFFF72585,
            coverGradientEnd = 0xFF7209B7,
            isKaraokeReady = true,
            playsCount = 4820,
            youtubeVideoId = "ePjtnSPFWK8",
            lyrics = listOf(
                LyricLine(0L, "♪ (Intro instrumental suave de piano) ♪", "ALL", 0.3f),
                LyricLine(4000L, "Quando eu digo que deixei de te amar", "LEAD", 0.45f),
                LyricLine(8500L, "É porque eu te amo...", "LEAD", 0.55f),
                LyricLine(12500L, "Quando eu digo que não quero mais você", "LEAD", 0.48f),
                LyricLine(17000L, "É porque eu te quero...", "LEAD", 0.60f),
                LyricLine(21500L, "Eu tenho medo de te dar meu coração", "LEAD", 0.52f),
                LyricLine(26000L, "E confessar que eu estou em tuas mãos", "LEAD", 0.65f),
                LyricLine(31000L, "Mas pra que inventar mentiras?", "DUET_1", 0.70f),
                LyricLine(35500L, "E negar o meu desejo...", "DUET_2", 0.75f),
                LyricLine(40000L, "E nessa loucura de dizer que não te quero...", "ALL", 0.88f),
                LyricLine(45000L, "Vou negando as aparências, disfarçando as evidências!", "ALL", 0.95f),
                LyricLine(51000L, "Mas pra que viver fingindo se eu não posso enganar meu coração?!", "ALL", 0.92f),
                LyricLine(58000L, "Eu sei que te amo! Chega de mentiras!", "ALL", 0.98f)
            )
        ),
        Track(
            id = "t2",
            title = "Bohemian Rhapsody",
            artist = "Queen",
            album = "A Night at the Opera",
            genre = "Rock",
            decade = "Anos 70",
            durationSeconds = 354,
            coverGradientStart = 0xFF4361EE,
            coverGradientEnd = 0xFF3A0CA3,
            isKaraokeReady = true,
            playsCount = 6120,
            youtubeVideoId = "fJ9rUzIMcZQ",
            lyrics = listOf(
                LyricLine(0L, "Is this the real life? Is this just fantasy?", "ALL", 0.40f),
                LyricLine(6000L, "Caught in a landslide, no escape from reality", "ALL", 0.45f),
                LyricLine(12000L, "Open your eyes, look up to the skies and see...", "LEAD", 0.55f),
                LyricLine(18000L, "I'm just a poor boy, I need no sympathy", "LEAD", 0.48f),
                LyricLine(24000L, "Because I'm easy come, easy go, little high, little low", "ALL", 0.65f),
                LyricLine(30000L, "Mama, just killed a man...", "LEAD", 0.60f),
                LyricLine(36000L, "Put a gun against his head, pulled my trigger, now he's dead", "LEAD", 0.58f),
                LyricLine(43000L, "Mama, life had just begun...", "LEAD", 0.72f),
                LyricLine(49000L, "Scaramouche, Scaramouche, will you do the Fandango?!", "ALL", 0.90f)
            )
        ),
        Track(
            id = "t3",
            title = "Take On Me",
            artist = "A-ha",
            album = "Hunting High and Low",
            genre = "Pop / Flashback",
            decade = "Anos 80",
            durationSeconds = 227,
            coverGradientStart = 0xFF00E5FF,
            coverGradientEnd = 0xFF7928CA,
            isKaraokeReady = true,
            playsCount = 5340,
            youtubeVideoId = "djV11Xbc914",
            lyrics = listOf(
                LyricLine(0L, "♪ (Sintetizador icônico dos anos 80) ♪", "ALL", 0.5f),
                LyricLine(5000L, "We're talking away, I don't know what I'm to say", "LEAD", 0.52f),
                LyricLine(10000L, "I'll say it anyway, today's another day to find you", "LEAD", 0.58f),
                LyricLine(16000L, "Shying away, I'll be coming for your love, OK?", "LEAD", 0.62f),
                LyricLine(22000L, "Take on me! (Take on me!)", "ALL", 0.78f),
                LyricLine(28000L, "Take me on! (Take on me!)", "ALL", 0.85f),
                LyricLine(34000L, "I'll be gone...", "LEAD", 0.95f),
                LyricLine(39000L, "In a day or twooooo!", "LEAD", 0.99f)
            )
        ),
        Track(
            id = "t4",
            title = "Tempo Perdido",
            artist = "Legião Urbana",
            album = "Dois",
            genre = "Rock / MPB",
            decade = "Anos 80",
            durationSeconds = 302,
            coverGradientStart = 0xFF00F5D4,
            coverGradientEnd = 0xFF0B525B,
            isKaraokeReady = true,
            playsCount = 3980,
            youtubeVideoId = "48m_v7_a6w8",
            lyrics = listOf(
                LyricLine(0L, "♪ (Dedilhado de guitarra melódica) ♪", "ALL", 0.4f),
                LyricLine(4000L, "Todos os dias quando acordo", "LEAD", 0.42f),
                LyricLine(8000L, "Não tenho mais o tempo que passou", "LEAD", 0.48f),
                LyricLine(13000L, "Mas tenho muito tempo...", "LEAD", 0.52f),
                LyricLine(17000L, "Temos todo o tempo do mundo!", "ALL", 0.65f),
                LyricLine(23000L, "Todos os dias antes de dormir", "LEAD", 0.45f),
                LyricLine(28000L, "Lembro e esqueço como foi o dia", "LEAD", 0.50f),
                LyricLine(33000L, "Sempre em frente: não temos tempo a perder!", "ALL", 0.75f)
            )
        ),
        Track(
            id = "t5",
            title = "Shallow",
            artist = "Lady Gaga & Bradley Cooper",
            album = "A Star Is Born",
            genre = "Pop",
            decade = "Anos 2010",
            durationSeconds = 216,
            coverGradientStart = 0xFFFF9E00,
            coverGradientEnd = 0xFFE85D04,
            isKaraokeReady = true,
            playsCount = 4450,
            youtubeVideoId = "bo_efYhV2DE",
            lyrics = listOf(
                LyricLine(0L, "Tell me somethin', girl...", "DUET_1", 0.40f),
                LyricLine(4000L, "Are you happy in this modern world?", "DUET_1", 0.46f),
                LyricLine(9000L, "Tell me somethin', boy...", "DUET_2", 0.48f),
                LyricLine(14000L, "Aren't you tired tryin' to fill that void?", "DUET_2", 0.54f),
                LyricLine(20000L, "I'm falling, in all the good times I find myself longing for change", "ALL", 0.68f),
                LyricLine(27000L, "I'm off the deep end, watch as I dive in", "DUET_2", 0.85f),
                LyricLine(32000L, "I'll never meet the ground!", "DUET_2", 0.92f),
                LyricLine(37000L, "Crash through the surface where they can't hurt us!", "ALL", 0.90f),
                LyricLine(43000L, "We're far from the shallow now!", "ALL", 0.96f)
            )
        ),
        Track(
            id = "t6",
            title = "Billie Jean",
            artist = "Michael Jackson",
            album = "Thriller",
            genre = "Pop / Flashback",
            decade = "Anos 80",
            durationSeconds = 294,
            coverGradientStart = 0xFF7209B7,
            coverGradientEnd = 0xFF3F37C9,
            isKaraokeReady = true,
            playsCount = 7890,
            youtubeVideoId = "Zi_XLOBDo_Y",
            lyrics = listOf(
                LyricLine(0L, "♪ (Batida marcante de baixo e bateria) ♪", "ALL", 0.5f),
                LyricLine(4000L, "She was more like a beauty queen from a movie scene", "LEAD", 0.52f),
                LyricLine(9000L, "I said don't mind, but what do you mean, I am the one...", "LEAD", 0.60f),
                LyricLine(15000L, "Who will dance on the floor in the round?", "LEAD", 0.65f),
                LyricLine(21000L, "People always told me, be careful of what you do", "LEAD", 0.58f),
                LyricLine(27000L, "Billie Jean is not my lover!", "ALL", 0.82f),
                LyricLine(32000L, "She's just a girl who claims that I am the one!", "ALL", 0.88f),
                LyricLine(38000L, "But the kid is not my son! (Hee-hee!)", "ALL", 0.94f)
            )
        ),
        Track(
            id = "t7",
            title = "Ai Se Eu Te Pego",
            artist = "Michel Teló",
            album = "Na Balada",
            genre = "Sertanejo",
            decade = "Anos 2010",
            durationSeconds = 168,
            coverGradientStart = 0xFFFFD166,
            coverGradientEnd = 0xFFEF476F,
            isKaraokeReady = true,
            playsCount = 4120,
            youtubeVideoId = "hcm55lU9knw",
            lyrics = listOf(
                LyricLine(0L, "Nossa, nossa! Assim você me mata!", "ALL", 0.65f),
                LyricLine(5000L, "Ai, se eu te pego! Ai, ai, se eu te pego!", "ALL", 0.72f),
                LyricLine(11000L, "Delícia, delícia! Assim você me mata!", "ALL", 0.70f),
                LyricLine(16000L, "Ai, se eu te pego! Ai, ai, se eu te pego!", "ALL", 0.78f),
                LyricLine(22000L, "Sábado na balada, a galera começou a dançar...", "LEAD", 0.55f),
                LyricLine(28000L, "E passou a menina mais linda, tomei coragem e comecei a falar!", "LEAD", 0.62f)
            )
        ),
        Track(
            id = "t8",
            title = "Garota de Ipanema",
            artist = "Tom Jobim & Vinicius de Moraes",
            album = "The Composer of Desafinado Plays",
            genre = "MPB",
            decade = "Anos 60",
            durationSeconds = 212,
            coverGradientStart = 0xFF06D6A0,
            coverGradientEnd = 0xFF118AB2,
            isKaraokeReady = true,
            playsCount = 3450,
            youtubeVideoId = "sV6j8y8O_Zg",
            lyrics = listOf(
                LyricLine(0L, "Olha que coisa mais linda, mais cheia de graça", "LEAD", 0.42f),
                LyricLine(6000L, "É ela, menina, que vem e que passa", "LEAD", 0.46f),
                LyricLine(12000L, "Num doce balanço a caminho do mar...", "LEAD", 0.50f),
                LyricLine(18000L, "Moça do corpo dourado do sol de Ipanema", "LEAD", 0.52f),
                LyricLine(24000L, "O seu balançado é mais que um poema", "LEAD", 0.58f),
                LyricLine(30000L, "É a coisa mais linda que eu já vi passar!", "ALL", 0.68f)
            )
        ),
        Track(
            id = "t9",
            title = "Sweet Child O' Mine",
            artist = "Guns N' Roses",
            album = "Appetite for Destruction",
            genre = "Rock",
            decade = "Anos 80",
            durationSeconds = 356,
            coverGradientStart = 0xFFD00000,
            coverGradientEnd = 0xFF6A040F,
            isKaraokeReady = true,
            playsCount = 6840,
            lyrics = listOf(
                LyricLine(0L, "♪ (Riff inconfundível de Slash na guitarra) ♪", "ALL", 0.6f),
                LyricLine(7000L, "She's got a smile that it seems to me", "LEAD", 0.55f),
                LyricLine(12000L, "Reminds me of childhood memories...", "LEAD", 0.60f),
                LyricLine(17000L, "Where everything was as fresh as the bright blue sky", "LEAD", 0.68f),
                LyricLine(23000L, "Now and then when I see her face, she takes me away to that special place", "LEAD", 0.74f),
                LyricLine(30000L, "Whoa, whoa, sweet child o' mine!", "ALL", 0.90f),
                LyricLine(37000L, "Whoa, oh, oh, oh! Sweet love of mine!", "ALL", 0.95f)
            )
        )
    )

    val videoItems: List<VideoItem> = listOf(
        VideoItem(
            id = "v1",
            title = "Queen - Live at Wembley Stadium (Show Completo)",
            artistOrChannel = "Queen Official Channel",
            duration = "1h 48m",
            category = VideoCategory.SHOW,
            bgGradientStart = 0xFF5A189A,
            bgGradientEnd = 0xFF03045E,
            description = "O concerto lendário que marcou a história do rock mundial direto do YouTube.",
            youtubeVideoId = "A22oy8dFjqc"
        ),
        VideoItem(
            id = "v2",
            title = "Coldplay - Live in São Paulo (Full Concert)",
            artistOrChannel = "Coldplay Official",
            duration = "2h 05m",
            category = VideoCategory.SHOW,
            bgGradientStart = 0xFFE85D04,
            bgGradientEnd = 0xFF6A040F,
            description = "Show histórico gravado no Allianz Parque em São Paulo em alta definição.",
            youtubeVideoId = "rbq2o6p8Ueo"
        ),
        VideoItem(
            id = "v3",
            title = "Chitãozinho & Xororó - 50 Anos (Evidências ao Vivo)",
            artistOrChannel = "Sertanejo Clássicos",
            duration = "1h 32m",
            category = VideoCategory.SHOW,
            bgGradientStart = 0xFFF72585,
            bgGradientEnd = 0xFF3F37C9,
            description = "Concerto histórico acompanhado de orquestra sinfônica internacional no YouTube.",
            youtubeVideoId = "ePjtnSPFWK8"
        ),
        VideoItem(
            id = "v4",
            title = "A-ha - Take On Me (Clipe Oficial 4K Remaster)",
            artistOrChannel = "Warner Music Vault",
            duration = "3m 48s",
            category = VideoCategory.CLIPE,
            bgGradientStart = 0xFF00E5FF,
            bgGradientEnd = 0xFF7209B7,
            description = "A clássica animação de rotoscopia remasterizada em 4K no YouTube.",
            youtubeVideoId = "djV11Xbc914"
        ),
        VideoItem(
            id = "v5",
            title = "Michael Jackson - Billie Jean (Clipe Oficial)",
            artistOrChannel = "Michael Jackson VEVO",
            duration = "4m 55s",
            category = VideoCategory.CLIPE,
            bgGradientStart = 0xFF9D4EDD,
            bgGradientEnd = 0xFF240046,
            description = "O marco da videografia musical transmitido direto do canal oficial do YouTube.",
            youtubeVideoId = "Zi_XLOBDo_Y"
        ),
        VideoItem(
            id = "v6",
            title = "Lady Gaga, Bradley Cooper - Shallow (From A Star Is Born)",
            artistOrChannel = "Lady Gaga VEVO",
            duration = "3m 36s",
            category = VideoCategory.CLIPE,
            bgGradientStart = 0xFFFF9E00,
            bgGradientEnd = 0xFFE85D04,
            description = "Performance ganhadora do Oscar oficial no YouTube.",
            youtubeVideoId = "bo_efYhV2DE"
        ),
        VideoItem(
            id = "v7",
            title = "Guns N' Roses - Sweet Child O' Mine (Official Music Video)",
            artistOrChannel = "Guns N' Roses Official",
            duration = "5m 03s",
            category = VideoCategory.CLIPE,
            bgGradientStart = 0xFFD00000,
            bgGradientEnd = 0xFF6A040F,
            description = "O maior solo de guitarra dos anos 80 em clipe oficial no YouTube.",
            youtubeVideoId = "1w7OgIMMRc4"
        ),
        VideoItem(
            id = "v8",
            title = "Synthwave Chill Neon City - 4K Ambience Loop",
            artistOrChannel = "Lounge Party TV",
            duration = "Contínuo",
            category = VideoCategory.AMBIENTE,
            bgGradientStart = 0xFF001219,
            bgGradientEnd = 0xFF005F73,
            description = "Visuais psicodélicos synthwave para rodar na TV durante sua festa.",
            youtubeVideoId = "4xDzrJKXOOY"
        ),
        VideoItem(
            id = "v9",
            title = "Lofi Hip Hop Beats - Relax / Party Study",
            artistOrChannel = "Lofi Girl Official",
            duration = "Contínuo",
            category = VideoCategory.AMBIENTE,
            bgGradientStart = 0xFF0A0908,
            bgGradientEnd = 0xFF49111C,
            description = "Música e animações contínuas para criar uma atmosfera incrível na sala.",
            youtubeVideoId = "jfKfP_Sr57w"
        )
    )

    val quizQuestions: List<QuizQuestion> = listOf(
        QuizQuestion(
            id = 1,
            question = "Quem canta o verso lendário 'E nessa loucura de dizer que não te quero...'?",
            hint = "A dupla mais consagrada do sertanejo clássico brasileiro.",
            options = listOf("Zezé Di Camargo & Luciano", "Chitãozinho & Xororó", "Leandro & Leonardo", "Bruno & Marrone"),
            correctIndex = 1,
            genre = "Sertanejo"
        ),
        QuizQuestion(
            id = 2,
            question = "Qual vocalista britânico eternizou o hino 'Bohemian Rhapsody' em 1975?",
            hint = "Famoso por sua incrível extensão vocal de 4 oitavas e bigode icônico.",
            options = listOf("Freddie Mercury", "David Bowie", "Elton John", "Mick Jagger"),
            correctIndex = 0,
            genre = "Rock"
        ),
        QuizQuestion(
            id = 3,
            question = "Em que década a banda norueguesa A-ha lançou o megahit 'Take On Me'?",
            hint = "A era dos sintetizadores analógicos, cabelos volumosos e neon.",
            options = listOf("Anos 70", "Anos 80", "Anos 90", "Anos 2000"),
            correctIndex = 1,
            genre = "Flashback"
        ),
        QuizQuestion(
            id = 4,
            question = "Qual música de Michel Teló viralizou no mundo e foi dançada por jogadores como Cristiano Ronaldo?",
            hint = "O refrão dizia 'Nossa, nossa, assim você me mata...'",
            options = listOf("Fugidinha", "Humilde Residência", "Ai Se Eu Te Pego", "Amiga da Minha Irmã"),
            correctIndex = 2,
            genre = "Pop / Sertanejo"
        ),
        QuizQuestion(
            id = 5,
            question = "Quem compôs a melodia atemporal de 'Garota de Ipanema' ao lado de Vinicius de Moraes?",
            hint = "O maestro soberano da Bossa Nova brasileira.",
            options = listOf("Caetano Veloso", "Tom Jobim", "Chico Buarque", "João Gilberto"),
            correctIndex = 1,
            genre = "MPB"
        ),
        QuizQuestion(
            id = 6,
            question = "Em qual álbum de 1982 Michael Jackson lançou 'Billie Jean' e 'Beat It'?",
            hint = "O álbum mais vendido da história da indústria fonográfica.",
            options = listOf("Bad", "Off the Wall", "Dangerous", "Thriller"),
            correctIndex = 3,
            genre = "Pop"
        )
    )

    val genres: List<String> = listOf("Todos", "Rock", "Pop", "Sertanejo", "MPB", "Flashback", "Eletrônica")
    val decades: List<String> = listOf("Todas", "Anos 70", "Anos 80", "Anos 90", "Anos 2000", "Anos 2010")
    val partyPlaylists: List<String> = listOf("Churrasco com os Amigos", "Festa na Garagem", "Nostalgia Retrô 80s", "Karaokê Clássicos", "Relax & Lounge")
}
