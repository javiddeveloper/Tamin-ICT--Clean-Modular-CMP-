package com.tamin.taminhamrah.data.repository.addDependent

import com.tamin.taminhamrah.dataSource.addDependent.AddDependentRemoteDataSource
import com.tamin.taminhamrah.model.addDependent.BranchDto
import com.tamin.taminhamrah.model.addDependent.FamilyRelationshipDto
import com.tamin.taminhamrah.model.addDependent.GeneralResponseDto
import com.tamin.taminhamrah.model.addDependent.RegistryDataDto
import com.tamin.taminhamrah.model.addDependent.RequestAddDependentDN
import com.tamin.taminhamrah.model.addDependent.RequestAddDependentDto
import com.tamin.taminhamrah.model.addDependent.UploadImageResponseDto
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class FakeAddDependentRemoteDataSource : AddDependentRemoteDataSource {
    var activeBranchesResult: List<BranchDto> = emptyList()
    var familyRelationshipsResult: List<FamilyRelationshipDto> = emptyList()
    var registryDataResult: RegistryDataDto = RegistryDataDto()
    var educationCodeResult: String = ""
    var uploadImageResult: UploadImageResponseDto = UploadImageResponseDto()
    var addNewDependentResult: GeneralResponseDto = GeneralResponseDto()

    var shouldThrowError: Exception? = null

    override suspend fun getActiveBranches(): List<BranchDto> {
        shouldThrowError?.let { throw it }
        return activeBranchesResult
    }

    override suspend fun getFamilyRelationships(queryJson: String?): List<FamilyRelationshipDto> {
        shouldThrowError?.let { throw it }
        return familyRelationshipsResult
    }

    override suspend fun inquiryRegistry(
        dependentNationalId: String,
        birthDateTimeStamp: String,
        dependencyCode: String
    ): RegistryDataDto {
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
    ): UploadImageResponseDto {
        shouldThrowError?.let { throw it }
        return uploadImageResult
    }

    override suspend fun addNewDependent(request: RequestAddDependentDto): GeneralResponseDto {
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
    fun getActiveBranches_emitsMappedBranchDNList() = runTest {
        remoteDataSource.activeBranchesResult = listOf(
            BranchDto(branchCode = "0101", branchName = "مرکزی", workshopCode = "001", workshopName = "کارگاه")
        )

        val items = repository.getActiveBranches().first()

        assertEquals(1, items.size)
        assertEquals("0101", items.first().branchCode)
        assertEquals("مرکزی", items.first().branchName)
    }

    @Test
    fun getFamilyRelationships_emitsMappedFamilyRelationshipDNList() = runTest {
        remoteDataSource.familyRelationshipsResult = listOf(
            FamilyRelationshipDto(id = 1, relationCode = "REL_01", relationDesc = "فرزند")
        )

        val items = repository.getFamilyRelationships(queryJson = null).first()

        assertEquals(1, items.size)
        assertEquals("REL_01", items.first().relationCode)
        assertEquals("فرزند", items.first().relationDesc)
    }

    @Test
    fun inquiryRegistry_emitsMappedRegistryDataDN() = runTest {
        remoteDataSource.registryDataResult = RegistryDataDto(
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
        remoteDataSource.uploadImageResult = UploadImageResponseDto(guid = "GUID_XYZ")

        val item = repository.uploadImage(byteArrayOf(1, 2), "file.jpg", "image/jpeg").first()

        assertEquals("GUID_XYZ", item.guid)
    }

    @Test
    fun addNewDependent_emitsMappedGeneralResultDN() = runTest {
        remoteDataSource.addNewDependentResult = GeneralResponseDto(
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
