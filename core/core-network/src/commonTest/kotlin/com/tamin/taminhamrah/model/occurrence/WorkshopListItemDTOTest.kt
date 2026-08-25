package com.tamin.taminhamrah.model.occurrence

import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class WorkshopListItemDTOTest {

    @Test
    fun deserialize_mapsPositionalArrayToNamedFields() {
        val result = Json.decodeFromString<WorkshopListItemDTO>(
            """["1412345", "کارگاه تولیدی الف", "014"]"""
        )

        assertEquals("1412345", result.workshopCode)
        assertEquals("کارگاه تولیدی الف", result.name)
        assertEquals("014", result.branchCode)
    }

    @Test
    fun deserialize_whenRowIsShorterThanExpected_fillsMissingFieldsWithNull() {
        val result = Json.decodeFromString<WorkshopListItemDTO>("""["1412345"]""")

        assertEquals("1412345", result.workshopCode)
        assertNull(result.name)
        assertNull(result.branchCode)
    }

    @Test
    fun deserialize_whenElementIsJsonNull_mapsToNull() {
        val result = Json.decodeFromString<WorkshopListItemDTO>(
            """["1412345", null, "014"]"""
        )

        assertNull(result.name)
    }
}
