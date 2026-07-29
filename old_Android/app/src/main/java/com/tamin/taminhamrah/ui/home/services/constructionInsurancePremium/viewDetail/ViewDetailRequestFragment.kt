package com.tamin.taminhamrah.ui.home.services.constructionInsurancePremium.viewDetail

import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.Constants.FILE_ID
import com.tamin.taminhamrah.Constants.REQUEST_DATE
import com.tamin.taminhamrah.Constants.REQUEST_ID
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.KeyValueModel
import com.tamin.taminhamrah.data.remote.models.services.constructionInsurancePremium.ConstructionFileResponse
import com.tamin.taminhamrah.databinding.FragmentViewDetailRequestBinding
import com.tamin.taminhamrah.ui.adapters.ExpandableListAdapter
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.ui.home.services.constructionInsurancePremium.ConstructionInsurancePremiumViewModel
import com.tamin.taminhamrah.utils.UiUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ViewDetailRequestFragment :
    BaseFragment<FragmentViewDetailRequestBinding, ConstructionInsurancePremiumViewModel>() {

    //region Variables
    override val mViewModel: ConstructionInsurancePremiumViewModel by viewModels()

    private val requestInfoAdapter by lazy { ExpandableListAdapter(expandingIndex = 4) }
    private val computingInfoAdapter by lazy { ExpandableListAdapter(expandingIndex = 4) }

    //endregion

    //region Base Methods
    override fun getBindingVariable() = Pair(BR.viewModel, mViewModel)
    override fun getLayoutId() = R.layout.fragment_view_detail_request
    override fun setupObserver() {
        mViewModel.mldConstructionFile.observe(this, ::onResponseConstructionFiles)
    }


    override fun initView() {
        viewDataBinding?.apply {
            recyclerRequestInfo.apply {
                adapter = requestInfoAdapter
                if (itemDecorationCount == 0) {
                    addItemDecoration(UiUtils.createDivider(requireContext()))
                }
            }
            recycleComputingInfo.apply {
                adapter = computingInfoAdapter
                if (itemDecorationCount == 0) {
                    addItemDecoration(UiUtils.createDivider(requireContext()))
                }
            }
        }
    }

    override fun getData() {
        arguments?.let { arg ->
            val requestId = arg.getLong(REQUEST_ID)
            val fileId = arg.getLong(FILE_ID)
            val requestDate = arg.getString(REQUEST_DATE)

            viewLifecycleOwner.lifecycleScope.launchWhenCreated {
                mViewModel.getConstructionFilesWhitOutPaging(
                    requestNumber = requestId.toString(),
                    fileNumber = fileId.toString(),
                    requestDate = requestDate
                )

            }
        }
    }

    override fun onClick() {
        viewDataBinding?.apply {
            imageBack.setOnClickListener {
                backButtonPress()
            }

            btnShowDetailRequest.setOnClickListener {
                requestInfoAdapter.toggleMinifyMode()
                btnShowDetailRequest.text =
                    requireContext().getString(if (requestInfoAdapter.isMinifyMode()) R.string.show_detail else R.string.hide_detail)
            }

            btnShowDetailComputing.setOnClickListener {
                computingInfoAdapter.toggleMinifyMode()
                btnShowDetailComputing.text =
                    requireContext().getString(if (computingInfoAdapter.isMinifyMode()) R.string.show_detail else R.string.hide_detail)
            }
        }
    }
    //endregion

    //region Listeners
    private fun onResponseConstructionFiles(response: ConstructionFileResponse) {
        if (response.isSuccess) {
            val list = response.data?.list ?: emptyList()
            if (list.isNotEmpty()) {
                viewDataBinding?.parentLayout?.isVisible = true
                val computingInfo = ArrayList<KeyValueModel>()
                val requestInfo = ArrayList<KeyValueModel>()
                list.forEach {
                    computingInfo.addAll(it.getComputingInfo())
                    requestInfo.addAll(it.getRequestInfo())
                }
                computingInfoAdapter.setItems(computingInfo)
                requestInfoAdapter.setItems(requestInfo)
            }
        }
    }
    //endregion

}