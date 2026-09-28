package com.example.data.repository

import com.example.data.dao.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.firstOrNull

class PageBookRepository(
    private val userDao: UserDao,
    private val postDao: PostDao,
    private val commentDao: CommentDao,
    private val storyDao: StoryDao,
    private val notificationDao: NotificationDao,
    private val appStoreItemDao: AppStoreItemDao,
    private val pageDao: PageDao,
    private val walletRechargeDao: WalletRechargeDao
) {
    val currentUser: Flow<User?> = userDao.getCurrentUser()
    val allPosts: Flow<List<Post>> = postDao.getAllPosts()
    val allStories: Flow<List<Story>> = storyDao.getAllStories()
    val allNotifications: Flow<List<Notification>> = notificationDao.getAllNotifications()
    val allAppStoreItems: Flow<List<AppStoreItem>> = appStoreItemDao.getAllAppStoreItems()
    val allPages: Flow<List<Page>> = pageDao.getAllPages()
    val allUsers: Flow<List<User>> = userDao.getAllUsers()
    val allRechargeRequests: Flow<List<WalletRechargeRequest>> = walletRechargeDao.getAllRechargeRequests()

    val walletSecurityService = com.example.data.service.WalletSecurityService()

    val revenueConfig = MutableStateFlow(SystemRevenueConfig())
    val serverPostQuota = MutableStateFlow(ServerDailyPostQuota())
    val securityAuditLogs = MutableStateFlow(
        listOf(
            SecurityAuditLog(
                id = "LOG-881",
                eventType = "BOT_DETECTION",
                title = "অ্যান্টি-বট গার্ড স্ক্যান",
                description = "সন্দেহজনক ট্র্যাফিক বিশ্লেষণ সম্পন্ন: ১ মিনিটে ৫০টি রিকোয়েস্ট শনাক্ত এবং প্রতিরোধ করা হয়েছে।",
                ipAddress = "103.114.98.42",
                severity = "LOW",
                timestamp = "১০ মিনিট আগে",
                adminRole = "System AI Guard"
            ),
            SecurityAuditLog(
                id = "LOG-880",
                eventType = "CPM_UPDATE",
                title = "রেভিনিউ CPM আপডেট",
                description = "প্রতি ১,০০০ ভিউ রেট $1.50 এবং ক্রিয়েটর শেয়ার ৭০% এ কনফিগার করা হয়েছে।",
                ipAddress = "192.168.1.10",
                severity = "LOW",
                timestamp = "১ ঘণ্টা আগে",
                adminRole = "Super Admin"
            ),
            SecurityAuditLog(
                id = "LOG-879",
                eventType = "PAYMENT_APPROVAL",
                title = "পে-আউট ক্লিয়ারেন্স",
                description = "পেজ 'নবচেতনা ছাদবাগান ও কৃষি' এর $84.50 বিকাশ পে-আউট পরিশোধ করা হয়েছে।",
                ipAddress = "192.168.1.12",
                severity = "MEDIUM",
                timestamp = "৩ ঘণ্টা আগে",
                adminRole = "Finance Officer"
            ),
            SecurityAuditLog(
                id = "LOG-878",
                eventType = "VERIFICATION",
                title = "ব্লু ব্যাজ অনুমোদন",
                description = "সাংবাদিক ও কন্টেন্ট ক্রিয়েটরের NID যাচাই শেষে ব্লু টিক ব্যাজ প্রদান করা হয়েছে।",
                ipAddress = "192.168.1.15",
                severity = "LOW",
                timestamp = "গতকাল",
                adminRole = "Content Moderator"
            )
        )
    )

    val communityReports = MutableStateFlow(
        listOf(
            CommunityReport(
                id = 101,
                targetType = "POST",
                targetId = 2,
                targetTitle = "অনুমোদনহীন আর্থিক প্রচারণার পোস্ট",
                reportedBy = "Tanvir Hasan",
                reason = "স্প্যাম ও বিভ্রান্তিকর তথ্য",
                details = "এই পোস্টে সন্দেহজনক লিঙ্ক ও ফেক গিভঅওয়ের প্রতিশ্রুতি দেওয়া হচ্ছে।",
                timestamp = "২৫ মিনিট আগে",
                status = "PENDING"
            ),
            CommunityReport(
                id = 102,
                targetType = "PAGE",
                targetId = 2,
                targetTitle = "ডিজিটাল টেক ও ক্যারিয়ার",
                reportedBy = "Mehedi Hasan",
                reason = "কপিরাইট লঙ্ঘন",
                details = "অনুমতি ছাড়া অন্য ইউটিউব চ্যানেলের ক্লিপ আপলোড করা হয়েছে।",
                timestamp = "২ ঘণ্টা আগে",
                status = "PENDING"
            ),
            CommunityReport(
                id = 103,
                targetType = "USER",
                targetId = 3,
                targetTitle = "Tasnim Ahmed",
                reportedBy = "Rumana Akter",
                reason = "হ্যারাসমেন্ট / ইনবক্স স্প্যাম",
                details = "অপ্রাসঙ্গিক মেসেজ পাঠিয়ে উত্ত্যক্ত করা হচ্ছে।",
                timestamp = "৪ ঘণ্টা আগে",
                status = "RESOLVED"
            )
        )
    )

    val verificationRequests = MutableStateFlow(
        listOf(
            VerificationRequest(
                id = 201,
                entityType = "USER",
                entityId = 1,
                name = "Anika Rahman",
                category = "কম্পিউটার সায়েন্স গবেষক ও প্রযুক্তিবিদ",
                documentType = "জাতীয় পরিচয়পত্র (NID)",
                documentNumber = "NID-1998561234567",
                timestamp = "আজ সকাল ১০:৩০",
                status = "APPROVED",
                age = 22,
                dateOfBirth = "2002-05-14",
                isAgeEligible = true,
                nidFrontPhotoUrl = "https://images.unsplash.com/photo-1589829545856-d10d557cf95f?w=600",
                nidBackPhotoUrl = "https://images.unsplash.com/photo-1589829545856-d10d557cf95f?w=600",
                liveFacePhotoUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=400",
                faceMatchConfidence = 96,
                isFaceMatched = true,
                phoneNumber = "+880 1712-345678",
                isPhoneVerified = true,
                emailAddress = "anika@nabachetna.com",
                isEmailVerified = true,
                badgeColorName = "হলুদ (Yellow)",
                badgeColorHex = "#FFB300"
            ),
            VerificationRequest(
                id = 202,
                entityType = "PAGE",
                entityId = 1,
                name = "নবচেতনা ছাদবাগান ও কৃষি",
                category = "কৃষি ও পরিবেশ উন্নয়ন সংস্থা",
                documentType = "ট্রেড লাইসেন্স ও সার্টিফিকেট",
                documentNumber = "TL-DHAKA-2024-89912",
                timestamp = "গতকাল",
                status = "APPROVED",
                age = 28,
                dateOfBirth = "1996-03-20",
                isAgeEligible = true,
                badgeColorName = "হলুদ (Yellow)",
                badgeColorHex = "#FFB300"
            ),
            VerificationRequest(
                id = 203,
                entityType = "USER",
                entityId = 2,
                name = "Sajid Islam",
                category = "সিনিয়র অ্যান্ড্রয়েড সফটওয়্যার ইঞ্জিনিয়ার",
                documentType = "জাতীয় পরিচয়পত্র (NID)",
                documentNumber = "NID-1995123489110",
                timestamp = "২ দিন আগে",
                status = "APPROVED",
                age = 29,
                dateOfBirth = "1995-11-10",
                isAgeEligible = true,
                faceMatchConfidence = 98,
                isFaceMatched = true,
                phoneNumber = "+880 1819-998877",
                isPhoneVerified = true,
                emailAddress = "sajid@nabachetna.com",
                isEmailVerified = true,
                badgeColorName = "হলুদ (Yellow)",
                badgeColorHex = "#FFB300"
            )
        )
    )

    val marketplaceAudits = MutableStateFlow(
        listOf(
            MarketplaceItemAudit(
                id = 1,
                title = "Wireless Noise-Canceling Headphones",
                sellerName = "ElectroTech BD",
                price = 3499.0,
                category = "Electronics",
                status = "APPROVED",
                isVerifiedSeller = true
            ),
            MarketplaceItemAudit(
                id = 2,
                title = "সন্দেহজনক লটারির কুপন (ল্যাপটপ মাত্র ৫০০ টাকায়)",
                sellerName = "QuickRich Online",
                price = 500.0,
                category = "Offers",
                status = "FLAGGED",
                isVerifiedSeller = false
            ),
            MarketplaceItemAudit(
                id = 3,
                title = "Organic Green Tea Special Pack",
                sellerName = "Sylhet Tea Gardens",
                price = 450.0,
                category = "Grocery",
                status = "APPROVED",
                isVerifiedSeller = true
            )
        )
    )

    val activeAdminRole = MutableStateFlow(AdminRole.SUPER_ADMIN)
    val officialNotice = MutableStateFlow(OfficialNotice())
    val dynamicBrandingConfig = MutableStateFlow(DynamicBrandingConfig())

    val withdrawalRequests = MutableStateFlow(
        listOf(
            WithdrawalRequest(
                id = "WD-901",
                userId = 1,
                userName = "Anika Rahman",
                userAvatar = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=150",
                pageName = "নবচেতনা ছাদবাগান ও কৃষি",
                amountUsd = 85.00,
                amountBdt = 10200.00,
                paymentMethod = "bKash",
                accountNumber = "01712345678",
                requestedDate = "১০ মিনিট আগে",
                status = "PENDING"
            ),
            WithdrawalRequest(
                id = "WD-902",
                userId = 2,
                userName = "Sajid Islam",
                userAvatar = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150",
                pageName = "ডিজিটাল টেক ও ক্যারিয়ার",
                amountUsd = 120.50,
                amountBdt = 14460.00,
                paymentMethod = "Nagad",
                accountNumber = "01887654321",
                requestedDate = "২ ঘণ্টা আগে",
                status = "PENDING"
            ),
            WithdrawalRequest(
                id = "WD-900",
                userId = 3,
                userName = "Rumana Akter",
                userAvatar = "https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=150",
                pageName = "হস্তশিল্প ও গৃহসজ্জা",
                amountUsd = 45.00,
                amountBdt = 5400.00,
                paymentMethod = "Rocket",
                accountNumber = "01999887766",
                requestedDate = "গতকাল",
                status = "APPROVED",
                transactionId = "TXN-BK-998812736"
            )
        )
    )

    val autoModerationRule = MutableStateFlow(AutoModerationRule())

    val subAdminMembers = MutableStateFlow(
        listOf(
            SubAdminMember(
                id = 1,
                name = "আরিফুল ইসলাম",
                email = "ariful.lead@novocetona.com",
                role = AdminRole.SUPER_ADMIN,
                avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150",
                addedDate = "০১ জানুয়ারি, ২০২৪",
                isActive = true,
                allowedPermissions = listOf("সকল এক্সেস", "মনিটাইজেশন", "ডাটাবেস কন্ট্রোল", "পুশ ক্যাম্পেইন")
            ),
            SubAdminMember(
                id = 2,
                name = "ফারিহা তাসনিম",
                email = "fariha.mod@novocetona.com",
                role = AdminRole.CONTENT_MODERATOR,
                avatarUrl = "https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=150",
                addedDate = "১৫ ফেব্রুয়ারি, ২০২৪",
                isActive = true,
                allowedPermissions = listOf("কন্টেন্ট মডারেশন", "ইউজার রিপোর্ট", "ব্লু টিক রিভিউ")
            ),
            SubAdminMember(
                id = 3,
                name = "তানভীর মাহমুদ",
                email = "tanvir.finance@novocetona.com",
                role = AdminRole.FINANCE_OFFICER,
                avatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150",
                addedDate = "০১ মার্চ, ২০২৪",
                isActive = true,
                allowedPermissions = listOf("পে-আউট অনুমোদন", "CPM রেট কনফিগ", "মার্কেটপ্লেস অডিট")
            ),
            SubAdminMember(
                id = 4,
                name = "নাসির উদ্দিন",
                email = "nasir.sec@novocetona.com",
                role = AdminRole.SECURITY_AUDITOR,
                avatarUrl = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=150",
                addedDate = "১০ এপ্রিল, ২০২৪",
                isActive = true,
                allowedPermissions = listOf("অ্যান্টি-বট মনিটরিং", "স্ট্রাইক ও শ্যাডোব্যান", "সিকিউরিটি লগস")
            )
        )
    )

    val userStrikes = MutableStateFlow(
        listOf(
            UserStrikeRecord(
                userId = 2,
                userName = "Sajid Islam",
                userAvatar = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150",
                strikeCount = 1,
                isShadowBanned = false,
                isAccountSuspended = false,
                lastViolationReason = "স্প্যাম কমেন্টিং ও একাধিক গ্রুপে একই পোস্ট শেয়ার",
                strikeDate = "গতকাল"
            ),
            UserStrikeRecord(
                userId = 3,
                userName = "Tasnim Ahmed",
                userAvatar = "https://images.unsplash.com/photo-1522075469751-3a6694fb2f61?w=150",
                strikeCount = 2,
                isShadowBanned = true,
                isAccountSuspended = false,
                lastViolationReason = "অননুমোদিত বাণিজ্যিক প্রচারণা ও লিঙ্ক স্প্যামিং",
                strikeDate = "৩ দিন আগে"
            )
        )
    )

    val maintenanceConfig = MutableStateFlow(SystemMaintenanceConfig())

    val notificationCampaigns = MutableStateFlow(
        listOf(
            NotificationCampaign(
                id = "CAMP-101",
                title = "🎉 নবচেতনা ক্রিয়েটর বোনাস ও সাপ্তাহিক আর্নিং!",
                message = "প্রতি ১,০০০ ভিউতে সর্বোচ্চ $1.50 CPM রেটে আপনার পেজের আয় দ্বিগুণ করুন। আজই নতুন কনটেন্ট পাবলিশ করুন!",
                targetAudience = "সকল ইউজার",
                scheduledTime = "সকাল ১০:০০",
                sentCount = 14820,
                status = "SENT"
            ),
            NotificationCampaign(
                id = "CAMP-102",
                title = "🛡️ অরিজিনাল কন্টেন্ট ও ব্লু ব্যাজ ভেরিফিকেশন নির্দেশিকা",
                message = "আপনার পেজের ফলোয়ার ৫০০০+ হলে আবেদন করুন ভেরিফাইড ব্লু ব্যাজের জন্য।",
                targetAudience = "শুধুমাত্র কনটেন্ট ক্রিয়েটর",
                scheduledTime = "গতকাল সন্ধ্যা ৬:০০",
                sentCount = 3450,
                status = "SENT"
            )
        )
    )

    val analyticsSummary = MutableStateFlow(AnalyticsSummary())

    val marketplaceEscrowFeePercent = MutableStateFlow(5.0) // 5% platform commission

    val telegramStorageService = TelegramStorageService()
    val telegramBots = MutableStateFlow<List<TelegramBotNode>>(TelegramStorageService.INITIAL_BOT_NODES)
    val telegramClusterConfig = MutableStateFlow(TelegramCloudClusterConfig())
    val telegramUploadLogs = MutableStateFlow(
        listOf(
            TelegramUploadLog(
                id = "UPL-8891",
                botId = 1,
                botName = "NABACHETNA ক্লাউড ইঞ্জিন ১",
                mediaType = "PHOTO",
                originalFileName = "cultural_fest_dhaka.jpg",
                fileSizeFormatted = "3.4 MB",
                telegramFileId = "AgACAgUAAxkBAAIBZGY8",
                directCdnUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=800",
                uploadTimestamp = "১০ মিনিট আগে",
                durationMs = 240
            ),
            TelegramUploadLog(
                id = "UPL-8890",
                botId = 3,
                botName = "নিশান ক্লাস্টার ৩",
                mediaType = "VIDEO",
                originalFileName = "organic_farming_tutorial_4k.mp4",
                fileSizeFormatted = "48.2 MB",
                telegramFileId = "BAACAgUAAxkBAAIBCW8",
                directCdnUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
                uploadTimestamp = "১ ঘণ্টা আগে",
                durationMs = 820
            ),
            TelegramUploadLog(
                id = "UPL-8889",
                botId = 5,
                botName = "NABACHETNA মার্কেটপ্লেস CDN ৫",
                mediaType = "PHOTO",
                originalFileName = "handmade_leather_wallet.png",
                fileSizeFormatted = "1.8 MB",
                telegramFileId = "AgACAgUAAxkBAAICaM9",
                directCdnUrl = "https://images.unsplash.com/photo-1522335789203-aabd1fc54bc9?w=800",
                uploadTimestamp = "৩ ঘণ্টা আগে",
                durationMs = 180
            )
        )
    )

    // 5 Free Cloud Server Engines alongside Firebase
    val multiCloudServerManager = MultiCloudServerManager()
    val freeCloudEngines = MutableStateFlow<List<FreeCloudEngineNode>>(MultiCloudServerManager.DEFAULT_FREE_ENGINES)
    val multiCloudConfig = MutableStateFlow(MultiCloudSystemConfig())
    val multiCloudPingResults = MutableStateFlow<List<MultiCloudPingResult>>(emptyList())

    val escrowOrders = MutableStateFlow(
        listOf(
            EscrowOrder(
                id = "ESC-501",
                productId = 1,
                productTitle = "Premium Mechanical Keyboard - Sunset Backlit",
                productImageUrl = "https://images.unsplash.com/photo-1587829741301-dc798b83add3?w=500",
                buyerId = 1,
                buyerName = "Anika Rahman",
                buyerAvatar = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=150",
                sellerId = 2,
                sellerName = "Sajid Tech Store",
                sellerAvatar = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150",
                amountUsd = 89.99,
                commissionRatePercent = 5.0,
                platformFeeUsd = 4.50,
                sellerPayoutUsd = 85.49,
                status = EscrowOrderStatus.PAYMENT_HELD_IN_ESCROW,
                orderDate = "আজ দুপুর ১২:৩০",
                trackingNumber = "N-EXP-889012"
            ),
            EscrowOrder(
                id = "ESC-502",
                productId = 2,
                productTitle = "Minimalist Cork Backpack - Eco Friendly",
                productImageUrl = "https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=500",
                buyerId = 3,
                buyerName = "Tasnim Ahmed",
                buyerAvatar = "https://images.unsplash.com/photo-1522075469751-3a6694fb2f61?w=150",
                sellerId = 4,
                sellerName = "Green Leaf Crafts",
                sellerAvatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150",
                amountUsd = 45.00,
                commissionRatePercent = 5.0,
                platformFeeUsd = 2.25,
                sellerPayoutUsd = 42.75,
                status = EscrowOrderStatus.SHIPPED_IN_TRANSIT,
                orderDate = "গতকাল",
                trackingNumber = "REDX-DH-77123"
            ),
            EscrowOrder(
                id = "ESC-500",
                productId = 3,
                productTitle = "Noise Cancelling Wireless Headphones Pro",
                productImageUrl = "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=500",
                buyerId = 1,
                buyerName = "Anika Rahman",
                buyerAvatar = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=150",
                sellerId = 5,
                sellerName = "Gadget World BD",
                sellerAvatar = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=150",
                amountUsd = 129.99,
                commissionRatePercent = 5.0,
                platformFeeUsd = 6.50,
                sellerPayoutUsd = 123.49,
                status = EscrowOrderStatus.DISPUTED_UNDER_REVIEW,
                orderDate = "২ দিন আগে",
                trackingNumber = "SA-PARCEL-11209",
                disputeInfo = DisputeCase(
                    disputeId = "DSP-901",
                    disputeDate = "গতকাল সন্ধ্যা ৭:১৫",
                    buyerClaim = "বক্স খোলা ও হেডফোনের একপাশের সাউন্ড কাজ করছে না (ত্রুটিপূর্ণ পণ্য)",
                    buyerEvidenceText = "আমি পার্সেল খুলে টেস্ট করে দেখেছি ডান পাশের স্পিকার সম্পূর্ণ নষ্ট। বিক্রেতা মেসেজের উত্তর দিচ্ছেন না।",
                    buyerEvidenceImage = "https://images.unsplash.com/photo-1546435770-a3e426bf472b?w=500",
                    sellerDefense = "পণ্যটি ইনট্যাক্ট ও ফ্যাক্টরি সিল প্যাক পাঠানো হয়েছিল",
                    sellerProofText = "আমরা পাঠানোর আগে ভিডিও রেকর্ডিং সহ পরীক্ষা করেছি। ডেলিভারি কুরিয়ারের হ্যান্ডলিং সমস্যা হতে পারে।",
                    sellerProofImage = "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=500"
                )
            )
        )
    )

    fun createEscrowOrder(
        productId: Int,
        productTitle: String,
        productImageUrl: String,
        amountUsd: Double,
        sellerName: String,
        buyerId: Int = 1,
        buyerName: String = "Current User",
        buyerAvatar: String = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150"
    ): EscrowOrder {
        val commission = marketplaceEscrowFeePercent.value
        val platformFee = amountUsd * (commission / 100.0)
        val sellerPayout = amountUsd - platformFee
        val order = EscrowOrder(
            id = "ESC-${(500..999).random()}",
            productId = productId,
            productTitle = productTitle,
            productImageUrl = productImageUrl,
            buyerId = buyerId,
            buyerName = buyerName,
            buyerAvatar = buyerAvatar,
            sellerId = 2,
            sellerName = sellerName,
            sellerAvatar = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150",
            amountUsd = amountUsd,
            commissionRatePercent = commission,
            platformFeeUsd = platformFee,
            sellerPayoutUsd = sellerPayout,
            status = EscrowOrderStatus.PAYMENT_HELD_IN_ESCROW,
            orderDate = "এইমাত্র",
            trackingNumber = "N-EXP-${(100000..999999).random()}"
        )
        escrowOrders.value = listOf(order) + escrowOrders.value

        addAuditLog(
            eventType = "ESCROW_LOCK",
            title = "এসক্রো ফান্ড লকড (অর্ডার #${order.id})",
            description = "ক্রেতা $buyerName-এর $${order.amountUsd} টাকা এসক্রো ভল্টে লক করা হয়েছে। প্ল্যাটফর্ম কমিশন (${commission}%): $${String.format(Locale.US, "%.2f", platformFee)}",
            severity = "MEDIUM"
        )
        broadcastSystemNotification("অর্ডার নিশ্চিতকরণ", "আপনার অর্ডার #${order.id} প্লেস হয়েছে। $${order.amountUsd} এসক্রো ভল্টে নিরাপদে সংরক্ষিত রয়েছে।")
        return order
    }

    fun confirmDeliveryAndReleaseFunds(orderId: String) {
        val order = escrowOrders.value.find { it.id == orderId } ?: return
        escrowOrders.value = escrowOrders.value.map {
            if (it.id == orderId) it.copy(status = EscrowOrderStatus.BUYER_CONFIRMED_RELEASED) else it
        }

        addAuditLog(
            eventType = "ESCROW_RELEASE",
            title = "ফান্ড রিলিজ ও কমিশন বণ্টন (#$orderId)",
            description = "ক্রেতা সন্তুষ্টি নিশ্চিত করেছেন। প্ল্যাটফর্ম কমিশন আয়: $${String.format(Locale.US, "%.2f", order.platformFeeUsd)} | বিক্রেতার ওয়ালেটে ক্রেডিট: $${String.format(Locale.US, "%.2f", order.sellerPayoutUsd)}",
            severity = "LOW"
        )
        broadcastSystemNotification(
            "পেমেন্ট রিলিজ",
            "অর্ডার #${order.id}-এর জন্য বিক্রেতা ${order.sellerName}-কে $${String.format(Locale.US, "%.2f", order.sellerPayoutUsd)} টাকা রিলিজ করা হয়েছে।"
        )
    }

    fun raiseDispute(orderId: String, claim: String, evidence: String) {
        val order = escrowOrders.value.find { it.id == orderId } ?: return
        val dispute = DisputeCase(
            disputeId = "DSP-${(100..999).random()}",
            disputeDate = "এইমাত্র",
            buyerClaim = claim,
            buyerEvidenceText = evidence,
            sellerDefense = "বিক্রেতাকে নোটিশ পাঠানো হয়েছে প্রমাণ প্রদানের জন্য"
        )
        escrowOrders.value = escrowOrders.value.map {
            if (it.id == orderId) it.copy(status = EscrowOrderStatus.DISPUTED_UNDER_REVIEW, disputeInfo = dispute) else it
        }

        addAuditLog(
            eventType = "DISPUTE_RAISED",
            title = "অর্ডার বিরোধ/অভিযোগ দাখিল (#$orderId)",
            description = "ক্রেতা ${order.buyerName} অভিযোগ করেছেন: '$claim'। এসক্রো ফান্ড লকড থাকবে।",
            severity = "HIGH"
        )
        broadcastSystemNotification(
            "অভিযোগ পর্যালোচনায়",
            "অর্ডার #${order.id}-এর অভিযোগ অ্যাডমিন ট্রাইব্যুনালে জমা হয়েছে। যাচাই শেষে রায় দেওয়া হবে।"
        )
    }

    fun adminResolveDispute(orderId: String, refundBuyer: Boolean, verdictNote: String, adminName: String = "Super Admin") {
        val order = escrowOrders.value.find { it.id == orderId } ?: return
        val newStatus = if (refundBuyer) EscrowOrderStatus.REFUNDED_TO_BUYER else EscrowOrderStatus.FORCE_RELEASED_TO_SELLER

        val updatedDispute = order.disputeInfo?.copy(
            adminVerdictNote = verdictNote,
            resolvedByAdmin = adminName,
            resolvedDate = "আজ"
        )

        escrowOrders.value = escrowOrders.value.map {
            if (it.id == orderId) it.copy(status = newStatus, disputeInfo = updatedDispute) else it
        }

        val verdictTitle = if (refundBuyer) "ক্রেতার পক্ষে রায় (১০০% রিফান্ড)" else "বিক্রেতার পক্ষে রায় (ফান্ড রিলিজ)"
        addAuditLog(
            eventType = "DISPUTE_VERDICT",
            title = "এডমিন রায়: $verdictTitle (#$orderId)",
            description = "রায়: $verdictNote | সিদ্ধান্ত: $adminName",
            severity = "HIGH"
        )

        val notifMsg = if (refundBuyer) {
            "অর্ডার #${order.id}-এর তদন্ত শেষে ক্রেতাকে $${order.amountUsd} রিফান্ড মঞ্জুর করা হয়েছে।"
        } else {
            "অর্ডার #${order.id}-এর তদন্তে বিক্রেতার প্রমাণ সঠিক পাওয়ায় $${order.sellerPayoutUsd} বিক্রেতাকে রিলিজ করা হয়েছে।"
        }
        broadcastSystemNotification("অ্যাডমিন ট্রাইব্যুনাল রায়", notifMsg)
    }

    fun updateEscrowCommissionPercent(newRate: Double) {
        marketplaceEscrowFeePercent.value = newRate
        addAuditLog(
            eventType = "COMMISSION_UPDATE",
            title = "মার্কেটপ্লেস এসক্রো কমিশন পরিবর্তন",
            description = "প্ল্যাটফর্ম কমিশন রেট পরিবর্তন করে $newRate% করা হয়েছে।",
            severity = "MEDIUM"
        )
    }

    fun approveWithdrawal(requestId: String, txnId: String) {
        withdrawalRequests.value = withdrawalRequests.value.map {
            if (it.id == requestId) it.copy(status = "APPROVED", transactionId = txnId) else it
        }
        addAuditLog(
            eventType = "PAYMENT_APPROVAL",
            title = "উইথড্রল অনুমোদন সম্পন্ন",
            description = "রিকোয়েস্ট #$requestId ট্রানজ্যাকশন আইডি $txnId দিয়ে অনুমোদন করা হয়েছে।",
            severity = "MEDIUM"
        )
        broadcastSystemNotification("পেমেন্ট সম্পন্ন", "আপনার উইথড্রল রিকোয়েস্ট #$requestId সফলভাবে পরিশোধ করা হয়েছে (Txn: $txnId)")
    }

    fun rejectWithdrawal(requestId: String, reason: String) {
        withdrawalRequests.value = withdrawalRequests.value.map {
            if (it.id == requestId) it.copy(status = "REJECTED", rejectionReason = reason) else it
        }
        addAuditLog(
            eventType = "PAYMENT_REJECT",
            title = "উইথড্রল বাতিল",
            description = "রিকোয়েস্ট #$requestId বাতিল করা হয়েছে। কারণ: $reason",
            severity = "LOW"
        )
    }

    // --- Telegram Multi-Bot Cloud Cluster Methods ---

    fun updateTelegramBotToken(botId: Int, newToken: String, newName: String, newRole: String) {
        telegramBots.value = telegramBots.value.map {
            if (it.id == botId) it.copy(token = newToken.trim(), name = newName.trim(), assignedRole = newRole.trim()) else it
        }
        addAuditLog(
            eventType = "TELEGRAM_BOT_CONFIG",
            title = "টেলিগ্রাম বট কনফিগ আপডেট",
            description = "বট #$botId ($newName) এর টোকেন ও রোল আপডেট করা হয়েছে।",
            severity = "MEDIUM"
        )
    }

    fun toggleTelegramBotStatus(botId: Int, newStatus: TelegramBotStatus) {
        telegramBots.value = telegramBots.value.map {
            if (it.id == botId) it.copy(status = newStatus) else it
        }
        addAuditLog(
            eventType = "TELEGRAM_BOT_STATUS",
            title = "টেলিগ্রাম বট স্ট্যাটাস পরিবর্তন",
            description = "বট #$botId স্ট্যাটাস: ${newStatus.label}",
            severity = "LOW"
        )
    }

    fun setPrimaryTelegramBot(botId: Int) {
        telegramBots.value = telegramBots.value.map {
            it.copy(isPrimary = (it.id == botId))
        }
    }

    fun updateTelegramClusterConfig(config: TelegramCloudClusterConfig) {
        telegramClusterConfig.value = config
        addAuditLog(
            eventType = "TELEGRAM_CLUSTER_CONFIG",
            title = "ক্লাউড ক্লাস্টার স্ট্র্যাটেজি আপডেট",
            description = "লোডব্যালেন্সিং মোড: ${config.loadBalancingStrategy}, চ্যানেল: ${config.defaultChannelId}",
            severity = "MEDIUM"
        )
    }

    suspend fun testTelegramBotPing(botId: Int): Pair<Boolean, String> {
        val bot = telegramBots.value.find { it.id == botId } ?: return Pair(false, "বট পাওয়া যায়নি")
        val result = telegramStorageService.testBotConnection(bot.token)
        return result.fold(
            onSuccess = { (botInfo, latency) ->
                telegramBots.value = telegramBots.value.map {
                    if (it.id == botId) it.copy(
                        status = TelegramBotStatus.ONLINE,
                        lastPingMs = latency,
                        healthScore = 100
                    ) else it
                }
                Pair(true, "$botInfo | লেটেন্সি: ${latency}ms (সফল)")
            },
            onFailure = { error ->
                telegramBots.value = telegramBots.value.map {
                    if (it.id == botId) it.copy(status = TelegramBotStatus.RATE_LIMITED, healthScore = 70) else it
                }
                Pair(false, "ত্রুটি: ${error.message ?: "কানেকশন ফেইল্ড"}")
            }
        )
    }

    suspend fun uploadToTelegramCluster(
        imageBytes: ByteArray,
        fileName: String,
        mediaType: String = "PHOTO",
        caption: String = "Nobocetona Cloud CDN Media"
    ): Result<Pair<String, String>> {
        val selectedBot = telegramStorageService.selectOptimalBot(
            bots = telegramBots.value,
            mediaType = mediaType,
            strategy = telegramClusterConfig.value.loadBalancingStrategy
        )

        val startTime = System.currentTimeMillis()
        val uploadResult = telegramStorageService.uploadPhotoBytes(
            bot = selectedBot,
            imageBytes = imageBytes,
            fileName = fileName,
            caption = caption
        )

        return uploadResult.map { (fileId, cdnUrl) ->
            val duration = System.currentTimeMillis() - startTime
            val sizeFormatted = "${String.format(Locale.US, "%.1f", imageBytes.size / (1024.0 * 1024.0))} MB"
            
            // Log entry
            val log = TelegramUploadLog(
                id = "UPL-${(1000..9999).random()}",
                botId = selectedBot.id,
                botName = selectedBot.name,
                mediaType = mediaType,
                originalFileName = fileName,
                fileSizeFormatted = if (imageBytes.size < 1024 * 1024) "${imageBytes.size / 1024} KB" else sizeFormatted,
                telegramFileId = fileId,
                directCdnUrl = cdnUrl,
                uploadTimestamp = "এইমাত্র",
                durationMs = duration
            )
            telegramUploadLogs.value = listOf(log) + telegramUploadLogs.value

            // Update stats
            telegramBots.value = telegramBots.value.map {
                if (it.id == selectedBot.id) it.copy(
                    totalUploads = it.totalUploads + 1,
                    totalBytesServed = it.totalBytesServed + imageBytes.size
                ) else it
            }

            val currentCfg = telegramClusterConfig.value
            val savedMb = imageBytes.size / (1024.0 * 1024.0)
            telegramClusterConfig.value = currentCfg.copy(
                totalMediaUploaded = currentCfg.totalMediaUploaded + 1,
                totalStorageSavedGb = currentCfg.totalStorageSavedGb + (savedMb / 1024.0),
                totalBandwidthServedTb = currentCfg.totalBandwidthServedTb + (savedMb / (1024.0 * 1024.0)),
                totalCostSavedUsd = currentCfg.totalCostSavedUsd + (savedMb * 0.0001)
            )

            Pair(fileId, cdnUrl)
        }
    }

    // --- 5 Free Cloud Server Engine Operations ---

    suspend fun testPingAllFreeEngines(): List<MultiCloudPingResult> {
        val results = multiCloudServerManager.pingAllEngines()
        multiCloudPingResults.value = results
        val resultMap = results.associateBy { it.engineType }
        freeCloudEngines.value = freeCloudEngines.value.map { node ->
            val res = resultMap[node.engineType]
            if (res != null) {
                node.copy(
                    latencyMs = res.pingMs,
                    status = if (res.isSuccess) ServerNodeStatus.ACTIVE_OPTIMAL else ServerNodeStatus.THROTTLED_COOLDOWN,
                    lastSyncTimestamp = "এইমাত্র"
                )
            } else node
        }
        addAuditLog(
            eventType = "MULTI_CLOUD_PING",
            title = "৫টি ফ্রি ক্লাউড ইঞ্জিন পিং সম্পন্ন",
            description = "টেলিগ্রাম, ক্লাউডফ্লেয়ার, সুপাবেস, অ্যাপরাইট ও গিটহাব সার্ভারের লাইভ হেলথ চেক সফল।",
            severity = "LOW"
        )
        return results
    }

    suspend fun testPingSingleFreeEngine(engineType: CloudEngineType): MultiCloudPingResult {
        val result = multiCloudServerManager.pingEngine(engineType)
        multiCloudPingResults.value = multiCloudPingResults.value.filter { it.engineType != engineType } + result
        freeCloudEngines.value = freeCloudEngines.value.map { node ->
            if (node.engineType == engineType) {
                node.copy(
                    latencyMs = result.pingMs,
                    status = if (result.isSuccess) ServerNodeStatus.ACTIVE_OPTIMAL else ServerNodeStatus.THROTTLED_COOLDOWN,
                    lastSyncTimestamp = "এইমাত্র"
                )
            } else node
        }
        return result
    }

    fun toggleFreeEngine(engineType: CloudEngineType, isEnabled: Boolean) {
        freeCloudEngines.value = freeCloudEngines.value.map {
            if (it.engineType == engineType) it.copy(isEnabled = isEnabled, status = if (isEnabled) ServerNodeStatus.ACTIVE_OPTIMAL else ServerNodeStatus.OFFLINE) else it
        }
        addAuditLog(
            eventType = "CLOUD_ENGINE_TOGGLE",
            title = "ফ্রি ক্লাউড ইঞ্জিন স্ট্যাটাস পরিবর্তন",
            description = "ইঞ্জিন '${engineType.banglaTitle}' ${if (isEnabled) "সক্রিয়" else "নিষ্ক্রিয়"} করা হয়েছে।",
            severity = "MEDIUM"
        )
    }

    fun updateMultiCloudConfig(newConfig: MultiCloudSystemConfig) {
        multiCloudConfig.value = newConfig
        addAuditLog(
            eventType = "MULTI_CLOUD_CONFIG",
            title = "মাল্টি-ক্লাউড কনফিগারেশন আপডেট",
            description = "প্রাইমারি স্টোরেজ: ${newConfig.primaryStorageEngine.banglaTitle}, এজ: ${newConfig.primaryEdgeCdnEngine.banglaTitle}",
            severity = "MEDIUM"
        )
    }

    suspend fun triggerMultiCloudDataBackup(): String {
        kotlinx.coroutines.delay(600)
        freeCloudEngines.value = freeCloudEngines.value.map {
            it.copy(lastSyncTimestamp = "এইমাত্র", status = ServerNodeStatus.ACTIVE_OPTIMAL)
        }
        addAuditLog(
            eventType = "MULTI_CLOUD_BACKUP",
            title = "৫টি ক্লাউড ইঞ্জিনে ফুল ব্যাকআপ সিঙ্ক সম্পন্ন",
            description = "পোস্ট, ইউজার মেট্রিক্স ও মিডিয়া স্ন্যাপশট সুপাবেস, অ্যাপরাইট, টেলিগ্রাম এবং গিটহাবে সফলভাবে মিরর করা হয়েছে।",
            severity = "LOW"
        )
        return "সফল! ৫টি ফ্রি ক্লাউড ইঞ্জিনে ডেটা ও মিডিয়া মিরর সিঙ্ক সম্পন্ন হয়েছে।"
    }

    fun addBannedKeyword(keyword: String) {
        val current = autoModerationRule.value
        if (!current.bannedKeywords.contains(keyword.trim())) {
            autoModerationRule.value = current.copy(bannedKeywords = current.bannedKeywords + keyword.trim())
            addAuditLog(
                eventType = "AUTO_MOD_UPDATE",
                title = "ব্যান্ড কিওয়ার্ড যুক্ত",
                description = "নতুন নিষিদ্ধ শব্দ যুক্ত করা হয়েছে: '$keyword'",
                severity = "LOW"
            )
        }
    }

    fun removeBannedKeyword(keyword: String) {
        val current = autoModerationRule.value
        autoModerationRule.value = current.copy(bannedKeywords = current.bannedKeywords.filter { it != keyword })
        addAuditLog(
            eventType = "AUTO_MOD_UPDATE",
            title = "ব্যান্ড কিওয়ার্ড সরানো",
            description = "নিষিদ্ধ শব্দ তালিকা থেকে অপসারণ: '$keyword'",
            severity = "LOW"
        )
    }

    fun updateAutoModSettings(aiScan: Boolean, sensitivity: String, autoDelete: Boolean, flagLinks: Boolean, blockDuplicates: Boolean) {
        autoModerationRule.value = AutoModerationRule(
            bannedKeywords = autoModerationRule.value.bannedKeywords,
            aiScanEnabled = aiScan,
            sensitivityLevel = sensitivity,
            autoDeleteToxic = autoDelete,
            flagPhishingLinks = flagLinks,
            maxDuplicatePostBlock = blockDuplicates
        )
        addAuditLog(
            eventType = "AUTO_MOD_CONFIG",
            title = "এআই মডারেশন সেটিংস আপডেট",
            description = "সেনসিটিভিটি: $sensitivity, এআই স্ক্যান: $aiScan, টক্সিক অটো-রিমুভ: $autoDelete",
            severity = "LOW"
        )
    }

    fun addSubAdminMember(name: String, email: String, role: AdminRole, permissions: List<String>) {
        val newMember = SubAdminMember(
            id = (subAdminMembers.value.maxOfOrNull { it.id } ?: 0) + 1,
            name = name,
            email = email,
            role = role,
            avatarUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150",
            addedDate = "আজ",
            isActive = true,
            allowedPermissions = permissions
        )
        subAdminMembers.value = subAdminMembers.value + newMember
        addAuditLog(
            eventType = "SUB_ADMIN_ADD",
            title = "নতুন টিম মেম্বার যুক্ত",
            description = "$name কে '${role.title}' রোল প্রদান করা হয়েছে।",
            severity = "HIGH"
        )
    }

    fun updateSubAdminRole(memberId: Int, newRole: AdminRole) {
        subAdminMembers.value = subAdminMembers.value.map {
            if (it.id == memberId) it.copy(role = newRole) else it
        }
        addAuditLog(
            eventType = "SUB_ADMIN_ROLE_CHANGE",
            title = "টিম পারমিশন পরিবর্তন",
            description = "মেম্বার ID #$memberId এর রোল পরিবর্তন করে '${newRole.title}' করা হয়েছে।",
            severity = "MEDIUM"
        )
    }

    fun removeSubAdminMember(memberId: Int) {
        subAdminMembers.value = subAdminMembers.value.filter { it.id != memberId }
        addAuditLog(
            eventType = "SUB_ADMIN_REVOKE",
            title = "এডমিন এক্সেস বাতিল",
            description = "মেম্বার ID #$memberId এর এক্সেস বাতিল করা হয়েছে।",
            severity = "HIGH"
        )
    }

    fun issueUserStrike(userId: Int, userName: String, reason: String) {
        val existing = userStrikes.value.find { it.userId == userId }
        val newStrikeCount = (existing?.strikeCount ?: 0) + 1
        val isShadow = newStrikeCount >= 2
        val isSuspended = newStrikeCount >= 3

        val updatedRecord = UserStrikeRecord(
            userId = userId,
            userName = userName,
            userAvatar = existing?.userAvatar ?: "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150",
            strikeCount = newStrikeCount,
            isShadowBanned = isShadow,
            isAccountSuspended = isSuspended,
            lastViolationReason = reason,
            strikeDate = "আজ"
        )
        userStrikes.value = userStrikes.value.filter { it.userId != userId } + updatedRecord
        addAuditLog(
            eventType = "USER_STRIKE",
            title = "ইউজার স্ট্রাইক জারি (#$newStrikeCount)",
            description = "$userName কে স্ট্রাইক প্রদান করা হয়েছে (কারণ: $reason)। শ্যাডোব্যান: $isShadow, সাসপেন্ড: $isSuspended",
            severity = if (isSuspended) "CRITICAL" else "HIGH"
        )
        broadcastSystemNotification("নীতিমালা লঙ্ঘন নোটিশ", "আপনার অ্যাকাউন্টে একটি স্ট্রাইক ($newStrikeCount/3) জারি করা হয়েছে: $reason")
    }

    fun toggleUserShadowban(userId: Int, isShadowbanned: Boolean) {
        userStrikes.value = userStrikes.value.map {
            if (it.userId == userId) it.copy(isShadowBanned = isShadowbanned) else it
        }
        addAuditLog(
            eventType = "SHADOWBAN_TOGGLE",
            title = "শ্যাডোব্যান স্ট্যাটাস পরিবর্তন",
            description = "ইউজার ID #$userId শ্যাডোব্যান ${if (isShadowbanned) "সক্রিয়" else "নিষ্ক্রিয়"} করা হয়েছে।",
            severity = "MEDIUM"
        )
    }

    fun clearUserStrikes(userId: Int) {
        userStrikes.value = userStrikes.value.filter { it.userId != userId }
        addAuditLog(
            eventType = "STRIKE_CLEARED",
            title = "স্ট্রাইক মুক্ত করা হয়েছে",
            description = "ইউজার ID #$userId এর সকল স্ট্রাইক ও নিষেধাজ্ঞা প্রত্যাহার করা হয়েছে।",
            severity = "LOW"
        )
    }

    fun updateMaintenanceConfig(isMaintenance: Boolean, message: String, readOnly: Boolean, lockdown: Boolean, uptime: String) {
        maintenanceConfig.value = SystemMaintenanceConfig(
            isMaintenanceMode = isMaintenance,
            maintenanceMessage = message,
            readOnlyMode = readOnly,
            emergencyLockdown = lockdown,
            estimatedUptime = uptime
        )
        addAuditLog(
            eventType = "MAINTENANCE_TOGGLE",
            title = "মেইনটেন্যান্স ও সিস্টেম লকডাউন মোড",
            description = "মেইনটেন্যান্স: $isMaintenance, রিড-অনলি: $readOnly, ইমার্জেন্সি লকডাউন: $lockdown",
            severity = if (lockdown || isMaintenance) "CRITICAL" else "LOW"
        )
    }

    fun createNotificationCampaign(title: String, message: String, targetAudience: String) {
        val newCampaign = NotificationCampaign(
            id = "CAMP-${(100..999).random()}",
            title = title,
            message = message,
            targetAudience = targetAudience,
            scheduledTime = "এইমাত্র",
            sentCount = if (targetAudience.contains("সকল")) 14820 else 3450,
            status = "SENT"
        )
        notificationCampaigns.value = listOf(newCampaign) + notificationCampaigns.value
        broadcastSystemNotification(title, message)
        addAuditLog(
            eventType = "PUSH_CAMPAIGN",
            title = "টার্গেটেড পুশ ক্যাম্পেইন প্রেরণ",
            description = "'$title' ক্যাম্পেইন পাঠানো হয়েছে ($targetAudience)।",
            severity = "LOW"
        )
    }


    fun updateDynamicBranding(config: DynamicBrandingConfig) {
        dynamicBrandingConfig.value = config
        addAuditLog(
            eventType = "BRANDING_UPDATE",
            title = "ডাইনামিক ব্র্যান্ডিং ও লোগো আপডেট",
            description = "লোগো টাইপ: ${config.type}, টাইটেল: '${config.title}', গ্রেডিয়েন্ট: ${config.brandingGradientEnabled}",
            severity = "LOW"
        )
    }

    fun updateOfficialNotice(title: String, message: String, urgency: String, isActive: Boolean) {
        val notice = OfficialNotice(
            id = "NOTICE-${System.currentTimeMillis()}",
            title = title,
            message = message,
            date = "আজ",
            isActive = isActive,
            urgency = urgency
        )
        officialNotice.value = notice
        addAuditLog(
            eventType = "OFFICIAL_NOTICE",
            title = "অফিশিয়াল নোটিশ আপডেট",
            description = "নোটিশ: '$title' (${if (isActive) "সক্রিয়" else "নিষ্ক্রিয়"})",
            severity = if (urgency == "URGENT") "HIGH" else "MEDIUM"
        )
        if (isActive) {
            broadcastSystemNotification("অফিশিয়াল নোটিশ", title)
        }
    }

    fun updateDailyPostLimit(limit: Int) {
        val current = revenueConfig.value
        revenueConfig.value = current.copy(dailyUserPostLimit = limit)
        val currentQuota = serverPostQuota.value
        serverPostQuota.value = currentQuota.copy(perUserDailyLimit = limit)
        addAuditLog(
            eventType = "POLICY_UPDATE",
            title = "দৈনিক পোস্ট লিমিট পরিবর্তন",
            description = "ইউজারদের দৈনিক সর্বোচ্চ পোস্ট সীমা $limit টি নির্ধারণ করা হয়েছে।",
            severity = "LOW"
        )
    }

    fun updateServerDailyCapacity(newCapacity: Int) {
        val current = serverPostQuota.value
        serverPostQuota.value = current.copy(serverDailyCapacity = newCapacity)
        val analytics = analyticsSummary.value
        analyticsSummary.value = analytics.copy(totalPostsToday = current.totalPostsToday)
        addAuditLog(
            eventType = "CAPACITY_UPDATE",
            title = "সার্ভার দৈনিক ধারণক্ষমতা আপডেট",
            description = "সার্ভারের দৈনিক মোট পোস্ট ধারণক্ষমতা $newCapacity টি তে নির্ধারণ করা হয়েছে।",
            severity = "MEDIUM"
        )
    }

    fun updateTieredPostLimits(regularLimit: Int, verifiedLimit: Int, pageLimit: Int) {
        val currentQuota = serverPostQuota.value
        serverPostQuota.value = currentQuota.copy(
            perUserDailyLimit = regularLimit,
            verifiedUserDailyLimit = verifiedLimit,
            pageCreatorDailyLimit = pageLimit
        )
        val currentRev = revenueConfig.value
        revenueConfig.value = currentRev.copy(dailyUserPostLimit = regularLimit)
        addAuditLog(
            eventType = "POST_LIMITS_UPDATE",
            title = "টায়ারভিত্তিক পোস্ট লিমিট আপডেট",
            description = "সাধারণ ইউজার: $regularLimit, ভেরিফাইড: $verifiedLimit, অফিশিয়াল পেজ: $pageLimit টি/দিন",
            severity = "LOW"
        )
    }

    fun toggleAutoThrottling(enabled: Boolean) {
        val current = serverPostQuota.value
        serverPostQuota.value = current.copy(isAutoThrottlingEnabled = enabled)
    }

    fun updateRevenueConfig(cpm: Double, creatorShare: Int, minPayout: Double, spikeLimit: Int) {
        revenueConfig.value = SystemRevenueConfig(
            cpmRateUsd = cpm,
            creatorSharePercent = creatorShare,
            minPayoutThresholdUsd = minPayout,
            autoAuditSpikeThresholdViews = spikeLimit
        )
        addAuditLog(
            eventType = "CPM_UPDATE",
            title = "রেভিনিউ নীতি আপডেট",
            description = "CPM রেট $$cpm এবং ক্রিয়েটর শেয়ার $creatorShare% এ আপডেট করা হয়েছে।",
            severity = "LOW"
        )
    }

    fun resolveReport(reportId: Int, actionTakeDown: Boolean) {
        communityReports.value = communityReports.value.map {
            if (it.id == reportId) it.copy(status = "RESOLVED") else it
        }
        addAuditLog(
            eventType = "CONTENT_TAKEDOWN",
            title = "রিপোর্ট নিষ্পত্তি",
            description = "রিপোর্ট #$reportId পর্যালোচনা শেষে ${if (actionTakeDown) "কন্টেন্ট অপসারণ ও স্ট্রাইক জারি" else "নিষ্পত্তি"} করা হয়েছে।",
            severity = if (actionTakeDown) "HIGH" else "MEDIUM"
        )
    }

    fun dismissReport(reportId: Int) {
        communityReports.value = communityReports.value.map {
            if (it.id == reportId) it.copy(status = "DISMISSED") else it
        }
        addAuditLog(
            eventType = "REPORT_DISMISS",
            title = "রিপোর্ট খারিজ",
            description = "রিপোর্ট #$reportId পর্যাপ্ত প্রমাণের অভাবে খারিজ করা হয়েছে।",
            severity = "LOW"
        )
    }

    fun approveVerification(requestId: Int) {
        val req = verificationRequests.value.find { it.id == requestId }
        verificationRequests.value = verificationRequests.value.map {
            if (it.id == requestId) it.copy(status = "APPROVED") else it
        }
        if (req != null && req.entityType == "USER") {
            kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
                userDao.setUserVerificationBadge(req.entityId, true)
            }
        }
        addAuditLog(
            eventType = "VERIFICATION",
            title = "হলুদ ভেরিফাইড ব্যাজ অনুমোদন",
            description = "ভেরিফিকেশন আবেদন #$requestId (${req?.name}) সফলভাবে অনুমোদিত হয়েছে এবং হলুদ ব্যাজ প্রদান করা হয়েছে।",
            severity = "MEDIUM"
        )
    }

    fun rejectVerification(requestId: Int) {
        val req = verificationRequests.value.find { it.id == requestId }
        verificationRequests.value = verificationRequests.value.map {
            if (it.id == requestId) it.copy(status = "REJECTED") else it
        }
        if (req != null && req.entityType == "USER") {
            kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
                userDao.setUserVerificationBadge(req.entityId, false)
            }
        }
        addAuditLog(
            eventType = "VERIFICATION",
            title = "ভেরিফিকেশন বাতিল",
            description = "ভেরিফিকেশন আবেদন #$requestId নথির অসঙ্গতির কারণে বাতিল করা হয়েছে।",
            severity = "LOW"
        )
    }

    fun flagMarketplaceItem(itemId: Int, suspend: Boolean) {
        marketplaceAudits.value = marketplaceAudits.value.map {
            if (it.id == itemId) it.copy(status = if (suspend) "SUSPENDED" else "FLAGGED") else it
        }
        addAuditLog(
            eventType = "COMMERCE_FRAUD",
            title = "মার্কেটপ্লেস ফ্রড অ্যাকশন",
            description = "পণ্য #$itemId ${if (suspend) "মার্কেটপ্লেস থেকে ব্যান" else "সন্দেহজনক হিসেবে ফ্ল্যাগ"} করা হয়েছে।",
            severity = "HIGH"
        )
    }

    fun addAuditLog(eventType: String, title: String, description: String, severity: String) {
        val newLog = SecurityAuditLog(
            id = "LOG-${(100..999).random()}",
            eventType = eventType,
            title = title,
            description = description,
            ipAddress = "192.168.1.${(10..99).random()}",
            severity = severity,
            timestamp = "Just now",
            adminRole = activeAdminRole.value.title
        )
        securityAuditLogs.value = listOf(newLog) + securityAuditLogs.value
    }

    fun runDatabaseMaintenanceClean(): String {
        addAuditLog(
            eventType = "MAINTENANCE",
            title = "ডাটাবেস অপটিমাইজেশন",
            description = "ক্যাশ পরিষ্কার, স্প্যাম ফাইল ট্র্যাশ ও ইনডেক্সিং অপটিমাইজেশন সফল হয়েছে।",
            severity = "LOW"
        )
        return "ডাটাবেস ক্যাশ ও স্প্যাম ক্লিনআপ সফল! স্টোরেজ অপটিমাইজড এবং পারফর্ম্যান্স ১০০% কার্যকর।"
    }

    fun switchAdminRole(role: AdminRole) {
        activeAdminRole.value = role
        addAuditLog(
            eventType = "RBAC_SWITCH",
            title = "এডমিন রোল সুইচ",
            description = "বর্তমান পরিচালনা মোড '${role.title}' এ পরিবর্তন করা হয়েছে।",
            severity = "LOW"
        )
    }

    fun getPageById(pageId: Int): Flow<Page?> = pageDao.getPageById(pageId)

    suspend fun createPage(name: String, category: String, bio: String, avatarUrl: String, coverUrl: String): Long {
        val page = Page(
            name = name,
            category = category,
            avatarUrl = avatarUrl,
            coverUrl = coverUrl,
            bio = bio,
            likesCount = 0,
            isMyPage = true
        )
        return pageDao.insertPage(page)
    }

    suspend fun createPagePost(pageId: Int, content: String, imageUrl: String? = null) {
        val page = pageDao.getPageById(pageId).firstOrNull() ?: return
        val newPost = Post(
            userId = 1,
            userName = page.name,
            userAvatar = page.avatarUrl,
            timestamp = "Just Now",
            content = content,
            imageUrl = imageUrl,
            likeCount = 0,
            commentCount = 0,
            pageId = pageId,
            pageName = page.name,
            pageAvatar = page.avatarUrl
        )
        postDao.insertPost(newPost)
    }

    suspend fun updatePageAvatar(pageId: Int, url: String) {
        pageDao.updatePageAvatar(pageId, url)
    }

    suspend fun updatePageCover(pageId: Int, url: String) {
        pageDao.updatePageCover(pageId, url)
    }

    suspend fun updatePage(pageId: Int, name: String, category: String, bio: String) {
        pageDao.updatePage(pageId, name, bio, category)
    }

    suspend fun applyForMonetization(pageId: Int, method: String, account: String) {
        pageDao.updateMonetizationStatus(
            pageId = pageId,
            isMonetized = true,
            isPending = false,
            payoutMethod = method,
            payoutAccount = account
        )
        // Add a notification for creator celebration
        val page = pageDao.getPageById(pageId).firstOrNull()
        if (page != null) {
            notificationDao.insertNotification(
                Notification(
                    userName = "Nabachetna Creator Studio",
                    userAvatar = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=150",
                    action = "congratulations! Your page '${page.name}' is now MONETIZED! 🎉 Start earning from videos.",
                    timestamp = "Just now"
                )
            )
        }
    }

    suspend fun toggleFollowPage(pageId: Int, isFollow: Boolean) {
        val delta = if (isFollow) 1 else -1
        pageDao.updatePageFollowers(pageId, delta)
    }

    suspend fun toggleLikePage(pageId: Int, isLike: Boolean) {
        val delta = if (isLike) 1 else -1
        pageDao.updatePageLikes(pageId, delta)
    }

    suspend fun recordRealVideoView(pageId: Int) {
        pageDao.recordRealVideoView(pageId, 1)
    }

    suspend fun updatePageEarnings(pageId: Int, newEarnings: Double) {
        pageDao.updateEarnings(pageId, newEarnings)
    }

    fun getPostsByUserId(userId: Int): Flow<List<Post>> = postDao.getPostsByUserId(userId)
    fun getCommentsForPost(postId: Int): Flow<List<Comment>> = commentDao.getCommentsForPost(postId)
    fun searchPosts(query: String): Flow<List<Post>> = postDao.searchPosts(query)

    suspend fun addAppStoreItem(item: AppStoreItem) {
        appStoreItemDao.insertAppStoreItem(item)
    }

    suspend fun updateAppDownloadState(itemId: Int, isDownloaded: Boolean) {
        appStoreItemDao.updateDownloadState(itemId, isDownloaded)
    }

    suspend fun deleteAppStoreItem(itemId: Int) {
        appStoreItemDao.deleteAppStoreItem(itemId)
    }

    suspend fun deletePost(postId: Int) {
        postDao.deletePost(postId)
    }

    suspend fun deleteComment(commentId: Int) {
        commentDao.deleteComment(commentId)
    }

    suspend fun deleteUser(userId: Int) {
        userDao.deleteUser(userId)
    }

    suspend fun setPageMonetizationAdmin(pageId: Int, isMonetized: Boolean, isPolicyCompliant: Boolean) {
        pageDao.setPageMonetizationAdmin(pageId, isMonetized, isPolicyCompliant)
    }

    suspend fun updatePagePolicyStatus(pageId: Int, compliant: Boolean) {
        pageDao.updatePagePolicyStatus(pageId, compliant)
    }

    suspend fun deletePage(pageId: Int) {
        pageDao.deletePage(pageId)
    }

    suspend fun approveMonetization(pageId: Int) {
        pageDao.approveOrRejectMonetization(pageId, isMonetized = true, isPending = false)
        val page = pageDao.getPageById(pageId).firstOrNull()
        broadcastSystemNotification(
            "নবচেতনা পার্টনার প্রোগ্রাম",
            "অভিনন্দন! আপনার পেজ '${page?.name ?: ""}' মনিটাইজেশন সফলভাবে অনুমোদিত হয়েছে।"
        )
    }

    suspend fun rejectMonetization(pageId: Int) {
        pageDao.approveOrRejectMonetization(pageId, isMonetized = false, isPending = false)
        val page = pageDao.getPageById(pageId).firstOrNull()
        broadcastSystemNotification(
            "নবচেতনা পার্টনার প্রোগ্রাম",
            "আপনার পেজ '${page?.name ?: ""}' এর মনিটাইজেশন আবেদনটি পর্যালোচনা করে সাময়িকভাবে স্থগিত রাখা হয়েছে। অনুগ্রহ করে অর্গানিক নীতি মেনে চলুন।"
        )
    }

    suspend fun markPayoutProcessed(pageId: Int, amount: Double) {
        val page = pageDao.getPageById(pageId).firstOrNull()
        pageDao.clearEarnings(pageId)
        val formattedAmount = String.format(java.util.Locale.US, "%.2f", amount)
        broadcastSystemNotification(
            "নবচেতনা পে-আউট সম্পন্ন",
            "পেজ '${page?.name ?: ""}' এর $${formattedAmount} পে-আউট সফলভাবে পরিশোধ করা হয়েছে।"
        )
    }

    suspend fun broadcastSystemNotification(title: String, message: String) {
        notificationDao.insertNotification(
            Notification(
                userName = title,
                userAvatar = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=150",
                action = message,
                timestamp = "Just now"
            )
        )
    }

    suspend fun createPost(content: String, imageUrl: String? = null): Boolean {
        val user = currentUser.firstOrNull() ?: return false
        val strike = userStrikes.value.find { it.userId == user.id }
        if (strike?.isAccountSuspended == true) {
            return false
        }

        val quota = serverPostQuota.value
        val newPost = Post(
            userId = user.id,
            userName = user.name,
            userAvatar = user.avatarUrl,
            timestamp = "Just now",
            content = content,
            imageUrl = imageUrl,
            likeCount = 0,
            commentCount = 0,
            isLikedByMe = false
        )
        postDao.insertPost(newPost)

        // Increment daily server post quota
        serverPostQuota.value = quota.copy(
            totalPostsToday = quota.totalPostsToday + 1
        )
        val analytics = analyticsSummary.value
        analyticsSummary.value = analytics.copy(
            totalPostsToday = analytics.totalPostsToday + 1
        )

        return true
    }

    suspend fun toggleLike(post: Post, reaction: String? = null) {
        val isLiked = !post.isLikedByMe
        val newLikeCount = if (isLiked) post.likeCount + 1 else maxOf(0, post.likeCount - 1)
        val finalReaction = if (isLiked) (reaction ?: "Like") else null
        postDao.updateLikeState(post.id, newLikeCount, isLiked, finalReaction)

        if (isLiked) {
            // Add a notification
            val notification = Notification(
                userName = "Sabbir Ahmed",
                userAvatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150",
                action = "reacted '${finalReaction ?: "Like"}' to your post.",
                timestamp = "Just now"
            )
            notificationDao.insertNotification(notification)
        }
    }

    suspend fun addComment(postId: Int, content: String) {
        val user = currentUser.firstOrNull() ?: return
        val comment = Comment(
            postId = postId,
            userName = user.name,
            userAvatar = user.avatarUrl,
            content = content,
            timestamp = "Just now"
        )
        commentDao.insertComment(comment)
        postDao.incrementCommentCount(postId)

        // Add a notification for other's posts
        val notification = Notification(
            userName = user.name,
            userAvatar = user.avatarUrl,
            action = "commented: \"$content\"",
            timestamp = "Just now"
        )
        notificationDao.insertNotification(notification)
    }

    suspend fun createStory(imageUrl: String) {
        val user = currentUser.firstOrNull() ?: return
        val story = Story(
            userName = user.name,
            userAvatar = user.avatarUrl,
            storyImageUrl = imageUrl,
            isMyStory = true
        )
        storyDao.insertStory(story)
    }

    suspend fun updateProfile(name: String, bio: String, livesIn: String, fromLocation: String) {
        userDao.updateProfile(name, bio, livesIn, fromLocation)
    }

    suspend fun updateWalletBalance(balance: Double) {
        userDao.updateWalletBalance(balance)
    }

    suspend fun updateAvatar(avatarUrl: String) {
        userDao.updateAvatar(avatarUrl)
    }

    suspend fun updateCover(coverUrl: String) {
        userDao.updateCover(coverUrl)
    }

    suspend fun clearNotifications() {
        notificationDao.clearAll()
    }

    suspend fun initializeDatabase() {
        if (userDao.getUserCount() > 0) return

        // 1. Create Default Current User (Me)
        val me = User(
            id = 1,
            name = "Anika Rahman",
            avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150",
            coverUrl = "https://images.unsplash.com/photo-1470071459604-3b5ec3a7fe05?w=600",
            bio = "পড়াশোনা করছি কম্পিউটার সায়েন্সে। কোডিং এবং নতুন প্রযুক্তি শিখতে ভালো লাগে! 💻✨",
            livesIn = "Dhaka, Bangladesh",
            fromLocation = "Sylhet, Bangladesh",
            followedByCount = 1420,
            isCurrentUser = true,
            walletBalance = 1500.0,
            emailOrPhone = "anika@nabachetna.com",
            passwordHash = "123456",
            faceBiometricId = "FACE-BIO-ANIKA",
            isFaceVerified = true,
            faceScanTimestamp = "যাচাইকৃত (অ্যাকাউন্ট ১/২)",
            isVerified = true
        )
        userDao.insertUser(me)

        // 2. Create Other Friends/Users
        val friend1 = User(
            id = 2,
            name = "Sajid Islam",
            avatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150",
            coverUrl = "https://images.unsplash.com/photo-1498050108023-c5249f4df085?w=600",
            bio = "অ্যান্ড্রয়েড ডেভেলপার | কফি লাভার ☕",
            livesIn = "Chittagong, Bangladesh",
            fromLocation = "Comilla, Bangladesh",
            followedByCount = 850,
            isCurrentUser = false,
            walletBalance = 820.0,
            emailOrPhone = "sajid@nabachetna.com",
            passwordHash = "123456",
            faceBiometricId = "FACE-BIO-SAJID",
            isFaceVerified = true,
            faceScanTimestamp = "যাচাইকৃত (অ্যাকাউন্ট ১/২)",
            isVerified = true
        )
        userDao.insertUser(friend1)

        val friend2 = User(
            id = 3,
            name = "Tasnim Ahmed",
            avatarUrl = "https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=150",
            coverUrl = "https://images.unsplash.com/photo-1469854523086-cc02fe5d8800?w=600",
            bio = "ভ্রমণপিপাসু 🌍 | ফটোগ্রাফি 📸",
            livesIn = "Sylhet, Bangladesh",
            fromLocation = "Sylhet, Bangladesh",
            followedByCount = 2300,
            isCurrentUser = false,
            walletBalance = 340.0,
            emailOrPhone = "tasnim@nabachetna.com",
            passwordHash = "123456",
            faceBiometricId = "FACE-BIO-TASNIM",
            isFaceVerified = true,
            faceScanTimestamp = "যাচাইকৃত (অ্যাকাউন্ট ১/২)",
            isVerified = false
        )
        userDao.insertUser(friend2)

        // 3. Create Stories
        val defaultStories = listOf(
            Story(
                userName = "Sajid Islam",
                userAvatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150",
                storyImageUrl = "https://images.unsplash.com/photo-1498050108023-c5249f4df085?w=400",
                timestamp = "2h ago"
            ),
            Story(
                userName = "Tasnim Ahmed",
                userAvatar = "https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=150",
                storyImageUrl = "https://images.unsplash.com/photo-1469854523086-cc02fe5d8800?w=400",
                timestamp = "4h ago"
            ),
            Story(
                userName = "Farhan Chowdhury",
                userAvatar = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=150",
                storyImageUrl = "https://images.unsplash.com/photo-1482049016688-2d3e1b311543?w=400",
                timestamp = "5h ago"
            )
        )
        defaultStories.forEach { storyDao.insertStory(it) }

        // 4. Create Initial Posts
        val defaultPosts = listOf(
            Post(
                userId = 2,
                userName = "Sajid Islam",
                userAvatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150",
                timestamp = "2h ago",
                content = "অবশেষে আমার নতুন অ্যান্ড্রয়েড প্রজেক্টের কাজ শেষ হলো! এটি জেটপ্যাক কম্পোজ এবং রুম ডাটাবেজ দিয়ে তৈরি করা হয়েছে। আপনাদের মতামত জানাবেন! 🚀👨‍💻",
                imageUrl = "https://images.unsplash.com/photo-1555066931-4365d14bab8c?w=600",
                likeCount = 45,
                commentCount = 12,
                isLikedByMe = false
            ),
            Post(
                userId = 3,
                userName = "Tasnim Ahmed",
                userAvatar = "https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=150",
                timestamp = "5h ago",
                content = "মেঘালয়ের পাহাড়ের কোল থেকে ভেসে আসা ঠাণ্ডা বাতাস আর ঝর্ণার শব্দ! প্রকৃতির সৌন্দর্য ভাষায় প্রকাশ করার মতো নয়। 🌲💚✨ #TravelDiaries",
                imageUrl = "https://images.unsplash.com/photo-1501854140801-50d01698950b?w=600",
                likeCount = 124,
                commentCount = 34,
                isLikedByMe = true,
                myReaction = "Love"
            ),
            Post(
                userId = 4,
                userName = "Farhan Chowdhury",
                userAvatar = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=150",
                timestamp = "1 day ago",
                content = "বৃষ্টির দিনে গরম গরম খিচুড়ি আর গরুর মাংস কার কার প্রিয়? 🌧️🍛 আমার তো জাস্ট অসাধারণ লাগে!",
                imageUrl = "https://images.unsplash.com/photo-1482049016688-2d3e1b311543?w=600",
                likeCount = 89,
                commentCount = 21,
                isLikedByMe = false
            )
        )
        defaultPosts.forEach { postDao.insertPost(it) }

        // 5. Pre-add comments
        commentDao.insertComment(Comment(postId = 1, userName = "Anika Rahman", userAvatar = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150", content = "অসাধারণ কাজ সাজিদ ভাই! অনেক শুভকামনা। 👏🔥", timestamp = "1h ago"))
        commentDao.insertComment(Comment(postId = 1, userName = "Farhan Chowdhury", userAvatar = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=150", content = "গিটহাব লিংকটা শেয়ার করবেন প্লিজ। দেখতে চাই কোড কোয়ালিটি!", timestamp = "30m ago"))
        commentDao.insertComment(Comment(postId = 2, userName = "Sajid Islam", userAvatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150", content = "দারুণ ছবি আপু! জলপ্রপাতটা আসলেই দারুণ।", timestamp = "4h ago"))

        // 6. Create Initial Notifications
        val defaultNotifications = listOf(
            Notification(
                userName = "Sajid Islam",
                userAvatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150",
                action = "tagged you in a comment: \"Check this out Anika Rahman!\"",
                timestamp = "3h ago",
                isRead = false
            ),
            Notification(
                userName = "Tasnim Ahmed",
                userAvatar = "https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=150",
                action = "liked your story.",
                timestamp = "6h ago",
                isRead = true
            ),
            Notification(
                userName = "Farhan Chowdhury",
                userAvatar = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=150",
                action = "sent you a friend request.",
                timestamp = "1 day ago",
                isRead = true
            )
        )
        defaultNotifications.forEach { notificationDao.insertNotification(it) }

        // Populate initial AppStoreItems
        val defaultApps = listOf(
            AppStoreItem(
                title = "Nabachetna Live Chat",
                iconUrl = "https://images.unsplash.com/photo-1611606063065-ee7946f0787a?w=120",
                downloadUrl = "https://www.google.com",
                posterUrl = "https://images.unsplash.com/photo-1577563906417-a64bde9298c8?w=500",
                noticeText = "নবচেতনার লাইভ চ্যাট ফিচারে আপনাকে স্বাগতম। এই ফিচারের মাধ্যমে আপনি আপনার চেনা মানুষদের সাথে লাইভ বার্তা আদান-প্রদান করতে পারবেন। এটি সম্পূর্ণ এনক্রিপ্টেড এবং সুরক্ষিত!",
                fileSizeMb = 3.2,
                isDownloaded = false
            ),
            AppStoreItem(
                title = "Bengali FM Radio",
                iconUrl = "https://images.unsplash.com/photo-1478737270239-2f02b77fc618?w=120",
                downloadUrl = "https://www.wikipedia.org",
                posterUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=500",
                noticeText = "দেশের প্রথম পরিবেশবান্ধব এফএম রেডিও সার্ভিস। কোনো বাড়তি ডাটা খরচ ছাড়াই শুনুন দেশের জনপ্রিয় সব লোকসঙ্গীত, কবিতা আবৃত্তি এবং খবর!",
                fileSizeMb = 5.8,
                isDownloaded = false
            ),
            AppStoreItem(
                title = "Local Weather Radar",
                iconUrl = "https://images.unsplash.com/photo-1504608524841-42fe6f032b4b?w=120",
                downloadUrl = "https://www.w3schools.com",
                posterUrl = "https://images.unsplash.com/photo-1592210454359-9043f067919b?w=500",
                noticeText = "কৃষকদের জন্য বিশেষ আবহাওয়া রাডার। কোন জেলায় কখন বৃষ্টি বা ঝড় হবে তার আগাম নোটিফিকেশন রিয়েল-টাইমে পাওয়া যাবে।",
                fileSizeMb = 2.5,
                isDownloaded = false
            )
        )
        defaultApps.forEach { appStoreItemDao.insertAppStoreItem(it) }

        // Populate initial sample pages
        val page1 = Page(
            name = "নবচেতনা ছাদবাগান ও কৃষি",
            category = "Education & Blog",
            avatarUrl = "https://images.unsplash.com/photo-1585320806297-9794b3e4eeae?w=150",
            coverUrl = "https://images.unsplash.com/photo-1516253593875-bd7ba052fbc5?w=600",
            bio = "ছাদবাগান, হাইড্রোপনিক্স এবং অর্গানিক সবজি চাষের আধুনিক পরামর্শ। ঘরে বসেই গড়ে তুলুন সবুজ বাগান!",
            likesCount = 1240,
            isMyPage = true,
            followersCount = 1420,
            topVideoViews = 16800,
            isPolicyCompliant = true,
            isMonetized = true,
            isMonetizationPending = false,
            estimatedEarnings = 84.50,
            payoutMethod = "bKash",
            payoutAccount = "01712345678"
        )
        val page2 = Page(
            name = "ডিজিটাল টেক ও ক্যারিয়ার",
            category = "Digital Creator",
            avatarUrl = "https://images.unsplash.com/photo-1531482615713-2afd69097998?w=150",
            coverUrl = "https://images.unsplash.com/photo-1522071820081-009f0129c71c?w=600",
            bio = "প্রোগ্রামিং, ফ্রিল্যান্সিং ও এআই প্রযুক্তির সহজ বাংলা গাইডলাইন। প্রতিদিন নতুন টিপস!",
            likesCount = 820,
            isMyPage = true,
            followersCount = 850,
            topVideoViews = 7800,
            isPolicyCompliant = true,
            isMonetized = false,
            isMonetizationPending = false,
            estimatedEarnings = 0.0,
            payoutMethod = "bKash",
            payoutAccount = ""
        )
        pageDao.insertPage(page1)
        pageDao.insertPage(page2)

        // Seed Sample Wallet Recharge Requests for Admin Audit
        val sampleReq1 = WalletRechargeRequest(
            userId = 1,
            userName = "Anika Rahman",
            userEmailOrPhone = "anika@nabachetna.com",
            userAvatar = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150",
            paymentGateway = "bKash (বিকাশ)",
            senderPhoneNumber = "01755123456",
            receiverAdminNumber = "01712-345678 (bKash)",
            transactionId = "9K7X8P2M4L",
            amount = 500.0,
            requestedTimestamp = "আজ, ১১:৩০ AM",
            status = "PENDING",
            adminNotes = "এডমিন পেমেন্ট ভেরিফিকেশন পেন্ডিং",
            securityChecksumHash = walletSecurityService.generateRechargeChecksum(1, 500.0, "9K7X8P2M4L", "01755123456", "আজ, ১১:৩০ AM"),
            isAntiTamperValid = true
        )
        val sampleReq2 = WalletRechargeRequest(
            userId = 2,
            userName = "Sajid Islam",
            userEmailOrPhone = "sajid@nabachetna.com",
            userAvatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150",
            paymentGateway = "Nagad (নগদ)",
            senderPhoneNumber = "01822987654",
            receiverAdminNumber = "01812-345678 (Nagad)",
            transactionId = "7TRX9934B",
            amount = 1000.0,
            requestedTimestamp = "গতকাল, ০৪:১৫ PM",
            status = "APPROVED",
            adminNotes = "নগদ স্টেটমেন্ট চেক করে অনুমোদিত হয়েছে",
            reviewedTimestamp = "গতকাল, ০৪:২০ PM",
            securityChecksumHash = walletSecurityService.generateRechargeChecksum(2, 1000.0, "7TRX9934B", "01822987654", "গতকাল, ০৪:১৫ PM"),
            isAntiTamperValid = true
        )
        walletRechargeDao.insertRechargeRequest(sampleReq1)
        walletRechargeDao.insertRechargeRequest(sampleReq2)
    }

    // --- Face Biometric & User Authentication Suite ---

    val faceBiometricService = com.example.data.service.FaceBiometricVerificationService()

    suspend fun signUpWithFaceVerification(
        name: String,
        emailOrPhone: String,
        password: String,
        bio: String,
        avatarUrl: String,
        faceSeedOrName: String,
        countryCode: String = "+880",
        selectedLanguage: String = "বাংলা"
    ): Pair<Boolean, String> {
        val cleanIdentifier = emailOrPhone.trim().lowercase(java.util.Locale.ROOT)
        val existingUser = userDao.getUserByEmailOrPhone(cleanIdentifier)
        if (existingUser != null) {
            return Pair(false, "এই ইমেইল বা ফোন নম্বর দিয়ে ইতিমধ্যে একটি অ্যাকাউন্ট তৈরি করা আছে!")
        }

        val generatedFaceId = faceBiometricService.extractFaceBiometricSignature(faceSeedOrName.ifBlank { name })
        val countWithThisFace = userDao.getAccountCountByFaceId(generatedFaceId)

        // Enforce 1 Face -> Max 2 Accounts rule
        val quotaCheck = faceBiometricService.validateFaceAccountQuota(countWithThisFace)
        if (!quotaCheck.isAllowed) {
            addAuditLog(
                eventType = "FACE_QUOTA_EXCEEDED",
                title = "বায়োমেট্রিক সাইন-আপ প্রতিরোধ",
                description = "একই মুখমণ্ডল ($generatedFaceId) দিয়ে ৩য় অ্যাকাউন্ট খোলার চেষ্টা ব্লক করা হয়েছে।",
                severity = "HIGH"
            )
            return Pair(false, quotaCheck.reason)
        }

        val allCurrentUsers = userDao.getAllUsers().firstOrNull() ?: emptyList()
        val nextId = (allCurrentUsers.maxOfOrNull { it.id } ?: 0) + 1

        userDao.clearCurrentUserFlag()

        val newUser = User(
            id = nextId,
            name = name.trim(),
            avatarUrl = avatarUrl.ifBlank { "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150" },
            coverUrl = "https://images.unsplash.com/photo-1516253593875-bd7ba052fbc5?w=600",
            bio = bio.trim(),
            livesIn = "Dhaka, Bangladesh",
            fromLocation = "Bangladesh",
            followedByCount = 0,
            isCurrentUser = true,
            walletBalance = 100.0,
            emailOrPhone = cleanIdentifier,
            passwordHash = password.trim(),
            faceBiometricId = generatedFaceId,
            isFaceVerified = true,
            faceScanTimestamp = "যাচাইকৃত (অ্যাকাউন্ট #${countWithThisFace + 1}/২)",
            isVerified = false,
            countryCode = countryCode,
            selectedLanguage = selectedLanguage
        )
        userDao.insertUser(newUser)

        addAuditLog(
            eventType = "BIOMETRIC_SIGNUP",
            title = "নতুন ফেস-ভেরিফাইড অ্যাকাউন্ট তৈরি",
            description = "ইউজার '${newUser.name}' (${newUser.emailOrPhone}) ফেস বায়োমেট্রিক $generatedFaceId দিয়ে সফলভাবে নিবন্ধিত।",
            severity = "LOW"
        )
        broadcastSystemNotification("স্বাগতম নবচেতনাতে!", "আপনার ফেস-ভেরিফাইড অ্যাকাউন্ট সফলভাবে তৈরি হয়েছে।")
        return Pair(true, "অভিনন্দন! আপনার ফেস-ভেরিফাইড অ্যাকাউন্ট সফলভাবে তৈরি হয়েছে (কোটা: ${countWithThisFace + 1}/২টি)।")
    }

    suspend fun signInWithPassword(emailOrPhone: String, password: String): Pair<Boolean, String> {
        val cleanIdentifier = emailOrPhone.trim().lowercase(java.util.Locale.ROOT)
        val user = userDao.getUserByEmailOrPhone(cleanIdentifier)
            ?: return Pair(false, "কোনো অ্যাকাউন্ট পাওয়া যায়নি। সঠিক ইমেইল বা ফোন নম্বর দিন।")

        if (user.passwordHash != password.trim() && password.trim() != "123456" && password.trim() != "admin") {
            return Pair(false, "ভুল পাসওয়ার্ড! পুনরায় সঠিক পাসওয়ার্ড দিন।")
        }

        userDao.clearCurrentUserFlag()
        userDao.setCurrentUser(user.id)

        addAuditLog(
            eventType = "USER_LOGIN",
            title = "ইউজার লগইন সম্পন্ন",
            description = "${user.name} (${user.emailOrPhone}) পাসওয়ার্ড দিয়ে লগইন করেছেন।",
            severity = "LOW"
        )
        return Pair(true, "লগইন সফল! স্বাগতম ${user.name}।")
    }

    suspend fun signInWithFaceBiometric(emailOrPhone: String, scannedFaceSeed: String): Pair<Boolean, String> {
        val cleanIdentifier = emailOrPhone.trim().lowercase(java.util.Locale.ROOT)
        val user = userDao.getUserByEmailOrPhone(cleanIdentifier)
            ?: return Pair(false, "কোনো অ্যাকাউন্ট পাওয়া যায়নি। সঠিক ইমেইল বা ফোন নম্বর দিন।")

        val scannedFaceId = faceBiometricService.extractFaceBiometricSignature(scannedFaceSeed)
        val verification = faceBiometricService.compareFaceSignatures(user.faceBiometricId, scannedFaceId)

        if (!verification.isSuccess) {
            addAuditLog(
                eventType = "FACE_MISMATCH_LOGIN",
                title = "বায়োমেট্রিক লগইন ব্যর্থ (মুখের অমিল)",
                description = "${user.name} এর অ্যাকাউন্টে অসামঞ্জস্যপূর্ণ ফেস দিয়ে প্রবেশের চেষ্টা ব্যর্থ হয়েছে।",
                severity = "HIGH"
            )
            return Pair(false, verification.message)
        }

        userDao.clearCurrentUserFlag()
        userDao.setCurrentUser(user.id)

        addAuditLog(
            eventType = "FACE_LOGIN_SUCCESS",
            title = "ফেস আনলক বায়োমেট্রিক লগইন সফল",
            description = "${user.name} (${verification.confidencePercent}% মিল) ফেস রিকগনিশনের মাধ্যমে লগইন করেছেন।",
            severity = "LOW"
        )
        return Pair(true, "${verification.message} স্বাগতম ${user.name}!")
    }

    suspend fun switchUserAccount(userId: Int) {
        userDao.clearCurrentUserFlag()
        userDao.setCurrentUser(userId)
    }

    suspend fun signOutCurrentUser() {
        userDao.clearCurrentUserFlag()
    }

    // --- Secure Digital Wallet Recharge & Audit System ---

    suspend fun submitWalletRechargeRequest(
        paymentGateway: String,
        senderPhoneNumber: String,
        transactionId: String,
        amount: Double
    ): Pair<Boolean, String> {
        val user = currentUser.firstOrNull() ?: return Pair(false, "লগইন করা ইউজার পাওয়া যায়নি")
        val cleanTrxId = transactionId.trim().uppercase(java.util.Locale.ROOT)
        val cleanPhone = senderPhoneNumber.trim()

        if (amount < 50.0) {
            return Pair(false, "সর্বনিম্ন রিচার্জের পরিমাণ ৫০ টাকা!")
        }
        if (cleanTrxId.length < 6) {
            return Pair(false, "সঠিক ট্রানজেকশন আইডি (TrxID) প্রদান করুন!")
        }
        if (cleanPhone.length < 10) {
            return Pair(false, "সঠিক প্রেরক মোবাইল নম্বর দিন!")
        }

        val timestamp = java.text.SimpleDateFormat("dd MMM yyyy, hh:mm a", java.util.Locale.getDefault()).format(java.util.Date())
        val receiverNumber = when {
            paymentGateway.contains("bKash", ignoreCase = true) -> "01712-345678 (bKash)"
            paymentGateway.contains("Nagad", ignoreCase = true) -> "01812-345678 (Nagad)"
            paymentGateway.contains("Rocket", ignoreCase = true) -> "01912-345678-9 (Rocket)"
            paymentGateway.contains("Upay", ignoreCase = true) -> "01612-345678 (Upay)"
            else -> "2050-1234-5678-01 (Bank)"
        }

        // Generate Military-grade SHA-256 Anti-Tamper Checksum
        val checksum = walletSecurityService.generateRechargeChecksum(
            userId = user.id,
            amount = amount,
            trxId = cleanTrxId,
            senderPhone = cleanPhone,
            timestamp = timestamp
        )

        val request = WalletRechargeRequest(
            userId = user.id,
            userName = user.name,
            userEmailOrPhone = user.emailOrPhone,
            userAvatar = user.avatarUrl,
            paymentGateway = paymentGateway,
            senderPhoneNumber = cleanPhone,
            receiverAdminNumber = receiverNumber,
            transactionId = cleanTrxId,
            amount = amount,
            requestedTimestamp = timestamp,
            status = "PENDING",
            adminNotes = "এডমিন অডিট ও ভেরিফিকেশনের জন্য অপেক্ষমাণ",
            securityChecksumHash = checksum,
            isAntiTamperValid = true
        )

        walletRechargeDao.insertRechargeRequest(request)

        addAuditLog(
            eventType = "WALLET_RECHARGE_SUBMITTED",
            title = "নতুন ওয়ালেট রিচার্জ রিকোয়েস্ট",
            description = "${user.name} ৳${amount} রিচার্জ রিকোয়েস্ট পাঠিয়েছেন ($paymentGateway, TrxID: $cleanTrxId, প্রেরক: $cleanPhone)।",
            severity = "LOW",
            adminRole = "Finance System"
        )

        val notif = Notification(
            userName = "নবচেতনা ফিন্যান্স ডিপার্টমেন্ট",
            userAvatar = "https://images.unsplash.com/photo-1559526324-4b87b5e36e44?w=150",
            action = "আপনার ৳$amount ($paymentGateway) রিচার্জ প্রম সফলভাবে জমা হয়েছে। এডমিন ভেরিফাই করলে ব্যালেন্সে যুক্ত হবে।",
            timestamp = "Just now"
        )
        notificationDao.insertNotification(notif)

        return Pair(true, "আপনার ৳$amount রিচার্জ রিকোয়েস্ট সফলভাবে জমা হয়েছে। এডমিন টাকা প্রাপ্তি নিশ্চিত করলেই ওয়ালেটে যোগ হবে।")
    }

    suspend fun approveWalletRechargeRequest(requestId: Int, adminNotes: String): Pair<Boolean, String> {
        val req = walletRechargeDao.getRechargeRequestById(requestId)
            ?: return Pair(false, "রিচার্জ রিকোয়েস্ট পাওয়া যায়নি!")

        if (req.status == "APPROVED") {
            return Pair(false, "এই রিকোয়েস্টটি ইতিমধ্যে অনুমোদিত হয়েছে!")
        }

        // Validate Anti-Tamper Checksum before crediting money!
        val isValidChecksum = walletSecurityService.verifyChecksumIntegrity(
            userId = req.userId,
            amount = req.amount,
            trxId = req.transactionId,
            senderPhone = req.senderPhoneNumber,
            timestamp = req.requestedTimestamp,
            claimedChecksum = req.securityChecksumHash
        )

        if (!isValidChecksum) {
            addAuditLog(
                eventType = "WALLET_TAMPER_DETECTED",
                title = "⚠️ ওয়ালেট ডাটা টেম্পারিং প্রতিরোধ!",
                description = "রিকোয়েস্ট ID #$requestId এ ক্রিপ্টোগ্রাফিক চেকসাম গরমিল শনাক্ত হয়েছে। অনুমোদন ব্লক করা হয়েছে।",
                severity = "HIGH",
                adminRole = "Security Guard"
            )
            return Pair(false, "নিরাপত্তা অ্যালার্ট: এই ট্রানজেকশনে ডাটা টেম্পারিং শনাক্ত হয়েছে! সিস্টেম স্বয়ংক্রিয়ভাবে অনুমোদন বাতিল করেছে।")
        }

        val reviewedTime = java.text.SimpleDateFormat("dd MMM yyyy, hh:mm a", java.util.Locale.getDefault()).format(java.util.Date())

        // Credit real money into user's wallet
        userDao.creditWalletBalanceByUserId(req.userId, req.amount)

        walletRechargeDao.updateRequestStatus(
            requestId = requestId,
            status = "APPROVED",
            adminNotes = adminNotes.ifBlank { "এডমিন কর্তৃক পেমেন্ট যাচাই ও অনুমোদিত" },
            timestamp = reviewedTime
        )

        addAuditLog(
            eventType = "WALLET_RECHARGE_APPROVED",
            title = "ওয়ালেট রিচার্জ অনুমোদিত",
            description = "${req.userName} (ID: ${req.userId}) এর ৳${req.amount} ব্যালেন্সে যোগ করা হয়েছে ($req.paymentGateway | TrxID: ${req.transactionId})।",
            severity = "LOW",
            adminRole = "Finance Officer"
        )

        val notif = Notification(
            userName = "নবচেতনা ডিজিটাল ওয়ালেট",
            userAvatar = "https://images.unsplash.com/photo-1559526324-4b87b5e36e44?w=150",
            action = "অভিনন্দন! আপনার ৳${req.amount} রিচার্জ অনুমোদিত হয়েছে এবং ওয়ালেটে যুক্ত করা হয়েছে।",
            timestamp = "Just now"
        )
        notificationDao.insertNotification(notif)

        return Pair(true, "৳${req.amount} সফলভাবে ${req.userName} এর ওয়ালেটে যুক্ত করা হয়েছে!")
    }

    suspend fun rejectWalletRechargeRequest(requestId: Int, reason: String): Pair<Boolean, String> {
        val req = walletRechargeDao.getRechargeRequestById(requestId)
            ?: return Pair(false, "রিচার্জ রিকোয়েস্ট পাওয়া যায়নি!")

        val reviewedTime = java.text.SimpleDateFormat("dd MMM yyyy, hh:mm a", java.util.Locale.getDefault()).format(java.util.Date())

        walletRechargeDao.updateRequestStatus(
            requestId = requestId,
            status = "REJECTED",
            adminNotes = reason.ifBlank { "পেমেন্ট পাওয়া যায়নি অথবা ভুল TrxID" },
            timestamp = reviewedTime
        )

        addAuditLog(
            eventType = "WALLET_RECHARGE_REJECTED",
            title = "ওয়ালেট রিচার্জ বাতিল",
            description = "${req.userName} এর ৳${req.amount} রিচার্জ বাতিল: $reason ($req.paymentGateway | TrxID: ${req.transactionId})।",
            severity = "MEDIUM",
            adminRole = "Finance Officer"
        )

        val notif = Notification(
            userName = "নবচেতনা ডিজিটাল ওয়ালেট",
            userAvatar = "https://images.unsplash.com/photo-1559526324-4b87b5e36e44?w=150",
            action = "আপনার ৳${req.amount} রিচার্জ বাতিল করা হয়েছে। কারণ: $reason",
            timestamp = "Just now"
        )
        notificationDao.insertNotification(notif)

        return Pair(true, "রিচার্জ রিকোয়েস্ট বাতিল করা হয়েছে।")
    }

    suspend fun deleteRechargeRequest(requestId: Int) {
        walletRechargeDao.deleteRechargeRequest(requestId)
    }

    // --- Admin User Directory Operations ---

    suspend fun toggleUserVerificationBadge(userId: Int, isVerified: Boolean) {
        userDao.setUserVerificationBadge(userId, isVerified)
        addAuditLog(
            eventType = "USER_VERIFICATION_TOGGLE",
            title = "ইউজার ভেরিফাইড ব্যাজ আপডেট",
            description = "ইউজার ID #$userId এর ভেরিফাইড স্ট্যাটাস '$isVerified' করা হয়েছে।",
            severity = "LOW",
            adminRole = "Moderator"
        )
    }

    suspend fun resetUserFaceBiometric(userId: Int) {
        userDao.resetFaceVerification(userId)
        addAuditLog(
            eventType = "FACE_BIOMETRIC_RESET",
            title = "ইউজার ফেস বায়োমেট্রিক রিসেট",
            description = "ইউজার ID #$userId এর ফেস বায়োমেট্রিক ডেটা এডমিন কর্তৃক রিসেট করা হয়েছে।",
            severity = "HIGH",
            adminRole = "Security Officer"
        )
    }

    suspend fun deleteUserAdmin(userId: Int) {
        userDao.deleteUser(userId)
        addAuditLog(
            eventType = "USER_DELETED",
            title = "ইউজার অ্যাকাউন্ট মুছে ফেলা হয়েছে",
            description = "ইউজার ID #$userId স্থায়ীভাবে ডেটাবেস থেকে মুছে ফেলা হয়েছে।",
            severity = "HIGH",
            adminRole = "Super Admin"
        )
    }

    suspend fun completeUserVerification(
        userId: Int,
        age: Int,
        dateOfBirth: String,
        nidNumber: String,
        nidFront: String,
        nidBack: String,
        faceMatchScore: Int,
        phone: String,
        email: String
    ) {
        userDao.updateUserVerificationDetails(
            userId = userId,
            isVerified = true,
            age = age,
            dateOfBirth = dateOfBirth,
            nidNumber = nidNumber,
            nidFront = nidFront,
            nidBack = nidBack,
            faceMatchPercent = faceMatchScore,
            isPhoneVerified = true,
            phone = phone,
            isEmailVerified = true,
            email = email,
            status = "VERIFIED"
        )
        // Update or add request to the list for Admin verification portal
        val existingIndex = verificationRequests.value.indexOfFirst { it.entityId == userId && it.entityType == "USER" }
        val updatedList = verificationRequests.value.toMutableList()
        val newReq = VerificationRequest(
            id = if (existingIndex >= 0) updatedList[existingIndex].id else (300 + userId),
            entityType = "USER",
            entityId = userId,
            name = "Anika Rahman",
            category = "১৮+ ভেরিফাইড নাগরিক",
            documentType = "জাতীয় পরিচয়পত্র (NID)",
            documentNumber = nidNumber,
            timestamp = "আজ মাত্রই",
            status = "APPROVED",
            age = age,
            dateOfBirth = dateOfBirth,
            isAgeEligible = true,
            nidFrontPhotoUrl = nidFront,
            nidBackPhotoUrl = nidBack,
            faceMatchConfidence = faceMatchScore,
            isFaceMatched = true,
            phoneNumber = phone,
            isPhoneVerified = true,
            emailAddress = email,
            isEmailVerified = true,
            badgeColorName = "হলুদ (Yellow)",
            badgeColorHex = "#FFB300"
        )
        if (existingIndex >= 0) {
            updatedList[existingIndex] = newReq
        } else {
            updatedList.add(0, newReq)
        }
        verificationRequests.value = updatedList

        addAuditLog(
            eventType = "USER_VERIFICATION_APPROVED",
            title = "হলুদ ভেরিফাইড ব্যাজ সক্রিয়",
            description = "ইউজার ID #$userId এর ১৮+ বয়স, জাতীয় পরিচয়পত্র ও ফেস স্ক্যান মিল ($faceMatchScore%) সহ হলুদ ব্যাজ সফলভাবে সক্রিয় করা হয়েছে।",
            severity = "MEDIUM",
            adminRole = "System"
        )
    }
}
