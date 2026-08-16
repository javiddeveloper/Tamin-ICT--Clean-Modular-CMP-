package com.tamin.taminhamrah.feature.userRequest.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import com.tamin.taminhamrah.feature.userRequest.ui.screens.contract.UserRequestDetailEvent
import com.tamin.taminhamrah.feature.userRequest.ui.screens.contract.UserRequestDetailIntent
import com.tamin.taminhamrah.model.userRequest.UserRequestPR
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.AnimatedRingHeaderIcon
import com.tamin.taminhamrah.ui.components.DecorativeBackgroundCircle
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.toPriceFormat
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel
import taminx.feature.userrequest.generated.resources.Res as UserRequestRes
import taminx.feature.userrequest.generated.resources.*
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_tamin_chevron_back

// Sentinel color constants used to tag specific detail rows for semantic coloring
private val COLOR_REPAYMENT_AMOUNT = Color(0xFF16A34A)
private val COLOR_GUARANTEE_AMOUNT = Color(0xFFD97706)

// Request type ID constants — mirror of RequestTypeEnumClass in legacy my-tamin-droid
private const val REQUEST_TYPE_ILL_DAY = 10L
private const val REQUEST_TYPE_ORTHOTICS_PROSTHESIS = 12L
private const val REQUEST_TYPE_FOLLOW_UP_OBJECTION = 8L
private const val REQUEST_TYPE_ARTICLE16 = 26L
private const val REQUEST_TYPE_DEFERRED_INSTALLMENT_CERTIFICATE = 22L

// ── Route ────────────────────────────────────────────────────────────────────

@Composable
fun UserRequestDetailRoute(
    requestId: Long,
    refCode: String,
    requestTypeId: Long,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    title: String,
    viewModel: UserRequestDetailViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(requestId) {
        viewModel.sendIntent(UserRequestDetailIntent.LoadDetail(requestId))
    }

    HandleUserRequestDetailEvents(
        events = viewModel.events,
        onBackClick = onBackClick,
    )

    UserRequestDetailScreen(
        requestId = requestId,
        refCode = refCode,
        requestTypeId = requestTypeId,
        title = title,
        isLoading = state.isLoading,
        request = state.request,
        error = state.error,
        onBackClick = onBackClick,
        modifier = modifier,
    )
}

@Composable
fun HandleUserRequestDetailEvents(
    events: Flow<UserRequestDetailEvent>,
    onBackClick: () -> Unit,
) {
    events.collectWithLifecycleAware { event ->
        when (event) {
            is UserRequestDetailEvent.NavigateBack -> onBackClick()
            is UserRequestDetailEvent.ShowToast -> { /* handled by caller if needed */ }
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
) {
    val taminColors = LocalTaminColors.current
    val profileGradientBrush = remember(taminColors.profileGradientStops) {
        Brush.horizontalGradient(taminColors.profileGradientStops)
    }

    val details = request?.details

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = LocalTaminColors.current.bgPage,
        topBar = {
            TaminTopAppBar(
                title = title,
                background = profileGradientBrush,
                bottomPadding = Spacing.xl,
                shape = RoundedCornerShape(
                    bottomStart = 40.dp,
                    bottomEnd = 40.dp
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
                        Spacer(modifier = Modifier.height(Spacing.md))
                        Text(
                            text = stringResource(UserRequestRes.string.user_request_header_subtitle),
                            style = MaterialTheme.typography.labelLarge,
                            color = taminColors.textHeaderSubtitle,
                            textAlign = TextAlign.Center
                        )
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
                    CircularProgressIndicator(color = Color(0xFF1F4FA3))
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
                        color = LocalTaminColors.current.textTertiary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = Spacing.page)
                    )
                }
            }

            // ── Content ──────────────────────────────────────────────────────
            else -> {
                val placeholder = stringResource(UserRequestRes.string.user_request_detail_placeholder_dash)

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    verticalArrangement = Arrangement.spacedBy(Spacing.md)
                ) {
                    // Section 1: Common request summary card
                    item {
                        RequestSummaryCard(
                            refCode = request?.refCode ?: refCode,
                            title = request?.title ?: title,
                            statusDesc = request?.statusDesc ?: "",
                            creationTime = request?.creationTime ?: "",
                            modifier = Modifier.padding(horizontal = Spacing.page)
                        )
                    }

                    // Section 2+: Type-specific content sections
                    when (requestTypeId) {
                        REQUEST_TYPE_DEFERRED_INSTALLMENT_CERTIFICATE -> {
                            val deferredDetails = details?.deferredInstallment

                            // 1. Guarantor / Pensioner Card (Always shown when available)
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

                            if (deferredDetails != null) {
                                // 2. Borrower Card
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

                                // 3. Loan Details Card
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
                                                deferredDetails.repaymentAmount?.toPriceFormat()?.let { "$it ریال" } ?: placeholder,
                                                COLOR_REPAYMENT_AMOUNT
                                            ),
                                            Triple(
                                                stringResource(UserRequestRes.string.user_request_detail_installment_count_label),
                                                deferredDetails.installmentCount?.toString() ?: placeholder,
                                                null
                                            ),
                                            Triple(
                                                stringResource(UserRequestRes.string.user_request_detail_installment_amount_label),
                                                deferredDetails.installmentAmount?.toPriceFormat()?.let { "$it ریال" } ?: placeholder,
                                                null
                                            ),
                                            Triple(
                                                stringResource(UserRequestRes.string.user_request_detail_guarantee_amount_label),
                                                deferredDetails.guaranteeAmount?.toPriceFormat()?.let { "$it ریال" } ?: placeholder,
                                                COLOR_GUARANTEE_AMOUNT
                                            )
                                        ),
                                        modifier = Modifier.padding(horizontal = Spacing.page)
                                    )
                                }
                            }
                        }

                        REQUEST_TYPE_ILL_DAY, REQUEST_TYPE_ORTHOTICS_PROSTHESIS -> {
                            val illDetails = details?.illDay

                            // Guarantor Card
                            item {
                                DetailSectionCard(
                                    title = stringResource(UserRequestRes.string.user_request_detail_pensioner_guarantor),
                                    items = listOf(
                                        Triple(
                                            stringResource(UserRequestRes.string.user_request_detail_full_name),
                                            request?.createByName ?: placeholder,
                                            null
                                        ),
                                    ),
                                    modifier = Modifier.padding(horizontal = Spacing.page)
                                )
                            }

                            if (illDetails != null) {
                                item {
                                    DetailSectionCard(
                                        title = stringResource(UserRequestRes.string.user_request_detail_loan_details),
                                        items = listOf(
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
                                                stringResource(UserRequestRes.string.user_request_detail_employer_name),
                                                illDetails.employerName ?: placeholder,
                                                null
                                            ),
                                            Triple(
                                                stringResource(UserRequestRes.string.user_request_detail_amount),
                                                illDetails.amount?.toPriceFormat()?.let { "$it ریال" } ?: placeholder,
                                                COLOR_REPAYMENT_AMOUNT
                                            )
                                        ),
                                        modifier = Modifier.padding(horizontal = Spacing.page)
                                    )
                                }
                            }
                        }

                        REQUEST_TYPE_ARTICLE16 -> {
                            val article16Details = details?.article16

                            // Guarantor Card
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

                            if (article16Details != null) {
                                item {
                                    DetailSectionCard(
                                        title = stringResource(UserRequestRes.string.user_request_detail_loan_details),
                                        items = listOf(
                                            Triple(
                                                stringResource(UserRequestRes.string.user_request_detail_article16_date),
                                                article16Details.meetingDate ?: placeholder,
                                                null
                                            ),
                                            Triple(
                                                stringResource(UserRequestRes.string.user_request_detail_article16_result),
                                                article16Details.result ?: placeholder,
                                                null
                                            )
                                        ),
                                        modifier = Modifier.padding(horizontal = Spacing.page)
                                    )
                                }
                            }
                        }

                        REQUEST_TYPE_FOLLOW_UP_OBJECTION -> {
                            val objectionDetails = details?.followUpObjection

                            // Guarantor Card
                            item {
                                DetailSectionCard(
                                    title = stringResource(UserRequestRes.string.user_request_detail_pensioner_guarantor),
                                    items = listOf(
                                        Triple(
                                            stringResource(UserRequestRes.string.user_request_detail_full_name),
                                            request?.createByName ?: placeholder,
                                            null
                                        ),
                                    ),
                                    modifier = Modifier.padding(horizontal = Spacing.page)
                                )
                            }

                            if (objectionDetails != null) {
                                item {
                                    DetailSectionCard(
                                        title = stringResource(UserRequestRes.string.user_request_detail_loan_details),
                                        items = listOf(
                                            Triple(
                                                stringResource(UserRequestRes.string.user_request_detail_objection_date),
                                                objectionDetails.objectionDate ?: placeholder,
                                                null
                                            ),
                                            Triple(
                                                stringResource(UserRequestRes.string.user_request_detail_objection_reason),
                                                objectionDetails.reason ?: placeholder,
                                                null
                                            )
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

                    item {
                        Spacer(modifier = Modifier.height(Spacing.xl))
                    }
                }
            }
        }
    }
}

// ── Components ────────────────────────────────────────────────────────────────

@Composable
private fun RequestSummaryCard(
    refCode: String,
    title: String,
    statusDesc: String,
    creationTime: String,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = LocalTaminColors.current.bgSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                    color = LocalTaminColors.current.textPrimary
                )
                if (statusDesc.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFEFF6FF))
                            .padding(horizontal = Spacing.md, vertical = Spacing.xs)
                    ) {
                        TaminText(
                            text = statusDesc,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFF1F4FA3)
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
                        color = LocalTaminColors.current.textTertiary
                    )
                    TaminText(
                        text = refCode,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = LocalTaminColors.current.textPrimary
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
                        color = LocalTaminColors.current.textTertiary
                    )
                    TaminText(
                        text = creationTime,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = LocalTaminColors.current.textPrimary
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
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = LocalTaminColors.current.bgSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1F4FA3))
                )
                Spacer(modifier = Modifier.size(Spacing.xs))
                TaminText(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = LocalTaminColors.current.textPrimary
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
                        color = LocalTaminColors.current.textTertiary
                    )
                    TaminText(
                        text = value,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = valueColor ?: LocalTaminColors.current.textPrimary
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
