package com.tamin.taminhamrah.feature.taminServices.funeralAllowance

import com.tamin.taminhamrah.model.funeralAllowance.DeceasedValidationDN
import com.tamin.taminhamrah.model.funeralAllowance.FuneralAllowanceInfoDN
import com.tamin.taminhamrah.model.funeralAllowance.SubmitFuneralAllowanceParamsDN
import com.tamin.taminhamrah.repository.funeralAllowance.FuneralAllowanceRepository

/**
 * Local [FuneralAllowanceRepository] fake for [FuneralAllowanceViewModel] tests. Cross-module test
 * source sets are not shared, so this mirrors core-domain's `FakeFuneralAllowanceRepository` rather
 * than importing it (same pattern as the occurrence ViewModel test's inline fakes).
 */
class FakeFuneralAllowanceRepository : FuneralAllowanceRepository {

    var shouldThrowError = false
    var error: Throwable = RuntimeException("Fake Funeral Allowance Repository Error")

    var infoResult: FuneralAllowanceInfoDN = FuneralAllowanceInfoDN(
        firstName = "علی",
        lastName = "رضایی",
        insuranceNumber = "1234567",
        bankAccount = "0203456789001",
        bankName = "بانک ملت",
        mobileNumber = "09121234567",
        branchName = "شعبه مرکزی",
        branchCode = "10",
        nationalCode = "0012345678",
        deceasedNationalId = "",
        requestHelpType = "07",
        hasBankAccountIssue = false,
        registeredRequest = null,
    )
    var validateResult: DeceasedValidationDN = DeceasedValidationDN(
        deceasedFullName = "زهرا رضایی",
        relationship = "همسر",
        isEligible = true,
        message = "دارای شرایط می‌باشید",
    )
    var submitResult: String = "درخواست شما ثبت شد"
    var confirmResult: String = "درخواست شما ثبت شد"

    var lastValidateNationalCode: String? = null
    var lastSubmitParams: SubmitFuneralAllowanceParamsDN? = null
    var lastConfirmRequestId: String? = null

    override suspend fun getFuneralAllowanceInfo(): FuneralAllowanceInfoDN {
        if (shouldThrowError) throw error
        return infoResult
    }

    override suspend fun validateDeceased(nationalCode: String): DeceasedValidationDN {
        lastValidateNationalCode = nationalCode
        if (shouldThrowError) throw error
        return validateResult
    }

    override suspend fun submitFuneralAllowanceRequest(params: SubmitFuneralAllowanceParamsDN): String {
        lastSubmitParams = params
        if (shouldThrowError) throw error
        return submitResult
    }

    override suspend fun confirmAccountCorrection(requestId: String): String {
        lastConfirmRequestId = requestId
        if (shouldThrowError) throw error
        return confirmResult
    }
}
