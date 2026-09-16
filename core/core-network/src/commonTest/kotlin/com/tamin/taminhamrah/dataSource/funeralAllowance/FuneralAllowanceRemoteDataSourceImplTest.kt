package com.tamin.taminhamrah.dataSource.funeralAllowance

import com.tamin.taminhamrah.apiService.funeralAllowance.FuneralAllowanceApiService
import com.tamin.taminhamrah.model.funeralAllowance.FuneralAllowanceInfoDTO
import com.tamin.taminhamrah.model.funeralAllowance.FuneralAllowanceRequestDTO
import com.tamin.taminhamrah.tools.BaseDTO
import com.tamin.taminhamrah.tools.errorHandling.ErrorParserImpl
import com.tamin.taminhamrah.tools.errorHandling.ErrorUri
import com.tamin.taminhamrah.tools.errorHandling.TaminApiException
import com.tamin.taminhamrah.tools.errorHandling.TaminErrorUriException
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class FakeFuneralAllowanceApiService : FuneralAllowanceApiService {
    var infoResult: BaseDTO<FuneralAllowanceInfoDTO> =
        BaseDTO(status = 200, family = "OK", reason = "OK", data = FuneralAllowanceInfoDTO())
    var validateResult: BaseDTO<List<String?>> =
        BaseDTO(status = 200, family = "OK", reason = "OK", data = emptyList())
    var submitResult: BaseDTO<JsonElement?> =
        BaseDTO(status = 200, family = "OK", reason = "OK", data = JsonPrimitive("درخواست شما ثبت شد"))
    var confirmResult: BaseDTO<JsonElement?> =
        BaseDTO(status = 200, family = "OK", reason = "OK", data = JsonPrimitive("درخواست شما ثبت شد"))

    var shouldThrowException: Exception? = null
    var lastValidateNationalCode: String? = null
    var lastSubmitRequest: FuneralAllowanceRequestDTO? = null
    var lastConfirmRequestId: String? = null

    override suspend fun getFuneralAllowanceInfo(): BaseDTO<FuneralAllowanceInfoDTO> {
        shouldThrowException?.let { throw it }
        return infoResult
    }

    override suspend fun validateDeceased(nationalCode: String): BaseDTO<List<String?>> {
        shouldThrowException?.let { throw it }
        lastValidateNationalCode = nationalCode
        return validateResult
    }

    override suspend fun submitFuneralAllowanceRequest(
        request: FuneralAllowanceRequestDTO,
    ): BaseDTO<JsonElement?> {
        shouldThrowException?.let { throw it }
        lastSubmitRequest = request
        return submitResult
    }

    override suspend fun confirmAccountCorrection(requestId: String): BaseDTO<JsonElement?> {
        shouldThrowException?.let { throw it }
        lastConfirmRequestId = requestId
        return confirmResult
    }
}

class FuneralAllowanceRemoteDataSourceImplTest {

    private lateinit var apiService: FakeFuneralAllowanceApiService
    private lateinit var dataSource: FuneralAllowanceRemoteDataSourceImpl

    @BeforeTest
    fun setup() {
        apiService = FakeFuneralAllowanceApiService()
        dataSource = FuneralAllowanceRemoteDataSourceImpl(
            apiService = apiService,
            errorParser = ErrorParserImpl(),
        )
    }

    @Test
    fun getFuneralAllowanceInfo_success_returnsDto() = runTest {
        val expected = FuneralAllowanceInfoDTO(insuranceFirstName = "علی", risuid = "1234567")
        apiService.infoResult = BaseDTO(status = 200, family = "OK", reason = "OK", data = expected)

        val result = dataSource.getFuneralAllowanceInfo()

        assertEquals(expected, result)
    }

    @Test
    fun validateDeceased_success_forwardsCodeAndDecodesPositionalArray() = runTest {
        val raw = listOf<String?>(
            "0", "1", "2", "3", "زهرا رضایی", "همسر", "1", "دارای شرایط",
        )
        apiService.validateResult = BaseDTO(status = 200, family = "OK", reason = "OK", data = raw)

        val result = dataSource.validateDeceased("0055667788")

        assertEquals("0055667788", apiService.lastValidateNationalCode)
        assertEquals("زهرا رضایی", result.fullName)
        assertEquals("همسر", result.relationship)
        assertTrue(result.isEligible)
        assertEquals("دارای شرایط", result.message)
    }

    @Test
    fun validateDeceased_shortArray_decodesAsNotEligibleWithBlankFields() = runTest {
        apiService.validateResult = BaseDTO(
            status = 200, family = "OK", reason = "OK",
            data = listOf<String?>("0", "1", "2", "3", "زهرا"),
        )

        val result = dataSource.validateDeceased("0055667788")

        assertTrue(!result.isEligible)
        assertEquals("زهرا", result.fullName)
        assertEquals("", result.relationship)
    }

    @Test
    fun submitFuneralAllowanceRequest_success_returnsMessageStringAndForwardsRequest() = runTest {
        apiService.submitResult =
            BaseDTO(status = 200, family = "OK", reason = "OK", data = JsonPrimitive("ثبت شد"))
        val request = FuneralAllowanceRequestDTO(deadNationalId = "0055667788")

        val result = dataSource.submitFuneralAllowanceRequest(request)

        assertEquals("ثبت شد", result)
        assertEquals(request, apiService.lastSubmitRequest)
    }

    @Test
    fun submitFuneralAllowanceRequest_nullData_fallsBackToReason() = runTest {
        apiService.submitResult =
            BaseDTO(status = 200, family = "OK", reason = "با موفقیت انجام شد", data = JsonNull)

        val result = dataSource.submitFuneralAllowanceRequest(FuneralAllowanceRequestDTO())

        assertEquals("با موفقیت انجام شد", result)
    }

    @Test
    fun confirmAccountCorrection_success_returnsMessageAndForwardsId() = runTest {
        apiService.confirmResult =
            BaseDTO(status = 200, family = "OK", reason = "OK", data = JsonPrimitive("مجدداً ثبت شد"))

        val result = dataSource.confirmAccountCorrection("998877")

        assertEquals("مجدداً ثبت شد", result)
        assertEquals("998877", apiService.lastConfirmRequestId)
    }

    @Test
    fun getFuneralAllowanceInfo_errorStatus_throwsParsedException() = runTest {
        apiService.infoResult = BaseDTO(status = 400, family = "CLIENT_ERROR", reason = "Bad Request", data = null)

        assertFailsWith<TaminApiException> { dataSource.getFuneralAllowanceInfo() }
    }

    @Test
    fun validateDeceased_onGenericException_throwsParsedNoConnectionException() = runTest {
        apiService.shouldThrowException = RuntimeException("boom")

        val error = assertFailsWith<TaminApiException> { dataSource.validateDeceased("0055667788") }

        assertEquals("خطای اتصال", error.title)
    }

    @Test
    fun submitFuneralAllowanceRequest_onTaminErrorUri_propagatesParsedException() = runTest {
        apiService.shouldThrowException = TaminErrorUriException(ErrorUri.RESOURCE_NOT_FOUND)

        val error = assertFailsWith<TaminApiException> {
            dataSource.submitFuneralAllowanceRequest(FuneralAllowanceRequestDTO())
        }

        assertEquals("یافت نشد", error.title)
    }
}
