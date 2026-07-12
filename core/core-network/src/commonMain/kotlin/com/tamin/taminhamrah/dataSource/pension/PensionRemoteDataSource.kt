package com.tamin.taminhamrah.dataSource.pension

import com.tamin.taminhamrah.model.pension.EdictPensionerDTO
import com.tamin.taminhamrah.model.pension.PensionIdDTO
import com.tamin.taminhamrah.model.pension.PensionInquiryDTO
import com.tamin.taminhamrah.model.pension.fish.PayRollDTO
import com.tamin.taminhamrah.model.pension.installment.DeferredInstallmentCertificateDTO
import com.tamin.taminhamrah.model.pension.installment.DeferredInstallmentRequest
import com.tamin.taminhamrah.model.pension.retirementInfo.RetirementRequestDTO
import com.tamin.taminhamrah.model.personal.age.AgeDTO
import com.tamin.taminhamrah.model.personal.disabilityRequest.disabilityRequestPersonal.DisabilityPersonalInfoDTO
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDTO
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.utils.ListData


interface PensionRemoteDataSource {
    suspend fun getPensionInquiry(query: ApiQueryParamDN): ListData<PensionInquiryDTO>
    suspend fun getPensionerId(): ListData<PensionIdDTO>
    suspend fun getEdictPensioner(query: ApiQueryParamDN): EdictPensionerDTO?
    suspend fun sendRequestDeferredInstallmentCertificate(request: DeferredInstallmentRequest): DeferredInstallmentCertificateDTO
    suspend fun getPensionerPayRoll(
        filter: List<ApiFilterDN>
    ): PayRollDTO

    suspend fun getDisabilityPersonalInfo(): DisabilityPersonalInfoDTO

    suspend fun getUserAge(
        filter: List<ApiFilterDN>
    ): AgeDTO

    suspend fun pensionerPayRollPDF(filter: List<ApiFilterDN>): PdfDownloadDTO

    suspend fun getRetirementRequestInfo(filter: List<ApiFilterDN>) :ListData<RetirementRequestDTO>
}
