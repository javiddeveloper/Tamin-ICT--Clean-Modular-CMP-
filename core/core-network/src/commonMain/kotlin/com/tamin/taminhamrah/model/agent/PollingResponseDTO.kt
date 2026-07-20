package com.tamin.taminhamrah.model.agent

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * پاسخ polling از سرور
 *
 * اولین پاسخ معمولاً PENDING است و [data.eta] ثانیه بعد باید track شود.
 * وقتی status == DONE، [data.result] حاوی پاسخ کامل است.
 */
@Serializable
data class PollingResponseDTO(
    @SerialName("status") val status: Int? = null,
    @SerialName("family") val family: String? = null,
    @SerialName("reason") val reason: String? = null,
    @SerialName("data") val data: PollingDataDTO? = null
)

@Serializable
data class PollingDataDTO(
    /** شناسه یکتای درخواست - برای track کردن استفاده می‌شود */
    @SerialName("id") val id: String? = null,
    /** زمان تخمینی پاسخ به ثانیه */
    @SerialName("eta") val eta: Int? = null,
    /** وضعیت: PENDING | DONE | FAILED | CANCEL */
    @SerialName("status") val status: String? = null,
    /** پاسخ نهایی - فقط وقتی status == DONE مقدار دارد */
    @SerialName("result") val result: AgentResponseDTO? = null
)
