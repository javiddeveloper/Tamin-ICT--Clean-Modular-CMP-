package com.tamin.taminhamrah.feature.workshops.ui.model

import com.tamin.taminhamrah.feature.workshops.ui.components.StatusTint
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

/**
 * The services a picked کارگاه can be taken to, in the order the menu lists them.
 *
 * Declaration order *is* the menu order, and each row's label, description, glyph and tone are
 * columns of this one table — so a label and its icon cannot drift apart the way parallel lists
 * do. Every action is offered for every workshop regardless of its activity status, which is how
 * the service behaves.
 *
 * Empty for now: a row belongs here only once the screen it opens exists. The `when` in
 * `WorkshopAction.route()` is exhaustive, so the compiler refuses an action with nowhere to go —
 * which is the codebase saying a menu row and its destination are one change, and why each
 * service arrives as its own task rather than a row that quietly does nothing.
 */
enum class WorkshopAction(
    val label: StringResource,
    val description: StringResource,
    val icon: DrawableResource,
    val tint: StatusTint,
)
