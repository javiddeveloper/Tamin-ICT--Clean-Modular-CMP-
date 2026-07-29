package com.tamin.taminhamrah.ui.home.services.employer.completeInfo

import android.annotation.SuppressLint
import android.view.View
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.services.request_pregnancy_pay.CurrentUserResponse
import com.tamin.taminhamrah.databinding.FragmentCompleteWorkshopInfoTabBinding
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.utils.Utility
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CompleteWorkshopInfoTabFragment :
    BaseFragment<FragmentCompleteWorkshopInfoTabBinding, CompleteInfoViewModel>() {

    override val mViewModel: CompleteInfoViewModel by viewModels()

    override fun getBindingVariable(): Pair<Int, Any?> {
        return Pair(BR.viewModel, mViewModel)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_complete_workshop_info_tab
    }

    override fun setupObserver() {
        mViewModel.mldUserInfo.observe(viewLifecycleOwner, ::onUserInfo)
    }

    @SuppressLint("SetTextI18n")
    private fun onUserInfo(result: CurrentUserResponse?) {
        if (result?.isSuccess == true && result.data != null) {
            viewDataBinding?.appBar?.apply {
                tvSubTitle.text =
                    "${getString(R.string.full_name)} : ${result.data!!.firstName} ${result.data!!.lastName}"
                tvSubSubTitle.text =
                    "${getString(R.string.national_code)} : ${result.data!!.nationalCode}"
                tvSubSubTitle.visibility = View.VISIBLE
            }
        }
    }

    override fun initView() {

        viewDataBinding?.apply {
            setupToolbar(
                appBar,
                appbarBackgroundImage.imageBackground,
            )
            val adapter = ViewStateAdapter(
                childFragmentManager,
                lifecycle,
                this@CompleteWorkshopInfoTabFragment,
                Utility.getToolbarIconImage(arguments)
            )
            pager.adapter = adapter
            pager.isSaveEnabled = false

            tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    pager.currentItem = tab.position
                }

                override fun onTabUnselected(tab: TabLayout.Tab) {}
                override fun onTabReselected(tab: TabLayout.Tab) {}
            })

            pager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    tabLayout.selectTab(tabLayout.getTabAt(position))
                }
            })

            pager.currentItem = 0
        }
    }


    override fun getData() {
        mViewModel.getUserInfo()
    }

    override fun onClick() {
    }
}
