package com.example.ui

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.data.model.FeatureSplashPoster
import com.example.data.service.FeatureSplashPosterService
import com.example.ui.components.DynamicFeaturePosterSplash
import com.example.ui.screens.*
import com.example.ui.viewmodel.PageBookViewModel
import com.example.ui.theme.TeaLeafGreen

sealed class AppTab(
    val index: Int,
    val selectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val unselectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val contentDescription: String
) {
    object Home : AppTab(0, Icons.Filled.Home, Icons.Outlined.Home, "Home Feed")
    object Friends : AppTab(1, Icons.Filled.People, Icons.Outlined.People, "Friends Connections")
    object Watch : AppTab(2, Icons.Filled.Tv, Icons.Outlined.Tv, "Watch Videos")
    object Profile : AppTab(3, Icons.Filled.Person, Icons.Outlined.Person, "User Profile")
    object Notifications : AppTab(4, Icons.Filled.Notifications, Icons.Outlined.Notifications, "Notifications")
    object Menu : AppTab(5, Icons.Filled.Menu, Icons.Outlined.Menu, "Hamburger Menu")
}

@Composable
fun NabachetnaLogoBadge() {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(
                Brush.linearGradient(
                    listOf(Color(0xFF81C784), Color(0xFF2E7D32))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "N",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.Serif
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SocialFeedScreen() {
    val context = LocalContext.current
    val viewModel: PageBookViewModel = viewModel(factory = PageBookViewModel.provideFactory(context))

    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val posts by viewModel.posts.collectAsStateWithLifecycle()
    val stories by viewModel.stories.collectAsStateWithLifecycle()
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val dynamicBranding by viewModel.dynamicBrandingConfig.collectAsStateWithLifecycle()

    var activeTab by remember { mutableStateOf<AppTab>(AppTab.Home) }
    var isSearchExpanded by remember { mutableStateOf(false) }
    var showMarketplaceScreen by remember { mutableStateOf(false) }
    var showSettingsScreen by remember { mutableStateOf(false) }
    var showCategoryBrowserScreen by remember { mutableStateOf(false) }
    var showBrowserScreen by remember { mutableStateOf(false) }
    var showAppStoreScreen by remember { mutableStateOf(false) }
    var showPagesScreen by remember { mutableStateOf(false) }
    var showMessengerScreen by remember { mutableStateOf(false) }
    var showNationalHelplineScreen by remember { mutableStateOf(false) }
    var showPublicBribeExposureScreen by remember { mutableStateOf(false) }
    var showGlobalTranslatorScreen by remember { mutableStateOf(false) }
    var showLanguageLearningScreen by remember { mutableStateOf(false) }
    var showVipMeetingRoomScreen by remember { mutableStateOf(false) }
    val officialNotice by viewModel.officialNotice.collectAsStateWithLifecycle()
    var dismissedNoticeId by remember { mutableStateOf("") }

    // Dynamic Feature & App Launch Splash Posters (Admin Controlled, Unskippable 2-3s Coding Preparation)
    var activeSplashPoster by remember { mutableStateOf<FeatureSplashPoster?>(null) }
    var onSplashFinishedAction by remember { mutableStateOf<(() -> Unit)?>(null) }
    var hasAppLaunchSplashShown by remember { mutableStateOf(false) }

    fun navigateWithSplash(featureKey: String, onProceed: () -> Unit) {
        val poster = FeatureSplashPosterService.getPosterForFeature(featureKey)
        if (poster != null && poster.isEnabled) {
            activeSplashPoster = poster
            onSplashFinishedAction = onProceed
        } else {
            onProceed()
        }
    }

    LaunchedEffect(Unit) {
        if (!hasAppLaunchSplashShown) {
            val appSplash = FeatureSplashPosterService.getPosterForFeature("app_launch")
            if (appSplash != null && appSplash.isEnabled) {
                activeSplashPoster = appSplash
                onSplashFinishedAction = { hasAppLaunchSplashShown = true }
            } else {
                hasAppLaunchSplashShown = true
            }
        }
    }

    val wellbeingState by com.example.data.service.DigitalWellbeingManager.wellbeingState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        com.example.data.service.DigitalWellbeingManager.startTracking()
    }

    val tabs = listOf(
        AppTab.Home,
        AppTab.Friends,
        AppTab.Watch,
        AppTab.Profile,
        AppTab.Notifications,
        AppTab.Menu
    )

    if (currentUser == null) {
        AuthGatewayScreen(
            viewModel = viewModel,
            onLoginSuccess = {
                // User logged in, currentUser StateFlow will update automatically
            }
        )
        return
    }

    val parsedTitleColor = try {
        Color(android.graphics.Color.parseColor(dynamicBranding.titleColorHex))
    } catch (e: Exception) {
        Color.White
    }

    val brandingGradient = if (dynamicBranding.brandingGradientEnabled) {
        val grad1 = try { Color(android.graphics.Color.parseColor(dynamicBranding.brandingGradientColor1Hex)) } catch (e: Exception) { Color(0xFF81C784) }
        val grad2 = try { Color(android.graphics.Color.parseColor(dynamicBranding.brandingGradientColor2Hex)) } catch (e: Exception) { Color(0xFF2E7D32) }
        Brush.horizontalGradient(listOf(grad1, grad2))
    } else {
        Brush.horizontalGradient(listOf(TeaLeafGreen, TeaLeafGreen))
    }

    Scaffold(
        topBar = {
            if (activeTab != AppTab.Watch) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(brandingGradient) // Premium Dynamic Branding Gradient Header
                ) {
                    // Top header: Brand logo, search, and messenger icons
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        if (isSearchExpanded) {
                            // Expanded Real-Time Search Bar
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = {
                                        isSearchExpanded = false
                                        viewModel.setSearchQuery("")
                                    }
                                ) {
                                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                                }

                                TextField(
                                    value = searchQuery,
                                    onValueChange = { viewModel.setSearchQuery(it) },
                                    placeholder = { Text("Search NABACHETNA...", fontSize = 14.sp, color = Color.White.copy(alpha = 0.7f)) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(44.dp)
                                        .clip(RoundedCornerShape(22.dp)),
                                    colors = TextFieldDefaults.colors(
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
                                        focusedContainerColor = Color.White.copy(alpha = 0.2f),
                                        unfocusedContainerColor = Color.White.copy(alpha = 0.15f),
                                        focusedIndicatorColor = Color.Transparent,
                                        unfocusedIndicatorColor = Color.Transparent,
                                        disabledIndicatorColor = Color.Transparent
                                    ),
                                    singleLine = true,
                                    trailingIcon = {
                                        if (searchQuery.isNotEmpty()) {
                                            IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                                Icon(imageVector = Icons.Default.Close, contentDescription = "Clear", tint = Color.White, modifier = Modifier.size(18.dp))
                                            }
                                        }
                                    }
                                )
                            }
                        } else {
                            // Adaptable Dynamic Logo & Header
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable { activeTab = AppTab.Home }
                            ) {
                                if (dynamicBranding.type == "IMAGE") {
                                    AsyncImage(
                                        model = dynamicBranding.logoImageUrl,
                                        contentDescription = "3D Branding",
                                        modifier = Modifier
                                            .width(135.dp)
                                            .height(38.dp)
                                            .clip(RoundedCornerShape(8.dp)),
                                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(
                                                Brush.linearGradient(
                                                    listOf(Color(0xFF81C784), Color(0xFF2E7D32))
                                                )
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = dynamicBranding.logoEmoji,
                                            fontSize = 20.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = dynamicBranding.title,
                                        fontSize = 19.sp,
                                        fontWeight = FontWeight.Black,
                                        color = parsedTitleColor,
                                        fontFamily = FontFamily.SansSerif,
                                        letterSpacing = 0.5.sp
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                // User Profile Avatar Icon in Header
                                AsyncImage(
                                    model = currentUser?.avatarUrl ?: "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150",
                                    contentDescription = "Profile",
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .clickable { activeTab = AppTab.Profile },
                                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                // Search Action Circle Button
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.15f))
                                        .clickable { isSearchExpanded = true },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = "Search",
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                // Messenger Shortcut Circle Button
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.15f))
                                        .clickable {
                                            navigateWithSplash("messenger") {
                                                showMessengerScreen = true
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Chat,
                                        contentDescription = "Messages",
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = TeaLeafGreen,
                contentColor = Color.White
            ) {
                tabs.forEach { tab ->
                    val isSelected = activeTab == tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { activeTab = tab },
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (tab == AppTab.Notifications && notifications.any { !it.isRead }) {
                                        Badge(
                                            containerColor = Color.Red,
                                            contentColor = Color.White
                                        ) {
                                            Text(text = "${notifications.count { !it.isRead }}")
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                    contentDescription = tab.contentDescription,
                                    tint = if (isSelected) Color.White else Color.White.copy(alpha = 0.6f),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        },
                        label = {
                            Text(
                                text = when (tab) {
                                    AppTab.Home -> "হোম"
                                    AppTab.Friends -> "বন্ধু"
                                    AppTab.Watch -> "ওয়াচ"
                                    AppTab.Profile -> "প্রোফাইল"
                                    AppTab.Notifications -> "নোটিফিকেশন"
                                    AppTab.Menu -> "মেনু"
                                },
                                fontSize = 10.sp,
                                color = if (isSelected) Color.White else Color.White.copy(alpha = 0.7f)
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = Color.White.copy(alpha = 0.2f),
                            selectedIconColor = Color.White,
                            unselectedIconColor = Color.White.copy(alpha = 0.6f),
                            selectedTextColor = Color.White,
                            unselectedTextColor = Color.White.copy(alpha = 0.7f)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (activeTab) {
                is AppTab.Home -> {
                    HomeScreen(
                        viewModel = viewModel,
                        posts = posts,
                        stories = stories,
                        currentUser = currentUser
                    )
                }
                is AppTab.Friends -> {
                    FriendsScreen()
                }
                is AppTab.Watch -> {
                    WatchScreen(
                        modifier = Modifier.fillMaxSize(),
                        viewModel = viewModel,
                        currentUser = currentUser
                    )
                }
                is AppTab.Profile -> {
                    ProfileScreen(
                        viewModel = viewModel,
                        currentUser = currentUser,
                        userPosts = posts.filter { it.userId == currentUser?.id }
                    )
                }
                is AppTab.Notifications -> {
                    NotificationsScreen(
                        viewModel = viewModel,
                        notifications = notifications
                    )
                }
                is AppTab.Menu -> {
                    MenuScreen(
                        currentUser = currentUser,
                        onNavigateToProfile = { activeTab = AppTab.Profile },
                        viewModel = viewModel,
                        onNavigateToMarketplace = {
                            navigateWithSplash("marketplace") {
                                showMarketplaceScreen = true
                            }
                        },
                        onNavigateToSettings = { showSettingsScreen = true },
                        onNavigateToCategoryBrowser = { showCategoryBrowserScreen = true },
                        onNavigateToBrowser = {
                            navigateWithSplash("browser") {
                                showBrowserScreen = true
                            }
                        },
                        onNavigateToAppStore = { showAppStoreScreen = true },
                        onNavigateToPages = { showPagesScreen = true },
                        onNavigateToNationalHelpline = {
                            navigateWithSplash("blood_bank") {
                                showNationalHelplineScreen = true
                            }
                        },
                        onNavigateToPublicBribeExposure = {
                            navigateWithSplash("anti_corruption") {
                                showPublicBribeExposureScreen = true
                            }
                        },
                        onNavigateToGlobalTranslator = { showGlobalTranslatorScreen = true },
                        onNavigateToLanguageLearning = {
                            navigateWithSplash("language_learning") {
                                showLanguageLearningScreen = true
                            }
                        },
                        onNavigateToVipMeetingRoom = { showVipMeetingRoomScreen = true },
                        onLogOut = { viewModel.signOutCurrentUser() }
                    )
                }
            }

            // High-quality, Amazon-styled copyright-free Marketplace screen overlay
            AnimatedVisibility(
                visible = showMarketplaceScreen,
                enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn(),
                exit = slideOutHorizontally(targetOffsetX = { it }) + fadeOut(),
                modifier = Modifier.fillMaxSize()
            ) {
                MarketplaceScreen(
                    viewModel = viewModel,
                    onBack = { showMarketplaceScreen = false }
                )
            }

            // Beautiful, detailed Settings & Privacy overlay
            AnimatedVisibility(
                visible = showSettingsScreen,
                enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn(),
                exit = slideOutHorizontally(targetOffsetX = { it }) + fadeOut(),
                modifier = Modifier.fillMaxSize()
            ) {
                SettingsScreen(onBack = { showSettingsScreen = false })
            }

            // Premium Category Browser overlay
            AnimatedVisibility(
                visible = showCategoryBrowserScreen,
                enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn(),
                exit = slideOutHorizontally(targetOffsetX = { it }) + fadeOut(),
                modifier = Modifier.fillMaxSize()
            ) {
                CategoryBrowserScreen(onBack = { showCategoryBrowserScreen = false })
            }

            // High-Performance Chrome-like Browser overlay
            AnimatedVisibility(
                visible = showBrowserScreen,
                enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn(),
                exit = slideOutHorizontally(targetOffsetX = { it }) + fadeOut(),
                modifier = Modifier.fillMaxSize()
            ) {
                BrowserScreen(onBack = { showBrowserScreen = false })
            }

            // Interactive Dynamic AppStore overlay
            AnimatedVisibility(
                visible = showAppStoreScreen,
                enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn(),
                exit = slideOutHorizontally(targetOffsetX = { it }) + fadeOut(),
                modifier = Modifier.fillMaxSize()
            ) {
                AppStoreScreen(viewModel = viewModel, onBack = { showAppStoreScreen = false })
            }

            // Real-Time fully functional Pages overlay
            AnimatedVisibility(
                visible = showPagesScreen,
                enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn(),
                exit = slideOutHorizontally(targetOffsetX = { it }) + fadeOut(),
                modifier = Modifier.fillMaxSize()
            ) {
                PagesScreen(viewModel = viewModel, onBack = { showPagesScreen = false })
            }

            // Global AI Voice & Text Real-Time Translator Overlay
            AnimatedVisibility(
                visible = showGlobalTranslatorScreen,
                enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn(),
                exit = slideOutHorizontally(targetOffsetX = { it }) + fadeOut(),
                modifier = Modifier.fillMaxSize()
            ) {
                GlobalAiTranslatorScreen(onBack = { showGlobalTranslatorScreen = false })
            }

            // Next-Gen 3-Category Messenger Suite Overlay
            AnimatedVisibility(
                visible = showMessengerScreen,
                enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn(),
                exit = slideOutHorizontally(targetOffsetX = { it }) + fadeOut(),
                modifier = Modifier.fillMaxSize()
            ) {
                com.example.ui.screens.MessengerScreen(
                    viewModel = viewModel,
                    currentUser = currentUser,
                    onBack = { showMessengerScreen = false }
                )
            }

            // National Helpline & First Aid Emergency Overlay
            AnimatedVisibility(
                visible = showNationalHelplineScreen,
                enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn(),
                exit = slideOutHorizontally(targetOffsetX = { it }) + fadeOut(),
                modifier = Modifier.fillMaxSize()
            ) {
                com.example.ui.screens.NationalHelplineScreen(
                    onBack = { showNationalHelplineScreen = false }
                )
            }

            // Public Bribe Exposure Portal Overlay
            AnimatedVisibility(
                visible = showPublicBribeExposureScreen,
                enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn(),
                exit = slideOutHorizontally(targetOffsetX = { it }) + fadeOut(),
                modifier = Modifier.fillMaxSize()
            ) {
                com.example.ui.screens.PublicBribeExposureScreen(
                    onBack = { showPublicBribeExposureScreen = false }
                )
            }

            // Multi-Language Learning (10 Languages with Practice Camps, Exams, and Leaderboards) Overlay
            AnimatedVisibility(
                visible = showLanguageLearningScreen,
                enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn(),
                exit = slideOutHorizontally(targetOffsetX = { it }) + fadeOut(),
                modifier = Modifier.fillMaxSize()
            ) {
                com.example.ui.screens.LanguageLearningScreen(
                    currentUser = currentUser,
                    onBack = { showLanguageLearningScreen = false }
                )
            }

            // Exclusive Verified VIP Company Meeting Room & Townhall Overlay
            AnimatedVisibility(
                visible = showVipMeetingRoomScreen,
                enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn(),
                exit = slideOutHorizontally(targetOffsetX = { it }) + fadeOut(),
                modifier = Modifier.fillMaxSize()
            ) {
                com.example.ui.screens.VerifiedVipMeetingRoomScreen(
                    currentUser = currentUser,
                    viewModel = viewModel,
                    onBack = { showVipMeetingRoomScreen = false }
                )
            }

            // Prominent Official Notice Popup (Appears on app entry until dismissed via Cross Icon)
            if (officialNotice.isActive && dismissedNoticeId != officialNotice.id) {
                Dialog(
                    onDismissRequest = { /* forced until user taps cross icon */ },
                    properties = DialogProperties(
                        dismissOnBackPress = false,
                        dismissOnClickOutside = false
                    )
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                        border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF1B4D3E))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp)
                        ) {
                            // Top bar with Urgency badge & Cross Icon
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (officialNotice.urgency == "URGENT") Color(0xFFD32F2F) else Color(0xFF1B4D3E)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Campaign,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = if (officialNotice.urgency == "URGENT") "জরুরি ঘোষণা" else "অফিশিয়াল নোটিশ",
                                            color = Color.White,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                // Cross Icon for dismissing the notice
                                IconButton(
                                    onClick = { dismissedNoticeId = officialNotice.id },
                                    modifier = Modifier
                                        .size(34.dp)
                                        .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "রিমুভ করুন (Close)",
                                        tint = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Notice Title
                            Text(
                                text = officialNotice.title,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight = 23.sp
                            )

                            Text(
                                text = "তারিখ: ${officialNotice.date} • নবচেতনা কেন্দ্রীয় পরিচালনা পর্ষদ",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 4.dp, bottom = 10.dp)
                            )

                            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))

                            Spacer(modifier = Modifier.height(12.dp))

                            // Notice Message Body
                            Text(
                                text = officialNotice.message,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight = 20.sp
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            // Bottom Dismiss Action Button
                            Button(
                                onClick = { dismissedNoticeId = officialNotice.id },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B4D3E))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "পড়েছি ও বুঝেছি (✕ রিমুভ করুন)",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Digital Wellbeing & Health Safety Overlay (5-min warning banner & 15-min mandatory lock screen)
            com.example.ui.components.DigitalWellbeingOverlay(wellbeingState = wellbeingState)

            // Unskippable 2-3s System Coding / Feature Poster Splash Overlay
            if (activeSplashPoster != null) {
                DynamicFeaturePosterSplash(
                    poster = activeSplashPoster!,
                    onFinished = {
                        val action = onSplashFinishedAction
                        activeSplashPoster = null
                        onSplashFinishedAction = null
                        action?.invoke()
                    }
                )
            }
        }
    }
}
