package com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.contract.AgreementRequestStep
import com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.contract.AgreementRequestUiState
import com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.contract.EmployerOnlineServicesErrorSource
import com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.contract.EmployerOnlineServicesIntent
import com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.ui.components.EmployerOnlineServicesErrorView
import com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.ui.components.EmployerOnlineServicesListSkeleton
import com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.ui.model.AgreementDocumentPR
import com.tamin.taminhamrah.model.workshop.WorkshopWithoutContractPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.NumericText
import com.tamin.taminhamrah.ui.components.TaminBottomActionBar
import com.tamin.taminhamrah.ui.components.TaminConfirmationDialog
import com.tamin.taminhamrah.ui.components.TaminFilledButton
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.components.toast.AppToastHost
import com.tamin.taminhamrah.ui.paging.PagingFooter
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.util.toPersianDigits
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.action_load_more
import taminx.core.core_ui.employer_online_services_national_code
import taminx.core.core_ui.employer_online_services_request_current_email
import taminx.core.core_ui.employer_online_services_request_current_mobile
import taminx.core.core_ui.employer_online_services_request_edit_contact
import taminx.core.core_ui.employer_online_services_request_new_email
import taminx.core.core_ui.employer_online_services_request_new_mobile
import taminx.core.core_ui.employer_online_services_request_submit
import taminx.core.core_ui.employer_online_services_request_success_body
import taminx.core.core_ui.employer_online_services_request_success_dismiss
import taminx.core.core_ui.employer_online_services_request_success_title
import taminx.core.core_ui.employer_online_services_request_workshops_without_contract
import taminx.core.core_ui.ic_tamin_chevron_down
import taminx.core.core_ui.ic_tamin_chevron_forward

/**
 * Step 2 — «پذیرش تعهدنامه». A collapsible identity recap, a collapsible list of the employer's
 * workshops still without an agreement, the تعهدنامه wording (served by `GetLegalDocumentUseCase`),
 * and a consent checkbox that gates "ثبت تعهدنامه". Content and errors are driven by the shared
 * ViewModel ([EmployerOnlineServicesErrorSource.STEP2_CONTENT] / [EmployerOnlineServicesErrorSource.SUBMIT]).
 */
@Composable
internal fun AcceptAgreementStep(
    request: AgreementRequestUiState,
    error: String?,
    submitError: String?,
    onIntent: (EmployerOnlineServicesIntent) -> Unit,
    onBack: () -> Unit,
) {
    val colors = LocalTaminColors.current
    val contentReady = request.document.clauses.isNotEmpty()

    Scaffold(
        modifier = Modifier.fillMaxSize().imePadding(),
        containerColor = colors.bgPage,
        contentWindowInsets = WindowInsets(0),
        bottomBar = {
            TaminBottomActionBar(
                primaryText = stringResource(Res.string.employer_online_services_request_submit),
                onPrimaryClick = { onIntent(EmployerOnlineServicesIntent.SubmitAgreement) },
                primaryEnabled = request.accepted && contentReady && !request.isSubmitting,
                isPrimaryLoading = request.isSubmitting,
                showChevron = false,
                secondaryText = stringResource(Res.string.employer_online_services_request_edit_contact),
                onSecondaryClick = onBack,
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            Spacer(Modifier.height(Spacing.md))

            when {
                request.isStep2Loading && !contentReady -> EmployerOnlineServicesListSkeleton()

                error != null && !contentReady -> EmployerOnlineServicesErrorView(
                    error = error,
                    onRetry = {
                        onIntent(
                            EmployerOnlineServicesIntent.RetrySource(
                                EmployerOnlineServicesErrorSource.STEP2_CONTENT,
                            ),
                        )
                    },
                    modifier = Modifier.fillMaxWidth().height(320.dp),
                )

                else -> {
                    IdentityRecapCard(request = request)
                    WorkshopsWithoutContractCard(
                        workshops = request.workshopsWithoutContract,
                        total = request.workshopsWithoutContractTotal,
                        isLoadingNextPage = request.workshopsWithoutContractLoadingNextPage,
                        endReached = request.workshopsWithoutContractEndReached,
                        paginationError = request.workshopsWithoutContractPaginationError,
                        onLoadMore = { onIntent(EmployerOnlineServicesIntent.LoadMoreWorkshopsWithoutContract) },
                        onWorkshopClicked = { workshop ->
                            onIntent(
                                EmployerOnlineServicesIntent.OpenContractRows(
                                    workshopName = workshop.name,
                                    workshopCodeLabel = workshop.codeLabel,
                                    workshopId = workshop.workshopId,
                                    branchCode = workshop.branchCode,
                                ),
                            )
                        },
                    )
                    RulesCard(document = request.document)
                    ConsentRow(
                        text = request.document.acknowledgement,
                        checked = request.accepted,
                        onCheckedChange = { onIntent(EmployerOnlineServicesIntent.SetAgreementAccepted(it)) },
                    )
                    if (!submitError.isNullOrBlank()) {
                        Text(
                            text = submitError,
                            style = MaterialTheme.typography.labelMedium,
                            color = colors.dangerText,
                        )
                    }
                }
            }

            Spacer(Modifier.height(Spacing.lg))
        }
    }
}

// --------------------------------------------------------------------- identity recap

@Composable
private fun IdentityRecapCard(request: AgreementRequestUiState) {
    val colors = LocalTaminColors.current
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth().taminSurface().padding(Spacing.lg)) {
        Row(
            modifier = Modifier.fillMaxWidth().clickable { expanded = !expanded },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            CheckBadge()
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(Spacing.xxs)) {
                Text(
                    text = request.employerName,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = colors.textPrimary,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                    Text(
                        text = stringResource(Res.string.employer_online_services_national_code),
                        style = MaterialTheme.typography.labelMedium,
                        color = colors.textMuted,
                    )
                    NumericText(
                        text = request.employerNationalCode.toPersianDigits(),
                        style = MaterialTheme.typography.labelMedium,
                        color = colors.textPrimary,
                    )
                }
            }
            ExpandChevron(expanded = expanded)
        }

        AnimatedVisibility(visible = expanded) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(top = Spacing.md),
                verticalArrangement = Arrangement.spacedBy(Spacing.sm),
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                    ContactCell(
                        label = stringResource(Res.string.employer_online_services_request_new_mobile),
                        value = request.mobile.toPersianDigits(),
                        highlighted = true,
                        modifier = Modifier.weight(1f),
                    )
                    ContactCell(
                        label = stringResource(Res.string.employer_online_services_request_current_mobile),
                        value = request.currentMobile.toPersianDigits(),
                        highlighted = false,
                        modifier = Modifier.weight(1f),
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                    ContactCell(
                        label = stringResource(Res.string.employer_online_services_request_new_email),
                        value = request.email,
                        highlighted = true,
                        numeric = false,
                        modifier = Modifier.weight(1f),
                    )
                    ContactCell(
                        label = stringResource(Res.string.employer_online_services_request_current_email),
                        value = request.currentEmail,
                        highlighted = false,
                        numeric = false,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun ContactCell(
    label: String,
    value: String,
    highlighted: Boolean,
    modifier: Modifier = Modifier,
    numeric: Boolean = true,
) {
    val colors = LocalTaminColors.current
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(CornerRadius.chip))
            .background(if (highlighted) colors.chipBg else Color.Transparent)
            .border(1.dp, colors.border, RoundedCornerShape(CornerRadius.chip))
            .padding(horizontal = Spacing.md, vertical = Spacing.sm),
        verticalArrangement = Arrangement.spacedBy(Spacing.xxs),
    ) {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = colors.textMuted)
        val valueColor = if (highlighted) colors.blueText else colors.textMuted
        if (numeric) {
            NumericText(text = value, style = MaterialTheme.typography.labelMedium, color = valueColor)
        } else {
            Text(text = value, style = MaterialTheme.typography.labelMedium, color = valueColor)
        }
    }
}

// -------------------------------------------------------- workshops without contract

@Composable
private fun WorkshopsWithoutContractCard(
    workshops: List<WorkshopWithoutContractPR>,
    total: Int,
    isLoadingNextPage: Boolean,
    endReached: Boolean,
    paginationError: String?,
    onLoadMore: () -> Unit,
    onWorkshopClicked: (WorkshopWithoutContractPR) -> Unit,
) {
    val colors = LocalTaminColors.current
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth().taminSurface().padding(Spacing.lg)) {
        Row(
            modifier = Modifier.fillMaxWidth().clickable { expanded = !expanded },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            Text(
                text = stringResource(Res.string.employer_online_services_request_workshops_without_contract),
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = colors.textPrimary,
                modifier = Modifier.weight(1f),
            )
            CountBadge(count = total)
            ExpandChevron(expanded = expanded)
        }

        AnimatedVisibility(visible = expanded) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(top = Spacing.sm),
                verticalArrangement = Arrangement.spacedBy(Spacing.sm),
            ) {
                workshops.forEach { workshop ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(CornerRadius.chip))
                            .border(1.dp, colors.border, RoundedCornerShape(CornerRadius.chip))
                            .clickable(enabled = workshop.hasIdentity) { onWorkshopClicked(workshop) }
                            .padding(Spacing.md),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(Spacing.xxs),
                        ) {
                            Text(
                                text = workshop.name,
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                color = colors.textPrimary,
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                                Text(
                                    text = workshop.branchOfficeName,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = colors.textMuted,
                                )
                                Text(text = "·", style = MaterialTheme.typography.labelMedium, color = colors.textMuted)
                                NumericText(
                                    text = workshop.codeLabel,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = colors.textMuted,
                                )
                            }
                        }
                        if (workshop.hasIdentity) {
                            Icon(
                                imageVector = vectorResource(Res.drawable.ic_tamin_chevron_forward),
                                contentDescription = null,
                                tint = colors.textMuted,
                                modifier = Modifier.size(14.dp),
                            )
                        }
                    }
                }

                // No LazyColumn here to drive an OnLoadMore scroll trigger — this card sits inside
                // the wizard's own scrolling Column — so the next page is fetched on tap instead.
                if (!endReached) {
                    if (isLoadingNextPage || paginationError != null) {
                        PagingFooter(
                            isLoadingNextPage = isLoadingNextPage,
                            error = paginationError,
                            onRetry = onLoadMore,
                        )
                    } else {
                        Text(
                            text = stringResource(Res.string.action_load_more),
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = colors.blueText,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(CornerRadius.chip))
                                .clickable(onClick = onLoadMore)
                                .padding(vertical = Spacing.sm),
                        )
                    }
                }
            }
        }
    }
}

// ------------------------------------------------------------------------- rules card

@Composable
private fun RulesCard(document: AgreementDocumentPR) {
    val colors = LocalTaminColors.current

    Column(
        modifier = Modifier.fillMaxWidth().taminSurface().padding(Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        Text(
            text = document.intro,
            style = MaterialTheme.typography.bodySmall,
            color = colors.textPrimary,
        )
        document.clauses.forEachIndexed { index, clause ->
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                NumberBadge(number = index + 1)
                Text(
                    text = clause,
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun ConsentRow(text: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    val colors = LocalTaminColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .taminSurface()
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = Spacing.sm, vertical = Spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = colors.blueText,
                uncheckedColor = colors.border,
                checkmarkColor = Color.White,
            ),
        )
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = colors.textPrimary,
            modifier = Modifier.weight(1f),
        )
    }
}

// ---------------------------------------------------------------------- small pieces

@Composable
private fun ExpandChevron(expanded: Boolean) {
    val rotation by animateFloatAsState(targetValue = if (expanded) 180f else 0f, label = "chevron")
    Icon(
        imageVector = vectorResource(Res.drawable.ic_tamin_chevron_down),
        contentDescription = null,
        tint = LocalTaminColors.current.textMuted,
        modifier = Modifier.size(18.dp).rotate(rotation),
    )
}

@Composable
private fun CheckBadge() {
    val colors = LocalTaminColors.current
    Box(
        modifier = Modifier.size(32.dp).clip(RoundedCornerShape(Spacing.sm)).background(colors.greenBg),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            tint = colors.greenText,
            modifier = Modifier.size(16.dp),
        )
    }
}

@Composable
private fun CountBadge(count: Int) {
    val colors = LocalTaminColors.current
    Box(
        modifier = Modifier.size(24.dp).clip(CircleShape).background(colors.chipBg),
        contentAlignment = Alignment.Center,
    ) {
        NumericText(
            text = count.toString().toPersianDigits(),
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = colors.blueText,
        )
    }
}

@Composable
private fun NumberBadge(number: Int) {
    val colors = LocalTaminColors.current
    Box(
        modifier = Modifier.size(20.dp).clip(RoundedCornerShape(Spacing.sm)).background(colors.chipBg),
        contentAlignment = Alignment.Center,
    ) {
        NumericText(
            text = number.toString().toPersianDigits(),
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = colors.blueText,
        )
    }
}

// --------------------------------------------------------------------- success dialog

@Composable
internal fun EmployerAgreementSuccessDialog(onDismiss: () -> Unit) {
    val colors = LocalTaminColors.current
    TaminConfirmationDialog(
        title = stringResource(Res.string.employer_online_services_request_success_title),
        description = stringResource(Res.string.employer_online_services_request_success_body),
        confirmButton = {
            TaminFilledButton(
                text = stringResource(Res.string.employer_online_services_request_success_dismiss),
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
            )
        },
        dismissButton = {},
        onDismissRequest = onDismiss,
        icon = Icons.Default.Check,
        iconTint = colors.greenText,
        iconBackground = colors.greenBg,
    )
}

// --------------------------------------------------------------------------- previews

private val PreviewDocument = AgreementDocumentPR(
    intro = "اینجانب حسین توکلی کرمانی به شماره ملی ۴۴۷۹۸۹۰۸۸۲ ضمن تائید مشخصات مذکور، با اطلاع از قواعد خدمات و ابلاغ الکترونیک سازمان تأمین اجتماعی و پذیرفتن مقررات، متعهد می‌باشم با رعایت ضوابط مذکور نسبت به تبادل اطلاعات و ارتباطات الکترونیک با سازمان تأمین اجتماعی اقدام نمایم:",
    clauses = listOf(
        "در خصوص انجام تعهدات و تکالیف مقرر در قانون تأمین اجتماعی و ضوابط و مقررات اجرایی از طریق سامانه خدمات الکترونیک و غیرحضوری سازمان تأمین اجتماعی، مکاتبه و ارائه اسناد و مدارک مورد نیاز را از طریق حساب کاربری ایجاد شده اقدام خواهم نمود.",
        "در ابلاغ الکترونیکی، با اطلاع از اینکه تاریخ ثبت اوراق در سامانه خدمات الکترونیک و غیرحضوری ملاک است، موظفم با ورود به حساب کاربری نسبت به رویت اوراق ابلاغ شده که به منزلهٔ رسید می‌باشد، اقدام نمایم.",
        "در صورت هرگونه اشتباه یا تغییر در مشخصات اعلام‌شده، در اسرع وقت نسبت به ثبت مشخصات صحیح یا تغییرات اقدام نمایم.",
    ),
    acknowledgement = "با اطلاع از شرایط و قواعد خدمات الکترونیک سازمان تأمین اجتماعی و ابلاغ الکترونیک، متعهد به رعایت ضوابط مذکور می‌باشم.",
)

private val PreviewAcceptRequest = AgreementRequestUiState(
    step = AgreementRequestStep.ACCEPT_AGREEMENT,
    mobile = "09153214478",
    email = "h.tavakoli@gmail.com",
    code = "۱۲۳۴۵۶",
    employerName = "حسین توکلی کرمانی",
    employerNationalCode = "۴۴۷۹۸۹۰۸۸۲",
    currentMobile = "09153214478",
    currentEmail = "h.tavakoli@gmail.com",
    workshopsWithoutContract = listOf(
        WorkshopWithoutContractPR(
            name = "درمانگاه دندان‌پزشکی دکتر محمدجعفری جبلی",
            codeLabel = "۰۰۱۶۳۱۸۹۴۱",
            branchOfficeName = "شعبهٔ ۵ مشهد",
        ),
        WorkshopWithoutContractPR(
            name = "کارگاه ساختمانی خیام ۳۲",
            codeLabel = "۰۰۷۷۱۲۳۴۵۶",
            branchOfficeName = "شعبهٔ ۲ مشهد",
        ),
    ),
    workshopsWithoutContractTotal = 2,
    document = PreviewDocument,
)

@PreviewRtlTheme
@Composable
private fun AcceptAgreementStepPreviewLight() {
    PreviewRtlThemeContent {
        AppToastHost {
            AcceptAgreementStep(
                request = PreviewAcceptRequest,
                error = null,
                submitError = null,
                onIntent = {},
                onBack = {},
            )
        }
    }
}

@PreviewRtlTheme
@Composable
private fun AcceptAgreementStepPreviewDark() {
    PreviewRtlThemeContent(darkTheme = true) {
        AppToastHost {
            AcceptAgreementStep(
                request = PreviewAcceptRequest.copy(accepted = true),
                error = null,
                submitError = null,
                onIntent = {},
                onBack = {},
            )
        }
    }
}

@PreviewRtlTheme
@Composable
private fun EmployerAgreementSuccessDialogPreview() {
    PreviewRtlThemeContent {
        EmployerAgreementSuccessDialog(onDismiss = {})
    }
}
