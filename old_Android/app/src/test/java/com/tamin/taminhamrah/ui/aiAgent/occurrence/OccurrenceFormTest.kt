package com.tamin.taminhamrah.ui.aiAgent.occurrence

import com.tamin.taminhamrah.data.repository.ai.model.FieldValidationType
import com.tamin.taminhamrah.data.repository.ai.model.FormFieldType
import com.tamin.taminhamrah.data.repository.ai.model.FormStep
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceNameEnum
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.occurrence.OccurrenceFormKeys
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.occurrence.buildOccurrenceSchema
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.occurrence.toOccurrenceReq
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for the accident-report (1011) AI form. Covers, per the requirements:
 *  - required-field validation triggers correctly (per step),
 *  - a step cannot advance when validation fails,
 *  - submission maps the payload into the correctly-shaped OccurrenceReq.
 *
 * Validation is exercised through [validateStep], a faithful pure-JVM mirror of
 * SchemaBusinessGenerator.validateFields (same skip set, same rule semantics, including
 * "optional + blank ⇒ valid"). This lets us assert that the declarative schema encodes
 * rules that will actually block/allow advancement, without the Android view layer.
 */
class OccurrenceFormTest {

    private fun step(index: Int): FormStep =
        buildOccurrenceSchema(payload = null, step = index, showCancelButton = true)
            .steps.first { it.index == index }

    /** Mirror of the renderer's per-step validator; returns true when the step may advance. */
    private fun validateStep(step: FormStep, values: Map<String, String>): Boolean {
        for (field in step.fields) {
            if (field.type == FormFieldType.HIDDEN ||
                field.type == FormFieldType.READ_ONLY ||
                field.type == FormFieldType.CHART
            ) continue

            val value = values[field.id].orEmpty()
            if (field.required && value.isBlank()) return false
            if (value.isBlank()) continue // optional + blank ⇒ valid

            for (v in field.validations) {
                val failed = when (v.type) {
                    FieldValidationType.MIN_LENGTH -> value.length < (v.value?.toIntOrNull() ?: 0)
                    FieldValidationType.MAX_LENGTH -> value.length > (v.value?.toIntOrNull() ?: Int.MAX_VALUE)
                    FieldValidationType.LENGTH -> value.length != (v.value?.toIntOrNull() ?: value.length)
                    FieldValidationType.PATTERN -> !Regex(v.value.orEmpty()).matches(value)
                    FieldValidationType.STARTS_WITH -> !value.startsWith(v.value.orEmpty())
                }
                if (failed) return false
            }
        }
        return true
    }

    // ── Schema shape ────────────────────────────────────────────────────────

    @Test
    fun `schema has four input steps plus a result step wired to the right actions`() {
        val schema = buildOccurrenceSchema(payload = null, step = 1, showCancelButton = true)
        assertEquals(5, schema.steps.size)
        assertEquals(OccurrenceFormKeys.SCHEMA_KEY, schema.key)

        assertEquals(
            ServiceNameEnum.OCCURRENCE_REPORT_WORKSHOP.key,
            step(1).actions.first().id
        )
        assertEquals(
            ServiceNameEnum.OCCURRENCE_REPORT_PERSONAL.key,
            step(2).actions.first().id
        )
        assertEquals(
            ServiceNameEnum.OCCURRENCE_REPORT_ACCIDENT.key,
            step(3).actions.first().id
        )
        assertEquals(
            ServiceNameEnum.OCCURRENCE_REPORT_SUBMIT.key,
            step(4).actions.first().id
        )
        // Result step is terminal: no fields, no actions.
        assertTrue(step(5).fields.isEmpty())
        assertTrue(step(5).actions.isEmpty())
    }

    @Test
    fun `national id is read-only and document field is a file upload`() {
        val nationalId = step(1).fields.first { it.id == OccurrenceFormKeys.NATIONAL_ID }
        assertEquals(FormFieldType.READ_ONLY, nationalId.type)

        val documents = step(4).fields.first { it.id == OccurrenceFormKeys.DOCUMENTS }
        assertEquals(FormFieldType.FILE_UPLOAD, documents.type)
        assertTrue(documents.required)
    }

    // ── Required-field validation triggers / blocks advancement ─────────────

    @Test
    fun `step 1 cannot advance without birth date`() {
        assertFalse(validateStep(step(1), emptyMap()))
        assertTrue(validateStep(step(1), mapOf(OccurrenceFormKeys.BIRTH_DATE to "1402/01/01")))
    }

    @Test
    fun `step 1 rejects a non-jalali birth date`() {
        assertFalse(validateStep(step(1), mapOf(OccurrenceFormKeys.BIRTH_DATE to "14020101")))
    }

    @Test
    fun `step 2 requires workshop, employer name and a valid employer mobile`() {
        val base = mapOf(
            OccurrenceFormKeys.WORKSHOP_ID to "123|45",
            OccurrenceFormKeys.EMPLOYER_NAME to "علی علوی",
            OccurrenceFormKeys.EMPLOYER_PHONE to "09123456789",
            OccurrenceFormKeys.WORKSHOP_ADDRESS to "تهران"
        )
        assertTrue(validateStep(step(2), base))

        // Missing required workshop selection blocks advance.
        assertFalse(validateStep(step(2), base - OccurrenceFormKeys.WORKSHOP_ID))
        // Bad employer mobile blocks advance.
        assertFalse(validateStep(step(2), base + (OccurrenceFormKeys.EMPLOYER_PHONE to "12345")))
    }

    @Test
    fun `step 2 optional workshop phone left blank does not block advance`() {
        val base = mapOf(
            OccurrenceFormKeys.WORKSHOP_ID to "123|45",
            OccurrenceFormKeys.EMPLOYER_NAME to "علی علوی",
            OccurrenceFormKeys.EMPLOYER_PHONE to "09123456789",
            OccurrenceFormKeys.WORKSHOP_ADDRESS to "تهران",
            OccurrenceFormKeys.WORKSHOP_PHONE to "" // optional, empty
        )
        assertTrue(validateStep(step(2), base))
    }

    @Test
    fun `step 4 enforces documents and a minimum-length description`() {
        val valid = mapOf(
            OccurrenceFormKeys.ACCIDENT_DATE to "1403/05/10",
            OccurrenceFormKeys.ACCIDENT_TIME to "12:30",
            OccurrenceFormKeys.ACCIDENT_RESULT to "4",
            OccurrenceFormKeys.ACCIDENT_LOCATION to "محوطه کارگاه",
            OccurrenceFormKeys.ACCIDENT_DESCRIPTION to "سقوط از داربست هنگام کار در ارتفاع رخ داد",
            OccurrenceFormKeys.DOCUMENTS to "1:guid-1"
        )
        assertTrue(validateStep(step(4), valid))

        // No uploaded documents blocks advance.
        assertFalse(validateStep(step(4), valid + (OccurrenceFormKeys.DOCUMENTS to "")))
        // Too-short description blocks advance.
        assertFalse(validateStep(step(4), valid + (OccurrenceFormKeys.ACCIDENT_DESCRIPTION to "کوتاه")))
    }

    // ── Submission payload mapping ──────────────────────────────────────────

    @Test
    fun `toOccurrenceReq maps the payload into the correctly shaped request`() {
        val payload = mapOf<String, Any?>(
            OccurrenceFormKeys.NATIONAL_ID to "0012345678",
            OccurrenceFormKeys.BIRTH_DATE to "1370/02/03",
            OccurrenceFormKeys.WORKSHOP_ID to "9988|12",
            OccurrenceFormKeys.EMPLOYER_NAME to "علی علوی",
            OccurrenceFormKeys.EMPLOYER_PHONE to "09120000000",
            OccurrenceFormKeys.WORKSHOP_ADDRESS to "تهران",
            OccurrenceFormKeys.MARITAL_STATUS to "1",
            OccurrenceFormKeys.EMPLOYMENT_DATE to "1399/01/01",
            OccurrenceFormKeys.JOB_DESCRIPTION to "کارگر فنی",
            OccurrenceFormKeys.WORK_START to "08:00",
            OccurrenceFormKeys.WORK_END to "16:00",
            OccurrenceFormKeys.ACCIDENT_DATE to "1403/05/10",
            OccurrenceFormKeys.ACCIDENT_TIME to "12:30",
            OccurrenceFormKeys.ACCIDENT_RESULT to "4",
            OccurrenceFormKeys.ACCIDENT_DESCRIPTION to "شرح کامل حادثه ناشی از کار",
            OccurrenceFormKeys.INSURANCE_ID to "555"
        )

        val req = payload.toOccurrenceReq()

        assertEquals("0012345678", req.pNationalCode)
        assertEquals("672352200000", req.birthDate) // timestamp format
        assertEquals("9988", req.workshopCode)  // workshop_id split on "|"
        assertEquals("12", req.workshopBranchCode)
        assertEquals("علی علوی", req.bossFullName)
        assertEquals(1L, req.marriageStatusCode)
        assertEquals("1584649800000", req.employeeDate)
        assertEquals("1722371400000", req.occurrenceDate)
        assertEquals(4L, req.occurrenceResult)
        assertEquals("12:30", req.occurrenceTime)
        assertEquals("555", req.insuranceID)
    }

    @Test
    fun `toOccurrenceReq recovers per-document type ids from the payload`() {
        val req = mapOf<String, Any?>(
            OccurrenceFormKeys.DOCUMENTS to "7:guid-a,guid-b"
        ).toOccurrenceReq()

        val docs = req.occurrenceDocumentList!!
        assertEquals(2, docs.size)
        // First entry carried an explicit doc type.
        assertEquals("7", docs[0].ocurrenceDocumentType?.docTypeId)
        assertEquals("guid-a", docs[0].documentFile?.id)
        // Second entry had no type ⇒ default.
        assertEquals(OccurrenceFormKeys.DEFAULT_DOC_TYPE_ID, docs[1].ocurrenceDocumentType?.docTypeId)
        assertEquals("guid-b", docs[1].documentFile?.id)
    }

    @Test
    fun `toOccurrenceReq leaves optional numeric fields null when absent`() {
        val req = emptyMap<String, Any?>().toOccurrenceReq()
        assertNull(req.marriageStatusCode)
        assertNull(req.occurrenceResult)
        assertTrue(req.occurrenceDocumentList!!.isEmpty())
    }
}
