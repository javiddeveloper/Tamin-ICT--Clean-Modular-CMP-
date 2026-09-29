package com.tamin.taminhamrah.mapper.objectionInsurance

import com.tamin.taminhamrah.model.objectionInsurance.ObjectionInsuranceHistoryDN
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class ObjectionInsuranceUiMapperTest {

    @Test
    fun dn_toPresentation_mapsNullsToEmptyDefaults() {
        val presentation = ObjectionInsuranceHistoryDN(
            branchCode = null,
            branchName = "شعبه",
            confirmed = null,
            year = "1402",
            oldMonth1 = "30",
            newMonth1 = null,
        ).toPresentation()

        assertEquals("", presentation.branchCode)
        assertEquals("شعبه", presentation.branchName)
        assertFalse(presentation.confirmed)
        assertEquals("1402", presentation.year)
        assertEquals("30", presentation.oldMonth1)
        assertEquals("", presentation.newMonth1)
    }

    @Test
    fun list_toPresentation_mapsAllItems() {
        val presentation = listOf(
            ObjectionInsuranceHistoryDN(year = "1401"),
            ObjectionInsuranceHistoryDN(year = "1402"),
        ).toPresentation()

        assertEquals(2, presentation.size)
        assertEquals("1401", presentation[0].year)
        assertEquals("1402", presentation[1].year)
    }
}
