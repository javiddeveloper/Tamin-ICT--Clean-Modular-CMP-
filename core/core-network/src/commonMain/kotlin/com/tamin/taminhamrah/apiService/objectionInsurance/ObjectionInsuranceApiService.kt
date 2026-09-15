package com.tamin.taminhamrah.apiService.objectionInsurance

import com.tamin.taminhamrah.model.objectionInsurance.ConfirmConflictItemDTO
import com.tamin.taminhamrah.model.objectionInsurance.ObjectionInsuranceHistoryDTO
import com.tamin.taminhamrah.model.utils.ListData
import com.tamin.taminhamrah.tools.BaseDTO
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.QueryMap

interface ObjectionInsuranceApiService {

    @GET("historyprotest-services/checkstatusconflict")
    suspend fun checkStatusConflict(): BaseDTO<Boolean>

    @GET("historyprotest-services/conflicthistories")
    suspend fun getConflictHistories(
        @QueryMap parameters: Map<String, String>,
    ): BaseDTO<ListData<ObjectionInsuranceHistoryDTO>>

    @POST("historyprotest-services/saveconflict")
    suspend fun saveConflict(
        @Body items: List<ObjectionInsuranceHistoryDTO>,
    ): BaseDTO<String?>

    @POST("historyprotest-services/confirmconflict")
    suspend fun confirmConflict(
        @Body items: List<ConfirmConflictItemDTO>,
    ): BaseDTO<Boolean>

    @POST("historyprotest-services/finalconfirmconflict")
    suspend fun finalConfirmConflict(
        @Body body: String = "",
    ): BaseDTO<String>
}
