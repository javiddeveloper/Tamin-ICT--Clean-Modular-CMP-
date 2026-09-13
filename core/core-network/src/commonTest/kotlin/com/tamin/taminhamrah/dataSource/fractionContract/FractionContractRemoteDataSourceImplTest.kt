package com.tamin.taminhamrah.dataSource.fractionContract

import com.tamin.taminhamrah.apiService.fractionContract.FractionContractApiService
import com.tamin.taminhamrah.model.fractionContract.FractionContractResultDTO
import com.tamin.taminhamrah.model.fractionContract.FractionEligibilityDTO
import com.tamin.taminhamrah.model.fractionContract.MakeFractionContractRequestDTO
import com.tamin.taminhamrah.tools.BaseDTO
import com.tamin.taminhamrah.tools.FakeIOException
import com.tamin.taminhamrah.tools.errorHandling.ErrorParserImpl
import com.tamin.taminhamrah.tools.errorHandling.ErrorUri
import com.tamin.taminhamrah.tools.errorHandling.TaminApiException
import com.tamin.taminhamrah.tools.errorHandling.getTaminErrorUri
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class FakeFractionContractApiService : FractionContractApiService {
    var checkAgeAndHistoryResult: BaseDTO<FractionEligibilityDTO?> =
        BaseDTO(status = 200, family = "OK", reason = "OK", data = null)
    var makeFractionContractResult: BaseDTO<FractionContractResultDTO> =
        BaseDTO(
            status = 200,
            family = "OK",
            reason = "OK",
            data = FractionContractResultDTO(contractNumber = 1L, contractDate = 2L),
        )
    var shouldThrowException: Exception? = null
    var lastRequest: MakeFractionContractRequestDTO? = null

    override suspend fun checkAgeAndHistory(): BaseDTO<FractionEligibilityDTO?> {
        shouldThrowException?.let { throw it }
        return checkAgeAndHistoryResult
    }

    override suspend fun makeFractionContract(
        request: MakeFractionContractRequestDTO,
    ): BaseDTO<FractionContractResultDTO> {
        shouldThrowException?.let { throw it }
        lastRequest = request
        return makeFractionContractResult
    }
}

class FractionContractRemoteDataSourceImplTest {

    private lateinit var fakeApiService: FakeFractionContractApiService
    private lateinit var dataSource: FractionContractRemoteDataSourceImpl

    @BeforeTest
    fun setup() {
        fakeApiService = FakeFractionContractApiService()
        dataSource = FractionContractRemoteDataSourceImpl(
            fractionContractApiService = fakeApiService,
            errorParser = ErrorParserImpl(),
        )
    }

    @Test
    fun checkAgeAndHistory_success_returnsEligibility() = runTest {
        val expected = FractionEligibilityDTO(
            isInsurance = true,
            checkFractionMonthStatus = "1",
            eligibilityStatus = 2,
        )
        fakeApiService.checkAgeAndHistoryResult =
            BaseDTO(status = 200, family = "OK", reason = "OK", data = expected)

        val result = dataSource.checkAgeAndHistory()

        assertEquals(expected, result)
    }

    @Test
    fun checkAgeAndHistory_nullData_returnsNull() = runTest {
        fakeApiService.checkAgeAndHistoryResult =
            BaseDTO(status = 200, family = "OK", reason = "OK", data = null)

        val result = dataSource.checkAgeAndHistory()

        assertNull(result)
    }

    @Test
    fun makeFractionContract_success_returnsResult() = runTest {
        val expected = FractionContractResultDTO(contractNumber = 99L, contractDate = 100L)
        fakeApiService.makeFractionContractResult =
            BaseDTO(status = 200, family = "OK", reason = "OK", data = expected)
        val request = MakeFractionContractRequestDTO(premium = "1500000")

        val result = dataSource.makeFractionContract(request)

        assertEquals(expected, result)
        assertEquals("1500000", fakeApiService.lastRequest?.premium)
    }

    @Test
    fun makeFractionContract_errorStatus_throws() = runTest {
        fakeApiService.makeFractionContractResult =
            BaseDTO(status = 400, family = "CLIENT_ERROR", reason = "Bad Request", data = null)

        assertFailsWith<TaminApiException> {
            dataSource.makeFractionContract(MakeFractionContractRequestDTO(premium = "1500000"))
        }
    }

    @Test
    fun makeFractionContract_networkException_throwsNoConnection() = runTest {
        fakeApiService.shouldThrowException = FakeIOException()

        val exception = assertFailsWith<TaminApiException> {
            dataSource.makeFractionContract(MakeFractionContractRequestDTO(premium = "1500000"))
        }
        assertEquals(ErrorUri.NO_CONNECTION_ERROR, exception.getTaminErrorUri())
    }

    @Test
    fun checkAgeAndHistory_errorStatus_throws() = runTest {
        fakeApiService.checkAgeAndHistoryResult =
            BaseDTO(status = 400, family = "CLIENT_ERROR", reason = "Bad Request", data = null)

        assertFailsWith<TaminApiException> {
            dataSource.checkAgeAndHistory()
        }
    }

    @Test
    fun checkAgeAndHistory_networkException_throwsNoConnection() = runTest {
        fakeApiService.shouldThrowException = FakeIOException()

        val exception = assertFailsWith<TaminApiException> {
            dataSource.checkAgeAndHistory()
        }
        assertEquals(ErrorUri.NO_CONNECTION_ERROR, exception.getTaminErrorUri())
    }
}
