package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.VerifiedYellow
import com.example.ui.theme.VerifiedYellowBorder
import com.example.ui.theme.VerifiedYellowContainer
import com.example.ui.theme.VerifiedYellowGold

/**
 * Official NABACHETNA Yellow Verified Badge (হলুদ ভেরিফাইড ব্যাজ)
 * Represents an authentic citizen verified with 18+ Age, NID Card, Biometric Face Scan Match, Mobile OTP & Email.
 */
@Composable
fun YellowVerifiedBadge(
    modifier: Modifier = Modifier,
    size: Dp = 16.dp,
    showClickDialog: Boolean = true,
    customColor: Color = VerifiedYellow
) {
    var showDialog by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .then(
                if (showClickDialog) {
                    Modifier.clickable { showDialog = true }
                } else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Filled.CheckCircle,
            contentDescription = "হলুদ ভেরিফাইড ব্যাজ (১৮+ NID ও ফেস স্ক্যান যাচাইকৃত)",
            tint = customColor,
            modifier = Modifier.fillMaxSize()
        )
    }

    if (showDialog) {
        YellowBadgeInfoDialog(onDismiss = { showDialog = false })
    }
}

/**
 * Informational dialog explaining the Yellow Verified Badge criteria
 */
@Composable
fun YellowBadgeInfoDialog(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header badge icon with golden radial halo
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(
                                    VerifiedYellowGold.copy(alpha = 0.35f),
                                    VerifiedYellow.copy(alpha = 0.15f),
                                    Color.Transparent
                                )
                            )
                        )
                        .border(2.dp, VerifiedYellowBorder, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Verified,
                        contentDescription = null,
                        tint = VerifiedYellow,
                        modifier = Modifier.size(42.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "হলুদ ভেরিফাইড ব্যাজ 🌟",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "Official Verified Citizen of Nabachetna",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp, bottom = 16.dp)
                )

                // Criteria checklist
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = VerifiedYellowContainer.copy(alpha = 0.5f),
                    border = BorderStroke(1.dp, VerifiedYellowBorder.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "যাচাইকৃত আবশ্যকীয় তথ্যসমূহ:",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF7A5800),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        VerificationCheckItem(text = "বয়স ১৮ বছর বা তার বেশি নিশ্চিত (Adult 18+)")
                        VerificationCheckItem(text = "জাতীয় পরিচয়পত্র (NID) নম্বর ও দুই পাশের কপি")
                        VerificationCheckItem(text = "NID ছবির সাথে লাইভ ফেস স্ক্যান মিল (Biometric Match)")
                        VerificationCheckItem(text = "মোবাইল নম্বর ওটিপি (SMS OTP) যাচাইকৃত")
                        VerificationCheckItem(text = "ই-মেল ঠিকানা ভেরিফিকেশন সম্পন্ন")
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = VerifiedYellow),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "ঠিক আছে, বুঝতে পেরেছি",
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }
        }
    }
}

@Composable
private fun VerificationCheckItem(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.CheckCircle,
            contentDescription = null,
            tint = Color(0xFF2E7D32),
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
