package com.tamin.taminhamrah.data.repository.contract

import com.tamin.taminhamrah.data.local.dao.BranchDao
import com.tamin.taminhamrah.data.local.dao.ContractDao
import com.tamin.taminhamrah.data.local.dao.RegistrationInfoDao
import com.tamin.taminhamrah.data.mapper.toDomain
import com.tamin.taminhamrah.data.mapper.toDto
import com.tamin.taminhamrah.data.mapper.toEntity
import com.tamin.taminhamrah.data.repository.contract.BranchListQuery
import com.tamin.taminhamrah.dataSource.contracts.ContractsRemoteDataSource
import com.tamin.taminhamrah.model.contracts.BranchDN
import com.tamin.taminhamrah.model.contracts.ContractDN
import com.tamin.taminhamrah.model.contracts.FreelanceCalculateSalaryParams
import com.tamin.taminhamrah.model.contracts.FreelanceContractResultDN
import com.tamin.taminhamrah.model.contracts.FreelanceMakeContractParams
import com.tamin.taminhamrah.model.contracts.FreelancePremiumRangeDN
import com.tamin.taminhamrah.model.contracts.FreelancePremiumRangeParams
import com.tamin.taminhamrah.model.contracts.PremiumRateDN
import com.tamin.taminhamrah.model.contracts.RegistrationInfoDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.request.FilterOperator
import com.tamin.taminhamrah.model.request.FilterProperty
import com.tamin.taminhamrah.repository.contracts.ContractsRepository
import com.tamin.taminhamrah.tools.apiQueryBuilder.ApiQueryBuilder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull

class ContractsRepositoryImpl(
    private val contractsRemoteDataSource: ContractsRemoteDataSource,
    private val contractDao: ContractDao,
    private val registrationInfoDao: RegistrationInfoDao,
    private val branchDao: BranchDao,
    private val apiQueryBuilder: ApiQueryBuilder,
) :
    ContractsRepository {
    override fun getContracts(query: ApiQueryParamDN?): Flow<List<ContractDN>> =
        flow {
            val effectiveQuery = query ?: apiQueryBuilder.defaultQuery()
            val localItems = contractDao.getContracts().first()
            emit(localItems.map { it.toDomain() })

            try {
                val response = contractsRemoteDataSource.getContracts(effectiveQuery)
                val remoteItems = response.list.orEmpty()
                contractDao.replaceAll(remoteItems.map { it.toEntity() })
            } catch (e: Exception) {
                if (localItems.isEmpty()) {
                    throw e
                }
            }

            emitAll(
                contractDao.getContracts().map { entities ->
                    entities.map { it.toDomain() }
                }
            )
        }.distinctUntilChanged()

    override fun getStudentInsuranceContracts(): Flow<List<ContractDN>> =
        getContracts(buildStudentInsuranceContractsQuery())

    override fun getBranches(cityCode: String): Flow<List<BranchDN>> = flow {
        val localBranches = branchDao.getBranchesByCityCode(cityCode).first()
        emit(localBranches.map { it.toDomain() })

        try {
            val response = contractsRemoteDataSource.getBranches(BranchListQuery.build(cityCode))
            val remoteBranches = response.list.orEmpty()
            branchDao.replaceAllForCity(cityCode, remoteBranches.map { it.toEntity() })
        } catch (e: Exception) {
            if (localBranches.isEmpty()) {
                throw e
            }
        }

        emitAll(
            branchDao.getBranchesByCityCode(cityCode).map { entities ->
                entities.map { it.toDomain() }
            },
        )
    }.distinctUntilChanged()

    override fun getSpcPremiumRates(): Flow<List<PremiumRateDN>> = flow {
        val response = contractsRemoteDataSource.getSpcPremiumRates()
        emit(response.list.orEmpty().map { it.toDomain() })
    }

    override fun getFreelancePremiumRange(params: FreelancePremiumRangeParams): Flow<FreelancePremiumRangeDN> = flow {
        emit(contractsRemoteDataSource.getFreelancePremiumRange(params).toDomain())
    }

    override fun calculateFreelanceSalary(params: FreelanceCalculateSalaryParams): Flow<Long> = flow {
        emit(contractsRemoteDataSource.calculateFreelanceSalary(params))
    }

    override fun makeFreelanceContract(params: FreelanceMakeContractParams): Flow<FreelanceContractResultDN> = flow {
        emit(
            contractsRemoteDataSource.makeFreelanceContract(
                monthlyPremium = params.monthlyPremium,
                request = params.request.toDto(),
            ).toDomain(),
        )
    }

    override fun getRegistrationInfo(): Flow<RegistrationInfoDN> = flow {
        val localInfo = registrationInfoDao.getRegistrationInfo().first()
        localInfo?.toDomain()?.let { emit(it) }

        try {
            val response = contractsRemoteDataSource.getRegistrationInfo()
            registrationInfoDao.upsertRegistrationInfo(response.toDomain().toEntity())
        } catch (e: Exception) {
            if (localInfo == null) {
                throw e
            }
        }
        emitAll(
            registrationInfoDao.getRegistrationInfo()
                .mapNotNull { it?.toDomain() }
        )
    }.distinctUntilChanged()

    private fun buildStudentInsuranceContractsQuery(): ApiQueryParamDN = ApiQueryParamDN(
        page = 1,
        start = 0,
        limit = 100,
        filters = listOf(
            ApiFilterDN(
                property = FilterProperty.PREMIUM_TYPE_CODE,
                operator = FilterOperator.EQ,
                value = FREELANCE_PREMIUM_TYPE_CODE,
            ),
        ),
    )

    private companion object {
        const val FREELANCE_PREMIUM_TYPE_CODE = "01"
    }
}
