package com.tamin.taminhamrah.dataSource.requestPaymentForIllDays

import com.tamin.taminhamrah.tools.FakeIOException
import com.tamin.taminhamrah.apiService.requestPaymentForIllDays.RequestPaymentForIllDaysApiService
import com.tamin.taminhamrah.model.requestPaymentForIllDays.CovidResultListDTO
import com.tamin.taminhamrah.model.requestPaymentForIllDays.IllDaysInsuredMainInfoDTO
import com.tamin.taminhamrah.model.requestPaymentForIllDays.IllDaysShortTermResultDTO
import com.tamin.taminhamrah.model.requestPaymentForIllDays.SaveShortTermIllnessRequestDTO
import com.tamin.taminhamrah.model.requestPaymentForIllDays.SaveShortTermIllnessResponseDTO
import com.tamin.taminhamrah.tools.BaseDTO
import com.tamin.taminhamrah.tools.errorHandling.ErrorParser
import com.tamin.taminhamrah.tools.errorHandling.TaminApiException
import com.tamin.taminhamrah.tools.errorHandling.TaminErrorUriException
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class RequestPaymentForIllDaysRemoteDataSourceImplTest {

    @Test
    fun getLatestInsuranceInfo_returnsExtractedDto() = runTest {
        val dataSource = RequestPaymentForIllDaysRemoteDataSourceImpl(
            apiService = FakeRequestPaymentForIllDaysApiService(),
            errorParser = FakeErrorParser(),
        )

        val result = dataSource.getLatestInsuranceInfo()

        assertEquals("1234567890", result?.risuid)
        assertEquals("0012345678", result?.nationalCode)
    }

    @Test
    fun getCovidResult_returnsExtractedList() = runTest {
        val dataSource = RequestPaymentForIllDaysRemoteDataSourceImpl(
            apiService = FakeRequestPaymentForIllDaysApiService(),
            errorParser = FakeErrorParser(),
        )

        val result = dataSource.getCovidResult()

        assertEquals(listOf("1700000000", "1700086400"), result?.list)
    }

    @Test
    fun sendRequestForIllDay_returnsResultMessage() = runTest {
        val dataSource = RequestPaymentForIllDaysRemoteDataSourceImpl(
            apiService = FakeRequestPaymentForIllDaysApiService(),
            errorParser = FakeErrorParser(),
        )

        val result = dataSource.sendRequestForIllDay(SaveShortTermIllnessRequestDTO())

        assertEquals("ok", result?.shorttermRequest?.resultMessage)
    }

    @Test
    fun getLatestInsuranceInfo_mapsConnectionErrors() = runTest {
        val dataSource = RequestPaymentForIllDaysRemoteDataSourceImpl(
            apiService = FakeRequestPaymentForIllDaysApiService(shouldThrow = true),
            errorParser = FakeErrorParser(),
        )

        val error = assertFailsWith<TaminApiException> {
            dataSource.getLatestInsuranceInfo()
        }
        assertEquals("NO_CONNECTION_ERROR", error.title)
    }
}

private class FakeRequestPaymentForIllDaysApiService(
    private val shouldThrow: Boolean = false,
) : RequestPaymentForIllDaysApiService {

    override suspend fun getLatestInsuranceInfo(): BaseDTO<IllDaysInsuredMainInfoDTO> {
        if (shouldThrow) throw FakeIOException()
        return success(
            IllDaysInsuredMainInfoDTO(
                risuid = "1234567890",
                nationalCode = "0012345678",
            )
        )
    }

    override suspend fun getCovidResult(): BaseDTO<CovidResultListDTO> {
        if (shouldThrow) throw FakeIOException()
        return success(CovidResultListDTO(list = listOf("1700000000", "1700086400")))
    }

    override suspend fun calcIllness(
        startDateTimeStamp: String,
        endDateTimeStamp: String,
        maritalStatus: String,
    ): BaseDTO<List<String>?> {
        if (shouldThrow) throw FakeIOException()
        return success(listOf("1000000"))
    }

    override suspend fun sendRequestForIllDay(
        request: SaveShortTermIllnessRequestDTO
    ): BaseDTO<SaveShortTermIllnessResponseDTO> {
        if (shouldThrow) throw FakeIOException()
        return success(
            SaveShortTermIllnessResponseDTO(
                shorttermRequest = IllDaysShortTermResultDTO(resultMessage = "ok")
            )
        )
    }

    private fun <T> success(data: T) = BaseDTO(
        status = 200,
        family = "SUCCESSFUL",
        reason = "OK",
        data = data,
    )
}

private class FakeErrorParser : ErrorParser {
    override fun parseGeneralError(exception: TaminErrorUriException): TaminApiException {
        return TaminApiException(title = exception.uri.name, cause = exception)
    }
}
