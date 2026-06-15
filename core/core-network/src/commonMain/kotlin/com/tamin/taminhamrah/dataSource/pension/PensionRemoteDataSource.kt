package com.tamin.taminhamrah.dataSource.pension

import com.tamin.taminhamrah.model.pension.PensionInquiryResponse
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.utils.ListData


interface  PensionRemoteDataSource {
    suspend fun getPensionInquiry(pensionInquiryDN: ApiQueryParamDN) : ListData<PensionInquiryResponse>
}
