package com.tamin.taminhamrah.data.mapper.occurrence

import com.tamin.taminhamrah.model.occurrence.InsuredRelationDTO
import com.tamin.taminhamrah.model.occurrence.NationDTO
import com.tamin.taminhamrah.model.occurrence.OccurrenceDocTypeDTO
import com.tamin.taminhamrah.model.occurrence.OccurrencePersonalInfoDTO
import com.tamin.taminhamrah.model.occurrence.OccurrenceResponseDTO
import com.tamin.taminhamrah.model.occurrence.OccurrenceSubmitRequestDN
import com.tamin.taminhamrah.model.occurrence.OccurrenceUploadedDocDN
import com.tamin.taminhamrah.model.occurrence.WorkshopItemDTO
import kotlin.test.Test
import kotlin.test.assertEquals

class OccurrenceMapperTest {

    @Test
    fun workshopItemDto_toDomain_mapsCorrectly() {
        val dto = WorkshopItemDTO(
            id = "1",
            workshopCode = "1412345",
            branchCode = "014",
            name = "کارگاه تولیدی الف",
            employerName = "شرکت الف",
            employerPhone = "02112345678",
            address = "تهران",
            postalCode = "1234567890",
            phone = "02112345678",
            nation = NationDTO(nationCode = "1", nationDesc = "ایرانی"),
        )

        val domain = dto.toDomain()

        assertEquals("1", domain.id)
        assertEquals("1412345", domain.workshopCode)
        assertEquals("014", domain.branchCode)
        assertEquals("کارگاه تولیدی الف", domain.name)
        assertEquals("شرکت الف", domain.employerName)
        assertEquals("02112345678", domain.employerPhone)
        assertEquals("تهران", domain.address)
        assertEquals("1234567890", domain.postalCode)
        assertEquals("02112345678", domain.phone)
        assertEquals("ایرانی", domain.nationality)
        assertEquals("1", domain.nationalityCode)
    }

    @Test
    fun workshopItemDto_toDomain_fillsMissingFieldsWithEmptyStrings() {
        val domain = WorkshopItemDTO().toDomain()

        assertEquals("", domain.id)
        assertEquals("", domain.workshopCode)
        assertEquals("", domain.nationality)
        assertEquals("", domain.nationalityCode)
    }

    @Test
    fun occurrencePersonalInfoDto_toDomain_mapsCorrectly() {
        val dto = OccurrencePersonalInfoDTO(
            nationalCode = "0012345678",
            firstName = "علی",
            lastName = "رضایی",
            fatherName = "محمد",
            gender = "01",
            birthDate = "1370/01/01",
            insuranceNumber = "1234567",
            branchCode = "10",
            nationality = "ایرانی",
            insuranceType = "اصلی",
        )

        val domain = dto.toDomain()

        assertEquals("0012345678", domain.nationalCode)
        assertEquals("علی", domain.firstName)
        assertEquals("رضایی", domain.lastName)
        assertEquals("علی رضایی", domain.fullName)
        assertEquals("محمد", domain.fatherName)
        assertEquals("01", domain.gender)
        assertEquals("10", domain.branchCode)
    }

    @Test
    fun insuredRelationDto_toDomain_mapsCorrectly() {
        val dto = InsuredRelationDTO(
            insuranceTypeCode = "01",
            insuranceType = "اصلی",
            branchCode = "10",
            branchName = "شعبه مرکزی",
        )

        val domain = dto.toDomain()

        assertEquals("01", domain.insuranceTypeCode)
        assertEquals("اصلی", domain.insuranceType)
        assertEquals("10", domain.branchCode)
        assertEquals("شعبه مرکزی", domain.branchName)
    }

    @Test
    fun occurrenceDocTypeDto_toDomain_parsesNumericId() {
        val domain = OccurrenceDocTypeDTO(docTypeId = "3", docDesc = "مدارک پزشکی").toDomain()

        assertEquals(3, domain.id)
        assertEquals("مدارک پزشکی", domain.title)
    }

    @Test
    fun occurrenceDocTypeDto_toDomain_fallsBackToZeroForNonNumericId() {
        val domain = OccurrenceDocTypeDTO(docTypeId = "not-a-number", docDesc = "مدارک").toDomain()

        assertEquals(0, domain.id)
    }

    @Test
    fun occurrenceResponseDto_toDomain_mapsTrackingCode() {
        val domain = OccurrenceResponseDTO(reportRefrenceNumber = "TRACK-123").toDomain()

        assertEquals("TRACK-123", domain.trackingCode)
    }

    @Test
    fun occurrenceUploadedDocDn_toDTO_mapsGuidAndTypeId() {
        val domain = OccurrenceUploadedDocDN(typeId = 2, typeName = "مدارک پزشکی", fileName = "a.jpg", guid = "guid-1")

        val dto = domain.toDTO()

        assertEquals("guid-1", dto.documentFile.id)
        assertEquals("2", dto.occurrenceDocumentType.docTypeId)
    }

    @Test
    fun occurrenceSubmitRequestDn_toDTO_mapsAllFieldsToLegacyShape() {
        val domain = OccurrenceSubmitRequestDN(
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
            documents = listOf(OccurrenceUploadedDocDN(typeId = 1, typeName = "گزارش", fileName = "a.jpg", guid = "guid-1")),
        )

        val dto = domain.toDTO()

        assertEquals("0012345678", dto.pNationalCode)
        assertEquals("علی", dto.pFirstName)
        assertEquals("رضایی", dto.pLastName)
        assertEquals(1, dto.gender)
        assertEquals("شرکت الف", dto.bossFullName)
        assertEquals("02112345678", dto.bossMobileNumber)
        assertEquals("1412345", dto.workshopCode)
        assertEquals("014", dto.workshopBranchCode)
        assertEquals("طبقه دوم", dto.occurrenceAddress)
        assertEquals("توضیحات حادثه", dto.occurrenceDesc)
        assertEquals(1, dto.occurrenceDocumentList.size)
        assertEquals("guid-1", dto.occurrenceDocumentList.first().documentFile.id)
        assertEquals("662688000", dto.birthDate)
        assertEquals("662688000", dto.employeeDate)
        assertEquals("662688000", dto.occurrenceDate)
    }
}
