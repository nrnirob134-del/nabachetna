package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.TeaLeafGreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class TranslatedMessage(
    val id: String,
    val senderName: String,
    val originalText: String,
    val translatedText: String,
    val sourceLang: String,
    val targetLang: String,
    val isMe: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlobalAiTranslatorScreen(
    onBack: () -> Unit
) {
    BackHandler { onBack() }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var sourceLang by remember { mutableStateOf("বাংলা (Bengali)") }
    var targetLang by remember { mutableStateOf("English (US)") }
    var inputMessage by remember { mutableStateOf("") }
    var isRecordingVoice by remember { mutableStateOf(false) }

    var messages by remember {
        mutableStateOf(
            listOf(
                TranslatedMessage(
                    id = "1",
                    senderName = "John (USA Business Partner)",
                    originalText = "Hello! Can we discuss the bulk export price for the organic cotton hoodies?",
                    translatedText = "হ্যালো! আমরা কি অর্গানিক কটন হুডির পাইকারি রপ্তানি মূল্য নিয়ে আলোচনা করতে পারি?",
                    sourceLang = "English",
                    targetLang = "Bengali",
                    isMe = false
                ),
                TranslatedMessage(
                    id = "2",
                    senderName = "আপনি (You)",
                    originalText = "অবশ্যই! প্রতি পিস ৩৫ ডলার এবং ৫০০০ পিসের বেশি হলে বিশেষ ছাড় পাওয়া যাবে।",
                    translatedText = "Sure! $35 per piece, and special discounts apply for orders over 5,000 pieces.",
                    sourceLang = "Bengali",
                    targetLang = "English",
                    isMe = true
                )
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("গ্লোবাল এআই রিয়েল-টাইম ট্রান্সলেটর 🌐", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("ভাষার বাধা ছাড়াই বিশ্বব্যাপী ব্যবসা ও কথা বলুন সম্পূর্ণ ফ্রিতে", fontSize = 10.sp, color = Color.White.copy(alpha = 0.8f))
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = TeaLeafGreen)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF8F9FA)),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Language Selector Bar
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("আমার ভাষা (My Language)", fontSize = 10.sp, color = Color.Gray)
                        Text(sourceLang, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TeaLeafGreen)
                    }
                    IconButton(onClick = {
                        val temp = sourceLang
                        sourceLang = targetLang
                        targetLang = temp
                        Toast.makeText(context, "ভাষা অদলবদল করা হয়েছে!", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(imageVector = Icons.Default.SwapHoriz, contentDescription = "Swap", tint = TeaLeafGreen)
                    }
                    Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                        Text("টার্গেট ভাষা (Partner Lang)", fontSize = 10.sp, color = Color.Gray)
                        Text(targetLang, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TeaLeafGreen)
                    }
                }
            }

            // Live Conversation & Translation Feed
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(messages) { msg ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = if (msg.isMe) TeaLeafGreen.copy(alpha = 0.08f) else Color.White
                        ),
                        border = BorderStroke(1.dp, if (msg.isMe) TeaLeafGreen.copy(alpha = 0.3f) else Color.LightGray.copy(alpha = 0.5f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(msg.senderName, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TeaLeafGreen)
                                Surface(
                                    color = TeaLeafGreen.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "${msg.sourceLang} ➔ ${msg.targetLang}",
                                        fontSize = 9.sp,
                                        color = TeaLeafGreen,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            // Original Text
                            Text(
                                text = "মূল (${msg.sourceLang}): ${msg.originalText}",
                                fontSize = 12.sp,
                                color = Color.DarkGray
                            )

                            Divider(color = Color.LightGray.copy(alpha = 0.4f))

                            // AI Translated Text
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "🤖 এআই অনুবাদ (${msg.targetLang}): ${msg.translatedText}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TeaLeafGreen,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(
                                    onClick = {
                                        Toast.makeText(context, "অডিও স্পিচ প্লে হচ্ছে...", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.VolumeUp, contentDescription = "Listen", tint = TeaLeafGreen, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }

            // Bottom Input Bar & Voice Simulation
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = {
                            isRecordingVoice = !isRecordingVoice
                            if (isRecordingVoice) {
                                Toast.makeText(context, "🎙️ লাইভ ভয়েস ট্রান্সলেশন রেকর্ডিং শুরু হয়েছে...", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "✨ ভয়েস অনুবাদ সম্পন্ন ও সেন্ড করা হয়েছে!", Toast.LENGTH_SHORT).show()
                                val newMsg = TranslatedMessage(
                                    id = System.currentTimeMillis().toString(),
                                    senderName = "আপনি (You)",
                                    originalText = "আমাদের পেমেন্ট এস্ক্রো সিস্টেম সম্পূর্ণ নিরাপদ।",
                                    translatedText = "Our payment escrow system is completely secure.",
                                    sourceLang = "Bengali",
                                    targetLang = "English",
                                    isMe = true
                                )
                                messages = messages + newMsg
                            }
                        },
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(if (isRecordingVoice) Color.Red else TeaLeafGreen)
                    ) {
                        Icon(
                            imageVector = if (isRecordingVoice) Icons.Default.MicOff else Icons.Default.Mic,
                            contentDescription = "Voice",
                            tint = Color.White
                        )
                    }

                    OutlinedTextField(
                        value = inputMessage,
                        onValueChange = { inputMessage = it },
                        placeholder = { Text("আপনার বার্তা লিখুন (যেকোনো ভাষায়)...", fontSize = 12.sp) },
                        modifier = Modifier.weight(1f),
                        maxLines = 2,
                        shape = RoundedCornerShape(24.dp)
                    )

                    Button(
                        onClick = {
                            if (inputMessage.isNotBlank()) {
                                val translated = "AI Translated: " + inputMessage
                                val newMsg = TranslatedMessage(
                                    id = System.currentTimeMillis().toString(),
                                    senderName = "আপনি (You)",
                                    originalText = inputMessage,
                                    translatedText = translated,
                                    sourceLang = sourceLang.substringBefore(" "),
                                    targetLang = targetLang.substringBefore(" "),
                                    isMe = true
                                )
                                messages = messages + newMsg
                                inputMessage = ""
                                Toast.makeText(context, "এআই অনুবাদ ও সেন্ড সফল!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = TeaLeafGreen),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Send, contentDescription = "Send", tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}
