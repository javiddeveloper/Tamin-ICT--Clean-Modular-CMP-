package com.tamin.taminhamrah.dataSource.objectionInsurance

import com.tamin.taminhamrah.apiService.objectionInsurance.ObjectionInsuranceApiService
import com.tamin.taminhamrah.model.objectionInsurance.ConfirmConflictItemDTO
import com.tamin.taminhamrah.model.objectionInsurance.ObjectionInsuranceHistoryDTO
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.utils.ListData
import com.tamin.taminhamrah.tools.BaseDTO
import com.tamin.taminhamrah.tools.FakeIOException
import com.tamin.taminhamrah.tools.apiQueryBuilder.ApiQueryBuilderImpl
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
import kotlin.test.assertTrue

class FakeObjectionInsuranceApiService : ObjectionInsuranceApiService {
    var checkStatusConflictResult: BaseDTO<Boolean> =
        BaseDTO(status = 200, family = "OK", reason = "OK", data = false)
    var getConflictHistoriesResult: BaseDTO<ListData<ObjectionInsuranceHistoryDTO>> =
        BaseDTO(status = 200, family = "OK", reason = "OK", data = ListData())
    var saveConflictResult: BaseDTO<String?> =
        BaseDTO(status = 200, family = "OK", reason = "OK", data = null)
    var confirmConflictResult: BaseDTO<Boolean> =
        BaseDTO(status = 200, family = "OK", reason = "OK", data = true)
    var finalConfirmConflictResult: BaseDTO<String> =
        BaseDTO(status = 200, family = "OK", reason = "OK", data = "TRK")
    var shouldThrowException: Exception? = null
    var lastQueryParams: Map<String, String>? = null
    var lastSaveItems: List<ObjectionInsuranceHistoryDTO>? = null
    var lastConfirmItems: List<ConfirmConflictItemDTO>? = null

    override suspend fun checkStatusConflict(): BaseDTO<Boolean> {
        shouldThrowException?.let { throw it }
        return checkStatusConflictResult
    }

    override suspend fun getConflictHistories(
        parameters: Map<String, String>,
    ): BaseDTO<ListData<ObjectionInsuranceHistoryDTO>> {
        shouldThrowException?.let { throw it }
        lastQueryParams = parameters
        return getConflictHistoriesResult
    }

    override suspend fun saveConflict(
        items: List<ObjectionInsuranceHistoryDTO>,
    ): BaseDTO<String?> {
        shouldThrowException?.let { throw it }
        lastSaveItems = items
        return saveConflictResult
    }

    override suspend fun confirmConflict(
        items: List<ConfirmConflictItemDTO>,
    ): BaseDTO<Boolean> {
        shouldThrowException?.let { throw it }
        lastConfirmItems = items
        return confirmConflictResult
    }

    override suspend fun finalConfirmConflict(body: String): BaseDTO<String> {
        shouldThrowException?.let { throw it }
        return finalConfirmConflictResult
    }
}

class ObjectionInsuranceRemoteDataSourceImplTest {

    private lateinit var fakeApiService: FakeObjectionInsuranceApiService
    private lateinit var dataSource: ObjectionInsuranceRemoteDataSourceImpl

    @BeforeTest
    fun setup() {
        fakeApiService = FakeObjectionInsuranceApiService()
        dataSource = ObjectionInsuranceRemoteDataSourceImpl(
            objectionInsuranceApiService = fakeApiService,
            apiQueryBuilder = ApiQueryBuilderImpl(),
            errorParser = ErrorParserImpl(),
        )
    }

    @Test
    fun checkStatusConflict_success_returnsBoolean() = runTest {
        fakeApiService.checkStatusConflictResult =
            BaseDTO(status = 200, family = "OK", reason = "OK", data = true)

        assertTrue(dataSource.checkStatusConflict())
    }

    @Test
    fun getConflictHistories_success_returnsList() = runTest {
        val expected = ListData(
            total = 1,
            list = listOf(ObjectionInsuranceHistoryDTO(year = "1402", branchCode = "0950")),
        )
        fakeApiService.getConflictHistoriesResult =
            BaseDTO(status = 200, family = "OK", reason = "OK", data = expected)

        val result = dataSource.getConflictHistories(ApiQueryParamDN(limit = 60))

        assertEquals(expected, result)
        assertEquals("60", fakeApiService.lastQueryParams?.get("limit"))
    }

    @Test
    fun saveConflict_nullData_returnsNull() = runTest {
        fakeApiService.saveConflictResult =
            BaseDTO(status = 200, family = "OK", reason = "OK", data = null)

        assertNull(dataSource.saveConflict(emptyList()))
    }

    @Test
    fun saveConflict_success_returnsMessage() = runTest {
        fakeApiService.saveConflictResult =
            BaseDTO(status = 200, family = "OK", reason = "OK", data = "ok")

        assertEquals("ok", dataSource.saveConflict(listOf(ObjectionInsuranceHistoryDTO(year = "1402"))))
    }

    @Test
    fun confirmConflict_wrapsDescriptionInList() = runTest {
        fakeApiService.confirmConflictResult =
            BaseDTO(status = 200, family = "OK", reason = "OK", data = true)

        assertTrue(dataSource.confirmConflict("توضیح"))
        assertEquals(1, fakeApiService.lastConfirmItems?.size)
        assertEquals("توضیح", fakeApiService.lastConfirmItems?.first()?.userDesc)
    }

    @Test
    fun finalConfirmConflict_success_returnsTrackingNumber() = runTest {
        fakeApiService.finalConfirmConflictResult =
            BaseDTO(status = 200, family = "OK", reason = "OK", data = "TRK-9")

        assertEquals("TRK-9", dataSource.finalConfirmConflict())
    }

    @Test
    fun checkStatusConflict_errorStatus_throws() = runTest {
        fakeApiService.checkStatusConflictResult =
            BaseDTO(status = 400, family = "CLIENT_ERROR", reason = "Bad Request", data = null)

        assertFailsWith<TaminApiException> {
            dataSource.checkStatusConflict()
        }
    }

    @Test
    fun checkStatusConflict_networkException_throwsNoConnection() = runTest {
        fakeApiService.shouldThrowException = FakeIOException()

        val exception = assertFailsWith<TaminApiException> {
            dataSource.checkStatusConflict()
        }
        assertEquals(ErrorUri.NO_CONNECTION_ERROR, exception.getTaminErrorUri())
    }
}
