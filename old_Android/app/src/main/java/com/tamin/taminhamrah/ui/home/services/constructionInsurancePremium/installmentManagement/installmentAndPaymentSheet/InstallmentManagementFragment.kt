package com.tamin.taminhamrah.ui.home.services.constructionInsurancePremium.installmentManagement.installmentAndPaymentSheet

import android.os.Bundle
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.pdfFile.PdfDownloadResponse
import com.tamin.taminhamrah.data.remote.models.services.GeneralStringRes
import com.tamin.taminhamrah.data.remote.models.services.constructionInsurancePremium.PaymentSheetConstructionFilesResponse
import com.tamin.taminhamrah.databinding.FragmentInstallmentManagmentBinding
import com.tamin.taminhamrah.ui.PdfViewerActivity
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.MessageOfRequestDialogFragment
import com.tamin.taminhamrah.ui.home.services.constructionInsurancePremium.ConstructionInsurancePremiumViewModel
import com.tamin.taminhamrah.ui.home.services.constructionInsurancePremium.adapter.InstallmentManagementAdapter
import com.tamin.taminhamrah.ui.home.services.constructionInsurancePremium.adapter.PaymentSheetAdapter
import com.tamin.taminhamrah.utils.Utility
import com.tamin.taminhamrah.utils.extentions.gone
import com.tamin.taminhamrah.utils.extentions.setupRecycler
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class InstallmentManagementAndPaymentSheetFragment :
    BaseFragment<FragmentInstallmentManagmentBinding, ConstructionInsurancePremiumViewModel>() {

    //region Variables
    override val mViewModel: ConstructionInsurancePremiumViewModel by viewModels()
    private val installmentListAdapter by lazy {
        InstallmentManagementAdapter()
    }
    private val paymentSheetAdapter by lazy {
        PaymentSheetAdapter()
    }

    //endregion

    //region BaseMethods
    override fun getBindingVariable() = Pair(BR.viewModel, mViewModel)

    override fun getLayoutId() = R.layout.fragment_installment_managment

    override fun setupObserver() {
    }

    override fun initView() {
        viewDataBinding?.apply {
            setupRecycler(recycleInstallmentList,installmentListAdapter)

        }
    }

    override fun getData() {
        arguments?.apply {
            val debitNumber = getString(Constants.DEBIT_NUMBER)
            val branchCode = getString(Constants.BRANCH_ID)
            val oldDebitNumber = getString(Constants.OLD_DEBIT_NUMBER)

            if (debitNumber != null && branchCode != null && oldDebitNumber != null) {
                viewLifecycleOwner.lifecycleScope.launchWhenCreated {
                 //   mViewModel.getInstallmentManagementInfo(debitNumber = debitNumber, branchId = branchCode,oldDebitNumber = oldDebitNumber)

                    mViewModel.getInstallmentList(debitNumber = debitNumber, branchId = branchCode)
                        .collectLatest { pagingDate ->
                            viewDataBinding?.rootLayout?.isVisible = true
                            installmentListAdapter.submitData(pagingDate)
                        }
                }
              /*  viewLifecycleOwner.lifecycleScope.launchWhenCreated {
                    mViewModel.updateInstallmentConstruction(oldDebitNumber = oldDebitNumber)
                }*/

                } else {
                    showAlertDialog(
                        MessageOfRequestDialogFragment.MessageType.ERROR,
                        getString(R.string.error_recive_data),
                        dismissType = MessageOfRequestDialogFragment.DismissType.BACK_TO_PREVIOUS
                    )

                }

            }
        }



    override fun onClick() {
        viewDataBinding?.apply {
          /*  btnShowDetail.setOnClickListener {
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

            }*/
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
            showAlertDialog(MessageOfRequestDialogFragment.MessageType.CONFIRM,getString(R.string.msg_desc_issucance_payment_sheet))
        }
    }
    //endregion
}