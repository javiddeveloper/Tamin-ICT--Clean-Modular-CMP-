package com.tamin.taminhamrah.feature.workshops.ui.completeEmployerInfo

import com.tamin.taminhamrah.feature.workshops.ui.completeEmployerInfo.contract.CompleteEmployerInfoUiState
import com.tamin.taminhamrah.model.employerInfo.COMPANY_TYPES
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import taminx.core.core_ui.Res
import taminx.core.core_ui.employer_info_err_ceo_birth
import taminx.core.core_ui.employer_info_err_ceo_nid
import taminx.core.core_ui.employer_info_err_company_type
import taminx.core.core_ui.employer_info_err_email
import taminx.core.core_ui.employer_info_err_legal_nid
import taminx.core.core_ui.employer_info_err_mobile

/**
 * The legal form is only reachable through a حقوقی workshop, which the test account does not have,
 * so its six per-field errors cannot be checked on a device. They are pure functions of the state,
 * so they are checked here instead: each field complains only about itself, only once it holds
 * something wrong, and the submit button follows the same rules the messages do.
 */
class LegalFormFieldErrorsTest {

    private val valid = CompleteEmployerInfoUiState(
        legalNationalId = "10101234567",
        selectedCompanyType = COMPANY_TYPES.first(),
        ceoNationalId = "0012345678",
        ceoBirthDateMillis = 1_000_000L,
        ceoBirthDatePersian = "1365/12/03",
        legalMobile = "09153214478",
        legalEmail = "h.tavakoli@gmail.com",
    )

    @Test
    fun anUntouchedFormComplainsAboutNothing() {
        val empty = CompleteEmployerInfoUiState()

        assertNull(empty.legalNationalIdError)
        assertNull(empty.companyTypeError)
        assertNull(empty.ceoNationalIdError)
        assertNull(empty.ceoBirthError)
        assertNull(empty.legalMobileError)
        assertNull(empty.legalEmailError)
    }

    @Test
    fun aCompletedFormComplainsAboutNothingAndMaySubmit() {
        assertNull(valid.legalNationalIdError)
        assertNull(valid.companyTypeError)
        assertNull(valid.ceoNationalIdError)
        assertNull(valid.ceoBirthError)
        assertNull(valid.legalMobileError)
        assertNull(valid.legalEmailError)
        assertTrue(valid.canSubmitLegal)
    }

    @Test
    fun aHalfTypedNationalIdComplainsAndOnlyAboutItself() {
        val state = valid.copy(legalNationalId = "10101")

        assertEquals(Res.string.employer_info_err_legal_nid, state.legalNationalIdError)
        assertNull(state.ceoNationalIdError)
        assertNull(state.legalMobileError)
        assertFalse(state.canSubmitLegal)
    }

    @Test
    fun aHalfTypedManagerCodeComplainsAndOnlyAboutItself() {
        val state = valid.copy(ceoNationalId = "001")

        assertEquals(Res.string.employer_info_err_ceo_nid, state.ceoNationalIdError)
        assertNull(state.legalNationalIdError)
        assertFalse(state.canSubmitLegal)
    }

    @Test
    fun anUnchosenCompanyTypeComplainsOnceTheFormHasBeenTouched() {
        val untouched = CompleteEmployerInfoUiState()
        assertNull(untouched.companyTypeError)

        val touched = valid.copy(selectedCompanyType = null)
        assertEquals(Res.string.employer_info_err_company_type, touched.companyTypeError)
        assertFalse(touched.canSubmitLegal)
    }

    @Test
    fun anUnchosenBirthDateComplainsOnceTheFormHasBeenTouched() {
        val state = valid.copy(ceoBirthDateMillis = null, ceoBirthDatePersian = "")

        assertEquals(Res.string.employer_info_err_ceo_birth, state.ceoBirthError)
        assertFalse(state.canSubmitLegal)
    }

    @Test
    fun aMobileIsRejectedUnlessItIsElevenDigitsFromZeroNine() {
        assertEquals(Res.string.employer_info_err_mobile, valid.copy(legalMobile = "0912").legalMobileError)
        assertEquals(Res.string.employer_info_err_mobile, valid.copy(legalMobile = "08153214478").legalMobileError)
        assertNull(valid.copy(legalMobile = "09153214478").legalMobileError)
    }

    /** Persian digits reach here whenever someone types on a Persian keypad. */
    @Test
    fun aMobileTypedInPersianDigitsIsAccepted() {
        val state = valid.copy(legalMobile = "۰۹۱۵۳۲۱۴۴۷۸")

        assertNull(state.legalMobileError)
        assertTrue(state.canSubmitLegal)
    }

    @Test
    fun anAddressWithoutAHostOrDotIsRejected() {
        assertNotNull(valid.copy(legalEmail = "h.tavakoli").legalEmailError)
        assertEquals(Res.string.employer_info_err_email, valid.copy(legalEmail = "a@b").legalEmailError)
        assertNull(valid.copy(legalEmail = "a@b.ir").legalEmailError)
    }
}
