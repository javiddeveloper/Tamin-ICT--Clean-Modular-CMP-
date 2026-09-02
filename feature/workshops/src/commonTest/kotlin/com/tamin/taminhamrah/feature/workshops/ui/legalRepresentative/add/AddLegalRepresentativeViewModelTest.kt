package com.tamin.taminhamrah.feature.workshops.ui.legalRepresentative.add

import app.cash.turbine.ReceiveTurbine
import app.cash.turbine.test
import com.tamin.taminhamrah.feature.workshops.fake.FakeWorkShopsRepository
import com.tamin.taminhamrah.model.legalRepresentative.LegalRepresentativeDN
import com.tamin.taminhamrah.tools.errorHandling.TaminApiException
import com.tamin.taminhamrah.useCases.workshops.GetLegalRepresentativeWorkshopContractsUseCase
import com.tamin.taminhamrah.useCases.workshops.RequestLegalRepresentativeTicketUseCase
import com.tamin.taminhamrah.useCases.workshops.SubmitLegalRepresentativeUseCase
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
import kotlin.test.assertTrue

/**
 * The two things this ViewModel must get right: the `accessCode` bitmask it builds for the
 * backend has to decode back to the same permissions on the domain side, and the two submit
 * gates (`canRequestTicket`/`canSubmit`) must actually stop a request from reaching the service
 * rather than just disabling a button that a retried intent could route around.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class AddLegalRepresentativeViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var repository: FakeWorkShopsRepository

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeWorkShopsRepository()
    }

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    private fun viewModel() = AddLegalRepresentativeViewModel(
        requestLegalRepresentativeTicketUseCase = RequestLegalRepresentativeTicketUseCase(repository),
        submitLegalRepresentativeUseCase = SubmitLegalRepresentativeUseCase(repository),
        getLegalRepresentativeWorkshopContractsUseCase = GetLegalRepresentativeWorkshopContractsUseCase(repository),
    )

    // ---------------------------------------------------------------- accessCode bitmask

    @Test
    fun `the access code preview decodes back to the same permissions on the domain side`() = runTest(testDispatcher) {
        val viewModel = viewModel()

        viewModel.uiState.test {
            awaitItem()
            viewModel.sendIntent(AddLegalRepresentativeIntent.ElectronicNotificationChanged(true))
            awaitUntil { it.hasElectronicNotification }
            viewModel.sendIntent(AddLegalRepresentativeIntent.InsuredRegistrationChanged(true))
            val state = awaitUntil { it.hasInsuredRegistration }

            assertEquals("10100000", state.accessCodePreview)

            val decoded = LegalRepresentativeDN(
                stakeId = 1L,
                nationalId = "1",
                accessCode = state.accessCodePreview,
                workshopId = "w",
                branchCode = "b",
            )
            assertTrue(decoded.hasElectronicNotification)
            assertFalse(decoded.hasInternetList)
            assertTrue(decoded.hasInsuredRegistration)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ----------------------------------------------------------- canRequestTicket gating

    @Test
    fun `requesting a ticket with an incomplete national code never reaches the service`() = runTest(testDispatcher) {
        val viewModel = viewModel()

        viewModel.uiState.test {
            awaitItem()
            viewModel.sendIntent(AddLegalRepresentativeIntent.NationalCodeChanged("123"))
            val partial = awaitUntil { it.nationalCode == "123" }
            assertFalse(partial.canRequestTicket)

            viewModel.sendIntent(AddLegalRepresentativeIntent.RequestTicket)
            val refused = awaitUntil { it.nationalCodeError != null }
            assertNotNull(refused.nationalCodeError)
            cancelAndIgnoreRemainingEvents()
        }

        assertEquals(0, repository.requestTicketCallCount)
    }

    @Test
    fun `requesting a ticket with a failed checksum never reaches the service`() = runTest(testDispatcher) {
        val viewModel = viewModel()

        viewModel.uiState.test {
            awaitItem()
            // Ten identical digits satisfy the checksum arithmetic but are never issued.
            viewModel.sendIntent(AddLegalRepresentativeIntent.NationalCodeChanged("1111111111"))
            val invalid = awaitUntil { it.nationalCode.length == 10 }
            assertFalse(invalid.canRequestTicket)
            assertNotNull(invalid.nationalCodeError)
            cancelAndIgnoreRemainingEvents()
        }

        assertEquals(0, repository.requestTicketCallCount)
    }

    @Test
    fun `a valid national code requests a ticket addressed to that representative`() = runTest(testDispatcher) {
        val viewModel = viewModel()

        viewModel.uiState.test {
            awaitItem()
            viewModel.sendIntent(AddLegalRepresentativeIntent.NationalCodeChanged(VALID_NATIONAL_CODE))
            awaitUntil { it.nationalCode.length == 10 }

            viewModel.sendIntent(AddLegalRepresentativeIntent.RequestTicket)
            val requested = awaitUntil { it.isTicketRequested }
            assertTrue(requested.isTicketRequested)
            cancelAndIgnoreRemainingEvents()
        }

        assertEquals(VALID_NATIONAL_CODE, repository.lastRequestTicketNationalCode)
    }

    // ------------------------------------------------------------------- canSubmit gating

    @Test
    fun `submit is gated on a complete national code and a typed otp`() = runTest(testDispatcher) {
        val viewModel = viewModel()
        assertFalse(viewModel.uiState.value.canSubmit)

        viewModel.uiState.test {
            awaitItem()
            viewModel.sendIntent(AddLegalRepresentativeIntent.NationalCodeChanged(VALID_NATIONAL_CODE))
            val withCode = awaitUntil { it.nationalCode.length == 10 }
            assertFalse(withCode.canSubmit)

            viewModel.sendIntent(AddLegalRepresentativeIntent.OtpChanged("999999"))
            val withOtp = awaitUntil { it.otpCode.isNotBlank() }
            assertTrue(withOtp.canSubmit)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ------------------------------------------------------------------ contract toggling

    @Test
    fun `toggling a contract row adds it once and removes it on a second toggle`() = runTest(testDispatcher) {
        val viewModel = viewModel()

        viewModel.uiState.test {
            awaitItem()
            viewModel.sendIntent(AddLegalRepresentativeIntent.ToggleContractRow("row-1"))
            val added = awaitUntil { it.selectedContractRows.contains("row-1") }
            assertEquals(listOf("row-1"), added.selectedContractRows)

            viewModel.sendIntent(AddLegalRepresentativeIntent.ToggleContractRow("row-1"))
            val removed = awaitUntil { !it.selectedContractRows.contains("row-1") }
            assertTrue(removed.selectedContractRows.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ------------------------------------------------------------------------------ submit

    @Test
    fun `submitting sends the otp as the ticket and the full request to the service`() = runTest(testDispatcher) {
        val viewModel = viewModel()

        viewModel.uiState.test {
            awaitItem()
            viewModel.sendIntent(AddLegalRepresentativeIntent.NationalCodeChanged(VALID_NATIONAL_CODE))
            awaitUntil { it.nationalCode.length == 10 }
            viewModel.sendIntent(AddLegalRepresentativeIntent.OtpChanged("654321"))
            awaitUntil { it.otpCode == "654321" }
            viewModel.sendIntent(AddLegalRepresentativeIntent.ElectronicNotificationChanged(true))
            awaitUntil { it.hasElectronicNotification }
            viewModel.sendIntent(AddLegalRepresentativeIntent.ToggleContractRow("row-9"))
            awaitUntil { it.selectedContractRows.contains("row-9") }

            viewModel.sendIntent(AddLegalRepresentativeIntent.Submit)
            val submitted = awaitUntil { it.isSuccess }
            assertTrue(submitted.isSuccess)
            cancelAndIgnoreRemainingEvents()
        }

        assertEquals("654321", repository.lastSubmittedTicket)
        val request = assertNotNull(repository.lastSubmittedRequest)
        assertEquals(VALID_NATIONAL_CODE, request.nationalCode)
        assertTrue(request.hasElectronicNotification)
        assertEquals(listOf("row-9"), request.contractRows)
    }

    @Test
    fun `a refused submit keeps the service's own wording and re-enables the form`() = runTest(testDispatcher) {
        repository.error = TaminApiException(title = "خطای ثبت")
        val viewModel = viewModel()

        viewModel.uiState.test {
            awaitItem()
            viewModel.sendIntent(AddLegalRepresentativeIntent.NationalCodeChanged(VALID_NATIONAL_CODE))
            awaitUntil { it.nationalCode.length == 10 }
            viewModel.sendIntent(AddLegalRepresentativeIntent.OtpChanged("654321"))
            awaitUntil { it.otpCode == "654321" }

            viewModel.sendIntent(AddLegalRepresentativeIntent.Submit)
            val failed = awaitUntil { it.error != null }
            assertEquals("خطای ثبت", failed.error)
            assertFalse(failed.isSubmitting)
            assertFalse(failed.isSuccess)
            cancelAndIgnoreRemainingEvents()
        }
    }

    private suspend fun ReceiveTurbine<AddLegalRepresentativeUiState>.awaitUntil(
        predicate: (AddLegalRepresentativeUiState) -> Boolean,
    ): AddLegalRepresentativeUiState {
        while (true) {
            val item = awaitItem()
            if (predicate(item)) return item
        }
    }

    private companion object {
        /** Passes the checksum in `isValidIranianNationalId` — see `NationalIdTest`. */
        const val VALID_NATIONAL_CODE = "0499370899"
    }
}
