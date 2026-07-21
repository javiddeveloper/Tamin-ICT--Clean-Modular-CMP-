package com.tamin.taminhamrah.ui.mytamin.myrequest.showRequestInfo.objectionNonExistentHistory

import androidx.fragment.app.viewModels
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.Constants.REFERENCE_ID
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.showRequestInfo.ResultFollowUpObjectionNonExitsResponse
import com.tamin.taminhamrah.databinding.FragmentFollowUpObjectionNonExistentHistoryBinding
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.MessageOfRequestDialogFragment
import com.tamin.taminhamrah.ui.home.services.KeyValueAdapter
import com.tamin.taminhamrah.ui.mytamin.myrequest.showRequestInfo.ShowRequestInfoViewModel
import com.tamin.taminhamrah.utils.UiUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FollowUpObjectionNonExistentHistoryFragment :
    BaseFragment<FragmentFollowUpObjectionNonExistentHistoryBinding, ShowRequestInfoViewModel>() {

    //region Variables
    override val mViewModel: ShowRequestInfoViewModel by viewModels()
    val listAdapter by lazy {
        KeyValueAdapter()
    }
    //endregion

    //region Base Methods
    override fun getBindingVariable() = Pair(BR.viewModel, mViewModel)

    override fun getLayoutId() = R.layout.fragment_follow_up_objection_non_existent_history

    override fun setupObserver() {
        mViewModel.mldFollowUpResultObjectionNonExistsHistory.observe(this,::onFollowUpResultResponse)
    }
    override fun initView() {
        viewDataBinding?.recyclerRequestInfo?.apply {
            this.adapter = listAdapter
            if (itemDecorationCount == 0) {
                addItemDecoration(UiUtils.createDivider(this.context))
            }
        }
        setupToolbar(
            viewDataBinding?.appBar,
            viewDataBinding?.appbarBackgroundImage?.imageBackground/*,
            actionIconRes = R.drawable.ic_search*/
        )

    }

    override fun getData() {
        val referenceId = arguments?.getString(REFERENCE_ID)
        if (referenceId != null) {
            mViewModel.getFollowUpResultObjectionNonExistsHistory(referenceId)
        } else {
            showAlertDialog(
                MessageOfRequestDialogFragment.MessageType.ERROR,
                getString(R.string.error_recive_data),
                dismissType = MessageOfRequestDialogFragment.DismissType.BACK_TO_PREVIOUS
            )
        }
    }

    override fun onClick() {}
    //endregion

    //region Listeners
    private fun onFollowUpResultResponse(result: ResultFollowUpObjectionNonExitsResponse) {
        if (result.isSuccess){
            result.data?.list?.forEach {list->
                listAdapter.setItems(list.getDetailInfo())
            }
        }
    }

    //endregion

    //region Utils
    //endregion
}