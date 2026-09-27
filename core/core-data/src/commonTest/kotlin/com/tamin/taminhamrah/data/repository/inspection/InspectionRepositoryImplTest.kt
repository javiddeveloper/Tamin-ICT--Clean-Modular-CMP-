package com.tamin.taminhamrah.data.repository.inspection

import com.tamin.taminhamrah.data.local.dao.InspectionDao
import com.tamin.taminhamrah.data.local.entity.InspectionBranchPageEntity
import com.tamin.taminhamrah.data.local.entity.InspectionJobPageEntity
import com.tamin.taminhamrah.data.local.entity.InspectionPerformedPageEntity
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
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FakeInspectionRemoteDataSource : InspectionRemoteDataSource {
    var allInsuranceResult: ListData<InspectionPerformedDTO> = ListData(total = 0, list = emptyList())
    var allManagerResult: ListData<InspectionPerformedDTO> = ListData(total = 0, list = emptyList())
    var branchesResult: ListData<BranchDTO> = ListData(total = 0, list = emptyList())
    var jobsResult: ListData<JobDTO> = ListData(total = 0, list = emptyList())
    var submitResult: SubmitInspectionRequestModelDTO = SubmitInspectionRequestModelDTO()
    var reportPdfResult: PdfDownloadDTO = PdfDownloadDTO(pdf = InputStreamDTO(pdf = null))

    var shouldThrowError: Exception? = null
    var lastAllInsuranceQuery: ApiQueryParamDN? = null
    var lastAllManagerQuery: ApiQueryParamDN? = null
    var lastBranchesQuery: ApiQueryParamDN? = null
    var lastJobsQuery: ApiQueryParamDN? = null
    var lastSubmitRequest: SubmitInspectionRequestDTO? = null
    var lastReportPdfInspectionNo: String? = null

    override suspend fun getAllInsurance(query: ApiQueryParamDN): ListData<InspectionPerformedDTO> {
        shouldThrowError?.let { throw it }
        lastAllInsuranceQuery = query
        return allInsuranceResult
    }

    override suspend fun getAllManager(query: ApiQueryParamDN): ListData<InspectionPerformedDTO> {
        shouldThrowError?.let { throw it }
        lastAllManagerQuery = query
        return allManagerResult
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
    private lateinit var dao: FakeInspectionDao
    private lateinit var repository: InspectionRepositoryImpl

    @BeforeTest
    fun setup() {
        remoteDataSource = FakeInspectionRemoteDataSource()
        dao = FakeInspectionDao()
        repository = InspectionRepositoryImpl(remoteDataSource, dao)
    }

    private fun inspections(vararg numbers: String) =
        ListData(total = 50, list = numbers.map { InspectionPerformedDTO(inspectionNo = it) })

    @Test
    fun getInsurancePage_cachedThenNetwork() = runTest {
        remoteDataSource.allInsuranceResult = inspections("1")
        repository.getInsurancePage(ApiQueryParamDN()).toList()
        remoteDataSource.allInsuranceResult = inspections("2")

        val emissions = repository.getInsurancePage(ApiQueryParamDN()).toList()

        assertEquals(listOf("1"), emissions[0].items.map { it.inspectionNo })
        assertTrue(emissions[0].isFromCache)
        assertEquals(listOf("2"), emissions[1].items.map { it.inspectionNo })
        assertFalse(emissions[1].isFromCache)
    }

    @Test
    fun getInsurancePage_offline_servesCachedPagesInServerOrder() = runTest {
        remoteDataSource.allInsuranceResult = inspections("9", "3")
        repository.getInsurancePage(ApiQueryParamDN(start = 0, limit = 2)).toList()
        remoteDataSource.allInsuranceResult = inspections("7", "1")
        repository.getInsurancePage(ApiQueryParamDN(start = 2, limit = 2)).toList()
        remoteDataSource.shouldThrowError = RuntimeException("offline")

        val offline = repository.getInsurancePage(ApiQueryParamDN(start = 2, limit = 2)).toList()

        assertEquals(1, offline.size)
        assertEquals(listOf("7", "1"), offline.single().items.map { it.inspectionNo })
        assertTrue(offline.single().isFromCache)
    }

    @Test
    fun getInsurancePage_firstPageReplacesOnlyItsOwnList() = runTest {
        remoteDataSource.allManagerResult = inspections("M")
        repository.getWorkshopInspectionsPage(ApiQueryParamDN()).toList()
        remoteDataSource.allInsuranceResult = inspections("1", "2")
        repository.getInsurancePage(ApiQueryParamDN()).toList()
        // Insurance list refreshed with fewer rows: its stale row goes, the workshop list stays.
        remoteDataSource.allInsuranceResult = inspections("3")
        repository.getInsurancePage(ApiQueryParamDN()).toList()
        remoteDataSource.shouldThrowError = RuntimeException("offline")

        assertEquals(listOf("3"), repository.getInsurancePage(ApiQueryParamDN()).first().items.map { it.inspectionNo })
        assertEquals(listOf("M"), repository.getWorkshopInspectionsPage(ApiQueryParamDN()).first().items.map { it.inspectionNo })
    }

    @Test
    fun getBranchesPage_and_getJobsPage_serveTheCacheOffline() = runTest {
        remoteDataSource.branchesResult = ListData(total = 1, list = listOf(BranchDTO(code = "0010", name = "تهران")))
        remoteDataSource.jobsResult = ListData(total = 1, list = listOf(JobDTO(jobCode = "2035", jobDescription = "قرص ساز")))
        val networkBranch = repository.getBranchesPage(ApiQueryParamDN()).first().items.single()
        val networkJob = repository.getJobsPage(ApiQueryParamDN()).first().items.single()
        remoteDataSource.shouldThrowError = RuntimeException("offline")

        assertEquals(networkBranch, repository.getBranchesPage(ApiQueryParamDN()).first().items.single())
        assertEquals(networkJob, repository.getJobsPage(ApiQueryParamDN()).first().items.single())
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
    fun getWorkshopInspectionsPage_success_emitsMappedPageWithTotal() = runTest {
        remoteDataSource.allManagerResult = ListData(
            total = 3,
            list = listOf(InspectionPerformedDTO(activityDesc = "فعالیت کارگاه"))
        )
        val query = ApiQueryParamDN(page = 1, start = 10, limit = 10)

        val page = repository.getWorkshopInspectionsPage(query).first()

        assertEquals(3, page.total)
        assertEquals(1, page.items.size)
        assertEquals("فعالیت کارگاه", page.items.first().activityDesc)
        assertEquals(query, remoteDataSource.lastAllManagerQuery)
    }

    @Test
    fun getWorkshopInspectionsPage_onError_throwsParsedException() = runTest {
        val expectedError = RuntimeException("Network Error")
        remoteDataSource.shouldThrowError = expectedError

        val actualError = assertFailsWith<RuntimeException> {
            repository.getWorkshopInspectionsPage(ApiQueryParamDN()).first()
        }

        assertEquals(expectedError.message, actualError.message)
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

/** Page tables keyed like Room's composite primary key (listKey, position). */
private class FakeInspectionDao : InspectionDao {
    private val inspections = mutableListOf<InspectionPerformedPageEntity>()
    private val branches = mutableListOf<InspectionBranchPageEntity>()
    private val jobs = mutableListOf<InspectionJobPageEntity>()

    override suspend fun getInspectionsSlice(listKey: String, limit: Int, offset: Int) =
        inspections.slice(listKey, limit, offset) { it.listKey to it.position }
    override suspend fun upsertInspections(rows: List<InspectionPerformedPageEntity>) =
        inspections.upsert(rows) { it.listKey to it.position }
    override suspend fun clearInspections(listKey: String) {
        inspections.removeAll { it.listKey == listKey }
    }

    override suspend fun getBranchesSlice(listKey: String, limit: Int, offset: Int) =
        branches.slice(listKey, limit, offset) { it.listKey to it.position }
    override suspend fun upsertBranches(rows: List<InspectionBranchPageEntity>) =
        branches.upsert(rows) { it.listKey to it.position }
    override suspend fun clearBranches(listKey: String) {
        branches.removeAll { it.listKey == listKey }
    }

    override suspend fun getJobsSlice(listKey: String, limit: Int, offset: Int) =
        jobs.slice(listKey, limit, offset) { it.listKey to it.position }
    override suspend fun upsertJobs(rows: List<InspectionJobPageEntity>) =
        jobs.upsert(rows) { it.listKey to it.position }
    override suspend fun clearJobs(listKey: String) {
        jobs.removeAll { it.listKey == listKey }
    }

    private fun <E> List<E>.slice(listKey: String, limit: Int, offset: Int, key: (E) -> Pair<String, Int>): List<E> =
        filter { key(it).first == listKey }.sortedBy { key(it).second }.drop(offset).take(limit)

    private fun <E> MutableList<E>.upsert(rows: List<E>, key: (E) -> Pair<String, Int>) {
        val incoming = rows.map(key).toSet()
        removeAll { key(it) in incoming }
        addAll(rows)
    }
}
