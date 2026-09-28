package com.example.ui.screens.admin

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.service.FeatureLockManager
import com.example.ui.theme.TeaLeafGreen

data class AppFeatureItem(
    val key: String,
    val title: String,
    val description: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminFeatureLockTab() {
    val context = LocalContext.current
    val locks by FeatureLockManager.featureLocks.collectAsStateWithLifecycle()

    val features = listOf(
        AppFeatureItem("marketplace", "মার্কেটপ্লেস ও শপ (Marketplace)", "কেনাবেচা ও ইভেন্ট এস্ক্রো সার্ভিস"),
        AppFeatureItem("pages", "পেজ সিস্টেম (Pages)", "ব্যবসা ও কমিউনিটি পেজ"),
        AppFeatureItem("groups", "গ্রুপ চ্যাট (Groups)", "কমিউনিটি ডিসকাশন গ্রুপ"),
        AppFeatureItem("voice_room", "ভয়েস রুম (Voice Room)", "লাইভ অডিও ডিসকাশন রুম"),
        AppFeatureItem("video_meet", "ভিডিও কল ও মিট (Video Meet)", "ওয়ান-টু-ওয়ান এবং কনফারেন্স ভিডিও কল"),
        AppFeatureItem("stories", "স্টোরিজ (Stories)", "২৪ ঘণ্টার লাইভ স্টোরি শেয়ারিং")
    )

    var editingFeature by remember { mutableStateOf<AppFeatureItem?>(null) }
    var noticeInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column {
            Text("সার্ভার লোড ও ফিচার লক কন্ট্রোল 🔒", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TeaLeafGreen)
            Text("সার্ভার ওভারলোড হলে যেকোনো ফিচার তাৎক্ষণিকভাবে লক করুন ও কারণসহ নোটিশ সেট করুন", fontSize = 11.sp, color = Color.Gray)
        }

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(features) { feature ->
                val lockState = locks[feature.key]
                val isLocked = lockState?.isLocked == true

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = if (isLocked) Color(0xFFFFEBEE) else Color.White),
                    border = BorderStroke(1.dp, if (isLocked) Color.Red.copy(alpha = 0.5f) else TeaLeafGreen.copy(alpha = 0.3f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(feature.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(feature.description, fontSize = 11.sp, color = Color.Gray)
                            }
                            Switch(
                                checked = isLocked,
                                onCheckedChange = { locked ->
                                    if (locked) {
                                        editingFeature = feature
                                        noticeInput = "সার্ভার লোড নিয়ন্ত্রণের স্বার্থে ${feature.title} সাময়িকভাবে বন্ধ রাখা হয়েছে। সাময়িক অসুবিধার জন্য আন্তরিকভাবে দুঃখিত।"
                                    } else {
                                        FeatureLockManager.setFeatureLock(feature.key, false, "")
                                        Toast.makeText(context, "${feature.title} আনলক করা হয়েছে!", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color.Red, checkedTrackColor = Color(0xFFFFCDD2))
                            )
                        }

                        if (isLocked) {
                            Surface(
                                color = Color.Red.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = Color.Red, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "নোটিশ: ${lockState?.noticeMessage ?: ""}",
                                        fontSize = 11.sp,
                                        color = Color.Red,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (editingFeature != null) {
        val feat = editingFeature!!
        AlertDialog(
            onDismissRequest = { editingFeature = null },
            title = { Text("ফিচার লক ও নোটিশ বার্তা সেট করুন", fontWeight = FontWeight.Bold, fontSize = 15.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("${feat.title} বন্ধ করার কারণ বা নোটিশ লিখুন:", fontSize = 12.sp)
                    OutlinedTextField(
                        value = noticeInput,
                        onValueChange = { noticeInput = it },
                        label = { Text("ইউজারদের জন্য নোটিশ বার্তা", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        FeatureLockManager.setFeatureLock(feat.key, true, noticeInput)
                        editingFeature = null
                        Toast.makeText(context, "${feat.title} লক করা হয়েছে এবং নোটিশ সেট হয়েছে!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("লক করুন ও নোটিশ প্রকাশ করুন", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { editingFeature = null }) {
                    Text("বাতিল", color = Color.Gray)
                }
            }
        )
    }
}
