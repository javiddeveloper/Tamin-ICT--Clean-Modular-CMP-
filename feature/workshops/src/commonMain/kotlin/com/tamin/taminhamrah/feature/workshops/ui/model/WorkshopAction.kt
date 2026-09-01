package com.tamin.taminhamrah.feature.workshops.ui.model

import com.tamin.taminhamrah.feature.workshops.ui.components.StatusTint
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_tamin_workshop_payment
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
) {
    PAYMENT_SHEETS(
        label = Res.string.workshop_action_payment_sheets,
        description = Res.string.workshop_action_payment_sheets_desc,
        icon = Res.drawable.ic_tamin_workshop_payment,
        tint = StatusTint.INFO,
    ),
}
