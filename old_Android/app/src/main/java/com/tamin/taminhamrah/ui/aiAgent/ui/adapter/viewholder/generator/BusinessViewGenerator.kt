package com.tamin.taminhamrah.ui.aiAgent.ui.adapter.viewholder.generator

import com.tamin.taminhamrah.data.repository.ai.model.AiGenerativeModel
import com.tamin.taminhamrah.data.repository.ai.model.FormSchema

interface BusinessViewGenerator {
    fun matches(schema: FormSchema, message: AiGenerativeModel): Boolean
    fun bind(host: FormHost, schema: FormSchema, message: AiGenerativeModel)
    fun cleanup()
}
