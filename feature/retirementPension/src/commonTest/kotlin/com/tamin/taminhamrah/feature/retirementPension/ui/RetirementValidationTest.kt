package com.tamin.taminhamrah.feature.retirementPension.ui

import com.tamin.taminhamrah.feature.retirementPension.ui.contract.RetirementDocumentPR
import com.tamin.taminhamrah.feature.retirementPension.ui.contract.RetirementDocumentType
import com.tamin.taminhamrah.feature.retirementPension.ui.contract.RetirementFormError
import com.tamin.taminhamrah.feature.retirementPension.ui.contract.RetirementPensionUiState
import com.tamin.taminhamrah.feature.retirementPension.ui.contract.RetirementStep
import kotlinx.collections.immutable.persistentMapOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * The step rules, and the order they report failures in.
 *
 * Method names are ASCII on purpose: Kotlin names synthetic lambda classes after the enclosing
 * function, and Persian names produce class files the Linux CI runner cannot write.
 */
class RetirementValidationTest {

    private val validIdentity = RetirementPensionUiState(
        step = RetirementStep.Identity,
        phoneNumber = "02188776655",
        address = "تهران، خیابان ولیعصر، کوچهٔ بهار، پلاک ۱۲",
        identityConfirmed = true,
    )

    private val validWorkshop = RetirementPensionUiState(
        step = RetirementStep.Workshop,
        workshopName = "شرکت ارد پارس اسپادانا",
        workshopCode = "1024300719",
        workshopAddress = "تهران، کیلومتر ۸ جادهٔ مخصوص، خیابان دوم",
        workshopConfirmed = true,
    )

    @Test
    fun rulesStepRequiresConsent() {
        val state = RetirementPensionUiState(step = RetirementStep.Rules)
        assertEquals(RetirementFormError.Consent, state.stepError())
        assertNull(state.copy(consentAccepted = true).stepError())
    }

    @Test
    fun authenticationStepRequiresAVerifiedCode() {
        val state = RetirementPensionUiState(step = RetirementStep.Authentication)
        assertEquals(RetirementFormError.Authentication, state.stepError())
        assertNull(state.copy(otpVerified = true).stepError())
    }

    @Test
    fun identityStepReportsTheFirstProblemOnly() {
        assertNull(validIdentity.stepError())
        assertEquals(
            RetirementFormError.PhoneRequired,
            validIdentity.copy(phoneNumber = "").stepError(),
        )
        assertEquals(
            RetirementFormError.PhonePrefix,
            validIdentity.copy(phoneNumber = "2188776655").stepError(),
        )
        assertEquals(
            RetirementFormError.PhoneLength,
            validIdentity.copy(phoneNumber = "0218877").stepError(),
        )
        assertEquals(
            RetirementFormError.AddressRequired,
            validIdentity.copy(address = "   ").stepError(),
        )
        assertEquals(
            RetirementFormError.AddressShort,
            validIdentity.copy(address = "تهران").stepError(),
        )
        assertEquals(
            RetirementFormError.IdentityConfirm,
            validIdentity.copy(identityConfirmed = false).stepError(),
        )
    }

    @Test
    fun phonePrefixIsCheckedBeforeLength() {
        // A short number that also starts wrong must name the prefix, which is the design's order.
        assertEquals(
            RetirementFormError.PhonePrefix,
            validIdentity.copy(phoneNumber = "21").stepError(),
        )
    }

    @Test
    fun workshopStepReportsTheFirstProblemOnly() {
        assertNull(validWorkshop.stepError())
        assertEquals(
            RetirementFormError.WorkshopName,
            validWorkshop.copy(workshopName = " ").stepError(),
        )
        assertEquals(
            RetirementFormError.WorkshopCode,
            validWorkshop.copy(workshopCode = "102430071").stepError(),
        )
        assertEquals(
            RetirementFormError.WorkshopAddress,
            validWorkshop.copy(workshopAddress = "تهران").stepError(),
        )
        assertEquals(
            RetirementFormError.WorkshopConfirm,
            validWorkshop.copy(workshopConfirmed = false).stepError(),
        )
    }

    @Test
    fun workshopCodeMustBeExactlyTenDigits() {
        assertEquals(
            RetirementFormError.WorkshopCode,
            validWorkshop.copy(workshopCode = "10243007190").stepError(),
        )
    }

    @Test
    fun historyStepNeverBlocks() {
        assertNull(RetirementPensionUiState(step = RetirementStep.History).stepError())
    }

    @Test
    fun identityDocumentsStepNeedsBothPages() {
        val state = RetirementPensionUiState(step = RetirementStep.IdentityDocuments)
        assertEquals(RetirementFormError.IdentityDocuments, state.stepError())

        val onlyFirstPage = state.copy(
            documents = persistentMapOf(
                RetirementDocumentType.IdFirstPage to RetirementDocumentPR(guid = "a"),
            ),
        )
        assertEquals(RetirementFormError.IdentityDocuments, onlyFirstPage.stepError())

        val both = state.copy(
            documents = persistentMapOf(
                RetirementDocumentType.IdFirstPage to RetirementDocumentPR(guid = "a"),
                RetirementDocumentType.IdDescriptionPage to RetirementDocumentPR(guid = "b"),
            ),
        )
        assertNull(both.stepError())
    }

    @Test
    fun quitLetterStepIgnoresTheIdentityDocuments() {
        val state = RetirementPensionUiState(
            step = RetirementStep.QuitLetter,
            documents = persistentMapOf(
                RetirementDocumentType.IdFirstPage to RetirementDocumentPR(guid = "a"),
                RetirementDocumentType.IdDescriptionPage to RetirementDocumentPR(guid = "b"),
            ),
        )
        assertEquals(RetirementFormError.QuitLetter, state.stepError())

        val withLetter = state.copy(
            documents = state.documents.put(
                RetirementDocumentType.QuitLetter,
                RetirementDocumentPR(guid = "c"),
            ),
        )
        assertNull(withLetter.stepError())
    }

    @Test
    fun anUploadInFlightDoesNotCountAsSupplied() {
        val state = RetirementPensionUiState(
            step = RetirementStep.QuitLetter,
            documents = persistentMapOf(
                RetirementDocumentType.QuitLetter to RetirementDocumentPR(isUploading = true),
            ),
        )
        assertEquals(RetirementFormError.QuitLetter, state.stepError())
    }

    @Test
    fun finalStepRequiresConfirmation() {
        val state = RetirementPensionUiState(step = RetirementStep.Final)
        assertEquals(RetirementFormError.FinalConfirm, state.stepError())
        assertNull(state.copy(finalConfirmed = true).stepError())
    }

    @Test
    fun documentComplaintsStayOffTheSharedLine() {
        val state = RetirementPensionUiState(
            step = RetirementStep.QuitLetter,
            validationAttempted = true,
            error = RetirementFormError.QuitLetter,
        )
        assertNull(state.visibleFormError)
        assertEquals(true, state.isDocumentMissing(RetirementDocumentType.QuitLetter))
    }

    @Test
    fun nothingIsReportedUntilTheStepActionIsPressed() {
        val state = RetirementPensionUiState(
            step = RetirementStep.Rules,
            error = RetirementFormError.Consent,
        )
        assertNull(state.visibleFormError)
        assertEquals(false, state.isDocumentMissing(RetirementDocumentType.IdFirstPage))
    }
}
