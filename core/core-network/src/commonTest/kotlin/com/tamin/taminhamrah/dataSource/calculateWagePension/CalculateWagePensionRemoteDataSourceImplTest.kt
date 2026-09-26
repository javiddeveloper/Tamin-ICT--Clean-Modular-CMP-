package com.tamin.taminhamrah.dataSource.calculateWagePension

import com.tamin.taminhamrah.tools.FakeIOException
import com.tamin.taminhamrah.apiService.calculateWagePension.CalculateWagePensionApiService
import com.tamin.taminhamrah.model.calculateWagePension.MultipleWorkshopPersonalInfoDTO
import com.tamin.taminhamrah.model.calculateWagePension.MultipleWorkshopResultDTO
import com.tamin.taminhamrah.tools.BaseDTO
import com.tamin.taminhamrah.tools.errorHandling.ErrorParser
import com.tamin.taminhamrah.tools.errorHandling.TaminApiException
import com.tamin.taminhamrah.tools.errorHandling.TaminErrorUriException
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException

class CalculateWagePensionRemoteDataSourceImplTest {

    @Test
    fun `getPersonalInfo returns extracted dto`() = runTest {
        val dataSource = CalculateWagePensionRemoteDataSourceImpl(
            apiService = FakeCalculateWagePensionApiService(),
            errorParser = FakeErrorParser()
        )

        val result = dataSource.getPersonalInfo()

        assertEquals("12345", result.organizationId)
        assertEquals("9876543210", result.insuranceId)
    }

    @Test
    fun `isMultipleWorkshops returns extracted result`() = runTest {
        val dataSource = CalculateWagePensionRemoteDataSourceImpl(
            apiService = FakeCalculateWagePensionApiService(),
            errorParser = FakeErrorParser()
        )

        val result = dataSource.isMultipleWorkshops("12345", "9876543210")

        assertEquals(1, result.result)
    }

    @Test
    fun `calculateMultipleWorkshops returns extracted amount`() = runTest {
        val dataSource = CalculateWagePensionRemoteDataSourceImpl(
            apiService = FakeCalculateWagePensionApiService(),
            errorParser = FakeErrorParser()
        )

        val result = dataSource.calculateMultipleWorkshops("12345", "9876543210")

        assertEquals(25_000_000, result.result)
    }

    @Test
    fun `getPersonalInfo maps connection errors`() = runTest {
        val dataSource = CalculateWagePensionRemoteDataSourceImpl(
            apiService = FakeCalculateWagePensionApiService(shouldThrow = true),
            errorParser = FakeErrorParser()
        )

        val error = assertFailsWith<TaminApiException> {
            dataSource.getPersonalInfo()
        }
        assertEquals("NO_CONNECTION_ERROR", error.title)
    }

    /** The old app told these apart: "زمان درخواست به پایان رسید", not "check your internet". */
    @Test
    fun `timeouts read as timeouts, not as a lost connection`() = runTest {
        val timeouts = listOf(
            HttpRequestTimeoutException("https://eservices.tamin.ir/api/x", 60_000),
            ConnectTimeoutException("connect timed out"),
            SocketTimeoutException("Read timed out"),
        )
        for (timeout in timeouts) {
            val dataSource = CalculateWagePensionRemoteDataSourceImpl(
                apiService = FakeCalculateWagePensionApiService(error = timeout),
                errorParser = FakeErrorParser()
            )

            val error = assertFailsWith<TaminApiException> { dataSource.getPersonalInfo() }
            assertEquals("SERVICE_TIMEOUT", error.title, timeout::class.simpleName)
        }
    }
}

private class FakeCalculateWagePensionApiService(
    private val shouldThrow: Boolean = false,
    private val error: Throwable? = null,
) : CalculateWagePensionApiService {

    override suspend fun getPersonalInfo(): BaseDTO<MultipleWorkshopPersonalInfoDTO> {
        error?.let { throw it }
        if (shouldThrow) throw FakeIOException()
        return success(
            MultipleWorkshopPersonalInfoDTO(
                organizationId = "12345",
                insuranceId = "9876543210",
                branch = "Tehran Main"
            )
        )
    }

    override suspend fun isMultipleWorkshops(
        branchCode: String,
        insuranceNumber: String
    ): BaseDTO<MultipleWorkshopResultDTO> {
        if (shouldThrow) throw FakeIOException()
        return success(MultipleWorkshopResultDTO(result = 1))
    }

    override suspend fun calculateMultipleWorkshops(
        branchCode: String,
        insuranceNumber: String
    ): BaseDTO<MultipleWorkshopResultDTO> {
        if (shouldThrow) throw FakeIOException()
        return success(MultipleWorkshopResultDTO(result = 25_000_000))
    }

    private fun <T> success(data: T) = BaseDTO(
        status = 200,
        family = "SUCCESSFUL",
        reason = "OK",
        data = data
    )
}

private class FakeErrorParser : ErrorParser {
    override fun parseGeneralError(exception: TaminErrorUriException): TaminApiException {
        return TaminApiException(title = exception.uri.name, cause = exception)
    }
}
