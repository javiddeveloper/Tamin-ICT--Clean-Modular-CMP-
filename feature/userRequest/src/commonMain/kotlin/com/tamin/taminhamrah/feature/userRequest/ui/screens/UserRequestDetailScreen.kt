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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.components.AnimatedRingHeaderIcon
import com.tamin.taminhamrah.ui.components.DecorativeBackgroundCircle
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.feature.userrequest.generated.resources.Res as UserRequestRes
import taminx.feature.userrequest.generated.resources.*
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_request
import taminx.core.core_ui.ic_tamin_chevron_back

@Composable
fun UserRequestDetailScreen(
    requestId: Long,
    refCode: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    title: String
) {
    val taminColors = LocalTaminColors.current
    val profileGradientBrush = remember(taminColors.profileGradientStops) {
        Brush.horizontalGradient(taminColors.profileGradientStops)
    }
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            // Blue Header Arc
//            item {
//                Box(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
//                        .background(Color(0xFF173D7E))
//                        .padding(horizontal = Spacing.page, vertical = Spacing.lg)
//                ) {
//                    Column(
//                        modifier = Modifier.fillMaxWidth(),
//                        horizontalAlignment = Alignment.CenterHorizontally,
//                        verticalArrangement = Arrangement.spacedBy(Spacing.md)
//                    ) {
//                        Row(
//                            modifier = Modifier.fillMaxWidth(),
//                            horizontalArrangement = Arrangement.SpaceBetween,
//                            verticalAlignment = Alignment.CenterVertically
//                        ) {
//                            Box(
//                                modifier = Modifier
//                                    .size(36.dp)
//                                    .clip(CircleShape)
//                                    .background(Color.White.copy(alpha = 0.2f))
//                                    .clickable { onBackClick() },
//                                contentAlignment = Alignment.Center
//                            ) {
//                                Icon(
//                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
//                                    contentDescription = "Back",
//                                    tint = Color.White,
//                                    modifier = Modifier.size(20.dp)
//                                )
//                            }
//
//                            TaminText(
//                                text = "نمایش درخواست کسر اقساط",
//                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
//                                color = Color.White
//                            )
//
//                            Spacer(modifier = Modifier.size(36.dp))
//                        }
//
//                        // Circular Document Icon
//                        Box(
//                            modifier = Modifier
//                                .size(64.dp)
//                                .clip(CircleShape)
//                                .background(Color.White.copy(alpha = 0.15f))
//                                .border(2.dp, Color.White.copy(alpha = 0.3f), CircleShape),
//                            contentAlignment = Alignment.Center
//                        ) {
//                            Icon(
//                                imageVector = Icons.Default.Description,
//                                contentDescription = null,
//                                tint = Color.White,
//                                modifier = Modifier.size(32.dp)
//                            )
//                        }
//                    }
//                }
//            }

            // Section 1: Hero Summary Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.page),
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
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFFEFF6FF))
                                    .padding(horizontal = Spacing.md, vertical = Spacing.xs)
                            ) {
                                TaminText(
                                    text = stringResource(UserRequestRes.string.user_request_detail_bank_example),
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color(0xFF1F4FA3)
                                )
                            }

                            TaminText(
                                text = stringResource(UserRequestRes.string.user_request_detail_guarantee_amount_label),
                                style = MaterialTheme.typography.labelSmall,
                                color = LocalTaminColors.current.textTertiary
                            )
                        }

                        TaminText(
                            text = stringResource(UserRequestRes.string.user_request_detail_guarantee_amount_value),
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                            color = LocalTaminColors.current.textPrimary,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.End
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                        ) {
                            MetricPill(stringResource(UserRequestRes.string.user_request_detail_repayment_label), stringResource(UserRequestRes.string.user_request_detail_placeholder_dash), Modifier.weight(1f))
                            MetricPill(stringResource(UserRequestRes.string.user_request_detail_installment_count_label), stringResource(UserRequestRes.string.user_request_detail_installment_count_value), Modifier.weight(1f))
                            MetricPill(stringResource(UserRequestRes.string.user_request_detail_installment_amount_label), stringResource(UserRequestRes.string.user_request_detail_installment_amount_value), Modifier.weight(1.2f))
                        }
                    }
                }
            }

            // Section 2: Guarantor Info Card
            item {
                DetailSectionCard(
                    title = stringResource(UserRequestRes.string.user_request_detail_pensioner_guarantor),
                    items = listOf(
                        stringResource(UserRequestRes.string.user_request_detail_full_name) to stringResource(UserRequestRes.string.user_request_detail_sample_name),
                        stringResource(UserRequestRes.string.user_request_detail_national_id) to stringResource(UserRequestRes.string.user_request_detail_sample_national_id),
                        stringResource(UserRequestRes.string.user_request_detail_pension_number) to stringResource(UserRequestRes.string.user_request_detail_sample_pension_num)
                    ),
                    modifier = Modifier.padding(horizontal = Spacing.page)
                )
            }

            // Section 3: Borrower Info Card
            item {
                DetailSectionCard(
                    title = stringResource(UserRequestRes.string.user_request_detail_borrower),
                    items = listOf(
                        stringResource(UserRequestRes.string.user_request_detail_full_name) to stringResource(UserRequestRes.string.user_request_detail_placeholder_dash),
                        stringResource(UserRequestRes.string.user_request_detail_national_id) to stringResource(UserRequestRes.string.user_request_detail_sample_national_id),
                        stringResource(UserRequestRes.string.user_request_detail_birth_date) to stringResource(UserRequestRes.string.user_request_detail_placeholder_dash)
                    ),
                    modifier = Modifier.padding(horizontal = Spacing.page)
                )
            }

            // Section 4: Loan Details Card
            item {
                DetailSectionCard(
                    title = stringResource(UserRequestRes.string.user_request_detail_loan_details),
                    items = listOf(
                        stringResource(UserRequestRes.string.user_request_detail_bank_institution) to stringResource(UserRequestRes.string.user_request_detail_bank_example),
                        stringResource(UserRequestRes.string.user_request_detail_branch_name) to stringResource(UserRequestRes.string.user_request_detail_branch_example),
                        stringResource(UserRequestRes.string.user_request_detail_installment_amount_label) to stringResource(UserRequestRes.string.user_request_detail_installment_amount_with_unit),
                        stringResource(UserRequestRes.string.user_request_detail_installment_count_label) to stringResource(UserRequestRes.string.user_request_detail_installment_count_value),
                        stringResource(UserRequestRes.string.user_request_detail_repayment_amount) to stringResource(UserRequestRes.string.user_request_detail_repayment_zero_rial),
                        stringResource(UserRequestRes.string.user_request_detail_guarantee_amount_label) to stringResource(UserRequestRes.string.user_request_detail_guarantee_amount_value)
                    ),
                    modifier = Modifier.padding(horizontal = Spacing.page)
                )
            }

            item {
                Spacer(modifier = Modifier.height(Spacing.xl))
            }
        }
    }
}

@Composable
private fun MetricPill(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(LocalTaminColors.current.bgPage)
            .padding(Spacing.sm),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            TaminText(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = LocalTaminColors.current.textTertiary
            )
            TaminText(
                text = value,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = LocalTaminColors.current.textPrimary
            )
        }
    }
}

@Composable
private fun DetailSectionCard(
    title: String,
    items: List<Pair<String, String>>,
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

            items.forEach { (label, value) ->
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
                        color = if (label == "مبلغ بازپرداخت") Color(0xFF16A34A) else if (label == "مبلغ ضمانت") Color(0xFFD97706) else LocalTaminColors.current.textPrimary
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
            onBackClick = {},
            title = "کسر اقساط معوق"
        )
    }
}

