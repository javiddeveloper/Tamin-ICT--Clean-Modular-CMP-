package com.tamin.taminhamrah.dataSource.pension

import com.tamin.taminhamrah.model.pension.PensionIdDTO
import com.tamin.taminhamrah.model.pension.PensionInquiryDTO
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.utils.ListData


interface  PensionRemoteDataSource {
    suspend fun getPensionInquiry(query: ApiQueryParamDN) : ListData<PensionInquiryDTO>
    suspend fun getPensionerId(): List<PensionIdDTO>
}
