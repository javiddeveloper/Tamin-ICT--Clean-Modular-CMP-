package com.tamin.taminhamrah.data.entity.ai.convertor

import androidx.room.TypeConverter
import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.tamin.taminhamrah.data.repository.ai.model.AgentActionContent
import com.tamin.taminhamrah.data.tools.RuntimeTypeAdapterFactory
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceData
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceResponse
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.param.DeepLinkData

class ServiceResponseConverter {

    private val gson = GsonBuilder()
        .registerTypeAdapterFactory(
            RuntimeTypeAdapterFactory.of(ServiceData::class.java, "type")
                .registerSubtype(ServiceData.StringMessage::class.java, "StringMessage")
                .registerSubtype(ServiceData.HeaderMessage::class.java, "HeaderMessage")
                .registerSubtype(ServiceData.KeyValueMessage::class.java, "KeyValueMessage")
                .registerSubtype(ServiceData.Clickable::class.java, "Clickable")
                .registerSubtype(ServiceData.GroupButton::class.java, "GroupButton")
                .registerSubtype(ServiceData.DeeplinkWeb::class.java, "DeeplinkWeb")
                .registerSubtype(ServiceData.Law::class.java, "Law")
                .registerSubtype(ServiceData.GenerativeForm::class.java,"GenerativeForm")
        )
        .registerTypeAdapterFactory(
            RuntimeTypeAdapterFactory.of(AgentActionContent::class.java, "type")
                .registerSubtype(AgentActionContent.SendPrompt::class.java, "SendPrompt")
                .registerSubtype(AgentActionContent.Web::class.java, "Web")
                .registerSubtype(AgentActionContent.DeepLink::class.java, "DeepLink")
                .registerSubtype(AgentActionContent.LocalDeepLink::class.java, "LocalDeepLink")
                .registerSubtype(AgentActionContent.Dial::class.java, "Dial")
                .registerSubtype(AgentActionContent.EditMobile::class.java, "EditMobile")
                .registerSubtype(AgentActionContent.AddAccountNumber::class.java, "AddAccountNumber")
                .registerSubtype(AgentActionContent.DisplayReport::class.java, "DisplayReport")
                .registerSubtype(AgentActionContent.CancelDependent::class.java, "CancelDependent")
                .registerSubtype(AgentActionContent.WeddingPresent::class.java, "WeddingPresent")
                .registerSubtype(AgentActionContent.InquiryEducation::class.java, "InquiryEducation")
                .registerSubtype(AgentActionContent.OccurrenceReportGet::class.java, "OccurrenceReportGet")
        )
        .registerTypeAdapterFactory(
            RuntimeTypeAdapterFactory.of(DeepLinkData::class.java, "type")
                .registerSubtype(DeepLinkData.Patient::class.java, "Patient")
        )
        .setExclusionStrategies(object : ExclusionStrategy {
            override fun shouldSkipField(f: FieldAttributes): Boolean {
                return f.declaringClass == AgentActionContent::class.java && f.name == "actionText"
            }
            override fun shouldSkipClass(clazz: Class<*>?): Boolean = false
        })
        .create()

    @TypeConverter
    fun fromServiceResponseList(value: List<ServiceResponse>?): String? {
        if (value == null) return null
        return gson.toJson(value)
    }

    @TypeConverter
    fun toServiceResponseList(value: String?): List<ServiceResponse>? {
        if (value == null) return null
        val type = object : TypeToken<List<ServiceResponse>>() {}.type
        return gson.fromJson(value, type)
    }
}
