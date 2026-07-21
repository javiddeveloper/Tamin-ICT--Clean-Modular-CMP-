package com.tamin.taminhamrah.ui.home.services.issuanceWageCertificate

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.map
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.remote.models.services.GeneralRes
import com.tamin.taminhamrah.data.remote.models.services.PensionerIdResponse
import com.tamin.taminhamrah.data.remote.models.services.asDomainModel
import com.tamin.taminhamrah.databinding.FragmentIssuanceWageCertificateBinding
import com.tamin.taminhamrah.ui.LocalPagingSource
import com.tamin.taminhamrah.ui.appinterface.DialogClickInterface
import com.tamin.taminhamrah.ui.appinterface.MenuInterface
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.ui.dialog.MenuDialogFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.MessageOfRequestDialogFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.manger.DialogManagerMessageOfRequest
import com.tamin.taminhamrah.ui.home.services.viewEdictPensioner.EdictPensionerFragment
import com.tamin.taminhamrah.utils.Utility
import com.tamin.taminhamrah.utils.extentions.createBundle
import com.tamin.taminhamrah.utils.extentions.isNumericString
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class IssuanceWageCertificateFragment :
    BaseFragment<FragmentIssuanceWageCertificateBinding, IssuanceWageCertificateViewModel>() {

    override val mViewModel: IssuanceWageCertificateViewModel by viewModels()
    private var recipientCode: String = ""

    override fun getBindingVariable(): Pair<Int, Any?> {
        return Pair(BR.viewModel, mViewModel)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_issuance_wage_certificate
    }

    override fun setupObserver() {
        mViewModel.mldCertificateResponse.observe(this, ::showCertificateResult)
        mViewModel.mldPensionerIdList.observe(this, ::showResultPensionerId)
    }

    private fun showResultPensionerId(result: PensionerIdResponse) {
        if (result.isSuccess) {
            mViewModel.pensionIdModelList.apply {
                clear()
                addAll(result.data?.list ?: emptyList())
                if (isNotEmpty()) {
                    viewDataBinding?.selectPensionId?.setValue(get(0).pensionerId ?: "")
                }
                if (size <= 1) {
                    viewDataBinding?.selectPensionId?.getIt()?.setOnClickListener(null)
                    viewDataBinding?.selectPensionId?.hideDrawable()
                }
            }
        }
    }

    override fun initView() {
        setupToolbar(
            viewDataBinding?.appBar,
            viewDataBinding?.appbarBackgroundImage?.imageBackground,
        )
        onClick()
    }

    override fun getData() {
        mViewModel.getPensionerIdList()
    }

    override fun onClick() {
        viewDataBinding?.apply {
            selectReceiver.apply {
                getIt().setOnClickListener {
                    getLayout().isErrorEnabled = false
                    getIt().error = null
                    val dialog = MenuDialogFragment.newInstance(true,  getString(R.string.recipient_certificate_hint))
                    dialog.setMenuListener(object : MenuInterface.OnFetchData {

                        override fun onFetch() {
                            this@IssuanceWageCertificateFragment.lifecycleScope.launchWhenCreated {
                                mViewModel.getReceiverListFlow(null).collectLatest { pagingData ->
                                    val result = pagingData.map { it.asDomainModel() }
                                    dialog.updateData(result)
                                }
                            }
                        }

                    }, object : MenuInterface.OnResult {

                        override fun onResult(itemResult: MenuModel) {
                            itemResult.title?.let {
                                recipientCode = itemResult.id!!
                                viewDataBinding?.selectReceiver?.setValue(it)
                            }
                        }
                    },object : MenuInterface.OnSearch{
                        override fun onSearch(str: String) {
                            this@IssuanceWageCertificateFragment.lifecycleScope.launchWhenCreated {
                                mViewModel.getReceiverListFlow(branchName = str)
                                    .collectLatest { pagingData ->
                                        val result = pagingData.map {
                                            MenuModel(
                                                id = it.recipientCode,
                                                title = it.recipientName
                                            )
                                        }

                                        dialog.updateData(result)
                                    }
                            }
                        }
                    })
                    dialog.show(childFragmentManager, "ytyuyy")

                }
            }

            selectPensionId.getIt().setOnClickListener {
                view?.windowToken?.let { Utility.hideKeyboard(requireContext(), it) }
                val dialog = MenuDialogFragment.newInstance(true , getString(R.string.pension_number))

                dialog. setMenuListener(object : MenuInterface.OnFetchData {
                    override fun onFetch() {
                        this@IssuanceWageCertificateFragment.lifecycleScope.launchWhenCreated {
                            Pager(
                                config = PagingConfig(Constants.QUERY_PAGE_SIZE_10, 2),
                                pagingSourceFactory = {
                                    LocalPagingSource(mViewModel.pensionIdModelList)
                                }
                            ).flow.cachedIn(lifecycleScope).collectLatest {paginData->
                                dialog.updateData(paginData.map {
                                    MenuModel(id = it.pensionerId, title = it.pensionerId)
                                })
                            }
                        }
                    }
                }, object : MenuInterface.OnResult {
                    override fun onResult(itemResult: MenuModel) {
                        selectPensionId.getLayout().isErrorEnabled = false
                        selectPensionId.setValue(itemResult.id ?: "")
                    }
                })

                dialog.  show(childFragmentManager, EdictPensionerFragment().javaClass.simpleName)

            }

            btnCertificateSendToInbox.setOnClickListener {
                var branchName = layoutBranchName.getInput().text.toString()
                if (selectPensionId.getValue(false).isNullOrBlank()) {
                    selectPensionId.getIt().error =
                        getString(R.string.error_select_pensioner_id)
                } else if (selectReceiver.getValue().isBlank()) {
                    selectReceiver.getLayout().error =
                        getString(R.string.error_recipient_select)
                } else if (!Utility.checkInputIsValidBranch(branchName)) {
                    layoutBranchName.getLayout().error =
                        getString(R.string.error_input_is_branch_name_not_valid)
                } else if (branchName.length < 2 && !branchName.isNumericString()) {
                    layoutBranchName.getLayout().error =
                        getString(R.string.error_input_is_less_then_2)
                } else {
                    if (branchName.isNotBlank() && !branchName.contains("شعبه")) {
                        branchName = " شعبه $branchName"
                    }
                    mViewModel.sendCertificateRequest(
                        selectPensionId.getValue(false),
                        recipientCode,
                        branchName
                    )
                }
            }
        }
    }

    private fun showCertificateResult(result: GeneralRes) {
        if (result.isSuccess) {
            val dialog = DialogManagerMessageOfRequest.getInstanceOfDialog()
            dialog.arguments = createBundle(
                MessageOfRequestDialogFragment.MessageType.SUCCESS,
                getString(R.string.message_success_send_issuance_wage_certificate)
            )

            dialog.setDialogClickListener(object : DialogClickInterface.onClickListener {
                override fun onConfirmClick() {
                    requireActivity().onBackPressed()
                }

                override fun onCancelClick() {
                }
            })
            dialog.show(childFragmentManager, "Alert Dialog MessageOfRequest")
        }
    }
}