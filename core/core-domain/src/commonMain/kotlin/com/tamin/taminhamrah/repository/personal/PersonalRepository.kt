package com.tamin.taminhamrah.repository.personal

import com.tamin.taminhamrah.model.personal.PersonalInfoDN

interface PersonalRepository {
    suspend fun getPersonalInfo(): PersonalInfoDN?
}
