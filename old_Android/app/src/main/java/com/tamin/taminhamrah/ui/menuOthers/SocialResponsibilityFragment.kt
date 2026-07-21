package com.tamin.taminhamrah.ui.menuOthers

import android.content.Intent
import android.net.Uri
import android.view.View
import androidx.fragment.app.viewModels
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.databinding.FragmentSocialResponsibilityBinding
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.utils.UiUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SocialResponsibilityFragment :
    BaseFragment<FragmentSocialResponsibilityBinding, MenuOthersViewModel>(),
    AdapterInterface.OnItemClickListener<MenuModel> {

    override val mViewModel: MenuOthersViewModel by viewModels()
    private lateinit var newFeaturesAdapter: SocialResponsibilityAdapter

    override fun getBindingVariable(): Pair<Int, Any?> {
        return Pair(BR.viewModel, mViewModel)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_social_responsibility
    }

    override fun setupObserver() {


    }

    override fun initView() {
        setupToolbar(
            viewDataBinding?.appBar,
            viewDataBinding?.appbarBackgroundImage?.imageBackground
        )
        newFeaturesAdapter = SocialResponsibilityAdapter()

        viewDataBinding?.recycler?.apply {
            this.adapter = newFeaturesAdapter
            if (itemDecorationCount == 0) {
                addItemDecoration(UiUtils.createDivider(this.context))
            }
        }

        newFeaturesAdapter.setItems(mViewModel.getSocialResponsibility(), this)
    }

    override fun getData() {
    }

    override fun onClick() {
    }

    override fun onItemClick(item: MenuModel, transitionView: View?, tag: String?) {
        when (item.id) {
            "0" -> {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(Constants.LINK_ETRAT_FATEMI))
                startActivity(intent)
            }
        }
    }

}