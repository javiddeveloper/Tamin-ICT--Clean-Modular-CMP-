package com.tamin.taminhamrah.repository.personal

import com.tamin.taminhamrah.model.personal.PersonalInfoDN
import com.tamin.taminhamrah.model.personal.deceasedInfo.DeceasedInfoDN
import kotlinx.coroutines.flow.Flow

interface PersonalRepository {
    fun getPersonalInfo(): Flow<PersonalInfoDN?>
    fun getDeceasedInfo(nationalId: String): Flow<DeceasedInfoDN>
}
