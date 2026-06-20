package com.tamin.taminhamrah.repository

import com.tamin.taminhamrah.model.personal.DisabilityDependentDN
import com.tamin.taminhamrah.model.personal.PersonalInfoDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.repository.personal.PersonalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakePersonalRepository : PersonalRepository {
    var shouldThrowError = false
    var error: Throwable = RuntimeException("Error")
    var personalInfoResult: PersonalInfoDN? = null
    var disabilityDependentInfoResult: List<DisabilityDependentDN> = emptyList()

    override fun getPersonalInfo(): Flow<PersonalInfoDN?> = flow {
        if (shouldThrowError) throw error
        emit(personalInfoResult)
    }

    override fun getDisabilityDependentInfo(filters: List<ApiFilterDN>): Flow<List<DisabilityDependentDN>> = flow {
        if (shouldThrowError) throw error
        emit(disabilityDependentInfoResult)
    }
}
