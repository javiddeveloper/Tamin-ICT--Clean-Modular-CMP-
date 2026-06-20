package com.tamin.taminhamrah.dataSource.personal

import com.tamin.taminhamrah.model.personal.PersonalInfoDTO
import com.tamin.taminhamrah.model.personal.disabilityRequest.DisabilityDependentDTO
import com.tamin.taminhamrah.model.request.ApiQueryParamDN

interface PersonalRemoteDataSource {
    suspend fun getPersonalInfo(): PersonalInfoDTO?
    suspend fun getDisabilityDependentInfo(query: ApiQueryParamDN): List<DisabilityDependentDTO>
}
