package com.tamin.taminhamrah.data.repository.common


import com.tamin.taminhamrah.data.local.dao.MenuDao
import com.tamin.taminhamrah.data.mapper.toDomain
import com.tamin.taminhamrah.data.mapper.toEntity
import com.tamin.taminhamrah.dataSource.commonSource.CommonRemoteDataSource
import com.tamin.taminhamrah.model.common.BeneficiaryDN
import com.tamin.taminhamrah.model.common.InsuranceTypeDN
import com.tamin.taminhamrah.model.common.JobTitleDN
import com.tamin.taminhamrah.model.common.JobTitleListDN
import com.tamin.taminhamrah.model.common.MainServiceDN
import com.tamin.taminhamrah.model.common.MenuServiceStatusDN
import com.tamin.taminhamrah.model.common.RoleDN
import com.tamin.taminhamrah.model.common.UserType
import com.tamin.taminhamrah.model.common.UserTypeInfoDN
import com.tamin.taminhamrah.model.paging.PageDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.request.FilterOperator
import com.tamin.taminhamrah.model.request.FilterProperty
import com.tamin.taminhamrah.repository.TokenStoreManager
import com.tamin.taminhamrah.repository.common.CommonRepository
import io.ktor.client.statement.readRawBytes
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

class CommonRepositoryImpl(
    private val commonRemoteDataSource: CommonRemoteDataSource,
    private val menuDao: MenuDao,
    private val tokenStoreManager: TokenStoreManager,
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
                menuDao.replaceAllMenuItems(remoteMenu.map { it.toDomain().toEntity() })
            } catch (e: Exception) {
                throw e
            }
        }

    override fun getRegistrationDeclarationForm(): Flow<ByteArray> = flow {
        try {
            val statement = commonRemoteDataSource.getRegistrationDeclarationForm()
            val bytes = statement.execute { response -> response.readRawBytes() }
            emit(bytes)
        } catch (e: Exception) {
            throw e
        }
    }

    override fun getJobTitle(query: ApiQueryParamDN): Flow<JobTitleListDN?> = flow {
        try {
            val response = commonRemoteDataSource.getJobTitle(query)
            emit(
                response?.let {
                    JobTitleListDN(
                        list = it.list?.map { item -> item.toDomain() } ?: emptyList(),
                        total = it.total
                    )
                }
            )
        } catch (e: Exception) {
            throw e
        }
    }

    override fun getJobTitlePage(query: ApiQueryParamDN): Flow<PageDN<JobTitleDN>> = flow {
        val response = commonRemoteDataSource.getJobTitle(query)
        emit(
            PageDN(
                items = response?.list?.map { item -> item.toDomain() } ?: emptyList(),
                total = response?.total,
            )
        )
    }

    override fun getRoles(): Flow<List<RoleDN>> = flow {
        emit(
            listOf(
                RoleDN(1, "بیمه شده"),
                RoleDN(2, "مستمری بگیر"),
                RoleDN(3, "کارفرما")
            )
        )
    }

    override fun getInsuranceTypes(searchText: String?): Flow<List<InsuranceTypeDN>> = flow {
        val query = ApiQueryParamDN(
            page = 0,
            start = 0,
            limit = 100,
            filters = listOf(
                ApiFilterDN(
                    property = FilterProperty.INSURANCE_TYPE_DESC,
                    operator = FilterOperator.LIKE,
                    value = searchText?.takeIf { it.isNotBlank() } ?: INSURANCE_TYPE_WILDCARD_SEARCH,
                ),
            ),
        )
        val response = commonRemoteDataSource.getInsuranceTypes(query)
        emit(response?.list.orEmpty().map { it.toDomain() })
    }

    override fun checkUserType(): Flow<UserTypeInfoDN> = flow {
        val cached = UserType.fromNameOrNull(tokenStoreManager.getUserType())
        if (cached != null && cached != UserType.ANONYMOUS) {
            emit(UserTypeInfoDN(userType = cached))
        } else {
            val result = commonRemoteDataSource.checkInsuredInfo().toDomain()
            tokenStoreManager.saveUserType(result.userType.name)
            emit(result)
        }
    }

    private companion object {
        const val INSURANCE_TYPE_WILDCARD_SEARCH = "**"
    }
}
