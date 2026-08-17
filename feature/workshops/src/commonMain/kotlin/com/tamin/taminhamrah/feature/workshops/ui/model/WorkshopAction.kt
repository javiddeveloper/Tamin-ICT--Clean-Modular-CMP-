package com.tamin.taminhamrah.feature.workshops.ui.model

import org.jetbrains.compose.resources.StringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.workshop_action_article16
import taminx.core.core_ui.workshop_action_debit_turnover
import taminx.core.core_ui.workshop_action_debt_inquiry
import taminx.core.core_ui.workshop_action_members
import taminx.core.core_ui.workshop_action_new_member
import taminx.core.core_ui.workshop_action_objection
import taminx.core.core_ui.workshop_action_payment_sheets
import taminx.core.core_ui.workshop_action_stackholders

/**
 * The eight services a picked کارگاه can be taken to, in the order the menu lists them.
 *
 * Declaration order *is* the menu order — one table, so a label and its position cannot drift
 * apart the way two parallel lists do. Every action is offered for every workshop regardless of
 * its activity status, which is how the service behaves.
 */
enum class WorkshopAction(val label: StringResource) {
    PAYMENT_SHEETS(Res.string.workshop_action_payment_sheets),
    DEBIT_TURNOVER(Res.string.workshop_action_debit_turnover),
    DEBT_INQUIRY(Res.string.workshop_action_debt_inquiry),
    OBJECTION(Res.string.workshop_action_objection),
    NEW_MEMBER(Res.string.workshop_action_new_member),

    /**
     * The one action that is not a plain navigation: the debts are fetched first, and a workshop
     * with none is told so instead of being taken to an empty screen.
     */
    ARTICLE16(Res.string.workshop_action_article16),
    MEMBERS(Res.string.workshop_action_members),
    STACKHOLDERS(Res.string.workshop_action_stackholders),
}
