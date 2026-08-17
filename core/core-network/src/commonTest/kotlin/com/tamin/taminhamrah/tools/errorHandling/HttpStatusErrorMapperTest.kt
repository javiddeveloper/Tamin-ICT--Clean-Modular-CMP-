package com.tamin.taminhamrah.tools.errorHandling

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class HttpStatusErrorMapperTest {

    @Test
    fun `401 maps to INVALID_AUTH with login copy when the body is not Arabic-script`() {
        val mapped = HttpStatusErrorMapper.map(401, "Unauthorized", cause = null)
        assertEquals(ErrorUri.INVALID_AUTH, mapped.uri)
        assertEquals("لطفا دوباره وارد شوید", mapped.userMessage)
        assertFalse(mapped.navigateBack)
    }

    @Test
    fun `403 always uses the VPN copy and requests back-navigation`() {
        val mapped = HttpStatusErrorMapper.map(403, "Forbidden", cause = null)
        assertEquals(ErrorUri.FORBIDDEN, mapped.uri)
        assertEquals(HttpErrorCopy.FORBIDDEN_VPN, mapped.userMessage)
        assertTrue(mapped.navigateBack)
    }

    @Test
    fun `500 prefers bank-account guidance over a matching Arabic-script snippet`() {
        val mapped = HttpStatusErrorMapper.map(
            status = 500,
            rawMessage = "شما فاقد شماره حساب بانکی می باشید",
            cause = null
        )
        assertEquals(ErrorUri.INTERNAL_ERROR, mapped.uri)
        assertEquals(HttpErrorCopy.BANK_ACCOUNT, mapped.userMessage)
    }

    @Test
    fun `500 maps sso connection exception to sabte-ahval copy`() {
        val mapped = HttpStatusErrorMapper.map(
            status = 500,
            rawMessage = "sso.to.sa.connection.exception",
            cause = null
        )
        assertEquals(HttpErrorCopy.SABTE_AHVAL, mapped.userMessage)
    }

    @Test
    fun `500 maps open pension request token`() {
        val mapped = HttpStatusErrorMapper.map(
            status = 500,
            rawMessage = "pension.request.current.request.open",
            cause = null
        )
        assertEquals(HttpErrorCopy.OPEN_REQUEST, mapped.userMessage)
    }

    @Test
    fun `500 maps already-saved survivor request token`() {
        val mapped = HttpStatusErrorMapper.map(
            status = 500,
            rawMessage = "pension.survivor.request.survivor.already.saved",
            cause = null
        )
        assertEquals(HttpErrorCopy.SURVIVOR_ALREADY_SAVED, mapped.userMessage)
    }

    @Test
    fun `500 INTERNAL_SERVER_ERROR falls back to generic server copy`() {
        val mapped = HttpStatusErrorMapper.map(
            status = 500,
            rawMessage = "INTERNAL_SERVER_ERROR",
            cause = null
        )
        assertEquals(HttpErrorCopy.GENERIC_SERVER, mapped.userMessage)
        assertTrue(mapped.userMessage.contains("امکان اتصال به اینترنت وجود ندارد"))
        assertFalse(mapped.userMessage.contains("<br/>"))
    }

    @Test
    fun `500 passes through Persian reason when no specific token matches`() {
        val mapped = HttpStatusErrorMapper.map(
            status = 500,
            rawMessage = "سرویس در دسترس نیست",
            cause = null
        )
        assertEquals("سرویس در دسترس نیست", mapped.userMessage)
    }

    @Test
    fun `500 english body without tokens uses generic server copy`() {
        val mapped = HttpStatusErrorMapper.map(500, "boom from nginx", cause = null)
        assertEquals(HttpErrorCopy.GENERIC_SERVER, mapped.userMessage)
    }

    @Test
    fun `502 and 503 and 504 use distinct copy`() {
        assertEquals(
            HttpErrorCopy.BAD_GATEWAY,
            HttpStatusErrorMapper.map(502, "Bad Gateway", null).userMessage
        )
        assertEquals(
            HttpErrorCopy.SERVICE_UNAVAILABLE,
            HttpStatusErrorMapper.map(503, null, null).userMessage
        )
        assertEquals(
            HttpErrorCopy.GATEWAY_TIMEOUT,
            HttpStatusErrorMapper.map(504, "Gateway Timeout", null).userMessage
        )
    }

    @Test
    fun `400 maps known pension tokens`() {
        assertEquals(
            HttpErrorCopy.DISABILITY_COMMISSION_NOT_READY,
            HttpStatusErrorMapper.map(400, "pension.disability.commission.notready.exception", null).userMessage
        )
        assertEquals(
            HttpErrorCopy.LOGIN_AGAIN,
            HttpStatusErrorMapper.map(400, "invalid_grant", null).userMessage
        )
    }
}
