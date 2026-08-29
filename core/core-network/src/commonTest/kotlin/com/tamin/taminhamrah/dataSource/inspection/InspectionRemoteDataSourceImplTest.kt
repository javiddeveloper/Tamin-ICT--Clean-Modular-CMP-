package com.tamin.taminhamrah.dataSource.inspection

import com.tamin.taminhamrah.apiService.inspection.InspectionApiService
import com.tamin.taminhamrah.model.inspection.BranchDTO
import com.tamin.taminhamrah.model.inspection.InspectionPerformedDTO
import com.tamin.taminhamrah.model.inspection.JobDTO
import com.tamin.taminhamrah.model.inspection.SubmitInspectionRequestDTO
import com.tamin.taminhamrah.model.inspection.SubmitInspectionRequestModelDTO
import com.tamin.taminhamrah.model.utils.ListData
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.tools.BaseDTO
import com.tamin.taminhamrah.tools.apiQueryBuilder.ApiQueryBuilderImpl
import com.tamin.taminhamrah.tools.errorHandling.ErrorParserImpl
import com.tamin.taminhamrah.tools.errorHandling.ErrorUri
import com.tamin.taminhamrah.tools.errorHandling.TaminApiException
import com.tamin.taminhamrah.tools.errorHandling.TaminErrorUriException
import io.ktor.client.statement.HttpStatement
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class FakeInspectionApiService : InspectionApiService {
    var allInsuranceResult: BaseDTO<ListData<InspectionPerformedDTO>> = BaseDTO(status = 200, family = "OK", reason = "OK", data = ListData(total = 0, list = emptyList()))
    var branchesResult: BaseDTO<ListData<BranchDTO>> = BaseDTO(status = 200, family = "OK", reason = "OK", data = ListData(total = 0, list = emptyList()))
    var jobsResult: BaseDTO<ListData<JobDTO>> = BaseDTO(status = 200, family = "OK", reason = "OK", data = ListData(total = 0, list = emptyList()))
    var submitResult: BaseDTO<SubmitInspectionRequestModelDTO> = BaseDTO(status = 200, family = "OK", reason = "OK", data = SubmitInspectionRequestModelDTO())

    var shouldThrowException: Exception? = null
    var lastAllInsuranceParameters: Map<String, String>? = null
    var lastBranchesParameters: Map<String, String>? = null
    var lastJobsParameters: Map<String, String>? = null

    override suspend fun getAllInsurance(parameters: Map<String, String>): BaseDTO<ListData<InspectionPerformedDTO>> {
        shouldThrowException?.let { throw it }
        lastAllInsuranceParameters = parameters
        return allInsuranceResult
    }

    override suspend fun getBranches(parameters: Map<String, String>): BaseDTO<ListData<BranchDTO>> {
        shouldThrowException?.let { throw it }
        lastBranchesParameters = parameters
        return branchesResult
    }

    override suspend fun getJobs(parameters: Map<String, String>): BaseDTO<ListData<JobDTO>> {
        shouldThrowException?.let { throw it }
        lastJobsParameters = parameters
        return jobsResult
    }

    override suspend fun submitInspectionRequest(request: SubmitInspectionRequestDTO): BaseDTO<SubmitInspectionRequestModelDTO> {
        shouldThrowException?.let { throw it }
        return submitResult
    }

    override suspend fun getInspectionReportPDF(inspectionNo: String): HttpStatement {
        shouldThrowException?.let { throw it }
        error("not exercised in this test class — see InspectionApiServiceTest for the streaming response")
    }
}

class InspectionRemoteDataSourceImplTest {

    private lateinit var fakeApiService: FakeInspectionApiService
    private lateinit var dataSource: InspectionRemoteDataSourceImpl

    @BeforeTest
    fun setup() {
        fakeApiService = FakeInspectionApiService()
        dataSource = InspectionRemoteDataSourceImpl(
            apiService = fakeApiService,
            queryBuilder = ApiQueryBuilderImpl(),
            errorParser = ErrorParserImpl()
        )
    }

    @Test
    fun getAllInsurance_success_returnsInspectionPerformedList() = runTest {
        val expected = ListData(total = 1, list = listOf(InspectionPerformedDTO(activityDesc = "تست")))
        fakeApiService.allInsuranceResult = BaseDTO(status = 200, family = "OK", reason = "OK", data = expected)

        val result = dataSource.getAllInsurance(ApiQueryParamDN())

        assertEquals(expected, result)
        assertEquals("0", fakeApiService.lastAllInsuranceParameters?.get("page"))
    }

    @Test
    fun getBranches_success_returnsBranchesList() = runTest {
        val expected = ListData(total = 1, list = listOf(BranchDTO(code = "010", name = "تهران")))
        fakeApiService.branchesResult = BaseDTO(status = 200, family = "OK", reason = "OK", data = expected)

        val result = dataSource.getBranches(ApiQueryParamDN())

        assertEquals(expected, result)
        assertEquals("0", fakeApiService.lastBranchesParameters?.get("page"))
    }

    @Test
    fun getJobs_success_returnsJobsList() = runTest {
        val expected = ListData(total = 1, list = listOf(JobDTO(jobCode = "2035", jobDescription = "قرص ساز")))
        fakeApiService.jobsResult = BaseDTO(status = 200, family = "OK", reason = "OK", data = expected)

        val result = dataSource.getJobs(ApiQueryParamDN())

        assertEquals(expected, result)
        assertEquals("0", fakeApiService.lastJobsParameters?.get("page"))
    }

    @Test
    fun submitInspectionRequest_success_returnsResponse() = runTest {
        val expected = SubmitInspectionRequestModelDTO()
        fakeApiService.submitResult = BaseDTO(status = 200, family = "OK", reason = "OK", data = expected)

        val result = dataSource.submitInspectionRequest(SubmitInspectionRequestDTO())

        assertEquals(expected, result)
    }

    @Test
    fun getAllInsurance_onNetworkError_throwsParsedTaminApiException() = runTest {
        fakeApiService.shouldThrowException = TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)

        val exception = assertFailsWith<TaminApiException> {
            dataSource.getAllInsurance(ApiQueryParamDN())
        }

        assertEquals("خطای اتصال", exception.title)
    }
}
