package com.tamin.taminhamrah.repository

import com.tamin.taminhamrah.model.personal.AgeDN
import com.tamin.taminhamrah.model.personal.PersonalInfoDN
import com.tamin.taminhamrah.repository.personal.PersonalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakePersonalRepository : PersonalRepository {
    var personalInfoResult: PersonalInfoDN? = null
    var ageResult: AgeDN? = null

    var shouldThrowError = false
    var error: Throwable = RuntimeException("Personal Repository Error")

    override fun getPersonalInfo(): Flow<PersonalInfoDN?> = flow {
        if (shouldThrowError) throw error
        emit(personalInfoResult)
    }

    override fun getAge(birthDate: Long): Flow<AgeDN> = flow {
        if (shouldThrowError) throw error
        ageResult?.let { emit(it) }
    }
}
