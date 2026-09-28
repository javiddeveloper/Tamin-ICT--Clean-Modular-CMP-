package com.tamin.taminhamrah.tools

import com.tamin.taminhamrah.tools.errorHandling.ErrorParserImpl
import com.tamin.taminhamrah.tools.errorHandling.ErrorUri
import com.tamin.taminhamrah.tools.errorHandling.HttpErrorCopy
import com.tamin.taminhamrah.tools.errorHandling.TaminErrorUriException
import com.tamin.taminhamrah.tools.errorHandling.shouldNavigateBack
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import com.tamin.taminhamrah.model.user.EditMobileResponseDto
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject
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

    // --- ensureSuccess ---

    @Test
    fun `ensureSuccess accepts a 200 with null data`() {
        val json = """{"status":200,"family":"SUCCESSFUL","reason":"OK","traceId":"x","data":null}"""
        Json { ignoreUnknownKeys = true }
            .decodeFromString(BaseDTO.serializer(Unit.serializer()), json)
            .ensureSuccess()
    }

    @Test
    fun `ensureSuccess throws on non-2xx`() {
        assertFailsWith<TaminErrorUriException> {
            BaseDTO<Unit>(status = 500, family = "SERVER_ERROR", reason = "boom").ensureSuccess()
        }
    }

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

    // --- HTTP status mapping / Arabic-script extraction ---

    @Test
    fun `extractData prefers JsonObject message over English reason`() {
        val dto = BaseDTO(
            status = 400,
            family = "CLIENT_ERROR",
            reason = "BadRequest",
            data = buildJsonObject { put("message", "کد ملی نامعتبر است") }
        )
        val error = assertFailsWith<TaminErrorUriException> { dto.extractData() }
        assertEquals(ErrorUri.INVALID_REQUEST, error.uri)
        assertEquals("کد ملی نامعتبر است", error.serverMessage)
        assertFalse(error.navigateBack)
    }

    @Test
    fun `extractData on 500 with English reason uses generic Persian fallback`() {
        val dto = BaseDTO<String>(status = 500, family = "SERVER_ERROR", reason = "Internal Server Error", data = null)
        val error = assertFailsWith<TaminErrorUriException> { dto.extractData() }
        assertEquals(ErrorUri.INTERNAL_ERROR, error.uri)
        assertEquals(HttpErrorCopy.GENERIC_SERVER, error.serverMessage)
    }

    @Test
    fun `extractData on 403 uses VPN copy and navigateBack`() {
        val dto = BaseDTO<String>(status = 403, family = "CLIENT_ERROR", reason = "Forbidden", data = null)
        val error = assertFailsWith<TaminErrorUriException> { dto.extractData() }
        assertEquals(ErrorUri.FORBIDDEN, error.uri)
        assertEquals(HttpErrorCopy.FORBIDDEN_VPN, error.serverMessage)
        assertTrue(error.navigateBack)

        val parsed = ErrorParserImpl().parseGeneralError(error)
        assertTrue(parsed.shouldNavigateBack())
        assertEquals(HttpErrorCopy.FORBIDDEN_VPN, parsed.subtitle)
    }

    @Test
    fun `extractData prefers ErrorCarrier message`() {
        val dto = BaseDTO(
            status = 500,
            family = "SERVER_ERROR",
            reason = "INTERNAL_SERVER_ERROR",
            data = object : ErrorCarrier {
                override val message: String = "خطای دیتای تایپ‌شده"
            }
        )
        val error = assertFailsWith<TaminErrorUriException> { dto.extractData() }
        assertEquals("خطای دیتای تایپ‌شده", error.serverMessage)
    }

    @Test
    fun `extractData maps 502 and 503 separately`() {
        val badGateway = assertFailsWith<TaminErrorUriException> {
            BaseDTO<String>(status = 502, family = "SERVER_ERROR", reason = "Bad Gateway").extractData()
        }
        assertEquals(HttpErrorCopy.BAD_GATEWAY, badGateway.serverMessage)

        val unavailable = assertFailsWith<TaminErrorUriException> {
            BaseDTO<String>(status = 503, family = "SERVER_ERROR", reason = "Service Unavailable").extractData()
        }
        assertEquals(HttpErrorCopy.SERVICE_UNAVAILABLE, unavailable.serverMessage)
    }

    @Test
    fun `persian problemMessage on hasError still passes through`() {
        val dto = BaseDTO<String>(
            status = 801,
            family = "System",
            reason = "ServerException",
            data = null,
            hasError = true,
            problems = listOf(ProblemDTO(errorCode = 1, errorMsg = "شناسه نامعتبر است"))
        )
        val error = assertFailsWith<TaminErrorUriException> { dto.extractData() }
        assertEquals(ErrorUri.SERVER_PROBLEM, error.uri)
        assertEquals("شناسه نامعتبر است", error.serverMessage)
    }

    // --- extractTypedData (um-mobile-api gateway error envelope: {cause, message}) ---

    @Test
    fun `extractTypedData decodes the typed payload on success`() {
        val dto = BaseDTO<kotlinx.serialization.json.JsonElement?>(
            status = 200,
            family = "System",
            reason = "OK",
            data = buildJsonObject {
                put("traceId", "trace-1")
                putJsonObject("data") {
                    put("hash", "hash-1")
                    put("expirationTime", 120L)
                }
            }
        )

        val decoded = dto.extractTypedData(Json, EditMobileResponseDto.serializer())

        assertEquals("trace-1", decoded.traceId)
        assertEquals("hash-1", decoded.data?.hash)
        assertEquals(120L, decoded.data?.expirationTime)
    }

    @Test
    fun `extractTypedData surfaces the gateway's own message on a 4xx error`() {
        val dto = BaseDTO<kotlinx.serialization.json.JsonElement?>(
            status = 409,
            family = "CLIENT_ERROR",
            reason = "Conflict",
            data = buildJsonObject {
                put("cause", "DUPLICATE_MOBILE")
                put("message", "این شماره موبایل قبلا ثبت شده است")
            }
        )
        val error = assertFailsWith<TaminErrorUriException> {
            dto.extractTypedData(Json, String.serializer())
        }
        assertEquals("این شماره موبایل قبلا ثبت شده است", error.serverMessage)
        assertEquals(ErrorUri.INVALID_REQUEST, error.uri)

        val parsed = ErrorParserImpl().parseGeneralError(error)
        assertEquals("این شماره موبایل قبلا ثبت شده است", parsed.subtitle)
    }

    @Test
    fun `extractTypedData surfaces the gateway's own message on a 5xx error`() {
        val dto = BaseDTO<kotlinx.serialization.json.JsonElement?>(
            status = 502,
            family = "SERVER_ERROR",
            reason = "Bad Gateway",
            data = buildJsonObject {
                put("cause", "UPSTREAM_TIMEOUT")
                put("message", "سرویس موقتا در دسترس نیست")
            }
        )
        val error = assertFailsWith<TaminErrorUriException> {
            dto.extractTypedData(Json, String.serializer())
        }
        assertEquals("سرویس موقتا در دسترس نیست", error.serverMessage)
        assertEquals(ErrorUri.SERVER_SERVICE_UNAVAILABLE, error.uri)
    }

    /** Was `ErrorUri.fromString("SERVER_ERROR: …")` — always UNKNOWN, and no message at all. */
    @Test
    fun `extractTypedData without a gateway message falls back to the status copy`() {
        val dto = BaseDTO<kotlinx.serialization.json.JsonElement?>(
            status = 500,
            family = "SERVER_ERROR",
            reason = "Internal Server Error",
            data = null,
        )
        val error = assertFailsWith<TaminErrorUriException> {
            dto.extractTypedData(Json, String.serializer())
        }
        assertEquals(ErrorUri.INTERNAL_ERROR, error.uri)
        assertEquals(HttpErrorCopy.GENERIC_SERVER, error.serverMessage)
    }

    // --- requireSuccessStatus (calls whose reply carries nothing to read, e.g. save-contact) ---

    @Test
    fun `requireSuccessStatus lets a 2xx through even with no data`() {
        BaseDTO<Unit>(status = 200, family = "SUCCESSFUL", reason = "OK").requireSuccessStatus()
    }

    /** `errorText` is what the reply converter recovers from a failed body's `data.message`. */
    @Test
    fun `requireSuccessStatus fails a 4xx with the server's own message`() {
        val dto = BaseDTO<Unit>(
            status = 400,
            family = "CLIENT_ERROR",
            reason = "Bad Request",
            errorText = "کد پستی نامعتبر است",
        )
        val error = assertFailsWith<TaminErrorUriException> { dto.requireSuccessStatus() }

        assertEquals(ErrorUri.INVALID_REQUEST, error.uri)
        assertEquals("کد پستی نامعتبر است", error.serverMessage)
    }

    @Test
    fun `requireSuccessStatus fails a 5xx with the status copy when the server says nothing readable`() {
        val dto = BaseDTO<Unit>(
            status = 500,
            family = "SERVER_ERROR",
            reason = "Internal Server Error",
        )
        val error = assertFailsWith<TaminErrorUriException> { dto.requireSuccessStatus() }

        assertEquals(ErrorUri.INTERNAL_ERROR, error.uri)
        assertEquals(HttpErrorCopy.GENERIC_SERVER, error.serverMessage)
    }
}
