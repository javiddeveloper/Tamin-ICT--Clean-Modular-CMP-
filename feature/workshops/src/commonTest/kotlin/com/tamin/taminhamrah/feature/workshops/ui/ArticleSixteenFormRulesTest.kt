package com.tamin.taminhamrah.feature.workshops.ui

import com.tamin.taminhamrah.feature.workshops.ui.managementDebit.ArticleSixteenFormState
import com.tamin.taminhamrah.feature.workshops.ui.model.ObjectionDocumentTypes
import com.tamin.taminhamrah.feature.workshops.ui.model.WorkshopAttachment
import com.tamin.taminhamrah.model.workshop.ArticleSixteenDebtPR
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
 * What stops a ماده ۱۶ request from being submitted.
 *
 * Step one is a review of the debt and has nothing to get wrong, so it stays quiet even after
 * submit is attempted; the rules belong to the last step.
 */
class ArticleSixteenFormRulesTest {


    private val attachment = WorkshopAttachment(
        guid = "a-guid",
        type = ObjectionDocumentTypes.first(),
        size = "۱۲",
    )

    @Test
    fun `an article sixteen request says nothing while step one is still up`() {
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
    fun `an article sixteen request on its last step asks for documents first`() {
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
    fun `an article sixteen request with documents asks for the tick`() {
        val onLastStep = ArticleSixteenFormState(
            debt = ArticleSixteenDebtPR(),
            step = 2,
            attachments = persistentListOf(attachment),
            hasTriedSubmit = true,
        )

        assertEquals(Res.string.ws_form_err_agree, onLastStep.error)
        assertFalse(onLastStep.isDocumentsError)
    }
}
