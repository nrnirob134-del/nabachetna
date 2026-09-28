package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.theme.TeaLeafGreen

// Data structures for our Category Browser
data class CategoryNode(
    val id: String,
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val imageUrl: String,
    val type: String, // "Group", "Market", "Watch", "Article"
    val subcategories: List<String>,
    val description: String,
    val actionText: String = "Explore"
)

data class MockItem(
    val id: Int,
    val title: String,
    val subTitle: String,
    val parentCategoryId: String,
    val imageUrl: String,
    val badge: String? = null,
    val metaInfo: String = ""
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun CategoryBrowserScreen(onBack: () -> Unit) {
    // Intercept system back press to return safely
    BackHandler {
        onBack()
    }

    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var selectedTabType by remember { mutableStateOf("All") } // "All", "Group", "Market", "Watch"
    var selectedCategoryForDetail by remember { mutableStateOf<CategoryNode?>(null) }

    // Categories definition
    val categories = remember {
        listOf(
            CategoryNode(
                id = "cat_tech",
                title = "Technology & IT",
                icon = Icons.Default.Computer,
                imageUrl = "https://images.unsplash.com/photo-1518770660439-4636190af475?w=400",
                type = "Group",
                subcategories = listOf("Coding & AI", "Mobile Phones", "Electronics Hackathon", "Local Freelancing"),
                description = "Connect with tech enthusiasts, learn computer programming, share tips about smartphones, and join hands-on AI projects."
            ),
            CategoryNode(
                id = "cat_agro",
                title = "Agriculture & Farming",
                icon = Icons.Default.Grass,
                imageUrl = "https://images.unsplash.com/photo-1500937386664-56d1dfef3854?w=400",
                type = "Group",
                subcategories = listOf("Roof Farming", "Organic Seeds", "Smart Irrigation", "Local Wholesale Markets"),
                description = "The heart of green growth. Discover roof vegetable farming, discuss organic composting, and get local crop market rates."
            ),
            CategoryNode(
                id = "cat_literature",
                title = "Bengali Poetry & Literature",
                icon = Icons.Default.Book,
                imageUrl = "https://images.unsplash.com/photo-1456513080510-7bf3a84b82f8?w=400",
                type = "Article",
                subcategories = listOf("Modern Poetry", "Short Stories", "Historical Novels", "Sufism & Philosophy"),
                description = "Nurture your soul with deep verses and beautiful short stories shared by writers across the country."
            ),
            CategoryNode(
                id = "cat_handloom",
                title = "Handloom & Crafts",
                icon = Icons.Default.Palette,
                imageUrl = "https://images.unsplash.com/photo-1528164344705-47542687000d?w=400",
                type = "Market",
                subcategories = listOf("Tangail Saree", "Clay Pottery", "Bamboo Weaving", "Jute Products"),
                description = "Support local artisans. Explore handmade jute home decors, pure hand-woven clothes, and classic earthenware."
            ),
            CategoryNode(
                id = "cat_organic",
                title = "Organic Foods & Spices",
                icon = Icons.Default.Restaurant,
                imageUrl = "https://images.unsplash.com/photo-1471943311424-646960669fbc?w=400",
                type = "Market",
                subcategories = listOf("Pure Honey", "Cold-pressed Oils", "Homemade Pickles", "Hill Tracts Spices"),
                description = "Pure healthy living. Shop chemical-free wild honey, natural ghee, and indigenous organic spices."
            ),
            CategoryNode(
                id = "cat_edu",
                title = "Educational Videos",
                icon = Icons.Default.School,
                imageUrl = "https://images.unsplash.com/photo-1427504494785-3a9ca7044f45?w=400",
                type = "Watch",
                subcategories = listOf("HSC Preparation", "Spoken English", "Skill Courses", "Islamic Teachings"),
                description = "Learn something new every day. Browse videos for school, programming tutorials, and critical life skills."
            ),
            CategoryNode(
                id = "cat_travel",
                title = "Travel & Outdoors",
                icon = Icons.Default.Explore,
                imageUrl = "https://images.unsplash.com/photo-1501785888041-af3ef285b470?w=400",
                type = "Watch",
                subcategories = listOf("Cox's Bazar Guide", "Sajek Valley Vlogs", "Himalayan Treks", "Budget Travel Tricks"),
                description = "Wanderlust redefined. Watch thrilling travel stories, local heritage documentation, and safety travel tips."
            ),
            CategoryNode(
                id = "cat_sports",
                title = "Sports & Fitness",
                icon = Icons.Default.SportsCricket,
                imageUrl = "https://images.unsplash.com/photo-1531547273666-85e9bc5c4a3b?w=400",
                type = "Group",
                subcategories = listOf("Cricket Feed", "Football Updates", "Home Workouts", "Yoga & Meditation"),
                description = "Stay energetic! Connect with local cricket fans, coordinate football tournaments, and find quick home workout guides."
            )
        )
    }

    // Mock Items to show inside categories or list overall
    val mockItems = remember {
        listOf(
            MockItem(1, "Roof Gardeners Dhaka", "5.4K Active Members", "cat_agro", "https://images.unsplash.com/photo-1530595467537-0b5996c41f2d?w=200", "POPULAR", "Join Group"),
            MockItem(2, "Kotlin & Jetpack Builders", "12.2K Developers", "cat_tech", "https://images.unsplash.com/photo-1607799279861-4dd421887fb3?w=200", "ACTIVE", "Join Group"),
            MockItem(3, "Pure Jute Table Mats (6 pcs)", "Price: $14.00", "cat_handloom", "https://images.unsplash.com/photo-1606744824163-985d376605aa?w=200", "BEST SELLER", "Add to Cart"),
            MockItem(4, "Premium Sundarban Honey 1kg", "Price: $18.50", "cat_organic", "https://images.unsplash.com/photo-1587049352846-4a222e784d38?w=200", "ORGANIC", "Add to Cart"),
            MockItem(5, "Understanding Artificial Intelligence", "Length: 14:35 mins", "cat_edu", "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=200", "FREE COURSE", "Watch Video"),
            MockItem(6, "Sajek Valley Monsoon Travel Story", "Length: 22:10 mins", "cat_travel", "https://images.unsplash.com/photo-1501785888041-af3ef285b470?w=200", "TRENDING", "Watch Video"),
            MockItem(7, "Chiro Chonchola Poetic Circle", "980 active writers", "cat_literature", "https://images.unsplash.com/photo-1516979187457-637abb4f9353?w=200", "LITERARY", "Explore Posts"),
            MockItem(8, "Local Organic Turmeric Powder", "Price: $4.50", "cat_organic", "https://images.unsplash.com/photo-1615485290382-441e4d049cb5?w=200", "NATIVE", "Add to Cart")
        )
    }

    // Filters
    val filteredCategories = remember(searchQuery, selectedTabType, categories) {
        categories.filter { cat ->
            val matchesSearch = cat.title.contains(searchQuery, ignoreCase = true) ||
                                cat.description.contains(searchQuery, ignoreCase = true) ||
                                cat.subcategories.any { it.contains(searchQuery, ignoreCase = true) }
            val matchesType = (selectedTabType == "All" || cat.type.equals(selectedTabType, ignoreCase = true))
            matchesSearch && matchesType
        }
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(TeaLeafGreen, TeaLeafGreen.copy(alpha = 0.9f))
                        )
                    )
            ) {
                // Header Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }

                    Text(
                        text = "Nabachetna Explore Hub",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )

                    // Quick help info
                    IconButton(
                        onClick = {
                            Toast.makeText(context, "Explore pages, products, circles, and video channels by topic!", Toast.LENGTH_LONG).show()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.HelpOutline,
                            contentDescription = "Help",
                            tint = Color.White
                        )
                    }
                }

                // Interactive search bar
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search categories, subtopics...", fontSize = 14.sp, color = Color.Gray) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .height(46.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = Color.Gray)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = "Clear", tint = Color.Gray)
                            }
                        }
                    },
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF2F5F3)),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Category Type filter chip Row
            item(span = { GridItemSpan(2) }) {
                val types = listOf("All", "Group", "Market", "Watch", "Article")
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(types) { type ->
                        val isSelected = selectedTabType == type
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    if (isSelected) TeaLeafGreen else Color.White
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) TeaLeafGreen else Color.LightGray.copy(alpha = 0.6f),
                                    RoundedCornerShape(16.dp)
                                )
                                .clickable { selectedTabType = type }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = when (type) {
                                    "Group" -> "Circles & Groups"
                                    "Market" -> "Marketplace"
                                    "Watch" -> "Watch Channels"
                                    "Article" -> "Articles & Poetry"
                                    else -> "All Ecosystem"
                                },
                                color = if (isSelected) Color.White else Color.DarkGray,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Beautiful Promo card
            item(span = { GridItemSpan(2) }) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                    border = BoxDefaults.BorderStroke
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(TeaLeafGreen),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Categorized Discovery",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TeaLeafGreen
                            )
                            Text(
                                text = "Easily find verified communities, organic marketplace items, and quality video contents.",
                                fontSize = 11.sp,
                                color = Color.DarkGray
                            )
                        }
                    }
                }
            }

            // Category Cards Grid
            if (filteredCategories.isEmpty()) {
                item(span = { GridItemSpan(2) }) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No categories match your search.",
                            color = Color.Gray,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            } else {
                items(filteredCategories) { category ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedCategoryForDetail = category },
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column {
                            // Category cover image
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp)
                                    .background(Color.LightGray)
                            ) {
                                AsyncImage(
                                    model = category.imageUrl,
                                    contentDescription = category.title,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )

                                // Ecosystem Type Badge
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(6.dp)
                                        .background(
                                            when (category.type) {
                                                "Group" -> Color(0xFF1E88E5)
                                                "Market" -> Color(0xFFFF9900)
                                                "Watch" -> Color(0xFFE53935)
                                                else -> Color(0xFF8E24AA)
                                            },
                                            RoundedCornerShape(4.dp)
                                        )
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = category.type.uppercase(),
                                        color = Color.White,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            }

                            // Info section
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = category.icon,
                                        contentDescription = null,
                                        tint = TeaLeafGreen,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = category.title,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        color = Color.Black
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = category.description,
                                    fontSize = 11.sp,
                                    color = Color.Gray,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    lineHeight = 14.sp
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                // Action link
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    Text(
                                        text = "Explore Topic",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TeaLeafGreen
                                    )
                                    Icon(
                                        imageVector = Icons.Default.ArrowForward,
                                        contentDescription = null,
                                        tint = TeaLeafGreen,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Detail Pop-up Modal when Category is clicked
    selectedCategoryForDetail?.let { cat ->
        AlertDialog(
            onDismissRequest = { selectedCategoryForDetail = null },
            confirmButton = {
                Button(
                    onClick = {
                        Toast.makeText(context, "Navigated successfully into ${cat.title} feeds!", Toast.LENGTH_SHORT).show()
                        selectedCategoryForDetail = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TeaLeafGreen),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Follow Category", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedCategoryForDetail = null }) {
                    Text("Close", color = Color.Gray)
                }
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = cat.icon, contentDescription = null, tint = TeaLeafGreen)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = cat.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Category cover photo
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .clip(RoundedCornerShape(8.dp))
                    ) {
                        AsyncImage(
                            model = cat.imageUrl,
                            contentDescription = cat.title,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = cat.description,
                        fontSize = 13.sp,
                        color = Color.DarkGray
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Subcategories (Tags)
                    Text(
                        text = "Popular Sub-topics:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Wrap FlowRow substitute using LazyRow or a custom column loop
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        cat.subcategories.take(3).forEach { sub ->
                            Box(
                                modifier = Modifier
                                    .background(TeaLeafGreen.copy(alpha = 0.08f), RoundedCornerShape(4.dp))
                                    .border(0.5.dp, TeaLeafGreen.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(text = sub, color = TeaLeafGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Matching active circles/listings
                    Text(
                        text = "Suggested content in this category:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Filter mock items match category
                    val relatedItems = mockItems.filter { it.parentCategoryId == cat.id }
                    if (relatedItems.isEmpty()) {
                        Text("No active channels found. Be the first to create one!", fontSize = 11.sp, color = Color.Gray)
                    } else {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            relatedItems.forEach { item ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFFF9F9F9), RoundedCornerShape(6.dp))
                                        .clickable {
                                            Toast.makeText(context, "${item.metaInfo}: ${item.title}", Toast.LENGTH_SHORT).show()
                                        }
                                        .padding(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    AsyncImage(
                                        model = item.imageUrl,
                                        contentDescription = item.title,
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(RoundedCornerShape(4.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(item.title, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                        Text(item.subTitle, fontSize = 9.sp, color = Color.Gray)
                                    }
                                    Box(
                                        modifier = Modifier
                                            .background(TeaLeafGreen, RoundedCornerShape(12.dp))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(item.metaInfo, color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            },
            shape = RoundedCornerShape(12.dp),
            containerColor = Color.White
        )
    }
}
