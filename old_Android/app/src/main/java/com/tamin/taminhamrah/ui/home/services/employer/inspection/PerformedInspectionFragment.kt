package com.tamin.taminhamrah.ui.home.services.employer.inspection

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.Resource
import com.tamin.taminhamrah.data.remote.models.services.insuredInspectionPerformed.InspectionPerformedModel
import com.tamin.taminhamrah.databinding.FragmentPerfomedInspectionBinding
import com.tamin.taminhamrah.ui.MainActivity
import com.tamin.taminhamrah.ui.PdfViewerActivity
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.utils.Utility
import com.tamin.taminhamrah.utils.extentions.setupRecycler
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class PerformedInspectionFragment :
    BaseFragment<FragmentPerfomedInspectionBinding, PerformedInspectionViewModel>(),
    AdapterInterface.OnItemClickListener<InspectionPerformedModel?> {

    private val listAdapter: PerformedInspectionAdapter by
    lazy { PerformedInspectionAdapter(this@PerformedInspectionFragment) }

    override val mViewModel: PerformedInspectionViewModel by viewModels()

    override fun getBindingVariable(): Pair<Int, Any?> {
        return Pair(BR.viewModel, mViewModel)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_perfomed_inspection
    }

    override fun setupObserver() {
        mViewModel.mldPdf.observe(this, ::showResultPdf)

    }

    override fun initView() {
        viewDataBinding?.apply {
            setupRecycler(recycler, listAdapter)
            setupToolbar(
                appBar,
                appbarBackgroundImage.imageBackground,
                moreViews = null
            )
            labelDetail.tvHelp.text = "${getString(R.string.label_workshop_inspection_desc_1)} ${getString(R.string.label_workshop_inspection_desc_2)}"
        }
    }

    override fun getData() {

        this@PerformedInspectionFragment.lifecycleScope.launchWhenCreated {

            mViewModel.getPerformedInspectionList()
                .collectLatest { pagingData -> listAdapter.submitData(pagingData) }
        }

    }

    override fun onClick() {

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

    override fun onItemClick(item: InspectionPerformedModel?, transitionView: View?, tag: String?) {
        when(tag){
            getString(R.string.label_see_detail)->{
                item?.inspectionNo?.let { mViewModel.getPerformedInspectionPdf(it) }
            }
            getString(R.string.label_register_objection)->{

                val bundle = Bundle()
                bundle.putString(Constants.ARG_VAL_INSURANCE_ID, item?.insuranceNo)
                bundle.putString(Constants.ARG_VAL_INSPECTION_CODE, item?.inspectionNo)
                bundle.putString(Constants.TOOLBAR_TITLE, Utility.getToolbarTitle(arguments))
                bundle.putString(Constants.TOOLBAR_ICON_IMAGE, Utility.getToolbarIconImage(arguments))

                handlePageDestination(
                    R.id.action_objectionList_to_submitInspectionRequest, bundle)
            }
        }

    }
}