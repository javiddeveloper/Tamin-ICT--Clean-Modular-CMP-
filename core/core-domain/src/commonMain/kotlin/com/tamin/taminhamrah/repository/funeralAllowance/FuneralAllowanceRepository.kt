package com.tamin.taminhamrah.repository.funeralAllowance

import com.tamin.taminhamrah.model.funeralAllowance.DeceasedValidationDN
import com.tamin.taminhamrah.model.funeralAllowance.FuneralAllowanceInfoDN
import com.tamin.taminhamrah.model.funeralAllowance.SubmitFuneralAllowanceParamsDN

interface FuneralAllowanceRepository {

    suspend fun getFuneralAllowanceInfo(): FuneralAllowanceInfoDN

    suspend fun validateDeceased(nationalCode: String): DeceasedValidationDN

    /** Returns the backend success message. */
    suspend fun submitFuneralAllowanceRequest(params: SubmitFuneralAllowanceParamsDN): String

    /** Re-submits a request rejected for a bank-account problem; returns the backend success message. */
    suspend fun confirmAccountCorrection(requestId: String): String
}
