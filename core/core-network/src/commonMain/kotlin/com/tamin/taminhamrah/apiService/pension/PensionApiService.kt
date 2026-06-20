package com.tamin.taminhamrah.apiService.pension

import com.tamin.taminhamrah.model.pension.EdictPensionerDTO
import com.tamin.taminhamrah.model.pension.installment.DeferredInstallmentRequest
import com.tamin.taminhamrah.model.pension.PensionIdDTO
import com.tamin.taminhamrah.model.pension.PensionInquiryDTO
import com.tamin.taminhamrah.model.pension.installment.DeferredInstallmentCertificateDTO
import com.tamin.taminhamrah.model.utils.ListData
import com.tamin.taminhamrah.tools.BaseDTO
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.QueryMap


interface PensionApiService {

    @GET("pension-inquiry")
    suspend fun getPensionInquiry(
        @QueryMap parameters: Map<String, String>
    ) : BaseDTO<ListData<PensionInquiryDTO>>


    @GET("pensioner-no")
    suspend fun getPensionerId(): BaseDTO<ListData<PensionIdDTO>>


    @GET("hokm")
    suspend fun getEdictPensioner(
        @QueryMap parameters: Map<String, String>
    ): BaseDTO<EdictPensionerDTO?>?

    @POST("wage-assignment")
    suspend fun sendRequestDeferredInstallmentCertificate(
        @Body deferredInstallmentRequest: DeferredInstallmentRequest
    ): BaseDTO<DeferredInstallmentCertificateDTO>

}
