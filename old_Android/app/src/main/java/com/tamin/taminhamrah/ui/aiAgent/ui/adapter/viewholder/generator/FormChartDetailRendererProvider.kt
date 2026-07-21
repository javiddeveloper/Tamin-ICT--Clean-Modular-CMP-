package com.tamin.taminhamrah.ui.aiAgent.ui.adapter.viewholder.generator

import android.content.Context
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface FormChartDetailRendererEntryPoint {
    fun chartDetailRenderers(): Set<ChartDetailRenderer>
    fun schemaGenerators(): Set<BusinessViewGenerator>
}

object FormChartDetailRendererProvider {
    fun getChartDetailRenderers(context: Context): List<ChartDetailRenderer> {
        val entryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext,
            FormChartDetailRendererEntryPoint::class.java
        )
        return entryPoint.chartDetailRenderers().toList()
    }

    fun getSchemaGenerators(context: Context): List<BusinessViewGenerator> {
        val entryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext,
            FormChartDetailRendererEntryPoint::class.java
        )
        return entryPoint.schemaGenerators().toList()
    }
}
