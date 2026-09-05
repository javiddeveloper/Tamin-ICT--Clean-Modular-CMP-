package com.tamin.taminhamrah.data.repository.inquiryEducation

import com.tamin.taminhamrah.data.mapper.toDomain
import com.tamin.taminhamrah.data.mapper.toInquiryEducationCertificateDN
import com.tamin.taminhamrah.dataSource.inquiryEducation.InquiryEducationRemoteDataSource
import com.tamin.taminhamrah.model.inquiryEducation.EducationDependentsDN
import com.tamin.taminhamrah.model.inquiryEducation.InquiryEducationCertificateDN
import com.tamin.taminhamrah.repository.inquiryEducation.InquiryEducationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class InquiryEducationRepositoryImpl(
    private val inquiryEducationRemoteDataSource: InquiryEducationRemoteDataSource,
) : InquiryEducationRepository {

    override fun getDataForEducation(): Flow<EducationDependentsDN> = flow {
        val response = inquiryEducationRemoteDataSource.getDataForEducation()
        emit(response?.toDomain() ?: EducationDependentsDN())
    }

    override fun inquiryEducationCertificate(
        code: String,
        educationCode: String,
    ): Flow<InquiryEducationCertificateDN> = flow {
        val message = inquiryEducationRemoteDataSource.inquiryEducationCertificate(
            code = code,
            educationCode = educationCode,
        )
        emit(message.toInquiryEducationCertificateDN())
    }
}
