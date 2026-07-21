package com.tamin.taminhamrah.ui.home.services.inquiryStudyCode

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.map
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.remote.models.services.GeneralStringRes
import com.tamin.taminhamrah.data.remote.models.services.showAndAddDependent.DependentInfoResponse
import com.tamin.taminhamrah.databinding.FragmentInquiryStudyCodeBinding
import com.tamin.taminhamrah.ui.LocalPagingSource
import com.tamin.taminhamrah.ui.appinterface.MenuInterface
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.ui.dialog.MenuDialogFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.MessageOfRequestDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class InquiryStudyCodeFragment :
    BaseFragment<FragmentInquiryStudyCodeBinding, InquiryStudyCodeViewModel>() {

    //Class variables
    override val mViewModel: InquiryStudyCodeViewModel by viewModels()

    //Base Methods
    override fun getBindingVariable() = Pair(BR.viewModel, mViewModel)

    override fun getLayoutId() = R.layout.fragment_inquiry_study_code

    override fun setupObserver() {
        mViewModel.mldCheckRenewCondition.observe(this, ::onCheckConditionRenewResponse)
        mViewModel.mldInquiryStudyCode.observe(this, ::onInquiryStudyCodeResponse)
    }

    override fun initView() {
        setupToolbar(viewDataBinding?.appBar,
            viewDataBinding?.appbarBackgroundImage?.imageBackground)
    }

    override fun getData() {
        mViewModel.checkRenewCondition()
    }

    override fun onClick() {
        viewDataBinding?.apply {

            selectSon.getIt().setOnClickListener {
                MenuDialogFragment().apply {
                    val bundle = Bundle()
                    bundle.putString(MenuDialogFragment.ARG_MENU_TITLE,
                        this@InquiryStudyCodeFragment.getString(R.string.son))
                    arguments = bundle
                    setMenuListener(object : MenuInterface.OnFetchData {
                        override fun onFetch() {
                            this@InquiryStudyCodeFragment.lifecycleScope.launchWhenCreated {
                                val pager = Pager(
                                    config = PagingConfig(Constants.QUERY_PAGE_SIZE_10, 2),
                                    pagingSourceFactory = { LocalPagingSource(mViewModel.sonList) })
                                pager.flow.collectLatest { pagingData ->
                                    updateData(pagingData.map { dependent ->
                                        MenuModel(id = dependent.dependentInfo.identityInfo.nationalId,
                                            title = "${dependent.dependentInfo.identityInfo.firstName} ${dependent.dependentInfo.identityInfo.lastName}")

                                    })
                                }
                            }
                        }
                    }, object : MenuInterface.OnResult {
                        override fun onResult(itemResult: MenuModel) {
                            selectSon.getLayout().isErrorEnabled = false
                            selectSon.setValue(itemResult.title ?: "")
                            mViewModel.selectedDependent = itemResult.id ?: ""
                        }

                    })
                }.show(childFragmentManager, "InquiryStudyCodeFragment")
            }

            btnInquiry.setOnClickListener {
                when {
                    selectSon.getValue(false).isBlank() && selectSon.isVisible ->
                        selectSon.getLayout().error = getString(R.string.error_select_son)

                    edStudyCode.getValue().isBlank() || edStudyCode.getLayout().isErrorEnabled ->
                        edStudyCode.getLayout().error =
                            getString(R.string.error_inquiry_study_code)
                    else ->
                        mViewModel.inquiryStudyCodeCertificate(code = mViewModel.selectedDependent,
                            edStudyCode.getValue())
                }
            }
        }
    }

    //Listeners
    private fun onCheckConditionRenewResponse(result: DependentInfoResponse) {
        if (result.isSuccess) {
            viewDataBinding?.apply {
                subLayout.visibility = View.VISIBLE
                if (result.data?.list.isNullOrEmpty()) {
                    selectSon.visibility = View.GONE
                } else {
                    selectSon.visibility = View.VISIBLE
                    mViewModel.sonList.clear()
                    mViewModel.sonList.addAll(result.data?.list ?: emptyList())
                    if (mViewModel.sonList.size == 1) {
                        selectSon.apply {
                            getIt().setOnClickListener(null)
                            setValue("${mViewModel.sonList[0].dependentInfo.identityInfo.firstName} ${mViewModel.sonList[0].dependentInfo.identityInfo.lastName}")
                            mViewModel.selectedDependent =
                            mViewModel.sonList[0].dependentInfo.identityInfo.nationalId ?: ""
                            hideDrawable()
                        }
                    }
                }
            }
        }
    }

    private fun onInquiryStudyCodeResponse(result: GeneralStringRes) {
        if (result.isSuccess) {
            if (result.data.isNullOrBlank())
                showAlertDialog(MessageOfRequestDialogFragment.MessageType.INFO,
                    getString(R.string.not_find_info_inquiry_study_code))
            else
                showAlertDialog(MessageOfRequestDialogFragment.MessageType.SUCCESS,
                    getString(R.string.success_inquiry_study_code, result.data),
                    MessageOfRequestDialogFragment.DismissType.BACK_TO_PREVIOUS)
        }
    }

//Utils


}