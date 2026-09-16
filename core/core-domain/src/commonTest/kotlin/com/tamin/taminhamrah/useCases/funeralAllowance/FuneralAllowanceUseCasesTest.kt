package com.tamin.taminhamrah.useCases.funeralAllowance

import com.tamin.taminhamrah.model.funeralAllowance.DeceasedValidationDN
import com.tamin.taminhamrah.model.funeralAllowance.FuneralAllowanceInfoDN
import com.tamin.taminhamrah.model.funeralAllowance.SubmitFuneralAllowanceParamsDN
import com.tamin.taminhamrah.repository.funeralAllowance.FakeFuneralAllowanceRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame

/**
 * Every funeral-allowance use case is a one-line delegate to [com.tamin.taminhamrah.repository.funeralAllowance.FuneralAllowanceRepository],
 * so these tests pin exactly that: the argument is forwarded unchanged, the repository result is
 * returned unchanged, and a repository failure propagates rather than being swallowed.
 */
class FuneralAllowanceUseCasesTest : BaseUseCaseTest() {

    private lateinit var repository: FakeFuneralAllowanceRepository
    private lateinit var getInfoUseCase: GetFuneralAllowanceInfoUseCase
    private lateinit var validateDeceasedUseCase: ValidateDeceasedUseCase
    private lateinit var submitRequestUseCase: SubmitFuneralAllowanceRequestUseCase
    private lateinit var confirmCorrectionUseCase: ConfirmFuneralAccountCorrectionUseCase

    @BeforeTest
    fun setup() {
        repository = FakeFuneralAllowanceRepository()
        getInfoUseCase = GetFuneralAllowanceInfoUseCase(repository)
        validateDeceasedUseCase = ValidateDeceasedUseCase(repository)
        submitRequestUseCase = SubmitFuneralAllowanceRequestUseCase(repository)
        confirmCorrectionUseCase = ConfirmFuneralAccountCorrectionUseCase(repository)
    }

    @Test
    fun `GetFuneralAllowanceInfoUseCase returns the repository info`() = runTest {
        val expected = FuneralAllowanceInfoDN(
            firstName = "مریم", lastName = "کریمی", insuranceNumber = "9988776",
            bankAccount = "01110", bankName = "بانک رفاه", mobileNumber = "09120000000",
            branchName = "شعبه دو", branchCode = "20", nationalCode = "0011223344",
            deceasedNationalId = "0055667788", requestHelpType = "07",
            hasBankAccountIssue = false, registeredRequest = null,
        )
        repository.infoResult = expected

        val result = getInfoUseCase()

        assertEquals(expected, result)
    }

    @Test
    fun `ValidateDeceasedUseCase forwards the national code and returns the validation`() = runTest {
        val expected = DeceasedValidationDN(
            deceasedFullName = "زهرا رضایی", relationship = "همسر", isEligible = true,
            message = "",
        )
        repository.validateResult = expected

        val result = validateDeceasedUseCase("0055667788")

        assertEquals(expected, result)
        assertEquals("0055667788", repository.lastValidateNationalCode)
    }

    @Test
    fun `SubmitFuneralAllowanceRequestUseCase forwards params and returns the backend message`() = runTest {
        val params = SubmitFuneralAllowanceParamsDN(
            deceasedNationalId = "0055667788", branchCode = "10", branchName = "شعبه مرکزی",
            insuranceFirstName = "علی", insuranceLastName = "رضایی", mobileNumber = "09121234567",
            nationalCode = "0012345678", insuranceNumber = "1234567",
        )
        repository.submitResult = "درخواست با موفقیت ثبت شد"

        val result = submitRequestUseCase(params)

        assertEquals("درخواست با موفقیت ثبت شد", result)
        assertSame(params, repository.lastSubmitParams)
    }

    @Test
    fun `ConfirmFuneralAccountCorrectionUseCase forwards the request id and returns the backend message`() = runTest {
        repository.confirmResult = "درخواست مجدداً ثبت شد"

        val result = confirmCorrectionUseCase("987654")

        assertEquals("درخواست مجدداً ثبت شد", result)
        assertEquals("987654", repository.lastConfirmRequestId)
    }

    @Test
    fun `GetFuneralAllowanceInfoUseCase propagates a repository error`() = runTest {
        repository.shouldThrowError = true
        repository.error = RuntimeException("load failed")

        val error = assertFailsWith<RuntimeException> { getInfoUseCase() }

        assertEquals("load failed", error.message)
    }

    @Test
    fun `SubmitFuneralAllowanceRequestUseCase propagates a repository error`() = runTest {
        repository.shouldThrowError = true
        repository.error = RuntimeException("submit failed")

        val error = assertFailsWith<RuntimeException> {
            submitRequestUseCase(
                SubmitFuneralAllowanceParamsDN(
                    deceasedNationalId = "0055667788", branchCode = "10", branchName = "شعبه مرکزی",
                    insuranceFirstName = "علی", insuranceLastName = "رضایی", mobileNumber = "09121234567",
                    nationalCode = "0012345678", insuranceNumber = "1234567",
                )
            )
        }

        assertEquals("submit failed", error.message)
    }
}
