package com.tamin.taminhamrah.feature.agent.service.impl

import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceParams
import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceResult
import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceUseCase
import com.tamin.taminhamrah.feature.agent.service.base.ChatBubbleContent
import com.tamin.taminhamrah.feature.agent.service.base.buildBubbles
import com.tamin.taminhamrah.feature.agent.service.base.toKeyValueRows
import com.tamin.taminhamrah.model.agent.AgentActionKey
import com.tamin.taminhamrah.model.health.PatientGeneralDN
import com.tamin.taminhamrah.ui.orDash
import com.tamin.taminhamrah.useCases.agent.GetCurrentUserNationalCodeUseCase
import com.tamin.taminhamrah.useCases.health.GetPatientGeneralUseCase
import kotlinx.coroutines.flow.firstOrNull

/**
 * Displays the patient's health record summary — سوابق بیمار.
 *
 * Ported from old_Android's PatientHistoryUseCase. Fetches [PatientGeneralDN]
 * using the current user's national code and renders key-value rows.
 */
class PatientHistoryAgentService(
    private val getPatientGeneralUseCase: GetPatientGeneralUseCase,
    private val getCurrentUserNationalCodeUseCase: GetCurrentUserNationalCodeUseCase,
) : AgentServiceUseCase {

    override val supportedKeys: List<AgentActionKey> = listOf(
        AgentActionKey.PATIENT_HISTORY,
        AgentActionKey.PATIENT_HISTORY_LAST
    )

    override suspend fun execute(params: AgentServiceParams): AgentServiceResult {
        return try {
            val nationalCode = getCurrentUserNationalCodeUseCase()
                ?: return AgentServiceResult.Error("کد ملی کاربر یافت نشد.")

            val patient = getPatientGeneralUseCase(nationalCode).firstOrNull()
                ?: return AgentServiceResult.Success(
                    params.buildBubbles { add(ChatBubbleContent.Text("اطلاعات بیمار یافت نشد.")) }
                )

            val rows = patient.toRows()

            AgentServiceResult.Success(
                params.buildBubbles {
                    add(
                        ChatBubbleContent.KeyValue(
                            title = params.message?.takeIf { it.isNotBlank() } ?: "اطلاعات بیمار",
                            items = rows.toKeyValueRows()
                        )
                    )
                }
            )
        } catch (e: Exception) {
            AgentServiceResult.Error("خطا در دریافت اطلاعات بیمار: ${e.message}", e)
        }
    }

    private fun PatientGeneralDN.toRows(): List<Pair<String, String>> = buildList {
        add("نام" to "${patientName.orDash()} ${patientFamily.orDash()}")
        patientNatCode?.takeIf { it.isNotBlank() }?.let { add("کد ملی" to it) }
        patientAge?.takeIf { it.isNotBlank() }?.let { add("سن" to it) }
        patientGender?.takeIf { it.isNotBlank() }?.let { add("جنسیت" to it) }
        patientBirthDate?.takeIf { it.isNotBlank() }?.let { add("تاریخ تولد" to it) }
        patientMobile?.takeIf { it.isNotBlank() }?.let { add("موبایل" to it) }
        patientAddress?.takeIf { it.isNotBlank() }?.let { add("آدرس" to it) }
    }
}
