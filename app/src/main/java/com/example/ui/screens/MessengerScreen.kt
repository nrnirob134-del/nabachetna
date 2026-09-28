package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.User
import com.example.ui.components.WalletRechargeModalDialog
import com.example.ui.theme.TeaLeafGreen
import com.example.ui.viewmodel.PageBookViewModel
import kotlinx.coroutines.delay

enum class MessengerCategory(val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector, val isEnabled: Boolean) {
    CHATS("চ্যাটস", Icons.Default.Chat, true),
    VOICE_ROOM_14("১৪-সিট ভয়েস রুম", Icons.Default.Mic, false),
    HD_MEETING("আল্ট্রা এইচডি মিটিং", Icons.Default.VideoCall, false)
}

data class ChatConversation(
    val id: Int,
    val name: String,
    val avatarUrl: String,
    val lastMessage: String,
    val timestamp: String,
    val unreadCount: Int = 0,
    val isOnline: Boolean = true,
    val isGroup: Boolean = false
)

data class ChatMessage(
    val id: Int,
    val senderName: String,
    val senderAvatar: String,
    val text: String,
    val timestamp: String,
    val isMe: Boolean,
    val reaction: String? = null
)

data class VoiceStageSpeaker(
    val seatNumber: Int,
    val name: String,
    val avatarUrl: String,
    val isHost: Boolean = false,
    val isSpeaking: Boolean = false,
    val isMuted: Boolean = false
)

data class BookItem(
    val title: String,
    val author: String,
    val coverUrl: String,
    val isApproved: Boolean
)

data class ClubRoomItem(
    val id: Int,
    val title: String,
    val category: String,
    var isFollowed: Boolean,
    val isOwner: Boolean
)

data class MeetingParticipant(
    val id: String,
    val name: String,
    val avatarUrl: String,
    val isHost: Boolean = false,
    val isSpeaking: Boolean = false,
    val isMuted: Boolean = false,
    val isVideoOn: Boolean = true,
    val isHandRaised: Boolean = false
)

data class MeetingLiveMessage(
    val id: String,
    val senderName: String,
    val message: String,
    val time: String,
    val isMe: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessengerScreen(
    viewModel: PageBookViewModel,
    currentUser: User?,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedCategory by remember { mutableStateOf(MessengerCategory.CHATS) }

    // Active Chat Thread state
    var activeChatConversation by remember { mutableStateOf<ChatConversation?>(null) }
    var chatMessages by remember {
        mutableStateOf(
            listOf(
                ChatMessage(1, "Anika Rahman", "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150", "হাই! নবচেতনা মেসেঞ্জারে স্বাগতম।", "10:30 AM", false),
                ChatMessage(2, "Me", currentUser?.avatarUrl ?: "", "ধন্যবাদ! অ্যাপটি অনেক দ্রুত কাজ করছে।", "10:31 AM", true, "❤️"),
                ChatMessage(3, "Anika Rahman", "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150", "হ্যাঁ, নতুন ভয়েস রুম ও মিটিং ফিচারও চেক করুন!", "10:32 AM", false)
            )
        )
    }
    var messageInput by remember { mutableStateOf("") }
    var activeCallType by remember { mutableStateOf<String?>(null) } // "AUDIO" or "VIDEO" or null

    // 14-Seat Voice Room states
    var isVoiceRoomActive by remember { mutableStateOf(true) }
    var showCreateVoiceRoomDialog by remember { mutableStateOf(false) }
    var voiceRoomTopic by remember { mutableStateOf("Gen-Z আড্ডা ও টেক ডিসকাশন 🚀") }
    var voiceRoomCategory by remember { mutableStateOf("প্রযুক্তি & আড্ডা") }
    var soundReactionList by remember { mutableStateOf(listOf<String>()) }
    var isMyMicMuted by remember { mutableStateOf(false) }
    var isHandRaised by remember { mutableStateOf(false) }

    val initial14Speakers = remember {
        listOf(
            VoiceStageSpeaker(1, "হোস্ট (Anika)", "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150", isHost = true, isSpeaking = true),
            VoiceStageSpeaker(2, "তাহসীন আহমেদ", "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150", isSpeaking = true),
            VoiceStageSpeaker(3, "রাফি হোসেন", "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=150"),
            VoiceStageSpeaker(4, "মিলা খান", "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=150"),
            VoiceStageSpeaker(5, "সাব্বির রহমান", "https://images.unsplash.com/photo-1522075469751-3a6694fb2f61?w=150", isMuted = true),
            VoiceStageSpeaker(6, "তানজিলা আক্তার", "https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=150"),
            VoiceStageSpeaker(7, "ফাহিম শাহরিয়ার", "https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?w=150"),
            VoiceStageSpeaker(8, "নুসরাত জাহান", "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=150"),
            VoiceStageSpeaker(9, "আরিফ হাসান", "https://images.unsplash.com/photo-1539571696357-5a69c17a67c6?w=150"),
            VoiceStageSpeaker(10, "মারুফ বিল্লাহ", "https://images.unsplash.com/photo-1527980965255-d3b416303d12?w=150"),
            VoiceStageSpeaker(11, "লামিয়া রহমান", "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=150"),
            VoiceStageSpeaker(12, "সৌরভ দত্ত", "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=150"),
            VoiceStageSpeaker(13, "ইমরান নাজির", "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150"),
            VoiceStageSpeaker(14, "ফারিয়া ইসলাম", "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=150")
        )
    }

    // Ultra HD AI Meeting States (Google Meet Architecture)
    var inMeetingCall by remember { mutableStateOf(false) }
    var isMeetingHost by remember { mutableStateOf(false) }
    var isMeetingVideoOn by remember { mutableStateOf(true) }
    var isMeetingMicOn by remember { mutableStateOf(true) }
    var isScreenSharing by remember { mutableStateOf(false) }
    var isWhiteboardActive by remember { mutableStateOf(false) }
    var isMeetingRecording by remember { mutableStateOf(false) }
    var isMeetingHandRaised by remember { mutableStateOf(false) }
    var isMeetingLocked by remember { mutableStateOf(false) }
    var meetingCode by remember { mutableStateOf("nab-tec-982") }
    var meetingLink by remember { mutableStateOf("https://meet.nobocetona.com/nab-tec-982") }
    var showMeetingCreatedDialog by remember { mutableStateOf(false) }
    var showJoinMeetingDialog by remember { mutableStateOf(false) }
    var joinLinkInput by remember { mutableStateOf("") }
    val activeMeetingRooms = remember {
        mutableStateMapOf(
            "nab-tec-982" to "নবচেতনা অফিশিয়াল টেক ডিসকাশন",
            "nab-dev-441" to "মোবাইল অ্যাপ আর্কিটেকচার সেশন"
        )
    }
    var waitingGuestName by remember { mutableStateOf<String?>(null) }
    var liveCaptionsText by remember { mutableStateOf("এআই লাইভ সাবটাইটেল (বাংলা): \"স্বাগতম! আল্ট্রা এইচডি গুগল মিট সিস্টেমে সরাসরি কানেক্টেড।\"") }
    val meetingLiveMessages = remember {
        mutableStateListOf(
            MeetingLiveMessage("1", "তাহসীন আহমেদ", "সবাই কি আমার স্লাইড ক্লিয়ার দেখতে পাচ্ছেন?", "10:02 AM"),
            MeetingLiveMessage("2", "রাফি হোসেন", "হ্যাঁ ভাই, আল্ট্রা এইচডি কোয়ালিটিতে স্পষ্ট দেখা যাচ্ছে!", "10:03 AM")
        )
    }

    // Voice Room Extra Interactive Features (Games, Gifts, Library, Phone Audio Stream)
    var showVoiceGamesModal by remember { mutableStateOf(false) }
    var showVoiceGiftsModal by remember { mutableStateOf(false) }
    var showWalletRechargeModal by remember { mutableStateOf(false) }
    var showVoiceLibraryModal by remember { mutableStateOf(false) }
    var showLocalAudioStreamModal by remember { mutableStateOf(false) }
    var selectedAudioTrack by remember { mutableStateOf("অফলাইন_গান_০১_নবচেতনা_মিক্স.mp3") }
    var isStreamingAudioPlaying by remember { mutableStateOf(false) }

    // Room creation limit (Max 3 rooms per user) & My Clubs List
    var myCreatedRoomsCount by remember { mutableStateOf(1) }
    var myClubsList = remember {
        mutableStateOf(
            mutableListOf(
                ClubRoomItem(1, "Gen-Z আড্ডা ও টেক ডিসকাশন", "প্রযুক্তি", true, true),
                ClubRoomItem(2, "ইসলামিক আলোচনা ও তিলাওয়াত", "ধর্মীয়", false, false),
                ClubRoomItem(3, "ফ্রিল্যান্সিং ও ক্যারিয়ার গাইড", "ক্যারিয়ার", true, false)
            )
        )
    }
    var isCurrentRoomFollowed by remember { mutableStateOf(true) }

    BackHandler {
        if (activeChatConversation != null) {
            activeChatConversation = null
        } else if (inMeetingCall) {
            inMeetingCall = false
        } else {
            onBack()
        }
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(TeaLeafGreen)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onBack) {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "নবচেতনা মেসেঞ্জার",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color.White.copy(alpha = 0.2f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF00E676))
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("অনলাইন", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // 3 Category Tabs Navigation
                TabRow(
                    selectedTabIndex = selectedCategory.ordinal,
                    containerColor = Color(0xFF143B2E),
                    contentColor = Color.White,
                    indicator = { tabPositions ->
                        TabRowDefaults.Indicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedCategory.ordinal]),
                            color = Color(0xFF81C784),
                            height = 3.dp
                        )
                    }
                ) {
                    MessengerCategory.values().forEach { category ->
                        val isSelected = selectedCategory == category
                        Tab(
                            selected = isSelected,
                            onClick = {
                                if (category.isEnabled) {
                                    selectedCategory = category
                                } else {
                                    Toast.makeText(context, "এই ফিচারটি শীঘ্রই আসছে! কাজ চলছে...", Toast.LENGTH_SHORT).show()
                                }
                            },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = category.icon,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = if (isSelected) Color(0xFF81C784) else Color.White.copy(alpha = if (category.isEnabled) 0.7f else 0.3f)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = category.title,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) Color(0xFF81C784) else Color.White.copy(alpha = if (category.isEnabled) 0.7f else 0.3f)
                                    )
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
            when (selectedCategory) {
                MessengerCategory.CHATS -> {
                    ChatsCategoryContent(
                        currentUser = currentUser,
                        onOpenConversation = { conv -> activeChatConversation = conv }
                    )
                }
                MessengerCategory.VOICE_ROOM_14 -> {
                    VoiceRoom14CategoryContent(
                        topic = voiceRoomTopic,
                        categoryTag = voiceRoomCategory,
                        speakers = initial14Speakers,
                        isMyMicMuted = isMyMicMuted,
                        isHandRaised = isHandRaised,
                        soundReactions = soundReactionList,
                        myCreatedRoomsCount = myCreatedRoomsCount,
                        myClubsList = myClubsList.value,
                        isCurrentRoomFollowed = isCurrentRoomFollowed,
                        onCreateRoomClick = { showCreateVoiceRoomDialog = true },
                        onToggleMic = { isMyMicMuted = !isMyMicMuted },
                        onToggleHandRaise = {
                            isHandRaised = !isHandRaised
                            Toast.makeText(context, if (isHandRaised) "হাত তোলা হয়েছে ✋" else "হাত নামানো হয়েছে", Toast.LENGTH_SHORT).show()
                        },
                        onSendEmojiReaction = { emoji ->
                            soundReactionList = soundReactionList + emoji
                        },
                        onOpenGames = { showVoiceGamesModal = true },
                        onOpenGifts = { showVoiceGiftsModal = true },
                        onOpenLibrary = { showVoiceLibraryModal = true },
                        onOpenAudioStream = { showLocalAudioStreamModal = true },
                        onToggleFollowCurrentRoom = {
                            isCurrentRoomFollowed = !isCurrentRoomFollowed
                            Toast.makeText(context, if (isCurrentRoomFollowed) "⭐ রুমটি ফলো করা হয়েছে!" else "রুমটি আনফলো করা হয়েছে", Toast.LENGTH_SHORT).show()
                        },
                        onSelectClub = { club ->
                            voiceRoomTopic = club.title
                            voiceRoomCategory = club.category
                            isCurrentRoomFollowed = club.isFollowed
                            Toast.makeText(context, "🔄 ক্লাবে সুইচ করা হয়েছে: '${club.title}'", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
                MessengerCategory.HD_MEETING -> {
                    UltraHdMeetingCategoryContent(
                        inMeeting = inMeetingCall,
                        meetingCode = meetingCode,
                        meetingLink = meetingLink,
                        isHost = isMeetingHost,
                        currentUser = currentUser,
                        isVideoOn = isMeetingVideoOn,
                        isMicOn = isMeetingMicOn,
                        isScreenSharing = isScreenSharing,
                        isWhiteboardActive = isWhiteboardActive,
                        isRecording = isMeetingRecording,
                        isHandRaised = isMeetingHandRaised,
                        isMeetingLocked = isMeetingLocked,
                        captionsText = liveCaptionsText,
                        waitingGuestName = waitingGuestName,
                        liveMessages = meetingLiveMessages,
                        onOrganizeNewMeeting = {
                            val code = "nab-" + (100..999).random() + "-" + ('a'..'z').shuffled().take(3).joinToString("")
                            meetingCode = code
                            meetingLink = "https://meet.nobocetona.com/$code"
                            activeMeetingRooms[code] = "নবচেতনা প্রাইভেট এইচডি মিটিং ($code)"
                            isMeetingHost = true
                            inMeetingCall = true
                            showMeetingCreatedDialog = true
                        },
                        onOpenJoinDialog = { showJoinMeetingDialog = true },
                        onLeaveMeeting = {
                            inMeetingCall = false
                            isMeetingHost = false
                            waitingGuestName = null
                        },
                        onToggleVideo = { isMeetingVideoOn = !isMeetingVideoOn },
                        onToggleMic = { isMeetingMicOn = !isMeetingMicOn },
                        onToggleScreenShare = {
                            isScreenSharing = !isScreenSharing
                            Toast.makeText(context, if (isScreenSharing) "স্ক্রিন শেয়ারিং চালু করা হয়েছে 📺" else "স্ক্রিন শেয়ারিং বন্ধ করা হয়েছে", Toast.LENGTH_SHORT).show()
                        },
                        onToggleWhiteboard = { isWhiteboardActive = !isWhiteboardActive },
                        onToggleRecording = {
                            isMeetingRecording = !isMeetingRecording
                            Toast.makeText(context, if (isMeetingRecording) "🔴 ক্লাউড এইচডি রেকর্ডিং শুরু হয়েছে..." else "রেকর্ডিং সমাপ্ত ও ক্লাউডে এনক্রিপ্ট হয়ে সংরক্ষিত হয়েছে।", Toast.LENGTH_LONG).show()
                        },
                        onToggleHandRaise = {
                            isMeetingHandRaised = !isMeetingHandRaised
                            Toast.makeText(context, if (isMeetingHandRaised) "✋ কথা বলতে হাত তোলা হয়েছে!" else "✋ হাত নামানো হয়েছে", Toast.LENGTH_SHORT).show()
                        },
                        onToggleLockMeeting = {
                            isMeetingLocked = !isMeetingLocked
                            Toast.makeText(context, if (isMeetingLocked) "🔒 মিটিং লক করা হয়েছে! লিংক থাকলেও নতুন কেউ ঢুকতে পারবে না।" else "🔓 মিটিং আনলক করা হয়েছে।", Toast.LENGTH_SHORT).show()
                        },
                        onAdmitWaitingGuest = {
                            waitingGuestName = null
                            Toast.makeText(context, "✅ অতিথিকে মিটিংয়ে যুক্ত করা হয়েছে!", Toast.LENGTH_SHORT).show()
                        },
                        onDenyWaitingGuest = {
                            waitingGuestName = null
                            Toast.makeText(context, "❌ অতিথির প্রবেশের অনুরোধ বাতিল করা হয়েছে।", Toast.LENGTH_SHORT).show()
                        },
                        onSendChatMessage = { msgText ->
                            if (msgText.isNotBlank()) {
                                meetingLiveMessages.add(
                                    MeetingLiveMessage(
                                        id = (meetingLiveMessages.size + 1).toString(),
                                        senderName = currentUser?.name ?: "You",
                                        message = msgText.trim(),
                                        time = "Just now",
                                        isMe = true
                                    )
                                )
                            }
                        }
                    )
                }
            }

            // Chat Window Modal Sheet
            activeChatConversation?.let { conv ->
                ChatConversationWindow(
                    conversation = conv,
                    currentUser = currentUser,
                    messages = chatMessages,
                    inputMessage = messageInput,
                    onMessageInputChange = { messageInput = it },
                    onSendMessage = {
                        if (messageInput.isNotBlank()) {
                            val newMsg = ChatMessage(
                                id = chatMessages.size + 1,
                                senderName = currentUser?.name ?: "Me",
                                senderAvatar = currentUser?.avatarUrl ?: "",
                                text = messageInput.trim(),
                                timestamp = "Just now",
                                isMe = true
                            )
                            chatMessages = chatMessages + newMsg
                            messageInput = ""
                        }
                    },
                    onStartCall = { callType -> activeCallType = callType },
                    onClose = { activeChatConversation = null }
                )
            }

            // Audio / Video Call Active Screen Overlay
            activeCallType?.let { type ->
                ActiveCallOverlay(
                    callType = type,
                    recipientName = activeChatConversation?.name ?: "Anika Rahman",
                    recipientAvatar = activeChatConversation?.avatarUrl ?: "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150",
                    currentUser = currentUser,
                    viewModel = viewModel,
                    onEndCall = { activeCallType = null }
                )
            }

            // Google Meet: Meeting Created & Share Link Dialog
            if (showMeetingCreatedDialog) {
                val clipboardManager = LocalClipboardManager.current
                AlertDialog(
                    onDismissRequest = { showMeetingCreatedDialog = false },
                    icon = { Icon(imageVector = Icons.Default.Share, contentDescription = null, tint = TeaLeafGreen) },
                    title = { Text("🔗 আপনার প্রাইভেট গুগল মিট লিংক প্রস্তুত!") },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(
                                "গুগল মিটের মতো এই মিটিংটি সম্পূর্ণ গোপন। এই লিংক ছাড়া কেউ মিটিং দেখতে বা যুক্ত হতে পারবে না।",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp).fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("মিটিং লিংক (গোপন):", fontSize = 10.sp, color = Color.Gray)
                                        Text(
                                            text = meetingLink,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = TeaLeafGreen,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                    IconButton(
                                        onClick = {
                                            clipboardManager.setText(AnnotatedString(meetingLink))
                                            Toast.makeText(context, "📋 লিংক কপি করা হয়েছে! বন্ধুদের পাঠান।", Toast.LENGTH_SHORT).show()
                                        }
                                    ) {
                                        Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy Link", tint = TeaLeafGreen)
                                    }
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        clipboardManager.setText(AnnotatedString(meetingLink))
                                        Toast.makeText(context, "মেসেঞ্জারে শেয়ার করতে লিংক কপি হয়েছে!", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("বন্ধুদের পাঠান", fontSize = 12.sp)
                                }
                            }
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = { showMeetingCreatedDialog = false },
                            colors = ButtonDefaults.buttonColors(containerColor = TeaLeafGreen),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("মিটিং রুমে প্রবেশ করুন")
                        }
                    }
                )
            }

            // Google Meet: Join By Link / Code Dialog
            if (showJoinMeetingDialog) {
                AlertDialog(
                    onDismissRequest = {
                        showJoinMeetingDialog = false
                        joinLinkInput = ""
                    },
                    icon = { Icon(imageVector = Icons.Default.Link, contentDescription = null, tint = TeaLeafGreen) },
                    title = { Text("🔗 লিংক বা কোড দিয়ে মিটিংয়ে যুক্ত হন") },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(
                                "আয়োজকের পাঠানো গুগল মিট লিংক বা কোড এখানে দিন। লিংক ছাড়া কোনো পাবলিক মিটিং তালিকা দেখা যায় না।",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            OutlinedTextField(
                                value = joinLinkInput,
                                onValueChange = { joinLinkInput = it },
                                label = { Text("মিটিং লিংক বা কোড লিখুন") },
                                placeholder = { Text("https://meet.nobocetona.com/nab-xxx-yyy") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                val trimmed = joinLinkInput.trim()
                                val extractedCode = if (trimmed.contains("/")) trimmed.substringAfterLast("/") else trimmed
                                val cleanCode = extractedCode.lowercase()

                                if (cleanCode.isNotBlank() && (activeMeetingRooms.containsKey(cleanCode) || cleanCode == meetingCode.lowercase())) {
                                    meetingCode = cleanCode
                                    meetingLink = "https://meet.nobocetona.com/$cleanCode"
                                    isMeetingHost = false
                                    inMeetingCall = true
                                    showJoinMeetingDialog = false
                                    joinLinkInput = ""
                                    waitingGuestName = currentUser?.name ?: "অতিথি ইউজার"
                                    Toast.makeText(context, "✅ সফলভাবে মিটিং লিংকে যুক্ত হয়েছেন!", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "❌ মিটিং খুঁজে পাওয়া যায়নি! লিংক ছাড়া কেউ মিটিং দেখতে বা যুক্ত হতে পারবে না।", Toast.LENGTH_LONG).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = TeaLeafGreen)
                        ) {
                            Text("যাচাই করে যুক্ত হন")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = {
                            showJoinMeetingDialog = false
                            joinLinkInput = ""
                        }) {
                            Text("বাতিল")
                        }
                    }
                )
            }

            // Create Voice Room Modal Dialog
            if (showCreateVoiceRoomDialog) {
                AlertDialog(
                    onDismissRequest = { showCreateVoiceRoomDialog = false },
                    icon = { Icon(imageVector = Icons.Default.Mic, contentDescription = null, tint = TeaLeafGreen) },
                    title = { Text("নতুন Gen-Z ১৪-সিট ভয়েস রুম তৈরি করুন") },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedTextField(
                                value = voiceRoomTopic,
                                onValueChange = { voiceRoomTopic = it },
                                label = { Text("ভয়েস রুমের শিরোনাম/বিষয়") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = voiceRoomCategory,
                                onValueChange = { voiceRoomCategory = it },
                                label = { Text("ক্যাটাগরি ট্যাগ (যেমন: টেক, আড্ডা, গান)") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Text("স্টেজ মোড: ১৪ জন স্পিকার স্টেজ + অনলিমিটেড শ্রোতা", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                if (myCreatedRoomsCount >= 3) {
                                    showCreateVoiceRoomDialog = false
                                    Toast.makeText(context, "❌ আপনি সর্বোচ্চ ৩টি রুম তৈরি করতে পেরেছেন। এর বেশি রুম তৈরি করা সম্ভব নয়।", Toast.LENGTH_LONG).show()
                                } else {
                                    myCreatedRoomsCount++
                                    myClubsList.value.add(ClubRoomItem(myClubsList.value.size + 1, voiceRoomTopic, voiceRoomCategory, true, true))
                                    showCreateVoiceRoomDialog = false
                                    Toast.makeText(context, "১৪-সিট ভয়েস রুম সফলভাবে তৈরি হয়েছে! 🚀 (তৈরি করেছেন: $myCreatedRoomsCount/৩)", Toast.LENGTH_LONG).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = TeaLeafGreen)
                        ) {
                            Text("স্টেজ তৈরি করুন")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showCreateVoiceRoomDialog = false }) {
                            Text("বাতিল")
                        }
                    }
                )
            }

            // 2. Voice Room Games & Coin Market Lounge (Fruit Wheel, Color Wheel, Dice, Coin Market)
            if (showVoiceGamesModal) {
                NobocetonaGameLoungeDialog(
                    currentUser = currentUser,
                    viewModel = viewModel,
                    onDismiss = { showVoiceGamesModal = false }
                )
            }

            // 3. Voice Room Virtual Gifts Modal (20 Gifts + 3D Emote + 5% Commission)
            if (showVoiceGiftsModal) {
                AlertDialog(
                    onDismissRequest = { showVoiceGiftsModal = false },
                    modifier = Modifier.fillMaxHeight(0.85f),
                    icon = { Icon(imageVector = Icons.Default.CardGiftcard, contentDescription = null, tint = Color(0xFFE91E63)) },
                    title = { Text("🎁 ২০টি আকর্ষণীয় ভার্চুয়াল গিফট (৩ডি ইমোট)") },
                    text = {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "ওয়ালেট: ৳${currentUser?.walletBalance ?: 0.0} (কমিশন ৫%)",
                                    fontSize = 11.sp,
                                    color = TeaLeafGreen,
                                    fontWeight = FontWeight.Bold
                                )
                                TextButton(
                                    onClick = {
                                        showVoiceGiftsModal = false
                                        showWalletRechargeModal = true
                                    },
                                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.AddCard, contentDescription = null, tint = TeaLeafGreen, modifier = Modifier.size(13.dp))
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text("রিচার্জ করুন", fontSize = 11.sp, color = TeaLeafGreen, fontWeight = FontWeight.Bold)
                                }
                            }
                            
                            val gifts20 = listOf(
                                Triple("🌹 লাল গোলাপ", "🌹", 10.0),
                                Triple("💍 ডায়মন্ড রিং", "💍", 100.0),
                                Triple("🏎️ স্পোর্টস কার", "🏎️", 500.0),
                                Triple("👑 গোল্ডেন ক্রাউন", "👑", 1000.0),
                                Triple("🚀 রকেট", "🚀", 2000.0),
                                Triple("🦁 সিংহ রাজা", "🦁", 3000.0),
                                Triple("🏰 রয়্যাল প্যালেস", "🏰", 5000.0),
                                Triple("🎂 বার্থডে কেক", "🎂", 50.0),
                                Triple("🎸 ইলেকট্রিক গিটার", "🎸", 150.0),
                                Triple("💫 ম্যাজিক স্টার", "💫", 250.0),
                                Triple("🍾 শ্যাম্পেন বোতল", "🍾", 350.0),
                                Triple("🧸 টেডি বিয়ার", "🧸", 80.0),
                                Triple("💎 নীল মণি", "💎", 800.0),
                                Triple("🌟 গোল্ডেন স্টার", "🌟", 400.0),
                                Triple("🎁 সারপ্রাইজ বক্স", "🎁", 120.0),
                                Triple("🌺 অর্কিড ফুল", "🌺", 30.0),
                                Triple("🎧 ডিজে হেডফোন", "🎧", 220.0),
                                Triple("⚡ থান্ডার বোল্ট", "⚡", 1500.0),
                                Triple("🎈 বেলুন বান্ডেল", "🎈", 60.0),
                                Triple("🏆 চ্যাম্পিয়ন ট্রফি", "🏆", 2500.0)
                            )

                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                modifier = Modifier.fillMaxWidth().height(320.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                items(gifts20.size) { index ->
                                    val (gName, gIcon, gPrice) = gifts20[index]
                                    OutlinedCard(
                                        modifier = Modifier.fillMaxWidth().clickable {
                                            val currentBal = currentUser?.walletBalance ?: 0.0
                                            if (currentBal >= gPrice) {
                                                val companyCut = gPrice * 0.05
                                                val recipientGets = gPrice * 0.95
                                                viewModel.updateWalletBalance(currentBal - gPrice)
                                                showVoiceGiftsModal = false
                                                activeGiftEmote = gIcon
                                                Toast.makeText(context, "🎉 '$gName' পাঠানো হয়েছে! (কোম্পানি কমিশন ৳${String.format("%.2f", companyCut)}, রিসিভার পেল ৳${String.format("%.2f", recipientGets)})", Toast.LENGTH_LONG).show()
                                            } else {
                                                Toast.makeText(context, "❌ ওয়ালেটে পর্যাপ্ত ব্যালেন্স নেই!", Toast.LENGTH_SHORT).show()
                                            }
                                        },
                                        colors = CardDefaults.outlinedCardColors(containerColor = Color(0xFF1B4D3E).copy(alpha = 0.2f))
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(8.dp).fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(gIcon, fontSize = 20.sp)
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(gName, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                            }
                                            Text("৳${gPrice.toInt()}", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color(0xFFFFD700))
                                        }
                                    }
                                }
                            }
                        }
                    },
                    confirmButton = {
                        Button(onClick = { showVoiceGiftsModal = false }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE91E63))) {
                            Text("বন্ধ করুন")
                        }
                    }
                )
            }

            // 4. Voice Room Library Modal (Secure In-App Reader, No Download, Admin Approval)
            if (showVoiceLibraryModal) {
                AlertDialog(
                    onDismissRequest = { showVoiceLibraryModal = false },
                    modifier = Modifier.fillMaxHeight(0.85f),
                    icon = { Icon(imageVector = Icons.Default.LibraryBooks, contentDescription = null, tint = Color(0xFF009688)) },
                    title = { Text("📚 ডিজিটাল লাইব্রেরি (রুমের ভেতরে পড়ুন)") },
                    text = {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("অনুমোদিত বইসমূহ (ডাউনলোড নিষিদ্ধ, রুমে পড়ুন):", fontSize = 11.sp, color = Color.Gray)
                                OutlinedButton(
                                    onClick = { showBookSubmitDialog = true },
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text("📖 নিজের বই জমা দিন", fontSize = 10.sp, color = Color(0xFF009688))
                                }
                            }

                            LazyColumn(
                                modifier = Modifier.fillMaxWidth().height(320.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(libraryBooksList.value.size) { index ->
                                    val book = libraryBooksList.value[index]
                                    if (book.isApproved) {
                                        Surface(
                                            color = Color(0xFF009688).copy(alpha = 0.08f),
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier.fillMaxWidth().clickable {
                                                activeReadingBook = book
                                            }
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(10.dp).fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(imageVector = Icons.Default.Book, contentDescription = null, tint = Color(0xFF009688), modifier = Modifier.size(20.dp))
                                                    Spacer(modifier = Modifier.width(10.dp))
                                                    Column {
                                                        Text(book.title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                                                        Text("লেখক: ${book.author} (অনুমোদিত)", fontSize = 10.sp, color = Color.Gray)
                                                    }
                                                }
                                                Surface(color = Color(0xFF009688), shape = RoundedCornerShape(6.dp)) {
                                                    Text("পড়ুন", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    },
                    confirmButton = {
                        Button(onClick = { showVoiceLibraryModal = false }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF009688))) {
                            Text("বন্ধ করুন")
                        }
                    }
                )
            }

            // Book Submission Dialog (Pending Admin Approval)
            if (showBookSubmitDialog) {
                AlertDialog(
                    onDismissRequest = { showBookSubmitDialog = false },
                    title = { Text("📖 নতুন বই জমা দিন (অ্যাডমিন পর্যালোচনার জন্য)") },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text("আপনার বই অ্যাডমিন কর্তৃক অনুমোদিত হওয়ার পরই লাইব্রেরিতে পাবলিক হবে।", fontSize = 11.sp, color = Color.Gray)
                            OutlinedTextField(
                                value = newBookTitle,
                                onValueChange = { newBookTitle = it },
                                label = { Text("বইয়ের নাম (PDF / Title)") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = newBookAuthor,
                                onValueChange = { newBookAuthor = it },
                                label = { Text("লেখকের নাম") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                if (newBookTitle.isNotBlank()) {
                                    libraryBooksList.value.add(BookItem(newBookTitle, newBookAuthor.ifBlank { currentUser?.name ?: "Gen-Z Author" }, "https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=150", isApproved = false))
                                    showBookSubmitDialog = false
                                    newBookTitle = ""
                                    newBookAuthor = ""
                                    Toast.makeText(context, "✅ বই সফলভাবে জমা দেওয়া হয়েছে! অ্যাডমিন অ্যাপ্রুভ করার পর লাইব্রেরিতে দেখাবে।", Toast.LENGTH_LONG).show()
                                } else {
                                    Toast.makeText(context, "❌ বইয়ের নাম লিখুন!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF009688))
                        ) {
                            Text("জমা দিন (Pending Admin)")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showBookSubmitDialog = false }) { Text("বাতিল") }
                    }
                )
            }

            // Secure In-App PDF Reader Viewer (No Download)
            activeReadingBook?.let { book ->
                AlertDialog(
                    onDismissRequest = { activeReadingBook = null },
                    modifier = Modifier.fillMaxHeight(0.9f),
                    title = {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("📖 ${book.title}", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Surface(color = Color.Red.copy(alpha = 0.2f), shape = RoundedCornerShape(6.dp)) {
                                Text("🔒 সিকিউর রিডার (ডাউনলোড নিষিদ্ধ)", fontSize = 9.sp, color = Color.Red, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                        }
                    },
                    text = {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("লেখক: ${book.author} • পাতা: ১ / ১২০", fontSize = 11.sp, color = Color.Gray)
                            Card(
                                modifier = Modifier.fillMaxWidth().height(360.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
                            ) {
                                Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "📖 [নিরাপদ ইন-অ্যাপ রিডার মোড]\n\n\"${book.title}\" বইটির ডিজিটাল কন্টেন্ট অত্যন্ত সুরক্ষিতভাবে কেবল রুমের ভেতরে পড়ার জন্য উন্মুক্ত। সুরক্ষার স্বার্থে এই ফাইল ডাউনলোড বা এক্সপোর্ট করা সম্পূর্ণ নিষিদ্ধ।\n\nপৃষ্ঠা ১: সূচিপত্র ও ভূমিকা...\nপৃষ্ঠা ২: মূল অধ্যায় শুরু...",
                                        fontSize = 13.sp,
                                        color = Color.DarkGray,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    },
                    confirmButton = {
                        Button(onClick = { activeReadingBook = null }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF009688))) {
                            Text("পড়া শেষ (বন্ধ করুন)")
                        }
                    }
                )
            }

            // 5. Local Audio Stream from Phone Storage Modal
            if (showLocalAudioStreamModal) {
                AlertDialog(
                    onDismissRequest = { showLocalAudioStreamModal = false },
                    icon = { Icon(imageVector = Icons.Default.Audiotrack, contentDescription = null, tint = Color(0xFFFF9800)) },
                    title = { Text("🎵 ফোন থেকে অডিও/গান ব্রডকাস্ট") },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text("আপনার ফোনের স্টোরেজ থেকে ডাউনলোড করা অডিও সিলেক্ট করে রুমে বাজান:", fontSize = 11.sp, color = Color.Gray)

                            listOf(
                                "অফলাইন_গান_০১_নবচেতনা_মিক্স.mp3",
                                "ইসলামিক_ওয়াজ_রেকর্ড_মাওলানা.mp3",
                                "পডকাস্ট_টেক_টক_বাংলা.mp3",
                                "লো-ফাই_রিল্যাক্সিং_মিউজিক.mp3"
                            ).forEach { track ->
                                OutlinedCard(
                                    modifier = Modifier.fillMaxWidth().clickable {
                                        selectedAudioTrack = track
                                        isStreamingAudioPlaying = true
                                        showLocalAudioStreamModal = false
                                        Toast.makeText(context, "▶️ স্ট্রিমিং শুরু: '$track'", Toast.LENGTH_LONG).show()
                                    },
                                    border = BorderStroke(1.dp, if (selectedAudioTrack == track && isStreamingAudioPlaying) Color(0xFFFF9800) else Color.LightGray)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = if (selectedAudioTrack == track && isStreamingAudioPlaying) Icons.Default.PlayArrow else Icons.Default.MusicNote,
                                                contentDescription = null,
                                                tint = Color(0xFFFF9800),
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(track, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                        }
                                        if (selectedAudioTrack == track && isStreamingAudioPlaying) {
                                            Surface(color = Color(0xFFFF9800), shape = CircleShape) {
                                                Text("LIVE", fontSize = 8.sp, color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    },
                    confirmButton = {
                        Button(onClick = { showLocalAudioStreamModal = false }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800))) {
                            Text("বন্ধ করুন")
                        }
                    }
                )
            }

            // Dynamic Global & Local Wallet Recharge Modal
            WalletRechargeModalDialog(
                isOpen = showWalletRechargeModal,
                onDismiss = { showWalletRechargeModal = false },
                viewModel = viewModel,
                currentUser = currentUser
            )

// -------------------------------------------------------------
// 1. CHATS CATEGORY CONTENT (PageBook Messenger)
// -------------------------------------------------------------
@Composable
fun ChatsCategoryContent(
    currentUser: User?,
    onOpenConversation: (ChatConversation) -> Unit
) {
    val sampleConversations = remember {
        listOf(
            ChatConversation(1, "Anika Rahman", "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150", "হ্যাঁ, নতুন ভয়েস রুম ও মিটিং ফিচারও চেক করুন!", "10:32 AM", unreadCount = 2, isOnline = true),
            ChatConversation(2, "তাহসীন আহমেদ", "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150", "আগামীকাল কি টেক লাইভ মিটিং হবে?", "Yesterday", isOnline = true),
            ChatConversation(3, "নবচেতনা অফিশিয়াল টিম (Group)", "https://images.unsplash.com/photo-1522071820081-009f0129c71c?w=150", "নতুন মেম্বারদের স্বাগতম!", "2 days ago", isGroup = true),
            ChatConversation(4, "রাফি হোসেন", "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=150", "অ্যালগরিদম আপডেট অনেক ভালো হয়েছে।", "3 days ago", isOnline = false)
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Online Active Story Bubbles Row
        item {
            Column {
                Text(
                    text = "অনলাইন আড্ডা & স্টোরিজ",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(TeaLeafGreen.copy(alpha = 0.15f))
                                    .border(1.5.dp, TeaLeafGreen, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Story", tint = TeaLeafGreen)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("আমার নোট", fontSize = 11.sp, fontWeight = FontWeight.Medium)
                        }
                    }

                    items(sampleConversations) { conv ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable { onOpenConversation(conv) }
                        ) {
                            Box(modifier = Modifier.size(56.dp)) {
                                AsyncImage(
                                    model = conv.avatarUrl,
                                    contentDescription = conv.name,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(56.dp)
                                        .clip(CircleShape)
                                        .border(2.dp, if (conv.isOnline) Color(0xFF00E676) else Color.Transparent, CircleShape)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = conv.name.split(" ")[0],
                                fontSize = 11.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(8.dp)) }

        // Conversations List Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "সাম্প্রতিক মেসেজসমূহ",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "${sampleConversations.size} টি চ্যাট",
                    fontSize = 12.sp,
                    color = TeaLeafGreen,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // List of Conversations
        items(sampleConversations, key = { it.id }) { conv ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenConversation(conv) },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.size(50.dp)) {
                        AsyncImage(
                            model = conv.avatarUrl,
                            contentDescription = conv.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                        )
                        if (conv.isOnline) {
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF00E676))
                                    .border(2.dp, Color.White, CircleShape)
                                    .align(Alignment.BottomEnd)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = conv.name,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = conv.timestamp,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = conv.lastMessage,
                                fontSize = 13.sp,
                                color = if (conv.unreadCount > 0) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = if (conv.unreadCount > 0) FontWeight.Bold else FontWeight.Normal,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )

                            if (conv.unreadCount > 0) {
                                Surface(
                                    shape = CircleShape,
                                    color = TeaLeafGreen
                                ) {
                                    Text(
                                        text = "${conv.unreadCount}",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
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
// 2. 14-SEAT GEN-Z VOICE ROOM CONTENT
// -------------------------------------------------------------
@Composable
fun VoiceRoom14CategoryContent(
    topic: String,
    categoryTag: String,
    speakers: List<VoiceStageSpeaker>,
    isMyMicMuted: Boolean,
    isHandRaised: Boolean,
    soundReactions: List<String>,
    myCreatedRoomsCount: Int,
    myClubsList: List<ClubRoomItem>,
    isCurrentRoomFollowed: Boolean,
    onCreateRoomClick: () -> Unit,
    onToggleMic: () -> Unit,
    onToggleHandRaise: () -> Unit,
    onSendEmojiReaction: (String) -> Unit,
    onOpenGames: () -> Unit,
    onOpenGifts: () -> Unit,
    onOpenLibrary: () -> Unit,
    onOpenAudioStream: () -> Unit,
    onToggleFollowCurrentRoom: () -> Unit,
    onSelectClub: (ClubRoomItem) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // "আমার ক্লাব" (My Clubs) List at the Top
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🌟 আমার ক্লাব ও রুমস (তৈরি: $myCreatedRoomsCount/৩)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "সর্বোচ্চ ৩টি রুম",
                        fontSize = 10.sp,
                        color = Color.Gray
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(myClubsList, key = { it.id }) { club ->
                        Card(
                            modifier = Modifier
                                .width(150.dp)
                                .clickable { onSelectClub(club) },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = if (club.title == topic) Color(0xFF1B4D3E) else MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, if (club.title == topic) Color(0xFF81C784) else Color.LightGray.copy(alpha = 0.5f))
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = if (club.isOwner) Color(0xFFFFD54F).copy(alpha = 0.2f) else Color(0xFF81C784).copy(alpha = 0.2f)
                                    ) {
                                        Text(
                                            text = if (club.isOwner) "👑 আমার রুম" else "⭐ ফলোয়েড",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (club.isOwner) Color(0xFFFFA000) else Color(0xFF388E3C),
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }
                                    if (club.isFollowed) {
                                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF81C784), modifier = Modifier.size(12.dp))
                                    }
                                }
                                Text(
                                    text = club.title,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "ট্যাগ: ${club.category}",
                                    fontSize = 10.sp,
                                    color = Color.Gray,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }

        // Header Stage Info Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F261C)),
                border = BorderStroke(1.dp, Color(0xFF81C784).copy(alpha = 0.5f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF81C784).copy(alpha = 0.2f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(Color.Red)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("🔴 LIVE 14-STAGE", color = Color(0xFF81C784), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color.White.copy(alpha = 0.1f)
                            ) {
                                Text(
                                    text = categoryTag,
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        // Follow Room & New Room Buttons
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = onToggleFollowCurrentRoom,
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(if (isCurrentRoomFollowed) Color(0xFFFFD54F) else Color.White.copy(alpha = 0.2f), CircleShape)
                            ) {
                                Icon(
                                    imageVector = if (isCurrentRoomFollowed) Icons.Default.Star else Icons.Default.StarBorder,
                                    contentDescription = "Follow Room",
                                    tint = if (isCurrentRoomFollowed) Color.Black else Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Button(
                                onClick = onCreateRoomClick,
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = TeaLeafGreen)
                            ) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(2.dp))
                                Text("নতুন রুম", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = topic,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.weight(1f)
                        )
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isCurrentRoomFollowed) Color(0xFF81C784) else Color.Gray
                        ) {
                            Text(
                                text = if (isCurrentRoomFollowed) "⭐ অনুসারিত (Followed)" else "➕ ফলো করুন",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "🎙️ ১৪ জন স্পিকার স্টেজ • ১২৮ জন শ্রোতা যুক্ত আছেন",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }
        }

        // Voice Room Feature Buttons Bar (Games, Gifts, Library, Phone Audio)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Button(
                    onClick = onOpenGames,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF673AB7)),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(4.dp)
                ) {
                    Icon(imageVector = Icons.Default.SportsEsports, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(2.dp))
                    Text("🎮 গেমস্", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onOpenGifts,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE91E63)),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(4.dp)
                ) {
                    Icon(imageVector = Icons.Default.CardGiftcard, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(2.dp))
                    Text("🎁 গিফট", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onOpenLibrary,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF009688)),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(4.dp)
                ) {
                    Icon(imageVector = Icons.Default.LibraryBooks, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(2.dp))
                    Text("📚 লাইব্রেরী", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onOpenAudioStream,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(4.dp)
                ) {
                    Icon(imageVector = Icons.Default.Audiotrack, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(2.dp))
                    Text("🎵 অডিও", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // 14 Speakers Grid (Gen-Z Neon Aura Stage)
        item {
            Text(
                text = "১৪ জন অফিশিয়াল স্পিকার স্টেজ",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        item {
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(380.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(speakers, key = { it.seatNumber }) { speaker ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            // Soundwave Animated Ring
                            if (speaker.isSpeaking) {
                                Box(
                                    modifier = Modifier
                                        .size(62.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF81C784).copy(alpha = 0.35f))
                                )
                            }

                            AsyncImage(
                                model = speaker.avatarUrl,
                                contentDescription = speaker.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .border(
                                        2.dp,
                                        if (speaker.isHost) Color(0xFFFFD54F) else if (speaker.isSpeaking) Color(0xFF81C784) else Color.Transparent,
                                        CircleShape
                                    )
                            )

                            // Seat Number Tag
                            Surface(
                                shape = CircleShape,
                                color = if (speaker.isHost) Color(0xFFFFD54F) else Color(0xFF1B4D3E),
                                modifier = Modifier.align(Alignment.TopStart)
                            ) {
                                Text(
                                    text = if (speaker.isHost) "👑" else "#${speaker.seatNumber}",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }

                            // Mic Muted Icon
                            if (speaker.isMuted) {
                                Box(
                                    modifier = Modifier
                                        .size(18.dp)
                                        .clip(CircleShape)
                                        .background(Color.Red)
                                        .align(Alignment.BottomEnd),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(imageVector = Icons.Default.MicOff, contentDescription = null, tint = Color.White, modifier = Modifier.size(10.dp))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = speaker.name,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        // Live Reactions Bar
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("সাউন্ড রিয়্যাকশন:", fontSize = 12.sp, fontWeight = FontWeight.Bold)

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("🔥", "👏", "🎉", "💖", "🎵", "🚀").forEach { emoji ->
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.clickable { onSendEmojiReaction(emoji) }
                            ) {
                                Text(emoji, fontSize = 18.sp, modifier = Modifier.padding(8.dp))
                            }
                        }
                    }
                }
            }
        }

        // Speaker Action Bar Bottom
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF102A20))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Mic Mute Toggle Button
                    Button(
                        onClick = onToggleMic,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isMyMicMuted) Color.Red else TeaLeafGreen
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = if (isMyMicMuted) Icons.Default.MicOff else Icons.Default.Mic, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isMyMicMuted) "মাইক আনমিউট" else "মাইক মিউট")
                    }

                    // Raise Hand Button
                    OutlinedButton(
                        onClick = onToggleHandRaise,
                        border = BorderStroke(1.dp, Color(0xFF81C784)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(if (isHandRaised) "✋ হাত নামান" else "✋ কথা বলতে হাত তুলুন", color = Color(0xFF81C784))
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 3. ULTRA HD AI MEETING CONTENT (Google Meet Architecture)
// -------------------------------------------------------------
@Composable
fun UltraHdMeetingCategoryContent(
    inMeeting: Boolean,
    meetingCode: String,
    meetingLink: String,
    isHost: Boolean,
    currentUser: User?,
    isVideoOn: Boolean,
    isMicOn: Boolean,
    isScreenSharing: Boolean,
    isWhiteboardActive: Boolean,
    isRecording: Boolean,
    isHandRaised: Boolean,
    isMeetingLocked: Boolean,
    captionsText: String,
    waitingGuestName: String?,
    liveMessages: List<MeetingLiveMessage>,
    onOrganizeNewMeeting: () -> Unit,
    onOpenJoinDialog: () -> Unit,
    onLeaveMeeting: () -> Unit,
    onToggleVideo: () -> Unit,
    onToggleMic: () -> Unit,
    onToggleScreenShare: () -> Unit,
    onToggleWhiteboard: () -> Unit,
    onToggleRecording: () -> Unit,
    onToggleHandRaise: () -> Unit,
    onToggleLockMeeting: () -> Unit,
    onAdmitWaitingGuest: () -> Unit,
    onDenyWaitingGuest: () -> Unit,
    onSendChatMessage: (String) -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    var isChatDrawerOpen by remember { mutableStateOf(false) }
    var isSecuritySheetOpen by remember { mutableStateOf(false) }
    var isArFilterDialogOpen by remember { mutableStateOf(false) }
    var selectedArFilter by remember { mutableStateOf("কোনোটি নয়") }
    var isWebRtcEngineDialogOpen by remember { mutableStateOf(false) }
    var isSpotlightView by remember { mutableStateOf(false) }
    var chatInputText by remember { mutableStateOf("") }
    val whiteboardNotes = remember {
        mutableStateListOf(
            "📌 এজেন্ডা ১: নবচেতনা Q3 প্রোডাক্ট ডেভেলপমেন্ট রোডম্যাপ",
            "📌 এজেন্ডা ২: গুগল মিটের মতো এনক্রিপ্টেড সিকিউর লিংক শেয়ারিং",
            "📌 এজেন্ডা ৩: আল্ট্রা এইচডি 1080p ও লাইভ এআই সাবটাইটেল কোয়ালিটি"
        )
    }
    var newWhiteboardNote by remember { mutableStateOf("") }
    var recordingTimerSeconds by remember { mutableStateOf(48) }

    LaunchedEffect(isRecording) {
        if (isRecording) {
            while (true) {
                delay(1000L)
                recordingTimerSeconds++
            }
        }
    }

    if (!inMeeting) {
        // Pre-Meeting Google Meet Lobby
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 600.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(CircleShape)
                            .background(TeaLeafGreen.copy(alpha = 0.15f))
                            .border(2.dp, TeaLeafGreen, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.VideoCall,
                            contentDescription = null,
                            tint = TeaLeafGreen,
                            modifier = Modifier.size(42.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "🔒 নবচেতনা প্রাইভেট গুগল মিট",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "সম্পূর্ণ এনক্রিপ্টেড ও সুরক্ষিত • গুগল মিটের মতো লিংক ছাড়া কেউ ঢুকতে পারবে না, দেখবেও না",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 6.dp, bottom = 16.dp)
                    )

                    // Security & Privacy Banner
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF1B4D3E).copy(alpha = 0.1f),
                        border = BorderStroke(1.dp, TeaLeafGreen.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = TeaLeafGreen, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "প্রাইভেট মিটিং সিকিউরিটি সক্রিয়",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "কোনো পাবলিক তালিকা নেই। আয়োজকের দেওয়া শেয়ারেবল লিংক বা সঠিক কোড ছাড়া মিটিং দৃশ্যমান হবে না।",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Button 1: Create New Meeting (Get Link)
                    Button(
                        onClick = onOrganizeNewMeeting,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = TeaLeafGreen)
                    ) {
                        Icon(imageVector = Icons.Default.Videocam, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("নতুন মিটিং শুরু করুন (লিংক পান)", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Button 2: Join with Link or Code
                    OutlinedButton(
                        onClick = onOpenJoinDialog,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.5.dp, TeaLeafGreen)
                    ) {
                        Icon(imageVector = Icons.Default.Link, contentDescription = null, tint = TeaLeafGreen)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("লিংক বা কোড দিয়ে যুক্ত হন", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TeaLeafGreen)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Feature highlights row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("⚡ 1080p 60fps", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                            Text("আল্ট্রা এইচডি ভিডিও", fontSize = 10.sp, color = Color.Gray)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🤖 বাংলা সাবটাইটেল", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                            Text("লাইভ এআই স্পিচ", fontSize = 10.sp, color = Color.Gray)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🎨 হোয়াইটবোর্ড", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                            Text("স্ক্রিন শেয়ারিং", fontSize = 10.sp, color = Color.Gray)
                        }
                    }
                }
            }
        }
    } else {
        // Active Ultra HD Google Meet Screen
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0D1E16))
                .padding(horizontal = 10.dp, vertical = 8.dp)
        ) {
            // Google Meet Top Bar
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF142B20),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Meeting Link Pill (1-Tap Copy)
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color.Black.copy(alpha = 0.4f),
                        modifier = Modifier.clickable {
                            clipboardManager.setText(AnnotatedString(meetingLink))
                            Toast.makeText(context, "📋 মিটিং লিংক কপি হয়েছে! বন্ধুদের পাঠান।", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.Link, contentDescription = null, tint = Color(0xFF81C784), modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "meet.nobocetona.com/$meetingCode",
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy", tint = Color(0xFF81C784), modifier = Modifier.size(13.dp))
                        }
                    }

                    // Recording indicator & Top Controls
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // REC Indicator Button
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (isRecording) Color.Red.copy(alpha = 0.25f) else Color.White.copy(alpha = 0.1f),
                            border = BorderStroke(1.dp, if (isRecording) Color.Red else Color.Transparent),
                            modifier = Modifier.clickable { onToggleRecording() }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(if (isRecording) Color.Red else Color.Gray)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isRecording) "REC ${String.format("%02d:%02d", recordingTimerSeconds / 60, recordingTimerSeconds % 60)}" else "REC",
                                    color = if (isRecording) Color.Red else Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Security Lock Status
                        IconButton(
                            onClick = onToggleLockMeeting,
                            modifier = Modifier.size(30.dp)
                        ) {
                            Icon(
                                imageVector = if (isMeetingLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                                contentDescription = "Lock",
                                tint = if (isMeetingLocked) Color(0xFFFFD54F) else Color.White.copy(alpha = 0.7f),
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        // View Switcher (Grid vs Spotlight)
                        IconButton(
                            onClick = { isSpotlightView = !isSpotlightView },
                            modifier = Modifier.size(30.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.GridView,
                                contentDescription = "Layout",
                                tint = if (isSpotlightView) Color(0xFF81C784) else Color.White.copy(alpha = 0.7f),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            // Waiting Room Admission Banner (If any guest requested to join)
            waitingGuestName?.let { guest ->
                Spacer(modifier = Modifier.height(6.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF2E7D32)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "🔔 $guest মিটিংয়ে প্রবেশের অনুমতি চাইছেন",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Button(
                                onClick = onAdmitWaitingGuest,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF81C784)),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("অনুমোদন", fontSize = 10.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                            }
                            OutlinedButton(
                                onClick = onDenyWaitingGuest,
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                                border = BorderStroke(1.dp, Color.White),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("বাতিল", fontSize = 10.sp)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Main Meeting Stage: Whiteboard / Screen Share / Video Grid
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                if (isWhiteboardActive) {
                    // Interactive Whiteboard Card
                    Card(
                        modifier = Modifier.fillMaxSize(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFAFAFA))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.Draw, contentDescription = null, tint = TeaLeafGreen)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("🎨 এআই ইন্টারেক্টিভ হোয়াইটবোর্ড ও নোটস", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 13.sp)
                                }
                                IconButton(onClick = onToggleWhiteboard, modifier = Modifier.size(28.dp)) {
                                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.Black)
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            // Notes list
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                items(whiteboardNotes) { note ->
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color(0xFFFFF9C4),
                                        border = BorderStroke(0.5.dp, Color(0xFFFBC02D)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = note,
                                            fontSize = 12.sp,
                                            color = Color.Black,
                                            modifier = Modifier.padding(10.dp)
                                        )
                                    }
                                }
                            }

                            // Add note bar
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = newWhiteboardNote,
                                    onValueChange = { newWhiteboardNote = it },
                                    placeholder = { Text("নতুন এজেন্ডা বা স্কেচ নোট লিখুন...", fontSize = 11.sp) },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = {
                                        if (newWhiteboardNote.isNotBlank()) {
                                            whiteboardNotes.add("📌 $newWhiteboardNote")
                                            newWhiteboardNote = ""
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = TeaLeafGreen)
                                ) {
                                    Text("যুক্ত করুন", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                } else if (isScreenSharing) {
                    // Presentation Screen Share View
                    Card(
                        modifier = Modifier.fillMaxSize(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1B3D2F)),
                        border = BorderStroke(1.5.dp, Color(0xFF81C784))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = CircleShape,
                                        color = Color(0xFF81C784),
                                        modifier = Modifier.size(10.dp)
                                    ) {}
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("📺 লাইভ স্ক্রিন প্রেজেন্টেশন চলছে", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color.Black.copy(alpha = 0.5f)
                                ) {
                                    Text("1080p 60fps HD", color = Color(0xFF81C784), fontSize = 10.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                }
                            }

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .padding(vertical = 12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F261C)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(imageVector = Icons.Default.ScreenShare, contentDescription = null, tint = Color(0xFF81C784), modifier = Modifier.size(60.dp))
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Text("উইন্ডো শেয়ার: [নবচেতনা_রোডম্যাপ_প্রেজেন্টেশন.pdf]", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                        Text("সকল পার্টিসিপেন্ট আল্ট্রা এইচডি কোয়ালিটিতে প্রেজেন্টেশন দেখছেন", color = Color.Gray, fontSize = 11.sp)
                                    }
                                }
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Button(
                                    onClick = onToggleScreenShare,
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("শেয়ারিং বন্ধ করুন")
                                }
                            }
                        }
                    }
                } else {
                    // Video Grid / Spotlight View
                    val participantsList = remember(currentUser, isHost, isVideoOn, isMicOn, isHandRaised) {
                        listOf(
                            MeetingParticipant(
                                id = "me",
                                name = (currentUser?.name ?: "আপনি (You)") + if (isHost) " (👑 হোস্ট)" else "",
                                avatarUrl = currentUser?.avatarUrl ?: "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=300",
                                isHost = isHost,
                                isSpeaking = false,
                                isMuted = !isMicOn,
                                isVideoOn = isVideoOn,
                                isHandRaised = isHandRaised
                            ),
                            MeetingParticipant(
                                id = "user2",
                                name = "তাহসীন আহমেদ",
                                avatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=300",
                                isHost = !isHost,
                                isSpeaking = true,
                                isMuted = false,
                                isVideoOn = true,
                                isHandRaised = false
                            ),
                            MeetingParticipant(
                                id = "user3",
                                name = "রাফি হোসেন",
                                avatarUrl = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=300",
                                isHost = false,
                                isSpeaking = false,
                                isMuted = true,
                                isVideoOn = true,
                                isHandRaised = false
                            ),
                            MeetingParticipant(
                                id = "user4",
                                name = "মিলা খান",
                                avatarUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=300",
                                isHost = false,
                                isSpeaking = false,
                                isMuted = false,
                                isVideoOn = true,
                                isHandRaised = true
                            )
                        )
                    }

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(participantsList, key = { it.id }) { participant ->
                            val isSpeaking = participant.isSpeaking
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF142B20)),
                                border = BorderStroke(
                                    width = if (isSpeaking) 2.5.dp else 1.dp,
                                    color = if (isSpeaking) Color(0xFF81C784) else Color.White.copy(alpha = 0.15f)
                                )
                            ) {
                                Box(modifier = Modifier.fillMaxSize()) {
                                    if (participant.isVideoOn) {
                                        AsyncImage(
                                            model = participant.avatarUrl,
                                            contentDescription = participant.name,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    } else {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(Color(0xFF1A3B2C)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = participant.name.take(1),
                                                fontSize = 32.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        }
                                    }

                                    // Top right badges: Raised Hand & Mic status
                                    Row(
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .padding(6.dp),
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        if (participant.isHandRaised) {
                                            Surface(
                                                shape = CircleShape,
                                                color = Color(0xFFFFD54F),
                                                modifier = Modifier.size(24.dp)
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Text("✋", fontSize = 12.sp)
                                                }
                                            }
                                        }
                                        Surface(
                                            shape = CircleShape,
                                            color = if (participant.isMuted) Color.Red else Color.Black.copy(alpha = 0.6f),
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    imageVector = if (participant.isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                                                    contentDescription = null,
                                                    tint = Color.White,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                            }
                                        }
                                    }

                                    // Bottom Name Badge
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color.Black.copy(alpha = 0.65f),
                                        modifier = Modifier
                                            .align(Alignment.BottomStart)
                                            .padding(6.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            if (isSpeaking) {
                                                Icon(imageVector = Icons.Default.VolumeUp, contentDescription = null, tint = Color(0xFF81C784), modifier = Modifier.size(12.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                            }
                                            Text(
                                                text = participant.name,
                                                fontSize = 11.sp,
                                                color = Color.White,
                                                fontWeight = FontWeight.SemiBold,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // AI Live Captions / Subtitles Bar
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.Black.copy(alpha = 0.8f),
                border = BorderStroke(0.5.dp, Color(0xFF81C784).copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFF81C784).copy(alpha = 0.2f)
                    ) {
                        Text("CC বাংলা", color = Color(0xFF81C784), fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = captionsText,
                        color = Color(0xFF81C784),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Bottom Google Meet Pill Action Bar
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color(0xFF142B20),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Mic
                    IconButton(
                        onClick = onToggleMic,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(if (isMicOn) Color.White.copy(alpha = 0.15f) else Color.Red)
                    ) {
                        Icon(imageVector = if (isMicOn) Icons.Default.Mic else Icons.Default.MicOff, contentDescription = "Mic", tint = Color.White, modifier = Modifier.size(20.dp))
                    }

                    // Video
                    IconButton(
                        onClick = onToggleVideo,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(if (isVideoOn) Color.White.copy(alpha = 0.15f) else Color.Red)
                    ) {
                        Icon(imageVector = if (isVideoOn) Icons.Default.Videocam else Icons.Default.VideocamOff, contentDescription = "Video", tint = Color.White, modifier = Modifier.size(20.dp))
                    }

                    // Hand Raise
                    IconButton(
                        onClick = onToggleHandRaise,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(if (isHandRaised) Color(0xFFFFD54F) else Color.White.copy(alpha = 0.15f))
                    ) {
                        Text("✋", fontSize = 16.sp)
                    }

                    // Screen Share
                    IconButton(
                        onClick = onToggleScreenShare,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(if (isScreenSharing) Color(0xFF81C784) else Color.White.copy(alpha = 0.15f))
                    ) {
                        Icon(imageVector = Icons.Default.ScreenShare, contentDescription = "Share", tint = Color.White, modifier = Modifier.size(20.dp))
                    }

                    // Whiteboard
                    IconButton(
                        onClick = onToggleWhiteboard,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(if (isWhiteboardActive) Color(0xFF81C784) else Color.White.copy(alpha = 0.15f))
                    ) {
                        Icon(imageVector = Icons.Default.Draw, contentDescription = "Whiteboard", tint = Color.White, modifier = Modifier.size(20.dp))
                    }

                    // Live Chat
                    IconButton(
                        onClick = { isChatDrawerOpen = true },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.15f))
                    ) {
                        Icon(imageVector = Icons.Default.Chat, contentDescription = "Chat", tint = Color.White, modifier = Modifier.size(20.dp))
                    }

                    // AR Face Filters
                    IconButton(
                        onClick = { isArFilterDialogOpen = true },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(if (selectedArFilter != "কোনোটি নয়") Color(0xFFFFD54F) else Color.White.copy(alpha = 0.15f))
                    ) {
                        Icon(imageVector = Icons.Default.Face, contentDescription = "AR Filters", tint = if (selectedArFilter != "কোনোটি নয়") Color.Black else Color.White, modifier = Modifier.size(20.dp))
                    }

                    // WebRTC Engine Info
                    IconButton(
                        onClick = { isWebRtcEngineDialogOpen = true },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.15f))
                    ) {
                        Icon(imageVector = Icons.Default.CloudSync, contentDescription = "WebRTC Engine", tint = Color(0xFF81C784), modifier = Modifier.size(20.dp))
                    }

                    // Security / Host
                    IconButton(
                        onClick = { isSecuritySheetOpen = true },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(if (isMeetingLocked) Color(0xFFFFD54F) else Color.White.copy(alpha = 0.15f))
                    ) {
                        Icon(imageVector = Icons.Default.Security, contentDescription = "Security", tint = if (isMeetingLocked) Color.Black else Color.White, modifier = Modifier.size(20.dp))
                    }

                    // Leave Call
                    Button(
                        onClick = onLeaveMeeting,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                        shape = RoundedCornerShape(16.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(imageVector = Icons.Default.CallEnd, contentDescription = "Leave", tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }

    // In-Meeting Live Chat Modal Sheet
    if (isChatDrawerOpen) {
        AlertDialog(
            onDismissRequest = { isChatDrawerOpen = false },
            modifier = Modifier.fillMaxHeight(0.85f),
            title = {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("💬 মিটিং ইন-কল মেসেজ", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    IconButton(onClick = { isChatDrawerOpen = false }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Reactions Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        listOf("👏", "❤️", "🎉", "🚀", "👍", "💡").forEach { emoji ->
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.clickable {
                                    onSendChatMessage(emoji)
                                }
                            ) {
                                Text(emoji, fontSize = 18.sp, modifier = Modifier.padding(6.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(liveMessages) { msg ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp)
                            ) {
                                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                    Text(msg.senderName, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TeaLeafGreen)
                                    Text(msg.time, fontSize = 10.sp, color = Color.Gray)
                                }
                                Text(msg.message, fontSize = 12.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = chatInputText,
                            onValueChange = { chatInputText = it },
                            placeholder = { Text("ইন-কল মেসেজ লিখুন...", fontSize = 12.sp) },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        IconButton(
                            onClick = {
                                if (chatInputText.isNotBlank()) {
                                    onSendChatMessage(chatInputText)
                                    chatInputText = ""
                                }
                            }
                        ) {
                            Icon(imageVector = Icons.Default.Send, contentDescription = "Send", tint = TeaLeafGreen)
                        }
                    }
                }
            },
            confirmButton = {}
        )
    }

    // Host Security Controls Dialog
    if (isSecuritySheetOpen) {
        AlertDialog(
            onDismissRequest = { isSecuritySheetOpen = false },
            icon = { Icon(imageVector = Icons.Default.Security, contentDescription = null, tint = TeaLeafGreen) },
            title = { Text("🛡️ হোস্ট ও সিকিউরিটি কন্ট্রোল") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("মিটিং নিরাপত্তা ও গেস্ট পারমিশন নিয়ন্ত্রণ করুন:", fontSize = 12.sp, color = Color.Gray)

                    // Lock Meeting toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("মিটিং লক করুন", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("লক থাকলে লিংক থাকলেও নতুন কেউ ঢুকতে পারবে না", fontSize = 11.sp, color = Color.Gray)
                        }
                        Switch(
                            checked = isMeetingLocked,
                            onCheckedChange = { onToggleLockMeeting() }
                        )
                    }

                    HorizontalDivider()

                    // Mute All Button
                    Button(
                        onClick = {
                            isSecuritySheetOpen = false
                            Toast.makeText(context, "🔇 সকল পার্টিসিপেন্টকে মিউট করা হয়েছে!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828)),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("সকলকে একসাথে মিউট করুন")
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { isSecuritySheetOpen = false },
                    colors = ButtonDefaults.buttonColors(containerColor = TeaLeafGreen)
                ) {
                    Text("ঠিক আছে")
                }
            }
        )
    }

    // AR Face Filter Selection Dialog
    if (isArFilterDialogOpen) {
        AlertDialog(
            onDismissRequest = { isArFilterDialogOpen = false },
            icon = { Icon(imageVector = Icons.Default.Face, contentDescription = null, tint = Color(0xFFFFD54F)) },
            title = { Text("🎭 এআই ফেস ফিল্টার্স ও স্টুডিও এফেক্ট") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("লাইভ ক্যামেরায় রিয়েল-টাইম অগমেন্টেড রিয়ালিটি (AR) ফিল্টার যুক্ত করুন:", fontSize = 11.sp, color = Color.Gray)

                    listOf(
                        "কোনোটি নয়" to "ফিল্টার ছাড়া স্বাভাবিক ভিডিও",
                        "👑 ভিআইপি গোল্ডেন ক্রাউন" to "মাথায় রাজকীয় সোনার মুকুট ও গ্লো",
                        "🕶️ নিয়ন সাইবার ভাইজর" to "হাইপার-ফিউচারিস্টিক সাইবার চশমা",
                        "💡 স্টুডিও প্রো লাইটিং" to "স্টুডিও সফট কী-লাইট ও ফেস ব্রাইটনেস",
                        "✨ গোল্ডেন অরা ও গ্লো" to "সফট স্কিন বিউটি ও গোল্ডেন আভা",
                        "🌸 ফ্লাওয়ার হ্যালো" to "মাথায় সুন্দর ফুলের মুকুট"
                    ).forEach { (filterName, desc) ->
                        val isSelected = selectedArFilter == filterName
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedArFilter = filterName
                                    isArFilterDialogOpen = false
                                    Toast.makeText(context, "AR ফিল্টার চালু: $filterName", Toast.LENGTH_SHORT).show()
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) Color(0xFFFFD54F).copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                            ),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(if (isSelected) 1.5.dp else 0.5.dp, if (isSelected) Color(0xFFFFD54F) else Color.LightGray)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(filterName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text(desc, fontSize = 10.sp, color = Color.Gray)
                                }
                                if (isSelected) {
                                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFFFFD54F), modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { isArFilterDialogOpen = false }, colors = ButtonDefaults.buttonColors(containerColor = TeaLeafGreen)) {
                    Text("বন্ধ করুন")
                }
            }
        )
    }

    // WebRTC / LiveKit Media Engine Info Dialog
    if (isWebRtcEngineDialogOpen) {
        AlertDialog(
            onDismissRequest = { isWebRtcEngineDialogOpen = false },
            icon = { Icon(imageVector = Icons.Default.CloudSync, contentDescription = null, tint = Color(0xFF81C784)) },
            title = { Text("📡 রিয়েল-টাইম WebRTC মিডিয়া ইঞ্জিন") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF1B4D3E).copy(alpha = 0.2f),
                        border = BorderStroke(0.5.dp, Color(0xFF81C784)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("সার্ভার স্ট্যাটাস: 🟢 কানেক্টেড (Live)", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF81C784))
                            Text("সিগন্যালিং নোড: wss://live.nobocetona.com/ws/v1", fontSize = 10.sp, color = Color.Gray)
                            Text("ল্যাটেন্সি: ১৮ms • প্যাকেট লস: ০.০০%", fontSize = 10.sp, color = Color.Gray)
                            Text("হার্ডওয়্যার কোডেক: VP9 / AV1 1080p 60fps", fontSize = 10.sp, color = Color.Gray)
                            Text("STUN/TURN: Google Live Relay Clustered", fontSize = 10.sp, color = Color.Gray)
                        }
                    }
                    Text("গুগল মিট আর্কিটেকচারের মতো মাল্টি-স্ট্রিম SFU ক্লাস্টার দ্বারা পরিচালিত। ট্রিলিয়ন ইউজার হ্যান্ডেল করতে সক্ষম।", fontSize = 11.sp, color = Color.Gray)
                }
            },
            confirmButton = {
                Button(onClick = { isWebRtcEngineDialogOpen = false }, colors = ButtonDefaults.buttonColors(containerColor = TeaLeafGreen)) {
                    Text("ঠিক আছে")
                }
            }
        )
    }
}

// -------------------------------------------------------------
// CHAT CONVERSATION WINDOW MODAL
// -------------------------------------------------------------
@Composable
fun ChatConversationWindow(
    conversation: ChatConversation,
    currentUser: User?,
    messages: List<ChatMessage>,
    inputMessage: String,
    onMessageInputChange: (String) -> Unit,
    onSendMessage: () -> Unit,
    onStartCall: (String) -> Unit,
    onClose: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxSize(),
        shape = RoundedCornerShape(0.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Chat Top Bar
            Surface(
                color = TeaLeafGreen,
                elevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onClose) {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }

                        AsyncImage(
                            model = conversation.avatarUrl,
                            contentDescription = conversation.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Text(conversation.name, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)
                            Text(if (conversation.isOnline) "সক্রিয় আছেন" else "অফলাইন", fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f))
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { onStartCall("AUDIO") }) {
                            Icon(imageVector = Icons.Default.Call, contentDescription = "Audio Call", tint = Color.White)
                        }

                        IconButton(onClick = { onStartCall("VIDEO") }) {
                            Icon(imageVector = Icons.Default.Videocam, contentDescription = "Video Call", tint = Color.White)
                        }
                    }
                }
            }

            // Message History
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                items(messages, key = { it.id }) { msg ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (msg.isMe) Arrangement.End else Arrangement.Start
                    ) {
                        if (!msg.isMe) {
                            AsyncImage(
                                model = msg.senderAvatar,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }

                        Column(horizontalAlignment = if (msg.isMe) Alignment.End else Alignment.Start) {
                            Surface(
                                shape = RoundedCornerShape(
                                    topStart = 16.dp,
                                    topEnd = 16.dp,
                                    bottomStart = if (msg.isMe) 16.dp else 4.dp,
                                    bottomEnd = if (msg.isMe) 4.dp else 16.dp
                                ),
                                color = if (msg.isMe) TeaLeafGreen else MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Text(
                                    text = msg.text,
                                    color = if (msg.isMe) Color.White else MaterialTheme.colorScheme.onSurface,
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(top = 2.dp)
                            ) {
                                Text(msg.timestamp, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                if (msg.reaction != null) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(msg.reaction, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }

            // Bottom Input Bar
            Surface(
                elevation = 8.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {}) {
                        Icon(imageVector = Icons.Default.Mic, contentDescription = "Voice Note", tint = TeaLeafGreen)
                    }

                    IconButton(onClick = {}) {
                        Icon(imageVector = Icons.Default.Image, contentDescription = "Attach", tint = TeaLeafGreen)
                    }

                    OutlinedTextField(
                        value = inputMessage,
                        onValueChange = onMessageInputChange,
                        placeholder = { Text("মেসেজ লিখুন...", fontSize = 13.sp) },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(24.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(
                        onClick = onSendMessage,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(TeaLeafGreen)
                    ) {
                        Icon(imageVector = Icons.Default.Send, contentDescription = "Send", tint = Color.White)
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// ACTIVE CALL OVERLAY (Audio & Video with AI Live Translation & Wallet Billing)
// -------------------------------------------------------------
@Composable
fun ActiveCallOverlay(
    callType: String,
    recipientName: String,
    recipientAvatar: String,
    currentUser: User?,
    viewModel: PageBookViewModel,
    onEndCall: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var isTranslationActive by remember { mutableStateOf(false) }
    var translatedCaption by remember { mutableStateOf("") }
    var translationSeconds by remember { mutableStateOf(0) }

    // Auto-detect user language based on phone number / prefix
    val userPhone = currentUser?.emailOrPhone ?: "+880"
    val myLanguage = when {
        userPhone.startsWith("+1") -> "English"
        userPhone.startsWith("+966") -> "Arabic"
        userPhone.startsWith("+91") -> "Hindi"
        else -> "বাংলা (Bengali)"
    }

    // Billing and Translation Timer Loop
    LaunchedEffect(isTranslationActive) {
        if (isTranslationActive) {
            while (isTranslationActive) {
                delay(1000L) // 1 second interval
                translationSeconds++
                
                // Every 60 seconds (1 minute), deduct 1 BDT
                if (translationSeconds % 60 == 0) {
                    val currentBal = currentUser?.walletBalance ?: 0.0
                    if (currentBal >= 1.0) {
                        val newBal = currentBal - 1.0
                        viewModel.updateWalletBalance(newBal)
                        Toast.makeText(context, "💳 কল অনুবাদের জন্য ১ টাকা কাটা হয়েছে। বর্তমান ব্যালেন্স: ৳${String.format("%.2f", newBal)}", Toast.LENGTH_SHORT).show()
                    } else {
                        isTranslationActive = false
                        translatedCaption = ""
                        Toast.makeText(context, "⚠️ ওয়ালেটে পর্যাপ্ত ব্যালেন্স না থাকায় লাইভ অনুবাদ বন্ধ করা হয়েছে!", Toast.LENGTH_LONG).show()
                    }
                }

                // Simulate live translation captions
                if (translationSeconds % 5 == 0) {
                    translatedCaption = "🗣️ ($myLanguage): \"অটো অনুবাদ চলছে... অপর পাশের কথা আপনার মাতৃভাষায় রূপান্তরিত হচ্ছে।\""
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D2818))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 40.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.White.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = if (callType == "VIDEO") "📹 নবচেতনা এইচডি ভিডিও কল" else "📞 নবচেতনা এইচডি অডিও কল",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                AsyncImage(
                    model = recipientAvatar,
                    contentDescription = recipientName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape)
                        .border(3.dp, if (isTranslationActive) Color(0xFFFFD700) else Color(0xFF81C784), CircleShape)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(recipientName, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text(
                    text = if (isTranslationActive) "🌐 লাইভ অনুবাদ সক্রিয় (৳১/মি) • ${translationSeconds / 60}m ${translationSeconds % 60}s" else "কল চলছে... (00:45)",
                    fontSize = 13.sp,
                    color = if (isTranslationActive) Color(0xFFFFD700) else Color(0xFF81C784),
                    fontWeight = FontWeight.Medium
                )

                if (isTranslationActive && translatedCaption.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        color = Color.Black.copy(alpha = 0.7f),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color(0xFFFFD700).copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.VolumeUp, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("নিউরাল ভয়েস আইসোলেশন (ডাবল কণ্ঠ রোধ ও অরিজিনাল মিউট)", fontSize = 10.sp, color = Color(0xFFFFD700), fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = translatedCaption,
                                color = Color(0xFFE8F5E9),
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 30.dp)
            ) {
                // Translation Toggle Button
                Button(
                    onClick = {
                        val currentBal = currentUser?.walletBalance ?: 0.0
                        if (!isTranslationActive && currentBal < 1.0) {
                            Toast.makeText(context, "❌ ওয়ালেটে পর্যাপ্ত ব্যালেন্স (কমপক্ষে ৳১.০০) নেই! অনুগ্রহ করে রিচার্জ করুন।", Toast.LENGTH_LONG).show()
                        } else {
                            isTranslationActive = !isTranslationActive
                            if (isTranslationActive) {
                                Toast.makeText(context, "🌐 এআই লাইভ অনুবাদ চালু হয়েছে (প্রতি মিনিটে ১ টাকা চার্জ প্রযোজ্য)", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "⏸️ লাইভ অনুবাদ বন্ধ করা হয়েছে।", Toast.LENGTH_SHORT).show()
                                translatedCaption = ""
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isTranslationActive) Color(0xFFFFD700) else Color.White.copy(alpha = 0.2f)
                    ),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Translate,
                        contentDescription = null,
                        tint = if (isTranslationActive) Color.Black else Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isTranslationActive) "অনুবাদ চলছে (৳১/মি)" : "🌐 লাইভ অনুবাদ অন করুন (৳১/মি)",
                        color = if (isTranslationActive) Color.Black else Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {},
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f))
                    ) {
                        Icon(imageVector = Icons.Default.Mic, contentDescription = null, tint = Color.White)
                    }

                    IconButton(
                        onClick = onEndCall,
                        modifier = Modifier
                            .size(68.dp)
                            .clip(CircleShape)
                            .background(Color.Red)
                    ) {
                        Icon(imageVector = Icons.Default.CallEnd, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
                    }

                }
            }
        }
    }
}

// -------------------------------------------------------------
// NOBOCETONA GAME LOUNGE & COIN MARKET (Fruit Wheel, Color Wheel, Dice, Coin Market)
// -------------------------------------------------------------
@Composable
fun NobocetonaGameLoungeDialog(
    currentUser: User?,
    viewModel: PageBookViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var activeGameTab by remember { mutableStateOf(1) } // 1: Fruit Wheel, 2: Color Wheel, 3: Dice, 4: Coin Market
    
    var userCoins by remember { mutableStateOf(50000L) }
    var wonCoins by remember { mutableStateOf(0L) }
    var selectedBetAmount by remember { mutableStateOf(1000L) } // 500, 1K, 5K, 10K, 50K
    
    // Timer state for 20 seconds
    var timeLeft by remember { mutableStateOf(20) }
    var gameStatusText by remember { mutableStateOf("বাজি লাগানোর সময় চলছে...") }
    
    // Anonymous stake pools per house
    val fruitStakes = remember { mutableStateMapOf<Int, Long>(0 to 12500L, 1 to 8000L, 2 to 15000L, 3 to 4500L, 4 to 22000L, 5 to 9000L, 6 to 11000L, 7 to 18000L, 8 to 6000L, 9 to 14000L) }
    val colorStakes = remember { mutableStateMapOf<Int, Long>(0 to 35000L, 1 to 12000L, 2 to 48000L, 3 to 9000L, 4 to 21000L, 5 to 15000L, 6 to 30000L) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000L)
            if (timeLeft > 1) {
                timeLeft--
                if (timeLeft == 5) {
                    gameStatusText = "⚠️ বাজি বন্ধ! ফলাফল প্রস্তুত হচ্ছে..."
                }
            } else {
                timeLeft = 20
                gameStatusText = "🎉 ফলাফল ঘোষণা হয়েছে! নতুন রাউন্ড শুরু..."
                wonCoins += (selectedBetAmount * 2)
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxWidth(0.965f).fillMaxHeight(0.92f),
        containerColor = Color(0xFF0D1B12),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("🎮 নবচেতনা লাইভ গেমিং লাউঞ্জ", color = Color(0xFFFFD700), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text("বিজয়ী জিতা কয়েন: 🪙 $wonCoins", color = Color(0xFF81C784), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF1B4D3E)
                ) {
                    Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("🪙", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("ব্যালেন্স: $userCoins", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Game Navigation Tabs
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Button(
                        onClick = { activeGameTab = 1 },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = if (activeGameTab == 1) TeaLeafGreen else Color.DarkGray),
                        contentPadding = PaddingValues(2.dp)
                    ) { Text("🍎 ফল রৌলেট", fontSize = 10.sp) }

                    Button(
                        onClick = { activeGameTab = 2 },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = if (activeGameTab == 2) TeaLeafGreen else Color.DarkGray),
                        contentPadding = PaddingValues(2.dp)
                    ) { Text("🎨 ৭ রঙ টেবিল", fontSize = 10.sp) }

                    Button(
                        onClick = { activeGameTab = 3 },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = if (activeGameTab == 3) TeaLeafGreen else Color.DarkGray),
                        contentPadding = PaddingValues(2.dp)
                    ) { Text("🎲 ছক্কা খেলা", fontSize = 10.sp) }

                    Button(
                        onClick = { activeGameTab = 4 },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = if (activeGameTab == 4) Color(0xFFE91E63) else Color.DarkGray),
                        contentPadding = PaddingValues(2.dp)
                    ) { Text("💰 কয়েন বাজার", fontSize = 10.sp) }
                }

                // Bet Amount Selector (500, 1K, 5K, 10K, 50K)
                if (activeGameTab in 1..3) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("বাজির পরিমাণ:", color = Color.White, fontSize = 11.sp)
                        listOf(500L, 1000L, 5000L, 10000L, 50000L).forEach { amt ->
                            val label = if (amt >= 1000) "${amt / 1000}K" else "$amt"
                            OutlinedButton(
                                onClick = { selectedBetAmount = amt },
                                border = BorderStroke(1.dp, if (selectedBetAmount == amt) Color(0xFFFFD700) else Color.Gray),
                                colors = ButtonDefaults.outlinedButtonColors(containerColor = if (selectedBetAmount == amt) Color(0xFFFFD700).copy(alpha = 0.2f) else Color.Transparent),
                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(label, color = if (selectedBetAmount == amt) Color(0xFFFFD700) else Color.White, fontSize = 10.sp)
                            }
                        }
                    }
                }

                // Center Timer & Status Banner
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF132E22)
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(gameStatusText, color = Color(0xFF81C784), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Surface(
                            shape = CircleShape,
                            color = Color.Red
                        ) {
                            Text("⏳ ${timeLeft}s", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                        }
                    }
                }

                // Active Game Content
                when (activeGameTab) {
                    1 -> {
                        // Game 1: 10 Circular Fruit Houses Round Table
                        Text("🍎 ১০ টি ফল ঘর (গোল টেবিল - সার্বজনীন বাজি)", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        val fruits = listOf("🍎 আপেল", "🍌 কলা", "🥭 আম", "🍊 কমলা", "🍍 আনারস", "🍉 তরমুজ", "🍓 স্ট্রবেরি", "🍇 আঙুর", "🍒 চেরি", "🍑 পেচ");
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(5),
                            modifier = Modifier.fillMaxWidth().height(260.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            items(10) { index ->
                                val fruitName = fruits[index]
                                val stakePool = fruitStakes[index] ?: 0L
                                Card(
                                    modifier = Modifier.fillMaxWidth().clickable {
                                        if (userCoins >= selectedBetAmount) {
                                            userCoins -= selectedBetAmount
                                            fruitStakes[index] = stakePool + selectedBetAmount
                                            Toast.makeText(context, "$fruitName ঘরে $selectedBetAmount কয়েন বাজি লাগিয়েছেন!", Toast.LENGTH_SHORT).show()
                                        } else {
                                            Toast.makeText(context, "❌ পর্যাপ্ত কয়েন নেই! কয়েন বাজার থেকে কিনুন।", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1B4D3E)),
                                    border = BorderStroke(1.dp, Color(0xFF81C784).copy(alpha = 0.6f))
                                ) {
                                    Column(
                                        modifier = Modifier.padding(6.dp).fillMaxWidth(),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(fruitName, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White, textAlign = TextAlign.Center)
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text("বাট: ${stakePool}C", fontSize = 9.sp, color = Color(0xFFFFD700))
                                    }
                                }
                            }
                        }
                    }
                    2 -> {
                        // Game 2: 7 Colored Houses Round Table with Multipliers
                        Text("🎨 ৭ রঙের ঘর ও মাল্টিপ্লায়ার (2x, 5x, 3x, 10x, 20x, 15x, 40x)", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        val colorHouses = listOf(
                            Triple("🔴 লাল", "2x", Color(0xFFE53935)),
                            Triple("🟢 সবুজ", "5x", Color(0xFF43A047)),
                            Triple("🔵 নীল", "3x", Color(0xFF1E88E5)),
                            Triple("🟡 হলুদ", "10x", Color(0xFFFBC02D)),
                            Triple("🟣 বেগুনি", "20x", Color(0xFF8E24AA)),
                            Triple("🟠 কমলা", "15x", Color(0xFFFB8C00)),
                            Triple("⚪ সাদা", "40x", Color(0xFFB0BEC5))
                        )
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(4),
                            modifier = Modifier.fillMaxWidth().height(260.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            items(colorHouses.size) { index ->
                                val (cName, mult, cColor) = colorHouses[index]
                                val stakePool = colorStakes[index] ?: 0L
                                Card(
                                    modifier = Modifier.fillMaxWidth().clickable {
                                        if (userCoins >= selectedBetAmount) {
                                            userCoins -= selectedBetAmount
                                            colorStakes[index] = stakePool + selectedBetAmount
                                            Toast.makeText(context, "$cName ($mult) ঘরে $selectedBetAmount কয়েন বাজি লাগিয়েছেন!", Toast.LENGTH_SHORT).show()
                                        } else {
                                            Toast.makeText(context, "❌ পর্যাপ্ত কয়েন নেই!", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    colors = CardDefaults.cardColors(containerColor = cColor),
                                    border = BorderStroke(1.dp, Color.White)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(8.dp).fillMaxWidth(),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(cName, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                        Text(mult, fontSize = 14.sp, fontWeight = FontWeight.Black, color = Color(0xFFFFD700))
                                        Text("বাট: ${stakePool}C", fontSize = 9.sp, color = Color.White.copy(alpha = 0.9f))
                                    }
                                }
                            }
                        }
                    }
                    3 -> {
                        // Game 3: Dice Battle (ছক্কা খেলা - দুই জোড়া গোড়া/পাশা)
                        Text("🎲 ছক্কা খেলা ও দুই পাশা ব্যাটল (সমান গুটি ম্যাচ)", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Column(
                            modifier = Modifier.fillMaxWidth().height(260.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                Surface(modifier = Modifier.size(60.dp), shape = RoundedCornerShape(12.dp), color = Color.White) {
                                    Box(contentAlignment = Alignment.Center) { Text("⚅ 6", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black) }
                                }
                                Surface(modifier = Modifier.size(60.dp), shape = RoundedCornerShape(12.dp), color = Color.White) {
                                    Box(contentAlignment = Alignment.Center) { Text("⚄ 5", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black) }
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("সমান গুটি (Double Match) বাজি লাগান (3x পেমেন্ট)", color = Color(0xFFFFD700), fontSize = 12.sp)
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = {
                                    if (userCoins >= selectedBetAmount) {
                                        userCoins -= selectedBetAmount
                                        Toast.makeText(context, "🎲 ছক্কা খেলায় $selectedBetAmount কয়েন বাজি সফল!", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "❌ পর্যাপ্ত কয়েন নেই!", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = TeaLeafGreen)
                            ) {
                                Text("ছক্কা বাজি নিশ্চিত করুন")
                            }
                        }
                    }
                    4 -> {
                        // Game 4 / Coin Market: 1 Lakh Coins = 100 Taka
                        Column(
                            modifier = Modifier.fillMaxWidth().height(260.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("💰 নবচেতনা ডিজিটাল কয়েন বাজার", color = Color(0xFFFFD700), fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            Text("আপনার ওয়ালেট ব্যালেন্স: ৳${currentUser?.walletBalance ?: 0.0}", color = Color.White, fontSize = 13.sp)
                            
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF1B4D3E)),
                                border = BorderStroke(1.dp, Color(0xFFFFD700))
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("🪙 ১ লক্ষ (100,000) কয়েন", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("মূল্য: মাত্র ১০০ টাকা (৳100)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFD700))
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Button(
                                        onClick = {
                                            val currentWallet = currentUser?.walletBalance ?: 0.0
                                            if (currentWallet >= 100.0) {
                                                viewModel.updateWalletBalance(currentWallet - 100.0)
                                                userCoins += 100000L
                                                Toast.makeText(context, "🎉 সফলভাবে ১,০০,০০০ কয়েন ক্রয় করা হয়েছে! ৳১০০ কেটে নেওয়া হয়েছে।", Toast.LENGTH_LONG).show()
                                            } else {
                                                Toast.makeText(context, "❌ ওয়ালেটে পর্যাপ্ত টাকা (৳১০০) নেই!", Toast.LENGTH_LONG).show()
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE91E63))
                                    ) {
                                        Text("৳১০০ দিয়ে ১ লক্ষ কয়েন কিনুন")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = TeaLeafGreen)) {
                Text("লাউঞ্জ বন্ধ করুন")
            }
        }
    )
}
