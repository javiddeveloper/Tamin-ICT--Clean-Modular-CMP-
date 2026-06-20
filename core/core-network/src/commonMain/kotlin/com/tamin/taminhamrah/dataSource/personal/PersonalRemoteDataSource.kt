package com.tamin.taminhamrah.dataSource.personal

import com.tamin.taminhamrah.model.personal.PersonalInfoDTO
import com.tamin.taminhamrah.model.personal.deceasedInfo.DeceasedInfoDTO

interface PersonalRemoteDataSource {
    suspend fun getPersonalInfo(): PersonalInfoDTO?
    suspend fun getDeceasedInfo(nationalId: String): DeceasedInfoDTO
}
