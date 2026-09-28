package com.example.ui.screens

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ui.theme.TeaLeafGreen

data class ShortcutBookmark(
    val name: String,
    val url: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val color: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun BrowserScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var webViewInstance by remember { mutableStateOf<WebView?>(null) }
    
    // Navigation and URL states
    var currentUrl by remember { mutableStateOf("about:blank") }
    var inputUrl by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var loadingProgress by remember { mutableStateOf(0) }
    var canGoBackState by remember { mutableStateOf(false) }
    var canGoForwardState by remember { mutableStateOf(false) }

    // Intercept back button to navigate WebView back first, otherwise close browser screen
    BackHandler {
        if (webViewInstance?.canGoBack() == true) {
            webViewInstance?.goBack()
        } else {
            onBack()
        }
    }

    // Default bookmarks/quick links for Chrome Start Screen
    val bookmarks = remember {
        listOf(
            ShortcutBookmark("Google", "https://www.google.com", Icons.Default.Search, Color(0xFF4285F4)),
            ShortcutBookmark("Wikipedia", "https://www.wikipedia.org", Icons.Default.MenuBook, Color(0xFF333333)),
            ShortcutBookmark("YouTube", "https://www.youtube.com", Icons.Default.PlayArrow, Color(0xFFFF0000)),
            ShortcutBookmark("Facebook", "https://www.facebook.com", Icons.Default.People, Color(0xFF1877F2)),
            ShortcutBookmark("Google Translate", "https://translate.google.com", Icons.Default.Translate, Color(0xFF1A73E8)),
            ShortcutBookmark("Banglapedia", "https://www.banglapedia.org", Icons.Default.ImportContacts, Color(0xFF2E7D32)),
            ShortcutBookmark("W3Schools", "https://www.w3schools.com", Icons.Default.Code, Color(0xFF04AA6D)),
            ShortcutBookmark("Github", "https://github.com", Icons.Default.Terminal, Color(0xFF24292E))
        )
    }

    // Helper function to format search query into a real Google Search query or a formatted URL
    fun triggerSearch(query: String) {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) return

        val targetUrl = if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
            trimmed
        } else if (trimmed.contains(".") && !trimmed.contains(" ")) {
            "https://$trimmed"
        } else {
            // Google Search URL for query
            "https://www.google.com/search?q=${trimmed.replace(" ", "+")}"
        }
        
        inputUrl = targetUrl
        webViewInstance?.loadUrl(targetUrl)
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF202124), Color(0xFF2F3033)) // Chrome dark top style
                        )
                    )
            ) {
                // Top Control Bar
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
                            contentDescription = "Close Browser",
                            tint = Color.White
                        )
                    }

                    // Address and Search Bar
                    TextField(
                        value = inputUrl,
                        onValueChange = { inputUrl = it },
                        placeholder = { Text("Search or type web address...", fontSize = 13.sp, color = Color.LightGray) },
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .clip(RoundedCornerShape(22.dp)),
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedContainerColor = Color(0xFF3C4043),
                            unfocusedContainerColor = Color(0xFF35363A),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        leadingIcon = {
                            Icon(
                                imageVector = if (currentUrl.startsWith("https")) Icons.Default.Lock else Icons.Default.Language,
                                contentDescription = null,
                                tint = if (currentUrl.startsWith("https")) Color(0xFF81C784) else Color.LightGray,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        trailingIcon = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (inputUrl.isNotEmpty()) {
                                    IconButton(onClick = { inputUrl = "" }) {
                                        Icon(imageVector = Icons.Default.Close, contentDescription = "Clear", tint = Color.LightGray, modifier = Modifier.size(18.dp))
                                    }
                                }
                                IconButton(onClick = { triggerSearch(inputUrl) }) {
                                    Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "Go", tint = Color.White, modifier = Modifier.size(18.dp))
                                }
                            }
                        },
                        singleLine = true,
                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 13.sp)
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    // Reload/Stop Button
                    IconButton(
                        onClick = {
                            if (isLoading) {
                                webViewInstance?.stopLoading()
                            } else {
                                if (currentUrl == "about:blank") {
                                    webViewInstance?.loadUrl("https://www.google.com")
                                } else {
                                    webViewInstance?.reload()
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = if (isLoading) Icons.Default.Close else Icons.Default.Refresh,
                            contentDescription = if (isLoading) "Stop Loading" else "Refresh",
                            tint = Color.White
                        )
                    }
                }

                // Smooth linear progress indicator like Chrome
                if (isLoading) {
                    LinearProgressIndicator(
                        progress = loadingProgress / 100f,
                        color = Color(0xFF4285F4), // Google Blue
                        trackColor = Color.Transparent,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(3.dp)
                    )
                } else {
                    Spacer(modifier = Modifier.height(3.dp))
                }
            }
        },
        bottomBar = {
            // Chrome-like Navigation Toolbar at bottom
            Surface(
                modifier = Modifier.fillMaxWidth(),
                tonalElevation = 8.dp,
                color = Color(0xFF202124) // Chrome dark bottom bar
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Back button
                    IconButton(
                        onClick = { webViewInstance?.goBack() },
                        enabled = canGoBackState
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBackIos,
                            contentDescription = "Back Page",
                            tint = if (canGoBackState) Color.White else Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Forward button
                    IconButton(
                        onClick = { webViewInstance?.goForward() },
                        enabled = canGoForwardState
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowForwardIos,
                            contentDescription = "Forward Page",
                            tint = if (canGoForwardState) Color.White else Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Home screen button (Goes back to bookmarks page)
                    IconButton(
                        onClick = {
                            webViewInstance?.loadUrl("about:blank")
                            inputUrl = ""
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Browser Home",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // Share Page link button
                    IconButton(
                        onClick = {
                            if (currentUrl != "about:blank" && currentUrl.isNotEmpty()) {
                                Toast.makeText(context, "URL Copied: $currentUrl", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "No active web page loaded to share.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
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
            // Render WebView
            AndroidView(
                factory = { ctx ->
                    WebView(ctx).apply {
                        settings.apply {
                            javaScriptEnabled = true
                            domStorageEnabled = true
                            loadWithOverviewMode = true
                            useWideViewPort = true
                            builtInZoomControls = true
                            displayZoomControls = false
                            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                            userAgentString = "Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Mobile Safari/537.36"
                        }
                        
                        webViewClient = object : WebViewClient() {
                            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                super.onPageStarted(view, url, favicon)
                                isLoading = true
                                url?.let {
                                    if (it != "about:blank") {
                                        currentUrl = it
                                        inputUrl = it
                                    } else {
                                        currentUrl = "about:blank"
                                    }
                                }
                            }

                            override fun onPageFinished(view: WebView?, url: String?) {
                                super.onPageFinished(view, url)
                                isLoading = false
                                view?.let {
                                    canGoBackState = it.canGoBack()
                                    canGoForwardState = it.canGoForward()
                                }
                            }

                            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                                url?.let { view?.loadUrl(it) }
                                return true
                            }
                        }

                        webChromeClient = object : WebChromeClient() {
                            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                                super.onProgressChanged(view, newProgress)
                                loadingProgress = newProgress
                            }
                        }

                        // Start with blank screen to show search shortcuts
                        loadUrl("about:blank")
                        webViewInstance = this
                    }
                },
                modifier = Modifier.fillMaxSize(),
                update = {
                    // Update views when needed
                }
            )

            // Overlap starting screen if current URL is blank
            if (currentUrl == "about:blank") {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF202124)) // Dark luxury start screen
                        .verticalScroll(androidx.compose.foundation.rememberScrollState())
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Chrome colorful stylized logo
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "N",
                            fontSize = 42.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF4285F4) // Blue
                        )
                        Text(
                            text = "a",
                            fontSize = 42.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFEA4335) // Red
                        )
                        Text(
                            text = "b",
                            fontSize = 42.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFFBBC05) // Yellow
                        )
                        Text(
                            text = "a",
                            fontSize = 42.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF4285F4) // Blue
                        )
                        Text(
                            text = "c",
                            fontSize = 42.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF34A853) // Green
                        )
                        Text(
                            text = "h",
                            fontSize = 42.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFEA4335) // Red
                        )
                        Text(
                            text = "e",
                            fontSize = 42.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFFBBC05) // Yellow
                        )
                        Text(
                            text = "t",
                            fontSize = 42.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF34A853) // Green
                        )
                        Text(
                            text = "n",
                            fontSize = 42.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF4285F4) // Blue
                        )
                        Text(
                            text = "a",
                            fontSize = 42.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFEA4335) // Red
                        )
                    }

                    Text(
                        text = "Global Web Browser Engine",
                        fontSize = 13.sp,
                        color = Color.LightGray,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 4.dp, bottom = 28.dp)
                    )

                    // Big beautiful search bar in middle of start page
                    var middleQuery by remember { mutableStateOf("") }
                    TextField(
                        value = middleQuery,
                        onValueChange = { middleQuery = it },
                        placeholder = { Text("Search Google or type URL...", color = Color.Gray, fontSize = 14.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .border(1.dp, Color.Gray.copy(alpha = 0.5f), RoundedCornerShape(26.dp))
                            .clip(RoundedCornerShape(26.dp)),
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedContainerColor = Color(0xFF303134),
                            unfocusedContainerColor = Color(0xFF303134),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = Color.Gray)
                        },
                        trailingIcon = {
                            if (middleQuery.isNotEmpty()) {
                                IconButton(onClick = {
                                    triggerSearch(middleQuery)
                                    middleQuery = ""
                                }) {
                                    Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "Search", tint = Color.White)
                                }
                            }
                        },
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(34.dp))

                    // Shortcuts Title
                    Text(
                        text = "QUICK SHORTCUTS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray,
                        textAlign = TextAlign.Start,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 6.dp, bottom = 12.dp)
                    )

                    // Lazy Grid for Quick tiles
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(4),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(bookmarks) { bookmark ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clickable {
                                        inputUrl = bookmark.url
                                        webViewInstance?.loadUrl(bookmark.url)
                                    }
                                    .padding(vertical = 4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(bookmark.color.copy(alpha = 0.15f))
                                        .border(1.dp, bookmark.color.copy(alpha = 0.3f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = bookmark.icon,
                                        contentDescription = bookmark.name,
                                        tint = bookmark.color,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = bookmark.name,
                                    fontSize = 10.sp,
                                    color = Color.LightGray,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
