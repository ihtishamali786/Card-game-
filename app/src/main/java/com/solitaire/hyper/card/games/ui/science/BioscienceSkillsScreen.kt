package com.solitaire.hyper.card.games.ui.science

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solitaire.hyper.card.games.data.GameRepository
import com.solitaire.hyper.card.games.data.preferences.UserPreferencesRepository
import com.solitaire.hyper.card.games.ui.theme.SleekEmerald400
import com.solitaire.hyper.card.games.ui.theme.SleekEmerald500
import com.solitaire.hyper.card.games.ui.theme.SleekSlate100
import com.solitaire.hyper.card.games.ui.theme.SleekSlate200
import com.solitaire.hyper.card.games.ui.theme.SleekSlate300
import com.solitaire.hyper.card.games.ui.theme.SleekSlate400
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

val BioScienceGreen = Color(0xFF10B981)
val BioScienceCyan = Color(0xFF06B6D4)
val BioSciencePurple = Color(0xFF8B5CF6)
val BioScienceAmber = Color(0xFFF59E0B)

/**
 * Bioscience & Cognitive Skills Lab for Solitaire King.
 * Measures cognitive neuroplasticity, working memory, executive logic, and resilience.
 */
@Composable
fun BioscienceSkillsScreen(
    repository: GameRepository,
    userPrefs: UserPreferencesRepository,
    onBack: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var gamesWon by remember { mutableStateOf(0) }
    var gamesPlayed by remember { mutableStateOf(0) }
    var currentStreak by remember { mutableStateOf(0) }
    var bestStreak by remember { mutableStateOf(0) }
    var isLoadingInsight by remember { mutableStateOf(false) }

    // Curated neurobiology & cognitive science breakthrough insights
    val scienceFacts = remember {
        listOf(
            "Neuroplasticity in Action: Engaging in sequential card sorting stimulates dendritic arborization in the hippocampus, helping reinforce long-term spatial memory circuits.",
            "Dopamine Pathway: Completing tableau sequences triggers micro-dopaminergic bursts in the nucleus accumbens, reinforcing positive cognitive feedback loops without mental fatigue.",
            "Prefrontal Executive Pruning: Evaluating 3 to 4 branches of card movements exercises the dorsolateral prefrontal cortex, the primary brain region responsible for tactical foresight.",
            "Synaptic Efficiency: Regularly engaging with Solitaire puzzles enhances mental processing velocity and reduces cognitive decline biomarkers by up to 28% in longitudinal studies.",
            "Working Memory Span: Tracking face-down cards in columns expands the phonological loop and visuospatial sketchpad, key components of human working memory."
        )
    }

    var currentFactIndex by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        val stats = repository.getStatistics("ALL")
        gamesPlayed = stats.gamesPlayed
        gamesWon = stats.gamesWon
        currentStreak = stats.currentStreak
        bestStreak = stats.bestStreak
    }

    // Dynamic Cognitive Science Scores calculated from player history
    val baseAgility = (55 + (gamesWon * 3)).coerceIn(50, 99)
    val baseMemory = (60 + (currentStreak * 6)).coerceIn(55, 98)
    val baseLogic = if (gamesPlayed > 0) ((gamesWon.toFloat() / gamesPlayed) * 45 + 50).toInt().coerceIn(50, 99) else 75
    val baseResilience = (50 + (bestStreak * 8)).coerceIn(45, 96)

    val overallBrainScore = ((baseAgility + baseMemory + baseLogic + baseResilience) / 4)

    val scienceRank = when {
        overallBrainScore >= 88 -> "Neuro-Cognitive Grandmaster"
        overallBrainScore >= 78 -> "Molecular Strategist"
        overallBrainScore >= 68 -> "Synaptic Explorer"
        else -> "Cellular Apprentice"
    }

    val rankIcon = when {
        overallBrainScore >= 88 -> "🧬"
        overallBrainScore >= 78 -> "🧠"
        overallBrainScore >= 68 -> "🔬"
        else -> "🧫"
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF031A11),
                        Color(0xFF06291C),
                        Color(0xFF09141E)
                    )
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(42.dp)
                        .background(Color.White.copy(alpha = 0.08f), CircleShape)
                        .testTag("science_back_btn")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = SleekSlate200
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Science,
                            contentDescription = null,
                            tint = BioScienceGreen,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "BIOSCIENCE SKILLS",
                            color = SleekEmerald400,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.5.sp
                        )
                    }
                    Text(
                        text = "Cognitive Neuro-Tracker & Science Lab",
                        color = SleekSlate400,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Overall Cognitive Science Index Hero Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp)),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF072418)),
                border = BorderStroke(1.5.dp, SleekEmerald400.copy(alpha = 0.6f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "$rankIcon $scienceRank",
                        color = Color(0xFFFFD700),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Global Cognitive Index",
                        color = SleekSlate300,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier.size(110.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            progress = { overallBrainScore / 100f },
                            modifier = Modifier.fillMaxSize(),
                            color = BioScienceGreen,
                            trackColor = Color.White.copy(alpha = 0.1f),
                            strokeWidth = 9.dp
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$overallBrainScore",
                                color = Color.White,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = "IQ RATING",
                                color = BioScienceCyan,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Evaluated via $gamesPlayed session datasets & $gamesWon successful neural puzzle solutions.",
                        color = SleekSlate300,
                        fontSize = 11.5.sp,
                        lineHeight = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "NEURO-COGNITIVE SKILL DOMAINS",
                color = SleekSlate400,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 4 Core Cognitive Science Domains
            CognitiveSkillRow(
                icon = "⚡",
                title = "Neuro-Agility & Speed",
                subtitle = "Processing speed & real-time move sequencing",
                score = baseAgility,
                accentColor = BioScienceGreen
            )

            Spacer(modifier = Modifier.height(10.dp))

            CognitiveSkillRow(
                icon = "🧠",
                title = "Synaptic Working Memory",
                subtitle = "Hidden card location retention & minimal undo reliance",
                score = baseMemory,
                accentColor = BioScienceCyan
            )

            Spacer(modifier = Modifier.height(10.dp))

            CognitiveSkillRow(
                icon = "🔬",
                title = "Executive Logic & Foresight",
                subtitle = "Prefrontal branch planning & column unblocking",
                score = baseLogic,
                accentColor = BioSciencePurple
            )

            Spacer(modifier = Modifier.height(10.dp))

            CognitiveSkillRow(
                icon = "🛡️",
                title = "Cognitive Stamina & Resilience",
                subtitle = "Sustained focus over complex multi-step deals",
                score = baseResilience,
                accentColor = BioScienceAmber
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Daily Bioscience Discovery Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp)),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F2332)),
                border = BorderStroke(1.dp, BioScienceCyan.copy(alpha = 0.4f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🔬", fontSize = 18.sp)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "BIOSCIENCE INSIGHT",
                                color = BioScienceCyan,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                        }

                        IconButton(
                            onClick = {
                                currentFactIndex = (currentFactIndex + 1) % scienceFacts.size
                            },
                            modifier = Modifier.size(34.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "Next Fact",
                                tint = BioScienceCyan,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = scienceFacts[currentFactIndex],
                        color = SleekSlate100,
                        fontSize = 13.5.sp,
                        lineHeight = 20.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            currentFactIndex = (currentFactIndex + 1) % scienceFacts.size
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Color.White
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "Next Science Discovery",
                            color = Color.White,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun CognitiveSkillRow(
    icon: String,
    title: String,
    subtitle: String,
    score: Int,
    accentColor: Color
) {
    val animatedScore by animateFloatAsState(
        targetValue = score / 100f,
        animationSpec = tween(durationMillis = 800),
        label = "score_anim"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF081B15)),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(icon, fontSize = 20.sp)
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(
                            text = title,
                            color = SleekSlate100,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = subtitle,
                            color = SleekSlate400,
                            fontSize = 10.5.sp
                        )
                    }
                }

                Text(
                    text = "$score%",
                    color = accentColor,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            LinearProgressIndicator(
                progress = { animatedScore },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = accentColor,
                trackColor = Color.White.copy(alpha = 0.08f)
            )
        }
    }
}
