package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "languages")
data class Language(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val nativeName: String = "",
    val code: String = "en",
    val flagEmoji: String = "🌐",
    val totalLevels: Int = 200
)

@Entity(
    tableName = "levels",
    foreignKeys = [ForeignKey(
        entity = Language::class,
        parentColumns = ["id"],
        childColumns = ["languageId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("languageId")]
)
data class Level(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val languageId: Int,
    val levelNumber: Int,
    val isLocked: Boolean = true,
    val pointsEarned: Int = 0,
    val isExamPassed: Boolean = false
)

@Entity(
    tableName = "steps",
    foreignKeys = [ForeignKey(
        entity = Level::class,
        parentColumns = ["id"],
        childColumns = ["levelId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("levelId")]
)
data class Step(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val levelId: Int,
    val stepNumber: Int,
    val content: String,
    val type: String = "PRACTICE"
)

@Entity(
    tableName = "words",
    foreignKeys = [ForeignKey(
        entity = Step::class,
        parentColumns = ["id"],
        childColumns = ["stepId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("stepId")]
)
data class Word(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val stepId: Int,
    val word: String,
    val translation: String,
    val pronunciation: String = "",
    val exampleSentence: String = ""
)

@Entity(tableName = "user_scores")
data class UserScore(
    @PrimaryKey val id: String = "",
    val userId: String = "",
    val languageId: Int = 1,
    val languageName: String = "English",
    val userName: String = "",
    val avatarUrl: String = "",
    val totalPoints: Int = 0,
    val levelsCompleted: Int = 0,
    val rankBadge: String = "🥉 Beginner"
)

@Entity(tableName = "admin_sponsored_ads")
data class AdminSponsoredAd(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val sponsorName: String,
    val sponsorLogoEmoji: String = "🏢",
    val title: String,
    val description: String = "",
    val bannerImageUrl: String = "",
    val targetUrl: String = "",
    val ctaText: String = "অফারটি দেখুন ➔",
    val countdownSeconds: Int = 5,
    val rewardPointsGranted: Int = 1,
    val isActive: Boolean = true,
    val impressions: Int = 0,
    val clicks: Int = 0,
    val category: String = "LANGUAGE_REWARD",
    val revenueEarnedBdt: Double = 0.0
)

@Entity(tableName = "language_certificates")
data class LanguageCertificate(
    @PrimaryKey val certificateId: String = "",
    val userId: String = "",
    val userName: String = "",
    val userAvatarUrl: String = "",
    val languageId: Int = 1,
    val languageName: String = "English",
    val levelReached: Int = 1,
    val totalPoints: Int = 0,
    val issueDate: String = "",
    val status: String = "VERIFIED", // VERIFIED, PENDING_PAYMENT
    val feeAmountBdt: Double = 150.0,
    val paymentMethod: String = "bKash", // bKash, Nagad, Rocket, Wallet
    val transactionId: String = "",
    val qrCodePayload: String = "",
    val grade: String = "A+ (Excellent)"
)
