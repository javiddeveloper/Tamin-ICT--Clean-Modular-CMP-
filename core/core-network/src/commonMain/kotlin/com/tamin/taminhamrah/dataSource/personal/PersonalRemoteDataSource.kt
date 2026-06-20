package com.tamin.taminhamrah.dataSource.personal

import com.tamin.taminhamrah.model.personal.PersonalInfoDTO
import com.tamin.taminhamrah.model.personal.age.AgeDTO

interface PersonalRemoteDataSource {
    suspend fun getPersonalInfo(): PersonalInfoDTO?
    suspend fun getAge(birthDate: Long): AgeDTO
}
