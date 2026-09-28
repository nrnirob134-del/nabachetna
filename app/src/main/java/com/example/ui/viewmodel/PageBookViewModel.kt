package com.example.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.model.*
import com.example.data.repository.PageBookRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class PageBookViewModel(
    private val repository: PageBookRepository,
    private val learningRepository: com.example.data.repository.LearningRepository
) : ViewModel() {

    init {
        viewModelScope.launch {
            repository.initializeDatabase()
            learningRepository.seedInitialData()
        }
    }

    val currentUser: StateFlow<User?> = repository.currentUser
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val posts: StateFlow<List<Post>> = combine(_searchQuery, repository.allPosts, currentUser) { query, allPostsList, user ->
        val userCountry = user?.countryCode ?: "+880"
        val filtered = if (query.isBlank()) {
            com.example.data.service.GlobalLocalizationRegistry.apply50Local50GlobalAlgorithm(allPostsList, userCountry)
        } else {
            allPostsList.filter { it.content.contains(query, ignoreCase = true) }
        }
        filtered
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val stories: StateFlow<List<Story>> = repository.allStories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notifications: StateFlow<List<Notification>> = repository.allNotifications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val appStoreItems: StateFlow<List<AppStoreItem>> = repository.allAppStoreItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pages: StateFlow<List<Page>> = repository.allPages
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allUsers: StateFlow<List<User>> = repository.allUsers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allRechargeRequests: StateFlow<List<WalletRechargeRequest>> = repository.allRechargeRequests
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val languages: StateFlow<List<Language>> = learningRepository.allLanguages
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val officialPaymentGateways: StateFlow<List<com.example.data.service.AdminPaymentGatewayInfo>> = repository.walletSecurityService.officialPaymentGateways

    fun togglePaymentGateway(id: String) {
        repository.walletSecurityService.toggleGatewayEnabled(id)
    }

    fun updatePaymentGateway(id: String, accountNumber: String, apiKey: String, isLive: Boolean) {
        repository.walletSecurityService.updateGatewayConfig(id, accountNumber, apiKey, isLive)
    }

    fun switchGatewayOperatingMode(mode: String) {
        repository.walletSecurityService.switchGatewayOperatingMode(mode)
    }

    // Biometric Security Layer
    private val _isBiometricAuthenticated = MutableStateFlow(false)
    val isBiometricAuthenticated: StateFlow<Boolean> = _isBiometricAuthenticated.asStateFlow()

    fun setBiometricAuthenticated(authenticated: Boolean) {
        _isBiometricAuthenticated.value = authenticated
    }

    // WebRTC / LiveKit Media Server Cluster Config
    private val _mediaServerConfig = MutableStateFlow(MediaServerConfig())
    val mediaServerConfig: StateFlow<MediaServerConfig> = _mediaServerConfig.asStateFlow()

    // Feature Lock Config
    private val _featureLockConfig = MutableStateFlow(FeatureLockConfig())
    val featureLockConfig: StateFlow<FeatureLockConfig> = _featureLockConfig.asStateFlow()

    fun updateFeatureLockConfig(config: FeatureLockConfig) {
        _featureLockConfig.value = config
    }

    fun updateMediaServerConfig(serverUrl: String, resolution: String, bitrate: Int) {
        _mediaServerConfig.value = _mediaServerConfig.value.copy(
            signalingServerUrl = serverUrl,
            resolution = resolution,
            targetBitrateKbps = bitrate
        )
    }

    val revenueConfig: StateFlow<SystemRevenueConfig> = repository.revenueConfig
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SystemRevenueConfig())

    val securityAuditLogs: StateFlow<List<SecurityAuditLog>> = repository.securityAuditLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val communityReports: StateFlow<List<CommunityReport>> = repository.communityReports
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val verificationRequests: StateFlow<List<VerificationRequest>> = repository.verificationRequests
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val marketplaceAudits: StateFlow<List<MarketplaceItemAudit>> = repository.marketplaceAudits
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeAdminRole: StateFlow<AdminRole> = repository.activeAdminRole
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AdminRole.SUPER_ADMIN)
    
    val dynamicBrandingConfig: StateFlow<DynamicBrandingConfig> = repository.dynamicBrandingConfig
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DynamicBrandingConfig())

    val officialNotice: StateFlow<OfficialNotice> = repository.officialNotice
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), OfficialNotice())

    val withdrawalRequests: StateFlow<List<WithdrawalRequest>> = repository.withdrawalRequests
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val autoModerationRule: StateFlow<AutoModerationRule> = repository.autoModerationRule
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AutoModerationRule())

    val subAdminMembers: StateFlow<List<SubAdminMember>> = repository.subAdminMembers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userStrikes: StateFlow<List<UserStrikeRecord>> = repository.userStrikes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val maintenanceConfig: StateFlow<SystemMaintenanceConfig> = repository.maintenanceConfig
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SystemMaintenanceConfig())

    val notificationCampaigns: StateFlow<List<NotificationCampaign>> = repository.notificationCampaigns
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val analyticsSummary: StateFlow<AnalyticsSummary> = repository.analyticsSummary
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AnalyticsSummary())

    val marketplaceEscrowFeePercent: StateFlow<Double> = repository.marketplaceEscrowFeePercent
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 5.0)

    val escrowOrders: StateFlow<List<EscrowOrder>> = repository.escrowOrders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val telegramBots: StateFlow<List<TelegramBotNode>> = repository.telegramBots
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TelegramStorageService.INITIAL_BOT_NODES)

    val telegramClusterConfig: StateFlow<TelegramCloudClusterConfig> = repository.telegramClusterConfig
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TelegramCloudClusterConfig())

    val telegramUploadLogs: StateFlow<List<TelegramUploadLog>> = repository.telegramUploadLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val serverPostQuota: StateFlow<ServerDailyPostQuota> = repository.serverPostQuota
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ServerDailyPostQuota())

    val freeCloudEngines: StateFlow<List<FreeCloudEngineNode>> = repository.freeCloudEngines
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), com.example.data.service.MultiCloudServerManager.DEFAULT_FREE_ENGINES)

    val multiCloudConfig: StateFlow<MultiCloudSystemConfig> = repository.multiCloudConfig
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MultiCloudSystemConfig())

    val multiCloudPingResults: StateFlow<List<MultiCloudPingResult>> = repository.multiCloudPingResults
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedPostIdForComments = MutableStateFlow<Int?>(null)
    val selectedPostIdForComments: StateFlow<Int?> = _selectedPostIdForComments.asStateFlow()

    val currentPostComments: StateFlow<List<Comment>> = _selectedPostIdForComments
        .flatMapLatest { postId ->
            if (postId != null) {
                repository.getCommentsForPost(postId)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectPostForComments(postId: Int?) {
        _selectedPostIdForComments.value = postId
    }

    fun createPost(content: String, imageUrl: String? = null, onResult: ((Boolean) -> Unit)? = null) {
        viewModelScope.launch {
            val success = repository.createPost(content, imageUrl)
            onResult?.invoke(success)
        }
    }

    fun toggleLike(post: Post, reaction: String? = null) {
        viewModelScope.launch {
            repository.toggleLike(post, reaction)
        }
    }

    fun addComment(postId: Int, content: String) {
        viewModelScope.launch {
            repository.addComment(postId, content)
        }
    }

    fun createStory(imageUrl: String) {
        viewModelScope.launch {
            repository.createStory(imageUrl)
        }
    }

    fun updateProfile(name: String, bio: String, livesIn: String, fromLocation: String) {
        viewModelScope.launch {
            repository.updateProfile(name, bio, livesIn, fromLocation)
        }
    }

    fun updateAvatar(avatarUrl: String) {
        viewModelScope.launch {
            repository.updateAvatar(avatarUrl)
        }
    }

    fun updateCover(coverUrl: String) {
        viewModelScope.launch {
            repository.updateCover(coverUrl)
        }
    }

    fun updateWalletBalance(balance: Double) {
        viewModelScope.launch {
            repository.updateWalletBalance(balance)
        }
    }

    fun clearNotifications() {
        viewModelScope.launch {
            repository.clearNotifications()
        }
    }

    fun addAppStoreItem(
        title: String,
        iconUrl: String,
        downloadUrl: String,
        posterUrl: String,
        noticeText: String,
        fileSizeMb: Double
    ) {
        viewModelScope.launch {
            repository.addAppStoreItem(
                AppStoreItem(
                    title = title,
                    iconUrl = iconUrl,
                    downloadUrl = downloadUrl,
                    posterUrl = posterUrl,
                    noticeText = noticeText,
                    fileSizeMb = fileSizeMb,
                    isDownloaded = false
                )
            )
        }
    }

    fun updateAppDownloadState(itemId: Int, isDownloaded: Boolean) {
        viewModelScope.launch {
            repository.updateAppDownloadState(itemId, isDownloaded)
        }
    }

    fun deleteAppStoreItem(itemId: Int) {
        viewModelScope.launch {
            repository.deleteAppStoreItem(itemId)
        }
    }

    fun createPage(name: String, category: String, bio: String, avatarUrl: String, coverUrl: String, onDone: (Long) -> Unit) {
        viewModelScope.launch {
            val pageId = repository.createPage(name, category, bio, avatarUrl, coverUrl)
            onDone(pageId)
        }
    }

    fun createPagePost(pageId: Int, content: String, imageUrl: String? = null) {
        viewModelScope.launch {
            repository.createPagePost(pageId, content, imageUrl)
        }
    }

    fun updatePageAvatar(pageId: Int, url: String) {
        viewModelScope.launch {
            repository.updatePageAvatar(pageId, url)
        }
    }

    fun updatePageCover(pageId: Int, url: String) {
        viewModelScope.launch {
            repository.updatePageCover(pageId, url)
        }
    }

    fun updatePage(pageId: Int, name: String, category: String, bio: String) {
        viewModelScope.launch {
            repository.updatePage(pageId, name, category, bio)
        }
    }

    fun applyForMonetization(pageId: Int, method: String, account: String) {
        viewModelScope.launch {
            repository.applyForMonetization(pageId, method, account)
        }
    }

    fun toggleFollowPage(pageId: Int, isFollow: Boolean) {
        viewModelScope.launch {
            repository.toggleFollowPage(pageId, isFollow)
        }
    }

    fun toggleLikePage(pageId: Int, isLike: Boolean) {
        viewModelScope.launch {
            repository.toggleLikePage(pageId, isLike)
        }
    }

    fun recordRealVideoView(pageId: Int) {
        viewModelScope.launch {
            repository.recordRealVideoView(pageId)
        }
    }

    fun updatePageEarnings(pageId: Int, newEarnings: Double) {
        viewModelScope.launch {
            repository.updatePageEarnings(pageId, newEarnings)
        }
    }

    // Admin Panel functions
    fun deletePostAdmin(postId: Int) {
        viewModelScope.launch {
            repository.deletePost(postId)
        }
    }

    fun deleteUserAdmin(userId: Int) {
        viewModelScope.launch {
            repository.deleteUser(userId)
        }
    }

    fun setPageMonetizationAdmin(pageId: Int, isMonetized: Boolean, isPolicyCompliant: Boolean) {
        viewModelScope.launch {
            repository.setPageMonetizationAdmin(pageId, isMonetized, isPolicyCompliant)
        }
    }

    fun updatePagePolicyStatusAdmin(pageId: Int, compliant: Boolean) {
        viewModelScope.launch {
            repository.updatePagePolicyStatus(pageId, compliant)
        }
    }

    fun deletePageAdmin(pageId: Int) {
        viewModelScope.launch {
            repository.deletePage(pageId)
        }
    }

    fun approveMonetizationAdmin(pageId: Int) {
        viewModelScope.launch {
            repository.approveMonetization(pageId)
        }
    }

    fun rejectMonetizationAdmin(pageId: Int) {
        viewModelScope.launch {
            repository.rejectMonetization(pageId)
        }
    }

    fun markPayoutProcessedAdmin(pageId: Int, amount: Double) {
        viewModelScope.launch {
            repository.markPayoutProcessed(pageId, amount)
        }
    }

    fun broadcastSystemNotification(title: String, message: String) {
        viewModelScope.launch {
            repository.broadcastSystemNotification(title, message)
        }
    }

    fun updateRevenueConfig(cpm: Double, creatorShare: Int, minPayout: Double, spikeLimit: Int) {
        repository.updateRevenueConfig(cpm, creatorShare, minPayout, spikeLimit)
    }

    fun resolveReport(reportId: Int, actionTakeDown: Boolean) {
        repository.resolveReport(reportId, actionTakeDown)
    }

    fun dismissReport(reportId: Int) {
        repository.dismissReport(reportId)
    }

    fun approveVerification(requestId: Int) {
        repository.approveVerification(requestId)
    }

    fun rejectVerification(requestId: Int) {
        repository.rejectVerification(requestId)
    }

    fun flagMarketplaceItem(itemId: Int, suspend: Boolean) {
        repository.flagMarketplaceItem(itemId, suspend)
    }

    fun runDatabaseMaintenanceClean(): String {
        return repository.runDatabaseMaintenanceClean()
    }

    fun switchAdminRole(role: AdminRole) {
        repository.switchAdminRole(role)
    }

    fun updateDynamicBranding(config: DynamicBrandingConfig) {
        repository.updateDynamicBranding(config)
    }

    fun updateOfficialNotice(title: String, message: String, urgency: String, isActive: Boolean) {
        repository.updateOfficialNotice(title, message, urgency, isActive)
    }

    fun updateDailyPostLimit(limit: Int) {
        repository.updateDailyPostLimit(limit)
    }

    fun approveWithdrawal(requestId: String, txnId: String) {
        repository.approveWithdrawal(requestId, txnId)
    }

    fun rejectWithdrawal(requestId: String, reason: String) {
        repository.rejectWithdrawal(requestId, reason)
    }

    fun addBannedKeyword(keyword: String) {
        repository.addBannedKeyword(keyword)
    }

    fun removeBannedKeyword(keyword: String) {
        repository.removeBannedKeyword(keyword)
    }

    fun updateAutoModSettings(aiScan: Boolean, sensitivity: String, autoDelete: Boolean, flagLinks: Boolean, blockDuplicates: Boolean) {
        repository.updateAutoModSettings(aiScan, sensitivity, autoDelete, flagLinks, blockDuplicates)
    }

    fun addSubAdminMember(name: String, email: String, role: AdminRole, permissions: List<String>) {
        repository.addSubAdminMember(name, email, role, permissions)
    }

    fun updateSubAdminRole(memberId: Int, newRole: AdminRole) {
        repository.updateSubAdminRole(memberId, newRole)
    }

    fun removeSubAdminMember(memberId: Int) {
        repository.removeSubAdminMember(memberId)
    }

    fun issueUserStrike(userId: Int, userName: String, reason: String) {
        repository.issueUserStrike(userId, userName, reason)
    }

    fun toggleUserShadowban(userId: Int, isShadowbanned: Boolean) {
        repository.toggleUserShadowban(userId, isShadowbanned)
    }

    fun clearUserStrikes(userId: Int) {
        repository.clearUserStrikes(userId)
    }

    fun updateMaintenanceConfig(isMaintenance: Boolean, message: String, readOnly: Boolean, lockdown: Boolean, uptime: String) {
        repository.updateMaintenanceConfig(isMaintenance, message, readOnly, lockdown, uptime)
    }

    fun createNotificationCampaign(title: String, message: String, targetAudience: String) {
        repository.createNotificationCampaign(title, message, targetAudience)
    }

    fun createEscrowOrder(
        productId: Int,
        productTitle: String,
        productImageUrl: String,
        amountUsd: Double,
        sellerName: String,
        buyerId: Int = 1,
        buyerName: String = "Current User"
    ): EscrowOrder {
        return repository.createEscrowOrder(productId, productTitle, productImageUrl, amountUsd, sellerName, buyerId, buyerName)
    }

    fun confirmDeliveryAndReleaseFunds(orderId: String) {
        repository.confirmDeliveryAndReleaseFunds(orderId)
    }

    fun raiseDispute(orderId: String, claim: String, evidence: String) {
        repository.raiseDispute(orderId, claim, evidence)
    }

    fun adminResolveDispute(orderId: String, refundBuyer: Boolean, verdictNote: String, adminName: String = "Super Admin") {
        repository.adminResolveDispute(orderId, refundBuyer, verdictNote, adminName)
    }

    fun updateEscrowCommissionPercent(newRate: Double) {
        repository.updateEscrowCommissionPercent(newRate)
    }

    // --- Telegram Cloud Cluster Management Methods ---

    fun updateTelegramBotToken(botId: Int, newToken: String, newName: String, newRole: String) {
        repository.updateTelegramBotToken(botId, newToken, newName, newRole)
    }

    fun toggleTelegramBotStatus(botId: Int, newStatus: TelegramBotStatus) {
        repository.toggleTelegramBotStatus(botId, newStatus)
    }

    fun setPrimaryTelegramBot(botId: Int) {
        repository.setPrimaryTelegramBot(botId)
    }

    fun updateTelegramClusterConfig(config: TelegramCloudClusterConfig) {
        repository.updateTelegramClusterConfig(config)
    }

    suspend fun testTelegramBotPing(botId: Int): Pair<Boolean, String> {
        return repository.testTelegramBotPing(botId)
    }

    suspend fun uploadToTelegramCluster(
        imageBytes: ByteArray,
        fileName: String,
        mediaType: String = "PHOTO",
        caption: String = "Nobocetona Cloud CDN Media"
    ): Result<Pair<String, String>> {
        return repository.uploadToTelegramCluster(imageBytes, fileName, mediaType, caption)
    }

    fun updateServerDailyCapacity(newCapacity: Int) {
        repository.updateServerDailyCapacity(newCapacity)
    }

    fun updateTieredPostLimits(regularLimit: Int, verifiedLimit: Int, pageLimit: Int) {
        repository.updateTieredPostLimits(regularLimit, verifiedLimit, pageLimit)
    }

    fun toggleAutoThrottling(enabled: Boolean) {
        repository.toggleAutoThrottling(enabled)
    }

    // --- 5 Free Cloud Server Engines Operations ---

    fun pingAllFreeCloudEngines(onComplete: ((List<MultiCloudPingResult>) -> Unit)? = null) {
        viewModelScope.launch {
            val results = repository.testPingAllFreeEngines()
            onComplete?.invoke(results)
        }
    }

    fun pingSingleFreeCloudEngine(engineType: CloudEngineType, onComplete: ((MultiCloudPingResult) -> Unit)? = null) {
        viewModelScope.launch {
            val res = repository.testPingSingleFreeEngine(engineType)
            onComplete?.invoke(res)
        }
    }

    fun toggleFreeEngine(engineType: CloudEngineType, isEnabled: Boolean) {
        repository.toggleFreeEngine(engineType, isEnabled)
    }

    fun updateMultiCloudConfig(newConfig: MultiCloudSystemConfig) {
        repository.updateMultiCloudConfig(newConfig)
    }

    fun triggerMultiCloudDataBackup(onComplete: ((String) -> Unit)? = null) {
        viewModelScope.launch {
            val msg = repository.triggerMultiCloudDataBackup()
            onComplete?.invoke(msg)
        }
    }

    // --- Face Biometric & User Auth Operations ---

    fun signUpWithFaceVerification(
        name: String,
        emailOrPhone: String,
        password: String,
        bio: String,
        avatarUrl: String,
        faceSeedOrName: String,
        countryCode: String = "+880",
        selectedLanguage: String = "বাংলা",
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            val (success, msg) = repository.signUpWithFaceVerification(
                name, emailOrPhone, password, bio, avatarUrl, faceSeedOrName, countryCode, selectedLanguage
            )
            onResult(success, msg)
        }
    }

    fun signInWithPassword(emailOrPhone: String, password: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val (success, msg) = repository.signInWithPassword(emailOrPhone, password)
            onResult(success, msg)
        }
    }

    fun signInWithFaceBiometric(emailOrPhone: String, scannedFaceSeed: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val (success, msg) = repository.signInWithFaceBiometric(emailOrPhone, scannedFaceSeed)
            onResult(success, msg)
        }
    }

    fun switchUserAccount(userId: Int) {
        viewModelScope.launch {
            repository.switchUserAccount(userId)
        }
    }

    fun signOutCurrentUser() {
        viewModelScope.launch {
            repository.signOutCurrentUser()
        }
    }

    // --- Admin User Directory & Wallet Recharge Methods ---

    fun submitWalletRechargeRequest(
        paymentGateway: String,
        senderPhoneNumber: String,
        transactionId: String,
        amount: Double,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            val (success, msg) = repository.submitWalletRechargeRequest(paymentGateway, senderPhoneNumber, transactionId, amount)
            onResult(success, msg)
        }
    }

    fun approveWalletRechargeRequest(requestId: Int, adminNotes: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val (success, msg) = repository.approveWalletRechargeRequest(requestId, adminNotes)
            onResult(success, msg)
        }
    }

    fun rejectWalletRechargeRequest(requestId: Int, reason: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val (success, msg) = repository.rejectWalletRechargeRequest(requestId, reason)
            onResult(success, msg)
        }
    }

    fun toggleUserVerificationBadge(userId: Int, isVerified: Boolean) {
        viewModelScope.launch {
            repository.toggleUserVerificationBadge(userId, isVerified)
        }
    }

    fun completeUserVerification(
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
        viewModelScope.launch {
            repository.completeUserVerification(
                userId = userId,
                age = age,
                dateOfBirth = dateOfBirth,
                nidNumber = nidNumber,
                nidFront = nidFront,
                nidBack = nidBack,
                faceMatchScore = faceMatchScore,
                phone = phone,
                email = email
            )
        }
    }

    fun resetUserFaceBiometric(userId: Int) {
        viewModelScope.launch {
            repository.resetUserFaceBiometric(userId)
        }
    }

    fun deleteUserAdmin(userId: Int) {
        viewModelScope.launch {
            repository.deleteUserAdmin(userId)
        }
    }

    fun deleteRechargeRequest(requestId: Int) {
        viewModelScope.launch {
            repository.deleteRechargeRequest(requestId)
        }
    }

    // Factory for instantiating the ViewModel with repository dependency
    companion object {
        fun provideFactory(context: Context): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val database = AppDatabase.getDatabase(context)
                    val repository = PageBookRepository(
                        userDao = database.userDao(),
                        postDao = database.postDao(),
                        commentDao = database.commentDao(),
                        storyDao = database.storyDao(),
                        notificationDao = database.notificationDao(),
                        appStoreItemDao = database.appStoreItemDao(),
                        pageDao = database.pageDao(),
                        walletRechargeDao = database.walletRechargeDao()
                    )
                    val learningRepository = com.example.data.repository.LearningRepository(
                        learningDao = database.learningDao()
                    )
                    return PageBookViewModel(repository, learningRepository) as T
                }
            }
        }
    }
}
