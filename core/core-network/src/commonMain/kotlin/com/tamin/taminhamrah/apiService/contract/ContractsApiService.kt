package com.tamin.taminhamrah.apiService.contract

import com.tamin.taminhamrah.model.contracts.BranchDTO
import com.tamin.taminhamrah.model.contracts.ContractDTO
import com.tamin.taminhamrah.model.contracts.FreelancePremiumRangeDTO
import com.tamin.taminhamrah.model.contracts.PremiumRateDTO
import com.tamin.taminhamrah.model.contracts.RegistrationInfoDTO
import com.tamin.taminhamrah.model.utils.ListData
import com.tamin.taminhamrah.tools.BaseDTO
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.QueryMap

interface ContractsApiService {

    @GET("special-insured-services/list-contracts-mobile")
    suspend fun getContractList(
        @QueryMap parameters: Map<String, String>
    ): BaseDTO<ListData<ContractDTO>>

    @GET("special-insured-services/get-registration-info")
    suspend fun getRegistrationInfo(): BaseDTO<RegistrationInfoDTO>

    @GET("special-insured-services/branches")
    suspend fun getBranches(
        @QueryMap parameters: Map<String, String>,
    ): BaseDTO<ListData<BranchDTO>>

    @GET("baseinfo/spc-premium-rate")
    suspend fun getSpcPremiumRates(): BaseDTO<ListData<PremiumRateDTO>>

    @GET("special-insured-services/freelance-get-low-high-premium/{treatmentSupportCode}/{spcRateCode}/{insuranceId}")
    suspend fun getFreelancePremiumRange(
        @Path("treatmentSupportCode") treatmentSupportCode: String,
        @Path("spcRateCode") spcRateCode: String,
        @Path("insuranceId") insuranceId: String,
    ): BaseDTO<FreelancePremiumRangeDTO>
}
