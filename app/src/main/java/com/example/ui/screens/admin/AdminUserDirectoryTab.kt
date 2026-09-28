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
import com.example.data.model.User
import com.example.ui.viewmodel.PageBookViewModel

@Composable
fun AdminUserDirectoryTab(
    users: List<User>,
    viewModel: PageBookViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var selectedUserForDetails by remember { mutableStateOf<User?>(null) }
    var userToDelete by remember { mutableStateOf<User?>(null) }

    val filteredUsers = remember(users, searchQuery) {
        if (searchQuery.isBlank()) users
        else users.filter { user ->
            user.name.contains(searchQuery, ignoreCase = true) ||
                    user.emailOrPhone.contains(searchQuery, ignoreCase = true) ||
                    user.id.toString() == searchQuery.trim() ||
                    user.faceBiometricId.contains(searchQuery, ignoreCase = true)
        }
    }

    val totalUsers = users.size
    val faceVerifiedUsers = users.count { it.isFaceVerified }
    val blueTickUsers = users.count { it.isVerified }
    val totalWalletBalanceSum = users.sumOf { it.walletBalance }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Summary Cards
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
                                imageVector = Icons.Default.SupervisedUserAccess,
                                contentDescription = null,
                                tint = Color(0xFF81C784),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "কেন্দ্রীয় ইউজার ডিরেক্টরি ও আইডি ডাটাবেস",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF81C784).copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "মোট $totalUsers ইউজার",
                                color = Color(0xFF81C784),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

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
                                Text("ফেস ভেরিফাইড", fontSize = 10.sp, color = Color.White.copy(alpha = 0.7f))
                                Text("$faceVerifiedUsers জন", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF81C784))
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
                                Text("ব্লু টিক ব্যাজ", fontSize = 10.sp, color = Color.White.copy(alpha = 0.7f))
                                Text("$blueTickUsers জন", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64B5F6))
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
                                Text("মোট ওয়ালেট ব্যালেন্স", fontSize = 10.sp, color = Color.White.copy(alpha = 0.7f))
                                Text("৳ ${String.format("%.0f", totalWalletBalanceSum)}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFD54F))
                            }
                        }
                    }
                }
            }
        }

        // Search Box
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("ইউজারের নাম, ইমেইল/নম্বর, বা ইউজার ID দিয়ে খুঁজুন...") },
                leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )
        }

        // User List Items
        items(filteredUsers, key = { it.id }) { user ->
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
                        Box(modifier = Modifier.size(52.dp)) {
                            AsyncImage(
                                model = user.avatarUrl,
                                contentDescription = user.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                            )
                            if (user.isVerified) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Verified Badge",
                                    tint = Color(0xFF1877F2),
                                    modifier = Modifier
                                        .size(18.dp)
                                        .align(Alignment.BottomEnd)
                                        .background(Color.White, CircleShape)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = user.name,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer
                                ) {
                                    Text(
                                        text = "ID: #${user.id}",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Text(
                                text = if (user.emailOrPhone.isNotBlank()) user.emailOrPhone else "নম্বর পাওয়া যায়নি",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Row(
                                modifier = Modifier.padding(top = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                if (user.isFaceVerified) {
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = Color(0xFF2E7D32).copy(alpha = 0.15f)
                                    ) {
                                        Text(
                                            text = "✓ ফেস ভেরিফাইড",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color(0xFF2E7D32),
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                } else {
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = Color(0xFFD32F2F).copy(alpha = 0.15f)
                                    ) {
                                        Text(
                                            text = "✕ ফেস আনভেরিফাইড",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color(0xFFD32F2F),
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color(0xFFF57F17).copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = "ওয়ালেট: ৳ ${String.format("%.2f", user.walletBalance)}",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFE65100),
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(8.dp))

                    // Admin Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = { selectedUserForDetails = user },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Info, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("আইডি অডিট", fontSize = 11.sp)
                        }

                        OutlinedButton(
                            onClick = {
                                val newStatus = !user.isVerified
                                viewModel.toggleUserVerificationBadge(user.id, newStatus)
                                Toast.makeText(
                                    context,
                                    if (newStatus) "${user.name} কে ব্লু টিক প্রদান করা হয়েছে" else "ব্লু টিক রিমুভ করা হয়েছে",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(
                                imageVector = if (user.isVerified) Icons.Default.VerifiedUser else Icons.Outlined.Verified,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = if (user.isVerified) Color(0xFF1877F2) else MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (user.isVerified) "ব্লু টিক সরান" else "ব্লু টিক দিন", fontSize = 11.sp)
                        }

                        OutlinedButton(
                            onClick = {
                                viewModel.resetUserFaceBiometric(user.id)
                                Toast.makeText(context, "${user.name} এর ফেস বায়োমেট্রিক রিসেট করা হয়েছে", Toast.LENGTH_SHORT).show()
                            },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Face, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color(0xFFE65100))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("ফেস রিসেট", fontSize = 11.sp, color = Color(0xFFE65100))
                        }

                        IconButton(
                            onClick = { userToDelete = user },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }
    }

    // Modal Details Dialog
    selectedUserForDetails?.let { u ->
        AlertDialog(
            onDismissRequest = { selectedUserForDetails = null },
            icon = { Icon(imageVector = Icons.Default.Badge, contentDescription = null, tint = Color(0xFF0F382A)) },
            title = { Text("ইউজার সিকিউরিটি ও আইডি প্রোফাইল") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("নাম: ${u.name}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("ইউজার আইডি: #${u.id}", fontSize = 12.sp)
                    Text("ইমেইল / মোবাইল: ${u.emailOrPhone}", fontSize = 12.sp)
                    Text("ওয়ালেট সিকিউর ব্যালেন্স: ৳ ${String.format("%.2f", u.walletBalance)}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("ফেস বায়োমেট্রিক হ্যাশ ID: ${u.faceBiometricId}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("ফেস ভেরিফিকেশন স্ট্যাটাস: ${if (u.isFaceVerified) "উত্তীর্ণ (3D Dynamic Scan)" else "রিসেট করা হয়েছে"}", fontSize = 12.sp)
                    Text("রেজিস্ট্রেশন তথ্য: ${u.faceScanTimestamp}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("ঠিকানা: ${u.livesIn.ifBlank { "তথ্য দেওয়া হয়নি" }}", fontSize = 12.sp)
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedUserForDetails = null }) {
                    Text("বন্ধ করুন")
                }
            }
        )
    }

    // Delete User Confirmation Dialog
    userToDelete?.let { u ->
        AlertDialog(
            onDismissRequest = { userToDelete = null },
            icon = { Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = Color.Red) },
            title = { Text("ইউজার মুছে ফেলার নিশ্চিতকরণ") },
            text = {
                Text("আপনি কি নিশ্চিতভাবে ইউজার '${u.name}' (ID: #${u.id}) কে ডেটাবেস থেকে মুছে ফেলতে চান? এই কাজটি আর পূর্বাবস্থায় ফিরিয়ে আনা যাবে না।")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteUserAdmin(u.id)
                        Toast.makeText(context, "ইউজার মুছে ফেলা হয়েছে", Toast.LENGTH_SHORT).show()
                        userToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("হ্যাঁ, মুছুন")
                }
            },
            dismissButton = {
                TextButton(onClick = { userToDelete = null }) {
                    Text("বাতিল")
                }
            }
        )
    }
}
