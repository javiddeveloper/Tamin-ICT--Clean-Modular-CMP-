package com.tamin.taminhamrah.di

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

class LenientReplyConverterTest {

    /** Nullable, no default — the shape that made a whole reply fail when one field was absent. */
    @Serializable
    private data class Sample(
        @SerialName("id") val id: Int?,
        @SerialName("title") val title: String?,
    )

    private val converter = LenientReplyConverter(taminJson)

    @Test
    fun `a reply missing a nullable field reads it as null`() = runTest {
        val decoded = converter.deserialize(Charsets.UTF_8, typeInfo<Sample>(), ByteReadChannel("""{"id":7}"""))

        assertEquals(Sample(id = 7, title = null), decoded)
    }

    @Test
    fun `a request body still sends its nulls`() = runTest {
        val body = converter.serialize(ContentType.Application.Json, Charsets.UTF_8, typeInfo<Sample>(), Sample(7, null))

        assertEquals(taminJson.encodeToString(Sample.serializer(), Sample(7, null)), (body as TextContent).text)
        assertEquals(true, body.text.contains("\"title\": null"))
    }
}
