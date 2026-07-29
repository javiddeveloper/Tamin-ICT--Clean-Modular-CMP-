package com.tamin.taminhamrah.ui.mytamin.inbox

import android.view.View
import androidx.fragment.app.activityViewModels
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.remote.models.Resource
import com.tamin.taminhamrah.data.remote.models.user.InboxItem
import com.tamin.taminhamrah.databinding.FragmentInboxDetailBinding
import com.tamin.taminhamrah.ui.MainActivity
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.ui.home.services.KeyValueAdapter
import com.tamin.taminhamrah.utils.UiUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InboxDetailFragment :
    BaseFragment<FragmentInboxDetailBinding, InboxViewModel>(),
    AdapterInterface.OnItemClickListener<InboxItem> {


    override val mViewModel: InboxViewModel by activityViewModels()// use activityViewModels() to share viewModel

    lateinit var listAdapter: KeyValueAdapter

    override fun getBindingVariable(): Pair<Int, Any?> {
        return Pair(BR.viewModel, mViewModel)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_inbox_detail
    }

    override fun setupObserver() {
        // mViewModel.mldInbox.observe(this, ::showResult)
    }


    override fun initView() {
        listAdapter = KeyValueAdapter()
        viewDataBinding?.recycler?.apply {
            this.adapter = listAdapter
            if (itemDecorationCount == 0) {
                addItemDecoration(UiUtils.createDivider(requireContext()))
            }
        }

        val itemList = mViewModel.mldSelectedItem.value?.let { mViewModel.createKeyValue(it) }
        itemList?.let { listAdapter.setItems(it) }

        setupToolbar(

            viewDataBinding?.appBar,
            viewDataBinding?.appbarBackgroundImage?.imageBackground,
            actionIconRes = R.drawable.ic_download
        )
    }

    override fun getData() {

    }

    override fun onClick() {

    }

    var requestTypeId = ""


    private fun showRequestTypeResult(result: Resource<List<MenuModel>?>?) {

        (requireActivity() as? MainActivity)?.handleResponse(result)
        if (result?.status == Resource.Status.SUCCESS) {

        }
    }

    /*    private fun showErrorListResult(result: Resource<List<ErrorModel>?>?) {

            (requireActivity() as? MainActivity)?.handleResponse(result)
            if (result?.status == Resource.Status.SUCCESS){
                val bundle = Bundle()
                bundle.putParcelableArrayList(
                    ErrorDialogFragment.ARG_ERROR_ITEMS,
                    result.data as? ArrayList<out Parcelable>
                )
                handlePageDestination(R.id.action_my_request_to_errorDialogFragment, bundle)
            }
        }*/

    override fun onItemClick(item: InboxItem, transitionView: View?, tag: String?) {

    }
}