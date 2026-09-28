package com.example.ui.screens.admin

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.database.AppDatabase
import com.example.data.model.AdminSponsoredAd
import com.example.data.model.LanguageCertificate
import com.example.data.repository.LearningRepository
import com.example.data.service.AdManagerRegistry
import com.example.ui.theme.TeaLeafGreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAdManagerTab(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val database = remember { AppDatabase.getDatabase(context) }
    val learningRepo = remember { LearningRepository(database.learningDao()) }

    val adminCustomAds by learningRepo.allSponsoredAds.collectAsStateWithLifecycle(emptyList())
    val certificates by learningRepo.allCertificates.collectAsStateWithLifecycle(emptyList())

    var selectedSubSection by remember { mutableIntStateOf(0) } // 0: Custom Ads, 1: Certificate Revenue
    var showCreateCustomAdDialog by remember { mutableStateOf(false) }

    val totalAdImpressions = remember(adminCustomAds) { adminCustomAds.sumOf { it.impressions } }
    val totalAdClicks = remember(adminCustomAds) { adminCustomAds.sumOf { it.clicks } }
    val totalAdRevenue = remember(adminCustomAds) { adminCustomAds.sumOf { it.revenueEarnedBdt } }

    val totalCertificatesSold = certificates.size
    val totalCertificateRevenue = remember(certificates) { certificates.sumOf { it.feeAmountBdt } }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        // Top Overview Banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("💰", fontSize = 22.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "এডমিন স্পন্সরড এডস ও আয় নিয়ন্ত্রণ",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "গুগল এড নির্ভরতা ছাড়া সরাসরি এডমিন আপলোড এড ও সার্টিফিকেট ফি",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                            )
                        }
                    }

                    if (selectedSubSection == 0) {
                        Button(
                            onClick = { showCreateCustomAdDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = TeaLeafGreen),
                            shape = RoundedCornerShape(20.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("নতুন এড", fontSize = 11.sp, color = Color.White)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Real-time Earnings Snapshot
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("স্পন্সর এড আয়", fontSize = 10.sp, color = Color.Gray)
                            Text("৳ ${totalAdRevenue.toInt()}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TeaLeafGreen)
                            Text("$totalAdImpressions ভিউ • $totalAdClicks ক্লিক", fontSize = 9.sp, color = Color.Gray)
                        }
                    }

                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("সার্টিফিকেট ফি আয়", fontSize = 10.sp, color = Color.Gray)
                            Text("৳ ${totalCertificateRevenue.toInt()}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFF9800))
                            Text("$totalCertificatesSold টি সার্টিফিকেট ইস্যু", fontSize = 9.sp, color = Color.Gray)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Sub Navigation Tabs
        TabRow(
            selectedTabIndex = selectedSubSection,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = TeaLeafGreen
        ) {
            Tab(
                selected = selectedSubSection == 0,
                onClick = { selectedSubSection = 0 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Campaign, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("কাস্টম স্পন্সর এডস (${adminCustomAds.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            )
            Tab(
                selected = selectedSubSection == 1,
                onClick = { selectedSubSection = 1 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.WorkspacePremium, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("সার্টিফিকেট সেলস (${certificates.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (selectedSubSection == 0) {
            // CUSTOM SPONSORED ADS LIST
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text(
                        text = "ভাষা শিক্ষায় পয়েন্ট পাওয়ার পূর্বে দেখানো সক্রিয় এডসমূহ:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                items(adminCustomAds, key = { it.id }) { ad ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(ad.sponsorLogoEmoji, fontSize = 20.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(ad.sponsorName, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        Text(
                                            "রিওয়ার্ড কাউন্টডাউন: ${ad.countdownSeconds}s • কাস্টম নন-গুগল এড",
                                            fontSize = 10.sp,
                                            color = TeaLeafGreen
                                        )
                                    }
                                }

                                Switch(
                                    checked = ad.isActive,
                                    onCheckedChange = {
                                        coroutineScope.launch {
                                            learningRepo.updateSponsoredAd(ad.copy(isActive = it))
                                            Toast.makeText(context, "এডের অবস্থা পরিবর্তন করা হয়েছে", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(ad.title, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text(ad.description, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2)

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "ইমপ্রেশন: ${ad.impressions} • ক্লিক: ${ad.clicks} • অর্জিত: ৳${ad.revenueEarnedBdt.toInt()}",
                                    fontSize = 10.sp,
                                    color = Color.Gray
                                )

                                IconButton(
                                    onClick = {
                                        coroutineScope.launch {
                                            learningRepo.deleteSponsoredAd(ad.id)
                                            Toast.makeText(context, "এড মুছে ফেলা হয়েছে", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                ) {
                                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // CERTIFICATE SALES & REVENUE DESK
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text(
                        text = "সার্টিফিকেট ফি পেমেন্ট ও ক্রেতা তালিকা (${certificates.size} জন):",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                items(certificates, key = { it.certificateId }) { cert ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = cert.userName,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "কোর্স: ${cert.languageName} • স্কোর: ${cert.totalPoints} পয়েন্ট",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "TrxID: ${cert.transactionId} (${cert.paymentMethod}) • ${cert.issueDate}",
                                    fontSize = 10.sp,
                                    color = Color.Gray
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Surface(
                                    color = Color(0xFF4CAF50).copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "৳ ${cert.feeAmountBdt.toInt()} BDT",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF2E7D32),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "ভেরিফাইড ✅",
                                    fontSize = 10.sp,
                                    color = Color(0xFF2E7D32),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Add Custom Sponsored Ad Dialog
    if (showCreateCustomAdDialog) {
        var sponsorName by remember { mutableStateOf("") }
        var sponsorLogo by remember { mutableStateOf("🏢") }
        var title by remember { mutableStateOf("") }
        var description by remember { mutableStateOf("") }
        var bannerUrl by remember { mutableStateOf("") }
        var targetUrl by remember { mutableStateOf("") }
        var ctaText by remember { mutableStateOf("অফারটি দেখুন ➔") }
        var countdownSec by remember { mutableStateOf("5") }

        Dialog(onDismissRequest = { showCreateCustomAdDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("📢 নতুন স্পন্সরড রিওয়ার্ড এড", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        IconButton(onClick = { showCreateCustomAdDialog = false }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = null)
                        }
                    }

                    OutlinedTextField(
                        value = sponsorName,
                        onValueChange = { sponsorName = it },
                        label = { Text("স্পন্সরের নাম", fontSize = 11.sp) },
                        placeholder = { Text("যেমন: British Council / EduWings BD", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("এডের শিরোনাম / অফার", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("এডের বিস্তারিত বিবরণ", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = bannerUrl,
                        onValueChange = { bannerUrl = it },
                        label = { Text("ব্যানার ছবির লিংক (Image URL)", fontSize = 11.sp) },
                        placeholder = { Text("https://...", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = targetUrl,
                            onValueChange = { targetUrl = it },
                            label = { Text("স্পন্সর ওয়েবসাইট লিংক", fontSize = 11.sp) },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = countdownSec,
                            onValueChange = { countdownSec = it },
                            label = { Text("কাউন্টডাউন (সেকেন্ড)", fontSize = 11.sp) },
                            modifier = Modifier.weight(0.7f),
                            singleLine = true
                        )
                    }

                    Button(
                        onClick = {
                            if (title.isBlank() || sponsorName.isBlank()) {
                                Toast.makeText(context, "স্পন্সরের নাম ও টাইটেল পূরণ করুন", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            coroutineScope.launch {
                                learningRepo.insertSponsoredAd(
                                    AdminSponsoredAd(
                                        sponsorName = sponsorName,
                                        sponsorLogoEmoji = sponsorLogo,
                                        title = title,
                                        description = description,
                                        bannerImageUrl = bannerUrl.ifBlank { "https://images.unsplash.com/photo-1523240795612-9a054b0db644?w=600" },
                                        targetUrl = targetUrl.ifBlank { "https://nabachetna.com" },
                                        ctaText = ctaText,
                                        countdownSeconds = countdownSec.toIntOrNull() ?: 5,
                                        isActive = true,
                                        category = "LANGUAGE_REWARD",
                                        revenueEarnedBdt = 500.0
                                    )
                                )
                                showCreateCustomAdDialog = false
                                Toast.makeText(context, "স্পন্সর এড সফলভাবে যুক্ত হয়েছে!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = TeaLeafGreen),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("এড চালু করুন (Publish Sponsored Ad)", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
