package com.tamin.taminhamrah.dataSource.personal

import com.tamin.taminhamrah.model.personal.PersonalInfoDTO

interface PersonalRemoteDataSource {
    suspend fun getPersonalInfo(): PersonalInfoDTO?
}
