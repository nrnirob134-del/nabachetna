package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.ui.theme.TeaLeafGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBack: () -> Unit) {
    BackHandler {
        onBack()
    }

    val context = LocalContext.current

    // State Variables for Toggles
    var isDarkModeEnabled by remember { mutableStateOf(false) }
    var pushNotificationsEnabled by remember { mutableStateOf(true) }
    var emailNotificationsEnabled by remember { mutableStateOf(false) }
    var highQualityMedia by remember { mutableStateOf(true) }
    var profileLocked by remember { mutableStateOf(false) }
    var soundEffectsEnabled by remember { mutableStateOf(true) }

    // State for Password Dialog
    var showPasswordDialog by remember { mutableStateOf(false) }
    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }

    // State for Theme Dialog
    var showThemeDialog by remember { mutableStateOf(false) }
    var selectedThemeOption by remember { mutableStateOf("System Default") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings & Privacy",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = TeaLeafGreen
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF2F5F3))
                .verticalScroll(rememberScrollState())
        ) {
            // Profile Quick Link Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(TeaLeafGreen.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = TeaLeafGreen,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Account Center",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Text(
                            text = "Manage your connected experiences and account settings.",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 🩺 DIGITAL WELLBEING & HEALTH SAFETY TEST CONTROLS
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.HealthAndSafety,
                            contentDescription = null,
                            tint = TeaLeafGreen,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "ডিজিটাল ওয়েলবিয়িং ও ৪-ঘণ্টা স্বাস্থ্য সেফটি",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E272C)
                        )
                    }

                    Text(
                        text = "টানা ৪ ঘণ্টা অ্যাপ ব্যবহার করলে চোখ ও স্বাস্থ্যের সুরক্ষায় ১৫ মিনিটের বাধ্যতামূলক বিরতি দেওয়া হয়। ৫ মিনিট আগে সতর্কবার্তা প্রদর্শিত হয়।",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 4.dp, bottom = 10.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                com.example.data.service.DigitalWellbeingManager.simulateUsageTime(14105L)
                                Toast.makeText(context, "৫ মিনিট আগের সতর্কবার্তা চালু করা হয়েছে!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Text("🔔 ৩ঘণ্টা ৫৫মি টেস্ট", fontSize = 11.sp)
                        }

                        Button(
                            onClick = {
                                com.example.data.service.DigitalWellbeingManager.simulateUsageTime(14401L)
                                Toast.makeText(context, "১৫ মিনিটের বাধ্যতামূলক স্বাস্থ্য বিরতি লক চালু করা হয়েছে!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE65100)),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Text("🔒 ৪ঘণ্টা লক টেস্ট", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        IconButton(
                            onClick = {
                                com.example.data.service.DigitalWellbeingManager.simulateUsageTime(0L)
                                Toast.makeText(context, "স্ক্রিন টাইম রিসেট করা হয়েছে", Toast.LENGTH_SHORT).show()
                            }
                        ) {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = "Reset", tint = TeaLeafGreen)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 1. Account Settings Group
            SettingsGroupHeader(title = "Account Settings")
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
            ) {
                Column {
                    SettingsRowItem(
                        icon = Icons.Default.ManageAccounts,
                        iconColor = Color(0xFF1E88E5),
                        title = "Personal & Account Information",
                        subtitle = "Update your name, contact info, and identity verification.",
                        onClick = {
                            Toast.makeText(context, "Redirecting to personal info edit...", Toast.LENGTH_SHORT).show()
                        }
                    )
                    Divider(modifier = Modifier.padding(horizontal = 14.dp), color = Color.LightGray.copy(alpha = 0.5f))
                    SettingsRowItem(
                        icon = Icons.Default.Security,
                        iconColor = Color(0xFF43A047),
                        title = "Password & Security",
                        subtitle = "Change password, enable 2FA and manage login alerts.",
                        onClick = { showPasswordDialog = true }
                    )
                    Divider(modifier = Modifier.padding(horizontal = 14.dp), color = Color.LightGray.copy(alpha = 0.5f))
                    SettingsRowItem(
                        icon = Icons.Default.Payment,
                        iconColor = Color(0xFFE53935),
                        title = "Payments & Orders",
                        subtitle = "Check your subscription billing and marketplace history.",
                        onClick = {
                            Toast.makeText(context, "No billing history found.", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Preferences Group
            SettingsGroupHeader(title = "Preferences")
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
            ) {
                Column {
                    // Dark Mode Toggle Row
                    SettingsToggleRow(
                        icon = Icons.Default.DarkMode,
                        iconColor = Color(0xFF5E35B1),
                        title = "Dark Mode",
                        subtitle = "Toggle dark/light theme manually.",
                        checked = isDarkModeEnabled,
                        onCheckedChange = {
                            isDarkModeEnabled = it
                            Toast.makeText(context, if (it) "Dark mode enabled" else "Dark mode disabled", Toast.LENGTH_SHORT).show()
                        }
                    )
                    Divider(modifier = Modifier.padding(horizontal = 14.dp), color = Color.LightGray.copy(alpha = 0.5f))
                    
                    // Push Notifications Toggle
                    SettingsToggleRow(
                        icon = Icons.Default.NotificationsActive,
                        iconColor = Color(0xFFFB8C00),
                        title = "Push Notifications",
                        subtitle = "Receive real-time alerts for likes, comments and posts.",
                        checked = pushNotificationsEnabled,
                        onCheckedChange = { pushNotificationsEnabled = it }
                    )
                    Divider(modifier = Modifier.padding(horizontal = 14.dp), color = Color.LightGray.copy(alpha = 0.5f))

                    // Email Notifications Toggle
                    SettingsToggleRow(
                        icon = Icons.Default.Email,
                        iconColor = Color(0xFF00ACC1),
                        title = "Email Notifications",
                        subtitle = "Receive periodic summaries and digests on email.",
                        checked = emailNotificationsEnabled,
                        onCheckedChange = { emailNotificationsEnabled = it }
                    )
                    Divider(modifier = Modifier.padding(horizontal = 14.dp), color = Color.LightGray.copy(alpha = 0.5f))

                    // Sound Effects Toggle
                    SettingsToggleRow(
                        icon = Icons.Default.VolumeUp,
                        iconColor = Color(0xFF3949AB),
                        title = "Sound Effects",
                        subtitle = "Play small tick sounds on likes and reactions.",
                        checked = soundEffectsEnabled,
                        onCheckedChange = { soundEffectsEnabled = it }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Privacy Settings Group
            SettingsGroupHeader(title = "Audience and Visibility")
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
            ) {
                Column {
                    SettingsToggleRow(
                        icon = Icons.Default.Lock,
                        iconColor = Color(0xFFD81B60),
                        title = "Profile Locking",
                        subtitle = "Restrict non-friends from viewing your photos/posts.",
                        checked = profileLocked,
                        onCheckedChange = {
                            profileLocked = it
                            Toast.makeText(
                                context,
                                if (it) "Your Nabachetna profile is now LOCKED to non-friends." else "Profile unlocked.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    )
                    Divider(modifier = Modifier.padding(horizontal = 14.dp), color = Color.LightGray.copy(alpha = 0.5f))
                    SettingsRowItem(
                        icon = Icons.Default.Visibility,
                        iconColor = Color(0xFF00897B),
                        title = "How People Find You",
                        subtitle = "Control who can send friend requests and view details.",
                        onClick = {
                            Toast.makeText(context, "Loaded discoverability configurations.", Toast.LENGTH_SHORT).show()
                        }
                    )
                    Divider(modifier = Modifier.padding(horizontal = 14.dp), color = Color.LightGray.copy(alpha = 0.5f))
                    SettingsRowItem(
                        icon = Icons.Default.Block,
                        iconColor = Color(0xFF5D4037),
                        title = "Blocking List",
                        subtitle = "Review and unblock people you've previously blocked.",
                        onClick = {
                            Toast.makeText(context, "Your blocked accounts list is empty.", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 4. Media & Data Group
            SettingsGroupHeader(title = "Media and Data")
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
            ) {
                Column {
                    SettingsToggleRow(
                        icon = Icons.Default.HighQuality,
                        iconColor = Color(0xFF7CB342),
                        title = "High Quality Uploads",
                        subtitle = "Always upload images and video in original HD resolutions.",
                        checked = highQualityMedia,
                        onCheckedChange = { highQualityMedia = it }
                    )
                    Divider(modifier = Modifier.padding(horizontal = 14.dp), color = Color.LightGray.copy(alpha = 0.5f))
                    SettingsRowItem(
                        icon = Icons.Default.DeleteSweep,
                        iconColor = Color(0xFFF4511E),
                        title = "Clear Cache & Temporary Data",
                        subtitle = "Free up device storage space immediately.",
                        onClick = {
                            Toast.makeText(context, "Successfully cleared 14.2 MB of cached assets!", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // Footer info
            Text(
                text = "Nabachetna v1.4.0 • Built with Love",
                fontSize = 11.sp,
                color = Color.Gray,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 24.dp)
            )
        }
    }

    // Password Update Modal
    if (showPasswordDialog) {
        AlertDialog(
            onDismissRequest = { showPasswordDialog = false },
            title = { Text("Update Security Password", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            confirmButton = {
                Button(
                    onClick = {
                        if (oldPassword.isNotBlank() && newPassword.isNotBlank()) {
                            Toast.makeText(context, "Password updated successfully!", Toast.LENGTH_SHORT).show()
                            showPasswordDialog = false
                            oldPassword = ""
                            newPassword = ""
                        } else {
                            Toast.makeText(context, "Both fields are required!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TeaLeafGreen)
                ) {
                    Text("Save", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showPasswordDialog = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = oldPassword,
                        onValueChange = { oldPassword = it },
                        label = { Text("Current Password") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        label = { Text("New Secure Password") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            containerColor = Color.White
        )
    }
}

@Composable
fun SettingsGroupHeader(title: String) {
    Text(
        text = title.uppercase(),
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = Color.DarkGray.copy(alpha = 0.7f),
        modifier = Modifier.padding(start = 16.dp, bottom = 6.dp)
    )
}

@Composable
fun SettingsRowItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = Color.Gray,
                lineHeight = 14.sp
              )
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = Color.LightGray,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun SettingsToggleRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = Color.Gray,
                lineHeight = 14.sp
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = TeaLeafGreen,
                uncheckedThumbColor = Color.LightGray,
                uncheckedTrackColor = Color.LightGray.copy(alpha = 0.3f)
            )
        )
    }
}
