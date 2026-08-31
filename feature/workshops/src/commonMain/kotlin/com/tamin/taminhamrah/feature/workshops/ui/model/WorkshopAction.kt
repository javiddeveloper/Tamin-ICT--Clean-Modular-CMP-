package com.tamin.taminhamrah.feature.workshops.ui.model

import com.tamin.taminhamrah.feature.workshops.ui.components.StatusTint
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_tamin_workshop_article_sixteen
import taminx.core.core_ui.ic_tamin_workshop_inquiry
import taminx.core.core_ui.ic_tamin_workshop_members
import taminx.core.core_ui.ic_tamin_workshop_new_member
import taminx.core.core_ui.ic_tamin_workshop_objection
import taminx.core.core_ui.ic_tamin_workshop_payment
import taminx.core.core_ui.ic_tamin_workshop_stackholders
import taminx.core.core_ui.ic_tamin_workshop_turnover
import taminx.core.core_ui.workshop_action_article_sixteen
import taminx.core.core_ui.workshop_action_article_sixteen_desc
import taminx.core.core_ui.workshop_action_debit_turnover
import taminx.core.core_ui.workshop_action_debit_turnover_desc
import taminx.core.core_ui.workshop_action_debt_inquiry
import taminx.core.core_ui.workshop_action_debt_inquiry_desc
import taminx.core.core_ui.workshop_action_members
import taminx.core.core_ui.workshop_action_members_desc
import taminx.core.core_ui.workshop_action_new_member
import taminx.core.core_ui.workshop_action_new_member_desc
import taminx.core.core_ui.workshop_action_objection
import taminx.core.core_ui.workshop_action_objection_desc
import taminx.core.core_ui.workshop_action_payment_sheets
import taminx.core.core_ui.workshop_action_payment_sheets_desc
import taminx.core.core_ui.workshop_action_stackholders
import taminx.core.core_ui.workshop_action_stackholders_desc

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
    ARTICLE_SIXTEEN(
        label = Res.string.workshop_action_article_sixteen,
        description = Res.string.workshop_action_article_sixteen_desc,
        icon = Res.drawable.ic_tamin_workshop_article_sixteen,
        tint = StatusTint.PURPLE,
    ),
    MEMBERS(
        label = Res.string.workshop_action_members,
        description = Res.string.workshop_action_members_desc,
        icon = Res.drawable.ic_tamin_workshop_members,
        tint = StatusTint.TEAL,
    ),
    STACKHOLDERS(
        label = Res.string.workshop_action_stackholders,
        description = Res.string.workshop_action_stackholders_desc,
        icon = Res.drawable.ic_tamin_workshop_stackholders,
        tint = StatusTint.WARNING,
    ),
}
