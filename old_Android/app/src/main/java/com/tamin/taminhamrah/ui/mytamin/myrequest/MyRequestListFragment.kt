package com.tamin.taminhamrah.ui.mytamin.myrequest

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.map
import com.google.gson.Gson
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.Constants.REFERENCE_ID
import com.tamin.taminhamrah.Constants.REQUEST_TYPE
import com.tamin.taminhamrah.Constants.TOOLBAR_ICON_IMAGE
import com.tamin.taminhamrah.Constants.TOOLBAR_TITLE
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.remote.models.user.MyRequestItem
import com.tamin.taminhamrah.data.remote.models.user.RequestError
import com.tamin.taminhamrah.data.remote.models.user.RequestErrorResponse
import com.tamin.taminhamrah.data.remote.models.user.SmartGuideResponse
import com.tamin.taminhamrah.databinding.FragmentMyRequestListBinding
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.appinterface.MenuInterface
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.ui.dialog.MenuDialogFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.MessageOfRequestDialogFragment
import com.tamin.taminhamrah.ui.mytamin.myrequest.showRequestInfo.RequestTypeEnumClass
import com.tamin.taminhamrah.utils.Utility
import com.tamin.taminhamrah.utils.extentions.gone
import com.tamin.taminhamrah.utils.extentions.setupRecycler
import com.tamin.taminhamrah.utils.extentions.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class MyRequestListFragment :
    BaseFragment<FragmentMyRequestListBinding, MyRequestListViewModel>(),
    AdapterInterface.OnItemClickListener<MyRequestItem> {

    lateinit var listAdapter: MyRequestAdapter

    override val mViewModel: MyRequestListViewModel by viewModels()

    var selectedRequestType: String? = null

    override fun getBindingVariable(): Pair<Int, Any?> {
        return Pair(BR.viewModel, mViewModel)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_my_request_list
    }

    override fun setupObserver() {
        mViewModel.mldRequestErrorList.observe(this, ::showErrorListResult)
        mViewModel.mldSmartGuideList.observe(this, ::showSmartGuidList)
    }

    override fun initView() {

        listAdapter = MyRequestAdapter(this)
        setupRecycler(viewDataBinding?.recycler, listAdapter)

        setupToolbar(
            viewDataBinding?.appBar,
            viewDataBinding?.appbarBackgroundImage?.imageBackground,
        )
        onClick()
    }

    override fun getData() {
        getMyRequestList()
    }

    private fun getMyRequestList(
        requestTypeId: String? = "",
        refCode: String? = ""
    ) {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            mViewModel.getMyRequestListFlow(requestTypeId, refCode).collectLatest { pagingData ->
                listAdapter.submitData(pagingData)
            }
        }
    }

    override fun onClick() {
        viewDataBinding?.apply {

                btnOpenSearch.setOnClickListener {
                    groupSearch.visible()
                    btnOpenSearch.gone()
                }
                btnCloseSearch.setOnClickListener {
                    groupSearch.gone()
                    btnOpenSearch.visible()
                }
            inputRequestType.selectableInput.setOnClickListener {

                view?.windowToken?.let { Utility.hideKeyboard(requireContext(), it) }

                val dialog = MenuDialogFragment.newInstance(
                    true,
                    inputRequestType.selectableInput.hint.toString()
                )
                dialog.setMenuListener(object : MenuInterface.OnFetchData {

                    override fun onFetch() {
                        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
                            mViewModel.getRequestTypes()
                                .collectLatest { pagingData ->
                                    val result = pagingData.map {
                                        MenuModel(
                                            id = it.id.toString(),
                                            title = it.title
                                        )
                                    }
                                    dialog.updateData(result)
                                }
                        }
                    }
                }, object : MenuInterface.OnResult {

                    override fun onResult(itemResult: MenuModel) {
                        selectedRequestType = itemResult.id
                     //   btnClearList.visibility = View.VISIBLE
                        itemResult.title?.let { it1 ->
                            inputRequestType.selectableInput.setText(it1)
                        }

                    }
                }, object : MenuInterface.OnSearch {
                    override fun onSearch(str: String) {
                    }
                })
                dialog.show(childFragmentManager, "MenuDialogFragmentMyReq")
            }

            btnSearch.setOnClickListener {
                getMyRequestList(selectedRequestType, inputTrackingCode.getNullableValue())
            }

         /*   this.btnRetry.setOnClickListener {
                getMyRequestList(selectedRequestType, inputTrackingCode.getNullableValue())
            }
*/
        }
    }

    private fun showErrorListResult(result: RequestErrorResponse) {
        if (result.isSuccess) {
            if (mViewModel.hasEmptyErrorList()) {
                showAlertDialog(
                    MessageOfRequestDialogFragment.MessageType.INFO,
                    getString(R.string.error_empty_list)
                )
            } else {
                val dialog =
                    ErrorDialogFragment.newInstance(result.data?.list as? ArrayList<RequestError>)
                dialog.show(childFragmentManager, ErrorDialogFragment::javaClass.name)

            }

        }
    }

    private fun showSmartGuidList(result: SmartGuideResponse) {
        if (result.isSuccess) {
            if (mViewModel.hasEmptySmartGuide()) {
                showAlertDialog(
                    MessageOfRequestDialogFragment.MessageType.INFO,
                    getString(R.string.error_empty_smart_guild_list)
                )
            } else {
                val bundle = Bundle()
                bundle.putString(
                    SmartGuideDialogFragment.ARG_ERROR_ITEMS, Gson().toJson(result)
                )
                handlePageDestination(R.id.action_my_request_to_smartGuide, bundle)
            }
        }
    }

    override fun onItemClick(item: MyRequestItem, transitionView: View?, tag: String?) {
        when (tag) {
            getString(R.string.label_show_error) -> {
                item.id?.let {
                    mViewModel.getMyRequestErrorList(item.id)
                }
            }
            getString(R.string.label_smart_guide) -> {
                mViewModel.getSmartGuideList(
                    item.requestType?.id,
                    item.status?.requestCode,
                    true
                )
            }
            getString(R.string.show_request) -> {
                when (item.requestType?.id) {
                    RequestTypeEnumClass.ILL_DAY.serviceId -> {
                        handlePageDestination(
                            R.id.action_my_request_to_short_term_request_info,
                            createBundle(item)
                        )
                    }
                    RequestTypeEnumClass.PREGNANCY.serviceId -> {
                        handlePageDestination(
                            R.id.action_my_request_to_short_term_request_info,
                            createBundle(item)
                        )
                    }
                    RequestTypeEnumClass.ORTHOTICS_PROSTHESIS.serviceId -> {
                        handlePageDestination(
                            R.id.action_my_request_to_short_term_request_info,
                            createBundle(item)
                        )
                    }
                    RequestTypeEnumClass.ARTICLE16.serviceId -> {
                        handlePageDestination(
                            R.id.action_my_request_to_article16_show_request,
                            createBundle(item)
                        )
                    }
                    RequestTypeEnumClass.DEFERRED_INSTALLMENT_CERTIFICATE.serviceId -> {
                        handlePageDestination(
                            R.id.action_my_request_to_deferred_installment_show_request,
                            createBundle(item)
                        )
                    }
                     RequestTypeEnumClass.MEDICAL_COMMISSION.serviceId -> {
                        handlePageDestination(
                            R.id.action_my_request_to_deferred_installment_show_request,
                            createBundle(item)
                        )
                    }


                }
            }

            getString(R.string.follow_up_objection) -> {
                when (item.requestType?.id) {
                    RequestTypeEnumClass.FOLLOW_UP_RESULT_OBJECTION_HISTORY_NONE_EXIST.serviceId -> {
                        handlePageDestination(
                            R.id.action_my_request_to_follow_up_Result_objection_history_none_exist,
                            createBundle(item)
                        )
                    }
                }
            }
        }
    }

    private fun createBundle(item: MyRequestItem) =
        Bundle().apply {
            putString(REFERENCE_ID, item.refrenceid?:item.id.toString())
            putString(TOOLBAR_TITLE, item.title)
            putInt(TOOLBAR_ICON_IMAGE, arguments?.getInt(TOOLBAR_ICON_IMAGE) ?: 0)
            putInt(REQUEST_TYPE, item.requestType?.id ?: -1)
        }
}