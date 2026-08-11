package com.tamin.taminhamrah.dataSource.contactUs

import com.tamin.taminhamrah.model.contactUs.ContactUsInfoDto

internal class ContactUsRemoteDataSourceImpl : ContactUsRemoteDataSource {
    override suspend fun getContactUsInfo(): ContactUsInfoDto {
        return mockContactUsData
    }
}
