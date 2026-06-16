package com.tamin.taminhamrah.apiService.pension

import com.tamin.taminhamrah.model.pension.PensionIdDTO
import com.tamin.taminhamrah.model.pension.PensionInquiryDTO
import com.tamin.taminhamrah.model.utils.ListData
import com.tamin.taminhamrah.tools.BaseDTO
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.QueryMap


interface PensionApiService {

    @GET("pension-inquiry")
    suspend fun getPensionInquiry(
        @QueryMap parameters: Map<String, String>
    ) : BaseDTO<ListData<PensionInquiryDTO>>


    @GET("pensioner-no")
    suspend fun getPensionerId(): BaseDTO<ListData<PensionIdDTO>>


}
