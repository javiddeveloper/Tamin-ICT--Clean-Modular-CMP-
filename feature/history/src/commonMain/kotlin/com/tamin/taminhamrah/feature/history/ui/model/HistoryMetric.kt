package com.tamin.taminhamrah.feature.history.ui.model

import org.jetbrains.compose.resources.StringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.history_metric_both
import taminx.core.core_ui.history_metric_days
import taminx.core.core_ui.history_metric_wage

/**
 * Which series the chart plots — the شاخص control above it.
 *
 * Declaration order is the order the chips sit in, and each chip's label is a column of this one
 * table, so a chip cannot end up labeled for one series while selecting another.
 *
 * [BOTH] is the default because the two series answer different questions about the same year —
 * how much was earned, and how long was worked — and reading one without the other is what makes a
 * short year look like a bad one.
 */
enum class HistoryMetric(val label: StringResource) {
    BOTH(Res.string.history_metric_both),
    WAGE(Res.string.history_metric_wage),
    DAYS(Res.string.history_metric_days);

    /** Whether the دستمزد series is drawn. */
    val showsWage: Boolean get() = this != DAYS

    /** Whether the روزهای کار series is drawn. */
    val showsDays: Boolean get() = this != WAGE
}
