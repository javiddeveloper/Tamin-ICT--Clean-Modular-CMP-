package com.tamin.taminhamrah.dataSource.weddingPresent

import com.tamin.taminhamrah.apiService.weddingPresent.WeddingPresentApiService
import com.tamin.taminhamrah.model.weddingPresent.MarriageGiftRequestDTO
import com.tamin.taminhamrah.model.weddingPresent.ShortTermMarriageRequestDTO
import com.tamin.taminhamrah.model.weddingPresent.WeddingPresentInfoDTO
import com.tamin.taminhamrah.tools.BaseDTO
import com.tamin.taminhamrah.tools.errorHandling.ErrorParserImpl
import com.tamin.taminhamrah.tools.errorHandling.ErrorUri
import com.tamin.taminhamrah.tools.errorHandling.TaminApiException
import com.tamin.taminhamrah.tools.errorHandling.getTaminErrorUri
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.JsonElement
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class FakeWeddingPresentApiService : WeddingPresentApiService {
    var getInfoResult: BaseDTO<WeddingPresentInfoDTO> =
        BaseDTO(status = 200, family = "OK", reason = "OK", data = WeddingPresentInfoDTO())
    var submitResult: BaseDTO<JsonElement?> =
        BaseDTO(status = 200, family = "OK", reason = "OK", data = null)
    var shouldThrowException: Exception? = null
    var lastSubmitRequest: ShortTermMarriageRequestDTO? = null

    override suspend fun getWeddingPresentInfo(): BaseDTO<WeddingPresentInfoDTO> {
        shouldThrowException?.let { throw it }
        return getInfoResult
    }

    override suspend fun submitWeddingPresent(
        request: ShortTermMarriageRequestDTO,
    ): BaseDTO<JsonElement?> {
        shouldThrowException?.let { throw it }
        lastSubmitRequest = request
        return submitResult
    }
}

class WeddingPresentRemoteDataSourceImplTest {

    private lateinit var fakeApiService: FakeWeddingPresentApiService
    private lateinit var dataSource: WeddingPresentRemoteDataSourceImpl

    @BeforeTest
    fun setup() {
        fakeApiService = FakeWeddingPresentApiService()
        dataSource = WeddingPresentRemoteDataSourceImpl(
            weddingPresentApiService = fakeApiService,
            errorParser = ErrorParserImpl(),
        )
    }

    @Test
    fun getWeddingPresentInfo_success_returnsInfo() = runTest {
        val expected = WeddingPresentInfoDTO(
            risuid = "1234567890",
            insuranceFirstName = "علی",
            insuranceLastName = "رضایی",
        )
        fakeApiService.getInfoResult =
            BaseDTO(status = 200, family = "OK", reason = "OK", data = expected)

        val result = dataSource.getWeddingPresentInfo()

        assertEquals(expected, result)
    }

    @Test
    fun submitWeddingPresent_nullData_succeeds() = runTest {
        fakeApiService.submitResult =
            BaseDTO(status = 200, family = "OK", reason = "OK", data = null)

        dataSource.submitWeddingPresent(
            ShortTermMarriageRequestDTO(
                partnerNationalId = "0098765432",
                shortTermRequest = MarriageGiftRequestDTO(risuid = "1"),
                weddingDateTimeStamp = 1L,
            )
        )

        assertEquals("0098765432", fakeApiService.lastSubmitRequest?.partnerNationalId)
    }

    @Test
    fun getWeddingPresentInfo_errorStatus_throws() = runTest {
        fakeApiService.getInfoResult =
            BaseDTO(status = 400, family = "CLIENT_ERROR", reason = "Bad Request", data = null)

        assertFailsWith<TaminApiException> {
            dataSource.getWeddingPresentInfo()
        }
    }

    @Test
    fun getWeddingPresentInfo_networkException_throwsNoConnection() = runTest {
        fakeApiService.shouldThrowException = RuntimeException("network")

        val exception = assertFailsWith<TaminApiException> {
            dataSource.getWeddingPresentInfo()
        }
        assertEquals(ErrorUri.NO_CONNECTION_ERROR, exception.getTaminErrorUri())
    }

    @Test
    fun submitWeddingPresent_errorStatus_throws() = runTest {
        fakeApiService.submitResult =
            BaseDTO(status = 500, family = "SERVER_ERROR", reason = "Error", data = null)

        assertFailsWith<TaminApiException> {
            dataSource.submitWeddingPresent(
                ShortTermMarriageRequestDTO(
                    partnerNationalId = "1",
                    shortTermRequest = MarriageGiftRequestDTO(),
                    weddingDateTimeStamp = 1L,
                )
            )
        }
    }
}
