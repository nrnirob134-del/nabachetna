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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.WalletRechargeRequest
import com.example.ui.viewmodel.PageBookViewModel

@Composable
fun AdminWalletRechargesTab(
    rechargeRequests: List<WalletRechargeRequest>,
    viewModel: PageBookViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedFilter by remember { mutableStateOf("PENDING") } // "ALL", "PENDING", "APPROVED", "REJECTED"
    var adminNoteInputs by remember { mutableStateOf(mutableMapOf<Int, String>()) }

    val filteredRequests = remember(rechargeRequests, selectedFilter) {
        when (selectedFilter) {
            "PENDING" -> rechargeRequests.filter { it.status == "PENDING" }
            "APPROVED" -> rechargeRequests.filter { it.status == "APPROVED" }
            "REJECTED" -> rechargeRequests.filter { it.status == "REJECTED" }
            else -> rechargeRequests
        }
    }

    val pendingCount = rechargeRequests.count { it.status == "PENDING" }
    val approvedCount = rechargeRequests.count { it.status == "APPROVED" }
    val rejectedCount = rechargeRequests.count { it.status == "REJECTED" }
    val totalApprovedAmount = rechargeRequests.filter { it.status == "APPROVED" }.sumOf { it.amount }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Security Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F382A)),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AccountBalanceWallet,
                                contentDescription = null,
                                tint = Color(0xFF81C784),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "ওয়ালেট রিচার্জ আবেদন ও ম্যানুয়াল ভেরিফিকেশন",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (pendingCount > 0) Color.Red else Color(0xFF81C784).copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = if (pendingCount > 0) "$pendingCount টি অপেক্ষমাণ" else "সব ক্লিয়ার",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "🔒 অ্যান্টি-হ্যাক সিকিউরিটি: ইউজার রিচার্জ জমা দিলেই ব্যালেন্স যোগ হয় না। এডমিন নিজ বিকাশ/নগদ/ব্যাংক স্টেটমেন্টের সাথে প্রেরক নম্বর ও TrxID মিলিয়ে 'অনুমোদন করুন' ক্লিক করলে SHA-256 ক্রিপ্টোগ্রাফিক লেজারের মাধ্যমে ইউজারের ওয়ালেটে টাকা যুক্ত হবে।",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1B4D3E))
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                horizontalAlignment = Alignment.CenterVertically
                            ) {
                                Text("অপেক্ষমাণ", fontSize = 10.sp, color = Color.White.copy(alpha = 0.7f))
                                Text("$pendingCount টি", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFB74D))
                            }
                        }

                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1B4D3E))
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                horizontalAlignment = Alignment.CenterVertically
                            ) {
                                Text("অনুমোদিত রিচার্জ", fontSize = 10.sp, color = Color.White.copy(alpha = 0.7f))
                                Text("৳ ${String.format("%.0f", totalApprovedAmount)}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF81C784))
                            }
                        }

                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1B4D3E))
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                horizontalAlignment = Alignment.CenterVertically
                            ) {
                                Text("বাতিলকৃত", fontSize = 10.sp, color = Color.White.copy(alpha = 0.7f))
                                Text("$rejectedCount টি", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE57373))
                            }
                        }
                    }
                }
            }
        }

        // Filter Bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedFilter == "PENDING",
                    onClick = { selectedFilter = "PENDING" },
                    label = { Text("অপেক্ষমাণ ($pendingCount)") },
                    leadingIcon = {
                        if (selectedFilter == "PENDING") {
                            Icon(imageVector = Icons.Default.HourglassTop, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    }
                )

                FilterChip(
                    selected = selectedFilter == "APPROVED",
                    onClick = { selectedFilter = "APPROVED" },
                    label = { Text("অনুমোদিত ($approvedCount)") },
                    leadingIcon = {
                        if (selectedFilter == "APPROVED") {
                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    }
                )

                FilterChip(
                    selected = selectedFilter == "REJECTED",
                    onClick = { selectedFilter = "REJECTED" },
                    label = { Text("বাতিল ($rejectedCount)") },
                    leadingIcon = {
                        if (selectedFilter == "REJECTED") {
                            Icon(imageVector = Icons.Default.Cancel, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    }
                )

                FilterChip(
                    selected = selectedFilter == "ALL",
                    onClick = { selectedFilter = "ALL" },
                    label = { Text("সব") }
                )
            }
        }

        if (filteredRequests.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Outlined.CheckCircleOutline,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "কোনো রিচার্জ আবেদন পাওয়া যায়নি",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }

        // List of Requests
        items(filteredRequests, key = { it.id }) { req ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    // Header Status Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = "#REQ-${req.id}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        val (statusText, statusBg, statusColor) = when (req.status) {
                            "APPROVED" -> Triple("✅ অনুমোদিত ও ব্যালেন্সে যুক্ত", Color(0xFF2E7D32).copy(alpha = 0.15f), Color(0xFF2E7D32))
                            "REJECTED" -> Triple("❌ বাতিলকৃত", Color(0xFFD32F2F).copy(alpha = 0.15f), Color(0xFFD32F2F))
                            else -> Triple("⏳ যাচাইয়ের জন্য অপেক্ষমাণ", Color(0xFFF57F17).copy(alpha = 0.15f), Color(0xFFE65100))
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = statusBg
                        ) {
                            Text(
                                text = statusText,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = statusColor,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // User Info Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = req.userAvatar,
                            contentDescription = req.userName,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = req.userName,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "ইউজার ID: #${req.userId} • ${req.userEmailOrPhone}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "৳ ${String.format("%.2f", req.amount)}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF2E7D32)
                            )
                            Text(
                                text = req.requestedTimestamp,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Payment Method & Sender Phone Details Table Box
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        border = androidx.compose.foundation.BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("পেমেন্ট মাধ্যম:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(req.paymentGateway, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("প্রেরক মোবাইল নম্বর:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(req.senderPhoneNumber, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1976D2))
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("ট্রানজেকশন আইডি (TrxID):", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(req.transactionId, fontSize = 12.sp, fontWeight = FontWeight.Black, color = Color(0xFFD81B60))
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("এডমিনের রিসিভার নম্বর:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(req.receiverAdminNumber, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("ক্রিপ্টোগ্রাফিক চেকসাম:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.VerifiedUser,
                                        contentDescription = null,
                                        tint = Color(0xFF2E7D32),
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "SHA-256 Validated",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF2E7D32)
                                    )
                                }
                            }
                        }
                    }

                    if (req.adminNotes.isNotBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "এডমিন নোট: ${req.adminNotes}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        )
                    }

                    // Action Controls if PENDING
                    if (req.status == "PENDING") {
                        Spacer(modifier = Modifier.height(10.dp))

                        val currentNote = adminNoteInputs[req.id] ?: ""
                        OutlinedTextField(
                            value = currentNote,
                            onValueChange = { newNote ->
                                adminNoteInputs = adminNoteInputs.toMutableMap().apply { put(req.id, newNote) }
                            },
                            placeholder = { Text("এডমিন মন্তব্য / নোট (ঐচ্ছিক)", fontSize = 12.sp) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    viewModel.approveWalletRechargeRequest(req.id, currentNote) { success, msg ->
                                        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                                    }
                                },
                                modifier = Modifier.weight(1.2f),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("নিশ্চিত করুন ও টাকা যুক্ত করুন", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = {
                                    viewModel.rejectWalletRechargeRequest(req.id, currentNote.ifBlank { "পেমেন্ট রেকর্ড পাওয়া যায়নি" }) { success, msg ->
                                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = Modifier.weight(0.8f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color.Red),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = null, tint = Color.Red, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("বাতিল", fontSize = 12.sp, color = Color.Red, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
