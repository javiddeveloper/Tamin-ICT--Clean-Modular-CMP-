package com.tamin.taminhamrah.di

import com.tamin.taminhamrah.ui.aiAgent.ui.adapter.viewholder.generator.BusinessViewGenerator
import com.tamin.taminhamrah.ui.aiAgent.ui.adapter.viewholder.generator.DependentCancellationBusinessGenerator
import com.tamin.taminhamrah.ui.aiAgent.ui.adapter.viewholder.generator.WeddingPresentBusinessGenerator
import com.tamin.taminhamrah.ui.aiAgent.ui.adapter.viewholder.generator.EditBankAccountBusinessGenerator
import com.tamin.taminhamrah.ui.aiAgent.ui.adapter.viewholder.generator.EditMobileBusinessGenerator
import com.tamin.taminhamrah.ui.aiAgent.ui.adapter.viewholder.generator.FuneralAllowanceBusinessGenerator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
object FormSchemaGeneratorModule {

    @Provides
    @IntoSet
    fun provideEditMobileGenerator(): BusinessViewGenerator {
        return EditMobileBusinessGenerator()
    }

    @Provides
    @IntoSet
    fun provideEditBankAccountGenerator(): BusinessViewGenerator {
        return EditBankAccountBusinessGenerator()
    }

    @Provides
    @IntoSet
    fun provideDependentCancellationGenerator(): BusinessViewGenerator {
        return DependentCancellationBusinessGenerator()
    }

    @Provides
    @IntoSet
    fun provideWeddingPresentGenerator(): BusinessViewGenerator {
        return WeddingPresentBusinessGenerator()
    }

    @Provides
    @IntoSet
    fun provideFuneralAllowanceGenerator(): BusinessViewGenerator {
        return FuneralAllowanceBusinessGenerator()
    }

    @Provides
    @IntoSet
    fun provideInquiryEducationGenerator(): BusinessViewGenerator {
        return com.tamin.taminhamrah.ui.aiAgent.ui.adapter.viewholder.generator.InquiryEducationBusinessGenerator()
    }
}
