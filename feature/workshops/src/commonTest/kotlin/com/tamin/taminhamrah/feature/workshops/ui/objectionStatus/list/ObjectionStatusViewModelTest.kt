package com.tamin.taminhamrah.feature.workshops.ui.objectionStatus.list

import app.cash.turbine.ReceiveTurbine
import app.cash.turbine.test
import com.tamin.taminhamrah.feature.workshops.fake.FakeWorkShopsRepository
import com.tamin.taminhamrah.model.activeRelation.ActiveRelationDN
import com.tamin.taminhamrah.model.bankAccount.BankAccountDN
import com.tamin.taminhamrah.model.certificate.RecipientDN
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
import com.tamin.taminhamrah.model.workshop.WorkShopObjectionDN
import com.tamin.taminhamrah.model.workshop.WorkShopObjectionStatus
import com.tamin.taminhamrah.model.workshop.WorkShopObjectionType
import com.tamin.taminhamrah.repository.UserRepository
import com.tamin.taminhamrah.useCases.user.GetIdentityInfoUseCase
import com.tamin.taminhamrah.useCases.workshops.GetWorkShopObjectionsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/**
 * پیگیری وضعیت اعتراض — the three business rules a later refactor could silently break: a search
 * must restart the list from page zero with only the applied filters, removing one filter chip must
 * not disturb the others, and scrolling to the end must grow the list rather than replace it.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ObjectionStatusViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var repository: FakeWorkShopsRepository

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeWorkShopsRepository()
    }

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    private fun viewModel() = ObjectionStatusViewModel(
        getWorkShopObjections = GetWorkShopObjectionsUseCase(repository),
        getIdentityInfo = GetIdentityInfoUseCase(FakeIdentityUserRepository()),
    )

    @Test
    fun `the list loads itself without being asked`() = runTest(testDispatcher) {
        repository.workShopObjections = objectionsPage(count = 2, total = 2)

        val viewModel = viewModel()

        assertEquals(2, viewModel.uiState.value.list.items.size)
        assertNotNull(repository.lastWorkShopObjectionQuery)
    }

    @Test
    fun `applying the objection number filter requests page zero with only that filter`() =
        runTest(testDispatcher) {
            repository.workShopObjections = objectionsPage(count = 1, total = 1)
            val viewModel = viewModel()

            viewModel.uiState.test {
                awaitItem()
                viewModel.sendIntent(
                    ObjectionStatusIntent.DraftChanged(
                        ObjectionStatusFilters(objectionNumber = "123456")
                    )
                )
                viewModel.sendIntent(ObjectionStatusIntent.ApplyFilters)
                val applied = awaitUntil { it.applied.objectionNumber == "123456" }

                assertEquals("123456", applied.applied.objectionNumber)
                val query = assertNotNull(repository.lastWorkShopObjectionQuery)
                assertEquals("123456", query.objectionNumber)
                assertNull(query.workshopId)
                assertNull(query.debitNumber)
                assertEquals(0, query.page)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `removing one filter chip keeps the others`() = runTest(testDispatcher) {
        repository.workShopObjections = objectionsPage(count = 1, total = 1)
        val viewModel = viewModel()

        viewModel.uiState.test {
            awaitItem()
            viewModel.sendIntent(
                ObjectionStatusIntent.DraftChanged(
                    ObjectionStatusFilters(objectionNumber = "123456", workshopId = "0968210170")
                )
            )
            viewModel.sendIntent(ObjectionStatusIntent.ApplyFilters)
            awaitUntil { it.applied.workshopId == "0968210170" }

            viewModel.sendIntent(ObjectionStatusIntent.RemoveFilter(ObjectionStatusFilterField.OBJECTION_NUMBER))
            val afterRemoval = awaitUntil { it.applied.objectionNumber.isBlank() }

            assertEquals("", afterRemoval.applied.objectionNumber)
            assertEquals("0968210170", afterRemoval.applied.workshopId)
            val query = assertNotNull(repository.lastWorkShopObjectionQuery)
            assertNull(query.objectionNumber)
            assertEquals("0968210170", query.workshopId)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `load more appends rows instead of replacing them`() = runTest(testDispatcher) {
        repository.workShopObjections = objectionsPage(count = 10, total = 20, startAt = 0)
        val viewModel = viewModel()

        viewModel.uiState.test {
            awaitItem()
            repository.workShopObjections = objectionsPage(count = 10, total = 20, startAt = 10)
            viewModel.sendIntent(ObjectionStatusIntent.LoadMore)
            val afterLoadMore = awaitUntil { it.list.items.size > 10 }

            assertEquals(20, afterLoadMore.list.items.size)
            assertEquals(1L, afterLoadMore.list.items.first().seqNo)
            assertEquals(20L, afterLoadMore.list.items.last().seqNo)
            cancelAndIgnoreRemainingEvents()
        }
    }

    private fun objectionsPage(count: Int, total: Int, startAt: Int = 0) = PagedListDN(
        items = List(count) {
            WorkShopObjectionDN(
                seqNo = (startAt + it + 1).toLong(),
                workshopId = "0968210170",
                debitNumber = "88214",
                objectionType = WorkShopObjectionType.ESTIMATE,
                objectionDate = "14030512",
                status = WorkShopObjectionStatus.SUBMITTED,
            )
        },
        total = total,
    )

    private suspend fun ReceiveTurbine<ObjectionStatusUiState>.awaitUntil(
        predicate: (ObjectionStatusUiState) -> Boolean,
    ): ObjectionStatusUiState {
        while (true) {
            val item = awaitItem()
            if (predicate(item)) return item
        }
    }
}

/** Only [getIdentityInfo] is exercised; every other member fails loudly if a future call reaches it. */
private class FakeIdentityUserRepository : UserRepository {
    override fun getIdentityInfo(): Flow<IdentityInfoDN> = flowOf(
        IdentityInfoDN(
            cityOfBirthId = null, cityOfIssueId = null, countryId = null, dateOfBirth = null,
            fatherName = null, firstName = "حسین", gender = null, id = null, idCardNumber = null,
            idCardSerial1 = null, idCardSerial2 = null, lastName = "توکلی", nationalId = "0012345678",
            ssn = null,
        )
    )

    override suspend fun getUserProfileImage(): Flow<String> = unused()
    override suspend fun fetchTaminRelation(): Flow<TaminRelationDN> = unused()
    override suspend fun sendImageRequest(branchCode: String, serialId: String): Flow<String> = unused()
    override suspend fun changeMobile(mobileNumber: String): Flow<EditMobileResponseDN> = unused()
    override suspend fun verifyChangeMobileCode(mobile: String, otp: String, otpHashCode: String): Flow<String> = unused()
    override suspend fun getSubDominantsInfo(filters: List<ApiFilterDN>): Flow<SubdominantDN> = unused()
    override suspend fun getBankAccountList(filters: List<ApiFilterDN>): Flow<List<BankAccountDN>> = unused()
    override suspend fun getInsuredActiveBranch(): Flow<List<InsuredActiveBranchDN>> = unused()
    override suspend fun getRelationTaminAll(filters: List<ApiFilterDN>): Flow<List<ActiveRelationDN>> = unused()
    override fun getElectronicFile(filters: List<ApiFilterDN>): Flow<List<ElectronicFileDN>> = unused()
    override suspend fun downloadDocument(url: String): PdfDownloadDN = unusedValue()
    override suspend fun getUserProfile(): Flow<UserProfileDN> = unused()
    override suspend fun getCurrentUser(): Flow<CurrentUserDN> = unused()
    override fun checkUserIsNew(nationalId: String): Flow<Boolean> = unused()
    override suspend fun registerBankAccount(
        accountNumber: String,
        bankCode: String,
        accountTypeCode: String,
        startDateMillis: Long,
    ): Flow<String?> = unused()
    override suspend fun getStatusCertificateReport(filters: List<ApiFilterDN>): Flow<String> = unused()
    override suspend fun getWageCertificateReport(filters: List<ApiFilterDN>): Flow<String> = unused()
    override suspend fun getRecipients(filters: List<ApiFilterDN>): Flow<List<RecipientDN>> = unused()

    private fun <T> unused(): Flow<T> = kotlinx.coroutines.flow.flow { error("not part of the identity load under test") }
    private fun <T> unusedValue(): T = error("not part of the identity load under test")
}
