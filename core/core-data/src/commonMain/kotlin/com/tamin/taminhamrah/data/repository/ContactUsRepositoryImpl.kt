package com.tamin.taminhamrah.data.repository

import com.tamin.taminhamrah.dataSource.contactUs.ContactUsRemoteDataSource
import com.tamin.taminhamrah.data.mapper.toDomain
import com.tamin.taminhamrah.model.contactUs.ContactUsInfoDN
import com.tamin.taminhamrah.repository.ContactUsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ContactUsRepositoryImpl(
    private val remoteDataSource: ContactUsRemoteDataSource
) : ContactUsRepository {

    override fun getContactUsInfo(): Flow<ContactUsInfoDN> = flow {
        val remoteData = remoteDataSource.getContactUsInfo()
        emit(remoteData.toDomain())
    }
}
