package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.service.*
import com.example.ui.theme.TeaLeafGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NationalHelplineScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager: ClipboardManager = LocalClipboardManager.current
    var selectedTab by remember { mutableStateOf(0) } // 0: BD Special, 1: 30 Countries, 2: First Aid, 3: Eye & Health Care
    var bdCategoryFilter by remember { mutableStateOf("সব") }
    var searchQuery by remember { mutableStateOf("") }
    var selectedFirstAidGuide by remember { mutableStateOf<FirstAidGuide?>(null) }

    // SOS Emergency Dialog State
    var showSosDialog by remember { mutableStateOf(false) }
    var sosContacts by remember { mutableStateOf(GlobalEmergencyRegistry.defaultSosContacts.toList()) }
    var editingContactIndex by remember { mutableStateOf(-1) }
    var newContactName by remember { mutableStateOf("") }
    var newContactPhone by remember { mutableStateOf("") }

    val mockGpsLat = "23.8103"
    val mockGpsLng = "90.4125"
    val mockLocationName = "ঢাকা, বাংলাদেশ"
    val sosMsgText = "🚨 জরুরি বিপদবার্তা! আমি বিপদে আছি। আমার বর্তমান জিপিএস লোকেশন: https://maps.google.com/?q=$mockGpsLat,$mockGpsLng ($mockLocationName)। দয়া করে দ্রুত সাহায্য পাঠান অথবা ৯৯৯ এ ফোন দিন।"

    val filteredBdHelplines = remember(searchQuery, bdCategoryFilter) {
        GlobalEmergencyRegistry.bdSpecialHelplines.filter { item ->
            val matchesCategory = (bdCategoryFilter == "সব") || (item.categoryBn == bdCategoryFilter)
            val matchesQuery = searchQuery.isBlank() ||
                    item.titleBn.contains(searchQuery, ignoreCase = true) ||
                    item.number.contains(searchQuery) ||
                    item.descriptionBn.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesQuery
        }
    }

    val filtered30Countries = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            GlobalEmergencyRegistry.emergencyHelplines30Countries
        } else {
            GlobalEmergencyRegistry.emergencyHelplines30Countries.filter {
                it.countryNameBn.contains(searchQuery, ignoreCase = true) ||
                it.countryNameEn.contains(searchQuery, ignoreCase = true) ||
                it.generalEmergency.contains(searchQuery)
            }
        }
    }

    val filteredFirstAid = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            GlobalEmergencyRegistry.essentialFirstAidGuides
        } else {
            GlobalEmergencyRegistry.essentialFirstAidGuides.filter {
                it.titleBn.contains(searchQuery, ignoreCase = true) ||
                it.emergencyType.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    fun makePhoneCall(numberStr: String) {
        val cleanNum = numberStr.replace(" ", "").split("/")[0]
        try {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$cleanNum"))
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "কল করতে সমস্যা হচ্ছে: $cleanNum", Toast.LENGTH_SHORT).show()
        }
    }

    fun sendSosSms(phoneNum: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("sms:$phoneNum")).apply {
                putExtra("sms_body", sosMsgText)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "এসএমএস পাঠাতে ব্যর্থ: $phoneNum", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(TeaLeafGreen)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onBack) {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "জাতীয় হেল্পলাইন & জরুরি সেবাসমূহ",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    // Prominent SOS Panic Trigger Button
                    Button(
                        onClick = { showSosDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("🚨 ১-ক্লিক SOS প্যানিক", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }

                // 4 Main Navigation Tabs
                ScrollableTabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color(0xFF143B2E),
                    contentColor = Color.White,
                    edgePadding = 8.dp,
                    indicator = { tabPositions ->
                        TabRowDefaults.Indicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = Color(0xFF81C784),
                            height = 3.dp
                        )
                    }
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("🇧🇩", fontSize = 13.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("বাংলাদেশ জরুরি নম্বর", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    )

                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("🌍", fontSize = 13.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("৩০ দেশের হেল্পলাইন", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    )

                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.MedicalServices, contentDescription = null, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("উন্নত জরুরি চিকিৎসা", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    )

                    Tab(
                        selected = selectedTab == 3,
                        onClick = { selectedTab = 3 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("👁️", fontSize = 13.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("চোখ ও হেলথ রিমাইন্ডার", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
        ) {
            // Universal Search Input (Only for tabs 0, 1, 2)
            if (selectedTab in 0..2) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            when (selectedTab) {
                                0 -> "বাংলাদেশ জরুরি নম্বর বা সেবা খুঁজুন (যেমন: ১০৯, ৩৩৩, ১৬২৬৩)..."
                                1 -> "৩০ দেশের নাম বা কোড খুঁজুন..."
                                else -> "জরুরি চিকিৎসা বা রোগ খুঁজুন (যেমন: স্ট্রোক, সাপের কামড়)..."
                            },
                            fontSize = 12.sp
                        )
                    },
                    leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = TeaLeafGreen) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(imageVector = Icons.Default.Clear, contentDescription = null)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    ),
                    singleLine = true
                )
            }

            when (selectedTab) {
                0 -> {
                    // TAB 0: Bangladesh Special Helplines Hub
                    Column {
                        // Category Filters Row
                        val bdCategories = remember { listOf("সব", "সর্বোচ্চ অগ্রাধিকার", "স্বাস্থ্য ও চিকিৎসা", "নারী ও শিশু সুরক্ষা", "সরকারি তথ্য ও সামাজিক সাহায্য", "আইনি সেবা", "মানসিক স্বাস্থ্য") }
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            items(bdCategories) { cat ->
                                val isSel = bdCategoryFilter == cat
                                FilterChip(
                                    selected = isSel,
                                    onClick = { bdCategoryFilter = cat },
                                    label = { Text(cat, fontSize = 11.sp, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = TeaLeafGreen,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }

                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(filteredBdHelplines, key = { it.number }) { item ->
                                BdSpecialHelplineCard(
                                    item = item,
                                    onCall = { makePhoneCall(item.number) }
                                )
                            }
                        }
                    }
                }
                1 -> {
                    // TAB 1: 30 Countries Helplines
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(filtered30Countries, key = { it.countryIso }) { item ->
                            EmergencyHelplineCard(
                                helpline = item,
                                onDial = { number -> makePhoneCall(number) }
                            )
                        }
                    }
                }
                2 -> {
                    // TAB 2: Advanced Medical & First Aid Guides
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F261C)),
                                border = BorderStroke(1.dp, Color(0xFF81C784).copy(alpha = 0.5f))
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.Default.HealthAndSafety, contentDescription = null, tint = Color(0xFF81C784))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "উন্নত জীবনরক্ষাকারী জরুরি চিকিৎসা গাইডলাইন",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "ব্রেন স্ট্রোক, হার্ট অ্যাটাক, সাপের কামড় বা বিষক্রিয়ার মতো সংকটাপন্ন মুহূর্তে সঠিক করণীয় ও বর্জনীয় নিম্নে স্পষ্টভাবে তুলে ধরা হলো। প্রয়োজনে ৯৯৯ বা ১৬২৬৩ তে কল দিন।",
                                        fontSize = 12.sp,
                                        color = Color.White.copy(alpha = 0.85f),
                                        lineHeight = 17.sp
                                    )
                                }
                            }
                        }

                        items(filteredFirstAid, key = { it.id }) { guide ->
                            AdvancedFirstAidCard(
                                guide = guide,
                                isExpanded = selectedFirstAidGuide?.id == guide.id,
                                onToggleExpand = {
                                    selectedFirstAidGuide = if (selectedFirstAidGuide?.id == guide.id) null else guide
                                },
                                onCallEmergency = { makePhoneCall("১৬২৬৩") }
                            )
                        }
                    }
                }
                3 -> {
                    // TAB 3: Digital Eye Care 20-20-20 Rule & Hydration Health Companion
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF1B5E20))
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("👁️ ডিজিটাল আই-কেয়ার & হাইড্রেশন অ্যালার্ট", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "মোবাইল বা কম্পিউটারের আলো চোখের রেটিনা ও চোখের পেশীকে ক্লান্ত করে। চোখের ক্লান্তি দূর করতে এবং শরীর আর্দ্র রাখতে নিচের বৈজ্ঞানিক নিয়মগুলো মেনে চলুন।",
                                        fontSize = 12.sp,
                                        color = Color.White.copy(alpha = 0.9f),
                                        lineHeight = 17.sp
                                    )
                                }
                            }
                        }

                        items(GlobalEmergencyRegistry.eyeCareGuides) { guide ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = TeaLeafGreen.copy(alpha = 0.15f)
                                        ) {
                                            Text(guide.ruleType, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TeaLeafGreen, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(guide.titleBn, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)

                                    Spacer(modifier = Modifier.height(8.dp))

                                    guide.stepsBn.forEach { step ->
                                        Text(step, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.padding(vertical = 2.dp), lineHeight = 17.sp)
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(guide.benefitBn, fontSize = 11.sp, color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // SOS Emergency Modal Dialog
    if (showSosDialog) {
        Dialog(onDismissRequest = { showSosDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🚨", fontSize = 24.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("১-ক্লিক SOS প্যানিক সিস্টেম", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD32F2F))
                        }

                        IconButton(onClick = { showSosDialog = false }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                        }
                    }

                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    Text("📍 তাৎক্ষণিক জিপিএস লোকেশন শনাক্তকরণের বার্তা:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFFFEBEE),
                        border = BorderStroke(1.dp, Color(0xFFFFCDD2))
                    ) {
                        Text(
                            text = sosMsgText,
                            fontSize = 11.sp,
                            color = Color(0xFFB71C1C),
                            modifier = Modifier.padding(10.dp),
                            lineHeight = 16.sp
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(sosMsgText))
                                Toast.makeText(context, "জিইও লোকেশন টেক্সট কপি করা হয়েছে!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(2.dp)
                        ) {
                            Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("লিংক কপি", fontSize = 11.sp)
                        }

                        Button(
                            onClick = { makePhoneCall("999") },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(2.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Call, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("৯৯৯ ডায়াল", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("📱 ৩ জন জরুরি স্বজনের কন্টাক্ট নম্বর:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E272C))

                    sosContacts.forEachIndexed { index, contact ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("${contact.nameBn} (${contact.relationBn})", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Text(contact.phone, fontSize = 11.sp, color = TeaLeafGreen)
                                }

                                Button(
                                    onClick = { sendSosSms(contact.phone) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00897B)),
                                    shape = RoundedCornerShape(6.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text("SMS পাঠান", fontSize = 10.sp)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Close Button
                    Button(
                        onClick = { showSosDialog = false },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = TeaLeafGreen),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("বন্ধ করুন", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun BdSpecialHelplineCard(
    item: BangladeshSpecialHelpline,
    onCall: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(TeaLeafGreen.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(item.iconEmoji, fontSize = 22.sp)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = TeaLeafGreen.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = item.categoryBn,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = TeaLeafGreen,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        if (item.isTollFree) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFFE8F5E9)
                            ) {
                                Text(
                                    text = "টোল ফ্রি 🆓",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2E7D32),
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = item.titleBn,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = item.descriptionBn,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Direct Call Button
            Button(
                onClick = onCall,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(imageVector = Icons.Default.Call, contentDescription = null, modifier = Modifier.size(16.dp))
                    Text(item.number, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun EmergencyHelplineCard(
    helpline: EmergencyHelpline,
    onDial: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = helpline.flagEmoji, fontSize = 28.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "${helpline.countryNameBn} (${helpline.countryNameEn})",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = helpline.notesBn,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = TeaLeafGreen.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, TeaLeafGreen)
                ) {
                    Text(
                        text = "জরুরী: ${helpline.generalEmergency}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TeaLeafGreen,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 3 Quick Action Buttons: Police, Ambulance, Fire
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Button(
                    onClick = { onDial(helpline.policeNumber) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5)),
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                ) {
                    Icon(imageVector = Icons.Default.LocalPolice, contentDescription = null, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(2.dp))
                    Text("পুলিশ: ${helpline.policeNumber.split(" ")[0]}", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { onDial(helpline.ambulanceNumber) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                ) {
                    Icon(imageVector = Icons.Default.MedicalServices, contentDescription = null, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(2.dp))
                    Text("অ্যাম্বুলেন্স: ${helpline.ambulanceNumber.split(" ")[0]}", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { onDial(helpline.fireNumber) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFB8C00)),
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                ) {
                    Icon(imageVector = Icons.Default.LocalFireDepartment, contentDescription = null, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(2.dp))
                    Text("ফায়ার: ${helpline.fireNumber.split(" ")[0]}", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun AdvancedFirstAidCard(
    guide: FirstAidGuide,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onCallEmergency: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggleExpand() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(guide.iconEmoji, fontSize = 28.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = guide.titleBn,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = guide.emergencyType,
                            fontSize = 11.sp,
                            color = TeaLeafGreen,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                IconButton(onClick = onToggleExpand) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = "Expand",
                        tint = TeaLeafGreen
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Critical Urgency Banner
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFFFEBEE),
                border = BorderStroke(1.dp, Color(0xFFFFCDD2))
            ) {
                Text(
                    text = guide.criticalUrgency,
                    fontSize = 11.sp,
                    color = Color(0xFFC62828),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            // Expanded Details Section
            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    // Symptoms
                    Text("🔍 প্রধান লক্ষণসমূহ:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    guide.symptomsBn.forEach { symptom ->
                        Text(symptom, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(vertical = 2.dp))
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Immediate Steps
                    Text("📋 জরুরি পদক্ষেপসমূহ:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TeaLeafGreen)
                    guide.stepsBn.forEach { step ->
                        Text(step, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.padding(vertical = 3.dp), lineHeight = 17.sp)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Do's and Don'ts Grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Do's
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text("করণীয় (Do's)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                                guide.dosListBn.forEach { d ->
                                    Text(d, fontSize = 10.sp, color = Color(0xFF1B5E20), modifier = Modifier.padding(top = 2.dp))
                                }
                            }
                        }

                        // Don'ts
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text("বর্জনীয় (Don'ts)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFC62828))
                                guide.dontsListBn.forEach { d ->
                                    Text(d, fontSize = 10.sp, color = Color(0xFFB71C1C), modifier = Modifier.padding(top = 2.dp))
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Warning & Call Medical Doctor
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = guide.warningBn,
                            fontSize = 11.sp,
                            color = Color(0xFFE65100),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )

                        Button(
                            onClick = onCallEmergency,
                            colors = ButtonDefaults.buttonColors(containerColor = TeaLeafGreen),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Call, contentDescription = null, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("১৬২৬৩ সরকারি ডাক্তার", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
