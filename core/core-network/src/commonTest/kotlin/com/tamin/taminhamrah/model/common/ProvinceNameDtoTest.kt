package com.tamin.taminhamrah.model.common

import com.tamin.core.network.model.common.ProvinceNameDto
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Guards deserialization of `proxy/models/province`.
 *
 * The Json instance mirrors the client configuration in NetworkKoinModule
 * (ignoreUnknownKeys + isLenient, no coerceInputValues), so a payload that
 * passes here is one the real client can parse.
 */
class ProvinceNameDtoTest {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    @Test
    fun `parses a full province payload`() {
        val payload = """
            {"list":[{"provinceCode":"00","provinceName":"مرکزی","status":"1","statusStartDate":"1390/01/01"}],"total":1}
        """.trimIndent()

        val dto = json.decodeFromString<ProvinceNameDto>(payload)

        assertEquals(1, dto.total)
        assertEquals("00", dto.list.single().provinceCode)
        assertEquals("مرکزی", dto.list.single().provinceName)
    }

    @Test
    fun `keeps explicit nulls on the optional fields`() {
        // ProvinceDto declares no defaults, so every key is required — the server must
        // send status/statusStartDate even when their value is null.
        val payload = """
            {"list":[{"provinceCode":"07","provinceName":"تهران","status":null,"statusStartDate":null}],"total":1}
        """.trimIndent()

        val dto = json.decodeFromString<ProvinceNameDto>(payload)

        val province = dto.list.single()
        assertEquals("07", province.provinceCode)
        assertEquals(null, province.status)
        assertEquals(null, province.statusStartDate)
    }
}
