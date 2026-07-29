package com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.inquiryEducation

import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.tamin.taminhamrah.data.repository.ai.model.FormSchema
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceData
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceResponse
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceNameEnum
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceResult
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.param.ServiceParams
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class InquiryEducationMockProvider @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gson: Gson
) {
    private fun readMockJson(): JsonObject {
        val jsonString = context.assets.open("inquiry_education_mock.json").bufferedReader().use { it.readText() }
        return JsonParser.parseString(jsonString).asJsonObject
    }

    fun getGetUseCaseResult(params: ServiceParams): ServiceResult {
        return try {
            val json = readMockJson()
            val getResponseObj = json.getAsJsonObject("get_response")
            val key = getResponseObj.get("key").asString
            val title = getResponseObj.get("title").asString
            val schemaJson = getResponseObj.getAsJsonObject("schema")
            val schema = gson.fromJson(schemaJson, FormSchema::class.java)

            val payload = params.payload?.toMutableMap() ?: mutableMapOf()
            payload.remove("studyCode")

            val formResponse = ServiceResponse(
                action = ServiceNameEnum.fromString(key) ?: params.serviceName,
                title = title,
                data = ServiceData.GenerativeForm(
                    schema = schema,
                    payload = payload.mapValues { it.value?.toString() }
                )
            )
            ServiceResult.Success(listOf(formResponse))
        } catch (e: Exception) {
            ServiceResult.Failure(e)
        }
    }

    fun getSubmitUseCaseResult(params: ServiceParams): ServiceResult {
        return try {
            val studyCode = params.payload?.get("studyCode")?.toString() ?: ""
            val isSuccess = studyCode == "12345" || studyCode == "123456"

            val json = readMockJson()
            val responseKey = if (isSuccess) "submit_success" else "submit_failure"
            val responseObj = json.getAsJsonObject(responseKey)
            val key = responseObj.get("key").asString
            val title = responseObj.get("title").asString
            val schemaJson = responseObj.getAsJsonObject("schema")
            val schema = gson.fromJson(schemaJson, FormSchema::class.java)

            val newData = params.payload?.toMutableMap() ?: mutableMapOf()

            val formResponse = ServiceResponse(
                action = ServiceNameEnum.fromString(key) ?: params.serviceName,
                title = title,
                data = ServiceData.GenerativeForm(
                    schema = schema,
                    payload = newData.mapValues { it.value?.toString() }
                )
            )
            ServiceResult.Success(listOf(formResponse))
        } catch (e: Exception) {
            ServiceResult.Failure(e)
        }
    }
}
