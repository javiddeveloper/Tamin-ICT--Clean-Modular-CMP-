package com.tamin.taminhamrah.model.funeralAllowance

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * [DeceasedValidationDTO.fromPositional] decodes the bare positional string array the backend
 * returns from `shortterm/validateFuneral/{nationalCode}` (native `DeceasedInfoResponse`). The slot
 * map and the "at least 8 entries and `data[6] == \"1\"`" eligibility rule are legacy behaviour, so
 * each edge — including every null/short/garbled shape — is pinned here. Legacy never reads past
 * `[7]`, so this DTO doesn't decode anything past it either.
 */
class DeceasedValidationDTOTest {

    private fun raw(
        name: String? = "زهرا رضایی",
        relationship: String? = "همسر",
        eligibleFlag: String? = "1",
        message: String? = "دارای شرایط می‌باشید",
    ): List<String?> = listOf(
        "0", "1", "2", "3",   // [0..3] unused
        name,                 // [4]
        relationship,         // [5]
        eligibleFlag,         // [6]
        message,              // [7]
    )

    @Test
    fun fromPositional_mapsTheSlotsTheAppUses() {
        val result = DeceasedValidationDTO.fromPositional(raw())

        assertEquals("زهرا رضایی", result.fullName)
        assertEquals("همسر", result.relationship)
        assertEquals("دارای شرایط می‌باشید", result.message)
        assertTrue(result.isEligible)
    }

    @Test
    fun fromPositional_treatsANonOneEligibilityFlagAsNotEligible() {
        assertFalse(DeceasedValidationDTO.fromPositional(raw(eligibleFlag = "0")).isEligible)
        assertFalse(DeceasedValidationDTO.fromPositional(raw(eligibleFlag = "")).isEligible)
        assertFalse(DeceasedValidationDTO.fromPositional(raw(eligibleFlag = null)).isEligible)
    }

    @Test
    fun fromPositional_requiresAtLeastEightEntriesForEligibility() {
        // 7 entries: flag slot present and "1", but the list is too short to trust.
        val sevenWithFlag = listOf<String?>("0", "1", "2", "3", "زهرا", "همسر", "1")
        assertFalse(DeceasedValidationDTO.fromPositional(sevenWithFlag).isEligible)

        // Exactly 8 entries with "1" at [6] is the boundary that passes.
        val eightWithFlag = listOf<String?>("0", "1", "2", "3", "زهرا", "همسر", "1", "پیام")
        assertTrue(DeceasedValidationDTO.fromPositional(eightWithFlag).isEligible)
    }

    @Test
    fun fromPositional_shortList_isNotEligibleAndFieldsFallBackToEmptyStrings() {
        val result = DeceasedValidationDTO.fromPositional(listOf("a", "b", "c"))

        assertFalse(result.isEligible)
        assertEquals("", result.fullName)
        assertEquals("", result.relationship)
        assertEquals("", result.message)
    }

    @Test
    fun fromPositional_toleratesNullEntries() {
        val result = DeceasedValidationDTO.fromPositional(
            raw(name = null, relationship = null, message = null),
        )

        assertEquals("", result.fullName)
        assertEquals("", result.relationship)
        assertEquals("", result.message)
        // A null name/etc. must not affect the eligibility gate.
        assertTrue(result.isEligible)
    }

    @Test
    fun fromPositional_nullList_isNotEligibleWithBlankFields() {
        val result = DeceasedValidationDTO.fromPositional(null)

        assertFalse(result.isEligible)
        assertEquals("", result.fullName)
    }

    @Test
    fun fromPositional_emptyList_isNotEligibleWithBlankFields() {
        val result = DeceasedValidationDTO.fromPositional(emptyList())

        assertFalse(result.isEligible)
        assertEquals("", result.fullName)
    }
}
