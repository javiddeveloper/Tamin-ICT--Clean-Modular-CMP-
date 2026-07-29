package com.tamin.taminhamrah.ui.home.services.employer.inspection

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.BaseListResponse
import com.tamin.taminhamrah.data.remote.models.Resource
import com.tamin.taminhamrah.data.remote.models.services.performedInspections.InspectionResponse
import com.tamin.taminhamrah.databinding.FragmentPerfomedInspectionBinding
import com.tamin.taminhamrah.ui.MainActivity
import com.tamin.taminhamrah.ui.PdfViewerActivity
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.appinterface.DialogResultInterface
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.utils.Utility
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PerformedInspectionFragment2 :
    BaseFragment<FragmentPerfomedInspectionBinding, PerformedInspectionViewModel>(),
    AdapterInterface.OnItemClickListener<InspectionResponse>,
    DialogResultInterface.OnResultListener<Map<String, String>> {

    lateinit var listAdapter: PerformedInspectionAdapter2

    override val mViewModel: PerformedInspectionViewModel by viewModels()

    override fun getBindingVariable(): Pair<Int, Any?> {
        return Pair(BR.viewModel, mViewModel)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_perfomed_inspection
    }

    override fun setupObserver() {
        mViewModel.mldPerformedInspection.observe(this, ::showResult)
        mViewModel.mldPdf.observe(this, ::showResultPdf)

    }

    override fun initView() {

        listAdapter = PerformedInspectionAdapter2()
        /*viewDataBinding?.widgetRecycler?.apply {
            getRecycler(setLoadMoreAction()).adapter = listAdapter
        }*/

        setupToolbar(
            viewDataBinding?.appBar,
            viewDataBinding?.appbarBackgroundImage?.imageBackground,
            actionIconRes = R.drawable.ic_search
        )
    }

    override fun getData() {
        mViewModel.getPerformedInspectionList()
    }

    override fun onClick() {
        /*viewDataBinding?.let {
            it.appBar.toolbar.apply {
                imgAction.setOnClickListener {
                    val dialog = WorkshopSearchDialogFragment()
                    dialog.setListener(this@PerformedInspectionFragment)
                    dialog.show(childFragmentManager, "jfhskljkjllkljl")
                }
            }
        }*/
    }


    private fun showResult(result: Resource<BaseListResponse<InspectionResponse>?>?) {
        /*(requireActivity() as? MainActivity)?.handleResponse(
            result,
            recyclerView = viewDataBinding?.widgetRecycler
        )*/

        if (result?.status == Resource.Status.SUCCESS) {
            result.data?.list?.let {
         //       listAdapter.setItems(it, this@PerformedInspectionFragment2)
            }
        }
    }

    private fun showResultPdf(result: Resource<String?>) {

        (requireActivity() as? MainActivity)?.handleResponse(result)
        if (result?.status == Resource.Status.SUCCESS) {
            //Utility.openPdfFile(requireActivity(), File(result.data!!))
            val bundle = Bundle()
            bundle.putString(PdfViewerActivity.ARG_TITLE, Utility.getToolbarTitle(arguments))
            bundle.putString(PdfViewerActivity.ARG_PDF_FILE_PATH, result.data)
            handlePageDestination(R.id.action_performed_inspection_to_pdf_viewer, bundle)

        }
    }

    override fun onItemClick(item: InspectionResponse, transitionView: View?, tag: String?) {
       // item.inspectionNo?.let { mViewModel.getPerformedInspectionPdf(it) }
    }

    override fun onDialogResult(item: Map<String, String>) {
        /*  val workshopId = item[mViewModel.ARG_WORKSHOP_ID] ?: ""
          val branchCode = item[mViewModel.ARG_BRANCH_CODE] ?: ""
          mViewModel.getWorkshop(workshopId = workshopId, branchCode = branchCode)*/
    }
}