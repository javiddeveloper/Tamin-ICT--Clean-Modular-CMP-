package com.tamin.taminhamrah.feature.treatment.ui.model

import com.tamin.taminhamrah.util.getBeginningTimestamp
import com.tamin.taminhamrah.util.getOneMonthAgoTimestamp
import com.tamin.taminhamrah.util.getOneYearAgoTimestamp
import com.tamin.taminhamrah.util.getSixMonthsAgoTimestamp
import org.jetbrains.compose.resources.StringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.period_all_time
import taminx.core.core_ui.period_custom
import taminx.core.core_ui.period_last_month
import taminx.core.core_ui.period_last_six_months
import taminx.core.core_ui.period_last_year

/**
 * Ranges offered by the date filter.
 */
enum class RecordPeriod(val label: StringResource) {
    LAST_MONTH(Res.string.period_last_month),
    LAST_SIX_MONTHS(Res.string.period_last_six_months),
    LAST_YEAR(Res.string.period_last_year),
    ALL_TIME(Res.string.period_all_time),

    /**
     * A from/to range the person picks themselves.
     *
     * [startTimestamp] falls back to the widest range so the list is never empty before a range is
     * chosen; the picked bounds are passed to `LoadList` explicitly. The old app refused to search
     * with an incomplete range ("error_message_select_date"), so the picker must supply both ends.
     */
    CUSTOM(Res.string.period_custom);

    /** Start bound in epoch millis, as the patient-history endpoint expects. */
    fun startTimestamp(): String = when (this) {
        LAST_MONTH -> getOneMonthAgoTimestamp()
        LAST_SIX_MONTHS -> getSixMonthsAgoTimestamp()
        LAST_YEAR -> getOneYearAgoTimestamp()
        ALL_TIME, CUSTOM -> getBeginningTimestamp()
    }
}
