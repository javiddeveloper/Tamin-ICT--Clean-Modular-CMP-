package com.tamin.taminhamrah.feature.workshops.ui

import com.tamin.taminhamrah.feature.workshops.ui.model.ObjectionDocumentTypes
import com.tamin.taminhamrah.feature.workshops.ui.model.WorkshopAttachment
import com.tamin.taminhamrah.feature.workshops.ui.objectionableDebit.ObjectionFormState
import com.tamin.taminhamrah.model.workshop.WorkShopDebtPR
import kotlinx.collections.immutable.persistentListOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import taminx.core.core_ui.Res
import taminx.core.core_ui.ws_form_err_agree
import taminx.core.core_ui.ws_form_err_docs

/**
 * What stops ثبت اعتراض from being submitted, and where that gets said.
 *
 * Two things are pinned down. That nothing complains before submit is attempted — telling
 * someone their form is incomplete while they are still filling it in is noise. And that
 * exactly one place claims each rule: `isDocumentsError` decides whether the upload panel
 * draws the border or the error line prints the text, and if the two ever disagree the user
 * sees the same complaint twice or not at all.
 */
class ObjectionFormRulesTest {

    private val attachment = WorkshopAttachment(
        guid = "a-guid",
        type = ObjectionDocumentTypes.first(),
        size = "۱۲",
    )

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
}
