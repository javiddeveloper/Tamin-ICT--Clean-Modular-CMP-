package com.tamin.taminhamrah.feature.treatment.ui.model

import com.tamin.taminhamrah.util.getBeginningTimestamp
import com.tamin.taminhamrah.util.getOneMonthAgoTimestamp
import com.tamin.taminhamrah.util.getOneYearAgoTimestamp
import com.tamin.taminhamrah.util.getSixMonthsAgoTimestamp

/**
 * Ranges offered by the date filter.
 */
enum class RecordPeriod(val label: String) {
    LAST_MONTH("۱ ماه اخیر"),
    LAST_SIX_MONTHS("۶ ماه اخیر"),
    LAST_YEAR("۱ سال اخیر"),
    ALL_TIME("از ابتدا"),

    /**
     * A from/to range the person picks themselves.
     *
     * [startTimestamp] falls back to the widest range so the list is never empty before a range is
     * chosen; the picked bounds are passed to `LoadList` explicitly. The old app refused to search
     * with an incomplete range ("error_message_select_date"), so the picker must supply both ends.
     */
    CUSTOM("تاریخ دلخواه");

    /** Start bound in epoch millis, as the patient-history endpoint expects. */
    fun startTimestamp(): String = when (this) {
        LAST_MONTH -> getOneMonthAgoTimestamp()
        LAST_SIX_MONTHS -> getSixMonthsAgoTimestamp()
        LAST_YEAR -> getOneYearAgoTimestamp()
        ALL_TIME, CUSTOM -> getBeginningTimestamp()
    }
}
