package com.tamin.taminhamrah.feature.userRequest.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.tamin.taminhamrah.feature.userRequest.ui.screens.contract.DocumentPreview
import com.tamin.taminhamrah.feature.userRequest.ui.screens.contract.UserRequestDetailEvent
import com.tamin.taminhamrah.feature.userRequest.ui.screens.contract.UserRequestDetailIntent
import com.tamin.taminhamrah.model.userRequest.UserRequestDocumentPR
import com.tamin.taminhamrah.model.userRequest.UserRequestPR
import com.tamin.taminhamrah.model.userRequest.UserRequestTypeIds
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.AnimatedRingHeaderIcon
import com.tamin.taminhamrah.ui.components.DecorativeBackgroundCircle
import com.tamin.taminhamrah.ui.components.LoadAsyncImage
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.Elevation
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.toPriceFormat
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_tamin_chevron_back
import taminx.core.core_ui.unit_rial
import taminx.feature.userrequest.generated.resources.user_request_detail_account_number
import taminx.feature.userrequest.generated.resources.user_request_detail_amount
import taminx.feature.userrequest.generated.resources.user_request_detail_answer_date
import taminx.feature.userrequest.generated.resources.user_request_detail_article_sixteen_date
import taminx.feature.userrequest.generated.resources.user_request_detail_article_sixteen_defect
import taminx.feature.userrequest.generated.resources.user_request_detail_article_sixteen_info
import taminx.feature.userrequest.generated.resources.user_request_detail_article_sixteen_result
import taminx.feature.userrequest.generated.resources.user_request_detail_bank_institution
import taminx.feature.userrequest.generated.resources.user_request_detail_birth_date
import taminx.feature.userrequest.generated.resources.user_request_detail_borrower
import taminx.feature.userrequest.generated.resources.user_request_detail_branch_name
import taminx.feature.userrequest.generated.resources.user_request_detail_childbearing_date
import taminx.feature.userrequest.generated.resources.user_request_detail_doctor_id
import taminx.feature.userrequest.generated.resources.user_request_detail_doctor_name
import taminx.feature.userrequest.generated.resources.user_request_detail_end_date
import taminx.feature.userrequest.generated.resources.user_request_detail_expert_explanation
import taminx.feature.userrequest.generated.resources.user_request_detail_full_name
import taminx.feature.userrequest.generated.resources.user_request_detail_guarantee_amount_label
import taminx.feature.userrequest.generated.resources.user_request_detail_installment_amount_label
import taminx.feature.userrequest.generated.resources.user_request_detail_installment_count_label
import taminx.feature.userrequest.generated.resources.user_request_detail_insurance_number
import taminx.feature.userrequest.generated.resources.user_request_detail_investigation_result
import taminx.feature.userrequest.generated.resources.user_request_detail_loan_details
import taminx.feature.userrequest.generated.resources.user_request_detail_mobile
import taminx.feature.userrequest.generated.resources.user_request_detail_national_id
import taminx.feature.userrequest.generated.resources.user_request_detail_objection_date
import taminx.feature.userrequest.generated.resources.user_request_detail_objection_info
import taminx.feature.userrequest.generated.resources.user_request_detail_objection_reason
import taminx.feature.userrequest.generated.resources.user_request_detail_pension_number
import taminx.feature.userrequest.generated.resources.user_request_detail_pensioner_guarantor
import taminx.feature.userrequest.generated.resources.user_request_detail_placeholder_dash
import taminx.feature.userrequest.generated.resources.user_request_detail_pregnancy_status
import taminx.feature.userrequest.generated.resources.user_request_detail_pregnancy_type
import taminx.feature.userrequest.generated.resources.user_request_detail_repayment_amount
import taminx.feature.userrequest.generated.resources.user_request_detail_request_info
import taminx.feature.userrequest.generated.resources.user_request_detail_rest_days
import taminx.feature.userrequest.generated.resources.user_request_detail_start_date
import taminx.feature.userrequest.generated.resources.user_request_detail_user_desc
import taminx.feature.userrequest.generated.resources.user_request_detail_user_info
import taminx.feature.userrequest.generated.resources.user_request_document_fallback_title
import taminx.feature.userrequest.generated.resources.user_request_document_preview_close
import taminx.feature.userrequest.generated.resources.user_request_document_view_action
import taminx.feature.userrequest.generated.resources.user_request_documents_section_title
import taminx.feature.userrequest.generated.resources.user_request_reject_reason_label
import taminx.feature.userrequest.generated.resources.Res as UserRequestRes

// Request type IDs — same values as RequestTypeEnumClass in my-tamin-droid
private const val REQUEST_TYPE_FOLLOW_UP_OBJECTION = UserRequestTypeIds.FOLLOW_UP_OBJECTION
private const val REQUEST_TYPE_ILL_DAY = UserRequestTypeIds.ILL_DAY
private const val REQUEST_TYPE_PREGNANCY = UserRequestTypeIds.PREGNANCY
private const val REQUEST_TYPE_ORTHOTICS_PROSTHESIS = UserRequestTypeIds.ORTHOTICS_PROSTHESIS
private const val REQUEST_TYPE_DEFERRED_INSTALLMENT_CERTIFICATE = UserRequestTypeIds.DEFERRED_INSTALLMENT
private const val REQUEST_TYPE_ARTICLE_SIXTEEN = UserRequestTypeIds.ARTICLE_SIXTEEN

// ── Route ────────────────────────────────────────────────────────────────────

@Composable
fun UserRequestDetailRoute(
    requestId: Long,
    refCode: String,
    requestTypeId: Long,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    title: String,
    referenceId: String = "",
    viewModel: UserRequestDetailViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(requestId, refCode, requestTypeId, referenceId) {
        viewModel.sendIntent(
            UserRequestDetailIntent.LoadDetail(
                requestId = requestId,
                refCode = refCode,
                requestTypeId = requestTypeId,
                title = title,
                referenceId = referenceId,
            )
        )
    }

    HandleUserRequestDetailEvents(
        events = viewModel.events,
        onBackClick = onBackClick,
        onShowToast = { message -> snackbarHostState.showSnackbar(message) },
    )

    UserRequestDetailScreen(
        requestId = requestId,
        refCode = refCode,
        requestTypeId = requestTypeId,
        title = title,
        isLoading = state.isLoading,
        request = state.request,
        error = state.error,
        downloadingDocumentGuid = state.downloadingDocumentGuid,
        documentPreview = state.documentPreview,
        snackbarHostState = snackbarHostState,
        onBackClick = onBackClick,
        onDownloadDocument = { document, resolvedTitle ->
            viewModel.sendIntent(
                UserRequestDetailIntent.DownloadDocument(
                    guid = document.guid,
                    title = resolvedTitle,
                )
            )
        },
        onDismissDocumentPreview = {
            viewModel.sendIntent(UserRequestDetailIntent.DismissDocumentPreview)
        },
        modifier = modifier,
    )
}

@Composable
fun HandleUserRequestDetailEvents(
    events: Flow<UserRequestDetailEvent>,
    onBackClick: () -> Unit,
    onShowToast: suspend (String) -> Unit,
) {
    events.collectWithLifecycleAware { event ->
        when (event) {
            is UserRequestDetailEvent.NavigateBack -> onBackClick()
            is UserRequestDetailEvent.ShowToast -> onShowToast(event.message)
        }
    }
}

// ── Screen ───────────────────────────────────────────────────────────────────

@Composable
fun UserRequestDetailScreen(
    requestId: Long,
    refCode: String,
    requestTypeId: Long,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    title: String,
    isLoading: Boolean = false,
    request: UserRequestPR? = null,
    error: String? = null,
    downloadingDocumentGuid: String? = null,
    documentPreview: DocumentPreview? = null,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    onDownloadDocument: (UserRequestDocumentPR, String) -> Unit = { _, _ -> },
    onDismissDocumentPreview: () -> Unit = {},
) {
    val taminColors = LocalTaminColors.current
    val profileGradientBrush = remember(taminColors.profileGradientStops) {
        Brush.horizontalGradient(taminColors.profileGradientStops)
    }

    val details = request?.details

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = taminColors.bgPage,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TaminTopAppBar(
                title = title,
                background = profileGradientBrush,
                bottomPadding = Spacing.xl,
                shape = RoundedCornerShape(
                    bottomStart = CornerRadius.x3l,
                    bottomEnd = CornerRadius.x3l
                ),
                navigationIcon = {
                    TaminTopAppBarButton(
                        icon = vectorResource(Res.drawable.ic_tamin_chevron_back),
                        contentDescription = null,
                        onClick = onBackClick,
                        bordered = true
                    )
                }
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    DecorativeBackgroundCircle(
                        size = 190.dp,
                        xOffset = 450.dp,
                        yOffset = (-150).dp
                    )

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AnimatedRingHeaderIcon(icon = Icons.Default.Description)
                    }
                }
            }
        }
    ) { innerPadding ->

        when {
            // ── Loading ──────────────────────────────────────────────────────
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }

            // ── Error ────────────────────────────────────────────────────────
            error != null && request == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    TaminText(
                        text = error,
                        style = MaterialTheme.typography.bodyMedium,
                        color = taminColors.textTertiary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = Spacing.page)
                    )
                }
            }

            // ── Content ──────────────────────────────────────────────────────
            else -> {
                val placeholder = stringResource(UserRequestRes.string.user_request_detail_placeholder_dash)
                val rialUnit = stringResource(Res.string.unit_rial)
                val repaymentAmountColor = taminColors.greenText
                val guaranteeAmountColor = taminColors.orangeText

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    verticalArrangement = Arrangement.spacedBy(Spacing.md)
                ) {

                    item {
                        Spacer(modifier = Modifier.height(Spacing.md))
                    }

                    // Section 1: Common request summary card
                    item {
                        RequestSummaryCard(
                            refCode = request?.refCode ?: refCode,
                            title = request?.title ?: title,
                            statusDesc = request?.statusDesc ?: "",
                            creationTime = request?.creationTime ?: "",
                            rejectReason = request?.details?.rejectReason,
                            modifier = Modifier.padding(horizontal = Spacing.page)
                        )
                    }

                    // Section 2+: Type-specific content sections
                    when (requestTypeId) {
                        REQUEST_TYPE_DEFERRED_INSTALLMENT_CERTIFICATE -> {
                            val deferredDetails = details?.deferredInstallment
                            val pensionerName = deferredDetails?.pensionerFullName
                                ?.takeIf { it.isNotBlank() }
                                ?: request?.createByName
                            val pensionerNationalId = deferredDetails?.pensionerNationalId
                                ?: request?.refCode

                            item {
                                DetailSectionCard(
                                    title = stringResource(UserRequestRes.string.user_request_detail_pensioner_guarantor),
                                    items = listOf(
                                        Triple(
                                            stringResource(UserRequestRes.string.user_request_detail_full_name),
                                            pensionerName ?: placeholder,
                                            null
                                        ),
                                        Triple(
                                            stringResource(UserRequestRes.string.user_request_detail_national_id),
                                            pensionerNationalId ?: placeholder,
                                            null
                                        ),
                                        Triple(
                                            stringResource(UserRequestRes.string.user_request_detail_pension_number),
                                            deferredDetails?.pensionerId ?: placeholder,
                                            null
                                        ),
                                    ),
                                    modifier = Modifier.padding(horizontal = Spacing.page)
                                )
                            }

                            if (deferredDetails != null) {
                                item {
                                    DetailSectionCard(
                                        title = stringResource(UserRequestRes.string.user_request_detail_borrower),
                                        items = listOf(
                                            Triple(
                                                stringResource(UserRequestRes.string.user_request_detail_full_name),
                                                deferredDetails.borrowerName ?: deferredDetails.borrowerFullName ?: placeholder,
                                                null
                                            ),
                                            Triple(
                                                stringResource(UserRequestRes.string.user_request_detail_national_id),
                                                deferredDetails.borrowerNationalCode ?: deferredDetails.borrowerNationalId ?: placeholder,
                                                null
                                            ),
                                            Triple(
                                                stringResource(UserRequestRes.string.user_request_detail_birth_date),
                                                deferredDetails.borrowerBirthDate ?: placeholder,
                                                null
                                            ),
                                        ),
                                        modifier = Modifier.padding(horizontal = Spacing.page)
                                    )
                                }

                                item {
                                    DetailSectionCard(
                                        title = stringResource(UserRequestRes.string.user_request_detail_loan_details),
                                        items = listOf(
                                            Triple(
                                                stringResource(UserRequestRes.string.user_request_detail_bank_institution),
                                                deferredDetails.bankName ?: deferredDetails.bank ?: placeholder,
                                                null
                                            ),
                                            Triple(
                                                stringResource(UserRequestRes.string.user_request_detail_branch_name),
                                                deferredDetails.branchName ?: deferredDetails.branch ?: placeholder,
                                                null
                                            ),
                                            Triple(
                                                stringResource(UserRequestRes.string.user_request_detail_repayment_amount),
                                                deferredDetails.repaymentAmount?.toPriceFormat()?.let { "$it $rialUnit" } ?: placeholder,
                                                repaymentAmountColor
                                            ),
                                            Triple(
                                                stringResource(UserRequestRes.string.user_request_detail_installment_count_label),
                                                deferredDetails.installmentCount?.toString() ?: placeholder,
                                                null
                                            ),
                                            Triple(
                                                stringResource(UserRequestRes.string.user_request_detail_installment_amount_label),
                                                deferredDetails.installmentAmount?.toPriceFormat()?.let { "$it $rialUnit" } ?: placeholder,
                                                null
                                            ),
                                            Triple(
                                                stringResource(UserRequestRes.string.user_request_detail_guarantee_amount_label),
                                                deferredDetails.guaranteeAmount?.toPriceFormat()?.let { "$it $rialUnit" } ?: placeholder,
                                                guaranteeAmountColor
                                            )
                                        ),
                                        modifier = Modifier.padding(horizontal = Spacing.page)
                                    )
                                }
                            }
                        }

                        REQUEST_TYPE_ILL_DAY, REQUEST_TYPE_ORTHOTICS_PROSTHESIS -> {
                            val illDetails = details?.illDay
                            item {
                                DetailSectionCard(
                                    title = stringResource(UserRequestRes.string.user_request_detail_request_info),
                                    items = listOf(
                                        Triple(
                                            stringResource(UserRequestRes.string.user_request_detail_full_name),
                                            illDetails?.insuredFullName ?: request?.createByName ?: placeholder,
                                            null
                                        ),
                                        Triple(
                                            stringResource(UserRequestRes.string.user_request_detail_insurance_number),
                                            illDetails?.insuranceNumber ?: placeholder,
                                            null
                                        ),
                                        Triple(
                                            stringResource(UserRequestRes.string.user_request_detail_mobile),
                                            illDetails?.mobile ?: placeholder,
                                            null
                                        ),
                                        Triple(
                                            stringResource(UserRequestRes.string.user_request_detail_bank_institution),
                                            illDetails?.bankName ?: placeholder,
                                            null
                                        ),
                                        Triple(
                                            stringResource(UserRequestRes.string.user_request_detail_account_number),
                                            illDetails?.bankAccount ?: placeholder,
                                            null
                                        ),
                                    ),
                                    modifier = Modifier.padding(horizontal = Spacing.page)
                                )
                            }

                            if (illDetails != null) {
                                item {
                                    DetailSectionCard(
                                        title = stringResource(UserRequestRes.string.user_request_detail_user_info),
                                        items = listOfNotNull(
                                            Triple(
                                                stringResource(UserRequestRes.string.user_request_detail_branch_name),
                                                illDetails.branchName ?: placeholder,
                                                null
                                            ),
                                            Triple(
                                                stringResource(UserRequestRes.string.user_request_detail_start_date),
                                                illDetails.startDate ?: placeholder,
                                                null
                                            ),
                                            Triple(
                                                stringResource(UserRequestRes.string.user_request_detail_end_date),
                                                illDetails.endDate ?: placeholder,
                                                null
                                            ),
                                            Triple(
                                                stringResource(UserRequestRes.string.user_request_detail_doctor_name),
                                                illDetails.doctorName ?: illDetails.employerName ?: placeholder,
                                                null
                                            ),
                                            Triple(
                                                stringResource(UserRequestRes.string.user_request_detail_doctor_id),
                                                illDetails.doctorId ?: placeholder,
                                                null
                                            ),
                                            illDetails.amount?.let { amount ->
                                                Triple(
                                                    stringResource(UserRequestRes.string.user_request_detail_amount),
                                                    amount.toPriceFormat().let { "$it $rialUnit" },
                                                    repaymentAmountColor
                                                )
                                            },
                                        ),
                                        modifier = Modifier.padding(horizontal = Spacing.page)
                                    )
                                }
                            }
                        }

                        REQUEST_TYPE_PREGNANCY -> {
                            val pregnancy = details?.pregnancy
                            item {
                                DetailSectionCard(
                                    title = stringResource(UserRequestRes.string.user_request_detail_request_info),
                                    items = listOf(
                                        Triple(
                                            stringResource(UserRequestRes.string.user_request_detail_full_name),
                                            pregnancy?.insuredFullName ?: request?.createByName ?: placeholder,
                                            null
                                        ),
                                        Triple(
                                            stringResource(UserRequestRes.string.user_request_detail_insurance_number),
                                            pregnancy?.insuranceNumber ?: placeholder,
                                            null
                                        ),
                                        Triple(
                                            stringResource(UserRequestRes.string.user_request_detail_mobile),
                                            pregnancy?.mobile ?: placeholder,
                                            null
                                        ),
                                    ),
                                    modifier = Modifier.padding(horizontal = Spacing.page)
                                )
                            }
                            if (pregnancy != null) {
                                item {
                                    DetailSectionCard(
                                        title = stringResource(UserRequestRes.string.user_request_detail_user_info),
                                        items = listOf(
                                            Triple(
                                                stringResource(UserRequestRes.string.user_request_detail_branch_name),
                                                pregnancy.branchName ?: placeholder,
                                                null
                                            ),
                                            Triple(
                                                stringResource(UserRequestRes.string.user_request_detail_start_date),
                                                pregnancy.startDate ?: placeholder,
                                                null
                                            ),
                                            Triple(
                                                stringResource(UserRequestRes.string.user_request_detail_end_date),
                                                pregnancy.endDate ?: placeholder,
                                                null
                                            ),
                                            Triple(
                                                stringResource(UserRequestRes.string.user_request_detail_childbearing_date),
                                                pregnancy.childbearingDate ?: placeholder,
                                                null
                                            ),
                                            Triple(
                                                stringResource(UserRequestRes.string.user_request_detail_doctor_name),
                                                pregnancy.doctorName ?: placeholder,
                                                null
                                            ),
                                            Triple(
                                                stringResource(UserRequestRes.string.user_request_detail_doctor_id),
                                                pregnancy.doctorId ?: placeholder,
                                                null
                                            ),
                                            Triple(
                                                stringResource(UserRequestRes.string.user_request_detail_rest_days),
                                                pregnancy.restDays ?: placeholder,
                                                null
                                            ),
                                            Triple(
                                                stringResource(UserRequestRes.string.user_request_detail_pregnancy_status),
                                                pregnancy.statusDesc ?: placeholder,
                                                null
                                            ),
                                            Triple(
                                                stringResource(UserRequestRes.string.user_request_detail_pregnancy_type),
                                                pregnancy.typeDesc ?: placeholder,
                                                null
                                            ),
                                        ),
                                        modifier = Modifier.padding(horizontal = Spacing.page)
                                    )
                                }
                            }
                        }

                        REQUEST_TYPE_ARTICLE_SIXTEEN -> {
                            val articleSixteenDetails = details?.articleSixteen
                            item {
                                DetailSectionCard(
                                    title = stringResource(UserRequestRes.string.user_request_detail_pensioner_guarantor),
                                    items = listOf(
                                        Triple(
                                            stringResource(UserRequestRes.string.user_request_detail_full_name),
                                            request?.createByName ?: placeholder,
                                            null
                                        ),
                                        Triple(
                                            stringResource(UserRequestRes.string.user_request_detail_national_id),
                                            request?.refCode ?: placeholder,
                                            null
                                        ),
                                    ),
                                    modifier = Modifier.padding(horizontal = Spacing.page)
                                )
                            }
                            if (articleSixteenDetails != null) {
                                item {
                                    DetailSectionCard(
                                        title = stringResource(UserRequestRes.string.user_request_detail_article_sixteen_info),
                                        items = listOf(
                                            Triple(
                                                stringResource(UserRequestRes.string.user_request_detail_article_sixteen_date),
                                                articleSixteenDetails.meetingDate ?: placeholder,
                                                null
                                            ),
                                            Triple(
                                                stringResource(UserRequestRes.string.user_request_detail_article_sixteen_result),
                                                articleSixteenDetails.result ?: articleSixteenDetails.defectDesc ?: placeholder,
                                                null
                                            ),
                                            Triple(
                                                stringResource(UserRequestRes.string.user_request_detail_article_sixteen_defect),
                                                articleSixteenDetails.defectDesc ?: placeholder,
                                                null
                                            ),
                                        ),
                                        modifier = Modifier.padding(horizontal = Spacing.page)
                                    )
                                }
                            }
                        }

                        REQUEST_TYPE_FOLLOW_UP_OBJECTION -> {
                            val objectionDetails = details?.followUpObjection
                            if (objectionDetails != null) {
                                item {
                                    DetailSectionCard(
                                        title = stringResource(UserRequestRes.string.user_request_detail_objection_info),
                                        items = listOf(
                                            Triple(
                                                stringResource(UserRequestRes.string.user_request_detail_branch_name),
                                                objectionDetails.branchName ?: placeholder,
                                                null
                                            ),
                                            Triple(
                                                stringResource(UserRequestRes.string.user_request_detail_objection_date),
                                                objectionDetails.objectionDate ?: placeholder,
                                                null
                                            ),
                                            Triple(
                                                stringResource(UserRequestRes.string.user_request_detail_objection_reason),
                                                objectionDetails.reason ?: objectionDetails.requestDesc ?: placeholder,
                                                null
                                            ),
                                            Triple(
                                                stringResource(UserRequestRes.string.user_request_detail_user_desc),
                                                objectionDetails.userDesc ?: placeholder,
                                                null
                                            ),
                                            Triple(
                                                stringResource(UserRequestRes.string.user_request_detail_answer_date),
                                                objectionDetails.answerDate ?: placeholder,
                                                null
                                            ),
                                            Triple(
                                                stringResource(UserRequestRes.string.user_request_detail_investigation_result),
                                                objectionDetails.answerTypeDesc ?: placeholder,
                                                null
                                            ),
                                            Triple(
                                                stringResource(UserRequestRes.string.user_request_detail_expert_explanation),
                                                objectionDetails.resultDesc ?: placeholder,
                                                null
                                            ),
                                        ),
                                        modifier = Modifier.padding(horizontal = Spacing.page)
                                    )
                                }
                            }
                        }

                        else -> {
                            // Generic fallback: nothing extra shown beyond the summary card
                        }
                    }

                    // Section: Submitted documents (shared across all request types)
                    val documents = details?.documents.orEmpty()
                    if (documents.isNotEmpty()) {
                        item {
                            SubmittedDocumentsCard(
                                documents = documents,
                                downloadingDocumentGuid = downloadingDocumentGuid,
                                onDownloadDocument = onDownloadDocument,
                                modifier = Modifier.padding(horizontal = Spacing.page)
                            )
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(Spacing.xl))
                    }
                }
            }
        }
    }

    if (documentPreview != null) {
        DocumentPreviewDialog(
            preview = documentPreview,
            onDismiss = onDismissDocumentPreview,
        )
    }
}

// ── Components ────────────────────────────────────────────────────────────────

@Composable
private fun RequestSummaryCard(
    refCode: String,
    title: String,
    statusDesc: String,
    creationTime: String,
    rejectReason: String? = null,
    modifier: Modifier = Modifier,
) {
    val taminColors = LocalTaminColors.current
    val colorScheme = MaterialTheme.colorScheme

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(CornerRadius.cardCompact),
        colors = CardDefaults.cardColors(containerColor = taminColors.bgSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation.xs)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.page),
            verticalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TaminText(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = taminColors.textPrimary
                )
                if (statusDesc.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(CornerRadius.avatarTile))
                            .background(taminColors.chipBg)
                            .padding(horizontal = Spacing.md, vertical = Spacing.xs)
                    ) {
                        TaminText(
                            text = statusDesc,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = colorScheme.primary
                        )
                    }
                }
            }

            if (refCode.isNotBlank()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    TaminText(
                        text = stringResource(UserRequestRes.string.user_request_detail_national_id),
                        style = MaterialTheme.typography.bodySmall,
                        color = taminColors.textTertiary
                    )
                    TaminText(
                        text = refCode,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = taminColors.textPrimary
                    )
                }
            }

            if (creationTime.isNotBlank() && creationTime != "0") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    TaminText(
                        text = stringResource(UserRequestRes.string.user_request_detail_birth_date),
                        style = MaterialTheme.typography.bodySmall,
                        color = taminColors.textTertiary
                    )
                    TaminText(
                        text = creationTime,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = taminColors.textPrimary
                    )
                }
            }

            if (!rejectReason.isNullOrBlank()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    TaminText(
                        text = stringResource(UserRequestRes.string.user_request_reject_reason_label, rejectReason),
                        style = MaterialTheme.typography.bodySmall,
                        color = taminColors.dangerText
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailSectionCard(
    title: String,
    items: List<Triple<String, String, Color?>>,
    modifier: Modifier = Modifier,
) {
    val taminColors = LocalTaminColors.current

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(CornerRadius.cardCompact),
        colors = CardDefaults.cardColors(containerColor = taminColors.bgSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation.xs)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.page),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(Spacing.sm)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                )
                Spacer(modifier = Modifier.size(Spacing.xs))
                TaminText(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = taminColors.textPrimary
                )
            }

            items.forEach { (label, value, valueColor) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = Spacing.xs),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TaminText(
                        text = label,
                        style = MaterialTheme.typography.bodySmall,
                        color = taminColors.textTertiary
                    )
                    TaminText(
                        text = value,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = valueColor ?: taminColors.textPrimary
                    )
                }
            }
        }
    }
}

@Composable
private fun SubmittedDocumentsCard(
    documents: List<UserRequestDocumentPR>,
    downloadingDocumentGuid: String?,
    onDownloadDocument: (UserRequestDocumentPR, String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val taminColors = LocalTaminColors.current

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(CornerRadius.cardCompact),
        colors = CardDefaults.cardColors(containerColor = taminColors.bgSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation.xs)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.page),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(Spacing.sm)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                )
                Spacer(modifier = Modifier.size(Spacing.xs))
                TaminText(
                    text = stringResource(UserRequestRes.string.user_request_documents_section_title),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = taminColors.textPrimary
                )
            }

            documents.forEachIndexed { index, document ->
                val resolvedTitle = document.documentType?.takeIf { it.isNotBlank() }
                    ?: stringResource(
                        UserRequestRes.string.user_request_document_fallback_title,
                        (index + 1).toString()
                    )
                val isDownloading = downloadingDocumentGuid == document.guid
                val isAnyDownloading = downloadingDocumentGuid != null

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(CornerRadius.cardCompact))
                        .clickable(enabled = !isAnyDownloading) {
                            onDownloadDocument(document, resolvedTitle)
                        }
                        .padding(vertical = Spacing.sm),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(Spacing.lg)
                        )
                        Spacer(modifier = Modifier.width(Spacing.sm))
                        TaminText(
                            text = resolvedTitle,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = taminColors.textPrimary
                        )
                    }

                    if (isDownloading) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(Spacing.lg)
                        )
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            TaminText(
                                text = stringResource(UserRequestRes.string.user_request_document_view_action),
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(Spacing.xs))
                            Icon(
                                imageVector = Icons.Default.RemoveRedEye,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(Spacing.lg)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DocumentPreviewDialog(
    preview: DocumentPreview,
    onDismiss: () -> Unit,
) {
    val taminColors = LocalTaminColors.current

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(CornerRadius.cardCompact),
            colors = CardDefaults.cardColors(containerColor = taminColors.bgSurface),
            elevation = CardDefaults.cardElevation(defaultElevation = Elevation.md)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.page),
                verticalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                TaminText(
                    text = preview.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = taminColors.textPrimary
                )

                LoadAsyncImage(
                    model = preview.imageData,
                    contentDescription = preview.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(CornerRadius.cardCompact))
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(CornerRadius.cardCompact))
                        .clickable(onClick = onDismiss)
                        .padding(vertical = Spacing.sm),
                    horizontalArrangement = Arrangement.Center
                ) {
                    TaminText(
                        text = stringResource(UserRequestRes.string.user_request_document_preview_close),
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@com.tamin.taminhamrah.ui.PreviewRtlTheme
@Composable
private fun UserRequestDetailScreenPreview() {
    com.tamin.taminhamrah.ui.PreviewRtlThemeContent {
        UserRequestDetailScreen(
            requestId = 491371155L,
            refCode = "۱۰۷۵۵۵۸۴۴۰",
            requestTypeId = REQUEST_TYPE_DEFERRED_INSTALLMENT_CERTIFICATE,
            onBackClick = {},
            title = "کسر اقساط معوق",
            isLoading = false,
            request = UserRequestPR(
                id = 491371155L,
                refCode = "۱۰۷۵۵۵۸۴۴۰",
                title = "گواهی کسر اقساط معوق",
                comment = "",
                creationTime = "۱۴۰۴/۱۲/۱۸",
                createByName = "سیدرحمت اله میرفضلی",
                statusDesc = "تایید نهایی",
                statusCode = "0018",
                requestTypeId = REQUEST_TYPE_DEFERRED_INSTALLMENT_CERTIFICATE,
                requestTypeTitle = "گواهی کسر اقساط معوق",
            )
        )
    }
}
