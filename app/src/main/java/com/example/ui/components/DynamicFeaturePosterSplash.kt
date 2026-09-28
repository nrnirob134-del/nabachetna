package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.data.model.FeatureSplashPoster
import com.example.ui.theme.TeaLeafGreen
import kotlinx.coroutines.delay

@Composable
fun DynamicFeaturePosterSplash(
    poster: FeatureSplashPoster,
    onFinished: () -> Unit
) {
    val totalSeconds = poster.durationSeconds.coerceIn(1, 5)
    var secondsLeft by remember { mutableIntStateOf(totalSeconds) }
    val progressAnimation = remember { Animatable(0f) }

    LaunchedEffect(poster.id) {
        progressAnimation.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = totalSeconds * 1000,
                easing = LinearEasing
            )
        )
    }

    LaunchedEffect(poster.id) {
        while (secondsLeft > 0) {
            delay(1000L)
            secondsLeft--
        }
        onFinished()
    }

    Dialog(
        onDismissRequest = {
            // Unskippable and unremovable: pure system loading & visual poster time
        },
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            // Pure Full-Screen / Gaming Style Poster Card
            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Black),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // 100% Crisp Visual Poster Image (No text overlay, no descriptions)
                    AsyncImage(
                        model = poster.imageUrl,
                        contentDescription = poster.featureName,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(20.dp)),
                        contentScale = ContentScale.Crop
                    )

                    // Minimalist Top-Right Feature Badge & Countdown (Gaming style indicator)
                    Row(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = Color.Black.copy(alpha = 0.75f),
                            shape = RoundedCornerShape(20.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.3f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF00E676))
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "${poster.iconEmoji} ${secondsLeft.coerceAtLeast(1)}s",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Sleek Slim Neon Progress Bar at the very bottom
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .padding(bottom = 12.dp, start = 16.dp, end = 16.dp)
                    ) {
                        LinearProgressIndicator(
                            progress = { progressAnimation.value },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp)),
                            color = Color(0xFF00E676),
                            trackColor = Color.White.copy(alpha = 0.2f)
                        )
                    }
                }
            }
        }
    }
}
