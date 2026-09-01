package com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.model.workshop.WorkshopContractRowPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.DetailRow
import com.tamin.taminhamrah.ui.components.StatusPill
import com.tamin.taminhamrah.ui.components.coloredShadow
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.employer_online_services_contract_row_chip
import taminx.core.core_ui.employer_online_services_contract_row_email
import taminx.core.core_ui.employer_online_services_contract_row_landline
import taminx.core.core_ui.employer_online_services_contract_row_mobile
import taminx.core.core_ui.employer_online_services_contract_row_national_code
import taminx.core.core_ui.employer_online_services_contract_row_period
import taminx.core.core_ui.employer_online_services_contract_row_period_value
import taminx.core.core_ui.employer_online_services_contract_row_ticket_code

/**
 * One پیمانکار / contract row of a workshop: a «ردیف NNN» pill + the person's name, then the
 * national code / بلیت / contact / contract-period readings. All values arrive pre-formatted on
 * [WorkshopContractRowPR].
 */
@Composable
internal fun EmployerContractRowCard(
    item: WorkshopContractRowPR,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .coloredShadow(
                color = colors.shadowSubtle,
                borderRadius = CornerRadius.card,
                blurRadius = 26.dp,
                offsetY = 10.dp,
            )
            .taminSurface()
            .padding(horizontal = Spacing.xlg)
            .padding(top = Spacing.lg, bottom = Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = Spacing.xs),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            StatusPill(
                text = stringResource(
                    Res.string.employer_online_services_contract_row_chip,
                    item.contractRow,
                ),
                containerColor = colors.chipBg,
                contentColor = colors.blueText,
                borderColor = colors.border,
            )
            Spacer(Modifier.width(Spacing.sm))
            Text(
                text = item.fullName,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
        }

        DetailRow(
            label = stringResource(Res.string.employer_online_services_contract_row_national_code),
            value = item.nationalCode,
        )
        DetailRow(
            label = stringResource(Res.string.employer_online_services_contract_row_ticket_code),
            value = item.postalCode,
        )
        DetailRow(
            label = stringResource(Res.string.employer_online_services_contract_row_email),
            value = item.email,
            numeric = false,
        )
        DetailRow(
            label = stringResource(Res.string.employer_online_services_contract_row_mobile),
            value = item.mobile,
        )
        DetailRow(
            label = stringResource(Res.string.employer_online_services_contract_row_landline),
            value = item.tel,
        )
        DetailRow(
            label = stringResource(Res.string.employer_online_services_contract_row_period),
            value = stringResource(
                Res.string.employer_online_services_contract_row_period_value,
                item.startDate,
                item.endDate,
            ),
            numeric = false,
        )
    }
}

private val PreviewRow = WorkshopContractRowPR(
    contractRow = "۰۰۱",
    fullName = "حسین توکلی کرمانی",
    nationalCode = "۴۴۷۹۸۹۰۸۸۲",
    mobile = "۰۹۱۵۳۲۱۴۴۷۸",
    email = "info@damabokhar.ir",
    tel = "۰۵۱۳۷۶۵۴۳۲۱",
    postalCode = "۸۴۵۲۱",
    startDate = "۱۴۰۳/۰۵/۱۹",
    endDate = "۱۴۰۵/۰۵/۱۸",
    workshopName = "شرکت صنایع دما بخار مشهد",
    workshopCodeLabel = "۰۰۸۱۶۳۱۸۲۹",
)

@PreviewRtlTheme
@Composable
private fun EmployerContractRowCardPreviewLight() {
    PreviewRtlThemeContent {
        EmployerContractRowCard(item = PreviewRow, modifier = Modifier.padding(Spacing.lg))
    }
}

@PreviewRtlTheme
@Composable
private fun EmployerContractRowCardPreviewDark() {
    PreviewRtlThemeContent(darkTheme = true) {
        EmployerContractRowCard(
            item = PreviewRow.copy(contractRow = "۰۰۲", fullName = "مریم توکلی", email = "-"),
            modifier = Modifier.padding(Spacing.lg),
        )
    }
}
