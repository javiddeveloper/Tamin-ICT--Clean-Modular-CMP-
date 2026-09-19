package com.tamin.taminhamrah.model.agent

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * آیتم «دکمه‌ی دیپ‌لینک» داخل آرایه‌ی `data` یک entity، مثل
 * `{"item_type":"deeplink","deeplink":{"to":"contract_freelance"},"title":"مشاغل آزاد"}`.
 */
@Serializable
data class DeepLinkItemDTO(
    @SerialName("item_type") val itemType: String? = null,
    @SerialName("action_type") val actionType: String? = null,
    @SerialName("deeplink") val deeplink: DeepLinkTargetDTO? = null,
    @SerialName("title") val title: String? = null,
)

@Serializable
data class DeepLinkTargetDTO(
    @SerialName("to") val to: String? = null,
)
