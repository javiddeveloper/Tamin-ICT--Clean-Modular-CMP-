package com.tamin.taminhamrah.data.repository.common

import com.tamin.taminhamrah.dataSource.commonSource.CommonRemoteDataSource
import com.tamin.taminhamrah.data.local.dao.MenuDao
import com.tamin.taminhamrah.data.mapper.toDomain
import com.tamin.taminhamrah.data.mapper.toEntity
import com.tamin.taminhamrah.model.common.BeneficiaryDN
import com.tamin.taminhamrah.model.common.MainServiceDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.common.MenuServiceStatusDN
import com.tamin.taminhamrah.repository.common.CommonRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

class CommonRepositoryImpl(
    private val commonRemoteDataSource: CommonRemoteDataSource,
    private val menuDao: MenuDao
) : CommonRepository {
    override fun getBeneficiary(filters: List<ApiFilterDN>): Flow<List<BeneficiaryDN>> = flow {
        try {
            val response = commonRemoteDataSource.getBeneficiary(ApiQueryParamDN(filters = filters))
            emit(response.list?.map { it.toDomain() } ?: emptyList())
        } catch (e: Exception) {
            throw e
        }
    }

    override fun getMainMenu(
        versionCode: String,
        forceUpdate: Boolean
    ): Flow<List<MainServiceDN>> = menuDao.getMenuItems()
        .map { entities ->
            entities
                .map { it.toDomain() }
                .filter { it.status != MenuServiceStatusDN.COMPLETELY_DISABLED }
        }
        .onStart {
            try {
                val remoteMenu = commonRemoteDataSource.getMainMenu(versionCode, forceUpdate)
                menuDao.insertMenuItems(remoteMenu.map { it.toDomain().toEntity() })
            } catch (e: Exception) {
                throw e
            }
        }
}
