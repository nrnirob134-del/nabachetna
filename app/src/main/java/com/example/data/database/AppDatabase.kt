package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.*
import com.example.data.model.*

@Database(
    entities = [
        User::class,
        Post::class,
        Comment::class,
        Story::class,
        Notification::class,
        AppStoreItem::class,
        Page::class,
        WalletRechargeRequest::class,
        Language::class,
        Level::class,
        Step::class,
        Word::class,
        UserScore::class,
        AdminSponsoredAd::class,
        LanguageCertificate::class
    ],
    version = 7,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun postDao(): PostDao
    abstract fun commentDao(): CommentDao
    abstract fun storyDao(): StoryDao
    abstract fun notificationDao(): NotificationDao
    abstract fun appStoreItemDao(): AppStoreItemDao
    abstract fun pageDao(): PageDao
    abstract fun walletRechargeDao(): WalletRechargeDao
    abstract fun learningDao(): LearningDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "pagebook_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
