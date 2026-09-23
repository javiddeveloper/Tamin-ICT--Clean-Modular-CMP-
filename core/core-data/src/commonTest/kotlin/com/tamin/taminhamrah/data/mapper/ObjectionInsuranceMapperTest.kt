package com.tamin.taminhamrah.data.mapper

import com.tamin.taminhamrah.model.objectionInsurance.ObjectionInsuranceHistoryDN
import com.tamin.taminhamrah.model.objectionInsurance.ObjectionInsuranceHistoryDTO
import kotlin.test.Test
import kotlin.test.assertEquals

class ObjectionInsuranceMapperTest {

    @Test
    fun dto_toDomain_mapsFields() {
        val dto = ObjectionInsuranceHistoryDTO(
            branchCode = "0950",
            branchName = "شعبه",
            year = "1402",
            workshopId = "W1",
            workshopName = "کارگاه",
            oldMonth1 = "30",
            newMonth1 = "20",
            requestNumber = "R1",
            insuredId = "I1",
        )

        val domain = dto.toDomain()

        assertEquals("0950", domain.branchCode)
        assertEquals("شعبه", domain.branchName)
        assertEquals("1402", domain.year)
        assertEquals("W1", domain.workshopId)
        assertEquals("کارگاه", domain.workshopName)
        assertEquals("30", domain.oldMonth1)
        assertEquals("20", domain.newMonth1)
        assertEquals("R1", domain.requestNumber)
        assertEquals("I1", domain.insuredId)
    }

    @Test
    fun dto_nullYear_mapsToEmptyString() {
        assertEquals("", ObjectionInsuranceHistoryDTO(year = null).toDomain().year)
    }

    @Test
    fun dn_toDto_roundTrips() {
        val domain = ObjectionInsuranceHistoryDN(
            branchCode = "0950",
            branchName = "شعبه",
            year = "1402",
            newMonth2 = "15",
            oldMonth2 = "31",
        )

        val dto = domain.toDTO()

        assertEquals("0950", dto.branchCode)
        assertEquals("شعبه", dto.branchName)
        assertEquals("1402", dto.year)
        assertEquals("15", dto.newMonth2)
        assertEquals("31", dto.oldMonth2)
    }
}
