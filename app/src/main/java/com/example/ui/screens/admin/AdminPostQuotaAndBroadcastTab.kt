package com.example.ui.screens.admin

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.OfficialNotice
import com.example.data.model.ServerDailyPostQuota
import com.example.ui.viewmodel.PageBookViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPostQuotaAndBroadcastTab(
    viewModel: PageBookViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val serverQuota by viewModel.serverPostQuota.collectAsStateWithLifecycle()
    val revenueConfig by viewModel.revenueConfig.collectAsStateWithLifecycle()
    val officialNotice by viewModel.officialNotice.collectAsStateWithLifecycle()

    var activeSubTab by remember { mutableStateOf(0) } // 0: Server Quota & Limits, 1: Broadcast & Notices

    // State for Capacity Form
    var capacityInput by remember { mutableStateOf(serverQuota.serverDailyCapacity.toString()) }
    var regularLimitInput by remember { mutableStateOf(serverQuota.perUserDailyLimit.toString()) }
    var verifiedLimitInput by remember { mutableStateOf(serverQuota.verifiedUserDailyLimit.toString()) }
    var pageLimitInput by remember { mutableStateOf(serverQuota.pageCreatorDailyLimit.toString()) }

    // State for Broadcast & Notices
    var noticeTitleInput by remember { mutableStateOf(officialNotice.title) }
    var noticeMessageInput by remember { mutableStateOf(officialNotice.message) }
    var noticeUrgencyInput by remember { mutableStateOf(officialNotice.urgency) }
    var isNoticeActiveInput by remember { mutableStateOf(officialNotice.isActive) }

    var broadcastTitleInput by remember { mutableStateOf("") }
    var broadcastMessageInput by remember { mutableStateOf("") }

    val presetNotices = remember {
        listOf(
            Pair("আজ রাত ১২:০০ থেকে সার্ভার অপ্টিমাইজেশন ও স্পীড আপগ্রেড চলবে।", "সিস্টেম এডমিন"),
            Pair("নতুন কনটেন্ট পলিসি কার্যকর হয়েছে! কোনো কপিরাইট লঙ্ঘন করা যাবে না।", "মডারেশন টিম"),
            Pair("রমজান উপলক্ষে নবচেতনা মার্কেটপ্লেসে ০% প্ল্যাটফর্ম কমিশন ঘোষণা!", "মার্কেটপ্লেস ডিরেক্টরেট"),
            Pair("ফেক লাইক ও বট অ্যাক্টিভিটি কঠোরভাবে নিষিদ্ধ। আইনানুগ ব্যবস্থা নেওয়া হবে।", "সিকিউরিটি টিম")
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- 1. Top Real-Time Quota Meter Banner ---
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(
                    1.5.dp,
                    if (serverQuota.isNearCapacity) Brush.horizontalGradient(listOf(Color(0xFFE65100), Color(0xFFD32F2F)))
                    else Brush.horizontalGradient(listOf(Color(0xFF1976D2), Color(0xFF00897B)))
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier.fillMaxWidth().testTag("server_quota_hero_card")
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    // Header Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (serverQuota.isNearCapacity) Color(0xFFD32F2F).copy(alpha = 0.15f)
                                        else Color(0xFF1976D2).copy(alpha = 0.15f)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (serverQuota.isNearCapacity) Icons.Default.Warning else Icons.Default.CloudSync,
                                    contentDescription = null,
                                    tint = if (serverQuota.isNearCapacity) Color(0xFFD32F2F) else Color(0xFF1976D2),
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "দৈনিক সার্ভার পোস্ট ক্ষমতা ও কোটা",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "রিয়েল-টাইম পোস্ট ট্র্যাকিং ও লোড মনিটর",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Status Badge
                        val badgeColor = when {
                            serverQuota.isAtFullCapacity -> Color(0xFFD32F2F)
                            serverQuota.isNearCapacity -> Color(0xFFE65100)
                            else -> Color(0xFF2E7D32)
                        }
                        val statusText = when {
                            serverQuota.isAtFullCapacity -> "কোটা পূর্ণ (100%)"
                            serverQuota.isNearCapacity -> "উচ্চ লোড (${(serverQuota.usagePercentage * 100).toInt()}%)"
                            else -> "সুস্থ (${(serverQuota.usagePercentage * 100).toInt()}%)"
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = badgeColor.copy(alpha = 0.12f),
                            border = BorderStroke(1.dp, badgeColor.copy(alpha = 0.4f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(badgeColor)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = statusText,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = badgeColor
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // 3-Metric Summary Boxes
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        QuotaMetricBox(
                            title = "মোট ধারণক্ষমতা",
                            value = "${serverQuota.serverDailyCapacity} টি",
                            subText = "১ দিনে সর্বোচ্চ সম্ভব",
                            icon = Icons.Default.AllInclusive,
                            tint = Color(0xFF1976D2),
                            modifier = Modifier.weight(1f)
                        )
                        QuotaMetricBox(
                            title = "আজ পোস্ট হয়েছে",
                            value = "${serverQuota.totalPostsToday} টি",
                            subText = "সার্ভারে লাইভ ডাটা",
                            icon = Icons.Default.PostAdd,
                            tint = Color(0xFF7B1FA2),
                            modifier = Modifier.weight(1f)
                        )
                        QuotaMetricBox(
                            title = "আর করতে পারবে",
                            value = "${serverQuota.remainingPostsToday} টি",
                            subText = "অবশিষ্ট খালি কোটা",
                            icon = Icons.Default.CheckCircle,
                            tint = Color(0xFF2E7D32),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Visual Progress Bar
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "সার্ভার কোটা ব্যবহারের অগ্রগতি",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${serverQuota.totalPostsToday} / ${serverQuota.serverDailyCapacity} (${String.format(Locale.US, "%.1f", serverQuota.usagePercentage * 100)}%)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (serverQuota.isNearCapacity) Color(0xFFD32F2F) else Color(0xFF1976D2)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        LinearProgressIndicator(
                            progress = { serverQuota.usagePercentage },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .clip(RoundedCornerShape(5.dp)),
                            color = if (serverQuota.isNearCapacity) Color(0xFFD32F2F) else Color(0xFF1976D2),
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "পিক আওয়ার: ${serverQuota.peakPostingTime}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "আজ পোস্টকারী ইউজার: ${serverQuota.activePostersTodayCount} জন",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // --- 2. Sub Tabs ---
        item {
            SecondaryScrollableTabRow(
                selectedTabIndex = activeSubTab,
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                contentColor = MaterialTheme.colorScheme.primary,
                edgePadding = 0.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
            ) {
                Tab(
                    selected = activeSubTab == 0,
                    onClick = { activeSubTab = 0 },
                    text = { Text("সার্ভার ক্যাপাসিটি ও পোস্ট লিমিট", fontWeight = FontWeight.SemiBold) },
                    icon = { Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(18.dp)) }
                )
                Tab(
                    selected = activeSubTab == 1,
                    onClick = { activeSubTab = 1 },
                    text = { Text("সেন্ট্রাল নোটিশ ও ব্রডকাস্ট", fontWeight = FontWeight.SemiBold) },
                    icon = { Icon(Icons.Default.Campaign, contentDescription = null, modifier = Modifier.size(18.dp)) }
                )
            }
        }

        if (activeSubTab == 0) {
            // --- SECTION: SERVER CAPACITY & POST LIMIT CONFIG ---

            // A. Smart Capacity Calculator & Safe Estimator
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Calculate, contentDescription = null, tint = Color(0xFF00897B), modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "💡 স্মার্ট ক্যাপাসিটি ক্যালকুলেটর ও পরামর্শক",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        val activeUsers = 14820
                        val estimatedMaxDailyPosts = activeUsers * (regularLimitInput.toIntOrNull() ?: 5) * 0.15
                        val currentCapacity = capacityInput.toIntOrNull() ?: 10000
                        val isEstimatedSafe = estimatedMaxDailyPosts <= currentCapacity

                        Text(
                            text = "আপনার প্ল্যাটফর্মে বর্তমানে ১৪,৮২০ জন অ্যাক্টিভ ইউজার রয়েছে। যদি ১৫% ইউজার সর্বোচ্চ লিমিট (${regularLimitInput}টি) পোস্ট করে, তবে আনুমানিক ${estimatedMaxDailyPosts.toInt()}টি পোস্ট হবে।",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 17.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isEstimatedSafe) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                            border = BorderStroke(1.dp, if (isEstimatedSafe) Color(0xFF81C784) else Color(0xFFEF9A9A)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (isEstimatedSafe) Icons.Default.CheckCircle else Icons.Default.ErrorOutline,
                                    contentDescription = null,
                                    tint = if (isEstimatedSafe) Color(0xFF2E7D32) else Color(0xFFC62828),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isEstimatedSafe)
                                        "অনুমোদিত: আপনার সেট করা $currentCapacity টি ধারণক্ষমতার ভেতরে এটি সম্পূর্ণ নিরাপদ ও বাফারিং-মুক্ত থাকবে।"
                                    else
                                        "সতর্কতা: সম্ভাব্য লোড ($estimatedMaxDailyPosts) সার্ভার ধারণক্ষমতার ($currentCapacity) চেয়ে বেশি। ক্যাপাসিটি বাড়ান বা ইউজার লিমিট কমান।",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isEstimatedSafe) Color(0xFF2E7D32) else Color(0xFFC62828)
                                )
                            }
                        }
                    }
                }
            }

            // B. Total Server Daily Post Capacity Form
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Storage, contentDescription = null, tint = Color(0xFF1976D2))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("১. সার্ভার মোট দৈনিক ধারণক্ষমতা (Total Capacity)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFE3F2FD)
                            ) {
                                Text(
                                    text = "${serverQuota.serverDailyCapacity} টি / দিন",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1976D2),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Text(
                            text = "সার্ভার ২৪ ঘণ্টায় সর্বমোট কতটি পোস্ট গ্রহণ করতে পারবে। এই সংখ্যা পার হলে স্বয়ংক্রিয়ভাবে ওভারলোড গার্ড ও নোটিশ চালু হবে।",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        // Presets
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(5000, 10000, 25000, 50000, 100000).forEach { cap ->
                                val isSelected = capacityInput == "$cap"
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { capacityInput = "$cap" },
                                    label = { Text("${cap / 1000}K", fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) }
                                )
                            }
                        }

                        OutlinedTextField(
                            value = capacityInput,
                            onValueChange = { capacityInput = it },
                            label = { Text("সার্ভারের দৈনিক মোট পোস্ট নেওয়ার সীমা") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            trailingIcon = { Text("টি পোস্ট", fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(end = 12.dp)) }
                        )

                        Button(
                            onClick = {
                                val cap = capacityInput.toIntOrNull() ?: 10000
                                if (cap >= 100) {
                                    viewModel.updateServerDailyCapacity(cap)
                                    Toast.makeText(context, "সার্ভার মোট ধারণক্ষমতা $cap টি সফলভাবে সেট করা হয়েছে!", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "কমপক্ষে ১০০ টি নির্ধারণ করুন", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("সার্ভার ক্যাপাসিটি আপডেট করুন", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // C. Tiered User Daily Post Limits
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Group, contentDescription = null, tint = Color(0xFF7B1FA2))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("২. ব্যবহারকারীর ধরনভিত্তিক দৈনিক পোস্ট লিমিট", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }

                        Text(
                            text = "বিভিন্ন ধরনের ইউজারের জন্য আলাদা আলাদা দৈনিক পোস্ট সীমা নির্ধারণ করুন যাতে স্প্যাম রোধ করা যায় এবং গুণগত মান বজায় থাকে।",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        // 1. Regular Users
                        OutlinedTextField(
                            value = regularLimitInput,
                            onValueChange = { regularLimitInput = it },
                            label = { Text("সাধারণ ব্যবহারকারী (Regular Users)") },
                            placeholder = { Text("যেমন: 5") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            trailingIcon = { Text("টি / দিন", fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(end = 12.dp)) }
                        )

                        // 2. Verified Creators
                        OutlinedTextField(
                            value = verifiedLimitInput,
                            onValueChange = { verifiedLimitInput = it },
                            label = { Text("ভেরিফাইড ক্রিয়েটর ও ইনফ্লুয়েন্সার (Verified)") },
                            placeholder = { Text("যেমন: 20") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            trailingIcon = { Text("টি / দিন", fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(end = 12.dp)) }
                        )

                        // 3. Official Pages
                        OutlinedTextField(
                            value = pageLimitInput,
                            onValueChange = { pageLimitInput = it },
                            label = { Text("অফিসিয়াল নিউজ ও ব্র্যান্ড পেজ (Official Pages)") },
                            placeholder = { Text("যেমন: 50") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            trailingIcon = { Text("টি / দিন", fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(end = 12.dp)) }
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "পোস্ট লিমিট সক্রিয় রাখুন (Enforce Limit)",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (serverQuota.isAutoThrottlingEnabled) "অন রয়েছে: ইউজাররা দৈনিক নির্ধারিত সীমার বেশি পোস্ট করতে পারবে না" else "অফ রয়েছে: ইউজাররা সীমাহীন পোস্ট করতে পারবে",
                                    fontSize = 11.sp,
                                    color = if (serverQuota.isAutoThrottlingEnabled) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Switch(
                                checked = serverQuota.isAutoThrottlingEnabled,
                                onCheckedChange = { enabled ->
                                    viewModel.toggleAutoThrottling(enabled)
                                    Toast.makeText(context, if (enabled) "পোস্ট লিমিট সক্রিয় করা হয়েছে" else "সীমাহীন পোস্টিং মোড সক্রিয়", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }

                        Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                        Button(
                            onClick = {
                                val reg = regularLimitInput.toIntOrNull() ?: 5
                                val ver = verifiedLimitInput.toIntOrNull() ?: 20
                                val pag = pageLimitInput.toIntOrNull() ?: 50
                                if (reg > 0 && ver > 0 && pag > 0) {
                                    viewModel.updateTieredPostLimits(reg, ver, pag)
                                    Toast.makeText(context, "সকল টায়ারের পোস্ট লিমিট সফলভাবে আপডেট করা হয়েছে! ✅", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "সঠিক সংখ্যা দিন", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7B1FA2)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("ইউজার পোস্ট লিমিট সংরক্ষণ করুন", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        } else {
            // --- SECTION: OFFICIAL NOTICES & CENTRAL BROADCAST ---

            // A. Emergency Official Notice (Pop-up & Banner)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Announcement, contentDescription = null, tint = Color(0xFFE65100))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("জরুরি অফিশিয়াল নোটিশ (হোম স্ক্রিন ব্যানার)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                            Switch(
                                checked = isNoticeActiveInput,
                                onCheckedChange = { isNoticeActiveInput = it }
                            )
                        }

                        OutlinedTextField(
                            value = noticeTitleInput,
                            onValueChange = { noticeTitleInput = it },
                            label = { Text("নোটিশ শিরোনাম") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = noticeMessageInput,
                            onValueChange = { noticeMessageInput = it },
                            label = { Text("নোটিশ বিস্তারিত বার্তা") },
                            minLines = 3,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Button(
                            onClick = {
                                viewModel.updateOfficialNotice(
                                    title = noticeTitleInput,
                                    message = noticeMessageInput,
                                    isActive = isNoticeActiveInput,
                                    urgency = noticeUrgencyInput
                                )
                                Toast.makeText(context, "অফিশিয়াল নোটিশ সফলভাবে পাবলিশ করা হয়েছে!", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE65100)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Publish, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("নোটিশ পাবলিশ করুন", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // B. Direct Push to All Users' Notification Feeds
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Send, contentDescription = null, tint = Color(0xFF2E7D32))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("সকলের নোটিফিকেশন ফিডে ব্রডকাস্ট", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }

                        OutlinedTextField(
                            value = broadcastTitleInput,
                            onValueChange = { broadcastTitleInput = it },
                            label = { Text("ঘোষণার প্রেরক / শিরোনাম") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = broadcastMessageInput,
                            onValueChange = { broadcastMessageInput = it },
                            label = { Text("বার্তা") },
                            minLines = 2,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Button(
                            onClick = {
                                if (broadcastTitleInput.isNotBlank() && broadcastMessageInput.isNotBlank()) {
                                    viewModel.broadcastSystemNotification(broadcastTitleInput.trim(), broadcastMessageInput.trim())
                                    Toast.makeText(context, "সফলভাবে ব্রডকাস্ট পাঠানো হয়েছে!", Toast.LENGTH_SHORT).show()
                                    broadcastTitleInput = ""
                                    broadcastMessageInput = ""
                                } else {
                                    Toast.makeText(context, "শিরোনাম এবং বার্তা লিখুন", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("সকলের নোটিফিকেশনে পাঠান", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            item {
                Text("রেডিমেড নোটিশ টেমপ্লেট:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }

            items(presetNotices) { (text, author) ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            broadcastTitleInput = author
                            broadcastMessageInput = text
                        },
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.EditNote, contentDescription = null, tint = Color(0xFF00897B))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = text, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                            Text(text = "প্রেরক: $author", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuotaMetricBox(
    title: String,
    value: String,
    subText: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = tint.copy(alpha = 0.08f),
        border = BorderStroke(1.dp, tint.copy(alpha = 0.25f)),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = tint)
            Text(text = title, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
            Text(text = subText, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
