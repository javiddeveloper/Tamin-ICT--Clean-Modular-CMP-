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
import com.tamin.taminhamrah.model.constructionInsurance.InstallmentConstructionListDN
import com.tamin.taminhamrah.model.constructionInsurance.InstallmentDebitListDN
import com.tamin.taminhamrah.model.constructionInsurance.InstallmentLetterDN
import com.tamin.taminhamrah.model.constructionInsurance.PaymentSheetConstructionFileDN
import com.tamin.taminhamrah.model.erecords.images.ElectronicFileDN
import com.tamin.taminhamrah.model.identity.IdentityInfoDN
import com.tamin.taminhamrah.model.paging.PageDN
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.request.FilterProperty
import com.tamin.taminhamrah.model.subdominant.SubdominantDN
import com.tamin.taminhamrah.model.subdominant.insuredActiveBranch.InsuredActiveBranchDN
import com.tamin.taminhamrah.model.user.CurrentUserDN
import com.tamin.taminhamrah.model.user.EditMobileResponseDN
import com.tamin.taminhamrah.model.user.TaminRelationDN
import com.tamin.taminhamrah.model.user.UserProfileDN
import com.tamin.taminhamrah.repository.UserRepository
import com.tamin.taminhamrah.repository.constructionInsurance.ConstructionInsuranceRepository
import com.tamin.taminhamrah.useCases.constructionInsurance.GetConstructionFilesPageUseCase
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
        getConstructionFilesPageUseCase = GetConstructionFilesPageUseCase(fakeRepository),
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
    fun executeSearch_forwardsSearchFieldsAsEqFiltersToThePageQuery() = runTest {
        val viewModel = buildViewModel()

        viewModel.sendIntent(ConstructionInsuranceIntent.OnFileNoQueryChanged("1234"))
        viewModel.sendIntent(ConstructionInsuranceIntent.OnReqNoQueryChanged("5678"))
        viewModel.sendIntent(ConstructionInsuranceIntent.ExecuteSearch)

        val filters = fakeRepository.lastPageQuery?.filters.orEmpty()
        assertEquals(FilterProperty.FILE_NO, filters.getOrNull(0)?.property)
        assertEquals("1234", filters.getOrNull(0)?.value)
        assertEquals(FilterProperty.REQ_NO, filters.getOrNull(1)?.property)
        assertEquals("5678", filters.getOrNull(1)?.value)
        assertEquals("1234", viewModel.uiState.value.appliedFileNoQuery)
        assertEquals("5678", viewModel.uiState.value.appliedReqNoQuery)
    }

    @Test
    fun resetSearch_clearsQueriesAndAppliedFiltersAndRefreshesUnfiltered() = runTest {
        val viewModel = buildViewModel()

        viewModel.sendIntent(ConstructionInsuranceIntent.OnFileNoQueryChanged("1234"))
        viewModel.sendIntent(ConstructionInsuranceIntent.ExecuteSearch)
        viewModel.sendIntent(ConstructionInsuranceIntent.ResetSearch)

        assertEquals("", viewModel.uiState.value.fileNoQuery)
        assertEquals("", viewModel.uiState.value.appliedFileNoQuery)
        assertTrue(fakeRepository.lastPageQuery?.filters.orEmpty().isEmpty())
    }

    @Test
    fun toggleSearchExpanded_updatesState() = runTest {
        val viewModel = buildViewModel()

        viewModel.sendIntent(ConstructionInsuranceIntent.ToggleSearchExpanded(true))
        assertTrue(viewModel.uiState.value.isSearchExpanded)
    }

    @Test
    fun loadNextPage_appendsTheSecondPageAndDetectsEndOfList() = runTest {
        fakeRepository.allFiles = (1..15L).map { createFile(fileNumber = it) }
        val viewModel = buildViewModel()

        assertEquals(10, viewModel.uiState.value.items.size)
        assertFalse(viewModel.uiState.value.endReached)

        viewModel.sendIntent(ConstructionInsuranceIntent.LoadNextPage)

        assertEquals(15, viewModel.uiState.value.items.size)
        assertTrue(viewModel.uiState.value.endReached)
    }

    @Test
    fun retryNextPage_recoversAfterAFailedPage() = runTest {
        fakeRepository.allFiles = (1..15L).map { createFile(fileNumber = it) }
        val viewModel = buildViewModel()
        fakeRepository.shouldThrowOnPage = true

        viewModel.sendIntent(ConstructionInsuranceIntent.LoadNextPage)
        assertEquals(10, viewModel.uiState.value.items.size)

        fakeRepository.shouldThrowOnPage = false
        viewModel.sendIntent(ConstructionInsuranceIntent.RetryNextPage)

        assertEquals(15, viewModel.uiState.value.items.size)
    }

    private fun createFile(fileNumber: Long) = ConstructionFileDN(
        fileNumber = fileNumber,
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
}

private class FakeConstructionInsuranceRepository : ConstructionInsuranceRepository {
    var allFiles = listOf(
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
    var lastPageQuery: ApiQueryParamDN? = null
    var shouldThrowOnPage = false

    override fun getConstructionFiles(
        search: ConstructionFileSearchParamsDN?
    ): Flow<List<ConstructionFileDN>> = flow {
        emit(allFiles)
    }

    override fun getConstructionFilesPage(query: ApiQueryParamDN): Flow<PageDN<ConstructionFileDN>> = flow {
        lastPageQuery = query
        if (shouldThrowOnPage) error("network")
        val start = query.start
        val end = (start + query.limit).coerceAtMost(allFiles.size)
        val slice = if (start >= allFiles.size) emptyList() else allFiles.subList(start, end)
        emit(PageDN(items = slice, total = allFiles.size))
    }

    override fun getBeneficiariesWorkshopPage(query: ApiQueryParamDN): Flow<PageDN<BeneficiaryConstructionDN>> = flow {
        emit(PageDN(items = emptyList(), total = 0))
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

    override fun getInstallmentLetterListPage(
        workshopId: String,
        branchId: String,
        query: ApiQueryParamDN,
    ): Flow<PageDN<InstallmentLetterDN>> = flow {
        emit(PageDN(items = emptyList(), total = 0))
    }

    override fun getDetailDebitListPage(
        debitNumber: String,
        branchId: String,
        query: ApiQueryParamDN,
    ): Flow<PageDN<InstallmentDebitListDN>> = flow {
        emit(PageDN(items = emptyList(), total = 0))
    }

    override fun getInstallmentConstructionListPage(
        debitNumber: String,
        branchId: String,
        query: ApiQueryParamDN,
    ): Flow<PageDN<InstallmentConstructionListDN>> = flow {
        emit(PageDN(items = emptyList(), total = 0))
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
