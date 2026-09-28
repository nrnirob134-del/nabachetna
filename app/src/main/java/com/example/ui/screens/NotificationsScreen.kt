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
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.Notification
import com.example.ui.viewmodel.PageBookViewModel

@Composable
fun NotificationsScreen(
    viewModel: PageBookViewModel,
    notifications: List<Notification>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    LazyColumn(
        modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        // Notifications Header Row
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.NotificationsActive,
                        contentDescription = "Notifications",
                        tint = Color(0xFF1B4D3E),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Notifications",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                if (notifications.isNotEmpty()) {
                    TextButton(
                        onClick = {
                            viewModel.clearNotifications()
                            Toast.makeText(context, "All notifications cleared!", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Text(text = "Clear All", color = Color(0xFF1B4D3E), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
            Divider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 0.5.dp)
        }

        // Empty State Layout
        if (notifications.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 100.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = "🔔", fontSize = 64.sp, modifier = Modifier.padding(bottom = 16.dp))
                    Text(
                        text = "You're all caught up!",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "New notifications will appear here.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        } else {
            // Notifications List
            items(notifications) { notification ->
                val cardColor = if (notification.isRead) {
                    MaterialTheme.colorScheme.surface
                } else {
                    Color(0xFFE7F3FF).copy(alpha = 0.5f) // Subtle blue highlight for unread
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(cardColor)
                        .clickable {
                            // Visual toggle read state
                            // In real database we can run a mark as read DAO command
                            Toast.makeText(context, "Opened notification details", Toast.LENGTH_SHORT).show()
                        }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Sender avatar
                    AsyncImage(
                        model = notification.userAvatar,
                        contentDescription = "Notification Sender Avatar",
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    // Notification description rich text formatting
                    Column(modifier = Modifier.weight(1f)) {
                        val annotatedText = buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)) {
                                append(notification.userName)
                            }
                            append(" ")
                            withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onSurface)) {
                                append(notification.action)
                            }
                        }

                        Text(
                            text = annotatedText,
                            fontSize = 14.sp,
                            lineHeight = 18.sp
                        )

                        Text(
                            text = notification.timestamp,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    // Blue Dot indicator for unread notifications
                    if (!notification.isRead) {
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 6.dp)
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF1B4D3E))
                        )
                    }
                }
            }
        }
    }
}
