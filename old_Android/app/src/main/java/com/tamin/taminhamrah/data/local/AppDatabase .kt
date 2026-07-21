package com.tamin.taminhamrah.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.tamin.taminhamrah.data.entity.ai.AiChatMessageEntity
import com.tamin.taminhamrah.data.entity.ai.AiHistoryCategoryEntity
import com.tamin.taminhamrah.data.entity.ai.ChatMessageEntity
import com.tamin.taminhamrah.data.local.ai.dao.AgentChatCategoryDao
import com.tamin.taminhamrah.data.local.ai.dao.AgentChatMessageDao
import com.tamin.taminhamrah.data.local.ai.dao.ChatCategoryDao
import com.tamin.taminhamrah.data.local.ai.dao.ChatMessageDao
import com.tamin.taminhamrah.data.local.services.ServiceDao
import com.tamin.taminhamrah.data.local.services.converter.AppliedServiceConverter
import com.tamin.taminhamrah.data.local.services.entity.AppliedServiceEntity


@Database(
    entities = [AppliedServiceEntity::class, AiHistoryCategoryEntity::class, ChatMessageEntity::class, AiChatMessageEntity::class],
    version = 3,
    exportSchema = false
)
@TypeConverters(
    AppliedServiceConverter::class
)

abstract class AppDatabase : RoomDatabase() {
    abstract val servicesDao: ServiceDao
    abstract val chatCategoryDao: ChatCategoryDao
    abstract val chatMessageDao: ChatMessageDao

    abstract val agentChatCategoryDao: AgentChatCategoryDao
    abstract val agentChatMessageDao: AgentChatMessageDao
}
