package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.service.DigitalWellbeingManager
import com.example.data.service.WellbeingState

@Composable
fun DigitalWellbeingOverlay(
    wellbeingState: WellbeingState,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        // 1. 5-Minute Advance Warning Top Banner (Appears at 3 hours 55 minutes)
        AnimatedVisibility(
            visible = wellbeingState.isWarningActive && !wellbeingState.isLockedActive,
            enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(12.dp)
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE65100)),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "⚠️ স্বাস্থ্য সতর্কবার্তা (৫ মিনিট বাকি)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "আপনি টানা ৩ ঘণ্টা ৫৫ মিনিট স্ক্রিন ব্যবহার করছেন। আগামী ৫ মিনিট পর ১৫ মিনিটের বাধ্যতামূলক স্বাস্থ্য বিরতি শুরু হবে। আপনার কাজ গুছিয়ে নিন।",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.95f),
                            lineHeight = 15.sp
                        )
                    }

                    IconButton(onClick = { DigitalWellbeingManager.dismissWarningBanner() }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Dismiss", tint = Color.White)
                    }
                }
            }
        }

        // 2. 15-Minute Mandatory Health Break Full Screen Lock Overlay (Appears at 4 hours)
        if (wellbeingState.isLockedActive) {
            val minutes = wellbeingState.lockRemainingSeconds / 60
            val seconds = wellbeingState.lockRemainingSeconds % 60
            val timeFormatted = String.format("%02d:%02d", minutes, seconds)

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF0F2027),
                                Color(0xFF203A43),
                                Color(0xFF2C5364)
                            )
                        )
                    )
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF102A20).copy(alpha = 0.95f)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF81C784).copy(alpha = 0.5f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF2E7D32).copy(alpha = 0.3f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.HealthAndSafety,
                                contentDescription = null,
                                tint = Color(0xFF81C784),
                                modifier = Modifier.size(48.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        Text(
                            text = "বাধ্যতামূলক স্বাস্থ্য বিরতি",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF1B4D3E),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF81C784).copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "আপনি টানা অনেক সময় ধরে স্ক্রিনের দিকে তাকিয়ে থাকায় আপনার স্বাস্থ্যের ক্ষতি হতে পারে। তাই ১৫ মিনিটের জন্য একটি বাধ্যতামূলক বিরতি দেওয়া হলো। ১৫ মিনিট পর আবার ফিরে আসুন। ধন্যবাদ।",
                                fontSize = 13.sp,
                                color = Color.White.copy(alpha = 0.9f),
                                textAlign = TextAlign.Center,
                                lineHeight = 19.sp,
                                modifier = Modifier.padding(16.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Countdown Timer Circle
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = Color(0xFF000000).copy(alpha = 0.4f),
                            border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF81C784))
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 28.dp, vertical = 16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "অবশিষ্ট বিরতি সময়",
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                                Text(
                                    text = timeFormatted,
                                    fontSize = 36.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF81C784)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "💡 স্বাস্থ্য টিপস: চোখ ম্যাসাজ করুন, এক গ্লাস পানি পান করুন এবং একটু হাঁটাচলা করুন।",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}
