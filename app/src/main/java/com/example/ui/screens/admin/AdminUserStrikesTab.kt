package com.example.ui.screens.admin

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.data.model.User
import com.example.data.model.UserStrikeRecord
import com.example.ui.viewmodel.PageBookViewModel

@Composable
fun AdminUserStrikesTab(
    viewModel: PageBookViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val strikes by viewModel.userStrikes.collectAsStateWithLifecycle()
    val allUsers by viewModel.allUsers.collectAsStateWithLifecycle()

    var showIssueStrikeDialog by remember { mutableStateOf(false) }
    var selectedUserForStrike by remember { mutableStateOf<User?>(null) }
    var violationReason by remember { mutableStateOf("অননুমোদিত স্প্যামিং ও ফেক তথ্য প্রচার") }
    var userToClearStrikes by remember { mutableStateOf<UserStrikeRecord?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(vertical = 14.dp)
    ) {
        // Banner Header
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFB71C1C)),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "🚨 স্ট্রাইক ও শ্যাডোব্যান সিস্টেম",
                                color = Color.White,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "নীতিমালা লঙ্ঘনকারী ব্যবহারকারীদের নিয়ন্ত্রণ ও শৃঙ্খলা প্রয়োগ",
                                color = Color(0xFFFFCDD2),
                                fontSize = 12.sp
                            )
                        }
                        Button(
                            onClick = { showIssueStrikeDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = Color(0xFFB71C1C), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "স্ট্রাইক দিন", color = Color(0xFFB71C1C), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Strike 3-Tier Policy Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    Text(
                        text = "⚖️ ৩-স্তরের স্বয়ংক্রিয় শাস্তিমূলক ব্যবস্থা",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFFFF3E0),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFB74D))
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text("১ম স্ট্রাইক", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color(0xFFE65100))
                                Text("২৪ ঘণ্টার পোস্ট ও কমেন্ট মিউট", fontSize = 10.sp, color = Color(0xFF5D4037))
                            }
                        }

                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFFBE9E7),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF8A65))
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text("২য় স্ট্রাইক", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color(0xFFD84315))
                                Text("৭ দিনের শ্যাডোব্যান (ফিড সীমিত)", fontSize = 10.sp, color = Color(0xFF5D4037))
                            }
                        }

                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFFFEBEE),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF9A9A))
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text("৩য় স্ট্রাইক", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color(0xFFC62828))
                                Text("স্থায়ী অ্যাকাউন্ট সাসপেনশন", fontSize = 10.sp, color = Color(0xFF5D4037))
                            }
                        }
                    }
                }
            }
        }

        // Active Penalized Users List
        item {
            Text(
                text = "শাস্তিপ্রাপ্ত বা নজরদারিতে থাকা অ্যাকাউন্টসমূহ (${strikes.size})",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        if (strikes.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(imageVector = Icons.Default.VerifiedUser, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(40.dp))
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = "কোনো সক্রিয় স্ট্রাইক নেই", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(text = "সকল অ্যাকাউন্ট বর্তমানে নিরাপদ ও কমপ্লায়েন্ট অবস্থায় রয়েছে।", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        } else {
            items(strikes, key = { it.userId }) { record ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                                AsyncImage(
                                    model = record.userAvatar,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = record.userName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "স্ট্রাইকের তারিখ: ${record.strikeDate}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = when (record.strikeCount) {
                                    1 -> Color(0xFFFFA000)
                                    2 -> Color(0xFFE65100)
                                    else -> Color(0xFFD32F2F)
                                }
                            ) {
                                Text(
                                    text = "স্ট্রাইক: ${record.strikeCount}/৩",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFFFEBEE),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "লঙ্ঘনের কারণ: ${record.lastViolationReason}",
                                fontSize = 11.sp,
                                color = Color(0xFFC62828),
                                modifier = Modifier.padding(8.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Controls: Shadowban & Clear Strikes
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "শ্যাডোব্যান:", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                Spacer(modifier = Modifier.width(6.dp))
                                Switch(
                                    checked = record.isShadowBanned,
                                    onCheckedChange = { isShadow ->
                                        viewModel.toggleUserShadowban(record.userId, isShadow)
                                    }
                                )
                            }

                            TextButton(
                                onClick = { userToClearStrikes = record },
                                colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF2E7D32))
                            ) {
                                Icon(imageVector = Icons.Default.LockReset, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("স্ট্রাইক মুক্ত করুন", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }

    // Issue Strike Dialog
    if (showIssueStrikeDialog) {
        var selectedUserId by remember { mutableStateOf(allUsers.firstOrNull()?.id ?: 1) }
        val reasonOptions = listOf(
            "অননুমোদিত বাণিজ্যিক স্প্যামিং ও ফেক অফার",
            "কপিরাইট লঙ্ঘন ও অনুমতিহীন কন্টেন্ট প্রচার",
            "সাইবার হ্যারাসমেন্ট বা আক্রমণাত্মক ভাষা প্রয়োগ",
            "মিথ্যা বা বিভ্রান্তিকর তথ্য ছড়ানো",
            "বট ভিউ বা ফেক ফলোয়ার জেনারেশন প্রচেষ্টা"
        )

        AlertDialog(
            onDismissRequest = { showIssueStrikeDialog = false },
            title = { Text("ব্যবহারকারীকে স্ট্রাইক প্রদান", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(text = "ব্যবহারকারী নির্বাচন করুন:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    allUsers.take(4).forEach { u ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedUserId = u.id }
                                .padding(vertical = 2.dp)
                        ) {
                            RadioButton(selected = selectedUserId == u.id, onClick = { selectedUserId = u.id })
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "${u.name} (ID: #${u.id})", fontSize = 12.sp)
                        }
                    }

                    Text(text = "লঙ্ঘনের কারণ নির্বাচন করুন:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    reasonOptions.forEach { r ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { violationReason = r }
                                .padding(vertical = 2.dp)
                        ) {
                            RadioButton(selected = violationReason == r, onClick = { violationReason = r })
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = r, fontSize = 11.sp)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val targetUser = allUsers.find { it.id == selectedUserId } ?: allUsers.firstOrNull()
                        targetUser?.let {
                            viewModel.issueUserStrike(it.id, it.name, violationReason)
                            showIssueStrikeDialog = false
                            Toast.makeText(context, "${it.name}-কে স্ট্রাইক দেওয়া হয়েছে!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                ) {
                    Text("স্ট্রাইক জারি করুন")
                }
            },
            dismissButton = {
                TextButton(onClick = { showIssueStrikeDialog = false }) {
                    Text("বাতিল")
                }
            }
        )
    }

    // Clear Strikes Dialog
    userToClearStrikes?.let { rec ->
        AlertDialog(
            onDismissRequest = { userToClearStrikes = null },
            title = { Text("স্ট্রাইক প্রত্যাহার?", fontWeight = FontWeight.Bold) },
            text = { Text("${rec.userName}-এর সকল স্ট্রাইক ও নিষেধাজ্ঞা প্রত্যাহার করতে চাচ্ছেন?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearUserStrikes(rec.userId)
                        userToClearStrikes = null
                        Toast.makeText(context, "স্ট্রাইক প্রত্যাহার সম্পন্ন!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                ) {
                    Text("হ্যাঁ, প্রত্যাহার করুন")
                }
            },
            dismissButton = {
                TextButton(onClick = { userToClearStrikes = null }) {
                    Text("না")
                }
            }
        )
    }
}
