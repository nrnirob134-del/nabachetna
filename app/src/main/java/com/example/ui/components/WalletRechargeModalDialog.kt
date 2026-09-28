package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.User
import com.example.data.service.AdminPaymentGatewayInfo
import com.example.ui.theme.TeaLeafGreen
import com.example.ui.viewmodel.PageBookViewModel

@Composable
fun WalletRechargeModalDialog(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    viewModel: PageBookViewModel,
    currentUser: User?,
    modifier: Modifier = Modifier
) {
    if (!isOpen) return

    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val gateways by viewModel.officialPaymentGateways.collectAsStateWithLifecycle()
    val activeGateways = remember(gateways) { gateways.filter { it.isEnabled } }

    var selectedGateway by remember(activeGateways) {
        mutableStateOf(activeGateways.firstOrNull())
    }

    var inputAmount by remember { mutableStateOf("500") }
    var inputSenderAccount by remember { mutableStateOf("") }
    var inputTransactionId by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.90f)
                .clip(RoundedCornerShape(24.dp)),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = TeaLeafGreen.copy(alpha = 0.15f),
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.AccountBalanceWallet,
                                    contentDescription = null,
                                    tint = TeaLeafGreen
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "ওয়ালেট রিচার্জ ও গ্লোবাল পেমেন্ট",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "বর্তমান ব্যালেন্স: ৳${String.format("%.2f", currentUser?.walletBalance ?: 0.0)}",
                                fontSize = 12.sp,
                                color = TeaLeafGreen,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Real-time Dynamic Gateway Notice
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF1B4D3E).copy(alpha = 0.1f),
                    border = BorderStroke(0.5.dp, TeaLeafGreen.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.FlashOn,
                            contentDescription = null,
                            tint = TeaLeafGreen,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "লাইভ পেমেন্ট সিস্টেম: এডমিন প্যানেল থেকে কনফিগার করা সক্রিয় দেশীয় ও আন্তর্জাতিক পেমেন্ট মেথড স্বয়ংক্রিয়ভাবে এখানে প্রদর্শিত হচ্ছে।",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 15.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Gateway Selection
                    item {
                        Text(
                            text = "পেমেন্ট মাধ্যম নির্বাচন করুন:",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        if (activeGateways.isEmpty()) {
                            Text(
                                text = "বর্তমানে কোনো পেমেন্ট গেটওয়ে সক্রিয় নেই। অনুগ্রহ করে কিছুক্ষণ পর চেষ্টা করুন।",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.error
                            )
                        } else {
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(activeGateways) { gateway ->
                                    val isSelected = selectedGateway?.id == gateway.id
                                    OutlinedCard(
                                        modifier = Modifier
                                            .clickable { selectedGateway = gateway },
                                        shape = RoundedCornerShape(14.dp),
                                        colors = CardDefaults.outlinedCardColors(
                                            containerColor = if (isSelected) Color(gateway.colorHex).copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface
                                        ),
                                        border = BorderStroke(
                                            width = if (isSelected) 2.dp else 1.dp,
                                            color = if (isSelected) Color(gateway.colorHex) else MaterialTheme.colorScheme.outlineVariant
                                        )
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(gateway.iconEmoji, fontSize = 24.sp)
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = gateway.gatewayName.split(" ").firstOrNull() ?: gateway.gatewayName,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) Color(gateway.colorHex) else MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = if (gateway.isInternational) "🌍 গ্লোবাল" else "🇧🇩 লোকাল",
                                                fontSize = 9.sp,
                                                color = if (gateway.isInternational) Color(0xFF635BFF) else Color(0xFF2E7D32),
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Gateway Details Card
                    selectedGateway?.let { gateway ->
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(gateway.colorHex).copy(alpha = 0.08f)
                                ),
                                border = BorderStroke(1.dp, Color(gateway.colorHex).copy(alpha = 0.3f))
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(gateway.iconEmoji, fontSize = 20.sp)
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = gateway.gatewayName,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(gateway.colorHex)
                                            )
                                        }
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = if (gateway.isLiveMode) Color(0xFF2E7D32).copy(alpha = 0.2f) else Color.Gray.copy(alpha = 0.2f)
                                        ) {
                                            Text(
                                                text = if (gateway.isLiveMode) "🟢 লাইভ গেটওয়ে" else "🧪 টেস্ট মোড",
                                                fontSize = 10.sp,
                                                color = if (gateway.isLiveMode) Color(0xFF2E7D32) else Color.DarkGray,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    // Account / Merchant details with copy button
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = MaterialTheme.colorScheme.surface,
                                        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant)
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 12.dp, vertical = 8.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = if (gateway.isInternational) "মার্চেন্ট আইডি / অ্যাকাউন্ট / পাবলিক কী:" else "অফিশিয়াল অ্যাকাউন্ট নম্বর:",
                                                    fontSize = 10.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                                Text(
                                                    text = gateway.accountNumber,
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                            }
                                            IconButton(
                                                onClick = {
                                                    clipboardManager.setText(AnnotatedString(gateway.accountNumber))
                                                    Toast.makeText(context, "নম্বর কপি করা হয়েছে!", Toast.LENGTH_SHORT).show()
                                                },
                                                modifier = Modifier.size(34.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.ContentCopy,
                                                    contentDescription = "Copy",
                                                    tint = Color(gateway.colorHex),
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = "নির্দেশনা: ${gateway.instructions}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        lineHeight = 16.sp
                                    )
                                }
                            }
                        }

                        // Amount Input & Quick Chips
                        item {
                            Text(
                                text = "রিচার্জের পরিমাণ (${if (gateway.currency == "USD") "ডলার $" else "টাকা ৳"}):",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            OutlinedTextField(
                                value = inputAmount,
                                onValueChange = { inputAmount = it.filter { char -> char.isDigit() || char == '.' } },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                placeholder = { Text("পরিমাণ লিখুন") },
                                leadingIcon = {
                                    Text(
                                        text = if (gateway.currency == "USD") "$" else "৳",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = TeaLeafGreen
                                    )
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Quick Amount chips
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                val quickAmounts = if (gateway.currency == "USD") listOf("10", "25", "50", "100") else listOf("100", "500", "1000", "2000")
                                quickAmounts.forEach { amt ->
                                    AssistChip(
                                        onClick = { inputAmount = amt },
                                        label = {
                                            Text(
                                                text = if (gateway.currency == "USD") "$$amt" else "৳$amt",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        },
                                        colors = AssistChipDefaults.assistChipColors(
                                            containerColor = if (inputAmount == amt) TeaLeafGreen.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                        )
                                    )
                                }
                            }
                        }

                        // Sender Account / Phone / Email
                        item {
                            Text(
                                text = if (gateway.isInternational) "প্রেরকের কার্ডের শেষ ৪ ডিজিট / পেপাল ইমেইল / ওয়ালেট:" else "আপনার প্রেরক মোবাইল/অ্যাকাউন্ট নম্বর:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = inputSenderAccount,
                                onValueChange = { inputSenderAccount = it },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                placeholder = {
                                    Text(if (gateway.isInternational) "যেমন: user@example.com বা শেষ ৪ ডিজিট" else "যেমন: 017XXXXXXXX")
                                },
                                leadingIcon = {
                                    Icon(imageVector = Icons.Default.PhoneAndroid, contentDescription = null)
                                },
                                singleLine = true
                            )
                        }

                        // Transaction ID / Reference Hash
                        item {
                            Text(
                                text = if (gateway.isInternational) "ট্রানজেকশন রেফারেন্স / পেমেন্ট আইডি / TxHash:" else "ট্রানজেকশন আইডি (TrxID):",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = inputTransactionId,
                                onValueChange = { inputTransactionId = it },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                placeholder = {
                                    Text("যেমন: 9J7A6B8X2 বা ref_123456")
                                },
                                leadingIcon = {
                                    Icon(imageVector = Icons.Default.ReceiptLong, contentDescription = null)
                                },
                                singleLine = true
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Submit Action Button
                selectedGateway?.let { gateway ->
                    Button(
                        onClick = {
                            val amount = inputAmount.toDoubleOrNull() ?: 0.0
                            if (amount <= 0.0) {
                                Toast.makeText(context, "সঠিক পরিমাণ লিখুন!", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            if (inputSenderAccount.isBlank()) {
                                Toast.makeText(context, "প্রেরকের নম্বর বা অ্যাকাউন্ট দিন!", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            if (inputTransactionId.isBlank()) {
                                Toast.makeText(context, "ট্রানজেকশন আইডি (TrxID) লিখুন!", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            isSubmitting = true
                            viewModel.submitWalletRechargeRequest(
                                paymentGateway = gateway.gatewayName,
                                senderPhoneNumber = inputSenderAccount.trim(),
                                transactionId = inputTransactionId.trim(),
                                amount = amount
                            )
                            Toast.makeText(
                                context,
                                "✅ রিচার্জ আবেদন সফলভাবে সাবমিট হয়েছে! এডমিন ভেরিফিকেশন সাপেক্ষে স্বয়ংক্রিয়ভাবে ব্যালেন্স যুক্ত হবে।",
                                Toast.LENGTH_LONG
                            ).show()
                            isSubmitting = false
                            onDismiss()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = TeaLeafGreen),
                        shape = RoundedCornerShape(14.dp),
                        enabled = !isSubmitting
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "রিচার্জ আবেদন সাবমিট করুন (SHA-256)",
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
