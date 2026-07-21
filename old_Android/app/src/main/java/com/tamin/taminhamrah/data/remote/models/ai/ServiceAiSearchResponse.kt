/*
*
* @author: Javid Sattar 
* @email: javiddeveloper@gmail.com
*
*/

package com.tamin.taminhamrah.data.remote.models.ai

import com.google.gson.annotations.SerializedName
import com.tamin.taminhamrah.data.repository.ai.model.AIClickType
import com.tamin.taminhamrah.data.repository.ai.model.AiChatModel
import com.tamin.taminhamrah.data.repository.ai.model.ClickableItemModel
import com.tamin.taminhamrah.utils.extentions.randomUUID

data class AiServiceResponse(
    @SerializedName("message") val message: String?,
    @SerializedName("sessionId") val sessionId: String?,
    @SerializedName("lastEntity") val lastEntity: String?,
    @SerializedName("results") val results: List<ServiceModelResponse?>?
)

data class ServiceModelResponse(

    @SerializedName("message") val message: String?,
    @SerializedName("url") val serviceUrl: String?,
    @SerializedName("data") val data: List<SchedulePatient>? = null,
)

fun AiServiceResponse.asDomainModel() = AiServiceModel(
    message = message,
    sessionId = sessionId,
    lastEntity = lastEntity,
    results = results?.map { it?.asDomainModel() },
)

fun ServiceModelResponse.asDomainModel() = ServiceModel(
    message = message,
    serviceUrl = serviceUrl,
    aiServiceModelType = AiServiceModelType.fromUrl(serviceUrl),
    data = data
)

data class AiServiceModel(
    val message: String?,
    val sessionId: String?,
    val lastEntity: String?,
    val results: List<ServiceModel?>?
)

data class ServiceModel(
    var message: String?,
    var serviceUrl: String?,
    val aiServiceModelType: AiServiceModelType?,
    val data: List<SchedulePatient>? = null,
)


enum class AiServiceModelType {
    LAST_PAY,
    DASTMOZDINFOS,
    PayrollData,
    Booklet,
    ElectronicPrescription,
    LastTrackingCode,
    SchedulePatient,
    PensionerEdict,         // Hokm
    JobTitles,              // Job Titles
    AverageWage,            // Average Wage
    SICK_PAY,               // Wage compensation for sickness days
    OTHER;

    companion object {
        fun fromUrl(url: String?): AiServiceModelType {
            return url?.let {
                when {
                    it.contains("dastmozdinfos-last-pay") -> LAST_PAY
                    it.contains("dastmozdinfos") -> DASTMOZDINFOS
                    it.contains("fish") -> PayrollData
                    it.contains("booklet-req/deserve") -> Booklet
                    it.contains("last-tracking-code") -> LastTrackingCode
                    it.contains("patient-history") -> ElectronicPrescription
                    it.contains("sick-pay-compensation") -> SICK_PAY
                    else -> OTHER
                }
            } ?: SchedulePatient
        }

        fun fromKey(key: String?): AiServiceModelType {
            val safeKey = key ?: return OTHER
            return when (safeKey) {
                "fish" -> PayrollData
                "booklet-req" -> Booklet
                "patient-history" -> ElectronicPrescription
                "last-tracking-code" -> LastTrackingCode
                "history-services", "dastmozdinfos" -> DASTMOZDINFOS
                "job-titles", "historyjobinfos" -> JobTitles
                "edict", "hokm" -> PensionerEdict
                "average-wage" -> AverageWage
                "sick-pay" -> SICK_PAY
                "appoinmet" -> SchedulePatient
                else -> OTHER
            }
        }
    }


}

data class SchedulePatient(
    @SerializedName("NAME") val doctorName: String,
    @SerializedName("PROFICIENCY") val proficiency: String,
    @SerializedName("CITY") val city: String,
    @SerializedName("ADDRESS") val address: String,
    @SerializedName("CENTER") val center: String,
    @SerializedName("TITLE") val title: String,
    @SerializedName("NEZAM") val nezam: String,
    @SerializedName("MATCH_PERCENTAGE") val matchPercentage: Float,
    override val id: String = randomUUID(),
    ) : AiChatModel() {

    fun createAiClickableItem(): ClickableItemModel = ClickableItemModel(
        title = createAiMessage(),
        customData = this,
        clickType = AIClickType.Prescription,
    )

    fun createAiMessage(): String {
        val message = StringBuilder()
            .append("● ")
            .append("عنوان")
            .append(": ")
            .append(title)
            .append("\n")
            .append("● ")
            .append("نام دکتر")
            .append(": ")
            .append(doctorName)
            .append("\n")
            .append("● ")
            .append("تخصص")
            .append(": ")
            .append(proficiency)
            .append("\n")
            .append("● ")
            .append("شهز")
            .append(": ")
            .append(city)
            .append("\n")
            .append("● ")
            .append("آدرس")
            .append(": ")
            .append(address)
            .append("\n")
            .append("● ")
            .append("مرکز")
            .append(": ")
            .append(center)
            .append("\n")
            .append("● ")
            .append(" امتیاز جستجو")
            .append(": ")
            .append(matchPercentage)
            .append("\n")
        return message.toString()
    }
}
