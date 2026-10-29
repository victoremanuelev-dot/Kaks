# Media Center TV Box: Jukebox, Karaokê com IA, Vídeos e Jogos Retrô

Central Multimídia imersiva para TV Box e Smart TVs focada em controle remoto (D-Pad + OK), trazendo Jukebox com visualizador de áudio, Videokê com letras sincronizadas e batalha vocal com inteligência artificial, videoteca de shows/clipes e emulador de jogos arcade com quizzes musicais para festas.

### User Review & Critical Decisions

> [!IMPORTANT]
> **Decisões confirmadas com o usuário**:
> 1. **Estilo Visual**: Tema principal **Neon Cyber & Dark Lounge** (inspirado fielmente no mockup fornecido com tons ciano `#00E5FF` e magenta `#FF2A85`), acompanhado de seletor de temas (Neon Cyber, Retrô Clássico Ouro/Madeira e Modern Minimal TV) nas configurações.
> 2. **Duelo de Canto e Batalha Musical**: Pontuação em tempo real com gráfico de afinação (pitch detection) e IA avaliadora com comentários e notas de jurados.
> 3. **Jogos Integrados**: Coletânea híbrida contendo tanto jogos arcade retrô clássicos jogáveis no controle remoto/gamepad quanto Quiz Musical Interativo para festas.
> 4. **Navegação 10-foot**: Foco visual proeminente com bordas neon brilhantes, escala 1.05x e navegação D-pad estrita e linear.

---

### 1. Overview & Core Concept

- **O que o aplicativo faz**: Transforma a TV Box ou dispositivo Android em uma Central de Entretenimento de sala completa. Oferece reprodução contínua de músicas com visualizador dinâmico de vinil e ondas sonoras, karaokê profissional (Videokê) com letras sincronizadas no estilo bouncing-ball, fila de espera de cantores com apelidos, modo duelo vocal com pontuação de pitch em tempo real e análise por IA, player de clipes e visuais de fundo psicodélicos, e central de jogos retrô e quizzes para animar reuniões e festas.
- **Público-alvo / Persona**: Famílias e grupos de amigos reunidos na sala em frente à TV, operando o sistema confortavelmente através do controle remoto comum (cima, baixo, esquerda, direita, OK e voltar) ou gamepads bluetooth.
- **Proposta de Valor**: Uma experiência unificada de entretenimento que une a nostalgia das Jukeboxes de bar, a diversão competitiva do videokê profissional e a conveniência moderna de Smart TV com IA.

---

### 2. User Experience & Visual Design

```
+-----------------------------------------------------------------------------+
| [MEDIA CENTER]  (Home)  (Jukebox)  (Karaoke)  (Videos)  (Jogos)  (Config) 10:09 |
+-----------------------------------------------------------------------------+
|                                                                             |
|  [ DESTAQUE / CARROSSEL NEON ]                                              |
|  - Grandes sucessos da festa, duelo da noite ou playlist em alta            |
|                                                                             |
|  [ PLAYLISTS JUKEBOX ]          [ KARAOKÊ EM ALTA ]      [ DUELO COM IA ]   |
|  * Flashback 80s & 90s          * Evidências - Chitão    * Batalha Vocal    |
|  * Rock Internacional           * Shallow - Gaga         * Desafio Pitch    |
|  * Sertanejo & Churrasco        * Bohemian Rhapsody      * Ranking da Noite |
|                                                                             |
|  [ REPRODUZINDO AGORA ]                                  [ FILA DE CANTO ]  |
|  Disco Vinil + Onda Sonora + Controles TV                1. Lucas (Próximo) |
+-----------------------------------------------------------------------------+
```

#### Fluxos Principais
1. **Navegação por Foco (D-Pad)**: Cada elemento interativo possui estado visual ativo destacado por anel neon brilhante (ciano/magenta), efeito de escala e indicação clara de profundidade.
2. **Módulo Jukebox**:
   - Navegação por Gêneros (Rock, Pop, Sertanejo, MPB, Eletrônica, Flashback), Décadas (70s, 80s, 90s, 2000s) e Playlists Temáticas ("Churrasco", "Festa na Garagem").
   - Player Ativo com vinil giratório, espectrograma dinâmico em Canvas, fila de reprodução reordenável e atalhos rápidos de controle (Play/Pause, Pular, Shuffle, Repetir).
3. **Módulo Karaokê / Videokê**:
   - Letras sincronizadas na tela em tempo real com indicador de tempo por linha e sílaba.
   - Fila de Cantores: permite cadastrar apelidos dos convidados para organizar a ordem de quem canta.
   - Modo Batalha Vocal: Detecção de frequência/pitch do microfone em tempo real, comparando a voz do cantor com a melodia de referência e gerando pontuação instantânea e avaliação divertida por IA ("Jurado IA").
   - Ajustes de Áudio: Volume do vocal guia, balanço do microfone e simulação de eco/reverb.
4. **Módulo de Vídeos & Clipes**:
   - Clipes musicais oficiais, gravações de shows históricos e modo "Visuais de Fundo" (animações de ambiente e sintetizadores visuais para deixar rodando na TV durante confraternizações).
5. **Módulo de Jogos Retrô & Trivia Musical**:
   - Jogos Arcade em Canvas (ex: Retro Space Defender, Rhythm Beats e Snake Neon) com controle nativo pelo D-Pad do controle remoto ou gamepad sem fio.
   - Quiz Musical: Desafio de adivinhar a música antes que o tempo acabe, perfeito para disputas em grupo.
6. **Configurações e Customização de Temas**:
   - Seletor de Tema: **Neon Cyber** (padrão), **Retrô Ouro & Madeira**, e **Modern Minimal TV**.
   - Gerenciamento de Cache de mídia, teste de microfone e status de dispositivos pareados, e Controle Parental com PIN.

---

### 3. Key Product Decisions & Trade-Offs

- **Navegação D-Pad em Primeiro Lugar (10-foot UI)**:
  - *Decisão*: Construir toda a hierarquia de navegação com foco nativo do Jetpack Compose (`Modifier.focusable()`, `onFocusChanged`, `onKeyEvent`), garantindo que qualquer tecla direcional do controle remoto movimente o foco de forma previsível e sem travamentos.
  - *Por que*: Usuários de TV Box utilizam controle remoto simples; menus que exigem ponteiro de mouse ou toques na tela são frustrantes em Smart TVs.
- **Motor de Videokê & Pitch Detection**:
  - *Decisão*: Implementar analisador de pitch em tempo real acoplado ao microfone, gerando gráficos de notas (estilo Rock Band / SingStar) e motor de letras sincronizadas em tempo real.
  - *Por que*: Proporciona a sensação autêntica de videokê de alta qualidade sem depender de equipamentos externos caros.
- **Jogos Retrô Integrados em Canvas Nativo**:
  - *Decisão*: Criar jogos arcade retrô diretamente em Compose Canvas com baixa latência e resposta instantânea a botões direcionais, além de um motor de Quiz Musical interativo.
  - *Por que*: Não exige emuladores pesados ou arquivos ROM externos ilegais, roda perfeitamente em qualquer hardware de TV Box com 60 FPS garantidos.
- **Sugestões e Batalha Musical com IA**:
  - *Decisão*: Sistema de recomendações musicais inteligentes com curadoria de festas e persona de Jurado IA que analisa o desempenho vocal com frases divertidas e notas técnicas.

---

### 4. Technical Architecture & Data Strategy

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              MainActivity                                   │
│              - WindowInsets & Edge-to-Edge TV Layout                        │
│              - D-Pad Key Event Interception & Sound Feedback                │
└──────────────────────────────────────┬──────────────────────────────────────┘
                                       │
                ┌──────────────────────┴──────────────────────┐
                ▼                                             ▼
┌───────────────────────────────┐             ┌───────────────────────────────┐
│     MediaCenterViewModel      │             │       ThemeManager            │
│  - Active Tab & Focus State   │             │  - Neon Cyber (Default)       │
│  - Jukebox Queue & Playback   │             │  - Retro Classic Wood/Gold    │
│  - Karaoke Lyrics & Singer Q  │             │  - Modern Minimal TV          │
│  - Pitch Engine & AI Battles  │             └───────────────────────────────┘
│  - Retro Games & Trivia State │
└───────────────┬───────────────┘
                │
  ┌─────────────┼─────────────┬─────────────┬─────────────┐
  ▼             ▼             ▼             ▼             ▼
┌───────────┐ ┌───────────┐ ┌───────────┐ ┌───────────┐ ┌───────────┐
│ HomeScreen│ │JukeboxView│ │KaraokeView│ │VideosView │ │ GamesView │
│ Destaques │ │ Vinil 3D  │ │ Videokê   │ │ Clipes    │ │ Arcade 2D │
│ Carrossel │ │ Espectro  │ │ Pitch/IA  │ │ Shows     │ │ Quiz Fest │
│ Atalhos TV│ │ Fila Foco │ │ Fila Voz  │ │ Ambientes │ │ Gamepad   │
└───────────┘ └───────────┘ └───────────┘ └───────────┘ └───────────┘
```

#### Entidades de Dados
- `Track`: Id, Título, Artista, Álbum, Gênero, Década, Duração, Capa, Letra Sincronizada (LRC), Pitch Curve de referência.
- `SingerInQueue`: Id, Nome/Apelido, Música Escolhida, Status (Cantando, Na Fila, Concluído), Pontuação Obtida.
- `KaraokeScore`: PitchAccuracy, TimingScore, AIEvaluation, JuradoFeedback, Troféu.
- `QuizQuestion`: Pergunta, Trecho Musical/Dica, Opções de Resposta, Resposta Correta, Categoria.
- `RetroGame`: Título, Tipo (Space Defender, Rhythm Hero, Snake Neon), HighScore.

---

### Verificação do Plano
- Após aprovação, configuraremos o nome do app (`Media Center TV`) em `metadata.json` e `strings.xml`, criaremos os ícones adaptativos para TV/Android e implementaremos todos os módulos com código Kotlin limpo, reativo e totalmente navegável via controle remoto e toque.
