package com.tamin.taminhamrah.feature.agent.service.impl

import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceParams
import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceResult
import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceUseCase
import com.tamin.taminhamrah.feature.agent.service.base.AgentStrings
import com.tamin.taminhamrah.feature.agent.service.base.ChatBubbleContent
import com.tamin.taminhamrah.feature.agent.service.base.agentMarkdown
import com.tamin.taminhamrah.model.agent.AgentActionKey
import com.tamin.taminhamrah.model.common.GenderCodeDN
import com.tamin.taminhamrah.useCases.identity.IdentityInfoUseCase
import com.tamin.taminhamrah.useCases.personal.GetPersonalInfoUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.firstOrNull
import taminx.core.core_ui.Res
import taminx.core.core_ui.agent_error_profile
import taminx.core.core_ui.agent_label_birth_city
import taminx.core.core_ui.agent_label_branch
import taminx.core.core_ui.agent_label_father_name
import taminx.core.core_ui.agent_label_first_name
import taminx.core.core_ui.agent_label_gender
import taminx.core.core_ui.agent_label_id_card_number
import taminx.core.core_ui.agent_label_id_card_serial
import taminx.core.core_ui.agent_label_id_card_series
import taminx.core.core_ui.agent_label_issue_city
import taminx.core.core_ui.agent_label_last_name
import taminx.core.core_ui.agent_label_mobile
import taminx.core.core_ui.agent_label_national_code
import taminx.core.core_ui.agent_label_nationality
import taminx.core.core_ui.agent_label_social_security_number
import taminx.core.core_ui.agent_value_female
import taminx.core.core_ui.agent_value_foreign
import taminx.core.core_ui.agent_value_iranian
import taminx.core.core_ui.agent_value_male

/** The user's identity summary — اطلاعات هویتی — ported from the native `ProfileUseCase`. */
class ProfileAgentService(
    private val identityInfoUseCase: IdentityInfoUseCase,
    private val getPersonalInfoUseCase: GetPersonalInfoUseCase,
    private val strings: AgentStrings,
) : AgentServiceUseCase {

    override val supportedKeys: List<AgentActionKey> = listOf(AgentActionKey.PROFILE_INFO)

    override suspend fun execute(params: AgentServiceParams): AgentServiceResult = try {
        val identity = identityInfoUseCase().firstOrNull()
        if (identity == null) {
            AgentServiceResult.Error(strings.get(Res.string.agent_error_profile))
        } else {
            val personal = getPersonalInfoUseCase().firstOrNull()
            val gender = when (GenderCodeDN.fromCode(identity.gender)) {
                GenderCodeDN.MALE -> strings.get(Res.string.agent_value_male)
                GenderCodeDN.FEMALE -> strings.get(Res.string.agent_value_female)
                null -> null
            }
            val nationality = identity.countryId?.let {
                strings.get(if (it == IRAN_COUNTRY_CODE) Res.string.agent_value_iranian else Res.string.agent_value_foreign)
            }
            val rows = listOf(
                strings.get(Res.string.agent_label_first_name) to identity.firstName,
                strings.get(Res.string.agent_label_last_name) to identity.lastName,
                strings.get(Res.string.agent_label_father_name) to identity.fatherName,
                strings.get(Res.string.agent_label_national_code) to identity.nationalId,
                strings.get(Res.string.agent_label_social_security_number) to (personal?.insuranceId ?: identity.ssn),
                strings.get(Res.string.agent_label_mobile) to personal?.mobileNumber,
                strings.get(Res.string.agent_label_gender) to gender,
                strings.get(Res.string.agent_label_id_card_number) to identity.idCardNumber,
                strings.get(Res.string.agent_label_id_card_series) to identity.idCardSerial1,
                strings.get(Res.string.agent_label_id_card_serial) to identity.idCardSerial2,
                strings.get(Res.string.agent_label_nationality) to nationality,
                strings.get(Res.string.agent_label_birth_city) to identity.cityOfBirthName,
                strings.get(Res.string.agent_label_issue_city) to identity.cityOfIssueName,
                strings.get(Res.string.agent_label_branch) to personal?.branch,
            )
            AgentServiceResult.Success(
                listOf(ChatBubbleContent.Markdown(agentMarkdown { heading(params.message); fields(rows) }))
            )
        }
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        AgentServiceResult.Error(strings.get(Res.string.agent_error_profile), e)
    }

    private companion object {
        const val IRAN_COUNTRY_CODE = "0001"
    }
}
