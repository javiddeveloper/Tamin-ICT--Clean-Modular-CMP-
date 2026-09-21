package com.tamin.taminhamrah.mapper.personal

import com.tamin.taminhamrah.model.personal.DisabilityDependentDN
import com.tamin.taminhamrah.util.PersianDateFormatter
import kotlin.test.Test
import kotlin.test.assertEquals

class PersonalMapperTest {

    @Test
    fun `DisabilityDependentDN toPresentation formats dateOfBirth as a Jalali date, not a raw timestamp`() {
        val birthTimestamp = 316310400000L // 1359/10/11 (Gregorian 1980-01-01)
        val dependent = DisabilityDependentDN(
            firstName = "منصوره",
            lastName = "آزادی",
            nationalId = "0073160997",
            dateOfBirth = birthTimestamp,
            fatherName = null,
            genderCode = "02",
            genderDesc = "زن",
            tendencyCode = "100",
            tendencyDescription = "همسر",
        )

        val presentation = dependent.toPresentation()

        assertEquals(PersianDateFormatter.formatTimestamp(birthTimestamp), presentation.dateOfBirth)
        assertEquals(false, presentation.dateOfBirth.contains(birthTimestamp.toString()))
    }

    @Test
    fun `DisabilityDependentDN toPresentation returns blank dateOfBirth when timestamp is null`() {
        val dependent = DisabilityDependentDN(
            firstName = "علی",
            lastName = "رضایی",
            nationalId = "0012345678",
            dateOfBirth = null,
            fatherName = null,
            genderCode = "01",
            genderDesc = "مرد",
            tendencyCode = "101",
            tendencyDescription = null,
        )

        val presentation = dependent.toPresentation()

        assertEquals("", presentation.dateOfBirth)
    }
}
