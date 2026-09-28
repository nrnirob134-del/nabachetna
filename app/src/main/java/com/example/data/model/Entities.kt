package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey val id: Int,
    val name: String,
    val avatarUrl: String,
    val coverUrl: String,
    val bio: String = "",
    val livesIn: String = "",
    val fromLocation: String = "",
    val followedByCount: Int = 0,
    val isCurrentUser: Boolean = false,
    val walletBalance: Double = 500.0,
    val emailOrPhone: String = "",
    val passwordHash: String = "123456",
    val faceBiometricId: String = "FACE-USER-DEFAULT",
    val isFaceVerified: Boolean = true,
    val faceScanTimestamp: String = "রেজিস্ট্রেশন সম্পন্ন",
    val isVerified: Boolean = false,
    val countryCode: String = "+880",
    val selectedLanguage: String = "বাংলা",
    val age: Int = 22,
    val dateOfBirth: String = "2002-05-14",
    val nidNumber: String = "",
    val nidFrontPhotoUrl: String = "",
    val nidBackPhotoUrl: String = "",
    val faceMatchPercent: Int = 0,
    val isPhoneVerified: Boolean = false,
    val verifiedPhone: String = "",
    val isEmailVerified: Boolean = false,
    val verifiedEmail: String = "",
    val verificationStatus: String = "NOT_VERIFIED", // "NOT_VERIFIED", "PENDING_REVIEW", "VERIFIED", "REJECTED"
    val verificationBadgeColor: String = "YELLOW",
    val isSubscribed: Boolean = false,
    val lastSubscriptionChargeDate: String? = null
)

@Entity(tableName = "posts")
data class Post(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val userName: String,
    val userAvatar: String,
    val timestamp: String,
    val content: String,
    val imageUrl: String? = null,
    val likeCount: Int = 0,
    val commentCount: Int = 0,
    val isLikedByMe: Boolean = false,
    val myReaction: String? = null, // "Like", "Love", "Care", "Haha", "Wow", "Sad", "Angry"
    val pageId: Int? = null,
    val pageName: String? = null,
    val pageAvatar: String? = null,
    val isVerified: Boolean = false
)

@Entity(tableName = "pages")
data class Page(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val category: String,
    val avatarUrl: String,
    val coverUrl: String,
    val bio: String = "",
    val likesCount: Int = 0,
    val isMyPage: Boolean = true,
    val followersCount: Int = 0,
    val topVideoViews: Int = 0,
    val isPolicyCompliant: Boolean = true,
    val isMonetized: Boolean = false,
    val isMonetizationPending: Boolean = false,
    val estimatedEarnings: Double = 0.0,
    val payoutMethod: String = "bKash",
    val payoutAccount: String = "",
    val isVerified: Boolean = false,
    val isSubscribed: Boolean = false,
    val lastSubscriptionChargeDate: String? = null
)

@Entity(tableName = "comments")
data class Comment(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val postId: Int,
    val userName: String,
    val userAvatar: String,
    val content: String,
    val timestamp: String
)

@Entity(tableName = "stories")
data class Story(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userName: String,
    val userAvatar: String,
    val storyImageUrl: String,
    val timestamp: String = "Just now",
    val isMyStory: Boolean = false
)

@Entity(tableName = "notifications")
data class Notification(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userName: String,
    val userAvatar: String,
    val action: String,
    val timestamp: String,
    val isRead: Boolean = false
)

@Entity(tableName = "app_store_items")
data class AppStoreItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val iconUrl: String,
    val downloadUrl: String,
    val posterUrl: String,
    val noticeText: String,
    val fileSizeMb: Double,
    val isDownloaded: Boolean = false
)

@Entity(tableName = "wallet_recharge_requests")
data class WalletRechargeRequest(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val userName: String,
    val userEmailOrPhone: String,
    val userAvatar: String,
    val paymentGateway: String, // "bKash", "Nagad", "Rocket", "Upay", "Bank Transfer"
    val senderPhoneNumber: String, // কোন নম্বর থেকে টাকা পাঠানো হয়েছে
    val receiverAdminNumber: String, // এডমিনের নম্বর
    val transactionId: String, // TrxID
    val amount: Double,
    val requestedTimestamp: String,
    val status: String = "PENDING", // "PENDING", "APPROVED", "REJECTED"
    val adminNotes: String = "",
    val reviewedTimestamp: String? = null,
    val securityChecksumHash: String = "",
    val isAntiTamperValid: Boolean = true
)
