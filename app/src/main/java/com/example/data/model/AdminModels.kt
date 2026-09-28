package com.example.data.model

import androidx.compose.ui.graphics.Color

enum class AdminRole(val title: String, val badgeColor: Color, val permissions: String) {
    SUPER_ADMIN("সুপার এডমিন (Master)", Color(0xFFD32F2F), "সকল এক্সেস ও ফিন্যান্সিয়াল কন্ট্রোল"),
    CONTENT_MODERATOR("কন্টেন্ট মডারেটর", Color(0xFF1976D2), "পোস্ট, কমেন্ট, রিপোর্ট ও ভেরিফিকেশন"),
    FINANCE_OFFICER("ফিন্যান্স অফিসার", Color(0xFF2E7D32), "মনিটাইজেশন, CPM রেট ও পে-আউট ক্লিয়ারেন্স"),
    SECURITY_AUDITOR("সিকিউরিটি ও কমপ্লায়েন্স", Color(0xFFE65100), "বট ফিল্টারিং, স্ট্রাইক ও সিস্টেম অডিট")
}

data class SystemRevenueConfig(
    val cpmRateUsd: Double = 1.50, // Per 1,000 views in USD
    val creatorSharePercent: Int = 70, // 70% Creator, 30% Platform
    val minPayoutThresholdUsd: Double = 20.00, // Minimum payout withdrawal
    val autoAuditSpikeThresholdViews: Int = 5000, // Flag if page views jump rapidly
    val dailyUserPostLimit: Int = 50 // Maximum posts a user can publish per day
)

data class ServerDailyPostQuota(
    val serverDailyCapacity: Int = 50000, // মোট দৈনিক সার্ভার ধারণক্ষমতা (Total Capacity)
    val totalPostsToday: Int = 3410,      // আজ পর্যন্ত করা মোট পোস্ট
    val perUserDailyLimit: Int = 50,      // সাধারণ ইউজারের দৈনিক পোস্ট সীমা
    val verifiedUserDailyLimit: Int = 200, // ভেরিফাইড ইউজারের দৈনিক পোস্ট সীমা
    val pageCreatorDailyLimit: Int = 500,  // অফিসিয়াল পেজের দৈনিক পোস্ট সীমা
    val peakPostingTime: String = "সন্ধ্যা ৭:৩০ - রাত ১০:১৫",
    val isAutoThrottlingEnabled: Boolean = false,
    val emergencyBufferPercent: Int = 10,  // ১০% ইমার্জেন্সি বাফার
    val activePostersTodayCount: Int = 1280
) {
    val remainingPostsToday: Int
        get() = (serverDailyCapacity - totalPostsToday).coerceAtLeast(0)

    val usagePercentage: Float
        get() = if (serverDailyCapacity > 0) (totalPostsToday.toFloat() / serverDailyCapacity.toFloat()).coerceIn(0f, 1f) else 0f

    val isNearCapacity: Boolean
        get() = usagePercentage >= 0.80f

    val isAtFullCapacity: Boolean
        get() = remainingPostsToday <= 0
}

data class OfficialNotice(
    val id: String = "NOTICE-1",
    val title: String = "জরুরি অফিশিয়াল নোটিশ: নবচেতনা প্ল্যাটফর্ম নীতি",
    val message: String = "সম্মানিত ব্যবহারকারী, নবচেতনা প্ল্যাটফর্মে কোনো ফেক লাইক, ফেক ফলোয়ার বা বট ভিউ এলাউ করা হবে না। স্প্যামিং প্রতিরোধে দৈনিক পোস্ট লিমিট কার্যকর রয়েছে। ১০০% প্রকৃত ও মৌলিক কন্টেন্ট তৈরি করে আমাদের সাথেই থাকুন।",
    val date: String = "আজ",
    val isActive: Boolean = true,
    val urgency: String = "IMPORTANT" // "NORMAL", "IMPORTANT", "URGENT"
)

data class CommunityReport(
    val id: Int,
    val targetType: String, // "POST", "PAGE", "USER", "MARKETPLACE"
    val targetId: Int,
    val targetTitle: String,
    val reportedBy: String,
    val reason: String,
    val details: String,
    val timestamp: String,
    var status: String = "PENDING" // "PENDING", "RESOLVED", "DISMISSED"
)

data class VerificationRequest(
    val id: Int,
    val entityType: String, // "USER" or "PAGE"
    val entityId: Int,
    val name: String,
    val category: String,
    val documentType: String, // "জাতীয় পরিচয়পত্র (NID)", "ট্রেড লাইসেন্স", "প্রেস কার্ড / আইডি"
    val documentNumber: String,
    val timestamp: String,
    var status: String = "PENDING", // "PENDING", "APPROVED", "REJECTED"
    val age: Int = 22,
    val dateOfBirth: String = "2002-05-14",
    val isAgeEligible: Boolean = true,
    val nidFrontPhotoUrl: String = "https://images.unsplash.com/photo-1589829545856-d10d557cf95f?w=400",
    val nidBackPhotoUrl: String = "https://images.unsplash.com/photo-1589829545856-d10d557cf95f?w=400",
    val liveFacePhotoUrl: String = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=400",
    val faceMatchConfidence: Int = 96,
    val isFaceMatched: Boolean = true,
    val phoneNumber: String = "+880 1712-345678",
    val isPhoneVerified: Boolean = true,
    val emailAddress: String = "anika@nabachetna.com",
    val isEmailVerified: Boolean = true,
    val badgeColorName: String = "হলুদ (Yellow)",
    val badgeColorHex: String = "#FFB300",
    val rejectionReason: String? = null
)

data class SecurityAuditLog(
    val id: String,
    val eventType: String, // "BOT_DETECTION", "PAYMENT_APPROVAL", "CPM_UPDATE", "CONTENT_TAKEDOWN", "VERIFICATION", "MAINTENANCE"
    val title: String,
    val description: String,
    val ipAddress: String,
    val severity: String, // "LOW", "MEDIUM", "HIGH", "CRITICAL"
    val timestamp: String,
    val adminRole: String
)

data class MarketplaceItemAudit(
    val id: Int,
    val title: String,
    val sellerName: String,
    val price: Double,
    val category: String,
    val status: String = "APPROVED", // "APPROVED", "FLAGGED", "SUSPENDED"
    val isVerifiedSeller: Boolean = true
)

// --- Revolutionary Admin Suite Models ---

data class WithdrawalRequest(
    val id: String,
    val userId: Int,
    val userName: String,
    val userAvatar: String,
    val pageName: String,
    val amountUsd: Double,
    val amountBdt: Double,
    val paymentMethod: String, // "bKash", "Nagad", "Rocket", "Bank Transfer"
    val accountNumber: String,
    val requestedDate: String,
    val status: String = "PENDING", // "PENDING", "APPROVED", "REJECTED"
    val transactionId: String? = null,
    val rejectionReason: String? = null
)

data class AutoModerationRule(
    val bannedKeywords: List<String> = listOf("ফেক", "হ্যাক", "স্ক্যাম", "ফ্রি গিভঅ্যাওয়ে", "স্প্যাম", "জুয়া", "ক্যাসিনো"),
    val aiScanEnabled: Boolean = true,
    val sensitivityLevel: String = "MEDIUM", // "LOW", "MEDIUM", "HIGH"
    val autoDeleteToxic: Boolean = true,
    val flagPhishingLinks: Boolean = true,
    val maxDuplicatePostBlock: Boolean = true
)

data class SubAdminMember(
    val id: Int,
    val name: String,
    val email: String,
    val role: AdminRole,
    val avatarUrl: String,
    val addedDate: String,
    val isActive: Boolean = true,
    val allowedPermissions: List<String> = listOf("ড্যাশবোর্ড ভিউ", "কন্টেন্ট মডারেশন", "ইউজার অডিট")
)

data class UserStrikeRecord(
    val userId: Int,
    val userName: String,
    val userAvatar: String,
    val strikeCount: Int = 0, // 0 to 3
    val isShadowBanned: Boolean = false,
    val isAccountSuspended: Boolean = false,
    val lastViolationReason: String = "",
    val strikeDate: String = ""
)

data class SystemMaintenanceConfig(
    val isMaintenanceMode: Boolean = false,
    val maintenanceMessage: String = "সিস্টেম আপগ্রেড চলছে! সাময়িক অসুবিধার জন্য আন্তরিকভাবে দুঃখিত। দ্রুততম সময়ে সার্ভিস সচল হবে।",
    val readOnlyMode: Boolean = false,
    val emergencyLockdown: Boolean = false,
    val estimatedUptime: String = "৩০ মিনিট"
)

data class NotificationCampaign(
    val id: String,
    val title: String,
    val message: String,
    val targetAudience: String, // "সকল ইউজার", "শুধুমাত্র কনটেন্ট ক্রিয়েটর", "ভেরিফাইড পেজ ও ইউজার", "নিষ্ক্রিয় ব্যবহারকারী"
    val scheduledTime: String,
    val sentCount: Int,
    val status: String = "SENT" // "SENT", "SCHEDULED", "DRAFT"
)

data class AnalyticsSummary(
    val dailyActiveUsers: Int = 14820,
    val monthlyActiveUsers: Int = 185400,
    val totalRevenueUsd: Double = 12480.50,
    val creatorPayoutTotalUsd: Double = 8736.35,
    val totalPostsToday: Int = 3410,
    val serverResponseTimeMs: Int = 42,
    val peakActivityHour: String = "রাত ৮:০০ - ১০:০০",
    val retentionRatePercent: Int = 78
)

// --- Marketplace Escrow & Dispute Resolution Models ---

enum class EscrowOrderStatus(val label: String, val badgeColor: Color) {
    PAYMENT_HELD_IN_ESCROW("এসক্রো ভল্টে লকড 🔒", Color(0xFFE65100)),
    SHIPPED_IN_TRANSIT("পার্সেল ট্রানজিটে 🚚", Color(0xFF1976D2)),
    BUYER_CONFIRMED_RELEASED("পণ্য প্রাপ্ত ও রিলিজ্ড ✓", Color(0xFF2E7D32)),
    DISPUTED_UNDER_REVIEW("বিরোধ/তদন্তাধীন ⚠️", Color(0xFFD32F2F)),
    REFUNDED_TO_BUYER("ক্রেতাকে রিফান্ডকৃত ↩", Color(0xFF7B1FA2)),
    FORCE_RELEASED_TO_SELLER("বিক্রেতাকে রিলিজ্ড 🛡️", Color(0xFF00796B))
}

data class EscrowOrder(
    val id: String,
    val productId: Int,
    val productTitle: String,
    val productImageUrl: String,
    val buyerId: Int,
    val buyerName: String,
    val buyerAvatar: String,
    val sellerId: Int,
    val sellerName: String,
    val sellerAvatar: String,
    val amountUsd: Double,
    val commissionRatePercent: Double = 5.0, // Platform commission %
    val platformFeeUsd: Double = amountUsd * (commissionRatePercent / 100.0),
    val sellerPayoutUsd: Double = amountUsd - (amountUsd * (commissionRatePercent / 100.0)),
    val status: EscrowOrderStatus = EscrowOrderStatus.PAYMENT_HELD_IN_ESCROW,
    val orderDate: String,
    val trackingNumber: String? = null,
    val disputeInfo: DisputeCase? = null
)

data class DisputeCase(
    val disputeId: String,
    val disputeDate: String,
    val buyerClaim: String,
    val buyerEvidenceText: String,
    val buyerEvidenceImage: String? = null,
    val sellerDefense: String? = null,
    val sellerProofText: String? = null,
    val sellerProofImage: String? = null,
    val adminVerdictNote: String? = null,
    val resolvedByAdmin: String? = null,
    val resolvedDate: String? = null
)

// --- Telegram Multi-Bot Cloud Storage & CDN Engine Models ---

enum class TelegramBotStatus(val label: String, val badgeColor: Color) {
    ONLINE("সক্রিয় (Active)", Color(0xFF2E7D32)),
    BUSY("আপলোডিং (Busy)", Color(0xFF1976D2)),
    STANDBY("স্ট্যান্ডবাই (Ready)", Color(0xFFF57C00)),
    RATE_LIMITED("কোল্ডাউন (Cooldown)", Color(0xFFD32F2F)),
    OFFLINE("অফলাইন (Disabled)", Color(0xFF757575))
}

data class TelegramBotNode(
    val id: Int,
    val name: String,
    val token: String,
    val botUsername: String,
    val assignedRole: String, // e.g., "ছবি ও ফিড মিডিয়া", "এইচডি ভিডিও ও রিলস", "মার্কেটপ্লেস", "স্টোরিজ", "সিস্টেম ব্যাকআপ", "হাই-স্পিড CDN বাফার"
    val channelChatId: String = "-1002849182741", // Default private storage channel
    val status: TelegramBotStatus = TelegramBotStatus.ONLINE,
    val isPrimary: Boolean = (id == 1),
    val totalUploads: Int = 0,
    val totalBytesServed: Long = 0L,
    val lastPingMs: Long = 48L + (id * 7),
    val healthScore: Int = 100 - (id % 3)
)

data class TelegramCloudClusterConfig(
    val isEnabled: Boolean = true,
    val loadBalancingStrategy: String = "ROUND_ROBIN", // "ROUND_ROBIN", "SMART_LOAD_DISTRIBUTION", "FAILOVER_ONLY"
    val defaultChannelId: String = "-1002849182741",
    val totalMediaUploaded: Int = 1248,
    val totalStorageSavedGb: Double = 428.6,
    val totalBandwidthServedTb: Double = 1.84,
    val totalCostSavedUsd: Double = 94.30,
    val autoCompressionQuality: Int = 85
)

data class TelegramUploadLog(
    val id: String,
    val botId: Int,
    val botName: String,
    val mediaType: String, // "PHOTO", "VIDEO", "DOCUMENT", "AUDIO"
    val originalFileName: String,
    val fileSizeFormatted: String,
    val telegramFileId: String,
    val directCdnUrl: String,
    val uploadTimestamp: String,
    val durationMs: Long
)

data class MediaServerConfig(
    val signalingServerUrl: String = "wss://live.nobocetona.com/ws/v1",
    val stunTurnServers: String = "stun:stun.l.google.com:19302, turn:turn.nobocetona.com:3478",
    val resolution: String = "1080p (Full HD)",
    val targetBitrateKbps: Int = 2500,
    val activeCodec: String = "VP9 / AV1 Hardware Accelerated"
)

data class FeatureLockConfig(
    val isVoiceRoomEnabled: Boolean = false, // Locked by default as requested
    val isCallEnabled: Boolean = false,      // Locked by default as requested
    val isMarketplaceEnabled: Boolean = true,
    val isMonetizationEnabled: Boolean = true
)

data class FeatureSplashPoster(
    val id: String,
    val featureKey: String,
    val featureName: String,
    val iconEmoji: String,
    val imageUrl: String,
    val durationSeconds: Int = 3,
    val isEnabled: Boolean = true
)

data class DynamicBrandingConfig(
    val title: String = "NABACHETNA",
    val type: String = "TEXT", // "TEXT" or "IMAGE"
    val titleColorHex: String = "#FFFFFF", // Crisp white contrast
    val logoImageUrl: String = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=200", // Default beautiful green/gold 3D mesh
    val logoEmoji: String = "🍀", // Default nice branding emoji
    val brandingGradientEnabled: Boolean = true,
    val brandingGradientColor1Hex: String = "#81C784", // Light Tea Green
    val brandingGradientColor2Hex: String = "#2E7D32"  // Dark Tea Green
)


