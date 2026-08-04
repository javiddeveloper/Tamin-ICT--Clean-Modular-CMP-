package com.tamin.taminhamrah.data.mapper.addDependent

import com.tamin.taminhamrah.model.addDependent.BranchDto
import com.tamin.taminhamrah.model.addDependent.FamilyRelationshipDto
import com.tamin.taminhamrah.model.addDependent.GeneralResponseDto
import com.tamin.taminhamrah.model.addDependent.RegistryDataDto
import com.tamin.taminhamrah.model.addDependent.RequestAddDependentDN
import com.tamin.taminhamrah.model.addDependent.RequestFileDN
import com.tamin.taminhamrah.model.addDependent.UploadImageResponseDto
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class AddDependentMapperTest {

    @Test
    fun branchDto_toDomain_mapsCorrectly() {
        val dto = BranchDto(
            branchCode = "0101",
            branchName = "شعبه مرکزی",
            workshopCode = "9900",
            workshopName = "کارگاه الف"
        )

        val domain = dto.toDomain()

        assertEquals("0101", domain.branchCode)
        assertEquals("شعبه مرکزی", domain.branchName)
        assertEquals("9900", domain.workshopCode)
        assertEquals("کارگاه الف", domain.workshopName)
    }

    @Test
    fun registryDataDto_toDomain_mapsCorrectly() {
        val dto = RegistryDataDto(
            age = 30,
            birthDate = "1372/05/10",
            fatherName = "محمد",
            firstName = "علی",
            lastName = "رضایی",
            nationalId = "0011223344",
            gender = "M",
            registryConfirmState = "CONFIRMED"
        )

        val domain = dto.toDomain()

        assertEquals(30, domain.age)
        assertEquals("1372/05/10", domain.birthDate)
        assertEquals("محمد", domain.fatherName)
        assertEquals("علی", domain.firstName)
        assertEquals("رضایی", domain.lastName)
        assertEquals("0011223344", domain.nationalId)
        assertEquals("M", domain.gender)
        assertEquals("CONFIRMED", domain.registryConfirmState)
    }

    @Test
    fun familyRelationshipDto_toDomain_mapsCorrectly() {
        val dto = FamilyRelationshipDto(
            id = 10,
            relationCode = "REL_01",
            relationDesc = "همسر",
            bailCode = "BAIL_01"
        )

        val domain = dto.toDomain()

        assertEquals(10, domain.id)
        assertEquals("REL_01", domain.relationCode)
        assertEquals("همسر", domain.relationDesc)
        assertEquals("BAIL_01", domain.bailCode)
    }

    @Test
    fun uploadImageResponseDto_toDomain_mapsCorrectly() {
        val dto = UploadImageResponseDto(guid = "GUID-123-456")

        val domain = dto.toDomain()

        assertEquals("GUID-123-456", domain.guid)
    }

    @Test
    fun generalResponseDto_toDomain_mapsCorrectly() {
        val dto = GeneralResponseDto(
            isSuccess = true,
            message = "عملیات با موفقیت انجام شد",
            code = 200
        )

        val domain = dto.toDomain()

        assertTrue(domain.isSuccess == true)
        assertEquals<String?>("عملیات با موفقیت انجام شد", domain.message)
        assertEquals<Int?>(200, domain.code)
    }

    @Test
    fun requestAddDependentDN_toDto_mapsCorrectly() {
        val domain = RequestAddDependentDN(
            bailTypeCode = "BAIL_1",
            branchCode = "0101",
            cityOfBirthId = "10",
            cityOfIssueId = "20",
            countryId = "1",
            dateOfBirth = "1380/01/01",
            dependencyId = 1,
            dependentTypeCode = "TYPE_1",
            firstName = "حسین",
            id = "100",
            lastName = "موسوی",
            nation = "IR",
            nationalId = "0022334455",
            parentId = "PARENT_1",
            requestFileList = listOf(
                RequestFileDN(
                    documentFileId = "DOC_1",
                    documentType = "IMAGE",
                    id = "1",
                    personal = "true"
                )
            )
        )

        val dto = domain.toDto()

        assertEquals("BAIL_1", dto.bailType?.code)
        assertEquals("0101", dto.branchCode)
        assertEquals("10", dto.cityOfBirthId)
        assertEquals(1, dto.dependency?.id)
        assertEquals("TYPE_1", dto.dependentType?.code)
        assertEquals("حسین", dto.firstName)
        assertEquals("موسوی", dto.lastName)
        assertEquals("0022334455", dto.nationalId)
        assertEquals("PARENT_1", dto.parentId?.id)
        assertNotNull(dto.requestFileList)
        assertEquals(1, dto.requestFileList?.size)
        assertEquals("DOC_1", dto.requestFileList?.first()?.documentFile?.id)
    }
}
