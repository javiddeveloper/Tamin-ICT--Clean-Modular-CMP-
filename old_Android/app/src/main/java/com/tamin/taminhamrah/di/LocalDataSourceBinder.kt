package com.tamin.taminhamrah.di

import com.tamin.taminhamrah.data.local.ai.datasource.AiHistoryLocalDataSource
import com.tamin.taminhamrah.data.local.ai.datasource.AiHistoryLocalDataSourceImp
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
interface LocalDataSourceBinder {

    @Binds
    fun provideAiHistoryLocalDataSource(aiHistoryLocalDataSource: AiHistoryLocalDataSourceImp): AiHistoryLocalDataSource

}