package com.example.ui.screens.admin

import android.widget.Toast
import androidx.compose.animation.*
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.data.model.WithdrawalRequest
import com.example.ui.viewmodel.PageBookViewModel
import java.util.Locale

@Composable
fun AdminWithdrawalsTab(
    viewModel: PageBookViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val withdrawalRequests by viewModel.withdrawalRequests.collectAsStateWithLifecycle()

    var selectedFilter by remember { mutableStateOf("ALL") } // "ALL", "PENDING", "APPROVED", "REJECTED"
    var selectedRequestForApproval by remember { mutableStateOf<WithdrawalRequest?>(null) }
    var selectedRequestForRejection by remember { mutableStateOf<WithdrawalRequest?>(null) }
    var txnIdInput by remember { mutableStateOf("") }
    var rejectionReasonInput by remember { mutableStateOf("") }

    val filteredList = remember(withdrawalRequests, selectedFilter) {
        when (selectedFilter) {
            "PENDING" -> withdrawalRequests.filter { it.status == "PENDING" }
            "APPROVED" -> withdrawalRequests.filter { it.status == "APPROVED" }
            "REJECTED" -> withdrawalRequests.filter { it.status == "REJECTED" }
            else -> withdrawalRequests
        }
    }

    val pendingCount = withdrawalRequests.count { it.status == "PENDING" }
    val pendingAmountUsd = withdrawalRequests.filter { it.status == "PENDING" }.sumOf { it.amountUsd }
    val approvedAmountUsd = withdrawalRequests.filter { it.status == "APPROVED" }.sumOf { it.amountUsd }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(vertical = 14.dp)
    ) {
        // Summary Header Cards
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1B5E20)),
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
                                text = "💰 পে-আউট ও উইথড্রল হাব",
                                color = Color.White,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "ক্রিয়েটরদের অর্জিত রেভিনিউ উত্তোলন অনুমোদন",
                                color = Color(0xFFA5D6A7),
                                fontSize = 12.sp
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF2E7D32)
                        ) {
                            Text(
                                text = "$pendingCount টি পেন্ডিং",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            color = Color.Black.copy(alpha = 0.25f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "পেন্ডিং উইথড্রল",
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontSize = 11.sp
                                )
                                Text(
                                    text = "$${String.format(Locale.US, "%.2f", pendingAmountUsd)}",
                                    color = Color(0xFFFFD54F),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "৳${String.format(Locale.US, "%.0f", pendingAmountUsd * 120)} BDT",
                                    color = Color.White.copy(alpha = 0.6f),
                                    fontSize = 10.sp
                                )
                            }
                        }

                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            color = Color.Black.copy(alpha = 0.25f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "পরিশোধিত মোট",
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontSize = 11.sp
                                )
                                Text(
                                    text = "$${String.format(Locale.US, "%.2f", approvedAmountUsd)}",
                                    color = Color(0xFF81C784),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "৳${String.format(Locale.US, "%.0f", approvedAmountUsd * 120)} BDT",
                                    color = Color.White.copy(alpha = 0.6f),
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Filter Chips
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val filters = listOf(
                    "ALL" to "সকল রিকোয়েস্ট (${withdrawalRequests.size})",
                    "PENDING" to "পেন্ডিং ($pendingCount)",
                    "APPROVED" to "অনুমোদিত (${withdrawalRequests.count { it.status == "APPROVED" }})",
                    "REJECTED" to "বাতিলকৃত (${withdrawalRequests.count { it.status == "REJECTED" }})"
                )
                items(filters) { (key, label) ->
                    val isSelected = selectedFilter == key
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedFilter = key },
                        label = {
                            Text(
                                text = label,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF2E7D32),
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }

        // Withdrawal Request List
        if (filteredList.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF2E7D32),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "কোনো রিকোয়েস্ট নেই",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "এই ফিল্টারে বর্তমানে কোনো উইথড্রল রিকোয়েস্ট জমা নেই।",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(filteredList, key = { it.id }) { request ->
                WithdrawalRequestCard(
                    request = request,
                    onApproveClick = {
                        selectedRequestForApproval = request
                        txnIdInput = "TXN-${request.paymentMethod.uppercase()}-${(100000..999999).random()}"
                    },
                    onRejectClick = {
                        selectedRequestForRejection = request
                        rejectionReasonInput = "অ্যাকাউন্ট তথ্যে অসঙ্গতি অথবা ন্যূনতম ভিউ শর্ত পূরণ হয়নি।"
                    },
                    onCopyAccount = {
                        clipboardManager.setText(AnnotatedString(request.accountNumber))
                        Toast.makeText(context, "অ্যাকাউন্ট নম্বর কপি করা হয়েছে: ${request.accountNumber}", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }

    // Approval Dialog
    selectedRequestForApproval?.let { req ->
        AlertDialog(
            onDismissRequest = { selectedRequestForApproval = null },
            icon = {
                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(36.dp))
            },
            title = {
                Text(text = "উইথড্রল অনুমোদন করুন", fontWeight = FontWeight.Bold, fontSize = 17.sp)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "${req.userName} (${req.pageName})-কে $${req.amountUsd} (৳${req.amountBdt}) পরিশোধ করতে চাচ্ছেন।",
                        fontSize = 13.sp
                    )
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFE8F5E9),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(text = "মাধ্যম: ${req.paymentMethod}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF1B5E20))
                            Text(text = "অ্যাকাউন্ট: ${req.accountNumber}", fontSize = 12.sp, color = Color(0xFF2E7D32))
                        }
                    }

                    OutlinedTextField(
                        value = txnIdInput,
                        onValueChange = { txnIdInput = it },
                        label = { Text("ট্রানজ্যাকশন আইডি (Txn ID)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (txnIdInput.isNotBlank()) {
                            viewModel.approveWithdrawal(req.id, txnIdInput.trim())
                            selectedRequestForApproval = null
                            Toast.makeText(context, "উইথড্রল সফলভাবে অনুমোদিত হয়েছে!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                ) {
                    Text("অনুমোদন ও পে সম্পন্ন")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedRequestForApproval = null }) {
                    Text("বাতিল")
                }
            }
        )
    }

    // Rejection Dialog
    selectedRequestForRejection?.let { req ->
        AlertDialog(
            onDismissRequest = { selectedRequestForRejection = null },
            icon = {
                Icon(imageVector = Icons.Default.Cancel, contentDescription = null, tint = Color(0xFFD32F2F), modifier = Modifier.size(36.dp))
            },
            title = {
                Text(text = "উইথড্রল বাতিল করুন", fontWeight = FontWeight.Bold, fontSize = 17.sp)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "${req.userName}-এর $${req.amountUsd} উইথড্রল রিকোয়েস্ট বাতিল করার কারণ লিখুন:",
                        fontSize = 13.sp
                    )
                    OutlinedTextField(
                        value = rejectionReasonInput,
                        onValueChange = { rejectionReasonInput = it },
                        label = { Text("বাতিলের কারণ") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.rejectWithdrawal(req.id, rejectionReasonInput.trim())
                        selectedRequestForRejection = null
                        Toast.makeText(context, "উইথড্রল বাতিল করা হয়েছে।", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                ) {
                    Text("বাতিল নিশ্চিত করুন")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedRequestForRejection = null }) {
                    Text("ফিরে যান")
                }
            }
        )
    }
}

@Composable
fun WithdrawalRequestCard(
    request: WithdrawalRequest,
    onApproveClick: () -> Unit,
    onRejectClick: () -> Unit,
    onCopyAccount: () -> Unit
) {
    val methodColor = when (request.paymentMethod.lowercase()) {
        "bkash" -> Color(0xFFE2136E)
        "nagad" -> Color(0xFFF7941D)
        "rocket" -> Color(0xFF8C3494)
        else -> Color(0xFF1976D2)
    }

    val statusColor = when (request.status) {
        "APPROVED" -> Color(0xFF2E7D32)
        "REJECTED" -> Color(0xFFD32F2F)
        else -> Color(0xFFE65100)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        model = request.userAvatar,
                        contentDescription = null,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = request.userName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "পেজ: ${request.pageName}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = statusColor.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = when (request.status) {
                            "APPROVED" -> "✓ অনুমোদিত"
                            "REJECTED" -> "✕ বাতিলকৃত"
                            else -> "⏳ অপেক্ষমাণ"
                        },
                        color = statusColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "উত্তোলনের পরিমাণ",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "$${request.amountUsd}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1B5E20)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "(৳${String.format(Locale.US, "%.0f", request.amountBdt)} BDT)",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = methodColor
                    ) {
                        Text(
                            text = request.paymentMethod,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onCopyAccount() }
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountBalanceWallet,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "অ্যাকাউন্ট: ${request.accountNumber}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(14.dp)
                    )
                }

                Text(
                    text = request.requestedDate,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (request.status == "APPROVED" && request.transactionId != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFFE8F5E9),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "ট্রানজ্যাকশন আইডি: ${request.transactionId}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF2E7D32),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            } else if (request.status == "REJECTED" && request.rejectionReason != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFFFFEBEE),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "বাতিলের কারণ: ${request.rejectionReason}",
                        fontSize = 11.sp,
                        color = Color(0xFFD32F2F),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            if (request.status == "PENDING") {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onRejectClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFD32F2F))
                    ) {
                        Text(text = "বাতিল করুন", fontSize = 12.sp)
                    }

                    Button(
                        onClick = onApproveClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                    ) {
                        Text(text = "অনুমোদন করুন", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
