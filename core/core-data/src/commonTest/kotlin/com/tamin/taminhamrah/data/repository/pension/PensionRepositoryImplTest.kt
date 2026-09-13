package com.tamin.taminhamrah.data.repository.pension

import com.tamin.taminhamrah.dataSource.pension.PensionRemoteDataSource
import com.tamin.taminhamrah.model.pension.EdictPensionerDTO
import com.tamin.taminhamrah.model.pension.PensionIdDTO
import com.tamin.taminhamrah.model.pension.PensionInquiryDTO
import com.tamin.taminhamrah.model.pension.fish.PayRollDTO
import com.tamin.taminhamrah.model.pension.installment.DeferredInstallmentCertificateDTO
import com.tamin.taminhamrah.model.pension.installment.DeferredInstallmentRequest
import com.tamin.taminhamrah.model.pension.retirement.RetirementPersonalDTO
import com.tamin.taminhamrah.model.pension.retirementInfo.RetirementRequestDTO
import com.tamin.taminhamrah.model.pension.sendRetirementDocument.RetirementSaveDocumentRequest
import com.tamin.taminhamrah.model.pension.authenticationTicket.AuthenticationTicketDTO
import com.tamin.taminhamrah.model.pension.checkRetirementStatus.RetirementStatusDTO
import com.tamin.taminhamrah.model.pension.retirement.RetirementRequestCreatedDTO
import com.tamin.taminhamrah.model.pension.retirement.RetirementRequestFormDTO
import com.tamin.taminhamrah.model.personal.age.AgeDTO
import com.tamin.taminhamrah.model.personal.disabilityRequest.disabilityRequestPersonal.DisabilityPersonalInfoDTO
import com.tamin.taminhamrah.model.personal.pdfDownload.InputStreamDTO
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDTO
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.request.FilterOperator
import com.tamin.taminhamrah.model.request.FilterProperty
import com.tamin.taminhamrah.model.utils.ListData
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class PensionRepositoryImplTest {

    private val payRollFilters = listOf(
        ApiFilterDN(FilterProperty.PENSIONER_ID, "123", FilterOperator.EQUAL)
    )

    @Test
    fun `getPensionerId maps dto list to domain`() = runTest {
        val remoteDataSource = FakePensionRemoteDataSource(
            pensionIdResult = ListData(total = 1, list = listOf(PensionIdDTO(pensionerId = "123")))
        )
        val repository = PensionRepositoryImpl(remoteDataSource)

        val result = repository.getPensionerId().first()

        assertEquals(1, result.size)
        assertEquals("123", result.first().pensionerId)
    }

    @Test
    fun `getPensionerId emits empty list when remote list is null`() = runTest {
        val remoteDataSource = FakePensionRemoteDataSource(
            pensionIdResult = ListData(total = 0, list = null)
        )
        val repository = PensionRepositoryImpl(remoteDataSource)

        val result = repository.getPensionerId().first()

        assertEquals(emptyList(), result)
    }

    @Test
    fun `getPensionerPayRoll maps dto list to domain`() = runTest {
        val remoteDataSource = FakePensionRemoteDataSource(
            payRollResult = ListData(
                total = 1,
                list = listOf(
                    PayRollDTO(
                        id = 1,
                        clpType = "Type A",
                        tprDesc = "Test Description",
                        sumAmount = 5_000_000L,
                        sumPay = 4_500_000L,
                        hisYear = "1402",
                        hisMon = "01"
                    )
                )
            )
        )
        val repository = PensionRepositoryImpl(remoteDataSource)

        val result = repository.getPensionerPayRoll(payRollFilters).first()

        assertEquals(1, result.size)
        assertEquals(1, result.first().id)
        assertEquals("Type A", result.first().clpType)
        assertEquals(5_000_000L, result.first().sumAmount)
    }

    @Test
    fun `pensionerPayRollPDF maps dto to domain`() = runTest {
        val remoteDataSource = FakePensionRemoteDataSource(
            payRollPDFResult = PdfDownloadDTO(pdf = InputStreamDTO(pdf = null))
        )
        val repository = PensionRepositoryImpl(remoteDataSource)

        val result = repository.pensionerPayRollPDF(payRollFilters).first()

        assertNull(result.pdf?.pdf)
    }

    @Test
    fun `sendPayRollToInbox wraps message into PayRollInboxDN`() = runTest {
        val remoteDataSource = FakePensionRemoteDataSource(
            sendPayRollToInboxResult = "عملیات با موفقیت انجام شد"
        )
        val repository = PensionRepositoryImpl(remoteDataSource)

        val result = repository.sendPayRollToInbox(payRollFilters).first()

        assertEquals("عملیات با موفقیت انجام شد", result.message)
    }
}

private class FakePensionRemoteDataSource(
    private val pensionIdResult: ListData<PensionIdDTO> = ListData(),
    private val payRollResult: ListData<PayRollDTO> = ListData(),
    private val payRollPDFResult: PdfDownloadDTO = PdfDownloadDTO(),
    private val sendPayRollToInboxResult: String? = null,
) : PensionRemoteDataSource {

    override suspend fun getPensionInquiry(query: ApiQueryParamDN): ListData<PensionInquiryDTO> =
        error("not used in PensionRepositoryImplTest")

    override suspend fun getPensionerId(): ListData<PensionIdDTO> = pensionIdResult

    override suspend fun getEdictPensioner(query: ApiQueryParamDN): EdictPensionerDTO? =
        error("not used in PensionRepositoryImplTest")

    override suspend fun sendRequestDeferredInstallmentCertificate(
        request: DeferredInstallmentRequest
    ): DeferredInstallmentCertificateDTO =
        error("not used in PensionRepositoryImplTest")

    override suspend fun getPensionerPayRoll(filter: List<ApiFilterDN>): ListData<PayRollDTO> = payRollResult

    override suspend fun getUserAge(filter: List<ApiFilterDN>): AgeDTO =
        error("not used in PensionRepositoryImplTest")

    override suspend fun pensionerPayRollPDF(filter: List<ApiFilterDN>): PdfDownloadDTO = payRollPDFResult

    override suspend fun getEdictReportPDF(filter: List<ApiFilterDN>): PdfDownloadDTO =
        error("not used in PensionRepositoryImplTest")

    override suspend fun getAuthenticationCode(): AuthenticationTicketDTO =
        error("not used in PensionRepositoryImplTest")

    override suspend fun getRetirementRequestInfo(filter: List<ApiFilterDN>): ListData<RetirementRequestDTO> =
        error("not used in PensionRepositoryImplTest")

    override suspend fun createRetirementRequest(
        authenticationsCode: Long,
        form: RetirementRequestFormDTO
    ): RetirementRequestCreatedDTO =
        error("not used in PensionRepositoryImplTest")

    override suspend fun checkRetirementStatus(): RetirementStatusDTO =
        error("not used in PensionRepositoryImplTest")

    override suspend fun getDisabilityPersonalInfo(): DisabilityPersonalInfoDTO =
        error("not used in PensionRepositoryImplTest")

    override suspend fun sendRequestInquirePensionCertificate(filter: List<ApiFilterDN>): String? =
        error("not used in PensionRepositoryImplTest")

    override suspend fun sendRetirementDocument(
        requestId: String,
        request: RetirementSaveDocumentRequest
    ): String? =
        error("not used in PensionRepositoryImplTest")

    override suspend fun authenticationAndGetPersonalInfo(authenticationsCode: Long): RetirementPersonalDTO =
        error("not used in PensionRepositoryImplTest")

    override suspend fun sendEdictPensionerToMyInbox(filter: List<ApiFilterDN>): String? =
        error("not used in PensionRepositoryImplTest")

    override suspend fun sendPayRollToInbox(filter: List<ApiFilterDN>): String? = sendPayRollToInboxResult
}
