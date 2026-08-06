package com.tamin.taminhamrah.useCases.addDependent

import app.cash.turbine.test
import com.tamin.taminhamrah.model.addDependent.BranchDN
import com.tamin.taminhamrah.model.addDependent.FamilyRelationshipDN
import com.tamin.taminhamrah.model.addDependent.GeneralResultDN
import com.tamin.taminhamrah.model.addDependent.RegistryDataDN
import com.tamin.taminhamrah.model.addDependent.RequestAddDependentDN
import com.tamin.taminhamrah.model.addDependent.UploadImageDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.FilterOperator
import com.tamin.taminhamrah.model.request.FilterProperty
import com.tamin.taminhamrah.repository.addDependent.FakeAddDependentRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class AddDependentUseCasesTest : BaseUseCaseTest() {

    private lateinit var repository: FakeAddDependentRepository
    private lateinit var getActiveBranchesUseCase: GetActiveBranchesUseCase
    private lateinit var getFamilyRelationshipsUseCase: GetFamilyRelationshipsUseCase
    private lateinit var getFamilyRelationshipsFromProxyUseCase: GetFamilyRelationshipsFromProxyUseCase
    private lateinit var inquiryRegistryUseCase: InquiryRegistryUseCase
    private lateinit var inquiryEducationCodeUseCase: InquiryEducationCodeUseCase
    private lateinit var uploadDependentImageUseCase: UploadDependentImageUseCase
    private lateinit var addNewDependentUseCase: AddNewDependentUseCase

    @BeforeTest
    fun setup() {
        repository = FakeAddDependentRepository()
        getActiveBranchesUseCase = GetActiveBranchesUseCase(repository)
        getFamilyRelationshipsUseCase = GetFamilyRelationshipsUseCase(repository)
        getFamilyRelationshipsFromProxyUseCase = GetFamilyRelationshipsFromProxyUseCase(repository)
        inquiryRegistryUseCase = InquiryRegistryUseCase(repository)
        inquiryEducationCodeUseCase = InquiryEducationCodeUseCase(repository)
        uploadDependentImageUseCase = UploadDependentImageUseCase(repository)
        addNewDependentUseCase = AddNewDependentUseCase(repository)
    }

    @Test
    fun `GetActiveBranchesUseCase should return branches from repository`() = runTest {
        val expectedBranches = listOf(
            BranchDN(branchCode = "0101", branchName = "شعبه یک")
        )
        repository.activeBranchesResult = expectedBranches

        getActiveBranchesUseCase().test {
            assertEquals(expectedBranches, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `GetFamilyRelationshipsUseCase should return relationships from repository`() = runTest {
        val expectedRelationships = listOf(
            FamilyRelationshipDN(id = 1, relationCode = "REL_1", relationDesc = "همسر")
        )
        repository.familyRelationshipsResult = expectedRelationships
        val filter = listOf(ApiFilterDN(property = FilterProperty.SERIAL_ID, value = "1", operator = FilterOperator.EQUAL))

        getFamilyRelationshipsUseCase(filter).test {
            assertEquals(expectedRelationships, awaitItem())
            awaitComplete()
        }

        assertEquals(filter, repository.lastFamilyRelationshipsFilter)
    }

    @Test
    fun `GetFamilyRelationshipsFromProxyUseCase should return relationships from repository`() = runTest {
        val expectedRelationships = listOf(
            FamilyRelationshipDN(id = 2, relationCode = "REL_2", relationDesc = "پسر", bailCode = "BAIL_01")
        )
        repository.familyRelationshipsFromProxyResult = expectedRelationships

        getFamilyRelationshipsFromProxyUseCase().test {
            assertEquals(expectedRelationships, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `InquiryRegistryUseCase should return registry data from repository`() = runTest {
        val expectedData = RegistryDataDN(firstName = "سارا", lastName = "احمدی", nationalId = "0011223344")
        repository.registryDataResult = expectedData

        inquiryRegistryUseCase("0011223344", "1000", "01").test {
            assertEquals(expectedData, awaitItem())
            awaitComplete()
        }

        assertEquals("0011223344", repository.lastInquiryNationalId)
    }

    @Test
    fun `InquiryEducationCodeUseCase should return education status from repository`() = runTest {
        val expectedStatus = "تایید اولیه"
        repository.educationCodeResult = expectedStatus

        inquiryEducationCodeUseCase("0011223344", "EDU_99").test {
            assertEquals(expectedStatus, awaitItem())
            awaitComplete()
        }

        assertEquals("0011223344", repository.lastInquiryNationalId)
        assertEquals("EDU_99", repository.lastInquiryEducationCode)
    }

    @Test
    fun `UploadDependentImageUseCase should return upload result from repository`() = runTest {
        val expectedUpload = UploadImageDN(guid = "GUID_SAMPLE")
        repository.uploadImageResult = expectedUpload

        uploadDependentImageUseCase(byteArrayOf(1, 2, 3), "doc.pdf", "application/pdf").test {
            assertEquals(expectedUpload, awaitItem())
            awaitComplete()
        }

        assertEquals("doc.pdf", repository.lastUploadedFileName)
    }

    @Test
    fun `AddNewDependentUseCase should return general result from repository`() = runTest {
        val expectedResult = GeneralResultDN(isSuccess = true, message = "با موفقیت ثبت شد", code = 200)
        repository.addNewDependentResult = expectedResult

        val request = RequestAddDependentDN(nationalId = "0011223344", firstName = "سارا")

        addNewDependentUseCase(request).test {
            assertEquals(expectedResult, awaitItem())
            awaitComplete()
        }

        assertEquals(request, repository.lastAddedRequest)
    }

    @Test
    fun `UseCases should propagate errors from repository`() = runTest {
        val expectedException = RuntimeException("Repository error")
        repository.shouldThrowError = true
        repository.error = expectedException

        getActiveBranchesUseCase().test {
            val actualException = awaitError()
            assertEquals(expectedException.message, actualException.message)
        }
    }
}
