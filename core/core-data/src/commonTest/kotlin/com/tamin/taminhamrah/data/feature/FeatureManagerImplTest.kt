package com.tamin.taminhamrah.data.feature

import com.tamin.taminhamrah.model.common.FeatureFlag
import com.tamin.taminhamrah.model.common.FeatureStatus
import com.tamin.taminhamrah.model.common.JobTitleDN
import com.tamin.taminhamrah.model.common.JobTitleListDN
import com.tamin.taminhamrah.model.common.MainServiceDN
import com.tamin.taminhamrah.model.common.MenuServiceStatusDN
import com.tamin.taminhamrah.model.common.RoleDN
import com.tamin.taminhamrah.model.common.UserType
import com.tamin.taminhamrah.model.common.UserTypeInfoDN
import com.tamin.taminhamrah.model.paging.PageDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.repository.common.CommonRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

// A simple fake repository just for this test
class FakeRepositoryForFeatureManager : CommonRepository {
    var mainMenuResult: List<MainServiceDN> = emptyList()

    override fun getMainMenu(versionCode: String, forceUpdate: Boolean): Flow<List<MainServiceDN>> = flow {
        emit(mainMenuResult)
    }

    override fun getRegistrationDeclarationForm(): Flow<ByteArray> = flow {
        emit(byteArrayOf())
    }

    override fun getJobTitle(query: ApiQueryParamDN): Flow<JobTitleListDN?> = flow {
        emit(null)
    }

    override fun getJobTitlePage(query: ApiQueryParamDN): Flow<PageDN<JobTitleDN>> = flow {
        emit(PageDN(items = emptyList(), total = 0))
    }

    override fun getRoles(): Flow<List<RoleDN>> = flow {
        emit(emptyList())
    }

    override fun getBeneficiary(filters: List<com.tamin.taminhamrah.model.request.ApiFilterDN>): Flow<List<com.tamin.taminhamrah.model.common.BeneficiaryDN>> = flow {}

    override fun getInsuranceTypes(searchText: String?): Flow<List<com.tamin.taminhamrah.model.common.InsuranceTypeDN>> = flow {
        emit(emptyList())
    }

    override fun checkUserType(): Flow<UserTypeInfoDN> = flow {
        emit(UserTypeInfoDN(userType = UserType.INSURED))
    }
}

class FeatureManagerImplTest {

    private lateinit var fakeRepository: FakeRepositoryForFeatureManager
    private lateinit var featureManager: FeatureManagerImpl

    @BeforeTest
    fun setup() {
        fakeRepository = FakeRepositoryForFeatureManager()
        featureManager = FeatureManagerImpl(fakeRepository)
    }


    @Test
    fun `test getFeatureStatus returns Enabled when ACTIVE`() = runTest {
        // Arrange
        val service = MainServiceDN(id = FeatureFlag.CONTRACTS.id, name = "Contracts", active = true, status = MenuServiceStatusDN.ACTIVE)
        fakeRepository.mainMenuResult = listOf(service)

        // Act
        val status = featureManager.getFeatureStatus(FeatureFlag.CONTRACTS).first()

        // Assert
        assertTrue(status is FeatureStatus.Enabled)
    }

    @Test
    fun `test getFeatureStatus returns Disabled when active is false`() = runTest {
        // Arrange
        val service = MainServiceDN(
            id = FeatureFlag.STUDENT_INSURANCE.id,
            name = "Student",
            active = false,
            status = MenuServiceStatusDN.ACTIVE, // Server says active, but active boolean is false
            message = "Not eligible"
        )
        fakeRepository.mainMenuResult = listOf(service)

        // Act
        val status = featureManager.getFeatureStatus(FeatureFlag.STUDENT_INSURANCE).first()

        // Assert
        assertTrue(status is FeatureStatus.Disabled)
        assertEquals("Not eligible", (status as FeatureStatus.Disabled).message)
    }

    @Test
    fun `test getFeatureStatus returns TemporaryDisabled when TEMPORARY_DISABLED`() = runTest {
        // Arrange
        val service = MainServiceDN(
            id = FeatureFlag.PENSION_INQUIRY.id,
            name = "Pension",
            active = true,
            status = MenuServiceStatusDN.TEMPORARY_DISABLED,
            message = "Updating..."
        )
        fakeRepository.mainMenuResult = listOf(service)

        // Act
        val status = featureManager.getFeatureStatus(FeatureFlag.PENSION_INQUIRY).first()

        // Assert
        assertTrue(status is FeatureStatus.TemporaryDisabled)
        assertEquals("Updating...", (status as FeatureStatus.TemporaryDisabled).message)
    }

    @Test
    fun `test isFeatureEnabled returns true only for Enabled status`() = runTest {
        // Arrange
        val activeService = MainServiceDN(id = FeatureFlag.CONTRACTS.id, active = true, status = MenuServiceStatusDN.ACTIVE)
        val disabledService = MainServiceDN(id = FeatureFlag.STUDENT_INSURANCE.id, active = true, status = MenuServiceStatusDN.DISABLED)

        fakeRepository.mainMenuResult = listOf(activeService, disabledService)

        // Act
        val isContractsEnabled = featureManager.isFeatureEnabled(FeatureFlag.CONTRACTS)
        val isStudentEnabled = featureManager.isFeatureEnabled(FeatureFlag.STUDENT_INSURANCE)

        // Assert
        assertTrue(isContractsEnabled)
        assertTrue(!isStudentEnabled) // Should be false
    }

    @Test
    fun `test observeFeatureStatuses resolves every flag from one menu read`() = runTest {
        val activeService = MainServiceDN(id = FeatureFlag.CONTRACTS.id, active = true, status = MenuServiceStatusDN.ACTIVE)
        val disabledService = MainServiceDN(
            id = FeatureFlag.STUDENT_INSURANCE.id,
            active = true,
            status = MenuServiceStatusDN.DISABLED,
            message = "Not eligible",
        )
        fakeRepository.mainMenuResult = listOf(activeService, disabledService)

        val statuses = featureManager.observeFeatureStatuses(
            setOf(FeatureFlag.CONTRACTS, FeatureFlag.STUDENT_INSURANCE, FeatureFlag.BANK_ACCOUNT_LIST)
        ).first()

        assertTrue(statuses.getValue(FeatureFlag.CONTRACTS) is FeatureStatus.Enabled)
        assertEquals("Not eligible", (statuses.getValue(FeatureFlag.STUDENT_INSURANCE) as FeatureStatus.Disabled).message)
        // Absent from the menu entirely: Disabled with no message, not a crash.
        assertTrue(statuses.getValue(FeatureFlag.BANK_ACCOUNT_LIST) is FeatureStatus.Disabled)
    }

    @Test
    fun `test observeFeatureStatuses falls back to Enabled for every flag when the menu fails`() = runTest {
        val failingRepository = object : CommonRepository by fakeRepository {
            override fun getMainMenu(versionCode: String, forceUpdate: Boolean): Flow<List<MainServiceDN>> = flow {
                throw RuntimeException("network down")
            }
        }
        val manager = FeatureManagerImpl(failingRepository)

        val statuses = manager.observeFeatureStatuses(setOf(FeatureFlag.CONTRACTS, FeatureFlag.STUDENT_INSURANCE)).first()

        assertTrue(statuses.getValue(FeatureFlag.CONTRACTS) is FeatureStatus.Enabled)
        assertTrue(statuses.getValue(FeatureFlag.STUDENT_INSURANCE) is FeatureStatus.Enabled)
    }

    @Test
    fun `test observeFeatureStatuses returns an empty map for an empty flag set without reading the menu`() = runTest {
        val statuses = featureManager.observeFeatureStatuses(emptySet()).first()
        assertTrue(statuses.isEmpty())
    }
}
