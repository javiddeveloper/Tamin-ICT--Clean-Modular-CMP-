package com.tamin.taminhamrah.dataSource.funeralAllowance

import com.tamin.taminhamrah.model.funeralAllowance.FuneralAllowanceInfoDTO
import com.tamin.taminhamrah.model.funeralAllowance.FuneralAllowanceRequestDTO

interface FuneralAllowanceRemoteDataSource {

    suspend fun getFuneralAllowanceInfo(): FuneralAllowanceInfoDTO

    /** Raw positional string array from `shortterm/validateFuneral/{nationalCode}`. */
    suspend fun validateDeceased(nationalCode: String): List<String?>

    /** Returns the backend's (already localized) success message; throws on any failure. */
    suspend fun submitFuneralAllowanceRequest(request: FuneralAllowanceRequestDTO): String

    /** Returns the backend's (already localized) success message; throws on any failure. */
    suspend fun confirmAccountCorrection(requestId: String): String
}
