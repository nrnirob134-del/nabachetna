package com.example.ui.screens.admin

import android.widget.Toast
import androidx.compose.animation.*
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.data.model.FeatureSplashPoster
import com.example.data.service.FeatureSplashPosterService
import com.example.ui.components.DynamicFeaturePosterSplash
import com.example.ui.theme.TeaLeafGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminFeatureSplashPostersTab(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val posters by FeatureSplashPosterService.posters.collectAsStateWithLifecycle()

    var editingPoster by remember { mutableStateOf<FeatureSplashPoster?>(null) }
    var previewPoster by remember { mutableStateOf<FeatureSplashPoster?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // Tab Header
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0D2818)),
            border = BorderStroke(1.dp, TeaLeafGreen.copy(alpha = 0.4f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = TeaLeafGreen.copy(alpha = 0.2f),
                            shape = CircleShape,
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.BurstMode,
                                    contentDescription = null,
                                    tint = TeaLeafGreen,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "গেমিং স্টাইল পিউর পোস্টার কন্ট্রোল",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "কোনো টেক্সট বা বিবরণ ছাড়াই পরিষ্কার ফুল-স্ক্রিন পোস্টার",
                                fontSize = 11.sp,
                                color = Color(0xFFA5D6A7)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Divider(color = Color.White.copy(alpha = 0.15f))
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "সক্রিয় পোস্টার: ${posters.count { it.isEnabled }} / ${posters.size}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                    Text(
                        text = "🎮 ফ্রি ফায়ার / গেম স্টাইল স্প্ল্যাশ",
                        fontSize = 11.sp,
                        color = Color(0xFFFFD54F)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Posters Gallery List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(posters, key = { it.id }) { poster ->
                AdminPosterItemCard(
                    poster = poster,
                    onToggleEnabled = { isEnabled ->
                        FeatureSplashPosterService.togglePosterEnabled(poster.id, isEnabled)
                        Toast.makeText(context, if (isEnabled) "${poster.featureName} পোস্টার সক্রিয়" else "পোস্টার নিষ্ক্রিয়", Toast.LENGTH_SHORT).show()
                    },
                    onEdit = { editingPoster = poster },
                    onPreview = { previewPoster = poster }
                )
            }
        }
    }

    // Edit Poster Modal Dialog (Clean Image URL & Duration Only)
    if (editingPoster != null) {
        EditFeaturePosterDialog(
            poster = editingPoster!!,
            onSave = { updated ->
                FeatureSplashPosterService.updatePoster(updated)
                editingPoster = null
                Toast.makeText(context, "পোস্টার সফলভাবে আপডেট হয়েছে! ✅", Toast.LENGTH_SHORT).show()
            },
            onDismiss = { editingPoster = null }
        )
    }

    // Live Fullscreen Poster Preview (Exact rendering as users see)
    if (previewPoster != null) {
        DynamicFeaturePosterSplash(
            poster = previewPoster!!,
            onFinished = {
                previewPoster = null
                Toast.makeText(context, "প্রিভিউ সম্পন্ন হয়েছে", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@Composable
private fun AdminPosterItemCard(
    poster: FeatureSplashPoster,
    onToggleEnabled: (Boolean) -> Unit,
    onEdit: () -> Unit,
    onPreview: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(
            1.dp,
            if (poster.isEnabled) TeaLeafGreen.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Text(poster.iconEmoji, fontSize = 22.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = poster.featureName,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "সময়কাল: ${poster.durationSeconds} সেকেন্ড",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Switch(
                    checked = poster.isEnabled,
                    onCheckedChange = onToggleEnabled,
                    colors = SwitchDefaults.colors(checkedThumbColor = TeaLeafGreen)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Pure Poster Banner Preview Box (No text/links)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    AsyncImage(
                        model = poster.imageUrl,
                        contentDescription = poster.featureName,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onPreview,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, TeaLeafGreen)
                ) {
                    Icon(imageVector = Icons.Default.Visibility, contentDescription = null, tint = TeaLeafGreen, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("👀 প্রিভিউ দেখুন", fontSize = 12.sp, color = TeaLeafGreen)
                }

                Button(
                    onClick = onEdit,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TeaLeafGreen)
                ) {
                    Icon(imageVector = Icons.Default.Image, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("পোস্টার পরিবর্তন", fontSize = 12.sp)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditFeaturePosterDialog(
    poster: FeatureSplashPoster,
    onSave: (FeatureSplashPoster) -> Unit,
    onDismiss: () -> Unit
) {
    var imageUrl by remember { mutableStateOf(poster.imageUrl) }
    var durationSec by remember { mutableIntStateOf(poster.durationSeconds) }
    var isEnabled by remember { mutableStateOf(poster.isEnabled) }

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
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(poster.iconEmoji, fontSize = 22.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${poster.featureName} পোস্টার ছবি",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        IconButton(onClick = onDismiss) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                        }
                    }
                }

                item {
                    // Image URL Input Only (No description / no links)
                    OutlinedTextField(
                        value = imageUrl,
                        onValueChange = { imageUrl = it },
                        label = { Text("এইচডি পোস্টার ইমেজ লিঙ্ক (Poster Image URL)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = false,
                        maxLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = TeaLeafGreen)
                    )
                }

                item {
                    // Duration Selector (2s or 3s)
                    Text(
                        text = "স্ক্রিনে পোস্টার থাকার সময়কাল (Duration):",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(2, 3, 4).forEach { sec ->
                            FilterChip(
                                selected = durationSec == sec,
                                onClick = { durationSec = sec },
                                label = { Text("$sec সেকেন্ড", fontWeight = if (durationSec == sec) FontWeight.Bold else FontWeight.Normal) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = TeaLeafGreen,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }

                item {
                    // Live Pure Visual Preview
                    Text(
                        text = "পোস্টার প্রিভিউ:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            AsyncImage(
                                model = imageUrl,
                                contentDescription = poster.featureName,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(6.dp))
                    Button(
                        onClick = {
                            onSave(
                                poster.copy(
                                    imageUrl = imageUrl,
                                    durationSeconds = durationSec,
                                    isEnabled = isEnabled
                                )
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = TeaLeafGreen),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("পোস্টার সংরক্ষণ করুন ✅", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
