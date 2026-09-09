package com.tamin.taminhamrah.mapper.requestPaymentForIllDays

import com.tamin.taminhamrah.model.requestPaymentForIllDays.CovidResultDN
import com.tamin.taminhamrah.model.requestPaymentForIllDays.IllDaysBranchWorkshopDN
import com.tamin.taminhamrah.model.requestPaymentForIllDays.IllDaysInsuredMainInfoDN
import kotlin.test.Test
import kotlin.test.assertEquals

class RequestPaymentForIllDaysUiMapperTest {

    @Test
    fun insuredMainInfo_mapsNullsToEmptyStrings() {
        val result = IllDaysInsuredMainInfoDN(
            risuid = null,
            nationalCode = null,
            firstName = "Ali",
            lastName = "Rezaei",
            mobileNumber = null,
            genderCode = null,
            branchCode = null,
            branchName = null,
            bankAccount = null,
            bankName = null,
            insuranceTypeDesc = null,
            insuranceStatusDesc = null,
            serviceDateTimeStamp = null,
            branchWorkshops = listOf(
                IllDaysBranchWorkshopDN("0100", "Branch", "W1", "Workshop")
            ),
        ).toPresentation()

        assertEquals("", result.risuid)
        assertEquals("Ali Rezaei", result.fullName)
        assertEquals("0100-W1", result.branchWorkshops.first().id)
        assertEquals("Branch - Workshop", result.branchWorkshops.first().label)
    }

    @Test
    fun covidResult_mapsNullTimestampsToEmpty() {
        val result = CovidResultDN(
            startDateTimeStamp = null,
            endDateTimeStamp = "x",
            timestamps = emptyList(),
        ).toPresentation()

        assertEquals("", result.startDateTimeStamp)
        assertEquals("x", result.endDateTimeStamp)
    }
}
