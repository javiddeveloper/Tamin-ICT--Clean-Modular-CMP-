package com.tamin.taminhamrah.ui.home.services.inspectionsPlaceEmployment.viewInspectionsPerformedByOrganization

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.pdfFile.PdfDownloadResponse
import com.tamin.taminhamrah.databinding.FragmentListOfInspectionsPerformedBinding
import com.tamin.taminhamrah.ui.PdfViewerActivity
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.MessageOfRequestDialogFragment
import com.tamin.taminhamrah.ui.home.services.inspectionsPlaceEmployment.adapter.InspectionsPerformedAdapter
import com.tamin.taminhamrah.utils.Utility
import com.tamin.taminhamrah.utils.extentions.setupRecycler
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber

@AndroidEntryPoint
class ListOfInspectionsPerformedFragment :
    BaseFragment<FragmentListOfInspectionsPerformedBinding, ListOfInspectionsPerformedViewModel>() {

    companion object{
        const val ARG_IS_WORKSHOP="ARG_IS_WORKSHOP"
    }

    //Class variables
    override val mViewModel: ListOfInspectionsPerformedViewModel by viewModels()

    val listAdapter by lazy {
        InspectionsPerformedAdapter(onDownloadListener)
    }

    val onDownloadListener by lazy {
        object : AdapterInterface.OnDownloadClickListener<String> {
            override fun onDownload(item: String, transitionView: View?, tag: String?) {
                mViewModel.getPerformedInspectionPdf(item)
            }
        }
    }

    val checkWorkshopInspection by lazy { arguments?.getBoolean(ARG_IS_WORKSHOP)?:false }

    //Base methods
    override fun getBindingVariable() = Pair(BR.viewModel, mViewModel)

    override fun getLayoutId() = R.layout.fragment_list_of_inspections_performed

    override fun setupObserver() {
        mViewModel.mldPdf.observe(this, ::onDownloadPdfResponse)
    }

    override fun initView() {
        viewDataBinding?.apply {
            setupRecycler(recycler, listAdapter)
            setupToolbar(
                appBar,
                appbarBackgroundImage.imageBackground,
                moreViews = null
            )
            labelDetail.tvHelp.text = getString(R.string.label_details_inspection_preformed)
        }
    }

    override fun getData() {
        lifecycleScope.launchWhenCreated {
            Timber.tag("debugPagination: ").i(" Called:GetData ")

            this@ListOfInspectionsPerformedFragment.lifecycleScope.launchWhenCreated {

                if (checkWorkshopInspection){
                    mViewModel.getWorkshopListInspectionPerformed.collectLatest { pagingData ->
                        listAdapter.submitData(pagingData)
                    }
                }else {
                    mViewModel.getListInspectionPerformed.collectLatest { pagingData ->
                        listAdapter.submitData(pagingData)
                    }
                }
            }
        }
    }

    override fun onClick() {
        viewDataBinding?.apply {
            btnAddInspection.setOnClickListener {
                handlePageDestination(
                    R.id.action_listOfInspectionsPerformedFragment_to_submitInspectionRequestFragment,
                    Bundle().apply {
                        putString(
                            PdfViewerActivity.ARG_TITLE,
                            Utility.getToolbarTitle(arguments)
                        )
                        putString(
                            Constants.TOOLBAR_ICON_IMAGE,
                            Utility.getToolbarIconImage(arguments)
                        )
                    }
                )
            }
        }
    }

    //Listeners
    private fun onDownloadPdfResponse(result: PdfDownloadResponse) {
        if (result.isSuccess) {
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
            handlePageDestination(
                R.id.action_inspections_performed_to_pdf_viewer,
                Bundle().apply {
                    putString(PdfViewerActivity.ARG_TITLE, Utility.getToolbarTitle(arguments))
                    putString(PdfViewerActivity.ARG_PDF_FILE_PATH, file.path)
                })
        }
    }
    //Utils
}
