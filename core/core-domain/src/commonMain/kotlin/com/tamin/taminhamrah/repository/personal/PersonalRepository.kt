package com.tamin.taminhamrah.repository.personal

import com.tamin.taminhamrah.model.personal.AgeDN
import com.tamin.taminhamrah.model.personal.PersonalInfoDN
import kotlinx.coroutines.flow.Flow

interface PersonalRepository {
    fun getPersonalInfo(): Flow<PersonalInfoDN?>
    fun getAge(birthDate: Long): Flow<AgeDN>
}
