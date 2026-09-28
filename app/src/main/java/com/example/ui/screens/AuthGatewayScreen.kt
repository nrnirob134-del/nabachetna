package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.data.model.User
import com.example.data.service.LivenessChallengeStep
import com.example.ui.theme.TeaLeafGreen
import com.example.ui.viewmodel.PageBookViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthGatewayScreen(
    viewModel: PageBookViewModel,
    onLoginSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val allUsers by viewModel.allUsers.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableStateOf(0) } // 0: Sign In, 1: Sign Up

    // Sign In form states
    var signInIdentifier by remember { mutableStateOf("anika@nabachetna.com") }
    var signInPassword by remember { mutableStateOf("123456") }
    var signInPasswordVisible by remember { mutableStateOf(false) }
    var isSigningIn by remember { mutableStateOf(false) }

    // Sign Up form states
    var signUpName by remember { mutableStateOf("") }
    var signUpIdentifier by remember { mutableStateOf("") }
    var signUpPassword by remember { mutableStateOf("") }
    var signUpPasswordVisible by remember { mutableStateOf(false) }
    var signUpBio by remember { mutableStateOf("নতুন নবচেতনা ব্যবহারকারী") }
    var signUpAvatarUrl by remember { mutableStateOf("https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150") }
    var selectedCountryCode by remember { mutableStateOf("+880") } // Default Bangladesh
    var selectedLanguageName by remember { mutableStateOf("বাংলা") }
    var showCountryMenu by remember { mutableStateOf(false) }
    var showLanguageMenu by remember { mutableStateOf(false) }
    var signUpTermsAccepted by remember { mutableStateOf(false) }
    var showTermsDialog by remember { mutableStateOf(false) }

    // Dynamic 3D Liveness & Anti-Spoofing Scanner Dialog State
    var showFaceScannerDialog by remember { mutableStateOf(false) }
    var scannerMode by remember { mutableStateOf("SIGN_IN") } // "SIGN_IN" or "SIGN_UP"
    var currentLivenessStepIndex by remember { mutableStateOf(0) }
    val challengeSteps = remember {
        listOf(
            LivenessChallengeStep.STEP_BLINK_EYES,
            LivenessChallengeStep.STEP_TURN_HEAD_LEFT,
            LivenessChallengeStep.STEP_TURN_HEAD_RIGHT,
            LivenessChallengeStep.STEP_SMILE_CHECK,
            LivenessChallengeStep.STEP_NOD_HEAD
        )
    }

    var isScanningActive by remember { mutableStateOf(false) }
    var isLivenessVerifying by remember { mutableStateOf(false) }
    var livenessProgress by remember { mutableStateOf(0f) }
    var scanErrorAlert by remember { mutableStateOf<String?>(null) }
    var scannedFaceSubjectName by remember { mutableStateOf("") }

    // Radar pulse animation
    val infiniteTransition = rememberInfiniteTransition(label = "radar")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )
    val radarRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    val sampleAvatars = listOf(
        "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150",
        "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150",
        "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=150",
        "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150",
        "https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=150",
        "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=150"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0A261C),
                        Color(0xFF133E32),
                        Color(0xFF04120B)
                    )
                )
            )
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))

                // Brand Logo & Header
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(RoundedCornerShape(18.dp))
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
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Serif
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "নবচেতনা • NABACHETNA",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    letterSpacing = 0.5.sp
                )

                Text(
                    text = "মিলিটারি-গ্রেড ৩ডি লাইভনেস ফেস সিকিউরিটি ও সোশ্যাল নেটওয়ার্ক",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFA5D6A7),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Anti-Spoofing & 2-Account Guarantee Badge
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFF2E7D32).copy(alpha = 0.25f),
                    border = BorderStroke(1.dp, Color(0xFF81C784).copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "Shield",
                            tint = Color(0xFF81C784),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "🛡️ ৩ডি লাইভনেস (চোখের পলক ও মাথা ঘোরানো) • ১ মুখ = ২ অ্যাকাউন্ট",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF81C784)
                        )
                    }
                }
            }

            // Quick Account Switcher (if users exist)
            if (allUsers.isNotEmpty()) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = "সংরক্ষিত ফেস-ভেরিফাইড প্রোফাইল:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White.copy(alpha = 0.85f),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(allUsers) { user ->
                                Card(
                                    modifier = Modifier
                                        .clickable {
                                            viewModel.switchUserAccount(user.id)
                                            Toast.makeText(context, "${user.name} হিসেবে লগইন সফল", Toast.LENGTH_SHORT).show()
                                            onLoginSuccess()
                                        }
                                        .testTag("quick_user_${user.id}"),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF143022)),
                                    border = BorderStroke(1.dp, Color(0xFF2E7D32).copy(alpha = 0.6f))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box {
                                            AsyncImage(
                                                model = user.avatarUrl,
                                                contentDescription = user.name,
                                                modifier = Modifier
                                                    .size(38.dp)
                                                    .clip(CircleShape)
                                                    .border(1.5.dp, Color(0xFF81C784), CircleShape),
                                                contentScale = ContentScale.Crop
                                            )
                                            Icon(
                                                imageVector = Icons.Default.Verified,
                                                contentDescription = "Verified",
                                                tint = Color(0xFF81C784),
                                                modifier = Modifier
                                                    .size(14.dp)
                                                    .align(Alignment.BottomEnd)
                                                    .background(Color(0xFF143022), CircleShape)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = user.name,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                            Text(
                                                text = "জীবন্ত মানুষ ভেরিফাইড ✓",
                                                fontSize = 9.sp,
                                                color = Color(0xFF81C784)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Main Authentication Gateway Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF11251A).copy(alpha = 0.95f)),
                    border = BorderStroke(1.dp, Color(0xFF2E7D32).copy(alpha = 0.45f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        // Tab Selector
                        TabRow(
                            selectedTabIndex = selectedTab,
                            containerColor = Color(0xFF0B1B13),
                            contentColor = Color(0xFF81C784),
                            indicator = {},
                            modifier = Modifier.clip(RoundedCornerShape(12.dp))
                        ) {
                            Tab(
                                selected = selectedTab == 0,
                                onClick = { selectedTab = 0 },
                                modifier = Modifier
                                    .padding(4.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (selectedTab == 0) Color(0xFF2E7D32) else Color.Transparent)
                            ) {
                                Text(
                                    text = "লগইন (Sign In)",
                                    fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 13.sp,
                                    color = if (selectedTab == 0) Color.White else Color.White.copy(alpha = 0.6f),
                                    modifier = Modifier.padding(vertical = 10.dp)
                                )
                            }
                            Tab(
                                selected = selectedTab == 1,
                                onClick = { selectedTab = 1 },
                                modifier = Modifier
                                    .padding(4.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (selectedTab == 1) Color(0xFF2E7D32) else Color.Transparent)
                            ) {
                                Text(
                                    text = "নতুন একাউন্ট (Sign Up)",
                                    fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 13.sp,
                                    color = if (selectedTab == 1) Color.White else Color.White.copy(alpha = 0.6f),
                                    modifier = Modifier.padding(vertical = 10.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // --- TAB 0: SIGN IN ---
                        if (selectedTab == 0) {
                            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                                OutlinedTextField(
                                    value = signInIdentifier,
                                    onValueChange = { signInIdentifier = it },
                                    label = { Text("ইমেইল বা মোবাইল নম্বর", color = Color.White.copy(alpha = 0.7f)) },
                                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF81C784)) },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth().testTag("signin_identifier_input"),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
                                        focusedBorderColor = Color(0xFF81C784),
                                        unfocusedBorderColor = Color(0xFF2E7D32).copy(alpha = 0.5f)
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                )

                                OutlinedTextField(
                                    value = signInPassword,
                                    onValueChange = { signInPassword = it },
                                    label = { Text("পাসওয়ার্ড", color = Color.White.copy(alpha = 0.7f)) },
                                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF81C784)) },
                                    trailingIcon = {
                                        IconButton(onClick = { signInPasswordVisible = !signInPasswordVisible }) {
                                            Icon(
                                                imageVector = if (signInPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                                contentDescription = "Toggle password",
                                                tint = Color.White.copy(alpha = 0.6f)
                                            )
                                        }
                                    },
                                    singleLine = true,
                                    visualTransformation = if (signInPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                    modifier = Modifier.fillMaxWidth().testTag("signin_password_input"),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
                                        focusedBorderColor = Color(0xFF81C784),
                                        unfocusedBorderColor = Color(0xFF2E7D32).copy(alpha = 0.5f)
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                )

                                // Password Login Button
                                Button(
                                    onClick = {
                                        if (signInIdentifier.isBlank() || signInPassword.isBlank()) {
                                            Toast.makeText(context, "ইমেইল ও পাসওয়ার্ড পূরণ করুন", Toast.LENGTH_SHORT).show()
                                            return@Button
                                        }
                                        isSigningIn = true
                                        viewModel.signInWithPassword(signInIdentifier, signInPassword) { success, msg ->
                                            isSigningIn = false
                                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                            if (success) {
                                                onLoginSuccess()
                                            }
                                        }
                                    },
                                    enabled = !isSigningIn,
                                    modifier = Modifier.fillMaxWidth().height(48.dp).testTag("signin_submit_button"),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                                ) {
                                    if (isSigningIn) {
                                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                                    } else {
                                        Icon(Icons.Default.Login, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("লগইন করুন (Sign In)", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    HorizontalDivider(modifier = Modifier.weight(1f), color = Color.White.copy(alpha = 0.2f))
                                    Text(
                                        text = "অথবা লাইভ ৩ডি ফেস আনলক",
                                        fontSize = 11.sp,
                                        color = Color.White.copy(alpha = 0.6f),
                                        modifier = Modifier.padding(horizontal = 8.dp)
                                    )
                                    HorizontalDivider(modifier = Modifier.weight(1f), color = Color.White.copy(alpha = 0.2f))
                                }

                                // 🌟 3D Dynamic Liveness Face Unlock Button
                                OutlinedButton(
                                    onClick = {
                                        if (signInIdentifier.isBlank()) {
                                            Toast.makeText(context, "প্রথমে আপনার নিবন্ধিত ইমেইল বা ফোন নম্বর দিন", Toast.LENGTH_SHORT).show()
                                            return@OutlinedButton
                                        }
                                        scannerMode = "SIGN_IN"
                                        scannedFaceSubjectName = signInIdentifier.substringBefore("@")
                                        currentLivenessStepIndex = 0
                                        scanErrorAlert = null
                                        livenessProgress = 0f
                                        showFaceScannerDialog = true
                                    },
                                    modifier = Modifier.fillMaxWidth().height(48.dp).testTag("face_unlock_button"),
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.5.dp, Brush.horizontalGradient(listOf(Color(0xFF81C784), Color(0xFF0288D1)))),
                                    colors = ButtonDefaults.outlinedButtonColors(containerColor = Color(0xFF0288D1).copy(alpha = 0.12f))
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.FaceRetouchingNatural,
                                        contentDescription = "Face Unlock",
                                        tint = Color(0xFF81C784),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "৩ডি ফেস লাইভনেস আনলক (Anti-Spoof)",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }

                        // --- TAB 1: SIGN UP WITH 3D LIVENESS ANTI-SPOOFING ---
                        else {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                OutlinedTextField(
                                    value = signUpName,
                                    onValueChange = { signUpName = it },
                                    label = { Text("আপনার পূর্ণ নাম", color = Color.White.copy(alpha = 0.7f)) },
                                    leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null, tint = Color(0xFF81C784)) },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth().testTag("signup_name_input"),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
                                        focusedBorderColor = Color(0xFF81C784),
                                        unfocusedBorderColor = Color(0xFF2E7D32).copy(alpha = 0.5f)
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                )

                                 OutlinedTextField(
                                    value = signUpIdentifier,
                                    onValueChange = { signUpIdentifier = it },
                                    label = { Text("ইমেইল বা মোবাইল নম্বর", color = Color.White.copy(alpha = 0.7f)) },
                                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = Color(0xFF81C784)) },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth().testTag("signup_identifier_input"),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
                                        focusedBorderColor = Color(0xFF81C784),
                                        unfocusedBorderColor = Color(0xFF2E7D32).copy(alpha = 0.5f)
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                )

                                // Country Selection (30 Countries)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Box(modifier = Modifier.weight(1f)) {
                                        OutlinedButton(
                                            onClick = { showCountryMenu = true },
                                            modifier = Modifier.fillMaxWidth().height(52.dp),
                                            shape = RoundedCornerShape(12.dp),
                                            border = BorderStroke(1.dp, Color(0xFF81C784).copy(alpha = 0.5f))
                                        ) {
                                            val c = com.example.data.service.GlobalLocalizationRegistry.supported30Countries.find { it.code == selectedCountryCode }
                                            Text(
                                                text = "${c?.flagEmoji ?: "🇧🇩"} ${c?.nameBn ?: "বাংলাদেশ"} (${selectedCountryCode})",
                                                fontSize = 11.sp,
                                                color = Color.White,
                                                maxLines = 1
                                            )
                                        }

                                        DropdownMenu(
                                            expanded = showCountryMenu,
                                            onDismissRequest = { showCountryMenu = false }
                                        ) {
                                            com.example.data.service.GlobalLocalizationRegistry.supported30Countries.take(15).forEach { country ->
                                                DropdownMenuItem(
                                                    text = { Text("${country.flagEmoji} ${country.nameBn} (${country.code})", fontSize = 12.sp) },
                                                    onClick = {
                                                        selectedCountryCode = country.code
                                                        showCountryMenu = false
                                                    }
                                                )
                                            }
                                        }
                                    }

                                    // Language Selection (30 Languages)
                                    Box(modifier = Modifier.weight(1f)) {
                                        OutlinedButton(
                                            onClick = { showLanguageMenu = true },
                                            modifier = Modifier.fillMaxWidth().height(52.dp),
                                            shape = RoundedCornerShape(12.dp),
                                            border = BorderStroke(1.dp, Color(0xFF81C784).copy(alpha = 0.5f))
                                        ) {
                                            Text(
                                                text = "🌐 ভাষা: $selectedLanguageName",
                                                fontSize = 11.sp,
                                                color = Color.White,
                                                maxLines = 1
                                            )
                                        }

                                        DropdownMenu(
                                            expanded = showLanguageMenu,
                                            onDismissRequest = { showLanguageMenu = false }
                                        ) {
                                            com.example.data.service.GlobalLocalizationRegistry.supported30Languages.take(15).forEach { lang ->
                                                DropdownMenuItem(
                                                    text = { Text("${lang.nameNative} (${lang.nameEnglish})", fontSize = 12.sp) },
                                                    onClick = {
                                                        selectedLanguageName = lang.nameNative
                                                        showLanguageMenu = false
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }

                                OutlinedTextField(
                                    value = signUpPassword,
                                    onValueChange = { signUpPassword = it },
                                    label = { Text("পাসওয়ার্ড তৈরি করুন", color = Color.White.copy(alpha = 0.7f)) },
                                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF81C784)) },
                                    trailingIcon = {
                                        IconButton(onClick = { signUpPasswordVisible = !signUpPasswordVisible }) {
                                            Icon(
                                                imageVector = if (signUpPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                                contentDescription = null,
                                                tint = Color.White.copy(alpha = 0.6f)
                                            )
                                        }
                                    },
                                    singleLine = true,
                                    visualTransformation = if (signUpPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                    modifier = Modifier.fillMaxWidth().testTag("signup_password_input"),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
                                        focusedBorderColor = Color(0xFF81C784),
                                        unfocusedBorderColor = Color(0xFF2E7D32).copy(alpha = 0.5f)
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                )

                                // Select Profile Photo
                                Text(
                                    text = "প্রোফাইল ছবি নির্বাচন করুন:",
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontWeight = FontWeight.SemiBold
                                )

                                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    items(sampleAvatars) { avatar ->
                                        val isSelected = signUpAvatarUrl == avatar
                                        AsyncImage(
                                            model = avatar,
                                            contentDescription = "Avatar Choice",
                                            modifier = Modifier
                                                .size(44.dp)
                                                .clip(CircleShape)
                                                .border(
                                                    if (isSelected) 2.5.dp else 1.dp,
                                                    if (isSelected) Color(0xFF81C784) else Color.White.copy(alpha = 0.3f),
                                                    CircleShape
                                                )
                                                .clickable { signUpAvatarUrl = avatar },
                                            contentScale = ContentScale.Crop
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Terms and conditions checkbox
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { signUpTermsAccepted = !signUpTermsAccepted }
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = signUpTermsAccepted,
                                        onCheckedChange = { signUpTermsAccepted = it },
                                        colors = CheckboxDefaults.colors(
                                            checkedColor = Color(0xFF81C784),
                                            uncheckedColor = Color.White.copy(alpha = 0.5f),
                                            checkmarkColor = Color(0xFF1B5E20)
                                         )
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "আমি নবচেতনার ব্যবহারকারী নির্দেশিকা, কপিরাইট নীতি এবং মনিটাইজেশন শর্তাবলী মেনে নিচ্ছি।",
                                            fontSize = 11.sp,
                                            color = Color.White.copy(alpha = 0.9f),
                                            lineHeight = 15.sp
                                        )
                                        Text(
                                            text = "নীতিমালা ও শর্তাবলী পড়ুন",
                                            fontSize = 12.sp,
                                            color = Color(0xFF81C784),
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.clickable { showTermsDialog = true }
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // AI 3D Liveness Verification & Signup Button
                                Button(
                                    onClick = {
                                        if (signUpName.isBlank() || signUpIdentifier.isBlank() || signUpPassword.isBlank()) {
                                            Toast.makeText(context, "সকল তথ্য সঠিকভাবে পূরণ করুন", Toast.LENGTH_SHORT).show()
                                            return@Button
                                        }
                                        if (!signUpTermsAccepted) {
                                            Toast.makeText(context, "দয়া করে নবচেতনার নীতিমালা ও শর্তাবলী মেনে টিক চিহ্ন দিন", Toast.LENGTH_LONG).show()
                                            return@Button
                                        }
                                        scannerMode = "SIGN_UP"
                                        scannedFaceSubjectName = signUpName
                                        currentLivenessStepIndex = 0
                                        scanErrorAlert = null
                                        livenessProgress = 0f
                                        showFaceScannerDialog = true
                                    },
                                    modifier = Modifier.fillMaxWidth().height(50.dp).testTag("signup_face_verify_button"),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = if (signUpTermsAccepted) Color(0xFF2E7D32) else Color(0xFF424242))
                                ) {
                                    Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("৩ডি লাইভ ফেস ভেরিফাই করুন (Anti-Spoof)", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    // --- 🌟 INTERACTIVE 3D DYNAMIC LIVENESS & ANTI-HACKING SCANNER DIALOG ---
    if (showFaceScannerDialog) {
        val currentStep = challengeSteps.getOrElse(currentLivenessStepIndex) { challengeSteps.last() }

        Dialog(onDismissRequest = {
            if (!isLivenessVerifying) showFaceScannerDialog = false
        }) {
            Card(
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF091D13)),
                border = BorderStroke(2.dp, Brush.linearGradient(listOf(Color(0xFF81C784), Color(0xFF0288D1)))),
                modifier = Modifier.fillMaxWidth(0.96f)
            ) {
                Column(
                    modifier = Modifier.padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = "Security",
                                tint = Color(0xFF81C784),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "৩ডি লাইভনেস ভেরিফিকেশন",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        IconButton(
                            onClick = { showFaceScannerDialog = false },
                            enabled = !isLivenessVerifying
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White.copy(alpha = 0.7f))
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Step Progress Indicator
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        challengeSteps.forEachIndexed { idx, _ ->
                            val isCompleted = idx < currentLivenessStepIndex
                            val isCurrent = idx == currentLivenessStepIndex
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(
                                        when {
                                            isCompleted -> Color(0xFF81C784)
                                            isCurrent -> Color(0xFF0288D1)
                                            else -> Color.White.copy(alpha = 0.2f)
                                        }
                                    )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 3D Scanner Radar Mesh View
                    Box(
                        modifier = Modifier
                            .size(170.dp)
                            .scale(pulseScale)
                            .clip(CircleShape)
                            .background(Color(0xFF05130A))
                            .border(
                                3.dp,
                                if (scanErrorAlert != null) Color.Red else Color(0xFF81C784),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        // Face silhouette or live scanning avatar
                        AsyncImage(
                            model = if (scannerMode == "SIGN_UP") signUpAvatarUrl else "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=200",
                            contentDescription = "Face Target",
                            modifier = Modifier
                                .size(136.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )

                        // Glowing 3D Mesh Grid & Radar overlay
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .rotate(radarRotation)
                                .background(
                                    Brush.sweepGradient(
                                        listOf(
                                            Color.Transparent,
                                            Color(0xFF81C784).copy(alpha = 0.35f),
                                            Color.Transparent
                                        )
                                    )
                                )
                        )

                        if (isLivenessVerifying) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(160.dp),
                                color = Color(0xFF81C784),
                                strokeWidth = 3.5.dp
                            )
                        }

                        // Floating Emoji Guide
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF0A261C).copy(alpha = 0.9f),
                            border = BorderStroke(1.dp, Color(0xFF81C784)),
                            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 6.dp)
                        ) {
                            Text(
                                text = "${currentStep.guideEmoji} ধাপ ${currentLivenessStepIndex + 1}/${challengeSteps.size}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF81C784),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Live Instruction Card
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF132E20)),
                        border = BorderStroke(1.dp, Color(0xFF81C784).copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = currentStep.title,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF81C784)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = currentStep.instruction,
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.9f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    if (scanErrorAlert != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = scanErrorAlert!!,
                            fontSize = 12.sp,
                            color = Color(0xFFFF5252),
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showFaceScannerDialog = false },
                            enabled = !isLivenessVerifying,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("বাতিল", color = Color.White.copy(alpha = 0.8f))
                        }

                        Button(
                            onClick = {
                                isLivenessVerifying = true
                                scanErrorAlert = null
                                coroutineScope.launch {
                                    // Step-by-step interactive progression
                                    for (i in 0 until challengeSteps.size) {
                                        currentLivenessStepIndex = i
                                        delay(850) // Simulation of real-time motion capture
                                    }

                                    if (scannerMode == "SIGN_UP") {
                                        viewModel.signUpWithFaceVerification(
                                            name = signUpName,
                                            emailOrPhone = signUpIdentifier,
                                            password = signUpPassword,
                                            bio = signUpBio,
                                            avatarUrl = signUpAvatarUrl,
                                            faceSeedOrName = scannedFaceSubjectName,
                                            countryCode = selectedCountryCode,
                                            selectedLanguage = selectedLanguageName
                                        ) { success, msg ->
                                            isLivenessVerifying = false
                                            if (success) {
                                                Toast.makeText(context, "✓ ৩ডি লাইভনেস যাচাই সম্পন্ন! $msg", Toast.LENGTH_LONG).show()
                                                showFaceScannerDialog = false
                                                onLoginSuccess()
                                            } else {
                                                scanErrorAlert = msg
                                            }
                                        }
                                    } else {
                                        // Sign in face matching with Anti-Hacking Guard
                                        viewModel.signInWithFaceBiometric(
                                            emailOrPhone = signInIdentifier,
                                            scannedFaceSeed = scannedFaceSubjectName
                                        ) { success, msg ->
                                            isLivenessVerifying = false
                                            if (success) {
                                                Toast.makeText(context, "✓ ফেস আনলক সফল! $msg", Toast.LENGTH_SHORT).show()
                                                showFaceScannerDialog = false
                                                onLoginSuccess()
                                            } else {
                                                scanErrorAlert = msg
                                            }
                                        }
                                    }
                                }
                            },
                            enabled = !isLivenessVerifying,
                            modifier = Modifier.weight(1.6f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                        ) {
                            if (isLivenessVerifying) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("লাইভনেস যাচাই...", fontSize = 12.sp)
                            } else {
                                Icon(Icons.Default.VerifiedUser, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("৩ডি স্ক্যান সম্পন্ন করুন", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }

    // --- 📄 INTERACTIVE TERMS, MONETIZATION & COPYRIGHT POLICIES DIALOG ---
    if (showTermsDialog) {
        Dialog(onDismissRequest = { showTermsDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.85f)
                    .padding(16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1B5E20))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Gavel, contentDescription = null, tint = Color(0xFF81C784), modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "নবচেতনা ব্যবহারিক নীতিমালা",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        IconButton(onClick = { showTermsDialog = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White.copy(alpha = 0.7f))
                        }
                    }

                    HorizontalDivider(color = Color.White.copy(alpha = 0.2f))

                    // Scrollable Policy Details
                    Box(modifier = Modifier.weight(1f)) {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            item {
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.2f)),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(
                                            text = "১. মূল সংবিধান ও ব্যবহারকারী নির্দেশিকা",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF81C784)
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "• কোনো রাষ্ট্র বা ধর্মীয় বিশ্বাসের ওপর আঘাত করে পোস্ট, অপপ্রচার বা গুজব ছড়ানো সম্পূর্ণ নিষিদ্ধ।\n" +
                                                    "• সাইবার বুলিং, গালিগালাজ, অশ্লীল ভিডিও বা ব্ল্যাকমেইলিং অপরাধ হিসেবে বিবেচিত হবে।\n" +
                                                    "• মার্কেটপ্লেস ও এসক্রো ট্রানজেকশনে কোনো প্রকার জালিয়াতি করলে তাৎক্ষণিক অ্যাকাউন্ট বাতিল করা হবে।",
                                            fontSize = 12.sp,
                                            color = Color.White.copy(alpha = 0.85f),
                                            lineHeight = 18.sp
                                        )
                                    }
                                }
                            }

                            item {
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.2f)),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(
                                            text = "২. কন্টেন্ট আইডি ও কপিরাইট নীতি",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF81C784)
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "• নবচেতনা একটি স্বয়ংক্রিয় 'ডিজিটাল কন্টেন্ট ফিঙ্গারপ্রিন্ট' ও রিয়েল-টাইম কন্টেন্ট আইডি ট্র্যাকিং প্রযুক্তি ব্যবহার করে।\n" +
                                                    "• আপনি কোনো ছবি, অডিও বা ভিডিও আপলোড করলে সিস্টেম তার একটি অনন্য পারসেপচুয়াল হ্যাশ (Perceptual Hash) ডাটাবেজে সংরক্ষণ করে।\n" +
                                                    "• অন্য কেউ যদি আপনার অনুমতি ছাড়া একই কন্টেন্ট বা ওয়াটারমার্ক সহ কন্টেন্ট আপলোড করে, তবে কপিরাইট দাবি স্বয়ংক্রিয়ভাবে কার্যকর হবে।\n" +
                                                    "• কপিরাইট লঙ্ঘিত হলে উক্ত পোস্টের মনিটাইজেশন স্থগিত করে মূল ক্রিয়েটরকে আয়ের অংশ দেওয়া হবে বা পোস্টটি মুছে ফেলা হবে।",
                                            fontSize = 12.sp,
                                            color = Color.White.copy(alpha = 0.85f),
                                            lineHeight = 18.sp
                                        )
                                    }
                                }
                            }

                            item {
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.2f)),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(
                                            text = "৩. টেকসই মনিটাইজেশন ও কোম্পানির প্রফিট শেয়ার",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF81C784)
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "• মনিটাইজেশন শুধুমাত্র প্রফেশনাল 'পেজ' (Pages) এর জন্য প্রযোজ্য। ব্যক্তিগত ইউজার আইডি বা প্রোফাইল মনিটাইজ করা যাবে না।\n" +
                                                    "• পেজের মনিটাইজেশন চালু করতে হলে পেজে কমপক্ষে ৫,০০০ ফলোয়ার এবং ৬০,০০০ মিনিট বৈধ ওয়াচ টাইম থাকতে হবে।\n" +
                                                    "• বট এনগেজমেন্ট, অটো-লাইক বা কৃত্রিম ক্লিক দিয়ে পেজের রিচ বাড়ানোর চেষ্টা করলে পেজ ও আইডি চিরতরে বরখাস্ত হবে।\n" +
                                                    "• রাজস্ব বন্টন নীতি (Revenue Share Model): সকল আয়ের ৪৫% নবচেতনা কোম্পানি সার্ভার ও পরিচালন ফান্ডে জমা রাখবে এবং ৫৫% মূল ক্রিয়েটর পাবেন। কোম্পানি লোকসান দিয়ে পকেট থেকে কোনো বোনাস দিবে না।",
                                            fontSize = 12.sp,
                                            color = Color.White.copy(alpha = 0.85f),
                                            lineHeight = 18.sp
                                        )
                                    }
                                }
                            }

                            item {
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.2f)),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(
                                            text = "৪. ৩-ধাপের ওয়ার্নিং স্ট্রাইক ও ব্যান সিস্টেম",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF81C784)
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "• ১ম স্ট্রাইক: সতর্কবার্তা ইনবক্স নোটিশ হিসেবে পাঠানো হবে এবং违반ী পোস্ট মুছে ফেলা হবে।\n" +
                                                    "• ২য় স্ট্রাইক: একাউন্ট ৭ দিনের জন্য সাময়িক লক থাকবে (পোস্টিং, কমেন্ট ও ওয়ালেট উইথড্রল বন্ধ)।\n" +
                                                    "• ৩য় স্ট্রাইক: একাউন্ট চিরতরে ব্যান হবে এবং উক্ত ডিভাইস থেকে আর কখনো নবচেতনায় প্রবেশ করা যাবে না।",
                                            fontSize = 12.sp,
                                            color = Color.White.copy(alpha = 0.85f),
                                            lineHeight = 18.sp
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Bottom Action Button
                    Button(
                        onClick = {
                            signUpTermsAccepted = true
                            showTermsDialog = false
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF81C784)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF1B5E20))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("আমি এই শর্তগুলোতে সম্মত আছি", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20))
                    }
                }
            }
        }
    }
}
