package com.example.ui.screens.admin

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.data.model.NotificationCampaign
import com.example.ui.viewmodel.PageBookViewModel

@Composable
fun AdminPushCampaignsTab(
    viewModel: PageBookViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val campaigns by viewModel.notificationCampaigns.collectAsStateWithLifecycle()

    var campaignTitle by remember { mutableStateOf("") }
    var campaignMsg by remember { mutableStateOf("") }
    var selectedAudience by remember { mutableStateOf("সকল ইউজার") }

    val audienceOptions = listOf(
        "সকল ইউজার" to "সব ব্যবহারকারী (১৪,৮২০+ অ্যাক্টিভ)",
        "শুধুমাত্র কনটেন্ট ক্রিয়েটর" to "পেজ মালিক ও ক্রিয়েটর (৩,৪৫০+)",
        "ভেরিফাইড পেজ ও ইউজার" to "ব্লু টিক ভেরিফাইড প্রোফাইল",
        "নিষ্ক্রিয় ব্যবহারকারী" to "গত ৭ দিন ধরে নিষ্ক্রিয়"
    )

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
                colors = CardDefaults.cardColors(containerColor = Color(0xFF4A148C)),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "📢 টার্গেটেড পুশ নোটিফিকেশন ক্যাম্পেইন",
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "ব্যবহারকারীদের নির্দিষ্ট গ্রুপে তাৎক্ষণিক পুশ এলার্ট ও এনগেজমেন্ট ক্যাম্পেইন পাঠান",
                        color = Color(0xFFE1BEE7),
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Campaign Builder Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(text = "✍️ নতুন পুশ নোটিফিকেশন তৈরি করুন", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = campaignTitle,
                        onValueChange = { campaignTitle = it },
                        label = { Text("ক্যাম্পেইন শিরোনাম (Title)") },
                        placeholder = { Text("যেমন: 🎉 নতুন আর্নিং বোনাস শুরু হয়েছে!") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = campaignMsg,
                        onValueChange = { campaignMsg = it },
                        label = { Text("বার্তা / বিস্তারিত নোটিফিকেশন (Body)") },
                        placeholder = { Text("ইউজারদের জন্য আকর্ষণীয় বার্তা লিখুন...") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(text = "টার্গেট অডিয়েন্স (Target Audience):", fontWeight = FontWeight.Medium, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(6.dp))

                    audienceOptions.forEach { (audKey, audDesc) ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedAudience = audKey }
                                .padding(vertical = 3.dp)
                        ) {
                            RadioButton(selected = selectedAudience == audKey, onClick = { selectedAudience = audKey })
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(text = audKey, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text(text = audDesc, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            if (campaignTitle.isNotBlank() && campaignMsg.isNotBlank()) {
                                viewModel.createNotificationCampaign(campaignTitle.trim(), campaignMsg.trim(), selectedAudience)
                                campaignTitle = ""
                                campaignMsg = ""
                                Toast.makeText(context, "পুশ ক্যাম্পেইন সফলভাবে সম্প্রচার করা হয়েছে!", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "দয়া করে শিরোনাম ও বার্তা পূরণ করুন", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A148C)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("তাৎক্ষণিক ব্রডকাস্ট করুন (Broadcast Now)", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Campaign History Log
        item {
            Text(
                text = "ইতিপূর্বে প্রেরিত ক্যাম্পেইন হিস্ট্রি (${campaigns.size})",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        items(campaigns, key = { it.id }) { camp ->
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
                        Text(
                            text = camp.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFE1BEE7)
                        ) {
                            Text(
                                text = "✓ পাঠানো হয়েছে",
                                color = Color(0xFF4A148C),
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = camp.message,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = "টার্গেট: ${camp.targetAudience}",
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        Text(
                            text = "ডেলিভারি: ${camp.sentCount} জন • ${camp.scheduledTime}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
