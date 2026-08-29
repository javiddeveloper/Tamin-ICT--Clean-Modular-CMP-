package com.tamin.taminhamrah.feature.workshops.ui.completeEmployerInfo

import app.cash.turbine.ReceiveTurbine
import app.cash.turbine.test
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
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.subdominant.SubdominantDN
import com.tamin.taminhamrah.model.subdominant.insuredActiveBranch.InsuredActiveBranchDN
import com.tamin.taminhamrah.model.user.EditMobileResponseDN
import com.tamin.taminhamrah.model.user.TaminRelationDN
import com.tamin.taminhamrah.model.user.UserProfileDN
import com.tamin.taminhamrah.model.workshop.EmployerAgreementDN
import com.tamin.taminhamrah.model.workshop.EmployerAgreementListDN
import com.tamin.taminhamrah.model.workshop.EmployerWorkshopDN
import com.tamin.taminhamrah.repository.CityProvinceRepository
import com.tamin.taminhamrah.repository.UserRepository
import com.tamin.taminhamrah.repository.WorkShopsRepository
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
import com.tamin.taminhamrah.useCases.workshops.GetAllEmployerAgreementByNationalIdUseCase
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
    private lateinit var fakeWorkShopsRepo: FakeTestWorkShopsRepo
    private lateinit var fakeCityProvinceRepo: FakeTestCityProvinceRepo
    private lateinit var fakeContractsRepo: FakeTestContractsRepo
    private lateinit var viewModel: CompleteEmployerInfoViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeEmployerInfoRepo = FakeTestEmployerInfoRepo()
        fakeUserRepo = FakeTestUserRepo()
        fakeWorkShopsRepo = FakeTestWorkShopsRepo()
        fakeCityProvinceRepo = FakeTestCityProvinceRepo()
        fakeContractsRepo = FakeTestContractsRepo()

        viewModel = CompleteEmployerInfoViewModel(
            getAllEmployerAgreementUseCase = GetAllEmployerAgreementByNationalIdUseCase(fakeWorkShopsRepo),
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
        emit("OK")
    }

    override fun requestRealTicket(mobile: String, email: String): Flow<String> = flow {
        emit("54321")
    }

    override fun submitRealWorkshopInfo(request: RealWorkshopInfoRequestDN): Flow<String> = flow {
        emit("OK")
    }
}

private class FakeTestUserRepo : UserRepository {
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

private class FakeTestWorkShopsRepo : WorkShopsRepository {
    override suspend fun getAllEmployerAgreementByNationalId(filters: List<ApiFilterDN>): EmployerAgreementListDN {
        return EmployerAgreementListDN(
            total = 2,
            list = listOf(
                EmployerAgreementDN(
                    pymseq = "1",
                    regno = "123",
                    firstname = "حسین",
                    lastname = "توکلی",
                    emailaddr = "info@damabokhar.ir",
                    workshop = EmployerWorkshopDN(
                        sswn = "0081631829",
                        branchTitle = "شعبه ۲ مشهد",
                        branchName = "شعبه ۲ مشهد",
                        lastAddress = "مشهد، خیام",
                        characterCode = "02",
                        characterDesc = "شخصیت حقوقی",
                        workshopApproveDate = "1403/05/19",
                        inclusionDate = null,
                        brhCode = "1202",
                        activityName = null,
                        workshopRegisterDate = null,
                        branchCode = "1202",
                        workshopName = "شرکت تست",
                        employerName = "شرکت تست",
                        actitvityCode = null,
                        userId = null,
                        workshopId = "0081631829",
                        workshopUnemployedStat = null,
                    ),
                    nationalno = null,
                    mobileno = "09153214478",
                    startdate = null,
                    mastcusttype = null,
                    createdt = null,
                    masttyp = null,
                    logicalDeleted = null,
                    regemailseq = null,
                    special = null,
                    risuid = null,
                    nationalcode = null,
                    enddate = null,
                    letDate = "1403/05/19",
                    regdate = null,
                    roletype = null,
                    dname = "شرکت تست",
                    letNo = null,
                    createuid = null,
                ),
                EmployerAgreementDN(
                    pymseq = "2",
                    regno = "124",
                    firstname = "محمد",
                    lastname = "جعفری",
                    emailaddr = "clinic@gmail.com",
                    workshop = EmployerWorkshopDN(
                        sswn = "0016318941",
                        branchTitle = "شعبه ۵ مشهد",
                        branchName = "شعبه ۵ مشهد",
                        lastAddress = "مشهد، احمدآباد",
                        characterCode = "01",
                        characterDesc = "شخصیت حقیقی",
                        workshopApproveDate = "1402/11/03",
                        inclusionDate = null,
                        brhCode = "1205",
                        activityName = null,
                        workshopRegisterDate = null,
                        branchCode = "1205",
                        workshopName = "درمانگاه دندانپزشکی",
                        employerName = "درمانگاه دندانپزشکی",
                        actitvityCode = null,
                        userId = null,
                        workshopId = "0016318941",
                        workshopUnemployedStat = null,
                    ),
                    nationalno = null,
                    mobileno = "09151102234",
                    startdate = null,
                    mastcusttype = null,
                    createdt = null,
                    masttyp = null,
                    logicalDeleted = null,
                    regemailseq = null,
                    special = null,
                    risuid = null,
                    nationalcode = null,
                    enddate = null,
                    letDate = "1402/11/03",
                    regdate = null,
                    roletype = null,
                    dname = "درمانگاه دندانپزشکی",
                    letNo = null,
                    createuid = null,
                )
            )
        )
    }

    override suspend fun getPaymentSheets(filters: List<ApiFilterDN>) = null
    override suspend fun getWorkshopDebit(workshopId: String, branchCode: String) = null
    override suspend fun getWorkshopDebtInquiry(workshopId: String, branchCode: String) = null
    override fun getWorkshopObjectionableDebitList(workshopNumber: String, branchCode: String, filters: List<ApiFilterDN>) = flowOf(null)
    override fun getWorkshopRecentlyAddedMembers(filters: List<ApiFilterDN>) = flowOf(null)
    override fun getWorkshopsDebtsList(workshopId: String, branchId: String, filters: List<ApiFilterDN>) = flowOf(null)
    override fun getWorkshopMembers(filters: List<ApiFilterDN>) = flowOf(null)
    override fun getWorkshopStackHolders(filters: List<ApiFilterDN>) = flowOf(null)
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
    override fun getBranches(cityCode: String): Flow<List<BranchDN>> = flowOf(
        listOf(
            BranchDN(
                code = "123",
                name = "شعبه ۱",
                branchAddress = "تهران",
                cityCode = cityCode,
                minCode = null,
                maxCode = null,
            )
        )
    )

    override fun getContracts(query: ApiQueryParamDN?): Flow<List<ContractDN>> = flowOf(emptyList())
    override fun getContractsByPremiumType(premiumTypeCode: String): Flow<List<ContractDN>> = flowOf(emptyList())
    override fun getStudentInsuranceContracts(): Flow<List<ContractDN>> = flowOf(emptyList())
    override fun getRegistrationInfo(): Flow<RegistrationInfoDN> = flowOf(RegistrationInfoDN(null, true, null, null, null))
    override fun getSpcPremiumRates(): Flow<List<PremiumRateDN>> = flowOf(emptyList())
    override fun getFreeJobWages(): Flow<List<FreeJobDN>> = flowOf(emptyList())
    override fun getFreelancePremiumRange(params: FreelancePremiumRangeParams): Flow<FreelancePremiumRangeDN> = flowOf(FreelancePremiumRangeDN(0L, 0L, 0, 0L))
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
