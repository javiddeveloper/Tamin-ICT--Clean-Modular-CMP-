package com.tamin.taminhamrah.feature.taminServices.constructionInsurance.ui.components

import androidx.compose.runtime.Composable
import com.tamin.taminhamrah.model.constructionInsurance.ConstructionFilePR
import com.tamin.taminhamrah.ui.ActionMenuItem
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.action_menu_beneficiaries
import taminx.core.core_ui.action_menu_installment
import taminx.core.core_ui.action_menu_payment_sheet
import taminx.core.core_ui.ic_tamin_calendar
import taminx.core.core_ui.ic_tamin_user
import taminx.core.core_ui.ic_tamin_workshop_payment

/**
 * The 2-3 عملیات options for one پرونده row, mirroring the old app's `getCashListAction(isInstallment)`
 * minus «نمایش جزییات درخواست» — that's already its own button on [ConstructionFileCard], not
 * repeated inside the menu. «صدور و مدیریت برگه پرداخت» and «مدیریت پرداخت اقساط» stay mutually
 * exclusive based on [ConstructionFilePR.debitStatusCode], exactly like the old app's per-row menu.
 */
enum class ConstructionInsuranceAction {
    PaymentSheet,
    InstallmentLetter,
    Beneficiaries,
}

private const val INSTALLMENT_DEBIT_STATUS_CODE = "51"

@Composable
fun constructionInsuranceActions(item: ConstructionFilePR): ImmutableList<ActionMenuItem<ConstructionInsuranceAction>> {
    val isInstallment = item.debitStatusCode == INSTALLMENT_DEBIT_STATUS_CODE
    return persistentListOf(
        if (isInstallment) {
            ActionMenuItem(
                hasDivider = true,
                value = ConstructionInsuranceAction.InstallmentLetter,
                label = stringResource(Res.string.action_menu_installment),
                icon = Res.drawable.ic_tamin_calendar,
            )
        } else {
            ActionMenuItem(
                hasDivider = true,
                value = ConstructionInsuranceAction.PaymentSheet,
                label = stringResource(Res.string.action_menu_payment_sheet),
                icon = Res.drawable.ic_tamin_workshop_payment,
            )
        },
        ActionMenuItem(
            hasDivider = true,
            value = ConstructionInsuranceAction.Beneficiaries,
            label = stringResource(Res.string.action_menu_beneficiaries),
            icon = Res.drawable.ic_tamin_user,
        ),
    )
}
