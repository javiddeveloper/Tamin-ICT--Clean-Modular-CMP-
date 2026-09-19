package com.tamin.taminhamrah.data.repository.home

import com.tamin.taminhamrah.data.local.dao.HomeContentDao
import com.tamin.taminhamrah.data.local.entity.HomeContentEntity
import com.tamin.taminhamrah.data.local.entity.RequestEntity
import com.tamin.taminhamrah.data.local.entity.UserInfoEntity
import com.tamin.taminhamrah.model.activeRelation.ActiveRelationDN
import com.tamin.taminhamrah.model.bankAccount.BankAccountDN
import com.tamin.taminhamrah.model.common.BeneficiaryDN
import com.tamin.taminhamrah.model.common.FeatureFlag
import com.tamin.taminhamrah.model.common.InsuranceTypeDN
import com.tamin.taminhamrah.model.common.JobTitleDN
import com.tamin.taminhamrah.model.common.JobTitleListDN
import com.tamin.taminhamrah.model.common.MainServiceDN
import com.tamin.taminhamrah.model.common.MenuServiceStatusDN
import com.tamin.taminhamrah.model.common.RoleDN
import com.tamin.taminhamrah.model.common.UserTypeInfoDN
import com.tamin.taminhamrah.model.identity.IdentityInfoDN
import com.tamin.taminhamrah.model.paging.PageDN
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.stories.StoryChannelDN
import com.tamin.taminhamrah.model.stories.StoryEngagementDN
import com.tamin.taminhamrah.model.treatment.DependantUserUnderEighteenDN
import com.tamin.taminhamrah.model.treatment.DeservedTreatmentDN
import com.tamin.taminhamrah.model.treatment.ElectronicPrescriptionDN
import com.tamin.taminhamrah.model.treatment.ElectronicPrescriptionDetailDN
import com.tamin.taminhamrah.model.treatment.ElectronicPrescriptionPriceDN
import com.tamin.taminhamrah.model.treatment.MedicalConfirmationDN
import com.tamin.taminhamrah.model.treatment.TreatmentCostDN
import com.tamin.taminhamrah.model.user.CurrentUserDN
import com.tamin.taminhamrah.model.user.EditMobileResponseDN
import com.tamin.taminhamrah.model.user.TaminRelationDN
import com.tamin.taminhamrah.model.user.UserProfileDN
import com.tamin.taminhamrah.model.userRequest.RequestErrorDN
import com.tamin.taminhamrah.model.userRequest.SmartGuideDN
import com.tamin.taminhamrah.model.userRequest.SmartGuideSearchParams
import com.tamin.taminhamrah.model.userRequest.UserRequestDN
import com.tamin.taminhamrah.model.userRequest.UserRequestDetailsDN
import com.tamin.taminhamrah.model.userRequest.UserRequestSearchParams
import com.tamin.taminhamrah.model.userRequest.UserRequestStatusDN
import com.tamin.taminhamrah.model.userRequest.UserRequestTypeDN
import com.tamin.taminhamrah.model.subdominant.SubdominantDN
import com.tamin.taminhamrah.model.subdominant.insuredActiveBranch.InsuredActiveBranchDN
import com.tamin.taminhamrah.model.erecords.images.ElectronicFileDN
import com.tamin.taminhamrah.model.certificate.RecipientDN
import com.tamin.taminhamrah.repository.UserRepository
import com.tamin.taminhamrah.repository.common.CommonRepository
import com.tamin.taminhamrah.repository.home.HomeQuickAccessGroup
import com.tamin.taminhamrah.repository.home.HomeServiceMembership
import com.tamin.taminhamrah.repository.stories.StoryRepository
import com.tamin.taminhamrah.repository.treatment.TreatmentRepository
import com.tamin.taminhamrah.repository.userRequest.UserRequestRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

private class FakeHomeContentDao : HomeContentDao {
    val state = MutableStateFlow<HomeContentEntity?>(null)
    var insertCallCount = 0

    override fun getHomeContent(): Flow<HomeContentEntity?> = state.asStateFlow()

    override suspend fun insertOrUpdate(content: HomeContentEntity) {
        insertCallCount++
        state.value = content
    }

    override suspend fun clear() { state.value = null }
}

/** Only [getIdentityInfo]/[getRelationTaminAll] are exercised by [HomeRepositoryImpl]; the rest
 *  throw if called, so an accidental new dependency on them fails loudly instead of silently. */
private class FakeUserRepository : UserRepository {
    var identityInfoResult: IdentityInfoDN? = null
    var relationTaminAllResult: List<ActiveRelationDN> = emptyList()
    var shouldThrowError = false
    var error: Throwable = RuntimeException("Fake User Repository Error")

    override fun getIdentityInfo(): Flow<IdentityInfoDN> = flow {
        if (shouldThrowError) throw error
        identityInfoResult?.let { emit(it) }
    }

    override suspend fun getRelationTaminAll(filters: List<ApiFilterDN>): Flow<List<ActiveRelationDN>> = flow {
        if (shouldThrowError) throw error
        emit(relationTaminAllResult)
    }

    override suspend fun getUserProfileImage(): Flow<String> = notUsed()
    override suspend fun fetchTaminRelation(): Flow<TaminRelationDN> = notUsed()
    override suspend fun sendImageRequest(branchCode: String, serialId: String): Flow<String> = notUsed()
    override suspend fun changeMobile(mobileNumber: String): Flow<EditMobileResponseDN> = notUsed()
    override suspend fun verifyChangeMobileCode(mobile: String, otp: String, otpHashCode: String): Flow<String> = notUsed()
    override suspend fun getSubDominantsInfo(filters: List<ApiFilterDN>): Flow<SubdominantDN> = notUsed()
    override suspend fun getBankAccountList(filters: List<ApiFilterDN>): Flow<List<BankAccountDN>> = notUsed()
    override suspend fun getInsuredActiveBranch(): Flow<List<InsuredActiveBranchDN>> = notUsed()
    override fun getElectronicFile(filters: List<ApiFilterDN>): Flow<List<ElectronicFileDN>> = notUsed()
    override suspend fun downloadDocument(url: String): PdfDownloadDN = throw NotImplementedError()
    override suspend fun getUserProfile(): Flow<UserProfileDN> = notUsed()
    override suspend fun getCurrentUser(): Flow<CurrentUserDN> = notUsed()
    override fun checkUserIsNew(nationalId: String): Flow<Boolean> = notUsed()
    override suspend fun registerBankAccount(
        accountNumber: String, bankCode: String, accountTypeCode: String, startDateMillis: Long,
    ): Flow<String?> = notUsed()
    override suspend fun getStatusCertificateReport(filters: List<ApiFilterDN>): Flow<String> = notUsed()
    override suspend fun getWageCertificateReport(filters: List<ApiFilterDN>): Flow<String> = notUsed()
    override suspend fun getRecipients(filters: List<ApiFilterDN>): Flow<List<RecipientDN>> = notUsed()

    private fun <T> notUsed(): Flow<T> = throw NotImplementedError("not exercised by HomeRepositoryImpl")
}

/** Only [refreshDeservedTreatment] is exercised. */
private class FakeTreatmentRepository : TreatmentRepository {
    var getDeservedTreatmentResult: List<DeservedTreatmentDN> = emptyList()
    var shouldThrowError = false
    var error: Throwable = RuntimeException("Fake Treatment Repository Error")

    override suspend fun refreshDeservedTreatment(nationalCode: String): List<DeservedTreatmentDN> {
        if (shouldThrowError) throw error
        return getDeservedTreatmentResult
    }

    override suspend fun getDeservedTreatment(nationalCode: String): Flow<List<DeservedTreatmentDN>> = notUsed()
    override suspend fun getElectronicPrescriptionList(
        requestTypeId: String, nationalCode: String, patientNationalCode: String, startDate: String, endDate: String,
    ): Flow<List<ElectronicPrescriptionDN>> = notUsed()
    override suspend fun getElectronicPrescriptionDetail(
        noteHeadID: String, nationalCode: String, patientNationalCode: String, flagSata: String, type: String,
    ): Flow<List<ElectronicPrescriptionDetailDN>> = notUsed()
    override suspend fun getElectronicPrescriptionPrice(noteHeadID: String, nationalCode: String): Flow<List<ElectronicPrescriptionPriceDN>> = notUsed()
    override suspend fun getDependantUnderEighteen(nationalCode: String): Flow<List<DependantUserUnderEighteenDN>> = notUsed()
    override suspend fun getPrescriptionPdfFile(prescriptionID: String): Flow<PdfDownloadDN> = notUsed()
    override suspend fun downloadLabResultPdf(
        patientID: String?, noteHeadEprescID: String?, currentUserNationalCode: String?,
    ): Flow<PdfDownloadDN> = notUsed()
    override suspend fun getTreatmentCosts(): Flow<List<TreatmentCostDN>> = notUsed()
    override suspend fun getTreatmentCostsPDF(repId: String): Flow<PdfDownloadDN> = notUsed()
    override suspend fun sendToInboxTreatmentCosts(repId: String): Flow<String> = notUsed()
    override suspend fun getMedicalConfirmations(): Flow<List<MedicalConfirmationDN>> = notUsed()
    override suspend fun getMedicalConfirmationPdf(repId: String): Flow<PdfDownloadDN> = notUsed()
    override suspend fun sendToInboxMedicalConfirmation(repId: String): Flow<String> = notUsed()

    private fun <T> notUsed(): Flow<T> = throw NotImplementedError("not exercised by HomeRepositoryImpl")
}

/** Only [refreshUserRequests] is exercised. */
private class FakeUserRequestRepository : UserRequestRepository {
    var userRequestsResult: List<UserRequestDN> = emptyList()
    var shouldThrowError = false
    var error: Throwable = RuntimeException("Fake UserRequest Repository Error")

    override suspend fun refreshUserRequests(search: UserRequestSearchParams): List<UserRequestDN> {
        if (shouldThrowError) throw error
        return userRequestsResult
    }

    override fun getUserRequests(search: UserRequestSearchParams): Flow<List<UserRequestDN>> = notUsed()
    override suspend fun getRequestTypes(query: ApiQueryParamDN?): List<UserRequestTypeDN> = throw NotImplementedError()
    override suspend fun getRequestErrors(requestId: Long): List<RequestErrorDN> = throw NotImplementedError()
    override suspend fun getSmartGuideList(params: SmartGuideSearchParams): List<SmartGuideDN> = throw NotImplementedError()
    override suspend fun getUserRequestDetail(id: Long): UserRequestDN = throw NotImplementedError()
    override suspend fun getShowRequestInfo(referenceId: String, requestTypeId: Long): UserRequestDetailsDN? = throw NotImplementedError()
    override suspend fun downloadUserRequestDocument(guid: String): String = throw NotImplementedError()

    private fun <T> notUsed(): Flow<T> = throw NotImplementedError("not exercised by HomeRepositoryImpl")
}

/** Only [getChannels] is exercised. */
private class FakeStoryRepository : StoryRepository {
    var channelsResult: List<StoryChannelDN> = emptyList()
    var shouldThrowError = false
    var error: Throwable = RuntimeException("Fake Story Repository Error")

    override fun getChannels(forceRefresh: Boolean): Flow<List<StoryChannelDN>> = flow {
        if (shouldThrowError) throw error
        emit(channelsResult)
    }

    override fun observeSeenChannels(): Flow<Set<String>> = MutableStateFlow(emptySet<String>()).asStateFlow()
    override fun observeEngagement(): Flow<StoryEngagementDN> = MutableStateFlow(StoryEngagementDN()).asStateFlow()
    override suspend fun markChannelSeen(channelKey: String) = throw NotImplementedError()
    override suspend fun toggleLike(itemId: String) = throw NotImplementedError()
    override suspend fun toggleSave(itemId: String) = throw NotImplementedError()
}

/** Only [getMainMenu] is exercised. */
private class FakeCommonRepository : CommonRepository {
    var mainMenuResult: List<MainServiceDN> = emptyList()
    var shouldThrowError = false
    var error: Throwable = RuntimeException("Fake Common Repository Error")

    override fun getMainMenu(versionCode: String, forceUpdate: Boolean): Flow<List<MainServiceDN>> = flow {
        if (shouldThrowError) throw error
        emit(mainMenuResult)
    }

    override fun getBeneficiary(filters: List<ApiFilterDN>): Flow<List<BeneficiaryDN>> = notUsed()
    override fun getRegistrationDeclarationForm(): Flow<ByteArray> = notUsed()
    override fun getJobTitle(query: ApiQueryParamDN): Flow<JobTitleListDN?> = notUsed()
    override fun getJobTitlePage(query: ApiQueryParamDN): Flow<PageDN<JobTitleDN>> = notUsed()
    override fun getRoles(): Flow<List<RoleDN>> = notUsed()
    override fun getInsuranceTypes(searchText: String?): Flow<List<InsuranceTypeDN>> = notUsed()
    override fun checkUserType(): Flow<UserTypeInfoDN> = notUsed()

    private fun <T> notUsed(): Flow<T> = throw NotImplementedError("not exercised by HomeRepositoryImpl")
}

class HomeRepositoryImplTest {

    private lateinit var dao: FakeHomeContentDao
    private lateinit var userRepository: FakeUserRepository
    private lateinit var treatmentRepository: FakeTreatmentRepository
    private lateinit var requestsRepository: FakeUserRequestRepository
    private lateinit var storyRepository: FakeStoryRepository
    private lateinit var commonRepository: FakeCommonRepository
    private lateinit var repository: HomeRepositoryImpl

    private fun identity(nationalId: String = "0012345678") = IdentityInfoDN(
        cityOfBirthId = null, cityOfIssueId = null, countryId = null, dateOfBirth = null,
        fatherName = null, firstName = "سنا", gender = null, id = 1, idCardNumber = null,
        idCardSerial1 = null, idCardSerial2 = null, lastName = "حقیقی", nationalId = nationalId, ssn = null,
    )

    /** A menu row for [flag], enabled unless [status] says otherwise. */
    private fun menuRow(flag: FeatureFlag, status: MenuServiceStatusDN = MenuServiceStatusDN.ACTIVE) =
        MainServiceDN(id = flag.id, name = flag.name, icon = "icon-${flag.id}", status = status)

    @BeforeTest
    fun setUp() {
        dao = FakeHomeContentDao()
        userRepository = FakeUserRepository()
        treatmentRepository = FakeTreatmentRepository()
        requestsRepository = FakeUserRequestRepository()
        storyRepository = FakeStoryRepository()
        commonRepository = FakeCommonRepository()
        repository = HomeRepositoryImpl(
            dao = dao,
            userRepository = userRepository,
            treatmentRepository = treatmentRepository,
            requestsRepository = requestsRepository,
            storyRepository = storyRepository,
            commonRepository = commonRepository,
        )
    }

    @Test
    fun `syncHomeContent carries the request status code and type id through to the cache`() = runTest {
        userRepository.identityInfoResult = identity()
        requestsRepository.userRequestsResult = listOf(
            UserRequestDN(
                id = 1L, refCode = "REF-1", title = "تأییدیه پزشکی", comment = null,
                creationTime = 1_700_000_000_000L, createByName = null,
                status = UserRequestStatusDN(requestCode = "18", requestDesc = "تأیید شد"),
                requestType = UserRequestTypeDN(id = 42L, title = null, description = null),
                referenceId = null,
            )
        )
        repository.syncHomeContent()

        val cachedRequest = dao.state.value?.requests?.single()
        assertNotNull(cachedRequest)
        assertEquals("18", cachedRequest.statusCode)
        assertEquals(42L, cachedRequest.requestTypeId)
    }

    // buildCampaignEntities/buildQuickAccessEntities/buildSpecialServiceEntities are pure functions
    // pulled out of syncHomeContent() specifically so they're testable directly — going through
    // syncHomeContent() itself would require AppConfig.versionName to resolve (it reads a
    // Koin-registered Context on Android, unavailable under a plain JVM unit test).

    @Test
    fun `buildCampaignEntities marks a flag openable only when its menu status opens something`() {
        val menu = FeatureFlag.entries.map { flag ->
            when (flag) {
                FeatureFlag.FREELANCE_INSURANCE -> menuRow(flag, MenuServiceStatusDN.DISABLED)
                else -> menuRow(flag)
            }
        }

        val entities = buildCampaignEntities(menu)

        val housewife = entities.find { it.flagId == FeatureFlag.HOUSEWIFE_INSURANCE.id }
        assertNotNull(housewife)
        assertTrue(housewife.isOpenable, "an ACTIVE-status flag should be openable")

        val freelance = entities.find { it.flagId == FeatureFlag.FREELANCE_INSURANCE.id }
        assertNotNull(freelance)
        assertFalse(freelance.isOpenable, "a DISABLED-status flag should not be openable")
    }

    @Test
    fun `buildQuickAccessEntities tags every row with its own of the 5 chip sections and carries status`() {
        val menu = FeatureFlag.entries.map { menuRow(it) }

        val entities = buildQuickAccessEntities(menu)

        val historyMember = HomeServiceMembership.history.first()
        val historyRow = entities.find { it.flagId == historyMember.id }
        assertNotNull(historyRow)
        assertEquals(HomeQuickAccessGroup.HISTORY, historyRow.group)
        assertEquals(MenuServiceStatusDN.ACTIVE, historyRow.status)

        val frequentMember = HomeServiceMembership.frequent.first()
        val frequentRow = entities.find { it.flagId == frequentMember.id }
        assertNotNull(frequentRow)
        assertEquals(HomeQuickAccessGroup.FREQUENT, frequentRow.group)
    }

    @Test
    fun `buildSpecialServiceEntities carries the menu status through`() {
        val menu = FeatureFlag.entries.map { flag ->
            if (flag == FeatureFlag.OCCURRENCE) menuRow(flag, MenuServiceStatusDN.TEMPORARY_DISABLED) else menuRow(flag)
        }

        val entities = buildSpecialServiceEntities(menu)

        val occurrence = entities.find { it.flagId == FeatureFlag.OCCURRENCE.id }
        assertNotNull(occurrence)
        assertEquals(MenuServiceStatusDN.TEMPORARY_DISABLED, occurrence.status)
    }

    @Test
    fun `syncHomeContent falls back to the cache for a piece whose fetch failed`() = runTest {
        // Seed a cache row as if a previous successful sync had already run.
        dao.insertOrUpdate(
            HomeContentEntity(
                id = 1,
                userInfo = UserInfoEntity(
                    fullName = "کاربر قبلی", hasDarmanCoverage = null, hasActiveRelation = null
                ),
                stories = null, campaigns = null, quickAccess = null, specialServices = null,
                requests = listOf(
                    RequestEntity(
                        id = "old", title = "درخواست قدیمی", date = "", status = "",
                        refCode = "OLD-REF", statusCode = "", requestTypeId = 0L,
                    )
                ),
            )
        )
        dao.insertCallCount = 0

        userRepository.identityInfoResult = identity()
        requestsRepository.shouldThrowError = true // requests fetch fails this round

        repository.syncHomeContent()

        assertEquals(1, dao.insertCallCount)
        val cached = dao.state.value
        // The failed requests fetch falls back to whatever was already cached, rather than the
        // sync throwing or blanking the section.
        assertEquals("OLD-REF", cached?.requests?.single()?.refCode)
    }

    @Test
    fun `syncHomeContent leaves fullName null on a blank identity, rather than falling back to a stale cached name`() = runTest {
        dao.insertOrUpdate(
            HomeContentEntity(
                id = 1,
                userInfo = UserInfoEntity(fullName = "نام قدیمی", hasDarmanCoverage = null, hasActiveRelation = null),
                stories = null, campaigns = null, quickAccess = null, specialServices = null, requests = null,
            )
        )
        // Identity fetch succeeds but the server has no name on file — fresher than the cache,
        // so it should win even though it resolves to "no name" rather than the stale cached one.
        userRepository.identityInfoResult = identity().copy(firstName = null, lastName = null)

        repository.syncHomeContent()

        assertEquals(null, dao.state.value?.userInfo?.fullName)
    }

    @Test
    fun `syncHomeContent falls back to the cached name only when the identity fetch itself failed`() = runTest {
        dao.insertOrUpdate(
            HomeContentEntity(
                id = 1,
                userInfo = UserInfoEntity(fullName = "نام قدیمی", hasDarmanCoverage = null, hasActiveRelation = null),
                stories = null, campaigns = null, quickAccess = null, specialServices = null, requests = null,
            )
        )
        userRepository.shouldThrowError = true

        repository.syncHomeContent()

        assertEquals("نام قدیمی", dao.state.value?.userInfo?.fullName)
    }

    @Test
    fun `syncHomeContent propagates a CancellationException instead of swallowing it`() = runTest {
        userRepository.shouldThrowError = true
        userRepository.error = CancellationException("scope torn down")

        assertFailsWith<CancellationException> {
            repository.syncHomeContent()
        }
        // Nothing should have been persisted — a swallowed cancellation completing the sync anyway
        // would be exactly the Item 3 bug.
        assertEquals(0, dao.insertCallCount)
    }
}
