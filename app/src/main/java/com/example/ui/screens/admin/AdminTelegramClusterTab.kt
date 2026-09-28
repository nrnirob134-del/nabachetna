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
import coil.compose.AsyncImage
import com.example.data.model.TelegramBotNode
import com.example.data.model.TelegramBotStatus
import com.example.data.model.TelegramCloudClusterConfig
import com.example.data.model.TelegramUploadLog
import com.example.ui.viewmodel.PageBookViewModel
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminTelegramClusterTab(
    viewModel: PageBookViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val bots by viewModel.telegramBots.collectAsStateWithLifecycle()
    val clusterConfig by viewModel.telegramClusterConfig.collectAsStateWithLifecycle()
    val uploadLogs by viewModel.telegramUploadLogs.collectAsStateWithLifecycle()

    var selectedSubTab by remember { mutableStateOf(0) } // 0: Nodes, 1: CDN Tester, 2: Upload Logs, 3: Settings
    var editingBot by remember { mutableStateOf<TelegramBotNode?>(null) }
    var testingBotId by remember { mutableStateOf<Int?>(null) }
    var testResultMap by remember { mutableStateOf<Map<Int, String>>(emptyMap()) }

    var testMediaCaption by remember { mutableStateOf("নবচেতনা এক্সক্লুসিভ লাইভ মিডিয়া") }
    var testSelectedMediaType by remember { mutableStateOf("PHOTO") }
    var isUploadingTestMedia by remember { mutableStateOf(false) }
    var lastGeneratedCdnUrl by remember { mutableStateOf<String?>(null) }
    var lastUploadSuccessNote by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- 1. Top Cluster Hero Banner ---
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.5.dp, Brush.horizontalGradient(listOf(Color(0xFF0288D1), Color(0xFF00897B)))),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier.fillMaxWidth().testTag("telegram_cluster_hero")
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    // Background Glow
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color(0xFF0288D1).copy(alpha = 0.12f), Color.Transparent)
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
                                        .background(Brush.linearGradient(listOf(Color(0xFF0288D1), Color(0xFF26A69A)))),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CloudSync,
                                        contentDescription = "Cloud Storage",
                                        tint = Color.White,
                                        modifier = Modifier.size(26.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "টেলিগ্রাম ক্লাউড স্টোরেজ ক্লাস্টার",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "৬-বট হাই-স্পিড সিডিএন ও আনলিমিটেড ফ্রি স্টোরেজ",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            // Active Cluster Badge
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFE8F5E9),
                                border = BorderStroke(1.dp, Color(0xFF81C784))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
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
                                        text = "${bots.count { it.status == TelegramBotStatus.ONLINE }}/${bots.size} সক্রিয়",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF2E7D32)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Stats Grid: Storage Saved, Bandwidth, Cost Saved
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            ClusterStatBadge(
                                title = "স্টোরেজ সেভড",
                                value = "${String.format(Locale.US, "%.1f", clusterConfig.totalStorageSavedGb)} GB",
                                subText = "আনলিমিটেড ফ্রি",
                                icon = Icons.Default.Storage,
                                tint = Color(0xFF0288D1),
                                modifier = Modifier.weight(1f)
                            )
                            ClusterStatBadge(
                                title = "সিডিএন ট্র্যাফিক",
                                value = "${String.format(Locale.US, "%.2f", clusterConfig.totalBandwidthServedTb)} TB",
                                subText = "জিরো ল্যাগ",
                                icon = Icons.Default.Speed,
                                tint = Color(0xFF7B1FA2),
                                modifier = Modifier.weight(1f)
                            )
                            ClusterStatBadge(
                                title = "সাশ্রয়কৃত খরচ",
                                value = "$${String.format(Locale.US, "%.2f", clusterConfig.totalCostSavedUsd)}",
                                subText = "vs AWS/Firebase",
                                icon = Icons.Default.Savings,
                                tint = Color(0xFF2E7D32),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        // --- 2. Tab Navigation ---
        item {
            SecondaryScrollableTabRow(
                selectedTabIndex = selectedSubTab,
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                contentColor = MaterialTheme.colorScheme.primary,
                edgePadding = 0.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
            ) {
                Tab(
                    selected = selectedSubTab == 0,
                    onClick = { selectedSubTab = 0 },
                    text = { Text("৬-বট ক্লাস্টার (${bots.size})", fontWeight = FontWeight.SemiBold) },
                    icon = { Icon(Icons.Default.Dns, contentDescription = null, modifier = Modifier.size(18.dp)) }
                )
                Tab(
                    selected = selectedSubTab == 1,
                    onClick = { selectedSubTab = 1 },
                    text = { Text("সিডিএন টেস্টার", fontWeight = FontWeight.SemiBold) },
                    icon = { Icon(Icons.Default.UploadFile, contentDescription = null, modifier = Modifier.size(18.dp)) }
                )
                Tab(
                    selected = selectedSubTab == 2,
                    onClick = { selectedSubTab = 2 },
                    text = { Text("আপলোড লগ (${uploadLogs.size})", fontWeight = FontWeight.SemiBold) },
                    icon = { Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(18.dp)) }
                )
                Tab(
                    selected = selectedSubTab == 3,
                    onClick = { selectedSubTab = 3 },
                    text = { Text("ক্লাস্টার কনফিগ", fontWeight = FontWeight.SemiBold) },
                    icon = { Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(18.dp)) }
                )
            }
        }

        // --- 3. Sub-Tab Contents ---
        when (selectedSubTab) {
            0 -> {
                // Node list
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "লাইভ বট নোডসমূহ (Load-Balanced Nodes)",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "লোডব্যালেন্সিং: ${clusterConfig.loadBalancingStrategy}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                items(bots, key = { it.id }) { bot ->
                    TelegramBotNodeCard(
                        bot = bot,
                        isTesting = (testingBotId == bot.id),
                        testResult = testResultMap[bot.id],
                        onTestPing = {
                            coroutineScope.launch {
                                testingBotId = bot.id
                                val (ok, msg) = viewModel.testTelegramBotPing(bot.id)
                                testResultMap = testResultMap + (bot.id to msg)
                                testingBotId = null
                                Toast.makeText(context, "বট #${bot.id}: $msg", Toast.LENGTH_SHORT).show()
                            }
                        },
                        onEdit = { editingBot = bot },
                        onToggleStatus = { newStatus ->
                            viewModel.toggleTelegramBotStatus(bot.id, newStatus)
                        },
                        onSetPrimary = {
                            viewModel.setPrimaryTelegramBot(bot.id)
                            Toast.makeText(context, "${bot.name} কে মাস্টার বট করা হয়েছে", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }

            1 -> {
                // CDN Tester
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CloudUpload, contentDescription = null, tint = Color(0xFF0288D1))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "রিয়েল-টাইম টেলিগ্রাম ক্লাউড আপলোডার ও সিডিএন জেনারেটর",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "এই টেস্ট টুলের মাধ্যমে আপনি যেকোনো ছবি বা ভিডিও ৬টি বটের মধ্য দিয়ে টেলিগ্রাম ক্লাউডে পাঠিয়ে সরাসরি আল্ট্রা-ফাস্ট সিডিএন লিংক তৈরি করতে পারেন।",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            OutlinedTextField(
                                value = testMediaCaption,
                                onValueChange = { testMediaCaption = it },
                                label = { Text("মিডিয়া ক্যাপশন / টাইটেল") },
                                leadingIcon = { Icon(Icons.Default.Subtitles, contentDescription = null) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                FilterChip(
                                    selected = testSelectedMediaType == "PHOTO",
                                    onClick = { testSelectedMediaType = "PHOTO" },
                                    label = { Text("ছবি (Photo/Image)") },
                                    leadingIcon = { Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(16.dp)) }
                                )
                                FilterChip(
                                    selected = testSelectedMediaType == "VIDEO",
                                    onClick = { testSelectedMediaType = "VIDEO" },
                                    label = { Text("এইচডি ভিডিও (Video)") },
                                    leadingIcon = { Icon(Icons.Default.Videocam, contentDescription = null, modifier = Modifier.size(16.dp)) }
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    coroutineScope.launch {
                                        isUploadingTestMedia = true
                                        // Sample test payload bytes
                                        val sampleBytes = "Nobocetona Cloud Media Stream Binary Payload ${System.currentTimeMillis()}".toByteArray()
                                        val result = viewModel.uploadToTelegramCluster(
                                            imageBytes = sampleBytes,
                                            fileName = "test_${testSelectedMediaType.lowercase(Locale.ROOT)}_${System.currentTimeMillis()}.jpg",
                                            mediaType = testSelectedMediaType,
                                            caption = testMediaCaption
                                        )
                                        result.fold(
                                            onSuccess = { (fileId, url) ->
                                                lastGeneratedCdnUrl = url
                                                lastUploadSuccessNote = "সফলভাবে আপলোড সম্পন্ন! File ID: $fileId"
                                                Toast.makeText(context, "টেলিগ্রাম ক্লাউড সিডিএন প্রস্তুত!", Toast.LENGTH_SHORT).show()
                                            },
                                            onFailure = { err ->
                                                Toast.makeText(context, "আপলোড ত্রুটি: ${err.message}", Toast.LENGTH_LONG).show()
                                            }
                                        )
                                        isUploadingTestMedia = false
                                    }
                                },
                                enabled = !isUploadingTestMedia,
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                if (isUploadingTestMedia) {
                                    CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("ক্লাউড ক্লাস্টারে প্রসেস হচ্ছে...")
                                } else {
                                    Icon(Icons.Default.CloudSync, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("৬-বট ক্লাস্টারে টেস্ট আপলোড করুন")
                                }
                            }

                            // Result Preview Box
                            if (lastGeneratedCdnUrl != null) {
                                Spacer(modifier = Modifier.height(16.dp))
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    border = BorderStroke(1.dp, Color(0xFF0288D1).copy(alpha = 0.5f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "🎉 লাইভ সিডিএন ইউআরএল (Live Stream URL)",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF0288D1)
                                            )
                                            IconButton(
                                                onClick = {
                                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                                    val clip = ClipData.newPlainText("CDN URL", lastGeneratedCdnUrl)
                                                    clipboard.setPrimaryClip(clip)
                                                    Toast.makeText(context, "সিডিএন লিংক কপি করা হয়েছে!", Toast.LENGTH_SHORT).show()
                                                },
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(16.dp))
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = lastGeneratedCdnUrl.orEmpty(),
                                            fontSize = 11.sp,
                                            fontFamily = FontFamily.Monospace,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )

                                        Spacer(modifier = Modifier.height(10.dp))
                                        // Media thumbnail preview
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(140.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(Color.Black.copy(alpha = 0.05f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            AsyncImage(
                                                model = lastGeneratedCdnUrl,
                                                contentDescription = "Preview",
                                                modifier = Modifier.fillMaxSize()
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            2 -> {
                // Upload Logs
                item {
                    Text(
                        text = "সাম্প্রতিক ক্লাউড আপলোড হিস্ট্রি ও ব্যান্ডউইথ লগ",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                if (uploadLogs.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("এখনও কোনো ক্লাউড আপলোড হিস্ট্রি নেই", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                } else {
                    items(uploadLogs, key = { it.id }) { log ->
                        UploadLogCard(log = log)
                    }
                }
            }

            3 -> {
                // Cluster Settings
                item {
                    ClusterSettingsCard(
                        config = clusterConfig,
                        onSaveConfig = { newConfig ->
                            viewModel.updateTelegramClusterConfig(newConfig)
                            Toast.makeText(context, "ক্লাস্টার কনফিগারেশন আপডেট সফল!", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }

    // Edit Bot Dialog
    if (editingBot != null) {
        val currentBot = editingBot!!
        var editToken by remember { mutableStateOf(currentBot.token) }
        var editName by remember { mutableStateOf(currentBot.name) }
        var editRole by remember { mutableStateOf(currentBot.assignedRole) }

        AlertDialog(
            onDismissRequest = { editingBot = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Edit, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("বট #${currentBot.id} কনফিগারেশন")
                }
            },
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
                        label = { Text("নির্ধারিত রোল / ট্র্যাফিক টাইপ") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editToken,
                        onValueChange = { editToken = it },
                        label = { Text("টেলিগ্রাম বট HTTP API টোকেন") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateTelegramBotToken(currentBot.id, editToken, editName, editRole)
                        editingBot = null
                        Toast.makeText(context, "বট #${currentBot.id} আপডেট হয়েছে!", Toast.LENGTH_SHORT).show()
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
fun ClusterStatBadge(
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
            Text(text = value, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = tint)
            Text(text = title, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
            Text(text = subText, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun TelegramBotNodeCard(
    bot: TelegramBotNode,
    isTesting: Boolean,
    testResult: String?,
    onTestPing: () -> Unit,
    onEdit: () -> Unit,
    onToggleStatus: (TelegramBotStatus) -> Unit,
    onSetPrimary: () -> Unit
) {
    val context = LocalContext.current
    var showToken by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(
            1.dp,
            if (bot.isPrimary) Color(0xFF0288D1) else MaterialTheme.colorScheme.outlineVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth().testTag("bot_node_${bot.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Bot Name + Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(bot.status.badgeColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "#${bot.id}",
                            fontWeight = FontWeight.Bold,
                            color = bot.status.badgeColor,
                            fontSize = 14.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = bot.name,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (bot.isPrimary) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFF0288D1).copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = "MASTER",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0288D1),
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                        Text(
                            text = bot.assignedRole,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Status Badge Dropdown
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = bot.status.badgeColor.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, bot.status.badgeColor.copy(alpha = 0.4f)),
                    modifier = Modifier.clickable {
                        val nextStatus = when (bot.status) {
                            TelegramBotStatus.ONLINE -> TelegramBotStatus.STANDBY
                            TelegramBotStatus.STANDBY -> TelegramBotStatus.OFFLINE
                            TelegramBotStatus.OFFLINE -> TelegramBotStatus.ONLINE
                            else -> TelegramBotStatus.ONLINE
                        }
                        onToggleStatus(nextStatus)
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(bot.status.badgeColor)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = bot.status.label,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = bot.status.badgeColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Token Preview Bar (Masked)
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (showToken) bot.token else "${bot.token.take(12)}••••••••••••••••${bot.token.takeLast(6)}",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    )
                    Row {
                        IconButton(onClick = { showToken = !showToken }, modifier = Modifier.size(24.dp)) {
                            Icon(
                                if (showToken) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = "Toggle token",
                                modifier = Modifier.size(15.dp)
                            )
                        }
                        IconButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Bot Token", bot.token)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "টোকেন কপি করা হয়েছে", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy token", modifier = Modifier.size(15.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Stats row: Latency, Upload Count, Health
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Bolt, contentDescription = null, tint = Color(0xFFF57C00), modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(text = "লেটেন্সি: ${bot.lastPingMs}ms", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CloudDone, contentDescription = null, tint = Color(0xFF0288D1), modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(text = "আপলোড: ${bot.totalUploads}টি", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Favorite, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(text = "হেলথ: ${bot.healthScore}%", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            if (testResult != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFE8F5E9),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = testResult,
                        fontSize = 11.sp,
                        color = Color(0xFF2E7D32),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onTestPing,
                    enabled = !isTesting,
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    if (isTesting) {
                        CircularProgressIndicator(modifier = Modifier.size(14.dp), strokeWidth = 2.dp)
                    } else {
                        Icon(Icons.Default.NetworkPing, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("পিং টেস্ট", fontSize = 12.sp)
                    }
                }

                OutlinedButton(
                    onClick = onEdit,
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("এডিট", fontSize = 12.sp)
                }

                if (!bot.isPrimary) {
                    Button(
                        onClick = onSetPrimary,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1)),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("মাস্টার করুন", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun UploadLogCard(log: TelegramUploadLog) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF0288D1).copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (log.mediaType == "VIDEO") Icons.Default.Videocam else Icons.Default.Image,
                    contentDescription = null,
                    tint = Color(0xFF0288D1),
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = log.originalFileName,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${log.botName} • ${log.fileSizeFormatted} • ${log.durationMs}ms",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = Color(0xFFE8F5E9)
            ) {
                Text(
                    text = "CDN ✓",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E7D32),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                )
            }
        }
    }
}

@Composable
fun ClusterSettingsCard(
    config: TelegramCloudClusterConfig,
    onSaveConfig: (TelegramCloudClusterConfig) -> Unit
) {
    var selectedStrategy by remember { mutableStateOf(config.loadBalancingStrategy) }
    var channelIdInput by remember { mutableStateOf(config.defaultChannelId) }
    var compressionQuality by remember { mutableFloatStateOf(config.autoCompressionQuality.toFloat()) }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text(
                text = "ক্লাস্টার লোডব্যালেন্সার ও স্টোরেজ চ্যানেল কনফিগারেশন",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )

            OutlinedTextField(
                value = channelIdInput,
                onValueChange = { channelIdInput = it },
                label = { Text("টেলিগ্রাম প্রাইভেট চ্যানেল / চ্যাট আইডি") },
                supportingText = { Text("যেখানে ফাইলগুলো ব্যাকগ্রাউন্ডে সংরক্ষিত থাকবে (যেমন: -1002849182741)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Text(
                text = "লোডব্যালেন্সিং স্ট্র্যাটেজি (Traffic Routing):",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf(
                    Triple("ROUND_ROBIN", "রাউন্ড-রবিন (Round Robin)", "সবগুলো বটের মাঝে সমানভাবে ট্র্যাফিক বণ্টন"),
                    Triple("SMART_LOAD_DISTRIBUTION", "স্মার্ট রোল-বেসড রাউটিং", "ভিডিও, ছবি ও মার্কেটপ্লেস ফাইল নির্দিষ্ট ডেডিকেটেড বটে যাবে"),
                    Triple("FAILOVER_ONLY", "মাস্টার ও ফেইলওভার", "মাস্টার বট ব্যস্ত বা অফলাইন হলে কেবল ব্যাকআপ বট ব্যবহার")
                ).forEach { (key, title, desc) ->
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (selectedStrategy == key) Color(0xFF0288D1).copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        border = BorderStroke(
                            1.dp,
                            if (selectedStrategy == key) Color(0xFF0288D1) else Color.Transparent
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedStrategy = key }
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (selectedStrategy == key),
                                onClick = { selectedStrategy = key }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(text = title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(text = desc, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }

            Text(
                text = "অটো-কম্প্রেশন কোয়ালিটি: ${compressionQuality.toInt()}% (হাই-স্পিড অপটিমাইজেশন)",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
            Slider(
                value = compressionQuality,
                onValueChange = { compressionQuality = it },
                valueRange = 50f..100f,
                steps = 10
            )

            Button(
                onClick = {
                    onSaveConfig(
                        config.copy(
                            loadBalancingStrategy = selectedStrategy,
                            defaultChannelId = channelIdInput,
                            autoCompressionQuality = compressionQuality.toInt()
                        )
                    )
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Save, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("কনফিগারেশন আপডেট করুন")
            }
        }
    }
}
