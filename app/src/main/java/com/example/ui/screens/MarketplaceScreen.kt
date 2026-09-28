package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.data.model.EscrowOrder
import com.example.data.model.EscrowOrderStatus
import androidx.compose.foundation.lazy.grid.itemsIndexed
import com.example.data.service.AdManagerRegistry
import com.example.data.service.OfficialWarehouseManager
import com.example.ui.components.NativeAdCard
import com.example.ui.viewmodel.PageBookViewModel
import kotlinx.coroutines.delay
import java.util.Locale

data class SellerDisputeItem(
    val id: String,
    val buyerName: String,
    val productTitle: String,
    val complaint: String,
    var status: String, // "AWAITING_COUNTER", "ADMIN_REVIEWING", "RESOLVED_SELLER_WON", "RESOLVED_BUYER_WON"
    val fineAmount: Double,
    var evidenceText: String
)

data class SellerChatInbox(
    val buyerName: String,
    var lastMsg: String,
    val messages: MutableList<ChatMessageItem>
)

data class ChatMessageItem(
    val sender: String, // "buyer", "seller"
    val text: String
)

data class WithdrawalTx(
    val id: String,
    val gateway: String,
    val mobileNumber: String,
    val amountBdt: Double,
    val status: String, // "Pending", "Completed"
    val timestamp: String
)

data class FreelanceGig(
    val id: String,
    val title: String,
    val freelancerName: String,
    val rating: Float,
    val description: String,
    val workScope: String,
    val category: String, // "Graphics Design", "Web Development", "Digital Marketing", "Video Editing", "SEO & SMM"
    val imageUrl1: String,
    val imageUrl2: String,
    val videoUrl: String,
    var priceBasicBdt: Double,
    var priceMediumBdt: Double,
    var priceProBdt: Double,
    val deliveryDaysBasic: Int = 3,
    val deliveryDaysMedium: Int = 5,
    val deliveryDaysPro: Int = 7
)

data class Product(
    val id: Int,
    val title: String,
    val price: Double,
    val rating: Float,
    val reviewCount: Int,
    val imageUrl: String,
    val category: String,
    val description: String,
    val isExpress: Boolean = true,
    val discountPercent: Int = 0, // Set manually by Seller
    val discountCouponCode: String = "", // Set manually by Seller
    val sellerName: String = "Nabachetna Verified Seller",
    val sellerVerifiedDeposit: Boolean = true,
    var isWishlisted: Boolean = false,
    val isGlobal: Boolean = true,
    val targetCountryCode: String = "ALL", // e.g., "+880" for Bangladesh
    val isOfficialWarehouse: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun MarketplaceScreen(
    viewModel: PageBookViewModel,
    onBack: () -> Unit
) {
    BackHandler {
        onBack()
    }

    val context = LocalContext.current
    val escrowOrders by viewModel.escrowOrders.collectAsStateWithLifecycle()
    val commissionPercent by viewModel.marketplaceEscrowFeePercent.collectAsStateWithLifecycle()

    val adCampaigns by AdManagerRegistry.adCampaigns.collectAsStateWithLifecycle()
    val adFrequencyInterval by AdManagerRegistry.adFrequencyInterval.collectAsStateWithLifecycle()
    val activeAds = remember(adCampaigns) { adCampaigns.filter { it.isActive } }

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var selectedProductForDetail by remember { mutableStateOf<Product?>(null) }
    var isAddListingOpen by remember { mutableStateOf(false) }
    var showMyOrdersModal by remember { mutableStateOf(false) }
    var productToCheckout by remember { mutableStateOf<Product?>(null) }
    var checkoutPhone by remember { mutableStateOf("") }
    var checkoutAddress by remember { mutableStateOf("") }
    var isAppBanned by remember { mutableStateOf(false) }
    var appBannedReason by remember { mutableStateOf("") }
    var userWalletBalance by remember { mutableStateOf(5000.0) } // Initial buyer wallet balance in BDT
    var showDirectChatWithSeller by remember { mutableStateOf<String?>(null) } // Seller Name to chat with
    var directChatMessages by remember { mutableStateOf(mutableListOf<ChatMessageItem>(
        ChatMessageItem("seller", "সালামু আলাইকুম, কোনো সাহায্য লাগবে ভাই?"),
        ChatMessageItem("buyer", "ভাই, আমি আপনার পন্যটি অর্ডার দিতে চাই।")
    )) }
    var directChatInputText by remember { mutableStateOf("") }
    var userViolationCount by remember { mutableStateOf(0) }
    
    val checkAndApplyFine: (String, Boolean) -> Boolean = { text, isBuyer ->
        val badWords = listOf("শালা", "চোদা", "ফালতু", "হারামি", "কুত্তা", "খারাপ", "চোর", "গাধা", "কুত্তার")
        val offPlatformWords = listOf("whatsapp", "messenger", "imo", "বিকাশ করুন", "নাম্বার দিন", "call me", "যোগাযোগ করুন", "০১৭", "017", "019", "018", "015", "016", "013", "014", "বিকাশে টাকা দিন", "পার্সোনাল পেমেন্ট", "ফেসবুক", "facebook")
        
        val containsBadWord = badWords.any { text.lowercase().contains(it) }
        val containsOffPlatform = offPlatformWords.any { text.lowercase().contains(it) }
        
        if (containsBadWord || containsOffPlatform) {
            if (isBuyer) {
                userWalletBalance -= 500.0
                userViolationCount += 1
                if (userViolationCount >= 2) {
                    isAppBanned = true
                    appBannedReason = "একাধিকবার অশালীন ভাষা বা অ্যাপের বাইরে লেনদেনের প্রস্তাব দেওয়ায় আপনার অ্যাকাউন্ট চিরতরে ব্যান করা হয়েছে।"
                }
            } else {
                sellerWalletBalance -= 500.0
                sellerViolationStrikes += 1
                if (sellerViolationStrikes >= 3) {
                    isSellerShopSuspended = true
                }
            }
            true
        } else {
            false
        }
    }
    var orderToDispute by remember { mutableStateOf<EscrowOrder?>(null) }
    var disputeClaimInput by remember { mutableStateOf("ত্রুটিপূর্ণ বা ভিন্ন পণ্য পেয়েছি") }
    var disputeEvidenceInput by remember { mutableStateOf("") }
    var chatProduct by remember { mutableStateOf<Product?>(null) }
    var sellerWalletBalance by remember { mutableStateOf(1250.0) } // Default verified seller deposit >= 500 BDT

    // Seller Console specific states
    var showSellerConsoleModal by remember { mutableStateOf(false) }
    var sellerConsoleSelectedTab by remember { mutableStateOf("orders") } // "orders", "inventory", "chat", "disputes"
    
    // Custom received orders for seller
    var receivedOrders by remember {
        mutableStateOf(
            listOf(
                EscrowOrder(
                    id = "ORD-9824",
                    buyerName = "তানভীর রহমান",
                    buyerAddress = "মিরপুর ১২, ঢাকা",
                    buyerPhone = "01712345678",
                    productId = 1,
                    productTitle = "Premium Mechanical Keyboard - Sunset Backlit",
                    amount = 89.99,
                    status = EscrowOrderStatus.HOLD,
                    timestamp = "2026-09-27 14:30"
                ),
                EscrowOrder(
                    id = "ORD-7741",
                    buyerName = "মায়িশা তাসনিম",
                    buyerAddress = "হালিশহর, চট্টগ্রাম",
                    buyerPhone = "01887654321",
                    productId = 3,
                    productTitle = "Pure Organic Honey - Sundarban Special",
                    amount = 12.50,
                    status = EscrowOrderStatus.RELEASED,
                    timestamp = "2026-09-26 11:15"
                )
            )
        )
    }

    // Custom seller shop disputes
    var sellerDisputes by remember {
        mutableStateOf(
            mutableListOf(
                SellerDisputeItem(
                    id = "DIS-409",
                    buyerName = "তানভীর রহমান",
                    productTitle = "Premium Mechanical Keyboard",
                    complaint = "কি-বোর্ডের RGB লাইট জ্বলছে না, ভিন্ন ডিফেক্টিভ কি-বোর্ড পাঠিয়েছে!",
                    status = "AWAITING_COUNTER", // "AWAITING_COUNTER", "ADMIN_REVIEWING", "RESOLVED_SELLER_WON", "RESOLVED_BUYER_WON"
                    fineAmount = 250.0,
                    evidenceText = ""
                )
            )
        )
    }

    var isSellerArbitrationSimulating by remember { mutableStateOf(false) }
    var arbitrationProgress by remember { mutableStateOf(0f) }
    var selectedDisputeForAction by remember { mutableStateOf<SellerDisputeItem?>(null) }
    var sellerCounterClaimTextInput by remember { mutableStateOf("") }
    var sellerViolationStrikes by remember { mutableStateOf(0) }
    var isSellerShopSuspended by remember { mutableStateOf(false) }

    // Dedicated seller chat messages (completely separate from general messenger)
    var sellerInboxes by remember {
        mutableStateOf(
            listOf(
                SellerChatInbox(
                    buyerName = "তানভীর রহমান",
                    lastMsg = "ভাই কীবোর্ডে কি ওয়ারেন্টি আছে?",
                    messages = mutableListOf(
                        ChatMessageItem(sender = "buyer", text = "সালামু আলাইকুম ভাই, কীবোর্ডটা কি অরিজিনাল?"),
                        ChatMessageItem(sender = "seller", text = "ওয়ালাইকুম আসসালাম, জী ভাই ১০০% অরিজিনাল sunset গ্রেডিয়েন্ট RGB কি-বোর্ড।"),
                        ChatMessageItem(sender = "buyer", text = "ভাই কীবোর্ডে কি ওয়ারেন্টি আছে?")
                    )
                ),
                SellerChatInbox(
                    buyerName = "রাফসান জামান",
                    lastMsg = "ডিসকাউন্ট বাড়িয়ে দিলে এখনই অর্ডার করব",
                    messages = mutableListOf(
                        ChatMessageItem(sender = "buyer", text = "ভাই হানিটার কোয়ালিটি কেমন?"),
                        ChatMessageItem(sender = "seller", text = "সুন্দরবনের শতভাগ প্রাকৃতিক খাঁটি মধু ভাই।"),
                        ChatMessageItem(sender = "buyer", text = "ডিসকাউন্ট বাড়িয়ে দিলে এখনই অর্ডার করব")
                    )
                )
            )
        )
    }
    var selectedChatInboxIndex by remember { mutableStateOf(0) }
    var sellerNewChatMessageText by remember { mutableStateOf("") }

    val officialProducts by OfficialWarehouseManager.officialProducts.collectAsStateWithLifecycle()

    var userProducts by remember {
        mutableStateOf(
            listOf(
                Product(
                    id = 1,
                    title = "Premium Mechanical Keyboard - Sunset Backlit",
                    price = 89.99,
                    rating = 4.8f,
                    reviewCount = 1420,
                    imageUrl = "https://images.unsplash.com/photo-1587829741301-dc798b83add3?w=500",
                    category = "Electronics",
                    description = "A responsive mechanical keyboard featuring premium switches, durable double-shot keycaps, and customizable sunset gradient LED backlighting.",
                    discountPercent = 15,
                    sellerName = "Sajid Tech Store"
                ),
                Product(
                    id = 2,
                    title = "Minimalist Cork Backpack - Eco Friendly",
                    price = 45.00,
                    rating = 4.6f,
                    reviewCount = 380,
                    imageUrl = "https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=500",
                    category = "Fashion",
                    description = "Handcrafted entirely from organic water-resistant cork. Features a padded sleeve for laptops up to 15 inches and hidden safety pockets.",
                    discountPercent = 10,
                    sellerName = "Green Leaf Crafts"
                ),
                Product(
                    id = 3,
                    title = "Pure Organic Honey - Sundarban Special",
                    price = 12.50,
                    rating = 4.9f,
                    reviewCount = 820,
                    imageUrl = "https://images.unsplash.com/photo-1471943311424-646960669fbc?w=500",
                    category = "Food & Grocery",
                    description = "Raw, unfiltered wild honey collected directly from sustainable forest reserves of the Sundarbans.",
                    isExpress = true,
                    sellerName = "Sundarban Naturals"
                ),
                Product(
                    id = 4,
                    title = "Ergonomic Smart Desk Lamp - SoftEye Pro",
                    price = 29.99,
                    rating = 4.5f,
                    reviewCount = 512,
                    imageUrl = "https://images.unsplash.com/photo-1507473885765-e6ed057f782c?w=500",
                    category = "Home & Living",
                    description = "Eye-care LED desk lamp with 5 levels of adjustable color temperature and sliding touch brightness control.",
                    discountPercent = 20,
                    sellerName = "Lumina Home"
                ),
                Product(
                    id = 5,
                    title = "Wireless Noise Cancelling Earbuds",
                    price = 59.99,
                    rating = 4.7f,
                    reviewCount = 1140,
                    imageUrl = "https://images.unsplash.com/photo-1590658268037-6bf12165a8df?w=500",
                    category = "Electronics",
                    description = "Advanced Active Noise Cancelling (ANC) earbuds with true stereo sound, deep rich bass, and 40h runtime case.",
                    isExpress = true,
                    discountPercent = 25,
                    sellerName = "Gadget World BD"
                ),
                Product(
                    id = 6,
                    title = "Aesthetic Handcrafted Ceramic Mug",
                    price = 18.00,
                    rating = 4.9f,
                    reviewCount = 210,
                    imageUrl = "https://images.unsplash.com/photo-1514432324607-a09d9b4aefdd?w=500",
                    category = "Home & Living",
                    description = "Individually hand-thrown ceramic mug finished with a speckled reactive glaze. Microwave and dishwasher safe.",
                    sellerName = "Mati Clay Arts"
                )
            )
        )
    }

    val products = remember(userProducts, officialProducts) {
        userProducts + officialProducts
    }

    var userFreelanceGigs by remember {
        mutableStateOf(
            listOf(
                FreelanceGig(
                    id = "GIG-101",
                    title = "Professional Brand Logo & Full Identity Package",
                    freelancerName = "Sabbir Ahmed (Creative Studio)",
                    rating = 4.9f,
                    description = "I will design a unique, minimalist, and memorable brand logo with complete color palette, typography guidelines, and vector files for your startup.",
                    workScope = "Includes custom logo concepts, business card design, social media kit, and source vector files in high resolution.",
                    category = "Graphics Design",
                    imageUrl1 = "https://images.unsplash.com/photo-1626785774573-4b799315345d?w=500",
                    imageUrl2 = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=500",
                    videoUrl = "https://www.w3schools.com/html/mov_bbb.mp4",
                    priceBasicBdt = 1500.0,
                    priceMediumBdt = 3500.0,
                    priceProBdt = 8000.0,
                    deliveryDaysBasic = 2,
                    deliveryDaysMedium = 4,
                    deliveryDaysPro = 7
                ),
                FreelanceGig(
                    id = "GIG-102",
                    title = "Modern Responsive eCommerce Website (React & Node)",
                    freelancerName = "Freelancer Mahbub (Web Dev)",
                    rating = 4.8f,
                    description = "Get a high-performance eCommerce web app or customized mobile-friendly website tailored with Admin Panel, secure checkouts, and custom products database.",
                    workScope = "Figma design conversion, responsive mobile layout, custom cart, integration with cash on delivery, and 3 months technical support.",
                    category = "Web Development",
                    imageUrl1 = "https://images.unsplash.com/photo-1547658719-da2b51169166?w=500",
                    imageUrl2 = "https://images.unsplash.com/photo-1460925895917-afdab827c52f?w=500",
                    videoUrl = "https://www.w3schools.com/html/movie.mp4",
                    priceBasicBdt = 5000.0,
                    priceMediumBdt = 12000.0,
                    priceProBdt = 25000.0,
                    deliveryDaysBasic = 5,
                    deliveryDaysMedium = 10,
                    deliveryDaysPro = 20
                ),
                FreelanceGig(
                    id = "GIG-103",
                    title = "Viral Facebook & Google Ads Social Marketing",
                    freelancerName = "Tasnim Alam (Digital Agency)",
                    rating = 5.0f,
                    description = "I will plan, set up, and optimize highly targeted conversion ads campaigns to boost your local shop/page sales in Bangladesh with low cost per acquisition.",
                    workScope = "Target audience research, budget allocation strategy, high conversion copywriting, custom graphics ad creatives, and weekly detailed performance reporting.",
                    category = "Digital Marketing",
                    imageUrl1 = "https://images.unsplash.com/photo-1460925895917-afdab827c52f?w=500",
                    imageUrl2 = "https://images.unsplash.com/photo-1557804506-669a67965ba0?w=500",
                    videoUrl = "https://www.w3schools.com/html/mov_bbb.mp4",
                    priceBasicBdt = 2000.0,
                    priceMediumBdt = 5000.0,
                    priceProBdt = 12000.0,
                    deliveryDaysBasic = 3,
                    deliveryDaysMedium = 7,
                    deliveryDaysPro = 15
                )
            )
        )
    }

    var selectedGigForDetail by remember { mutableStateOf<FreelanceGig?>(null) }
    var showAddGigDialog by remember { mutableStateOf(false) }
    var gigRequirementInput by remember { mutableStateOf("") }

    var showWithdrawModal by remember { mutableStateOf(false) }
    var withdrawAmountBdt by remember { mutableStateOf("") }
    var withdrawMobileNumber by remember { mutableStateOf("") }
    var selectedWithdrawGateway by remember { mutableStateOf("bKash") } // "bKash", "Nagad", "Rocket"
    var withdrawalTransactionsList by remember {
        mutableStateOf(
            listOf(
                WithdrawalTx("TX-9901", "bKash", "01755******", 2500.0, "Completed", "2026-09-27 12:45"),
                WithdrawalTx("TX-9902", "Nagad", "01912******", 1200.0, "Pending", "2026-09-28 00:10")
            )
        )
    }

    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val userCountry = currentUser?.countryCode ?: "+880"

    val categories = listOf("All", "অফিশিয়াল গোডাউন 🏢", "ফ্রিল্যান্সিং গিগস 💻", "Wishlist ❤️", "Electronics", "Fashion", "Home & Living", "Food & Grocery")

    val filteredProducts = remember(searchQuery, selectedCategory, products, userCountry) {
        products.filter { product ->
            val matchesCountry = product.isGlobal || product.targetCountryCode == "ALL" || product.targetCountryCode == userCountry
            val matchesCategory = when (selectedCategory) {
                "All" -> true
                "অফিশিয়াল গোডাউন 🏢" -> product.isOfficialWarehouse
                "Wishlist ❤️" -> product.isWishlisted
                else -> product.category == selectedCategory && !product.isOfficialWarehouse
            }
            val matchesSearch = (product.title.contains(searchQuery, ignoreCase = true) || 
                                 product.description.contains(searchQuery, ignoreCase = true))
            matchesCountry && matchesCategory && matchesSearch
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
                // Top Search Bar
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

                    TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("সার্চ করুন নবচেতনা মার্কেটপ্লেস...", fontSize = 13.sp, color = Color.Gray) },
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .border(1.5.dp, Color(0xFFFF9900).copy(alpha = 0.8f), RoundedCornerShape(22.dp))
                            .clip(RoundedCornerShape(22.dp)),
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

                    Spacer(modifier = Modifier.width(8.dp))

                    // My Escrow Orders Button with Badge
                    BadgedBox(
                        badge = {
                            if (escrowOrders.isNotEmpty()) {
                                Badge(
                                    containerColor = Color(0xFFFF9900),
                                    contentColor = Color.White
                                ) {
                                    Text(text = "${escrowOrders.size}", fontWeight = FontWeight.Bold)
                                }
                            }
                        },
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable { showMyOrdersModal = true }
                            .padding(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalShipping,
                            contentDescription = "My Orders",
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    // 🏪 Dedicated Seller Console Button
                    BadgedBox(
                        badge = {
                            val activeAlerts = sellerDisputes.count { it.status == "AWAITING_COUNTER" }
                            if (activeAlerts > 0) {
                                Badge(
                                    containerColor = Color.Red,
                                    contentColor = Color.White
                                ) {
                                    Text(text = "$activeAlerts", fontWeight = FontWeight.Bold, fontSize = 9.sp)
                                }
                            } else {
                                Badge(
                                    containerColor = Color(0xFF4CAF50),
                                    modifier = Modifier.size(8.dp)
                                ) {}
                            }
                        },
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable { showSellerConsoleModal = true }
                            .padding(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Storefront,
                            contentDescription = "Seller Console",
                            tint = Color(0xFFFFD54F),
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }

                // Escrow Buyer Protection Banner
                Surface(
                    color = Color.Black.copy(alpha = 0.2f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = Color(0xFFFFD54F),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "১০০% এসক্রো সুরক্ষা: পণ্য পেয়ে সম্মতি দিলেই বিক্রেতা টাকা পাবে",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { isAddListingOpen = true },
                containerColor = Color(0xFFFF9900),
                contentColor = Color.Black,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(imageVector = Icons.Default.AddBusiness, contentDescription = "Sell Product")
                Spacer(modifier = Modifier.width(6.dp))
                Text("পণ্য বিক্রি করুন", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF3F3F3)),
            contentPadding = PaddingValues(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Category Selector
            item(span = { GridItemSpan(2) }) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 6.dp)
                ) {
                    items(categories) { cat ->
                        val isSel = selectedCategory == cat
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (isSel) TeaLeafGreen else Color.White,
                            border = BorderStroke(1.dp, if (isSel) TeaLeafGreen else Color.LightGray.copy(alpha = 0.5f)),
                            modifier = Modifier.clickable { selectedCategory = cat }
                        ) {
                            Text(
                                text = cat,
                                fontSize = 12.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSel) Color.White else Color.Black,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }

            // Products Grid
            if (selectedCategory == "ফ্রিল্যান্সিং গিগস 💻") {
                items(userFreelanceGigs) { gig ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedGigForDetail = gig },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(135.dp)
                                    .background(Color(0xFFF9F9F9))
                            ) {
                                AsyncImage(
                                    model = gig.imageUrl1,
                                    contentDescription = gig.title,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )

                                Surface(
                                    color = Color(0xFF00B0FF),
                                    shape = RoundedCornerShape(bottomEnd = 8.dp),
                                    modifier = Modifier.align(Alignment.TopStart)
                                ) {
                                    Text(
                                        text = gig.category,
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                    )
                                }
                            }

                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.AccountCircle, contentDescription = null, tint = TeaLeafGreen, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(gig.freelancerName, fontSize = 10.sp, maxLines = 1, overflow = TextOverflow.Ellipsis, color = Color.Gray)
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = gig.title,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black,
                                    maxLines = 2,
                                    minLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFF9900), modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text("${gig.rating}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                                    }

                                    Column(horizontalAlignment = Alignment.End) {
                                        Text("শুরু হচ্ছে", fontSize = 8.sp, color = Color.Gray)
                                        Text("৳${gig.priceBasicBdt.toInt()} BDT", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TeaLeafGreen)
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                itemsIndexed(filteredProducts, key = { _, it -> it.id }) { index, product ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedProductForDetail = product },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(135.dp)
                                .background(Color(0xFFF9F9F9))
                        ) {
                            AsyncImage(
                                model = product.imageUrl,
                                contentDescription = product.title,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )

                            if (product.discountPercent > 0) {
                                Surface(
                                    color = Color(0xFFCC0C39),
                                    shape = RoundedCornerShape(bottomEnd = 8.dp),
                                    modifier = Modifier.align(Alignment.TopStart)
                                ) {
                                    Text(
                                        text = "${product.discountPercent}% OFF" + if (product.discountCouponCode.isNotBlank()) " (${product.discountCouponCode})" else "",
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                    )
                                }
                            }

                            // Wishlist Heart Icon
                            IconButton(
                                onClick = {
                                    products = products.map {
                                        if (it.id == product.id) it.copy(isWishlisted = !it.isWishlisted) else it
                                    }
                                    Toast.makeText(context, if (product.isWishlisted) "উইশলিস্ট থেকে সরানো হয়েছে" else "উইশলিস্টে যুক্ত করা হয়েছে ❤️", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .size(32.dp)
                                    .padding(4.dp)
                            ) {
                                Icon(
                                    imageVector = if (product.isWishlisted) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                                    contentDescription = "Wishlist",
                                    tint = if (product.isWishlisted) Color.Red else Color.Gray
                                )
                            }

                            // Escrow Protected Badge
                            Surface(
                                color = Color(0xFF004D40).copy(alpha = 0.85f),
                                shape = RoundedCornerShape(topStart = 8.dp),
                                modifier = Modifier.align(Alignment.BottomEnd)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = Color(0xFFFFD54F), modifier = Modifier.size(10.dp))
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(text = "এসক্রো সুরক্ষিত", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = product.title,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 2,
                                minLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                color = Color.Black
                            )

                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = TeaLeafGreen, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = product.sellerName,
                                    fontSize = 10.sp,
                                    color = Color.Gray,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column {
                                    Text(
                                        text = "$${product.price}",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFF1B1B1B)
                                    )
                                    Text(
                                        text = "৳${(product.price * 120).toInt()} BDT",
                                        fontSize = 10.sp,
                                        color = Color.Gray
                                    )
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    IconButton(
                                        onClick = { chatProduct = product },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.Chat, contentDescription = "Chat", tint = Color(0xFF1976D2), modifier = Modifier.size(18.dp))
                                    }

                                    Button(
                                        onClick = { productToCheckout = product },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC400), contentColor = Color.Black),
                                        shape = RoundedCornerShape(10.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                        modifier = Modifier.height(32.dp)
                                    ) {
                                        Text("অর্ডার", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Checkout Escrow Modal
    productToCheckout?.let { prod ->
        val commission = prod.price * (commissionPercent / 100.0)
        val sellerGet = prod.price - commission

        AlertDialog(
            onDismissRequest = { productToCheckout = null },
            icon = {
                Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = Color(0xFF004D40), modifier = Modifier.size(36.dp))
            },
            title = {
                Text("🔒 এসক্রো সুরক্ষিত অর্ডার নিশ্চিতকরণ", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(
                            model = prod.imageUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(text = prod.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1)
                            Text(text = "বিক্রেতা: ${prod.sellerName}", fontSize = 11.sp, color = Color.Gray)
                            Text(text = "মূল্য: $${prod.price} (৳${(prod.price * 120).toInt()} BDT)", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF004D40))
                        }
                    }

                    OutlinedTextField(
                        value = checkoutPhone,
                        onValueChange = { checkoutPhone = it },
                        label = { Text("মোবাইল নাম্বার", fontSize = 11.sp) },
                        placeholder = { Text("০১৭XXXXXXXX", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = checkoutAddress,
                        onValueChange = { checkoutAddress = it },
                        label = { Text("ডেলিভারি ঠিকানা", fontSize = 11.sp) },
                        placeholder = { Text("যেমন: মিরপুর, ঢাকা", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 2
                    )

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFE0F2F1),
                        border = BorderStroke(1.dp, Color(0xFF80CBC4)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("🛡️ এসক্রো সুরক্ষা নিশ্চয়তা:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF004D40))
                            Text("১. আপনার একাউন্ট থেকে $${prod.price} কেটে এসক্রো ভল্টে নিরাপদে লক থাকবে।", fontSize = 11.sp, color = Color(0xFF004D40))
                            Text("২. বিক্রেতা পণ্য পাঠানোর পর আপনি হাতে পেয়ে 'পণ্য পেয়েছি' কনফার্ম করলে তবেই বিক্রেতা টাকা ($${String.format(Locale.US, "%.2f", sellerGet)}) পাবেন।", fontSize = 11.sp, color = Color(0xFF004D40))
                            Text("৩. কোনো ত্রুটি থাকলে অভিযোগ করলে অ্যাডমিন ট্রাইব্যুনাল তদন্ত করে আপনার ১০০% টাকা রিফান্ড করবে।", fontSize = 11.sp, color = Color(0xFF004D40))
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (checkoutPhone.isBlank() || checkoutAddress.isBlank()) {
                            Toast.makeText(context, "দয়া করে মোবাইল নাম্বার এবং ডেলিভারি ঠিকানা পূরণ করুন!", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        val costBdt = (prod.price * 120).toInt()
                        if (userWalletBalance < costBdt) {
                            Toast.makeText(context, "দুঃখিত! আপনার ওয়ালেট ব্যালেন্স পর্যাপ্ত নয়। অনুগ্রহ করে রিচার্জ করুন।", Toast.LENGTH_LONG).show()
                            return@Button
                        }
                        
                        userWalletBalance -= costBdt
                        
                        val newOrder = viewModel.createEscrowOrder(
                            productId = prod.id,
                            productTitle = prod.title,
                            productImageUrl = prod.imageUrl,
                            amountUsd = prod.price,
                            sellerName = prod.sellerName
                        )
                        
                        val newReceivedOrder = EscrowOrder(
                            id = newOrder.id,
                            buyerName = "Anika Rahman (Buyer)",
                            buyerAddress = checkoutAddress,
                            buyerPhone = checkoutPhone,
                            productId = prod.id,
                            productTitle = prod.title,
                            amount = prod.price,
                            status = EscrowOrderStatus.HOLD,
                            timestamp = "2026-09-28 00:05"
                        )
                        receivedOrders = receivedOrders + newReceivedOrder
                        
                        productToCheckout = null
                        showMyOrdersModal = true
                        Toast.makeText(context, "✓ অর্ডার #${newOrder.id} সফলভাবে সম্পন্ন হয়েছে! ৳$costBdt BDT এসক্রো ভল্টে লক করা হয়েছে।", Toast.LENGTH_LONG).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF004D40))
                ) {
                    Text("অর্ডার কনফার্ম ও পে ($${prod.price})", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { productToCheckout = null }) {
                    Text("বাতিল")
                }
            }
        )
    }

    // My Escrow Orders Sheet / Modal
    if (showMyOrdersModal) {
        AlertDialog(
            onDismissRequest = { showMyOrdersModal = false },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("📦 আমার এসক্রো অর্ডার ও পার্সেল", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    IconButton(onClick = { showMyOrdersModal = false }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }
            },
            text = {
                if (escrowOrders.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("আপনার কোনো সক্রিয় এসক্রো অর্ডার নেই", color = Color.Gray)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 450.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(escrowOrders, key = { it.id }) { order ->
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = "অর্ডার #${order.id}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = order.status.badgeColor.copy(alpha = 0.15f)
                                        ) {
                                            Text(
                                                text = order.status.label,
                                                color = order.status.badgeColor,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 10.sp,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(text = order.productTitle, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1)
                                    Text(text = "বিক্রেতা: ${order.sellerName} • $${order.amountUsd}", fontSize = 11.sp, color = Color.Gray)

                                    if (order.trackingNumber != null) {
                                        Text(text = "ট্র্যাকিং: ${order.trackingNumber}", fontSize = 11.sp, color = Color(0xFF1976D2), fontWeight = FontWeight.Medium)
                                    }

                                    // Action buttons for Buyer
                                    if (order.status == EscrowOrderStatus.PAYMENT_HELD_IN_ESCROW || order.status == EscrowOrderStatus.SHIPPED_IN_TRANSIT) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        
                                        Card(
                                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
                                            border = BorderStroke(0.5.dp, Color(0xFFFFB74D)),
                                            shape = RoundedCornerShape(6.dp),
                                            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                                        ) {
                                            Text(
                                                text = "📢 নিরাপত্তা সতর্কবার্তা: ডেলিভারিম্যানকে সামনে রেখে পার্সেল খুলে পণ্য পেয়েছি নিশ্চিত করুন। আপনি নিশ্চিত করা ছাড়া বিক্রেতার ওয়ালেটে কোনো টাকা ট্রান্সফার হবে না।",
                                                color = Color(0xFFE65100),
                                                fontSize = 9.5.sp,
                                                lineHeight = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(6.dp)
                                            )
                                        }
                                        
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Button(
                                                onClick = {
                                                    viewModel.confirmDeliveryAndReleaseFunds(order.id)
                                                    Toast.makeText(context, "ধন্যবাদ! বিক্রেতাকে পেমেন্ট রিলিজ এবং কোম্পানি কমিশন সফল হয়েছে।", Toast.LENGTH_LONG).show()
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                                                shape = RoundedCornerShape(8.dp),
                                                modifier = Modifier.weight(1.3f)
                                            ) {
                                                Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("পণ্য পেয়েছি (Release)", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                            }

                                            Button(
                                                onClick = {
                                                    showDirectChatWithSeller = order.sellerName
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)),
                                                shape = RoundedCornerShape(8.dp),
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Icon(imageVector = Icons.Default.Forum, contentDescription = null, modifier = Modifier.size(13.dp))
                                                Spacer(modifier = Modifier.width(3.dp))
                                                Text("মেসেজ করুন", fontSize = 9.sp)
                                            }

                                            OutlinedButton(
                                                onClick = {
                                                    orderToDispute = order
                                                    disputeClaimInput = "পণ্য নষ্ট বা বিবরণ অনুযায়ী সঠিক নয়"
                                                    disputeEvidenceInput = ""
                                                },
                                                shape = RoundedCornerShape(8.dp),
                                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFD32F2F)),
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Icon(imageVector = Icons.Default.Report, contentDescription = null, modifier = Modifier.size(14.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("অভিযোগ / রিফান্ড", fontSize = 10.sp)
                                            }
                                        }
                                    } else if (order.status == EscrowOrderStatus.DISPUTED_UNDER_REVIEW) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = Color(0xFFFFEBEE),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(
                                                text = "⚖️ আপনার অভিযোগটি এডমিন ট্রাইব্যুনাল পর্যালোচনা করছে। উভয় পক্ষের প্রমাণ যাচাই শেষে রায় দেওয়া হবে।",
                                                color = Color(0xFFC62828),
                                                fontSize = 11.sp,
                                                modifier = Modifier.padding(8.dp)
                                            )
                                        }
                                    } else if (order.status == EscrowOrderStatus.REFUNDED_TO_BUYER) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = Color(0xFFF3E5F5),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(
                                                text = "✓ ট্রাইব্যুনালের রায়ে আপনার $${order.amountUsd} টাকা রিফান্ড মঞ্জুর করা হয়েছে।",
                                                color = Color(0xFF6A1B9A),
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(8.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showMyOrdersModal = false }) {
                    Text("ঠিক আছে")
                }
            }
        )
    }

    // Raise Dispute / Refund Modal
    orderToDispute?.let { order ->
        AlertDialog(
            onDismissRequest = { orderToDispute = null },
            icon = {
                Icon(imageVector = Icons.Default.Gavel, contentDescription = null, tint = Color(0xFFD32F2F), modifier = Modifier.size(36.dp))
            },
            title = {
                Text("⚠️ বিরোধ বা রিফান্ড আবেদন দাখিল", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "অর্ডার #${order.id} (${order.productTitle})-এর জন্য অভিযোগ দায়ের করছেন। টাকা এসক্রো ভল্টে সম্পূর্ণ লক থাকবে।",
                        fontSize = 12.sp
                    )

                    OutlinedTextField(
                        value = disputeClaimInput,
                        onValueChange = { disputeClaimInput = it },
                        label = { Text("অভিযোগের ধরন / বিষয়") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = disputeEvidenceInput,
                        onValueChange = { disputeEvidenceInput = it },
                        label = { Text("বিস্তারিত বিবরণ ও প্রমাণ (যেমন পার্সেল ত্রুটি)") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (disputeClaimInput.isNotBlank()) {
                            viewModel.raiseDispute(
                                orderId = order.id,
                                claim = disputeClaimInput.trim(),
                                evidence = disputeEvidenceInput.ifBlank { "ক্রেতা কর্তৃক পার্সেল ত্রুটি রিপোর্ট করা হয়েছে।" }
                            )
                            orderToDispute = null
                            Toast.makeText(context, "অভিযোগ ট্রাইব্যুনালে জমা হয়েছে! এডমিন শীঘ্রই যাচাই করে সিদ্ধান্ত জানাবে।", Toast.LENGTH_LONG).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                ) {
                    Text("অভিযোগ দাখিল করুন")
                }
            },
            dismissButton = {
                TextButton(onClick = { orderToDispute = null }) {
                    Text("বাতিল")
                }
            }
        )
    }

    // Product Detail Popup Dialog
    selectedProductForDetail?.let { product ->
        AlertDialog(
            onDismissRequest = { selectedProductForDetail = null },
            confirmButton = {
                Button(
                    onClick = {
                        val chosen = product
                        selectedProductForDetail = null
                        productToCheckout = chosen
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9900)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("এসক্রো সুরক্ষিত বাই নাও", fontWeight = FontWeight.Bold, color = Color.Black)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        Toast.makeText(context, "Message sent to seller: ${product.sellerName}", Toast.LENGTH_SHORT).show()
                        selectedProductForDetail = null
                    }
                ) {
                    Text("Message Seller", color = TeaLeafGreen, fontWeight = FontWeight.Bold)
                }
            },
            title = {
                Text(
                    text = product.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(8.dp))
                    ) {
                        AsyncImage(
                            model = product.imageUrl,
                            contentDescription = product.title,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Price: $${product.price}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFB12704)
                        )

                        Text(
                            text = product.category,
                            color = Color.Gray,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .background(Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Seller: ${product.sellerName}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = TeaLeafGreen
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = product.description,
                        fontSize = 13.sp,
                        color = Color.DarkGray
                    )
                }
            },
            shape = RoundedCornerShape(12.dp),
            containerColor = Color.White
        )
    }

    // In-App Buyer-Seller Live Chat Modal
    chatProduct?.let { product ->
        var chatMessages by remember {
            mutableStateOf(
                listOf(
                    Pair(product.sellerName, "হ্যালো! '${product.title}' পণ্যটি সম্পর্কে আপনার কোনো প্রশ্ন থাকলে লিখুন।"),
                    Pair("You", "পণ্যটির স্টক আছে কি? এবং কত দিনে ডেলিভারি পাব?")
                )
            )
        }
        var messageInput by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { chatProduct = null },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("💬", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(product.sellerName, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text("অনলাইন • ভেরিফায়েড বিক্রেতা 🛡️", fontSize = 10.sp, color = TeaLeafGreen)
                        }
                    }
                    IconButton(onClick = { chatProduct = null }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            AsyncImage(model = product.imageUrl, contentDescription = null, modifier = Modifier.size(36.dp).clip(RoundedCornerShape(6.dp)))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(product.title, fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                                Text("$${product.price} (৳${(product.price * 120).toInt()})", fontSize = 10.sp, color = TeaLeafGreen, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                            .padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(chatMessages) { (sender, msg) ->
                            val isMe = sender == "You"
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isMe) TeaLeafGreen else Color.White,
                                    shadowElevation = 1.dp
                                ) {
                                    Text(
                                        text = msg,
                                        fontSize = 11.sp,
                                        color = if (isMe) Color.White else Color.Black,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = messageInput,
                            onValueChange = { messageInput = it },
                            placeholder = { Text("মেসেজ লিখুন...", fontSize = 11.sp) },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        IconButton(
                            onClick = {
                                if (messageInput.isNotBlank()) {
                                    chatMessages = chatMessages + Pair("You", messageInput.trim())
                                    messageInput = ""
                                }
                            }
                        ) {
                            Icon(imageVector = Icons.Default.Send, contentDescription = "Send", tint = TeaLeafGreen)
                        }
                    }
                }
            },
            confirmButton = {}
        )
    }

    // Add Product Dialog (Seller Form with ৳500 Deposit Policy & Manual Discount Option)
    if (isAddListingOpen) {
        var addTitle by remember { mutableStateOf("") }
        var addPrice by remember { mutableStateOf("") }
        var addCategory by remember { mutableStateOf("Electronics") }
        var addDescription by remember { mutableStateOf("") }
        var addImage by remember { mutableStateOf("") }
        var addDiscountPercent by remember { mutableStateOf("") }
        var addCouponCode by remember { mutableStateOf("") }
        var isGlobalProduct by remember { mutableStateOf(true) }
        var targetCountry by remember { mutableStateOf("+880") }

        AlertDialog(
            onDismissRequest = { isAddListingOpen = false },
            title = {
                Column {
                    Text("নবচেতনা মার্কেটপ্লেসে পণ্য লিস্ট করুন", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text("গ্লোবাল অথবা দেশভিত্তিক পণ্য পাবলিশ অপশন", fontSize = 10.sp, color = Color.Gray)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (addTitle.isBlank() || addPrice.isBlank()) {
                            Toast.makeText(context, "অনুগ্রহ করে শিরোনাম ও দাম পূরণ করুন!", Toast.LENGTH_SHORT).show()
                        } else if (sellerWalletBalance < 500.0) {
                            Toast.makeText(
                                context,
                                "⚠️ পণ্য পাবলিশ করতে বিক্রেতার একাউন্টে কমপক্ষে ৳৫০০ জমা থাকতে হবে! আপনার বর্তমান ব্যালেন্স: ৳${sellerWalletBalance.toInt()}। অনুগ্রহ করে ওয়ালেট রিচার্জ করুন।",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            val doublePrice = addPrice.toDoubleOrNull() ?: 10.0
                            val discPerc = addDiscountPercent.toIntOrNull() ?: 0
                            val finalImage = if (addImage.isBlank()) "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=500" else addImage
                            val newProduct = Product(
                                id = products.size + 1,
                                title = addTitle,
                                price = doublePrice,
                                rating = 5.0f,
                                reviewCount = 1,
                                imageUrl = finalImage,
                                category = addCategory,
                                description = addDescription.ifBlank { "বিক্রেতা কর্তৃক সরবরাহকৃত পণ্য।" },
                                isExpress = true,
                                discountPercent = discPerc,
                                discountCouponCode = addCouponCode.trim(),
                                sellerName = "You (ভেরিফায়েড বিক্রেতা)",
                                sellerVerifiedDeposit = true,
                                isGlobal = isGlobalProduct,
                                targetCountryCode = if (isGlobalProduct) "ALL" else targetCountry
                            )
                            products = listOf(newProduct) + products
                            isAddListingOpen = false
                            Toast.makeText(context, "পণ্য সফলভাবে মার্কেটপ্লেসে প্রকাশিত হয়েছে! 🛒", Toast.LENGTH_LONG).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TeaLeafGreen)
                ) {
                    Text("পাবলিশ করুন (Publish)", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { isAddListingOpen = false }) {
                    Text("বাতিল", color = Color.Gray)
                }
            },
            text = {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Seller Security Deposit Warning Card
                    item {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (sellerWalletBalance >= 500) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                            ),
                            border = BorderStroke(1.dp, if (sellerWalletBalance >= 500) TeaLeafGreen else Color.Red),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (sellerWalletBalance >= 500) Icons.Default.VerifiedUser else Icons.Default.Warning,
                                        contentDescription = null,
                                        tint = if (sellerWalletBalance >= 500) TeaLeafGreen else Color.Red,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "সঠিক সেলার ভেরিফিকেশন নীতি (৳৫০০ ডিপোজিট)",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = if (sellerWalletBalance >= 500) TeaLeafGreen else Color.Red
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "ভুয়া বিক্রেতা ও স্ক্যাম রোধে আপনার একাউন্টে ন্যূনতম ৳৫০০ জমা থাকতে হবে। বর্তমান ব্যালেন্স: ৳${sellerWalletBalance.toInt()} BDT।",
                                    fontSize = 10.sp,
                                    color = Color.DarkGray
                                )
                            }
                        }
                    }

                    item {
                        OutlinedTextField(
                            value = addTitle,
                            onValueChange = { addTitle = it },
                            label = { Text("পণ্যের শিরোনাম*", fontSize = 11.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = addPrice,
                            onValueChange = { addPrice = it },
                            label = { Text("পণ্যের মূল্য ($ USD)*", fontSize = 11.sp) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }

                    // Manual Seller Discount Option
                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            OutlinedTextField(
                                value = addDiscountPercent,
                                onValueChange = { addDiscountPercent = it },
                                label = { Text("ডিসকাউন্ট % (ঐচ্ছিক)", fontSize = 11.sp) },
                                placeholder = { Text("যেমন: 15", fontSize = 10.sp) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = addCouponCode,
                                onValueChange = { addCouponCode = it },
                                label = { Text("কুপন কোড (ঐচ্ছিক)", fontSize = 11.sp) },
                                placeholder = { Text("যেমন: EID2026", fontSize = 10.sp) },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        }
                    }

                    item {
                        Text("পণ্যের ধরণ ও রিজিওন (Global vs Country):", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(
                                selected = isGlobalProduct,
                                onClick = { isGlobalProduct = true },
                                label = { Text("🌍 গ্লোবাল (সকল দেশ)", fontSize = 10.sp) },
                                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = TeaLeafGreen, selectedLabelColor = Color.White)
                            )
                            FilterChip(
                                selected = !isGlobalProduct,
                                onClick = { isGlobalProduct = false },
                                label = { Text("📍 দেশ ভিত্তিক", fontSize = 10.sp) },
                                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = TeaLeafGreen, selectedLabelColor = Color.White)
                            )
                        }
                    }

                    if (!isGlobalProduct) {
                        item {
                            OutlinedTextField(
                                value = targetCountry,
                                onValueChange = { targetCountry = it },
                                label = { Text("দেশ/কান্ট্রি কোড (যেমন: +880, +91)", fontSize = 11.sp) },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                        }
                    }

                    item {
                        Text("ক্যাটাগরি নির্বাচন করুন:", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            items(categories.filter { it != "All" && it != "Wishlist ❤️" }) { cat ->
                                val isSel = addCategory == cat
                                FilterChip(
                                    selected = isSel,
                                    onClick = { addCategory = cat },
                                    label = { Text(cat, fontSize = 10.sp) },
                                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = TeaLeafGreen, selectedLabelColor = Color.White)
                                )
                            }
                        }
                    }

                    item {
                        OutlinedTextField(
                            value = addImage,
                            onValueChange = { addImage = it },
                            label = { Text("পণ্যের ছবির লিংক (Image URL)", fontSize = 11.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = addDescription,
                            onValueChange = { addDescription = it },
                            label = { Text("পণ্যের বিস্তারিত বিবরণ", fontSize = 11.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2
                        )
                    }
                }
            }
        )
    }

    // --- 🏪 STATE-OF-THE-ART INTERACTIVE SELLER CONSOLE MODAL DIALOG ---
    if (showSellerConsoleModal) {
        Dialog(onDismissRequest = { showSellerConsoleModal = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.92f)
                    .padding(vertical = 12.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Header Area
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Storefront,
                                contentDescription = null,
                                tint = TeaLeafGreen,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "প্রফেশনাল বিক্রেতা ড্যাশবোর্ড",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                                Text(
                                    text = "সেলার আইডি: #NC-SELLER-89",
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                            }
                        }

                        IconButton(onClick = { showSellerConsoleModal = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                        }
                    }

                    // Seller Stats Strip
                    Card(
                        colors = CardDefaults.cardColors(containerColor = TeaLeafGreen.copy(alpha = 0.08f)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("সেলার ডিপোজিট ওয়ালেট", fontSize = 10.sp, color = Color.Gray)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("৳${String.format(Locale.US, "%,.2f", sellerWalletBalance)} BDT", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TeaLeafGreen)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Button(
                                        onClick = { showWithdrawModal = true },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9900)),
                                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                        modifier = Modifier.height(24.dp),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text("উত্তোলন 💸", fontSize = 9.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = if (sellerViolationStrikes > 0) Color(0xFFFFEBEE) else Color(0xFFE8F5E9)),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "স্ট্রাইক: $sellerViolationStrikes/৩",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (sellerViolationStrikes > 0) Color.Red else Color(0xFF2E7D32),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }

                                Card(
                                    colors = CardDefaults.cardColors(containerColor = if (isSellerShopSuspended) Color.Red else Color(0xFF2E7D32)),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = if (isSellerShopSuspended) "স্থগিত (Suspended)" else "সক্রিয় (Active)",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Strict Anti-Fraud Seller Directive Card
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFDE7)),
                        border = BorderStroke(1.dp, Color(0xFFFBC02D)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Gavel,
                                contentDescription = null,
                                tint = Color(0xFFF57F17),
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "⚠️ বিক্রেতা নিরাপত্তা ও প্রতারণা রোধে কঠোর নির্দেশ:",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = Color(0xFFE65100)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "পণ্য কুরিয়ার বা ডেলিভারিম্যানের কাছে হস্তান্তরের সময় কঠোর নির্দেশ দিন যেন ক্রেতাকে পণ্য বুঝিয়ে দেওয়ার সময় ক্রেতার ফোনে থাকা আমাদের অ্যাপের 'পণ্য পেয়েছি (Release)' বাটনে টিক দিয়ে নিশ্চিত করে নেয়। ডেলিভারিম্যান চলে যাওয়ার পর ক্রেতা টিক দিতে অবহেলা করলে বা ভুলে গেলে আপনার পেমেন্ট আটকে যাবে এবং তা বিক্রেতার লোকসান হিসেবে গণ্য হবে!",
                                    fontSize = 9.5.sp,
                                    color = Color.DarkGray,
                                    lineHeight = 13.sp
                                )
                            }
                        }
                    }

                    if (isSellerShopSuspended) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(Icons.Default.ReportGmailerrorred, contentDescription = null, tint = Color.Red, modifier = Modifier.size(36.dp))
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "অ্যাকাউন্ট স্থগিত করা হয়েছে!",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Red
                                )
                                Text(
                                    text = "একাধিকবার কপিরাইট লঙ্ঘন বা ফেক পণ্য বিক্রির অভিযোগ প্রমানিত হওয়ায় আপনার বিক্রেতা অ্যাকাউন্ট স্থগিত করা হয়েছে। আপিল করতে এডমিনের সাথে যোগাযোগ করুন।",
                                    fontSize = 11.sp,
                                    color = Color.DarkGray,
                                    lineHeight = 15.sp,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    } else {
                        // Horizontal Custom Tab Selectors
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            val tabs = listOf(
                                Triple("orders", "অর্ডার", Icons.Default.ShoppingBag),
                                Triple("inventory", "পণ্য নিয়ন্ত্রণ", Icons.Default.Inventory),
                                Triple("chat", "মেসেজ", Icons.Default.Forum),
                                Triple("disputes", "অভিযোগ কেন্দ্র", Icons.Default.Gavel)
                            )
                            tabs.forEach { (tabId, label, icon) ->
                                val isSelected = sellerConsoleSelectedTab == tabId
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isSelected) TeaLeafGreen else Color(0xFFF5F5F5))
                                        .clickable { sellerConsoleSelectedTab = tabId }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(
                                            imageVector = icon,
                                            contentDescription = null,
                                            tint = if (isSelected) Color.White else Color.Gray,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = label,
                                            fontSize = 9.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) Color.White else Color.DarkGray
                                        )
                                    }
                                }
                            }
                        }

                        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))

                        // Tab Contents
                        Box(modifier = Modifier.weight(1f)) {
                            when (sellerConsoleSelectedTab) {
                                "orders" -> {
                                    LazyColumn(
                                        verticalArrangement = Arrangement.spacedBy(10.dp),
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        item {
                                            Text("প্রাপ্ত কাস্টমার অর্ডার তালিকা:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                        }

                                        if (receivedOrders.isEmpty()) {
                                            item {
                                                Box(modifier = Modifier.fillMaxWidth().padding(30.dp), contentAlignment = Alignment.Center) {
                                                    Text("কোনো অর্ডার পাওয়া যায়নি", color = Color.Gray, fontSize = 12.sp)
                                                }
                                            }
                                        }

                                        items(receivedOrders) { order ->
                                            Card(
                                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9)),
                                                border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f)),
                                                shape = RoundedCornerShape(12.dp),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Column(modifier = Modifier.padding(12.dp)) {
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.SpaceBetween
                                                    ) {
                                                        Text("অর্ডার আইডি: ${order.id}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TeaLeafGreen)
                                                        Text(order.timestamp, fontSize = 10.sp, color = Color.Gray)
                                                    }

                                                    Spacer(modifier = Modifier.height(4.dp))
                                                    Text("পণ্য: ${order.productTitle}", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                                    Text("পরিমাণ: $${order.amount}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFF9900))

                                                    Spacer(modifier = Modifier.height(6.dp))
                                                    Text("ক্রেতার নাম: ${order.buyerName}", fontSize = 11.sp, color = Color.DarkGray)
                                                    Text("ঠিকানা: ${order.buyerAddress}", fontSize = 11.sp, color = Color.DarkGray)
                                                    Text("ফোন: ${order.buyerPhone}", fontSize = 11.sp, color = Color.DarkGray)

                                                    Spacer(modifier = Modifier.height(8.dp))
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.SpaceBetween,
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Text(
                                                            text = when (order.status) {
                                                                EscrowOrderStatus.HOLD -> "⏳ এসক্রোতে পেন্ডিং"
                                                                EscrowOrderStatus.RELEASED -> "✓ রিলিজড (৳ ওয়ালেটে যুক্ত)"
                                                                EscrowOrderStatus.DISPUTED -> "❌ কাস্টমার বিবাদকারী"
                                                                else -> "শিপড করা হয়েছে"
                                                            },
                                                            fontSize = 11.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = when (order.status) {
                                                                EscrowOrderStatus.RELEASED -> Color(0xFF2E7D32)
                                                                EscrowOrderStatus.DISPUTED -> Color.Red
                                                                else -> Color(0xFFFF9900)
                                                            }
                                                        )

                                                        if (order.status == EscrowOrderStatus.HOLD) {
                                                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                                Button(
                                                                    onClick = {
                                                                        receivedOrders = receivedOrders.map {
                                                                            if (it.id == order.id) it.copy(status = EscrowOrderStatus.RELEASED) else it
                                                                        }
                                                                        // Add funds to seller wallet
                                                                        sellerWalletBalance += order.amount
                                                                        Toast.makeText(context, "অর্ডার সফলভাবে ডেলিভারি ও ওয়ালেট ফান্ড যুক্ত হয়েছে!", Toast.LENGTH_SHORT).show()
                                                                    },
                                                                    colors = ButtonDefaults.buttonColors(containerColor = TeaLeafGreen),
                                                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                                                    shape = RoundedCornerShape(6.dp),
                                                                    modifier = Modifier.height(28.dp)
                                                                ) {
                                                                    Text("ডেলিভারি সম্পন্ন", fontSize = 10.sp, color = Color.White)
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                "inventory" -> {
                                    LazyColumn(
                                        verticalArrangement = Arrangement.spacedBy(10.dp),
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        item {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text("আমার প্রকাশিত পণ্যসমূহ:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                    Button(
                                                        onClick = { showAddGigDialog = true },
                                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00B0FF)),
                                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                                        modifier = Modifier.height(28.dp)
                                                    ) {
                                                        Icon(Icons.Default.Add, contentDescription = null, size = 14.dp, tint = Color.White)
                                                        Text("নতুন গিগ 💻", fontSize = 10.sp, color = Color.White)
                                                    }

                                                    Button(
                                                        onClick = { isAddListingOpen = true },
                                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9900)),
                                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                                        modifier = Modifier.height(28.dp)
                                                    ) {
                                                        Icon(Icons.Default.Add, contentDescription = null, size = 14.dp)
                                                        Text("নতুন পণ্য", fontSize = 10.sp, color = Color.Black)
                                                    }
                                                }
                                            }
                                        }

                                        items(userProducts) { prod ->
                                            var isPublished by remember { mutableStateOf(true) }
                                            var currentDiscountPercent by remember { mutableStateOf(prod.discountPercent) }

                                            Card(
                                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9)),
                                                border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f)),
                                                shape = RoundedCornerShape(12.dp),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(10.dp),
                                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                                ) {
                                                    AsyncImage(
                                                        model = prod.imageUrl,
                                                        contentDescription = null,
                                                        modifier = Modifier
                                                            .size(60.dp)
                                                            .clip(RoundedCornerShape(8.dp)),
                                                        contentScale = ContentScale.Crop
                                                    )

                                                    Column(modifier = Modifier.weight(1f)) {
                                                        Text(prod.title, fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                                        Text("মূল্য: $${prod.price}", fontSize = 11.sp, color = Color.DarkGray)

                                                        Spacer(modifier = Modifier.height(4.dp))

                                                        // Discount Control Box
                                                        Row(
                                                            verticalAlignment = Alignment.CenterVertically,
                                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                                        ) {
                                                            Text("ডিসকাউন্ট:", fontSize = 11.sp, color = Color.Gray)
                                                            IconButton(
                                                                onClick = { if (currentDiscountPercent > 0) currentDiscountPercent -= 5 },
                                                                modifier = Modifier.size(22.dp)
                                                            ) {
                                                                Icon(Icons.Default.RemoveCircleOutline, contentDescription = null, tint = Color.Red, modifier = Modifier.size(16.dp))
                                                            }
                                                            Text("$currentDiscountPercent%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Red)
                                                            IconButton(
                                                                onClick = { if (currentDiscountPercent < 80) currentDiscountPercent += 5 },
                                                                modifier = Modifier.size(22.dp)
                                                            ) {
                                                                Icon(Icons.Default.AddCircleOutline, contentDescription = null, tint = TeaLeafGreen, modifier = Modifier.size(16.dp))
                                                            }
                                                        }
                                                    }

                                                    Column(horizontalAlignment = Alignment.End) {
                                                        // Toggle publish state
                                                        Switch(
                                                            checked = isPublished,
                                                            onCheckedChange = { isPublished = it },
                                                            modifier = Modifier.scale(0.7f)
                                                        )
                                                        Text(if (isPublished) "পাবলিশড" else "ড্রাফট", fontSize = 9.sp, color = if (isPublished) TeaLeafGreen else Color.Gray)

                                                        Spacer(modifier = Modifier.height(10.dp))

                                                        IconButton(
                                                            onClick = {
                                                                userProducts = userProducts.filter { it.id != prod.id }
                                                                Toast.makeText(context, "পণ্যটি সফলভাবে রিমুভ করা হয়েছে!", Toast.LENGTH_SHORT).show()
                                                            },
                                                            modifier = Modifier.size(24.dp)
                                                        ) {
                                                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red, modifier = Modifier.size(18.dp))
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        item {
                                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color.LightGray.copy(alpha = 0.5f))
                                            Text("আমার ফ্রিল্যান্সিং গিগস (💻 Fiverr Style):", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                        }

                                        items(userFreelanceGigs) { gig ->
                                            Card(
                                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                                                border = BorderStroke(1.dp, Color(0xFFBBF7D0)),
                                                shape = RoundedCornerShape(12.dp),
                                                modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp)
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(10.dp),
                                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    AsyncImage(
                                                        model = gig.imageUrl1,
                                                        contentDescription = null,
                                                        modifier = Modifier
                                                            .size(50.dp)
                                                            .clip(RoundedCornerShape(8.dp)),
                                                        contentScale = ContentScale.Crop
                                                    )

                                                    Column(modifier = Modifier.weight(1f)) {
                                                        Text(gig.title, fontSize = 11.5.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                                        Text("ক্যাটাগরি: ${gig.category}", fontSize = 9.5.sp, color = Color.Gray)
                                                        
                                                        Spacer(modifier = Modifier.height(4.dp))
                                                        
                                                        // Price package changer for Basic, Medium, Pro
                                                        Text("প্যাকেজ পরিবর্তন (সবার জন্য কার্যকর হবে):", fontSize = 8.5.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                                                        
                                                        Row(
                                                            modifier = Modifier.fillMaxWidth(),
                                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                                        ) {
                                                            // Basic
                                                            Column(
                                                                modifier = Modifier.weight(1f),
                                                                horizontalAlignment = Alignment.CenterHorizontally
                                                            ) {
                                                                Text("সাধারণ", fontSize = 8.sp, color = Color.DarkGray)
                                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                                    IconButton(
                                                                        onClick = {
                                                                            if (gig.priceBasicBdt > 500) {
                                                                                gig.priceBasicBdt -= 100
                                                                                userFreelanceGigs = userFreelanceGigs.toList()
                                                                            }
                                                                        },
                                                                        modifier = Modifier.size(16.dp)
                                                                    ) {
                                                                        Icon(Icons.Default.RemoveCircle, contentDescription = null, tint = Color.Red, modifier = Modifier.size(12.dp))
                                                                    }
                                                                    Text("৳${gig.priceBasicBdt.toInt()}", fontSize = 8.5.sp, fontWeight = FontWeight.Bold)
                                                                    IconButton(
                                                                        onClick = {
                                                                            gig.priceBasicBdt += 100
                                                                            userFreelanceGigs = userFreelanceGigs.toList()
                                                                        },
                                                                        modifier = Modifier.size(16.dp)
                                                                    ) {
                                                                        Icon(Icons.Default.AddCircle, contentDescription = null, tint = TeaLeafGreen, modifier = Modifier.size(12.dp))
                                                                    }
                                                                }
                                                            }

                                                            // Medium
                                                            Column(
                                                                modifier = Modifier.weight(1f),
                                                                horizontalAlignment = Alignment.CenterHorizontally
                                                            ) {
                                                                Text("মিডিয়াম", fontSize = 8.sp, color = Color.DarkGray)
                                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                                    IconButton(
                                                                        onClick = {
                                                                            if (gig.priceMediumBdt > 1000) {
                                                                                gig.priceMediumBdt -= 200
                                                                                userFreelanceGigs = userFreelanceGigs.toList()
                                                                            }
                                                                        },
                                                                        modifier = Modifier.size(16.dp)
                                                                    ) {
                                                                        Icon(Icons.Default.RemoveCircle, contentDescription = null, tint = Color.Red, modifier = Modifier.size(12.dp))
                                                                    }
                                                                    Text("৳${gig.priceMediumBdt.toInt()}", fontSize = 8.5.sp, fontWeight = FontWeight.Bold)
                                                                    IconButton(
                                                                        onClick = {
                                                                            gig.priceMediumBdt += 200
                                                                            userFreelanceGigs = userFreelanceGigs.toList()
                                                                        },
                                                                        modifier = Modifier.size(16.dp)
                                                                    ) {
                                                                        Icon(Icons.Default.AddCircle, contentDescription = null, tint = TeaLeafGreen, modifier = Modifier.size(12.dp))
                                                                    }
                                                                }
                                                            }

                                                            // Pro
                                                            Column(
                                                                modifier = Modifier.weight(1f),
                                                                horizontalAlignment = Alignment.CenterHorizontally
                                                            ) {
                                                                Text("প্রো", fontSize = 8.sp, color = Color.DarkGray)
                                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                                    IconButton(
                                                                        onClick = {
                                                                            if (gig.priceProBdt > 2000) {
                                                                                gig.priceProBdt -= 500
                                                                                userFreelanceGigs = userFreelanceGigs.toList()
                                                                            }
                                                                        },
                                                                        modifier = Modifier.size(16.dp)
                                                                    ) {
                                                                        Icon(Icons.Default.RemoveCircle, contentDescription = null, tint = Color.Red, modifier = Modifier.size(12.dp))
                                                                    }
                                                                    Text("৳${gig.priceProBdt.toInt()}", fontSize = 8.5.sp, fontWeight = FontWeight.Bold)
                                                                    IconButton(
                                                                        onClick = {
                                                                            gig.priceProBdt += 500
                                                                            userFreelanceGigs = userFreelanceGigs.toList()
                                                                        },
                                                                        modifier = Modifier.size(16.dp)
                                                                    ) {
                                                                        Icon(Icons.Default.AddCircle, contentDescription = null, tint = TeaLeafGreen, modifier = Modifier.size(12.dp))
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }

                                                    IconButton(
                                                        onClick = {
                                                            userFreelanceGigs = userFreelanceGigs.filter { it.id != gig.id }
                                                            Toast.makeText(context, "গিগটি সফলভাবে বাতিল করা হয়েছে!", Toast.LENGTH_SHORT).show()
                                                        },
                                                        modifier = Modifier.size(28.dp)
                                                    ) {
                                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red, modifier = Modifier.size(18.dp))
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                "chat" -> {
                                    // Split Screen for Direct Seller Chat
                                    Row(modifier = Modifier.fillMaxSize()) {
                                        // Left Side List of Buyers
                                        Column(
                                            modifier = Modifier
                                                .weight(1f)
                                                .fillMaxHeight()
                                                .border(BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.4f)), RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp))
                                                .padding(6.dp),
                                            verticalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Text("ক্রেতা সংযোগ", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                                            LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                                itemsIndexed(sellerInboxes) { index, chat ->
                                                    val isSel = selectedChatInboxIndex == index
                                                    Card(
                                                        colors = CardDefaults.cardColors(containerColor = if (isSel) TeaLeafGreen.copy(alpha = 0.15f) else Color.White),
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .clickable { selectedChatInboxIndex = index },
                                                        shape = RoundedCornerShape(8.dp),
                                                        border = BorderStroke(1.dp, if (isSel) TeaLeafGreen else Color.LightGray.copy(alpha = 0.3f))
                                                    ) {
                                                        Column(modifier = Modifier.padding(6.dp)) {
                                                            Text(chat.buyerName, fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                                                            Text(chat.lastMsg, fontSize = 9.sp, color = Color.Gray, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        // Right Side Chat Window
                                        val activeChat = sellerInboxes.getOrNull(selectedChatInboxIndex)
                                        if (activeChat != null) {
                                            Column(
                                                modifier = Modifier
                                                    .weight(2f)
                                                    .fillMaxHeight()
                                                    .background(Color(0xFFFAFAFA))
                                                    .padding(6.dp)
                                            ) {
                                                // Active Chat Header
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .background(TeaLeafGreen.copy(alpha = 0.05f))
                                                        .padding(6.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Icon(Icons.Default.AccountCircle, contentDescription = null, tint = TeaLeafGreen, modifier = Modifier.size(24.dp))
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text(activeChat.buyerName, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                }

                                                // Message Messages List
                                                LazyColumn(
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .fillMaxWidth(),
                                                    verticalArrangement = Arrangement.spacedBy(6.dp),
                                                    contentPadding = PaddingValues(vertical = 6.dp)
                                                ) {
                                                    items(activeChat.messages) { msg ->
                                                        val isMe = msg.sender == "seller"
                                                        Box(
                                                            modifier = Modifier.fillMaxWidth(),
                                                            contentAlignment = if (isMe) Alignment.CenterEnd else Alignment.CenterStart
                                                        ) {
                                                            Card(
                                                                colors = CardDefaults.cardColors(
                                                                    containerColor = if (isMe) TeaLeafGreen else Color(0xFFEEEEEE)
                                                                ),
                                                                shape = RoundedCornerShape(
                                                                    topStart = 12.dp,
                                                                    topEnd = 12.dp,
                                                                    bottomStart = if (isMe) 12.dp else 0.dp,
                                                                    bottomEnd = if (isMe) 0.dp else 12.dp
                                                                ),
                                                                modifier = Modifier.widthIn(max = 140.dp)
                                                            ) {
                                                                Text(
                                                                    text = msg.text,
                                                                    color = if (isMe) Color.White else Color.Black,
                                                                    fontSize = 11.sp,
                                                                    modifier = Modifier.padding(8.dp)
                                                                )
                                                            }
                                                        }
                                                    }
                                                }

                                                // Quick Reply Chips
                                                LazyRow(
                                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                                    modifier = Modifier.padding(bottom = 4.dp)
                                                ) {
                                                    val suggestions = listOf("ভাই ১০০% অরিজিনাল", "একটু কম রাখা যাবে", "কনফার্ম করুন")
                                                    items(suggestions) { text ->
                                                        SuggestionChip(
                                                            onClick = {
                                                                activeChat.messages.add(ChatMessageItem("seller", text))
                                                                activeChat.lastMsg = text
                                                                sellerInboxes = sellerInboxes.toList() // Force trigger refresh
                                                            },
                                                            label = { Text(text, fontSize = 9.sp) }
                                                        )
                                                    }
                                                }

                                                // Input Box
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    TextField(
                                                        value = sellerNewChatMessageText,
                                                        onValueChange = { sellerNewChatMessageText = it },
                                                        placeholder = { Text("উত্তর লিখুন...", fontSize = 11.sp) },
                                                        modifier = Modifier
                                                            .weight(1f)
                                                            .height(38.dp),
                                                        colors = TextFieldDefaults.colors(
                                                            focusedContainerColor = Color.White,
                                                            unfocusedContainerColor = Color.White,
                                                            focusedIndicatorColor = Color.Transparent,
                                                            unfocusedIndicatorColor = Color.Transparent
                                                        ),
                                                        singleLine = true
                                                    )
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    IconButton(
                                                        onClick = {
                                                            if (sellerNewChatMessageText.isNotBlank()) {
                                                                val isViolation = checkAndApplyFine(sellerNewChatMessageText, false)
                                                                if (isViolation) {
                                                                    Toast.makeText(context, "⚠️ নিয়ম লঙ্ঘন! অ্যাপের বাইরে লেনদেন বা অশালীন কথার জন্য ৫০০ টাকা জরিমানা কাটা হয়েছে। পুনরায় করলে আইডি স্থগিত বা ব্যান হবে।", Toast.LENGTH_LONG).show()
                                                                    sellerNewChatMessageText = ""
                                                                    return@IconButton
                                                                }
                                                                activeChat.messages.add(ChatMessageItem("seller", sellerNewChatMessageText))
                                                                activeChat.lastMsg = sellerNewChatMessageText
                                                                sellerInboxes = sellerInboxes.toList() // Force refresh
                                                                sellerNewChatMessageText = ""
                                                            }
                                                        },
                                                        modifier = Modifier.size(32.dp)
                                                    ) {
                                                        Icon(Icons.Default.Send, contentDescription = "Send", tint = TeaLeafGreen, modifier = Modifier.size(18.dp))
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                "disputes" -> {
                                    LazyColumn(
                                        verticalArrangement = Arrangement.spacedBy(10.dp),
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        item {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text("অভিযোগ ও বিবাদ নিষ্পত্তি কেন্দ্র (Escrow Court)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Red)
                                                Icon(Icons.Default.Gavel, contentDescription = null, tint = Color.Red)
                                            }
                                        }

                                        if (sellerDisputes.isEmpty()) {
                                            item {
                                                Box(modifier = Modifier.fillMaxWidth().padding(30.dp), contentAlignment = Alignment.Center) {
                                                    Text("কোনো অভিযোগ বিচারাধীন নেই। ধন্যবাদ!", color = Color.Gray, fontSize = 12.sp)
                                                }
                                            }
                                        }

                                        items(sellerDisputes) { dispute ->
                                            Card(
                                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9F9)),
                                                border = BorderStroke(1.dp, Color.Red.copy(alpha = 0.2f)),
                                                shape = RoundedCornerShape(12.dp),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Column(modifier = Modifier.padding(12.dp)) {
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.SpaceBetween
                                                    ) {
                                                        Text("মামলা নম্বর: ${dispute.id}", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color.Red)
                                                        Text(
                                                            text = when (dispute.status) {
                                                                "AWAITING_COUNTER" -> "⏳ জবাবের অপেক্ষায়"
                                                                "ADMIN_REVIEWING" -> "⚙️ এডমিন জুরি রিভিউ"
                                                                "RESOLVED_SELLER_WON" -> "✓ সেলার জয়ী (৳ ওয়ালেটে যুক্ত)"
                                                                else -> "❌ কাস্টমার জয়ী (জরিমানা কাটা হয়েছে)"
                                                            },
                                                            fontSize = 11.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = when (dispute.status) {
                                                                "RESOLVED_SELLER_WON" -> Color(0xFF2E7D32)
                                                                "RESOLVED_BUYER_WON" -> Color.Red
                                                                else -> Color(0xFFFF9900)
                                                            }
                                                        )
                                                    }

                                                    Spacer(modifier = Modifier.height(4.dp))
                                                    Text("ক্রেতার নাম: ${dispute.buyerName}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                                    Text("পণ্যের নাম: ${dispute.productTitle}", fontSize = 11.sp, color = Color.DarkGray)
                                                    Text("ক্রেতার অভিযোগ: \"${dispute.complaint}\"", fontSize = 11.sp, color = Color.Red, fontWeight = FontWeight.Bold)

                                                    Spacer(modifier = Modifier.height(6.dp))

                                                    if (dispute.status == "AWAITING_COUNTER") {
                                                        OutlinedTextField(
                                                            value = sellerCounterClaimTextInput,
                                                            onValueChange = { sellerCounterClaimTextInput = it },
                                                            placeholder = { Text("আপনার অরিজিনালিটির প্রমাণ ও জবাব লিখুন...", fontSize = 11.sp) },
                                                            modifier = Modifier.fillMaxWidth(),
                                                            maxLines = 2
                                                        )

                                                        Spacer(modifier = Modifier.height(8.dp))

                                                        Button(
                                                            onClick = {
                                                                if (sellerCounterClaimTextInput.isBlank()) {
                                                                    Toast.makeText(context, "অনুগ্রহ করে আপনার অরিজিনালিটির প্রমাণ লিখুন!", Toast.LENGTH_SHORT).show()
                                                                    return@Button
                                                                }
                                                                dispute.status = "ADMIN_REVIEWING"
                                                                dispute.evidenceText = sellerCounterClaimTextInput
                                                                selectedDisputeForAction = dispute
                                                                
                                                                // Launch Court Simulation
                                                                isSellerArbitrationSimulating = true
                                                                arbitrationProgress = 0f
                                                            },
                                                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                                                            modifier = Modifier.fillMaxWidth()
                                                        ) {
                                                            Icon(Icons.Default.VerifiedUser, contentDescription = null, size = 16.dp)
                                                            Spacer(modifier = Modifier.width(6.dp))
                                                            Text("প্রমাণের মাধ্যমে রিপোর্টের প্রতিদ্বন্দ্বিতা করুন (Escrow Court)", fontSize = 11.sp)
                                                        }
                                                    } else if (dispute.status == "ADMIN_REVIEWING") {
                                                        // Circular Progress Bar Simulation
                                                        Column(
                                                            modifier = Modifier.fillMaxWidth().padding(8.dp),
                                                            horizontalAlignment = Alignment.CenterHorizontally
                                                        ) {
                                                            CircularProgressIndicator(color = Color.Red)
                                                            Spacer(modifier = Modifier.height(4.dp))
                                                            Text("এডমিন জুরি কন্টেন্ট, চ্যাট রেকর্ড এবং শিপিং লেবেল অডিট করছে...", fontSize = 10.sp, color = Color.Gray)
                                                        }
                                                    } else {
                                                        Card(
                                                            colors = CardDefaults.cardColors(containerColor = if (dispute.status == "RESOLVED_SELLER_WON") Color(0xFFE8F5E9) else Color(0xFFFFEBEE)),
                                                            shape = RoundedCornerShape(8.dp),
                                                            modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                                                        ) {
                                                            Text(
                                                                text = if (dispute.status == "RESOLVED_SELLER_WON") {
                                                                    "✓ এডমিন রায় দিয়েছে: সেলার নির্দোষ। মিথ্যা অভিযোগ করায় ক্রেতাকে ২৫০ টাকা জরিমানা করা হয়েছে এবং সেলার ওয়ালেটে ফান্ড রিলিজড।"
                                                                } else {
                                                                    "❌ এডমিন রায় দিয়েছে: সেলারের প্রমান অসম্পূর্ণ বা ত্রুটিপূর্ণ। সেলারকে ৫০০ টাকা জরিমানা করা হয়েছে এবং আইডি স্ট্রাইক দেওয়া হয়েছে।"
                                                                },
                                                                fontSize = 11.sp,
                                                                color = if (dispute.status == "RESOLVED_SELLER_WON") Color(0xFF2E7D32) else Color.Red,
                                                                lineHeight = 15.sp,
                                                                modifier = Modifier.padding(8.dp)
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
                    }
                }
            }
        }
    }

    // --- Court Juries Simulation Delay ---
    LaunchedEffect(isSellerArbitrationSimulating) {
        if (isSellerArbitrationSimulating) {
            delay(2000) // Admin auditing timeline
            isSellerArbitrationSimulating = false
            
            val dispute = selectedDisputeForAction
            if (dispute != null) {
                // Determine Winner based on counter-claim length as fun gamified logic
                val sellerWins = sellerCounterClaimTextInput.length > 20
                if (sellerWins) {
                    dispute.status = "RESOLVED_SELLER_WON"
                    sellerWalletBalance += 250.0 // Released escrow money and Buyer fine reward
                    Toast.makeText(context, "✓ অভিনন্দন! আপনি মামলা জিতেছেন! ক্রেতার জরিমানা থেকে ২৫০ টাকা বোনাস পেলেন।", Toast.LENGTH_LONG).show()
                } else {
                    dispute.status = "RESOLVED_BUYER_WON"
                    sellerWalletBalance -= 500.0 // Deduction
                    sellerViolationStrikes += 1
                    if (sellerViolationStrikes >= 3) {
                        isSellerShopSuspended = true
                    }
                    Toast.makeText(context, "❌ সতর্কবার্তা: আপনার প্রমান বাতিল হয়েছে। ৫০০ টাকা জরিমানা এবং ১টি স্ট্রাইক যুক্ত হয়েছে!", Toast.LENGTH_LONG).show()
                }
                sellerCounterClaimTextInput = ""
                sellerDisputes = sellerDisputes.toMutableList() // refresh
            }
        }
    }

    // --- 💬 DIRECT BUYER-SELLER LIVE CHAT MODAL (OUTSIDE MESSENGER) ---
    showDirectChatWithSeller?.let { sellerName ->
        Dialog(onDismissRequest = { showDirectChatWithSeller = null }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(450.dp)
                    .padding(12.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF004D40))
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Forum, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(sellerName, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Text("অফিসিয়াল বিক্রেতা • সক্রিয়", fontSize = 10.sp, color = Color.White.copy(alpha = 0.7f))
                            }
                        }
                        IconButton(onClick = { showDirectChatWithSeller = null }) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                        }
                    }

                    // Messages View
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(directChatMessages) { msg ->
                            val isMe = msg.sender == "buyer"
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = if (isMe) Alignment.CenterEnd else Alignment.CenterStart
                            ) {
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isMe) Color(0xFF004D40) else Color(0xFFECEFF1)
                                    ),
                                    shape = RoundedCornerShape(
                                        topStart = 12.dp,
                                        topEnd = 12.dp,
                                        bottomStart = if (isMe) 12.dp else 0.dp,
                                        bottomEnd = if (isMe) 0.dp else 12.dp
                                    ),
                                    modifier = Modifier.widthIn(max = 200.dp)
                                ) {
                                    Text(
                                        text = msg.text,
                                        color = if (isMe) Color.White else Color.Black,
                                        fontSize = 11.sp,
                                        modifier = Modifier.padding(8.dp)
                                    )
                                }
                            }
                        }
                    }

                    // App Guard Policy Warning
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                        shape = RoundedCornerShape(0.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "🔒 অ্যাপ গার্ড পলিসি: চ্যাটে মোবাইল নাম্বার বা অ্যাপের বাইরে লেনদেন এবং কোনো অশালীন বা মন্দ কথা বললে সাথে সাথে ৫০০ টাকা জরিমানা কাটা হবে এবং আইডি চিরতরে ব্যান হবে।",
                            color = Color(0xFFC62828),
                            fontSize = 8.5.sp,
                            lineHeight = 12.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    // Input Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextField(
                            value = directChatInputText,
                            onValueChange = { directChatInputText = it },
                            placeholder = { Text("মেসেজ লিখুন...", fontSize = 11.sp) },
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFFF5F5F5),
                                unfocusedContainerColor = Color(0xFFF5F5F5),
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        IconButton(
                            onClick = {
                                if (directChatInputText.isNotBlank()) {
                                    val isViolation = checkAndApplyFine(directChatInputText, true)
                                    if (isViolation) {
                                        Toast.makeText(context, "⚠️ নিয়ম লঙ্ঘন! অ্যাপের বাইরে লেনদেন বা অশালীন কথার জন্য ৫০০ টাকা জরিমানা কাটা হয়েছে। পুনরায় করলে আইডি চিরতরে ব্যান হবে।", Toast.LENGTH_LONG).show()
                                        directChatInputText = ""
                                        return@IconButton
                                    }
                                    
                                    val newMsg = ChatMessageItem("buyer", directChatInputText)
                                    directChatMessages = (directChatMessages + newMsg).toMutableList()
                                    
                                    val userText = directChatInputText
                                    directChatInputText = ""
                                    
                                    // Generate auto reply
                                    val replyText = when {
                                        userText.contains("দাম") || userText.contains("price") -> "ভাই দাম একদম ফিক্সড, অরিজিনাল কন্টেন্ট ও অফিশিয়াল কোয়ালিটি ওয়ারেন্টি সহ পাবেন।"
                                        userText.contains("ডেলিভারি") || userText.contains("কবে") -> "অর্ডার করার ৪৮ ঘন্টার মধ্যে ডেলিভারিম্যান আপনার ঠিকানায় পার্সেল পৌছে দেবে।"
                                        else -> "ধন্যবাদ ভাই, আপনি অর্ডার সাবমিট করুন। আমরা দ্রুত প্রসেস করে পাঠিয়ে দেব।"
                                    }
                                    
                                    // Delayed reply trigger
                                    kotlin.concurrent.thread {
                                        Thread.sleep(1200)
                                        directChatMessages = (directChatMessages + ChatMessageItem("seller", replyText)).toMutableList()
                                    }
                                }
                            },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(Icons.Default.Send, contentDescription = "Send", tint = Color(0xFF004D40), modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }
        }
    }

    // --- 🚨 FULL SCREEN BAN SCREEN COVER ---
    if (isAppBanned) {
        Dialog(onDismissRequest = {}) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.85f)
                    .padding(16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFB71C1C))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.ReportGmailerrorred,
                        contentDescription = "Banned",
                        tint = Color.White,
                        modifier = Modifier.size(80.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "আপনার আইডি ব্যান করা হয়েছে!",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = appBannedReason,
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.9f),
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "নিয়ম লঙ্ঘনকারী আইডির জরিমানা: ৫০০ টাকা\nঅবशिष्ट ওয়ালেট ব্যালেন্স: ৳$userWalletBalance BDT",
                        fontSize = 13.sp,
                        color = Color.Yellow,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(30.dp))
                    Text(
                        text = "কোনো প্রশ্ন থাকলে অনুগ্রহ করে আমাদের সাপোর্ট টিমে যোগাযোগ করুন।",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }

    // --- 💻 FIVERR-STYLE FREELANCE GIG DETAILS & ESCROW ORDER DIALOG ---
    selectedGigForDetail?.let { gig ->
        var selectedTier by remember { mutableStateOf("basic") } // "basic", "medium", "pro"
        var activeDemoTab by remember { mutableStateOf("img1") } // "img1", "img2", "video"
        var isVideoPlaying by remember { mutableStateOf(false) }

        Dialog(onDismissRequest = { selectedGigForDetail = null }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.95f)
                    .padding(4.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(14.dp)
                ) {
                    // Header Area
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = Color(0xFFE0F7FA),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "💻 ডিজিটাল সেবা ও গিগ গ্যারান্টি",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF006064),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }

                        IconButton(onClick = { selectedGigForDetail = null }) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            // Demo Media Area (Images and Video)
                            Column {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(180.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(Color.Black),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (activeDemoTab == "img1") {
                                        AsyncImage(
                                            model = gig.imageUrl1,
                                            contentDescription = null,
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                    } else if (activeDemoTab == "img2") {
                                        AsyncImage(
                                            model = gig.imageUrl2,
                                            contentDescription = null,
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                    } else {
                                        // Simulated Video Player
                                        Column(
                                            modifier = Modifier.fillMaxSize(),
                                            verticalArrangement = Arrangement.Center,
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Icon(
                                                imageVector = if (isVideoPlaying) Icons.Default.PauseCircle else Icons.Default.PlayCircle,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier
                                                    .size(54.dp)
                                                    .clickable { isVideoPlaying = !isVideoPlaying }
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                text = if (isVideoPlaying) "ভিডিও ডেমো চলছে..." else "ভিডিও ডেমো প্লে করুন",
                                                color = Color.White,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                // Media Tabs
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Button(
                                        onClick = { activeDemoTab = "img1"; isVideoPlaying = false },
                                        colors = ButtonDefaults.buttonColors(containerColor = if (activeDemoTab == "img1") TeaLeafGreen else Color(0xFFEEEEEE)),
                                        modifier = Modifier.weight(1f).height(32.dp),
                                        contentPadding = PaddingValues(0.dp)
                                    ) {
                                        Text("ছবি ১ 🖼️", fontSize = 10.sp, color = if (activeDemoTab == "img1") Color.White else Color.Black)
                                    }

                                    Button(
                                        onClick = { activeDemoTab = "img2"; isVideoPlaying = false },
                                        colors = ButtonDefaults.buttonColors(containerColor = if (activeDemoTab == "img2") TeaLeafGreen else Color(0xFFEEEEEE)),
                                        modifier = Modifier.weight(1f).height(32.dp),
                                        contentPadding = PaddingValues(0.dp)
                                    ) {
                                        Text("ছবি ২ 🖼️", fontSize = 10.sp, color = if (activeDemoTab == "img2") Color.White else Color.Black)
                                    }

                                    Button(
                                        onClick = { activeDemoTab = "video" },
                                        colors = ButtonDefaults.buttonColors(containerColor = if (activeDemoTab == "video") Color.Red else Color(0xFFEEEEEE)),
                                        modifier = Modifier.weight(1f).height(32.dp),
                                        contentPadding = PaddingValues(0.dp)
                                    ) {
                                        Text("ভিডিও 🎥", fontSize = 10.sp, color = if (activeDemoTab == "video") Color.White else Color.Black)
                                    }
                                }
                            }
                        }

                        item {
                            // Title & Freelancer row
                            Text(
                                text = gig.title,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                lineHeight = 19.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.AccountCircle, contentDescription = null, tint = TeaLeafGreen, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(gig.freelancerName, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFF9900), modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text("${gig.rating}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                                }
                            }
                        }

                        item {
                            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.4f))
                        }

                        item {
                            // Description Section
                            Text("গিগ বিবরণ (Description):", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                            Text(
                                text = gig.description,
                                fontSize = 11.sp,
                                color = Color.DarkGray,
                                lineHeight = 15.sp
                            )
                        }

                        item {
                            // Scope of Work
                            Text("কাজের আওতা ও সুবিধাসমূহ (Scope of Work):", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFAFAFA)),
                                border = BorderStroke(0.5.dp, Color.LightGray),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = gig.workScope,
                                    fontSize = 11.sp,
                                    color = Color.DarkGray,
                                    lineHeight = 15.sp,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }

                        item {
                            // Fiverr-style Pricing Packages tabs selector (Basic, Medium, Pro)
                            Text("সার্ভিস প্যাকেজ সিলেক্ট করুন (৩টি টিয়ার):", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                listOf("basic" to "সাধারণ", "medium" to "মিডিয়াম", "pro" to "প্রো").forEach { (tierId, label) ->
                                    val isSelected = selectedTier == tierId
                                    val price = when (tierId) {
                                        "basic" -> gig.priceBasicBdt
                                        "medium" -> gig.priceMediumBdt
                                        else -> gig.priceProBdt
                                    }
                                    Button(
                                        onClick = { selectedTier = tierId },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isSelected) Color(0xFF004D40) else Color(0xFFECEFF1)
                                        ),
                                        modifier = Modifier.weight(1f).height(44.dp),
                                        shape = RoundedCornerShape(10.dp),
                                        contentPadding = PaddingValues(0.dp)
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(label, fontSize = 10.sp, color = if (isSelected) Color.White else Color.Black, fontWeight = FontWeight.Bold)
                                            Text("৳${price.toInt()}", fontSize = 11.sp, color = if (isSelected) Color.Yellow else Color.DarkGray, fontWeight = FontWeight.Black)
                                        }
                                    }
                                }
                            }
                        }

                        item {
                            // Show current package summary details
                            val currentPrice = when (selectedTier) {
                                "basic" -> gig.priceBasicBdt
                                "medium" -> gig.priceMediumBdt
                                else -> gig.priceProBdt
                            }
                            val currentDays = when (selectedTier) {
                                "basic" -> gig.deliveryDaysBasic
                                "medium" -> gig.deliveryDaysMedium
                                else -> gig.deliveryDaysPro
                            }
                            
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9)),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Schedule, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("ডেলিভারি সময়: $currentDays দিন", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = TeaLeafGreen, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("নির্ধারিত মূল্য: ৳${currentPrice.toInt()} BDT", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TeaLeafGreen)
                                    }
                                }
                            }
                        }

                        item {
                            // COMMISSION SYSTEM DETAILS
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text(
                                        text = "📈 নবচেতনা কমিশন ও চার্জ নীতি:",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFE65100)
                                    )
                                    Text(
                                        text = "১. ডিজিটাল সেবা বিক্রির জন্য ৬% এসক্রো ফি কর্তন করা হবে (বিক্রেতা পাবেন ৯৪%)।\n২. শারীরিক প্রোডাক্ট বিক্রির জন্য ৩% কর্তন করা হবে (বিক্রেতা পাবেন ৯৭%)。\n৩. ক্রেতা থেকে এক টাকাও কম বা বেশি কাটা হবে না (শতভাগ স্বচ্ছ)।",
                                        fontSize = 9.sp,
                                        color = Color.DarkGray,
                                        lineHeight = 13.sp
                                    )
                                }
                            }
                        }

                        item {
                            // Contact Freelancer Action first to clarify
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFE0F2F1)),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("আগে ফ্রিল্যান্সারের সাথে কথা বলুন!", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF004D40))
                                        Text("প্রজেক্টের কোনো কাস্টম ডিমান্ড থাকলে চ্যাট করে সব ক্লিয়ার করে নিন।", fontSize = 8.5.sp, color = Color.DarkGray)
                                    }
                                    Button(
                                        onClick = {
                                            showDirectChatWithSeller = gig.freelancerName
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00796B)),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                        modifier = Modifier.height(30.dp)
                                    ) {
                                        Text("চ্যাট করুন 💬", fontSize = 10.sp, color = Color.White)
                                    }
                                }
                            }
                        }

                        item {
                            // Escrow Order Form requirements
                            Text("💻 ফ্রিল্যান্সারের জন্য কাজের রিকোয়ারমেন্ট লিখুন*", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                            OutlinedTextField(
                                value = gigRequirementInput,
                                onValueChange = { gigRequirementInput = it },
                                placeholder = { Text("যেমন: আমার ব্র্যান্ডের লোগো লাগবে, টেক্সট হবে 'নবচেতনা ক্যাফে', কালার হবে গোল্ডেন...", fontSize = 10.sp) },
                                modifier = Modifier.fillMaxWidth(),
                                maxLines = 3
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Purchase/Order Button with exact pricing system
                    val costBdt = when (selectedTier) {
                        "basic" -> gig.priceBasicBdt
                        "medium" -> gig.priceMediumBdt
                        else -> gig.priceProBdt
                    }.toInt()

                    Button(
                        onClick = {
                            if (gigRequirementInput.isBlank()) {
                                Toast.makeText(context, "দয়া করে কাজের নির্দেশনা ও রিকোয়ারমেন্ট পূরণ করুন!", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            if (userWalletBalance < costBdt) {
                                Toast.makeText(context, "দুঃখিত! আপনার ওয়ালেট ব্যালেন্স পর্যাপ্ত নয়। অনুগ্রহ করে রিচার্জ করুন।", Toast.LENGTH_LONG).show()
                                return@Button
                            }

                            // Deduct exact wallet money
                            userWalletBalance -= costBdt

                            // Create an escrow digital order
                            val newOrder = EscrowOrder(
                                id = "DIG-ORD-${(1000..9999).random()}",
                                buyerName = "Anika Rahman (Buyer)",
                                buyerAddress = "Digital Project Workspace",
                                buyerPhone = "Requirements: $gigRequirementInput",
                                productId = gig.id.hashCode(),
                                productTitle = "💻 Freelance [${selectedTier.uppercase()}]: ${gig.title}",
                                amount = costBdt / 120.0, // convert BDT to virtual USD or keep equivalent BDT
                                status = EscrowOrderStatus.HOLD,
                                timestamp = "2026-09-28 00:46"
                            )

                            // Add to received orders and escrow list
                            receivedOrders = receivedOrders + newOrder
                            
                            selectedGigForDetail = null
                            gigRequirementInput = ""
                            showMyOrdersModal = true
                            Toast.makeText(context, "✓ গিগ অর্ডার সফল! ৳$costBdt BDT এসক্রো ভল্টে লক করা হয়েছে। কাজ পেয়ে সম্মতি দিলে টাকা রিলিজ হবে।", Toast.LENGTH_LONG).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C853)),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Payment, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("গিগ অর্ডার করুন (🔒 এসক্রো পেমেন্ট: ৳$costBdt)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }

    // --- 💻 FIVERR-STYLE NEW GIG PUBLICATION DIALOG ---
    if (showAddGigDialog) {
        var newGigTitle by remember { mutableStateOf("") }
        var newGigCategory by remember { mutableStateOf("Graphics Design") }
        var newGigPriceBasicBdt by remember { mutableStateOf("1500") }
        var newGigPriceMediumBdt by remember { mutableStateOf("3500") }
        var newGigPriceProBdt by remember { mutableStateOf("8000") }
        var newGigDesc by remember { mutableStateOf("") }
        var newGigWorkScope by remember { mutableStateOf("") }
        var newGigImage1 by remember { mutableStateOf("") }
        var newGigImage2 by remember { mutableStateOf("") }
        var newGigVideo by remember { mutableStateOf("") }
        var newGigDeliveryBasic by remember { mutableStateOf("3") }
        var newGigDeliveryMedium by remember { mutableStateOf("5") }
        var newGigDeliveryPro by remember { mutableStateOf("7") }

        Dialog(onDismissRequest = { showAddGigDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.95f)
                    .padding(10.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("💻 নতুন ডিজিটাল গিগ প্রকাশ করুন", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        IconButton(onClick = { showAddGigDialog = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                        }
                    }
                    
                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))

                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            OutlinedTextField(
                                value = newGigTitle,
                                onValueChange = { newGigTitle = it },
                                label = { Text("গিগের শিরোনাম*", fontSize = 11.sp) },
                                placeholder = { Text("যেমন: I will design stunning modern vector logo for your startup", fontSize = 10.sp) },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                        }

                        item {
                            Text("সেবা ক্যাটাগরি নির্বাচন করুন*", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            val catOptions = listOf("Graphics Design", "Web Development", "Digital Marketing", "Video Editing", "SEO & SMM")
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                catOptions.forEach { opt ->
                                    val isS = newGigCategory == opt
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isS) Color(0xFF00B0FF) else Color(0xFFEEEEEE))
                                            .clickable { newGigCategory = opt }
                                            .padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Text(opt, fontSize = 9.5.sp, color = if (isS) Color.White else Color.Black, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        item {
                            OutlinedTextField(
                                value = newGigDesc,
                                onValueChange = { newGigDesc = it },
                                label = { Text("সার্ভিস বিবরণী (Description)*", fontSize = 11.sp) },
                                placeholder = { Text("আপনার সেবা সম্পর্কে বিস্তারিত আলোচনা করুন...", fontSize = 10.sp) },
                                modifier = Modifier.fillMaxWidth(),
                                maxLines = 4
                            )
                        }

                        item {
                            OutlinedTextField(
                                value = newGigWorkScope,
                                onValueChange = { newGigWorkScope = it },
                                label = { Text("কাজের বিবরণী (Scope of Work)*", fontSize = 11.sp) },
                                placeholder = { Text("প্যাকেজে কী কী কাজ করে দেবেন তার নির্দিষ্ট তথ্য...", fontSize = 10.sp) },
                                modifier = Modifier.fillMaxWidth(),
                                maxLines = 3
                            )
                        }

                        item {
                            Text("কাজের ডেমো লিঙ্কসমূহ (ফটো ও ভিডিও):", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        item {
                            OutlinedTextField(
                                value = newGigImage1,
                                onValueChange = { newGigImage1 = it },
                                label = { Text("ডেমো ছবি ১ URL (ঐচ্ছিক)", fontSize = 10.sp) },
                                placeholder = { Text("https://example.com/demo1.jpg", fontSize = 10.sp) },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                        }

                        item {
                            OutlinedTextField(
                                value = newGigImage2,
                                onValueChange = { newGigImage2 = it },
                                label = { Text("ডেমো ছবি ২ URL (ঐচ্ছিক)", fontSize = 10.sp) },
                                placeholder = { Text("https://example.com/demo2.jpg", fontSize = 10.sp) },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                        }

                        item {
                            OutlinedTextField(
                                value = newGigVideo,
                                onValueChange = { newGigVideo = it },
                                label = { Text("ডেমো ভিডিও URL (ঐচ্ছিক)", fontSize = 10.sp) },
                                placeholder = { Text("https://example.com/video.mp4", fontSize = 10.sp) },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                        }

                        item {
                            Text("সার্ভিস চার্জ ও প্যাকেজসমূহ (৳ BDT):", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text("💡 সতর্কতা: দাম পরিবর্তন করলে তা সকলের জন্য প্রযোজ্য হবে। ক্রেতা থেকে এক টাকাও কম বা বেশি কাটা হবে না।", fontSize = 9.sp, color = Color.Gray)
                        }

                        item {
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                OutlinedTextField(
                                    value = newGigPriceBasicBdt,
                                    onValueChange = { newGigPriceBasicBdt = it },
                                    label = { Text("সাধারণ (Basic)", fontSize = 10.sp) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )

                                OutlinedTextField(
                                    value = newGigPriceMediumBdt,
                                    onValueChange = { newGigPriceMediumBdt = it },
                                    label = { Text("মিডিয়াম (Medium)", fontSize = 10.sp) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )

                                OutlinedTextField(
                                    value = newGigPriceProBdt,
                                    onValueChange = { newGigPriceProBdt = it },
                                    label = { Text("প্রো (Premium)", fontSize = 10.sp) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )
                            }
                        }

                        item {
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                OutlinedTextField(
                                    value = newGigDeliveryBasic,
                                    onValueChange = { newGigDeliveryBasic = it },
                                    label = { Text("Basic ডেলিভারি (দিন)", fontSize = 10.sp) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )

                                OutlinedTextField(
                                    value = newGigDeliveryMedium,
                                    onValueChange = { newGigDeliveryMedium = it },
                                    label = { Text("Medium ডেলিভারি (দিন)", fontSize = 10.sp) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )

                                OutlinedTextField(
                                    value = newGigDeliveryPro,
                                    onValueChange = { newGigDeliveryPro = it },
                                    label = { Text("Pro ডেলিভারি (দিন)", fontSize = 10.sp) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showAddGigDialog = false },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("বাতিল", fontSize = 11.sp)
                        }

                        Button(
                            onClick = {
                                val bPrice = newGigPriceBasicBdt.toDoubleOrNull()
                                val mPrice = newGigPriceMediumBdt.toDoubleOrNull()
                                val pPrice = newGigPriceProBdt.toDoubleOrNull()
                                
                                if (newGigTitle.isBlank() || bPrice == null || mPrice == null || pPrice == null || newGigDesc.isBlank() || newGigWorkScope.isBlank()) {
                                    Toast.makeText(context, "দয়া করে তারকা চিহ্নিত সকল ঘর সঠিক তথ্য দিয়ে পূরণ করুন!", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }

                                val finalImg1 = if (newGigImage1.isNotBlank()) newGigImage1 else "https://images.unsplash.com/photo-1626785774573-4b799315345d?w=500"
                                val finalImg2 = if (newGigImage2.isNotBlank()) newGigImage2 else "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=500"
                                val finalVid = if (newGigVideo.isNotBlank()) newGigVideo else "https://www.w3schools.com/html/mov_bbb.mp4"

                                val customGig = FreelanceGig(
                                    id = "GIG-USR-${(100..999).random()}",
                                    title = newGigTitle,
                                    freelancerName = "Anika Rahman (Freelance Expert)",
                                    rating = 5.0f,
                                    description = newGigDesc,
                                    workScope = newGigWorkScope,
                                    category = newGigCategory,
                                    imageUrl1 = finalImg1,
                                    imageUrl2 = finalImg2,
                                    videoUrl = finalVid,
                                    priceBasicBdt = bPrice,
                                    priceMediumBdt = mPrice,
                                    priceProBdt = pPrice,
                                    deliveryDaysBasic = newGigDeliveryBasic.toIntOrNull() ?: 3,
                                    deliveryDaysMedium = newGigDeliveryMedium.toIntOrNull() ?: 5,
                                    deliveryDaysPro = newGigDeliveryPro.toIntOrNull() ?: 7
                                )

                                userFreelanceGigs = userFreelanceGigs + customGig
                                showAddGigDialog = false
                                Toast.makeText(context, "✓ অভিনন্দন! আপনার ফ্রিল্যান্স গিগটি সফলভাবে প্রকাশ করা হয়েছে!", Toast.LENGTH_LONG).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C853)),
                            modifier = Modifier.weight(1.5f)
                        ) {
                            Text("গিগ পাবলিশ করুন 🚀", fontSize = 11.sp, color = Color.White)
                        }
                    }
                }
            }
        }
    }

    // --- 💸 MANUAL MOBILE BANKING WITHDRAWAL MODAL ---
    if (showWithdrawModal) {
        Dialog(onDismissRequest = { showWithdrawModal = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.92f)
                    .padding(8.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = TeaLeafGreen, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("মোবাইল ব্যাংকিং টাকা উত্তোলন", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                        IconButton(onClick = { showWithdrawModal = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                        }
                    }

                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))

                    // Explanation of Manual System
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.HourglassEmpty,
                                contentDescription = null,
                                tint = Color(0xFFE65100),
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "🔒 নিরাপত্তা যাচাই চলছে...",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = Color(0xFFE65100)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "নিরাপত্তা যাচাই চলছে, উইথড্র সম্পূর্ণ হতে সর্বোচ্চ ২৪ ঘণ্টা সময় লাগতে পারে। দয়া করে অপেক্ষা করুন।",
                                    fontSize = 11.sp,
                                    color = Color.DarkGray,
                                    lineHeight = 15.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            Text("১. গেটওয়ে নির্বাচন করুন (Select Gateway)*", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                listOf("bKash" to "বিকাশ 👑", "Nagad" to "নগদ 🍊", "Rocket" to "রকেট 🔮").forEach { (gateId, label) ->
                                    val isS = selectedWithdrawGateway == gateId
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(if (isS) TeaLeafGreen else Color(0xFFECEFF1))
                                            .clickable { selectedWithdrawGateway = gateId }
                                            .padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(label, fontSize = 10.sp, color = if (isS) Color.White else Color.Black, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        item {
                            Text("২. উত্তোলন পরিমাণ (Amount BDT)*", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text("সর্বনিম্ন উইথড্র সীমা: ৳৫০০ টাকা | বর্তমান ব্যালেন্স: ৳${String.format(Locale.US, "%,.2f", sellerWalletBalance)}", fontSize = 9.sp, color = Color.Gray)
                            OutlinedTextField(
                                value = withdrawAmountBdt,
                                onValueChange = { withdrawAmountBdt = it },
                                placeholder = { Text("যেমন: ১০০০", fontSize = 11.sp) },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )
                        }

                        item {
                            Text("৩. মোবাইল ব্যাংকিং নাম্বার (Personal)*", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            OutlinedTextField(
                                value = withdrawMobileNumber,
                                onValueChange = { withdrawMobileNumber = it },
                                placeholder = { Text("০১৭XXXXXXXX", fontSize = 11.sp) },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                            )
                        }

                        item {
                            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = Color.LightGray.copy(alpha = 0.5f))
                            Text("উত্তোলন হিস্টোরি (Transactions Status):", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        }

                        items(withdrawalTransactionsList) { tx ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text("${tx.gateway} [${tx.mobileNumber}]", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Surface(
                                                color = if (tx.status == "Completed") Color(0xFFE8F5E9) else Color(0xFFFFE0B2),
                                                shape = RoundedCornerShape(4.dp)
                                            ) {
                                                Text(
                                                    text = if (tx.status == "Completed") "সম্পন্ন" else "পেন্ডিং",
                                                    color = if (tx.status == "Completed") Color(0xFF2E7D32) else Color(0xFFE65100),
                                                    fontSize = 8.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                                )
                                            }
                                        }
                                        Text(tx.timestamp, fontSize = 8.sp, color = Color.Gray)
                                    }
                                    Text("৳${tx.amountBdt.toInt()} BDT", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TeaLeafGreen)
                                }
                            }
                        }
                    }

                    // Withdraw Submission Button
                    Button(
                        onClick = {
                            val amount = withdrawAmountBdt.toDoubleOrNull()
                            if (amount == null || amount < 500) {
                                Toast.makeText(context, "দুঃখিত! সর্বনিম্ন উইথড্র সীমা ৫০০ টাকা।", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            if (withdrawMobileNumber.isBlank() || withdrawMobileNumber.length < 11) {
                                Toast.makeText(context, "দয়া করে সঠিক ১১ ডিজিটের সচল মোবাইল নাম্বার দিন!", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            if (sellerWalletBalance < amount) {
                                Toast.makeText(context, "দুঃখিত! আপনার সেলার ওয়ালেট ব্যালেন্স পর্যাপ্ত নয়।", Toast.LENGTH_LONG).show()
                                return@Button
                            }

                            // Deduct from seller wallet
                            sellerWalletBalance -= amount

                            // Generate manual withdrawal Tx
                            val newTx = WithdrawalTx(
                                id = "TX-${(1000..9999).random()}",
                                gateway = selectedWithdrawGateway,
                                mobileNumber = withdrawMobileNumber,
                                amountBdt = amount,
                                status = "Pending",
                                timestamp = "2026-09-28 01:00"
                            )

                            withdrawalTransactionsList = listOf(newTx) + withdrawalTransactionsList
                            withdrawAmountBdt = ""
                            withdrawMobileNumber = ""
                            Toast.makeText(context, "✓ উইথড্র রিকোয়েস্ট সফল! অ্যাডমিন যাচাই করে ২৪ ঘণ্টার মধ্যে আপনার বিকাশ/নগদ নাম্বারে টাকা পাঠিয়ে দেবেন।", Toast.LENGTH_LONG).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9900)),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Payment, contentDescription = null, tint = Color.Black)
                        Spacer(modifier = Modifier.width(6.0.dp))
                        Text("টাকা উত্তোলনের রিকোয়েস্ট পাঠান 🚀", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                }
            }
        }
    }
                }
            }
        }
    }
}
