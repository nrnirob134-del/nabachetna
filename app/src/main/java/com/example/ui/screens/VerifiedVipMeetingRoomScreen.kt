package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.data.model.User
import com.example.ui.theme.TeaLeafGreen
import com.example.ui.viewmodel.PageBookViewModel
import kotlinx.coroutines.delay

data class VipSpeaker(
    val id: String,
    val name: String,
    val roleTitle: String,
    val avatarUrl: String,
    val isCompanyOfficial: Boolean = false,
    val isSpeaking: Boolean = false,
    val isMuted: Boolean = false
)

data class VipProposal(
    val id: String,
    val authorName: String,
    val authorAvatar: String,
    val text: String,
    val timestamp: String,
    var upvotes: Int = 0,
    var isUpvotedByMe: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerifiedVipMeetingRoomScreen(
    currentUser: User?,
    onBack: () -> Unit,
    viewModel: PageBookViewModel? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isVerified = currentUser?.isVerified == true

    // If user is not verified, show elite security lock screen
    if (!isVerified) {
        UnverifiedAccessDeniedView(onBack = onBack)
        return
    }

    var isMicOn by remember { mutableStateOf(false) }
    var isHandRaised by remember { mutableStateOf(false) }
    var showProposalDialog by remember { mutableStateOf(false) }
    var activeTab by remember { mutableIntStateOf(0) } // 0: Live Stage, 1: Proposals & Feedback, 2: Company Polls

    // Speakers List
    val speakers = remember {
        mutableStateListOf(
            VipSpeaker(
                id = "ceo_1",
                name = "নবচেতনা পরিচালনা পর্ষদ",
                roleTitle = "কেন্দ্রীয় নির্বাহী নেতৃত্ব (Executive)",
                avatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150",
                isCompanyOfficial = true,
                isSpeaking = true
            ),
            VipSpeaker(
                id = "cto_2",
                name = "সিস্টেম আর্কিটেক্ট ও টেক হেড",
                roleTitle = "প্ল্যাটফর্ম কোর ইঞ্জিনিয়ারিং",
                avatarUrl = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=150",
                isCompanyOfficial = true,
                isSpeaking = false
            ),
            VipSpeaker(
                id = "vip_1",
                name = "সাদিয়া সুলতানা",
                roleTitle = "ভেরিফাইড টপ ক্রিয়েটর",
                avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150",
                isSpeaking = false
            ),
            VipSpeaker(
                id = "vip_2",
                name = "তানভীর আহমেদ",
                roleTitle = "ভেরিফাইড মার্চেন্ট প্রতিনিধি",
                avatarUrl = "https://images.unsplash.com/photo-1522075469751-3a6694fb2f61?w=150",
                isSpeaking = false
            )
        )
    }

    // Proposals List
    val proposals = remember {
        mutableStateListOf(
            VipProposal(
                id = "prop_1",
                authorName = "সাদিয়া সুলতানা",
                authorAvatar = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150",
                text = "মার্কেটপ্লেসে লাইভ শপিং ব্রডকাস্ট ফিচার যুক্ত করলে ভেরিফাইড সেলারদের বিক্রি দ্বিগুণ হবে। এটি পরবর্তী আপডেটে আনা যায় কি?",
                timestamp = "১০ মিনিট আগে",
                upvotes = 34,
                isUpvotedByMe = true
            ),
            VipProposal(
                id = "prop_2",
                authorName = "তানভীর আহমেদ",
                authorAvatar = "https://images.unsplash.com/photo-1522075469751-3a6694fb2f61?w=150",
                text = "ভাষা শিক্ষা একাডেমিতে স্থানীয় শিক্ষানবিশদের জন্য সাপ্তাহিক অনলাইন কুইজ টুর্নামেন্ট চালু করার প্রস্তাব দিচ্ছি।",
                timestamp = "২৫ মিনিট আগে",
                upvotes = 48,
                isUpvotedByMe = false
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFFFD700).copy(alpha = 0.2f),
                            modifier = Modifier.size(34.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("👑", fontSize = 18.sp)
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "ভিআইপি বোর্ডরুম ও মিটিং",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color(0xFFD32F2F)
                                ) {
                                    Text(
                                        text = "LIVE",
                                        color = Color.White,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "কোম্পানির সাথে সরাসরি গোলটেবিল আলোচনা (এনক্রিপ্টেড)",
                                fontSize = 11.sp,
                                color = Color(0xFFFFD54F)
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFF1B4D3E),
                        border = BorderStroke(1.dp, Color(0xFFFFD700).copy(alpha = 0.6f))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Icon(Icons.Default.Verified, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("ভেরিফাইড মেম্বার", color = Color(0xFFFFD700), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0A192F))
            )
        },
        bottomBar = {
            // Live Interactive Controls Bar
            Surface(
                color = Color(0xFF0D1B2A),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
                shadowElevation = 16.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Hand Raise Button
                    IconButton(
                        onClick = {
                            isHandRaised = !isHandRaised
                            Toast.makeText(
                                context,
                                if (isHandRaised) "✋ পরিচালনা পর্ষদের কাছে কথা বলার অনুরোধ পাঠানো হয়েছে" else "অনুরোধ প্রত্যাহার করা হয়েছে",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(if (isHandRaised) Color(0xFFFF9800) else Color.White.copy(alpha = 0.15f))
                            .size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PanTool,
                            contentDescription = "Raise Hand",
                            tint = if (isHandRaised) Color.Black else Color.White
                        )
                    }

                    // Mic Toggle Button
                    Button(
                        onClick = {
                            isMicOn = !isMicOn
                            Toast.makeText(
                                context,
                                if (isMicOn) "🎙️ আপনার মাইক অন রয়েছে (আপনি সরাসরি কথা বলছেন)" else "🔇 মাইক মিউট করা হয়েছে",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isMicOn) Color(0xFF00E676) else Color(0xFF2C3E50)
                        ),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.height(48.dp)
                    ) {
                        Icon(
                            imageVector = if (isMicOn) Icons.Default.Mic else Icons.Default.MicOff,
                            contentDescription = "Mic",
                            tint = if (isMicOn) Color.Black else Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isMicOn) "কথা বলছেন (Live)" else "কথা বলতে চাপুন",
                            color = if (isMicOn) Color.Black else Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }

                    // Add Proposal Button
                    IconButton(
                        onClick = { showProposalDialog = true },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color(0xFF673AB7))
                            .size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddComment,
                            contentDescription = "Submit Proposal",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(Color(0xFF060D17))
                .padding(paddingValues)
        ) {
            // Live Stage Banner & Topic Header
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F2438)),
                border = BorderStroke(1.dp, Color(0xFFFFD700).copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF00E676))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "আজকের নির্বাহী গোলটেবিল এজেন্ডা",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF00E676)
                            )
                        }

                        Text(
                            text = "🔒 ভেরিফাইড-অনলি",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFD700)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "🏛️ প্ল্যাটফর্ম পলিসি, কনটেন্ট মনিটাইজেশন ও ব্যবহারকারীদের সরাসরি উন্নয়ন পরামর্শ",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "উপস্থিত পরিচালনা পর্ষদ: ৩ জন | ভেরিফাইড সদস্য: ১২৪ জন",
                            fontSize = 11.sp,
                            color = Color(0xFF90CAF9)
                        )
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.White.copy(alpha = 0.1f)
                        ) {
                            Text(
                                text = "HD অডিও সক্রিয়",
                                fontSize = 10.sp,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }

            // Tab Selector
            TabRow(
                selectedTabIndex = activeTab,
                containerColor = Color(0xFF0D1B2A),
                contentColor = Color(0xFFFFD700),
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[activeTab]),
                        color = Color(0xFFFFD700)
                    )
                }
            ) {
                Tab(
                    selected = activeTab == 0,
                    onClick = { activeTab = 0 },
                    text = { Text("🎙️ লাইভ স্টেজ", fontWeight = FontWeight.Bold, fontSize = 13.sp) }
                )
                Tab(
                    selected = activeTab == 1,
                    onClick = { activeTab = 1 },
                    text = { Text("💡 পরামর্শ ও প্রস্তাবনা (${proposals.size})", fontWeight = FontWeight.Bold, fontSize = 13.sp) }
                )
                Tab(
                    selected = activeTab == 2,
                    onClick = { activeTab = 2 },
                    text = { Text("🗳️ কোম্পানি ভোটিং", fontWeight = FontWeight.Bold, fontSize = 13.sp) }
                )
            }

            // Content per Tab
            when (activeTab) {
                0 -> {
                    // LIVE STAGE SPEAKERS GRID
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            Text(
                                text = "👑 মূল মঞ্চ (Company Leadership & Active Speakers):",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFD54F)
                            )
                        }

                        items(speakers) { speaker ->
                            VipSpeakerCard(speaker = speaker)
                        }

                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF14213D).copy(alpha = 0.7f)),
                                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF64B5F6))
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = "আপনি একজন ভেরিফাইড সদস্য। যেকোনো সময় নিচে 'কথা বলতে চাপুন' বাটনে ক্লিক করে সরাসরি কথা বলতে বা হাত তুলতে পারেন।",
                                        fontSize = 12.sp,
                                        color = Color.White.copy(alpha = 0.85f),
                                        lineHeight = 18.sp
                                    )
                                }
                            }
                        }
                    }
                }
                1 -> {
                    // PROPOSALS & FEEDBACK STREAM
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "সদস্যদের সরাসরি প্রস্তাবনা তালিকা:",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFFD54F)
                                )

                                TextButton(onClick = { showProposalDialog = true }) {
                                    Icon(Icons.Default.Add, contentDescription = null, tint = Color(0xFF00E676), modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("নতুন প্রস্তাব দিন", color = Color(0xFF00E676), fontSize = 12.sp)
                                }
                            }
                        }

                        items(proposals) { proposal ->
                            VipProposalCard(
                                proposal = proposal,
                                onUpvote = {
                                    val index = proposals.indexOf(proposal)
                                    if (index != -1) {
                                        val current = proposals[index]
                                        proposals[index] = current.copy(
                                            upvotes = if (current.isUpvotedByMe) current.upvotes - 1 else current.upvotes + 1,
                                            isUpvotedByMe = !current.isUpvotedByMe
                                        )
                                    }
                                }
                            )
                        }
                    }
                }
                2 -> {
                    // COMPANY POLLS & POLICY VOTING
                    CompanyPolicyPollsSection()
                }
            }
        }
    }

    // Submit Proposal Modal Dialog
    if (showProposalDialog) {
        var proposalInput by remember { mutableStateOf("") }

        Dialog(onDismissRequest = { showProposalDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0D1B2A)),
                border = BorderStroke(1.dp, Color(0xFFFFD700).copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("💡", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "কোম্পানির জন্য পরামর্শ / প্রস্তাবনা",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        IconButton(onClick = { showProposalDialog = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "আপনার প্রস্তাবটি সরাসরি সেন্ট্রাল বোর্ড অফ ডিরেক্টরস ও টেকনিক্যাল টিমের নজরে আসবে।",
                        fontSize = 11.sp,
                        color = Color(0xFF90CAF9)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = proposalInput,
                        onValueChange = { proposalInput = it },
                        placeholder = { Text("আপনার পরামর্শ বা প্রস্তাব বিস্তারিত লিখুন...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFFFD700),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (proposalInput.isNotBlank()) {
                                proposals.add(
                                    0,
                                    VipProposal(
                                        id = "prop_${System.currentTimeMillis()}",
                                        authorName = currentUser?.name ?: "ভেরিফাইড মেম্বার",
                                        authorAvatar = currentUser?.avatarUrl ?: "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150",
                                        text = proposalInput.trim(),
                                        timestamp = "এইমাত্র",
                                        upvotes = 1,
                                        isUpvotedByMe = true
                                    )
                                )
                                showProposalDialog = false
                                Toast.makeText(context, "আপনার পরামর্শ পরিচালনা পর্ষদে জমা হয়েছে! ✅", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B4D3E)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("প্রস্তাব সাবমিট করুন", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun VipSpeakerCard(speaker: VipSpeaker) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF112233)),
        border = BorderStroke(
            1.5.dp,
            if (speaker.isSpeaking) Color(0xFF00E676) else if (speaker.isCompanyOfficial) Color(0xFFFFD700).copy(alpha = 0.5f) else Color.White.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box {
                    AsyncImage(
                        model = speaker.avatarUrl,
                        contentDescription = speaker.name,
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    if (speaker.isCompanyOfficial) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFFFD700),
                            modifier = Modifier
                                .size(18.dp)
                                .align(Alignment.BottomEnd)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("★", fontSize = 10.sp, color = Color.Black, fontWeight = FontWeight.ExtraBold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = speaker.name,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        if (speaker.isCompanyOfficial) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFF1B4D3E)
                            ) {
                                Text(
                                    text = "পরিচালনা পর্ষদ",
                                    fontSize = 9.sp,
                                    color = Color(0xFFFFD700),
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Text(
                        text = speaker.roleTitle,
                        fontSize = 11.sp,
                        color = Color(0xFFB0BEC5)
                    )
                }
            }

            // Audio Status Indicator
            if (speaker.isSpeaking) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFF00E676).copy(alpha = 0.2f),
                    border = BorderStroke(1.dp, Color(0xFF00E676))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.VolumeUp, contentDescription = null, tint = Color(0xFF00E676), modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("কথা বলছেন", color = Color(0xFF00E676), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                Icon(
                    imageVector = Icons.Default.MicOff,
                    contentDescription = "Muted",
                    tint = Color.White.copy(alpha = 0.4f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun VipProposalCard(
    proposal: VipProposal,
    onUpvote: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF112233)),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        model = proposal.authorAvatar,
                        contentDescription = proposal.authorName,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = proposal.authorName,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.Verified, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(12.dp))
                        }
                        Text(
                            text = proposal.timestamp,
                            fontSize = 10.sp,
                            color = Color.White.copy(alpha = 0.5f)
                        )
                    }
                }

                // Upvote Button
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (proposal.isUpvotedByMe) Color(0xFF1B4D3E) else Color.White.copy(alpha = 0.08f),
                    border = BorderStroke(1.dp, if (proposal.isUpvotedByMe) Color(0xFF00E676) else Color.Transparent),
                    modifier = Modifier.clickable { onUpvote() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.ThumbUp,
                            contentDescription = "Upvote",
                            tint = if (proposal.isUpvotedByMe) Color(0xFF00E676) else Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${proposal.upvotes}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (proposal.isUpvotedByMe) Color(0xFF00E676) else Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = proposal.text,
                fontSize = 13.sp,
                color = Color(0xFFECEFF1),
                lineHeight = 19.sp
            )
        }
    }
}

@Composable
private fun CompanyPolicyPollsSection() {
    var selectedOption1 by remember { mutableIntStateOf(0) }
    var hasVoted1 by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF112233)),
                border = BorderStroke(1.dp, Color(0xFFFFD700).copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🗳️", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "কোম্পানি লাইভ পলিসি ভোটিং",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFD700)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "প্রশ্ন: আগামী মাসে ক্রিয়েটরদের জন্য ভিডিও অ্যাড রেভিনিউ শেয়ারিং কি ৭০% থেকে বাড়িয়ে ৭৫% করা উচিত?",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    listOf(
                        "হ্যাঁ, ক্রিয়েটরদের উৎসাহ দিতে ৭৫% শেয়ারিং দেওয়া উচিত" to 82,
                        "বর্তমান ৭০% শেয়ারিং ও প্ল্যাটফর্ম স্পিড অপ্টিমাইজেশনে ব্যয় বাড়ানো উচিত" to 18
                    ).forEachIndexed { index, (option, percent) ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    selectedOption1 = index
                                    hasVoted1 = true
                                },
                            shape = RoundedCornerShape(10.dp),
                            color = if (hasVoted1 && selectedOption1 == index) Color(0xFF1B4D3E) else Color.White.copy(alpha = 0.06f),
                            border = BorderStroke(
                                1.dp,
                                if (hasVoted1 && selectedOption1 == index) Color(0xFF00E676) else Color.White.copy(alpha = 0.1f)
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = option,
                                    fontSize = 12.sp,
                                    color = Color.White,
                                    modifier = Modifier.weight(1f)
                                )
                                if (hasVoted1) {
                                    Text(
                                        text = "$percent%",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (selectedOption1 == index) Color(0xFF00E676) else Color.White.copy(alpha = 0.6f)
                                    )
                                }
                            }
                        }
                    }

                    if (hasVoted1) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "✅ আপনার মূল্যবান ভোট সরাসরি কার্যনির্বাহী সভায় অন্তর্ভুক্ত হয়েছে।",
                            fontSize = 11.sp,
                            color = Color(0xFF00E676)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun UnverifiedAccessDeniedView(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ভিআইপি মিটিং হল", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0A192F))
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF060D17))
                .padding(padding)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F2438)),
                border = BorderStroke(1.5.dp, Color(0xFFFFD700).copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFFFD700).copy(alpha = 0.15f),
                        modifier = Modifier.size(72.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(36.dp))
                        }
                    }

                    Text(
                        text = "🔒 শুধুমাত্র ভেরিফাইড সদস্যদের জন্য সংরক্ষিত",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "কোম্পানির পরিচালনা পর্ষদের সাথে সরাসরি গোলটেবিল মিটিং ও নীতি নির্ধারণী কক্ষে প্রবেশ করতে জাতীয় পরিচয়পত্র (NID) ও ফেস ভেরিফিকেশন সম্পন্ন থাকা বাধ্যতামূলক।",
                        fontSize = 13.sp,
                        color = Color(0xFFB0BEC5),
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = onBack,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B4D3E)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = Color(0xFFFFD700))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("প্রোফাইলে গিয়ে ভেরিফাই করুন", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
