package com.tamin.taminhamrah.feature.agent.service.impl.prescription

import com.tamin.taminhamrah.feature.agent.service.base.AgentDate
import com.tamin.taminhamrah.feature.agent.service.base.AgentDateRange
import com.tamin.taminhamrah.feature.agent.service.base.AgentStrings
import com.tamin.taminhamrah.model.treatment.ElectronicPrescriptionDN
import com.tamin.taminhamrah.useCases.treatment.GetElectronicPrescriptionListUseCase
import com.tamin.taminhamrah.util.PersianDateFormatter
import kotlinx.coroutines.flow.firstOrNull
import taminx.core.core_ui.Res
import taminx.core.core_ui.agent_label_doctor
import taminx.core.core_ui.agent_label_insurance_number
import taminx.core.core_ui.agent_label_prescription_type
import taminx.core.core_ui.agent_label_specialty
import taminx.core.core_ui.agent_label_tracking_code
import taminx.core.core_ui.agent_label_visit_date
import kotlinx.datetime.Clock

/**
 * The electronic prescription list as the native assistant queried it: the user's own
 * prescriptions (request type 1, no dependant), filtered by an epoch-millis window.
 */
internal class PrescriptionQuery(
    private val getElectronicPrescriptionListUseCase: GetElectronicPrescriptionListUseCase,
) {

    /**
     * The native window rules: no dates → the last 7 days; only an end → the 7 days before it;
     * only a start → from it until now; both → exactly that range.
     */
    suspend fun inRange(nationalCode: String, range: AgentDateRange): List<ElectronicPrescriptionDN> {
        val now = nowMillis()
        val end = range.end?.toMillis() ?: now
        val start = range.start?.toMillis() ?: (end - WEEK_MILLIS)
        return fetch(nationalCode, start, end)
    }

    /** Month-by-month search backwards from today, stopping at the first month with prescriptions. */
    suspend fun latestByMonth(nationalCode: String): List<ElectronicPrescriptionDN> {
        var end = nowMillis()
        repeat(MAX_MONTHS_BACK) {
            val start = end - MONTH_MILLIS
            val list = fetch(nationalCode, start, end)
            if (list.isNotEmpty()) return list
            end = start
        }
        return emptyList()
    }

    private suspend fun fetch(nationalCode: String, start: Long, end: Long): List<ElectronicPrescriptionDN> =
        getElectronicPrescriptionListUseCase(
            requestTypeId = OWN_REQUEST_TYPE,
            nationalCode = nationalCode,
            patientNationalCode = NO_DEPENDANT,
            startDate = start.toString(),
            endDate = end.toString(),
        ).firstOrNull().orEmpty()

    private fun AgentDate.toMillis(): Long = PersianDateFormatter.toEpochMillis(year, month ?: 1, day ?: 1)

    private fun nowMillis(): Long = Clock.System.now().toEpochMilliseconds()

    companion object {
        const val OWN_REQUEST_TYPE = "1"
        const val NO_DEPENDANT = "0"
        private const val WEEK_MILLIS = 7L * 24 * 60 * 60 * 1000
        private const val MONTH_MILLIS = 30L * 24 * 60 * 60 * 1000
        /** The native search had no bound and could loop forever; a year is where it stops now. */
        private const val MAX_MONTHS_BACK = 12
    }
}

/** The native assistant's prescription rows. */
internal suspend fun ElectronicPrescriptionDN.rows(strings: AgentStrings): List<Pair<String, String?>> = listOf(
    strings.get(Res.string.agent_label_tracking_code) to trackingCode?.toString(),
    strings.get(Res.string.agent_label_visit_date) to prescDate?.toLongOrNull()?.let(PersianDateFormatter::formatTimestamp),
    strings.get(Res.string.agent_label_doctor) to docName,
    strings.get(Res.string.agent_label_specialty) to specDesc,
    strings.get(Res.string.agent_label_insurance_number) to patientID,
    strings.get(Res.string.agent_label_prescription_type) to prescName,
)
