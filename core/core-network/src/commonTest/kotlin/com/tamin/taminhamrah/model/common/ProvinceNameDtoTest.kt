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
    fun `parses a row that omits the optional status fields`() {
        // The old app modelled every province field as nullable-with-default, so rows
        // lacking status/statusStartDate were tolerated. Absent keys must not fail the
        // whole list here either.
        val payload = """
            {"list":[{"provinceCode":"07","provinceName":"تهران"}],"total":1}
        """.trimIndent()

        val dto = json.decodeFromString<ProvinceNameDto>(payload)

        val province = dto.list.single()
        assertEquals("07", province.provinceCode)
        assertEquals(null, province.status)
        assertEquals(null, province.statusStartDate)
    }
}
