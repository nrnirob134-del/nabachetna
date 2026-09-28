package com.example.data.dao

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE isCurrentUser = 1 LIMIT 1")
    fun getCurrentUser(): Flow<User?>

    @Query("SELECT * FROM users WHERE id = :id")
    fun getUserById(id: Int): Flow<User?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Query("UPDATE users SET name = :name, bio = :bio, livesIn = :livesIn, fromLocation = :fromLocation WHERE isCurrentUser = 1")
    suspend fun updateProfile(name: String, bio: String, livesIn: String, fromLocation: String)

    @Query("UPDATE users SET walletBalance = :balance WHERE isCurrentUser = 1")
    suspend fun updateWalletBalance(balance: Double)

    @Query("UPDATE users SET avatarUrl = :avatarUrl WHERE isCurrentUser = 1")
    suspend fun updateAvatar(avatarUrl: String)

    @Query("UPDATE users SET coverUrl = :coverUrl WHERE isCurrentUser = 1")
    suspend fun updateCover(coverUrl: String)

    @Query("SELECT COUNT(*) FROM users")
    suspend fun getUserCount(): Int

    @Query("SELECT * FROM users ORDER BY id ASC")
    fun getAllUsers(): Flow<List<User>>

    @Query("SELECT * FROM users WHERE emailOrPhone = :identifier LIMIT 1")
    suspend fun getUserByEmailOrPhone(identifier: String): User?

    @Query("SELECT COUNT(*) FROM users WHERE faceBiometricId = :faceId")
    suspend fun getAccountCountByFaceId(faceId: String): Int

    @Query("UPDATE users SET isCurrentUser = 0")
    suspend fun clearCurrentUserFlag()

    @Query("UPDATE users SET isCurrentUser = 1 WHERE id = :userId")
    suspend fun setCurrentUser(userId: Int)

    @Query("UPDATE users SET walletBalance = walletBalance + :creditAmount WHERE id = :userId")
    suspend fun creditWalletBalanceByUserId(userId: Int, creditAmount: Double)

    @Query("UPDATE users SET isVerified = :isVerified WHERE id = :userId")
    suspend fun setUserVerificationBadge(userId: Int, isVerified: Boolean)

    @Query("""
        UPDATE users SET 
            isVerified = :isVerified,
            age = :age,
            dateOfBirth = :dateOfBirth,
            nidNumber = :nidNumber,
            nidFrontPhotoUrl = :nidFront,
            nidBackPhotoUrl = :nidBack,
            faceMatchPercent = :faceMatchPercent,
            isPhoneVerified = :isPhoneVerified,
            verifiedPhone = :phone,
            isEmailVerified = :isEmailVerified,
            verifiedEmail = :email,
            verificationStatus = :status
        WHERE id = :userId
    """)
    suspend fun updateUserVerificationDetails(
        userId: Int,
        isVerified: Boolean,
        age: Int,
        dateOfBirth: String,
        nidNumber: String,
        nidFront: String,
        nidBack: String,
        faceMatchPercent: Int,
        isPhoneVerified: Boolean,
        phone: String,
        isEmailVerified: Boolean,
        email: String,
        status: String
    )

    @Query("UPDATE users SET isFaceVerified = 0, faceScanTimestamp = 'এডমিন কর্তৃক রিসেট' WHERE id = :userId")
    suspend fun resetFaceVerification(userId: Int)

    @Query("DELETE FROM users WHERE id = :userId")
    suspend fun deleteUser(userId: Int)
}

@Dao
interface PostDao {
    @Query("SELECT * FROM posts ORDER BY id DESC")
    fun getAllPosts(): Flow<List<Post>>

    @Query("SELECT * FROM posts WHERE userId = :userId ORDER BY id DESC")
    fun getPostsByUserId(userId: Int): Flow<List<Post>>

    @Query("SELECT * FROM posts WHERE content LIKE '%' || :query || '%' ORDER BY id DESC")
    fun searchPosts(query: String): Flow<List<Post>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: Post)

    @Query("UPDATE posts SET likeCount = :likeCount, isLikedByMe = :isLiked, myReaction = :reaction WHERE id = :postId")
    suspend fun updateLikeState(postId: Int, likeCount: Int, isLiked: Boolean, reaction: String?)

    @Query("UPDATE posts SET commentCount = commentCount + 1 WHERE id = :postId")
    suspend fun incrementCommentCount(postId: Int)

    @Query("SELECT COUNT(*) FROM posts WHERE userId = :userId")
    suspend fun getPostCountByUserId(userId: Int): Int

    @Query("DELETE FROM posts WHERE id = :postId")
    suspend fun deletePost(postId: Int)
}

@Dao
interface CommentDao {
    @Query("SELECT * FROM comments WHERE postId = :postId ORDER BY id ASC")
    fun getCommentsForPost(postId: Int): Flow<List<Comment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: Comment)

    @Query("DELETE FROM comments WHERE id = :commentId")
    suspend fun deleteComment(commentId: Int)
}

@Dao
interface StoryDao {
    @Query("SELECT * FROM stories ORDER BY id DESC")
    fun getAllStories(): Flow<List<Story>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStory(story: Story)
}

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications ORDER BY id DESC")
    fun getAllNotifications(): Flow<List<Notification>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: Notification)

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: Int)

    @Query("DELETE FROM notifications")
    suspend fun clearAll()
}

@Dao
interface AppStoreItemDao {
    @Query("SELECT * FROM app_store_items ORDER BY id DESC")
    fun getAllAppStoreItems(): Flow<List<AppStoreItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppStoreItem(item: AppStoreItem)

    @Query("UPDATE app_store_items SET isDownloaded = :downloaded WHERE id = :itemId")
    suspend fun updateDownloadState(itemId: Int, downloaded: Boolean)

    @Query("DELETE FROM app_store_items WHERE id = :itemId")
    suspend fun deleteAppStoreItem(itemId: Int)
}

@Dao
interface PageDao {
    @Query("SELECT * FROM pages ORDER BY id DESC")
    fun getAllPages(): Flow<List<Page>>

    @Query("SELECT * FROM pages WHERE id = :pageId LIMIT 1")
    fun getPageById(pageId: Int): Flow<Page?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPage(page: Page): Long

    @Query("UPDATE pages SET name = :name, bio = :bio, category = :category WHERE id = :pageId")
    suspend fun updatePage(pageId: Int, name: String, bio: String, category: String)

    @Query("UPDATE pages SET avatarUrl = :avatarUrl WHERE id = :pageId")
    suspend fun updatePageAvatar(pageId: Int, avatarUrl: String)

    @Query("UPDATE pages SET coverUrl = :coverUrl WHERE id = :pageId")
    suspend fun updatePageCover(pageId: Int, coverUrl: String)

    @Query("UPDATE pages SET isMonetized = :isMonetized, isMonetizationPending = :isPending, payoutMethod = :payoutMethod, payoutAccount = :payoutAccount WHERE id = :pageId")
    suspend fun updateMonetizationStatus(pageId: Int, isMonetized: Boolean, isPending: Boolean, payoutMethod: String, payoutAccount: String)

    @Query("UPDATE pages SET followersCount = followersCount + :delta WHERE id = :pageId")
    suspend fun updatePageFollowers(pageId: Int, delta: Int)

    @Query("UPDATE pages SET topVideoViews = topVideoViews + :delta WHERE id = :pageId")
    suspend fun recordRealVideoView(pageId: Int, delta: Int)

    @Query("UPDATE pages SET likesCount = likesCount + :delta WHERE id = :pageId")
    suspend fun updatePageLikes(pageId: Int, delta: Int)

    @Query("UPDATE pages SET followersCount = :followersCount, topVideoViews = :topVideoViews, isPolicyCompliant = :isPolicyCompliant WHERE id = :pageId")
    suspend fun updatePageMetrics(pageId: Int, followersCount: Int, topVideoViews: Int, isPolicyCompliant: Boolean)

    @Query("UPDATE pages SET estimatedEarnings = :earnings WHERE id = :pageId")
    suspend fun updateEarnings(pageId: Int, earnings: Double)

    @Query("UPDATE pages SET isPolicyCompliant = :compliant WHERE id = :pageId")
    suspend fun updatePagePolicyStatus(pageId: Int, compliant: Boolean)

    @Query("UPDATE pages SET isMonetized = :isMonetized, isPolicyCompliant = :isPolicyCompliant WHERE id = :pageId")
    suspend fun setPageMonetizationAdmin(pageId: Int, isMonetized: Boolean, isPolicyCompliant: Boolean)

    @Query("UPDATE pages SET isMonetized = :isMonetized, isMonetizationPending = :isPending WHERE id = :pageId")
    suspend fun approveOrRejectMonetization(pageId: Int, isMonetized: Boolean, isPending: Boolean)

    @Query("UPDATE pages SET estimatedEarnings = 0.0 WHERE id = :pageId")
    suspend fun clearEarnings(pageId: Int)

    @Query("DELETE FROM pages WHERE id = :pageId")
    suspend fun deletePage(pageId: Int)
}

@Dao
interface WalletRechargeDao {
    @Query("SELECT * FROM wallet_recharge_requests ORDER BY id DESC")
    fun getAllRechargeRequests(): Flow<List<WalletRechargeRequest>>

    @Query("SELECT * FROM wallet_recharge_requests WHERE userId = :userId ORDER BY id DESC")
    fun getRechargeRequestsByUserId(userId: Int): Flow<List<WalletRechargeRequest>>

    @Query("SELECT * FROM wallet_recharge_requests WHERE id = :id LIMIT 1")
    suspend fun getRechargeRequestById(id: Int): WalletRechargeRequest?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRechargeRequest(request: WalletRechargeRequest): Long

    @Query("UPDATE wallet_recharge_requests SET status = :status, adminNotes = :adminNotes, reviewedTimestamp = :timestamp WHERE id = :requestId")
    suspend fun updateRequestStatus(requestId: Int, status: String, adminNotes: String, timestamp: String)

    @Query("DELETE FROM wallet_recharge_requests WHERE id = :requestId")
    suspend fun deleteRechargeRequest(requestId: Int)
}
