package com.tamin.taminhamrah.feature.healthProfile.ui.mapper

import com.tamin.taminhamrah.model.health.HealthProblemDN
import kotlin.test.Test
import kotlin.test.assertEquals

class HealthProfileUiMappersTest {

    @Test
    fun `HealthProblemDN toPresentation copies code and message as-is`() {
        val domain = HealthProblemDN(code = 9001, message = "شناسه رکورد باید بزرگتر از 1 باشد.")

        val presentation = domain.toPresentation()

        assertEquals(9001, presentation.code)
        assertEquals("شناسه رکورد باید بزرگتر از 1 باشد.", presentation.message)
    }

    @Test
    fun `HealthProblemDN toPresentation preserves a null code`() {
        val domain = HealthProblemDN(code = null, message = "خطای نامشخص")

        val presentation = domain.toPresentation()

        assertEquals(null, presentation.code)
        assertEquals("خطای نامشخص", presentation.message)
    }
}
