package com.tamin.taminhamrah.feature.workshops.ui.objectionableDebit

import app.cash.turbine.test
import com.tamin.taminhamrah.feature.workshops.fake.FakeWorkShopsRepository
import com.tamin.taminhamrah.feature.workshops.ui.model.WorkshopAttachmentUploader
import com.tamin.taminhamrah.model.util.PagedListDN
import com.tamin.taminhamrah.model.workshop.WorkShopDebtDN
import com.tamin.taminhamrah.useCases.workshops.CheckObjectionDeadlineUseCase
import com.tamin.taminhamrah.useCases.workshops.GetDebitObjectionPdfUseCase
import com.tamin.taminhamrah.useCases.workshops.GetObjectionableDebitsUseCase
import com.tamin.taminhamrah.repository.contracts.ContractsRepository
import com.tamin.taminhamrah.model.contracts.BranchDN
import com.tamin.taminhamrah.model.contracts.FreelanceContractByGuardianParams
import com.tamin.taminhamrah.model.contracts.OptionalContractByGuardianParams
import com.tamin.taminhamrah.model.contracts.ContractDN
import com.tamin.taminhamrah.model.contracts.FreelanceCalculateSalaryParams
import com.tamin.taminhamrah.model.contracts.FreelanceContractResultDN
import com.tamin.taminhamrah.model.contracts.FreelanceMakeContractParams
import com.tamin.taminhamrah.model.contracts.FreelancePremiumRangeDN
import com.tamin.taminhamrah.model.contracts.FreelancePremiumRangeParams
import com.tamin.taminhamrah.model.contracts.FreeJobDN
import com.tamin.taminhamrah.model.contracts.InsurancePaymentDN
import com.tamin.taminhamrah.model.contracts.InsurancePaymentParamsDN
import com.tamin.taminhamrah.model.contracts.PremiumRateDN
import com.tamin.taminhamrah.model.contracts.RegistrationInfoDN
import com.tamin.taminhamrah.model.contracts.SaveContactRequestDN
import com.tamin.taminhamrah.model.contracts.UploadImageRequestDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import kotlinx.coroutines.flow.Flow
import com.tamin.taminhamrah.useCases.contracts.UploadImageUseCase
import com.tamin.taminhamrah.useCases.workshops.SaveDebitObjectionUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
import kotlin.test.assertTrue

/**
 * اعتراض به بدهی — one list, one action per row, and the action is not the same for every row.
 *
 * What is pinned here is that branch. A debt already objected to opens its filed objection; a debt
 * still open to objection is checked against its deadline first, and only a debt inside the window
 * reaches the form. Getting that wrong either hides a filing window that is still open or invites
 * someone to fill in a form the service will refuse.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ObjectionableDebitViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var repository: FakeWorkShopsRepository

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeWorkShopsRepository()
    }

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    private fun viewModel() = ObjectionableDebitViewModel(
        GetObjectionableDebitsUseCase(repository),
        CheckObjectionDeadlineUseCase(repository),
        GetDebitObjectionPdfUseCase(repository),
        WorkshopAttachmentUploader(UploadImageUseCase(NoUploads)),
        SaveDebitObjectionUseCase(repository),
    )

    private fun open(viewModel: ObjectionableDebitViewModel) =
        viewModel.sendIntent(ObjectionableDebitIntent.Open(WORKSHOP_ID, BRANCH_CODE))

    @Test
    fun `opening loads the objectionable debts of that workshop`() = runTest(testDispatcher) {
        repository.objectionableDebits = debts(count = 2)

        val viewModel = viewModel()
        open(viewModel)

        assertEquals(2, viewModel.uiState.value.list.items.size)
        assertEquals(WORKSHOP_ID, viewModel.uiState.value.workshopId)
    }

    @Test
    fun `reopening the same workshop does not refetch`() = runTest(testDispatcher) {
        repository.objectionableDebits = debts(count = 1)

        val viewModel = viewModel()
        open(viewModel)
        repository.objectionableDebits = debts(count = 5)
        open(viewModel)

        assertEquals(1, viewModel.uiState.value.list.items.size)
    }

    /**
     * A debt whose objection was already filed opens that objection rather than a new form — the
     * row's single action means different things depending on the debt.
     */
    @Test
    fun `a debt already objected to opens its filed objection`() = runTest(testDispatcher) {
        repository.objectionableDebits = PagedListDN(items = listOf(filedDebt()), total = 1)

        val viewModel = viewModel()
        open(viewModel)
        viewModel.sendIntent(
            ObjectionableDebitIntent.RowAction(viewModel.uiState.value.list.items[0])
        )

        assertNotNull(viewModel.uiState.value.viewerPdf, "the filed objection should be shown")
        assertNull(viewModel.uiState.value.form, "no new form is offered for a filed objection")
    }

    /** Without a sequence number there is no objection to fetch, so it says so instead of failing. */
    @Test
    fun `a filed objection with no sequence number is reported`() = runTest(testDispatcher) {
        repository.objectionableDebits =
            PagedListDN(items = listOf(filedDebt(seqNo = null)), total = 1)

        val viewModel = viewModel()
        open(viewModel)

        viewModel.events.test {
            viewModel.sendIntent(
                ObjectionableDebitIntent.RowAction(viewModel.uiState.value.list.items[0])
            )
            assertTrue(awaitItem() is ObjectionableDebitEvent.ShowMessage)
        }
        assertNull(viewModel.uiState.value.viewerPdf)
    }

    /** Inside the filing window, the row opens the form. */
    @Test
    fun `an objectionable debt still in time opens the form`() = runTest(testDispatcher) {
        repository.objectionableDebits = PagedListDN(items = listOf(estimateDebt()), total = 1)
        repository.objectionElapsedDays = WITHIN_WINDOW

        val viewModel = viewModel()
        open(viewModel)
        viewModel.sendIntent(
            ObjectionableDebitIntent.RowAction(viewModel.uiState.value.list.items[0])
        )

        assertNotNull(viewModel.uiState.value.form, "a debt inside its window should reach the form")
        assertNull(viewModel.uiState.value.checkingDebitNumber, "the row must stop showing progress")
    }

    /**
     * Past the window the form must not open at all — letting someone fill it in only to have the
     * service refuse it is the failure this check exists to prevent.
     */
    @Test
    fun `an objectionable debt out of time is refused before the form`() = runTest(testDispatcher) {
        repository.objectionableDebits = PagedListDN(items = listOf(estimateDebt()), total = 1)
        repository.objectionElapsedDays = PAST_WINDOW

        val viewModel = viewModel()
        open(viewModel)

        viewModel.events.test {
            viewModel.sendIntent(
                ObjectionableDebitIntent.RowAction(viewModel.uiState.value.list.items[0])
            )
            assertTrue(awaitItem() is ObjectionableDebitEvent.ShowMessage)
        }
        assertNull(viewModel.uiState.value.form, "no form for a debt past its filing window")
    }

    private fun debts(count: Int) = PagedListDN(
        items = List(count) { estimateDebt(debitNumber = "$RAW_DEBIT_NUMBER$it") },
        total = count,
    )

    /** برآوردی and objectionable — the row that leads to the form. */
    private fun estimateDebt(debitNumber: String = RAW_DEBIT_NUMBER) = WorkShopDebtDN(
        debitNumber = debitNumber,
        orderRecipeDate = "14050525",
        debitStepCode = STEP_ESTIMATE,
        debitStatCode = STAT_OBJECTIONABLE,
        debitAmount = 14_203_311L,
        debitRemain = 14_203_311L,
    )

    /** Already objected to: [WorkShopDebtDN.seqNo] is what makes the kind FILED. */
    private fun filedDebt(seqNo: Long? = 42L) = WorkShopDebtDN(
        debitNumber = RAW_DEBIT_NUMBER,
        orderRecipeDate = "14050525",
        seqNo = seqNo,
    )

    private companion object {
        const val WORKSHOP_ID = "9028218513"
        const val BRANCH_CODE = "14"
        const val RAW_DEBIT_NUMBER = "6310030089235"

        /** The codes [WorkShopDebtDN.objectionKind] reads to decide a row is still objectionable. */
        const val STEP_ESTIMATE = "01"
        const val STAT_OBJECTIONABLE = "03"

        /** ESTIMATE allows 31 days, so these sit either side of it. */
        const val WITHIN_WINDOW = 10
        const val PAST_WINDOW = 99
    }
}

/**
 * The uploader's dependency, present only because the view model takes one.
 *
 * None of these tests attaches a file, so every call fails loudly rather than returning a silent
 * default — a test that starts uploading by accident should say so rather than quietly pass.
 */
private object NoUploads : ContractsRepository {
    private fun notUsed(): Nothing =
        error("not used by ObjectionableDebitViewModel tests")

    override fun getContracts(query: ApiQueryParamDN?): Flow<List<ContractDN>> = notUsed()
    override fun getContractsByPremiumType(premiumTypeCode: String): Flow<List<ContractDN>> = notUsed()
    override fun getStudentInsuranceContracts(): Flow<List<ContractDN>> = notUsed()
    override fun getRegistrationInfo(): Flow<RegistrationInfoDN> = notUsed()
    override fun getBranches(cityCode: String): Flow<List<BranchDN>> = notUsed()
    override fun getSpcPremiumRates(): Flow<List<PremiumRateDN>> = notUsed()
    override fun getFreeJobWages(): Flow<List<FreeJobDN>> = notUsed()
    override fun getFreelancePremiumRange(params: FreelancePremiumRangeParams): Flow<FreelancePremiumRangeDN> = notUsed()
    override fun getOptionalPremiumRange(): Flow<FreelancePremiumRangeDN> = notUsed()
    override fun checkRedCrossStatus(): Flow<String> = notUsed()
    override fun checkMedicalStudent(): Flow<String> = notUsed()
    override fun calculateFreelanceSalary(params: FreelanceCalculateSalaryParams): Flow<Long> = notUsed()
    override fun calculateOptionalSalary(premiumRateCode: String): Flow<Long> = notUsed()
    override fun makeFreelanceContract(params: FreelanceMakeContractParams): Flow<FreelanceContractResultDN> = notUsed()
    override fun makeContract(params: FreelanceMakeContractParams): Flow<FreelanceContractResultDN> = notUsed()
    override fun makeFreelanceContractByGuardian(params: FreelanceContractByGuardianParams): Flow<FreelanceContractResultDN> = notUsed()
    override fun makeOptionalContractByGuardian(params: OptionalContractByGuardianParams): Flow<FreelanceContractResultDN> = notUsed()
    override fun getInsurancePayment(params: InsurancePaymentParamsDN): Flow<InsurancePaymentDN> = notUsed()
    override fun checkInsurancePaymentStatus(systemType: String): Flow<Any?> = notUsed()
    override fun uploadImage(request: UploadImageRequestDN): Flow<String> = notUsed()
    override fun saveContact(request: SaveContactRequestDN): Flow<Any?> = notUsed()
}
