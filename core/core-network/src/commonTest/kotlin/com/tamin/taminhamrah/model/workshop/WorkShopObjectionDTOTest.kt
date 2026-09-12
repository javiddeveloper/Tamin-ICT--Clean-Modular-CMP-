package com.tamin.taminhamrah.model.workshop

import com.tamin.taminhamrah.model.utils.ListData
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

    /**
     * `SmsMessageDTO.status` is declared `String`, but the legacy app's own model for this same
     * endpoint (`SmsModel.status`) types it `Int` — the wire value is a raw JSON number, not a
     * quoted string. The app's real `Json` config (`isLenient = true`, matched here) coerces that
     * into the `String` field without throwing or losing the value.
     */
    @Test
    fun `a raw JSON number status decodes into SmsMessageDTO's String field`() {
        val payload = """{"smsDescription":"x","status":1}"""
        val dto = json.decodeFromString<SmsMessageDTO>(payload)
        assertEquals("1", dto.status)
    }

    /**
     * Real `debit-objection/objection-detail` capture: 5 messages, 3 of them (ids 56405874,
     * 57155124, 58013609) sharing identical `smsDescription`/`status` — the service resent the same
     * committee notice three times. `SmsMessageDTO` used to have no `id` field, so after mapping to
     * `SmsMessagePR` these three became structurally identical rows, and the shared paged-list dedup
     * (`PagedListState.loaded`, which drops rows equal to one already shown — correct for the other
     * seven کارگاه lists, where a repeat really is the same row) silently collapsed them into one:
     * a server total of 5 rendered as 3 in the app. `id` is what lets three distinct messages survive
     * that dedup despite having the same text and status.
     */
    @Test
    fun `each message in a real objection-detail response keeps its own distinct id`() {
        val payload = """
            {"total":5,"list":[
                {"id":56219181,"seqNo":56219178,"smsDescription":"اعتراض ثبت شد","status":"3"},
                {"id":56405874,"seqNo":56219178,"smsDescription":"جلسه هیات تشکیل می‌شود","status":"5"},
                {"id":57155124,"seqNo":56219178,"smsDescription":"جلسه هیات تشکیل می‌شود","status":"5"},
                {"id":58013609,"seqNo":56219178,"smsDescription":"جلسه هیات تشکیل می‌شود","status":"5"},
                {"id":65188505,"seqNo":56219178,"smsDescription":"null","status":"6"}
            ]}
        """.trimIndent()

        val list = json.decodeFromString<ListData<SmsMessageDTO>>(payload)
        val ids = list.list.orEmpty().map { it.id }

        assertEquals(listOf(56219181L, 56405874L, 57155124L, 58013609L, 65188505L), ids)
        assertEquals(ids.size, ids.distinct().size)
    }
}
