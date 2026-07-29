package com.tamin.taminhamrah.ui.home.services.employer.completeInfo

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter


class ViewStateAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle, fragment: Fragment, private val bundleInfo:String) :
    FragmentStateAdapter(fragment) {

    override fun createFragment(position: Int): Fragment {
        // Hardcoded in this order, you'll want to use lists and make sure the titles match
        return if (position == 0) {
            LegalWorkshopListFragment.newInstance(bundleInfo)
        } else  CompleteInfoOfRealWorkshopFragment.newInstance(bundleInfo)
    }

    override fun getItemCount(): Int {
        // Hardcoded, use lists
        return 2
    }

}