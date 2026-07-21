package com.tamin.taminhamrah.ui.home.services.electronicFileServices.myElectronicFileService

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.electronicFile.myElectronicFile.ElectronicFileModel
import com.tamin.taminhamrah.data.remote.models.pdfFile.PdfDownloadResponse
import com.tamin.taminhamrah.databinding.FragmentMyElectronicFileBinding
import com.tamin.taminhamrah.ui.PdfViewerActivity
import com.tamin.taminhamrah.ui.ViewerImageActivity
import com.tamin.taminhamrah.ui.appinterface.ActionAppBarInterface
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.MessageOfRequestDialogFragment
import com.tamin.taminhamrah.ui.home.services.electronicFileServices.myElectronicFileService.adapter.MyElectronicFileServiceAdapter
import com.tamin.taminhamrah.utils.Utility
import com.tamin.taminhamrah.utils.extentions.setupRecycler
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber

@AndroidEntryPoint
class MyElectronicFileFragment :
    BaseFragment<FragmentMyElectronicFileBinding, MyElectronicFileViewModel>(),
    ActionAppBarInterface.OnActionClickListener {

    //region Variables
    private val listAdapter by lazy {
        MyElectronicFileServiceAdapter()
    }
    private val itemListener by lazy {
        object : AdapterInterface.OnItemClickListener<ElectronicFileModel?> {
            override fun onItemClick(
                item: ElectronicFileModel?,
                transitionView: View?,
                tag: String?
            ) {
                showFragment(item)
            }
        }
    }
    var tempTitle = ""
    //endregion

    //region Base Methods
    override val mViewModel: MyElectronicFileViewModel by viewModels()
    override fun getBindingVariable()= Pair(BR.viewModel, mViewModel)
    override fun getLayoutId()= R.layout.fragment_my_electronic_file

    override fun setupObserver() {
        mViewModel.mldMyElectronicFileDocumentFullSize.observe(
            viewLifecycleOwner, ::onDownloadResponse)
    }
    override fun initView() {
        viewDataBinding?.apply {
            listAdapter.initAdapter(token =  mViewModel.getToken(), listener =  itemListener)
            setupRecycler(recycler, listAdapter)
            setupToolbar(
                viewDataBinding?.appBar,
                viewDataBinding?.appbarBackgroundImage?.imageBackground,
                moreViews = null
            )
        }
    }

    override fun getData() {
        this@MyElectronicFileFragment.lifecycleScope.launchWhenCreated {
            mViewModel.getElectronicFile.collectLatest { pagingData ->
                listAdapter.submitData(pagingData)
            }
        }
    }

    override fun onClick() {}

    override fun onActionClick() {}
    //endregion

    //region Listeners
    private fun onDownloadResponse(result: PdfDownloadResponse) {
        if (result.isSuccess) {
            val file = Utility.writeByteStreamToDisk(tempTitle, requireContext(), result.pdf)
            if (file == null) {
                showAlertDialog(
                    MessageOfRequestDialogFragment.MessageType.ERROR,
                    getString(R.string.error_recive_file)
                )
                return
            }
            handlePageDestination(
                R.id.action_myElectronicFile_to_pdf_viewer,
                bundle =  Bundle().apply {
                    putString(PdfViewerActivity.ARG_TITLE, tempTitle)
                    putString(PdfViewerActivity.ARG_PDF_FILE_PATH, file.path)
                }
            )
        }
    }
    //endregion

    //region Utils
    private fun showFragment(item: ElectronicFileModel?) {
        try {
            item?.thumb?.apply {
                if (contains(".tif") || contains(".pdf")) {
                    val path = replace("thumbs", "full-pdf")
                    mViewModel.getMyElectronicFileDocumentFullSize(path)
                } else {
                    val path = replace("thumbs", "full")

                    handlePageDestination(
                        R.id.action_myElectronicFile_to_image_viewer,
                        bundle = Bundle().apply {
                            putString(ViewerImageActivity.URI_IMAGE, path)
                            putString(ViewerImageActivity.TITLE_IMAGE, item.name)
                            putBoolean(ViewerImageActivity.ENABLE_BUTTON_SHARE_AND_DOWNLOAD, true)
                        }
                    )
                }
                tempTitle = item.name ?: "سند"
            }
        } catch (e: java.lang.Exception) {
            Timber.tag("showFragment: ").e(e.message.toString())
        }
    }
    //endregion








}