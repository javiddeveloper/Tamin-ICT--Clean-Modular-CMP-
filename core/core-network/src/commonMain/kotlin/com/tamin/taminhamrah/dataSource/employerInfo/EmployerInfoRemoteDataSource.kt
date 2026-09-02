package com.tamin.taminhamrah.dataSource.employerInfo

import com.tamin.taminhamrah.model.employerInfo.LegalWorkshopCeoDTO
import com.tamin.taminhamrah.model.employerInfo.LegalWorkshopDTO
import com.tamin.taminhamrah.model.employerInfo.LegalWorkshopInfoRequestDTO
import com.tamin.taminhamrah.model.employerInfo.RealWorkshopInfoRequestDTO
import com.tamin.taminhamrah.model.request.ApiFilterDN

interface EmployerInfoRemoteDataSource {
    suspend fun getLegalWorkshop(legalWorkshopId: String): LegalWorkshopDTO
    suspend fun getLegalWorkshopCeo(nationalCode: String, birthDate: String): LegalWorkshopCeoDTO
    suspend fun requestLegalTicket(filters: List<ApiFilterDN>): String
    suspend fun submitLegalWorkshopInfo(body: LegalWorkshopInfoRequestDTO): String
    suspend fun requestRealTicket(filters: List<ApiFilterDN>): String
    suspend fun submitRealWorkshopInfo(body: RealWorkshopInfoRequestDTO): String
}
