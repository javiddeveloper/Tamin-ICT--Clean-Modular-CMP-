package com.tamin.taminhamrah.model.workshop

import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Guards deserialization of `debit-objection/objection-all`'s `voteType` object.
 *
 * The wire key is `voteTypeDesc`, confirmed against the legacy production app's own model for the
 * same field (`WorkShopObjection.VoteType.voteTypeDesc` in `AllObjectionsResponse.kt`, and the
 * independent `WorkShopDebtResponse.VoteType.voteTypeDesc`) — not `description`, which this DTO used
 * to declare and which silently deserialized to null on every real response.
 */
class WorkShopObjectionDTOTest {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    @Test
    fun `parses voteTypeDesc into VoteTypeDTO description`() {
        val payload = """
            {"seqNo":1403008720,"voteType":{"voteTypeCode":"1","voteTypeDesc":"رای هیئت بدوی"}}
        """.trimIndent()

        val dto = json.decodeFromString<WorkShopObjectionDTO>(payload)

        assertEquals("رای هیئت بدوی", dto.voteType?.description)
    }
}
