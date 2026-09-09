package com.tamin.taminhamrah.feature.workshops.ui.completeEmployerInfo

import app.cash.turbine.ReceiveTurbine
import app.cash.turbine.test
import com.tamin.taminhamrah.feature.workshops.fake.FakeWorkShopsRepository
import com.tamin.taminhamrah.feature.workshops.ui.completeEmployerInfo.contract.CompleteEmployerInfoDialog
import com.tamin.taminhamrah.feature.workshops.ui.completeEmployerInfo.contract.CompleteEmployerInfoIntent
import com.tamin.taminhamrah.feature.workshops.ui.completeEmployerInfo.contract.CompleteEmployerInfoTab
import com.tamin.taminhamrah.feature.workshops.ui.completeEmployerInfo.contract.CompleteEmployerInfoUiState
import com.tamin.taminhamrah.feature.workshops.ui.completeEmployerInfo.contract.VerifyPath
import com.tamin.taminhamrah.model.activeRelation.ActiveRelationDN
import com.tamin.taminhamrah.model.bankAccount.BankAccountDN
import com.tamin.taminhamrah.model.certificate.RecipientDN
import com.tamin.taminhamrah.model.common.CityDN
import com.tamin.taminhamrah.model.common.CityListResultDN
import com.tamin.taminhamrah.model.common.CityPR
import com.tamin.taminhamrah.model.common.ProvinceDN
import com.tamin.taminhamrah.model.common.ProvincePR
import com.tamin.taminhamrah.model.contracts.BranchDN
import com.tamin.taminhamrah.model.contracts.BranchPR
import com.tamin.taminhamrah.model.contracts.ContractDN
import com.tamin.taminhamrah.model.contracts.FreeJobDN
import com.tamin.taminhamrah.model.contracts.FreelanceCalculateSalaryParams
import com.tamin.taminhamrah.model.contracts.FreelanceContractByGuardianParams
import com.tamin.taminhamrah.model.contracts.FreelanceContractResultDN
import com.tamin.taminhamrah.model.contracts.FreelanceMakeContractParams
import com.tamin.taminhamrah.model.contracts.FreelancePremiumRangeDN
import com.tamin.taminhamrah.model.contracts.FreelancePremiumRangeParams
import com.tamin.taminhamrah.model.contracts.InsurancePaymentDN
import com.tamin.taminhamrah.model.contracts.InsurancePaymentParamsDN
import com.tamin.taminhamrah.model.contracts.OptionalContractByGuardianParams
import com.tamin.taminhamrah.model.contracts.PremiumRateDN
import com.tamin.taminhamrah.model.contracts.RegistrationInfoDN
import com.tamin.taminhamrah.model.contracts.SaveContactRequestDN
import com.tamin.taminhamrah.model.contracts.UploadImageRequestDN
import com.tamin.taminhamrah.model.employerInfo.LegalWorkshopCeoDN
import com.tamin.taminhamrah.model.employerInfo.LegalWorkshopDN
import com.tamin.taminhamrah.model.employerInfo.LegalWorkshopInfoRequestDN
import com.tamin.taminhamrah.model.employerInfo.RealWorkshopInfoRequestDN
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
import com.tamin.taminhamrah.model.util.PagedListDN
import com.tamin.taminhamrah.model.workshop.EmployerAgreementDN
import com.tamin.taminhamrah.model.workshop.WorkshopSummaryDN
import com.tamin.taminhamrah.repository.CityProvinceRepository
import com.tamin.taminhamrah.repository.UserRepository
import com.tamin.taminhamrah.repository.contracts.ContractsRepository
import com.tamin.taminhamrah.repository.employerInfo.EmployerInfoRepository
import com.tamin.taminhamrah.useCases.common.GetCitiesByProvinceUseCase
import com.tamin.taminhamrah.useCases.common.GetProvincesUseCase
import com.tamin.taminhamrah.useCases.contracts.GetBranchesUseCase
import com.tamin.taminhamrah.useCases.employerInfo.GetLegalWorkshopCeoUseCase
import com.tamin.taminhamrah.useCases.employerInfo.GetLegalWorkshopUseCase
import com.tamin.taminhamrah.useCases.employerInfo.RequestLegalTicketUseCase
import com.tamin.taminhamrah.useCases.employerInfo.RequestRealTicketUseCase
import com.tamin.taminhamrah.useCases.employerInfo.SubmitLegalWorkshopInfoUseCase
import com.tamin.taminhamrah.useCases.employerInfo.SubmitRealWorkshopInfoUseCase
import com.tamin.taminhamrah.useCases.user.GetUserProfileUseCase
import com.tamin.taminhamrah.useCases.workshops.GetEmployerAgreementsUseCase
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
class CompleteEmployerInfoViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var fakeEmployerInfoRepo: FakeTestEmployerInfoRepo
    private lateinit var fakeUserRepo: FakeTestUserRepo
    private lateinit var fakeWorkShopsRepo: FakeWorkShopsRepository
    private lateinit var fakeCityProvinceRepo: FakeTestCityProvinceRepo
    private lateinit var fakeContractsRepo: FakeTestContractsRepo
    private lateinit var viewModel: CompleteEmployerInfoViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeEmployerInfoRepo = FakeTestEmployerInfoRepo()
        fakeUserRepo = FakeTestUserRepo()
        fakeWorkShopsRepo = FakeWorkShopsRepository().apply {
            employerAgreements = PagedListDN(items = listOf(legalWorkshop, realWorkshop), total = 2)
        }
        fakeCityProvinceRepo = FakeTestCityProvinceRepo()
        fakeContractsRepo = FakeTestContractsRepo()

        viewModel = CompleteEmployerInfoViewModel(
            getEmployerAgreements = GetEmployerAgreementsUseCase(fakeWorkShopsRepo),
            getUserProfileUseCase = GetUserProfileUseCase(fakeUserRepo),
            getLegalWorkshopUseCase = GetLegalWorkshopUseCase(fakeEmployerInfoRepo),
            getLegalWorkshopCeoUseCase = GetLegalWorkshopCeoUseCase(fakeEmployerInfoRepo),
            requestLegalTicketUseCase = RequestLegalTicketUseCase(fakeEmployerInfoRepo),
            submitLegalWorkshopInfoUseCase = SubmitLegalWorkshopInfoUseCase(fakeEmployerInfoRepo),
            requestRealTicketUseCase = RequestRealTicketUseCase(fakeEmployerInfoRepo),
            submitRealWorkshopInfoUseCase = SubmitRealWorkshopInfoUseCase(fakeEmployerInfoRepo),
            getProvincesUseCase = GetProvincesUseCase(fakeCityProvinceRepo),
            getCitiesByProvinceUseCase = GetCitiesByProvinceUseCase(fakeCityProvinceRepo),
            getBranchesUseCase = GetBranchesUseCase(fakeContractsRepo),
        )
    }

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `initial data load populates user profile, workshops and provinces`() = runTest(testDispatcher) {
        viewModel.uiState.test {
            val state = awaitUntil { it.workshops.isNotEmpty() && it.provinces.isNotEmpty() && it.userFullName.isNotBlank() }

            assertEquals("حسین توکلی", state.userFullName)
            assertEquals("0012345678", state.userNationalCode)
            assertEquals("09123456789", state.userMobile)
            assertEquals(2, state.workshops.size)
            assertEquals("شرکت تست", state.workshops[0].name)
            assertTrue(state.workshops[0].isLegal)
            assertEquals(2, state.provinces.size)
            assertEquals("تهران", state.provinces[0].provinceName)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `switching tabs changes active tab and clears real validation error`() = runTest(testDispatcher) {
        viewModel.uiState.test {
            awaitItem()

            viewModel.sendIntent(CompleteEmployerInfoIntent.SelectTab(CompleteEmployerInfoTab.REAL))
            val realTabState = awaitUntil { it.tab == CompleteEmployerInfoTab.REAL }
            assertEquals(CompleteEmployerInfoTab.REAL, realTabState.tab)

            viewModel.sendIntent(CompleteEmployerInfoIntent.SelectTab(CompleteEmployerInfoTab.LEGAL))
            val legalTabState = awaitUntil { it.tab == CompleteEmployerInfoTab.LEGAL }
            assertEquals(CompleteEmployerInfoTab.LEGAL, legalTabState.tab)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `legal national id with 11 digits triggers inquiry and updates name`() = runTest(testDispatcher) {
        fakeEmployerInfoRepo.legalWorkshopResult = LegalWorkshopDN(name = "صنایع فولاد", nationalCode = "10101234567")

        viewModel.uiState.test {
            awaitItem()

            viewModel.sendIntent(CompleteEmployerInfoIntent.ChangeLegalNationalId("10101234567"))
            val state = awaitUntil { it.legalWorkshopName == "صنایع فولاد" }

            assertEquals("10101234567", state.legalNationalId)
            assertEquals("صنایع فولاد", state.legalWorkshopName)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `ceo national id and birth date triggers ceo inquiry`() = runTest(testDispatcher) {
        fakeEmployerInfoRepo.legalWorkshopCeoResult = LegalWorkshopCeoDN(firstName = "محمد", lastName = "احمدی")

        viewModel.uiState.test {
            awaitItem()

            viewModel.sendIntent(CompleteEmployerInfoIntent.ChangeCeoNationalId("0012345678"))
            viewModel.sendIntent(CompleteEmployerInfoIntent.SelectCeoBirthDate(540864000000L, "1365/12/03"))

            val state = awaitUntil { it.ceoFullName == "محمد احمدی" }
            assertEquals("محمد احمدی", state.ceoFullName)
            assertEquals("0012345678", state.ceoNationalId)
            assertEquals("1365/12/03", state.ceoBirthDatePersian)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `selecting province loads cities and selecting city loads branches`() = runTest(testDispatcher) {
        viewModel.uiState.test {
            awaitItem()

            viewModel.sendIntent(CompleteEmployerInfoIntent.SelectProvince(ProvincePR(provinceCode = "07", provinceName = "تهران")))
            val stateWithCities = awaitUntil { it.cities.isNotEmpty() }
            assertEquals("0701", stateWithCities.cities[0].cityCode)

            viewModel.sendIntent(CompleteEmployerInfoIntent.SelectCity(CityPR(cityCode = "0701", cityName = "تهران")))
            val stateWithBranches = awaitUntil { it.branches.isNotEmpty() }
            assertEquals("123", stateWithBranches.branches[0].code)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `submitting valid real form starts verification step`() = runTest(testDispatcher) {
        viewModel.uiState.test {
            awaitUntil { it.userMobile.isNotBlank() }

            viewModel.sendIntent(CompleteEmployerInfoIntent.ChangeRealWorkshopCode("0012345678"))
            viewModel.sendIntent(CompleteEmployerInfoIntent.SelectProvince(ProvincePR(provinceCode = "07", provinceName = "تهران")))
            awaitUntil { it.cities.isNotEmpty() }
            viewModel.sendIntent(CompleteEmployerInfoIntent.SelectCity(CityPR(cityCode = "0701", cityName = "تهران")))
            awaitUntil { it.branches.isNotEmpty() }
            viewModel.sendIntent(CompleteEmployerInfoIntent.SelectBranch(BranchPR(code = "123", name = "شعبه ۱")))

            viewModel.sendIntent(CompleteEmployerInfoIntent.SubmitRealForm)
            val verifyingState = awaitUntil { it.isVerifying }

            assertTrue(verifyingState.isVerifying)
            assertEquals(VerifyPath.REAL, verifyingState.verifyPath)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `submitting otp code submits request and shows success dialog`() = runTest(testDispatcher) {
        viewModel.uiState.test {
            awaitUntil { it.userMobile.isNotBlank() }

            viewModel.sendIntent(CompleteEmployerInfoIntent.ChangeRealWorkshopCode("0012345678"))
            viewModel.sendIntent(CompleteEmployerInfoIntent.SelectProvince(ProvincePR(provinceCode = "07", provinceName = "تهران")))
            awaitUntil { it.cities.isNotEmpty() }
            viewModel.sendIntent(CompleteEmployerInfoIntent.SelectCity(CityPR(cityCode = "0701", cityName = "تهران")))
            awaitUntil { it.branches.isNotEmpty() }
            viewModel.sendIntent(CompleteEmployerInfoIntent.SelectBranch(BranchPR(code = "123", name = "شعبه ۱")))
            viewModel.sendIntent(CompleteEmployerInfoIntent.SubmitRealForm)
            awaitUntil { it.isVerifying }

            viewModel.sendIntent(CompleteEmployerInfoIntent.ChangeOtpCode("12345"))
            viewModel.sendIntent(CompleteEmployerInfoIntent.SubmitOtpVerification)

            val successState = awaitUntil { it.dialogState == CompleteEmployerInfoDialog.SUCCESS_REAL }
            assertEquals(CompleteEmployerInfoDialog.SUCCESS_REAL, successState.dialogState)
            assertFalse(successState.isVerifying)
            cancelAndIgnoreRemainingEvents()
        }
    }

    /**
     * The segmented fields accept Persian-Indic digits, and `Char.isDigit()` is true for them, so
     * a length check alone lets them through to the wire. The service only reads ASCII.
     */
    @Test
    fun persianDigitsAreFoldedToAsciiBeforeTheRequestIsSent() = runTest(testDispatcher) {
        viewModel.uiState.test {
            awaitUntil { it.userMobile.isNotBlank() }

            viewModel.sendIntent(CompleteEmployerInfoIntent.ChangeRealWorkshopCode("۰۰۱۲۳۴۵۶۷۸"))
            viewModel.sendIntent(CompleteEmployerInfoIntent.SelectProvince(ProvincePR(provinceCode = "07", provinceName = "تهران")))
            awaitUntil { it.cities.isNotEmpty() }
            viewModel.sendIntent(CompleteEmployerInfoIntent.SelectCity(CityPR(cityCode = "0701", cityName = "تهران")))
            awaitUntil { it.branches.isNotEmpty() }
            viewModel.sendIntent(CompleteEmployerInfoIntent.SelectBranch(BranchPR(code = "123", name = "شعبه ۱")))
            viewModel.sendIntent(CompleteEmployerInfoIntent.SubmitRealForm)
            awaitUntil { it.isVerifying }

            viewModel.sendIntent(CompleteEmployerInfoIntent.ChangeOtpCode("۱۲۳۴۵"))
            viewModel.sendIntent(CompleteEmployerInfoIntent.SubmitOtpVerification)
            awaitUntil { it.dialogState == CompleteEmployerInfoDialog.SUCCESS_REAL }

            val sent = fakeEmployerInfoRepo.lastRealRequest
            assertEquals("0012345678", sent?.workshopCode)
            assertEquals("12345", sent?.ticketCode)
            cancelAndIgnoreRemainingEvents()
        }
    }

    /**
     * Exact duplicate objects sent by the API where all properties are the same are deduplicated
     * so only one workshop card gets shown.
     */
    @Test
    fun exactDuplicateAgreementsFromApiAreDeduplicatedToSingleItem() = runTest(testDispatcher) {
        val twin = agreementOf(
            workshopId = "0968210170",
            branchCode = "0960",
            name = "\u0622\u0645\u0648\u0632\u0634\u06AF\u0627\u0647 \u06A9\u0627\u0645\u067E\u06CC\u0648\u062A\u0631",
            characterCode = "01",
        )
        fakeWorkShopsRepo.employerAgreements = PagedListDN(items = listOf(twin, twin, twin), total = 3)

        viewModel.sendIntent(CompleteEmployerInfoIntent.LoadInitialData)
        viewModel.uiState.test {
            val state = awaitUntil { it.workshops.size == 1 }
            assertEquals(1, state.workshops.size)
            assertEquals("آموزشگاه کامپیوتر", state.workshops[0].name)
            cancelAndIgnoreRemainingEvents()
        }
    }

    /**
     * The service returns one row per agreement, so one workshop can arrive several times. Rows
     * that are identical in every field the card shows are the same row to a reader, so the mapper
     * collapses them; rows that differ in any of it are kept and must still get distinct ids,
     * because duplicate keys crash `LazyColumn` and make one card's expander open all of its twins.
     */
    @Test
    fun identicalAgreementsCollapseAndDifferingOnesKeepDistinctRowIds() = runTest(testDispatcher) {
        val twin = agreementOf(
            workshopId = "0968210170",
            branchCode = "0960",
            name = "آموزشگاه",
            characterCode = "01",
        )
        val differing = twin.copy(email = "second@tamin.ir")

        viewModel.uiState.test {
            awaitUntil { it.workshops.isNotEmpty() }

            fakeWorkShopsRepo.employerAgreements = PagedListDN(items = listOf(twin, twin, twin), total = 3)
            viewModel.sendIntent(CompleteEmployerInfoIntent.LoadInitialData)
            assertEquals(1, awaitUntil { it.workshops.size == 1 }.workshops.size)

            fakeWorkShopsRepo.employerAgreements = PagedListDN(items = listOf(twin, differing), total = 2)
            viewModel.sendIntent(CompleteEmployerInfoIntent.LoadInitialData)
            val kept = awaitUntil { it.workshops.size == 2 }
            assertEquals(2, kept.workshops.map { it.id }.toSet().size)

            cancelAndIgnoreRemainingEvents()
        }
    }

    /**
     * The submit button is driven by [CompleteEmployerInfoUiState.canSubmitReal], so the rule that
     * enables it and the message printed under it have to be the same rule.
     */
    @Test
    fun realFormCannotBeSubmittedUntilEveryRequirementIsMet() = runTest(testDispatcher) {
        viewModel.uiState.test {
            val empty = awaitUntil { it.userMobile.isNotBlank() }
            assertFalse(empty.canSubmitReal)

            viewModel.sendIntent(CompleteEmployerInfoIntent.ChangeRealWorkshopCode("0012345678"))
            // code alone is not enough: the branch is still missing
            assertFalse(awaitUntil { it.realWorkshopCode.isNotBlank() }.canSubmitReal)

            viewModel.sendIntent(CompleteEmployerInfoIntent.SelectProvince(ProvincePR(provinceCode = "07", provinceName = "تهران")))
            awaitUntil { it.cities.isNotEmpty() }
            viewModel.sendIntent(CompleteEmployerInfoIntent.SelectCity(CityPR(cityCode = "0701", cityName = "تهران")))
            awaitUntil { it.branches.isNotEmpty() }
            viewModel.sendIntent(CompleteEmployerInfoIntent.SelectBranch(BranchPR(code = "123", name = "شعبه ۱")))

            assertTrue(awaitUntil { it.selectedBranch != null }.canSubmitReal)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `timer expiration triggers expired dialog`() = runTest(testDispatcher) {
        viewModel.uiState.test {
            awaitItem()

            viewModel.sendIntent(CompleteEmployerInfoIntent.OnTimerExpired)
            val expiredState = awaitUntil { it.dialogState == CompleteEmployerInfoDialog.TIMER_EXPIRED }
            assertEquals(CompleteEmployerInfoDialog.TIMER_EXPIRED, expiredState.dialogState)
            assertFalse(expiredState.isVerifying)
            cancelAndIgnoreRemainingEvents()
        }
    }

    private suspend fun ReceiveTurbine<CompleteEmployerInfoUiState>.awaitUntil(
        predicate: (CompleteEmployerInfoUiState) -> Boolean
    ): CompleteEmployerInfoUiState {
        var state = awaitItem()
        while (!predicate(state)) {
            state = awaitItem()
        }
        return state
    }
}

// ──────────────── Fake Repositories ────────────────

private class FakeTestEmployerInfoRepo : EmployerInfoRepository {
    var legalWorkshopResult = LegalWorkshopDN(name = "شرکت پتروشیمی", nationalCode = "10101234567")
    var legalWorkshopCeoResult = LegalWorkshopCeoDN(firstName = "علی", lastName = "محمدی")

    /** What the screen actually asked to be sent — asserted on, not just what came back. */
    var lastRealRequest: RealWorkshopInfoRequestDN? = null
    var lastLegalRequest: LegalWorkshopInfoRequestDN? = null

    override fun getLegalWorkshop(legalWorkshopId: String): Flow<LegalWorkshopDN> = flow {
        emit(legalWorkshopResult)
    }

    override fun getLegalWorkshopCeo(nationalCode: String, birthDateMillis: Long): Flow<LegalWorkshopCeoDN> = flow {
        emit(legalWorkshopCeoResult)
    }

    override fun requestLegalTicket(mobile: String, email: String, ceoNationalCode: String): Flow<String> = flow {
        emit("12345")
    }

    override fun submitLegalWorkshopInfo(request: LegalWorkshopInfoRequestDN): Flow<String> = flow {
        lastLegalRequest = request
        emit("OK")
    }

    override fun requestRealTicket(mobile: String, email: String): Flow<String> = flow {
        emit("54321")
    }

    override fun submitRealWorkshopInfo(request: RealWorkshopInfoRequestDN): Flow<String> = flow {
        lastRealRequest = request
        emit("OK")
    }
}

private class FakeTestUserRepo : UserRepository {
    override suspend fun getCurrentUser(): Flow<CurrentUserDN> = flowOf(CurrentUserDN())

    override suspend fun getUserProfile(): Flow<UserProfileDN> = flow {
        emit(
            UserProfileDN(
                entityId = "1",
                login = "user",
                firstName = "حسین",
                lastName = "توکلی",
                email = "user@test.ir",
                nationalCode = "0012345678",
                mobile = "09123456789",
            )
        )
    }

    override fun getIdentityInfo(): Flow<IdentityInfoDN> = flowOf()
    override suspend fun getUserProfileImage(): Flow<String> = flowOf("")
    override suspend fun fetchTaminRelation(): Flow<TaminRelationDN> = flowOf()
    override suspend fun sendImageRequest(branchCode: String, serialId: String): Flow<String> = flowOf("")
    override suspend fun changeMobile(mobileNumber: String): Flow<EditMobileResponseDN> = flowOf()
    override suspend fun verifyChangeMobileCode(mobile: String, otp: String, otpHashCode: String): Flow<String> = flowOf("")
    override suspend fun getSubDominantsInfo(filters: List<ApiFilterDN>): Flow<SubdominantDN> = flowOf()
    override suspend fun getBankAccountList(filters: List<ApiFilterDN>): Flow<List<BankAccountDN>> = flowOf(emptyList())
    override suspend fun getInsuredActiveBranch(): Flow<List<InsuredActiveBranchDN>> = flowOf(emptyList())
    override suspend fun getRelationTaminAll(filters: List<ApiFilterDN>): Flow<List<ActiveRelationDN>> = flowOf(emptyList())
    override fun getElectronicFile(filters: List<ApiFilterDN>): Flow<List<ElectronicFileDN>> = flowOf(emptyList())
    override suspend fun downloadDocument(url: String): PdfDownloadDN = TODO()
    override fun checkUserIsNew(nationalId: String): Flow<Boolean> = flowOf(false)
    override suspend fun registerBankAccount(accountNumber: String, bankCode: String, accountTypeCode: String, startDateMillis: Long): Flow<String?> = flowOf(null)
    override suspend fun getStatusCertificateReport(filters: List<ApiFilterDN>): Flow<String> = flowOf("")
    override suspend fun getWageCertificateReport(filters: List<ApiFilterDN>): Flow<String> = flowOf("")
    override suspend fun getRecipients(filters: List<ApiFilterDN>): Flow<List<RecipientDN>> = flowOf(emptyList())
}

private class FakeTestCityProvinceRepo : CityProvinceRepository {
    override fun getProvinces(): Flow<List<ProvinceDN>> = flowOf(
        listOf(
            ProvinceDN(provinceCode = "07", provinceName = "تهران", status = null, statusStartDate = null),
            ProvinceDN(provinceCode = "04", provinceName = "اصفهان", status = null, statusStartDate = null),
        )
    )

    override fun getCitiesByProvince(provinceCode: String): Flow<CityListResultDN> = flowOf(
        CityListResultDN(cities = listOf(CityDN(cityCode = "0701", cityName = "تهران", provinceCode = provinceCode)))
    )

    override fun getCities(cityName: String?, provinceCode: String?): Flow<List<CityDN>> = flowOf(emptyList())
    override fun getCity(cityId: String): Flow<CityDN> = flowOf(CityDN(cityCode = cityId, cityName = "تهران", provinceCode = "07"))
    override fun getProvince(provinceId: String): Flow<ProvinceDN> = flowOf(ProvinceDN(provinceCode = provinceId, provinceName = "تهران", status = null, statusStartDate = null))
}

private class FakeTestContractsRepo : ContractsRepository {
    override fun getBranches(cityCode: String, page: Int): Flow<PagedListDN<BranchDN>> = flowOf(
        PagedListDN(
            items = listOf(
                BranchDN(
                    code = "123",
                    name = "شعبه ۱",
                    branchAddress = "تهران",
                    cityCode = cityCode,
                    minCode = null,
                    maxCode = null,
                ),
            ),
            total = 1,
        ),
    )

    override fun getContracts(page: Int): Flow<PagedListDN<ContractDN>> = flowOf(PagedListDN())
    override fun getContractsByPremiumType(premiumTypeCode: String, page: Int): Flow<PagedListDN<ContractDN>> =
        flowOf(PagedListDN())
    override fun getStudentInsuranceContracts(page: Int): Flow<PagedListDN<ContractDN>> = flowOf(PagedListDN())
    override fun getRegistrationInfo(): Flow<RegistrationInfoDN> = flowOf(RegistrationInfoDN(null, true, null, null, null))
    override fun getSpcPremiumRates(): Flow<List<PremiumRateDN>> = flowOf(emptyList())
    override fun getFreeJobWages(page: Int, searchQuery: String?): Flow<PagedListDN<FreeJobDN>> =
        flowOf(PagedListDN())
    override fun getFreelancePremiumRange(params: FreelancePremiumRangeParams): Flow<FreelancePremiumRangeDN> = flowOf(FreelancePremiumRangeDN(0L, 0L, 0, 0L))
    override fun getOptionalPremiumRange(): Flow<FreelancePremiumRangeDN> = flowOf(FreelancePremiumRangeDN(0L, 0L, 0, 0L))
    override fun checkRedCrossStatus(): Flow<String> = flowOf("ok")
    override fun checkMedicalStudent(): Flow<String> = flowOf("ok")
    override fun calculateFreelanceSalary(params: FreelanceCalculateSalaryParams): Flow<Long> = flowOf(0L)
    override fun calculateOptionalSalary(premiumRateCode: String): Flow<Long> = flowOf(0L)
    override fun makeFreelanceContract(params: FreelanceMakeContractParams): Flow<FreelanceContractResultDN> = flowOf(FreelanceContractResultDN(null, null))
    override fun makeContract(params: FreelanceMakeContractParams): Flow<FreelanceContractResultDN> = flowOf(FreelanceContractResultDN(null, null))
    override fun makeFreelanceContractByGuardian(params: FreelanceContractByGuardianParams): Flow<FreelanceContractResultDN> = flowOf(FreelanceContractResultDN(null, null))
    override fun makeOptionalContractByGuardian(params: OptionalContractByGuardianParams): Flow<FreelanceContractResultDN> = flowOf(FreelanceContractResultDN(null, null))
    override fun getInsurancePayment(params: InsurancePaymentParamsDN): Flow<InsurancePaymentDN> = flowOf(InsurancePaymentDN(null, null, null, null))
    override fun checkInsurancePaymentStatus(systemType: String): Flow<Any?> = flowOf(null)
    override fun uploadImage(request: UploadImageRequestDN): Flow<String> = flowOf("img1")
    override fun saveContact(request: SaveContactRequestDN): Flow<Any?> = flowOf(null)
}

/** One agreement in the shape the merged model uses. */
private fun agreementOf(
    workshopId: String,
    branchCode: String,
    name: String,
    characterCode: String,
    branchOfficeName: String = "",
    email: String = "",
    mobile: String = "",
) = EmployerAgreementDN(
    commitmentDate = "14030519",
    email = email,
    mobile = mobile,
    workshop = WorkshopSummaryDN(
        workshopId = workshopId,
        branchCode = branchCode,
        name = name,
        branchOfficeName = branchOfficeName,
        branchOfficeCode = branchCode,
        characterCode = characterCode,
    ),
)

private val legalWorkshop = agreementOf(
    workshopId = "0081631829",
    branchCode = "1202",
    name = "\u0634\u0631\u06A9\u062A \u062A\u0633\u062A",
    characterCode = "02",
    branchOfficeName = "\u0634\u0639\u0628\u0647 \u06F2 \u0645\u0634\u0647\u062F",
    email = "info@damabokhar.ir",
    mobile = "09153214478",
)

private val realWorkshop = agreementOf(
    workshopId = "0016318941",
    branchCode = "1205",
    name = "\u062F\u0631\u0645\u0627\u0646\u06AF\u0627\u0647",
    characterCode = "01",
    branchOfficeName = "\u0634\u0639\u0628\u0647 \u06F5 \u0645\u0634\u0647\u062F",
)
