package com.tamin.taminhamrah.dataSource.contactUs

import com.tamin.taminhamrah.model.contactUs.ContactUsInfoDto

interface ContactUsRemoteDataSource {
    suspend fun getContactUsInfo(): ContactUsInfoDto
}
