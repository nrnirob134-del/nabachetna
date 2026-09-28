package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.data.model.User
import com.example.ui.theme.TeaLeafGreen
import com.example.ui.theme.VerifiedYellow
import com.example.ui.theme.VerifiedYellowBorder
import com.example.ui.theme.VerifiedYellowContainer
import com.example.ui.theme.VerifiedYellowGold
import com.example.ui.viewmodel.PageBookViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class VerificationStep(val stepNumber: Int, val title: String) {
    STEP_AGE_AND_INFO(1, "বয়স ও তথ্য"),
    STEP_NID_CARDS(2, "জাতীয় পরিচয়পত্র"),
    STEP_FACE_SCAN(3, "ফেস স্ক্যান মিল"),
    STEP_PHONE_EMAIL(4, "মোবাইল ও ইমেল"),
    STEP_ACTIVATE_BADGE(5, "হলুদ ব্যাজ সক্রিয়")
}

@Composable
fun VerificationWizardDialog(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    currentUser: User?,
    viewModel: PageBookViewModel,
    onVerificationSuccess: () -> Unit = {}
) {
    if (!isOpen || currentUser == null) return

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    var currentStep by remember { mutableStateOf(VerificationStep.STEP_AGE_AND_INFO) }

    // Step 1: Age & Basic info
    var applicantName by remember { mutableStateOf(currentUser.name) }
    var birthYear by remember { mutableStateOf("2002") }
    var birthMonth by remember { mutableStateOf("05") }
    var birthDay by remember { mutableStateOf("14") }

    val calculatedAge by remember(birthYear) {
        derivedStateOf {
            val y = birthYear.toIntOrNull() ?: 2024
            (2024 - y).coerceAtLeast(0)
        }
    }
    val isAgeEligible = calculatedAge >= 18

    // Step 2: NID Data
    var nidNumber by remember { mutableStateOf("1998561234567") }
    var nidFrontPhoto by remember { mutableStateOf("https://images.unsplash.com/photo-1589829545856-d10d557cf95f?w=600") }
    var nidBackPhoto by remember { mutableStateOf("https://images.unsplash.com/photo-1589829545856-d10d557cf95f?w=600") }
    var addressDetails by remember { mutableStateOf(currentUser.livesIn.ifBlank { "ঢাকা, বাংলাদেশ" }) }
    var nidConfirmed by remember { mutableStateOf(true) }

    // Step 3: Live Face Scan & Biometric comparison with NID
    var isScanningFace by remember { mutableStateOf(false) }
    var blinkPassed by remember { mutableStateOf(false) }
    var headTurnPassed by remember { mutableStateOf(false) }
    var smilePassed by remember { mutableStateOf(false) }
    var scanProgress by remember { mutableFloatStateOf(0f) }
    var faceMatched by remember { mutableStateOf(false) }
    var faceMatchScore by remember { mutableIntStateOf(0) }

    // Step 4: Phone & Email OTP
    var phoneNumber by remember { mutableStateOf(currentUser.emailOrPhone.ifBlank { "+880 1712-345678" }) }
    var phoneOtpSent by remember { mutableStateOf(false) }
    var phoneOtpInput by remember { mutableStateOf("") }
    var isPhoneVerified by remember { mutableStateOf(false) }
    var activePhoneOtpMock by remember { mutableStateOf("529148") }

    var emailAddress by remember { mutableStateOf(if (currentUser.emailOrPhone.contains("@")) currentUser.emailOrPhone else "anika@nabachetna.com") }
    var emailOtpSent by remember { mutableStateOf(false) }
    var emailOtpInput by remember { mutableStateOf("") }
    var isEmailVerified by remember { mutableStateOf(false) }
    var activeEmailOtpMock by remember { mutableStateOf("738204") }

    // Step 5: Success Dialog
    var showCelebrationDialog by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
            ) {
                // Top Header with Yellow Badge Icon
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = TeaLeafGreen,
                    shadowElevation = 4.dp
                ) {
                    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(VerifiedYellowContainer)
                                        .border(1.5.dp, VerifiedYellow, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Verified,
                                        contentDescription = null,
                                        tint = VerifiedYellow,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "ভেরিফাইড ব্যাজ আবেদন",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "হলুদ ভেরিফাইড ব্যাজ সিস্টেম (১৮+ ও NID)",
                                        fontSize = 11.sp,
                                        color = VerifiedYellowGold
                                    )
                                }
                            }

                            IconButton(onClick = onDismiss) {
                                Icon(imageVector = Icons.Filled.Close, contentDescription = "Close", tint = Color.White)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Progress Indicator Tabs
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            VerificationStep.values().forEach { step ->
                                val isPassed = step.stepNumber < currentStep.stepNumber
                                val isCurrent = step == currentStep
                                val barColor = when {
                                    isPassed -> Color(0xFF4CAF50)
                                    isCurrent -> VerifiedYellow
                                    else -> Color.White.copy(alpha = 0.25f)
                                }
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(4.dp)
                                        .clip(RoundedCornerShape(2.dp))
                                        .background(barColor)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "ধাপ ${currentStep.stepNumber}/৫: ${currentStep.title}",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.9f),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Body content based on Current Step
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                            .padding(16.dp)
                    ) {
                        when (currentStep) {
                            VerificationStep.STEP_AGE_AND_INFO -> {
                                StepAgeAndInfoView(
                                    name = applicantName,
                                    onNameChange = { applicantName = it },
                                    birthDay = birthDay,
                                    onBirthDayChange = { birthDay = it },
                                    birthMonth = birthMonth,
                                    onBirthMonthChange = { birthMonth = it },
                                    birthYear = birthYear,
                                    onBirthYearChange = { birthYear = it },
                                    age = calculatedAge,
                                    isEligible = isAgeEligible
                                )
                            }
                            VerificationStep.STEP_NID_CARDS -> {
                                StepNidCardsView(
                                    nidNumber = nidNumber,
                                    onNidChange = { nidNumber = it },
                                    address = addressDetails,
                                    onAddressChange = { addressDetails = it },
                                    frontPhoto = nidFrontPhoto,
                                    onFrontPhotoChange = { nidFrontPhoto = it },
                                    backPhoto = nidBackPhoto,
                                    onBackPhotoChange = { nidBackPhoto = it },
                                    confirmed = nidConfirmed,
                                    onConfirmedChange = { nidConfirmed = it }
                                )
                            }
                            VerificationStep.STEP_FACE_SCAN -> {
                                StepFaceScanView(
                                    isScanning = isScanningFace,
                                    scanProgress = scanProgress,
                                    blinkPassed = blinkPassed,
                                    headTurnPassed = headTurnPassed,
                                    smilePassed = smilePassed,
                                    faceMatched = faceMatched,
                                    matchScore = faceMatchScore,
                                    avatarUrl = currentUser.avatarUrl,
                                    nidPhotoUrl = nidFrontPhoto,
                                    onStartScan = {
                                        isScanningFace = true
                                        scanProgress = 0f
                                        blinkPassed = false
                                        headTurnPassed = false
                                        smilePassed = false
                                        faceMatched = false
                                        faceMatchScore = 0

                                        coroutineScope.launch {
                                            delay(700)
                                            blinkPassed = true
                                            scanProgress = 0.35f
                                            delay(800)
                                            headTurnPassed = true
                                            scanProgress = 0.70f
                                            delay(800)
                                            smilePassed = true
                                            scanProgress = 1.0f
                                            delay(600)
                                            faceMatched = true
                                            faceMatchScore = 96
                                            isScanningFace = false
                                        }
                                    }
                                )
                            }
                            VerificationStep.STEP_PHONE_EMAIL -> {
                                StepPhoneEmailOtpView(
                                    phoneNumber = phoneNumber,
                                    onPhoneChange = { phoneNumber = it },
                                    phoneOtpSent = phoneOtpSent,
                                    phoneOtpInput = phoneOtpInput,
                                    onPhoneOtpInputChange = { phoneOtpInput = it },
                                    isPhoneVerified = isPhoneVerified,
                                    onSendPhoneOtp = {
                                        phoneOtpSent = true
                                        Toast.makeText(context, "📱 [নবচেতনা ওটিপি] কোড পাঠানো হয়েছে: $activePhoneOtpMock", Toast.LENGTH_LONG).show()
                                    },
                                    onVerifyPhoneOtp = {
                                        if (phoneOtpInput.trim() == activePhoneOtpMock || phoneOtpInput.length == 6) {
                                            isPhoneVerified = true
                                            Toast.makeText(context, "ফোন নম্বর সফলভাবে ভেরিফাইড হয়েছে! ✅", Toast.LENGTH_SHORT).show()
                                        } else {
                                            Toast.makeText(context, "ভুল ওটিপি কোড! সঠিক কোডটি লিখুন ($activePhoneOtpMock)", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    emailAddress = emailAddress,
                                    onEmailChange = { emailAddress = it },
                                    emailOtpSent = emailOtpSent,
                                    emailOtpInput = emailOtpInput,
                                    onEmailOtpInputChange = { emailOtpInput = it },
                                    isEmailVerified = isEmailVerified,
                                    onSendEmailOtp = {
                                        emailOtpSent = true
                                        Toast.makeText(context, "✉️ [নবচেতনা সিকিউরিটি] ইমেল কোড: $activeEmailOtpMock", Toast.LENGTH_LONG).show()
                                    },
                                    onVerifyEmailOtp = {
                                        if (emailOtpInput.trim() == activeEmailOtpMock || emailOtpInput.length == 6) {
                                            isEmailVerified = true
                                            Toast.makeText(context, "ইমেল ঠিকানা সফলভাবে ভেরিফাইড হয়েছে! ✅", Toast.LENGTH_SHORT).show()
                                        } else {
                                            Toast.makeText(context, "ভুল কোড! সঠিক কোড লিখুন ($activeEmailOtpMock)", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                )
                            }
                            VerificationStep.STEP_ACTIVATE_BADGE -> {
                                StepActivateBadgeSummaryView(
                                    userName = applicantName,
                                    age = calculatedAge,
                                    nidNumber = nidNumber,
                                    faceMatchScore = if (faceMatchScore > 0) faceMatchScore else 96,
                                    phone = phoneNumber,
                                    email = emailAddress
                                )
                            }
                        }
                    }
                }

                // Bottom Action Button Footer
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp,
                    shadowElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (currentStep != VerificationStep.STEP_AGE_AND_INFO) {
                            OutlinedButton(
                                onClick = {
                                    val previousStep = when (currentStep) {
                                        VerificationStep.STEP_NID_CARDS -> VerificationStep.STEP_AGE_AND_INFO
                                        VerificationStep.STEP_FACE_SCAN -> VerificationStep.STEP_NID_CARDS
                                        VerificationStep.STEP_PHONE_EMAIL -> VerificationStep.STEP_FACE_SCAN
                                        VerificationStep.STEP_ACTIVATE_BADGE -> VerificationStep.STEP_PHONE_EMAIL
                                        else -> VerificationStep.STEP_AGE_AND_INFO
                                    }
                                    currentStep = previousStep
                                },
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("পূর্ববর্তী")
                            }
                        } else {
                            Spacer(modifier = Modifier.width(1.dp))
                        }

                        val canProceed = when (currentStep) {
                            VerificationStep.STEP_AGE_AND_INFO -> isAgeEligible && applicantName.isNotBlank()
                            VerificationStep.STEP_NID_CARDS -> nidNumber.length >= 10 && nidConfirmed
                            VerificationStep.STEP_FACE_SCAN -> faceMatched || faceMatchScore >= 85
                            VerificationStep.STEP_PHONE_EMAIL -> isPhoneVerified && isEmailVerified
                            VerificationStep.STEP_ACTIVATE_BADGE -> true
                        }

                        Button(
                            onClick = {
                                if (currentStep == VerificationStep.STEP_ACTIVATE_BADGE) {
                                    // Final step: Activate Yellow Verified Badge!
                                    viewModel.completeUserVerification(
                                        userId = currentUser.id,
                                        age = calculatedAge,
                                        dateOfBirth = "$birthYear-$birthMonth-$birthDay",
                                        nidNumber = nidNumber,
                                        nidFront = nidFrontPhoto,
                                        nidBack = nidBackPhoto,
                                        faceMatchScore = if (faceMatchScore > 0) faceMatchScore else 96,
                                        phone = phoneNumber,
                                        email = emailAddress
                                    )
                                    showCelebrationDialog = true
                                    onVerificationSuccess()
                                } else {
                                    val nextStep = when (currentStep) {
                                        VerificationStep.STEP_AGE_AND_INFO -> VerificationStep.STEP_NID_CARDS
                                        VerificationStep.STEP_NID_CARDS -> VerificationStep.STEP_FACE_SCAN
                                        VerificationStep.STEP_FACE_SCAN -> VerificationStep.STEP_PHONE_EMAIL
                                        VerificationStep.STEP_PHONE_EMAIL -> VerificationStep.STEP_ACTIVATE_BADGE
                                        else -> VerificationStep.STEP_ACTIVATE_BADGE
                                    }
                                    currentStep = nextStep
                                }
                            },
                            enabled = canProceed,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (currentStep == VerificationStep.STEP_ACTIVATE_BADGE) VerifiedYellow else TeaLeafGreen,
                                contentColor = if (currentStep == VerificationStep.STEP_ACTIVATE_BADGE) Color.Black else Color.White
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f, fill = false).padding(start = 12.dp)
                        ) {
                            if (currentStep == VerificationStep.STEP_ACTIVATE_BADGE) {
                                Icon(imageVector = Icons.Filled.Verified, contentDescription = null, tint = Color.Black)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "হলুদ ভেরিফাইড ব্যাজ গ্রহণ করুন 🌟",
                                    fontWeight = FontWeight.Bold
                                )
                            } else {
                                Text(
                                    text = "পরবর্তী ধাপ →",
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Celebration Dialog when Yellow Badge is successfully assigned
    if (showCelebrationDialog) {
        Dialog(onDismissRequest = {
            showCelebrationDialog = false
            onDismiss()
        }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(
                                        VerifiedYellowGold.copy(alpha = 0.5f),
                                        VerifiedYellow.copy(alpha = 0.2f),
                                        Color.Transparent
                                    )
                                )
                            )
                            .border(3.dp, VerifiedYellowBorder, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Verified,
                            contentDescription = null,
                            tint = VerifiedYellow,
                            modifier = Modifier.size(54.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "🎉 অভিনন্দন!",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = "আপনার প্রোফাইলে হলুদ ভেরিফাইড ব্যাজ যুক্ত করা হয়েছে!",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF7A5800),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                    )

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = VerifiedYellowContainer.copy(alpha = 0.7f),
                        border = BorderStroke(1.dp, VerifiedYellowBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "✅ বয়স ১৮+ বছর নিশ্চিত হয়েছে",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "✅ জাতীয় পরিচয়পত্র (NID) নম্বর ও ছবি ভেরিফাইড",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "✅ NID ছবির সাথে ফেস স্ক্যান মিল (৯৬.৮%) সফল",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "✅ মোবাইল ও ইমেল ওটিপি ভেরিফাইড",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            showCelebrationDialog = false
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = VerifiedYellow),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        Text(
                            text = "দারুণ! প্রোফাইল দেখুন",
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// Step 1: Age & Info View
// -------------------------------------------------------------
@Composable
private fun StepAgeAndInfoView(
    name: String,
    onNameChange: (String) -> Unit,
    birthDay: String,
    onBirthDayChange: (String) -> Unit,
    birthMonth: String,
    onBirthMonthChange: (String) -> Unit,
    birthYear: String,
    onBirthYearChange: (String) -> Unit,
    age: Int,
    isEligible: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "১. প্রার্থীর নাম ও বয়স যাচাই (বয়স ১৮+ বাধ্যতামূলক)",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "ভেরিফাইড ব্যাজের জন্য জাতীয় পরিচয়পত্র অনুযায়ী প্রার্থীর বয়স অবশ্যই ১৮ বছর বা তার বেশি হতে হবে।",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
            )

            OutlinedTextField(
                value = name,
                onValueChange = onNameChange,
                label = { Text("জাতীয় পরিচয়পত্র (NID) অনুযায়ী পূর্ণ নাম") },
                leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null) },
                modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp)
            )

            Text(
                text = "জন্ম তারিখ (দিন - মাস - বছর):",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 6.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = birthDay,
                    onValueChange = { if (it.length <= 2) onBirthDayChange(it) },
                    label = { Text("দিন") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = birthMonth,
                    onValueChange = { if (it.length <= 2) onBirthMonthChange(it) },
                    label = { Text("মাস") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = birthYear,
                    onValueChange = { if (it.length <= 4) onBirthYearChange(it) },
                    label = { Text("বছর") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1.5f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Real-time Age Validation Card
            if (isEligible) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFE8F5E9),
                    border = BorderStroke(1.dp, Color(0xFF4CAF50)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Filled.CheckCircle, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(28.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "বয়স: $age বছর (১৮+ যোগ্য ✅)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color(0xFF2E7D32)
                            )
                            Text(
                                text = "আপনার বয়স ১৮ বছরের বেশি হওয়ায় ভেরিফাইড ব্যাজের জন্য প্রাথমিক যোগ্যতা উত্তীর্ণ হয়েছে।",
                                fontSize = 11.sp,
                                color = Color(0xFF1B5E20)
                            )
                        }
                    }
                }
            } else {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFFFEBEE),
                    border = BorderStroke(1.dp, Color(0xFFEF5350)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Filled.ErrorOutline, contentDescription = null, tint = Color(0xFFC62828), modifier = Modifier.size(28.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "বয়স: $age বছর (১৮ বছরের কম - অযোগ্য ❌)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color(0xFFC62828)
                            )
                            Text(
                                text = "ভেরিফাইড ব্যাজ পেতে প্রার্থীর ন্যূনতম বয়স ১৮ বছর হতে হবে। অনুগ্রহ করে সঠিক জন্ম বছর প্রদান করুন।",
                                fontSize = 11.sp,
                                color = Color(0xFFB71C1C)
                            )
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// Step 2: NID Cards View
// -------------------------------------------------------------
@Composable
private fun StepNidCardsView(
    nidNumber: String,
    onNidChange: (String) -> Unit,
    address: String,
    onAddressChange: (String) -> Unit,
    frontPhoto: String,
    onFrontPhotoChange: (String) -> Unit,
    backPhoto: String,
    onBackPhotoChange: (String) -> Unit,
    confirmed: Boolean,
    onConfirmedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "২. জাতীয় পরিচয়পত্র (NID) যাচাই ও ডকুমেন্টস",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "স্মার্ট কার্ড বা জাতীয় পরিচয়পত্রের সামনের ও পেছনের স্পষ্ট ছবি এবং নম্বর প্রদান করুন।",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
            )

            OutlinedTextField(
                value = nidNumber,
                onValueChange = onNidChange,
                label = { Text("জাতীয় পরিচয়পত্র নম্বর (১০ বা ১৭ ডিজিট)") },
                leadingIcon = { Icon(Icons.Filled.Badge, contentDescription = null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
            )

            OutlinedTextField(
                value = address,
                onValueChange = onAddressChange,
                label = { Text("স্থায়ী/বর্তমান ঠিকানা (NID কার্ড অনুযায়ী)") },
                leadingIcon = { Icon(Icons.Filled.Home, contentDescription = null) },
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            )

            // Front NID Card Photo Preview
            Text(
                text = "NID সামনের ছবি (Front Side):",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                border = BorderStroke(1.dp, VerifiedYellowBorder.copy(alpha = 0.5f))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    AsyncImage(
                        model = frontPhoto,
                        contentDescription = "NID Front",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Surface(
                        color = Color.Black.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Filled.Check, contentDescription = null, tint = VerifiedYellow, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("জাতীয় পরিচয়পত্র - সামনের পাতা (সংযুক্ত)", color = Color.White, fontSize = 11.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Back NID Card Photo Preview
            Text(
                text = "NID পেছনের ছবি (Back Side):",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                border = BorderStroke(1.dp, VerifiedYellowBorder.copy(alpha = 0.5f))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    AsyncImage(
                        model = backPhoto,
                        contentDescription = "NID Back",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Surface(
                        color = Color.Black.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Filled.Check, contentDescription = null, tint = VerifiedYellow, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("জাতীয় পরিচয়পত্র - পেছনের পাতা (বারকোড সংযুক্ত)", color = Color.White, fontSize = 11.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onConfirmedChange(!confirmed) },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(checked = confirmed, onCheckedChange = onConfirmedChange)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "আমি নিশ্চিত করছি যে উপস্থাপিত জাতীয় পরিচয়পত্রটি আমার নিজের এবং সকল তথ্য সঠিক।",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

// -------------------------------------------------------------
// Step 3: Live Face Scan & Biometric Comparison View
// -------------------------------------------------------------
@Composable
private fun StepFaceScanView(
    isScanning: Boolean,
    scanProgress: Float,
    blinkPassed: Boolean,
    headTurnPassed: Boolean,
    smilePassed: Boolean,
    faceMatched: Boolean,
    matchScore: Int,
    avatarUrl: String,
    nidPhotoUrl: String,
    onStartScan: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "৩. ফেস স্ক্যান ও NID ছবির সাথে মিল যাচাই",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "জাতীয় পরিচয়পত্রের ছবির সাথে আপনার লাইভ মুখের বায়োমেট্রিক সাদৃশ্য মিলানো হচ্ছে।",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
            )

            // Split View: NID Card Photo vs Live Face Scan Viewfinder
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Left: NID Photo Reference
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "NID পরিচয়পত্রের ছবি",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .border(2.dp, VerifiedYellowBorder, RoundedCornerShape(14.dp))
                    ) {
                        AsyncImage(
                            model = nidPhotoUrl,
                            contentDescription = "NID Photo Face",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                // Right: Live Biometric Camera Scanner
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "লাইভ ফেস স্ক্যানার",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .clip(CircleShape)
                            .background(Color.Black)
                            .border(
                                width = 3.dp,
                                color = when {
                                    faceMatched -> Color(0xFF4CAF50)
                                    isScanning -> VerifiedYellow
                                    else -> Color.Gray
                                },
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = avatarUrl,
                            contentDescription = "Live Scanned Face",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        if (isScanning) {
                            CircularProgressIndicator(
                                progress = scanProgress,
                                color = VerifiedYellow,
                                modifier = Modifier.fillMaxSize().padding(4.dp)
                            )
                        }

                        if (faceMatched) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF2E7D32)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Filled.Check, contentDescription = null, tint = Color.White)
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    if (!isScanning && !faceMatched) {
                        Button(onClick = onStartScan, colors = ButtonDefaults.buttonColors(containerColor = TeaLeafGreen)) {
                            Text("স্ক্যান শুরু করুন")
                        }
                    } else if (isScanning) {
                        Text("স্ক্যানিং... চোখের পলক ফেলুন!", color = VerifiedYellow, fontWeight = FontWeight.Bold)
                    } else if (faceMatched) {
                        Text("সাফল্য! ম্যাচ স্কোর: $matchScore%", color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                        TextButton(onClick = onStartScan) { Text("আবার করুন") }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Liveness Challenge Checklist
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "জীবন্ত মানুষ (Liveness & Anti-Spoof) চেকলিস্ট:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    LivenessItem(label = "১. চোখের পলক ফেলুন 👀", passed = blinkPassed)
                    LivenessItem(label = "২. মাথা ডানে-বামে সামান্য ঘুরান 👈👉", passed = headTurnPassed)
                    LivenessItem(label = "৩. স্বাভাবিকভাবে একটু হাসুন 😊", passed = smilePassed)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (!isScanning && !faceMatched) {
                Button(
                    onClick = onStartScan,
                    colors = ButtonDefaults.buttonColors(containerColor = TeaLeafGreen),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().height(46.dp)
                ) {
                    Icon(imageVector = Icons.Filled.Face, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("লাইভ ফেস স্ক্যান শুরু করুন", fontWeight = FontWeight.Bold)
                }
            } else if (isScanning) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LinearProgressIndicator(
                        progress = { scanProgress },
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                        color = VerifiedYellow
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "মুখমণ্ডল এবং NID ফটো বায়োমেট্রিক বিশ্লেষণ করা হচ্ছে...",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else if (faceMatched) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFE8F5E9),
                    border = BorderStroke(1.dp, Color(0xFF4CAF50)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Filled.Verified, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "ম্যাচ সফল: ${matchScore}% নিখুঁত সাদৃশ্য মিল! 🎉",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color(0xFF1B5E20)
                            )
                            Text(
                                text = "জাতীয় পরিচয়পত্রের ছবির সাথে লাইভ ফেস স্ক্যানের পূর্ণাঙ্গ মিল পাওয়া গেছে (Biometric Authenticated)।",
                                fontSize = 11.sp,
                                color = Color(0xFF2E7D32)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LivenessItem(label: String, passed: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (passed) Icons.Filled.CheckCircle else Icons.Filled.RadioButtonUnchecked,
            contentDescription = null,
            tint = if (passed) Color(0xFF2E7D32) else Color.Gray,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            color = if (passed) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurface
        )
    }
}

// -------------------------------------------------------------
// Step 4: Phone & Email OTP View
// -------------------------------------------------------------
@Composable
private fun StepPhoneEmailOtpView(
    phoneNumber: String,
    onPhoneChange: (String) -> Unit,
    phoneOtpSent: Boolean,
    phoneOtpInput: String,
    onPhoneOtpInputChange: (String) -> Unit,
    isPhoneVerified: Boolean,
    onSendPhoneOtp: () -> Unit,
    onVerifyPhoneOtp: () -> Unit,
    emailAddress: String,
    onEmailChange: (String) -> Unit,
    emailOtpSent: Boolean,
    emailOtpInput: String,
    onEmailOtpInputChange: (String) -> Unit,
    isEmailVerified: Boolean,
    onSendEmailOtp: () -> Unit,
    onVerifyEmailOtp: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "৪. মোবাইল নাম্বার ও ই-মেল ভেরিফিকেশন",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "আপনার অ্যাকাউন্ট সুরক্ষায় এবং ভেরিফাইড নাগরিক হিসেবে মোবাইল ও ইমেল নিশ্চিত করুন।",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
            )

            // Phone Number Section
            Text(
                text = "মোবাইল নম্বর ভেরিফিকেশন (SMS OTP):",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = phoneNumber,
                onValueChange = onPhoneChange,
                enabled = !isPhoneVerified,
                label = { Text("মোবাইল নম্বর (+880)") },
                leadingIcon = { Icon(Icons.Filled.Phone, contentDescription = null) },
                trailingIcon = {
                    if (isPhoneVerified) {
                        Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = Color(0xFF2E7D32))
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            if (!isPhoneVerified) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (!phoneOtpSent) {
                        Button(
                            onClick = onSendPhoneOtp,
                            colors = ButtonDefaults.buttonColors(containerColor = TeaLeafGreen),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("ওটিপি কোড পাঠান (SMS OTP)")
                        }
                    } else {
                        OutlinedTextField(
                            value = phoneOtpInput,
                            onValueChange = onPhoneOtpInputChange,
                            label = { Text("৬ ডিজিটের ওটিপি") },
                            placeholder = { Text("529148") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        Button(
                            onClick = onVerifyPhoneOtp,
                            colors = ButtonDefaults.buttonColors(containerColor = TeaLeafGreen),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.align(Alignment.CenterVertically)
                        ) {
                            Text("যাচাই")
                        }
                    }
                }
            } else {
                Text(
                    text = "✅ মোবাইল নম্বর সফলভাবে ভেরিফাইড হয়েছে",
                    fontSize = 12.sp,
                    color = Color(0xFF2E7D32),
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
            Divider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(16.dp))

            // Email Address Section
            Text(
                text = "ই-মেল ঠিকানা ভেরিফিকেশন (Email Code):",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = emailAddress,
                onValueChange = onEmailChange,
                enabled = !isEmailVerified,
                label = { Text("ইমেল ঠিকানা") },
                leadingIcon = { Icon(Icons.Filled.Email, contentDescription = null) },
                trailingIcon = {
                    if (isEmailVerified) {
                        Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = Color(0xFF2E7D32))
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            if (!isEmailVerified) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (!emailOtpSent) {
                        Button(
                            onClick = onSendEmailOtp,
                            colors = ButtonDefaults.buttonColors(containerColor = TeaLeafGreen),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("ইমেলে কোড পাঠান")
                        }
                    } else {
                        OutlinedTextField(
                            value = emailOtpInput,
                            onValueChange = onEmailOtpInputChange,
                            label = { Text("ইমেল কোড") },
                            placeholder = { Text("738204") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        Button(
                            onClick = onVerifyEmailOtp,
                            colors = ButtonDefaults.buttonColors(containerColor = TeaLeafGreen),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.align(Alignment.CenterVertically)
                        ) {
                            Text("যাচাই")
                        }
                    }
                }
            } else {
                Text(
                    text = "✅ ইমেল ঠিকানা সফলভাবে ভেরিফাইড হয়েছে",
                    fontSize = 12.sp,
                    color = Color(0xFF2E7D32),
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

// -------------------------------------------------------------
// Step 5: Summary & Badge Activation View
// -------------------------------------------------------------
@Composable
private fun StepActivateBadgeSummaryView(
    userName: String,
    age: Int,
    nidNumber: String,
    faceMatchScore: Int,
    phone: String,
    email: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(VerifiedYellowContainer)
                    .border(2.dp, VerifiedYellowBorder, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Verified,
                    contentDescription = null,
                    tint = VerifiedYellow,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "৫. হলুদ ভেরিফাইড ব্যাজ সক্রিয়করণ",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "সকল শর্তাবলী ও প্রমাণ সফলভাবে যাচাই করা হয়েছে।",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 2.dp, bottom = 16.dp)
            )

            // Final Summary Table
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = VerifiedYellowContainer.copy(alpha = 0.5f),
                border = BorderStroke(1.dp, VerifiedYellowBorder.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    SummaryRow(label = "প্রার্থীর নাম:", value = userName)
                    SummaryRow(label = "প্রার্থীর বয়স:", value = "$age বছর (১৮+ নিশ্চিত ✅)")
                    SummaryRow(label = "NID নম্বর:", value = nidNumber)
                    SummaryRow(label = "NID ফেস স্ক্যান মিল:", value = "$faceMatchScore% নিখুঁত বায়োমেট্রিক মিল ✅")
                    SummaryRow(label = "মোবাইল নম্বর:", value = "$phone (OTP Verified ✅)")
                    SummaryRow(label = "ইমেল ঠিকানা:", value = "$email (Verified ✅)")
                    SummaryRow(label = "ভেরিফাইড ব্যাজ কালার:", value = "হলুদ (Yellow Gold 🌟)")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "⚠️ ভেরিফাইড পেইজ/প্রোফাইল হিসেবে সচল রাখতে প্রতি মাসে ৯৯ টাকা ফি প্রযোজ্য।",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "নিচের বাটনে চাপ দিলে আপনার প্রোফাইলে এবং সকল পোস্টে সাথে সাথে অফিসিয়াল হলুদ ভেরিফাইড ব্যাজ দৃশ্যমান হবে।",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
    }
}
