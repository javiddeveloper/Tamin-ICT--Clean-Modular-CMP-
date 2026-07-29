package com.tamin.taminhamrah.ui.aiAgent.di

import com.tamin.taminhamrah.data.repository.ai.AiHistoryRepositoryImpl
import com.tamin.taminhamrah.ui.aiAgent.domain.repository.AiHistoryRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
interface RepositoryBinder {

    @Binds
    fun provideAiHistoryRepository(aiHistoryRepository: AiHistoryRepositoryImpl): AiHistoryRepository
}