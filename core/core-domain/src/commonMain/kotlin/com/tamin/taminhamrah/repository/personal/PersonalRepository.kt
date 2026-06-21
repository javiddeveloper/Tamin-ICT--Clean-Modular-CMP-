package com.tamin.taminhamrah.repository.personal

import com.tamin.taminhamrah.model.personal.AgeDN
import com.tamin.taminhamrah.model.personal.DisabilityDependentDN
import com.tamin.taminhamrah.model.personal.GirlSurvivorConditionDN
import com.tamin.taminhamrah.model.personal.PersonalInfoDN
import com.tamin.taminhamrah.model.personal.survivorList.ConfirmSurvivorDN
import com.tamin.taminhamrah.model.personal.SubmitFinalSurvivorPensionDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import kotlinx.coroutines.flow.Flow

interface PersonalRepository {
    fun getPersonalInfo(): Flow<PersonalInfoDN?>
    fun getAge(birthDate: Long): Flow<AgeDN>
    fun getDisabilityDependentInfo(filters: List<ApiFilterDN>): Flow<List<DisabilityDependentDN>>
    fun checkGirlSurvivorConditions(nationalCode: String, pensionerId: String): Flow<GirlSurvivorConditionDN>
    fun getConfirmSurvivorsList(filters: List<ApiFilterDN>): Flow<List<ConfirmSurvivorDN>>
    fun submitFinalSurvivorPension(requestId: Int, body: SubmitFinalSurvivorPensionDN): Flow<String?>
}
