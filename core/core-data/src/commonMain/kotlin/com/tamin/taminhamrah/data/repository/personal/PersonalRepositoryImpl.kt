package com.tamin.taminhamrah.data.repository.personal

import com.tamin.taminhamrah.data.mapper.toDomain
import com.tamin.taminhamrah.dataSource.personal.PersonalRemoteDataSource
import com.tamin.taminhamrah.model.personal.PersonalInfoDN
import com.tamin.taminhamrah.repository.personal.PersonalRepository

class PersonalRepositoryImpl(
    private val personalRemoteDataSource: PersonalRemoteDataSource
) : PersonalRepository {
    override suspend fun getPersonalInfo(): PersonalInfoDN? {
        return personalRemoteDataSource.getPersonalInfo()?.toDomain()
    }
}
