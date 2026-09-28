package com.example.ui.screens.admin

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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.data.model.DisputeCase
import com.example.data.model.EscrowOrder
import com.example.data.model.EscrowOrderStatus
import com.example.data.model.MarketplaceItemAudit
import com.example.ui.theme.TeaLeafGreen
import com.example.ui.viewmodel.PageBookViewModel
import java.util.Locale

@Composable
fun AdminMarketplaceEscrowAuditTab(
    marketplaceAudits: List<MarketplaceItemAudit>,
    viewModel: PageBookViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val escrowOrders by viewModel.escrowOrders.collectAsStateWithLifecycle()
    val commissionPercent by viewModel.marketplaceEscrowFeePercent.collectAsStateWithLifecycle()

    var activeSubTab by remember { mutableStateOf("ESCROW_ORDERS") } // "ESCROW_ORDERS", "DISPUTES", "PRODUCT_AUDIT", "COMMISSION_CONFIG"
    var selectedOrderForInvestigation by remember { mutableStateOf<EscrowOrder?>(null) }
    var verdictDialogAction by remember { mutableStateOf<Pair<EscrowOrder, Boolean>?>(null) } // Order to (refundBuyer)
    var verdictNoteInput by remember { mutableStateOf("") }
    var tempCommissionInput by remember { mutableStateOf(commissionPercent.toString()) }

    val totalEscrowLockedUsd = remember(escrowOrders) {
        escrowOrders.filter { it.status == EscrowOrderStatus.PAYMENT_HELD_IN_ESCROW || it.status == EscrowOrderStatus.SHIPPED_IN_TRANSIT || it.status == EscrowOrderStatus.DISPUTED_UNDER_REVIEW }
            .sumOf { it.amountUsd }
    }
    val totalPlatformCommissionEarned = remember(escrowOrders) {
        escrowOrders.filter { it.status == EscrowOrderStatus.BUYER_CONFIRMED_RELEASED || it.status == EscrowOrderStatus.FORCE_RELEASED_TO_SELLER }
            .sumOf { it.platformFeeUsd }
    }
    val activeDisputeCount = remember(escrowOrders) {
        escrowOrders.count { it.status == EscrowOrderStatus.DISPUTED_UNDER_REVIEW }
    }

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
                colors = CardDefaults.cardColors(containerColor = Color(0xFF004D40)),
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
                                text = "🛡️ এসক্রো ভল্ট ও মার্কেটপ্লেস ট্রাইব্যুনাল",
                                color = Color.White,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "নিরাপদ লেনদেন, অটো-কমিশন ও ক্রেতা-বিক্রেতা বিরোধ নিষ্পত্তি",
                                color = Color(0xFF80CBC4),
                                fontSize = 12.sp
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (activeDisputeCount > 0) Color(0xFFD32F2F) else Color(0xFF00796B)
                        ) {
                            Text(
                                text = if (activeDisputeCount > 0) "$activeDisputeCount টি বিরোধ তদন্তাধীন" else "সব নিরাপদ",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
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
                                    text = "এসক্রো ভল্টে লকড ফান্ড",
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontSize = 11.sp
                                )
                                Text(
                                    text = "$${String.format(Locale.US, "%.2f", totalEscrowLockedUsd)}",
                                    color = Color(0xFFFFD54F),
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "৳${String.format(Locale.US, "%.0f", totalEscrowLockedUsd * 120)} BDT",
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
                                    text = "কোম্পানির কমিশন আয় (${commissionPercent.toInt()}%)",
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontSize = 11.sp
                                )
                                Text(
                                    text = "$${String.format(Locale.US, "%.2f", totalPlatformCommissionEarned)}",
                                    color = Color(0xFF81C784),
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "৳${String.format(Locale.US, "%.0f", totalPlatformCommissionEarned * 120)} BDT",
                                    color = Color.White.copy(alpha = 0.6f),
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Sub Tab Selector
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                val tabs = listOf(
                    "ESCROW_ORDERS" to "এসক্রো অর্ডারসমূহ (${escrowOrders.size})",
                    "DISPUTES" to "⚖️ বিরোধ ট্রাইব্যুনাল ($activeDisputeCount)",
                    "PRODUCT_AUDIT" to "পণ্য অডিট (${marketplaceAudits.size})",
                    "COMMISSION_CONFIG" to "⚙️ কমিশন রেট"
                )
                items(tabs) { (tabKey, title) ->
                    val isSel = activeSubTab == tabKey
                    FilterChip(
                        selected = isSel,
                        onClick = { activeSubTab = tabKey },
                        label = {
                            Text(
                                text = title,
                                fontSize = 12.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF00796B),
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }

        // Content based on sub-tab
        when (activeSubTab) {
            "ESCROW_ORDERS" -> {
                items(escrowOrders, key = { it.id }) { order ->
                    AdminEscrowOrderCard(
                        order = order,
                        onInvestigateClick = { selectedOrderForInvestigation = order }
                    )
                }
            }
            "DISPUTES" -> {
                val disputedList = escrowOrders.filter { it.status == EscrowOrderStatus.DISPUTED_UNDER_REVIEW || it.disputeInfo != null }
                if (disputedList.isEmpty()) {
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
                                Icon(imageVector = Icons.Default.Gavel, contentDescription = null, tint = Color(0xFF00796B), modifier = Modifier.size(44.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(text = "কোনো সক্রিয় বিরোধ বা ডিসপিউট নেই", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(text = "সকল ক্রেতা ও বিক্রেতার লেনদেন সন্তোষজনক অবস্থায় রয়েছে।", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                } else {
                    items(disputedList, key = { it.id }) { order ->
                        AdminDisputeTribunalCard(
                            order = order,
                            onRefundBuyerClick = {
                                verdictDialogAction = Pair(order, true)
                                verdictNoteInput = "ক্রেতার পেশকৃত ছবি ও প্রমাণাদি যাচাই করে ত্রুটিপূর্ণ পণ্য পাওয়ার সত্যতা প্রমাণিত হয়েছে। ক্রেতাকে ১০০% রিফান্ড প্রদান করা হলো।"
                            },
                            onReleaseSellerClick = {
                                verdictDialogAction = Pair(order, false)
                                verdictNoteInput = "বিক্রেতার শিপমেন্ট ভিডিও ও সিলপ্যাক প্রমাণ সন্তোষজনক। অভিযোগ অমূলক প্রমাণিত হওয়ায় ফান্ড বিক্রেতাকে রিলিজ করা হলো।"
                            }
                        )
                    }
                }
            }
            "PRODUCT_AUDIT" -> {
                items(marketplaceAudits, key = { it.id }) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(imageVector = Icons.Default.ShoppingBag, contentDescription = null, tint = TeaLeafGreen, modifier = Modifier.size(32.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = item.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text(text = "বিক্রেতা: ${item.sellerName} • মূল্য: ৳${item.price.toInt()}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = when (item.status) {
                                        "APPROVED" -> Color(0xFF4CAF50).copy(alpha = 0.15f)
                                        "FLAGGED" -> Color(0xFFFF9800).copy(alpha = 0.15f)
                                        else -> Color.Red.copy(alpha = 0.15f)
                                    }
                                ) {
                                    Text(
                                        text = item.status,
                                        color = when (item.status) {
                                            "APPROVED" -> Color(0xFF2E7D32)
                                            "FLAGGED" -> Color(0xFFE65100)
                                            else -> Color.Red
                                        },
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        viewModel.flagMarketplaceItem(item.id, suspend = false)
                                        Toast.makeText(context, "আইটেমটি ফ্ল্যাগ করা হয়েছে", Toast.LENGTH_SHORT).show()
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("সন্দেহজনক ফ্ল্যাগ", fontSize = 11.sp, color = Color(0xFFE65100))
                                }

                                Button(
                                    onClick = {
                                        viewModel.flagMarketplaceItem(item.id, suspend = true)
                                        Toast.makeText(context, "পণ্যটি মার্কেটপ্লেস থেকে ব্যান করা হয়েছে!", Toast.LENGTH_SHORT).show()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("ব্যান ও রিমুভ", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
            "COMMISSION_CONFIG" -> {
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
                            Text(text = "💰 প্ল্যাটফর্ম মার্কেটপ্লেস কমিশন রেট কনফিগারেশন", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "প্রতিটি সফল অর্ডারের ক্ষেত্রে ক্রেতা সন্তুষ্টি নিশ্চিত হওয়ার পর মোট মূল্যের এই শতাংশ প্ল্যাটফর্ম কমিশন হিসেবে কর্তন করা হবে এবং অবশিষ্ট ফান্ড বিক্রেতার ওয়ালেটে প্রদান করা হবে।",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            OutlinedTextField(
                                value = tempCommissionInput,
                                onValueChange = { tempCommissionInput = it },
                                label = { Text("কমিশন রেট শতাংশ (%)") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf(3.0, 5.0, 8.0, 10.0).forEach { rate ->
                                    OutlinedButton(
                                        onClick = {
                                            tempCommissionInput = rate.toString()
                                            viewModel.updateEscrowCommissionPercent(rate)
                                            Toast.makeText(context, "কমিশন রেট $rate% সেট করা হয়েছে", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("${rate.toInt()}%", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Button(
                                onClick = {
                                    val rate = tempCommissionInput.toDoubleOrNull() ?: 5.0
                                    viewModel.updateEscrowCommissionPercent(rate)
                                    Toast.makeText(context, "কমিশন রেট সফলভাবে $rate% সংরক্ষিত হয়েছে!", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF004D40)),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("সংরক্ষণ করুন", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }

    // Verdict Dialog
    verdictDialogAction?.let { (order, isRefund) ->
        AlertDialog(
            onDismissRequest = { verdictDialogAction = null },
            icon = {
                Icon(
                    imageVector = if (isRefund) Icons.Default.AssignmentReturn else Icons.Default.VerifiedUser,
                    contentDescription = null,
                    tint = if (isRefund) Color(0xFF7B1FA2) else Color(0xFF00796B),
                    modifier = Modifier.size(36.dp)
                )
            },
            title = {
                Text(
                    text = if (isRefund) "⚖️ ক্রেতার পক্ষে রায় (১০০% রিফান্ড)" else "⚖️ বিক্রেতার পক্ষে রায় (ফান্ড রিলিজ)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = if (isRefund) {
                            "অর্ডার #${order.id}-এর $${order.amountUsd} টাকা ক্রেতা ${order.buyerName}-এর অ্যাকাউন্টে ফেরত প্রদান করা হবে।"
                        } else {
                            "অভিযোগ বাতিল করে প্ল্যাটফর্ম কমিশন ($${order.platformFeeUsd}) কেটে অবশিষ্ট $${order.sellerPayoutUsd} বিক্রেতা ${order.sellerName}-এর অ্যাকাউন্টে রিলিজ করা হবে।"
                        },
                        fontSize = 12.sp
                    )

                    OutlinedTextField(
                        value = verdictNoteInput,
                        onValueChange = { verdictNoteInput = it },
                        label = { Text("এডমিন রায় ও সিদ্ধান্তের নোট") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.adminResolveDispute(
                            orderId = order.id,
                            refundBuyer = isRefund,
                            verdictNote = verdictNoteInput.trim()
                        )
                        verdictDialogAction = null
                        Toast.makeText(context, "ট্রাইব্যুনালের রায় কার্যকর করা হয়েছে!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = if (isRefund) Color(0xFF7B1FA2) else Color(0xFF00796B))
                ) {
                    Text("রায় কার্যকর করুন")
                }
            },
            dismissButton = {
                TextButton(onClick = { verdictDialogAction = null }) {
                    Text("বাতিল")
                }
            }
        )
    }

    // Investigation Detail Dialog
    selectedOrderForInvestigation?.let { order ->
        AlertDialog(
            onDismissRequest = { selectedOrderForInvestigation = null },
            title = { Text("এসক্রো অর্ডার বিস্তারিত (#${order.id})", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("পণ্য: ${order.productTitle}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("ক্রেতা: ${order.buyerName} | বিক্রেতা: ${order.sellerName}", fontSize = 12.sp)
                    Text("মোট মূল্য: $${order.amountUsd}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("প্ল্যাটফর্ম কমিশন (${order.commissionRatePercent}%): $${String.format(Locale.US, "%.2f", order.platformFeeUsd)}", color = Color(0xFF00796B), fontSize = 12.sp)
                    Text("বিক্রেতা প্রাপ্যতা: $${String.format(Locale.US, "%.2f", order.sellerPayoutUsd)}", color = Color(0xFF2E7D32), fontSize = 12.sp)
                    Text("ট্র্যাকিং নম্বর: ${order.trackingNumber ?: "N/A"}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedOrderForInvestigation = null }) {
                    Text("বন্ধ করুন")
                }
            }
        )
    }
}

@Composable
fun AdminEscrowOrderCard(
    order: EscrowOrder,
    onInvestigateClick: () -> Unit
) {
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
                        model = order.productImageUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = order.productTitle,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            maxLines = 1,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "অর্ডার ID: #${order.id} • ${order.orderDate}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                shape = RoundedCornerShape(10.dp),
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
                        Text(text = "ক্রেতা: ${order.buyerName}", fontSize = 11.sp, fontWeight = FontWeight.Medium)
                        Text(text = "বিক্রেতা: ${order.sellerName}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "$${order.amountUsd}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color(0xFF004D40)
                        )
                        Text(
                            text = "কমিশন: $${String.format(Locale.US, "%.2f", order.platformFeeUsd)} (${order.commissionRatePercent}%)",
                            fontSize = 10.sp,
                            color = Color(0xFF00796B),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = order.status.badgeColor.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = order.status.label,
                        color = order.status.badgeColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                TextButton(onClick = onInvestigateClick) {
                    Icon(imageVector = Icons.Default.Info, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("অডিট বিস্তারিত", fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
fun AdminDisputeTribunalCard(
    order: EscrowOrder,
    onRefundBuyerClick: () -> Unit,
    onReleaseSellerClick: () -> Unit
) {
    val dispute = order.disputeInfo

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.5.dp, Color(0xFFD32F2F).copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
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
                    Icon(imageVector = Icons.Default.Gavel, contentDescription = null, tint = Color(0xFFD32F2F), modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "বিরোধ কেস #${dispute?.disputeId ?: "DSP"}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color(0xFFD32F2F)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = order.status.badgeColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = order.status.label,
                        color = order.status.badgeColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "পণ্য: ${order.productTitle} ($${order.amountUsd})",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Buyer's Claim Box
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFFFEBEE),
                border = BorderStroke(1.dp, Color(0xFFEF9A9A)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = "👤 ক্রেতার অভিযোগ (${order.buyerName}):",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = Color(0xFFC62828)
                    )
                    Text(
                        text = dispute?.buyerClaim ?: "ত্রুটিপূর্ণ পণ্য",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFFB71C1C)
                    )
                    Text(
                        text = "বিবরণ: ${dispute?.buyerEvidenceText ?: "প্রমাণ জমা হয়েছে"}",
                        fontSize = 11.sp,
                        color = Color(0xFF5D4037)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Seller's Defense Box
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFE8F5E9),
                border = BorderStroke(1.dp, Color(0xFFA5D6A7)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = "🏪 বিক্রেতার বক্তব্য ও প্রমাণ (${order.sellerName}):",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = Color(0xFF1B5E20)
                    )
                    Text(
                        text = dispute?.sellerDefense ?: "বিক্রেতার মতামত পেন্ডিং",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF2E7D32)
                    )
                    Text(
                        text = "প্রমাণ বিবরণ: ${dispute?.sellerProofText ?: "ইনট্যাক্ট পার্সেল ছবি ও ট্র্যাকিং স্লিপ জমা দেওয়া হয়েছে"}",
                        fontSize = 11.sp,
                        color = Color(0xFF1B5E20).copy(alpha = 0.8f)
                    )
                }
            }

            if (dispute?.adminVerdictNote != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFF3E5F5),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(text = "⚖️ বিচারিক রায় (${dispute.resolvedByAdmin ?: "Admin"}):", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color(0xFF4A148C))
                        Text(text = dispute.adminVerdictNote, fontSize = 11.sp, color = Color(0xFF4A148C))
                    }
                }
            }

            if (order.status == EscrowOrderStatus.DISPUTED_UNDER_REVIEW) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onRefundBuyerClick,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7B1FA2)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("ক্রেতাকে রিফান্ড", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = onReleaseSellerClick,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00796B)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("বিক্রেতাকে রিলিজ", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
