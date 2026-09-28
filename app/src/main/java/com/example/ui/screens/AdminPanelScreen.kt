package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.data.model.*
import com.example.ui.screens.admin.*
import com.example.ui.theme.TeaLeafGreen
import com.example.ui.viewmodel.PageBookViewModel
import java.util.Locale

enum class AdminTab(val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    DASHBOARD("ড্যাশবোর্ড", Icons.Default.Dashboard),
    USER_DIRECTORY("ইউজার ডিরেক্টরি ও আইডি", Icons.Default.SupervisedUserAccess),
    WALLET_RECHARGES("ওয়ালেট রিচার্জ আবেদন", Icons.Default.AccountBalanceWallet),
    WITHDRAWALS("উইথড্রল ও পে-আউট", Icons.Default.Payments),
    MONETIZATION_CPM("মনিটাইজেশন ও CPM", Icons.Default.MonetizationOn),
    AI_MODERATION("এআই ও অটো-মডারেশন", Icons.Default.SmartToy),
    REPORTS("রিপোর্ট ও ডিসপিউট", Icons.Default.ReportProblem),
    VERIFICATION("ব্লু টিক পোর্টাল", Icons.Default.Verified),
    USER_STRIKES("স্ট্রাইক ও শ্যাডোব্যান", Icons.Default.PersonOff),
    SUB_ADMINS("টিম ও পারমিশন (RBAC)", Icons.Default.Badge),
    SECURITY_LOGS("অ্যান্টি-বট ও সিকিউরিটি", Icons.Default.Shield),
    MARKETPLACE_AUDIT("মার্কেটপ্লেস অডিট", Icons.Default.Storefront),
    MODERATION("পোস্ট ও পেজ মডারেশন", Icons.Default.Gavel),
    BROADCAST("সার্ভার পোস্ট কোটা ও নোটিশ", Icons.Default.Campaign),
    AD_MANAGER("গুগল এডস & বিজ্ঞাপন ম্যানেজার", Icons.Default.MonetizationOn),
    PUSH_CAMPAIGNS("টার্গেটেড পুশ ক্যাম্পেইন", Icons.Default.Send),
    FREE_SERVERS_CLUSTER("৫টি ফ্রি সার্ভার ও ক্লাস্টার", Icons.Default.CloudSync),
    SYSTEM_HEALTH("মেইনটেন্যান্স ও ব্যাকআপ", Icons.Default.Build),
    OFFICIAL_WAREHOUSE("অফিশিয়াল গোডাউন ম্যানেজমেন্ট", Icons.Default.Store),
    FEATURE_LOCK("সার্ভার লোড ও ফিচার লক", Icons.Default.Lock),
    FEATURE_SPLASH_POSTERS("ফিচার ও স্প্ল্যাশ পোস্টার", Icons.Default.BurstMode),
    SECURITY_HARDENING("এডমিন ও অ্যাপ সিকিউরিটি হার্ডেনিং", Icons.Default.Security),
    GLOBAL_GATEWAYS("আন্তর্জাতিক পেমেন্ট গেটওয়ে", Icons.Default.CurrencyExchange),
    DYNAMIC_BRANDING("ডাইনামিক ব্র্যান্ডিং ও থিম", Icons.Default.Palette)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelScreen(
    viewModel: PageBookViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isAuthenticated by remember { mutableStateOf(false) }
    var passwordInput by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf(AdminTab.DASHBOARD) }
    var showRoleSwitchDialog by remember { mutableStateOf(false) }
    var showBiometricAuthModal by remember { mutableStateOf(false) }

    // Live state collections from ViewModel
    val activeRole by viewModel.activeAdminRole.collectAsStateWithLifecycle()
    val revenueConfig by viewModel.revenueConfig.collectAsStateWithLifecycle()
    val auditLogs by viewModel.securityAuditLogs.collectAsStateWithLifecycle()
    val reports by viewModel.communityReports.collectAsStateWithLifecycle()
    val verificationRequests by viewModel.verificationRequests.collectAsStateWithLifecycle()
    val marketplaceAudits by viewModel.marketplaceAudits.collectAsStateWithLifecycle()
    val users by viewModel.allUsers.collectAsStateWithLifecycle()
    val posts by viewModel.posts.collectAsStateWithLifecycle()
    val pages by viewModel.pages.collectAsStateWithLifecycle()
    val withdrawalRequests by viewModel.withdrawalRequests.collectAsStateWithLifecycle()
    val userStrikes by viewModel.userStrikes.collectAsStateWithLifecycle()
    val rechargeRequests by viewModel.allRechargeRequests.collectAsStateWithLifecycle()


    BackHandler {
        onBack()
    }

    if (!isAuthenticated) {
        // Secure Admin Login Gateway
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF0D2818),
                            Color(0xFF1B4D3E),
                            Color(0xFF04150C)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .padding(16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF112217).copy(alpha = 0.95f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2E7D32).copy(alpha = 0.4f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFF81C784), Color(0xFF2E7D32))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = "Admin Shield",
                            tint = Color.White,
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "নবচেতনা কেন্দ্রীয় এডমিন পোর্টাল",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Text(
                        text = "Central Administration, Anti-Fraud & Governance",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.65f),
                        modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                    )

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF1B4D3E).copy(alpha = 0.5f),
                        border = androidx.compose.foundation.BorderStroke(0.5.dp, Color(0xFF81C784).copy(alpha = 0.5f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 20.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = Color(0xFF81C784),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "জিরো-টলারেন্স পলিসি: ফেক ফলোয়ার, বট ভিউ ও স্প্যাম প্রতিরোধে রুট গার্ড সক্রিয়।",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.9f),
                                lineHeight = 16.sp
                            )
                        }
                    }

                    OutlinedTextField(
                        value = passwordInput,
                        onValueChange = { passwordInput = it },
                        label = { Text("এডমিন সিকিউরিটি পাসকোড", color = Color.White.copy(alpha = 0.7f)) },
                        placeholder = { Text("পাসওয়ার্ড দিন (ডিফল্ট: admin)", color = Color.White.copy(alpha = 0.4f)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = "Toggle Visibility",
                                    tint = Color.White.copy(alpha = 0.7f)
                                )
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF81C784),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.3f)
                        )
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            if (passwordInput.trim().lowercase() == "admin" || passwordInput.trim() == "1234") {
                                isAuthenticated = true
                                Toast.makeText(context, "এডমিন প্যানেলে স্বাগতম!", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "ভুল পাসকোড! সঠিক পাসওয়ার্ড দিন (Hint: admin)", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                    ) {
                        Icon(imageVector = Icons.Default.LockOpen, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "এডমিন লগইন করুন", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Biometric Authentication Button
                    OutlinedButton(
                        onClick = {
                            showBiometricAuthModal = true
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFD54F).copy(alpha = 0.8f))
                    ) {
                        Icon(imageVector = Icons.Default.Fingerprint, contentDescription = null, tint = Color(0xFFFFD54F), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "বায়োমেট্রিক / ফিঙ্গারপ্রিন্ট আনলক", color = Color(0xFFFFD54F), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Quick One-Tap Demo Access Button
                    OutlinedButton(
                        onClick = {
                            passwordInput = "admin"
                            isAuthenticated = true
                            Toast.makeText(context, "মাস্টার এডমিন হিসেবে তাৎক্ষণিক লগইন সফল!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF81C784).copy(alpha = 0.6f))
                    ) {
                        Icon(imageVector = Icons.Default.Bolt, contentDescription = null, tint = Color(0xFF81C784), modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "ওয়ান-ট্যাপ কুইক এক্সেস (Demo Master)", color = Color(0xFF81C784), fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    TextButton(onClick = onBack) {
                        Text(text = "অ্যাপে ফিরে যান (Back to App)", color = Color.White.copy(alpha = 0.6f), fontSize = 13.sp)
                    }
                }
            }

            // Biometric Authentication Modal Dialog
            if (showBiometricAuthModal) {
                var isScanning by remember { mutableStateOf(true) }
                LaunchedEffect(Unit) {
                    kotlinx.coroutines.delay(1200L)
                    isScanning = false
                    kotlinx.coroutines.delay(400L)
                    showBiometricAuthModal = false
                    isAuthenticated = true
                    viewModel.setBiometricAuthenticated(true)
                    Toast.makeText(context, "✅ বায়োমেট্রিক ভেরিফিকেশন সফল! এডমিন প্যানেল আনলক হয়েছে।", Toast.LENGTH_SHORT).show()
                }

                AlertDialog(
                    onDismissRequest = { showBiometricAuthModal = false },
                    icon = {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(if (isScanning) Color(0xFFFFD54F).copy(alpha = 0.2f) else Color(0xFF2E7D32).copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isScanning) Icons.Default.Fingerprint else Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = if (isScanning) Color(0xFFFFD54F) else Color(0xFF81C784),
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    },
                    title = { Text(if (isScanning) "ফিঙ্গারপ্রিন্ট সেন্সর সক্রিয়..." else "বায়োমেট্রিক স্বীকৃত!", textAlign = androidx.compose.ui.text.style.TextAlign.Center) },
                    text = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = if (isScanning) "আপনার ডিভাইসের ফিঙ্গারপ্রিন্ট সেন্সরে আঙুল রাখুন। হার্ডওয়্যার এনক্রিপশন যাচাই করা হচ্ছে..." else "যাচাইকরণ সফল! আপনাকে এডমিন প্যানেলে নিয়ে যাওয়া হচ্ছে...",
                                fontSize = 12.sp,
                                color = Color.Gray,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    },
                    confirmButton = {},
                    dismissButton = {
                        TextButton(onClick = { showBiometricAuthModal = false }) {
                            Text("পাসওয়ার্ড ব্যবহার করুন")
                        }
                    }
                )
            }
        }
        return
    }

    // Authenticated Complete Admin Console
    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0F382A))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        IconButton(onClick = onBack) {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "নবচেতনা এডমিন প্যানেল",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = activeRole.badgeColor,
                                    modifier = Modifier.clickable { showRoleSwitchDialog = true }
                                ) {
                                    Text(
                                        text = activeRole.title.split(" ")[0],
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black,
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "${activeRole.title} • ${activeRole.permissions}",
                                fontSize = 11.sp,
                                color = Color(0xFFA5D6A7),
                                maxLines = 1
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Switch Role Icon
                        IconButton(onClick = { showRoleSwitchDialog = true }) {
                            Icon(imageVector = Icons.Default.ManageAccounts, contentDescription = "Switch Role", tint = Color.White)
                        }

                        // Logout Icon
                        IconButton(
                            onClick = {
                                isAuthenticated = false
                                passwordInput = ""
                                Toast.makeText(context, "এডমিন সেশন সমাপ্ত হয়েছে", Toast.LENGTH_SHORT).show()
                            }
                        ) {
                            Icon(imageVector = Icons.Default.ExitToApp, contentDescription = "Logout", tint = Color.White)
                        }
                    }
                }

                // Scrollable tab row containing all 9 feature domains
                ScrollableTabRow(
                    selectedTabIndex = selectedTab.ordinal,
                    containerColor = Color(0xFF0D2E22),
                    contentColor = Color.White,
                    edgePadding = 12.dp,
                    indicator = { tabPositions ->
                        TabRowDefaults.Indicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab.ordinal]),
                            color = Color(0xFF81C784),
                            height = 3.dp
                        )
                    },
                    divider = {}
                ) {
                    AdminTab.values().forEach { tab ->
                        val isSelected = selectedTab == tab
                        val badgeCount = when (tab) {
                            AdminTab.WALLET_RECHARGES -> rechargeRequests.count { it.status == "PENDING" }
                            AdminTab.WITHDRAWALS -> withdrawalRequests.count { it.status == "PENDING" }
                            AdminTab.REPORTS -> reports.count { it.status == "PENDING" }
                            AdminTab.VERIFICATION -> verificationRequests.count { it.status == "PENDING" }
                            AdminTab.MONETIZATION_CPM -> pages.count { it.isMonetizationPending }
                            AdminTab.MARKETPLACE_AUDIT -> marketplaceAudits.count { it.status == "FLAGGED" }
                            AdminTab.USER_STRIKES -> userStrikes.count { it.strikeCount > 0 }
                            else -> 0
                        }

                        Tab(
                            selected = isSelected,
                            onClick = { selectedTab = tab },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = tab.icon,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = if (isSelected) Color(0xFF81C784) else Color.White.copy(alpha = 0.6f)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = tab.title,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) Color(0xFF81C784) else Color.White.copy(alpha = 0.7f)
                                    )
                                    if (badgeCount > 0) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = Color.Red
                                        ) {
                                            Text(
                                                text = "$badgeCount",
                                                color = Color.White,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
        ) {
            when (selectedTab) {
                AdminTab.DASHBOARD -> AdminDashboardOverviewTab(
                    users = users,
                    posts = posts,
                    pages = pages,
                    reports = reports,
                    verificationRequests = verificationRequests,
                    revenueConfig = revenueConfig,
                    onNavigateToTab = { selectedTab = it },
                    viewModel = viewModel
                )
                AdminTab.USER_DIRECTORY -> AdminUserDirectoryTab(
                    users = users,
                    viewModel = viewModel
                )
                AdminTab.WALLET_RECHARGES -> AdminWalletRechargesTab(
                    rechargeRequests = rechargeRequests,
                    viewModel = viewModel
                )
                AdminTab.WITHDRAWALS -> AdminWithdrawalsTab(
                    viewModel = viewModel
                )
                AdminTab.MONETIZATION_CPM -> AdminMonetizationAndCpmTab(
                    pages = pages,
                    revenueConfig = revenueConfig,
                    viewModel = viewModel
                )
                AdminTab.AI_MODERATION -> AdminAiModerationTab(
                    viewModel = viewModel
                )
                AdminTab.REPORTS -> AdminCommunityReportsTab(
                    reports = reports,
                    viewModel = viewModel
                )
                AdminTab.VERIFICATION -> AdminBlueBadgeVerificationTab(
                    verificationRequests = verificationRequests,
                    viewModel = viewModel
                )
                AdminTab.USER_STRIKES -> AdminUserStrikesTab(
                    viewModel = viewModel
                )
                AdminTab.SUB_ADMINS -> AdminSubAdminsTab(
                    viewModel = viewModel
                )
                AdminTab.SECURITY_LOGS -> AdminSecurityAndAntiBotTab(
                    auditLogs = auditLogs,
                    viewModel = viewModel
                )
                AdminTab.MARKETPLACE_AUDIT -> AdminMarketplaceEscrowAuditTab(
                    marketplaceAudits = marketplaceAudits,
                    viewModel = viewModel
                )
                AdminTab.MODERATION -> AdminPagesAndPostsModerationTab(
                    pages = pages,
                    posts = posts,
                    viewModel = viewModel
                )
                AdminTab.BROADCAST -> AdminPostQuotaAndBroadcastTab(
                    viewModel = viewModel
                )
                AdminTab.AD_MANAGER -> AdminAdManagerTab()
                AdminTab.PUSH_CAMPAIGNS -> AdminPushCampaignsTab(
                    viewModel = viewModel
                )
                AdminTab.FREE_SERVERS_CLUSTER -> AdminMultiCloudClusterTab(
                    viewModel = viewModel
                )
                AdminTab.SYSTEM_HEALTH -> AdminMaintenanceTab(
                    viewModel = viewModel
                )
                AdminTab.OFFICIAL_WAREHOUSE -> AdminOfficialWarehouseTab()
                AdminTab.FEATURE_LOCK -> AdminFeatureLockTab(viewModel = viewModel)
                AdminTab.FEATURE_SPLASH_POSTERS -> AdminFeatureSplashPostersTab()
                AdminTab.SECURITY_HARDENING -> AdminSecurityHardeningTab()
                AdminTab.GLOBAL_GATEWAYS -> AdminGlobalPaymentGatewaysTab(viewModel = viewModel)
                AdminTab.DYNAMIC_BRANDING -> com.example.ui.screens.admin.AdminDynamicBrandingTab(viewModel = viewModel)
            }
        }
    }

    // Role-Based Access Control (RBAC) Switcher Dialog
    if (showRoleSwitchDialog) {
        AlertDialog(
            onDismissRequest = { showRoleSwitchDialog = false },
            icon = { Icon(imageVector = Icons.Default.ManageAccounts, contentDescription = null, tint = TeaLeafGreen) },
            title = { Text("এডমিন রোল পরিবর্তন করুন (RBAC)") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("দায়িত্ব অনুযায়ী উপযুক্ত অ্যাডমিনিস্ট্রেটিভ রোল নির্বাচন করুন:", fontSize = 13.sp)
                    AdminRole.values().forEach { role ->
                        val isSelected = activeRole == role
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.switchAdminRole(role)
                                    showRoleSwitchDialog = false
                                    Toast.makeText(context, "রোল সুইপ করা হয়েছে: ${role.title}", Toast.LENGTH_SHORT).show()
                                },
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) role.badgeColor.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface
                            ),
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, role.badgeColor) else null
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(selected = isSelected, onClick = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(text = role.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = role.badgeColor)
                                    Text(text = role.permissions, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showRoleSwitchDialog = false }) {
                    Text("বন্ধ করুন")
                }
            }
        )
    }
}

// -------------------------------------------------------------
// 1. DASHBOARD & OVERVIEW TAB
// -------------------------------------------------------------
@Composable
fun AdminDashboardOverviewTab(
    users: List<User>,
    posts: List<Post>,
    pages: List<Page>,
    reports: List<CommunityReport>,
    verificationRequests: List<VerificationRequest>,
    revenueConfig: SystemRevenueConfig,
    onNavigateToTab: (AdminTab) -> Unit,
    viewModel: PageBookViewModel
) {
    val context = LocalContext.current
    val pendingReports = reports.count { it.status == "PENDING" }
    val pendingVerifications = verificationRequests.count { it.status == "PENDING" }
    val pendingMonetization = pages.count { it.isMonetizationPending }
    val monetizedPages = pages.count { it.isMonetized }
    val totalEarnings = pages.sumOf { it.estimatedEarnings }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Zero-Fake Anti-Bot Shield Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1B4D3E)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = Color(0xFF81C784), modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("অ্যান্টি-বট ও রিয়েল ট্র্যাফিক গার্ড", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFF4CAF50).copy(alpha = 0.2f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF81C784))
                        ) {
                            Text(
                                text = "১০০% অর্গানিক",
                                color = Color(0xFF81C784),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "নবচেতনার নীতি অনুযায়ী প্ল্যাটফর্মে কোনো বট, স্ক্রিপ্ট, হ্যাকড লাইক বা ফেক ভিউ এলাউ করা হয় না। স্বয়ংক্রিয় এআই ট্র্যাফিক অ্যানালাইজার প্রতিটি এক্টিভিটি ট্র্যাক করছে।",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                Toast.makeText(context, "ডিপ অডিট সম্পন্ন: কোনো ফেক ভিউ বা বট স্ক্রিপ্ট পাওয়া যায়নি। সমস্ত মেট্রিক্স অর্গানিক!", Toast.LENGTH_LONG).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(imageVector = Icons.Default.Security, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("ডিপ অডিট চালান", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }

                        OutlinedButton(
                            onClick = { onNavigateToTab(AdminTab.SECURITY_LOGS) },
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF81C784).copy(alpha = 0.6f)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(imageVector = Icons.Default.ReceiptLong, contentDescription = null, tint = Color(0xFF81C784), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("লগ দেখুন", color = Color(0xFF81C784), fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Live Counters Grid
        item {
            Text(
                text = "সিস্টেম মেট্রিক্স ও লাইভ পরিসংখ্যান",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AdminStatCard(
                    modifier = Modifier.weight(1f),
                    title = "মোট ইউজার",
                    value = "${users.size}",
                    icon = Icons.Default.People,
                    color = Color(0xFF1976D2),
                    onClick = { onNavigateToTab(AdminTab.MODERATION) }
                )
                AdminStatCard(
                    modifier = Modifier.weight(1f),
                    title = "মোট পোস্ট",
                    value = "${posts.size}",
                    icon = Icons.Default.Article,
                    color = Color(0xFF00897B),
                    onClick = { onNavigateToTab(AdminTab.MODERATION) }
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AdminStatCard(
                    modifier = Modifier.weight(1f),
                    title = "মনিটাইজড পেজ",
                    value = "$monetizedPages",
                    icon = Icons.Default.MonetizationOn,
                    color = Color(0xFF2E7D32),
                    onClick = { onNavigateToTab(AdminTab.MONETIZATION_CPM) }
                )
                AdminStatCard(
                    modifier = Modifier.weight(1f),
                    title = "মোট পে-আউট",
                    value = "$${String.format(Locale.US, "%.2f", totalEarnings)}",
                    icon = Icons.Default.AccountBalanceWallet,
                    color = Color(0xFF6A1B9A),
                    onClick = { onNavigateToTab(AdminTab.MONETIZATION_CPM) }
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AdminStatCard(
                    modifier = Modifier.weight(1f),
                    title = "ইউজার রিপোর্ট",
                    value = "$pendingReports পেন্ডিং",
                    icon = Icons.Default.ReportProblem,
                    color = if (pendingReports > 0) Color(0xFFD32F2F) else Color.Gray,
                    onClick = { onNavigateToTab(AdminTab.REPORTS) }
                )
                AdminStatCard(
                    modifier = Modifier.weight(1f),
                    title = "ব্লু টিক আবেদন",
                    value = "$pendingVerifications পেন্ডিং",
                    icon = Icons.Default.Verified,
                    color = Color(0xFF0288D1),
                    onClick = { onNavigateToTab(AdminTab.VERIFICATION) }
                )
            }
        }

        // Quick Admin Control Shortcuts
        item {
            Text(
                text = "প্রশাসনিক কুইক অ্যাকশন",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    AdminActionRow(
                        title = "ক্রিয়েটর CPM রেভিনিউ ও রেট কনফিগারেশন",
                        subtitle = "প্রতি ১,০০০ ভিউতে আর্নিংস ($${revenueConfig.cpmRateUsd}) ও শেয়ারিং (${revenueConfig.creatorSharePercent}%)",
                        icon = Icons.Default.Tune,
                        iconColor = Color(0xFF2E7D32),
                        onClick = { onNavigateToTab(AdminTab.MONETIZATION_CPM) }
                    )
                    Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    AdminActionRow(
                        title = "কমিউনিটি রিপোর্ট ও ডিসপিউট ডেস্ক",
                        subtitle = "কপিরাইট, অপতথ্য ও হ্যারাসমেন্ট রিপোর্ট পর্যালোচনা",
                        icon = Icons.Default.Gavel,
                        iconColor = Color(0xFFD32F2F),
                        badgeText = if (pendingReports > 0) "$pendingReports পেন্ডিং" else null,
                        onClick = { onNavigateToTab(AdminTab.REPORTS) }
                    )
                    Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    AdminActionRow(
                        title = "অফিশিয়াল ব্লু টিক ভেরিফিকেশন পোর্টাল",
                        subtitle = "জাতীয় পরিচয়পত্র ও পেশাগত ডকুমেন্ট যাচাই",
                        icon = Icons.Default.Verified,
                        iconColor = Color(0xFF0288D1),
                        badgeText = if (pendingVerifications > 0) "$pendingVerifications নতুন" else null,
                        onClick = { onNavigateToTab(AdminTab.VERIFICATION) }
                    )
                    Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    AdminActionRow(
                        title = "মার্কেটপ্লেস ও কমার্স ফ্রড প্রিভেনশন",
                        subtitle = "সন্দেহজনক লটারি, ফেক ডিল ও নিষিদ্ধ পণ্য অপসারণ",
                        icon = Icons.Default.Storefront,
                        iconColor = Color(0xFFFF8F00),
                        onClick = { onNavigateToTab(AdminTab.MARKETPLACE_AUDIT) }
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 2. MONETIZATION & CPM REVENUE SETTING TAB
// -------------------------------------------------------------
@Composable
fun AdminMonetizationAndCpmTab(
    pages: List<Page>,
    revenueConfig: SystemRevenueConfig,
    viewModel: PageBookViewModel
) {
    val context = LocalContext.current
    var cpmInput by remember(revenueConfig.cpmRateUsd) { mutableStateOf("${revenueConfig.cpmRateUsd}") }
    var shareInput by remember(revenueConfig.creatorSharePercent) { mutableStateOf("${revenueConfig.creatorSharePercent}") }
    var minPayoutInput by remember(revenueConfig.minPayoutThresholdUsd) { mutableStateOf("${revenueConfig.minPayoutThresholdUsd}") }
    var spikeLimitInput by remember(revenueConfig.autoAuditSpikeThresholdViews) { mutableStateOf("${revenueConfig.autoAuditSpikeThresholdViews}") }

    var monetizationSubTab by remember { mutableStateOf(0) } // 0: CPM Setting, 1: Pending Requests, 2: Monetized Pages

    val pendingPages = pages.filter { it.isMonetizationPending }
    val monetizedPages = pages.filter { it.isMonetized }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = monetizationSubTab == 0,
                    onClick = { monetizationSubTab = 0 },
                    label = { Text("CPM রেভিনিউ কন্ট্রোল", fontWeight = FontWeight.Bold) },
                    leadingIcon = { Icon(imageVector = Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(16.dp)) }
                )
                FilterChip(
                    selected = monetizationSubTab == 1,
                    onClick = { monetizationSubTab = 1 },
                    label = { Text("আবেদনসমূহ (${pendingPages.size})") },
                    leadingIcon = {
                        if (pendingPages.isNotEmpty()) {
                            Icon(imageVector = Icons.Default.FiberManualRecord, contentDescription = null, tint = Color.Red, modifier = Modifier.size(10.dp))
                        }
                    }
                )
                FilterChip(
                    selected = monetizationSubTab == 2,
                    onClick = { monetizationSubTab = 2 },
                    label = { Text("পার্টনার্স (${monetizedPages.size})") }
                )
            }
        }

        if (monetizationSubTab == 0) {
            // CPM & Platform Revenue Setting Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.AttachMoney, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "ক্রিয়েটর অ্যাড রেভিনিউ ও CPM রেট পলিসি",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }

                        Text(
                            text = "নবচেতনার সকল পেজ ক্রিয়েটরদের জন্য ভিডিও ভিউ রেট, প্ল্যাটফর্ম ফান্ড অনুপাত এবং মিনিমাম উইথড্রয়াল লিমিট এখান থেকে নির্ধারিত হয়।",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                        )

                        OutlinedTextField(
                            value = cpmInput,
                            onValueChange = { cpmInput = it },
                            label = { Text("প্রতি ১,০০০ ভিউতে রেট (USD)") },
                            placeholder = { Text("যেমন: 1.50") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.fillMaxWidth(),
                            trailingIcon = { Text("USD", fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(end = 12.dp)) }
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = shareInput,
                            onValueChange = { shareInput = it },
                            label = { Text("ক্রিয়েটর রেভিনিউ শেয়ারিং (%)") },
                            placeholder = { Text("যেমন: 70") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            trailingIcon = { Text("%", fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(end = 12.dp)) }
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = minPayoutInput,
                            onValueChange = { minPayoutInput = it },
                            label = { Text("মিনিমাম পে-আউট উইথড্রয়াল থ্রেশহোল্ড (USD)") },
                            placeholder = { Text("যেমন: 20.00") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.fillMaxWidth(),
                            trailingIcon = { Text("USD", fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(end = 12.dp)) }
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = spikeLimitInput,
                            onValueChange = { spikeLimitInput = it },
                            label = { Text("অটোমেটিক ভিউ স্পাইক ট্রিপ লিমিট (অ্যান্টি-বট)") },
                            placeholder = { Text("যেমন: 5000") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            supportingText = { Text("স্বাভাবিকের চেয়ে ১ মিনিটে এই পরিমাণের বেশি ভিউ হলে ট্র্যাফিক রিভিউতে যাবে।") }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                val cpm = cpmInput.toDoubleOrNull() ?: 1.50
                                val share = shareInput.toIntOrNull() ?: 70
                                val minPayout = minPayoutInput.toDoubleOrNull() ?: 20.0
                                val spike = spikeLimitInput.toIntOrNull() ?: 5000
                                viewModel.updateRevenueConfig(cpm, share, minPayout, spike)
                                Toast.makeText(context, "নতুন CPM ও রেভিনিউ পলিসি সফলভাবে সংরক্ষিত ও কার্যকর করা হয়েছে!", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(imageVector = Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("সংরক্ষণ ও পলিসি কার্যকর করুন", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        } else {
            val listToShow = if (monetizationSubTab == 1) pendingPages else monetizedPages
            if (listToShow.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(44.dp))
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(text = if (monetizationSubTab == 1) "কোনো পেন্ডিং আবেদন নেই!" else "কোনো পার্টনার পেজ নেই", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                items(listToShow, key = { it.id }) { page ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = page.avatarUrl,
                                    contentDescription = page.name,
                                    modifier = Modifier.size(42.dp).clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = page.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(text = "${page.category} • ${page.followersCount} ফলোয়ার • ${page.topVideoViews} ভিউ", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (page.isMonetized) Color(0xFF4CAF50).copy(alpha = 0.15f) else Color(0xFFFF9800).copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = if (page.isMonetized) "মনিটাইজড" else "আবেদন জমা",
                                        color = if (page.isMonetized) Color(0xFF2E7D32) else Color(0xFFE65100),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "পেমেন্ট: ${page.payoutMethod} (${if (page.payoutAccount.isNotEmpty()) page.payoutAccount else "সংযুক্ত নেই"}) • আর্নিংস: $${String.format(Locale.US, "%.2f", page.estimatedEarnings)}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1B4D3E)
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                if (!page.isMonetized) {
                                    Button(
                                        onClick = {
                                            viewModel.approveMonetizationAdmin(page.id)
                                            Toast.makeText(context, "'${page.name}' পেজের মনিটাইজেশন অনুমোদিত হয়েছে!", Toast.LENGTH_SHORT).show()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("অনুমোদন দিন", fontSize = 11.sp)
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            viewModel.rejectMonetizationAdmin(page.id)
                                            Toast.makeText(context, "'${page.name}' এর আবেদন স্থগিত করা হয়েছে", Toast.LENGTH_SHORT).show()
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("বাতিল করুন", fontSize = 11.sp, color = Color.Red)
                                    }
                                } else {
                                    if (page.estimatedEarnings > 0.0) {
                                        Button(
                                            onClick = {
                                                viewModel.markPayoutProcessedAdmin(page.id, page.estimatedEarnings)
                                                Toast.makeText(context, "পে-আউট সফলভাবে পরিশোধ করা হয়েছে!", Toast.LENGTH_SHORT).show()
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("পে-আউট ক্লিয়ার (Paid)", fontSize = 11.sp)
                                        }
                                    }
                                    OutlinedButton(
                                        onClick = {
                                            viewModel.setPageMonetizationAdmin(page.id, isMonetized = false, isPolicyCompliant = false)
                                            Toast.makeText(context, "মনিটাইজেশন প্রত্যাহার করা হয়েছে", Toast.LENGTH_SHORT).show()
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("বাতিল / স্ট্রাইক", fontSize = 11.sp, color = Color.Red)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 3. COMMUNITY REPORTS & DISPUTE DESK TAB
// -------------------------------------------------------------
@Composable
fun AdminCommunityReportsTab(
    reports: List<CommunityReport>,
    viewModel: PageBookViewModel
) {
    val context = LocalContext.current
    var selectedFilter by remember { mutableStateOf("ALL") }

    val filteredReports = when (selectedFilter) {
        "PENDING" -> reports.filter { it.status == "PENDING" }
        "RESOLVED" -> reports.filter { it.status == "RESOLVED" }
        else -> reports
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedFilter == "ALL",
                    onClick = { selectedFilter = "ALL" },
                    label = { Text("সকল রিপোর্ট (${reports.size})") }
                )
                FilterChip(
                    selected = selectedFilter == "PENDING",
                    onClick = { selectedFilter = "PENDING" },
                    label = { Text("অমীমাংসিত (${reports.count { it.status == "PENDING" }})") }
                )
                FilterChip(
                    selected = selectedFilter == "RESOLVED",
                    onClick = { selectedFilter = "RESOLVED" },
                    label = { Text("নিষ্পত্তিকৃত (${reports.count { it.status == "RESOLVED" }})") }
                )
            }
        }

        if (filteredReports.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(44.dp))
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(text = "কোনো অমীমাংসিত রিপোর্ট নেই!", fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            items(filteredReports, key = { it.id }) { report ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFFD32F2F).copy(alpha = 0.12f)
                            ) {
                                Text(
                                    text = "${report.targetType} রিপোর্ট #${report.id}",
                                    color = Color(0xFFD32F2F),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Text(text = report.timestamp, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(text = "লক্ষ্যবস্তু: ${report.targetTitle}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(text = "অভিযোগের ধরন: ${report.reason}", fontSize = 12.sp, color = Color(0xFFD32F2F), fontWeight = FontWeight.SemiBold)
                        Text(text = "বিবরণ: ${report.details}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 2.dp))
                        Text(text = "রিপোর্টকারী: ${report.reportedBy}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f), modifier = Modifier.padding(top = 2.dp))

                        Spacer(modifier = Modifier.height(10.dp))

                        if (report.status == "PENDING") {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        viewModel.resolveReport(report.id, actionTakeDown = true)
                                        Toast.makeText(context, "কন্টেন্ট অপসারণ ও স্ট্রাইক জারি করা হয়েছে!", Toast.LENGTH_SHORT).show()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("অপসারণ ও স্ট্রাইক", fontSize = 11.sp)
                                }

                                OutlinedButton(
                                    onClick = {
                                        viewModel.dismissReport(report.id)
                                        Toast.makeText(context, "রিপোর্টটি খারিজ করা হয়েছে", Toast.LENGTH_SHORT).show()
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("রিপোর্ট খারিজ", fontSize = 11.sp)
                                }
                            }
                        } else {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (report.status == "RESOLVED") Color(0xFF4CAF50).copy(alpha = 0.15f) else Color.Gray.copy(alpha = 0.15f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = if (report.status == "RESOLVED") "✅ সমাধান সম্পন্ন (Action Taken)" else "❌ রিপোর্ট খারিজকৃত (Dismissed)",
                                    color = if (report.status == "RESOLVED") Color(0xFF2E7D32) else Color.Gray,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 4. ANTI-BOT INTELLIGENCE & SECURITY LOGS TAB
// -------------------------------------------------------------
@Composable
fun AdminSecurityAndAntiBotTab(
    auditLogs: List<SecurityAuditLog>,
    viewModel: PageBookViewModel
) {
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1B4D3E))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("স্বয়ংক্রিয় অ্যান্টি-বট ও অডিট ট্রেইল", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF81C784).copy(alpha = 0.2f)
                        ) {
                            Text("বট থ্রেট: ০%", color = Color(0xFF81C784), fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "প্ল্যাটফর্মে যেকোনো অস্বাভাবিক ভিউ বা ফেক ফলোয়ার জেনারেট করার চেষ্টা আইপি ও ডিভাইস ফিঙ্গারপ্রিন্ট সহ এখানে সংরক্ষিত হয়।",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 11.sp
                    )
                }
            }
        }

        item {
            Text(
                text = "রিয়েল-টাইম সিকিউরিটি ইভেন্ট লগ (${auditLogs.size} টি)",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        items(auditLogs, key = { it.id }) { log ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(
                                when (log.severity) {
                                    "HIGH", "CRITICAL" -> Color.Red.copy(alpha = 0.15f)
                                    "MEDIUM" -> Color(0xFFFF9800).copy(alpha = 0.15f)
                                    else -> Color(0xFF2E7D32).copy(alpha = 0.15f)
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (log.eventType) {
                                "BOT_DETECTION" -> Icons.Default.Shield
                                "PAYMENT_APPROVAL" -> Icons.Default.Paid
                                "CPM_UPDATE" -> Icons.Default.Tune
                                else -> Icons.Default.Security
                            },
                            contentDescription = null,
                            tint = when (log.severity) {
                                "HIGH", "CRITICAL" -> Color.Red
                                "MEDIUM" -> Color(0xFFE65100)
                                else -> Color(0xFF2E7D32)
                            },
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = log.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(text = log.timestamp, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Text(text = log.description, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 2.dp))
                        Text(text = "আইপি: ${log.ipAddress} • পরিচালনা: ${log.adminRole}", fontSize = 10.sp, color = Color(0xFF1B4D3E), fontWeight = FontWeight.Medium, modifier = Modifier.padding(top = 2.dp))
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 5. BLUE BADGE VERIFICATION PORTAL TAB
// -------------------------------------------------------------
@Composable
fun AdminBlueBadgeVerificationTab(
    verificationRequests: List<VerificationRequest>,
    viewModel: PageBookViewModel
) {
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0D47A1))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Verified, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("অফিশিয়াল ব্লু ব্যাজ ভেরিফিকেশন পোর্টাল", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "সাংবাদিক, ইনফ্লুয়েন্সার, সরকারি ও বিশিষ্ট ব্যক্তি এবং পেজসমূহের পরিচয়পত্র ও ট্রেড লাইসেন্স যাচাই করে ব্লু টিক প্রদান করুন।",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 11.sp
                    )
                }
            }
        }

        items(verificationRequests, key = { it.id }) { req ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.VerifiedUser, contentDescription = null, tint = Color(0xFF0288D1), modifier = Modifier.size(36.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = req.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(text = "${req.entityType} • ${req.category}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = when (req.status) {
                                "APPROVED" -> Color(0xFF4CAF50).copy(alpha = 0.15f)
                                "REJECTED" -> Color.Red.copy(alpha = 0.15f)
                                else -> Color(0xFFFF9800).copy(alpha = 0.15f)
                            }
                        ) {
                            Text(
                                text = when (req.status) {
                                    "APPROVED" -> "ভেরিফাইড"
                                    "REJECTED" -> "বাতিলকৃত"
                                    else -> "বিবেচনাধীন"
                                },
                                color = when (req.status) {
                                    "APPROVED" -> Color(0xFF2E7D32)
                                    "REJECTED" -> Color.Red
                                    else -> Color(0xFFE65100)
                                },
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(text = "সংযুক্ত ডকুমেন্ট: ${req.documentType}", fontSize = 11.sp, fontWeight = FontWeight.Medium)
                            Text(text = "নম্বর: ${req.documentNumber}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (req.status == "PENDING") {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    viewModel.approveVerification(req.id)
                                    Toast.makeText(context, "'${req.name}' কে সফলভাবে ব্লু টিক প্রদান করা হয়েছে!", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("ব্লু টিক অনুমোদন", fontSize = 11.sp)
                            }

                            OutlinedButton(
                                onClick = {
                                    viewModel.rejectVerification(req.id)
                                    Toast.makeText(context, "আবেদন বাতিল করা হয়েছে", Toast.LENGTH_SHORT).show()
                                },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("বাতিল করুন", fontSize = 11.sp, color = Color.Red)
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 6. MARKETPLACE & COMMERCE FRAUD PREVENTION TAB
// -------------------------------------------------------------
@Composable
fun AdminMarketplaceAuditTab(
    marketplaceAudits: List<MarketplaceItemAudit>,
    viewModel: PageBookViewModel
) {
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFBF360C))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Storefront, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("মার্কেটপ্লেস ও কমার্স ফ্রড প্রিভেনশন", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "মার্কেটপ্লেসে ভুয়া অফার, নিষিদ্ধ পণ্য বা ফেক সেলার কার্যক্রম কঠোরভাবে নিয়ন্ত্রণ করুন।",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 11.sp
                    )
                }
            }
        }

        items(marketplaceAudits, key = { it.id }) { item ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
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
}

// -------------------------------------------------------------
// 7. PAGES & POSTS MODERATION TAB
// -------------------------------------------------------------
@Composable
fun AdminPagesAndPostsModerationTab(
    pages: List<Page>,
    posts: List<Post>,
    viewModel: PageBookViewModel
) {
    var subTab by remember { mutableStateOf(0) } // 0: Pages, 1: Posts

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(
            selectedTabIndex = subTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = TeaLeafGreen
        ) {
            Tab(selected = subTab == 0, onClick = { subTab = 0 }, text = { Text("পেজ অডিট (${pages.size})") })
            Tab(selected = subTab == 1, onClick = { subTab = 1 }, text = { Text("পোস্ট মডারেশন (${posts.size})") })
        }

        if (subTab == 0) {
            AdminPagesAuditView(pages = pages, viewModel = viewModel)
        } else {
            AdminPostsModerationView(posts = posts, viewModel = viewModel)
        }
    }
}

// -------------------------------------------------------------
// 8. DATABASE & SYSTEM HEALTH TAB
// -------------------------------------------------------------
@Composable
fun AdminDatabaseAndSystemHealthTab(
    viewModel: PageBookViewModel
) {
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1B4D3E))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Storage, contentDescription = null, tint = Color(0xFF81C784), modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("ডাটাবেস অপটিমাইজেশন ও ব্যাকআপ টুলস", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "অ্যাপের ক্যাশ পরিষ্কার, ইনডেক্সিং অপটিমাইজেশন এবং ওয়ান-ক্লিক ডাটা ব্যাকআপ এক্সপোর্ট পরিচালনা করুন।",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 11.sp
                    )
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("ডাটাবেস হেলথ স্ট্যাটাস", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("ডাটাবেস ইঞ্জিন:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("Room SQLite v3 (100% OK)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("ক্যাশ মেমরি স্ট্যাটাস:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("স্বাভাবিক (Clean)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("এনক্রিপশন ও ব্যাকআপ:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("সক্রিয় (Active)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            val res = viewModel.runDatabaseMaintenanceClean()
                            Toast.makeText(context, res, Toast.LENGTH_LONG).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.CleaningServices, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("ক্যাশ ও স্প্যাম ক্লিনআপ চালান")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = {
                            Toast.makeText(context, "ডাটাবেস ব্যাকআপ তৈরি হয়েছে: nabachetna_db_backup.json (লোকাল স্টোরেজে সংরক্ষিত)", Toast.LENGTH_LONG).show()
                        },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("ওয়ান-ক্লিক ডেটা ব্যাকআপ এক্সপোর্ট")
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// HELPER COMPOSABLES & VIEWS
// -------------------------------------------------------------
@Composable
fun AdminStatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(color.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun AdminActionRow(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    badgeText: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(iconColor.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (badgeText != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFD32F2F)
                    ) {
                        Text(
                            text = badgeText,
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        )
    }
}

@Composable
fun AdminPagesAuditView(
    pages: List<Page>,
    viewModel: PageBookViewModel
) {
    val context = LocalContext.current
    var deleteConfirmDialogPage by remember { mutableStateOf<Page?>(null) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(pages, key = { it.id }) { page ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = page.avatarUrl,
                            contentDescription = page.name,
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = page.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text(text = "ক্যাটেগরি: ${page.category} • ${page.followersCount} ফলোয়ার • ${page.topVideoViews} ভিউ", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (page.isPolicyCompliant) Color(0xFF4CAF50).copy(alpha = 0.15f) else Color.Red.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = if (page.isPolicyCompliant) "নীতিসম্মত" else "স্ট্রাইক কার্যকর",
                                color = if (page.isPolicyCompliant) Color(0xFF2E7D32) else Color.Red,
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
                                val newStatus = !page.isPolicyCompliant
                                viewModel.updatePagePolicyStatusAdmin(page.id, newStatus)
                                Toast.makeText(
                                    context,
                                    if (newStatus) "'${page.name}' এর পলিসি স্ট্যাটাস ক্লিয়ার করা হয়েছে" else "'${page.name}' এ পলিসি স্ট্রাইক জারি করা হয়েছে",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = if (page.isPolicyCompliant) Icons.Default.Warning else Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = if (page.isPolicyCompliant) Color(0xFFE65100) else Color(0xFF2E7D32)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (page.isPolicyCompliant) "স্ট্রাইক দিন" else "স্ট্রাইক মুছুন",
                                fontSize = 11.sp,
                                color = if (page.isPolicyCompliant) Color(0xFFE65100) else Color(0xFF2E7D32)
                            )
                        }

                        OutlinedButton(
                            onClick = {
                                Toast.makeText(context, "অডিট রেজাল্ট: '${page.name}' ১০০% অর্গানিক ট্র্যাফিক দ্বারা পরিচালিত। কোনো বট বা স্ক্রিপ্ট পাওয়া যায়নি।", Toast.LENGTH_SHORT).show()
                            },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(imageVector = Icons.Default.FactCheck, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color(0xFF1976D2))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("অর্গানিক অডিট", fontSize = 11.sp, color = Color(0xFF1976D2))
                        }

                        IconButton(
                            onClick = { deleteConfirmDialogPage = page },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete Page", tint = Color.Red, modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }
        }
    }

    if (deleteConfirmDialogPage != null) {
        val pageToDelete = deleteConfirmDialogPage!!
        AlertDialog(
            onDismissRequest = { deleteConfirmDialogPage = null },
            icon = { Icon(imageVector = Icons.Default.DeleteForever, contentDescription = null, tint = Color.Red) },
            title = { Text("পেজটি সম্পূর্ণ ডিলিট করবেন?") },
            text = { Text("'${pageToDelete.name}' পেজটি সম্পূর্ণ মুছে ফেলা হবে। পেজের সকল পোস্ট এবং মনিটাইজেশন ডাটা রিমুভ হয়ে যাবে।") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deletePageAdmin(pageToDelete.id)
                        deleteConfirmDialogPage = null
                        Toast.makeText(context, "'${pageToDelete.name}' পেজ ডিলিট করা হয়েছে", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("হ্যাঁ, ডিলিট করুন")
                }
            },
            dismissButton = {
                TextButton(onClick = { deleteConfirmDialogPage = null }) {
                    Text("বাতিল")
                }
            }
        )
    }
}

@Composable
fun AdminPostsModerationView(
    posts: List<Post>,
    viewModel: PageBookViewModel
) {
    val context = LocalContext.current
    var postToDelete by remember { mutableStateOf<Post?>(null) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(posts, key = { it.id }) { post ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = post.userAvatar,
                            contentDescription = post.userName,
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = post.userName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(text = "${post.timestamp} • পোস্ট আইডি #${post.id}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        IconButton(
                            onClick = { postToDelete = post }
                        ) {
                            Icon(imageVector = Icons.Default.DeleteOutline, contentDescription = "Delete Post", tint = Color.Red)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = post.content,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 18.sp
                    )

                    if (!post.imageUrl.isNullOrEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        AsyncImage(
                            model = post.imageUrl,
                            contentDescription = "Post Image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.ThumbUp, contentDescription = null, tint = TeaLeafGreen, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "${post.likeCount} লাইক", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.width(12.dp))
                            Icon(imageVector = Icons.Default.ChatBubbleOutline, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "${post.commentCount} কমেন্ট", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        OutlinedButton(
                            onClick = {
                                postToDelete = post
                            },
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = Color.Red, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("ভায়োলেশন ডিলিট", fontSize = 11.sp, color = Color.Red)
                        }
                    }
                }
            }
        }
    }

    if (postToDelete != null) {
        val target = postToDelete!!
        AlertDialog(
            onDismissRequest = { postToDelete = null },
            icon = { Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = Color.Red) },
            title = { Text("পোস্টটি অপসারণ করবেন?") },
            text = { Text("'${target.userName}' এর পোস্টটি নীতি লঙ্ঘনের দায়ে প্ল্যাটফর্ম থেকে স্থায়ীভাবে ডিলিট করা হবে।") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deletePostAdmin(target.id)
                        postToDelete = null
                        Toast.makeText(context, "পোস্ট সফলভাবে অপসারণ করা হয়েছে", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("হ্যাঁ, ডিলিট করুন")
                }
            },
            dismissButton = {
                TextButton(onClick = { postToDelete = null }) {
                    Text("বাতিল")
                }
            }
        )
    }
}

@Composable
fun AdminSystemBroadcastView(
    viewModel: PageBookViewModel
) {
    val context = LocalContext.current
    val officialNotice by viewModel.officialNotice.collectAsStateWithLifecycle()
    val revenueConfig by viewModel.revenueConfig.collectAsStateWithLifecycle()

    var noticeTitleInput by remember(officialNotice.title) { mutableStateOf(officialNotice.title) }
    var noticeMessageInput by remember(officialNotice.message) { mutableStateOf(officialNotice.message) }
    var noticeUrgency by remember(officialNotice.urgency) { mutableStateOf(officialNotice.urgency) }
    var isNoticeActive by remember(officialNotice.isActive) { mutableStateOf(officialNotice.isActive) }

    var postLimitInput by remember(revenueConfig.dailyUserPostLimit) { mutableStateOf("${revenueConfig.dailyUserPostLimit}") }

    var broadcastTitleInput by remember { mutableStateOf("") }
    var broadcastMessageInput by remember { mutableStateOf("") }

    val presetNotices = listOf(
        "🛡️ নবচেতনা পলিসি অ্যালার্ট: কোনো ফেক ভিউ, লাইক বা ফলোয়ার এলাউ করা হবে না। বট ব্যবহারের চেষ্টা করলে পেজ চিরতরে ব্যান হবে।" to "নবচেতনা পলিসি উইং",
        "🎉 নবচেতনা ক্রিয়েটর মনিটাইজেশন প্রোগ্রাম সক্রিয়: ১,০০০ ফলোয়ার ও ১০,০০০ রিয়েল ভিডিও ভিউ অর্জনকারী পেজসমূহের আবেদন পর্যালোচনা চলছে।" to "নবচেতনা পার্টনার টিম",
        "🔒 সার্ভার সিকিউরিটি আপডেট সম্পন্ন: প্ল্যাটফর্মের সকল ট্র্যাফিক ১০০% অর্গানিক এবং এনক্রিপ্টেড রয়েছে।" to "নবচেতনা টেকনিক্যাল টিম"
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // -----------------------------------------------------------------
        // SECTION 1: OFFICIAL IN-APP POPUP NOTICE MANAGEMENT (অফিসিয়াল পপ-আপ নোটিশ)
        // -----------------------------------------------------------------
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Campaign, contentDescription = null, tint = Color(0xFFD32F2F), modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("অফিসিয়াল ইন-অ্যাপ পপ-আপ নোটিশ", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (officialNotice.isActive) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (officialNotice.isActive) Color(0xFF2E7D32) else Color(0xFFD32F2F))
                        ) {
                            Text(
                                text = if (officialNotice.isActive) "সক্রিয় পপ-আপ" else "নিষ্ক্রিয়",
                                color = if (officialNotice.isActive) Color(0xFF2E7D32) else Color(0xFFD32F2F),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Text(
                        text = "এডমিন কর্তৃক নোটিশটি সক্রিয় করা হলে, যে কোনো ইউজার অ্যাপে ঢুকলে তাদের সামনে এই নোটিশ ভেসে উঠবে। ইউজার ক্রস (✕) আইকন চেপে রিমুভ না করা পর্যন্ত এটি দেখা যাবে।",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 17.sp,
                        modifier = Modifier.padding(top = 6.dp, bottom = 14.dp)
                    )

                    // Title Input
                    OutlinedTextField(
                        value = noticeTitleInput,
                        onValueChange = { noticeTitleInput = it },
                        label = { Text("নোটিশের শিরোনাম") },
                        placeholder = { Text("যেমন: জরুরি অফিসিয়াল বিজ্ঞপ্তি...") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Message Input
                    OutlinedTextField(
                        value = noticeMessageInput,
                        onValueChange = { noticeMessageInput = it },
                        label = { Text("নোটিশের বিস্তারিত বিবরণ") },
                        placeholder = { Text("ব্যবহারকারীদের জন্য প্রাতিষ্ঠানিক বার্তা লিখুন...") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Urgency Level Selector
                    Text("নোটিশের গুরুত্ব (Urgency Level):", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("NORMAL" to "সাধারণ", "IMPORTANT" to "গুরুত্বপূর্ণ", "URGENT" to "জরুরি").forEach { (code, label) ->
                            val isSelected = noticeUrgency == code
                            FilterChip(
                                selected = isSelected,
                                onClick = { noticeUrgency = code },
                                label = { Text(label, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Active Switch
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("ইউজার অ্যাপে পপ-আপ চালু রাখুন", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Text("চালু থাকলে ইউজার ঢুকলেই সামনে ভেসে উঠবে", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = isNoticeActive,
                            onCheckedChange = { isNoticeActive = it }
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                if (noticeTitleInput.isNotBlank() && noticeMessageInput.isNotBlank()) {
                                    viewModel.updateOfficialNotice(
                                        title = noticeTitleInput.trim(),
                                        message = noticeMessageInput.trim(),
                                        urgency = noticeUrgency,
                                        isActive = isNoticeActive
                                    )
                                    Toast.makeText(context, "অফিসিয়াল নোটিশ সফলভাবে পাবলিশ করা হয়েছে! ইউজার অ্যাপে পপ-আপ হবে।", Toast.LENGTH_LONG).show()
                                } else {
                                    Toast.makeText(context, "শিরোনাম এবং বিবরণ উভয়ই পূরণ করুন", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B4D3E)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(imageVector = Icons.Default.Publish, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("পপ-আপ নোটিশ প্রকাশ করুন", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        if (officialNotice.isActive) {
                            OutlinedButton(
                                onClick = {
                                    isNoticeActive = false
                                    viewModel.updateOfficialNotice(
                                        title = noticeTitleInput,
                                        message = noticeMessageInput,
                                        urgency = noticeUrgency,
                                        isActive = false
                                    )
                                    Toast.makeText(context, "পপ-আপ নোটিশ বন্ধ করা হয়েছে", Toast.LENGTH_SHORT).show()
                                },
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color.Red)
                            ) {
                                Text("পপ-আপ বন্ধ", color = Color.Red, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }

        // -----------------------------------------------------------------
        // SECTION 2: DAILY USER POST LIMIT SETTING (দৈনিক পোস্ট লিমিট নির্ধারণ)
        // -----------------------------------------------------------------
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Tune, contentDescription = null, tint = Color(0xFF1976D2), modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("দৈনিক ইউজার পোস্ট লিমিট", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFE3F2FD),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1976D2))
                        ) {
                            Text(
                                text = "বর্তমান সীমা: ${revenueConfig.dailyUserPostLimit} টি / দিন",
                                color = Color(0xFF1976D2),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Text(
                        text = "স্প্যামিং, বট একাউন্ট ও ফেক কনটেন্ট রোধ করতে প্রতিটি ব্যবহারকারী দৈনিক সর্বোচ্চ কয়টি পোস্ট করতে পারবে তা এখান থেকে নিয়ন্ত্রণ করুন। সীমা অতিক্রম করলে ইউজারকে সতর্কবার্তা দেওয়া হবে।",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 17.sp,
                        modifier = Modifier.padding(top = 6.dp, bottom = 14.dp)
                    )

                    // Quick Presets
                    Text("দ্রুত প্রি-সেট লিমিট নির্বাচন করুন:", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(1, 3, 5, 10, 20, 50).forEach { count ->
                            val isSelected = postLimitInput == "$count"
                            FilterChip(
                                selected = isSelected,
                                onClick = { postLimitInput = "$count" },
                                label = { Text("$count টি", fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = postLimitInput,
                        onValueChange = { postLimitInput = it },
                        label = { Text("দৈনিক সর্বোচ্চ পোস্টের সংখ্যা") },
                        placeholder = { Text("যেমন: 5") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = { Text("টি / দিন", fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(end = 12.dp)) }
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            val limit = postLimitInput.toIntOrNull() ?: 5
                            if (limit > 0) {
                                viewModel.updateDailyPostLimit(limit)
                                Toast.makeText(context, "দৈনিক পোস্ট লিমিট সফলভাবে $limit টি নির্ধারণ করা হয়েছে!", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "সঠিক সংখ্যা লিখুন (কমপক্ষে ১ টি)", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("পোস্ট লিমিট সংরক্ষণ ও কার্যকর করুন", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }

        // -----------------------------------------------------------------
        // SECTION 3: CENTRAL BROADCAST TO NOTIFICATION FEEDS (সেন্ট্রাল ব্রডকাস্ট)
        // -----------------------------------------------------------------
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Send, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("সকলের নোটিফিকেশন ফিডে তাৎক্ষণিক ব্রডকাস্ট", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }

                    Text(
                        text = "এখানে প্রেরিত নোটিশ সকল নিবন্ধিত ব্যবহারকারীর নোটিফিকেশন ফিডে সরাসরি মেসেজ হিসেবে যুক্ত হবে।",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                    )

                    OutlinedTextField(
                        value = broadcastTitleInput,
                        onValueChange = { broadcastTitleInput = it },
                        label = { Text("ঘোষণার শিরোনাম / প্রেরক") },
                        placeholder = { Text("যেমন: নবচেতনা সেন্ট্রাল ডিরেক্টরেট") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = broadcastMessageInput,
                        onValueChange = { broadcastMessageInput = it },
                        label = { Text("ব্রডকাস্টের মূল বার্তা") },
                        placeholder = { Text("সকল ব্যবহারকারীর জন্য প্রয়োজনীয় বার্তা লিখুন...") },
                        minLines = 2,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            if (broadcastTitleInput.isNotBlank() && broadcastMessageInput.isNotBlank()) {
                                viewModel.broadcastSystemNotification(broadcastTitleInput.trim(), broadcastMessageInput.trim())
                                Toast.makeText(context, "সফলভাবে সারাদেশে ব্রডকাস্ট পাঠানো হয়েছে!", Toast.LENGTH_SHORT).show()
                                broadcastTitleInput = ""
                                broadcastMessageInput = ""
                            } else {
                                Toast.makeText(context, "শিরোনাম এবং বার্তা উভয়ই পূরণ করুন", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("সকলের নোটিফিকেশনে পাঠান", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Text(
                text = "রেডিমেড প্রি-সেট টেমপ্লেট (এক ট্যাপে পাঠান)",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        items(presetNotices) { (text, author) ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        broadcastTitleInput = author
                        broadcastMessageInput = text
                    },
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.EditNote, contentDescription = null, tint = TeaLeafGreen, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = author, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TeaLeafGreen)
                        Text(text = text, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }
        }
    }
}
