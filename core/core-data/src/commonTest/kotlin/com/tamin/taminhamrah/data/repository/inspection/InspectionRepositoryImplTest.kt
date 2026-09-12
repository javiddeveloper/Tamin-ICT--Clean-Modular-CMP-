package com.tamin.taminhamrah.data.repository.inspection

import com.tamin.taminhamrah.data.repository.InspectionRepositoryImpl
import com.tamin.taminhamrah.dataSource.inspection.InspectionRemoteDataSource
import com.tamin.taminhamrah.model.inspection.BranchDTO
import com.tamin.taminhamrah.model.inspection.InspectionPerformedDTO
import com.tamin.taminhamrah.model.inspection.JobDTO
import com.tamin.taminhamrah.model.inspection.SubmitInspectionRequestDN
import com.tamin.taminhamrah.model.inspection.SubmitInspectionRequestDTO
import com.tamin.taminhamrah.model.inspection.SubmitInspectionRequestModelDTO
import com.tamin.taminhamrah.model.inspection.RequestSubmitInspectionDTO
import com.tamin.taminhamrah.model.personal.pdfDownload.InputStreamDTO
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDTO
import com.tamin.taminhamrah.model.utils.ListData
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class FakeInspectionRemoteDataSource : InspectionRemoteDataSource {
    var allInsuranceResult: ListData<InspectionPerformedDTO> = ListData(total = 0, list = emptyList())
    var branchesResult: ListData<BranchDTO> = ListData(total = 0, list = emptyList())
    var jobsResult: ListData<JobDTO> = ListData(total = 0, list = emptyList())
    var submitResult: SubmitInspectionRequestModelDTO = SubmitInspectionRequestModelDTO()
    var reportPdfResult: PdfDownloadDTO = PdfDownloadDTO(pdf = InputStreamDTO(pdf = null))

    var shouldThrowError: Exception? = null
    var lastAllInsuranceQuery: ApiQueryParamDN? = null
    var lastBranchesQuery: ApiQueryParamDN? = null
    var lastJobsQuery: ApiQueryParamDN? = null
    var lastSubmitRequest: SubmitInspectionRequestDTO? = null
    var lastReportPdfInspectionNo: String? = null

    override suspend fun getAllInsurance(query: ApiQueryParamDN): ListData<InspectionPerformedDTO> {
        shouldThrowError?.let { throw it }
        lastAllInsuranceQuery = query
        return allInsuranceResult
    }

    override suspend fun getBranches(query: ApiQueryParamDN): ListData<BranchDTO> {
        shouldThrowError?.let { throw it }
        lastBranchesQuery = query
        return branchesResult
    }

    override suspend fun getJobs(query: ApiQueryParamDN): ListData<JobDTO> {
        shouldThrowError?.let { throw it }
        lastJobsQuery = query
        return jobsResult
    }

    override suspend fun submitInspectionRequest(request: SubmitInspectionRequestDTO): SubmitInspectionRequestModelDTO {
        shouldThrowError?.let { throw it }
        lastSubmitRequest = request
        return submitResult
    }

    override suspend fun getInspectionReportPDF(inspectionNo: String): PdfDownloadDTO {
        shouldThrowError?.let { throw it }
        lastReportPdfInspectionNo = inspectionNo
        return reportPdfResult
    }
}

class InspectionRepositoryImplTest {

    private lateinit var remoteDataSource: FakeInspectionRemoteDataSource
    private lateinit var repository: InspectionRepositoryImpl

    @BeforeTest
    fun setup() {
        remoteDataSource = FakeInspectionRemoteDataSource()
        repository = InspectionRepositoryImpl(remoteDataSource)
    }

    @Test
    fun getInsurancePage_success_emitsMappedPageWithTotal() = runTest {
        remoteDataSource.allInsuranceResult = ListData(
            total = 42,
            list = listOf(InspectionPerformedDTO(activityDesc = "فعالیت تست"))
        )
        val query = ApiQueryParamDN(page = 1, start = 10, limit = 10)

        val page = repository.getInsurancePage(query).first()

        assertEquals(42, page.total)
        assertEquals(1, page.items.size)
        assertEquals("فعالیت تست", page.items.first().activityDesc)
        assertEquals(query, remoteDataSource.lastAllInsuranceQuery)
    }

    @Test
    fun getBranchesPage_success_emitsMappedPageWithTotal() = runTest {
        remoteDataSource.branchesResult = ListData(
            total = 5,
            list = listOf(BranchDTO(code = "0010", name = "تهران"))
        )
        val query = ApiQueryParamDN(page = 0, start = 0, limit = 10)

        val page = repository.getBranchesPage(query).first()

        assertEquals(5, page.total)
        assertEquals(1, page.items.size)
        assertEquals("0010", page.items.first().code)
        assertEquals("تهران", page.items.first().name)
        assertEquals(query, remoteDataSource.lastBranchesQuery)
    }

    @Test
    fun getJobsPage_success_emitsMappedPageWithTotal() = runTest {
        remoteDataSource.jobsResult = ListData(
            total = 1,
            list = listOf(JobDTO(jobCode = "2035", jobDescription = "قرص ساز"))
        )
        val query = ApiQueryParamDN(page = 2, start = 20, limit = 10)

        val page = repository.getJobsPage(query).first()

        assertEquals(1, page.total)
        assertEquals(1, page.items.size)
        assertEquals("2035", page.items.first().jobCode)
        assertEquals("قرص ساز", page.items.first().jobDescription)
        assertEquals(query, remoteDataSource.lastJobsQuery)
    }

    @Test
    fun submitInspectionRequest_success_emitsMappedResultDN() = runTest {
        remoteDataSource.submitResult = SubmitInspectionRequestModelDTO(
            request = RequestSubmitInspectionDTO(id = 12345L)
        )

        val result = repository.submitInspectionRequest(
            SubmitInspectionRequestDN(
                brchCode = "0010",
                endDate = 0L,
                inspectionNumberOld = "",
                insuranceId = "",
                insuranceJob = "",
                requestDescription = "",
                startDate = 0L,
                workshopAddress = "",
                workshopManager = "",
                workshopName = "",
                workshopNumber = "",
                workshopTel = ""
            )
        )

        assertEquals(12345L, result.id)
        assertEquals("0010", remoteDataSource.lastSubmitRequest?.brchCode)
    }

    @Test
    fun getInspectionReportPDF_success_emitsMappedPdfDownloadDN() = runTest {
        remoteDataSource.reportPdfResult = PdfDownloadDTO(pdf = InputStreamDTO(pdf = null))

        val result = repository.getInspectionReportPDF("0130980012641")

        assertEquals(null, result.pdf?.pdf)
        assertEquals("0130980012641", remoteDataSource.lastReportPdfInspectionNo)
    }

    @Test
    fun getInsurancePage_onError_throwsParsedException() = runTest {
        val expectedError = RuntimeException("Network Error")
        remoteDataSource.shouldThrowError = expectedError

        val actualError = assertFailsWith<RuntimeException> {
            repository.getInsurancePage(ApiQueryParamDN()).first()
        }

        assertEquals(expectedError.message, actualError.message)
    }
}
