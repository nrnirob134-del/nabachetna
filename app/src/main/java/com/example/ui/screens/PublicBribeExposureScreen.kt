package com.example.ui.screens

import android.content.Intent
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
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.service.BribeReport
import com.example.data.service.MinistryBribeSummary
import com.example.data.service.PublicBribeExposureRegistry
import com.example.ui.theme.TeaLeafGreen
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicBribeExposureScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val reports by PublicBribeExposureRegistry.bribeReports.collectAsStateWithLifecycle()
    val ministrySummaries = remember(reports) {
        PublicBribeExposureRegistry.getMinistrySummaries(reports)
    }

    var selectedMinistryFilter by remember { mutableStateOf("সব") }
    var selectedDistrictFilter by remember { mutableStateOf("সব") }
    var searchQuery by remember { mutableStateOf("") }
    var showReportDialog by remember { mutableStateOf(false) }

    val totalCalculatedBribeBdt = remember(ministrySummaries) {
        ministrySummaries.sumOf { it.totalBribeAmountBdt }
    }

    val filteredReports = remember(reports, selectedMinistryFilter, selectedDistrictFilter, searchQuery) {
        reports.filter { item ->
            val matchesMinistry = (selectedMinistryFilter == "সব") || (item.ministry.contains(selectedMinistryFilter))
            val matchesDistrict = (selectedDistrictFilter == "সব") || (item.district == selectedDistrictFilter)
            val matchesQuery = searchQuery.isBlank() ||
                    item.officerName.contains(searchQuery, ignoreCase = true) ||
                    item.designation.contains(searchQuery, ignoreCase = true) ||
                    item.departmentOffice.contains(searchQuery, ignoreCase = true) ||
                    item.incidentDescription.contains(searchQuery, ignoreCase = true) ||
                    item.countryNameBn.contains(searchQuery, ignoreCase = true)
            matchesMinistry && matchesDistrict && matchesQuery
        }
    }

    fun formatBdt(amount: Long): String {
        return try {
            val formatter = NumberFormat.getInstance(Locale("bn", "BD"))
            "৳" + formatter.format(amount) + " টাকা"
        } catch (e: Exception) {
            "৳$amount টাকা"
        }
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF8C1D1D))
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
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("🛡️", fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "দুর্নীতি & ঘুষের সোশ্যাল ফিড",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                            Text(
                                text = "১০০% গোপনীয় • শুধুমাত্র অ্যাপের ভেতর সংরক্ষিত ফিড",
                                fontSize = 10.sp,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }
                    }

                    // Floating Feed Post Trigger
                    Button(
                        onClick = { showReportDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Icon(imageVector = Icons.Default.PostAdd, contentDescription = null, tint = Color(0xFF8C1D1D), modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("রিপোর্ট লিখুন", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF8C1D1D))
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Social Feed Post Creator Bar
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .clickable { showReportDialog = true },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                                .clip(CircleShape)
                                .background(Color(0xFF8C1D1D).copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🛡️", fontSize = 20.sp)
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            text = "কোন কর্মকর্তা বা অফিসে ঘুষের শিকার হয়েছেন? পরিচয় গোপন রেখে লিখুন...",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.weight(1f)
                        )

                        IconButton(onClick = { showReportDialog = true }) {
                            Icon(imageVector = Icons.Default.CameraAlt, contentDescription = null, tint = Color(0xFF8C1D1D))
                        }
                    }
                }
            }

            // Live Analytics Banner
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF230909)),
                    border = BorderStroke(1.dp, Color(0xFFFF5252).copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = Color(0xFFFF5252).copy(alpha = 0.2f)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.TrendingUp,
                                        contentDescription = null,
                                        tint = Color(0xFFFF5252),
                                        modifier = Modifier.padding(6.dp).size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "মন্ত্রণালয়ভিত্তিক মোট ঘুষ দাবির লাইভ ফিড",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFFF5252)
                            ) {
                                Text(
                                    text = "EXPOSED 🔴",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = formatBdt(totalCalculatedBribeBdt),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFFFF5252)
                        )

                        Text(
                            text = "পুরো দেশে অনৈতিক ঘুষের শিকার ব্যক্তিদের মোট দাবিকৃত অর্থের যোগফল",
                            fontSize = 10.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Horizontal Ministry Breakdown
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(ministrySummaries) { summary ->
                                Card(
                                    shape = RoundedCornerShape(10.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF3B1212)),
                                    border = BorderStroke(1.dp, Color(0xFFFF8A80).copy(alpha = 0.3f))
                                ) {
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(summary.iconEmoji, fontSize = 12.sp)
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(summary.ministryName, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                        }
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(formatBdt(summary.totalBribeAmountBdt), fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFFFF8A80))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Search and District Filter Controls
            item {
                Column(modifier = Modifier.padding(horizontal = 12.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("দেশ, মন্ত্রণালয়, জেলা বা কর্মকর্তার নাম দিয়ে খুঁজুন...", fontSize = 11.sp) },
                        leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = Color(0xFF8C1D1D)) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(imageVector = Icons.Default.Clear, contentDescription = null)
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // District Filter Slider
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        item {
                            val isSel = selectedDistrictFilter == "সব"
                            FilterChip(
                                selected = isSel,
                                onClick = { selectedDistrictFilter = "সব" },
                                label = { Text("সব জেলা", fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFF8C1D1D), selectedLabelColor = Color.White)
                            )
                        }

                        items(PublicBribeExposureRegistry.bdDistrictsList) { dist ->
                            val isSel = selectedDistrictFilter == dist
                            FilterChip(
                                selected = isSel,
                                onClick = { selectedDistrictFilter = dist },
                                label = { Text(dist, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFF8C1D1D), selectedLabelColor = Color.White)
                            )
                        }
                    }
                }
            }

            // Social Timeline Feed Stream
            items(filteredReports, key = { it.id }) { report ->
                SocialFeedBribeCard(
                    report = report,
                    onUpvote = { PublicBribeExposureRegistry.upvoteReport(report.id) },
                    onShare = {
                        val shareText = "🚨 অনৈতিক ঘুষের অভিযোগ!\nদেশ: ${report.countryNameBn} ${report.countryFlag}\nমন্ত্রণালয়: ${report.ministry}\nঅভিযুক্ত কর্মকর্তা: ${report.officerName} (${report.designation})\nদপ্তর: ${report.departmentOffice}\nস্থান: ${report.upazilaThana}, ${report.district}\nদাবিকৃত ঘুষ: ৳${report.bribeAmountBdt} টাকা\nবিস্তারিত নবচেতনা অ্যাপে।"
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, shareText)
                        }
                        context.startActivity(Intent.createChooser(intent, "বাইরে শেয়ার করুন"))
                    }
                )
            }
        }
    }

    // Submit Anonymous Bribe Report Dialog
    if (showReportDialog) {
        var selectedCountry by remember { mutableStateOf(PublicBribeExposureRegistry.countriesList[0]) }
        var officerName by remember { mutableStateOf("") }
        var designation by remember { mutableStateOf("") }
        var selectedMinistry by remember { mutableStateOf(PublicBribeExposureRegistry.bdMinistriesList[0]) }
        var officeName by remember { mutableStateOf("") }
        var selectedDistrict by remember { mutableStateOf("ঢাকা") }
        var thanaName by remember { mutableStateOf("") }
        var amountText by remember { mutableStateOf("") }
        var descriptionText by remember { mutableStateOf("") }
        var proofAttached by remember { mutableStateOf(false) }

        Dialog(onDismissRequest = { showReportDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("🛡️", fontSize = 22.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text("১০০% গোপনীয় সোশ্যাল পোস্ট তৈরি করুন", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF8C1D1D))
                                    Text("কোনো ব্যাক্তিগত ডেটা বাহিরে পাঠানো হবে না", fontSize = 10.sp, color = Color.Gray)
                                }
                            }
                            IconButton(onClick = { showReportDialog = false }) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = null)
                            }
                        }
                        Divider(modifier = Modifier.padding(vertical = 4.dp))
                    }

                    item {
                        Text("দেশ নির্বাচন করুন:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(PublicBribeExposureRegistry.countriesList) { (flag, name) ->
                                val isSel = selectedCountry.second == name
                                FilterChip(
                                    selected = isSel,
                                    onClick = { selectedCountry = Pair(flag, name) },
                                    label = { Text("$flag $name", fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFF8C1D1D), selectedLabelColor = Color.White)
                                )
                            }
                        }
                    }

                    item {
                        OutlinedTextField(
                            value = officerName,
                            onValueChange = { officerName = it },
                            label = { Text("অভিযুক্ত কর্মকর্তার নাম (জানা থাকলে)", fontSize = 11.sp) },
                            placeholder = { Text("যেমন: রফিকুল ইসলাম", fontSize = 11.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = designation,
                            onValueChange = { designation = it },
                            label = { Text("তার পদবি / পদবী", fontSize = 11.sp) },
                            placeholder = { Text("যেমন: কানুনগো / এসআই / ক্লার্ক", fontSize = 11.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }

                    item {
                        Text("মন্ত্রণালয় / বিভাগ নির্বাচন করুন:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(PublicBribeExposureRegistry.bdMinistriesList) { m ->
                                val isSel = selectedMinistry == m
                                FilterChip(
                                    selected = isSel,
                                    onClick = { selectedMinistry = m },
                                    label = { Text(m, fontSize = 10.sp) },
                                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFF8C1D1D), selectedLabelColor = Color.White)
                                )
                            }
                        }
                    }

                    item {
                        OutlinedTextField(
                            value = officeName,
                            onValueChange = { officeName = it },
                            label = { Text("দপ্তর বা অফিসের নাম", fontSize = 11.sp) },
                            placeholder = { Text("যেমন: উপজেলা ভূমি অফিস / মডেল থানা", fontSize = 11.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }

                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = thanaName,
                                onValueChange = { thanaName = it },
                                label = { Text("উপজেলা / থানা", fontSize = 11.sp) },
                                placeholder = { Text("যেমন: সাভার / কোতোয়ালী", fontSize = 11.sp) },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = amountText,
                                onValueChange = { amountText = it },
                                label = { Text("ঘুষের পরিমাণ (টাকা)", fontSize = 11.sp) },
                                placeholder = { Text("যেমন: 50000", fontSize = 11.sp) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        }
                    }

                    item {
                        OutlinedTextField(
                            value = descriptionText,
                            onValueChange = { descriptionText = it },
                            label = { Text("ঘটনা & হয়রানির সংক্ষিপ্ত বর্ণনা", fontSize = 11.sp) },
                            placeholder = { Text("কী কাজের বিনিময়ে কত টাকা ঘুষ চাওয়া হয়েছিল...", fontSize = 11.sp) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp)
                        )
                    }

                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { proofAttached = !proofAttached }
                        ) {
                            Checkbox(checked = proofAttached, onCheckedChange = { proofAttached = it })
                            Text("আমার কাছে ছবি/অডিও/নথি প্রমাণের সত্যতা রয়েছে", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    item {
                        Button(
                            onClick = {
                                val amt = amountText.toLongOrNull() ?: 0L
                                if (descriptionText.isBlank()) {
                                    Toast.makeText(context, "অনুগ্রহ করে ঘটনার বর্ণনা লিখুন", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                PublicBribeExposureRegistry.submitNewReport(
                                    countryFlag = selectedCountry.first,
                                    countryName = selectedCountry.second,
                                    officerName = officerName,
                                    designation = designation,
                                    ministry = selectedMinistry,
                                    office = officeName,
                                    district = selectedDistrict,
                                    thana = thanaName,
                                    amountBdt = amt,
                                    description = descriptionText,
                                    hasProof = proofAttached
                                )
                                showReportDialog = false
                                Toast.makeText(context, "ঘুষের পোস্ট সোশ্যাল ওয়ালে যুক্ত করা হয়েছে!", Toast.LENGTH_LONG).show()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8C1D1D)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Public, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("সোশ্যাল ফিডে প্রকাশ করুন (Publish to Feed)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SocialFeedBribeCard(
    report: BribeReport,
    onUpvote: () -> Unit,
    onShare: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Anonymous Author Profile Avatar & Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF8C1D1D).copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🛡️", fontSize = 22.sp)
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "গোপন প্রতিবাদকারী",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFF8C1D1D).copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "ANONYMOUS",
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF8C1D1D),
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Text(
                            text = "${report.reportedTimeAgo} • 🔒 কেবলমাত্র অ্যাপের সোশ্যাল ফিডে",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Country Flag & Country Name Badge
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(report.countryFlag, fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(report.countryNameBn, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Officer & Ministry Details Info Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.AccountBalance, contentDescription = null, tint = Color(0xFF8C1D1D), modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "মন্ত্রণালয়/বিভাগ: ${report.ministry}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF8C1D1D)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "অভিযুক্ত কর্মকর্তা: ${report.officerName} (${report.designation})",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = "দপ্তর & স্থান: ${report.departmentOffice}, ${report.upazilaThana}, ${report.district}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Bribe Amount Banner
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFF3B1212)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("💸", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("দাবিকৃত অনৈতিক ঘুষের পরিমাণ:", fontSize = 11.sp, color = Color.White.copy(alpha = 0.9f))
                    }

                    Text(
                        text = "৳${report.bribeAmountBdt} টাকা",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFFFF5252)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Post Description Story
            Text(
                text = report.incidentDescription,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 17.sp
            )

            if (report.verifiedProofAttached) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Attachment, contentDescription = null, tint = TeaLeafGreen, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("অডিও/ছবি/প্রমাণপত্র সোশ্যাল পোস্টের সাথে সংযুক্ত রয়েছে", fontSize = 10.sp, color = TeaLeafGreen, fontWeight = FontWeight.Bold)
                }
            }

            Divider(modifier = Modifier.padding(vertical = 10.dp))

            // Social Media Action Bar (Upvote, Comment, Share Externally)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(onClick = onUpvote) {
                        Icon(imageVector = Icons.Default.ThumbUp, contentDescription = null, modifier = Modifier.size(15.dp), tint = Color(0xFF8C1D1D))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("প্রতিবাদ (${report.upvotesCount})", fontSize = 11.sp, color = Color(0xFF8C1D1D))
                    }

                    TextButton(onClick = { }) {
                        Icon(imageVector = Icons.Default.ChatBubbleOutline, contentDescription = null, modifier = Modifier.size(15.dp), tint = Color.Gray)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("মন্তব্য (${report.commentsCount})", fontSize = 11.sp, color = Color.Gray)
                    }
                }

                // External Share Button Only
                Button(
                    onClick = onShare,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8C1D1D)),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(13.dp), tint = Color.White)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("বাইরে শেয়ার করুন", fontSize = 10.sp, color = Color.White)
                }
            }
        }
    }
}
