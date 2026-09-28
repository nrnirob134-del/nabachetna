package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.User
import com.example.ui.components.WalletRechargeModalDialog
import com.example.ui.theme.TeaLeafGreen
import com.example.ui.viewmodel.PageBookViewModel

data class ShortcutItem(
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val iconColor: Color
)

@Composable
fun MenuScreen(
    currentUser: User?,
    onNavigateToProfile: () -> Unit,
    viewModel: PageBookViewModel? = null,
    onNavigateToMarketplace: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onNavigateToCategoryBrowser: () -> Unit = {},
    onNavigateToBrowser: () -> Unit = {},
    onNavigateToAppStore: () -> Unit = {},
    onNavigateToPages: () -> Unit = {},
    onNavigateToNationalHelpline: () -> Unit = {},
    onNavigateToPublicBribeExposure: () -> Unit = {},
    onNavigateToGlobalTranslator: () -> Unit = {},
    onNavigateToLanguageLearning: () -> Unit = {},
    onNavigateToVipMeetingRoom: () -> Unit = {},
    onLogOut: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isHelpExpanded by remember { mutableStateOf(false) }
    var isSettingsExpanded by remember { mutableStateOf(false) }
    var showWalletRechargeModal by remember { mutableStateOf(false) }

    val shortcuts = remember {
        listOf(
            ShortcutItem("ভিআইপি কোম্পানি মিটিং 👑", Icons.Default.Groups, Color(0xFFFFD700)),
            ShortcutItem("আমার ওয়ালেট ও রিচার্জ 💳", Icons.Default.AccountBalanceWallet, TeaLeafGreen),
            ShortcutItem("ঘুষ & দুর্নীতি এক্সপোজার 🛡️", Icons.Default.Gavel, Color(0xFF8C1D1D)),
            ShortcutItem("জাতীয় হেল্পলাইন 🚑", Icons.Default.MedicalServices, Color(0xFFE53935)),
            ShortcutItem("গ্লোবাল এআই অনুবাদক 🌐", Icons.Default.Translate, TeaLeafGreen),
            ShortcutItem("ভাষা শিক্ষা (১০টি ভাষা) 📚", Icons.Default.School, Color(0xFF673AB7)),
            ShortcutItem("Pages", Icons.Default.Flag, Color(0xFFFF5722)),
            ShortcutItem("Groups", Icons.Default.Groups, Color(0xFF1B4D3E)),
            ShortcutItem("App Store", Icons.Default.CloudDownload, TeaLeafGreen),
            ShortcutItem("Marketplace", Icons.Default.Storefront, Color(0xFF41B35D)),
            ShortcutItem("Explore Topics", Icons.Default.Explore, Color(0xFFE91E63)),
            ShortcutItem("Web Browser", Icons.Default.Language, Color(0xFF4285F4))
        )
    }

    LazyColumn(
        modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
        contentPadding = PaddingValues(16.dp)
    ) {
        // Hamburger Title Header
        item {
            Text(
                text = "Menu",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        // Profile Shortcut Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
                    .clickable { onNavigateToProfile() },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = currentUser?.avatarUrl ?: "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150",
                        contentDescription = "My Avatar",
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = currentUser?.name ?: "Anika Rahman",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (currentUser?.isVerified == true) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = "Verified",
                                    tint = Color(0xFF1976D2),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        Text(
                            text = "See your profile",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Go to Profile",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Golden Exclusive VIP Company Meeting Room Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .clickable { onNavigateToVipMeetingRoom() },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F2438)),
                border = BorderStroke(1.5.dp, Color(0xFFFFD700).copy(alpha = 0.7f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFFFD700).copy(alpha = 0.2f),
                        modifier = Modifier.size(46.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("👑", fontSize = 22.sp)
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "কোম্পানি ডিরেক্ট বোর্ডরুম ও মিটিং",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFFD32F2F)
                            ) {
                                Text(
                                    text = "VIP LIVE",
                                    color = Color.White,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = if (currentUser?.isVerified == true) "ভেরিফাইড মেম্বার: সরাসরি কথা বলুন ও পরামর্শ দিন" else "শুধুমাত্র ভেরিফাইড সদস্যদের জন্য সংরক্ষিত কক্ষ",
                            fontSize = 11.sp,
                            color = if (currentUser?.isVerified == true) Color(0xFF00E676) else Color(0xFFFFD54F)
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Open VIP Room",
                        tint = Color(0xFFFFD700)
                    )
                }
            }
        }

        // Digital Wallet Card (Dynamic Payment Gateway Access)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, TeaLeafGreen.copy(alpha = 0.4f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = TeaLeafGreen.copy(alpha = 0.15f),
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.AccountBalanceWallet,
                                    contentDescription = null,
                                    tint = TeaLeafGreen,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "ডিজিটাল ওয়ালেট ও গ্লোবাল পে",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "ব্যালেন্স: ৳ ${String.format("%.2f", currentUser?.walletBalance ?: 0.0)}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TeaLeafGreen
                            )
                        }
                    }

                    Button(
                        onClick = {
                            if (viewModel != null) {
                                showWalletRechargeModal = true
                            } else {
                                Toast.makeText(context, "ওয়ালেট লোড হচ্ছে...", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = TeaLeafGreen),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(imageVector = Icons.Default.AddCard, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("রিচার্জ", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }

        // Shortcuts Title
        item {
            Text(
                text = "All Shortcuts",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        // Shortcuts Grid Section (Represented as Row/Column grids inside LazyColumn for stable rendering)
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val chunks = shortcuts.chunked(2)
                chunks.forEach { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowItems.forEach { item ->
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(80.dp)
                                    .clickable {
                                        if (item.title.contains("ভিআইপি কোম্পানি")) {
                                            onNavigateToVipMeetingRoom()
                                        } else if (item.title.contains("আমার ওয়ালেট")) {
                                            if (viewModel != null) {
                                                showWalletRechargeModal = true
                                            } else {
                                                Toast.makeText(context, "ওয়ালেট লোড হচ্ছে...", Toast.LENGTH_SHORT).show()
                                            }
                                        } else if (item.title.contains("ঘুষ & দুর্নীতি")) {
                                            onNavigateToPublicBribeExposure()
                                        } else if (item.title.contains("জাতীয় হেল্পলাইন")) {
                                            onNavigateToNationalHelpline()
                                        } else if (item.title.contains("গ্লোবাল এআই অনুবাদক")) {
                                            onNavigateToGlobalTranslator()
                                        } else if (item.title.contains("ভাষা শিক্ষা")) {
                                            onNavigateToLanguageLearning()
                                        } else if (item.title == "Marketplace") {
                                            onNavigateToMarketplace()
                                        } else if (item.title == "Explore Topics") {
                                            onNavigateToCategoryBrowser()
                                        } else if (item.title == "Web Browser") {
                                            onNavigateToBrowser()
                                        } else if (item.title == "App Store") {
                                            onNavigateToAppStore()
                                        } else if (item.title == "Pages") {
                                            onNavigateToPages()
                                        } else {
                                            Toast.makeText(context, "${item.title} shortcut clicked", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(10.dp),
                                    verticalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = item.title,
                                        tint = item.iconColor,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Text(
                                        text = item.title,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Expandable Panel 1: Help & Support
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isHelpExpanded = !isHelpExpanded }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.HelpOutline, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(text = "Help & Support", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        }
                        Icon(
                            imageVector = if (isHelpExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (isHelpExpanded) {
                        Divider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 0.5.dp)
                        MenuSubItem(title = "Help Center") { Toast.makeText(context, "Opening Help Center...", Toast.LENGTH_SHORT).show() }
                        MenuSubItem(title = "Support Inbox") { Toast.makeText(context, "Opening Support Inbox...", Toast.LENGTH_SHORT).show() }
                        MenuSubItem(title = "Report a Problem") { Toast.makeText(context, "Report form loaded.", Toast.LENGTH_SHORT).show() }
                    }
                }
            }
        }

        // Expandable Panel 2: Settings & Privacy
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isSettingsExpanded = !isSettingsExpanded }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Settings, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(text = "Settings & Privacy", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        }
                        Icon(
                            imageVector = if (isSettingsExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (isSettingsExpanded) {
                        Divider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 0.5.dp)
                        MenuSubItem(title = "Settings") { onNavigateToSettings() }
                        MenuSubItem(title = "Privacy Shortcuts") { onNavigateToSettings() }
                        MenuSubItem(title = "Dark Mode") { onNavigateToSettings() }
                    }
                }
            }
        }

        // Log Out Button
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    onLogOut()
                    Toast.makeText(context, "নবচেতনা অ্যাকাউন্ট থেকে সফলভাবে লগআউট করা হয়েছে!", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(Icons.Default.Logout, contentDescription = "Log Out", modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "লগআউট ও অ্যাকাউন্ট পরিবর্তন (Log Out)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
    }

    if (showWalletRechargeModal && viewModel != null) {
        WalletRechargeModalDialog(
            isOpen = showWalletRechargeModal,
            onDismiss = { showWalletRechargeModal = false },
            viewModel = viewModel,
            currentUser = currentUser
        )
    }
}

@Composable
fun MenuSubItem(
    title: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 24.dp, vertical = 14.dp)
    ) {
        Text(text = title, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
    }
}
