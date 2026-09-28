package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface LearningDao {
    @Query("SELECT * FROM languages ORDER BY id ASC")
    fun getAllLanguages(): Flow<List<Language>>

    @Query("SELECT * FROM languages WHERE id = :id")
    fun getLanguageById(id: Int): Flow<Language?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLanguage(language: Language): Long

    @Query("SELECT * FROM levels WHERE languageId = :languageId ORDER BY levelNumber ASC")
    fun getLevelsForLanguage(languageId: Int): Flow<List<Level>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLevel(level: Level): Long

    @Update
    suspend fun updateLevel(level: Level)

    @Query("SELECT * FROM steps WHERE levelId = :levelId ORDER BY stepNumber ASC")
    fun getStepsForLevel(levelId: Int): Flow<List<Step>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStep(step: Step): Long

    @Query("SELECT * FROM words WHERE stepId = :stepId ORDER BY id ASC")
    fun getWordsForStep(stepId: Int): Flow<List<Word>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWord(word: Word): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateScore(userScore: UserScore)

    @Query("SELECT * FROM user_scores WHERE languageId = :languageId ORDER BY totalPoints DESC")
    fun getLeaderboardForLanguage(languageId: Int): Flow<List<UserScore>>

    @Query("SELECT * FROM user_scores ORDER BY totalPoints DESC")
    fun getAllLeaderboards(): Flow<List<UserScore>>

    @Query("SELECT * FROM user_scores WHERE userId = :userId AND languageId = :languageId LIMIT 1")
    fun getUserScoreForLanguage(userId: String, languageId: Int): Flow<UserScore?>

    // --- Admin-Controlled Custom Sponsored Ads ---
    @Query("SELECT * FROM admin_sponsored_ads WHERE isActive = 1 ORDER BY id DESC")
    fun getActiveSponsoredAds(): Flow<List<AdminSponsoredAd>>

    @Query("SELECT * FROM admin_sponsored_ads ORDER BY id DESC")
    fun getAllSponsoredAds(): Flow<List<AdminSponsoredAd>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSponsoredAd(ad: AdminSponsoredAd): Long

    @Update
    suspend fun updateSponsoredAd(ad: AdminSponsoredAd)

    @Query("DELETE FROM admin_sponsored_ads WHERE id = :adId")
    suspend fun deleteSponsoredAd(adId: Int)

    @Query("UPDATE admin_sponsored_ads SET impressions = impressions + 1 WHERE id = :adId")
    suspend fun incrementAdImpressions(adId: Int)

    @Query("UPDATE admin_sponsored_ads SET clicks = clicks + 1 WHERE id = :adId")
    suspend fun incrementAdClicks(adId: Int)

    // --- Language Certificates & Revenue Transactions ---
    @Query("SELECT * FROM language_certificates ORDER BY issueDate DESC")
    fun getAllCertificates(): Flow<List<LanguageCertificate>>

    @Query("SELECT * FROM language_certificates WHERE userId = :userId ORDER BY issueDate DESC")
    fun getUserCertificates(userId: String): Flow<List<LanguageCertificate>>

    @Query("SELECT * FROM language_certificates WHERE userId = :userId AND languageId = :languageId LIMIT 1")
    fun getCertificateForUserLanguage(userId: String, languageId: Int): Flow<LanguageCertificate?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCertificate(certificate: LanguageCertificate)

    @Update
    suspend fun updateCertificate(certificate: LanguageCertificate)
}
