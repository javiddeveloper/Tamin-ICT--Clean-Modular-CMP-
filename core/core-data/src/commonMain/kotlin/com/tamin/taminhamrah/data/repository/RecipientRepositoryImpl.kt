package com.tamin.taminhamrah.data.repository

import com.tamin.core.network.datasource.commonSource.CommonRemoteDataSource
import com.tamin.taminhamrah.data.local.dao.RecipientDao
import com.tamin.taminhamrah.data.mapper.toDomain
import com.tamin.taminhamrah.data.mapper.toEntity
import com.tamin.taminhamrah.model.common.RecipientDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.repository.RecipientRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

internal class RecipientRepositoryImpl(
    private val commonRemoteDataSource: CommonRemoteDataSource,
    private val recipientDao: RecipientDao
) : RecipientRepository {
    override fun getRecipientList(filters: List<ApiFilterDN>): Flow<List<RecipientDN>> = flow {
        val localRecipients = recipientDao.getRecipients().first()
        emit(localRecipients.map { it.toDomain() })

        try {
            val response = commonRemoteDataSource.getRecipientList(ApiQueryParamDN(filters = filters))
            val remoteRecipients = response.list?.map { it.toDomain() } ?: emptyList()

            if (remoteRecipients.isNotEmpty()) {
                recipientDao.upsertRecipients(remoteRecipients.map { it.toEntity() })
            }
        } catch (e: Exception) {
            if (localRecipients.isEmpty()) {
                throw e
            }
        }

        emitAll(recipientDao.getRecipients().map { list -> list.map { it.toDomain() } })
    }.distinctUntilChanged()
}
