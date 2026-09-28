package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.PickVisualMediaRequest
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.data.model.Post
import com.example.data.model.Story
import com.example.data.model.User
import com.example.ui.components.*
import com.example.ui.viewmodel.PageBookViewModel

import androidx.compose.foundation.lazy.itemsIndexed
import com.example.data.service.AdManagerRegistry

@Composable
fun HomeScreen(
    viewModel: PageBookViewModel,
    posts: List<Post>,
    stories: List<Story>,
    currentUser: User?,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showCreatePostDialog by remember { mutableStateOf(false) }
    var selectedPostForComments by remember { mutableStateOf<Post?>(null) }
    val comments by viewModel.currentPostComments.collectAsState()

    val officialNotice by viewModel.officialNotice.collectAsStateWithLifecycle()
    val revenueConfig by viewModel.revenueConfig.collectAsStateWithLifecycle()
    var dismissedNoticeId by rememberSaveable { mutableStateOf<String?>(null) }
    var showPostLimitWarningDialog by remember { mutableStateOf<String?>(null) }

    val adCampaigns by AdManagerRegistry.adCampaigns.collectAsStateWithLifecycle()
    val adFrequencyInterval by AdManagerRegistry.adFrequencyInterval.collectAsStateWithLifecycle()
    val activeAds = remember(adCampaigns) { adCampaigns.filter { it.isActive } }

    Box(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            // 1. "What's on your mind?" Input Header
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp),
                    shape = RoundedCornerShape(0.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = currentUser?.avatarUrl ?: "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150",
                                contentDescription = "My Avatar",
                                modifier = Modifier.size(38.dp).clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(38.dp)
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                                    .border(0.5.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(20.dp))
                                    .clickable { showCreatePostDialog = true }
                                    .padding(horizontal = 14.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Text(
                                    text = "What's on your mind, ${currentUser?.name?.split(" ")?.firstOrNull() ?: ""}",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 14.sp
                                )
                            }
                        }

                        Divider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 0.5.dp, modifier = Modifier.padding(vertical = 10.dp))

                        // Quick buttons (Live, Photo, Check In)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            QuickActionBarButton(
                                icon = Icons.Default.Videocam,
                                iconTint = Color(0xFFE42645),
                                label = "Live",
                                onClick = { Toast.makeText(context, "Live video starts soon...", Toast.LENGTH_SHORT).show() }
                            )
                            QuickActionBarButton(
                                icon = Icons.Default.Image,
                                iconTint = Color(0xFF41B35D),
                                label = "Photo",
                                onClick = { showCreatePostDialog = true }
                            )
                            QuickActionBarButton(
                                icon = Icons.Default.LocationOn,
                                iconTint = Color(0xFFF35369),
                                label = "Check In",
                                onClick = { Toast.makeText(context, "Locating nearby places...", Toast.LENGTH_SHORT).show() }
                            )
                        }
                    }
                }
            }

            // 2. Stories Section (Horizontal Slider)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp),
                    shape = RoundedCornerShape(0.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    LazyRow(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            CreateStoryCard(
                                currentUser = currentUser,
                                onClick = {
                                    // Generate a random gorgeous background story photo
                                    val storyUrls = listOf(
                                        "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=400",
                                        "https://images.unsplash.com/photo-1447752875215-b2761acb3c5d?w=400",
                                        "https://images.unsplash.com/photo-1472214222541-d510753a4907?w=400"
                                    )
                                    viewModel.createStory(storyUrls.random())
                                    Toast.makeText(context, "New story shared!", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                        items(stories) { story ->
                            StoryItem(
                                story = story,
                                onClick = { Toast.makeText(context, "Viewing ${story.userName}'s story...", Toast.LENGTH_SHORT).show() }
                            )
                        }
                    }
                }
            }

            // 3. Main Posts Feed List with Native Ad Insertion
            itemsIndexed(posts) { index, post ->
                PostCard(
                    post = post,
                    onLikeClick = { viewModel.toggleLike(post) },
                    onReactionSelect = { reaction -> viewModel.toggleLike(post, reaction) },
                    onCommentClick = {
                        selectedPostForComments = post
                        viewModel.selectPostForComments(post.id)
                    },
                    onShareClick = {
                        Toast.makeText(context, "Shared to your Feed successfully!", Toast.LENGTH_SHORT).show()
                    }
                )

                // Seamless Native Ad Insertion every 4-5 posts
                if (activeAds.isNotEmpty() && (index + 1) % adFrequencyInterval == 0) {
                    val adIndex = ((index + 1) / adFrequencyInterval - 1) % activeAds.size
                    val currentAd = activeAds[adIndex]
                    Spacer(modifier = Modifier.height(8.dp))
                    NativeAdCard(
                        ad = currentAd,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

        // Show Comments Dialog Sheet
        if (selectedPostForComments != null) {
            CommentSectionDialog(
                comments = comments,
                onDismissRequest = {
                    selectedPostForComments = null
                    viewModel.selectPostForComments(null)
                },
                onSendComment = { text ->
                    selectedPostForComments?.let { post ->
                        viewModel.addComment(post.id, text)
                        // Update local object commentCount
                        selectedPostForComments = post.copy(commentCount = post.commentCount + 1)
                    }
                }
            )
        }

        // Show Create Post Dialog Box
        if (showCreatePostDialog) {
            CreatePostDialog(
                currentUser = currentUser,
                onDismiss = { showCreatePostDialog = false },
                onPostSubmitted = { content, image ->
                    viewModel.createPost(content, image) { success ->
                        if (success) {
                            showCreatePostDialog = false
                            Toast.makeText(context, "পোস্ট সফলভাবে প্রকাশিত হয়েছে!", Toast.LENGTH_SHORT).show()
                        } else {
                            showPostLimitWarningDialog = "দৈনিক লিমিট"
                            Toast.makeText(context, "⚠️ দৈনিক পোস্ট লিমিট (${revenueConfig.dailyUserPostLimit}টি) পূর্ণ হয়েছে!", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            )
        }

        // 4. Official Notice Popup (Appears in front of user on enter, dismissed ONLY on cross click or acknowledge)
        if (officialNotice.isActive && dismissedNoticeId != officialNotice.id) {
            Dialog(
                onDismissRequest = {
                    // Do not auto-dismiss on outside touch per requirement: "ইউজার ক্রস আইকন দিয়ে রিমুভ করলে তার পর রিমুভ হবে"
                },
                properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        // Header with urgency and cross icon
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val (badgeBg, badgeTextColor, urgencyText) = when (officialNotice.urgency) {
                                "URGENT" -> Triple(Color(0xFFFFEBEE), Color(0xFFD32F2F), "জরুরি বিজ্ঞপ্তি")
                                "IMPORTANT" -> Triple(Color(0xFFFFF3E0), Color(0xFFE65100), "গুরুত্বপূর্ণ নোটিশ")
                                else -> Triple(Color(0xFFE8F5E9), Color(0xFF2E7D32), "অফিসিয়াল নোটিশ")
                            }

                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = badgeBg,
                                border = androidx.compose.foundation.BorderStroke(1.dp, badgeTextColor.copy(alpha = 0.4f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Campaign,
                                        contentDescription = null,
                                        tint = badgeTextColor,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = urgencyText,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = badgeTextColor
                                    )
                                }
                            }

                            // Prominent Cross Button to Dismiss
                            IconButton(
                                onClick = {
                                    dismissedNoticeId = officialNotice.id
                                },
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "বন্ধ করুন (Close)",
                                    tint = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Official Seal / Title
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF1B4D3E)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = null,
                                    tint = Color(0xFF81C784),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = officialNotice.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    lineHeight = 22.sp
                                )
                                Text(
                                    text = "নবচেতনা সেন্ট্রাল ডিরেক্টরেট • ${officialNotice.date}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Notice Content Box
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                            border = androidx.compose.foundation.BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Text(
                                text = officialNotice.message,
                                fontSize = 13.sp,
                                lineHeight = 20.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(14.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Action / Acknowledge Button
                        Button(
                            onClick = {
                                dismissedNoticeId = officialNotice.id
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B4D3E))
                        ) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("বিজ্ঞপ্তিটি পড়েছি ও সম্মত (Acknowledge)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }
        }

        // 5. Post Limit Exceeded Dialog
        if (showPostLimitWarningDialog != null) {
            AlertDialog(
                onDismissRequest = { showPostLimitWarningDialog = null },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = Color(0xFFE65100),
                        modifier = Modifier.size(32.dp)
                    )
                },
                title = {
                    Text("দৈনিক পোস্ট লিমিট পূর্ণ হয়েছে", fontWeight = FontWeight.Bold)
                },
                text = {
                    Column {
                        Text(
                            text = "নবচেতনার স্প্যাম প্রতিরোধ নীতিমালা অনুযায়ী এডমিন কর্তৃক ব্যবহারকারীদের জন্য দৈনিক সর্বোচ্চ ${revenueConfig.dailyUserPostLimit}টি পোস্টের সীমা নির্ধারণ করা হয়েছে।",
                            fontSize = 13.sp,
                            lineHeight = 19.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "আজকের জন্য আপনার কোটা পূর্ণ হয়েছে। আগামীকাল পুনরায় নতুন কন্টেন্ট শেয়ার করতে পারবেন।",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { showPostLimitWarningDialog = null },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B4D3E))
                    ) {
                        Text("বুঝেছি")
                    }
                }
            )
        }
    }
}

@Composable
fun QuickActionBarButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    label: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = label, tint = iconTint, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = label, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun CreatePostDialog(
    currentUser: User?,
    onDismiss: () -> Unit,
    onPostSubmitted: (String, String?) -> Unit
) {
    var text by remember { mutableStateOf("") }
    var selectedBackgroundBrush by remember { mutableStateOf<Brush?>(null) }
    var selectedPhotoUrl by remember { mutableStateOf<String?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            selectedPhotoUrl = uri.toString()
            selectedBackgroundBrush = null
        }
    }

    // Custom colorful solid/gradient backgrounds
    val gradientColors = listOf(
        null, // Normal white background
        Brush.linearGradient(listOf(Color(0xFF833AB4), Color(0xFFFD1D1D), Color(0xFFF77737))), // Sunset
        Brush.linearGradient(listOf(Color(0xFF2193b0), Color(0xFF6dd5ed))), // Cool Blue
        Brush.linearGradient(listOf(Color(0xFF11998e), Color(0xFF38ef7d))), // Green Mint
        Brush.linearGradient(listOf(Color(0xFFFF416C), Color(0xFFFF4B2B)))  // Passion Red
    )

    // Stock high-quality Unsplash post image choices
    val postStockPhotos = listOf(
        "https://images.unsplash.com/photo-1506744038136-46273834b3fb?w=600", // Valley
        "https://images.unsplash.com/photo-1513836279014-a89f7a76ae86?w=600", // Nature Forest
        "https://images.unsplash.com/photo-1498050108023-c5249f4df085?w=600", // Tech workspace
        "https://images.unsplash.com/photo-1517256064527-09c53b2d0bc6?w=600"  // Cafe Tea
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top Action Bar
                Row(
                    modifier = Modifier.fillMaxWidth().statusBarsPadding().padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onDismiss) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = MaterialTheme.colorScheme.onSurface)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Create Post", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    }

                    Button(
                        onClick = { onPostSubmitted(text, selectedPhotoUrl) },
                        enabled = text.isNotBlank() || selectedPhotoUrl != null,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1B4D3E),
                            contentColor = Color.White
                        )
                    ) {
                        Text(text = "Post", fontWeight = FontWeight.Bold)
                    }
                }

                Divider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 0.5.dp)

                // User Info Row
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(
                            model = currentUser?.avatarUrl ?: "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150",
                            contentDescription = "My Avatar",
                            modifier = Modifier.size(45.dp).clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(text = currentUser?.name ?: "User", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(imageVector = Icons.Default.LocationOn, contentDescription = "Public", modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "Public", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }

                    // Daily post quota badge
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF1976D2).copy(alpha = 0.1f),
                        border = BorderStroke(1.dp, Color(0xFF1976D2).copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Speed, contentDescription = null, tint = Color(0xFF1976D2), modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "দৈনিক লিমিট সক্রিয়",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1976D2)
                            )
                        }
                    }
                }

                // Input Content Area
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 16.dp)
                ) {
                    if (selectedBackgroundBrush != null) {
                        // Colored Facebook background block
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(selectedBackgroundBrush!!),
                            contentAlignment = Alignment.Center
                        ) {
                            TextField(
                                value = text,
                                onValueChange = { text = it },
                                placeholder = { Text("What's on your mind?", color = Color.White.copy(alpha = 0.7f), fontSize = 20.sp) },
                                textStyle = MaterialTheme.typography.headlineMedium.copy(color = Color.White, textAlign = androidx.compose.ui.text.style.TextAlign.Center, fontWeight = FontWeight.Bold),
                                modifier = Modifier.fillMaxWidth().padding(24.dp),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    disabledIndicatorColor = Color.Transparent
                                )
                            )
                        }
                    } else {
                        // Standard layout
                        Column(modifier = Modifier.fillMaxSize()) {
                            TextField(
                                value = text,
                                onValueChange = { text = it },
                                placeholder = { Text("What's on your mind?", fontSize = 18.sp) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    disabledIndicatorColor = Color.Transparent
                                ),
                                textStyle = MaterialTheme.typography.bodyLarge.copy(fontSize = 17.sp)
                            )

                            if (selectedPhotoUrl != null) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .padding(top = 10.dp)
                                ) {
                                    AsyncImage(
                                        model = selectedPhotoUrl,
                                        contentDescription = "Selected Photo",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                    IconButton(
                                        onClick = { selectedPhotoUrl = null },
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .padding(8.dp)
                                            .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                                            .size(30.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.Close, contentDescription = "Remove Photo", tint = Color.White, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }
                }

                // Bottom Styling Helpers (Grid Colors, Stock Photos selection)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                        .padding(16.dp)
                ) {
                    // Option 1: Select Background Colors (Gradients)
                    Text(text = "Choose Background Color", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    LazyRow(
                        modifier = Modifier.padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(gradientColors) { brush ->
                            val borderMod = if (selectedBackgroundBrush == brush) {
                                Modifier.border(2.dp, Color(0xFF1B4D3E), CircleShape)
                            } else Modifier

                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .then(borderMod)
                                    .clickable {
                                        selectedBackgroundBrush = brush
                                        selectedPhotoUrl = null // reset image if background is chosen
                                    }
                            ) {
                                if (brush == null) {
                                    Icon(
                                        imageVector = Icons.Default.Palette,
                                        contentDescription = "Reset Color",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.align(Alignment.Center).size(20.dp)
                                    )
                                } else {
                                    Box(modifier = Modifier.fillMaxSize().background(brush))
                                }
                            }
                        }
                    }

                    // Option 2: Select from local Gallery
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B4D3E)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.Image, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Add Photo (Telegram Cloud CDN Protected)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFF0288D1).copy(alpha = 0.1f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Default.CloudSync, contentDescription = null, tint = Color(0xFF0288D1), modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "৬-বট ক্লাউড সিডিএন ক্লাস্টার সক্রিয় • $0 আনলিমিটেড স্টোরেজ",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF0288D1)
                            )
                        }
                    }
                }
            }
        }
    }
}
