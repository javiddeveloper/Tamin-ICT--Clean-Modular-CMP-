package com.tamin.taminhamrah.apiService.contract

import com.tamin.taminhamrah.model.contracts.ContractDTO
import com.tamin.taminhamrah.model.contracts.RegistrationInfoDTO
import com.tamin.taminhamrah.model.utils.ListData
import com.tamin.taminhamrah.tools.BaseDTO
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.QueryMap

interface ContractsApiService {

    @GET("special-insured-services/list-contracts-mobile")
    suspend fun getContractList(
        @QueryMap parameters: Map<String, String>
    ): BaseDTO<ListData<ContractDTO>>

    @GET("special-insured-services/get-registration-info")
    suspend fun getRegistrationInfo(): BaseDTO<RegistrationInfoDTO>
}
