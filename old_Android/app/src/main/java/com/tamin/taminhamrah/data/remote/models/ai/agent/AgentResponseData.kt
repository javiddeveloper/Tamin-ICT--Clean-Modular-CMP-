package com.tamin.taminhamrah.data.remote.models.ai.agent

import com.google.gson.annotations.SerializedName
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.data.repository.ai.model.AgentActionContent
import com.tamin.taminhamrah.data.repository.ai.model.KeyValueModel
import com.tamin.taminhamrah.ui.aiAgent.domain.AgentLaw
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceData
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceResponse
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceNameEnum
import kotlinx.serialization.Serializable

abstract class AgentResponseData

data class PromptModel(
    @SerializedName("prompt") val prompt: String?,
    @SerializedName("action_type") val actionType: String? = null
) : AgentResponseData() {

    fun toDomain(deepLinkTransform: ((String) -> String)? = null): com.tamin.taminhamrah.data.repository.ai.model.PromptModel {
        val safePrompt = prompt.orEmpty()
        val action = when {
            actionType.equals("DIAL", ignoreCase = true) ->
                AgentActionContent.Dial(safePrompt, safePrompt)

            actionType.equals("open_support_dial", ignoreCase = true) ->
                AgentActionContent.Dial("1420", safePrompt)

            actionType.equals("LINK", ignoreCase = true) ->
                AgentActionContent.Web(safePrompt, safePrompt)

            actionType.equals("open_support_link", ignoreCase = true) ->
                AgentActionContent.Web(Constants.LINK_1420, safePrompt)

            actionType.equals("edit_mobile", ignoreCase = true) ||
                    actionType.equals("open_deeplink", ignoreCase = true) ->
                AgentActionContent.EditMobile(safePrompt)

            actionType.equals("add_account_number", ignoreCase = true) ->
                AgentActionContent.AddAccountNumber(safePrompt)

            actionType.equals("cancel_dependent", ignoreCase = true) ->
                AgentActionContent.CancelDependent(safePrompt)

            actionType.equals("local_deep_link", ignoreCase = true) -> {
                val finalUri = deepLinkTransform?.invoke(safePrompt) ?: safePrompt
                AgentActionContent.LocalDeepLink(finalUri, safePrompt)
            }
            actionType.equals("local_deeplink", ignoreCase = true) -> {
                val finalUri = deepLinkTransform?.invoke(safePrompt) ?: safePrompt
                AgentActionContent.LocalDeepLink(finalUri, safePrompt)
            }

            else -> AgentActionContent.SendPrompt(safePrompt, safePrompt)
        }
        return com.tamin.taminhamrah.data.repository.ai.model.PromptModel(safePrompt, action)
    }
}

data class MessageModel(
    @SerializedName("message") val message: String,
) : AgentResponseData()

data class AppointmentModel(
    @SerializedName("NAME") val name: String?,
    @SerializedName("PROFICIENCY") val proficiency: String?,
    @SerializedName("CITY") val city: String?,
    @SerializedName("ADDRESS") val address: String?,
    @SerializedName("CENTER") val center: String?,
    @SerializedName("TITLE") val title: String?,
    @SerializedName("URL") val url: String?,
    @SerializedName("MATCH_PERCENTAGE") val matchPercentage: Double?,
) : AgentResponseData()

data class DeeplinkDataModel(
    @SerializedName("action_type") val actionType: String?,
    @SerializedName("deeplink") val deepLink: Deeplink?,
    @SerializedName("title") val title: String?,
) : AgentResponseData() {

    fun toAgentActionContent(deepLinkTransform: ((String) -> String)? = null): com.tamin.taminhamrah.data.repository.ai.model.PromptModel {
        val safeTitle = title.orEmpty()
        val target = deepLink?.to?.trim()?.lowercase()

        val action = when (target) {
            "cancel_dependent" -> AgentActionContent.CancelDependent(safeTitle)
            "add_dependent" -> createLocalAction(Constants.ADD_DEPENDENT_DEEPLINK, safeTitle, deepLinkTransform)
            "objection_non_existent_history" -> createLocalAction(Constants.OBJECTION_INSURANCE_NON_EXISTENCE_DEEPLINK, safeTitle, deepLinkTransform)
            "objection_insurance_history" -> createLocalAction(Constants.OBJECTION_INSURANCE_HISTORY, safeTitle, deepLinkTransform)
            "issuance_wage_certificate" -> createLocalAction(Constants.ISSUANCE_WAGE_CERTIFICATE, safeTitle, deepLinkTransform)
            "contract_freelance" -> createLocalAction("mytamin://contract_freelance", safeTitle, deepLinkTransform)
            "contract_optional" -> createLocalAction("mytamin://contract_optional", safeTitle, deepLinkTransform)
            "contract_woman" -> createLocalAction("mytamin://contract_woman", safeTitle, deepLinkTransform)
            "contract_student" -> createLocalAction("mytamin://contract_student", safeTitle, deepLinkTransform)
            else -> AgentActionContent.SendPrompt(safeTitle, safeTitle)
        }

        return com.tamin.taminhamrah.data.repository.ai.model.PromptModel(safeTitle, action)
    }

    private fun createLocalAction(uri: String, actionText: String, transform: ((String) -> String)?): AgentActionContent.LocalDeepLink {
        val finalUri = transform?.invoke(uri) ?: uri
        return AgentActionContent.LocalDeepLink(finalUri, actionText)
    }
}

data class AgentLawDto(
    @SerializedName("url") val url: String,
    @SerializedName("content") val content: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("reference") val reference: String?,
    @SerializedName("score") val score: Double?,
    @SerializedName("source_file") val sourceFile: String?,
) : AgentResponseData() {
    fun toDomain() = AgentLaw(
        url = url,
        content = content,
        name = name,
        reference = reference,
        score = score,
        sourceFile = sourceFile,
    )
}

fun AgentResponseData.toServiceResponse(
    serviceName: ServiceNameEnum,
    message: String?,
    deepLinkTransform: ((String) -> String)? = null
): ServiceResponse {
    return when (this) {
        is AppointmentModel -> ServiceResponse(
            action = serviceName,
            title = this.title,
            data = ServiceData.Clickable(
                message = createAppointmentKeyValue(this),
                actionType = AgentActionContent.Web(
                    url = this.url ?: "",
                    actionText = "سایت نوبت دهی"
                )
            )
        )

        is DeeplinkDataModel -> ServiceResponse(
            action = serviceName,
            title = null,
            data = ServiceData.GroupButton(
                prompts = listOf(this.toAgentActionContent(deepLinkTransform)),
                actionType = AgentActionContent.SendPrompt(this.title.orEmpty(), this.title.orEmpty())
            )
        )

        is PromptModel -> this.toActionTypeModel(serviceName, message, deepLinkTransform)

        is MessageModel -> ServiceResponse(
            action = serviceName,
            title = this.message,
            data = ServiceData.HeaderMessage
        )

        is AgentLawDto -> ServiceResponse(
            action = serviceName,
            title = this.content,
            data = ServiceData.Law(this.toDomain())
        )

        else -> ServiceResponse(
            action = serviceName,
            title = message,
            data = ServiceData.StringMessage("موردی یافت نشد!")
        )
    }
}

private fun PromptModel.toActionTypeModel(
    serviceName: ServiceNameEnum,
    message: String?,
    deepLinkTransform: ((String) -> String)? = null
): ServiceResponse {
    val domainPrompt = this.toDomain(deepLinkTransform)
    val safePrompt = prompt.orEmpty()

    return when (this.actionType) {
        "send_prompt" -> ServiceResponse(
            action = serviceName,
            title = message,
            data = ServiceData.GroupButton(
                prompts = listOf(domainPrompt),
                actionType = AgentActionContent.SendPrompt(safePrompt, safePrompt)
            )
        )

        "edit_mobile", "open_deeplink" -> ServiceResponse(
            action = serviceName,
            title = message,
            data = ServiceData.GroupButton(
                prompts = listOf(domainPrompt),
                actionType = AgentActionContent.EditMobile(safePrompt)
            )
        )

        "add_account_number" -> ServiceResponse(
            action = serviceName,
            title = message,
            data = ServiceData.GroupButton(
                prompts = listOf(domainPrompt),
                actionType = AgentActionContent.AddAccountNumber(safePrompt)
            )
        )

        "cancel_dependent" -> ServiceResponse(
            action = serviceName,
            title = message,
            data = ServiceData.GroupButton(
                prompts = listOf(domainPrompt),
                actionType = AgentActionContent.CancelDependent(safePrompt)
            )
        )

        "open_support_link" -> ServiceResponse(
            action = serviceName,
            title = message,
            data = ServiceData.GroupButton(
                prompts = listOf(domainPrompt),
                actionType = AgentActionContent.Web(
                    url = Constants.LINK_1420,
                    actionText = safePrompt
                )
            )
        )

        "open_support_dial" -> ServiceResponse(
            action = serviceName,
            title = message,
            data = ServiceData.GroupButton(
                prompts = listOf(domainPrompt),
                actionType = AgentActionContent.Dial(
                    phoneNumber = "1420",
                    actionText = safePrompt
                )
            )
        )

        else -> ServiceResponse(
            action = serviceName,
            title = message,
            data = ServiceData.StringMessage("موردی یافت نشد!")
        )
    }
}

fun createAppointmentKeyValue(appointmentModel: AppointmentModel): List<KeyValueModel> {
    return listOf(
        KeyValueModel(_key = "نام", _value = appointmentModel.name ?: "-"),
        KeyValueModel(_key = "تخصص", _value = appointmentModel.proficiency ?: "-"),
        KeyValueModel(_key = "شهر", _value = appointmentModel.city ?: "-"),
        KeyValueModel(_key = "آدرس", _value = appointmentModel.address ?: "-"),
        KeyValueModel(_key = "مرکز", _value = appointmentModel.center ?: "-"),
        KeyValueModel(
            _key = "امتیاز جستجو",
            _value = (appointmentModel.matchPercentage ?: 0.0).toString()
        )
    )
}

@Serializable
data class Deeplink(
    @SerializedName("to") val to: String,
)

fun List<ServiceResponse>.collectAllPromptToList(): List<ServiceResponse> {
    val groupResponses = filter { it.data is ServiceData.GroupButton }
    if (groupResponses.size <= 1) return this

    val mergedPrompts = groupResponses
        .map { it.data as ServiceData.GroupButton }
        .flatMap { it.prompts }

    val mergedContent = groupResponses
        .mapNotNull { (it.data as ServiceData.GroupButton).content }
        .flatten()
        .takeIf { it.isNotEmpty() }

    val firstGroupIndex = indexOfFirst { it.data is ServiceData.GroupButton }
    val firstGroup = groupResponses.first()
    val firstGroupData = firstGroup.data as ServiceData.GroupButton

    val mergedGroup = firstGroup.copy(
        data = ServiceData.GroupButton(
            prompts = mergedPrompts,
            actionType = firstGroupData.actionType,
            content = mergedContent
        )
    )

    return buildList {
        this@collectAllPromptToList.forEachIndexed { index, item ->
            when {
                index == firstGroupIndex -> add(mergedGroup)
                item.data is ServiceData.GroupButton -> Unit
                else -> add(item)
            }
        }
    }
}
