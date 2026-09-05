package com.tamin.taminhamrah.data.feature

import com.tamin.taminhamrah.model.common.FeatureFlag
import com.tamin.taminhamrah.model.common.FeatureStatus
import com.tamin.taminhamrah.model.common.JobTitleListDN
import com.tamin.taminhamrah.model.common.MainServiceDN
import com.tamin.taminhamrah.model.common.MenuServiceStatusDN
import com.tamin.taminhamrah.model.common.RoleDN
import com.tamin.taminhamrah.model.common.UserType
import com.tamin.taminhamrah.model.common.UserTypeInfoDN
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
}
