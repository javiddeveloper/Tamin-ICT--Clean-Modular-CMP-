package com.tamin.taminhamrah.data.remote.common

import com.tamin.taminhamrah.data.entity.PensionInquiryModel
import com.tamin.taminhamrah.data.remote.models.Resource
import com.tamin.taminhamrah.data.remote.models.services.RecipientResponse

interface CommonRemoteDataSource {

    // suspend fun getServices(token: String): Resource<ServicesResponse?>

    suspend fun getPensionInquiry(token: String): Resource<List<PensionInquiryModel>?>
    suspend fun getRecipientList(
        token: String,
        paramsMap: MutableMap<String, String>?
    ): RecipientResponse

}

