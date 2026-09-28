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
import com.example.ui.viewmodel.PageBookViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AdminAiModerationTab(
    viewModel: PageBookViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val autoModRule by viewModel.autoModerationRule.collectAsStateWithLifecycle()

    var newKeywordInput by remember { mutableStateOf("") }
    var testSentenceInput by remember { mutableStateOf("") }
    var testResult by remember { mutableStateOf<Pair<Boolean, String>?>(null) }

    var aiScanEnabled by remember(autoModRule) { mutableStateOf(autoModRule.aiScanEnabled) }
    var sensitivityLevel by remember(autoModRule) { mutableStateOf(autoModRule.sensitivityLevel) }
    var autoDeleteToxic by remember(autoModRule) { mutableStateOf(autoModRule.autoDeleteToxic) }
    var flagPhishingLinks by remember(autoModRule) { mutableStateOf(autoModRule.flagPhishingLinks) }
    var maxDuplicatePostBlock by remember(autoModRule) { mutableStateOf(autoModRule.maxDuplicatePostBlock) }

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
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A237E)),
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
                                text = "🛡️ এআই অটো-মডারেশন ও ফিল্টার",
                                color = Color.White,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "স্প্যাম, ব্যাড-ওয়ার্ড ও ক্ষতিকর কন্টেন্ট অটো-ব্লকিং ইঞ্জিন",
                                color = Color(0xFF9FA8DA),
                                fontSize = 12.sp
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (aiScanEnabled) Color(0xFF2E7D32) else Color(0xFFD32F2F)
                        ) {
                            Text(
                                text = if (aiScanEnabled) "এআই সক্রিয়" else "নিষ্ক্রিয়",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        // Banned Keywords Management
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🚫 নিষিদ্ধ শব্দের তালিকা (Banned Keywords)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${autoModRule.bannedKeywords.size} টি শব্দ",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "এই শব্দগুলো কোনো পোস্ট বা কমেন্টে থাকলে তা স্বয়ংক্রিয়ভাবে ব্লক বা ফ্ল্যাগ করা হবে।",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Keyword Flow Chips
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        autoModRule.bannedKeywords.forEach { word ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFFFEBEE),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF9A9A))
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = word,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFFC62828)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Remove",
                                        tint = Color(0xFFC62828),
                                        modifier = Modifier
                                            .size(14.dp)
                                            .clickable {
                                                viewModel.removeBannedKeyword(word)
                                                Toast.makeText(context, "'$word' নিষিদ্ধ তালিকা থেকে সরানো হয়েছে", Toast.LENGTH_SHORT).show()
                                            }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Add Keyword Input Box
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = newKeywordInput,
                            onValueChange = { newKeywordInput = it },
                            placeholder = { Text("নতুন নিষিদ্ধ শব্দ লিখুন...", fontSize = 12.sp) },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp)
                        )
                        Button(
                            onClick = {
                                if (newKeywordInput.isNotBlank()) {
                                    viewModel.addBannedKeyword(newKeywordInput.trim())
                                    newKeywordInput = ""
                                    Toast.makeText(context, "নিষিদ্ধ শব্দ যুক্ত করা হয়েছে!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828))
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("যুক্ত করুন", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // AI Engine Rules & Sensitivity Config
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
                    Text(
                        text = "⚙️ এআই স্ক্যানিং ও ফিল্টার কনফিগারেশন",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // AI Scan Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "রিয়েল-টাইম এআই কনটেন্ট স্ক্যানার", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(text = "প্রতিটি পোস্ট ও কমেন্টের টেক্সট স্বয়ংক্রিয়ভাবে অডিট হবে", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = aiScanEnabled,
                            onCheckedChange = {
                                aiScanEnabled = it
                                viewModel.updateAutoModSettings(it, sensitivityLevel, autoDeleteToxic, flagPhishingLinks, maxDuplicatePostBlock)
                            }
                        )
                    }

                    Divider(modifier = Modifier.padding(vertical = 10.dp))

                    // Sensitivity Level Selector
                    Text(text = "স্প্যাম ও বট সেনসিটিভিটি লেভেল:", fontWeight = FontWeight.Medium, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("LOW" to "মৃদু (Low)", "MEDIUM" to "স্ট্যান্ডার্ড (Med)", "HIGH" to "কঠোর (High)").forEach { (level, title) ->
                            val isSel = sensitivityLevel == level
                            OutlinedButton(
                                onClick = {
                                    sensitivityLevel = level
                                    viewModel.updateAutoModSettings(aiScanEnabled, level, autoDeleteToxic, flagPhishingLinks, maxDuplicatePostBlock)
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = if (isSel) Color(0xFF1A237E) else Color.Transparent,
                                    contentColor = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface
                                ),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSel) Color(0xFF1A237E) else MaterialTheme.colorScheme.outlineVariant
                                )
                            ) {
                                Text(text = title, fontSize = 11.sp, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal)
                            }
                        }
                    }

                    Divider(modifier = Modifier.padding(vertical = 10.dp))

                    // Auto Delete Toxic Comments
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "টক্সিক কমেন্ট অটো-হাইড", fontWeight = FontWeight.Medium, fontSize = 13.sp)
                            Text(text = "অশালীন বা আক্রমণাত্মক ভাষা শনাক্ত হলে কমেন্ট তৎক্ষণাৎ গোপন রাখা হবে", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = autoDeleteToxic,
                            onCheckedChange = {
                                autoDeleteToxic = it
                                viewModel.updateAutoModSettings(aiScanEnabled, sensitivityLevel, it, flagPhishingLinks, maxDuplicatePostBlock)
                            }
                        )
                    }

                    Divider(modifier = Modifier.padding(vertical = 10.dp))

                    // Anti-Phishing Link Scanner
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "অ্যান্টি-ফিশিং ও স্ক্যাম লিঙ্ক ডিটেক্টর", fontWeight = FontWeight.Medium, fontSize = 13.sp)
                            Text(text = "সন্দেহজনক বাহ্যিক URL ও জুয়া/লটারি লিঙ্ক সহ পোস্ট ব্লক হবে", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = flagPhishingLinks,
                            onCheckedChange = {
                                flagPhishingLinks = it
                                viewModel.updateAutoModSettings(aiScanEnabled, sensitivityLevel, autoDeleteToxic, it, maxDuplicatePostBlock)
                            }
                        )
                    }

                    Divider(modifier = Modifier.padding(vertical = 10.dp))

                    // Duplicate Post Blocker
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "একই পোস্ট বারবার শেয়ার প্রতিরোধ", fontWeight = FontWeight.Medium, fontSize = 13.sp)
                            Text(text = "১০ মিনিটের মধ্যে একই টেক্সট একাধিকবার পোস্ট করলে ব্লক হবে", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = maxDuplicatePostBlock,
                            onCheckedChange = {
                                maxDuplicatePostBlock = it
                                viewModel.updateAutoModSettings(aiScanEnabled, sensitivityLevel, autoDeleteToxic, flagPhishingLinks, it)
                            }
                        )
                    }
                }
            }
        }

        // Live Test AI Filter Box
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF3F51B5).copy(alpha = 0.3f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Science, contentDescription = null, tint = Color(0xFF3F51B5), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "লাইভ ফিল্টার সিমুলেটর ও টেস্ট বক্স",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "যেকোনো বাক্য লিখে পরীক্ষা করুন বর্তমান এআই নীতি ও নিষিদ্ধ শব্দ অনুযায়ী এটি পাশ করবে নাকি ব্লক হবে।",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = testSentenceInput,
                        onValueChange = {
                            testSentenceInput = it
                            testResult = null
                        },
                        placeholder = { Text("পরীক্ষার জন্য বাক্য লিখুন (উদা: 'অনলাইনে ফেক অফার ও লটারি জিতে নিন')", fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            if (testSentenceInput.isNotBlank()) {
                                val text = testSentenceInput.lowercase()
                                val matchedBanned = autoModRule.bannedKeywords.filter { text.contains(it.lowercase()) }
                                val containsLink = text.contains("http://") || text.contains("https://") || text.contains(".com")

                                if (matchedBanned.isNotEmpty()) {
                                    testResult = Pair(false, "❌ ব্লক হবে! নিষিদ্ধ শব্দ শনাক্ত: ${matchedBanned.joinToString(", ")}")
                                } else if (flagPhishingLinks && containsLink) {
                                    testResult = Pair(false, "⚠️ সতর্কতা! বাহ্যিক লিঙ্ক শনাক্ত হয়েছে যা ম্যানুয়াল পর্যালোচনায় যাবে।")
                                } else {
                                    testResult = Pair(true, "✅ নিরাপদ! এই পোস্ট কোনো নীতিমালা লঙ্ঘন করেনি।")
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3F51B5))
                    ) {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("এআই ফিল্টার টেস্ট করুন", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }

                    testResult?.let { (isSafe, msg) ->
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSafe) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSafe) Color(0xFF81C784) else Color(0xFFEF9A9A)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = msg,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (isSafe) Color(0xFF1B5E20) else Color(0xFFC62828),
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
