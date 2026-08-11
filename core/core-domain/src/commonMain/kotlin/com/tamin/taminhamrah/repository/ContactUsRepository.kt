package com.tamin.taminhamrah.repository

import com.tamin.taminhamrah.model.contactUs.ContactUsInfoDN
import kotlinx.coroutines.flow.Flow

interface ContactUsRepository {
    fun getContactUsInfo(): Flow<ContactUsInfoDN>
}
