package com.tamin.taminhamrah.feature.taminServices.constructionInsurance

import app.cash.turbine.test
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.contract.ConstructionInsuranceIntent
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.ui.ConstructionInsuranceViewModel
import com.tamin.taminhamrah.model.activeRelation.ActiveRelationDN
import com.tamin.taminhamrah.model.bankAccount.BankAccountDN
import com.tamin.taminhamrah.model.certificate.RecipientDN
import com.tamin.taminhamrah.model.constructionInsurance.BeneficiaryConstructionDN
import com.tamin.taminhamrah.model.constructionInsurance.ConstructionFileDN
import com.tamin.taminhamrah.model.constructionInsurance.ConstructionFileSearchParamsDN
import com.tamin.taminhamrah.model.constructionInsurance.InstallmentLetterDN
import com.tamin.taminhamrah.model.constructionInsurance.PaymentSheetConstructionFileDN
import com.tamin.taminhamrah.model.erecords.images.ElectronicFileDN
import com.tamin.taminhamrah.model.identity.IdentityInfoDN
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.subdominant.SubdominantDN
import com.tamin.taminhamrah.model.subdominant.insuredActiveBranch.InsuredActiveBranchDN
import com.tamin.taminhamrah.model.user.CurrentUserDN
import com.tamin.taminhamrah.model.user.EditMobileResponseDN
import com.tamin.taminhamrah.model.user.TaminRelationDN
import com.tamin.taminhamrah.model.user.UserProfileDN
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
            requestDate = null,
            workshopInfo = null,
            postalCode = null,
            address = null,
            mainPlaque = null,
            subPlaque = null,
            block = null,
            propertyConstruction = null,
            apartment = null,
            trade = null,
            partPlaque = null,
            sumOfComplications = null,
            debitNumber = null,
            totalPayment = 486000000L,
            meterage = null,
            debitStatusCode = "51",
            protrusion = null,
            applicationFees = null,
            residentialServiceInfrastructureFees = null,
            excessDensitySurchargeFees = null,
            increasePropertyValue = null,
            issuanceFencingWallConstructionFees = null,
            coveredClause3Fees = null,
            article100 = null,
            paymentDeadLine = null,
        )
    )
    var searchParam: ConstructionFileSearchParamsDN? = null

    override fun getConstructionFiles(
        search: ConstructionFileSearchParamsDN?
    ): Flow<List<ConstructionFileDN>> = flow {
        searchParam = search
        emit(files)
    }

    override fun getBeneficiariesWorkshop(
        requestNumber: Long?,
        fileNumber: Long?,
        requestDate: String?,
    ): Flow<List<BeneficiaryConstructionDN>> = flow {
        emit(emptyList())
    }

    override fun getPaymentSheetConstructionInfo(debitNumber: String): Flow<List<PaymentSheetConstructionFileDN>> =
        flow {
            emit(emptyList())
        }

    override fun getCertificatePaymentSheetPdf(
        debitNumber: String,
        branchCode: String
    ): Flow<PdfDownloadDN> = flow {
        emit(PdfDownloadDN(pdf = null))
    }

    override fun issuancePaymentSheet(debitNumber: String): Flow<String> = flow {
        emit("OK")
    }

    override fun getInstallmentLetterList(
        workshopId: String,
        branchId: String
    ): Flow<List<InstallmentLetterDN>> = flow {
        emit(emptyList())
    }
}

private class FakeUserRepository : UserRepository {

    private fun notUsed(): Nothing = error("not used")

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

    override suspend fun getUserProfileImage(): Flow<String> {
        notUsed()
    }

    override suspend fun fetchTaminRelation(): Flow<TaminRelationDN> {
        notUsed()
    }

    override suspend fun sendImageRequest(branchCode: String, serialId: String): Flow<String> {
        notUsed()
    }

    override suspend fun changeMobile(mobileNumber: String): Flow<EditMobileResponseDN> {
        notUsed()
    }

    override suspend fun verifyChangeMobileCode(
        mobile: String,
        otp: String,
        otpHashCode: String
    ): Flow<String> {
        notUsed()
    }

    override suspend fun getSubDominantsInfo(filters: List<ApiFilterDN>): Flow<SubdominantDN> {
        notUsed()
    }

    override suspend fun getBankAccountList(filters: List<ApiFilterDN>): Flow<List<BankAccountDN>> {
        notUsed()
    }

    override suspend fun getInsuredActiveBranch(): Flow<List<InsuredActiveBranchDN>> {
        notUsed()
    }

    override suspend fun getRelationTaminAll(filters: List<ApiFilterDN>): Flow<List<ActiveRelationDN>> {
        notUsed()
    }

    override fun getElectronicFile(filters: List<ApiFilterDN>): Flow<List<ElectronicFileDN>> {
        notUsed()
    }

    override suspend fun downloadDocument(url: String): PdfDownloadDN {
        notUsed()
    }

    override suspend fun getUserProfile(): Flow<UserProfileDN> {
        notUsed()
    }

    override suspend fun getCurrentUser(): Flow<CurrentUserDN> {
        notUsed()
    }

    override fun checkUserIsNew(nationalId: String): Flow<Boolean> {
        notUsed()
    }

    override suspend fun registerBankAccount(
        accountNumber: String,
        bankCode: String,
        accountTypeCode: String,
        startDateMillis: Long,
    ): Flow<String?> {
        notUsed()
    }

    override suspend fun getStatusCertificateReport(filters: List<ApiFilterDN>): Flow<String> {
        notUsed()
    }

    override suspend fun getWageCertificateReport(filters: List<ApiFilterDN>): Flow<String> {
        notUsed()
    }

    override suspend fun getRecipients(filters: List<ApiFilterDN>): Flow<List<RecipientDN>> {
        notUsed()
    }
}
