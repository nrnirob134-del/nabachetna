package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PersonAdd
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
import coil.compose.AsyncImage

data class FriendRequest(
    val id: Int,
    val name: String,
    val avatar: String,
    val mutualFriends: Int,
    val timestamp: String,
    var status: String = "PENDING" // "PENDING", "ACCEPTED", "DECLINED"
)

@Composable
fun FriendsScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val requestList = remember {
        mutableStateListOf(
            FriendRequest(1, "Anwar Hossain", "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150", 12, "2d ago"),
            FriendRequest(2, "Runa Laila", "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=150", 4, "3d ago"),
            FriendRequest(3, "Tanvir Rahman", "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=150", 28, "1w ago")
        )
    }

    val suggestionList = remember {
        mutableStateListOf(
            FriendRequest(4, "Sadia Afrin", "https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=150", 8, ""),
            FriendRequest(5, "Mahmudul Hasan", "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150", 15, "")
        )
    }

    LazyColumn(
        modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface),
        contentPadding = PaddingValues(16.dp)
    ) {
        // Friends Screen Header Title
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.People,
                        contentDescription = "Friends",
                        tint = Color(0xFF1B4D3E),
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Friends",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Text(
                    text = "Suggestions",
                    color = Color(0xFF1B4D3E),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable {
                        Toast.makeText(context, "Suggestions updated!", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }

        // Section: Friend Requests Header
        val pendingCount = requestList.count { it.status == "PENDING" }
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Friend Requests ($pendingCount)",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (pendingCount > 0) {
                    Text(
                        text = "See All",
                        fontSize = 14.sp,
                        color = Color(0xFF1B4D3E)
                    )
                }
            }
        }

        // List: Friend Requests Card Items
        if (pendingCount == 0) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                ) {
                    Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                        Text(text = "No pending friend requests", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
                    }
                }
            }
        } else {
            items(requestList) { request ->
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = request.avatar,
                            contentDescription = request.name,
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = request.name,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = request.timestamp,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            if (request.mutualFriends > 0) {
                                Text(
                                    text = "${request.mutualFriends} mutual friends",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(bottom = 6.dp)
                                )
                            }

                            when (request.status) {
                                "PENDING" -> {
                                    Row(modifier = Modifier.fillMaxWidth()) {
                                        Button(
                                            onClick = {
                                                request.status = "ACCEPTED"
                                                Toast.makeText(context, "${request.name}'s request accepted!", Toast.LENGTH_SHORT).show()
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B4D3E)),
                                            modifier = Modifier.weight(1f).height(36.dp),
                                            shape = RoundedCornerShape(6.dp),
                                            contentPadding = PaddingValues(0.dp)
                                        ) {
                                            Text(text = "Confirm", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
                                        }

                                        Spacer(modifier = Modifier.width(8.dp))

                                        Button(
                                            onClick = {
                                                request.status = "DECLINED"
                                                Toast.makeText(context, "Request deleted.", Toast.LENGTH_SHORT).show()
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                            modifier = Modifier.weight(1f).height(36.dp),
                                            shape = RoundedCornerShape(6.dp),
                                            contentPadding = PaddingValues(0.dp)
                                        ) {
                                            Text(text = "Delete", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                                        }
                                    }
                                }
                                "ACCEPTED" -> {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(vertical = 6.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.Check, contentDescription = "Accepted", tint = Color(0xFF41B35D), modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(text = "Request Accepted", color = Color(0xFF41B35D), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                "DECLINED" -> {
                                    Text(text = "Request Deleted", color = MaterialTheme.colorScheme.error, fontSize = 14.sp, modifier = Modifier.padding(vertical = 6.dp))
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section: Suggestions / People You May Know
        item {
            Text(
                text = "People You May Know",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(vertical = 12.dp)
            )
        }

        // List: Suggestions
        items(suggestionList) { suggest ->
            var added by remember { mutableStateOf(false) }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = suggest.avatar,
                    contentDescription = suggest.name,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = suggest.name,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${suggest.mutualFriends} mutual friends",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )

                    Row(modifier = Modifier.fillMaxWidth()) {
                        if (!added) {
                            Button(
                                onClick = {
                                    added = true
                                    Toast.makeText(context, "Friend request sent to ${suggest.name}!", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE7F3FF)),
                                modifier = Modifier.height(34.dp),
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.PersonAdd, contentDescription = "Add Friend", tint = Color(0xFF1B4D3E), modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = "Add Friend", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF1B4D3E))
                                }
                            }
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                                Icon(imageVector = Icons.Default.Check, contentDescription = "Sent", tint = Color(0xFF1B4D3E), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "Request Sent", color = Color(0xFF1B4D3E), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = {
                                suggestionList.remove(suggest)
                                Toast.makeText(context, "Removed from suggestions", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            modifier = Modifier.height(34.dp),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp)
                        ) {
                            Text(text = "Remove", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }
            }
        }
    }
}
