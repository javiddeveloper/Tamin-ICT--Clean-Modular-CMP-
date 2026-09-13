package com.tamin.taminhamrah.feature.taminServices.constructionInsurance

import app.cash.turbine.test
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.contract.ConstructionInsuranceIntent
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.ui.ConstructionInsuranceViewModel
import com.tamin.taminhamrah.model.constructionInsurance.ConstructionFileDN
import com.tamin.taminhamrah.model.constructionInsurance.ConstructionFileSearchParamsDN
import com.tamin.taminhamrah.model.identity.IdentityInfoDN
import com.tamin.taminhamrah.repository.UserRepository
import com.tamin.taminhamrah.repository.constructionInsurance.ConstructionInsuranceRepository
import com.tamin.taminhamrah.useCases.constructionInsurance.GetConstructionFilesUseCase
import com.tamin.taminhamrah.useCases.user.GetIdentityInfoUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ConstructionInsuranceViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var fakeRepository: FakeConstructionInsuranceRepository
    private lateinit var fakeUserRepository: FakeUserRepository

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeConstructionInsuranceRepository()
        fakeUserRepository = FakeUserRepository()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun buildViewModel() = ConstructionInsuranceViewModel(
        getConstructionFilesUseCase = GetConstructionFilesUseCase(fakeRepository),
        getIdentityInfoUseCase = GetIdentityInfoUseCase(fakeUserRepository)
    )

    @Test
    fun loadData_populatesItemsAndUserInfo() = runTest {
        val viewModel = buildViewModel()

        viewModel.uiState.test {
            val state = expectMostRecentItem()
            assertFalse(state.isLoading)
            assertEquals(1, state.items.size)
            assertEquals(124037L, state.items.first().fileNumber)
            assertEquals("تست کاربر", state.userName)
            assertEquals("0060241721", state.nationalCode)
        }
    }

    @Test
    fun executeSearch_passesSearchParametersToUseCase() = runTest {
        val viewModel = buildViewModel()

        viewModel.sendIntent(ConstructionInsuranceIntent.OnFileNoQueryChanged("1234"))
        viewModel.sendIntent(ConstructionInsuranceIntent.OnReqNoQueryChanged("5678"))
        viewModel.sendIntent(ConstructionInsuranceIntent.ExecuteSearch)

        assertEquals("1234", fakeRepository.searchParam?.fileNo)
        assertEquals("5678", fakeRepository.searchParam?.reqNo)
    }

    @Test
    fun resetSearch_clearsQueries() = runTest {
        val viewModel = buildViewModel()

        viewModel.sendIntent(ConstructionInsuranceIntent.OnFileNoQueryChanged("1234"))
        viewModel.sendIntent(ConstructionInsuranceIntent.ResetSearch)

        assertEquals("", viewModel.uiState.value.fileNoQuery)
    }

    @Test
    fun toggleSearchExpanded_updatesState() = runTest {
        val viewModel = buildViewModel()

        viewModel.sendIntent(ConstructionInsuranceIntent.ToggleSearchExpanded(true))
        assertTrue(viewModel.uiState.value.isSearchExpanded)
    }
}

private class FakeConstructionInsuranceRepository : ConstructionInsuranceRepository {
    var files = listOf(
        ConstructionFileDN(
            fileNumber = 124037L,
            requestNumber = 881902L,
            totalPayment = 486000000L,
            debitStatusCode = "51"
        )
    )
    var searchParam: ConstructionFileSearchParamsDN? = null

    override fun getConstructionFiles(
        search: ConstructionFileSearchParamsDN?
    ): Flow<List<ConstructionFileDN>> = flow {
        searchParam = search
        emit(files)
    }
}

private class FakeUserRepository : UserRepository {
    override fun getIdentityInfo(): Flow<IdentityInfoDN> {
        return flowOf(
            IdentityInfoDN(
                cityOfBirthId = null,
                cityOfIssueId = null,
                countryId = null,
                dateOfBirth = null,
                fatherName = null,
                firstName = "تست",
                gender = null,
                id = null,
                idCardNumber = null,
                idCardSerial1 = null,
                idCardSerial2 = null,
                lastName = "کاربر",
                nationalId = "0060241721",
                ssn = null
            )
        )
    }

    override suspend fun getUserProfile(): com.tamin.taminhamrah.model.user.UserProfileDN {
        TODO("Not required")
    }

    override suspend fun getRecipients(): List<com.tamin.taminhamrah.model.user.RecipientDN> {
        TODO("Not required")
    }

    override suspend fun getInsuredActiveBranch(): com.tamin.taminhamrah.model.user.InsuredActiveBranchDN {
        TODO("Not required")
    }

    override suspend fun getRelationTaminAll(): List<com.tamin.taminhamrah.model.user.RelationTaminAllDN> {
        TODO("Not required")
    }

    override suspend fun getStatusCertificateReport(): com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN {
        TODO("Not required")
    }

    override suspend fun getWageCertificateReport(requestNumber: String): com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN {
        TODO("Not required")
    }

    override suspend fun changeMobile(mobile: String): String {
        TODO("Not required")
    }

    override suspend fun verifyChangeMobile(mobile: String, otp: String): String {
        TODO("Not required")
    }

    override suspend fun sendImageRequest(guid: String): String {
        TODO("Not required")
    }

    override suspend fun checkUserIsNew(): Boolean {
        TODO("Not required")
    }

    override suspend fun getSubdominant(): com.tamin.taminhamrah.model.user.SubdominantDN {
        TODO("Not required")
    }

    override suspend fun getTaminRelation(): com.tamin.taminhamrah.model.user.TaminRelationDN {
        TODO("Not required")
    }
}
