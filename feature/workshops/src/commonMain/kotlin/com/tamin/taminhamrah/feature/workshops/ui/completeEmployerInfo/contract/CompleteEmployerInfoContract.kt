package com.tamin.taminhamrah.feature.workshops.ui.completeEmployerInfo.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.model.common.CityPR
import com.tamin.taminhamrah.model.common.ProvincePR
import com.tamin.taminhamrah.model.contracts.BranchPR
import com.tamin.taminhamrah.model.employerInfo.CompanyTypePR
import com.tamin.taminhamrah.model.employerInfo.WorkshopItemPR
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf
import com.tamin.taminhamrah.ui.digitsOnly
import org.jetbrains.compose.resources.StringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.employer_info_err_ceo_birth
import taminx.core.core_ui.employer_info_err_ceo_nid
import taminx.core.core_ui.employer_info_err_company_type
import taminx.core.core_ui.employer_info_err_email
import taminx.core.core_ui.employer_info_err_legal_nid
import taminx.core.core_ui.employer_info_err_mobile
import taminx.core.core_ui.employer_info_err_otp_code
import taminx.core.core_ui.employer_info_err_province_city_branch
import taminx.core.core_ui.employer_info_err_ws_code

enum class CompleteEmployerInfoScreenState {
    LIST,
    LEGAL_FORM,
}

enum class CompleteEmployerInfoTab {
    LEGAL,
    REAL,
}

enum class VerifyPath {
    LEGAL,
    REAL,
}

enum class ActiveBottomSheet {
    COMPANY_TYPE,
    PROVINCE,
    CITY,
    BRANCH,
}

/**
 * The one body of the page that is on screen at a time.
 *
 * [depth] is how deep into the flow the step sits, and is what decides which way the page pushes:
 * the two tabs are siblings, the form is one level in from them, and the validation step one
 * further. Going deeper pushes forward, coming back pushes back.
 */
enum class EmployerInfoStep(val depth: Int) {
    LEGAL_LIST(depth = 0),
    REAL_FORM(depth = 1),
    LEGAL_FORM(depth = 2),
    VERIFY(depth = 3),
}

enum class CompleteEmployerInfoDialog {
    SUCCESS_LEGAL,
    SUCCESS_REAL,
    TIMER_EXPIRED,
}

@Immutable
data class CompleteEmployerInfoUiState(
    val isLoading: Boolean = false,
    /** The ticket and submit calls only. Kept apart from [isLoading]: the initial load ends in a
     *  database flow that never completes, so one shared flag left the button spinning for good. */
    val isSubmitting: Boolean = false,
    val screen: CompleteEmployerInfoScreenState = CompleteEmployerInfoScreenState.LIST,
    val tab: CompleteEmployerInfoTab = CompleteEmployerInfoTab.LEGAL,
    val isVerifying: Boolean = false,
    val verifyPath: VerifyPath? = null,

    // User Profile for Hero Card & Real Path
    val userFullName: String = "",
    val userNationalCode: String = "",
    val userMobile: String = "",
    val userEmail: String = "",

    // Workshop List (Tab: Legal)
    val workshops: ImmutableList<WorkshopItemPR> = persistentListOf(),
    val expandedWorkshopIds: ImmutableSet<String> = persistentSetOf(),
    val isWorkshopsLoading: Boolean = false,
    val selectedWorkshop: WorkshopItemPR? = null,

    // Legal Form
    val legalNationalId: String = "",
    val isLegalWorkshopInquiring: Boolean = false,
    val legalWorkshopName: String? = null,
    val selectedCompanyType: CompanyTypePR? = null,
    val ceoNationalId: String = "",
    val ceoBirthDateMillis: Long? = null,
    val ceoBirthDatePersian: String = "",
    val isCeoInquiring: Boolean = false,
    val ceoFullName: String? = null,
    val telephone: String = "",
    val legalMobile: String = "",
    val legalEmail: String = "",
    val hasAttemptedLegalSubmit: Boolean = false,
    val legalValidationError: StringResource? = null,

    // Real Form (Tab: Real)
    val realWorkshopCode: String = "",
    val provinces: ImmutableList<ProvincePR> = persistentListOf(),
    val isProvincesLoading: Boolean = false,
    val selectedProvince: ProvincePR? = null,
    val cities: ImmutableList<CityPR> = persistentListOf(),
    val isCitiesLoading: Boolean = false,
    val selectedCity: CityPR? = null,
    val branches: ImmutableList<BranchPR> = persistentListOf(),
    val isBranchesLoading: Boolean = false,
    val selectedBranch: BranchPR? = null,
    val hasAttemptedRealSubmit: Boolean = false,
    val realValidationError: StringResource? = null,

    // Verification / OTP
    val otpCode: String = "",
    val hasAttemptedOtpSubmit: Boolean = false,
    val otpValidationError: StringResource? = null,

    // Bottom Sheet & Dialog
    val activeBottomSheet: ActiveBottomSheet? = null,
    val dialogState: CompleteEmployerInfoDialog? = null,
    val generalError: String? = null,
) {
    /**
     * The first unmet requirement of the legal form, in the order the design checks them, or null
     * when it is ready to send. Derived rather than stored so the button's enabled state and the
     * message under it can never disagree.
     */
    val legalBlockingError: StringResource?
        get() = when {
            legalNationalId.digitsOnly().length != LEGAL_NATIONAL_ID_LENGTH ->
                Res.string.employer_info_err_legal_nid
            selectedCompanyType == null -> Res.string.employer_info_err_company_type
            ceoNationalId.digitsOnly().length != CEO_NATIONAL_ID_LENGTH ->
                Res.string.employer_info_err_ceo_nid
            ceoBirthDateMillis == null -> Res.string.employer_info_err_ceo_birth
            !MOBILE_PATTERN.matches(legalMobile.digitsOnly()) -> Res.string.employer_info_err_mobile
            !EMAIL_PATTERN.matches(legalEmail.trim()) -> Res.string.employer_info_err_email
            else -> null
        }

    val realBlockingError: StringResource?
        get() = when {
            realWorkshopCode.digitsOnly().length != WORKSHOP_CODE_LENGTH ->
                Res.string.employer_info_err_ws_code
            selectedProvince == null || selectedCity == null || selectedBranch == null ->
                Res.string.employer_info_err_province_city_branch
            else -> null
        }

    val otpBlockingError: StringResource?
        get() = if (otpCode.digitsOnly().length < OTP_LENGTH) Res.string.employer_info_err_otp_code else null

    val canSubmitLegal: Boolean get() = legalBlockingError == null
    val canSubmitReal: Boolean get() = realBlockingError == null
    val canSubmitOtp: Boolean get() = otpBlockingError == null

    /** True once anything has been typed or chosen, so a disabled button can explain itself. */
    val hasTouchedLegalForm: Boolean
        get() = legalNationalId.isNotBlank() || selectedCompanyType != null ||
            ceoNationalId.isNotBlank() || ceoBirthDateMillis != null ||
            legalMobile.isNotBlank() || legalEmail.isNotBlank()

    val hasTouchedRealForm: Boolean
        get() = realWorkshopCode.isNotBlank() || selectedProvince != null ||
            selectedCity != null || selectedBranch != null

    val step: EmployerInfoStep
        get() = when {
            isVerifying -> EmployerInfoStep.VERIFY
            screen == CompleteEmployerInfoScreenState.LEGAL_FORM -> EmployerInfoStep.LEGAL_FORM
            tab == CompleteEmployerInfoTab.REAL -> EmployerInfoStep.REAL_FORM
            else -> EmployerInfoStep.LEGAL_LIST
        }
}

sealed interface CompleteEmployerInfoPartialState {
    data class Loading(val isLoading: Boolean) : CompleteEmployerInfoPartialState
    data class Submitting(val isSubmitting: Boolean) : CompleteEmployerInfoPartialState
    data class UserInfoLoaded(
        val fullName: String,
        val nationalCode: String,
        val mobile: String,
        val email: String,
    ) : CompleteEmployerInfoPartialState
    data class WorkshopsLoaded(val workshops: ImmutableList<WorkshopItemPR>) : CompleteEmployerInfoPartialState
    data class WorkshopExpandedToggled(val workshopId: String) : CompleteEmployerInfoPartialState
    data class OpenLegalForm(val workshop: WorkshopItemPR) : CompleteEmployerInfoPartialState
    data class SwitchTab(val tab: CompleteEmployerInfoTab) : CompleteEmployerInfoPartialState

    // Legal form partials
    data class LegalNationalIdChanged(val nid: String) : CompleteEmployerInfoPartialState
    data class LegalWorkshopInquiryLoading(val isLoading: Boolean) : CompleteEmployerInfoPartialState
    data class LegalWorkshopInquiryResult(val name: String?, val isError: Boolean) : CompleteEmployerInfoPartialState
    data class CompanyTypeSelected(val companyType: CompanyTypePR) : CompleteEmployerInfoPartialState
    data class CeoNationalIdChanged(val nid: String) : CompleteEmployerInfoPartialState
    data class CeoBirthDateSelected(val millis: Long, val persianDate: String) : CompleteEmployerInfoPartialState
    data class CeoInquiryLoading(val isLoading: Boolean) : CompleteEmployerInfoPartialState
    data class CeoInquiryResult(val fullName: String?, val isError: Boolean) : CompleteEmployerInfoPartialState
    data class TelephoneChanged(val tel: String) : CompleteEmployerInfoPartialState
    data class LegalMobileChanged(val mobile: String) : CompleteEmployerInfoPartialState
    data class LegalEmailChanged(val email: String) : CompleteEmployerInfoPartialState
    data class LegalValidationFailed(val error: StringResource) : CompleteEmployerInfoPartialState

    // Real form partials
    data class RealWorkshopCodeChanged(val code: String) : CompleteEmployerInfoPartialState
    data class ProvincesLoaded(val provinces: ImmutableList<ProvincePR>) : CompleteEmployerInfoPartialState
    data class ProvinceSelected(val province: ProvincePR) : CompleteEmployerInfoPartialState
    data class CitiesLoading(val isLoading: Boolean) : CompleteEmployerInfoPartialState
    data class CitiesLoaded(val cities: ImmutableList<CityPR>) : CompleteEmployerInfoPartialState
    data class CitySelected(val city: CityPR) : CompleteEmployerInfoPartialState
    data class BranchesLoading(val isLoading: Boolean) : CompleteEmployerInfoPartialState
    data class BranchesLoaded(val branches: ImmutableList<BranchPR>) : CompleteEmployerInfoPartialState
    data class BranchSelected(val branch: BranchPR) : CompleteEmployerInfoPartialState
    data class RealValidationFailed(val error: StringResource) : CompleteEmployerInfoPartialState

    // Sheets & Dialogs
    data class OpenSheet(val sheet: ActiveBottomSheet) : CompleteEmployerInfoPartialState
    data object DismissSheet : CompleteEmployerInfoPartialState

    // Verification
    data class StartVerification(val path: VerifyPath) : CompleteEmployerInfoPartialState
    data class OtpCodeChanged(val code: String) : CompleteEmployerInfoPartialState
    data class OtpValidationFailed(val error: StringResource) : CompleteEmployerInfoPartialState
    data object BackFromVerification : CompleteEmployerInfoPartialState
    data object BackToList : CompleteEmployerInfoPartialState

    data class ShowDialog(val dialog: CompleteEmployerInfoDialog) : CompleteEmployerInfoPartialState
    data object DismissDialog : CompleteEmployerInfoPartialState
    data class Error(val message: String) : CompleteEmployerInfoPartialState
    data object ErrorDismissed : CompleteEmployerInfoPartialState
}

sealed interface CompleteEmployerInfoIntent {
    data object LoadInitialData : CompleteEmployerInfoIntent
    data class SelectTab(val tab: CompleteEmployerInfoTab) : CompleteEmployerInfoIntent
    data class ToggleWorkshopExpanded(val workshopId: String) : CompleteEmployerInfoIntent
    data class SelectWorkshopForLegalForm(val workshop: WorkshopItemPR) : CompleteEmployerInfoIntent

    // Legal Form Intents
    data class ChangeLegalNationalId(val nid: String) : CompleteEmployerInfoIntent
    data class SelectCompanyType(val type: CompanyTypePR) : CompleteEmployerInfoIntent
    data class ChangeCeoNationalId(val nid: String) : CompleteEmployerInfoIntent
    data class SelectCeoBirthDate(val millis: Long, val persianDate: String) : CompleteEmployerInfoIntent
    data class ChangeTelephone(val telephone: String) : CompleteEmployerInfoIntent
    data class ChangeLegalMobile(val mobile: String) : CompleteEmployerInfoIntent
    data class ChangeLegalEmail(val email: String) : CompleteEmployerInfoIntent
    data object SubmitLegalForm : CompleteEmployerInfoIntent

    // Real Form Intents
    data class ChangeRealWorkshopCode(val code: String) : CompleteEmployerInfoIntent
    data class SelectProvince(val province: ProvincePR) : CompleteEmployerInfoIntent
    data class SelectCity(val city: CityPR) : CompleteEmployerInfoIntent
    data class SelectBranch(val branch: BranchPR) : CompleteEmployerInfoIntent
    data object SubmitRealForm : CompleteEmployerInfoIntent

    // Sheets
    data class OpenBottomSheet(val sheet: ActiveBottomSheet) : CompleteEmployerInfoIntent
    data object CloseBottomSheet : CompleteEmployerInfoIntent

    // Verification Intents
    data class ChangeOtpCode(val code: String) : CompleteEmployerInfoIntent
    data object SubmitOtpVerification : CompleteEmployerInfoIntent
    data object BackFromOtp : CompleteEmployerInfoIntent
    data object BackToWorkshopList : CompleteEmployerInfoIntent
    data object OnTimerExpired : CompleteEmployerInfoIntent
    data object CloseDialog : CompleteEmployerInfoIntent
    data object DismissGeneralError : CompleteEmployerInfoIntent
}

sealed interface CompleteEmployerInfoEvent {
    data object NavigateBack : CompleteEmployerInfoEvent
    data class ShowToast(val message: String) : CompleteEmployerInfoEvent
}

private const val LEGAL_NATIONAL_ID_LENGTH = 11
private const val CEO_NATIONAL_ID_LENGTH = 10
private const val WORKSHOP_CODE_LENGTH = 10
private const val OTP_LENGTH = 5

private val MOBILE_PATTERN = Regex("""^09\d{9}$""")
private val EMAIL_PATTERN = Regex("""^\S+@\S+\.\S+$""")
