package com.tamin.taminhamrah.repository.pension

import com.tamin.taminhamrah.model.pension.PensionIdDN
import com.tamin.taminhamrah.model.pension.EdictPensionerDN
import com.tamin.taminhamrah.model.pension.PensionInquiryDN
import com.tamin.taminhamrah.model.pension.PayRollDN
import com.tamin.taminhamrah.model.pension.installment.DeferredInstallmentCertificateDN
import com.tamin.taminhamrah.model.pension.installment.DeferredInstallmentRequestDN
import com.tamin.taminhamrah.model.personal.DisabilityPersonalInfoDN
import com.tamin.taminhamrah.model.personal.AgeDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakePensionRepository : PensionRepository {
    var pensionInquiryResult: List<PensionInquiryDN> = emptyList()
    var pensionIdResult: List<PensionIdDN> = emptyList()
    var edictPensionerResult: EdictPensionerDN? = null
    var deferredInstallmentCertificateResult: DeferredInstallmentCertificateDN? = null
    var payRollResult: PayRollDN? = null
    var userAgeResult: AgeDN? = null
    var disabilityPersonalInfoResult: DisabilityPersonalInfoDN? = null
    var payRollPDFResult: com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN? = null
    var shouldThrowError: Boolean = false
    var error: Throwable? = null


    override suspend fun getPensionInquiry(
        filters: List<ApiFilterDN>
    ): Flow<List<PensionInquiryDN>> = flow {
        if (shouldThrowError) {
            throw error!!
        }
        emit(pensionInquiryResult)
    }

    override suspend fun getPensionerId(): Flow<List<PensionIdDN>> = flow {
        if (shouldThrowError) {
            throw error!!
        }
        emit(pensionIdResult)
    }

    override suspend fun getEdictPensioner(query: ApiQueryParamDN): Flow<EdictPensionerDN?> = flow {
        if (shouldThrowError) {
            throw error!!
        }
        emit(edictPensionerResult)
    }

    override suspend fun sendRequestDeferredInstallmentCertificate(request: DeferredInstallmentRequestDN): Flow<DeferredInstallmentCertificateDN> = flow {
        if (shouldThrowError) {
            throw error!!
        }
        emit(deferredInstallmentCertificateResult!!)
    }

    override suspend fun getPensionerPayRoll(filters: List<ApiFilterDN>): Flow<PayRollDN> = flow {
        if (shouldThrowError) {
            throw error!!
        }
        emit(payRollResult!!)
    }

    override suspend fun getDisabilityPersonalInfo(): Flow<DisabilityPersonalInfoDN> = flow {
        if (shouldThrowError) {
            throw error!!
        }
        emit(disabilityPersonalInfoResult!!)
    }

    override suspend fun getUserAge(filters: List<ApiFilterDN>): Flow<AgeDN> = flow {
        if (shouldThrowError) {
            throw error!!
        }
        emit(userAgeResult!!)
    }


    override suspend fun pensionerPayRollPDF(filters: List<ApiFilterDN>): Flow<com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN> =
        flow {
            if (shouldThrowError) {
                throw error!!
            }
            emit(payRollPDFResult!!)
        }

}
