package com.tamin.taminhamrah.ui.mytamin.myrequest.showRequestInfo.article16

import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.data.entity.DownloadFileResponse
import com.tamin.taminhamrah.data.remote.models.services.workshop.definitiveDebtArticle16.Article16RequestInfoResponse
import com.tamin.taminhamrah.ui.mytamin.myrequest.showRequestInfo.ShowRequestBaseFragment
import com.tamin.taminhamrah.utils.UiUtils
import com.tamin.taminhamrah.utils.extentions.isNumericString
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class Article16ShowRequestInfoFragment : ShowRequestBaseFragment() {

    //Base Methode
    override fun initView() {
        super.initView()
        viewDataBinding?.recycler?.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = documentAdapter
            if (itemDecorationCount == 0)
                addItemDecoration(UiUtils.createDivider(requireContext()))
        }
    }

    override fun getBindingVariable() = Pair(BR.viewModel, mViewModel)

    override fun setupObserver() {
        super.setupObserver()
        mViewModel.mldArticle16RequestInfo.observe(this,::onArticle16RequestInfoResponse)
        mViewModel.mldDownloadDocument.observe(this,::onResponseDownload)
    }

    private fun onResponseDownload(result: List<DownloadFileResponse>) {
        viewDataBinding?.docGroup?.isVisible = true
    }


    override fun getData() {
        super.getData()
        if (referenceId.isNumericString())
        mViewModel.getArticle16RequestInfo(referenceId.toLong())
    }

    //Listeners
    private fun onArticle16RequestInfoResponse(result: Article16RequestInfoResponse) {
        if(result.isSuccess){
            result.data?.objectionPhotos?.let { mViewModel.downloadDocument(it) }
        }
    }
}