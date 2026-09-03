package com.tamin.taminhamrah.dataSource.pension

import com.tamin.taminhamrah.apiService.pension.PensionApiService
import com.tamin.taminhamrah.model.pension.EdictPensionerDTO
import com.tamin.taminhamrah.model.pension.PensionIdDTO
import com.tamin.taminhamrah.model.pension.PensionInquiryDTO
import com.tamin.taminhamrah.model.pension.authenticationTicket.AuthenticationTicketDTO
import com.tamin.taminhamrah.model.pension.checkRetirementStatus.RetirementStatusDTO
import com.tamin.taminhamrah.model.pension.disabilityRequest.DisabilityFinalConfirmRequest
import com.tamin.taminhamrah.model.pension.disabilityRequest.DisabilitySaveDocumentRequest
import com.tamin.taminhamrah.model.pension.disabilityRequest.DisabilitySaveInfoRequest
import com.tamin.taminhamrah.model.pension.disabilityRequest.DisabilitySaveInfoResponseDTO
import com.tamin.taminhamrah.model.pension.disabilityRequest.medicalCommission.RegisteredMedicalCommissionDTO
import com.tamin.taminhamrah.model.pension.fish.PayRollDTO
import com.tamin.taminhamrah.model.pension.installment.DeferredInstallmentCertificateDTO
import com.tamin.taminhamrah.model.pension.installment.DeferredInstallmentRequest
import com.tamin.taminhamrah.model.pension.retirement.RetirementPersonalDTO
import com.tamin.taminhamrah.model.pension.retirementInfo.RetirementRequestDTO
import com.tamin.taminhamrah.model.pension.sendRetirementDocument.RetirementSaveDocumentRequest
import com.tamin.taminhamrah.model.personal.age.AgeDTO
import com.tamin.taminhamrah.model.personal.disabilityRequest.disabilityRequestPersonal.DisabilityPersonalInfoDTO
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.request.FilterOperator
import com.tamin.taminhamrah.model.request.FilterProperty
import com.tamin.taminhamrah.model.utils.ListData
import com.tamin.taminhamrah.tools.BaseDTO
import com.tamin.taminhamrah.tools.apiQueryBuilder.ApiQueryBuilder
import com.tamin.taminhamrah.tools.errorHandling.ErrorParser
import com.tamin.taminhamrah.tools.errorHandling.TaminApiException
import com.tamin.taminhamrah.tools.errorHandling.TaminErrorUriException
import io.ktor.client.statement.HttpStatement
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class PensionRemoteDataSourceImplTest {

    private val payRollFilters = listOf(
        ApiFilterDN(FilterProperty.PENSIONER_ID, "123", FilterOperator.EQUAL)
    )

    @Test
    fun `getPensionerId returns extracted list`() = runTest {
        val dataSource = PensionRemoteDataSourceImpl(
            pensionApiService = FakePensionApiService(),
            apiQueryBuilder = FakeApiQueryBuilder(),
            errorParser = FakeErrorParser()
        )

        val result = dataSource.getPensionerId()

        assertEquals(1, result.list?.size)
        assertEquals("123", result.list?.first()?.pensionerId)
    }

    @Test
    fun `getPensionerPayRoll returns extracted list`() = runTest {
        val dataSource = PensionRemoteDataSourceImpl(
            pensionApiService = FakePensionApiService(),
            apiQueryBuilder = FakeApiQueryBuilder(),
            errorParser = FakeErrorParser()
        )

        val result = dataSource.getPensionerPayRoll(payRollFilters)

        assertEquals(1, result.list?.size)
        assertEquals(1, result.list?.first()?.id)
        assertEquals("Type A", result.list?.first()?.clpType)
        assertEquals(5_000_000L, result.list?.first()?.sumAmount)
    }

    @Test
    fun `sendPayRollToInbox returns extracted message`() = runTest {
        val dataSource = PensionRemoteDataSourceImpl(
            pensionApiService = FakePensionApiService(),
            apiQueryBuilder = FakeApiQueryBuilder(),
            errorParser = FakeErrorParser()
        )

        val result = dataSource.sendPayRollToInbox(payRollFilters)

        assertEquals("عملیات با موفقیت انجام شد", result)
    }

    @Test
    fun `getPensionerPayRoll maps connection errors`() = runTest {
        val dataSource = PensionRemoteDataSourceImpl(
            pensionApiService = FakePensionApiService(shouldThrow = true),
            apiQueryBuilder = FakeApiQueryBuilder(),
            errorParser = FakeErrorParser()
        )

        val error = assertFailsWith<TaminApiException> {
            dataSource.getPensionerPayRoll(payRollFilters)
        }
        assertEquals("NO_CONNECTION_ERROR", error.title)
    }
}

private class FakeApiQueryBuilder : ApiQueryBuilder {
    override fun defaultQuery(): ApiQueryParamDN = ApiQueryParamDN()
    override fun buildQuery(query: ApiQueryParamDN): Map<String, String> = emptyMap()
    override fun buildFilterJson(filters: List<ApiFilterDN>): String = "filter-json"
}

private class FakeErrorParser : ErrorParser {
    override fun parseGeneralError(exception: TaminErrorUriException): TaminApiException {
        return TaminApiException(title = exception.uri.name, cause = exception)
    }
}

private class FakePensionApiService(
    private val shouldThrow: Boolean = false
) : PensionApiService {

    override suspend fun getPensionInquiry(parameters: Map<String, String>): BaseDTO<ListData<PensionInquiryDTO>> =
        error("not used in PensionRemoteDataSourceImplTest")

    override suspend fun getPensionerId(): BaseDTO<ListData<PensionIdDTO>> {
        if (shouldThrow) throw IllegalStateException("network")
        return success(ListData(total = 1, list = listOf(PensionIdDTO(pensionerId = "123"))))
    }

    override suspend fun getEdictPensioner(parameters: Map<String, String>): BaseDTO<EdictPensionerDTO> =
        error("not used in PensionRemoteDataSourceImplTest")

    override suspend fun sendRequestDeferredInstallmentCertificate(
        deferredInstallmentRequest: DeferredInstallmentRequest
    ): BaseDTO<DeferredInstallmentCertificateDTO> =
        error("not used in PensionRemoteDataSourceImplTest")

    override suspend fun getDisabilityPersonalInfo(): BaseDTO<DisabilityPersonalInfoDTO> =
        error("not used in PensionRemoteDataSourceImplTest")

    override suspend fun getUserAge(parameters: Map<String, String>): BaseDTO<AgeDTO> =
        error("not used in PensionRemoteDataSourceImplTest")

    override suspend fun pensionerPayRollPDF(parameters: Map<String, String>): HttpStatement =
        error("not used in PensionRemoteDataSourceImplTest")

    override suspend fun getPensionerPayRoll(filter: String): BaseDTO<ListData<PayRollDTO>> {
        if (shouldThrow) throw IllegalStateException("network")
        return success(
            ListData(
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
    }

    override suspend fun sendPayRollToInbox(filter: String): BaseDTO<JsonElement?>? {
        if (shouldThrow) throw IllegalStateException("network")
        return success(JsonPrimitive("عملیات با موفقیت انجام شد"))
    }

    override suspend fun getEdictReportPDF(parameters: Map<String, String>): HttpStatement =
        error("not used in PensionRemoteDataSourceImplTest")

    override suspend fun getRetirementRequestInfo(parameters: Map<String, String>): BaseDTO<ListData<RetirementRequestDTO>> =
        error("not used in PensionRemoteDataSourceImplTest")

    override suspend fun authenticationAndGetPersonalInfo(authenticationsCode: Long): BaseDTO<RetirementPersonalDTO> =
        error("not used in PensionRemoteDataSourceImplTest")

    override suspend fun checkRetirementStatus(): BaseDTO<RetirementStatusDTO> =
        error("not used in PensionRemoteDataSourceImplTest")

    override suspend fun getAuthenticationCode(): BaseDTO<AuthenticationTicketDTO> =
        error("not used in PensionRemoteDataSourceImplTest")

    override suspend fun sendRequestInquirePensionCertificate(parameters: Map<String, String>): BaseDTO<JsonElement?> =
        error("not used in PensionRemoteDataSourceImplTest")

    override suspend fun sendRetirementDocument(
        requestId: String,
        body: RetirementSaveDocumentRequest
    ): BaseDTO<String?> =
        error("not used in PensionRemoteDataSourceImplTest")

    override suspend fun sendEdictPensionerToMyInbox(parameters: Map<String, String>): BaseDTO<JsonElement?> =
        error("not used in PensionRemoteDataSourceImplTest")

    override suspend fun saveDisabilityUserInfo(body: DisabilitySaveInfoRequest): BaseDTO<DisabilitySaveInfoResponseDTO> =
        error("not used in PensionRemoteDataSourceImplTest")

    override suspend fun finalConfirmDisabilityRequest(
        requestId: Long,
        body: DisabilityFinalConfirmRequest
    ): BaseDTO<JsonElement?> =
        error("not used in PensionRemoteDataSourceImplTest")

    override suspend fun saveDocumentDisability(
        requestId: Long,
        body: DisabilitySaveDocumentRequest
    ): BaseDTO<JsonElement?> =
        error("not used in PensionRemoteDataSourceImplTest")

    override suspend fun getMedicalCommissionPdf(lastWorkshop: String): HttpStatement =
        error("not used in PensionRemoteDataSourceImplTest")

    override suspend fun getRegisteredMedicalCommission(
        parameters: Map<String, String>
    ): BaseDTO<ListData<RegisteredMedicalCommissionDTO>> =
        error("not used in PensionRemoteDataSourceImplTest")

    private fun <T> success(data: T) = BaseDTO(
        status = 200,
        family = "SUCCESSFUL",
        reason = "OK",
        data = data
    )
}
