package com.tamin.taminhamrah.data.repository.employerInfo

import com.tamin.taminhamrah.data.mapper.employerInfo.formatCeoBirthDateForInquiry
import com.tamin.taminhamrah.data.mapper.employerInfo.toDTO
import com.tamin.taminhamrah.data.mapper.employerInfo.toDomain
import com.tamin.taminhamrah.dataSource.employerInfo.EmployerInfoRemoteDataSource
import com.tamin.taminhamrah.model.employerInfo.LegalWorkshopCeoDN
import com.tamin.taminhamrah.model.employerInfo.LegalWorkshopDN
import com.tamin.taminhamrah.model.employerInfo.LegalWorkshopInfoRequestDN
import com.tamin.taminhamrah.model.employerInfo.RealWorkshopInfoRequestDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.FilterOperator
import com.tamin.taminhamrah.model.request.FilterProperty
import com.tamin.taminhamrah.repository.employerInfo.EmployerInfoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class EmployerInfoRepositoryImpl(
    private val remoteDataSource: EmployerInfoRemoteDataSource,
) : EmployerInfoRepository {

    override fun getLegalWorkshop(legalWorkshopId: String): Flow<LegalWorkshopDN> = flow {
        val dto = remoteDataSource.getLegalWorkshop(legalWorkshopId)
        emit(dto.toDomain())
    }

    override fun getLegalWorkshopCeo(
        nationalCode: String,
        birthDateMillis: Long,
    ): Flow<LegalWorkshopCeoDN> = flow {
        val formattedDate = formatCeoBirthDateForInquiry(birthDateMillis)
        val dto = remoteDataSource.getLegalWorkshopCeo(nationalCode, formattedDate)
        emit(dto.toDomain())
    }

    override fun requestLegalTicket(
        mobile: String,
        email: String,
        ceoNationalCode: String,
    ): Flow<String> = flow {
        val filters = listOf(
            ApiFilterDN(
                property = FilterProperty.MOBILE_NUMBER,
                operator = FilterOperator.EQ,
                value = mobile,
            ),
            ApiFilterDN(
                property = FilterProperty.EMAIL,
                operator = FilterOperator.EQ,
                value = email,
            ),
            ApiFilterDN(
                property = FilterProperty.NATIONAL_CODE,
                operator = FilterOperator.EQ,
                value = ceoNationalCode,
            ),
            ApiFilterDN(
                property = FilterProperty.SERVICE_NAME,
                operator = FilterOperator.EQ,
                value = "saveStackHolder",
            ),
        )
        val message = remoteDataSource.requestLegalTicket(filters)
        emit(message)
    }

    override fun submitLegalWorkshopInfo(request: LegalWorkshopInfoRequestDN): Flow<String> = flow {
        val message = remoteDataSource.submitLegalWorkshopInfo(request.toDTO())
        emit(message)
    }

    override fun requestRealTicket(mobile: String, email: String): Flow<String> = flow {
        val filters = listOf(
            ApiFilterDN(
                property = FilterProperty.MOBILE_NUMBER,
                operator = FilterOperator.EQ,
                value = mobile,
            ),
            ApiFilterDN(
                property = FilterProperty.EMAIL,
                operator = FilterOperator.EQ,
                value = email,
            ),
            ApiFilterDN(
                property = FilterProperty.SERVICE_NAME,
                operator = FilterOperator.EQ,
                value = "saveStackHolder",
            ),
        )
        val message = remoteDataSource.requestRealTicket(filters)
        emit(message)
    }

    override fun submitRealWorkshopInfo(request: RealWorkshopInfoRequestDN): Flow<String> = flow {
        val message = remoteDataSource.submitRealWorkshopInfo(request.toDTO())
        emit(message)
    }
}
