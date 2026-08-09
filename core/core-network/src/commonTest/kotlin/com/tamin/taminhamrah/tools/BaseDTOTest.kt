package com.tamin.taminhamrah.tools

import com.tamin.taminhamrah.tools.errorHandling.TaminErrorUriException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Covers the `hasError`/`problems` envelope added to BaseDTO for endpoints
 * (health profile in particular) that report structured business problems,
 * e.g. {"data":null,"problems":[{"error_Code":9001,"error_Msg":"..."}],"hasError":true,
 * "status":801,"family":"System","reason":"ServerException"}.
 */
class BaseDTOTest {

    private val sampleProblem = ProblemDTO(errorCode = 9001, errorMsg = "شناسه رکورد باید بزرگتر از 1 باشد.")

    // --- hasProblems / problemMessage ---

    @Test
    fun `hasProblems is false for a plain success response`() {
        val dto = BaseDTO(status = 200, family = "System", reason = "OK", data = "hi")
        assertFalse(dto.hasProblems)
        assertEquals(null, dto.problemMessage)
    }

    @Test
    fun `hasProblems is true when problems list is non-empty`() {
        val dto = BaseDTO(status = 801, family = "System", reason = "ServerException", data = null, problems = listOf(sampleProblem))
        assertTrue(dto.hasProblems)
        assertEquals(sampleProblem.errorMsg, dto.problemMessage)
    }

    @Test
    fun `hasProblems is true when hasError is true even with an empty problems list`() {
        val dto = BaseDTO<String>(status = 801, family = "System", reason = "ServerException", data = null, hasError = true, problems = emptyList())
        assertTrue(dto.hasProblems)
        assertEquals(null, dto.problemMessage)
    }

    @Test
    fun `problemMessage joins multiple problems and skips blank messages`() {
        val dto = BaseDTO<String>(
            status = 801, family = "System", reason = "ServerException", data = null,
            problems = listOf(
                ProblemDTO(errorCode = 1, errorMsg = "خطای اول"),
                ProblemDTO(errorCode = 2, errorMsg = " "),
                ProblemDTO(errorCode = 3, errorMsg = "خطای سوم")
            )
        )
        assertEquals("خطای اول\nخطای سوم", dto.problemMessage)
    }

    // --- extractDataOrProblems ---

    @Test
    fun `extractDataOrProblems returns the data on success`() {
        val dto = BaseDTO(status = 200, family = "System", reason = "OK", data = "value")
        val outcome = dto.extractDataOrProblems()
        assertEquals("value", outcome.data)
        assertTrue(outcome.problems.isEmpty())
    }

    @Test
    fun `extractDataOrProblems returns problems instead of throwing when problems is non-empty`() {
        val dto = BaseDTO(status = 801, family = "System", reason = "ServerException", data = null, problems = listOf(sampleProblem))
        val outcome = dto.extractDataOrProblems()
        assertEquals(null, outcome.data)
        assertEquals(listOf(sampleProblem), outcome.problems)
    }

    @Test
    fun `extractDataOrProblems still throws for a generic client error with no problems detail`() {
        val dto = BaseDTO<String>(status = 400, family = "System", reason = "BadRequest", data = null)
        assertFailsWith<TaminErrorUriException> { dto.extractDataOrProblems() }
    }

    @Test
    fun `extractDataOrProblems still throws when hasError is true but there is no problems detail`() {
        val dto = BaseDTO<String>(status = 801, family = "System", reason = "ServerException", data = null, hasError = true)
        assertFailsWith<TaminErrorUriException> { dto.extractDataOrProblems() }
    }

    // --- extractMessageOrProblems ---

    @Test
    fun `extractMessageOrProblems returns problems instead of throwing when problems is non-empty`() {
        val dto = BaseDTO<kotlinx.serialization.json.JsonElement?>(
            status = 801, family = "System", reason = "ServerException", data = null, problems = listOf(sampleProblem)
        )
        val outcome = dto.extractMessageOrProblems()
        assertEquals(null, outcome.data)
        assertEquals(listOf(sampleProblem), outcome.problems)
    }

    @Test
    fun `extractMessageOrProblems falls back to reason on success with no primitive data`() {
        val dto = BaseDTO<kotlinx.serialization.json.JsonElement?>(
            status = 200, family = "System", reason = "Saved successfully", data = null
        )
        val outcome = dto.extractMessageOrProblems()
        assertEquals("Saved successfully", outcome.data)
        assertTrue(outcome.problems.isEmpty())
    }
}
