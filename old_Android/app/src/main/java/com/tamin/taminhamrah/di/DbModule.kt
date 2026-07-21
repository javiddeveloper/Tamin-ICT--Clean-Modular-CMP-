package com.tamin.taminhamrah.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.tamin.taminhamrah.data.local.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DbModule {

    @Provides
    @Singleton
    internal fun provideDatabase(
        @ApplicationContext context: Context,
        @Named("dbName") dbName: String
    ) = Room.databaseBuilder(context, AppDatabase::class.java, dbName)
        .fallbackToDestructiveMigration(false)
        .addCallback(roomCallBack)
//        .addMigrations(MIGRATION_2_3)
        .build()


    @Provides
    @Singleton
    internal fun provideExploreDao(appDatabase: AppDatabase) = appDatabase.servicesDao

    @Provides
    @Singleton
    internal fun provideAiCategoryDao(appDatabase: AppDatabase) = appDatabase.chatCategoryDao
    @Provides
    @Singleton
    internal fun provideChatMessageDao(appDatabase: AppDatabase) = appDatabase.chatMessageDao

    @Provides
    @Singleton
    internal fun provideAgentChatMessageDao(appDatabase: AppDatabase) = appDatabase.agentChatMessageDao

    @Provides
    @Singleton
    internal fun provideAgentChatCategoryMessageDao(appDatabase: AppDatabase) = appDatabase.agentChatCategoryDao



    @Provides
    @Singleton
    @Named("dbName")
    fun provideDatabaseName() = "eServices.db"


    //fallbacktodestructivemigration : to handle versions
    private val roomCallBack: RoomDatabase.Callback = object : RoomDatabase.Callback() {
    }

    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("CREATE TABLE IF NOT EXISTS `service_log` (`id` INTEGER, PRIMARY KEY(`id`))")
        }
    }

    val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE ai_chat_messages ADD COLUMN voicePath TEXT")
        }
    }


}
