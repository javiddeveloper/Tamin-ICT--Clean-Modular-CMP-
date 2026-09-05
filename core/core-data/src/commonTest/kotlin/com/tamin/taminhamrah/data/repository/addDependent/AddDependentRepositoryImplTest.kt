package com.tamin.taminhamrah.data.repository.addDependent

import com.tamin.taminhamrah.dataSource.addDependent.AddDependentRemoteDataSource
import com.tamin.taminhamrah.model.addDependent.BranchDTO
import com.tamin.taminhamrah.model.addDependent.DependentInfoDTO
import com.tamin.taminhamrah.model.addDependent.FamilyRelationshipDTO
import com.tamin.taminhamrah.model.addDependent.FamilyRelationshipProxyDTO
import com.tamin.taminhamrah.model.addDependent.GeneralResponseDTO
import com.tamin.taminhamrah.model.addDependent.RegistryDataDTO
import com.tamin.taminhamrah.model.addDependent.RequestAddDependentDN
import com.tamin.taminhamrah.model.addDependent.RequestAddDependentDTO
import com.tamin.taminhamrah.model.addDependent.UploadImageResponseDTO
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.FilterOperator
import com.tamin.taminhamrah.model.request.FilterProperty
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class FakeAddDependentRemoteDataSource : AddDependentRemoteDataSource {
    var dependentInfoResult: List<DependentInfoDTO> = emptyList()
    var activeBranchesResult: List<BranchDTO> = emptyList()
    var familyRelationshipsResult: List<FamilyRelationshipDTO> = emptyList()
    var familyRelationshipsFromProxyResult: List<FamilyRelationshipProxyDTO> = emptyList()
    var registryDataResult: RegistryDataDTO = RegistryDataDTO()
    var educationCodeResult: String = ""
    var uploadImageResult: UploadImageResponseDTO = UploadImageResponseDTO()
    var addNewDependentResult: GeneralResponseDTO = GeneralResponseDTO()

    var shouldThrowError: Exception? = null
    var lastFamilyRelationshipsFilter: List<ApiFilterDN>? = null
    var lastFamilyRelationshipsFromProxyFilter: List<ApiFilterDN>? = null

    override suspend fun getDependentInfo(): List<DependentInfoDTO> {
        shouldThrowError?.let { throw it }
        return dependentInfoResult
    }

    override suspend fun getActiveBranches(): List<BranchDTO> {
        shouldThrowError?.let { throw it }
        return activeBranchesResult
    }

    override suspend fun getFamilyRelationships(filter: List<ApiFilterDN>): List<FamilyRelationshipDTO> {
        shouldThrowError?.let { throw it }
        lastFamilyRelationshipsFilter = filter
        return familyRelationshipsResult
    }

    override suspend fun getFamilyRelationshipsFromProxy(filter: List<ApiFilterDN>): List<FamilyRelationshipProxyDTO> {
        shouldThrowError?.let { throw it }
        lastFamilyRelationshipsFromProxyFilter = filter
        return familyRelationshipsFromProxyResult
    }

    override suspend fun inquiryRegistry(
        dependentNationalId: String,
        birthDateTimeStamp: String,
        dependencyCode: String
    ): RegistryDataDTO {
        shouldThrowError?.let { throw it }
        return registryDataResult
    }

    override suspend fun inquiryEducationCode(
        nationalId: String,
        educationCode: String
    ): String {
        shouldThrowError?.let { throw it }
        return educationCodeResult
    }

    override suspend fun uploadImage(
        imageBytes: ByteArray,
        fileName: String,
        mimeType: String
    ): UploadImageResponseDTO {
        shouldThrowError?.let { throw it }
        return uploadImageResult
    }

    override suspend fun addNewDependent(request: RequestAddDependentDTO): GeneralResponseDTO {
        shouldThrowError?.let { throw it }
        return addNewDependentResult
    }
}

class AddDependentRepositoryImplTest {

    private lateinit var remoteDataSource: FakeAddDependentRemoteDataSource
    private lateinit var repository: AddDependentRepositoryImpl

    @BeforeTest
    fun setup() {
        remoteDataSource = FakeAddDependentRemoteDataSource()
        repository = AddDependentRepositoryImpl(remoteDataSource)
    }

    @Test
    fun getDependentInfo_emitsMappedDependentInfoDNList() = runTest {
        remoteDataSource.dependentInfoResult = listOf(
            DependentInfoDTO(id = "1", fullName = "مریم حسینی")
        )

        val items = repository.getDependentInfo().first()

        assertEquals(1, items.size)
        assertEquals("1", items.first().id)
    }

    @Test
    fun getActiveBranches_emitsMappedBranchDNList() = runTest {
        remoteDataSource.activeBranchesResult = listOf(
            BranchDTO(branchCode = "0101", branchName = "مرکزی", workshopCode = "001", workshopName = "کارگاه")
        )

        val items = repository.getActiveBranches().first()

        assertEquals(1, items.size)
        assertEquals("0101", items.first().branchCode)
        assertEquals("مرکزی", items.first().branchName)
    }

    @Test
    fun getFamilyRelationships_emitsMappedFamilyRelationshipDNList() = runTest {
        remoteDataSource.familyRelationshipsResult = listOf(
            FamilyRelationshipDTO(id = 1, relationCode = "REL_01", relationDesc = "فرزند")
        )
        val filter = listOf(ApiFilterDN(property = FilterProperty.SERIAL_ID, value = "1", operator = FilterOperator.EQUAL))

        val items = repository.getFamilyRelationships(filter).first()

        assertEquals(1, items.size)
        assertEquals("REL_01", items.first().relationCode)
        assertEquals("فرزند", items.first().relationDesc)
        assertEquals(filter, remoteDataSource.lastFamilyRelationshipsFilter)
    }

    @Test
    fun getFamilyRelationshipsFromProxy_emitsMappedFamilyRelationshipDNList() = runTest {
        remoteDataSource.familyRelationshipsFromProxyResult = listOf(
            FamilyRelationshipProxyDTO(id = 1, relationCode = "REL_01", relationDesc = "فرزند")
        )
        val filter = listOf(ApiFilterDN(property = FilterProperty.DEPENDENCY_DESC, value = "**", operator = FilterOperator.LIKE))

        val items = repository.getFamilyRelationshipsFromProxy(filter).first()

        assertEquals(1, items.size)
        assertEquals("REL_01", items.first().relationCode)
        assertEquals(filter, remoteDataSource.lastFamilyRelationshipsFromProxyFilter)
    }

    @Test
    fun inquiryRegistry_emitsMappedRegistryDataDN() = runTest {
        remoteDataSource.registryDataResult = RegistryDataDTO(
            firstName = "رضا",
            lastName = "کریمی",
            nationalId = "0012345678"
        )

        val item = repository.inquiryRegistry("0012345678", "1000", "01").first()

        assertEquals("رضا", item.firstName)
        assertEquals("کریمی", item.lastName)
        assertEquals("0012345678", item.nationalId)
    }

    @Test
    fun inquiryEducationCode_emitsResultString() = runTest {
        remoteDataSource.educationCodeResult = "موفق"

        val item = repository.inquiryEducationCode("0012345678", "EDU100").first()

        assertEquals("موفق", item)
    }

    @Test
    fun uploadImage_emitsMappedUploadImageDN() = runTest {
        remoteDataSource.uploadImageResult = UploadImageResponseDTO(guid = "GUID_XYZ")

        val item = repository.uploadImage(byteArrayOf(1, 2), "file.jpg", "image/jpeg").first()

        assertEquals("GUID_XYZ", item.guid)
    }

    @Test
    fun addNewDependent_emitsMappedGeneralResultDN() = runTest {
        remoteDataSource.addNewDependentResult = GeneralResponseDTO(
            isSuccess = true,
            message = "ثبت شد",
            code = 200
        )

        val item = repository.addNewDependent(RequestAddDependentDN(nationalId = "0012345678")).first()

        assertEquals(true, item.isSuccess)
        assertEquals("ثبت شد", item.message)
        assertEquals(200, item.code)
    }

    @Test
    fun getActiveBranches_onError_emitsError() = runTest {
        val expectedError = RuntimeException("Network Failure")
        remoteDataSource.shouldThrowError = expectedError

        val actualError = assertFailsWith<RuntimeException> {
            repository.getActiveBranches().first()
        }

        assertEquals(expectedError.message, actualError.message)
    }
}
