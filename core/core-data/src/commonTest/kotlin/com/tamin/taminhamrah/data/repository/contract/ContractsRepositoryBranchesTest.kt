package com.tamin.taminhamrah.data.repository.contract

import com.tamin.taminhamrah.data.local.dao.BranchDao
import com.tamin.taminhamrah.data.local.dao.ContractDao
import com.tamin.taminhamrah.data.local.dao.RegistrationInfoDao
import com.tamin.taminhamrah.data.local.entity.BranchEntity
import com.tamin.taminhamrah.data.local.entity.ContractEntity
import com.tamin.taminhamrah.data.local.entity.RegistrationInfoEntity
import com.tamin.taminhamrah.dataSource.contracts.ContractsRemoteDataSource
import com.tamin.taminhamrah.model.contracts.BranchDTO
import com.tamin.taminhamrah.model.contracts.ContractByGuardianRequestDTO
import com.tamin.taminhamrah.model.contracts.ContractDTO
import com.tamin.taminhamrah.model.contracts.FreeJobDTO
import com.tamin.taminhamrah.model.contracts.FreelanceCalculateSalaryParams
import com.tamin.taminhamrah.model.contracts.FreelanceContractResultDTO
import com.tamin.taminhamrah.model.contracts.FreelanceMakeContractRequestDTO
import com.tamin.taminhamrah.model.contracts.FreelancePremiumRangeDTO
import com.tamin.taminhamrah.model.contracts.FreelancePremiumRangeParams
import com.tamin.taminhamrah.model.contracts.InsurancePaymentDTO
import com.tamin.taminhamrah.model.contracts.InsurancePaymentParamsDN
import com.tamin.taminhamrah.model.contracts.OptionalContractByGuardianRequestDTO
import com.tamin.taminhamrah.model.contracts.PremiumRateDTO
import com.tamin.taminhamrah.model.contracts.RegistrationInfoDTO
import com.tamin.taminhamrah.model.contracts.SaveContactRequestDTO
import com.tamin.taminhamrah.model.contracts.UploadImageRequestDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.request.FilterOperator
import com.tamin.taminhamrah.model.utils.ListData
import com.tamin.taminhamrah.tools.apiQueryBuilder.ApiQueryBuilder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

/**
 * `getBranches` feeds a picker, so it has to **finish**. It used to end by `emitAll`-ing the DAO's
 * Flow, which never completes — callers clear their loading flag in a `finally` after collecting,
 * so the branch field sat on "در حال بارگذاری..." forever. `toList()` below would hang if that
 * ever came back.
 */
class ContractsRepositoryBranchesTest {

    private val tehranBranch = BranchDTO(
        code = "123",
        name = "شعبه ۱ تهران",
        minCode = null,
        maxCode = null,
        type = null,
        branchAddress = "خیابان ولیعصر",
        cityCode = "0701",
        status = null,
    )

    private fun repository(
        remote: FakeContractsRemoteDataSource,
        dao: FakeBranchDao,
    ) = ContractsRepositoryImpl(
        contractsRemoteDataSource = remote,
        contractDao = FakeContractDao(),
        registrationInfoDao = FakeRegistrationInfoDao(),
        branchDao = dao,
        apiQueryBuilder = FakeApiQueryBuilder(),
    )

    @Test
    fun `getBranches completes and emits what the server returned`() = runTest {
        val remote = FakeContractsRemoteDataSource(branches = listOf(tehranBranch))
        val dao = FakeBranchDao()

        // toList() only returns for a flow that completes.
        val emissions = repository(remote, dao).getBranches("0701").toList()

        assertEquals(listOf("123"), emissions.last().map { it.code })
        assertTrue(emissions.isNotEmpty())
    }

    @Test
    fun `getBranches filters by cityCode with the operator the old client sends`() = runTest {
        val remote = FakeContractsRemoteDataSource(branches = listOf(tehranBranch))

        repository(remote, FakeBranchDao()).getBranches("0701").toList()

        val filter = remote.lastQuery?.filters?.single()
        assertEquals("cityCode", filter?.property?.key)
        assertEquals(FilterOperator.EQUAL, filter?.operator)
        assertEquals("0701", filter?.value)
    }

    @Test
    fun `a failed lookup with nothing cached reports the failure`() = runTest {
        val remote = FakeContractsRemoteDataSource(error = IllegalStateException("boom"))

        assertFailsWith<IllegalStateException> {
            repository(remote, FakeBranchDao()).getBranches("0701").toList()
        }
    }

    @Test
    fun `a failed lookup still serves the cached branches`() = runTest {
        val dao = FakeBranchDao(
            initial = listOf(
                BranchEntity(
                    code = "999",
                    name = "شعبه ذخیره",
                    branchAddress = "-",
                    cityCode = "0701",
                    minCode = null,
                    maxCode = null,
                ),
            ),
        )
        val remote = FakeContractsRemoteDataSource(error = IllegalStateException("boom"))

        val emissions = repository(remote, dao).getBranches("0701").toList()

        assertEquals(listOf("999"), emissions.last().map { it.code })
    }
}

private class FakeBranchDao(initial: List<BranchEntity> = emptyList()) : BranchDao {
    private val rows = MutableStateFlow(initial)

    override fun getBranchesByCityCode(cityCode: String): Flow<List<BranchEntity>> =
        rows.map { all -> all.filter { it.cityCode == cityCode } }

    override suspend fun upsertBranches(branches: List<BranchEntity>) {
        rows.value = rows.value.filterNot { existing -> branches.any { it.code == existing.code } } +
            branches
    }

    override suspend fun clearBranchesByCityCode(cityCode: String) {
        rows.value = rows.value.filterNot { it.cityCode == cityCode }
    }
}

private class FakeContractsRemoteDataSource(
    private val branches: List<BranchDTO> = emptyList(),
    private val error: Exception? = null,
) : ContractsRemoteDataSource {
    var lastQuery: ApiQueryParamDN? = null

    override suspend fun getBranches(query: ApiQueryParamDN): ListData<BranchDTO> {
        lastQuery = query
        error?.let { throw it }
        return ListData(list = branches, total = branches.size)
    }

    override suspend fun getContracts(query: ApiQueryParamDN): ListData<ContractDTO> = unused()
    override suspend fun getRegistrationInfo(): RegistrationInfoDTO = unused()
    override suspend fun getSpcPremiumRates(): ListData<PremiumRateDTO> = unused()
    override suspend fun getFreelancePremiumRange(
        params: FreelancePremiumRangeParams,
    ): FreelancePremiumRangeDTO = unused()
    override suspend fun calculateFreelanceSalary(params: FreelanceCalculateSalaryParams): Long = unused()
    override suspend fun calculateOptionalSalary(premiumRateCode: String): Long = unused()
    override suspend fun getFreeJobWages(query: ApiQueryParamDN): ListData<FreeJobDTO> = unused()
    override suspend fun makeFreelanceContract(
        monthlyPremium: Long,
        request: FreelanceMakeContractRequestDTO,
    ): FreelanceContractResultDTO = unused()
    override suspend fun makeContract(
        selectedSalary: Long,
        request: FreelanceMakeContractRequestDTO,
    ): FreelanceContractResultDTO = unused()
    override suspend fun makeFreelanceContractByGuardian(
        selectedSalary: Long,
        request: ContractByGuardianRequestDTO,
    ): FreelanceContractResultDTO = unused()
    override suspend fun makeOptionalContractByGuardian(
        selectedSalary: Long,
        request: OptionalContractByGuardianRequestDTO,
    ): FreelanceContractResultDTO = unused()
    override suspend fun getInsurancePayment(params: InsurancePaymentParamsDN): InsurancePaymentDTO = unused()
    override suspend fun checkInsurancePaymentStatus(systemType: String): Any? = unused()
    override suspend fun uploadImage(request: UploadImageRequestDN): String? = unused()
    override suspend fun saveContact(request: SaveContactRequestDTO): Any? = unused()
}

private class FakeContractDao : ContractDao {
    override fun getContracts(): Flow<List<ContractEntity>> = unused()
    override suspend fun upsertContracts(contracts: List<ContractEntity>) = unused<Unit>()
    override suspend fun clearContracts() = unused<Unit>()
}

private class FakeRegistrationInfoDao : RegistrationInfoDao {
    override fun getRegistrationInfo(id: Int): Flow<RegistrationInfoEntity?> = unused()
    override suspend fun upsertRegistrationInfo(info: RegistrationInfoEntity) = unused<Unit>()
    override suspend fun clearRegistrationInfo() = unused<Unit>()
}

private class FakeApiQueryBuilder : ApiQueryBuilder {
    override fun defaultQuery(): ApiQueryParamDN = ApiQueryParamDN()
    override fun buildQuery(query: ApiQueryParamDN): Map<String, String> = emptyMap()
    override fun buildFilterJson(filters: List<ApiFilterDN>): String = "[]"
}

private fun <T> unused(): T = error("not part of the branches path under test")
