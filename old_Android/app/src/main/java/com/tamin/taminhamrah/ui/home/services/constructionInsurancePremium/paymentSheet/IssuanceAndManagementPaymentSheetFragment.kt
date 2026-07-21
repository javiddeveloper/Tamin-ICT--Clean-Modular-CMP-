package com.tamin.taminhamrah.ui.home.services.constructionInsurancePremium.paymentSheet

import android.os.Bundle
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.KeyValueModel
import com.tamin.taminhamrah.data.remote.models.pdfFile.PdfDownloadResponse
import com.tamin.taminhamrah.data.remote.models.services.GeneralStringRes
import com.tamin.taminhamrah.data.remote.models.services.constructionInsurancePremium.PaymentSheetConstructionFilesResponse
import com.tamin.taminhamrah.databinding.FragmentIssuanceAndManagementPaymentSheetBinding
import com.tamin.taminhamrah.ui.PdfViewerActivity
import com.tamin.taminhamrah.ui.adapters.ExpandableListAdapter
import com.tamin.taminhamrah.ui.appinterface.DialogClickInterface
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.MessageOfRequestDialogFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.manger.DialogManagerMessageOfRequest
import com.tamin.taminhamrah.ui.home.services.constructionInsurancePremium.ConstructionInsurancePremiumViewModel
import com.tamin.taminhamrah.ui.home.services.constructionInsurancePremium.adapter.PaymentSheetAdapter
import com.tamin.taminhamrah.utils.UiUtils
import com.tamin.taminhamrah.utils.Utility
import com.tamin.taminhamrah.utils.extentions.createBundle
import com.tamin.taminhamrah.utils.extentions.gone
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class IssuanceAndManagementPaymentSheetFragment :
    BaseFragment<FragmentIssuanceAndManagementPaymentSheetBinding, ConstructionInsurancePremiumViewModel>() {

    //region Variables
    override val mViewModel: ConstructionInsurancePremiumViewModel by viewModels()
    private val requestInfoAdapter by lazy {
        ExpandableListAdapter(expandingIndex = 3)
    }
    private val paymentSheetAdapter by lazy {
        PaymentSheetAdapter()
    }
    var debitNumber: String? = null
    var branchCode: String? = null
    var requestInfoList: ArrayList<KeyValueModel>? = null
    //endregion

    //region BaseMethods
    override fun getBindingVariable() = Pair(BR.viewModel, mViewModel)

    override fun getLayoutId() = R.layout.fragment_issuance_and_management_payment_sheet

    override fun setupObserver() {
        mViewModel.mldPaymentSheetConstructionInfo.observe(this, ::omPaymentSheetListResponse)
        mViewModel.mldPDF.observe(this, ::onPdfResponse)
        mViewModel.mldIssuancePaymentSheet.observe(this,::onResponseIssuancePayment)

    }

    override fun initView() {
        viewDataBinding?.apply {
            recycleRequestInfo.apply {
                adapter = requestInfoAdapter
                if (itemDecorationCount == 0) {
                    addItemDecoration(UiUtils.createDivider(requireContext()))
                }
            }
            recyclePaymentSheet.apply {
                adapter = paymentSheetAdapter
                if (itemDecorationCount == 0) {
                    addItemDecoration(UiUtils.VerticalItemMarginDecoration(10))
                }
            }

        }
    }

    override fun getData() {
        debitNumber = arguments?.getString(Constants.DEBIT_NUMBER)
        branchCode = arguments?.getString(Constants.BRANCH_ID)
        requestInfoList = arguments?.getParcelableArrayList(Constants.REQUEST_INFO)
        requestInfoAdapter.setItems(requestInfoList?.toList()?: emptyList())

        debitNumber?.let {
            mViewModel.getPaymentSheetConstructionInfo(it.toString())
        } ?: showAlertDialog(
            MessageOfRequestDialogFragment.MessageType.ERROR,
            getString(R.string.error_recive_data),
            dismissType = MessageOfRequestDialogFragment.DismissType.BACK_TO_PREVIOUS
        )
    }

    override fun onClick() {
        viewDataBinding?.apply {
            btnShowDetail.setOnClickListener {
                requestInfoAdapter.toggleMinifyMode()
                btnShowDetail.text = if (requestInfoAdapter.isMinifyMode())
                    getString(R.string.show_detail) else getString(R.string.hide_detail)
            }
            imageBack.setOnClickListener {
                backButtonPress()
            }
            btnPaymentCertificate.setOnClickListener {
                val debitNum = debitNumber
                val branchId = branchCode
                if (!debitNum.isNullOrBlank() && !branchId.isNullOrBlank()) {
                    mViewModel.getCertificatePaymentSheetPDF(
                        debitNumber = debitNum,
                        branchCode = branchId
                    )
                } else {
                    showAlertDialog(
                        MessageOfRequestDialogFragment.MessageType.ERROR,
                        getString(R.string.error_recive_file)
                    )
                }
            }
            btnIssuancePaymentSheet.setOnClickListener {
                 DialogManagerMessageOfRequest.getInstanceOfDialog().apply {
                     arguments = createBundle(
                         MessageOfRequestDialogFragment.MessageType.CONFIRM,
                         this@IssuanceAndManagementPaymentSheetFragment.getString(R.string.msg_confirm_issuance_payment_sheet),true)
                     setDialogClickListener(object : DialogClickInterface.onClickListener {
                         override fun onConfirmClick() {
                             debitNumber?.let {
                                 mViewModel.issuancePaymentSheet(it)
                             }
                         }

                         override fun onCancelClick() {
                         }
                     })
                }.show(childFragmentManager, "IssuanceAndManagementPaymentSheetFragment")

            }
        }
    }
    //endregion

    //region Listeners
    private fun omPaymentSheetListResponse(result: PaymentSheetConstructionFilesResponse) {
        if (result.isSuccess) {
            val info = result.data?.list ?: emptyList()
            if (info.isNotEmpty()) {
                viewDataBinding?.apply {
                    rootLayout.isVisible = true
                    tvMessage.isVisible = false
                }
                paymentSheetAdapter.setItems(info)
            } else {
                viewDataBinding?.apply {
                    rootLayout.isVisible = true
                    tvMessage.isVisible = true
                    btnPaymentCertificate.gone()
                }
            }
        }
    }

    private fun onPdfResponse(result: PdfDownloadResponse) {
        if (result.isSuccess) {
            val title = Utility.getToolbarTitle(arguments)
            val file = Utility.writeByteStreamToDisk(title, requireContext(), result.pdf)
            if (file == null) {
                showAlertDialog(
                    MessageOfRequestDialogFragment.MessageType.ERROR,
                    getString(R.string.error_recive_file)
                )
                return
            } else {
                handlePageDestination(R.id.action_payment_sheet_to_pdf, Bundle().apply {
                    putString(PdfViewerActivity.ARG_TITLE, title)
                    putString(PdfViewerActivity.ARG_PDF_FILE_PATH, file.path)
                })
            }
        }
    }

    private fun onResponseIssuancePayment(result: GeneralStringRes) {
        if (result.isSuccess){
            showAlertDialog(MessageOfRequestDialogFragment.MessageType.SUCCESS,getString(R.string.msg_desc_issucance_payment_sheet))
        }
    }
    //endregion
}