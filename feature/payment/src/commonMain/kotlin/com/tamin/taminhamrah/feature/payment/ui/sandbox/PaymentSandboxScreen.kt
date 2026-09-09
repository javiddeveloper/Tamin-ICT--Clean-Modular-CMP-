package com.tamin.taminhamrah.feature.payment.ui.sandbox

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.tamin.taminhamrah.model.payment.PaymentMockScenario
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness
import com.tamin.taminhamrah.ui.toPriceFormat
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_tamin_chevron_back
import taminx.core.core_ui.payment_currency_unit
import taminx.core.core_ui.payment_sandbox_description
import taminx.core.core_ui.payment_sandbox_scenario_construction_premium
import taminx.core.core_ui.payment_sandbox_scenario_construction_workers
import taminx.core.core_ui.payment_sandbox_scenario_debt_installment
import taminx.core.core_ui.payment_sandbox_scenario_freelance
import taminx.core.core_ui.payment_sandbox_scenario_optional
import taminx.core.core_ui.payment_sandbox_scenario_student
import taminx.core.core_ui.payment_sandbox_scenario_unconfirmed
import taminx.core.core_ui.payment_sandbox_scenario_workshop_debt
import taminx.core.core_ui.payment_sandbox_title

/**
 * Debug-only catalogue of every payment the app has to be able to make, each runnable against the
 * mock gateway.
 *
 * It exists because the features that will issue these tickets are still being ported: without it
 * the shared payment flow could only be exercised by whichever feature happened to be finished
 * first, and the outcomes that are hardest to get right — a refusal, an expiry, money taken
 * without the service confirming — could not be reached at all.
 *
 * Every run gets a fresh ticket. A gateway treats a ticket as spent once it has been through it,
 * so reusing one previews as already-paid the second time round.
 */
@Composable
fun PaymentSandboxScreen(
    onStartPayment: (PaymentMockScenario) -> Unit,
    onNavigateBack: () -> Unit,
) {
    val colors = LocalTaminColors.current

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = colors.bgPage,
        topBar = {
            TaminTopAppBar(
                title = stringResource(Res.string.payment_sandbox_title),
                navigationIcon = {
                    TaminTopAppBarButton(
                        icon = vectorResource(Res.drawable.ic_tamin_chevron_back),
                        contentDescription = null,
                        onClick = onNavigateBack,
                        bordered = true,
                    )
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.page, vertical = Spacing.lg)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            TaminText(
                text = stringResource(Res.string.payment_sandbox_description),
                style = MaterialTheme.typography.bodySmall,
                color = colors.textSecondary,
                modifier = Modifier.padding(bottom = Spacing.sm),
            )

            PaymentMockScenario.entries.forEach { scenario ->
                ScenarioRow(scenario = scenario, onClick = { onStartPayment(scenario) })
            }
        }
    }
}

@Composable
private fun ScenarioRow(scenario: PaymentMockScenario, onClick: () -> Unit) {
    val colors = LocalTaminColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(CornerRadius.listRow))
            .background(colors.bgSurface)
            .border(
                width = Thickness.border,
                color = colors.border,
                shape = RoundedCornerShape(CornerRadius.listRow),
            )
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.lg, vertical = Spacing.md),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.xxs)) {
            TaminText(
                text = scenario.label(),
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textPrimary,
            )
            TaminText(
                text = "${scenario.mockAmount.toPriceFormat()} " +
                    stringResource(Res.string.payment_currency_unit),
                style = MaterialTheme.typography.bodySmall,
                color = colors.textSecondary,
            )
        }
    }
}

@Composable
private fun PaymentMockScenario.label(): String = when (this) {
    PaymentMockScenario.WORKSHOP_DEBT ->
        stringResource(Res.string.payment_sandbox_scenario_workshop_debt)

    PaymentMockScenario.DEBT_INSTALLMENT ->
        stringResource(Res.string.payment_sandbox_scenario_debt_installment)

    PaymentMockScenario.CONSTRUCTION_PREMIUM ->
        stringResource(Res.string.payment_sandbox_scenario_construction_premium)

    PaymentMockScenario.CONSTRUCTION_WORKERS ->
        stringResource(Res.string.payment_sandbox_scenario_construction_workers)

    PaymentMockScenario.FREELANCE_INSURANCE ->
        stringResource(Res.string.payment_sandbox_scenario_freelance)

    PaymentMockScenario.OPTIONAL_INSURANCE ->
        stringResource(Res.string.payment_sandbox_scenario_optional)

    PaymentMockScenario.STUDENT_INSURANCE ->
        stringResource(Res.string.payment_sandbox_scenario_student)

    PaymentMockScenario.PAID_BUT_UNCONFIRMED ->
        stringResource(Res.string.payment_sandbox_scenario_unconfirmed)
}
