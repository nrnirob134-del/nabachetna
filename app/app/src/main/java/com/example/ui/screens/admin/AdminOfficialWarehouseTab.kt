package com.example.ui.screens.admin

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.data.service.OfficialWarehouseManager
import com.example.ui.screens.Product
import com.example.ui.theme.TeaLeafGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminOfficialWarehouseTab() {
    val context = LocalContext.current
    val officialProducts by OfficialWarehouseManager.officialProducts.collectAsStateWithLifecycle()

    var showAddDialog by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Electronics") }
    var imageUrl by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var isGlobal by remember { mutableStateOf(true) }
    var targetCountry by remember { mutableStateOf("+880") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("অফিশিয়াল গোডাউন ম্যানেজমেন্ট 🏢", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TeaLeafGreen)
                Text("শুধুমাত্র এডমিন প্যানেল থেকে অফিশিয়াল প্রডাক্ট যুক্ত ও রিমুভ করা যাবে", fontSize = 11.sp, color = Color.Gray)
            }
            Button(
                onClick = { showAddDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = TeaLeafGreen)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("নতুন অফিশিয়াল প্রডাক্ট", color = Color.White, fontSize = 12.sp)
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(officialProducts) { product ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, TeaLeafGreen.copy(alpha = 0.3f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = product.imageUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(product.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1)
                            Text("মূল্য: $${product.price} | ক্যাটাগরি: ${product.category}", fontSize = 11.sp, color = TeaLeafGreen)
                            Text(if (product.isGlobal) "🌍 গ্লোবাল প্রডাক্ট" else "📍 দেশ: ${product.targetCountryCode}", fontSize = 10.sp, color = Color.Gray)
                        }
                        IconButton(
                            onClick = {
                                OfficialWarehouseManager.removeOfficialProduct(product.id)
                                Toast.makeText(context, "অফিশিয়াল প্রডাক্ট রিমুভ করা হয়েছে!", Toast.LENGTH_SHORT).show()
                            }
                        ) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("নতুন অফিশিয়াল গোডাউন প্রডাক্ট যোগ করুন", fontWeight = FontWeight.Bold, fontSize = 15.sp) },
            confirmButton = {
                Button(
                    onClick = {
                        if (title.isBlank() || price.isBlank()) {
                            Toast.makeText(context, "অনুগ্রহ করে শিরোনাম ও মূল্য দিন!", Toast.LENGTH_SHORT).show()
                        } else {
                            val p = price.toDoubleOrNull() ?: 10.0
                            val img = if (imageUrl.isBlank()) "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=500" else imageUrl
                            val newProd = Product(
                                id = (System.currentTimeMillis() % 100000).toInt(),
                                title = title,
                                price = p,
                                rating = 5.0f,
                                reviewCount = 1,
                                imageUrl = img,
                                category = category,
                                description = description.ifBlank { "নবচেতনা অফিশিয়াল গুদামজাত প্রডাক্ট।" },
                                isOfficialWarehouse = true,
                                isGlobal = isGlobal,
                                targetCountryCode = if (isGlobal) "ALL" else targetCountry,
                                sellerName = "Nabachetna Official Warehouse 🏢"
                            )
                            OfficialWarehouseManager.addOfficialProduct(newProd)
                            showAddDialog = false
                            title = ""
                            price = ""
                            imageUrl = ""
                            description = ""
                            Toast.makeText(context, "অফিশিয়াল প্রডাক্ট সফলভাবে যুক্ত হয়েছে!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TeaLeafGreen)
                ) {
                    Text("সংরক্ষণ করুন", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("বাতিল", color = Color.Gray)
                }
            },
            text = {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("পণ্যের শিরোনাম", fontSize = 11.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = price,
                            onValueChange = { price = it },
                            label = { Text("মূল্য ($ USD)", fontSize = 11.sp) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = category,
                            onValueChange = { category = it },
                            label = { Text("ক্যাটাগরি (Electronics/Fashion ইত্যাদি)", fontSize = 11.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = imageUrl,
                            onValueChange = { imageUrl = it },
                            label = { Text("ছবির লিংক (Image URL)", fontSize = 11.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(
                                selected = isGlobal,
                                onClick = { isGlobal = true },
                                label = { Text("🌍 গ্লোবাল", fontSize = 10.sp) }
                            )
                            FilterChip(
                                selected = !isGlobal,
                                onClick = { isGlobal = false },
                                label = { Text("📍 দেশভিত্তিক", fontSize = 10.sp) }
                            )
                        }
                    }
                    if (!isGlobal) {
                        item {
                            OutlinedTextField(
                                value = targetCountry,
                                onValueChange = { targetCountry = it },
                                label = { Text("কান্ট্রি কোড (যেমন: +880)", fontSize = 11.sp) },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                        }
                    }
                    item {
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("বিবরণ", fontSize = 11.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2
                        )
                    }
                }
            }
        )
    }
}
