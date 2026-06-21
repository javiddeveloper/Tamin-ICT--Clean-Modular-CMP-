package com.tamin.taminhamrah.repository

import com.tamin.taminhamrah.model.personal.AgeDN
import com.tamin.taminhamrah.model.personal.DisabilityDependentDN
import com.tamin.taminhamrah.model.personal.PersonalInfoDN
import com.tamin.taminhamrah.model.personal.SubmitFinalSurvivorPensionDN
import com.tamin.taminhamrah.model.personal.survivorList.ConfirmSurvivorDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.repository.personal.PersonalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakePersonalRepository : PersonalRepository {
    var personalInfoResult: PersonalInfoDN? = null
    var ageResult: AgeDN? = null
    var submitFinalSurvivorPensionResult: String? = null

    var shouldThrowError = false
    var disabilityDependentInfoResult: List<DisabilityDependentDN> = emptyList()
    var confirmSurvivorsListResult: List<ConfirmSurvivorDN> = emptyList()
    var error: Throwable = RuntimeException("Personal Repository Error")

    override fun getPersonalInfo(): Flow<PersonalInfoDN?> = flow {
        if (shouldThrowError) throw error
        emit(personalInfoResult)
    }

    override fun getAge(birthDate: Long): Flow<AgeDN> = flow {
        if (shouldThrowError) throw error
        ageResult?.let { emit(it) }
    }

    override fun getDisabilityDependentInfo(filters: List<ApiFilterDN>): Flow<List<DisabilityDependentDN>> =
        flow {
            if (shouldThrowError) throw error
            emit(disabilityDependentInfoResult)
        }

    override fun submitFinalSurvivorPension(
        requestId: Int,
        body: SubmitFinalSurvivorPensionDN
    ): Flow<String?> = flow {
        if (shouldThrowError) throw error
        emit(submitFinalSurvivorPensionResult)
    }

    override fun getConfirmSurvivorsList(filters: List<ApiFilterDN>): Flow<List<ConfirmSurvivorDN>> = flow {
        if (shouldThrowError) throw error
        emit(confirmSurvivorsListResult)
    }
}
