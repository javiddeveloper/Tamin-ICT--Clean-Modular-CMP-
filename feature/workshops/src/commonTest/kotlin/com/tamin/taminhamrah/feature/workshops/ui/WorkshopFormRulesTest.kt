package com.tamin.taminhamrah.feature.workshops.ui

import com.tamin.taminhamrah.feature.workshops.ui.managementDebit.ArticleSixteenFormState
import com.tamin.taminhamrah.feature.workshops.ui.model.ObjectionDocumentTypes
import com.tamin.taminhamrah.feature.workshops.ui.model.WorkshopAttachment
import com.tamin.taminhamrah.feature.workshops.ui.objectionableDebit.ObjectionFormState
import com.tamin.taminhamrah.feature.workshops.ui.workshopRecentlyAddedMembers.PickedOption
import com.tamin.taminhamrah.feature.workshops.ui.workshopRecentlyAddedMembers.REGISTRATION_FORM_STEPS
import com.tamin.taminhamrah.feature.workshops.ui.workshopRecentlyAddedMembers.RegistrationFormState
import com.tamin.taminhamrah.model.workshop.ArticleSixteenDebtPR
import com.tamin.taminhamrah.model.workshop.WorkShopDebtPR
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
 * What stops each کارگاه form from being submitted, and where that gets said.
 *
 * Two things are being pinned down. First, that nothing complains before submit is attempted —
 * telling someone their form is incomplete while they are still filling it in is noise. Second,
 * that exactly one place claims each rule: `isDocumentsError` decides whether the upload panel
 * draws the border or the error line prints the text, and if the two ever disagree the user sees
 * the same complaint twice or not at all.
 */
class WorkshopFormRulesTest {

    private val attachment = WorkshopAttachment(
        guid = "a-guid",
        type = ObjectionDocumentTypes.first(),
        size = "۱۲",
    )

    // ------------------------------------------------------------------ ثبت اعتراض

    @Test
    fun `an objection says nothing until submit is attempted`() {
        val untouched = ObjectionFormState(debt = WorkShopDebtPR())

        assertNull(untouched.error)
        assertFalse(untouched.isDocumentsError)
    }

    @Test
    fun `an objection with no documents asks for documents`() {
        val tried = ObjectionFormState(debt = WorkShopDebtPR(), hasTriedSubmit = true)

        assertEquals(Res.string.ws_form_err_docs, tried.error)
        assertTrue(tried.isDocumentsError)
    }

    @Test
    fun `an objection with documents but no tick asks for the tick`() {
        val tried = ObjectionFormState(
            debt = WorkShopDebtPR(),
            attachments = persistentListOf(attachment),
            hasTriedSubmit = true,
        )

        assertEquals(Res.string.ws_form_err_agree, tried.error)
        // The panel must not also draw a border: this one is about the checkbox.
        assertFalse(tried.isDocumentsError)
    }

    @Test
    fun `a complete objection has nothing to say`() {
        val ready = ObjectionFormState(
            debt = WorkShopDebtPR(),
            attachments = persistentListOf(attachment),
            isConfirmed = true,
            hasTriedSubmit = true,
        )

        assertNull(ready.error)
        assertFalse(ready.isDocumentsError)
    }

    @Test
    fun `an objection is busy while either half of the submit is in flight`() {
        val debt = WorkShopDebtPR()

        assertFalse(ObjectionFormState(debt = debt).isBusy)
        assertTrue(ObjectionFormState(debt = debt, isUploading = true).isBusy)
        assertTrue(ObjectionFormState(debt = debt, isSubmitting = true).isBusy)
    }

    // ---------------------------------------------------------------------- ماده ۱۶

    @Test
    fun `a ماده ۱۶ request says nothing while step one is still up`() {
        val onStepOne = ArticleSixteenFormState(
            debt = ArticleSixteenDebtPR(),
            step = 1,
            hasTriedSubmit = true,
        )

        // Step one has nothing to get wrong — it is a review of the debt.
        assertNull(onStepOne.error)
        assertFalse(onStepOne.isDocumentsError)
    }

    @Test
    fun `a ماده ۱۶ request on its last step asks for documents first`() {
        val onLastStep = ArticleSixteenFormState(
            debt = ArticleSixteenDebtPR(),
            step = 2,
            hasTriedSubmit = true,
        )

        assertTrue(onLastStep.isLastStep)
        assertEquals(Res.string.ws_form_err_docs, onLastStep.error)
        assertTrue(onLastStep.isDocumentsError)
    }

    @Test
    fun `a ماده ۱۶ request with documents asks for the tick`() {
        val onLastStep = ArticleSixteenFormState(
            debt = ArticleSixteenDebtPR(),
            step = 2,
            attachments = persistentListOf(attachment),
            hasTriedSubmit = true,
        )

        assertEquals(Res.string.ws_form_err_agree, onLastStep.error)
        assertFalse(onLastStep.isDocumentsError)
    }

    // -------------------------------------------------------------------- نام‌نویسی

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
