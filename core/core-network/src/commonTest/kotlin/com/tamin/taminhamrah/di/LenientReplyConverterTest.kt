package com.tamin.taminhamrah.di

import com.tamin.taminhamrah.tools.BaseDTO
import com.tamin.taminhamrah.tools.errorHandling.ErrorUri
import com.tamin.taminhamrah.tools.errorHandling.TaminErrorUriException
import com.tamin.taminhamrah.tools.extractData
import io.ktor.http.ContentType
import io.ktor.http.content.TextContent
import io.ktor.util.reflect.typeInfo
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.charsets.Charsets
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class LenientReplyConverterTest {

    /** Nullable, no default — the shape that made a whole reply fail when one field was absent. */
    @Serializable
    private data class Sample(
        @SerialName("id") val id: Int?,
        @SerialName("title") val title: String?,
    )

    private val converter = LenientReplyConverter(taminJson)

    private suspend inline fun <reified T> read(body: String): Any? =
        converter.deserialize(Charsets.UTF_8, typeInfo<T>(), ByteReadChannel(body))

    @Test
    fun `a reply missing a nullable field reads it as null`() = runTest {
        assertEquals(Sample(id = 7, title = null), read<Sample>("""{"id":7}"""))
    }

    @Test
    fun `a request body still sends its nulls`() = runTest {
        val body = converter.serialize(ContentType.Application.Json, Charsets.UTF_8, typeInfo<Sample>(), Sample(7, null))

        assertEquals(taminJson.encodeToString(Sample.serializer(), Sample(7, null)), (body as TextContent).text)
        assertEquals(true, body.text.contains("\"title\": null"))
    }

    /** EM-2716 follow-up: the typed `data` dropped `message`, so the user saw only generic copy. */
    @Test
    fun `a typed error reply keeps the server's message`() = runTest {
        val dto = read<BaseDTO<Sample>>(
            """{"status":500,"family":"SERVER_ERROR","reason":"Internal Server Error",
               "data":{"cause":"ProxyProcessingException","message":"خطا در واکشی اطلاعات از سامانه ی وزارت بهداشت"}}"""
        ) as BaseDTO<*>

        val error = assertFailsWith<TaminErrorUriException> { dto.extractData() }
        assertEquals("خطا در واکشی اطلاعات از سامانه ی وزارت بهداشت", error.serverMessage)
    }

    /** An array `data` could not decode into an object type at all; the old app showed the violations. */
    @Test
    fun `validation violations reach the user`() = runTest {
        val dto = read<BaseDTO<Sample>>(
            """{"status":400,"family":"CLIENT_ERROR","reason":"Bad Request",
               "data":[{"propertyViolations":{"mobile":["شماره موبایل نامعتبر است"]}}]}"""
        ) as BaseDTO<*>

        assertNull(dto.data)
        val error = assertFailsWith<TaminErrorUriException> { dto.extractData() }
        assertEquals(ErrorUri.INVALID_REQUEST, error.uri)
        assertEquals("شماره موبایل نامعتبر است", error.serverMessage)
    }

    @Test
    fun `a bare string data reaches the user`() = runTest {
        val dto = read<BaseDTO<Sample>>(
            """{"status":400,"family":"CLIENT_ERROR","reason":"Bad Request","data":"کد ملی نامعتبر است"}"""
        ) as BaseDTO<*>

        val error = assertFailsWith<TaminErrorUriException> { dto.extractData() }
        assertEquals("کد ملی نامعتبر است", error.serverMessage)
    }

    /**
     * A type with no `status` of its own cannot say it failed: decoded leniently, an error envelope
     * became an all-null "success". It has to be the failure it is.
     */
    @Test
    fun `an error envelope aimed at a bare type is a failure, not an empty success`() = runTest {
        val error = assertFailsWith<TaminErrorUriException> {
            read<Sample>("""{"status":500,"family":"SERVER_ERROR","reason":"Internal Server Error","data":{"message":"سرویس در دسترس نیست"}}""")
        }
        assertEquals("سرویس در دسترس نیست", error.serverMessage)
    }

    @Test
    fun `a successful envelope still decodes its data`() = runTest {
        val dto = read<BaseDTO<Sample>>("""{"status":200,"family":"SUCCESSFUL","reason":"OK","data":{"id":3}}""") as BaseDTO<*>

        assertEquals(Sample(3, null), dto.extractData())
    }
}
