package com.tamin.taminhamrah.feature.taminServices.constructionInsurance.installmentManagement.ui.components

import androidx.compose.runtime.Composable
import com.tamin.taminhamrah.ui.ActionMenuItem
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.action_menu_installment_debit_list
import taminx.core.core_ui.action_menu_installment_management
import taminx.core.core_ui.ic_tamin_computational_base
import taminx.core.core_ui.ic_tamin_document_lines

/**
 * The 2 عملیات options for one تقسیط‌نامه row — mirroring the old app's
 * `InstallmentLetterFragment.showDialog()` (`getInstallmentListAction()`), the deeper flow
 * `InstallmentLetterContract.kt` originally left out of scope.
 */
enum class InstallmentLetterAction {
    ManagementAndPaymentSheet,
    DebitList,
}

@Composable
fun installmentLetterActions(): ImmutableList<ActionMenuItem<InstallmentLetterAction>> = persistentListOf(
    ActionMenuItem(
        hasDivider = true,
        value = InstallmentLetterAction.ManagementAndPaymentSheet,
        label = stringResource(Res.string.action_menu_installment_management),
        icon = Res.drawable.ic_tamin_document_lines,
    ),
    ActionMenuItem(
        value = InstallmentLetterAction.DebitList,
        label = stringResource(Res.string.action_menu_installment_debit_list),
        icon = Res.drawable.ic_tamin_computational_base,
    ),
)
