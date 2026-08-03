package com.tamin.taminhamrah.data.mapper.health

import com.tamin.taminhamrah.tools.ProblemDTO
import kotlin.test.Test
import kotlin.test.assertEquals

class HealthDeclarationMappersTest {

    @Test
    fun `ProblemDTO toDomain copies code and message`() {
        val dto = ProblemDTO(errorCode = 9001, errorMsg = "شناسه رکورد باید بزرگتر از 1 باشد.")

        val domain = dto.toDomain()

        assertEquals(9001, domain.code)
        assertEquals("شناسه رکورد باید بزرگتر از 1 باشد.", domain.message)
    }

    @Test
    fun `ProblemDTO toDomain falls back to a generic message when errorMsg is blank`() {
        val dto = ProblemDTO(errorCode = 9002, errorMsg = "  ")

        val domain = dto.toDomain()

        assertEquals(9002, domain.code)
        assertEquals("خطای نامشخص", domain.message)
    }

    @Test
    fun `ProblemDTO toDomain falls back to a generic message when errorMsg is null`() {
        val dto = ProblemDTO(errorCode = null, errorMsg = null)

        val domain = dto.toDomain()

        assertEquals(null, domain.code)
        assertEquals("خطای نامشخص", domain.message)
    }
}
