package com.tamin.taminhamrah.dataSource.addDependent

import com.tamin.taminhamrah.apiService.addDependent.AddDependentApiService
import com.tamin.taminhamrah.model.addDependent.BranchDTO
import com.tamin.taminhamrah.model.addDependent.DependentInfoDTO
import com.tamin.taminhamrah.model.addDependent.FamilyRelationshipDTO
import com.tamin.taminhamrah.model.addDependent.FamilyRelationshipProxyDTO
import com.tamin.taminhamrah.model.addDependent.GeneralResponseDTO
import com.tamin.taminhamrah.model.addDependent.RegistryDataDTO
import com.tamin.taminhamrah.model.addDependent.RequestAddDependentDTO
import com.tamin.taminhamrah.model.addDependent.UploadImageResponseDTO
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.FilterOperator
import com.tamin.taminhamrah.model.request.FilterProperty
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

class FakeAddDependentApiService : AddDependentApiService {
    var dependentInfoResult: BaseDTO<List<DependentInfoDTO>> = BaseDTO(status = 200, family = "OK", reason = "OK", data = emptyList())
    var activeBranchesResult: BaseDTO<List<BranchDTO>> = BaseDTO(status = 200, family = "OK", reason = "OK", data = emptyList())
    var familyRelationshipsResult: BaseDTO<List<FamilyRelationshipDTO>> = BaseDTO(status = 200, family = "OK", reason = "OK", data = emptyList())
    var familyRelationshipsFromProxyResult: BaseDTO<ListData<FamilyRelationshipProxyDTO>> = BaseDTO(status = 200, family = "OK", reason = "OK", data = ListData(list = emptyList()))
    var registryDataResult: BaseDTO<RegistryDataDTO> = BaseDTO(status = 200, family = "OK", reason = "OK", data = RegistryDataDTO())
    var educationCodeResult: BaseDTO<String> = BaseDTO(status = 200, family = "OK", reason = "OK", data = "OK")
    var uploadImageResult: UploadImageResponseDTO = UploadImageResponseDTO()
    var addNewDependentResult: BaseDTO<GeneralResponseDTO> = BaseDTO(status = 200, family = "OK", reason = "OK", data = GeneralResponseDTO())

    var shouldThrowException: Exception? = null
    var lastFamilyRelationshipsParameters: Map<String, String>? = null
    var lastFamilyRelationshipsFromProxyParameters: Map<String, String>? = null

    override suspend fun getDependentInfo(): BaseDTO<List<DependentInfoDTO>> {
        shouldThrowException?.let { throw it }
        return dependentInfoResult
    }

    override suspend fun getActiveBranches(): BaseDTO<List<BranchDTO>> {
        shouldThrowException?.let { throw it }
        return activeBranchesResult
    }

    override suspend fun getFamilyRelationships(parameters: Map<String, String>): BaseDTO<List<FamilyRelationshipDTO>> {
        shouldThrowException?.let { throw it }
        lastFamilyRelationshipsParameters = parameters
        return familyRelationshipsResult
    }

    override suspend fun getFamilyRelationshipsFromProxy(parameters: Map<String, String>): BaseDTO<ListData<FamilyRelationshipProxyDTO>> {
        shouldThrowException?.let { throw it }
        lastFamilyRelationshipsFromProxyParameters = parameters
        return familyRelationshipsFromProxyResult
    }

    override suspend fun inquiryRegistry(
        dependentNationalId: String,
        birthDateTimeStamp: String,
        dependencyCode: String
    ): BaseDTO<RegistryDataDTO> {
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

    override suspend fun uploadImage(body: MultiPartFormDataContent): UploadImageResponseDTO {
        shouldThrowException?.let { throw it }
        return uploadImageResult
    }

    override suspend fun addNewDependent(request: RequestAddDependentDTO): BaseDTO<GeneralResponseDTO> {
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
            apiQueryBuilder = ApiQueryBuilderImpl(),
            errorParser = ErrorParserImpl()
        )
    }

    @Test
    fun getDependentInfo_success_returnsDependentInfoList() = runTest {
        val expectedInfo = listOf(DependentInfoDTO(id = "1", fullName = "علی رضایی"))
        fakeApiService.dependentInfoResult = BaseDTO(status = 200, family = "OK", reason = "OK", data = expectedInfo)

        val result = dataSource.getDependentInfo()

        assertEquals(expectedInfo, result)
    }

    @Test
    fun getActiveBranches_success_returnsBranchesList() = runTest {
        val expectedBranches = listOf(BranchDTO(branchCode = "01", branchName = "مرکزی"))
        fakeApiService.activeBranchesResult = BaseDTO(status = 200, family = "OK", reason = "OK", data = expectedBranches)

        val result = dataSource.getActiveBranches()

        assertEquals(expectedBranches, result)
    }

    @Test
    fun getFamilyRelationships_withoutFilter_sendsNoQueryParameters() = runTest {
        val expectedRelationships = listOf(FamilyRelationshipDTO(relationCode = "01", relationDesc = "فرزند"))
        fakeApiService.familyRelationshipsResult = BaseDTO(status = 200, family = "OK", reason = "OK", data = expectedRelationships)

        val result = dataSource.getFamilyRelationships()

        assertEquals(expectedRelationships, result)
        assertEquals(emptyMap(), fakeApiService.lastFamilyRelationshipsParameters)
    }

    @Test
    fun getFamilyRelationships_withFilter_sendsBuiltQueryJson() = runTest {
        val expectedRelationships = listOf(FamilyRelationshipDTO(relationCode = "01", relationDesc = "فرزند"))
        fakeApiService.familyRelationshipsResult = BaseDTO(status = 200, family = "OK", reason = "OK", data = expectedRelationships)
        val filter = listOf(ApiFilterDN(property = FilterProperty.SERIAL_ID, value = "123", operator = FilterOperator.EQUAL))

        val result = dataSource.getFamilyRelationships(filter)

        assertEquals(expectedRelationships, result)
        assertEquals(setOf("query"), fakeApiService.lastFamilyRelationshipsParameters?.keys)
    }

    @Test
    fun getFamilyRelationshipsFromProxy_success_returnsProxyList() = runTest {
        val expectedRelationships = listOf(FamilyRelationshipProxyDTO(relationCode = "01", relationDesc = "فرزند"))
        fakeApiService.familyRelationshipsFromProxyResult = BaseDTO(status = 200, family = "OK", reason = "OK", data = ListData(list = expectedRelationships))

        val result = dataSource.getFamilyRelationshipsFromProxy()

        assertEquals(expectedRelationships, result)
    }

    @Test
    fun getFamilyRelationshipsFromProxy_buildsPageOneQueryWithFilter() = runTest {
        val expectedRelationships = listOf(FamilyRelationshipProxyDTO(relationCode = "01", relationDesc = "همسر"))
        fakeApiService.familyRelationshipsFromProxyResult = BaseDTO(status = 200, family = "OK", reason = "OK", data = ListData(list = expectedRelationships))
        val filter = listOf(ApiFilterDN(property = FilterProperty.DEPENDENCY_DESC, value = "**", operator = FilterOperator.LIKE))

        val result = dataSource.getFamilyRelationshipsFromProxy(filter)

        assertEquals(expectedRelationships, result)
        assertEquals("1", fakeApiService.lastFamilyRelationshipsFromProxyParameters?.get("page"))
        assertEquals(setOf("page", "start", "limit", "filter", "sort"), fakeApiService.lastFamilyRelationshipsFromProxyParameters?.keys)
    }

    @Test
    fun inquiryRegistry_success_returnsRegistryData() = runTest {
        val expectedData = RegistryDataDTO(firstName = "احمد", lastName = "محمدی")
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
        val expectedResponse = UploadImageResponseDTO(guid = "FID123", isSuccess = true)
        fakeApiService.uploadImageResult = expectedResponse

        val result = dataSource.uploadImage(byteArrayOf(1, 2, 3), "img.png", "image/png")

        assertEquals(expectedResponse, result)
    }

    @Test
    fun addNewDependent_success_returnsGeneralResponse() = runTest {
        val expectedResponse = GeneralResponseDTO(isSuccess = true, message = "انجام شد")
        fakeApiService.addNewDependentResult = BaseDTO(status = 200, family = "OK", reason = "OK", data = expectedResponse)

        val result = dataSource.addNewDependent(RequestAddDependentDTO())

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
