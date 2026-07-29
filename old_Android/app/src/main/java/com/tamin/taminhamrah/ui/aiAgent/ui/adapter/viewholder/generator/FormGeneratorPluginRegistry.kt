package com.tamin.taminhamrah.ui.aiAgent.ui.adapter.viewholder.generator

import com.tamin.taminhamrah.data.repository.ai.model.AiGenerativeModel
import com.tamin.taminhamrah.data.repository.ai.model.FormField
import com.tamin.taminhamrah.data.repository.ai.model.FormSchema
import kotlin.reflect.KClass

interface FormChartBehaviorPlugin {
    fun matches(schema: FormSchema, field: FormField): Boolean
    fun onChartBound(
        host: FormHost,
        schema: FormSchema,
        field: FormField,
        chartHandle: ChartRenderer.ChartHandle
    )
}

interface ChartDetailRenderer {
    val actionKey: String
    val payloadType: KClass<*>

    fun render(
        host: FormHost,
        chartHandle: ChartRenderer.ChartHandle,
        payload: Any
    )
}

interface FormGeneratorPluginRegistry {
    fun findSchemaGenerator(schema: FormSchema, message: AiGenerativeModel): BusinessViewGenerator?
    fun applyChartBehavior(
        host: FormHost,
        schema: FormSchema,
        field: FormField,
        chartHandle: ChartRenderer.ChartHandle
    )
    fun cleanup()
}

class DefaultFormGeneratorPluginRegistry(
    private val schemaGenerators: List<BusinessViewGenerator> = emptyList(),
    chartDetailRenderers: List<ChartDetailRenderer> = emptyList()
) : FormGeneratorPluginRegistry {
    private val chartBehaviorPlugins: List<FormChartBehaviorPlugin> = listOf(
        GenericChartDetailPlugin(renderers = chartDetailRenderers)
    )

    override fun findSchemaGenerator(schema: FormSchema, message: AiGenerativeModel): BusinessViewGenerator? {
        return schemaGenerators.firstOrNull { it.matches(schema, message) }
    }

    override fun applyChartBehavior(
        host: FormHost,
        schema: FormSchema,
        field: FormField,
        chartHandle: ChartRenderer.ChartHandle
    ) {
        chartBehaviorPlugins
            .firstOrNull { it.matches(schema, field) }
            ?.onChartBound(host, schema, field, chartHandle)
    }

    override fun cleanup() {
        schemaGenerators.forEach { it.cleanup() }
    }
}
