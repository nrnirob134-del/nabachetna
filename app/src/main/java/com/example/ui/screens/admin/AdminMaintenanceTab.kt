package com.example.ui.screens.admin

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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

@Composable
fun AdminMaintenanceTab(
    viewModel: PageBookViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val maintenanceConfig by viewModel.maintenanceConfig.collectAsStateWithLifecycle()

    var isMaintenanceMode by remember(maintenanceConfig) { mutableStateOf(maintenanceConfig.isMaintenanceMode) }
    var maintenanceMsg by remember(maintenanceConfig) { mutableStateOf(maintenanceConfig.maintenanceMessage) }
    var readOnlyMode by remember(maintenanceConfig) { mutableStateOf(maintenanceConfig.readOnlyMode) }
    var emergencyLockdown by remember(maintenanceConfig) { mutableStateOf(maintenanceConfig.emergencyLockdown) }
    var estimatedUptime by remember(maintenanceConfig) { mutableStateOf(maintenanceConfig.estimatedUptime) }

    var cleanResult by remember { mutableStateOf<String?>(null) }
    var isCleaning by remember { mutableStateOf(false) }

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
                colors = CardDefaults.cardColors(containerColor = if (isMaintenanceMode || emergencyLockdown) Color(0xFFC62828) else Color(0xFF263238)),
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
                                text = "⚡ মেইনটেন্যান্স ও ইমার্জেন্সি লকডাউন",
                                color = Color.White,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "সার্ভার রক্ষণাবেক্ষণ মোড ও তাৎক্ষণিক আপতকালীন সুরক্ষা",
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 12.sp
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isMaintenanceMode || emergencyLockdown) Color(0xFFFFD54F) else Color(0xFF2E7D32)
                        ) {
                            Text(
                                text = if (isMaintenanceMode) "মেইনটেন্যান্স সক্রিয়" else "সার্ভার অনলাইন",
                                color = if (isMaintenanceMode || emergencyLockdown) Color.Black else Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        // Maintenance Mode Toggle Card
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
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "🛠️ মেইনটেন্যান্স মোড (Maintenance Mode)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(text = "চালু থাকলে সাধারণ ইউজাররা পুরো অ্যাপ জুড়ে মেইনটেন্যান্স নোটিশ দেখতে পাবেন", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = isMaintenanceMode,
                            onCheckedChange = { isMaintenanceMode = it }
                        )
                    }

                    if (isMaintenanceMode) {
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = maintenanceMsg,
                            onValueChange = { maintenanceMsg = it },
                            label = { Text("মেইনটেন্যান্স বার্তা") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(text = "সম্ভাব্য আপটাইম / সময়সীমা:", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf("১৫ মিনিট", "৩০ মিনিট", "১ ঘণ্টা", "২ ঘণ্টা").forEach { time ->
                                val isSel = estimatedUptime == time
                                OutlinedButton(
                                    onClick = { estimatedUptime = time },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        containerColor = if (isSel) Color(0xFFC62828) else Color.Transparent,
                                        contentColor = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface
                                    ),
                                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                                ) {
                                    Text(text = time, fontSize = 10.sp, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = {
                            viewModel.updateMaintenanceConfig(
                                isMaintenance = isMaintenanceMode,
                                message = maintenanceMsg,
                                readOnly = readOnlyMode,
                                lockdown = emergencyLockdown,
                                uptime = estimatedUptime
                            )
                            Toast.makeText(context, "মেইনটেন্যান্স সেটিংস সফলভাবে আপডেট হয়েছে!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("সেটিংস সংরক্ষণ করুন", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }

        // Additional Lockdown Toggles
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
                    Text(text = "🔒 বিশেষ মোড ও জরুরি সুরক্ষা", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    // Read-only Mode
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "রিড-অনলি মোড (Read-Only Mode)", fontWeight = FontWeight.Medium, fontSize = 13.sp)
                            Text(text = "ইউজাররা পোস্ট দেখতে পারবে কিন্তু নতুন পোস্ট বা কমেন্ট করা বন্ধ থাকবে", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = readOnlyMode,
                            onCheckedChange = {
                                readOnlyMode = it
                                viewModel.updateMaintenanceConfig(isMaintenanceMode, maintenanceMsg, it, emergencyLockdown, estimatedUptime)
                            }
                        )
                    }

                    Divider(modifier = Modifier.padding(vertical = 10.dp))

                    // Emergency Lockdown
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "🚨 ইমার্জেন্সি ফিন্যান্সিয়াল কিলসুইচ", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFFC62828))
                            Text(text = "সকল প্রকার উইথড্রল, নতুন পেজ মনিটাইজেশন ও ওয়ালেট ট্রানজ্যাকশন সাময়িক স্থগিত", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = emergencyLockdown,
                            onCheckedChange = {
                                emergencyLockdown = it
                                viewModel.updateMaintenanceConfig(isMaintenanceMode, maintenanceMsg, readOnlyMode, it, estimatedUptime)
                            }
                        )
                    }
                }
            }
        }

        // Database Optimizer Card
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Storage, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "ডাটাবেস ভ্যাকিউম ও স্প্যাম ক্লিনার", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "টেম্পোরারি ফাইল, স্প্যাম কমেন্ট ট্র্যাশ ও অব্যবহৃত ক্যাশ পরিষ্কার করে ডাটাবেস পারফর্ম্যান্স ৩০% বৃদ্ধি করুন।",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            isCleaning = true
                            cleanResult = viewModel.runDatabaseMaintenanceClean()
                            isCleaning = false
                            Toast.makeText(context, "ডাটাবেস ক্লিনআপ সফল!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00695C)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.CleaningServices, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("ডাটাবেস অপটিমাইজ করুন (Run Clean)", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }

                    cleanResult?.let { msg ->
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFE0F2F1),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "✓ $msg",
                                color = Color(0xFF004D40),
                                fontSize = 11.sp,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
