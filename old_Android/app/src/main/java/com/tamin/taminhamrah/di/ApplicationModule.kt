package com.tamin.taminhamrah.di

import android.content.Context
import com.tamin.taminhamrah.data.local.preference.PreferenceManager
import com.tamin.taminhamrah.utils.NetworkUtil
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApplicationModule {
    @Provides
    @Singleton
    fun provideUtils(@ApplicationContext context: Context): NetworkUtil = NetworkUtil(context)

}