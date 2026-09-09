package com.tamin.taminhamrah.feature.developerOptions.tokens

import kotlinx.datetime.Clock
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

/**
 * The token screen's «منقضی شده / N دقیقه تا انقضا» line is read straight off the token, so the
 * decoding has to survive the shapes real tokens arrive in — base64url without padding above all.
 */
class JwtExpiryTest {

    @OptIn(ExperimentalEncodingApi::class)
    private fun jwtWithClaims(claimsJson: String): String {
        // Real JWTs are base64url with the padding stripped; that stripping is what the decoder
        // has to put back, so the fixture must reproduce it rather than encode plain base64.
        val payload = Base64.UrlSafe.encode(claimsJson.encodeToByteArray()).trimEnd('=')
        return "header.$payload.signature"
    }

    private fun jwtExpiringIn(seconds: Long): String =
        jwtWithClaims("""{"exp":${Clock.System.now().epochSeconds + seconds}}""")

    @Test
    fun `a token that expires later reports the time remaining`() {
        val expiry = readJwtExpiry(jwtExpiringIn(seconds = 3600))

        val valid = assertIs<JwtExpiry.Valid>(expiry)
        assertTrue(
            valid.remaining.inWholeSeconds in 3500..3600,
            "expected about an hour left, was ${valid.remaining}",
        )
    }

    @Test
    fun `a token whose exp has passed reports how long ago`() {
        val expiry = readJwtExpiry(jwtExpiringIn(seconds = -600))

        val expired = assertIs<JwtExpiry.Expired>(expiry)
        assertTrue(
            expired.since.inWholeSeconds in 590..700,
            "expected about ten minutes ago, was ${expired.since}",
        )
    }

    @Test
    fun `payload lengths that need padding still decode`() {
        // One claim per length class, so a padding bug can't hide behind a conveniently sized one.
        val paddings = listOf(
            """{"exp":1788676433}""",
            """{"exp":1788676433,"a":"1"}""",
            """{"exp":1788676433,"ab":"1"}""",
            """{"exp":1788676433,"abc":"1"}""",
        )

        paddings.forEach { claims ->
            assertTrue(
                readJwtExpiry(jwtWithClaims(claims)) !is JwtExpiry.Unknown,
                "failed to read exp out of $claims",
            )
        }
    }

    @Test
    fun `an opaque token is unknown rather than expired`() {
        // Not every token is a JWT — reporting "expired" for one would send a tester chasing a
        // refresh that was never the problem.
        assertEquals(JwtExpiry.Unknown, readJwtExpiry("an-opaque-token"))
    }

    @Test
    fun `a JWT with no exp claim is unknown`() {
        assertEquals(JwtExpiry.Unknown, readJwtExpiry(jwtWithClaims("""{"sub":"442e832b"}""")))
    }

    @Test
    fun `a missing token is unknown`() {
        assertEquals(JwtExpiry.Unknown, readJwtExpiry(null))
    }
}
