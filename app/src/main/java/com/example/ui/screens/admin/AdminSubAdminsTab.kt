package com.example.ui.screens.admin

import android.widget.Toast
import androidx.compose.animation.*
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
import androidx.compose.material.icons.outlined.*
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.data.model.AdminRole
import com.example.data.model.SubAdminMember
import com.example.ui.viewmodel.PageBookViewModel

@Composable
fun AdminSubAdminsTab(
    viewModel: PageBookViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val subAdmins by viewModel.subAdminMembers.collectAsStateWithLifecycle()

    var showAddAdminDialog by remember { mutableStateOf(false) }
    var selectedMemberForRoleEdit by remember { mutableStateOf<SubAdminMember?>(null) }
    var memberToDelete by remember { mutableStateOf<SubAdminMember?>(null) }

    var newName by remember { mutableStateOf("") }
    var newEmail by remember { mutableStateOf("") }
    var newRole by remember { mutableStateOf(AdminRole.CONTENT_MODERATOR) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(vertical = 14.dp)
    ) {
        // Banner Header
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF37474F)),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "👥 সাব-এডমিন ও টিম পারমিশন",
                                color = Color.White,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "রোল-বেসড এক্সেস কন্ট্রোল (RBAC) ও দায়িত্ব বণ্টন",
                                color = Color(0xFFB0BEC5),
                                fontSize = 12.sp
                            )
                        }
                        Button(
                            onClick = { showAddAdminDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(imageVector = Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "নতুন মেম্বার", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Sub Admins List
        items(subAdmins, key = { it.id }) { member ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            AsyncImage(
                                model = member.avatarUrl,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = member.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = member.email,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = member.role.badgeColor.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = member.role.title,
                                color = member.role.badgeColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(
                                text = "প্রদত্ত পারমিশন:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = member.allowedPermissions.joinToString(" • "),
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "যুক্ত হয়েছেন: ${member.addedDate}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Row {
                            TextButton(onClick = { selectedMemberForRoleEdit = member }) {
                                Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("রোল পরিবর্তন", fontSize = 11.sp)
                            }
                            if (member.role != AdminRole.SUPER_ADMIN) {
                                TextButton(
                                    onClick = { memberToDelete = member },
                                    colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFD32F2F))
                                ) {
                                    Icon(imageVector = Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("অপসারণ", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Permissions Matrix Guide
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "📋 রোল ভিত্তিক পারমিশন চার্ট (Access Matrix)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    val matrix = listOf(
                        Triple("সুপার এডমিন", "সকল ডাটাબેસ, পে-আউট, নোটিশ ও টিম ম্যানেজমেন্ট", Color(0xFFD32F2F)),
                        Triple("কন্টেন্ট মডারেটর", "পোস্ট ও পেজ অডিট, রিপোর্ট ডিসপিউট ও ব্লু ব্যাজ", Color(0xFF1976D2)),
                        Triple("ফিন্যান্স অফিসার", "মনিটাইজেশন অনুমোদন, CPM রেট ও উইথড্রল ক্লিয়ারেন্স", Color(0xFF2E7D32)),
                        Triple("সিকিউরিটি ও কমপ্লায়েন্স", "বট ডিটেকশন, স্ট্রাইক জারি ও সিস্টেম সিকিউরিটি লগ", Color(0xFFE65100))
                    )

                    matrix.forEach { (roleName, perms, color) ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = color.copy(alpha = 0.08f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(text = roleName, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = color)
                                Text(text = perms, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }
                }
            }
        }
    }

    // Add Admin Dialog
    if (showAddAdminDialog) {
        AlertDialog(
            onDismissRequest = { showAddAdminDialog = false },
            title = { Text("নতুন সাব-এডমিন যুক্ত করুন", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newName,
                        onValueChange = { newName = it },
                        label = { Text("নাম") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newEmail,
                        onValueChange = { newEmail = it },
                        label = { Text("অফিসিয়াল ইমেইল") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(text = "নির্ধারিত ভূমিকা (Role):", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    AdminRole.values().forEach { role ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { newRole = role }
                                .padding(vertical = 2.dp)
                        ) {
                            RadioButton(selected = newRole == role, onClick = { newRole = role })
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = role.title, fontSize = 12.sp)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newName.isNotBlank() && newEmail.isNotBlank()) {
                            val perms = when (newRole) {
                                AdminRole.SUPER_ADMIN -> listOf("সকল এক্সেস", "মনিটাইজেশন", "ডাটাবেস কন্ট্রোল")
                                AdminRole.CONTENT_MODERATOR -> listOf("কন্টেন্ট মডারেশন", "ইউজার রিপোর্ট", "ব্লু টিক")
                                AdminRole.FINANCE_OFFICER -> listOf("পে-আউট অনুমোদন", "CPM কনফিগ")
                                AdminRole.SECURITY_AUDITOR -> listOf("স্ট্রাইক জারি", "অ্যান্টি-বট মনিটরিং")
                            }
                            viewModel.addSubAdminMember(newName.trim(), newEmail.trim(), newRole, perms)
                            showAddAdminDialog = false
                            newName = ""
                            newEmail = ""
                            Toast.makeText(context, "টিম মেম্বার সফলভাবে যুক্ত হয়েছে!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                ) {
                    Text("সংযোজন করুন")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddAdminDialog = false }) {
                    Text("বাতিল")
                }
            }
        )
    }

    // Role Edit Dialog
    selectedMemberForRoleEdit?.let { member ->
        var tempRole by remember { mutableStateOf(member.role) }
        AlertDialog(
            onDismissRequest = { selectedMemberForRoleEdit = null },
            title = { Text("${member.name}-এর রোল পরিবর্তন", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    AdminRole.values().forEach { role ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { tempRole = role }
                                .padding(vertical = 4.dp)
                        ) {
                            RadioButton(selected = tempRole == role, onClick = { tempRole = role })
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = role.title, fontSize = 13.sp)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateSubAdminRole(member.id, tempRole)
                        selectedMemberForRoleEdit = null
                        Toast.makeText(context, "রোল আপডেট সম্পন্ন!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                ) {
                    Text("সংরক্ষণ করুন")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedMemberForRoleEdit = null }) {
                    Text("বাতিল")
                }
            }
        )
    }

    // Delete Member Confirmation
    memberToDelete?.let { member ->
        AlertDialog(
            onDismissRequest = { memberToDelete = null },
            title = { Text("এডমিন এক্সেস প্রত্যাহার?", fontWeight = FontWeight.Bold) },
            text = { Text("আপনি কি নিশ্চিতভাবে ${member.name}-এর এডমিন এক্সেস বাতিল করতে চান?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.removeSubAdminMember(member.id)
                        memberToDelete = null
                        Toast.makeText(context, "এক্সেস প্রত্যাহার করা হয়েছে", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                ) {
                    Text("হ্যাঁ, প্রত্যাহার করুন")
                }
            },
            dismissButton = {
                TextButton(onClick = { memberToDelete = null }) {
                    Text("না")
                }
            }
        )
    }
}
