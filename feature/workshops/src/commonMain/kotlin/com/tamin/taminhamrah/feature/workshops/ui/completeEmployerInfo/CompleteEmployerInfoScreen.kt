package com.tamin.taminhamrah.feature.workshops.ui.completeEmployerInfo

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.workshops.ui.completeEmployerInfo.components.EmployerInfoBottomSheet
import com.tamin.taminhamrah.feature.workshops.ui.completeEmployerInfo.components.EmployerInfoDialogs
import com.tamin.taminhamrah.feature.workshops.ui.completeEmployerInfo.components.EmployerInfoHero
import com.tamin.taminhamrah.feature.workshops.ui.completeEmployerInfo.components.EmployerInfoScreenShimmer
import com.tamin.taminhamrah.feature.workshops.ui.completeEmployerInfo.components.EmployerInfoVerifySection
import com.tamin.taminhamrah.feature.workshops.ui.completeEmployerInfo.components.LegalWorkshopFormSection
import com.tamin.taminhamrah.feature.workshops.ui.completeEmployerInfo.components.LegalWorkshopListSection
import com.tamin.taminhamrah.feature.workshops.ui.completeEmployerInfo.components.RealWorkshopFormSection
import com.tamin.taminhamrah.feature.workshops.ui.completeEmployerInfo.contract.ActiveBottomSheet
import com.tamin.taminhamrah.feature.workshops.ui.completeEmployerInfo.contract.CompleteEmployerInfoEvent
import com.tamin.taminhamrah.feature.workshops.ui.completeEmployerInfo.contract.CompleteEmployerInfoIntent
import com.tamin.taminhamrah.feature.workshops.ui.completeEmployerInfo.contract.CompleteEmployerInfoScreenState
import com.tamin.taminhamrah.feature.workshops.ui.completeEmployerInfo.contract.CompleteEmployerInfoTab
import com.tamin.taminhamrah.feature.workshops.ui.completeEmployerInfo.contract.CompleteEmployerInfoUiState
import com.tamin.taminhamrah.model.employerInfo.WorkshopItemPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.toast.LocalToaster
import com.tamin.taminhamrah.ui.components.toast.error
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.Flow
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CompleteEmployerInfoRoute(
    onBack: () -> Unit,
    viewModel: CompleteEmployerInfoViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val toaster = LocalToaster.current

    HandleCompleteEmployerInfoEvents(
        events = viewModel.events,
        onBack = onBack,
        showToast = { toaster.error(it) },
    )

    CompleteEmployerInfoScreen(
        state = state,
        onIntent = viewModel::sendIntent,
        onBack = onBack,
    )
}

@Composable
private fun HandleCompleteEmployerInfoEvents(
    events: Flow<CompleteEmployerInfoEvent>,
    onBack: () -> Unit,
    showToast: (String) -> Unit,
) {
    LaunchedEffect(events) {
        events.collect { event ->
            when (event) {
                is CompleteEmployerInfoEvent.NavigateBack -> onBack()
                is CompleteEmployerInfoEvent.ShowToast -> showToast(event.message)
            }
        }
    }
}

@Composable
fun CompleteEmployerInfoScreen(
    state: CompleteEmployerInfoUiState,
    onIntent: (CompleteEmployerInfoIntent) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val scrollState = rememberScrollState()

    val isInitialLoading = state.isLoading && state.workshops.isEmpty() && state.userFullName.isBlank()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = colors.bgPage,
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            if (isInitialLoading) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .navigationBarsPadding(),
                ) {
                    EmployerInfoScreenShimmer()
                    Spacer(modifier = Modifier.height(Spacing.xxl))
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .navigationBarsPadding(),
                ) {
                    // Header + Hero + Tabs / Workshop Chip
                    EmployerInfoHero(
                        screen = state.screen,
                        tab = state.tab,
                        onSelectTab = { onIntent(CompleteEmployerInfoIntent.SelectTab(it)) },
                        userFullName = state.userFullName,
                        userNationalCode = state.userNationalCode,
                        selectedWorkshop = state.selectedWorkshop,
                        onBack = {
                            if (state.isVerifying) {
                                onIntent(CompleteEmployerInfoIntent.BackFromOtp)
                            } else if (state.screen == CompleteEmployerInfoScreenState.LEGAL_FORM) {
                                onIntent(CompleteEmployerInfoIntent.BackToWorkshopList)
                            } else {
                                onBack()
                            }
                        },
                    )

                    Spacer(modifier = Modifier.height(if (state.screen == CompleteEmployerInfoScreenState.LIST) 0.dp else Spacing.md))

                    // Content Section based on screen and verification state
                    if (state.isVerifying) {
                        val mobile = if (state.verifyPath == com.tamin.taminhamrah.feature.workshops.ui.completeEmployerInfo.contract.VerifyPath.LEGAL) {
                            state.legalMobile.ifBlank { state.userMobile }
                        } else {
                            state.userMobile
                        }

                        EmployerInfoVerifySection(
                            mobile = mobile,
                            otpCode = state.otpCode,
                            onOtpCodeChanged = { onIntent(CompleteEmployerInfoIntent.ChangeOtpCode(it)) },
                            verifyPath = state.verifyPath,
                            errorMessage = if (state.hasAttemptedOtpSubmit) state.otpValidationError else null,
                            isLoading = state.isLoading,
                            onEditInfo = { onIntent(CompleteEmployerInfoIntent.BackFromOtp) },
                            onSubmit = { onIntent(CompleteEmployerInfoIntent.SubmitOtpVerification) },
                            onTimerExpired = { onIntent(CompleteEmployerInfoIntent.OnTimerExpired) },
                        )
                    } else if (state.screen == CompleteEmployerInfoScreenState.LEGAL_FORM) {
                        LegalWorkshopFormSection(
                            legalNationalId = state.legalNationalId,
                            onLegalNationalIdChanged = { onIntent(CompleteEmployerInfoIntent.ChangeLegalNationalId(it)) },
                            isLegalWorkshopInquiring = state.isLegalWorkshopInquiring,
                            legalWorkshopName = state.legalWorkshopName,
                            selectedCompanyType = state.selectedCompanyType,
                            onOpenCompanyTypePicker = {
                                onIntent(CompleteEmployerInfoIntent.OpenBottomSheet(ActiveBottomSheet.COMPANY_TYPE))
                            },
                            ceoNationalId = state.ceoNationalId,
                            onCeoNationalIdChanged = { onIntent(CompleteEmployerInfoIntent.ChangeCeoNationalId(it)) },
                            ceoBirthDatePersian = state.ceoBirthDatePersian,
                            onCeoBirthDateSelected = { millis, str ->
                                onIntent(CompleteEmployerInfoIntent.SelectCeoBirthDate(millis, str))
                            },
                            isCeoInquiring = state.isCeoInquiring,
                            ceoFullName = state.ceoFullName,
                            telephone = state.telephone,
                            onTelephoneChanged = { onIntent(CompleteEmployerInfoIntent.ChangeTelephone(it)) },
                            mobile = state.legalMobile,
                            onMobileChanged = { onIntent(CompleteEmployerInfoIntent.ChangeLegalMobile(it)) },
                            email = state.legalEmail,
                            onEmailChanged = { onIntent(CompleteEmployerInfoIntent.ChangeLegalEmail(it)) },
                            errorMessage = if (state.hasAttemptedLegalSubmit) state.legalValidationError else null,
                            hasAttemptedSubmit = state.hasAttemptedLegalSubmit,
                            isLoading = state.isLoading,
                            onSubmit = { onIntent(CompleteEmployerInfoIntent.SubmitLegalForm) },
                        )
                    } else {
                        // Screen is LIST: either Legal List tab or Real Form tab
                        if (state.tab == CompleteEmployerInfoTab.LEGAL) {
                            LegalWorkshopListSection(
                                workshops = state.workshops,
                                expandedWorkshopIds = state.expandedWorkshopIds,
                                onToggleExpanded = { onIntent(CompleteEmployerInfoIntent.ToggleWorkshopExpanded(it)) },
                                onSelectWorkshop = { onIntent(CompleteEmployerInfoIntent.SelectWorkshopForLegalForm(it)) },
                            )
                        } else {
                            RealWorkshopFormSection(
                                workshopCode = state.realWorkshopCode,
                                onWorkshopCodeChanged = { onIntent(CompleteEmployerInfoIntent.ChangeRealWorkshopCode(it)) },
                                userEmail = state.userEmail,
                                selectedProvince = state.selectedProvince,
                                onOpenProvincePicker = {
                                    onIntent(CompleteEmployerInfoIntent.OpenBottomSheet(ActiveBottomSheet.PROVINCE))
                                },
                                selectedCity = state.selectedCity,
                                onOpenCityPicker = {
                                    onIntent(CompleteEmployerInfoIntent.OpenBottomSheet(ActiveBottomSheet.CITY))
                                },
                                selectedBranch = state.selectedBranch,
                                onOpenBranchPicker = {
                                    onIntent(CompleteEmployerInfoIntent.OpenBottomSheet(ActiveBottomSheet.BRANCH))
                                },
                                errorMessage = if (state.hasAttemptedRealSubmit) state.realValidationError else null,
                                hasAttemptedSubmit = state.hasAttemptedRealSubmit,
                                isLoading = state.isLoading,
                                onSubmit = { onIntent(CompleteEmployerInfoIntent.SubmitRealForm) },
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(Spacing.xxl))
                }
            }

            // Bottom Sheets
            EmployerInfoBottomSheet(
                activeBottomSheet = state.activeBottomSheet,
                onDismiss = { onIntent(CompleteEmployerInfoIntent.CloseBottomSheet) },
                selectedCompanyType = state.selectedCompanyType,
                onSelectCompanyType = { onIntent(CompleteEmployerInfoIntent.SelectCompanyType(it)) },
                provinces = state.provinces,
                isProvincesLoading = state.isProvincesLoading,
                selectedProvince = state.selectedProvince,
                onSelectProvince = { onIntent(CompleteEmployerInfoIntent.SelectProvince(it)) },
                cities = state.cities,
                isCitiesLoading = state.isCitiesLoading,
                selectedCity = state.selectedCity,
                onSelectCity = { onIntent(CompleteEmployerInfoIntent.SelectCity(it)) },
                branches = state.branches,
                isBranchesLoading = state.isBranchesLoading,
                selectedBranch = state.selectedBranch,
                onSelectBranch = { onIntent(CompleteEmployerInfoIntent.SelectBranch(it)) },
            )

            // Dialogs
            EmployerInfoDialogs(
                dialogState = state.dialogState,
                onDismiss = { onIntent(CompleteEmployerInfoIntent.CloseDialog) },
            )
        }
    }
}

@PreviewRtlTheme
@Composable
private fun CompleteEmployerInfoScreenPreview() {
    PreviewRtlThemeContent {
        CompleteEmployerInfoScreen(
            state = CompleteEmployerInfoUiState(
                userFullName = "حسین توکلی کرمانی",
                userNationalCode = "۴۴۷۹۸۹۰۸۸۲",
                workshops = persistentListOf(
                    WorkshopItemPR(
                        id = "w1",
                        name = "شرکت صنایع دما بخار مشهد",
                        code = "۰۰۸۱۶۳۱۸۲۹",
                        branch = "شعبهٔ ۲ مشهد",
                        bcode = "۱۲۰۲",
                        isLegal = true,
                        letDate = "۱۴۰۳/۰۵/۱۹",
                        email = "info@damabokhar.ir",
                        mobile = "۰۹۱۵۳۲۱۴۴۷۸",
                        address = "مشهد، بلوار خیام، نبش خیام ۳۲، پلاک ۱۴",
                    ),
                    WorkshopItemPR(
                        id = "w2",
                        name = "درمانگاه دندانپزشکی دکتر محمدجعفری جبلی",
                        code = "۰۰۱۶۳۱۸۹۴۱",
                        branch = "شعبهٔ ۵ مشهد",
                        bcode = "۱۲۰۵",
                        isLegal = false,
                        letDate = "۱۴۰۲/۱۱/۰۳",
                        email = "clinic.jebeli@gmail.com",
                        mobile = "۰۹۱۵۱۱۰۲۲۳۴",
                        address = "مشهد، خیابان احمدآباد، نبش قائم، ساختمان پزشکان",
                    ),
                ),
            ),
            onIntent = {},
            onBack = {},
        )
    }
}
