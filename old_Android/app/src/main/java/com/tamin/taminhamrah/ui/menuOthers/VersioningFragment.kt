package com.tamin.taminhamrah.ui.menuOthers

import androidx.fragment.app.viewModels
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.local.othersInfo.entity.VersionInfoModel
import com.tamin.taminhamrah.databinding.FragmentVersioningBinding
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.utils.UiUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VersioningFragment :
    BaseFragment<FragmentVersioningBinding, MenuOthersViewModel>() {

    override val mViewModel: MenuOthersViewModel by viewModels()
    private val featuresAdapter by lazy {
        VersioningAdapter()
    }

    override fun getBindingVariable(): Pair<Int, Any?> {
        return Pair(BR.viewModel, mViewModel)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_versioning
    }

    override fun setupObserver() {
        mViewModel.mldVersioning.observe(this, ::showResult)

    }

    private fun showResult(result: List<VersionInfoModel>) {
        featuresAdapter.setItems(result.sortedByDescending { it.versionCode })
    }

    override fun initView() {

        setupToolbar(
            viewDataBinding?.appBar, viewDataBinding?.appbarBackgroundImage?.imageBackground
        )


        viewDataBinding?.recycler?.apply {
            this.adapter = featuresAdapter
            if (itemDecorationCount == 0) {
                addItemDecoration(UiUtils.VerticalItemMarginDecoration(40, true))
            }
        }
    }

    override fun getData() {
        mViewModel.getVersioningInfo()
        viewDataBinding?.appBar?.tvTitle?.text = getString(R.string.label_drawer_title_versioning)
        viewDataBinding?.appBar?.tvSubTitle?.text = ""
    }

    override fun onClick() {

    }
}