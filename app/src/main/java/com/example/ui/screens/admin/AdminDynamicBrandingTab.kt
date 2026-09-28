package com.example.ui.screens.admin

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.data.model.DynamicBrandingConfig
import com.example.ui.theme.TeaLeafGreen
import com.example.ui.viewmodel.PageBookViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDynamicBrandingTab(
    viewModel: PageBookViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentBranding by viewModel.dynamicBrandingConfig.collectAsStateWithLifecycle()

    var titleInput by remember { mutableStateOf(currentBranding.title) }
    var typeInput by remember { mutableStateOf(currentBranding.type) } // "TEXT" or "IMAGE"
    var titleColorHexInput by remember { mutableStateOf(currentBranding.titleColorHex) }
    var logoImageUrlInput by remember { mutableStateOf(currentBranding.logoImageUrl) }
    var logoEmojiInput by remember { mutableStateOf(currentBranding.logoEmoji) }
    var gradientEnabledInput by remember { mutableStateOf(currentBranding.brandingGradientEnabled) }
    var gradientColor1Input by remember { mutableStateOf(currentBranding.brandingGradientColor1Hex) }
    var gradientColor2Input by remember { mutableStateOf(currentBranding.brandingGradientColor2Hex) }

    // Update form when backing state changes
    LaunchedEffect(currentBranding) {
        titleInput = currentBranding.title
        typeInput = currentBranding.type
        titleColorHexInput = currentBranding.titleColorHex
        logoImageUrlInput = currentBranding.logoImageUrl
        logoEmojiInput = currentBranding.logoEmoji
        gradientEnabledInput = currentBranding.brandingGradientEnabled
        gradientColor1Input = currentBranding.brandingGradientColor1Hex
        gradientColor2Input = currentBranding.brandingGradientColor2Hex
    }

    // Dynamic Live Preview Colors
    val previewTitleColor = try {
        Color(android.graphics.Color.parseColor(titleColorHexInput))
    } catch (e: Exception) {
        Color.White
    }

    val previewGradient = if (gradientEnabledInput) {
        val g1 = try { Color(android.graphics.Color.parseColor(gradientColor1Input)) } catch (e: Exception) { Color(0xFF81C784) }
        val g2 = try { Color(android.graphics.Color.parseColor(gradientColor2Input)) } catch (e: Exception) { Color(0xFF2E7D32) }
        Brush.horizontalGradient(listOf(g1, g2))
    } else {
        Brush.horizontalGradient(listOf(TeaLeafGreen, TeaLeafGreen))
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Dynamic Header Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Palette,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "ডাইনামিক থিম ও রিয়েল-টাইম ব্র্যান্ডিং",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "অ্যাপ আপডেট ছাড়াই এডমিন প্যানেল থেকে লোগো ও হেডার পরিবর্তন করুন",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }

        // --- 1. REAL-TIME LIVE PREVIEW CARD ---
        item {
            Text(
                text = "লাইভ প্রিভিউ (ইউজাররা স্ক্রিনে যেমন দেখবে):",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(14.dp)),
                shape = RoundedCornerShape(14.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column {
                    // Header Bar Preview
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(previewGradient)
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Brand/Logo Part
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (typeInput == "IMAGE") {
                                AsyncImage(
                                    model = logoImageUrlInput,
                                    contentDescription = "3D Logo Preview",
                                    modifier = Modifier
                                        .width(135.dp)
                                        .height(38.dp)
                                        .clip(RoundedCornerShape(8.dp)),
                                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            Brush.linearGradient(
                                                listOf(Color(0xFF81C784), Color(0xFF2E7D32))
                                            )
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = logoEmojiInput,
                                        fontSize = 20.sp
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = titleInput,
                                    fontSize = 19.sp,
                                    fontWeight = FontWeight.Black,
                                    color = previewTitleColor,
                                    fontFamily = FontFamily.SansSerif,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }

                        // Fake Action Buttons to mimic real App
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(RoundedCornerShape(17.dp))
                                    .background(Color.White.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(RoundedCornerShape(17.dp))
                                    .background(Color.White.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = Icons.Default.Chat, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            }
                        }
                    }

                    // Simulated Content
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "📱 এটি লাইভ ডেমো মোবাইল স্ক্রিন",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // --- 2. CONFIGURATION FORM ---
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "ব্র্যান্ড লোগো টাইপ নির্বাচন করুন",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    // Type Radio Buttons (TEXT vs IMAGE)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { typeInput = "TEXT" },
                            colors = CardDefaults.cardColors(
                                containerColor = if (typeInput == "TEXT") MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            ),
                            border = if (typeInput == "TEXT") androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary) else null,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(selected = typeInput == "TEXT", onClick = { typeInput = "TEXT" })
                                Spacer(modifier = Modifier.width(4.dp))
                                Column {
                                    Text("নাম ও ইমোজি থিম", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Text("Text Mode", fontSize = 10.sp, color = Color.Gray)
                                }
                            }
                        }

                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { typeInput = "IMAGE" },
                            colors = CardDefaults.cardColors(
                                containerColor = if (typeInput == "IMAGE") MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            ),
                            border = if (typeInput == "IMAGE") androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary) else null,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(selected = typeInput == "IMAGE", onClick = { typeInput = "IMAGE" })
                                Spacer(modifier = Modifier.width(4.dp))
                                Column {
                                    Text("৩D লোগো ব্যানার", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Text("3D Image Mode", fontSize = 10.sp, color = Color.Gray)
                                }
                            }
                        }
                    }

                    Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                    // Mode conditional inputs
                    if (typeInput == "TEXT") {
                        // TEXT MODE FIELDS
                        Text(
                            text = "টেক্সট ও ফন্ট কালার কনফিগারেশন",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )

                        OutlinedTextField(
                            value = titleInput,
                            onValueChange = { titleInput = it },
                            label = { Text("অ্যাপ ব্র্যান্ড নাম (যেমন: নবচেতনা)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = logoEmojiInput,
                                onValueChange = { logoEmojiInput = it },
                                label = { Text("ব্র্যান্ড ইমোজি (🍀)") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )

                            OutlinedTextField(
                                value = titleColorHexInput,
                                onValueChange = { titleColorHexInput = it },
                                label = { Text("নামের রঙ (Hex Code)") },
                                singleLine = true,
                                modifier = Modifier.weight(2f)
                            )
                        }

                        // Quick title colors
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            val colorsList = listOf(
                                "হোয়াইট ⚪" to "#FFFFFF",
                                "গোল্ডেন 🟡" to "#FFD700",
                                "গ্রিন 🟢" to "#00E676",
                                "স্কাই ব্লু 🔵" to "#80D8FF",
                                "পিঙ্ক 🔴" to "#FF4081"
                            )
                            colorsList.forEach { (label, hex) ->
                                AssistChip(
                                    onClick = { titleColorHexInput = hex },
                                    label = { Text(label, fontSize = 10.sp) }
                                )
                            }
                        }
                    } else {
                        // IMAGE MODE FIELDS
                        Text(
                            text = "৩D লোগো ব্যানার ইমেজ কনফিগারেশন",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )

                        OutlinedTextField(
                            value = logoImageUrlInput,
                            onValueChange = { logoImageUrlInput = it },
                            label = { Text("৩D লোগো ইমেজ ডিরেক্ট লিংক (Direct URL)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        // 3D image presets
                        Text("বিশেষ দিনের লোগো প্রিসেটস (সহজে টেস্ট করতে ক্লিক করুন):", fontSize = 11.sp, color = Color.Gray)
                        
                        val imagePresets = listOf(
                            "🍀 নিয়ন ৩D ক্লোভার (Default)" to "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=400",
                            "🌙 ঈদ মোবারক গোল্ডেন মুন (Eid)" to "https://images.unsplash.com/photo-1544377193-33dcf4d68fb5?w=400",
                            "🌸 দুর্গাপূজা স্পেশাল ডেকোরেশন" to "https://images.unsplash.com/photo-1605379399642-870262d3d051?w=400",
                            "🇧🇩 বিজয় দিবস লাল-সবুজ ৩D" to "https://images.unsplash.com/photo-1595853035070-59a39fe84de3?w=400",
                            "👑 রয়্যাল মেটালিক গোল্ড ক্রাউন" to "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=400"
                        )

                        imagePresets.forEach { (name, url) ->
                            AssistChip(
                                onClick = { logoImageUrlInput = url },
                                label = { Text(name, fontSize = 10.sp) },
                                modifier = Modifier.padding(bottom = 2.dp)
                            )
                        }
                    }

                    Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                    // --- GRADIENT HEADER FIELDS ---
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "টপ হেডার কাস্টম গ্রেডিয়েন্ট থিম",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Switch(
                            checked = gradientEnabledInput,
                            onCheckedChange = { gradientEnabledInput = it }
                        )
                    }

                    if (gradientEnabledInput) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = gradientColor1Input,
                                onValueChange = { gradientColor1Input = it },
                                label = { Text("Color 1 (Left)") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )

                            OutlinedTextField(
                                value = gradientColor2Input,
                                onValueChange = { gradientColor2Input = it },
                                label = { Text("Color 2 (Right)") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        // Gradient presets
                        Text("প্রিমিয়াম থিম প্রিসেটস:", fontSize = 11.sp, color = Color.Gray)
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            val gradientsList = listOf(
                                Triple("🍀 সিগনেচার নবচেতনা সবুজ থিম", "#81C784", "#2E7D32"),
                                Triple("🌅 লাল-সবুজ উৎসব থিম (জাতীয়)", "#E53935", "#2E7D32"),
                                Triple("🌌 মিডনাইট কসমিক লাক্সারি", "#0F2027", "#203A43"),
                                Triple("🍯 গোল্ডেন রয়্যাল সানসেট", "#FFB300", "#E65100"),
                                Triple("🍇 পার্পল অরকিড নিয়ন ভাইবস", "#B388FF", "#6200EA")
                            )
                            gradientsList.forEach { (name, hex1, hex2) ->
                                AssistChip(
                                    onClick = {
                                        gradientColor1Input = hex1
                                        gradientColor2Input = hex2
                                    },
                                    label = { Text(name, fontSize = 10.sp) },
                                    modifier = Modifier.padding(end = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // --- SAVE BRANDING BUTTON ---
                    Button(
                        onClick = {
                            viewModel.updateDynamicBranding(
                                DynamicBrandingConfig(
                                    title = titleInput,
                                    type = typeInput,
                                    titleColorHex = titleColorHexInput,
                                    logoImageUrl = logoImageUrlInput,
                                    logoEmoji = logoEmojiInput,
                                    brandingGradientEnabled = gradientEnabledInput,
                                    brandingGradientColor1Hex = gradientColor1Input,
                                    brandingGradientColor2Hex = gradientColor2Input
                                )
                            )
                            Toast.makeText(context, "🎉 ডাইনামিক ব্র্যান্ডিং ও থিম আপডেট সফল হয়েছে! সকল ইউজারের অ্যাপে এটি ইনস্ট্যান্ট কাজ করবে।", Toast.LENGTH_LONG).show()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = TeaLeafGreen)
                    ) {
                        Icon(imageVector = Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("সেভ করে থিম এক্টিভেট করুন", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}
