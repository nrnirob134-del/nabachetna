package com.example.data.service

import com.example.data.model.FeatureSplashPoster
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

object FeatureSplashPosterService {

    private val initialPosters = listOf(
        FeatureSplashPoster(
            id = "app_launch",
            featureKey = "APP_LAUNCH",
            featureName = "অ্যাপ ওপেনিং (App Launch)",
            iconEmoji = "🌐",
            imageUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=1080",
            durationSeconds = 2,
            isEnabled = true
        ),
        FeatureSplashPoster(
            id = "language_learning",
            featureKey = "LANGUAGE_LEARNING",
            featureName = "ভাষা শিক্ষা একাডেমি",
            iconEmoji = "🎓",
            imageUrl = "https://images.unsplash.com/photo-1523240795612-9a054b0db644?w=1080",
            durationSeconds = 3,
            isEnabled = true
        ),
        FeatureSplashPoster(
            id = "marketplace",
            featureKey = "MARKETPLACE",
            featureName = "এসক্রো মার্কেটপ্লেস ও ওয়্যারহাউজ",
            iconEmoji = "🛍️",
            imageUrl = "https://images.unsplash.com/photo-1556742049-0a67e55722ee?w=1080",
            durationSeconds = 3,
            isEnabled = true
        ),
        FeatureSplashPoster(
            id = "blood_bank",
            featureKey = "BLOOD_BANK",
            featureName = "জরুরি সেবা ও ব্লাড ব্যাংক",
            iconEmoji = "🩸",
            imageUrl = "https://images.unsplash.com/photo-1615461066841-6116e61058f4?w=1080",
            durationSeconds = 3,
            isEnabled = true
        ),
        FeatureSplashPoster(
            id = "messenger",
            featureKey = "MESSENGER",
            featureName = "মেসেঞ্জার ও স্পোকেন চ্যাট",
            iconEmoji = "💬",
            imageUrl = "https://images.unsplash.com/photo-1577563908411-5077b6dc7624?w=1080",
            durationSeconds = 2,
            isEnabled = true
        ),
        FeatureSplashPoster(
            id = "browser",
            featureKey = "BROWSER",
            featureName = "সার্বভৌম সুরক্ষিত ব্রাউজার",
            iconEmoji = "🧭",
            imageUrl = "https://images.unsplash.com/photo-1526374965328-7f61d4dc18c5?w=1080",
            durationSeconds = 2,
            isEnabled = true
        ),
        FeatureSplashPoster(
            id = "anti_corruption",
            featureKey = "ANTI_CORRUPTION",
            featureName = "দুর্নীতি প্রকাশ ও হুইসেলব্লোয়ার",
            iconEmoji = "⚖️",
            imageUrl = "https://images.unsplash.com/photo-1589829545856-d10d557cf95f?w=1080",
            durationSeconds = 3,
            isEnabled = true
        ),
        FeatureSplashPoster(
            id = "watch",
            featureKey = "WATCH",
            featureName = "ভিডিও হাব ও শর্টস রিলস",
            iconEmoji = "🎬",
            imageUrl = "https://images.unsplash.com/photo-1536240478700-b869070f9279?w=1080",
            durationSeconds = 2,
            isEnabled = true
        )
    )

    private val _posters = MutableStateFlow(initialPosters)
    val posters: StateFlow<List<FeatureSplashPoster>> = _posters.asStateFlow()

    fun getPosterForFeature(featureKey: String): FeatureSplashPoster? {
        return _posters.value.find { it.featureKey.equals(featureKey, ignoreCase = true) || it.id.equals(featureKey, ignoreCase = true) }
    }

    fun updatePoster(updated: FeatureSplashPoster) {
        _posters.update { list ->
            list.map { if (it.id == updated.id) updated else it }
        }
    }

    fun togglePosterEnabled(posterId: String, enabled: Boolean) {
        _posters.update { list ->
            list.map { if (it.id == posterId) it.copy(isEnabled = enabled) else it }
        }
    }

    fun updateDuration(posterId: String, durationSec: Int) {
        _posters.update { list ->
            list.map { if (it.id == posterId) it.copy(durationSeconds = durationSec.coerceIn(1, 5)) else it }
        }
    }
}
