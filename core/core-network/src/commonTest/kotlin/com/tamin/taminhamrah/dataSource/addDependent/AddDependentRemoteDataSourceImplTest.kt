package com.tamin.taminhamrah.dataSource.addDependent

import com.tamin.taminhamrah.apiService.addDependent.AddDependentApiService
import com.tamin.taminhamrah.model.addDependent.BranchDto
import com.tamin.taminhamrah.model.addDependent.FamilyRelationshipDto
import com.tamin.taminhamrah.model.addDependent.GeneralResponseDto
import com.tamin.taminhamrah.model.addDependent.RegistryDataDto
import com.tamin.taminhamrah.model.addDependent.RequestAddDependentDto
import com.tamin.taminhamrah.model.addDependent.UploadImageResponseDto
import com.tamin.taminhamrah.tools.BaseDTO
import com.tamin.taminhamrah.tools.errorHandling.ErrorParserImpl
import com.tamin.taminhamrah.tools.errorHandling.ErrorUri
import com.tamin.taminhamrah.tools.errorHandling.TaminApiException
import com.tamin.taminhamrah.tools.errorHandling.TaminErrorUriException
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class FakeAddDependentApiService : AddDependentApiService {
    var activeBranchesResult: BaseDTO<List<BranchDto>> = BaseDTO(status = 200, family = "OK", reason = "OK", data = emptyList())
    var familyRelationshipsResult: BaseDTO<List<FamilyRelationshipDto>> = BaseDTO(status = 200, family = "OK", reason = "OK", data = emptyList())
    var registryDataResult: BaseDTO<RegistryDataDto> = BaseDTO(status = 200, family = "OK", reason = "OK", data = RegistryDataDto())
    var educationCodeResult: BaseDTO<String> = BaseDTO(status = 200, family = "OK", reason = "OK", data = "OK")
    var uploadImageResult: BaseDTO<UploadImageResponseDto> = BaseDTO(status = 200, family = "OK", reason = "OK", data = UploadImageResponseDto())
    var addNewDependentResult: BaseDTO<GeneralResponseDto> = BaseDTO(status = 200, family = "OK", reason = "OK", data = GeneralResponseDto())

    var shouldThrowException: Exception? = null

    override suspend fun getActiveBranches(): BaseDTO<List<BranchDto>> {
        shouldThrowException?.let { throw it }
        return activeBranchesResult
    }

    override suspend fun getFamilyRelationships(queryJson: String?): BaseDTO<List<FamilyRelationshipDto>> {
        shouldThrowException?.let { throw it }
        return familyRelationshipsResult
    }

    override suspend fun inquiryRegistry(
        dependentNationalId: String,
        birthDateTimeStamp: String,
        dependencyCode: String
    ): BaseDTO<RegistryDataDto> {
        shouldThrowException?.let { throw it }
        return registryDataResult
    }

    override suspend fun inquiryEducationCode(
        nationalId: String,
        educationCode: String
    ): BaseDTO<String> {
        shouldThrowException?.let { throw it }
        return educationCodeResult
    }

    override suspend fun uploadImage(imageBytes: ByteArray): BaseDTO<UploadImageResponseDto> {
        shouldThrowException?.let { throw it }
        return uploadImageResult
    }

    override suspend fun addNewDependent(request: RequestAddDependentDto): BaseDTO<GeneralResponseDto> {
        shouldThrowException?.let { throw it }
        return addNewDependentResult
    }
}

class AddDependentRemoteDataSourceImplTest {

    private lateinit var fakeApiService: FakeAddDependentApiService
    private lateinit var dataSource: AddDependentRemoteDataSourceImpl

    @BeforeTest
    fun setup() {
        fakeApiService = FakeAddDependentApiService()
        dataSource = AddDependentRemoteDataSourceImpl(
            apiService = fakeApiService,
            errorParser = ErrorParserImpl()
        )
    }

    @Test
    fun getActiveBranches_success_returnsBranchesList() = runTest {
        val expectedBranches = listOf(BranchDto(branchCode = "01", branchName = "مرکزی"))
        fakeApiService.activeBranchesResult = BaseDTO(status = 200, family = "OK", reason = "OK", data = expectedBranches)

        val result = dataSource.getActiveBranches()

        assertEquals(expectedBranches, result)
    }

    @Test
    fun getFamilyRelationships_success_returnsRelationshipsList() = runTest {
        val expectedRelationships = listOf(FamilyRelationshipDto(relationCode = "01", relationDesc = "فرزند"))
        fakeApiService.familyRelationshipsResult = BaseDTO(status = 200, family = "OK", reason = "OK", data = expectedRelationships)

        val result = dataSource.getFamilyRelationships(queryJson = null)

        assertEquals(expectedRelationships, result)
    }

    @Test
    fun inquiryRegistry_success_returnsRegistryData() = runTest {
        val expectedData = RegistryDataDto(firstName = "احمد", lastName = "محمدی")
        fakeApiService.registryDataResult = BaseDTO(status = 200, family = "OK", reason = "OK", data = expectedData)

        val result = dataSource.inquiryRegistry("1234567890", "1000000", "01")

        assertEquals(expectedData, result)
    }

    @Test
    fun inquiryEducationCode_success_returnsString() = runTest {
        fakeApiService.educationCodeResult = BaseDTO(status = 200, family = "OK", reason = "OK", data = "معتبر")

        val result = dataSource.inquiryEducationCode("1234567890", "EDU01")

        assertEquals("معتبر", result)
    }

    @Test
    fun uploadImage_success_returnsUploadResponse() = runTest {
        val expectedResponse = UploadImageResponseDto(guid = "FID123", isSuccess = true)
        fakeApiService.uploadImageResult = BaseDTO(status = 200, family = "OK", reason = "OK", data = expectedResponse)

        val result = dataSource.uploadImage(byteArrayOf(1, 2, 3), "img.png", "image/png")

        assertEquals(expectedResponse, result)
    }

    @Test
    fun addNewDependent_success_returnsGeneralResponse() = runTest {
        val expectedResponse = GeneralResponseDto(isSuccess = true, message = "انجام شد")
        fakeApiService.addNewDependentResult = BaseDTO(status = 200, family = "OK", reason = "OK", data = expectedResponse)

        val result = dataSource.addNewDependent(RequestAddDependentDto())

        assertEquals(expectedResponse, result)
    }

    @Test
    fun getActiveBranches_onNetworkError_throwsParsedTaminApiException() = runTest {
        fakeApiService.shouldThrowException = TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)

        val exception = assertFailsWith<TaminApiException> {
            dataSource.getActiveBranches()
        }

        assertEquals("خطای اتصال", exception.title)
    }
}
