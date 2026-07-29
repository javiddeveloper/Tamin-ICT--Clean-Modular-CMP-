package com.tamin.taminhamrah.data.repository.ai

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.tamin.common.network.Dispatcher
import com.tamin.common.network.TaminDispatchers.IO
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.data.entity.ai.CategoryWithLastMessage
import com.tamin.taminhamrah.data.entity.ai.AiChatModelMapper
import com.tamin.taminhamrah.data.entity.ai.toDomain
import com.tamin.taminhamrah.data.local.ai.datasource.AiHistoryLocalDataSource
import com.tamin.taminhamrah.data.repository.ai.model.AiChatModel
import com.tamin.taminhamrah.data.repository.ai.model.AiHistoryCategory
import com.tamin.taminhamrah.data.repository.ai.model.toEntity
import com.tamin.taminhamrah.ui.aiAgent.domain.repository.AiHistoryRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AiHistoryRepositoryImpl @Inject constructor(
    private val localDataSource: AiHistoryLocalDataSource,
    private val mapper: AiChatModelMapper,
    @Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher,
) : AiHistoryRepository {

    override fun getAllCategories(userNationalCode: String): Flow<List<AiHistoryCategory>> {
        return localDataSource.getAllCategories(userNationalCode).map { it.toDomain() }
            .flowOn(ioDispatcher)
    }

    override fun getAllCategoriesPaging(userNationalCode: String): Flow<PagingData<AiHistoryCategory>> {
        return Pager(
            config = PagingConfig(
                pageSize = Constants.QUERY_PAGE_SIZE_10,
                prefetchDistance = 2,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { localDataSource.getAllCategoriesPaging(userNationalCode) }
        ).flow.map { pagingData -> pagingData.map { it.toDomain() } }
            .flowOn(ioDispatcher)
    }

    override fun getMessagesInCategoryPaging(categoryId: String): Flow<PagingData<AiChatModel>> {
        return Pager(
            config = PagingConfig(
                pageSize = Constants.QUERY_PAGE_SIZE_10,
                prefetchDistance = 2,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { localDataSource.getMessagesInCategoryPaging(categoryId) }
        ).flow.map { pagingData ->
            pagingData.map { mapper.fromEntityOrPlaceholder(it) }
        }.flowOn(ioDispatcher)
    }


    override suspend fun deleteAllCategories(userNationalCode: String): Int? =
        withContext(ioDispatcher) {
            localDataSource.deleteAllCategories(userNationalCode)
        }


    override suspend fun createNewCategory(title: String, userNationalCode: String): String {
        return withContext(ioDispatcher) {
            localDataSource.createNewCategory(title, userNationalCode)
        }
    }

    override suspend fun getCategory(id: String) :AiHistoryCategory = withContext(ioDispatcher) {
        localDataSource.getCategory(id)?.toDomain() as AiHistoryCategory
    }

    override suspend fun deleteCategory(id: String) = withContext(ioDispatcher) {
        localDataSource.deleteCategory(id)
    }

    override suspend fun deleteCategory(category: AiHistoryCategory) = withContext(ioDispatcher) {
        localDataSource.deleteCategory(category.toEntity())
    }

    override suspend fun updateCategory(id: String, title: String): Int? {
        return withContext(ioDispatcher) {
            localDataSource.updateCategory(id, title)
        }
    }

    override suspend fun saveMessage(
        message: AiChatModel,
        categoryId: String
    ) {
        withContext(ioDispatcher) {
            localDataSource.saveMessage(message, categoryId)
        }
    }

    override suspend fun saveMessages(
        messages: List<AiChatModel>,
        categoryId: String
    ) {
        withContext(ioDispatcher) {
            localDataSource.saveMessages(messages, categoryId)
        }
    }

    override suspend fun getMessagesInCategory(categoryId: String): List<AiChatModel> {
        return withContext(ioDispatcher) {
            localDataSource.getMessagesInCategory(categoryId)
        }
    }

    override suspend fun getCategoriesWithLastMessage(): List<CategoryWithLastMessage> {
        return withContext(ioDispatcher) {
            localDataSource.getCategoriesWithLastMessage()
        }
    }

    override suspend fun updateCategory(category: AiHistoryCategory): Int? {
        return withContext(ioDispatcher) {
            localDataSource.updateCategory(category.toEntity())
        }
    }


}
