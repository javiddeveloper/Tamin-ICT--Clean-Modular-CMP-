package com.tamin.taminhamrah.ui.home.services.employer.workshopInfo.debit

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.pdfFile.PdfDownloadResponse
import com.tamin.taminhamrah.data.remote.models.services.workshop.WorkShopDemandDoc
import com.tamin.taminhamrah.databinding.FragmentWorkshopInfoBinding
import com.tamin.taminhamrah.ui.PdfViewerActivity
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.MessageOfRequestDialogFragment
import com.tamin.taminhamrah.ui.home.services.employer.workshopInfo.WorkshopInfoViewModel
import com.tamin.taminhamrah.utils.Utility
import com.tamin.taminhamrah.utils.extentions.setupRecycler
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class WorkshopDemandDocumentsFragment :
    BaseFragment<FragmentWorkshopInfoBinding, WorkshopInfoViewModel>(),
    AdapterInterface.OnItemClickListener<WorkShopDemandDoc> {

    lateinit var listAdapter: WorkshopDemandDocsAdapter
    override val mViewModel: WorkshopInfoViewModel by viewModels()

    override fun getBindingVariable(): Pair<Int, Any?> {
        return Pair(BR.viewModel, mViewModel)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_workshop_info
    }

    override fun setupObserver() {
        mViewModel.mldPdf.observe(this, ::showPDFResult)

    }

    private fun getDebtNumber(): String? {
        return arguments?.getString(mViewModel.ARG_DEBT_NUMBER)
    }

    private fun getBranchCode(): String? {
        return arguments?.getString(Constants.BRANCH_ID)
    }

    override fun initView() {
        listAdapter = WorkshopDemandDocsAdapter(this)
        setupRecycler(viewDataBinding?.recycler, listAdapter)

        setupToolbar(
            viewDataBinding?.appBar,
            viewDataBinding?.appbarBackgroundImage?.imageBackground
        )
    }

    override fun getData() {

//        if (mViewModel.workshopInfoPager == null)
            viewLifecycleOwner.lifecycleScope.launchWhenCreated {
                getDebtNumber()?.let { debtNumber ->
                    getBranchCode()?.let { branchCode ->
                        mViewModel.getWorkshopDemandDocsFlow(debtNumber, branchCode)
                            .collectLatest { pagingData ->
                                listAdapter.submitData(pagingData)
                            }
                    }
                }
            }

    }

    override fun onClick() {

    }


    private fun showPDFResult(result: PdfDownloadResponse) {
        if (result.isSuccess) {
            val file = Utility.writeByteStreamToDisk(Utility.getToolbarTitle(arguments), requireContext(), result.pdf)
            if (file == null) {
                showAlertDialog(
                    MessageOfRequestDialogFragment.MessageType.ERROR,
                    getString(R.string.error_recive_file)
                )
                return
            }
            val bundle = Bundle()
            bundle.putString(PdfViewerActivity.ARG_TITLE,Utility.getToolbarTitle(arguments))
            bundle.putString(PdfViewerActivity.ARG_PDF_FILE_PATH, file.path)
            handlePageDestination(R.id.action_workshop_docs_to_pdf_viewer, bundle)
        }
    }

    override fun onItemClick(item: WorkShopDemandDoc, transitionView: View?, tag: String?) {

        if (tag == getString(R.string.label_show_calculate_details)) {
            getDebtNumber()?.let { debtNumber ->
                getBranchCode()?.let { branchCode ->
                    mViewModel.downloadPdf(debtNumber, branchCode)
                }
            }
        } else if (tag == getString(R.string.label_debit_reason)) {

        }
    }
}