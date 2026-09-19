package com.tamin.taminhamrah.feature.workshops.ui.managementDebit

import app.cash.turbine.test
import com.tamin.taminhamrah.feature.workshops.fake.FakeWorkShopsRepository
import com.tamin.taminhamrah.feature.workshops.ui.model.ArticleSixteenDocumentTypes
import com.tamin.taminhamrah.feature.workshops.ui.model.WorkshopAttachmentUploader
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN
import com.tamin.taminhamrah.model.util.PagedListDN
import com.tamin.taminhamrah.model.workshop.ArticleSixteenDebtPR
import com.tamin.taminhamrah.model.workshop.ArticleSixteenRequestInfoDN
import com.tamin.taminhamrah.model.workshop.ArticleSixteenRequestStatus
import com.tamin.taminhamrah.model.workshop.ArticleSixteenSaveResultDN
import com.tamin.taminhamrah.model.workshop.ArticleSixteenWorkshopInfoDN
import com.tamin.taminhamrah.model.workshop.WorkshopsDebtListModelDN
import com.tamin.taminhamrah.ui.digitsOnly
import com.tamin.taminhamrah.tools.errorHandling.TaminApiException
import com.tamin.taminhamrah.util.PersianDateFormatter
import taminx.core.core_ui.Res
import taminx.core.core_ui.article_sixteen_deadline_passed
import taminx.core.core_ui.workshop_error_receive_data
import com.tamin.taminhamrah.useCases.workshops.GetArticleSixteenDebtsUseCase
import com.tamin.taminhamrah.useCases.workshops.GetArticleSixteenReportPdfUseCase
import com.tamin.taminhamrah.useCases.workshops.GetArticleSixteenRequestInfoUseCase
import com.tamin.taminhamrah.useCases.workshops.GetArticleSixteenWorkshopInfoUseCase
import com.tamin.taminhamrah.useCases.workshops.SaveArticleSixteenRequestUseCase
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
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ManagementDebitViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var repository: FakeWorkShopsRepository

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeWorkShopsRepository()
    }

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    private fun viewModel() = ManagementDebitViewModel(
        getArticleSixteenDebts = GetArticleSixteenDebtsUseCase(repository),
        getArticleSixteenRequestInfo = GetArticleSixteenRequestInfoUseCase(repository),
        getArticleSixteenReportPdf = GetArticleSixteenReportPdfUseCase(repository),
        getArticleSixteenWorkshopInfo = GetArticleSixteenWorkshopInfoUseCase(repository),
        uploadAttachment = WorkshopAttachmentUploader { "uploaded_guid" },
        saveArticleSixteenRequest = SaveArticleSixteenRequestUseCase(repository),
    )

    private fun open(viewModel: ManagementDebitViewModel) {
        viewModel.sendIntent(
            ManagementDebitIntent.Open(
                workshopId = "0968210170",
                branchCode = "0010",
                workshopName = "آموزشگاه کامپیوتر",
            )
        )
    }

    @Test
    fun `status filter narrows visible debts without reloading`() = runTest {
        val debt1 = WorkshopsDebtListModelDN(
            debitNumber = "1001",
            status = ArticleSixteenRequestStatus.NONE,
        )
        val debt2 = WorkshopsDebtListModelDN(
            debitNumber = "1002",
            status = ArticleSixteenRequestStatus.DOCUMENT_DEFECT,
            seqNo = 12L,
        )
        repository.articleSixteenDebts = PagedListDN(items = listOf(debt1, debt2))

        val vm = viewModel()
        open(vm)

        vm.uiState.test {
            val loaded = awaitItem()
            assertEquals(2, loaded.list.items.size)
            assertEquals(2, loaded.visibleDebts.size)

            // Filter to DOCUMENT_DEFECT
            vm.sendIntent(ManagementDebitIntent.StatusFilterChanged(ArticleSixteenRequestStatus.DOCUMENT_DEFECT))
            val filtered = awaitItem()
            assertEquals(ArticleSixteenRequestStatus.DOCUMENT_DEFECT, filtered.statusFilter)
            assertEquals(2, filtered.list.items.size)
            assertEquals(1, filtered.visibleDebts.size)
            assertEquals("1002", filtered.visibleDebts.first().debitNumber)

            // Clear filter
            vm.sendIntent(ManagementDebitIntent.StatusFilterChanged(null))
            val cleared = awaitItem()
            assertNull(cleared.statusFilter)
            assertEquals(2, cleared.visibleDebts.size)
        }
    }

    @Test
    fun `showing and dismissing expert message updates state`() = runTest {
        repository.articleSixteenRequestInfo = ArticleSixteenRequestInfoDN(
            defectDescription = "مدارک ارسالی ناخوانا است. لطفا تصویر با کیفیت ارسال کنید.",
        )
        val vm = viewModel()
        open(vm)

        val debt = ArticleSixteenDebtPR(
            debitNumber = "1002",
            status = ArticleSixteenRequestStatus.DOCUMENT_DEFECT,
            seqNo = 12L,
        )

        vm.uiState.test {
            awaitItem() // initial loaded

            vm.sendIntent(ManagementDebitIntent.ShowExpertMessage(debt))
            val withMessage = awaitItem()
            assertEquals("مدارک ارسالی ناخوانا است. لطفا تصویر با کیفیت ارسال کنید.", withMessage.expertMessage)

            vm.sendIntent(ManagementDebitIntent.DismissExpertMessage)
            val dismissed = awaitItem()
            assertNull(dismissed.expertMessage)
        }
    }

    @Test
    fun `showing and dismissing request pdf updates state`() = runTest {
        repository.pdf = PdfDownloadDN()
        val vm = viewModel()
        open(vm)

        val debt = ArticleSixteenDebtPR(
            debitNumber = "1003",
            status = ArticleSixteenRequestStatus.SUBMITTED,
            seqNo = 15L,
        )

        vm.uiState.test {
            awaitItem() // initial loaded

            vm.sendIntent(ManagementDebitIntent.ShowRequestPdf(debt))
            val withPdf = awaitItem()
            assertNotNull(withPdf.viewerPdf)

            vm.sendIntent(ManagementDebitIntent.DismissViewer)
            val dismissed = awaitItem()
            assertNull(dismissed.viewerPdf)
        }
    }

    @Test
    fun `a request a month after the executive notice opens the form`() = runTest {
        val vm = viewModel()
        open(vm)

        vm.sendIntent(ManagementDebitIntent.RequestReview(debtNotifiedDaysAgo(30)))

        // The design's «یک روز» would have refused this.
        val form = assertNotNull(vm.uiState.value.form)
        assertFalse(form.isResubmitNoticeOpen)
    }

    @Test
    fun `day 729 after the executive notice is still accepted, as the old app counts`() = runTest {
        val vm = viewModel()
        open(vm)

        // Whole years of 365 days, refused only once the count exceeds one.
        vm.sendIntent(ManagementDebitIntent.RequestReview(debtNotifiedDaysAgo(729)))

        assertNotNull(vm.uiState.value.form)
    }

    @Test
    fun `day 730 after the executive notice is refused`() = runTest {
        val vm = viewModel()
        open(vm)

        vm.events.test {
            vm.sendIntent(ManagementDebitIntent.RequestReview(debtNotifiedDaysAgo(730)))

            assertEquals(
                ManagementDebitEvent.ShowMessage(Res.string.article_sixteen_deadline_passed),
                awaitItem(),
            )
            cancelAndIgnoreRemainingEvents()
        }
        assertNull(vm.uiState.value.form)
    }

    @Test
    fun `a correction opens on the one-resubmission notice, past the deadline too`() = runTest {
        val vm = viewModel()
        open(vm)

        vm.sendIntent(ManagementDebitIntent.FixRequest(debtNotifiedDaysAgo(800)))
        assertTrue(assertNotNull(vm.uiState.value.form).isResubmitNoticeOpen)

        vm.sendIntent(ManagementDebitIntent.FormResubmitNoticeDismissed)
        assertFalse(assertNotNull(vm.uiState.value.form).isResubmitNoticeOpen)
    }

    @Test
    fun `an expert message with nothing written says so instead of opening empty`() = runTest {
        repository.articleSixteenRequestInfo = ArticleSixteenRequestInfoDN(defectDescription = " ")
        val vm = viewModel()
        open(vm)

        vm.events.test {
            vm.sendIntent(
                ManagementDebitIntent.ShowExpertMessage(
                    ArticleSixteenDebtPR(debitNumber = "1002", seqNo = 12L),
                ),
            )

            assertEquals(
                ManagementDebitEvent.ShowMessage(Res.string.workshop_error_receive_data),
                awaitItem(),
            )
            cancelAndIgnoreRemainingEvents()
        }
        assertNull(vm.uiState.value.expertMessage)
        assertFalse(vm.uiState.value.isBusy)
    }

    @Test
    fun `a filed request keeps its tracking code on screen until dismissed`() = runTest {
        repository.articleSixteenDebts = PagedListDN(
            items = listOf(
                WorkshopsDebtListModelDN(
                    debitNumber = "1001",
                    executiveNotifyDate = notifiedDaysAgo(30),
                ),
            ),
        )
        repository.articleSixteenSaveResult = ArticleSixteenSaveResultDN(referenceCode = "49271105")
        val vm = viewModel()
        open(vm)

        vm.sendIntent(ManagementDebitIntent.RequestReview(vm.uiState.value.list.items.first()))
        vm.sendIntent(ManagementDebitIntent.FormNext)
        vm.sendIntent(
            ManagementDebitIntent.FormAddDocument(
                fileName = "notice.jpg",
                bytes = byteArrayOf(1),
                typeCode = ArticleSixteenDocumentTypes.first().code,
            ),
        )
        vm.sendIntent(ManagementDebitIntent.FormConfirmedChanged(true))
        vm.sendIntent(ManagementDebitIntent.FormNext)

        assertNull(vm.uiState.value.form)
        assertEquals("49271105", vm.uiState.value.filedReferenceCode)

        vm.sendIntent(ManagementDebitIntent.DismissFiled)
        assertNull(vm.uiState.value.filedReferenceCode)
    }

    @Test
    fun `a request does not open without its workshop info, as in the old app`() = runTest {
        val vm = viewModel()
        open(vm)
        repository.error = TaminApiException(title = "کارگاه یافت نشد")

        vm.events.test {
            vm.sendIntent(ManagementDebitIntent.RequestReview(debtNotifiedDaysAgo(30)))

            assertEquals(ManagementDebitEvent.ShowServerMessage("کارگاه یافت نشد"), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        assertNull(vm.uiState.value.form)
        assertFalse(vm.uiState.value.isBusy)
    }

    @Test
    fun `the form opens on the workshop the service describes`() = runTest {
        repository.articleSixteenWorkshopInfo = ArticleSixteenWorkshopInfoDN(
            workshopId = "0968210170",
            branchCode = "0010",
            employerName = "حسین توکلی کرمانی",
        )
        val vm = viewModel()
        open(vm)

        vm.sendIntent(ManagementDebitIntent.RequestReview(debtNotifiedDaysAgo(30)))

        val info = assertNotNull(vm.uiState.value.form).workshopInfo
        assertEquals("۰۹۶۸۲۱۰۱۷۰", info.workshopId)
        assertEquals("۰۰۱۰", info.branchCode)
        assertEquals("حسین توکلی کرمانی", info.employerName)
    }

    @Test
    fun `a failed request pdf says why and leaves the list as it was`() = runTest {
        repository.articleSixteenDebts = PagedListDN(items = listOf(WorkshopsDebtListModelDN(debitNumber = "1003")))
        val vm = viewModel()
        open(vm)
        repository.error = TaminApiException(title = "فایل یافت نشد")

        vm.events.test {
            vm.sendIntent(
                ManagementDebitIntent.ShowRequestPdf(
                    ArticleSixteenDebtPR(debitNumber = "1003", seqNo = 15L),
                ),
            )

            // It used to go into the list's error state, which is not drawn while rows are up.
            assertEquals(ManagementDebitEvent.ShowServerMessage("فایل یافت نشد"), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        assertNull(vm.uiState.value.list.error)
        assertEquals(1, vm.uiState.value.list.items.size)
        assertFalse(vm.uiState.value.isBusy)
    }

    /** A debt with no request yet, whose ابلاغ اجراییه was [days] days before today. */
    private fun debtNotifiedDaysAgo(days: Int) = ArticleSixteenDebtPR(
        debitNumber = "1001",
        executiveNotifyDate = notifiedDaysAgo(days),
    )

    /** The date [days] days before today, spelt as the service sends it: `14050525`. */
    private fun notifiedDaysAgo(days: Int): String {
        val (year, month, day) = PersianDateFormatter.today()
        // Midday, so a clock change between the two dates cannot move it across midnight.
        val midday = PersianDateFormatter.toEpochMillis(year, month, day) + HOUR_MILLIS * 12
        return PersianDateFormatter.formatTimestamp(midday - DAY_MILLIS * days).digitsOnly()
    }

    private companion object {
        const val HOUR_MILLIS = 60 * 60 * 1000L
        const val DAY_MILLIS = 24 * HOUR_MILLIS
    }
}
