package com.tamin.taminhamrah.data.mapper.requestPaymentForIllDays

import com.tamin.taminhamrah.model.requestPaymentForIllDays.CovidResultListDTO
import com.tamin.taminhamrah.model.requestPaymentForIllDays.IllDaysBranchWorkshopDTO
import com.tamin.taminhamrah.model.requestPaymentForIllDays.IllDaysInsuredMainInfoDTO
import com.tamin.taminhamrah.model.requestPaymentForIllDays.IllDaysRequestFileDN
import com.tamin.taminhamrah.model.requestPaymentForIllDays.SaveShortTermIllnessRequestDN
import kotlin.test.Test
import kotlin.test.assertEquals

class RequestPaymentForIllDaysMapperTest {

    @Test
    fun insuredMainInfo_mapsDtoToDomain() {
        val result = IllDaysInsuredMainInfoDTO(
            risuid = "1",
            nationalCode = "001",
            insuranceFirstName = "Ali",
            insuranceLastName = "Rezaei",
            mobileNumber = "0912",
            serviceDateTimeStamp = 100L,
            branchWorkshop = listOf(
                IllDaysBranchWorkshopDTO(
                    branchCode = "0100",
                    branchName = "Branch",
                    workshopCode = "W1",
                    workshopName = "Workshop",
                )
            ),
        ).toDomain()

        assertEquals("1", result.risuid)
        assertEquals("Ali", result.firstName)
        assertEquals("Rezaei", result.lastName)
        assertEquals(100L, result.serviceDateTimeStamp)
        assertEquals(1, result.branchWorkshops.size)
        assertEquals("0100", result.branchWorkshops.first().branchCode)
    }

    @Test
    fun covidResult_mapsListIndexes() {
        val result = CovidResultListDTO(list = listOf("a", "b")).toDomain()

        assertEquals("a", result.startDateTimeStamp)
        assertEquals("b", result.endDateTimeStamp)
        assertEquals(listOf("a", "b"), result.timestamps)
    }

    @Test
    fun saveRequest_mapsDomainToDtoWithHelpType() {
        val result = SaveShortTermIllnessRequestDN(
            doctorId = "d1",
            doctorName = "Dr",
            startDateTimeStamp = 1L,
            endDateTimeStamp = 2L,
            illnessKind = "2",
            workStatus = "2",
            provinceCode = "08",
            cityCode = "01",
            branchCode = "0100",
            branchName = "Branch",
            insuranceFirstName = "Ali",
            insuranceLastName = "Rezaei",
            mobileNumber = "0912",
            nationalCode = "001",
            risuid = "1",
            serviceDateTimeStamp = 10L,
            requestFileList = listOf(
                IllDaysRequestFileDN(documentFile = "guid", documentType = "01")
            ),
        ).toDTO()

        assertEquals("d1", result.doctorId)
        assertEquals("01", result.shorttermRequest?.requestHelpType)
        assertEquals("guid", result.shorttermRequest?.requestFileList?.first()?.documentFile)
        assertEquals("0100", result.shorttermRequest?.branchCode)
    }
}
