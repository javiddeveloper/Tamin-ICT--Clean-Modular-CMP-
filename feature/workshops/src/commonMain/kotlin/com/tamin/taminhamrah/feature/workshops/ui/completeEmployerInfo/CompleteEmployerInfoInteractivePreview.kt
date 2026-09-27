package com.tamin.taminhamrah.feature.workshops.ui.completeEmployerInfo

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.tamin.taminhamrah.feature.workshops.ui.completeEmployerInfo.contract.CompleteEmployerInfoDialog
import com.tamin.taminhamrah.feature.workshops.ui.completeEmployerInfo.contract.CompleteEmployerInfoIntent
import com.tamin.taminhamrah.feature.workshops.ui.completeEmployerInfo.contract.CompleteEmployerInfoScreenState
import com.tamin.taminhamrah.feature.workshops.ui.completeEmployerInfo.contract.CompleteEmployerInfoUiState
import com.tamin.taminhamrah.feature.workshops.ui.completeEmployerInfo.contract.VerifyPath
import com.tamin.taminhamrah.model.common.CityPR
import com.tamin.taminhamrah.model.common.ProvincePR
import com.tamin.taminhamrah.model.contracts.BranchPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.digitsOnly
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableSet

/**
 * The whole flow, walkable in the interactive preview: both tabs, a card's details, the
 * complete-information button into the legal form, every picker, the validation step, and both
 * endings.
 *
 * It answers intents from a table of canned data instead of the services, so it is a way to
 * *drive* the screen rather than a second implementation of it: no validation rule, no request
 * shape and no copy lives here, and everything drawn is the real composable reading the real
 * [CompleteEmployerInfoUiState].
 */
@PreviewRtlTheme
@Composable
private fun CompleteEmployerInfoInteractivePreview() {
    PreviewRtlThemeContent {
        var state by remember {
            mutableStateOf(
                CompleteEmployerInfoUiState(
                    userFullName = "حسین توکلی کرمانی",
                    userNationalCode = "۴۴۷۹۸۹۰۸۸۲",
                    userMobile = "۰۹۱۵۳۲۱۴۴۷۸",
                    userEmail = "h.tavakoli@gmail.com",
                    workshops = previewWorkshops(),
                    provinces = PreviewProvinces,
                )
            )
        }

        CompleteEmployerInfoScreen(
            state = state,
            onIntent = { state = state.answer(it) },
            onBack = {},
        )
    }
}

private val PreviewProvinces = persistentListOf(
    ProvincePR(provinceCode = "09", provinceName = "خراسان رضوی"),
    ProvincePR(provinceCode = "07", provinceName = "تهران"),
)

private val PreviewCities = persistentListOf(
    CityPR(cityCode = "0901", cityName = "مشهد"),
    CityPR(cityCode = "0902", cityName = "نیشابور"),
)

private val PreviewBranches = persistentListOf(
    BranchPR(code = "۱۲۰۲", name = "شعبهٔ ۲ مشهد"),
    BranchPR(code = "۱۲۰۵", name = "شعبهٔ ۵ مشهد"),
)

private const val LEGAL_NATIONAL_ID_LENGTH = 11
private const val CEO_NATIONAL_ID_LENGTH = 10

/** Canned answers, in the shape the services would have replied with. */
@Suppress("CyclomaticComplexMethod")
private fun CompleteEmployerInfoUiState.answer(
    intent: CompleteEmployerInfoIntent,
): CompleteEmployerInfoUiState = when (intent) {
    is CompleteEmployerInfoIntent.SelectTab -> copy(tab = intent.tab)

    is CompleteEmployerInfoIntent.ToggleWorkshopExpanded -> copy(
        expandedWorkshopIds = if (intent.workshopId in expandedWorkshopIds) {
            (expandedWorkshopIds - intent.workshopId).toImmutableSet()
        } else {
            (expandedWorkshopIds + intent.workshopId).toImmutableSet()
        },
    )

    is CompleteEmployerInfoIntent.SelectWorkshopForLegalForm -> copy(
        screen = CompleteEmployerInfoScreenState.LEGAL_FORM,
        selectedWorkshop = intent.workshop,
    )

    is CompleteEmployerInfoIntent.ChangeLegalNationalId -> copy(
        legalNationalId = intent.nid,
        legalWorkshopName = "شرکت صنایع دما بخار مشهد (سهامی خاص)"
            .takeIf { intent.nid.digitsOnly().length == LEGAL_NATIONAL_ID_LENGTH },
    )

    is CompleteEmployerInfoIntent.SelectCompanyType ->
        copy(selectedCompanyType = intent.type, activeBottomSheet = null)

    is CompleteEmployerInfoIntent.ChangeCeoNationalId ->
        copy(ceoNationalId = intent.nid).withCeoName()

    is CompleteEmployerInfoIntent.SelectCeoBirthDate -> copy(
        ceoBirthDateMillis = intent.millis,
        ceoBirthDatePersian = intent.persianDate,
    ).withCeoName()

    is CompleteEmployerInfoIntent.ChangeTelephone -> copy(telephone = intent.telephone)
    is CompleteEmployerInfoIntent.ChangeLegalMobile -> copy(legalMobile = intent.mobile)
    is CompleteEmployerInfoIntent.ChangeLegalEmail -> copy(legalEmail = intent.email)
    is CompleteEmployerInfoIntent.ChangeRealWorkshopCode -> copy(realWorkshopCode = intent.code)

    is CompleteEmployerInfoIntent.OpenBottomSheet -> copy(activeBottomSheet = intent.sheet)
    is CompleteEmployerInfoIntent.CloseBottomSheet -> copy(activeBottomSheet = null)

    is CompleteEmployerInfoIntent.SelectProvince -> copy(
        selectedProvince = intent.province,
        selectedCity = null,
        selectedBranch = null,
        cities = PreviewCities,
        branches = persistentListOf(),
        activeBottomSheet = null,
    )
    CompleteEmployerInfoIntent.ProvincePickerLoadMore -> this

    is CompleteEmployerInfoIntent.SelectCity -> copy(
        selectedCity = intent.city,
        selectedBranch = null,
        branches = PreviewBranches,
        activeBottomSheet = null,
    )
    CompleteEmployerInfoIntent.CityPickerLoadMore -> this

    is CompleteEmployerInfoIntent.SelectBranch ->
        copy(selectedBranch = intent.branch, activeBottomSheet = null)

    is CompleteEmployerInfoIntent.SubmitLegalForm ->
        copy(isVerifying = true, verifyPath = VerifyPath.LEGAL, otpCode = "")

    is CompleteEmployerInfoIntent.SubmitRealForm ->
        copy(isVerifying = true, verifyPath = VerifyPath.REAL, otpCode = "")

    is CompleteEmployerInfoIntent.ChangeOtpCode -> copy(otpCode = intent.code)

    is CompleteEmployerInfoIntent.SubmitOtpVerification -> copy(
        isVerifying = false,
        dialogState = if (verifyPath == VerifyPath.LEGAL) {
            CompleteEmployerInfoDialog.SUCCESS_LEGAL
        } else {
            CompleteEmployerInfoDialog.SUCCESS_REAL
        },
    )

    is CompleteEmployerInfoIntent.OnTimerExpired ->
        copy(dialogState = CompleteEmployerInfoDialog.TIMER_EXPIRED, isVerifying = false)

    is CompleteEmployerInfoIntent.BackFromOtp -> copy(isVerifying = false, otpCode = "")

    is CompleteEmployerInfoIntent.BackToWorkshopList -> copy(
        screen = CompleteEmployerInfoScreenState.LIST,
        isVerifying = false,
        selectedWorkshop = null,
    )

    is CompleteEmployerInfoIntent.CloseDialog -> copy(
        dialogState = null,
        screen = if (dialogState == CompleteEmployerInfoDialog.SUCCESS_LEGAL) {
            CompleteEmployerInfoScreenState.LIST
        } else {
            screen
        },
    )

    is CompleteEmployerInfoIntent.DismissGeneralError -> copy(generalError = null)
    is CompleteEmployerInfoIntent.LoadInitialData -> this
}

/** The manager's name only resolves once both halves of the lookup are present, as on the wire. */
private fun CompleteEmployerInfoUiState.withCeoName(): CompleteEmployerInfoUiState = copy(
    ceoFullName = "مریم توکلی".takeIf {
        ceoNationalId.digitsOnly().length == CEO_NATIONAL_ID_LENGTH && ceoBirthDateMillis != null
    },
)
