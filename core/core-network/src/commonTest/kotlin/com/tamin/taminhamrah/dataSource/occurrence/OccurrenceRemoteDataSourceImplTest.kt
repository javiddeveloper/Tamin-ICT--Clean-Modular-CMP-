package com.tamin.taminhamrah.dataSource.occurrence

import com.tamin.taminhamrah.apiService.occurrence.OccurrenceApiService
import com.tamin.taminhamrah.model.occurrence.InsuredRelationDTO
import com.tamin.taminhamrah.model.occurrence.OccurrenceDocTypeDTO
import com.tamin.taminhamrah.model.occurrence.OccurrencePersonalInfoDTO
import com.tamin.taminhamrah.model.occurrence.OccurrenceRequestDTO
import com.tamin.taminhamrah.model.occurrence.OccurrenceResponseDTO
import com.tamin.taminhamrah.model.occurrence.OccurrenceUploadImageResponseDTO
import com.tamin.taminhamrah.model.occurrence.WorkshopItemDTO
import com.tamin.taminhamrah.model.occurrence.WorkshopListItemDTO
import com.tamin.taminhamrah.model.utils.ListData
import com.tamin.taminhamrah.tools.BaseDTO
import com.tamin.taminhamrah.tools.apiQueryBuilder.ApiQueryBuilderImpl
import com.tamin.taminhamrah.tools.errorHandling.ErrorParserImpl
import com.tamin.taminhamrah.tools.errorHandling.ErrorUri
import com.tamin.taminhamrah.tools.errorHandling.TaminApiException
import com.tamin.taminhamrah.tools.errorHandling.TaminErrorUriException
import io.ktor.client.request.forms.MultiPartFormDataContent
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class FakeOccurrenceApiService : OccurrenceApiService {
    var personalInfoResult: BaseDTO<OccurrencePersonalInfoDTO> =
        BaseDTO(status = 200, family = "OK", reason = "OK", data = OccurrencePersonalInfoDTO())
    var allWorkshopsResult: BaseDTO<ListData<WorkshopListItemDTO>> =
        BaseDTO(status = 200, family = "OK", reason = "OK", data = ListData(list = emptyList()))
    var workshopSpecResult: BaseDTO<WorkshopItemDTO> =
        BaseDTO(status = 200, family = "OK", reason = "OK", data = WorkshopItemDTO())
    var insuredRelationResult: BaseDTO<InsuredRelationDTO> =
        BaseDTO(status = 200, family = "OK", reason = "OK", data = InsuredRelationDTO())
    var documentTypesResult: BaseDTO<ListData<OccurrenceDocTypeDTO>> =
        BaseDTO(status = 200, family = "OK", reason = "OK", data = ListData(list = emptyList()))
    var uploadImageResult: OccurrenceUploadImageResponseDTO = OccurrenceUploadImageResponseDTO(guid = "guid-1")
    var submitOccurrenceResult: BaseDTO<OccurrenceResponseDTO> =
        BaseDTO(status = 200, family = "OK", reason = "OK", data = OccurrenceResponseDTO())

    var shouldThrowException: Exception? = null
    var lastGetPersonalInfoQueries: Map<String, String>? = null
    var lastGetAllWorkshopsQueries: Map<String, String>? = null
    var lastGetWorkshopSpecQueries: Map<String, String>? = null
    var lastGetInsuredRelationQueries: Map<String, String>? = null
    var lastGetDocumentTypesQueries: Map<String, String>? = null
    var lastSubmitOccurrenceRequest: OccurrenceRequestDTO? = null

    override suspend fun getPersonalInfo(queries: Map<String, String>): BaseDTO<OccurrencePersonalInfoDTO> {
        shouldThrowException?.let { throw it }
        lastGetPersonalInfoQueries = queries
        return personalInfoResult
    }

    override suspend fun getAllWorkshops(
        queries: Map<String, String>,
    ): BaseDTO<ListData<WorkshopListItemDTO>> {
        shouldThrowException?.let { throw it }
        lastGetAllWorkshopsQueries = queries
        return allWorkshopsResult
    }

    override suspend fun getWorkshopSpec(queries: Map<String, String>): BaseDTO<WorkshopItemDTO> {
        shouldThrowException?.let { throw it }
        lastGetWorkshopSpecQueries = queries
        return workshopSpecResult
    }

    override suspend fun getInsuredRelation(queries: Map<String, String>): BaseDTO<InsuredRelationDTO> {
        shouldThrowException?.let { throw it }
        lastGetInsuredRelationQueries = queries
        return insuredRelationResult
    }

    override suspend fun getDocumentTypes(queries: Map<String, String>): BaseDTO<ListData<OccurrenceDocTypeDTO>> {
        shouldThrowException?.let { throw it }
        lastGetDocumentTypesQueries = queries
        return documentTypesResult
    }

    override suspend fun uploadImage(content: MultiPartFormDataContent): OccurrenceUploadImageResponseDTO {
        shouldThrowException?.let { throw it }
        return uploadImageResult
    }

    override suspend fun submitOccurrence(request: OccurrenceRequestDTO): BaseDTO<OccurrenceResponseDTO> {
        shouldThrowException?.let { throw it }
        lastSubmitOccurrenceRequest = request
        return submitOccurrenceResult
    }
}

class OccurrenceRemoteDataSourceImplTest {

    private lateinit var fakeApiService: FakeOccurrenceApiService
    private lateinit var dataSource: OccurrenceRemoteDataSourceImpl

    @BeforeTest
    fun setup() {
        fakeApiService = FakeOccurrenceApiService()
        dataSource = OccurrenceRemoteDataSourceImpl(
            apiService = fakeApiService,
            queryBuilder = ApiQueryBuilderImpl(),
            errorParser = ErrorParserImpl(),
        )
    }

    @Test
    fun getPersonalInfo_success_returnsDataAndBuildsFilterQuery() = runTest {
        val expected = OccurrencePersonalInfoDTO(nationalCode = "0012345678", firstName = "علی")
        fakeApiService.personalInfoResult = BaseDTO(status = 200, family = "OK", reason = "OK", data = expected)

        val result = dataSource.getPersonalInfo("0012345678", "1000", "1412345", "014")

        assertEquals(expected, result)
        assertEquals(setOf("page", "start", "limit", "filter", "sort"), fakeApiService.lastGetPersonalInfoQueries?.keys)
        assertEquals(true, fakeApiService.lastGetPersonalInfoQueries?.get("filter")?.contains("0012345678"))
    }

    @Test
    fun getAllWorkshops_success_returnsDataAndBuildsFilterQuery() = runTest {
        val expected = ListData(
            total = 1,
            list = listOf(WorkshopListItemDTO(workshopCode = "1412345", name = "کارگاه تولیدی الف", branchCode = "014")),
        )
        fakeApiService.allWorkshopsResult = BaseDTO(status = 200, family = "OK", reason = "OK", data = expected)

        val result = dataSource.getAllWorkshops("0012345678")

        assertEquals(expected, result)
        assertEquals(true, fakeApiService.lastGetAllWorkshopsQueries?.get("filter")?.contains("0012345678"))
    }

    @Test
    fun getWorkshopSpec_success_returnsData() = runTest {
        val expected = WorkshopItemDTO(workshopCode = "1412345", branchCode = "014")
        fakeApiService.workshopSpecResult = BaseDTO(status = 200, family = "OK", reason = "OK", data = expected)

        val result = dataSource.getWorkshopSpec("1412345", "014")

        assertEquals(expected, result)
    }

    @Test
    fun getInsuredRelation_usesNationalCodeAsWorkshopIdFilterValue() = runTest {
        val expected = InsuredRelationDTO(insuranceTypeCode = "01")
        fakeApiService.insuredRelationResult = BaseDTO(status = 200, family = "OK", reason = "OK", data = expected)

        val result = dataSource.getInsuredRelation("0012345678")

        assertEquals(expected, result)
        assertEquals(true, fakeApiService.lastGetInsuredRelationQueries?.get("filter")?.contains("0012345678"))
        assertEquals(true, fakeApiService.lastGetInsuredRelationQueries?.get("filter")?.contains("workshopId"))
    }

    @Test
    fun getDocumentTypes_success_returnsListData() = runTest {
        val expected = ListData(list = listOf(OccurrenceDocTypeDTO(docTypeId = "1", docDesc = "گزارش حادثه")))
        fakeApiService.documentTypesResult = BaseDTO(status = 200, family = "OK", reason = "OK", data = expected)

        val result = dataSource.getDocumentTypes()

        assertEquals(expected, result)
    }

    @Test
    fun uploadImage_success_returnsGuid() = runTest {
        fakeApiService.uploadImageResult = OccurrenceUploadImageResponseDTO(guid = "returned-guid")

        val result = dataSource.uploadImage("photo.jpg", byteArrayOf(1, 2, 3))

        assertEquals("returned-guid", result)
    }

    @Test
    fun uploadImage_whenGuidIsMissing_throwsParsedException() = runTest {
        fakeApiService.uploadImageResult = OccurrenceUploadImageResponseDTO(guid = null)

        assertFailsWith<TaminApiException> {
            dataSource.uploadImage("photo.jpg", byteArrayOf(1, 2, 3))
        }
    }

    @Test
    fun submitOccurrence_success_returnsMappedResponseAndForwardsRequest() = runTest {
        val request = OccurrenceRequestDTO(
            birthDate = "662688000", bossFullName = "شرکت الف", bossMobileNumber = "02112345678",
            branchCode = "10", branchName = "شعبه مرکزی", employeeDate = "662688000", gender = 1,
            insuranceID = "1234567", isuTypeDesc = "اصلی", isuTypecode = "01", jobDesc = "کارگر",
            marriageStatusCode = 1, nationCode = 1, occurrenceAddress = "طبقه دوم", occurrenceDate = "662688000",
            occurrenceDesc = "توضیحات", occurrenceDocumentList = emptyList(), occurrenceResult = 1,
            occurrenceTime = "10:30", pFirstName = "علی", pLastName = "رضایی", pNationalCode = "0012345678",
            reportAddress = "تهران", reportJobLocation = "خط تولید", reportPostalCode = "1234567891",
            reportTelephone = "02112345679", reporterType = "1", rwworkfinish = "16:00", rwworkstart = "08:00",
            vehicle = "شخصی", workshopAddress = "تهران", workshopBranchCode = "014", workshopCode = "1412345",
            workshopName = "کارگاه الف", workshopPostalCode = "1234567890", workshopTelephone = "02112345678",
        )
        fakeApiService.submitOccurrenceResult = BaseDTO(
            status = 200, family = "OK", reason = "OK",
            data = OccurrenceResponseDTO(reportRefrenceNumber = "TRACK-1"),
        )

        val result = dataSource.submitOccurrence(request)

        assertEquals("TRACK-1", result.reportRefrenceNumber)
        assertEquals(request, fakeApiService.lastSubmitOccurrenceRequest)
    }

    @Test
    fun getDocumentTypes_onGenericError_throwsParsedNoConnectionException() = runTest {
        fakeApiService.shouldThrowException = RuntimeException("boom")

        val exception = assertFailsWith<TaminApiException> { dataSource.getDocumentTypes() }

        assertEquals("خطای اتصال", exception.title)
    }

    @Test
    fun getWorkshopSpec_onTaminErrorUri_propagatesParsedException() = runTest {
        fakeApiService.shouldThrowException = TaminErrorUriException(ErrorUri.RESOURCE_NOT_FOUND)

        val exception = assertFailsWith<TaminApiException> { dataSource.getWorkshopSpec("1412345", "014") }

        assertEquals("یافت نشد", exception.title)
    }
}
