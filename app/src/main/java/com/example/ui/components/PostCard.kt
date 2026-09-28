package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import coil.compose.AsyncImage
import com.example.data.model.Post

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PostCard(
    post: Post,
    onLikeClick: () -> Unit,
    onReactionSelect: (String) -> Unit,
    onCommentClick: () -> Unit,
    onShareClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showReactionPopup by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp), // Distinct, modern premium rounded style
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // User Avatar
                AsyncImage(
                    model = post.userAvatar,
                    contentDescription = "User Avatar",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(8.dp))

                // User Info
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = post.userName,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = post.timestamp,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Outlined.Public,
                            contentDescription = "Public",
                            modifier = Modifier.size(12.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(onClick = { /* Visual only */ }) {
                    Icon(
                        imageVector = Icons.Default.MoreHoriz,
                        contentDescription = "More Options",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Post Text Content
            Text(
                text = post.content,
                fontSize = 15.sp,
                lineHeight = 20.sp,
                modifier = Modifier.padding(horizontal = 12.dp, bottom = 10.dp),
                color = MaterialTheme.colorScheme.onSurface
            )

            // Post Media Image
            if (post.imageUrl != null) {
                AsyncImage(
                    model = post.imageUrl,
                    contentDescription = "Post Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 380.dp),
                    contentScale = ContentScale.FillWidth // Fill width
                )
            }

            // Likes & Comments Stats Summary
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left: Reactions Badge List
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (post.likeCount > 0) {
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF1B4D3E)),
                            contentAlignment = Alignment.Center
                        ) {
                            val reactionEmoji = when (post.myReaction) {
                                "Love" -> "❤️"
                                "Inspire" -> "💡"
                                "Respect" -> "🙏"
                                "Agree" -> "🤝"
                                "Wow" -> "😮"
                                else -> "👍"
                            }
                            Text(text = if (post.isLikedByMe) reactionEmoji else "👍", fontSize = 10.sp, color = Color.White)
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (post.isLikedByMe && post.likeCount == 1) "You" else "${post.likeCount}",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Right: Comments & Shares
                Row {
                    if (post.commentCount > 0) {
                        Text(
                            text = "${post.commentCount} comments",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(
                        text = "9 shares",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Divider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 0.5.dp, modifier = Modifier.padding(horizontal = 12.dp))

            // Footer Action Row (Like, Comment, Share)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Like Button with Long Press Support
                val likeTint = if (post.isLikedByMe) {
                    when (post.myReaction) {
                        "Love" -> Color(0xFFE0245E)
                        "Care", "Haha", "Wow", "Sad" -> Color(0xFFF5B800)
                        "Angry" -> Color(0xFFE13F2B)
                        else -> Color(0xFF1B4D3E) // Tea Leaf Green
                    }
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }

                val likeText = if (post.isLikedByMe) {
                    post.myReaction ?: "Like"
                } else {
                    "Like"
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .combinedClickable(
                            onClick = { onLikeClick() },
                            onLongClick = { showReactionPopup = true }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        if (post.isLikedByMe && post.myReaction != null) {
                            val reactionEmoji = when (post.myReaction) {
                                "Love" -> "❤️"
                                "Inspire" -> "💡"
                                "Respect" -> "🙏"
                                "Agree" -> "🤝"
                                "Wow" -> "😮"
                                else -> "👍"
                            }
                            Text(text = reactionEmoji, fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                        } else {
                            Icon(
                                imageVector = Icons.Outlined.ThumbUp,
                                contentDescription = "Like",
                                tint = likeTint,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                        }
                        Text(
                            text = likeText,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = likeTint
                        )
                    }

                    // Floating Reaction Popup overlay
                    if (showReactionPopup) {
                        Popup(
                            alignment = Alignment.TopCenter,
                            onDismissRequest = { showReactionPopup = false },
                            offset = androidx.compose.ui.unit.IntOffset(0, -110)
                        ) {
                            Surface(
                                tonalElevation = 6.dp,
                                shadowElevation = 8.dp,
                                shape = RoundedCornerShape(24.dp),
                                color = MaterialTheme.colorScheme.surface,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val reactions = listOf(
                                        "Like" to "👍",
                                        "Love" to "❤️",
                                        "Inspire" to "💡",
                                        "Respect" to "🙏",
                                        "Agree" to "🤝",
                                        "Wow" to "😮"
                                    )

                                    reactions.forEach { (name, emoji) ->
                                        Text(
                                            text = emoji,
                                            fontSize = 28.sp,
                                            modifier = Modifier
                                                .clickable {
                                                    onReactionSelect(name)
                                                    showReactionPopup = false
                                                }
                                                .padding(2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Comment Button
                IconButtonWithText(
                    icon = Icons.Outlined.ChatBubbleOutline,
                    text = "Comment",
                    onClick = onCommentClick,
                    modifier = Modifier.weight(1f)
                )

                // Share Button
                IconButtonWithText(
                    icon = Icons.Outlined.Share,
                    text = "Share",
                    onClick = onShareClick,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun IconButtonWithText(
    imageVector: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    Box(
        modifier = modifier
            .height(44.dp)
            .clip(RoundedCornerShape(4.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = imageVector,
                contentDescription = text,
                tint = tint,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = text,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = tint
            )
        }
    }
}
