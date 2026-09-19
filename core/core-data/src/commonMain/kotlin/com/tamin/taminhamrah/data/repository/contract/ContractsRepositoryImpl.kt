package com.tamin.taminhamrah.data.repository.contract

import com.tamin.taminhamrah.data.local.dao.BranchDao
import com.tamin.taminhamrah.data.local.dao.ContractDao
import com.tamin.taminhamrah.data.local.dao.RegistrationInfoDao
import com.tamin.taminhamrah.data.mapper.toDomain
import com.tamin.taminhamrah.data.mapper.toDto
import com.tamin.taminhamrah.data.mapper.toEntity
import com.tamin.taminhamrah.data.mapper.toUpdateDto
import com.tamin.taminhamrah.dataSource.contracts.ContractsRemoteDataSource
import com.tamin.taminhamrah.model.contracts.BranchDN
import com.tamin.taminhamrah.model.contracts.ContractsPaging
import com.tamin.taminhamrah.model.contracts.FreelanceContractByGuardianParams
import com.tamin.taminhamrah.model.contracts.OptionalContractByGuardianParams
import com.tamin.taminhamrah.model.contracts.ContractDN
import com.tamin.taminhamrah.model.contracts.FreelanceCalculateSalaryParams
import com.tamin.taminhamrah.model.contracts.FreelanceContractResultDN
import com.tamin.taminhamrah.model.contracts.FreelanceMakeContractParams
import com.tamin.taminhamrah.model.contracts.FreelancePremiumRangeDN
import com.tamin.taminhamrah.model.contracts.FreelancePremiumRangeParams
import com.tamin.taminhamrah.model.contracts.FreeJobDN
import com.tamin.taminhamrah.model.contracts.FreeJobWagesPaging
import com.tamin.taminhamrah.model.contracts.InsurancePaymentDN
import com.tamin.taminhamrah.model.contracts.InsurancePaymentParamsDN
import com.tamin.taminhamrah.model.contracts.PremiumRateDN
import com.tamin.taminhamrah.model.contracts.RegistrationInfoDN
import com.tamin.taminhamrah.model.contracts.SaveContactRequestDN
import com.tamin.taminhamrah.model.contracts.UploadImageRequestDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.request.FilterOperator
import com.tamin.taminhamrah.model.request.FilterProperty
import com.tamin.taminhamrah.model.util.PagedListDN
import com.tamin.taminhamrah.repository.contracts.ContractsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapNotNull

class ContractsRepositoryImpl(
    private val contractsRemoteDataSource: ContractsRemoteDataSource,
    private val contractDao: ContractDao,
    private val registrationInfoDao: RegistrationInfoDao,
    private val branchDao: BranchDao,
) : ContractsRepository {

    override fun getContracts(page: Int): Flow<PagedListDN<ContractDN>> =
        getContractsPage(contractListQuery(premiumTypeCode = null, page = page))

    override fun getContractsByPremiumType(
        premiumTypeCode: String,
        page: Int,
    ): Flow<PagedListDN<ContractDN>> =
        getContractsPage(contractListQuery(premiumTypeCode = premiumTypeCode, page = page))

    override fun getStudentInsuranceContracts(page: Int): Flow<PagedListDN<ContractDN>> =
        getContractsByPremiumType(
            premiumTypeCode = com.tamin.taminhamrah.model.contracts.ContractPremiumTypeCode.STUDENT,
            page = page,
        )

    private fun getContractsPage(query: ApiQueryParamDN): Flow<PagedListDN<ContractDN>> = flow {
        val isFirstPage = query.start == 0
        if (isFirstPage) {
            val localItems = contractDao.getContracts().first()
            if (localItems.isNotEmpty()) {
                emit(
                    PagedListDN(
                        items = localItems.map { it.toDomain() },
                        total = localItems.size,
                    ),
                )
            }
        }

        try {
            val response = contractsRemoteDataSource.getContracts(query)
            val remoteItems = response.list.orEmpty()
            if (isFirstPage) {
                contractDao.replaceAll(remoteItems.map { it.toEntity() })
            }
            emit(
                PagedListDN(
                    items = remoteItems.map { it.toDomain() },
                    total = response.total,
                ),
            )
        } catch (e: Exception) {
            if (!isFirstPage) throw e
            val localItems = contractDao.getContracts().first()
            if (localItems.isEmpty()) throw e
            emit(
                PagedListDN(
                    items = localItems.map { it.toDomain() },
                    total = localItems.size,
                ),
            )
        }
    }

    override fun getBranches(cityCode: String, page: Int): Flow<PagedListDN<BranchDN>> = flow {
        val query = branchListQuery(cityCode, page)
        val isFirstPage = query.start == 0
        if (isFirstPage) {
            val localBranches = branchDao.getBranchesByCityCode(cityCode).first()
            if (localBranches.isNotEmpty()) {
                emit(
                    PagedListDN(
                        items = localBranches.map { it.toDomain() },
                        total = localBranches.size,
                    ),
                )
            }
        }

        try {
            val response = contractsRemoteDataSource.getBranches(query)
            val remoteBranches = (response.list ?: emptyList())
                // `code` is the primary key and is what the picker returns; a row without one
                // cannot be selected and would collide with every other blank-coded row.
                .filter { !it.code.isNullOrBlank() }
            if (isFirstPage) {
                branchDao.replaceAllForCity(cityCode, remoteBranches.map { it.toEntity(cityCode) })
            }
            val items = if (isFirstPage) {
                branchDao.getBranchesByCityCode(cityCode).first().map { it.toDomain() }
            } else {
                remoteBranches.map { it.toDomain() }
            }
            emit(PagedListDN(items = items, total = response.total))
        } catch (e: Exception) {
            if (!isFirstPage) throw e
            val localBranches = branchDao.getBranchesByCityCode(cityCode).first()
            if (localBranches.isEmpty()) throw e
            // The cached list was already emitted above when non-empty; if we skipped the
            // early emit (empty cache) we still need to surface the failure.
            emit(
                PagedListDN(
                    items = localBranches.map { it.toDomain() },
                    total = localBranches.size,
                ),
            )
        }
    }

    override fun getSpcPremiumRates(): Flow<List<PremiumRateDN>> = flow {
        val response = contractsRemoteDataSource.getSpcPremiumRates()
        emit((response.list ?: emptyList()).map { it.toDomain() })
    }

    override fun getFreeJobWages(page: Int, searchQuery: String?): Flow<PagedListDN<FreeJobDN>> = flow {
        val response = contractsRemoteDataSource.getFreeJobWages(freeJobWagesQuery(page, searchQuery))
        val items = response.list.orEmpty().map { it.toDomain() }
        emit(PagedListDN(items = items, total = response.total))
    }

    override fun getFreelancePremiumRange(params: FreelancePremiumRangeParams): Flow<FreelancePremiumRangeDN> = flow {
        emit(contractsRemoteDataSource.getFreelancePremiumRange(params).toDomain())
    }

    override fun getOptionalPremiumRange(): Flow<FreelancePremiumRangeDN> = flow {
        emit(contractsRemoteDataSource.getOptionalPremiumRange().toDomain())
    }

    override fun checkRedCrossStatus(): Flow<String> = flow {
        emit(contractsRemoteDataSource.checkRedCrossStatus())
    }

    override fun checkMedicalStudent(): Flow<String> = flow {
        emit(contractsRemoteDataSource.checkMedicalStudent())
    }

    override fun calculateFreelanceSalary(params: FreelanceCalculateSalaryParams): Flow<Long> = flow {
        emit(contractsRemoteDataSource.calculateFreelanceSalary(params))
    }

    override fun calculateOptionalSalary(premiumRateCode: String): Flow<Long> = flow {
        emit(contractsRemoteDataSource.calculateOptionalSalary(premiumRateCode))
    }

    override fun makeFreelanceContract(params: FreelanceMakeContractParams): Flow<FreelanceContractResultDN> = flow {
        emit(
            contractsRemoteDataSource.makeFreelanceContract(
                monthlyPremium = params.monthlyPremium,
                request = params.request.toDto(),
            ).toDomain(),
        )
    }

    override fun makeContract(params: FreelanceMakeContractParams): Flow<FreelanceContractResultDN> = flow {
        emit(
            contractsRemoteDataSource.makeContract(
                selectedSalary = params.monthlyPremium,
                request = params.request.toDto(),
            ).toDomain(),
        )
    }

    override fun makeFreelanceContractByGuardian(
        params: FreelanceContractByGuardianParams,
    ): Flow<FreelanceContractResultDN> = flow {
        emit(
            contractsRemoteDataSource.makeFreelanceContractByGuardian(
                selectedSalary = params.selectedSalary,
                request = params.toDto(),
            ).toDomain(),
        )
    }

    override fun makeOptionalContractByGuardian(
        params: OptionalContractByGuardianParams,
    ): Flow<FreelanceContractResultDN> = flow {
        emit(
            contractsRemoteDataSource.makeOptionalContractByGuardian(
                selectedSalary = params.selectedSalary,
                request = params.toDto(),
            ).toDomain(),
        )
    }

    override fun updateFreelanceContract(params: FreelanceMakeContractParams): Flow<Unit> = flow {
        contractsRemoteDataSource.updateFreelanceContract(
            premium = params.monthlyPremium,
            request = params.request.toDto(),
        )
        emit(Unit)
    }

    override fun updateOptionalContract(premium: Long): Flow<Unit> = flow {
        contractsRemoteDataSource.updateOptionalContract(premium = premium)
        emit(Unit)
    }

    override fun updateFreelanceContractByGuardian(
        params: FreelanceContractByGuardianParams,
    ): Flow<Unit> = flow {
        contractsRemoteDataSource.updateFreelanceContractByGuardian(
            premium = params.selectedSalary,
            request = params.toDto(),
        )
        emit(Unit)
    }

    override fun updateOptionalContractByGuardian(
        params: OptionalContractByGuardianParams,
    ): Flow<Unit> = flow {
        contractsRemoteDataSource.updateOptionalContractByGuardian(
            premium = params.selectedSalary,
            request = params.toUpdateDto(),
        )
        emit(Unit)
    }

    override fun getInsurancePayment(params: InsurancePaymentParamsDN): Flow<InsurancePaymentDN> = flow {
        emit(contractsRemoteDataSource.getInsurancePayment(params).toDomain())
    }

    override fun checkInsurancePaymentStatus(systemType: String) = flow {
        emit(contractsRemoteDataSource.checkInsurancePaymentStatus(systemType))
    }

    override fun uploadImage(request: UploadImageRequestDN): Flow<String> = flow {
        val imageId = contractsRemoteDataSource.uploadImage(request)
        imageId?.let { emit(it) }
    }

    override fun saveContact(request: SaveContactRequestDN): Flow<Any?> = flow {
        emit(contractsRemoteDataSource.saveContact(request.toDto()))
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
                .mapNotNull { it?.toDomain() },
        )
    }.distinctUntilChanged()

    private fun contractListQuery(premiumTypeCode: String?, page: Int): ApiQueryParamDN {
        val safePage = page.coerceAtLeast(1)
        val start = (safePage - 1) * ContractsPaging.PAGE_SIZE
        val filters = premiumTypeCode
            ?.takeIf { it.isNotBlank() }
            ?.let {
                listOf(
                    ApiFilterDN(
                        property = FilterProperty.PREMIUM_TYPE_CODE,
                        operator = FilterOperator.EQ,
                        value = it,
                    ),
                )
            }
            .orEmpty()
        return ApiQueryParamDN(
            page = safePage,
            start = start,
            limit = ContractsPaging.PAGE_SIZE,
            filters = filters,
        )
    }

    private fun branchListQuery(cityCode: String, page: Int): ApiQueryParamDN {
        val safePage = page.coerceAtLeast(1)
        val start = (safePage - 1) * ContractsPaging.PAGE_SIZE
        return ApiQueryParamDN(
            page = safePage,
            start = start,
            limit = ContractsPaging.PAGE_SIZE,
            filters = listOf(
                ApiFilterDN(
                    property = FilterProperty.CITY_CODE,
                    // EQUAL, not EQ: this is the operator `old_android` sends to
                    // special-insured-services/branches, and the old client owns the wire contract.
                    operator = FilterOperator.EQUAL,
                    value = cityCode,
                ),
            ),
        )
    }

    private fun freeJobWagesQuery(page: Int, searchQuery: String?): ApiQueryParamDN {
        val safePage = page.coerceAtLeast(1)
        val start = (safePage - 1) * FreeJobWagesPaging.PAGE_SIZE
        val filters = searchQuery
            ?.takeIf { it.isNotBlank() }
            ?.let {
                listOf(
                    ApiFilterDN(
                        property = FilterProperty.DISCRIOPTION,
                        operator = FilterOperator.LIKE,
                        value = "*$it*",
                    ),
                )
            }
            .orEmpty()
        return ApiQueryParamDN(
            page = safePage,
            start = start,
            limit = FreeJobWagesPaging.PAGE_SIZE,
            filters = filters,
        )
    }
}
