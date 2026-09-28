package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import coil.compose.AsyncImage
import com.example.data.model.Post
import com.example.data.model.User
import com.example.ui.components.PostCard
import com.example.ui.components.VerificationWizardDialog
import com.example.ui.components.WalletRechargeModalDialog
import com.example.ui.components.YellowVerifiedBadge
import com.example.ui.theme.TeaLeafGreen
import com.example.ui.theme.VerifiedYellow
import com.example.ui.viewmodel.PageBookViewModel

@Composable
fun ProfileScreen(
    viewModel: PageBookViewModel,
    currentUser: User?,
    userPosts: List<Post>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showWalletRechargeModal by remember { mutableStateOf(false) }
    var showVerificationWizard by remember { mutableStateOf(false) }

    val avatarPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            viewModel.updateAvatar(uri.toString())
            Toast.makeText(context, "Avatar updated successfully!", Toast.LENGTH_SHORT).show()
        }
    }

    val coverPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            viewModel.updateCover(uri.toString())
            Toast.makeText(context, "Cover photo updated successfully!", Toast.LENGTH_SHORT).show()
        }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
    ) {
        // Profile Header Section (Cover, Avatar, Name)
        item {
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp),
                shape = RoundedCornerShape(0.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Cover Photo & Avatar Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp)
                    ) {
                        // Cover image (click to change)
                        AsyncImage(
                            model = currentUser?.coverUrl ?: "https://images.unsplash.com/photo-1470071459604-3b5ec3a7fe05?w=600",
                            contentDescription = "Cover Image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .clickable {
                                    coverPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                            contentScale = ContentScale.Crop
                        )

                        // Camera icon overlay for Cover
                        Box(
                            modifier = Modifier
                                .padding(8.dp)
                                .align(Alignment.BottomEnd)
                                .offset(y = (-110).dp)
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.5f))
                                .clickable {
                                    coverPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.CameraAlt, contentDescription = "Edit Cover", tint = Color.White, modifier = Modifier.size(18.dp))
                        }

                        // Avatar overlapping Cover (click to change)
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .offset(y = (-10).dp)
                                .size(140.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surface)
                                .border(5.dp, MaterialTheme.colorScheme.surface, CircleShape)
                        ) {
                            AsyncImage(
                                model = currentUser?.avatarUrl ?: "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150",
                                contentDescription = "Profile Picture",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                                    .clickable {
                                        avatarPickerLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    },
                                contentScale = ContentScale.Crop
                            )
                            // Camera icon overlay for Avatar
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
                                    .clickable {
                                        avatarPickerLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = Icons.Default.CameraAlt, contentDescription = "Edit Avatar", tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(16.dp))
                            }
                        }
                    }

                    // Profile Name & Bio
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = currentUser?.name ?: "Anika Rahman",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (currentUser?.isVerified == true) {
                                Spacer(modifier = Modifier.width(6.dp))
                                YellowVerifiedBadge(size = 20.dp)
                            }
                        }
                        if (!currentUser?.bio.isNullOrBlank()) {
                            Text(
                                text = currentUser!!.bio,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                        }

                        if (currentUser?.isVerified == false) {
                            Button(
                                onClick = { showVerificationWizard = true },
                                colors = ButtonDefaults.buttonColors(containerColor = VerifiedYellow),
                                modifier = Modifier.padding(bottom = 12.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("হলুদ ভেরিফাইড ব্যাজ পান", fontWeight = FontWeight.Bold, color = Color.Black)
                            }
                        }

                        // Action buttons row (Add to Story, Edit Profile)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp)
                        ) {
                            Button(
                                onClick = {
                                    Toast.makeText(context, "Choose photo to add story!", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B4D3E)),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(38.dp),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add Story", tint = Color.White, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = "Add to Story", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                                }
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Button(
                                onClick = { showEditProfileDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(38.dp),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Profile", tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = "Edit Profile", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp)
                                }
                            }
                        }

                        // Digital Wallet Balance & Recharge Card (Dynamic Server Gateways)
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 14.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                            border = BorderStroke(1.dp, TeaLeafGreen.copy(alpha = 0.3f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = CircleShape,
                                        color = TeaLeafGreen.copy(alpha = 0.15f),
                                        modifier = Modifier.size(38.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.Default.AccountBalanceWallet,
                                                contentDescription = null,
                                                tint = TeaLeafGreen,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "আমার ওয়ালেট ব্যালেন্স",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = "৳ ${String.format("%.2f", currentUser?.walletBalance ?: 0.0)}",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TeaLeafGreen
                                        )
                                    }
                                }

                                Button(
                                    onClick = { showWalletRechargeModal = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = TeaLeafGreen),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AddCard,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "রিচার্জ / পে",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }

                    Divider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 0.5.dp, modifier = Modifier.padding(horizontal = 16.dp))

                    // Bio Details List (Lives In, From, Followed By)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        DetailRow(icon = Icons.Default.Home, text = "Lives in ${currentUser?.livesIn ?: "Dhaka, Bangladesh"}")
                        DetailRow(icon = Icons.Default.LocationOn, text = "From ${currentUser?.fromLocation ?: "Sylhet, Bangladesh"}")
                        DetailRow(icon = Icons.Default.Favorite, text = "Single")
                        DetailRow(icon = Icons.Default.RssFeed, text = "Followed by ${currentUser?.followedByCount ?: 1420} people")
                    }
                }
            }
        }

        // Section: "Your Posts" header
        item {
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp),
                shape = RoundedCornerShape(0.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Your Posts", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Text(text = "Filters", fontSize = 14.sp, color = Color(0xFF1B4D3E))
                }
            }
        }

        // User's own posts list
        if (userPosts.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                    shape = RoundedCornerShape(0.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text(text = "You haven't posted anything yet.", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
                    }
                }
            }
        } else {
            items(userPosts) { post ->
                PostCard(
                    post = post,
                    onLikeClick = { viewModel.toggleLike(post) },
                    onReactionSelect = { reaction -> viewModel.toggleLike(post, reaction) },
                    onCommentClick = {
                        Toast.makeText(context, "Opening comments from Profile...", Toast.LENGTH_SHORT).show()
                    },
                    onShareClick = {
                        Toast.makeText(context, "Shared to your Feed!", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }

    // Dialog: Edit Profile Information
    if (showEditProfileDialog && currentUser != null) {
        EditProfileDialog(
            user = currentUser,
            onDismiss = { showEditProfileDialog = false },
            onSave = { name, bio, livesIn, fromLocation ->
                viewModel.updateProfile(name, bio, livesIn, fromLocation)
                showEditProfileDialog = false
                Toast.makeText(context, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    WalletRechargeModalDialog(
        isOpen = showWalletRechargeModal,
        onDismiss = { showWalletRechargeModal = false },
        viewModel = viewModel,
        currentUser = currentUser
    )

    VerificationWizardDialog(
        isOpen = showVerificationWizard,
        onDismiss = { showVerificationWizard = false },
        currentUser = currentUser,
        viewModel = viewModel
    )
}

@Composable
fun DetailRow(
    imageVector: androidx.compose.ui.graphics.vector.ImageVector,
    text: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = imageVector, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(10.dp))
        Text(text = text, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
fun EditProfileDialog(
    user: User,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String) -> Unit
) {
    var name by remember { mutableStateOf(user.name) }
    var bio by remember { mutableStateOf(user.bio) }
    var livesIn by remember { mutableStateOf(user.livesIn) }
    var fromLocation by remember { mutableStateOf(user.fromLocation) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(text = "Edit Profile Info", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.padding(bottom = 16.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                )

                OutlinedTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    label = { Text("Bio") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    maxLines = 3
                )

                OutlinedTextField(
                    value = livesIn,
                    onValueChange = { livesIn = it },
                    label = { Text("Current City") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                )

                OutlinedTextField(
                    value = fromLocation,
                    onValueChange = { fromLocation = it },
                    label = { Text("Hometown") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(text = "Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { onSave(name, bio, livesIn, fromLocation) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B4D3E))
                    ) {
                        Text(text = "Save", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
