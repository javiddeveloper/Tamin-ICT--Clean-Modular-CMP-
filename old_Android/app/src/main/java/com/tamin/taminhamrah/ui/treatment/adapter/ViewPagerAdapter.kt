package com.tamin.taminhamrah.ui.treatment.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.tamin.taminhamrah.ui.treatment.treatmentCard.TreatmentCardFragment
import javax.inject.Inject

class ViewPagerAdapter @Inject constructor(fragment: FragmentActivity) : FragmentStateAdapter(fragment) {

    private val fragmentList = ArrayList<TreatmentCardFragment>()

    fun submitList(fragments: List<TreatmentCardFragment>) {
        val diffResult = DiffUtil.calculateDiff(DiffCallback(fragmentList, fragments), true)
        fragmentList.clear()
        fragmentList.addAll(fragments)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun getItemCount(): Int {
        return fragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }


    class DiffCallback(
        private val mOldList: List<TreatmentCardFragment>,
        private val mNewList: List<TreatmentCardFragment>
    ) : DiffUtil.Callback() {
        override fun getOldListSize()= mOldList.size

        override fun getNewListSize()= mNewList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldFragment = mOldList[oldItemPosition]
            val newFragment = mNewList[newItemPosition]
            // Compare the unique identifier of the fragments
            return oldFragment.userInfo?.nationalCode == newFragment.userInfo?.nationalCode
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldFragment = mOldList[oldItemPosition]
            val newFragment = mNewList[newItemPosition]
            // Compare the content of the fragments if needed
            return oldFragment == newFragment
        }
    }
}