package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.User
import com.example.ui.components.WalletRechargeModalDialog
import com.example.ui.viewmodel.PageBookViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class WatchComment(
    val id: String,
    val authorName: String,
    val authorAvatar: String,
    val commentText: String,
    val timestamp: String,
    var likes: Int = 0,
    var isLiked: Boolean = false
)

data class WatchVideo(
    val id: Int,
    val pageName: String,
    val authorHandle: String,
    val pageAvatar: String,
    val title: String,
    val tags: List<String>,
    val musicTrack: String,
    val views: String,
    val timestamp: String,
    val thumbnail: String,
    val duration: String,
    var likeCount: Int,
    var commentCount: Int,
    var shareCount: Int,
    var bookmarkCount: Int,
    var isLiked: Boolean = false,
    var isBookmarked: Boolean = false,
    var isFollowing: Boolean = false,
    val comments: MutableList<WatchComment> = mutableListOf()
)

data class HeartBurst(
    val id: Long,
    val x: Float,
    val y: Float
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WatchScreen(
    modifier: Modifier = Modifier,
    viewModel: PageBookViewModel? = null,
    currentUser: User? = null
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val coroutineScope = rememberCoroutineScope()

    // Sample TikTok-style short videos list
    val videoList = remember {
        mutableStateListOf(
            WatchVideo(
                id = 1,
                pageName = "Tech Bangla 🇧🇩",
                authorHandle = "@techbangla_official",
                pageAvatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150",
                title = "ভবিষ্যতের ৫টি অবিশ্বাস্য AI প্রযুক্তি যা জীবন বদলে দিচ্ছে! ⚡🧠 রোবটিক্স এবং স্বয়ংক্রিয় এআই টুলস।",
                tags = listOf("#tech", "#future", "#bangladesh", "#viral", "#ai"),
                musicTrack = "Original Audio - Tech Bangla Tech Vibes 🎵",
                views = "240K",
                timestamp = "২ ঘণ্টা আগে",
                thumbnail = "https://images.unsplash.com/photo-1485827404703-89b55fcc595e?w=800",
                duration = "00:45",
                likeCount = 24150,
                commentCount = 1840,
                shareCount = 942,
                bookmarkCount = 3850,
                isLiked = false,
                isFollowing = false,
                comments = mutableListOf(
                    WatchComment("c1", "মাহমুদুল হাসান", "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150", "অসাধারণ তথ্য ভাই! অনেক ভালো লাগলো।", "১০ মি. আগে", 34),
                    WatchComment("c2", "ফারহানা আক্তার", "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=150", "পরের পার্ট কবে আসবে? অপেক্ষায় রইলাম! 🔥", "২৫ মি. আগে", 18),
                    WatchComment("c3", "আসিফ ইকবাল", "https://images.unsplash.com/photo-1570295999919-56ceb5ecca61?w=150", "বাংলায় এত নিখুঁত উপস্থাপনা সচরাচর দেখা যায় না। ধন্যবাদ নবচেতনা!", "১ ঘণ্টা আগে", 52)
                )
            ),
            WatchVideo(
                id = 2,
                pageName = "ভ্রমণকাহিনী (Vromon Kahini)",
                authorHandle = "@vromon_sajek",
                pageAvatar = "https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=150",
                title = "সাজেক ভ্যালির মেঘের ভেতর দিয়ে ভোরের এক অবিশ্বাস্য দৃশ্য! ⛰️☁️🌲 যেন স্বপ্নের রাজ্য!",
                tags = listOf("#sajek", "#cloud", "#travel", "#bangladesh", "#nature"),
                musicTrack = "♫ সুরের ধারা - পাহাড়ের গান (Acoustic Folk)",
                views = "520K",
                timestamp = "৫ ঘণ্টা আগে",
                thumbnail = "https://images.unsplash.com/photo-1506744038136-46273834b3fb?w=800",
                duration = "00:58",
                likeCount = 85200,
                commentCount = 4210,
                shareCount = 2390,
                bookmarkCount = 15300,
                isLiked = true,
                isFollowing = true,
                comments = mutableListOf(
                    WatchComment("c4", "তানভীর রহমান", "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=150", "সাজেক যাওয়ার স্বপ্ন দীর্ঘদিনের, ভিডিওটা দেখে মন ভরে গেল ❤️", "৩ ঘণ্টা আগে", 89),
                    WatchComment("c5", "নুসরাত জাহান", "https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=150", "মেঘের দেশ সাজেক! অসাধারণ সিনেমাটোগ্রাফি ☁️", "৪ ঘণ্টা আগে", 41)
                )
            ),
            WatchVideo(
                id = 3,
                pageName = "ভোজনরসিক (Food Explorer)",
                authorHandle = "@foodie_dhaka",
                pageAvatar = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=150",
                title = "পুরান ঢাকার ঐতিহ্যবাহী কাচ্চি বিরিয়ানি ও স্পেশাল বোরহানি রিভিউ! 🍛🤤 সুবাসেই ক্ষুধা বেড়ে যায়!",
                tags = listOf("#dhaka", "#foodie", "#kacchi", "#biryani", "#olddhaka"),
                musicTrack = "♫ Street Food Beats - Dhaka Food Festival 2026",
                views = "310K",
                timestamp = "গতকাল",
                thumbnail = "https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8?w=800",
                duration = "00:42",
                likeCount = 38900,
                commentCount = 1450,
                shareCount = 820,
                bookmarkCount = 4900,
                isLiked = false,
                isFollowing = false,
                comments = mutableListOf(
                    WatchComment("c6", "সাকিব আল হাসান", "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150", "লোকেশনটা কোথায় ভাই? দাম কত প্লেট?", "১ দিন আগে", 15),
                    WatchComment("c7", "নাদিয়া ইসলাম", "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=150", "দেখেই তো জিভে জল চলে এলো! 🤤", "১ দিন আগে", 27)
                )
            ),
            WatchVideo(
                id = 4,
                pageName = "নবচেতনা সমাজ উন্নয়ন ✊",
                authorHandle = "@nobochetna_youth",
                pageAvatar = "https://images.unsplash.com/photo-1522075469751-3a6694fb2f61?w=150",
                title = "দুর্নীতিমুক্ত সমাজ ও তারুণ্যের ঐক্য গড়ার শপথ! 🇧🇩 সচেতন নাগরিক হিসেবে এগিয়ে আসুন।",
                tags = listOf("#nobochetna", "#youthpower", "#bangladesh", "#awareness"),
                musicTrack = "♫ দেশাত্মবোধক সুর - নবচেতনা থিম সঙ্গীত",
                views = "890K",
                timestamp = "২ দিন আগে",
                thumbnail = "https://images.unsplash.com/photo-1529156069898-49953e39b3ac?w=800",
                duration = "01:00",
                likeCount = 98400,
                commentCount = 6800,
                shareCount = 5400,
                bookmarkCount = 19400,
                isLiked = false,
                isFollowing = true,
                comments = mutableListOf(
                    WatchComment("c8", "কাজী নজরুল", "https://images.unsplash.com/photo-1570295999919-56ceb5ecca61?w=150", "আমরা তরুণ প্রজন্ম এক সাথে এগিয়ে যাবো ইনশাআল্লাহ!", "২ দিন আগে", 120)
                )
            ),
            WatchVideo(
                id = 5,
                pageName = "ক্রিকেট ম্যানিয়া (Cricket Mania)",
                authorHandle = "@bdcricket_fever",
                pageAvatar = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150",
                title = "শেষ ওভারে ৬ বলে ১৬ রান! রুদ্ধশ্বাস ম্যাচে অবিস্মরণীয় জয় উদযাপন! 🏏🔥🏆",
                tags = listOf("#cricket", "#tigers", "#bangladesh", "#matchwin", "#lastover"),
                musicTrack = "♫ গর্জে ওঠো বাংলাদেশ - স্টেডিয়াম ক্রাউড সাউন্ড",
                views = "1.2M",
                timestamp = "৩ দিন আগে",
                thumbnail = "https://images.unsplash.com/photo-1540747913346-19e32dc3e97e?w=800",
                duration = "00:50",
                likeCount = 145000,
                commentCount = 9200,
                shareCount = 11200,
                bookmarkCount = 28000,
                isLiked = true,
                isFollowing = false,
                comments = mutableListOf(
                    WatchComment("c9", "রাকিবুল হাসান", "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150", "টাইগারদের খেলা দেখে গায়ের রোম খাঁড়া হয়ে গেল! সাবাশ!", "৩ দিন আগে", 230)
                )
            )
        )
    }

    // TikTok Top Tabs: "অনুসরণ" (Following) vs "আপনার জন্য" (For You)
    var selectedFeedTab by remember { mutableStateOf(1) } // 0 = Following, 1 = For You

    // Vertical Pager State for TikTok-style 1-by-1 snapping
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { videoList.size })

    // Active bottom sheet modal states
    var activeCommentVideo by remember { mutableStateOf<WatchVideo?>(null) }
    var activeShareVideo by remember { mutableStateOf<WatchVideo?>(null) }
    var activeGiftVideo by remember { mutableStateOf<WatchVideo?>(null) }
    var showCreateVideoModal by remember { mutableStateOf(false) }
    var showWalletRechargeModal by remember { mutableStateOf(false) }

    // Search query state
    var showSearchDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Vertical Pager - TikTok Experience (one video per screen)
        VerticalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            key = { index -> videoList.getOrNull(index)?.id ?: index }
        ) { pageIndex ->
            val video = videoList.getOrNull(pageIndex)
            if (video != null) {
                val isCurrentPage = pagerState.currentPage == pageIndex

                TikTokVideoPage(
                    video = video,
                    isCurrentPage = isCurrentPage,
                    onOpenComments = { activeCommentVideo = video },
                    onOpenShare = { activeShareVideo = video },
                    onOpenGift = { activeGiftVideo = video },
                    onToggleFollow = {
                        video.isFollowing = !video.isFollowing
                        val msg = if (video.isFollowing) "${video.pageName} কে অনুসরণ করা হয়েছে!" else "অনুসরণ বাতিল করা হয়েছে"
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    },
                    onToggleLike = {
                        video.isLiked = !video.isLiked
                        if (video.isLiked) video.likeCount++ else video.likeCount--
                    },
                    onToggleBookmark = {
                        video.isBookmarked = !video.isBookmarked
                        if (video.isBookmarked) video.bookmarkCount++ else video.bookmarkCount--
                        Toast.makeText(context, if (video.isBookmarked) "সংরক্ষণ করা হয়েছে! 🔖" else "সংরক্ষণ বাতিল", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }

        // Top Navigation Header (Floating over video)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(top = 8.dp, start = 12.dp, end = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Live Stream shortcut
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.Black.copy(alpha = 0.45f))
                        .clickable {
                            Toast.makeText(context, "লাইভ স্ট্রিমিং শীঘ্রই শুরু হচ্ছে... 🔴", Toast.LENGTH_SHORT).show()
                        }
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color.Red)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "লাইভ",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Center Tabs: অনুসরণ | আপনার জন্য
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable {
                                selectedFeedTab = 0
                                Toast.makeText(context, "অনুসরণকৃত ক্রিয়েটরদের ফিড", Toast.LENGTH_SHORT).show()
                            }
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "অনুসরণ",
                            fontSize = 16.sp,
                            fontWeight = if (selectedFeedTab == 0) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedFeedTab == 0) Color.White else Color.White.copy(alpha = 0.6f)
                        )
                        if (selectedFeedTab == 0) {
                            Box(
                                modifier = Modifier
                                    .padding(top = 4.dp)
                                    .width(28.dp)
                                    .height(2.5.dp)
                                    .background(Color.White, RoundedCornerShape(2.dp))
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(14.dp)
                            .background(Color.White.copy(alpha = 0.3f))
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable {
                                selectedFeedTab = 1
                            }
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "আপনার জন্য",
                            fontSize = 17.sp,
                            fontWeight = if (selectedFeedTab == 1) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedFeedTab == 1) Color.White else Color.White.copy(alpha = 0.6f)
                        )
                        if (selectedFeedTab == 1) {
                            Box(
                                modifier = Modifier
                                    .padding(top = 4.dp)
                                    .width(36.dp)
                                    .height(2.5.dp)
                                    .background(Color(0xFF2E7D32), RoundedCornerShape(2.dp))
                            )
                        }
                    }
                }

                // Right Search & Add Video Buttons
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { showSearchDialog = true },
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color.Black.copy(alpha = 0.45f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search Videos",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(
                        onClick = { showCreateVideoModal = true },
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color(0xFF1B4D3E), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Upload Reel",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }

        // TikTok Comments Bottom Sheet
        if (activeCommentVideo != null) {
            val video = activeCommentVideo!!
            ModalBottomSheet(
                onDismissRequest = { activeCommentVideo = null },
                containerColor = Color(0xFF18191A),
                contentColor = Color.White,
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
            ) {
                TikTokCommentsSheetContent(
                    video = video,
                    currentUser = currentUser,
                    onDismiss = { activeCommentVideo = null }
                )
            }
        }

        // TikTok Share Bottom Sheet
        if (activeShareVideo != null) {
            val video = activeShareVideo!!
            ModalBottomSheet(
                onDismissRequest = { activeShareVideo = null },
                containerColor = Color(0xFF1E1E1E),
                contentColor = Color.White,
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
            ) {
                TikTokShareSheetContent(
                    video = video,
                    onCopyLink = {
                        clipboardManager.setText(AnnotatedString("https://nobachetna.app/watch/${video.id}"))
                        Toast.makeText(context, "ভিডিও লিংক কপি করা হয়েছে! 📋", Toast.LENGTH_SHORT).show()
                        activeShareVideo = null
                    },
                    onShareTo = { platform ->
                        Toast.makeText(context, "$platform এ শেয়ার করা হচ্ছে...", Toast.LENGTH_SHORT).show()
                        video.shareCount++
                        activeShareVideo = null
                    },
                    onDownload = {
                        Toast.makeText(context, "ভিডিও ডাউনলোড শুরু হয়েছে... 📥", Toast.LENGTH_SHORT).show()
                        activeShareVideo = null
                    }
                )
            }
        }

        // Creator Gift Modal (integrated with Nabachetna digital wallet)
        if (activeGiftVideo != null) {
            val video = activeGiftVideo!!
            ModalBottomSheet(
                onDismissRequest = { activeGiftVideo = null },
                containerColor = Color(0xFF1A1A1A),
                contentColor = Color.White,
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
            ) {
                TikTokGiftSheetContent(
                    video = video,
                    currentUser = currentUser,
                    onSendGift = { giftName, cost ->
                        val currentBalance = currentUser?.walletBalance ?: 500.0
                        if (currentBalance >= cost) {
                            Toast.makeText(context, "অভিনন্দন! আপনি ${video.pageName} কে $giftName (৳$cost) উপহার পাঠিয়েছেন! 🎁🎉", Toast.LENGTH_LONG).show()
                            activeGiftVideo = null
                        } else {
                            Toast.makeText(context, "অপর্যাপ্ত ব্যালেন্স! ওয়ালেটে টাকা রিচার্জ করুন。", Toast.LENGTH_SHORT).show()
                            showWalletRechargeModal = true
                        }
                    },
                    onRecharge = {
                        showWalletRechargeModal = true
                    }
                )
            }
        }

        // Search Videos Dialog
        if (showSearchDialog) {
            AlertDialog(
                onDismissRequest = { showSearchDialog = false },
                containerColor = Color(0xFF222222),
                title = {
                    Text("ভিডিও অনুসন্ধান করুন 🔍", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                },
                text = {
                    Column {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("কীওয়ার্ড বা হ্যাশট্যাগ (#tech, #sajek)...", color = Color.Gray, fontSize = 13.sp) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFF2E7D32),
                                unfocusedBorderColor = Color.Gray
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("জনপ্রিয় ট্রেন্ডস:", color = Color.White.copy(alpha = 0.8f), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(listOf("#viral", "#sajek", "#nobochetna", "#dhakafood", "#tigers")) { tag ->
                                SuggestionChip(
                                    onClick = {
                                        searchQuery = tag
                                    },
                                    label = { Text(tag, color = Color.White, fontSize = 12.sp) },
                                    colors = SuggestionChipDefaults.suggestionChipColors(
                                        containerColor = Color(0xFF333333)
                                    )
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (searchQuery.isNotBlank()) {
                                Toast.makeText(context, "'$searchQuery' দিয়ে সার্চ করা হচ্ছে...", Toast.LENGTH_SHORT).show()
                                showSearchDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B4D3E))
                    ) {
                        Text("সার্চ", color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showSearchDialog = false }) {
                        Text("বাতিল", color = Color.LightGray)
                    }
                }
            )
        }

        // Upload Reel Dialog
        if (showCreateVideoModal) {
            var newTitle by remember { mutableStateOf("") }
            var newTag by remember { mutableStateOf("#nobochetna #reel") }
            AlertDialog(
                onDismissRequest = { showCreateVideoModal = false },
                containerColor = Color(0xFF222222),
                title = {
                    Text("নতুন শর্ট ভিডিও / রিল আপলোড 🎬", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("ভিডিও ক্যাপশন লিখুন:", color = Color.LightGray, fontSize = 13.sp)
                        OutlinedTextField(
                            value = newTitle,
                            onValueChange = { newTitle = it },
                            placeholder = { Text("আপনার আকর্ষণীয় ক্যাপশন...", color = Color.Gray, fontSize = 13.sp) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFF2E7D32),
                                unfocusedBorderColor = Color.Gray
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = newTag,
                            onValueChange = { newTag = it },
                            label = { Text("হ্যাশট্যাগ", color = Color.LightGray) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFF2E7D32),
                                unfocusedBorderColor = Color.Gray
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (newTitle.isNotBlank()) {
                                val newVid = WatchVideo(
                                    id = (videoList.maxOfOrNull { it.id } ?: 0) + 1,
                                    pageName = currentUser?.fullName ?: "আমার চ্যানেল",
                                    authorHandle = "@user_${(1000..9999).random()}",
                                    pageAvatar = currentUser?.avatarUrl ?: "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150",
                                    title = newTitle,
                                    tags = newTag.split(" ").filter { it.isNotBlank() },
                                    musicTrack = "Original Audio - ${currentUser?.fullName ?: "Nobochetna User"} 🎵",
                                    views = "1",
                                    timestamp = "এখনই",
                                    thumbnail = "https://images.unsplash.com/photo-1518770660439-4636190af475?w=800",
                                    duration = "00:30",
                                    likeCount = 1,
                                    commentCount = 0,
                                    shareCount = 0,
                                    bookmarkCount = 0,
                                    isLiked = true,
                                    isFollowing = true
                                )
                                videoList.add(0, newVid)
                                coroutineScope.launch {
                                    pagerState.scrollToPage(0)
                                }
                                Toast.makeText(context, "আপনার রিল সফলভাবে পাবলিশ হয়েছে! 🚀", Toast.LENGTH_LONG).show()
                                showCreateVideoModal = false
                            } else {
                                Toast.makeText(context, "দয়া করে ক্যাপশন লিখুন", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B4D3E))
                    ) {
                        Text("পাবলিশ করুন", color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showCreateVideoModal = false }) {
                        Text("বাতিল", color = Color.LightGray)
                    }
                }
            )
        }

        // Integrated Wallet Recharge Modal
        if (showWalletRechargeModal) {
            WalletRechargeModalDialog(
                isOpen = showWalletRechargeModal,
                onDismiss = { showWalletRechargeModal = false },
                viewModel = viewModel,
                currentUser = currentUser
            )
        }
    }
}

/**
 * Single TikTok Video Page (Full-screen vertical viewport)
 */
@Composable
fun TikTokVideoPage(
    video: WatchVideo,
    isCurrentPage: Boolean,
    onOpenComments: () -> Unit,
    onOpenShare: () -> Unit,
    onOpenGift: () -> Unit,
    onToggleFollow: () -> Unit,
    onToggleLike: () -> Unit,
    onToggleBookmark: () -> Unit
) {
    var isPlaying by remember { mutableStateOf(true) }
    var showPauseOverlay by remember { mutableStateOf(false) }
    var isCaptionExpanded by remember { mutableStateOf(false) }

    // Floating heart bursts on double-tap
    val heartBursts = remember { mutableStateListOf<HeartBurst>() }

    // Simulated playback progress
    var progress by remember { mutableFloatStateOf(0.15f) }

    // Continuous progress loop when current page and playing
    LaunchedEffect(isCurrentPage, isPlaying) {
        if (isCurrentPage && isPlaying) {
            while (true) {
                delay(100)
                progress += 0.005f
                if (progress >= 1.0f) {
                    progress = 0f // Loop playback
                }
            }
        }
    }

    // Disc rotation animation
    val infiniteTransition = rememberInfiniteTransition(label = "VinylDisc")
    val discRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "DiscAngle"
    )

    // Musical note floating animation
    val noteOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -40f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "NoteFloat"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        isPlaying = !isPlaying
                        showPauseOverlay = true
                    },
                    onDoubleTap = { offset ->
                        // Double tap likes the video and spawns heart animation
                        if (!video.isLiked) {
                            onToggleLike()
                        }
                        heartBursts.add(HeartBurst(System.currentTimeMillis(), offset.x, offset.y))
                    }
                )
            }
    ) {
        // High Quality Full-Screen Vertical Media Visual
        AsyncImage(
            model = video.thumbnail,
            contentDescription = video.title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Gradient Shading for Text Legibility (Top & Bottom Vignettes)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.55f),
                            Color.Transparent,
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.85f)
                        )
                    )
                )
        )

        // Double-Tap Animated Hearts
        heartBursts.forEach { burst ->
            key(burst.id) {
                FloatingHeartAnimation(
                    x = burst.x,
                    y = burst.y,
                    onAnimationEnd = { heartBursts.remove(burst) }
                )
            }
        }

        // Center Play / Pause Indicator on Tap
        if (!isPlaying || showPauseOverlay) {
            LaunchedEffect(showPauseOverlay) {
                if (showPauseOverlay && isPlaying) {
                    delay(700)
                    showPauseOverlay = false
                }
            }

            AnimatedVisibility(
                visible = !isPlaying || showPauseOverlay,
                enter = scaleIn(tween(150)) + fadeIn(),
                exit = scaleOut(tween(200)) + fadeOut(),
                modifier = Modifier.align(Alignment.Center)
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.6f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (!isPlaying) Icons.Filled.PlayArrow else Icons.Filled.Pause,
                        contentDescription = "Playback State",
                        tint = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier.size(44.dp)
                    )
                }
            }
        }

        // Right-Side TikTok Action Bar
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 12.dp, bottom = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Author Avatar with Follow "+" Badge
            Box(
                contentAlignment = Alignment.BottomCenter,
                modifier = Modifier.padding(bottom = 6.dp)
            ) {
                AsyncImage(
                    model = video.pageAvatar,
                    contentDescription = video.pageName,
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .border(1.5.dp, Color.White, CircleShape),
                    contentScale = ContentScale.Crop
                )

                // Follow Badge
                if (!video.isFollowing) {
                    Box(
                        modifier = Modifier
                            .offset(y = 10.dp)
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFE2C55)) // TikTok Red
                            .clickable { onToggleFollow() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Follow",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .offset(y = 10.dp)
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF2E7D32)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Following",
                            tint = Color.White,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }
            }

            // 2. Like Button (Heart)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onToggleLike() }
            ) {
                val scale by animateFloatAsState(
                    targetValue = if (video.isLiked) 1.25f else 1.0f,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                    label = "HeartBounce"
                )

                Icon(
                    imageVector = if (video.isLiked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = "Like",
                    tint = if (video.isLiked) Color(0xFFFE2C55) else Color.White,
                    modifier = Modifier
                        .size(34.dp)
                        .scale(scale)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = formatCount(video.likeCount),
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // 3. Comment Button
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onOpenComments() }
            ) {
                Icon(
                    imageVector = Icons.Filled.ChatBubble,
                    contentDescription = "Comments",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = formatCount(video.commentCount),
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // 4. Bookmark / Favorite Button
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onToggleBookmark() }
            ) {
                Icon(
                    imageVector = if (video.isBookmarked) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                    contentDescription = "Bookmark",
                    tint = if (video.isBookmarked) Color(0xFFFFB800) else Color.White,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = formatCount(video.bookmarkCount),
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // 5. Share Button
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onOpenShare() }
            ) {
                Icon(
                    imageVector = Icons.Filled.Share,
                    contentDescription = "Share",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = formatCount(video.shareCount),
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // 6. Gift Button (উপহার 🎁)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onOpenGift() }
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFFFF5722), Color(0xFFFF9800))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CardGiftcard,
                        contentDescription = "Gift Creator",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "উপহার",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // 7. Rotating Music Vinyl Disc with floating music note
            Box(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .size(46.dp),
                contentAlignment = Alignment.Center
            ) {
                // Floating Musical Note
                if (isPlaying) {
                    Text(
                        text = "♪",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 14.sp,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(y = noteOffset.dp, x = (-4).dp)
                    )
                }

                // Vinyl Outer Ring
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(Color(0xFF1E1E1E))
                        .border(6.dp, Color(0xFF111111), CircleShape)
                        .rotate(if (isPlaying) discRotation else 0f),
                    contentAlignment = Alignment.Center
                ) {
                    // Center Album Art
                    AsyncImage(
                        model = video.pageAvatar,
                        contentDescription = "Music Art",
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }

        // Bottom-Left TikTok Video Information (Author, Title, Hashtags, Music)
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth(0.76f)
                .padding(start = 16.dp, bottom = 26.dp)
        ) {
            // Author handle & Verified check
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = video.authorHandle,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = "Verified Creator",
                    tint = Color(0xFF20D5EC), // TikTok verified blue
                    modifier = Modifier.size(15.dp)
                )

                if (!video.isFollowing) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .border(1.dp, Color.White.copy(alpha = 0.7f), RoundedCornerShape(4.dp))
                            .clickable { onToggleFollow() }
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "Follow",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Caption / Title (Click to expand)
            Text(
                text = video.title,
                color = Color.White,
                fontSize = 13.5.sp,
                lineHeight = 18.sp,
                maxLines = if (isCaptionExpanded) 6 else 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.clickable { isCaptionExpanded = !isCaptionExpanded }
            )

            // Hashtags
            if (video.tags.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    video.tags.take(4).forEach { tag ->
                        Text(
                            text = tag,
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Audio / Music Ticker Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Black.copy(alpha = 0.35f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = "Music",
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = video.musicTrack,
                    color = Color.White,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // Bottom Progress Bar (TikTok Scrubber)
        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(2.dp),
            color = Color.White.copy(alpha = 0.9f),
            trackColor = Color.White.copy(alpha = 0.2f)
        )
    }
}

/**
 * Animated Floating Heart Burst for TikTok Double-Tap
 */
@Composable
fun FloatingHeartAnimation(
    x: Float,
    y: Float,
    onAnimationEnd: () -> Unit
) {
    val scale = remember { Animatable(0.2f) }
    val alpha = remember { Animatable(1f) }
    val offsetY = remember { Animatable(0f) }
    val rotation = remember { (-15..15).random().toFloat() }

    LaunchedEffect(Unit) {
        launch {
            scale.animateTo(
                targetValue = 1.3f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium)
            )
            scale.animateTo(1.0f, tween(100))
        }
        launch {
            offsetY.animateTo(-120f, tween(600, easing = FastOutSlowInEasing))
        }
        launch {
            delay(400)
            alpha.animateTo(0f, tween(250))
            onAnimationEnd()
        }
    }

    Box(
        modifier = Modifier
            .offset(x = (x / 2.7f).dp - 30.dp, y = (y / 2.7f).dp - 30.dp)
            .offset(y = offsetY.value.dp)
            .scale(scale.value)
            .rotate(rotation)
    ) {
        Icon(
            imageVector = Icons.Filled.Favorite,
            contentDescription = null,
            tint = Color(0xFFFE2C55).copy(alpha = alpha.value),
            modifier = Modifier.size(64.dp)
        )
    }
}

/**
 * Comments Bottom Sheet Content
 */
@Composable
fun TikTokCommentsSheetContent(
    video: WatchVideo,
    currentUser: User?,
    onDismiss: () -> Unit
) {
    var newCommentText by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.68f)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.size(24.dp))
            Text(
                text = "মন্তব্য (${video.comments.size + video.commentCount})",
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Color.LightGray,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Divider(color = Color.White.copy(alpha = 0.12f), thickness = 0.5.dp, modifier = Modifier.padding(vertical = 8.dp))

        // Comments List
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(video.comments) { comment ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    AsyncImage(
                        model = comment.authorAvatar,
                        contentDescription = comment.authorName,
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = comment.authorName,
                                color = Color.LightGray,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = comment.timestamp,
                                color = Color.Gray,
                                fontSize = 11.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = comment.commentText,
                            color = Color.White,
                            fontSize = 13.5.sp,
                            lineHeight = 18.sp
                        )
                    }

                    // Like comment
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable {
                            comment.isLiked = !comment.isLiked
                            if (comment.isLiked) comment.likes++ else comment.likes--
                        }
                    ) {
                        Icon(
                            imageVector = if (comment.isLiked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = "Like Comment",
                            tint = if (comment.isLiked) Color(0xFFFE2C55) else Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                        if (comment.likes > 0) {
                            Text(
                                text = "${comment.likes}",
                                color = Color.Gray,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }

        // Quick Emoji Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            listOf("❤️", "🔥", "👏", "😂", "😍", "🇧🇩", "🙌").forEach { emoji ->
                Text(
                    text = emoji,
                    fontSize = 20.sp,
                    modifier = Modifier
                        .clickable { newCommentText += emoji }
                        .padding(4.dp)
                )
            }
        }

        // Bottom Input Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(top = 4.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = currentUser?.avatarUrl ?: "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150",
                contentDescription = "Me",
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedTextField(
                value = newCommentText,
                onValueChange = { newCommentText = it },
                placeholder = { Text("একটি সুন্দর মন্তব্য লিখুন...", color = Color.Gray, fontSize = 13.sp) },
                singleLine = true,
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = Color(0xFF2B2B2B),
                    unfocusedContainerColor = Color(0xFF2B2B2B),
                    focusedBorderColor = Color(0xFF2E7D32),
                    unfocusedBorderColor = Color.Transparent
                ),
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = {
                    if (newCommentText.isNotBlank()) {
                        val comment = WatchComment(
                            id = "c_${System.currentTimeMillis()}",
                            authorName = currentUser?.fullName ?: "আপনি",
                            authorAvatar = currentUser?.avatarUrl ?: "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150",
                            commentText = newCommentText,
                            timestamp = "এইমাত্র",
                            likes = 0
                        )
                        video.comments.add(0, comment)
                        video.commentCount++
                        newCommentText = ""
                        Toast.makeText(context, "মন্তব্য পোস্ট করা হয়েছে! 💬", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0xFF1B4D3E), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Send",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

/**
 * TikTok Share Sheet Content
 */
@Composable
fun TikTokShareSheetContent(
    video: WatchVideo,
    onCopyLink: () -> Unit,
    onShareTo: (String) -> Unit,
    onDownload: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(16.dp)
    ) {
        Text(
            text = "ভিডিও শেয়ার করুন 📤",
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Share Destination Icons
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                ShareActionItem(
                    title = "কপি লিংক",
                    icon = Icons.Default.Link,
                    bgColor = Color(0xFF333333),
                    onClick = onCopyLink
                )
            }
            item {
                ShareActionItem(
                    title = "WhatsApp",
                    icon = Icons.Default.Send,
                    bgColor = Color(0xFF25D366),
                    onClick = { onShareTo("WhatsApp") }
                )
            }
            item {
                ShareActionItem(
                    title = "মেসেঞ্জার",
                    icon = Icons.Default.Chat,
                    bgColor = Color(0xFF0084FF),
                    onClick = { onShareTo("Messenger") }
                )
            }
            item {
                ShareActionItem(
                    title = "ফেসবুক",
                    icon = Icons.Default.Share,
                    bgColor = Color(0xFF1877F2),
                    onClick = { onShareTo("Facebook") }
                )
            }
            item {
                ShareActionItem(
                    title = "ডাউনলোড",
                    icon = Icons.Default.Download,
                    bgColor = Color(0xFF555555),
                    onClick = onDownload
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun ShareActionItem(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    bgColor: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(bgColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = title,
            color = Color.LightGray,
            fontSize = 11.sp
        )
    }
}

/**
 * TikTok Gift Sheet Content (Sends gifts with wallet balance)
 */
@Composable
fun TikTokGiftSheetContent(
    video: WatchVideo,
    currentUser: User?,
    onSendGift: (giftName: String, cost: Double) -> Unit,
    onRecharge: () -> Unit
) {
    val gifts = listOf(
        Triple("গোলাপ ফুল 🌹", 10.0, "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=120"),
        Triple("গরম চা ☕", 20.0, "https://images.unsplash.com/photo-1544787219-7f47ccb76574?w=120"),
        Triple("গোল্ড স্টার ⭐", 50.0, "https://images.unsplash.com/photo-1534447677768-be436bb09401?w=120"),
        Triple("রয়্যাল ক্রাউন 👑", 100.0, "https://images.unsplash.com/photo-1579783902614-a3fb3927b675?w=120"),
        Triple("হীরার নেকলেস 💎", 250.0, "https://images.unsplash.com/photo-1515562141207-7a88fb7ce338?w=120"),
        Triple("সুপার ফায়ার 🔥", 500.0, "https://images.unsplash.com/photo-1517594422361-5eeb8ae275a9?w=120")
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "ক্রিয়েটরকে উপহার পাঠান 🎁",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "ব্যালেন্স: ৳${String.format("%.2f", currentUser?.walletBalance ?: 500.0)}",
                    color = Color(0xFF4CAF50),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            OutlinedButton(
                onClick = onRecharge,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF2E7D32)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2E7D32)),
                shape = RoundedCornerShape(16.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text("রিচার্জ 💳", fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 3x2 Grid of Gifts
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(gifts) { (giftName, cost, _) ->
                Card(
                    modifier = Modifier
                        .width(105.dp)
                        .clickable { onSendGift(giftName, cost) },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF262626))
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = giftName.split(" ").lastOrNull() ?: "🎁",
                            fontSize = 32.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = giftName.split(" ").firstOrNull() ?: giftName,
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "৳${cost.toInt()}",
                            color = Color(0xFFFFB800),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
    }
}

/**
 * Format count helper (e.g. 14200 -> 14.2K)
 */
fun formatCount(count: Int): String {
    return when {
        count >= 1_000_000 -> String.format("%.1fM", count / 1_000_000.0)
        count >= 1_000 -> String.format("%.1fK", count / 1_000.0)
        else -> count.toString()
    }
}
