package com.tamin.taminhamrah.dataSource.inquiryEducation

import com.tamin.taminhamrah.model.inquiryEducation.EducationDependentsListDTO

interface InquiryEducationRemoteDataSource {
    suspend fun getDataForEducation(): EducationDependentsListDTO?
    suspend fun inquiryEducationCertificate(code: String, educationCode: String): String?
}
