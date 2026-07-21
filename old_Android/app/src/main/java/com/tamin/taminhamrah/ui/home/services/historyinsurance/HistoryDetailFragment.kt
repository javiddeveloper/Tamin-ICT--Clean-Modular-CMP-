package com.tamin.taminhamrah.ui.home.services.historyinsurance

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.services.AllHistoryInsuranceResponseModel
import com.tamin.taminhamrah.data.remote.models.services.AllHistoryInsuranceResponseModels
import com.tamin.taminhamrah.databinding.FragmentHistoryDetailBinding
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.base.BaseBottomSheetDialogFragment
import com.tamin.taminhamrah.ui.base.BaseViewModel
import com.tamin.taminhamrah.utils.UiUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HistoryDetailFragment :
    BaseBottomSheetDialogFragment<FragmentHistoryDetailBinding, BaseViewModel>(),
    AdapterInterface.OnShowMoreClickListener<Int> {

    override val mViewModelDialog: BaseViewModel by viewModels()
    override fun getLayoutId()= R.layout.fragment_history_detail

    lateinit var listAdapter: HistoryDetailAdapter
    var listHistoryByMonth: ArrayList<AllHistoryInsuranceResponseModel> = arrayListOf()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initData()
    }

    private fun initData() {
        viewBinding?.recycler?.apply {
            this.layoutManager = layoutManager
            this.adapter = listAdapter
            if (itemDecorationCount == 0) {
                addItemDecoration(UiUtils.BackgroundItemDecorationDrowable(requireContext()))
            }
        }
        (arguments?.getParcelable(Constants.ARRAYLIST) as? AllHistoryInsuranceResponseModels)?.let {
            listAdapter.setItems(it, this)
        }
    }

    private fun initView() {
        listAdapter = HistoryDetailAdapter()
        viewBinding?.recycler?.apply {
            adapter = listAdapter
            val layoutManager = LinearLayoutManager(requireContext())
            this.layoutManager = layoutManager
        }
    }

    override fun onShowMoreClick(item: Int, transitionView: View?, tag: String?) {
        viewBinding?.recycler?.layoutManager?.apply {
            scrollToPosition(item)
        }
    }
}