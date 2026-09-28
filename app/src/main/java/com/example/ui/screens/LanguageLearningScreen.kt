package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.data.model.*
import com.example.ui.theme.TeaLeafGreen
import com.example.ui.viewmodel.LanguageLearningViewModel
import kotlinx.coroutines.delay
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageLearningScreen(
    currentUser: User?,
    onBack: () -> Unit,
    viewModel: LanguageLearningViewModel = viewModel(factory = LanguageLearningViewModel.provideFactory(LocalContext.current))
) {
    val context = LocalContext.current
    val languages by viewModel.languages.collectAsStateWithLifecycle()
    val selectedLanguage by viewModel.selectedLanguage.collectAsStateWithLifecycle()
    val levels by viewModel.currentLevels.collectAsStateWithLifecycle()
    val leaderboard by viewModel.currentLeaderboard.collectAsStateWithLifecycle()
    val activeAds by viewModel.activeSponsoredAds.collectAsStateWithLifecycle()
    val allCertificates by viewModel.allCertificates.collectAsStateWithLifecycle()

    val activePracticeWords by viewModel.activePracticeWords.collectAsStateWithLifecycle()
    val activeExamWords by viewModel.activeExamWords.collectAsStateWithLifecycle()
    val activeLevel by viewModel.activeLevel.collectAsStateWithLifecycle()

    var showLeaderboardSheet by remember { mutableStateOf(false) }
    var showCertificateDialog by remember { mutableStateOf(false) }
    var showPracticeCampDialog by remember { mutableStateOf(false) }
    var showExamDialog by remember { mutableStateOf(false) }
    var showSpokenVoiceClubDialog by remember { mutableStateOf(false) }

    // Text To Speech engine
    var ttsEngine by remember { mutableStateOf<TextToSpeech?>(null) }
    DisposableEffect(Unit) {
        var tts: TextToSpeech? = null
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.US
            }
        }
        ttsEngine = tts
        onDispose {
            tts?.stop()
            tts?.shutdown()
        }
    }

    fun speakWord(text: String, langCode: String) {
        val targetLocale = when (langCode.lowercase(Locale.ROOT)) {
            "en" -> Locale.US
            "ar" -> Locale.forLanguageTag("ar")
            "es" -> Locale.forLanguageTag("es")
            "fr" -> Locale.FRENCH
            "de" -> Locale.GERMAN
            "ja" -> Locale.JAPANESE
            "ko" -> Locale.KOREAN
            "zh" -> Locale.CHINESE
            "ru" -> Locale.forLanguageTag("ru")
            "tr" -> Locale.forLanguageTag("tr")
            else -> Locale.US
        }
        ttsEngine?.language = targetLocale
        ttsEngine?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "TTS_ID_${System.currentTimeMillis()}")
    }

    // Admin Sponsored Reward Ad state
    var pendingAdAction by remember { mutableStateOf<(() -> Unit)?>(null) }
    var pendingAdTitle by remember { mutableStateOf("") }
    var pendingAdRewardDescription by remember { mutableStateOf("") }
    var showSponsoredAdDialog by remember { mutableStateOf(false) }

    // Automatically select first language (English) if not selected
    LaunchedEffect(languages) {
        if (selectedLanguage == null && languages.isNotEmpty()) {
            viewModel.selectLanguage(languages.first())
        }
    }

    BackHandler {
        if (showSponsoredAdDialog) {
            showSponsoredAdDialog = false
        } else if (showSpokenVoiceClubDialog) {
            showSpokenVoiceClubDialog = false
        } else if (showCertificateDialog) {
            showCertificateDialog = false
        } else if (showLeaderboardSheet) {
            showLeaderboardSheet = false
        } else if (showPracticeCampDialog) {
            showPracticeCampDialog = false
            viewModel.closeActiveSession()
        } else if (showExamDialog) {
            showExamDialog = false
            viewModel.closeActiveSession()
        } else {
            onBack()
        }
    }

    val currentLang = selectedLanguage ?: languages.firstOrNull()
    val userScoreInCurrentLang = leaderboard.find { it.userId == (currentUser?.id?.toString() ?: "user_me") }
    val currentPoints = userScoreInCurrentLang?.totalPoints ?: 0
    val userCertificate = allCertificates.find {
        it.userId == (currentUser?.id?.toString() ?: "user_me") && it.languageId == (currentLang?.id ?: 1)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${currentLang?.flagEmoji ?: "🌐"} ${currentLang?.name ?: "ভাষা শিক্ষা"}",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Live Spoken Audio Club
                    IconButton(
                        onClick = { showSpokenVoiceClubDialog = true }
                    ) {
                        Icon(imageVector = Icons.Default.Headphones, contentDescription = "Spoken Club", tint = TeaLeafGreen)
                    }

                    // Certificate Portal Button
                    FilledTonalButton(
                        onClick = { showCertificateDialog = true },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = if (userCertificate != null) Color(0xFF4CAF50).copy(alpha = 0.18f) else Color(0xFFFF9800).copy(alpha = 0.18f),
                            contentColor = if (userCertificate != null) Color(0xFF2E7D32) else Color(0xFFE65100)
                        ),
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = if (userCertificate != null) Icons.Default.WorkspacePremium else Icons.Default.CardMembership,
                            contentDescription = "Certificate",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = if (userCertificate != null) "সার্টিফিকেট ✅" else "সার্টিফিকেট 📜",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(3.dp))

                    // Leaderboard Button in Top Bar
                    FilledTonalButton(
                        onClick = { showLeaderboardSheet = true },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = TeaLeafGreen.copy(alpha = 0.15f),
                            contentColor = TeaLeafGreen
                        ),
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = "Leaderboard",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("র‍্যাংক", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f))
        ) {
            // 1. Horizontal Language Selector (10 Languages)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(0.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    Text(
                        text = "ভাষা কোর্স নির্বাচন করুন (১০টি ভাষা):",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)
                    )
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(languages) { lang ->
                            val isSelected = lang.id == currentLang?.id
                            Card(
                                modifier = Modifier
                                    .clickable { viewModel.selectLanguage(lang) }
                                    .testTag("lang_chip_${lang.code}"),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) TeaLeafGreen else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                ),
                                border = if (isSelected) null else BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = lang.flagEmoji, fontSize = 16.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = lang.name.split(" ").first(),
                                        fontSize = 13.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 2. Daily Learning Streak & Live Audio Club Banner
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1B4D3E))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🔥", fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "৩ দিনের দৈনিক স্ট্রিক (Streak)",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "আজ ৫ মিনিট শব্দ প্র্যাকটিস করে স্ট্রিক ধরে রাখুন",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 10.sp
                            )
                        }
                    }

                    Button(
                        onClick = { showSpokenVoiceClubDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                        shape = RoundedCornerShape(16.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Mic, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.White)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("স্পোকেন ক্লাব", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // 3. Language Progress, Certificate & Points Summary Banner
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${currentLang?.name ?: "English"} শেখার অগ্রগতি",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "মোট ২০০টি লেভেল • ১০০ প্র্যাকটিস শব্দ • ৫০ পরীক্ষার প্রশ্ন",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Total Points in this Language
                        Surface(
                            color = TeaLeafGreen.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Stars,
                                    contentDescription = null,
                                    tint = TeaLeafGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "$currentPoints পয়েন্ট",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TeaLeafGreen
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                    Spacer(modifier = Modifier.height(8.dp))

                    // Certificate Notice & Gateway
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showCertificateDialog = true },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Icon(
                                imageVector = Icons.Default.VerifiedUser,
                                contentDescription = null,
                                tint = if (userCertificate != null) Color(0xFF4CAF50) else Color(0xFFE65100),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = if (userCertificate != null) "ভেরিফায়েড স্কিল সার্টিফিকেট সক্রিয় ✅" else "অফিশিয়াল ভেরিফাইড সার্টিফিকেট সংগ্রহ করুন",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (userCertificate != null) Color(0xFF2E7D32) else Color(0xFFE65100)
                                )
                                Text(
                                    text = if (userCertificate != null) "আইডি: ${userCertificate.certificateId} • ডাউনলোড ও শেয়ার করুন" else "সিভি ও চাকরির জন্য ভেরিফাইড QR সার্টিফিকেট ফি: ৳১৫০",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // 4. Game-like Levels Map / List
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("levels_list"),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text(
                        text = "🏆 লেভেল পথচলা (Gamified Learning Map)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }

                items(levels) { level ->
                    LevelItemCard(
                        level = level,
                        onOpenPractice = {
                            viewModel.openPracticeCamp(level, currentUser)
                            showPracticeCampDialog = true
                        },
                        onOpenExam = {
                            viewModel.openExam(level, currentUser)
                            showExamDialog = true
                        }
                    )
                }
            }
        }
    }

    // Practice Camp Dialog (100 words in 5 milestones of 20 words = +1 point each)
    if (showPracticeCampDialog && activeLevel != null) {
        PracticeCampDialog(
            level = activeLevel!!,
            words = activePracticeWords,
            langCode = currentLang?.code ?: "en",
            onSpeak = { text, code -> speakWord(text, code) },
            onDismiss = {
                showPracticeCampDialog = false
                viewModel.closeActiveSession()
            },
            onEarnPointRequested = {
                // Trigger Admin Sponsored Reward Ad before point is awarded
                pendingAdTitle = "🎉 ২০টি শব্দ অনুশীলন সম্পন্ন!"
                pendingAdRewardDescription = "স্পন্সর এডটি সম্পন্ন করে +১ প্র্যাকটিস পয়েন্ট অর্জন করুন"
                pendingAdAction = {
                    viewModel.earnPracticePoint(activeLevel!!, currentUser)
                    Toast.makeText(context, "🎉 অভিনন্দন! +১ পয়েন্ট সফলভাবে যুক্ত হয়েছে!", Toast.LENGTH_SHORT).show()
                }
                showSponsoredAdDialog = true
            }
        )
    }

    // Exam Dialog (50 questions, unlocks next level on passing)
    if (showExamDialog && activeLevel != null) {
        ExamDialog(
            level = activeLevel!!,
            questions = activeExamWords,
            onDismiss = {
                showExamDialog = false
                viewModel.closeActiveSession()
            },
            onPassExamRequested = {
                // Trigger Admin Sponsored Reward Ad before exam pass & 5 bonus points are awarded
                pendingAdTitle = "🏆 লেভেল ${activeLevel!!.levelNumber} পরীক্ষা উত্তীর্ণ!"
                pendingAdRewardDescription = "স্পন্সর এডটি সম্পন্ন করে পরবর্তী লেভেল আনলক ও +৫ বোনাস পয়েন্ট গ্রহণ করুন"
                pendingAdAction = {
                    viewModel.completeAndPassExam(activeLevel!!, currentUser)
                    Toast.makeText(context, "🏆 চমৎকার! পরবর্তী লেভেল আনলক এবং +৫ বোনাস পয়েন্ট যোগ হয়েছে!", Toast.LENGTH_LONG).show()
                    showExamDialog = false
                }
                showSponsoredAdDialog = true
            }
        )
    }

    // Admin Sponsored Reward Ad Popup (Admin Controlled Non-Google Ad)
    if (showSponsoredAdDialog && pendingAdAction != null) {
        val selectedAd = activeAds.firstOrNull() ?: AdminSponsoredAd(
            id = 1,
            sponsorName = "British Council IELTS Prep",
            sponsorLogoEmoji = "🎓",
            title = "IELTS & স্পোকেন ইংলিশ মাস্টারক্লাস - ৫০% স্কলারশিপ!",
            description = "যুক্তরাজ্য ও কানাডা উচ্চশিক্ষায় সেরা প্রস্তুতি নিন। আজই বিশেষ অফার কোড সংগ্রহ করুন।",
            bannerImageUrl = "https://images.unsplash.com/photo-1523240795612-9a054b0db644?w=600",
            targetUrl = "https://nabachetna.com",
            ctaText = "অফারটি দেখুন ➔",
            countdownSeconds = 5
        )

        AdminSponsoredRewardAdDialog(
            ad = selectedAd,
            title = pendingAdTitle,
            rewardDescription = pendingAdRewardDescription,
            onClaimReward = {
                viewModel.recordAdImpression(selectedAd.id)
                showSponsoredAdDialog = false
                pendingAdAction?.invoke()
                pendingAdAction = null
            },
            onAdClick = {
                viewModel.recordAdClick(selectedAd.id)
                try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(selectedAd.targetUrl.ifBlank { "https://google.com" }))
                    context.startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(context, "অফার লিংক খোলা হচ্ছে...", Toast.LENGTH_SHORT).show()
                }
            },
            onDismiss = {
                showSponsoredAdDialog = false
                pendingAdAction = null
                Toast.makeText(context, "এডটি না দেখায় পয়েন্ট ক্রেডিট হয়নি", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // Official Verified Certificate Dialog & Fee Checkout
    if (showCertificateDialog && currentLang != null) {
        LanguageCertificateDialog(
            language = currentLang,
            currentPoints = currentPoints,
            highestLevel = levels.filter { it.isExamPassed }.maxOfOrNull { it.levelNumber } ?: 1,
            currentUser = currentUser,
            existingCertificate = userCertificate,
            onBuyCertificate = { method, txnId, fee ->
                viewModel.purchaseCertificate(
                    userId = currentUser?.id?.toString() ?: "user_me",
                    userName = currentUser?.name ?: "Learner Name",
                    userAvatarUrl = currentUser?.avatarUrl ?: "",
                    languageId = currentLang.id,
                    languageName = currentLang.name,
                    levelReached = levels.filter { it.isExamPassed }.maxOfOrNull { it.levelNumber } ?: 1,
                    totalPoints = currentPoints,
                    paymentMethod = method,
                    transactionId = txnId,
                    feeAmountBdt = fee,
                    onSuccess = {
                        Toast.makeText(context, "🎉 অভিনন্দন! আপনার ভেরিফায়েড সার্টিফিকেট ইস্যু হয়েছে!", Toast.LENGTH_LONG).show()
                    }
                )
            },
            onDismiss = { showCertificateDialog = false }
        )
    }

    // Spoken Audio Voice Club Modal Dialog
    if (showSpokenVoiceClubDialog && currentLang != null) {
        SpokenVoiceClubDialog(
            language = currentLang,
            currentUser = currentUser,
            onDismiss = { showSpokenVoiceClubDialog = false }
        )
    }

    // Leaderboard Screen overlay
    if (showLeaderboardSheet) {
        Dialog(
            onDismissRequest = { showLeaderboardSheet = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                LeaderboardScreen(
                    initialLanguageId = currentLang?.id ?: 1,
                    onBack = { showLeaderboardSheet = false }
                )
            }
        }
    }
}

@Composable
private fun LevelItemCard(
    level: Level,
    onOpenPractice: () -> Unit,
    onOpenExam: () -> Unit
) {
    val isLocked = level.isLocked
    val cardBg = if (isLocked) {
        MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
    } else {
        MaterialTheme.colorScheme.surface
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("level_card_${level.levelNumber}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isLocked) 0.dp else 2.dp),
        border = if (!isLocked) BorderStroke(1.dp, TeaLeafGreen.copy(alpha = 0.3f)) else null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(
                                if (isLocked) Color.Gray.copy(alpha = 0.2f)
                                else if (level.isExamPassed) Color(0xFFFFD700).copy(alpha = 0.2f)
                                else TeaLeafGreen.copy(alpha = 0.2f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isLocked) {
                            Icon(imageVector = Icons.Default.Lock, contentDescription = "Locked", tint = Color.Gray, modifier = Modifier.size(18.dp))
                        } else if (level.isExamPassed) {
                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = "Passed", tint = Color(0xFFE6A100), modifier = Modifier.size(20.dp))
                        } else {
                            Text(
                                text = "${level.levelNumber}",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TeaLeafGreen
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "লেভেল ${level.levelNumber}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isLocked) Color.Gray else MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (isLocked) "🔒 আগের লেভেলের পরীক্ষা পাস করে আনলক করুন"
                            else if (level.isExamPassed) "✅ পরীক্ষা সম্পন্ন • ৫/৫ পয়েন্ট"
                            else "⭐ প্র্যাকটিস পয়েন্ট: ${level.pointsEarned}/৫",
                            fontSize = 12.sp,
                            color = if (isLocked) Color.Gray else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (!isLocked && level.isExamPassed) {
                    Surface(
                        color = Color(0xFF4CAF50).copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "উত্তীর্ণ ✅",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            if (!isLocked) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Practice Camp Button
                    Button(
                        onClick = onOpenPractice,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = TeaLeafGreen),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.School, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("প্র্যাকটিস কেন্দ্র", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    // Exam Button
                    OutlinedButton(
                        onClick = onOpenExam,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(
                            1.dp,
                            if (level.isExamPassed) Color(0xFF4CAF50) else Color(0xFFE53935)
                        ),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = if (level.isExamPassed) Icons.Default.Check else Icons.Default.Quiz,
                            contentDescription = null,
                            tint = if (level.isExamPassed) Color(0xFF4CAF50) else Color(0xFFE53935),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (level.isExamPassed) "পুনরায় পরীক্ষা" else "পরীক্ষা দিন (৫০)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (level.isExamPassed) Color(0xFF4CAF50) else Color(0xFFE53935)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PracticeCampDialog(
    level: Level,
    words: List<Word>,
    langCode: String,
    onSpeak: (String, String) -> Unit,
    onDismiss: () -> Unit,
    onEarnPointRequested: () -> Unit
) {
    var currentIndex by remember { mutableIntStateOf(0) }
    var wordsPracticedCount by remember { mutableIntStateOf(0) }

    val totalWords = if (words.isNotEmpty()) words.size else 100
    val currentWord = if (words.isNotEmpty() && currentIndex < words.size) words[currentIndex] else null

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(vertical = 24.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "🏕️ প্র্যাকটিস কেন্দ্র (লেভেল ${level.levelNumber})",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TeaLeafGreen
                        )
                        Text(
                            text = "প্রতি ২০টি শব্দ শিখলে পাবেন +১ পয়েন্ট",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Progress Bar
                val progress = (currentIndex + 1).toFloat() / totalWords.toFloat()
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = TeaLeafGreen,
                    trackColor = TeaLeafGreen.copy(alpha = 0.2f)
                )

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "শব্দ: ${currentIndex + 1} / $totalWords",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Flashcard Box with Audio Speaker TTS
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = TeaLeafGreen.copy(alpha = 0.08f)
                    ),
                    border = BorderStroke(1.dp, TeaLeafGreen.copy(alpha = 0.3f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = currentWord?.word ?: "Word ${currentIndex + 1}",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            // TTS Speaker Audio Button
                            IconButton(
                                onClick = {
                                    val wordClean = currentWord?.word?.split(" ")?.firstOrNull() ?: "Hello"
                                    onSpeak(wordClean, langCode)
                                },
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(TeaLeafGreen.copy(alpha = 0.15f))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VolumeUp,
                                    contentDescription = "Pronounce",
                                    tint = TeaLeafGreen,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        if (!currentWord?.pronunciation.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "উচ্চারণ: [ ${currentWord?.pronunciation} ]",
                                fontSize = 13.sp,
                                color = TeaLeafGreen,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "অর্থ: ${currentWord?.translation ?: "অর্থ ${currentIndex + 1}"}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center
                        )

                        if (!currentWord?.exampleSentence.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "💡 উদাহরণ: ${currentWord?.exampleSentence}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            if (currentIndex > 0) currentIndex--
                        },
                        enabled = currentIndex > 0,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("আগের শব্দ")
                    }

                    Button(
                        onClick = {
                            wordsPracticedCount++
                            if (wordsPracticedCount % 20 == 0) {
                                onEarnPointRequested()
                            }
                            if (currentIndex < totalWords - 1) {
                                currentIndex++
                            } else {
                                onDismiss()
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = TeaLeafGreen),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(if (currentIndex < totalWords - 1) "পরবর্তী শব্দ ➔" else "সম্পন্ন করুন ✅")
                    }
                }
            }
        }
    }
}

@Composable
private fun ExamDialog(
    level: Level,
    questions: List<Word>,
    onDismiss: () -> Unit,
    onPassExamRequested: () -> Unit
) {
    var currentQIndex by remember { mutableIntStateOf(0) }
    var correctAnswersCount by remember { mutableIntStateOf(0) }
    var selectedOption by remember { mutableStateOf<String?>(null) }
    var isExamFinished by remember { mutableStateOf(false) }

    val totalQuestions = if (questions.isNotEmpty()) minOf(50, questions.size) else 50
    val currentQuestion = if (questions.isNotEmpty() && currentQIndex < questions.size) questions[currentQIndex] else null

    // Generate 4 multiple choice options
    val options = remember(currentQIndex, currentQuestion) {
        val correct = currentQuestion?.translation ?: "সঠিক উত্তর"
        listOf(
            correct,
            "বিকল্প অর্থ ক",
            "বিকল্প অর্থ খ",
            "বিকল্প অর্থ গ"
        ).shuffled()
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(vertical = 24.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (!isExamFinished) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "📝 পরীক্ষা (লেভেল ${level.levelNumber})",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFD32F2F)
                        )
                        IconButton(onClick = onDismiss) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    val progress = (currentQIndex + 1).toFloat() / totalQuestions.toFloat()
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = Color(0xFFD32F2F),
                        trackColor = Color(0xFFD32F2F).copy(alpha = 0.2f)
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "প্রশ্ন: ${currentQIndex + 1} / $totalQuestions",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Question Box
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "সঠিক অর্থ কোনটি?",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = currentQuestion?.word ?: "Question ${currentQIndex + 1}",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Options
                    options.forEach { opt ->
                        val isSelected = selectedOption == opt
                        OutlinedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable { selectedOption = opt },
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(
                                1.5.dp,
                                if (isSelected) TeaLeafGreen else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                            ),
                            colors = CardDefaults.outlinedCardColors(
                                containerColor = if (isSelected) TeaLeafGreen.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { selectedOption = opt },
                                    colors = RadioButtonDefaults.colors(selectedColor = TeaLeafGreen)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = opt,
                                    fontSize = 14.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (selectedOption == (currentQuestion?.translation ?: "সঠিক উত্তর")) {
                                correctAnswersCount++
                            }
                            selectedOption = null
                            if (currentQIndex < totalQuestions - 1) {
                                currentQIndex++
                            } else {
                                isExamFinished = true
                            }
                        },
                        enabled = selectedOption != null,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = TeaLeafGreen),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(if (currentQIndex < totalQuestions - 1) "পরবর্তী প্রশ্ন ➔" else "ফলাফল দেখুন 🎯")
                    }
                } else {
                    // Result View
                    val passed = correctAnswersCount >= (totalQuestions * 0.5) // 50%+ passing mark
                    Icon(
                        imageVector = if (passed) Icons.Default.EmojiEvents else Icons.Default.Cancel,
                        contentDescription = null,
                        tint = if (passed) Color(0xFFFFD700) else Color(0xFFD32F2F),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (passed) "🎉 অভিনন্দন! আপনি উত্তীর্ণ হয়েছেন!" else "⚠️ আবার চেষ্টা করুন!",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "আপনার স্কোর: $correctAnswersCount / $totalQuestions",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TeaLeafGreen
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (passed) "পরবর্তী লেভেলের লক খুলতে এবং +৫ বোনাস পয়েন্ট গ্রহণ করতে স্পন্সর রিওয়ার্ড সম্পন্ন করুন।"
                        else "লেভেল আনলক করতে ন্যূনতম ৫০% প্রশ্নের সঠিক উত্তর দিতে হবে।",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = {
                            if (passed) {
                                onPassExamRequested()
                            } else {
                                onDismiss()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = TeaLeafGreen),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(if (passed) "পয়েন্ট সংগ্রহ ও পরবর্তী লেভেলে যান ➔" else "বন্ধ করুন")
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// LIVE SPOKEN AUDIO VOICE CLUB DIALOG
// -------------------------------------------------------------
@Composable
fun SpokenVoiceClubDialog(
    language: Language,
    currentUser: User?,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var isMuted by remember { mutableStateOf(false) }
    var isHandRaised by remember { mutableStateOf(false) }

    val activeParticipants = remember {
        listOf(
            Triple(currentUser?.name ?: "You (Host)", "🎙️ Speaking", true),
            Triple("Tanvir Ahmed", "🎧 Listening", false),
            Triple("Ayesha Rahman", "✋ Raised Hand", false),
            Triple("Fahim Chowdhury", "🎧 Listening", false),
            Triple("Nabila Hasan", "🎧 Listening", false)
        )
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .padding(vertical = 20.dp),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0E2218)),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🎙️", fontSize = 22.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "${language.name} স্পোকেন ক্লাব",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "লাইভ অডিও প্র্যাকটিস রুম • ৫ জন অনলাইন",
                                color = Color(0xFFA5D6A7),
                                fontSize = 11.sp
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Topic Card
                Surface(
                    color = Color(0xFF1B4D3E),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "💡 আজকের আলোচনার বিষয় (Topic):",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFD54F)
                        )
                        Text(
                            text = "\"How do you spend your free time and weekends?\" (আপনার অবসর সময় কীভাবে কাটান?)",
                            fontSize = 12.sp,
                            color = Color.White,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Participants Grid
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    activeParticipants.forEach { (name, status, isHost) ->
                        Surface(
                            color = Color(0xFF17382B),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(if (isHost) TeaLeafGreen else Color.Gray.copy(alpha = 0.3f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(name.take(1), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(name, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                }

                                Text(status, color = if (isHost) Color(0xFF81C784) else Color.LightGray, fontSize = 11.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Audio Controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            isMuted = !isMuted
                            Toast.makeText(context, if (isMuted) "মাইক্রোফোন মিউট করা হয়েছে" else "মাইক্রোফোন চালু", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isMuted) Color(0xFFD32F2F) else Color(0xFF2E7D32)
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(imageVector = if (isMuted) Icons.Default.MicOff else Icons.Default.Mic, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (isMuted) "আনমিউট" else "মিউট")
                    }

                    OutlinedButton(
                        onClick = {
                            isHandRaised = !isHandRaised
                            Toast.makeText(context, if (isHandRaised) "হাত তুলেছেন ✋ স্পিকারের অনুমতির অপেক্ষা করুন" else "হাত নামানো হয়েছে", Toast.LENGTH_SHORT).show()
                        },
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, if (isHandRaised) Color(0xFFFFD54F) else Color.White.copy(alpha = 0.6f)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(if (isHandRaised) "✋ হাত তোলা আছে" else "✋ হাত তুলুন", color = if (isHandRaised) Color(0xFFFFD54F) else Color.White)
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// ADMIN-CONTROLLED REWARD SPONSORED AD (NON-GOOGLE AD)
// -------------------------------------------------------------
@Composable
fun AdminSponsoredRewardAdDialog(
    ad: AdminSponsoredAd,
    title: String,
    rewardDescription: String,
    onClaimReward: () -> Unit,
    onAdClick: () -> Unit,
    onDismiss: () -> Unit
) {
    var timeLeft by remember { mutableIntStateOf(ad.countdownSeconds.coerceAtLeast(4)) }
    val isCompleted by remember { derivedStateOf { timeLeft <= 0 } }

    LaunchedEffect(Unit) {
        while (timeLeft > 0) {
            delay(1000L)
            timeLeft--
        }
    }

    Dialog(
        onDismissRequest = {
            if (isCompleted) onDismiss()
        },
        properties = DialogProperties(dismissOnBackPress = isCompleted, dismissOnClickOutside = false, usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.93f)
                .padding(vertical = 20.dp),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Ad Badge Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Surface(
                        color = TeaLeafGreen.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("📢", fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "নবচেতনা স্পন্সরড রিওয়ার্ড এড",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TeaLeafGreen
                            )
                        }
                    }

                    if (isCompleted) {
                        IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                        }
                    } else {
                        Surface(
                            color = Color.Black.copy(alpha = 0.7f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "রিওয়ার্ড: ${timeLeft}s",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Milestone Notice
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = rewardDescription,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Sponsor Banner Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onAdClick() },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
                ) {
                    Column {
                        AsyncImage(
                            model = ad.bannerImageUrl.ifBlank { "https://images.unsplash.com/photo-1523240795612-9a054b0db644?w=600" },
                            contentDescription = ad.title,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp)
                                .clip(RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp)),
                            contentScale = ContentScale.Crop
                        )

                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(ad.sponsorLogoEmoji, fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = ad.sponsorName,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = ad.title,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (ad.description.isNotBlank()) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = ad.description,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 2
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Direct Sponsor Link Button
                OutlinedButton(
                    onClick = onAdClick,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, TeaLeafGreen)
                ) {
                    Icon(imageVector = Icons.Default.Launch, contentDescription = null, tint = TeaLeafGreen, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = ad.ctaText.ifBlank { "স্পন্সর অফার দেখুন ➔" }, color = TeaLeafGreen, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Reward Claim Button (Activated when timer hits 0)
                Button(
                    onClick = onClaimReward,
                    enabled = isCompleted,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TeaLeafGreen,
                        disabledContainerColor = Color.Gray.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    if (isCompleted) {
                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "রিওয়ার্ড পয়েন্ট সংগ্রহ করুন ✅", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    } else {
                        Icon(imageVector = Icons.Default.Timer, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "অপেক্ষা করুন (${timeLeft}s)", fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// OFFICIAL VERIFIED DIGITAL CERTIFICATE & PAYMENT DIALOG
// -------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageCertificateDialog(
    language: Language,
    currentPoints: Int,
    highestLevel: Int,
    currentUser: User?,
    existingCertificate: LanguageCertificate?,
    onBuyCertificate: (paymentMethod: String, txnId: String, feeAmount: Double) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var selectedMethod by remember { mutableStateOf("bKash") }
    var txnIdInput by remember { mutableStateOf("") }
    val certificateFee = 150.0

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.WorkspacePremium, contentDescription = null, tint = Color(0xFFFF9800), modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("অফিশিয়াল ভেরিফাইড সার্টিফিকেট", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        }
                        IconButton(onClick = onDismiss) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                        }
                    }
                }

                // Official Certificate Graphical Preview Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFBF9F1)),
                        border = BorderStroke(2.dp, Color(0xFFC5A059))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // National seal header
                            Text(
                                text = "গণপ্রজাতন্ত্রী বাংলাদেশ সরকার অনুমোদিত",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1B4D3E),
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "NABACHETNA LANGUAGE PROFICIENCY BOARD",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF0F281E),
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Divider(color = Color(0xFFC5A059), thickness = 1.dp, modifier = Modifier.width(180.dp))
                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "CERTIFICATE OF ACHIEVEMENT",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF8C6B1B),
                                fontFamily = FontFamily.Serif
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "এই মর্মে প্রত্যয়ন করা যাচ্ছে যে,",
                                fontSize = 11.sp,
                                color = Color.DarkGray
                            )
                            Text(
                                text = currentUser?.name ?: "শিক্ষার্থীর নাম",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF1B4D3E)
                            )
                            Text(
                                text = "সফলভাবে ${language.name} ভাষা প্রশিক্ষণ কোর্সে অংশগ্রহণ করে লেভেল $highestLevel ও মোট $currentPoints পয়েন্ট অর্জন করেছেন।",
                                fontSize = 11.sp,
                                color = Color.DarkGray,
                                textAlign = TextAlign.Center,
                                lineHeight = 16.sp
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Bottom Certificate Details
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "সার্টিফিকেট আইডি:",
                                        fontSize = 9.sp,
                                        color = Color.Gray
                                    )
                                    Text(
                                        text = existingCertificate?.certificateId ?: "NC-${language.name.take(2).uppercase()}-PENDING",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1B4D3E)
                                    )
                                }

                                // Verified Gold Stamp Badge
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(CircleShape)
                                        .background(
                                            Brush.linearGradient(
                                                listOf(Color(0xFFFFD54F), Color(0xFFFF9800), Color(0xFFFFA000))
                                            )
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(imageVector = Icons.Default.Verified, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                        Text("SEAL", fontSize = 8.sp, fontWeight = FontWeight.Black, color = Color.White)
                                    }
                                }
                            }
                        }
                    }
                }

                // If Certificate is already issued/verified
                if (existingCertificate != null) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF4CAF50).copy(alpha = 0.1f)),
                            border = BorderStroke(1.dp, Color(0xFF4CAF50))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2E7D32))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "সার্টিফিকেট ভেরিফায়েড ও লাইভ রয়েছে!",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF2E7D32)
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "ইস্যু তারিখ: ${existingCertificate.issueDate} • পেমেন্ট মাধ্যম: ${existingCertificate.paymentMethod} (TrxID: ${existingCertificate.transactionId})",
                                    fontSize = 11.sp,
                                    color = Color.DarkGray
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(
                                        onClick = {
                                            clipboardManager.setText(AnnotatedString("https://nabachetna.com/verify-certificate/${existingCertificate.certificateId}"))
                                            Toast.makeText(context, "সার্টিফিকেট ভেরিফিকেশন লিংক কপি হয়েছে!", Toast.LENGTH_SHORT).show()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("লিংক শেয়ার", fontSize = 12.sp)
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            Toast.makeText(context, "সার্টিফিকেট PDF ডাউনলোড প্রস্তুত করা হচ্ছে...", Toast.LENGTH_SHORT).show()
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(imageVector = Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("PDF ডাউনলোড", fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // Payment Gateway Checkout for Certificate Issuance Fee
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "সার্টিফিকেট ভেরিফিকেশন ফি:",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "৳ ${certificateFee.toInt()} BDT",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Black,
                                        color = TeaLeafGreen
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Text("পেমেন্ট মেথড নির্বাচন করুন:", fontSize = 11.sp, color = Color.Gray)
                                Spacer(modifier = Modifier.height(4.dp))

                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    listOf("bKash", "Nagad", "Rocket", "Wallet").forEach { method ->
                                        val isSel = selectedMethod == method
                                        FilterChip(
                                            selected = isSel,
                                            onClick = { selectedMethod = method },
                                            label = { Text(method, fontSize = 11.sp, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal) },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = TeaLeafGreen,
                                                selectedLabelColor = Color.White
                                            )
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Surface(
                                    color = MaterialTheme.colorScheme.surface,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text(
                                            text = "$selectedMethod অফিশিয়াল নম্বর: 01881052993",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = "টাকা পাঠিয়ে প্রাপ্ত Transaction ID (TrxID) নিচে লিখে সাবমিট করুন।",
                                            fontSize = 10.sp,
                                            color = Color.Gray
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                OutlinedTextField(
                                    value = txnIdInput,
                                    onValueChange = { txnIdInput = it },
                                    label = { Text("Transaction ID (TrxID)", fontSize = 11.sp) },
                                    placeholder = { Text("যেমন: 9J8K2LA190", fontSize = 11.sp) },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = TeaLeafGreen
                                    )
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Button(
                                    onClick = {
                                        val finalTxn = txnIdInput.trim().ifBlank { "TRX${System.currentTimeMillis().toString().takeLast(6)}" }
                                        onBuyCertificate(selectedMethod, finalTxn, certificateFee)
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = TeaLeafGreen),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("ফি পেমেন্ট ও সার্টিফিকেট সংগ্রহ করুন (৳${certificateFee.toInt()})", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
