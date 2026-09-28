package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.ui.theme.TeaLeafGreen
import com.example.ui.viewmodel.LeaderboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardScreen(
    initialLanguageId: Int = 1,
    onBack: () -> Unit = {},
    viewModel: LeaderboardViewModel = viewModel(factory = LeaderboardViewModel.provideFactory(LocalContext.current))
) {
    val languages by viewModel.languages.collectAsStateWithLifecycle()
    val selectedLanguageId by viewModel.selectedLanguageId.collectAsStateWithLifecycle()
    val leaderboard by viewModel.leaderboard.collectAsStateWithLifecycle()

    LaunchedEffect(initialLanguageId) {
        if (initialLanguageId > 0) {
            viewModel.selectLanguage(initialLanguageId)
        }
    }

    val currentLang = languages.find { it.id == selectedLanguageId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "🏆 ${currentLang?.name ?: "ভাষা শিক্ষা"} লিডারবোর্ড",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "সেরা শিক্ষার্থীদের র‍্যাঙ্কিং ও পয়েন্ট তালিকা",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f))
        ) {
            // 10 Language Tabs
            if (languages.isNotEmpty()) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(languages) { lang ->
                        val isSelected = lang.id == selectedLanguageId
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.selectLanguage(lang.id) },
                            label = {
                                Text(
                                    text = "${lang.flagEmoji} ${lang.name.split(" ").first()}",
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = TeaLeafGreen,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            // Top 3 Podium Cards
            if (leaderboard.isNotEmpty()) {
                val top3 = leaderboard.take(3)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "👑 শীর্ষস্থান অধিকারী (Top Champions)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TeaLeafGreen
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            // 2nd Place
                            if (top3.size > 1) {
                                PodiumItem(rank = 2, score = top3[1], badgeColor = Color(0xFFC0C0C0))
                            }
                            // 1st Place
                            if (top3.isNotEmpty()) {
                                PodiumItem(rank = 1, score = top3[0], badgeColor = Color(0xFFFFD700), isFirst = true)
                            }
                            // 3rd Place
                            if (top3.size > 2) {
                                PodiumItem(rank = 3, score = top3[2], badgeColor = Color(0xFFCD7F32))
                            }
                        }
                    }
                }
            }

            // Full Leaderboard List
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("leaderboard_list"),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Text(
                        text = "সকল অংশগ্রহণকারী (${leaderboard.size} জন)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
                    )
                }

                if (leaderboard.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.EmojiEvents,
                                    contentDescription = null,
                                    tint = TeaLeafGreen,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "এই ভাষায় আপনি প্রথম শিক্ষার্থী হতে পারেন!",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "প্র্যাকটিস শুরু করুন এবং পয়েন্ট সংগ্রহ করে লিডারবোর্ডে এগিয়ে থাকুন।",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                itemsIndexed(leaderboard) { index, score ->
                    val rank = index + 1
                    val rankColor = when (rank) {
                        1 -> Color(0xFFFFD700)
                        2 -> Color(0xFFC0C0C0)
                        3 -> Color(0xFFCD7F32)
                        else -> TeaLeafGreen.copy(alpha = 0.7f)
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("leaderboard_item"),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Rank Badge
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(rankColor.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "#$rank",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (rank <= 3) rankColor else MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            // Avatar
                            AsyncImage(
                                model = score.avatarUrl.ifBlank { "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=100" },
                                contentDescription = score.userName,
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            // Name & Badge
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = score.userName,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${score.rankBadge} • ${score.levelsCompleted} লেভেল সম্পন্ন",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            // Points
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "${score.totalPoints}",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Black,
                                    color = TeaLeafGreen
                                )
                                Text(
                                    text = stringResource(id = com.example.R.string.leaderboard_points),
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PodiumItem(
    rank: Int,
    score: com.example.data.model.UserScore,
    badgeColor: Color,
    isFirst: Boolean = false
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(90.dp)
    ) {
        Box(contentAlignment = Alignment.BottomEnd) {
            AsyncImage(
                model = score.avatarUrl.ifBlank { "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=100" },
                contentDescription = score.userName,
                modifier = Modifier
                    .size(if (isFirst) 56.dp else 46.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(badgeColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$rank",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = score.userName.split(" ").first(),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )
        Text(
            text = "${score.totalPoints} পয়েন্ট",
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = TeaLeafGreen
        )
    }
}
