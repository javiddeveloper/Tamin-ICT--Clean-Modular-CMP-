package com.tamin.taminhamrah.data.local.ai.datasource

import androidx.paging.PagingSource
import com.tamin.taminhamrah.data.entity.ai.AiHistoryCategoryEntity
import com.tamin.taminhamrah.data.entity.ai.ChatMessageEntity
import com.tamin.taminhamrah.data.entity.ai.CategoryWithLastMessage
import com.tamin.taminhamrah.data.repository.ai.model.AiChatModel
import kotlinx.coroutines.flow.Flow

interface AiHistoryLocalDataSource {
   suspend fun createNewCategory(title: String,userNationalCode: String): String
    suspend fun getCategory(id: String): AiHistoryCategoryEntity?
    fun getAllCategories(userNationalCode: String?): Flow<List<AiHistoryCategoryEntity>>
    fun getAllCategoriesPaging(userNationalCode: String?): PagingSource<Int, AiHistoryCategoryEntity>
   suspend fun deleteAllCategories(userNationalCode: String?): Int
   suspend fun deleteCategory(id: String): Int?
   suspend fun deleteCategory(categoryEntity: AiHistoryCategoryEntity): Int?
   suspend fun updateCategory(id: String, title: String): Int?
   suspend fun saveMessage(message: AiChatModel, categoryId: String)
   suspend fun saveMessages(messages: List<AiChatModel>, categoryId: String)
   suspend fun getMessagesInCategory(categoryId: String): List<AiChatModel>
   fun getMessagesInCategoryPaging(categoryId: String): PagingSource<Int, ChatMessageEntity>
   suspend fun getCategoriesWithLastMessage(): List<CategoryWithLastMessage>
   suspend fun updateCategory(category: AiHistoryCategoryEntity): Int?
}
