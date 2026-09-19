package com.tamin.taminhamrah.feature.workshops.ui.legalRepresentative.otp

import app.cash.turbine.ReceiveTurbine
import app.cash.turbine.test
import com.tamin.taminhamrah.feature.workshops.fake.FakeWorkShopsRepository
import com.tamin.taminhamrah.tools.errorHandling.TaminApiException
import com.tamin.taminhamrah.useCases.workshops.RequestLegalRepresentativeTicketUseCase
import com.tamin.taminhamrah.useCases.workshops.VerifyLegalRepresentativeTicketUseCase
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
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * This flow verifies the *signed-in user* before the representative list is shown — a different
 * call shape than [AddLegalRepresentativeViewModelTest], which verifies the representative being
 * added. Both go through `RequestLegalRepresentativeTicketUseCase`'s optional-national-code
 * parameter; here it must stay unset.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class LegalRepresentativeOtpViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var repository: FakeWorkShopsRepository

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeWorkShopsRepository()
    }

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    private fun viewModel() = LegalRepresentativeOtpViewModel(
        requestLegalRepresentativeTicketUseCase = RequestLegalRepresentativeTicketUseCase(repository),
        verifyLegalRepresentativeTicketUseCase = VerifyLegalRepresentativeTicketUseCase(repository),
    )

    @Test
    fun `requesting a ticket verifies the signed-in user, not a representative`() = runTest(testDispatcher) {
        val viewModel = viewModel()

        viewModel.uiState.test {
            awaitItem()
            viewModel.sendIntent(LegalRepresentativeOtpIntent.RequestTicket)
            val requested = awaitUntil { it.isTicketRequested }
            assertTrue(requested.isTicketRequested)
            assertFalse(requested.isRequestingTicket)
            cancelAndIgnoreRemainingEvents()
        }

        assertEquals(1, repository.requestTicketCallCount)
        assertNull(repository.lastRequestTicketNationalCode)
    }

    @Test
    fun `a failed ticket request surfaces the service's own wording`() = runTest(testDispatcher) {
        repository.error = TaminApiException(title = "خطا")
        val viewModel = viewModel()

        viewModel.uiState.test {
            awaitItem()
            viewModel.sendIntent(LegalRepresentativeOtpIntent.RequestTicket)
            val failed = awaitUntil { it.error != null }
            assertEquals("خطا", failed.error)
            assertFalse(failed.isRequestingTicket)
            assertFalse(failed.isTicketRequested)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `verifying sends the typed code and emits a success event`() = runTest(testDispatcher) {
        val viewModel = viewModel()

        viewModel.uiState.test {
            awaitItem()
            viewModel.sendIntent(LegalRepresentativeOtpIntent.OtpChanged("123456"))
            awaitUntil { it.otpCode == "123456" }
            cancelAndIgnoreRemainingEvents()
        }

        viewModel.events.test {
            viewModel.sendIntent(LegalRepresentativeOtpIntent.VerifyTicket)
            val event = awaitItem()
            assertTrue(event is LegalRepresentativeOtpEvent.VerifiedSuccessfully)
            assertEquals("123456", (event as LegalRepresentativeOtpEvent.VerifiedSuccessfully).ticket)
            cancelAndIgnoreRemainingEvents()
        }

        assertEquals("123456", repository.lastVerifiedTicket)
    }

    @Test
    fun `a failed verification is reported without a success event`() = runTest(testDispatcher) {
        repository.error = TaminApiException(title = "کد اعتباری نامعتبر است")
        val viewModel = viewModel()

        viewModel.uiState.test {
            awaitItem()
            viewModel.sendIntent(LegalRepresentativeOtpIntent.OtpChanged("000000"))
            awaitUntil { it.otpCode == "000000" }

            viewModel.sendIntent(LegalRepresentativeOtpIntent.VerifyTicket)
            val failed = awaitUntil { it.error != null }
            assertEquals("کد اعتباری نامعتبر است", failed.error)
            assertFalse(failed.isVerifying)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `expiry clears the code and drops back to not-requested`() = runTest(testDispatcher) {
        val viewModel = viewModel()

        viewModel.uiState.test {
            awaitItem()
            viewModel.sendIntent(LegalRepresentativeOtpIntent.RequestTicket)
            awaitUntil { it.isTicketRequested }
            viewModel.sendIntent(LegalRepresentativeOtpIntent.OtpChanged("111111"))
            awaitUntil { it.otpCode == "111111" }

            viewModel.sendIntent(LegalRepresentativeOtpIntent.OtpExpired)
            val expired = awaitUntil { it.isExpired }
            assertFalse(expired.isTicketRequested)
            assertEquals("", expired.otpCode)

            viewModel.sendIntent(LegalRepresentativeOtpIntent.DismissExpiredDialog)
            assertFalse(awaitUntil { !it.isExpired }.isExpired)
            cancelAndIgnoreRemainingEvents()
        }
    }

    private suspend fun ReceiveTurbine<LegalRepresentativeOtpUiState>.awaitUntil(
        predicate: (LegalRepresentativeOtpUiState) -> Boolean,
    ): LegalRepresentativeOtpUiState {
        while (true) {
            val item = awaitItem()
            if (predicate(item)) return item
        }
    }
}
