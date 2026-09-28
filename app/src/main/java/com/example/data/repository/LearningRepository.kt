package com.example.data.repository

import com.example.data.dao.LearningDao
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.text.SimpleDateFormat
import java.util.*

class LearningRepository(private val learningDao: LearningDao) {
    val allLanguages: Flow<List<Language>> = learningDao.getAllLanguages()

    suspend fun insertLanguage(language: Language) = learningDao.insertLanguage(language)

    fun getLevelsForLanguage(languageId: Int): Flow<List<Level>> = learningDao.getLevelsForLanguage(languageId)

    suspend fun insertLevel(level: Level) = learningDao.insertLevel(level)

    fun getStepsForLevel(levelId: Int): Flow<List<Step>> = learningDao.getStepsForLevel(levelId)

    suspend fun insertStep(step: Step) = learningDao.insertStep(step)

    fun getWordsForStep(stepId: Int): Flow<List<Word>> = learningDao.getWordsForStep(stepId)

    suspend fun insertWord(word: Word) = learningDao.insertWord(word)

    fun getLeaderboard(languageId: Int): Flow<List<UserScore>> = learningDao.getLeaderboardForLanguage(languageId)

    fun getUserScoreForLanguage(userId: String, languageId: Int): Flow<UserScore?> =
        learningDao.getUserScoreForLanguage(userId, languageId)

    // Admin Custom Sponsored Ads flows & methods
    val allSponsoredAds: Flow<List<AdminSponsoredAd>> = learningDao.getAllSponsoredAds()
    val activeSponsoredAds: Flow<List<AdminSponsoredAd>> = learningDao.getActiveSponsoredAds()

    suspend fun insertSponsoredAd(ad: AdminSponsoredAd) = learningDao.insertSponsoredAd(ad)
    suspend fun updateSponsoredAd(ad: AdminSponsoredAd) = learningDao.updateSponsoredAd(ad)
    suspend fun deleteSponsoredAd(adId: Int) = learningDao.deleteSponsoredAd(adId)
    suspend fun recordAdImpression(adId: Int) = learningDao.incrementAdImpressions(adId)
    suspend fun recordAdClick(adId: Int) = learningDao.incrementAdClicks(adId)

    // Certificate flows & methods
    val allCertificates: Flow<List<LanguageCertificate>> = learningDao.getAllCertificates()
    fun getUserCertificates(userId: String): Flow<List<LanguageCertificate>> = learningDao.getUserCertificates(userId)
    fun getCertificateForUserLanguage(userId: String, languageId: Int): Flow<LanguageCertificate?> =
        learningDao.getCertificateForUserLanguage(userId, languageId)

    suspend fun issueCertificate(
        userId: String,
        userName: String,
        userAvatarUrl: String,
        languageId: Int,
        languageName: String,
        levelReached: Int,
        totalPoints: Int,
        paymentMethod: String,
        transactionId: String,
        feeAmountBdt: Double = 150.0
    ): LanguageCertificate {
        val certId = "NC-${languageName.take(2).uppercase(Locale.ROOT)}-${System.currentTimeMillis().toString().takeLast(6)}"
        val dateStr = SimpleDateFormat("dd MMM yyyy", Locale.US).format(Date())
        val grade = when {
            totalPoints >= 100 -> "A+ (Master)"
            totalPoints >= 60 -> "A (Expert)"
            totalPoints >= 30 -> "B+ (Advanced)"
            else -> "B (Qualified)"
        }
        val cert = LanguageCertificate(
            certificateId = certId,
            userId = userId,
            userName = userName,
            userAvatarUrl = userAvatarUrl,
            languageId = languageId,
            languageName = languageName,
            levelReached = levelReached,
            totalPoints = totalPoints,
            issueDate = dateStr,
            status = "VERIFIED",
            feeAmountBdt = feeAmountBdt,
            paymentMethod = paymentMethod,
            transactionId = transactionId.ifBlank { "TXN_${System.currentTimeMillis().toString().takeLast(8)}" },
            qrCodePayload = "NABACHETNA-OFFICIAL-VERIFIED-CERTIFICATE:$certId",
            grade = grade
        )
        learningDao.insertCertificate(cert)
        return cert
    }

    suspend fun seedInitialData() {
        val existingLanguages = learningDao.getAllLanguages().firstOrNull().orEmpty()
        if (existingLanguages.isEmpty()) {
            LanguageSeedData.SUPPORTED_LANGUAGES.forEach { lang ->
                val langId = learningDao.insertLanguage(lang).toInt()
                seedLevelsAndInitialContent(langId, lang.name, lang.code)
                LanguageSeedData.getInitialLeaderboard(langId, lang.name).forEach { score ->
                    learningDao.insertOrUpdateScore(score)
                }
            }
        } else {
            // Check if all 10 languages exist, insert any missing ones
            LanguageSeedData.SUPPORTED_LANGUAGES.forEach { seedLang ->
                val found = existingLanguages.find { it.id == seedLang.id || it.code == seedLang.code }
                if (found == null) {
                    val langId = learningDao.insertLanguage(seedLang).toInt()
                    seedLevelsAndInitialContent(langId, seedLang.name, seedLang.code)
                    LanguageSeedData.getInitialLeaderboard(langId, seedLang.name).forEach { score ->
                        learningDao.insertOrUpdateScore(score)
                    }
                }
            }
        }

        // Seed initial sponsored ads if empty
        val existingAds = learningDao.getAllSponsoredAds().firstOrNull().orEmpty()
        if (existingAds.isEmpty()) {
            LanguageSeedData.getInitialSponsoredAds().forEach { ad ->
                learningDao.insertSponsoredAd(ad)
            }
        }

        // Seed initial certificates if empty
        val existingCerts = learningDao.getAllCertificates().firstOrNull().orEmpty()
        if (existingCerts.isEmpty()) {
            LanguageSeedData.getInitialCertificates().forEach { cert ->
                learningDao.insertCertificate(cert)
            }
        }
    }

    private suspend fun seedLevelsAndInitialContent(languageId: Int, languageName: String, langCode: String) {
        // Create 200 levels: Level 1 is unlocked, 2..200 locked
        for (lvl in 1..200) {
            val levelId = learningDao.insertLevel(
                Level(
                    languageId = languageId,
                    levelNumber = lvl,
                    isLocked = lvl > 1,
                    pointsEarned = 0,
                    isExamPassed = false
                )
            ).toInt()

            if (lvl == 1) {
                populateWordsForLevel(languageId, levelId, lvl, langCode)
            }
        }
    }

    suspend fun fetchAndPopulateWords(languageId: Int, levelNum: Int) {
        val levels = learningDao.getLevelsForLanguage(languageId).firstOrNull().orEmpty()
        val level = levels.find { it.levelNumber == levelNum } ?: return
        val existingSteps = learningDao.getStepsForLevel(level.id).firstOrNull().orEmpty()
        if (existingSteps.isNotEmpty()) return

        val languages = learningDao.getAllLanguages().firstOrNull().orEmpty()
        val lang = languages.find { it.id == languageId }
        val langCode = lang?.code ?: "en"
        populateWordsForLevel(languageId, level.id, levelNum, langCode)
    }

    private suspend fun populateWordsForLevel(languageId: Int, levelId: Int, levelNum: Int, langCode: String) {
        // Step 1: Practice Camp (100 words in 5 milestones of 20 words)
        val practiceStepId = learningDao.insertStep(
            Step(levelId = levelId, stepNumber = 1, content = "PRACTICE_CAMP_$levelNum", type = "PRACTICE")
        ).toInt()

        val vocabList = LanguageSeedData.REAL_VOCAB_MAP[langCode] ?: LanguageSeedData.REAL_VOCAB_MAP["en"]!!
        val vocabSize = vocabList.size

        for (i in 1..100) {
            val template = vocabList[(i - 1) % vocabSize]
            val wordTitle = if (i <= vocabSize && levelNum == 1) {
                template.word
            } else {
                "${template.word} L${levelNum}·W$i"
            }
            val translation = if (i <= vocabSize && levelNum == 1) {
                template.translation
            } else {
                "${template.translation} (লেভেল $levelNum - $i)"
            }
            learningDao.insertWord(
                Word(
                    stepId = practiceStepId,
                    word = wordTitle,
                    translation = translation,
                    pronunciation = template.pronunciation,
                    exampleSentence = template.example
                )
            )
        }

        // Step 2: Exam (50 questions)
        val examStepId = learningDao.insertStep(
            Step(levelId = levelId, stepNumber = 2, content = "EXAM_$levelNum", type = "EXAM")
        ).toInt()

        for (i in 1..50) {
            val template = vocabList[(i - 1) % vocabSize]
            learningDao.insertWord(
                Word(
                    stepId = examStepId,
                    word = "Q$i: ${template.word}",
                    translation = template.translation,
                    pronunciation = template.pronunciation,
                    exampleSentence = "Choose correct translation for ${template.word}"
                )
            )
        }
    }

    suspend fun addPracticePoint(
        level: Level,
        userId: String = "user_me",
        userName: String = "You",
        languageName: String = "English",
        avatarUrl: String = ""
    ) {
        val newPoints = minOf(5, level.pointsEarned + 1)
        learningDao.updateLevel(level.copy(pointsEarned = newPoints))
        updateScore(userId, userName, level.languageId, languageName, 1, avatarUrl)
    }

    suspend fun passExam(
        level: Level,
        userId: String = "user_me",
        userName: String = "You",
        languageName: String = "English",
        avatarUrl: String = ""
    ) {
        learningDao.updateLevel(level.copy(isExamPassed = true))

        // Unlock next level for this specific language
        val levels = learningDao.getLevelsForLanguage(level.languageId).firstOrNull().orEmpty()
        val nextLevel = levels.find { it.levelNumber == level.levelNumber + 1 }
        if (nextLevel != null) {
            learningDao.updateLevel(nextLevel.copy(isLocked = false))
        }

        // Award bonus exam points (+5 points) to the language's leaderboard
        updateScore(userId, userName, level.languageId, languageName, 5, avatarUrl, incrementCompletedLevels = true)
    }

    suspend fun updateScore(
        userId: String,
        userName: String,
        languageId: Int,
        languageName: String,
        pointsToAdd: Int,
        avatarUrl: String = "",
        incrementCompletedLevels: Boolean = false
    ) {
        val scoreKey = "${userId}_${languageId}"
        val leaderboard = learningDao.getLeaderboardForLanguage(languageId).firstOrNull().orEmpty()
        val currentScore = leaderboard.find { it.id == scoreKey || it.userId == userId }

        val newTotalPoints = (currentScore?.totalPoints ?: 0) + pointsToAdd
        val newLevelsCompleted = (currentScore?.levelsCompleted ?: 0) + (if (incrementCompletedLevels) 1 else 0)

        val rankBadge = when {
            newTotalPoints >= 100 -> "👑 Legend"
            newTotalPoints >= 60 -> "🥇 Master"
            newTotalPoints >= 30 -> "🥈 Expert"
            newTotalPoints >= 15 -> "🥉 Advanced"
            newTotalPoints >= 5 -> "⭐ Intermediate"
            else -> "🌱 Beginner"
        }

        learningDao.insertOrUpdateScore(
            UserScore(
                id = scoreKey,
                userId = userId,
                languageId = languageId,
                languageName = languageName,
                userName = userName,
                avatarUrl = avatarUrl.ifBlank { currentScore?.avatarUrl ?: "" },
                totalPoints = newTotalPoints,
                levelsCompleted = newLevelsCompleted,
                rankBadge = rankBadge
            )
        )
    }
}
