package com.tamin.taminhamrah.ui.home.services.viewShortTermSupport

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.services.ViewShortTermRequestResponse
import com.tamin.taminhamrah.databinding.FragmentViewShortTermBinding
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.utils.extentions.setupRecycler
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ViewShortTermFragment : BaseFragment<FragmentViewShortTermBinding,ViewShortTermViewModel>() {
    var scrollRange = -1
    var isShow = true
    lateinit var listAdapter: ViewShortTermAdapter
    var oldList = arrayListOf<ViewShortTermRequestResponse>()
    var isLastPage = false
    var isScrolling = false
    var isLoading = true
    var totalPages = 100

    override val mViewModel: ViewShortTermViewModel by viewModels()

    override fun getBindingVariable(): Pair<Int, Any?> {
        return Pair(BR.viewModel, mViewModel)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_view_short_term
    }

    override fun setupObserver() {
    }

    override fun initView() {
        listAdapter = ViewShortTermAdapter()
        setupRecycler(viewDataBinding?.recycler, listAdapter)
        setupToolbar(
            viewDataBinding?.appBar,
            viewDataBinding?.appbarBackgroundImage?.imageBackground
        )

    }

    override fun getData() {
        this@ViewShortTermFragment.lifecycleScope.launchWhenCreated {
            mViewModel.getViewShortTermRequest.collectLatest {
                listAdapter.submitData(it)
            }
        }
    }

    override fun onClick() {

    }


}