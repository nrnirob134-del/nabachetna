package com.example.ui.screens

import android.net.Uri
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import com.example.data.model.AppStoreItem
import com.example.ui.theme.TeaLeafGreen
import com.example.ui.viewmodel.PageBookViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppStoreScreen(
    viewModel: PageBookViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val appStoreItems by viewModel.appStoreItems.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    val userJson = remember(currentUser) {
        if (currentUser != null) {
            """
            {
                "name": "${currentUser!!.name.replace("\"", "\\\"")}",
                "avatarUrl": "${currentUser!!.avatarUrl}",
                "bio": "${currentUser!!.bio.replace("\"", "\\\"").replace("\n", " ")}",
                "livesIn": "${currentUser!!.livesIn.replace("\"", "\\\"")}",
                "fromLocation": "${currentUser!!.fromLocation.replace("\"", "\\\"")}",
                "walletBalance": ${currentUser!!.walletBalance}
            }
            """.trimIndent()
        } else {
            "{}"
        }
    }

    var isTabAdmin by remember { mutableStateOf(false) } // False for Category Menu, True for Admin Portal
    var searchQuery by remember { mutableStateOf("") }

    // Navigation and synchronization state trackers
    var activeSetupCategory by remember { mutableStateOf<AppStoreItem?>(null) }
    var activeRunningCategoryUrl by remember { mutableStateOf<String?>(null) }

    // Admin authentication
    var isAdminAuthenticated by remember { mutableStateOf(false) }
    var adminPasswordInput by remember { mutableStateOf("") }

    // Admin form states (Uris instead of text URLs)
    var appTitle by remember { mutableStateOf("") }
    var appIconUri by remember { mutableStateOf<Uri?>(null) }
    var appPosterUri by remember { mutableStateOf<Uri?>(null) }
    var appDestinationUri by remember { mutableStateOf<Uri?>(null) }
    var appNoticeText by remember { mutableStateOf("") }

    // File selection launchers using Android standard content picker
    val iconPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            appIconUri = uri
            Toast.makeText(context, "Icon selected from Gallery!", Toast.LENGTH_SHORT).show()
        }
    }

    val posterPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            appPosterUri = uri
            Toast.makeText(context, "Intro Poster selected from Gallery!", Toast.LENGTH_SHORT).show()
        }
    }

    // Dynamic Module selector (Select any local html file or interactive file)
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            appDestinationUri = uri
            Toast.makeText(context, "Dynamic Screen File Linked!", Toast.LENGTH_SHORT).show()
        }
    }

    // Filtering items
    val filteredApps = remember(searchQuery, appStoreItems) {
        appStoreItems.filter {
            it.title.contains(searchQuery, ignoreCase = true) ||
            it.noticeText.contains(searchQuery, ignoreCase = true)
        }
    }

    // Intercept back button to navigate out smoothly
    BackHandler {
        if (activeRunningCategoryUrl != null) {
            activeRunningCategoryUrl = null
        } else if (activeSetupCategory != null) {
            activeSetupCategory = null
        } else {
            onBack()
        }
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(TeaLeafGreen, TeaLeafGreen.copy(alpha = 0.9f))
                        )
                    )
            ) {
                // Top control bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }

                    Text(
                        text = if (isTabAdmin) "Category Management Panel" else "Nabachetna Dynamic Categories",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )

                    // Secret Admin access icon
                    IconButton(
                        onClick = {
                            isTabAdmin = !isTabAdmin
                        }
                    ) {
                        Icon(
                            imageVector = if (isTabAdmin) Icons.Default.MenuBook else Icons.Default.AdminPanelSettings,
                            contentDescription = "Switch Portal",
                            tint = Color.White
                        )
                    }
                }

                // If not in Admin, show Search
                if (!isTabAdmin) {
                    TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search category modules, topics...", fontSize = 13.sp, color = Color.Gray) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                            .height(46.dp)
                            .clip(RoundedCornerShape(23.dp)),
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = Color.Gray)
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(imageVector = Icons.Default.Close, contentDescription = "Clear", tint = Color.Gray)
                                }
                            }
                        },
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF2F5F3))
        ) {
            if (isTabAdmin) {
                // Admin Portal
                if (!isAdminAuthenticated) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = TeaLeafGreen,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Developer Admin Portal",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.Black
                        )
                        Text(
                            text = "Authenticate to publish custom categories using local Gallery files",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        OutlinedTextField(
                            value = adminPasswordInput,
                            onValueChange = { adminPasswordInput = it },
                            label = { Text("Enter Passcode") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                if (adminPasswordInput.trim() == "admin") {
                                    isAdminAuthenticated = true
                                    Toast.makeText(context, "Welcome Admin!", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Passcode incorrect! Hint: 'admin'", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = TeaLeafGreen),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Unlock Portal", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                } else {
                    // Category Publisher Form (Entirely URL-free, relies 100% on Local Gallery & Files Selector)
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        item {
                            Text(
                                text = "Publish Category from Gallery & Files",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Text(
                                text = "Select images from gallery and HTML/screens directly from files. No links needed!",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }

                        item {
                            OutlinedTextField(
                                value = appTitle,
                                onValueChange = { appTitle = it },
                                label = { Text("Category Title (e.g., Roof Gardening Hub)") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        // 1. Local Icon Image Selector
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                border = BoxDefaults.BorderStroke
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Button(
                                        onClick = {
                                            iconPickerLauncher.launch(
                                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                            )
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = TeaLeafGreen),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Select Icon", fontSize = 12.sp)
                                    }
                                    Spacer(modifier = Modifier.width(14.dp))
                                    if (appIconUri != null) {
                                        AsyncImage(
                                            model = appIconUri,
                                            contentDescription = null,
                                            modifier = Modifier
                                                .size(46.dp)
                                                .clip(CircleShape)
                                                .border(1.dp, TeaLeafGreen, CircleShape),
                                            contentScale = ContentScale.Crop
                                        )
                                    } else {
                                        Text("No icon selected", fontSize = 11.sp, color = Color.Gray)
                                    }
                                }
                            }
                        }

                        // 2. Local Poster Banner Selector
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                border = BoxDefaults.BorderStroke
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Button(
                                        onClick = {
                                            posterPickerLauncher.launch(
                                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                            )
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = TeaLeafGreen),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.Image, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Select Poster Banner", fontSize = 12.sp)
                                    }
                                    Spacer(modifier = Modifier.width(14.dp))
                                    if (appPosterUri != null) {
                                        AsyncImage(
                                            model = appPosterUri,
                                            contentDescription = null,
                                            modifier = Modifier
                                                .size(50.dp)
                                                .clip(RoundedCornerShape(6.dp)),
                                            contentScale = ContentScale.Crop
                                        )
                                    } else {
                                        Text("No poster selected", fontSize = 11.sp, color = Color.Gray)
                                    }
                                }
                            }
                        }

                        // 3. Local Dynamic Screen/HTML File Selector
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                border = BoxDefaults.BorderStroke
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Button(
                                        onClick = {
                                            filePickerLauncher.launch("*/*")
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = TeaLeafGreen),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Icon(imageVector = Icons.Default.AttachFile, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Select HTML/Category Module File", fontSize = 12.sp)
                                    }
                                    if (appDestinationUri != null) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "Linked File: ${appDestinationUri?.lastPathSegment ?: "dynamic_screen.html"}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TeaLeafGreen
                                        )
                                    }
                                }
                            }
                        }

                        item {
                            OutlinedTextField(
                                value = appNoticeText,
                                onValueChange = { appNoticeText = it },
                                label = { Text("Intro Announcement / Notice description") },
                                modifier = Modifier.fillMaxWidth(),
                                maxLines = 4
                            )
                        }

                        item {
                            Button(
                                onClick = {
                                    if (appTitle.isBlank()) {
                                        Toast.makeText(context, "Please enter a Category Title!", Toast.LENGTH_SHORT).show()
                                    } else if (appDestinationUri == null) {
                                        Toast.makeText(context, "Please select an HTML/Screen module file!", Toast.LENGTH_SHORT).show()
                                    } else {
                                        // Save to database. Store URI strings.
                                        val iconStr = appIconUri?.toString() ?: "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=120"
                                        val posterStr = appPosterUri?.toString() ?: "https://images.unsplash.com/photo-1577563906417-a64bde9298c8?w=500"
                                        val destStr = appDestinationUri?.toString() ?: "https://www.google.com"

                                        viewModel.addAppStoreItem(
                                            title = appTitle,
                                            iconUrl = iconStr,
                                            downloadUrl = destStr,
                                            posterUrl = posterStr,
                                            noticeText = appNoticeText.ifBlank { "নবচেতনার গ্যালারি থেকে প্রকাশিত বিশেষ প্ল্যাটফর্মে আপনাকে স্বাগতম।" },
                                            fileSizeMb = 3.8
                                        )

                                        Toast.makeText(context, "Published '$appTitle' Category from Local Files! 🚀", Toast.LENGTH_LONG).show()

                                        // Reset states
                                        appTitle = ""
                                        appIconUri = null
                                        appPosterUri = null
                                        appDestinationUri = null
                                        appNoticeText = ""
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = TeaLeafGreen),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(imageVector = Icons.Default.CloudUpload, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Publish Local Category 🚀", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "Active Published Screens (${appStoreItems.size})",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }

                        items(appStoreItems) { item ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    AsyncImage(
                                        model = item.iconUrl,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(RoundedCornerShape(8.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = item.title,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black,
                                        modifier = Modifier.weight(1f)
                                    )
                                    IconButton(
                                        onClick = {
                                            viewModel.deleteAppStoreItem(item.id)
                                            Toast.makeText(context, "Removed ${item.title}", Toast.LENGTH_SHORT).show()
                                        }
                                    ) {
                                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // User View: Clean Category Layout
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                            border = BoxDefaults.BorderStroke
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(TeaLeafGreen),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Nabachetna Curated Topics",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TeaLeafGreen
                                    )
                                    Text(
                                        text = "Tap on any category below to access real-time interactive dashboards and community networks.",
                                        fontSize = 11.sp,
                                        color = Color.DarkGray,
                                        lineHeight = 14.sp
                                    )
                                }
                            }
                        }
                    }

                    items(filteredApps) { item ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (item.isDownloaded) {
                                        activeRunningCategoryUrl = item.downloadUrl
                                    } else {
                                        activeSetupCategory = item
                                    }
                                },
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Dynamic Category Icon matching local selected Image
                                AsyncImage(
                                    model = item.iconUrl,
                                    contentDescription = item.title,
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(CircleShape)
                                        .background(Color.LightGray)
                                        .border(1.dp, Color.LightGray.copy(alpha = 0.5f), CircleShape),
                                    contentScale = ContentScale.Crop
                                )

                                Spacer(modifier = Modifier.width(14.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.title,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Nabachetna Authorized Category",
                                        fontSize = 11.sp,
                                        color = Color.Gray
                                    )
                                }

                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = "Open",
                                    tint = Color.LightGray,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }

            // A. SILENT INITIALIZING SCREEN
            activeSetupCategory?.let { app ->
                var syncProgress by remember { mutableStateOf(0) }
                LaunchedEffect(app) {
                    syncProgress = 0
                    while (syncProgress < 100) {
                        delay(65)
                        syncProgress += 2
                    }
                    viewModel.updateAppDownloadState(app.id, true)
                    activeRunningCategoryUrl = app.downloadUrl
                    activeSetupCategory = null
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF121212))
                        .clickable(enabled = false) {}
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // Banner Poster (Local selected Uri)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(230.dp)
                        ) {
                            AsyncImage(
                                model = app.posterUrl,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(Color.Transparent, Color(0xFF121212))
                                        )
                                    )
                            )
                        }

                        // Title and Icon
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = app.iconUrl,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(CircleShape)
                                    .border(1.5.dp, Color.White, CircleShape),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = app.title,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Synchronizing module files",
                                    fontSize = 11.sp,
                                    color = Color.LightGray
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(22.dp))

                        // Notice Box
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = null,
                                        tint = TeaLeafGreen,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "ANNOUNCEMENT & DETAILS",
                                        color = TeaLeafGreen,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = app.noticeText,
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        // Synchronizing Progress indicator
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 40.dp, start = 24.dp, end = 24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(bottom = 12.dp)
                            ) {
                                CircularProgressIndicator(
                                    color = TeaLeafGreen,
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "Setting up secure category... $syncProgress%",
                                    fontSize = 13.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            LinearProgressIndicator(
                                progress = syncProgress / 100f,
                                color = TeaLeafGreen,
                                trackColor = Color.DarkGray,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp))
                            )
                        }
                    }
                }
            }

            // B. ACTIVE RUNNING VIEW
            activeRunningCategoryUrl?.let { urlString ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                        .clickable(enabled = false) {}
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF202124))
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = { activeRunningCategoryUrl = null }) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                            }
                            Text(
                                text = "Nabachetna Live Module",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = {
                                    Toast.makeText(context, "Module synchronized!", Toast.LENGTH_SHORT).show()
                                }
                            ) {
                                Icon(imageVector = Icons.Default.Refresh, contentDescription = "Reload", tint = Color.White)
                            }
                        }

                        // Seamless WebView that supports loading Android local Content Uris!
                        AndroidView(
                            factory = { ctx ->
                                WebView(ctx).apply {
                                    settings.apply {
                                        javaScriptEnabled = true
                                        domStorageEnabled = true
                                        allowFileAccess = true
                                        allowContentAccess = true
                                        mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                                    }
                                    addJavascriptInterface(
                                        NabachetnaWebBridge(
                                            userJson = userJson,
                                            currentBalance = currentUser?.walletBalance ?: 0.0,
                                            onPay = { amount, description ->
                                                val currentVal = currentUser?.walletBalance ?: 0.0
                                                if (currentVal >= amount) {
                                                    viewModel.updateWalletBalance(currentVal - amount)
                                                    true
                                                } else {
                                                    false
                                                }
                                            }
                                        ),
                                        "NabachetnaBridge"
                                    )
                                    webViewClient = object : WebViewClient() {
                                        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                                            url?.let { view?.loadUrl(it) }
                                            return true
                                        }

                                        override fun onPageFinished(view: WebView?, url: String?) {
                                            super.onPageFinished(view, url)
                                            view?.evaluateJavascript(
                                                "window.nabachetnaUser = $userJson; " +
                                                "if (typeof onNabachetnaUserLoaded === 'function') { " +
                                                "    onNabachetnaUserLoaded(window.nabachetnaUser); " +
                                                "}",
                                                null
                                            )
                                        }
                                    }
                                    
                                    try {
                                        val uri = Uri.parse(urlString)
                                        loadUrl(uri.toString())
                                    } catch (e: Exception) {
                                        loadUrl(urlString)
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}

class NabachetnaWebBridge(
    private val userJson: String,
    private val currentBalance: Double,
    private val onPay: (Double, String) -> Boolean
) {
    @android.webkit.JavascriptInterface
    fun getUserProfile(): String {
        return userJson
    }

    @android.webkit.JavascriptInterface
    fun getBalance(): Double {
        return currentBalance
    }

    @android.webkit.JavascriptInterface
    fun pay(amount: Double, description: String): Boolean {
        return onPay(amount, description)
    }
}
