package com.tamin.taminhamrah.model.agent

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * مدلی برای استخراج دیتای "پرامپت‌های پیشنهادی" (Suggested Prompts)
 * که ممکن است داخل آرایه [data] در هر اکشنی از سمت هوش مصنوعی بازگردانده شود.
 */
@Serializable
data class PromptItemDTO(
    @SerialName("item_type") val itemType: String? = null,
    @SerialName("action_type") val actionType: String? = null,
    @SerialName("prompt") val prompt: String? = null
)
