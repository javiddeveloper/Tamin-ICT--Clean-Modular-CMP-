package com.tamin.taminhamrah.ui.menuOthers

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.entity.ProfileModel
import com.tamin.taminhamrah.data.remote.models.Resource
import com.tamin.taminhamrah.databinding.FragmentMenuOthersBinding
import com.tamin.taminhamrah.ui.MainActivity
import com.tamin.taminhamrah.ui.NavigatorAdapter
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.utils.UiUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MenuOthersFragment :
    BaseFragment<FragmentMenuOthersBinding, MenuOthersViewModel>(),
    AdapterInterface.OnItemClickListener<MenuModel> {

    override val mViewModel: MenuOthersViewModel by viewModels()
    private lateinit var listAdapter: NavigatorAdapter

    override fun getBindingVariable(): Pair<Int, Any?> {
        return Pair(BR.viewModel, mViewModel)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_menu_others
    }

    override fun setupObserver() {
//        mViewModel.mldProfile.observe(this, ::showResult)
//        mViewModel.mldEligibilityResult.observe(this, ::fetchEligibilitySuccess)
    }

    override fun initView() {

        listAdapter = NavigatorAdapter()

        viewDataBinding?.recycler?.apply {
            this.adapter = listAdapter
            if (itemDecorationCount == 0) {
                addItemDecoration(UiUtils.createDivider(this.context))
            }
        }

        listAdapter.setItems(mViewModel.getItemsList(), this)

    }

    override fun getData() {

    }

    override fun onClick() {
    }

    fun createToolbarBundle(item: MenuModel): Bundle {
        val bundle = Bundle()
        bundle.putString(Constants.TOOLBAR_TITLE, item.title)
        bundle.putString(Constants.TOOLBAR_SUBTITLE, item.description)
        bundle.putInt(Constants.TOOLBAR_ICON_IMAGE, item.iconRes)
        return bundle
    }

    private fun showResult(result: Resource<ProfileModel?>) {
        (requireActivity() as? MainActivity)?.handleResponse(result)
    }

    override fun onItemClick(item: MenuModel, transitionView: View?, tag: String?) {

        when (item.id) {
            "1" -> {
                handlePageDestination(
                    R.id.action_drawer_to_versioning,
                    createToolbarBundle(item)
                )
            }
            "2" -> {
                handlePageDestination(
                    R.id.action_menu_to_contact_us,
                    createToolbarBundle(item)
                )
            }
        }
    }
}