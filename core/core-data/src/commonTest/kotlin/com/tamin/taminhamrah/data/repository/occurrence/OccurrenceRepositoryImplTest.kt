package com.tamin.taminhamrah.data.repository.occurrence

import com.tamin.taminhamrah.dataSource.occurrence.OccurrenceRemoteDataSource
import com.tamin.taminhamrah.model.occurrence.InsuredRelationDTO
import com.tamin.taminhamrah.model.occurrence.OccurrenceDocTypeDTO
import com.tamin.taminhamrah.model.occurrence.OccurrencePersonalInfoDTO
import com.tamin.taminhamrah.model.occurrence.OccurrenceRequestDTO
import com.tamin.taminhamrah.model.occurrence.OccurrenceResponseDTO
import com.tamin.taminhamrah.model.occurrence.OccurrenceSubmitRequestDN
import com.tamin.taminhamrah.model.occurrence.WorkshopItemDTO
import com.tamin.taminhamrah.model.utils.ListData
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class FakeOccurrenceRemoteDataSource : OccurrenceRemoteDataSource {
    var personalInfoResult: OccurrencePersonalInfoDTO = OccurrencePersonalInfoDTO()
    var allWorkshopsResult: ListData<WorkshopItemDTO> = ListData(list = emptyList())
    var workshopSpecResult: WorkshopItemDTO = WorkshopItemDTO()
    var insuredRelationResult: InsuredRelationDTO = InsuredRelationDTO()
    var documentTypesResult: ListData<OccurrenceDocTypeDTO> = ListData(list = emptyList())
    var uploadImageResult: String = "guid-1"
    var submitOccurrenceResult: OccurrenceResponseDTO = OccurrenceResponseDTO()

    var shouldThrowError: Exception? = null
    var lastUploadImageParams: Pair<String, ByteArray>? = null
    var lastSubmitRequest: OccurrenceRequestDTO? = null

    override suspend fun getPersonalInfo(
        nationalCode: String,
        birthDate: String,
        workshopCode: String,
        branchCode: String,
    ): OccurrencePersonalInfoDTO {
        shouldThrowError?.let { throw it }
        return personalInfoResult
    }

    override suspend fun getAllWorkshops(nationalCode: String): ListData<WorkshopItemDTO> {
        shouldThrowError?.let { throw it }
        return allWorkshopsResult
    }

    override suspend fun getWorkshopSpec(workshopCode: String, branchCode: String): WorkshopItemDTO {
        shouldThrowError?.let { throw it }
        return workshopSpecResult
    }

    override suspend fun getInsuredRelation(nationalCode: String): InsuredRelationDTO {
        shouldThrowError?.let { throw it }
        return insuredRelationResult
    }

    override suspend fun getDocumentTypes(): ListData<OccurrenceDocTypeDTO> {
        shouldThrowError?.let { throw it }
        return documentTypesResult
    }

    override suspend fun uploadImage(fileName: String, fileBytes: ByteArray): String {
        shouldThrowError?.let { throw it }
        lastUploadImageParams = fileName to fileBytes
        return uploadImageResult
    }

    override suspend fun submitOccurrence(request: OccurrenceRequestDTO): OccurrenceResponseDTO {
        shouldThrowError?.let { throw it }
        lastSubmitRequest = request
        return submitOccurrenceResult
    }
}

class OccurrenceRepositoryImplTest {

    private lateinit var remoteDataSource: FakeOccurrenceRemoteDataSource
    private lateinit var repository: OccurrenceRepositoryImpl

    @BeforeTest
    fun setup() {
        remoteDataSource = FakeOccurrenceRemoteDataSource()
        repository = OccurrenceRepositoryImpl(remoteDataSource)
    }

    @Test
    fun getPersonalInfo_returnsMappedDomainModel() = runTest {
        remoteDataSource.personalInfoResult = OccurrencePersonalInfoDTO(
            nationalCode = "0012345678",
            firstName = "علی",
            lastName = "رضایی",
        )

        val result = repository.getPersonalInfo("0012345678", "1000", "1412345", "014")

        assertEquals("0012345678", result.nationalCode)
        assertEquals("علی رضایی", result.fullName)
    }

    @Test
    fun getAllWorkshops_returnsMappedList() = runTest {
        remoteDataSource.allWorkshopsResult = ListData(
            list = listOf(WorkshopItemDTO(id = "1", workshopCode = "1412345", branchCode = "014"))
        )

        val result = repository.getAllWorkshops("0012345678")

        assertEquals(1, result.size)
        assertEquals("1412345", result.first().workshopCode)
    }

    @Test
    fun getAllWorkshops_whenListIsNull_returnsEmptyList() = runTest {
        remoteDataSource.allWorkshopsResult = ListData(list = null)

        val result = repository.getAllWorkshops("0012345678")

        assertTrue(result.isEmpty())
    }

    @Test
    fun getWorkshopSpec_returnsMappedDomainModel() = runTest {
        remoteDataSource.workshopSpecResult = WorkshopItemDTO(workshopCode = "1412345", branchCode = "014")

        val result = repository.getWorkshopSpec("1412345", "014")

        assertEquals("1412345", result.workshopCode)
    }

    @Test
    fun getInsuredRelation_returnsMappedDomainModel() = runTest {
        remoteDataSource.insuredRelationResult = InsuredRelationDTO(
            insuranceTypeCode = "01", insuranceType = "اصلی", branchCode = "10", branchName = "شعبه مرکزی",
        )

        val result = repository.getInsuredRelation("0012345678")

        assertEquals("01", result.insuranceTypeCode)
        assertEquals("شعبه مرکزی", result.branchName)
    }

    @Test
    fun getDocumentTypes_returnsMappedList() = runTest {
        remoteDataSource.documentTypesResult = ListData(
            list = listOf(OccurrenceDocTypeDTO(docTypeId = "1", docDesc = "گزارش حادثه"))
        )

        val result = repository.getDocumentTypes()

        assertEquals(1, result.size)
        assertEquals(1, result.first().id)
        assertEquals("گزارش حادثه", result.first().title)
    }

    @Test
    fun getDocumentTypes_whenListIsNull_returnsEmptyList() = runTest {
        remoteDataSource.documentTypesResult = ListData(list = null)

        val result = repository.getDocumentTypes()

        assertTrue(result.isEmpty())
    }

    @Test
    fun uploadImage_forwardsFileNameAndBytesAndReturnsGuid() = runTest {
        remoteDataSource.uploadImageResult = "returned-guid"
        val bytes = byteArrayOf(1, 2, 3)

        val result = repository.uploadImage("photo.jpg", bytes)

        assertEquals("returned-guid", result)
        val (fileName, fileBytes) = requireNotNull(remoteDataSource.lastUploadImageParams)
        assertEquals("photo.jpg", fileName)
        assertEquals(bytes.toList(), fileBytes.toList())
    }

    @Test
    fun submitOccurrence_mapsRequestToDTOAndReturnsMappedResult() = runTest {
        remoteDataSource.submitOccurrenceResult = OccurrenceResponseDTO(reportRefrenceNumber = "TRACK-1")

        val result = repository.submitOccurrence(sampleSubmitRequest())

        assertEquals("TRACK-1", result.trackingCode)
        assertEquals("0012345678", remoteDataSource.lastSubmitRequest?.pNationalCode)
    }

    @Test
    fun getDocumentTypes_onError_propagatesException() = runTest {
        val expectedError = RuntimeException("Network failure")
        remoteDataSource.shouldThrowError = expectedError

        val actualError = assertFailsWith<RuntimeException> { repository.getDocumentTypes() }

        assertEquals(expectedError.message, actualError.message)
    }

    private fun sampleSubmitRequest() = OccurrenceSubmitRequestDN(
        nationalCode = "0012345678",
        firstName = "علی",
        lastName = "رضایی",
        gender = 1,
        nationalityCode = 1,
        insuranceType = "اصلی",
        insuranceTypeCode = "01",
        insuranceNumber = "1234567",
        branchCode = "10",
        branchName = "شعبه مرکزی",
        birthDate = 662688000L,
        workshopId = "1412345",
        workshopBranchCode = "014",
        workshopName = "کارگاه الف",
        employerName = "شرکت الف",
        employerPhone = "02112345678",
        workshopAddress = "تهران",
        workshopPostalCode = "1234567890",
        workshopPhone = "02112345678",
        employmentDate = 662688000L,
        maritalStatus = 1,
        jobTitle = "کارگر",
        workLocation = "خط تولید",
        transportation = "شخصی",
        workStartTime = "08:00",
        workEndTime = "16:00",
        homeAddress = "تهران",
        homePhone = "02112345679",
        homePostalCode = "1234567891",
        accidentDate = 662688000L,
        accidentTime = "10:30",
        accidentOutcomeId = 1,
        exactLocation = "طبقه دوم",
        description = "توضیحات حادثه",
        reporterType = "1",
        documents = emptyList(),
    )
}
