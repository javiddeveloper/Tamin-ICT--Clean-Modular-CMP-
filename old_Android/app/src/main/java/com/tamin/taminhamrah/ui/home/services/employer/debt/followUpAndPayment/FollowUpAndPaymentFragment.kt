package com.tamin.taminhamrah.ui.home.services.employer.debt.followUpAndPayment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.cachedIn
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.Constants.BRANCH_CODE
import com.tamin.taminhamrah.Constants.DEBIT_SERIAL_NUMBER
import com.tamin.taminhamrah.Constants.DEBT_NUMBER
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.remote.models.pdfFile.PdfDownloadResponse
import com.tamin.taminhamrah.data.remote.models.services.workshop.WorkShopDebtModel
import com.tamin.taminhamrah.databinding.FragmentFollowUpAndPaymentBinding
import com.tamin.taminhamrah.ui.PdfViewerActivity
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.appinterface.MenuInterface
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.ui.dialog.MenuDialogFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.MessageOfRequestDialogFragment
import com.tamin.taminhamrah.ui.home.services.employer.debt.adapter.InstallmentListAdapter
import com.tamin.taminhamrah.ui.home.services.employer.debt.bottomSheet.DebtSearchBottomSheet
import com.tamin.taminhamrah.ui.home.services.employer.debt.viewModel.InstallmentDebtViewModel
import com.tamin.taminhamrah.utils.Utility
import com.tamin.taminhamrah.utils.extentions.setupRecycler
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FollowUpAndPaymentFragment :
    BaseFragment<FragmentFollowUpAndPaymentBinding, InstallmentDebtViewModel>() {
    override val mViewModel: InstallmentDebtViewModel by viewModels()
    override fun getBindingVariable() = Pair(BR.viewModel, mViewModel)
    override fun getLayoutId() = R.layout.fragment_follow_up_and_payment
    val listAdapter by lazy { InstallmentListAdapter(onInstallmentClickListener) }

    private val onInstallmentClickListener =
        object : AdapterInterface.OnItemClickListener<WorkShopDebtModel> {
            override fun onItemClick(item: WorkShopDebtModel, transitionView: View?, tag: String?) {
                val dialog = MenuDialogFragment.newInstance()
                dialog.setMenuListener(object : MenuInterface.OnFetchData {
                    override fun onFetch() {
                        this@FollowUpAndPaymentFragment.lifecycleScope.launch {
                            //finishCode== 0 ->"در انتظار پرداخت اقساط"
                            //finishCode== 3 ->"تایید درخواست"
                            val actionList = mViewModel.getActionList(isNotPaid = item.finishCode == "0" || item.finishCode=="3")
                            actionList.flow.cachedIn(lifecycleScope).collectLatest {pagingData->
                                dialog.updateData(pagingData)
                            }
                        }
                    }
                }, object : MenuInterface.OnResult {

                    override fun onResult(menu: MenuModel) {
                        when (menu.title) {
                            "پرداخت قسط" -> {//payment
                                handlePageDestination(
                                    R.id.action_followAndPayment_to_PaymentDebtFragment,
                                    Bundle().apply {
                                        putString(DEBT_NUMBER,item.debitNumber)
                                        putString(BRANCH_CODE,item.branchCode)
                                        putString(DEBIT_SERIAL_NUMBER,item.serialNo.toString())
                                        putString(Constants.TOOLBAR_TITLE, getString(R.string.label_history_payment))
                                        putInt(Constants.TOOLBAR_ICON_IMAGE, R.drawable.ic_contract_list)
                                    }
                                )
                            }
                            "مشاهده پرداخت ها" -> {//payment report
                                    handlePageDestination(
                                        R.id.action_followAndPayment_to_paidDebtList,
                                        bundle = Bundle().apply {
                                            putString(
                                                Constants.TOOLBAR_TITLE,
                                                getString(R.string.label_history_payment)
                                            )
                                            putInt(
                                                Constants.TOOLBAR_ICON_IMAGE,
                                                R.drawable.ic_contract_list
                                            )
                                            putLong(
                                                Constants.DEBIT_SERIAL_NUMBER,
                                                item.serialNo?:0
                                            )
                                })
                            }
                            "مشاهده درخواست تقسیط" ->
                                mViewModel.downloadInstallmentReport(item.letterNumber ?: "")
                        }

                    }
                }
                )
                dialog.show(childFragmentManager, "contractListMenu")
            }
        }

    override fun setupObserver() {
        mViewModel.mldPdf.observe(this, ::onDownloadPdfFileResponse)
    }

    override fun initView() {
        setupRecycler(viewDataBinding?.recycler, listAdapter)
        setupToolbar(
            viewDataBinding?.appBar,
            viewDataBinding?.appbarBackgroundImage?.imageBackground,
            actionIconRes = R.drawable.ic_search
        )
    }

    override fun getData() {
        getAllInstallmentList()
    }

    private fun getAllInstallmentList(workshopId: String = "", letterDate: String = "") {
        lifecycleScope.launch {
            mViewModel.getAllInstallmentList(workshopId, letterDate).collectLatest { data ->
                listAdapter.submitData(data)
            }
        }
    }


    override fun onClick() {
        viewDataBinding?.apply {
            appBar.toolbar.imgAction.setOnClickListener {
                workshopSearchBottomSheet.show(childFragmentManager, "")
            }
        }
    }


    private val workshopSearchBottomSheet by lazy {
        val bundle = Bundle()
        bundle.putSerializable(
            DebtSearchBottomSheet.DIALOG_TYPE,
            DebtSearchBottomSheet.SearchType.LETTER_DATE
        )
        val dialog = DebtSearchBottomSheet(workshopSearchListener)
        dialog.arguments = bundle
        dialog
    }
    private val workshopSearchListener = object : DebtSearchBottomSheet.WorkshopSearchListener {
        override fun onWorkshopSearchListener(workshopId: String, agreementRow: String) {
            getAllInstallmentList(workshopId, agreementRow)

        }

    }

    private fun onDownloadPdfFileResponse(result: PdfDownloadResponse) {
        if (!result.isSuccess)
            return
        val file = Utility.writeByteStreamToDisk(
            Utility.getToolbarTitle(arguments),
            requireContext(),
            result.pdf
        )
        if (file == null) {
            showAlertDialog(
                MessageOfRequestDialogFragment.MessageType.ERROR,
                getString(R.string.error_recive_file)
            )
            return
        }
        handlePageDestination(R.id.action_followAndPayment_to_Activity_pdf_view, Bundle().apply {
            putString(PdfViewerActivity.ARG_TITLE, Utility.getToolbarTitle(arguments))
            putString(PdfViewerActivity.ARG_PDF_FILE_PATH, file.path)
        })
    }
}
