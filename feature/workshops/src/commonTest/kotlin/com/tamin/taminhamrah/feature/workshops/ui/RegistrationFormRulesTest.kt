package com.tamin.taminhamrah.feature.workshops.ui

import com.tamin.taminhamrah.feature.workshops.ui.model.ObjectionDocumentTypes
import com.tamin.taminhamrah.feature.workshops.ui.model.WorkshopAttachment
import com.tamin.taminhamrah.feature.workshops.ui.workshopRecentlyAddedMembers.PickedOption
import com.tamin.taminhamrah.feature.workshops.ui.workshopRecentlyAddedMembers.REGISTRATION_FORM_STEPS
import com.tamin.taminhamrah.feature.workshops.ui.workshopRecentlyAddedMembers.RegistrationFormState
import kotlinx.collections.immutable.persistentListOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import taminx.core.core_ui.Res
import taminx.core.core_ui.ws_form_err_agree
import taminx.core.core_ui.ws_form_err_docs

/**
 * What stops نام‌نویسی غیرحضوری from moving on, step by step.
 *
 * Each step reports only its own rules, and only once بعدی has been pressed. The national code
 * is the interesting one: ten digits is not the same as valid, so a filled field can still be
 * refused, and a half-typed one must not be.
 */
class RegistrationFormRulesTest {


    private val attachment = WorkshopAttachment(
        guid = "a-guid",
        type = ObjectionDocumentTypes.first(),
        size = "۱۲",
    )

    @Test
    fun `step one needs all four identity fields`() {
        val partial = RegistrationFormState(step = 1, firstName = "احمد", lastName = "احمدی")
        assertFalse(partial.isStepComplete)

        val complete = partial.copy(nationalId = "1234567891", birthDate = "13700101")
        assertTrue(complete.isStepComplete)
    }

    @Test
    fun `step one refuses a national code that fails its check digit`() {
        val badCode = RegistrationFormState(
            step = 1,
            firstName = "احمد",
            lastName = "احمدی",
            // Ten digits, so the field is "filled" — but the check digit does not add up.
            nationalId = "0024567894",
            birthDate = "13700101",
            hasTriedNext = true,
        )

        assertTrue(badCode.isStepComplete)
        assertNotNull(badCode.error)
    }

    @Test
    fun `step one accepts a national code that adds up`() {
        val goodCode = RegistrationFormState(
            step = 1,
            firstName = "احمد",
            lastName = "احمدی",
            nationalId = "1234567891",
            birthDate = "13700101",
            hasTriedNext = true,
        )

        assertNull(goodCode.error)
    }

    @Test
    fun `step two needs both cities, a job and a start date`() {
        val city = PickedOption(code = "1", label = "تهران")
        val partial = RegistrationFormState(step = 2, birthCity = city, issueCity = city)
        assertFalse(partial.isStepComplete)

        val complete = partial.copy(
            job = PickedOption(code = "7", label = "برنامه‌نویس"),
            startDate = "14050101",
        )
        assertTrue(complete.isStepComplete)
    }

    @Test
    fun `the last step asks for documents, then for the tick`() {
        val bare = RegistrationFormState(step = REGISTRATION_FORM_STEPS, hasTriedNext = true)
        assertEquals(Res.string.ws_form_err_docs, bare.error)
        assertTrue(bare.isDocumentsError)

        val withDocuments = bare.copy(attachments = persistentListOf(attachment))
        assertEquals(Res.string.ws_form_err_agree, withDocuments.error)
        assertFalse(withDocuments.isDocumentsError)

        val ready = withDocuments.copy(isConfirmed = true)
        assertNull(ready.error)
    }

    @Test
    fun `a registration says nothing until next is pressed`() {
        val untouched = RegistrationFormState(step = REGISTRATION_FORM_STEPS)

        assertNull(untouched.error)
        assertFalse(untouched.isDocumentsError)
    }

    @Test
    fun `the last step is the last step`() {
        assertFalse(RegistrationFormState(step = 1).isLastStep)
        assertTrue(RegistrationFormState(step = REGISTRATION_FORM_STEPS).isLastStep)
    }

    @Test
    fun `a name with a missing half does not keep a stray space`() {
        assertEquals("احمد", RegistrationFormState(firstName = "احمد").fullName)
        assertEquals("احمدی", RegistrationFormState(lastName = "احمدی").fullName)
    }
}
