package com.tamin.taminhamrah.feature.workshops.ui.model

import com.tamin.taminhamrah.feature.workshops.ui.components.StatusTint
import com.tamin.taminhamrah.model.common.FeatureFlag
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.assigner_contracts_action_desc
import taminx.core.core_ui.assigner_contracts_title
import taminx.core.core_ui.contract_rows_action_desc
import taminx.core.core_ui.contract_rows_title
import taminx.core.core_ui.ic_tamin_assigner_contracts
import taminx.core.core_ui.ic_tamin_workshop_contract_rows
import taminx.core.core_ui.ic_tamin_workshop_inquiry
import taminx.core.core_ui.ic_tamin_workshop_new_member
import taminx.core.core_ui.ic_tamin_workshop_payment
import taminx.core.core_ui.ic_tamin_workshop_objection
import taminx.core.core_ui.ic_tamin_workshop_turnover
import taminx.core.core_ui.workshop_action_debit_turnover
import taminx.core.core_ui.workshop_action_debit_turnover_desc
import taminx.core.core_ui.workshop_action_debt_inquiry
import taminx.core.core_ui.workshop_action_debt_inquiry_desc
import taminx.core.core_ui.workshop_action_new_member
import taminx.core.core_ui.workshop_action_new_member_desc
import taminx.core.core_ui.workshop_action_objection
import taminx.core.core_ui.workshop_action_objection_desc
import taminx.core.core_ui.workshop_action_payment_sheets
import taminx.core.core_ui.workshop_action_payment_sheets_desc

/**
 * The services a picked کارگاه can be taken to, in the order the menu lists them.
 *
 * Declaration order *is* the menu order, and each row's label, description, glyph and tone are
 * columns of this one table — so a label and its icon cannot drift apart the way parallel lists
 * do. Every action is offered for every workshop regardless of its activity status, which is how
 * the service behaves.
 *
 * A row belongs here only once the screen it opens exists: the `when` in `WorkshopAction.route()`
 * is exhaustive, so the compiler refuses an action with nowhere to go.
 */
enum class WorkshopAction(
    val label: StringResource,
    val description: StringResource,
    val icon: DrawableResource,
    val tint: StatusTint,
    /**
     * The server-side switch that hides this service, or null when it has none.
     *
     * A service reachable from the services grid is reachable from here too, so the flag that turns
     * it off there has to turn it off here — otherwise disabling it only closes one of two doors.
     */
    val featureFlag: FeatureFlag? = null,
) {
    PAYMENT_SHEETS(
        label = Res.string.workshop_action_payment_sheets,
        description = Res.string.workshop_action_payment_sheets_desc,
        icon = Res.drawable.ic_tamin_workshop_payment,
        tint = StatusTint.INFO,
    ),
    DEBIT_TURNOVER(
        label = Res.string.workshop_action_debit_turnover,
        description = Res.string.workshop_action_debit_turnover_desc,
        icon = Res.drawable.ic_tamin_workshop_turnover,
        tint = StatusTint.TEAL,
    ),
    DEBT_INQUIRY(
        label = Res.string.workshop_action_debt_inquiry,
        description = Res.string.workshop_action_debt_inquiry_desc,
        icon = Res.drawable.ic_tamin_workshop_inquiry,
        tint = StatusTint.MINT,
    ),
    OBJECTION(
        label = Res.string.workshop_action_objection,
        description = Res.string.workshop_action_objection_desc,
        icon = Res.drawable.ic_tamin_workshop_objection,
        tint = StatusTint.WARNING,
    ),
    NEW_MEMBER(
        label = Res.string.workshop_action_new_member,
        description = Res.string.workshop_action_new_member_desc,
        icon = Res.drawable.ic_tamin_workshop_new_member,
        tint = StatusTint.INFO,
    ),

    CONTRACT_ROWS(
        label = Res.string.contract_rows_title,
        description = Res.string.contract_rows_action_desc,
        icon = Res.drawable.ic_tamin_workshop_contract_rows,
        tint = StatusTint.INFO,
        // «اطلاعات پیمان» in the server menu — the same flag the services-grid tile routes through.
        featureFlag = FeatureFlag.CONTRACT_INFO,
    ),

    ASSIGNER_CONTRACTS(
        label = Res.string.assigner_contracts_title,
        description = Res.string.assigner_contracts_action_desc,
        icon = Res.drawable.ic_tamin_assigner_contracts,
        tint = StatusTint.MINT,
        // 1003 in the server menu, under the same name the screen carries.
        featureFlag = FeatureFlag.ASSIGNER_CONTRACT,
    ),
}
