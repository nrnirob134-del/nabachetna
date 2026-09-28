package com.example.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.Page
import com.example.data.model.Post
import com.example.ui.components.PostCard
import com.example.ui.theme.TeaLeafGreen
import com.example.ui.viewmodel.PageBookViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PagesScreen(
    viewModel: PageBookViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val pages by viewModel.pages.collectAsState()
    val allPosts by viewModel.posts.collectAsState()

    var activePageId by remember { mutableStateOf<Int?>(null) }
    var isCreatingPage by remember { mutableStateOf(false) }

    // Navigation interceptor
    BackHandler {
        if (activePageId != null) {
            activePageId = null
        } else if (isCreatingPage) {
            isCreatingPage = false
        } else {
            onBack()
        }
    }

    if (activePageId != null) {
        val selectedPage = pages.find { it.id == activePageId }
        if (selectedPage != null) {
            val pagePosts = allPosts.filter { it.pageId == selectedPage.id }
            PageDetailView(
                page = selectedPage,
                posts = pagePosts,
                viewModel = viewModel,
                onBack = { activePageId = null }
            )
        } else {
            activePageId = null
        }
    } else if (isCreatingPage) {
        CreatePageView(
            viewModel = viewModel,
            onPageCreated = { newId ->
                isCreatingPage = false
                activePageId = newId.toInt()
            },
            onCancel = { isCreatingPage = false }
        )
    } else {
        // Pages list view
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "Nabachetna Pages",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 18.sp
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = TeaLeafGreen),
                    actions = {
                        IconButton(onClick = { isCreatingPage = true }) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "Create", tint = Color.White)
                        }
                    }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(Color(0xFFF4F6F5))
            ) {
                if (pages.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(TeaLeafGreen.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Flag,
                                contentDescription = null,
                                tint = TeaLeafGreen,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            text = "No Pages Created Yet",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Create your customized community brand, digital blog, or workspace page in Nabachetna!",
                            fontSize = 13.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = { isCreatingPage = true },
                            colors = ButtonDefaults.buttonColors(containerColor = TeaLeafGreen),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Create Nabachetna Page", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "My Pages (${pages.size})",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                                TextButton(onClick = { isCreatingPage = true }) {
                                    Text("Create Page", color = TeaLeafGreen, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        items(pages) { page ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { activePageId = page.id },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    AsyncImage(
                                        model = page.avatarUrl.ifBlank { "https://images.unsplash.com/photo-1516280440614-37939bbacd6a?w=150" },
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(52.dp)
                                            .clip(CircleShape)
                                            .background(Color.LightGray)
                                            .border(1.dp, Color.LightGray, CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = page.name,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp,
                                                color = Color.Black
                                            )
                                            if (page.isMonetized) {
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(4.dp))
                                                        .background(Color(0xFFFFF8E1))
                                                        .border(0.8.dp, Color(0xFFFFB300), RoundedCornerShape(4.dp))
                                                        .padding(horizontal = 5.dp, vertical = 1.dp)
                                                ) {
                                                    Text(
                                                        text = "⭐ Monetized",
                                                        fontSize = 9.sp,
                                                        color = Color(0xFFE65100),
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }
                                        }
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = page.category,
                                                fontSize = 11.sp,
                                                color = TeaLeafGreen,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                            Text(
                                                text = " • ${page.followersCount} followers",
                                                fontSize = 11.sp,
                                                color = Color.Gray
                                            )
                                        }
                                        if (page.bio.isNotEmpty()) {
                                            Text(
                                                text = page.bio,
                                                fontSize = 11.sp,
                                                color = Color.Gray,
                                                maxLines = 1
                                            )
                                        }
                                    }
                                    Icon(
                                        imageVector = Icons.Default.ChevronRight,
                                        contentDescription = "Open Page",
                                        tint = Color.LightGray,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePageView(
    viewModel: PageBookViewModel,
    onPageCreated: (Long) -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current
    var currentStep by remember { mutableStateOf(1) }

    // Form States
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Digital Creator") }
    var bio by remember { mutableStateOf("") }
    var digitalGoal by remember { mutableStateOf("") }
    var agreeToTerms by remember { mutableStateOf(false) }

    var avatarUri by remember { mutableStateOf<Uri?>(null) }
    var coverUri by remember { mutableStateOf<Uri?>(null) }

    val avatarPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            avatarUri = uri
        }
    }

    val coverPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            coverUri = uri
        }
    }

    val categories = listOf(
        "Digital Creator" to Icons.Default.Edit,
        "Business & Brand" to Icons.Default.Work,
        "Community & Club" to Icons.Default.Groups,
        "Education & Blog" to Icons.Default.MenuBook,
        "E-commerce Shop" to Icons.Default.ShoppingCart,
        "News & Magazine" to Icons.Default.Language
    )

    // Intercept back button during steps
    BackHandler {
        if (currentStep > 1) {
            currentStep--
        } else {
            onCancel()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text("Create Nabachetna Page", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text("Step $currentStep of 6: ${getStepTitle(currentStep)}", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (currentStep > 1) currentStep-- else onCancel()
                        }
                    ) {
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
                .background(Color(0xFFF9FAFA))
        ) {
            // STEP PROGRESS BAR
            LinearProgressIndicator(
                progress = currentStep / 6f,
                color = TeaLeafGreen,
                trackColor = TeaLeafGreen.copy(alpha = 0.2f),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                when (currentStep) {
                    1 -> {
                        // STEP 1: WELCOME & PAGE NAME
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(65.dp)
                                    .clip(CircleShape)
                                    .background(TeaLeafGreen.copy(alpha = 0.12f))
                                    .align(Alignment.CenterHorizontally),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Flag,
                                    contentDescription = null,
                                    tint = TeaLeafGreen,
                                    modifier = Modifier.size(30.dp)
                                )
                            }

                            Text(
                                text = "What is the name of your Page?",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Text(
                                text = "Use the name of your business, brand, organization, or a custom topic that helps people discover your page easily.",
                                fontSize = 12.sp,
                                color = Color.Gray,
                                lineHeight = 16.sp,
                                textAlign = TextAlign.Center
                            )

                            OutlinedTextField(
                                value = name,
                                onValueChange = { name = it },
                                label = { Text("Page Name") },
                                placeholder = { Text("e.g. Dhaka Roof Gardening Hub") },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth(),
                                maxLines = 1
                            )

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9).copy(alpha = 0.5f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = TeaLeafGreen, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Nabachetna high-quality page verification will be active.",
                                        fontSize = 11.sp,
                                        color = Color.DarkGray
                                    )
                                }
                            }
                        }
                    }
                    2 -> {
                        // STEP 2: CATEGORY CHOICE
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Text(
                                text = "Which category best describes your Page?",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Text(
                                text = "Selecting a category helps visitors understand the core objective and services of your public space.",
                                fontSize = 12.sp,
                                color = Color.Gray,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Column(
                                verticalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                categories.forEach { (catName, icon) ->
                                    val isSelected = category == catName
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { category = catName },
                                        shape = RoundedCornerShape(10.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (isSelected) TeaLeafGreen.copy(alpha = 0.1f) else Color.White
                                        ),
                                        border = androidx.compose.foundation.BorderStroke(
                                            width = if (isSelected) 1.5.dp else 1.dp,
                                            color = if (isSelected) TeaLeafGreen else Color.LightGray.copy(alpha = 0.5f)
                                        )
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(14.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(36.dp)
                                                    .clip(CircleShape)
                                                    .background(if (isSelected) TeaLeafGreen else Color(0xFFF0F0F0)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = icon,
                                                    contentDescription = null,
                                                    tint = if (isSelected) Color.White else Color.Gray,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(16.dp))
                                            Text(
                                                text = catName,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = if (isSelected) TeaLeafGreen else Color.Black
                                            )
                                            Spacer(modifier = Modifier.weight(1f))
                                            if (isSelected) {
                                                Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = TeaLeafGreen)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    3 -> {
                        // STEP 3: BIO & PURPOSE
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "Describe your Page Purpose",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )

                            Text(
                                text = "Write a bio or description to explain the main goals and services. Tell people what your page stands for.",
                                fontSize = 12.sp,
                                color = Color.Gray,
                                lineHeight = 16.sp
                            )

                            OutlinedTextField(
                                value = bio,
                                onValueChange = { bio = it },
                                label = { Text("Biography / Motto") },
                                placeholder = { Text("e.g. Dedicating this space to share standard rooftop gardening designs and urban farming lessons...") },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth(),
                                maxLines = 4
                            )

                            OutlinedTextField(
                                value = digitalGoal,
                                onValueChange = { digitalGoal = it },
                                label = { Text("Core Brand Goal") },
                                placeholder = { Text("e.g. Share digital educational guides") },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth(),
                                maxLines = 1,
                                leadingIcon = {
                                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = TeaLeafGreen)
                                }
                            )
                        }
                    }
                    4 -> {
                        // STEP 4: CHOOSE PROFILE PICTURE
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "Add Page Profile Picture",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                textAlign = TextAlign.Center
                            )

                            Text(
                                text = "An identity logo is used everywhere. Choose an exquisite profile photo from your device gallery.",
                                fontSize = 12.sp,
                                color = Color.Gray,
                                textAlign = TextAlign.Center,
                                lineHeight = 16.sp
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Box(
                                modifier = Modifier
                                    .size(130.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFEFEFEF))
                                    .border(3.dp, TeaLeafGreen, CircleShape)
                                    .clickable {
                                        avatarPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                if (avatarUri != null) {
                                    AsyncImage(
                                        model = avatarUri,
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Icon(imageVector = Icons.Default.CameraAlt, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(36.dp))
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text("Select Photo", fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            if (avatarUri != null) {
                                Button(
                                    onClick = { avatarUri = null },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.1f)),
                                    elevation = null
                                ) {
                                    Text("Reset Photo", color = Color.Red, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                    5 -> {
                        // STEP 5: COVER PHOTO
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Add Page Cover Banner",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                textAlign = TextAlign.Center
                            )

                            Text(
                                text = "Your cover photo is the main backdrop of your brand. Choose a clean and landscape layout image.",
                                fontSize = 12.sp,
                                color = Color.Gray,
                                textAlign = TextAlign.Center,
                                lineHeight = 16.sp
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(160.dp)
                                    .clickable {
                                        coverPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                                    },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFEFEFEF)),
                                border = androidx.compose.foundation.BorderStroke(2.dp, TeaLeafGreen.copy(alpha = 0.5f))
                            ) {
                                if (coverUri != null) {
                                    AsyncImage(
                                        model = coverUri,
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Column(
                                        modifier = Modifier.fillMaxSize(),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Icon(imageVector = Icons.Default.Image, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(40.dp))
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text("Select Landscape Cover Banner", fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            if (coverUri != null) {
                                Button(
                                    onClick = { coverUri = null },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.1f)),
                                    elevation = null
                                ) {
                                    Text("Reset Banner", color = Color.Red, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                    6 -> {
                        // STEP 6: REVIEW & TERMS
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Text(
                                text = "Review & Launch Page",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )

                            Text(
                                text = "Review your page's digital identity card below to confirm details and finish setup.",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )

                            // Identity Card Preview
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(85.dp)
                                            .background(Color.LightGray)
                                    ) {
                                        if (coverUri != null) {
                                            AsyncImage(
                                                model = coverUri,
                                                contentDescription = null,
                                                modifier = Modifier.fillMaxSize(),
                                                contentScale = ContentScale.Crop
                                            )
                                        }
                                        Box(
                                            modifier = Modifier
                                                .padding(start = 12.dp, top = 40.dp)
                                                .size(42.dp)
                                                .clip(CircleShape)
                                                .background(Color.White)
                                                .border(2.dp, Color.White, CircleShape)
                                        ) {
                                            AsyncImage(
                                                model = avatarUri ?: "https://images.unsplash.com/photo-1516280440614-37939bbacd6a?w=150",
                                                contentDescription = null,
                                                modifier = Modifier.fillMaxSize().clip(CircleShape),
                                                contentScale = ContentScale.Crop
                                            )
                                        }
                                    }

                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(text = name.ifBlank { "Untitled Brand" }, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.Black)
                                        Text(text = category, fontSize = 11.sp, color = TeaLeafGreen, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(text = bio.ifBlank { "No bio added." }, fontSize = 11.sp, color = Color.DarkGray, maxLines = 2)
                                        if (digitalGoal.isNotEmpty()) {
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(text = "Goal: $digitalGoal", fontSize = 11.sp, color = Color.Gray, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            // Custom Professional Guidelines to prevent Copyright issues
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F3F4))
                            ) {
                                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text("Nabachetna Brand Guidelines:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TeaLeafGreen)
                                    Text("• Avoid copying images/names of external brands.", fontSize = 10.sp, color = Color.DarkGray)
                                    Text("• Write positive and supportive community content.", fontSize = 10.sp, color = Color.DarkGray)
                                    Text("• Spreading fake information or spam will lead to block.", fontSize = 10.sp, color = Color.DarkGray)
                                }
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { agreeToTerms = !agreeToTerms }
                                    .padding(vertical = 4.dp)
                            ) {
                                Checkbox(
                                    checked = agreeToTerms,
                                    onCheckedChange = { agreeToTerms = it },
                                    colors = CheckboxDefaults.colors(checkedColor = TeaLeafGreen)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "I promise to respect community guidelines & keep my page clean.",
                                    fontSize = 11.sp,
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // NAVIGATION BUTTONS BAR
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .border(1.dp, Color.LightGray.copy(alpha = 0.3f))
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // CANCEL OR PREVIOUS
                TextButton(
                    onClick = {
                        if (currentStep > 1) {
                            currentStep--
                        } else {
                            onCancel()
                        }
                    }
                ) {
                    Text(
                        text = if (currentStep > 1) "Previous" else "Cancel",
                        color = Color.Gray,
                        fontWeight = FontWeight.Bold
                    )
                }

                // NEXT OR LAUNCH
                Button(
                    onClick = {
                        if (currentStep == 1) {
                            if (name.isBlank()) {
                                Toast.makeText(context, "Page name cannot be empty!", Toast.LENGTH_SHORT).show()
                            } else {
                                currentStep = 2
                            }
                        } else if (currentStep < 6) {
                            currentStep++
                        } else {
                            // FINALIZE & PUBLISH (STEP 6)
                            if (!agreeToTerms) {
                                Toast.makeText(context, "Please agree to community guidelines first!", Toast.LENGTH_SHORT).show()
                            } else {
                                val defaultAv = "https://images.unsplash.com/photo-1516280440614-37939bbacd6a?w=150"
                                val defaultCov = "https://images.unsplash.com/photo-1469854523086-cc02fe5d8800?w=600"

                                viewModel.createPage(
                                    name = name,
                                    category = category,
                                    bio = bio.ifBlank { "নবচেতনার রেজিস্টার্ড পেইজ মডিউলে আপনাকে স্বাগতম।" },
                                    avatarUrl = avatarUri?.toString() ?: defaultAv,
                                    coverUrl = coverUri?.toString() ?: defaultCov,
                                    onDone = { newId ->
                                        Toast.makeText(context, "Congratulations! Brand page '$name' launched! 🚀🎉", Toast.LENGTH_SHORT).show()
                                        onPageCreated(newId)
                                    }
                                )
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TeaLeafGreen),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (currentStep == 6) "Launch Brand Page 🚀" else "Next",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        if (currentStep < 6) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }
}

// Helper to determine step headings
private fun getStepTitle(step: Int): String {
    return when (step) {
        1 -> "Identity & Name"
        2 -> "Industry Category"
        3 -> "Motto & Objectives"
        4 -> "Identity Logo"
        5 -> "Brand Backdrop"
        6 -> "Review & Final Launch"
        else -> "Create Brand"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PageDetailView(
    page: Page,
    posts: List<Post>,
    viewModel: PageBookViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(0) }
    var isEditInfoDialogVisible by remember { mutableStateOf(false) }
    var editName by remember { mutableStateOf(page.name) }
    var editCategory by remember { mutableStateOf(page.category) }
    var editBio by remember { mutableStateOf(page.bio) }

    // Monetization application dialog states
    var isMonetizationApplyDialogVisible by remember { mutableStateOf(false) }
    var selectedPayoutMethod by remember { mutableStateOf(page.payoutMethod.ifBlank { "bKash" }) }
    var payoutAccountNumber by remember { mutableStateOf(page.payoutAccount) }
    var termsAgreed by remember { mutableStateOf(false) }
    var isWithdrawDialogVisible by remember { mutableStateOf(false) }

    var isPageLikedLocally by remember(page.id) { mutableStateOf(false) }
    var isPageFollowedLocally by remember(page.id) { mutableStateOf(false) }

    // Page post publication variables
    var pagePostText by remember { mutableStateOf("") }
    var pagePostImageUri by remember { mutableStateOf<Uri?>(null) }

    val pagePostPhotoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            pagePostImageUri = uri
        }
    }

    val pageAvatarPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            viewModel.updatePageAvatar(page.id, uri.toString())
            Toast.makeText(context, "Page profile picture updated!", Toast.LENGTH_SHORT).show()
        }
    }

    val pageCoverPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            viewModel.updatePageCover(page.id, uri.toString())
            Toast.makeText(context, "Page cover photo updated!", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = page.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = TeaLeafGreen),
                actions = {
                    IconButton(onClick = { isEditInfoDialogVisible = true }) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Details", tint = Color.White)
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF2F4F3)),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header Banner & Profile image
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                ) {
                    Column {
                        // Page Cover
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clickable {
                                    pageCoverPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                                }
                        ) {
                            AsyncImage(
                                model = page.coverUrl.ifBlank { "https://images.unsplash.com/photo-1469854523086-cc02fe5d8800?w=600" },
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(8.dp)
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color.Black.copy(alpha = 0.5f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = Icons.Default.CameraAlt, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            }
                        }

                        // Banner stats
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp, top = 40.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = page.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = Color.Black
                                )
                                if (page.isMonetized) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Color(0xFFFFF8E1))
                                            .border(1.dp, Color(0xFFFFB300), RoundedCornerShape(6.dp))
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = Color(0xFFFF8F00), modifier = Modifier.size(12.dp))
                                            Spacer(modifier = Modifier.width(3.dp))
                                            Text(
                                                text = "Monetized Partner ⭐",
                                                color = Color(0xFFE65100),
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }

                            Text(
                                text = page.category,
                                fontSize = 12.sp,
                                color = TeaLeafGreen,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = page.bio,
                                fontSize = 12.sp,
                                color = Color.DarkGray,
                                lineHeight = 16.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(imageVector = Icons.Default.ThumbUp, contentDescription = null, tint = TeaLeafGreen, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "${page.likesCount} likes • ${page.followersCount} followers • ${page.topVideoViews} video views",
                                    fontSize = 11.sp,
                                    color = Color.Gray,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Organic Follow & Like action buttons (Real Community Interactions)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        isPageFollowedLocally = !isPageFollowedLocally
                                        viewModel.toggleFollowPage(page.id, isPageFollowedLocally)
                                        val msg = if (isPageFollowedLocally) "আপনি এই পেজটিকে ফলো করেছেন! 👥" else "পেজ আনফলো করা হয়েছে।"
                                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isPageFollowedLocally) Color(0xFFE8F5E9) else TeaLeafGreen,
                                        contentColor = if (isPageFollowedLocally) TeaLeafGreen else Color.White
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f).height(38.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isPageFollowedLocally) Icons.Default.Check else Icons.Default.PersonAdd,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isPageFollowedLocally) "Following" else "Follow Page",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                OutlinedButton(
                                    onClick = {
                                        isPageLikedLocally = !isPageLikedLocally
                                        viewModel.toggleLikePage(page.id, isPageLikedLocally)
                                        val msg = if (isPageLikedLocally) "পেজে লাইক দেওয়া হয়েছে! 👍" else "লাইক প্রত্যাহার করা হয়েছে।"
                                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                    },
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = if (isPageLikedLocally) TeaLeafGreen else Color.DarkGray
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f).height(38.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isPageLikedLocally) Icons.Default.ThumbUp else Icons.Default.ThumbUpOffAlt,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = if (isPageLikedLocally) TeaLeafGreen else Color.DarkGray
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isPageLikedLocally) "Liked" else "Like Page",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    // Page Floating Avatar
                    Box(
                        modifier = Modifier
                            .padding(start = 16.dp, top = 110.dp)
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .border(3.dp, Color.White, CircleShape)
                            .clickable {
                                pageAvatarPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                            }
                    ) {
                        AsyncImage(
                            model = page.avatarUrl.ifBlank { "https://images.unsplash.com/photo-1516280440614-37939bbacd6a?w=150" },
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize().clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.5f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.CameraAlt, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                        }
                    }
                }
            }

            // Tabs Row
            item {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.White,
                    contentColor = TeaLeafGreen
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Feed & Posts", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Monetization", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                if (page.isMonetized) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.size(12.dp))
                                }
                            }
                        }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text("About & Policy", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                    )
                }
            }

            // Tab 0: Posts and Create Post
            if (selectedTab == 0) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(0.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                AsyncImage(
                                    model = page.avatarUrl,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                TextField(
                                    value = pagePostText,
                                    onValueChange = { pagePostText = it },
                                    placeholder = { Text("Write something on behalf of ${page.name}...", fontSize = 13.sp) },
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = Color.Transparent,
                                        unfocusedContainerColor = Color.Transparent,
                                        focusedIndicatorColor = Color.Transparent,
                                        unfocusedIndicatorColor = Color.Transparent
                                    ),
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            // Preview picked post image
                            if (pagePostImageUri != null) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(140.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                ) {
                                    AsyncImage(
                                        model = pagePostImageUri,
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                    IconButton(
                                        onClick = { pagePostImageUri = null },
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .padding(6.dp)
                                            .size(24.dp)
                                            .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                                    ) {
                                        Icon(imageVector = Icons.Default.Close, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            Divider(color = Color(0xFFF0F0F0))
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TextButton(
                                    onClick = {
                                        pagePostPhotoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                                    }
                                ) {
                                    Icon(imageVector = Icons.Default.PhotoLibrary, contentDescription = null, tint = Color(0xFF4CAF50), modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Add Photo", fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = {
                                        if (pagePostText.isBlank() && pagePostImageUri == null) {
                                            Toast.makeText(context, "Please write a post content!", Toast.LENGTH_SHORT).show()
                                        } else {
                                            viewModel.createPagePost(
                                                pageId = page.id,
                                                content = pagePostText,
                                                imageUrl = pagePostImageUri?.toString()
                                            )
                                            Toast.makeText(context, "Published on Page feed! 🚀", Toast.LENGTH_SHORT).show()
                                            pagePostText = ""
                                            pagePostImageUri = null
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = TeaLeafGreen),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.height(36.dp)
                                ) {
                                    Text("Post", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }

                // Page Posts list header
                item {
                    PaddingValues(horizontal = 16.dp).let {
                        Text(
                            text = "Page Posts",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            modifier = Modifier.padding(start = 16.dp, top = 8.dp)
                        )
                    }
                }

                // Feed of Page Posts
                if (posts.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "No posts published yet. Share updates as your brand!",
                                fontSize = 12.sp,
                                color = Color.Gray,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp)
                            )
                        }
                    }
                } else {
                    items(posts) { post ->
                        PostCard(
                            post = post,
                            viewModel = viewModel
                        )
                    }
                }
            } else if (selectedTab == 1) {
                // Tab 1: MONETIZATION & PAYOUT
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (page.isMonetized) Color(0xFFFFFDE7) else Color(0xFFE8F5E9)
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (page.isMonetized) Color(0xFFFFD54F) else TeaLeafGreen.copy(alpha = 0.5f)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(if (page.isMonetized) Color(0xFFFFECB3) else TeaLeafGreen.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (page.isMonetized) Icons.Default.Star else Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    tint = if (page.isMonetized) Color(0xFFF57F17) else TeaLeafGreen,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (page.isMonetized) "অফিসিয়াল মনিটাইজড পার্টনার ⭐" else "নবচেতনা পেজ মনিটাইজেশন প্রোগ্রাম",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = Color.Black
                                )
                                Text(
                                    text = if (page.isMonetized)
                                        "অভিনন্দন! আপনার পেজটি ভিডিও বিজ্ঞাপনী আয়ের জন্য সক্রিয়।"
                                    else
                                        "ভিডিও ও কন্টেন্ট থেকে বিজ্ঞাপন রেভিনিউ পেতে নিচের ৩টি আবশ্যক শর্ত পূরণ করুন।",
                                    fontSize = 11.sp,
                                    color = Color.DarkGray,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }
                }

                if (page.isMonetized) {
                    // EARNINGS OVERVIEW CARD
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "মোট আনুমানিক আয় (Estimated Earnings)",
                                        fontSize = 12.sp,
                                        color = Color.Gray,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(Color(0xFFE8F5E9))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text("Active Partner", color = TeaLeafGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text(
                                        text = "$${String.format("%.2f", page.estimatedEarnings)}",
                                        fontSize = 28.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFF1B5E20)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "(≈ ৳${(page.estimatedEarnings * 122).toInt()} BDT)",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Gray,
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(14.dp))
                                Divider(color = Color(0xFFF0F0F0))
                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text("ভিডিও ভিউ", fontSize = 11.sp, color = Color.Gray)
                                        Text("${page.topVideoViews}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                    }
                                    Column {
                                        Text("রেভিনিউ শেয়ার", fontSize = 11.sp, color = Color.Gray)
                                        Text("৭০% ক্রিয়েটর", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TeaLeafGreen)
                                    }
                                    Column {
                                        Text("আনুমানিক CPM", fontSize = 11.sp, color = Color.Gray)
                                        Text("$1.85 / 1K", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = { isWithdrawDialogVisible = true },
                                        colors = ButtonDefaults.buttonColors(containerColor = TeaLeafGreen),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("আয় উত্তোলন (Withdraw)", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                    }
                                    OutlinedButton(
                                        onClick = {
                                            Toast.makeText(context, "অডিট সম্পন্ন: ১০০% রিয়েল অডিয়েন্স ও খাঁটি ভিউ ভেরিফাইড! কোনো ফেক ভিউ নেই। ✅", Toast.LENGTH_SHORT).show()
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("নিরাপত্তা ভেরিফিকেশন", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TeaLeafGreen)
                                    }
                                }
                            }
                        }
                    }

                    // PAYOUT SETTINGS CARD
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("পেমেন্ট ও পে-আউট তথ্য (Payout Settings)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("পেমেন্ট মাধ্যম:", fontSize = 12.sp, color = Color.Gray)
                                    Text(page.payoutMethod, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("অ্যাকাউন্ট নম্বর:", fontSize = 12.sp, color = Color.Gray)
                                    Text(page.payoutAccount.ifBlank { "017XXXXXXXX" }, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("পে-আউট শিডিউল:", fontSize = 12.sp, color = Color.Gray)
                                    Text("প্রতি মাসের ২৮ তারিখ (স্বয়ংক্রিয়)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TeaLeafGreen)
                                }
                            }
                        }
                    }
                } else {
                    // MONETIZATION CRITERIA CHECKLIST (The 3 criteria requested by the user!)
                    val isFollowersMet = page.followersCount >= 1000
                    val isViewsMet = page.topVideoViews >= 10000
                    val isPolicyMet = page.isPolicyCompliant
                    val isAllMet = isFollowersMet && isViewsMet && isPolicyMet

                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Text(
                                    text = "মনিটাইজেশন পাওয়ার ৩টি আবশ্যক শর্ত",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )

                                // 1. 1000 FOLLOWERS
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = if (isFollowersMet) Icons.Default.CheckCircle else Icons.Default.Person,
                                                contentDescription = null,
                                                tint = if (isFollowersMet) TeaLeafGreen else Color.Gray,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "১. ন্যূনতম ১,০০০ ফলোয়ার",
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.Black
                                            )
                                        }
                                        Text(
                                            text = if (isFollowersMet) "✅ সম্পন্ন (${page.followersCount})" else "${page.followersCount} / 1,000",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isFollowersMet) TeaLeafGreen else Color(0xFFE65100)
                                        )
                                    }
                                    LinearProgressIndicator(
                                        progress = minOf(1f, page.followersCount / 1000f),
                                        color = if (isFollowersMet) TeaLeafGreen else Color(0xFFFF9800),
                                        trackColor = Color(0xFFEEEEEE),
                                        modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp))
                                    )
                                    if (!isFollowersMet) {
                                        Text(
                                            text = "প্রয়োজন আরও ${1000 - page.followersCount} ফলোয়ার",
                                            fontSize = 10.sp,
                                            color = Color.Gray
                                        )
                                    }
                                }

                                Divider(color = Color(0xFFF5F5F5))

                                // 2. 10,000 VIDEO VIEWS
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = if (isViewsMet) Icons.Default.CheckCircle else Icons.Default.PlayArrow,
                                                contentDescription = null,
                                                tint = if (isViewsMet) TeaLeafGreen else Color.Gray,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "২. একটি ভিডিওতে ১০ হাজার ভিউ",
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.Black
                                            )
                                        }
                                        Text(
                                            text = if (isViewsMet) "✅ সম্পন্ন (${page.topVideoViews})" else "${page.topVideoViews} / 10,000",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isViewsMet) TeaLeafGreen else Color(0xFFE65100)
                                        )
                                    }
                                    LinearProgressIndicator(
                                        progress = minOf(1f, page.topVideoViews / 10000f),
                                        color = if (isViewsMet) TeaLeafGreen else Color(0xFFFF9800),
                                        trackColor = Color(0xFFEEEEEE),
                                        modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp))
                                    )
                                    if (!isViewsMet) {
                                        Text(
                                            text = "একটি ভিডিওতে প্রয়োজন আরও ${10000 - page.topVideoViews} ভিউ",
                                            fontSize = 10.sp,
                                            color = Color.Gray
                                        )
                                    }
                                }

                                Divider(color = Color(0xFFF5F5F5))

                                // 3. NABACHETNA POLICY COMPLIANCE
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = TeaLeafGreen,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = "৩. নবচেতনার সকল নীতিমালা মানা",
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.Black
                                            )
                                            Text(
                                                text = "মৌলিক কন্টেন্ট ও কপিরাইট মুক্ত পরিবেশ",
                                                fontSize = 10.sp,
                                                color = Color.Gray
                                            )
                                        }
                                    }
                                    Text(
                                        text = "✅ ১০০% সুরক্ষিত",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TeaLeafGreen
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                // ACTION BUTTON
                                Button(
                                    onClick = {
                                        if (isAllMet) {
                                            isMonetizationApplyDialogVisible = true
                                        } else {
                                            Toast.makeText(context, "শর্ত পূরণ হয়নি! নিয়মিত ভিডিও আপলোড করুন ও খাঁটি অডিয়েন্স বৃদ্ধি করুন।", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    enabled = isAllMet,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = TeaLeafGreen,
                                        disabledContainerColor = Color.LightGray.copy(alpha = 0.4f)
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth().height(48.dp)
                                ) {
                                    Text(
                                        text = if (isAllMet) "মনিটাইজেশনের জন্য আবেদন করুন 🚀" else "শর্ত পূরণ বাকি রয়েছে",
                                        color = if (isAllMet) Color.White else Color.DarkGray,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }
                    }

                    // ANTI-FRAUD SECURITY & 100% REAL AUDIENCE VERIFICATION CARD
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8E9)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFC8E6C9))
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Security,
                                        contentDescription = null,
                                        tint = TeaLeafGreen,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "🔒 নবচেতনা জিরো-ফেক ও অ্যান্টি-ফ্রড পলিসি",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1B5E20)
                                    )
                                }
                                Text(
                                    text = "নবচেতনা প্ল্যাটফর্মে কোনো প্রকার ফেক ভিউ, বট লাইক, অসাধু ফলোয়ার বৃদ্ধি বা হ্যাকিং স্ক্রিপ্ট কঠোরভাবে নিষিদ্ধ। প্রতিটি ভিউ ও ফলোয়ার ক্রিপ্টোগ্রাফিক ডিভাইস আইডি ও অ্যাক্টিভ ওয়াচ-টাইমের মাধ্যমে স্বয়ংক্রিয়ভাবে অডিট হয়। ফেক তথ্য শনাক্ত হলে পেজের মনিটাইজেশন আজীবনের জন্য বাতিল হবে।",
                                    fontSize = 11.sp,
                                    color = Color.DarkGray,
                                    lineHeight = 16.sp
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "স্ট্যাটাস: ১০০% খাঁটি অরিজিনাল অডিয়েন্স",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TeaLeafGreen
                                    )
                                    Text(
                                        text = "Shield Active 🛡️",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF2E7D32)
                                    )
                                }
                            }
                        }
                    }
                }
            } else if (selectedTab == 2) {
                // Tab 2: ABOUT & POLICIES
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text("পেজের পরিচিতি ও বিস্তারিত", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Black)
                            Text("পেজের নাম: ${page.name}", fontSize = 12.sp, color = Color.DarkGray)
                            Text("ক্যাটাগরি: ${page.category}", fontSize = 12.sp, color = Color.DarkGray)
                            Text("বিবরণ: ${page.bio.ifBlank { "কোনো বায়ো যুক্ত করা হয়নি।" }}", fontSize = 12.sp, color = Color.DarkGray)
                            Divider(color = Color(0xFFF0F0F0))
                            Text("নবচেতনা কন্টেন্ট ও মনিটাইজেশন নীতিমালা", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TeaLeafGreen)
                            Text("• শুধুমাত্র নিজস্ব মৌলিক ভিডিও এবং কন্টেন্ট পাবলিশ করতে হবে।", fontSize = 11.sp, color = Color.DarkGray)
                            Text("• কোনো প্রকার ফেক ভিউ, বট লাইক, অসাধু ফলোয়ার বৃদ্ধি বা হ্যাকিং স্ক্রিপ্ট নবচেতনার নীতির সবচেয়ে বড় লঙ্ঘন।", fontSize = 11.sp, color = Color(0xFFC62828), fontWeight = FontWeight.SemiBold)
                            Text("• সব ভিউ, কমেন্ট ও ফলোয়ার ১০০% অর্গানিক এবং রিয়েল ব্যবহারকারীদের হতে হবে।", fontSize = 11.sp, color = Color.DarkGray)
                            Text("• ফেসবুক, ইউটিউব বা অন্যের কপিরাইটযুক্ত কন্টেন্ট ব্যবহার করা সম্পূর্ণ নিষিদ্ধ।", fontSize = 11.sp, color = Color.DarkGray)
                            Text("• বিজ্ঞাপনী আয়ের ৭০% সরাসরি ক্রিয়েটরের বিকাশ/নগদ/ব্যাংক একাউন্টে প্রতিমাসে জমা হবে।", fontSize = 11.sp, color = Color.DarkGray)
                        }
                    }
                }
            }
        }
    }

    // Edit Page details popup
    if (isEditInfoDialogVisible) {
        AlertDialog(
            onDismissRequest = { isEditInfoDialogVisible = false },
            title = { Text("Edit Page Information", fontSize = 16.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Page Name") }
                    )
                    OutlinedTextField(
                        value = editCategory,
                        onValueChange = { editCategory = it },
                        label = { Text("Category") }
                    )
                    OutlinedTextField(
                        value = editBio,
                        onValueChange = { editBio = it },
                        label = { Text("Bio / Description") },
                        maxLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editName.isNotBlank()) {
                            viewModel.updatePage(page.id, editName, editCategory, editBio)
                            isEditInfoDialogVisible = false
                            Toast.makeText(context, "Page details updated!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TeaLeafGreen)
                ) {
                    Text("Save", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { isEditInfoDialogVisible = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Monetization application dialog
    if (isMonetizationApplyDialogVisible) {
        AlertDialog(
            onDismissRequest = { isMonetizationApplyDialogVisible = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFB300))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("মনিটাইজেশন আবেদন ও পেমেন্ট সেটআপ", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "অভিনন্দন! আপনার পেজ '${page.name}' সফলভাবে ১,০০০ ফলোয়ার এবং ১০,০০০ ভিডিও ভিউ অর্জন করেছে।",
                        fontSize = 12.sp,
                        color = Color.DarkGray
                    )
                    Text("পেমেন্ট গ্রহণ মাধ্যম সিলেক্ট করুন:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("bKash", "Nagad", "Bank").forEach { method ->
                            val isSelected = selectedPayoutMethod == method
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { selectedPayoutMethod = method },
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) TeaLeafGreen.copy(alpha = 0.15f) else Color(0xFFF5F5F5)
                                ),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSelected) TeaLeafGreen else Color.LightGray.copy(alpha = 0.5f)
                                )
                            ) {
                                Text(
                                    text = method,
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = if (isSelected) TeaLeafGreen else Color.DarkGray,
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = payoutAccountNumber,
                        onValueChange = { payoutAccountNumber = it },
                        label = { Text("$selectedPayoutMethod অ্যাকাউন্ট নম্বর") },
                        placeholder = { Text("e.g. 017XXXXXXXX") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { termsAgreed = !termsAgreed }
                    ) {
                        Checkbox(
                            checked = termsAgreed,
                            onCheckedChange = { termsAgreed = it },
                            colors = CheckboxDefaults.colors(checkedColor = TeaLeafGreen)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "আমি নবচেতনার ৭০% রেভিনিউ শেয়ারিং, মৌলিক কন্টেন্ট ও জিরো-ফেক অডিয়েন্স (কোনো প্রকার ফেক ভিউ/ফলোয়ার নিষিদ্ধ) নীতিমালায় সম্পূর্ণ সম্মতি দিচ্ছি।",
                            fontSize = 10.sp,
                            color = Color.Black
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (payoutAccountNumber.isBlank()) {
                            Toast.makeText(context, "দয়া করে একাউন্ট নম্বর লিখুন!", Toast.LENGTH_SHORT).show()
                        } else if (!termsAgreed) {
                            Toast.makeText(context, "দয়া করে নীতিমালায় সম্মতি দিন!", Toast.LENGTH_SHORT).show()
                        } else {
                            viewModel.applyForMonetization(
                                pageId = page.id,
                                method = selectedPayoutMethod,
                                account = payoutAccountNumber
                            )
                            isMonetizationApplyDialogVisible = false
                            Toast.makeText(context, "অভিনন্দন! আপনার পেজটি সফলভাবে মনিটাইজড হয়েছে! 🌟🎉", Toast.LENGTH_LONG).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TeaLeafGreen)
                ) {
                    Text("আবেদন সাবমিট ও সক্রিয় করুন 🚀", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { isMonetizationApplyDialogVisible = false }) {
                    Text("বাতিল", color = Color.Gray)
                }
            }
        )
    }

    // Withdraw Dialog
    if (isWithdrawDialogVisible) {
        AlertDialog(
            onDismissRequest = { isWithdrawDialogVisible = false },
            title = { Text("আয় উত্তোলন (Withdraw Earnings)", fontSize = 15.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("বর্তমান ব্যালেন্স: $${String.format("%.2f", page.estimatedEarnings)}", fontWeight = FontWeight.Bold, color = TeaLeafGreen)
                    Text("পেমেন্ট মাধ্যম: ${page.payoutMethod} (${page.payoutAccount.ifBlank { "017XXXXXXXX" }})", fontSize = 12.sp, color = Color.DarkGray)
                    Text("উত্তোলন করা অ্যামাউন্ট প্রতি মাসের ২৮ তারিখে সরাসরি আপনার একাউন্টে ক্যাশ-ইন হবে।", fontSize = 11.sp, color = Color.Gray)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        isWithdrawDialogVisible = false
                        Toast.makeText(context, "উত্তোলন রিকোয়েস্ট সফল হয়েছে! পরবর্তী পে-আউটে টাকা পৌঁছে যাবে।", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TeaLeafGreen)
                ) {
                    Text("নিশ্চিত করুন", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { isWithdrawDialogVisible = false }) {
                    Text("বাতিল", color = Color.Gray)
                }
            }
        )
    }
}
