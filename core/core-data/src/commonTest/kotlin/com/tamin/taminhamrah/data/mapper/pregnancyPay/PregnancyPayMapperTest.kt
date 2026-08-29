package com.tamin.taminhamrah.data.mapper.pregnancyPay

import com.tamin.taminhamrah.data.mapper.toDTO
import com.tamin.taminhamrah.data.mapper.toDomain
import com.tamin.taminhamrah.data.mapper.toPregnancyPayEstimateDomain
import com.tamin.taminhamrah.model.pregnancyPay.PregnancyBranchWorkshopDTO
import com.tamin.taminhamrah.model.pregnancyPay.PregnancyMainInfoDTO
import com.tamin.taminhamrah.model.pregnancyPay.PregnancyOptionDTO
import com.tamin.taminhamrah.model.pregnancyPay.PregnancyRequestFileDN
import com.tamin.taminhamrah.model.pregnancyPay.SendPregnancyPayRequestDN
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class PregnancyPayMapperTest {

    /**
     * Regression test for the bug fixed in fb1cd9715 ("fix pregnancy pay estimate mapping"): the
     * backend's `calculateEstimate` array can contain a null element (list is `List<String?>`, not
     * `List<String>`), and this[0]/this[1] crashed with an NPE/ClassCastException on that response.
     */
    @Test
    fun toPregnancyPayEstimateDomain_nullElement_mapsToEmptyStringInsteadOfCrashing() {
        val result = listOf<String?>(null, "91200000").toPregnancyPayEstimateDomain()

        assertEquals("", result.averageSalaryLast90Days)
        assertEquals("91200000", result.amountPayable)
    }

    @Test
    fun toPregnancyPayEstimateDomain_shorterThanExpectedList_mapsMissingIndexToEmptyString() {
        val result = listOf("2850000").toPregnancyPayEstimateDomain()

        assertEquals("2850000", result.averageSalaryLast90Days)
        assertEquals("", result.amountPayable)
    }

    @Test
    fun toPregnancyPayEstimateDomain_emptyList_mapsBothFieldsToEmptyString() {
        val result = emptyList<String?>().toPregnancyPayEstimateDomain()

        assertEquals("", result.averageSalaryLast90Days)
        assertEquals("", result.amountPayable)
    }

    @Test
    fun toPregnancyPayEstimateDomain_bothPresent_mapsInOrder() {
        val result = listOf("2850000", "91200000").toPregnancyPayEstimateDomain()

        assertEquals("2850000", result.averageSalaryLast90Days)
        assertEquals("91200000", result.amountPayable)
    }

    @Test
    fun pregnancyOptionDto_toDomain_nullCode_returnsNull() {
        val dto = PregnancyOptionDTO(code = null, name = "بارداری طبیعی")

        assertNull(dto.toDomain())
    }

    @Test
    fun pregnancyOptionDto_toDomain_nullName_returnsNull() {
        val dto = PregnancyOptionDTO(code = "1", name = null)

        assertNull(dto.toDomain())
    }

    @Test
    fun pregnancyOptionDto_toDomain_bothPresent_mapsCorrectly() {
        val dto = PregnancyOptionDTO(code = "1", name = "بارداری طبیعی")

        val domain = dto.toDomain()

        assertEquals("1", domain?.code)
        assertEquals("بارداری طبیعی", domain?.name)
    }

    @Test
    fun pregnancyMainInfoDto_toDomain_nullBranchWorkshop_mapsToEmptyList() {
        val dto = PregnancyMainInfoDTO(risuid = "risuid-1", branchWorkshop = null)

        val domain = dto.toDomain()

        assertEquals(emptyList(), domain.branchWorkshops)
    }

    @Test
    fun pregnancyMainInfoDto_toDomain_mapsAllFields() {
        val dto = PregnancyMainInfoDTO(
            risuid = "risuid-1",
            nationalCode = "0012345678",
            insuranceFirstName = "زهرا",
            insuranceLastName = "احمدی",
            mobileNumber = "09120000000",
            genderCode = "02",
            serviceDateTimeStamp = 1000,
            branchWorkshop = listOf(
                PregnancyBranchWorkshopDTO(branchCode = "10", branchName = "شعبه مرکزی", workshopName = "کارگاه اصلی"),
            ),
            bankAccount = "5678123459870012",
            bankName = "بانک ملت",
            insuranceTypeDesc = "بیمهٔ اجباری کارگری",
            insuranceStatusDesc = "برخوردار",
        )

        val domain = dto.toDomain()

        assertEquals("risuid-1", domain.risuid)
        assertEquals("زهرا", domain.firstName)
        assertEquals("احمدی", domain.lastName)
        assertEquals("02", domain.genderCode)
        assertEquals(1, domain.branchWorkshops.size)
        assertEquals("10", domain.branchWorkshops[0].branchCode)
    }

    @Test
    fun sendPregnancyPayRequestDn_toDTO_mapsFieldsIntoNestedShorttermRequest() {
        val request = SendPregnancyPayRequestDN(
            branchCode = "10",
            branchName = "شعبه مرکزی-کارگاه اصلی",
            insuranceFirstName = "زهرا",
            insuranceLastName = "احمدی",
            mobileNumber = "09120000000",
            nationalCode = "0012345678",
            risuid = "risuid-1",
            serviceDateTimeStamp = 1000,
            pregnancyStatusCode = "1",
            pregnancyTypeCode = "2",
            requestTypeCode = "3",
            restStartDateTimeStamp = 0L,
            restEndDateTimeStamp = 10_000_000L,
            restDaysCount = "116",
            babyBirthDateTimeStamp = 100_000L,
            doctorName = "دکتر رضایی",
            doctorCode = "12345",
            childNationalId = "0011122233",
            childNationalId2 = null,
            childNationalId3 = null,
            requestFileList = listOf(
                PregnancyRequestFileDN(documentFile = "guid-1", documentType = "0205"),
            ),
        )

        val dto = request.toDTO()

        assertEquals("2", dto.barChild)
        assertEquals("1", dto.barType)
        assertEquals("12345", dto.doctorCode)
        assertEquals("116", dto.restDaysCount)
        assertEquals("risuid-1", dto.shorttermRequest?.risuid)
        assertEquals("10", dto.shorttermRequest?.branchCode)
        assertEquals(1, dto.shorttermRequest?.requestFileList?.size)
        assertEquals("guid-1", dto.shorttermRequest?.requestFileList?.get(0)?.documentFile)
        assertEquals("02", dto.shorttermRequest?.requestHelpType)
    }
}
