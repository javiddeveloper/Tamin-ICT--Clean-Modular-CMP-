package com.tamin.taminhamrah.ui.aiAgent.ui.adapter.viewholder.generator

import com.google.gson.Gson
import com.tamin.taminhamrah.data.repository.ai.model.FormField
import com.tamin.taminhamrah.data.repository.ai.model.FormSchema

/**
 * Generic chart detail bottom-sheet dispatcher.
 *
 * Contract in field extras:
 * - detailAction: String -> renderer key
 * - detailPayload: List<*> -> indexed payload per selected chart bar
 */
class GenericChartDetailPlugin(
    private val renderers: List<ChartDetailRenderer>
) : FormChartBehaviorPlugin {
    private val gson = Gson()

    override fun matches(schema: FormSchema, field: FormField): Boolean {
        val detailAction = field.extras?.get("detailAction") as? String
        val detailPayload = field.extras?.get("detailPayload") as? List<*>
        return !detailAction.isNullOrBlank() &&
            detailPayload != null &&
            renderers.any { it.actionKey == detailAction }
    }

    override fun onChartBound(
        host: FormHost,
        schema: FormSchema,
        field: FormField,
        chartHandle: ChartRenderer.ChartHandle
    ) {
        val detailAction = field.extras?.get("detailAction") as? String ?: return
        val detailPayload = field.extras["detailPayload"] as? List<*> ?: return
        val renderer = renderers.firstOrNull { it.actionKey == detailAction } ?: return

        chartHandle.setOnBarSelected { index ->
            val rawPayload = detailPayload.getOrNull(index) ?: return@setOnBarSelected
            val resolvedPayload = resolvePayload(rawPayload, renderer) ?: return@setOnBarSelected
            renderer.render(host, chartHandle, resolvedPayload)
        }
    }

    /**
     * After DB roundtrip, extras payloads may come back as generic maps/lists.
     * Convert them back to the renderer's expected concrete payload type.
     */
    private fun resolvePayload(rawPayload: Any, renderer: ChartDetailRenderer): Any? {
        if (renderer.payloadType.isInstance(rawPayload)) return rawPayload

        return runCatching {
            val json = if (rawPayload is String) rawPayload else gson.toJson(rawPayload)
            gson.fromJson(json, renderer.payloadType.java)
        }.getOrNull()
    }
}
