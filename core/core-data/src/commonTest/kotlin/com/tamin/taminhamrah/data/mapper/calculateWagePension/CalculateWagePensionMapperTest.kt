package com.tamin.taminhamrah.data.mapper.calculateWagePension

import com.tamin.taminhamrah.model.calculateWagePension.MultipleWorkshopPersonalInfoDTO
import com.tamin.taminhamrah.model.calculateWagePension.MultipleWorkshopResultDTO
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CalculateWagePensionMapperTest {

    @Test
    fun `personal info maps organizationId to branchCode`() {
        val result = MultipleWorkshopPersonalInfoDTO(
            organizationId = "12345",
            insuranceId = "9876543210",
            branch = "Tehran Main"
        ).toDomain()

        assertEquals("12345", result.branchCode)
        assertEquals("9876543210", result.insuranceNumber)
        assertEquals("Tehran Main", result.branch)
    }

    @Test
    fun `personal info maps nulls to empty strings`() {
        val result = MultipleWorkshopPersonalInfoDTO().toDomain()

        assertEquals("", result.branchCode)
        assertEquals("", result.insuranceNumber)
        assertEquals(null, result.branch)
    }

    @Test
    fun `result dto maps isMultiple from 1`() {
        val result = MultipleWorkshopResultDTO(result = 1).toDomain()

        assertEquals(1, result.result)
        assertTrue(result.isMultiple)
        assertEquals(1L, result.pensionAmount)
    }

    @Test
    fun `result dto maps missing result to zero`() {
        val result = MultipleWorkshopResultDTO(result = null).toDomain()

        assertEquals(0, result.result)
        assertFalse(result.isMultiple)
    }
}
