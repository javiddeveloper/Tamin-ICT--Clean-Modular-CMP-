package com.tamin.taminhamrah.ui.mytamin.myrequest.showRequestInfo

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.DownloadFileModel
import com.tamin.taminhamrah.data.entity.DownloadFileResponse
import com.tamin.taminhamrah.data.entity.UploadedImageModel
import com.tamin.taminhamrah.databinding.FragmentShowRequestInfoBinding
import com.tamin.taminhamrah.ui.ViewerImageActivity
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.ui.home.services.ImagePreviewAdapter

abstract class ShowRequestBaseFragment :
    BaseFragment<FragmentShowRequestInfoBinding, ShowRequestInfoViewModel>() {

    //Variables
    override val mViewModel: ShowRequestInfoViewModel by viewModels()
    val documentAdapter: ImagePreviewAdapter by lazy { ImagePreviewAdapter(onDocumentClickListener) }
    protected val documentFiles by lazy { ArrayList<DownloadFileModel>() }
    val onDocumentClickListener by lazy {
        object : AdapterInterface.OnItemClickListener<UploadedImageModel> {
            override fun onItemClick(
                item: UploadedImageModel,
                transitionView: View?,
                tag: String?,
            ) {
                when (tag) {
                    Constants.IMAGE_PREVIEW_TAG -> {
                        handlePageDestination(
                            R.id.show_request_Info_to_imageView,
                            Bundle().apply {
                                putString(ViewerImageActivity.TITLE_IMAGE, item.imageName)
                                putString(ViewerImageActivity.URI_IMAGE, item.imageUri.toString())
                                putBoolean(ViewerImageActivity.ENABLE_BUTTON_SHARE_AND_DOWNLOAD,true)
                            }
                        )
                    }
                    Constants.DELETE_IMAGE_TAG -> {
                        //documentFiles
                    }
                }
            }
        }
    }
    protected var requestType = 0
    protected var referenceId = ""

    //Base Methods
    override fun getLayoutId() = R.layout.fragment_show_request_info

    override fun initView() {
        setupToolbar(
            viewDataBinding?.appBar,
            viewDataBinding?.appbarBackgroundImage?.imageBackground,
            null
        )
        documentAdapter.setVisibilityDeleteItem(false)
    }

    override fun getData() {
        arguments?.apply {
            referenceId = getString(Constants.REFERENCE_ID) ?: ""
            requestType = getInt(Constants.REQUEST_TYPE)
            if (requestType != 0) {
                viewDataBinding?.apply {
                    appBar.tvTitle.text = getString(mViewModel.getServiceName(requestType))
                }
            }
        }
    }


    override fun onClick() {
    }

    override fun setupObserver() {
        mViewModel.mldDownloadDocument.observe(this, ::onDownloadDocumentResponse)
    }

    //Listeners
    private fun onDownloadDocumentResponse(images: List<DownloadFileResponse>) {
        val listDoc = ArrayList<UploadedImageModel>()
        images.forEach { imageResult->
            if (imageResult.isSuccess) {
                    listDoc.add(
                        UploadedImageModel(
                            guid = "",
                            imageName = imageResult.detail?.fileName?:"",
                            orgUri = imageResult.uri,
                            imageUri = imageResult.uri
                        )
                    )
            }
        }
        documentAdapter.setItems(listDoc)
    }


}