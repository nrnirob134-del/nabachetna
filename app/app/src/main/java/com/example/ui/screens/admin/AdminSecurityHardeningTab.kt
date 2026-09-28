package com.example.ui.screens.admin

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.TeaLeafGreen

@Composable
fun AdminSecurityHardeningTab() {
    val context = LocalContext.current

    var admin2faEnabled by remember { mutableStateOf(true) }
    var autoLockEnabled by remember { mutableStateOf(true) }
    var ipWhitelistEnabled by remember { mutableStateOf(true) }
    var emergencyPanicMode by remember { mutableStateOf(false) }

    var appRootDetection by remember { mutableStateOf(true) }
    var screenCaptureBlock by remember { mutableStateOf(true) }
    var biometricEnforced by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Column {
            Text("🛡️ এডমিন প্যানেল ও ইউজার অ্যাপ সিকিউরিটি হার্ডেনিং", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TeaLeafGreen)
            Text("প্ল্যাটফর্মের সর্বোচ্চ সুরক্ষা নিশ্চিত করতে এডমিন ও ক্লায়েন্ট সাইড ডিফেন্স কনফিগারেশন", fontSize = 11.sp, color = Color.Gray)
        }

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Emergency Panic Freeze Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = if (emergencyPanicMode) Color(0xFFFFEBEE) else Color(0xFFFFF3E0)),
                    border = BorderStroke(1.dp, if (emergencyPanicMode) Color.Red else Color(0xFFFF9800))
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (emergencyPanicMode) Icons.Default.Warning else Icons.Default.Security,
                                contentDescription = null,
                                tint = if (emergencyPanicMode) Color.Red else Color(0xFFEF6C00)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (emergencyPanicMode) "🚨 ইমার্জেন্সি প্যানিক ফ্রিজ সক্রিয়!" else "ইমার্জেন্সি প্ল্যাটফর্ম প্যানিক বাটন",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = if (emergencyPanicMode) Color.Red else Color(0xFFEF6C00)
                            )
                        }
                        Text(
                            text = "হ্যাক বা সাইবার আক্রমণের আশঙ্কা দেখা দিলে এক ক্লিকে প্ল্যাটফর্মের সমস্ত পেমেন্ট, উইথড্রল ও ওয়ালেট ট্রানজ্যাকশন তাৎক্ষণিকভাবে লক করে দিতে পারেন।",
                            fontSize = 11.sp,
                            color = Color.DarkGray
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = {
                                    emergencyPanicMode = !emergencyPanicMode
                                    val msg = if (emergencyPanicMode) "🚨 ইমার্জেন্সি প্যানিক মোড চালু! সমস্ত ট্রানজ্যাকশন লক করা হয়েছে।" else "প্যানিক মোড বন্ধ করা হয়েছে।"
                                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = if (emergencyPanicMode) Color.Red else Color(0xFFEF6C00))
                            ) {
                                Text(if (emergencyPanicMode) "প্যানিক ফ্রিজ ডিসেবল করুন" else "🚨 প্যানিক ফ্রিজ অন করুন", color = Color.White, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }

            // Admin Panel Security Settings
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, TeaLeafGreen.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("🔒 এডমিন প্যানেল হার্ডেনিং ডিফেন্স", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TeaLeafGreen)

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("এডমিন টু-ফ্যাক্টর অথেন্টিকেশন (2FA PIN)", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                Text("গুরুত্বপূর্ণ এডমিন কাজের আগে সেকেন্ডারি পিন আবশ্যক", fontSize = 10.sp, color = Color.Gray)
                            }
                            Switch(checked = admin2faEnabled, onCheckedChange = { admin2faEnabled = it })
                        }

                        Divider(color = Color.LightGray.copy(alpha = 0.5f))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("অটো সেশন লক (3 মিনিট নিষ্ক্রিয়তা)", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                Text("নিষ্ক্রিয় থাকলে স্বয়ংক্রিয়ভাবে এডমিন লগআউট হয়ে যাবে", fontSize = 10.sp, color = Color.Gray)
                            }
                            Switch(checked = autoLockEnabled, onCheckedChange = { autoLockEnabled = it })
                        }

                        Divider(color = Color.LightGray.copy(alpha = 0.5f))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("এডমিন আইপি হোয়াইটলিস্টিং (IP Whitelist)", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                Text("শুধুমাত্র নির্ধারিত বিশ্বস্ত আইপি থেকে এডমিন প্যানেল অ্যাক্সেস", fontSize = 10.sp, color = Color.Gray)
                            }
                            Switch(checked = ipWhitelistEnabled, onCheckedChange = { ipWhitelistEnabled = it })
                        }
                    }
                }
            }

            // User App Security Settings
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, TeaLeafGreen.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("🛡️ ইউজার অ্যাপ সিকিউরিটি ও অ্যান্টি-টেম্পারিং", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TeaLeafGreen)

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("রুট ও জেইলব্রেক ডিটেকশন (Root Detection)", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                Text("রুট করা বা মডিফাইড ডিভাইসে অ্যাপ চলতে বাধা দেওয়া", fontSize = 10.sp, color = Color.Gray)
                            }
                            Switch(checked = appRootDetection, onCheckedChange = { appRootDetection = it })
                        }

                        Divider(color = Color.LightGray.copy(alpha = 0.5f))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("স্ক্রিনশট ও স্ক্রিন রেকর্ডিং প্রটেকশন", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                Text("ওয়ালেট ও সংবেদনশীল স্ক্রিনে স্ক্রিনশট ব্লক করা", fontSize = 10.sp, color = Color.Gray)
                            }
                            Switch(checked = screenCaptureBlock, onCheckedChange = { screenCaptureBlock = it })
                        }

                        Divider(color = Color.LightGray.copy(alpha = 0.5f))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("বায়োমেট্রিক ও পিন এনফোর্সমেন্ট", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                Text("লেনদেনের সময় ফিঙ্গারপ্রিন্ট বা ফেস আইডি বাধ্যতমূলক", fontSize = 10.sp, color = Color.Gray)
                            }
                            Switch(checked = biometricEnforced, onCheckedChange = { biometricEnforced = it })
                        }
                    }
                }
            }
        }
    }
}
