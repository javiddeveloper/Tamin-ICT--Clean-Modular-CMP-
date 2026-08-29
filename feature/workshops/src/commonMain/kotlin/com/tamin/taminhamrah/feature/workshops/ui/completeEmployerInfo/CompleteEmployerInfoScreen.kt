package com.tamin.taminhamrah.feature.workshops.ui.completeEmployerInfo

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.tamin.taminhamrah.feature.workshops.ui.completeEmployerInfo.components.EmployerInfoBottomSheet
import com.tamin.taminhamrah.feature.workshops.ui.completeEmployerInfo.components.EmployerInfoDialogs
import com.tamin.taminhamrah.feature.workshops.ui.completeEmployerInfo.components.EmployerInfoHero
import com.tamin.taminhamrah.feature.workshops.ui.completeEmployerInfo.components.EmployerInfoScreenShimmer
import com.tamin.taminhamrah.feature.workshops.ui.completeEmployerInfo.components.EmployerInfoVerifySection
import com.tamin.taminhamrah.feature.workshops.ui.completeEmployerInfo.components.LegalWorkshopFormSection
import com.tamin.taminhamrah.feature.workshops.ui.completeEmployerInfo.components.RealWorkshopFormSection
import com.tamin.taminhamrah.feature.workshops.ui.completeEmployerInfo.components.legalWorkshopListSection
import com.tamin.taminhamrah.feature.workshops.ui.completeEmployerInfo.contract.ActiveBottomSheet
import com.tamin.taminhamrah.feature.workshops.ui.completeEmployerInfo.contract.CompleteEmployerInfoEvent
import com.tamin.taminhamrah.feature.workshops.ui.completeEmployerInfo.contract.CompleteEmployerInfoIntent
import com.tamin.taminhamrah.feature.workshops.ui.completeEmployerInfo.contract.CompleteEmployerInfoScreenState
import com.tamin.taminhamrah.feature.workshops.ui.completeEmployerInfo.contract.CompleteEmployerInfoUiState
import com.tamin.taminhamrah.feature.workshops.ui.completeEmployerInfo.contract.EmployerInfoStep
import com.tamin.taminhamrah.feature.workshops.ui.completeEmployerInfo.contract.VerifyPath
import com.tamin.taminhamrah.mapper.employerInfo.toWorkshopItemPR
import com.tamin.taminhamrah.model.employerInfo.WorkshopItemPR
import com.tamin.taminhamrah.model.workshop.EmployerAgreementDN
import com.tamin.taminhamrah.model.workshop.EmployerWorkshopDN
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.ErrorStateView
import com.tamin.taminhamrah.ui.components.rememberStaggeredEntranceState
import com.tamin.taminhamrah.ui.components.toast.LocalToaster
import com.tamin.taminhamrah.ui.components.toast.error
import com.tamin.taminhamrah.ui.pushBack
import com.tamin.taminhamrah.ui.pushForward
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.stringResource
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
    val listState = rememberLazyListState()

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
                Column(modifier = Modifier.fillMaxSize()) {
                    // Header, hero and the two tabs. Outside the animated body on purpose: a tab
                    // bar that slid away with its own content would not read as a tab bar.
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

                    // The same push the app navigates with, so moving between the tabs, into the
                    // form and on to the validation step all read as one gesture.
                    AnimatedContent(
                        targetState = state.step,
                        transitionSpec = {
                            if (targetState.depth >= initialState.depth) pushForward() else pushBack()
                        },
                        label = "EmployerInfoStep",
                        modifier = Modifier.fillMaxSize(),
                    ) { step ->
                        when (step) {
                            EmployerInfoStep.VERIFY -> EmployerInfoStepBody {
                                val mobile = if (state.verifyPath == VerifyPath.LEGAL) {
                                    state.legalMobile.ifBlank { state.userMobile }
                                } else {
                                    state.userMobile
                                }

                                EmployerInfoVerifySection(
                                    mobile = mobile,
                                    otpCode = state.otpCode,
                                    onOtpCodeChanged = { onIntent(CompleteEmployerInfoIntent.ChangeOtpCode(it)) },
                                    verifyPath = state.verifyPath,
                                    errorMessage = if (state.hasAttemptedOtpSubmit) state.otpValidationError?.let { stringResource(it) } else null,
                                    isSubmitting = state.isSubmitting,
                                    canSubmit = state.canSubmitOtp,
                                    onEditInfo = { onIntent(CompleteEmployerInfoIntent.BackFromOtp) },
                                    onSubmit = { onIntent(CompleteEmployerInfoIntent.SubmitOtpVerification) },
                                    onTimerExpired = { onIntent(CompleteEmployerInfoIntent.OnTimerExpired) },
                                )
                            }

                            EmployerInfoStep.LEGAL_FORM -> EmployerInfoStepBody {
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
                                    // Shown as soon as the form is touched, so a disabled button
                                    // always says what is still missing.
                                    errorMessage = if (state.hasTouchedLegalForm) {
                                        state.legalBlockingError?.let { stringResource(it) }
                                    } else {
                                        null
                                    },
                                    hasAttemptedSubmit = state.hasAttemptedLegalSubmit,
                                    isSubmitting = state.isSubmitting,
                                    canSubmit = state.canSubmitLegal,
                                    onSubmit = { onIntent(CompleteEmployerInfoIntent.SubmitLegalForm) },
                                )
                            }

                            EmployerInfoStep.REAL_FORM -> EmployerInfoStepBody {
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
                                    errorMessage = if (state.hasTouchedRealForm) {
                                        state.realBlockingError?.let { stringResource(it) }
                                    } else {
                                        null
                                    },
                                    hasAttemptedSubmit = state.hasAttemptedRealSubmit,
                                    isSubmitting = state.isSubmitting,
                                    canSubmit = state.canSubmitReal,
                                    onSubmit = { onIntent(CompleteEmployerInfoIntent.SubmitRealForm) },
                                )
                            }

                            EmployerInfoStep.LEGAL_LIST -> {
                                val entranceState = rememberStaggeredEntranceState(state.workshops)
                                LazyColumn(
                                    modifier = Modifier.fillMaxSize(),
                                    state = listState,
                                    contentPadding = PaddingValues(
                                        top = Spacing.sm,
                                        bottom = Spacing.xxl,
                                    ),
                                ) {
                                    legalWorkshopListSection(
                                        workshops = state.workshops,
                                        expandedWorkshopIds = state.expandedWorkshopIds,
                                        entranceState = entranceState,
                                        onToggleExpanded = { onIntent(CompleteEmployerInfoIntent.ToggleWorkshopExpanded(it)) },
                                        onSelectWorkshop = { onIntent(CompleteEmployerInfoIntent.SelectWorkshopForLegalForm(it)) },
                                    )
                                }
                            }
                        }
                    }
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

            // The initial load is the only failure that leaves the page with nothing to show, so
            // it is the only one offered a retry; everything else surfaces as a toast.
            ErrorStateView(
                message = state.generalError,
                onDismiss = { onIntent(CompleteEmployerInfoIntent.DismissGeneralError) },
                onRetry = {
                    onIntent(CompleteEmployerInfoIntent.DismissGeneralError)
                    onIntent(CompleteEmployerInfoIntent.LoadInitialData)
                },
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
                workshops = previewWorkshops(),
            ),
            onIntent = {},
            onBack = {},
        )
    }
}

/**
 * Built through [toWorkshopItemPR] rather than by hand: the row's branch line and its legal/real
 * badge are the mapper's work, and a preview holding them as literals still looks right after the
 * mapper stops producing them.
 */
internal fun previewWorkshops(): ImmutableList<WorkshopItemPR> = persistentListOf(
    previewAgreement(
        workshopId = "۰۰۸۱۶۳۱۸۲۹",
        name = "شرکت صنایع دما بخار مشهد",
        branchName = "شعبهٔ ۲ مشهد",
        branchCode = "۱۲۰۲",
        characterCode = "02",
        letDate = "۱۴۰۳/۰۵/۱۹",
        email = "info@damabokhar.ir",
        mobile = "۰۹۱۵۳۲۱۴۴۷۸",
        address = "مشهد، بلوار خیام، نبش خیام ۳۲، پلاک ۱۴",
    ).toWorkshopItemPR(),
    previewAgreement(
        workshopId = "۰۰۱۶۳۱۸۹۴۱",
        name = "درمانگاه دندانپزشکی دکتر محمدجعفری جبلی",
        branchName = "شعبهٔ ۵ مشهد",
        branchCode = "۱۲۰۵",
        characterCode = "01",
        letDate = "۱۴۰۲/۱۱/۰۳",
        email = "clinic.jebeli@gmail.com",
        mobile = "۰۹۱۵۱۱۰۲۲۳۴",
        address = "مشهد، خیابان احمدآباد، نبش قائم، ساختمان پزشکان",
    ).toWorkshopItemPR(),
)

@Suppress("LongParameterList")
internal fun previewAgreement(
    workshopId: String,
    name: String,
    branchName: String,
    branchCode: String,
    characterCode: String,
    letDate: String,
    email: String,
    mobile: String,
    address: String,
) = EmployerAgreementDN(
    pymseq = null, regno = null, firstname = null, emailaddr = email, nationalno = null,
    mobileno = mobile, startdate = null, mastcusttype = null, createdt = null, masttyp = null,
    logicalDeleted = false, regemailseq = null, lastname = null, special = null, risuid = null,
    nationalcode = null, enddate = null, letDate = letDate, regdate = null, roletype = null,
    dname = null, letNo = null, createuid = null,
    workshop = EmployerWorkshopDN(
        sswn = null, branchTitle = null, branchName = branchName, lastAddress = address,
        characterCode = characterCode, characterDesc = null, workshopApproveDate = null,
        inclusionDate = null, brhCode = null, activityName = null, workshopRegisterDate = null,
        branchCode = branchCode, workshopName = name, employerName = null, actitvityCode = null,
        userId = null, workshopId = workshopId, workshopUnemployedStat = null,
    ),
)

/**
 * A single step's body: scrolls on its own, so the animated swap above never has to move a lazy
 * list and a scrolling form with the same machinery.
 */
@Composable
private fun EmployerInfoStepBody(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = Spacing.xxl),
        content = content,
    )
}
