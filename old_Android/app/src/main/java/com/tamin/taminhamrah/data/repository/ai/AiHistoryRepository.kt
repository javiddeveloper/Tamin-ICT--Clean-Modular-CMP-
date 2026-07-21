package com.tamin.taminhamrah.data.repository.ai

import com.tamin.taminhamrah.data.entity.ai.CategoryWithLastMessage
import com.tamin.taminhamrah.data.repository.ai.model.AiChatModel
import com.tamin.taminhamrah.data.repository.ai.model.AiHistoryCategory
import kotlinx.coroutines.flow.Flow

interface AiAgentHistoryRepository {
    fun getAllCategories(userNationalCode: String): Flow<List<AiHistoryCategory>>
    suspend fun deleteAllCategories(userNationalCode: String): Int?
    suspend fun createNewCategory(title: String,userNationalCode: String): String
    suspend fun getCategory(id: String): AiHistoryCategory?
    suspend fun deleteCategory(id: String): Int?
    suspend fun deleteCategory(category: AiHistoryCategory): Int?
    suspend fun updateCategory(id: String, title: String): Int?
    suspend fun saveMessage(message: AiChatModel, categoryId: String)
    suspend fun saveMessages(messages: List<AiChatModel>, categoryId: String)
    suspend fun getMessagesInCategory(categoryId: String): List<AiChatModel>
    suspend fun getCategoriesWithLastMessage(): List<CategoryWithLastMessage>
    suspend fun updateCategory(category: AiHistoryCategory) : Int?
}