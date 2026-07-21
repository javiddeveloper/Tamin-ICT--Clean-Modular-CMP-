package com.tamin.taminhamrah.data.local.ai.datasource

import androidx.paging.PagingSource
import com.tamin.taminhamrah.data.entity.ai.AiChatModelMapper
import com.tamin.taminhamrah.data.entity.ai.AiHistoryCategoryEntity
import com.tamin.taminhamrah.data.entity.ai.ChatMessageEntity
import com.tamin.taminhamrah.data.entity.ai.CategoryWithLastMessage
import com.tamin.taminhamrah.data.local.ai.dao.ChatCategoryDao
import com.tamin.taminhamrah.data.local.ai.dao.ChatMessageDao
import com.tamin.taminhamrah.data.repository.ai.model.AiChatModel
import com.tamin.taminhamrah.utils.extentions.randomUUID
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AiHistoryLocalDataSourceImp @Inject constructor(
    private val categoryDao: ChatCategoryDao,
    private val chatDao: ChatMessageDao,
    private val mapper: AiChatModelMapper
) : AiHistoryLocalDataSource {

    override fun getAllCategories(userNationalCode: String?): Flow<List<AiHistoryCategoryEntity>> = categoryDao.getAll(userNationalCode)

    override fun getAllCategoriesPaging(userNationalCode: String?): PagingSource<Int, AiHistoryCategoryEntity> =
        categoryDao.getAllPaging(userNationalCode)

    override suspend fun deleteAllCategories(userNationalCode: String?) = categoryDao.deleteAll(userNationalCode)


    override suspend fun getCategory(id: String): AiHistoryCategoryEntity? =
        categoryDao.getCategory(id)

    override suspend fun deleteCategory(id: String): Int? = categoryDao.deleteCategory(id)

    override suspend fun deleteCategory(categoryEntity: AiHistoryCategoryEntity): Int? =
        categoryDao.deleteCategory(categoryEntity)

    override suspend fun updateCategory(id: String, title: String): Int? =
        categoryDao.updateCategory(id, title)




    override suspend fun createNewCategory(title: String,userNationalCode: String): String {
        val categoryId = "category_${randomUUID()}"
        val category = AiHistoryCategoryEntity(
            id = categoryId,
            title = title,
            userNationalCode =  userNationalCode,
            date = System.currentTimeMillis(),
            lastMessageDate = System.currentTimeMillis(),
            messageCount = 0
        )
        categoryDao.insertCategory(category)
        return categoryId
    }

    override suspend fun saveMessage(message: AiChatModel, categoryId: String) {
        val lastOrder = chatDao.getLastMessageOrder(categoryId) ?: -1
        val entity = mapper.toEntity(message, categoryId).copy(
            categoryId = categoryId,
            messageOrder = lastOrder + 1
        )

        chatDao.insertMessage(entity)
        categoryDao.updateCategoryStats(categoryId, System.currentTimeMillis())
    }

    override suspend fun saveMessages(messages: List<AiChatModel>, categoryId: String) {
        val startOrder = chatDao.getLastMessageOrder(categoryId)?.plus(1) ?: 0

        val entities = messages.mapIndexed { index, message ->
            mapper.toEntity(message, categoryId).copy(
                categoryId = categoryId,
                messageOrder = startOrder + index
            )
        }

        chatDao.insertMessages(entities)
        categoryDao.updateCategoryStats(categoryId, System.currentTimeMillis())
    }

    override suspend fun getMessagesInCategory(categoryId: String): List<AiChatModel> {
        val entities = chatDao.getMessagesByCategory(categoryId)
        return mapper.fromEntityList(entities)
    }

    override fun getMessagesInCategoryPaging(categoryId: String): PagingSource<Int, ChatMessageEntity> {
        return chatDao.getMessagesByCategoryPaging(categoryId)
    }

    override suspend fun getCategoriesWithLastMessage(): List<CategoryWithLastMessage> {
        return categoryDao.getCategoriesWithLastMessage()
    }

    suspend fun getLastMessageOrder(categoryId: String): Int? {
        return chatDao.getLastMessageOrder(categoryId)
    }

    override suspend fun updateCategory(category: AiHistoryCategoryEntity): Int? = categoryDao.updateCategory(category)



}
