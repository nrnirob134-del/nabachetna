package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.model.Language
import com.example.data.model.UserScore
import com.example.data.repository.LearningRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

@OptIn(ExperimentalCoroutinesApi::class)
class LeaderboardViewModel(private val learningRepository: LearningRepository) : ViewModel() {
    val languages: StateFlow<List<Language>> = learningRepository.allLanguages
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedLanguageId = MutableStateFlow(1)
    val selectedLanguageId: StateFlow<Int> = _selectedLanguageId.asStateFlow()

    val leaderboard: StateFlow<List<UserScore>> = _selectedLanguageId.flatMapLatest { langId ->
        learningRepository.getLeaderboard(langId)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectLanguage(languageId: Int) {
        _selectedLanguageId.value = languageId
    }

    companion object {
        fun provideFactory(context: android.content.Context): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val database = AppDatabase.getDatabase(context)
                    val learningRepository = LearningRepository(database.learningDao())
                    return LeaderboardViewModel(learningRepository) as T
                }
            }
        }
    }
}
