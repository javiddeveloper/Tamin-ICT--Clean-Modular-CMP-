package com.tamin.taminhamrah.di

import com.tamin.taminhamrah.ui.aiAgent.ui.adapter.viewholder.generator.ChartDetailRenderer
import com.tamin.taminhamrah.ui.aiAgent.ui.adapter.viewholder.generator.WageAndHistoryChartDetailRenderer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
object FormChartDetailRendererModule {

    @Provides
    @IntoSet
    fun provideWageAndHistoryChartDetailRenderer(): ChartDetailRenderer {
        return WageAndHistoryChartDetailRenderer()
    }
}
