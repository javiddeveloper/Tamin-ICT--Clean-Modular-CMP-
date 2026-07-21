package com.tamin.taminhamrah.di

import androidx.fragment.app.Fragment
import com.tamin.taminhamrah.ui.treatment.adapter.ViewPagerAdapter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent

@Module
@InstallIn(FragmentComponent::class)
object TreatmentCostsModule {

    @Provides
    fun providePagerAdapter(fragment: Fragment) : ViewPagerAdapter {
        return ViewPagerAdapter(fragment.requireActivity())
    }
}