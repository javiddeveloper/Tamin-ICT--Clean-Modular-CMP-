package com.tamin.taminhamrah.dataSource.agent

import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AgentMultipartBodyTest {

    @Test
    fun `the buffered prompt keeps its boundary and every part`() = runTest {
        val form = MultiPartFormDataContent(
            formData {
                append("data", """{"prompt":"سابقه"}""", Headers.build {
                    append(HttpHeaders.ContentType, "application/json; charset=UTF-8")
                })
                append("file", byteArrayOf(1, 2, 3), Headers.build {
                    append(HttpHeaders.ContentDisposition, "filename=\"voice.m4a\"")
                })
            }
        )

        val buffered = form.toByteArrayContent()
        val text = buffered.bytes().decodeToString()

        assertEquals(form.contentType, buffered.contentType)
        assertEquals(buffered.bytes().size.toLong(), buffered.contentLength)
        assertTrue(text.contains("name=data"))
        assertTrue(text.contains("""{"prompt":"سابقه"}"""))
        assertTrue(text.contains("filename=\"voice.m4a\""))
        assertTrue(text.trimEnd().endsWith("--"))
    }
}
