package com.tamin.taminhamrah.ui.home.services.employer.contract.computationalBase

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.Resource
import com.tamin.taminhamrah.data.remote.models.services.contract.ComputationalBase
import com.tamin.taminhamrah.data.remote.models.services.contract.ComputationalBaseSection
import com.tamin.taminhamrah.databinding.FragmentComputationalBaseDetailBinding
import com.tamin.taminhamrah.ui.MainActivity
import com.tamin.taminhamrah.ui.PdfViewerActivity
import com.tamin.taminhamrah.ui.ViewerImageActivity
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.ui.home.services.employer.contract.ContractInfoViewModel
import com.tamin.taminhamrah.utils.UiUtils
import com.tamin.taminhamrah.utils.Utility
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ComputationalBaseDetailFragment :
    BaseFragment<FragmentComputationalBaseDetailBinding, ContractInfoViewModel>(),
    AdapterInterface.OnItemClickListener<ComputationalBase.DataDetail> {

    lateinit var sectionAdapterInfo:ComputationalBaseSectionAdapter
    lateinit var adapterImage:ComputationalBaseImageAdapter

    companion object {
        const val ARG_SELECTED_ITEM = "ARG_SELECTED_ITEM"
    }

    override val mViewModel: ContractInfoViewModel by viewModels()

    override fun getBindingVariable(): Pair<Int, Any?> {
        return Pair(BR.viewModel, mViewModel)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_computational_base_detail
    }

    override fun setupObserver() {
        mViewModel.mldComputationalBaseList.observe(viewLifecycleOwner, ::onResult)
        mViewModel.mldPdf.observe(this, ::showPDFResult)
    }

    override fun initView() {

        viewDataBinding?.apply {
            setupToolbar(
                appBar,
                appbarBackgroundImage.imageBackground
            )

            sectionAdapterInfo = ComputationalBaseSectionAdapter(this@ComputationalBaseDetailFragment)
            recyclerInfo.apply {
                if (itemDecorationCount == 0) {
                    addItemDecoration(UiUtils.BackgroundItemDecorationDrowable(requireContext()))
                }
                this.adapter = sectionAdapterInfo
            }
        }
    }

    override fun getData() {
        mViewModel.getComputationalBaseList(getItem())
    }


    override fun onClick() {
    }

    private fun getItem(): ComputationalBase? {
        return arguments?.getParcelable(ARG_SELECTED_ITEM) as? ComputationalBase
    }


    private fun showFragment(item: ComputationalBase.DataDetail?) {
        /*  item?.thumb?.apply {
              if (contains(".tif") || contains(".pdf")) {
                  *//* insuranceNumber?.let {
                     mViewModel.getFullSizeMyElectronicPdfFile(
                         nationalId + item.id,
                         it
                     )
                 }*//*
            } else {
                val path = replace("thumbs", "full")
                val bundle = Bundle()
                bundle.putString(ViewerImageActivity.URI_IMAGE, path)
                bundle.putString(ViewerImageActivity.TITLE_IMAGE, item.name)
                bundle.putBoolean(ViewerImageActivity.ENABLE_BUTTON_SHARE_AND_DOWNLOAD, true)
                handlePageDestination(
                    R.id.action_myElectronicFile_to_image_viewer,
                    bundle = bundle
                )
            }
            //  tempTitle = item.name ?: "سند"
        }*/
    }

    override fun onItemClick(
        item: ComputationalBase.DataDetail,
        transitionView: View?,
        tag: String?
    ) {
        when(item.documentType){
            "1"->{
                val bundle = Bundle()
                bundle.putString(ViewerImageActivity.TITLE_IMAGE, tag)
                bundle.putString(ViewerImageActivity.IMAGE, item.attachmentUrl)
                handlePageDestination(R.id.action_computational_base_to_image_preview, bundle)
            }
            "2"->{
                item.documentId?.let { documentId-> tag?.let { tag ->
                    mViewModel.downloadComputationalBasePdf(documentId,
                        tag
                    )
                } }
            }
        }

    }

    private fun onResult(result: MutableList<ComputationalBaseSection>) {
        sectionAdapterInfo.setItems(result)

    }

    private fun showPDFResult(result: Resource<String?>) {
        (requireActivity() as? MainActivity)?.handleResponse(result)
        if (result.status == Resource.Status.SUCCESS) {
            //Utility.openPdfFile(requireActivity(), File(result.data!!))
            val bundle = Bundle()
            bundle.putString(PdfViewerActivity.ARG_TITLE, Utility.getToolbarTitle(arguments))
            bundle.putString(PdfViewerActivity.ARG_PDF_FILE_PATH, result.data)
            handlePageDestination(R.id.action_computational_base_to_pdf_viewer, bundle)

        }
    }
}