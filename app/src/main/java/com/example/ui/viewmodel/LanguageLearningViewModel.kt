package com.example.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.model.*
import com.example.data.repository.LearningRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class LanguageLearningViewModel(
    private val learningRepository: LearningRepository
) : ViewModel() {

    init {
        viewModelScope.launch {
            learningRepository.seedInitialData()
        }
    }

    val languages: StateFlow<List<Language>> = learningRepository.allLanguages
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedLanguage = MutableStateFlow<Language?>(null)
    val selectedLanguage: StateFlow<Language?> = _selectedLanguage.asStateFlow()

    val currentLevels: StateFlow<List<Level>> = _selectedLanguage.flatMapLatest { lang ->
        if (lang != null) {
            learningRepository.getLevelsForLanguage(lang.id)
        } else {
            flowOf(emptyList())
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val currentLeaderboard: StateFlow<List<UserScore>> = _selectedLanguage.flatMapLatest { lang ->
        val langId = lang?.id ?: 1
        learningRepository.getLeaderboard(langId)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeSponsoredAds: StateFlow<List<AdminSponsoredAd>> = learningRepository.activeSponsoredAds
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allCertificates: StateFlow<List<LanguageCertificate>> = learningRepository.allCertificates
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active practice session state
    private val _activePracticeWords = MutableStateFlow<List<Word>>(emptyList())
    val activePracticeWords: StateFlow<List<Word>> = _activePracticeWords.asStateFlow()

    // Active exam session state
    private val _activeExamWords = MutableStateFlow<List<Word>>(emptyList())
    val activeExamWords: StateFlow<List<Word>> = _activeExamWords.asStateFlow()

    private val _activeLevel = MutableStateFlow<Level?>(null)
    val activeLevel: StateFlow<Level?> = _activeLevel.asStateFlow()

    fun selectLanguage(language: Language) {
        _selectedLanguage.value = language
        viewModelScope.launch {
            learningRepository.fetchAndPopulateWords(language.id, 1)
        }
    }

    fun openPracticeCamp(level: Level, currentUser: User?) {
        _activeLevel.value = level
        val lang = _selectedLanguage.value ?: return
        viewModelScope.launch {
            learningRepository.fetchAndPopulateWords(lang.id, level.levelNumber)
            val steps = learningRepository.getStepsForLevel(level.id).firstOrNull().orEmpty()
            val practiceStep = steps.find { it.type == "PRACTICE" } ?: steps.firstOrNull()
            if (practiceStep != null) {
                val words = learningRepository.getWordsForStep(practiceStep.id).firstOrNull().orEmpty()
                _activePracticeWords.value = words
            }
        }
    }

    fun openExam(level: Level, currentUser: User?) {
        _activeLevel.value = level
        val lang = _selectedLanguage.value ?: return
        viewModelScope.launch {
            learningRepository.fetchAndPopulateWords(lang.id, level.levelNumber)
            val steps = learningRepository.getStepsForLevel(level.id).firstOrNull().orEmpty()
            val examStep = steps.find { it.type == "EXAM" } ?: steps.lastOrNull()
            if (examStep != null) {
                val words = learningRepository.getWordsForStep(examStep.id).firstOrNull().orEmpty()
                _activeExamWords.value = words
            }
        }
    }

    fun earnPracticePoint(level: Level, currentUser: User?) {
        val lang = _selectedLanguage.value ?: return
        viewModelScope.launch {
            learningRepository.addPracticePoint(
                level = level,
                userId = currentUser?.id?.toString() ?: "user_me",
                userName = currentUser?.name ?: "Learner",
                languageName = lang.name,
                avatarUrl = currentUser?.avatarUrl ?: ""
            )
        }
    }

    fun completeAndPassExam(level: Level, currentUser: User?) {
        val lang = _selectedLanguage.value ?: return
        viewModelScope.launch {
            learningRepository.passExam(
                level = level,
                userId = currentUser?.id?.toString() ?: "user_me",
                userName = currentUser?.name ?: "Learner",
                languageName = lang.name,
                avatarUrl = currentUser?.avatarUrl ?: ""
            )
        }
    }

    fun recordAdImpression(adId: Int) {
        viewModelScope.launch {
            learningRepository.recordAdImpression(adId)
        }
    }

    fun recordAdClick(adId: Int) {
        viewModelScope.launch {
            learningRepository.recordAdClick(adId)
        }
    }

    fun purchaseCertificate(
        userId: String,
        userName: String,
        userAvatarUrl: String,
        languageId: Int,
        languageName: String,
        levelReached: Int,
        totalPoints: Int,
        paymentMethod: String,
        transactionId: String,
        feeAmountBdt: Double = 150.0,
        onSuccess: (LanguageCertificate) -> Unit
    ) {
        viewModelScope.launch {
            val cert = learningRepository.issueCertificate(
                userId = userId,
                userName = userName,
                userAvatarUrl = userAvatarUrl,
                languageId = languageId,
                languageName = languageName,
                levelReached = levelReached,
                totalPoints = totalPoints,
                paymentMethod = paymentMethod,
                transactionId = transactionId,
                feeAmountBdt = feeAmountBdt
            )
            onSuccess(cert)
        }
    }

    fun closeActiveSession() {
        _activePracticeWords.value = emptyList()
        _activeExamWords.value = emptyList()
        _activeLevel.value = null
    }

    companion object {
        fun provideFactory(context: Context): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val database = AppDatabase.getDatabase(context)
                    val learningRepository = LearningRepository(database.learningDao())
                    return LanguageLearningViewModel(learningRepository) as T
                }
            }
        }
    }
}
