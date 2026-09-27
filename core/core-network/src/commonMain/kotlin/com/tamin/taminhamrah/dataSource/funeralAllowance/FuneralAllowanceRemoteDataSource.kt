package com.tamin.taminhamrah.dataSource.funeralAllowance

import com.tamin.taminhamrah.model.funeralAllowance.DeceasedValidationDTO
import com.tamin.taminhamrah.model.funeralAllowance.FuneralAllowanceInfoDTO
import com.tamin.taminhamrah.model.funeralAllowance.FuneralAllowanceRequestDTO

interface FuneralAllowanceRemoteDataSource {

    suspend fun getFuneralAllowanceInfo(): FuneralAllowanceInfoDTO

    /**
     * Eligibility check for `shortterm/validateFuneral/{nationalCode}`. The backend's positional
     * string array is decoded into [DeceasedValidationDTO] here so no caller has to index it.
     */
    suspend fun validateDeceased(nationalCode: String): DeceasedValidationDTO

    /** Returns the backend's (already localized) success message; throws on any failure. */
    suspend fun submitFuneralAllowanceRequest(request: FuneralAllowanceRequestDTO): String

    /** Returns the backend's (already localized) success message; throws on any failure. */
    suspend fun confirmAccountCorrection(requestId: String): String
}
