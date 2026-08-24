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
    fun getAllInsurance_success_emitsMappedInspectionPerformedListDN() = runTest {
        remoteDataSource.allInsuranceResult = ListData(
            total = 1,
            list = listOf(InspectionPerformedDTO(activityDesc = "فعالیت تست"))
        )

        val result = repository.getAllInsurance(emptyList())

        assertEquals(1, result.total)
        assertEquals(1, result.list.size)
        assertEquals("فعالیت تست", result.list.first().activityDesc)
        assertEquals(100, remoteDataSource.lastAllInsuranceQuery?.limit)
    }

    @Test
    fun getBranches_success_emitsMappedBranchListDN() = runTest {
        remoteDataSource.branchesResult = ListData(
            total = 1,
            list = listOf(BranchDTO(code = "0010", name = "تهران"))
        )

        val result = repository.getBranches(emptyList())

        assertEquals(1, result.total)
        assertEquals(1, result.list.size)
        assertEquals("0010", result.list.first().code)
        assertEquals("تهران", result.list.first().name)
        assertEquals(100, remoteDataSource.lastBranchesQuery?.limit)
    }

    @Test
    fun getJobs_success_emitsMappedJobListDN() = runTest {
        remoteDataSource.jobsResult = ListData(
            total = 1,
            list = listOf(JobDTO(jobCode = "2035", jobDescription = "قرص ساز"))
        )

        val result = repository.getJobs(emptyList())

        assertEquals(1, result.total)
        assertEquals(1, result.list.size)
        assertEquals("2035", result.list.first().jobCode)
        assertEquals("قرص ساز", result.list.first().jobDescription)
        assertEquals(100, remoteDataSource.lastJobsQuery?.limit)
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
    fun getAllInsurance_onError_throwsParsedException() = runTest {
        val expectedError = RuntimeException("Network Error")
        remoteDataSource.shouldThrowError = expectedError

        val actualError = assertFailsWith<RuntimeException> {
            repository.getAllInsurance(emptyList())
        }

        assertEquals(expectedError.message, actualError.message)
    }
}
