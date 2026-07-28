package com.tamin.taminhamrah.feature.agent.service.impl

import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceParams
import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceResult
import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceUseCase
import com.tamin.taminhamrah.feature.agent.service.base.ChatBubbleContent
import com.tamin.taminhamrah.feature.agent.service.base.buildBubbles
import com.tamin.taminhamrah.feature.agent.service.base.orDash
import com.tamin.taminhamrah.model.agent.AgentActionKey
import com.tamin.taminhamrah.useCases.identity.IdentityInfoUseCase
import com.tamin.taminhamrah.useCases.personal.GetPersonalInfoUseCase
import kotlinx.coroutines.flow.firstOrNull

/**
 * Shows the user's identity/profile summary in the chat.
 *
 * Ported from old_Android's `ProfileUseCase`, which combined identity info with
 * personal info and resolved the birth/issue city names. In the new architecture
 * [IdentityInfoUseCase] already resolves those city names internally.
 */
class ProfileAgentService(
    private val identityInfoUseCase: IdentityInfoUseCase,
    private val getPersonalInfoUseCase: GetPersonalInfoUseCase
) : AgentServiceUseCase {

    override val supportedKeys: List<AgentActionKey> = listOf(AgentActionKey.PROFILE_INFO)

    override suspend fun execute(params: AgentServiceParams): AgentServiceResult {
        return try {
            val identity = identityInfoUseCase().firstOrNull()
            val personal = getPersonalInfoUseCase().firstOrNull()

            if (identity == null) {
                return AgentServiceResult.Success(
                    listOf(ChatBubbleContent.Text("متاسفانه مشکلی در دریافت اطلاعات پیش آمده است!"))
                )
            }

            val rows = listOf(
                "نام" to identity.firstName.orDash(),
                "نام خانوادگی" to identity.lastName.orDash(),
                "نام پدر" to identity.fatherName.orDash(),
                "کد ملی" to identity.nationalId.orDash(),
                "شماره تامین اجتماعی" to (personal?.insuranceId ?: identity.ssn).orDash(),
                "شماره تلفن همراه" to personal?.mobileNumber.orDash(),
                "جنسیت" to if (identity.gender == MALE_GENDER_CODE) "مرد" else "زن",
                "شماره شناسنامه" to identity.idCardNumber.orDash(),
                "سری شناسنامه" to identity.idCardSerial1.orDash(),
                "سریال شناسنامه" to identity.idCardSerial2.orDash(),
                "ملیت" to if (identity.countryId == IRAN_COUNTRY_CODE) "ایرانی" else "غیر ایرانی",
                "شهر محل تولد" to identity.cityOfBirthName.orDash(),
                "شهر محل صدور" to identity.cityOfIssueName.orDash(),
                "شعبه" to personal?.branch.orDash()
            )

            AgentServiceResult.Success(
                params.buildBubbles {
                    add(
                        ChatBubbleContent.KeyValue(
                            title = params.message?.takeIf { it.isNotBlank() } ?: "مشخصات هویتی",
                            items = rows
                        )
                    )
                }
            )
        } catch (e: Exception) {
            AgentServiceResult.Error("خطا در دریافت اطلاعات هویتی: ${e.message}", e)
        }
    }

    private companion object {
        const val MALE_GENDER_CODE = "01"
        const val IRAN_COUNTRY_CODE = "0001"
    }
}
