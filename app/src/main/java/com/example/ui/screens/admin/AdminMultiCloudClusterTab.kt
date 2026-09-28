package com.example.ui.screens.admin

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.*
import com.example.ui.viewmodel.PageBookViewModel
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminMultiCloudClusterTab(
    viewModel: PageBookViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val freeEngines by viewModel.freeCloudEngines.collectAsStateWithLifecycle()
    val multiCloudConfig by viewModel.multiCloudConfig.collectAsStateWithLifecycle()
    val pingResults by viewModel.multiCloudPingResults.collectAsStateWithLifecycle()
    val bots by viewModel.telegramBots.collectAsStateWithLifecycle()
    val clusterConfig by viewModel.telegramClusterConfig.collectAsStateWithLifecycle()
    val uploadLogs by viewModel.telegramUploadLogs.collectAsStateWithLifecycle()

    var selectedSubTab by remember { mutableStateOf(0) } // 0: 5 Engines Overview, 1: Telegram 6-Bots, 2: Speed Benchmark, 3: Sync & Failover, 4: Upload Logs
    var isPingingAll by remember { mutableStateOf(false) }
    var pingingEngineType by remember { mutableStateOf<CloudEngineType?>(null) }
    var isBackingUpData by remember { mutableStateOf(false) }
    var editingBot by remember { mutableStateOf<TelegramBotNode?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- 1. Multi-Cloud Top Hero Banner ---
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.5.dp, Brush.horizontalGradient(listOf(Color(0xFF0288D1), Color(0xFF00897B), Color(0xFFF38020)))),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier.fillMaxWidth().testTag("multi_cloud_cluster_hero")
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color(0xFF0288D1).copy(alpha = 0.15f), Color.Transparent)
                                )
                            )
                    )

                    Column(modifier = Modifier.padding(20.dp)) {
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
                                        .background(Brush.linearGradient(listOf(Color(0xFF0288D1), Color(0xFF3ECF8E)))),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CloudSync,
                                        contentDescription = "Multi Cloud",
                                        tint = Color.White,
                                        modifier = Modifier.size(26.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "৫টি ফ্রি সার্ভার ও ক্লাউড ইঞ্জিন",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "ফায়ারবেস + ৫টি ফ্রি হাই-স্পিড সার্ভার নেটওয়ার্ক",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFF2E7D32).copy(alpha = 0.15f),
                                border = BorderStroke(1.dp, Color(0xFF2E7D32).copy(alpha = 0.4f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF2E7D32))
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "১০০% ফ্রি ক্লাস্টার সক্রিয়",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF2E7D32)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Multi-Cloud Metrics Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            MultiCloudMetricCard(
                                title = "সংযুক্ত ফ্রি ইঞ্জিন",
                                value = "${freeEngines.count { it.isEnabled }} / ৫টি",
                                subtitle = "ফায়ারবেস সহ ৬-ওয়ে আর্কিটেকচার",
                                icon = Icons.Default.Hub,
                                color = Color(0xFF0288D1),
                                modifier = Modifier.weight(1f)
                            )
                            MultiCloudMetricCard(
                                title = "মাসিক সেভড কস্ট",
                                value = "$${String.format(Locale.US, "%.2f", multiCloudConfig.totalEstimatedMonthlySavingsUsd)}",
                                subtitle = "জিরো হোস্টিং খরচ ($0.00 বিল)",
                                icon = Icons.Default.Savings,
                                color = Color(0xFF2E7D32),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            MultiCloudMetricCard(
                                title = "মোট ফ্রি স্টোরেজ",
                                value = "আনলিমিটেড",
                                subtitle = "টেলিগ্রাম + সুপাবেস + অ্যাপরাইট",
                                icon = Icons.Default.Storage,
                                color = Color(0xFF7B1FA2),
                                modifier = Modifier.weight(1f)
                            )
                            MultiCloudMetricCard(
                                title = "ব্যান্ডউইথ ট্র্যাফিক",
                                value = "${multiCloudConfig.totalCombinedBandwidthTb} TB",
                                subtitle = "ক্লাউডফ্লেয়ার ও jsDelivr এজ ক্যাশ",
                                icon = Icons.Default.Speed,
                                color = Color(0xFFF38020),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        // --- 2. Sub-Tab Selector ---
        item {
            ScrollableTabRow(
                selectedTabIndex = selectedSubTab,
                edgePadding = 0.dp,
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)),
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                indicator = {}
            ) {
                val subTabs = listOf(
                    "৫টি ফ্রি ইঞ্জিন ওভারভিউ",
                    "টেলিগ্রাম ৬-বট ক্লাস্টার",
                    "লাইভ স্পিড বেঞ্চমার্ক",
                    "সিঙ্ক ও ফেলওভার সেটিংস",
                    "মিডিয়া আপলোড লগস"
                )
                subTabs.forEachIndexed { index, title ->
                    val isSelected = selectedSubTab == index
                    Tab(
                        selected = isSelected,
                        onClick = { selectedSubTab = index },
                        modifier = Modifier
                            .padding(4.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
                            ),
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 12.sp,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    )
                }
            }
        }

        // --- SUB-TAB 0: 5 FREE CLOUD ENGINES OVERVIEW ---
        if (selectedSubTab == 0) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "৫টি ফ্রি ক্লাউড ব্যাকএন্ড ইঞ্জিন গ্রিড",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "ফায়ারবেস ডাটাবেসের পাশাপাশি সংযুক্ত ফ্রি সার্ভিসেস",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Button(
                        onClick = {
                            isPingingAll = true
                            viewModel.pingAllFreeCloudEngines {
                                isPingingAll = false
                                Toast.makeText(context, "সকল ৫টি ফ্রি ক্লাউড সার্ভার পিং টেস্ট সফল!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        enabled = !isPingingAll,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        if (isPingingAll) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("পিং হচ্ছে...", fontSize = 12.sp)
                        } else {
                            Icon(Icons.Default.Refresh, contentDescription = "Ping", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("সবগুলো পিং টেস্ট", fontSize = 12.sp)
                        }
                    }
                }
            }

            items(freeEngines) { engine ->
                FreeEngineCard(
                    engine = engine,
                    isPinging = pingingEngineType == engine.engineType,
                    onPing = {
                        pingingEngineType = engine.engineType
                        viewModel.pingSingleFreeCloudEngine(engine.engineType) { result ->
                            pingingEngineType = null
                            Toast.makeText(context, "${engine.engineType.banglaTitle}: ${result.message}", Toast.LENGTH_SHORT).show()
                        }
                    },
                    onToggle = { enabled ->
                        viewModel.toggleFreeEngine(engine.engineType, enabled)
                    }
                )
            }
        }

        // --- SUB-TAB 1: TELEGRAM 6-BOT NODES ---
        else if (selectedSubTab == 1) {
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0088CC).copy(alpha = 0.08f)),
                    border = BorderStroke(1.dp, Color(0xFF0088CC).copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Telegram Info",
                            tint = Color(0xFF0088CC),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "আপনার দেওয়া ৬টি টেলিগ্রাম বট টোকেন দিয়ে গঠিত আনলিমিটেড ফ্রি ক্লাউড স্টোরেজ। লোডব্যালেন্সারের মাধ্যমে সব নোডে ট্র্যাফিক স্বয়ংক্রিয়ভাবে ভাগ হয়ে যায়।",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            items(bots) { bot ->
                TelegramBotNodeCard(
                    bot = bot,
                    onEdit = { editingBot = bot },
                    onPing = {
                        coroutineScope.launch {
                            val (success, msg) = viewModel.testTelegramBotPing(bot.id)
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }
        }

        // --- SUB-TAB 2: LIVE SPEED BENCHMARK ---
        else if (selectedSubTab == 2) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "লাইভ রেসপন্স টাইম ও লেটেন্সি বেঞ্চমার্ক",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "প্রতিটি ফ্রি ক্লাউড ইঞ্জিনের সরাসরি রেসপন্স স্পিড (ms)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        freeEngines.forEach { engine ->
                            val latency = engine.latencyMs
                            val latencyFraction = (latency.toFloat() / 150f).coerceIn(0.1f, 1f)
                            val barColor = when {
                                latency < 40 -> Color(0xFF2E7D32)
                                latency < 80 -> Color(0xFF1976D2)
                                latency < 120 -> Color(0xFFF57C00)
                                else -> Color(0xFFD32F2F)
                            }

                            Column(modifier = Modifier.padding(vertical = 6.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "${engine.engineType.banglaTitle}",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "${latency} ms",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = barColor
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                LinearProgressIndicator(
                                    progress = { latencyFraction },
                                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                                    color = barColor,
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                isPingingAll = true
                                viewModel.pingAllFreeCloudEngines {
                                    isPingingAll = false
                                    Toast.makeText(context, "সকল ইঞ্জিনের স্পিড টেস্ট আপডেট সম্পন্ন!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Speed, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("সম্পূর্ণ ক্লাস্টার স্পিড টেস্ট রি-রান করুন")
                        }
                    }
                }
            }

            if (pingResults.isNotEmpty()) {
                item {
                    Text(
                        text = "সর্বশেষ পিং রেজাল্ট ও ডায়াগনস্টিক",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                items(pingResults) { res ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(if (res.isSuccess) Color(0xFF2E7D32) else Color(0xFFD32F2F))
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = res.engineType.banglaTitle,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = res.message,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                text = res.timestamp,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // --- SUB-TAB 3: SYNC & FAILOVER SETTINGS ---
        else if (selectedSubTab == 3) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "স্মার্ট ট্র্যাফিক ও মাল্টি-সার্ভার সিঙ্ক কনফিগারেশন",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "কোনো একটি সার্ভার ডাউন হলে স্বয়ংক্রিয়ভাবে পরবর্তী ফ্রি সার্ভারে সুইচ হবে",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Switch 1: Auto Failover
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("অটো-ফেলওভার সুরক্ষা (Auto Failover)", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                Text("সার্ভার রেট-লিমিট হলে সেকেন্ডারি সার্ভারে ট্র্যাফিক শিফট", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Switch(
                                checked = multiCloudConfig.autoFailoverEnabled,
                                onCheckedChange = { checked ->
                                    viewModel.updateMultiCloudConfig(multiCloudConfig.copy(autoFailoverEnabled = checked))
                                }
                            )
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                        // Switch 2: Multi Mirror Sync
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("মাল্টি-মিরর ব্যাকআপ (Multi-Mirror Sync)", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                Text("পোস্ট ও মিডিয়া সুপাবেস ও টেলিগ্রাম উভয় সার্ভারে কপি রাখা", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Switch(
                                checked = multiCloudConfig.multiMirrorSyncEnabled,
                                onCheckedChange = { checked ->
                                    viewModel.updateMultiCloudConfig(multiCloudConfig.copy(multiMirrorSyncEnabled = checked))
                                }
                            )
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                        // Switch 3: Smart Traffic Routing
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("স্মার্ট এজ ক্যাশিং ও রাউটিং (Smart Edge)", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                Text("ক্লাউডফ্লেয়ার এজ থেকে দ্রুততম লোকাল রুট নির্ধারণ", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Switch(
                                checked = multiCloudConfig.smartTrafficRouting,
                                onCheckedChange = { checked ->
                                    viewModel.updateMultiCloudConfig(multiCloudConfig.copy(smartTrafficRouting = checked))
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        Button(
                            onClick = {
                                isBackingUpData = true
                                viewModel.triggerMultiCloudDataBackup { msg ->
                                    isBackingUpData = false
                                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                                }
                            },
                            enabled = !isBackingUpData,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                        ) {
                            if (isBackingUpData) {
                                CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("৫টি ইঞ্জিনে ব্যাকআপ সিঙ্ক হচ্ছে...")
                            } else {
                                Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("৫টি ইঞ্জিনে ইনস্ট্যান্ট ডেটা ব্যাকআপ সিঙ্ক করুন")
                            }
                        }
                    }
                }
            }
        }

        // --- SUB-TAB 4: UPLOAD LOGS ---
        else if (selectedSubTab == 4) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "সাম্প্রতিক ক্লাউড মিডিয়া আপলোড হিস্ট্রি",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${uploadLogs.size} টি এন্ট্রি",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            items(uploadLogs) { log ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF0088CC).copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (log.mediaType == "VIDEO") Icons.Default.VideoLibrary else Icons.Default.Image,
                                contentDescription = log.mediaType,
                                tint = Color(0xFF0088CC),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = log.originalFileName,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${log.botName} • ${log.fileSizeFormatted} • ${log.uploadTimestamp}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        IconButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Direct CDN URL", log.directCdnUrl)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "CDN URL ক্লিপবোর্ডে কপি করা হয়েছে", Toast.LENGTH_SHORT).show()
                            }
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy CDN URL", tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }
    }

    // Edit Bot Dialog
    if (editingBot != null) {
        val bot = editingBot!!
        var editName by remember { mutableStateOf(bot.name) }
        var editToken by remember { mutableStateOf(bot.token) }
        var editRole by remember { mutableStateOf(bot.assignedRole) }

        AlertDialog(
            onDismissRequest = { editingBot = null },
            title = { Text("বট নোড কনফিগারেশন (#${bot.id})") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("বটের নাম") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editRole,
                        onValueChange = { editRole = it },
                        label = { Text("অ্যাসাইনকৃত রোল / দায়িত্ব") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editToken,
                        onValueChange = { editToken = it },
                        label = { Text("টেলিগ্রাম HTTP API বট টোকেন") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateTelegramBotToken(bot.id, editToken, editName, editRole)
                        editingBot = null
                        Toast.makeText(context, "বট কনফিগ সংরক্ষিত হয়েছে", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text("সংরক্ষণ করুন")
                }
            },
            dismissButton = {
                TextButton(onClick = { editingBot = null }) {
                    Text("বাতিল")
                }
            }
        )
    }
}

@Composable
fun FreeEngineCard(
    engine: FreeCloudEngineNode,
    isPinging: Boolean,
    onPing: () -> Unit,
    onToggle: (Boolean) -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, engine.engineType.brandColor.copy(alpha = 0.3f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(engine.engineType.brandColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (engine.engineType) {
                                CloudEngineType.TELEGRAM_CLUSTER -> Icons.Default.Send
                                CloudEngineType.CLOUDFLARE_EDGE -> Icons.Default.FlashOn
                                CloudEngineType.SUPABASE_POSTGRES -> Icons.Default.Storage
                                CloudEngineType.APPWRITE_BAAS -> Icons.Default.Extension
                                CloudEngineType.GITHUB_JSDELIVR -> Icons.Default.Code
                            },
                            contentDescription = engine.name,
                            tint = engine.engineType.brandColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = engine.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = engine.engineType.provider,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Switch(
                    checked = engine.isEnabled,
                    onCheckedChange = onToggle
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = "প্রধান কাজ: ${engine.assignedFunction}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "ফ্রি টিয়ার সুবিধা: ${engine.engineType.freeTierDetails}",
                        fontSize = 11.sp,
                        color = Color(0xFF2E7D32),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = engine.status.badgeColor.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = engine.status.label,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = engine.status.badgeColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "রেসপন্স: ${engine.latencyMs}ms",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                OutlinedButton(
                    onClick = onPing,
                    enabled = !isPinging && engine.isEnabled,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    if (isPinging) {
                        CircularProgressIndicator(modifier = Modifier.size(14.dp), strokeWidth = 2.dp)
                    } else {
                        Icon(Icons.Default.Speed, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("পিং টেস্ট", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun TelegramBotNodeCard(
    bot: TelegramBotNode,
    onEdit: () -> Unit,
    onPing: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF0088CC).copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${bot.id}",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0088CC),
                            fontSize = 14.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = bot.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = bot.assignedRole,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = bot.status.badgeColor.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = bot.status.label,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = bot.status.badgeColor,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                shape = RoundedCornerShape(6.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "টোকেন: ${bot.token.take(15)}••••••••••${bot.token.takeLast(6)}",
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "আপলোড: ${bot.totalUploads}টি | লেটেন্সি: ${bot.lastPingMs}ms",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    IconButton(onClick = onPing, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Speed, contentDescription = "Ping", modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                    }
                    IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun MultiCloudMetricCard(
    title: String,
    value: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = color.copy(alpha = 0.08f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.25f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
