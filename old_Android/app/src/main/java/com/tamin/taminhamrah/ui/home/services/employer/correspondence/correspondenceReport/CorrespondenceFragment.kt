package com.tamin.taminhamrah.ui.home.services.employer.correspondence.correspondenceReport

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.filter
import androidx.paging.map
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.remote.models.responses.DeleteItemResponse
import com.tamin.taminhamrah.data.remote.models.responses.InquiryLicenseResponse
import com.tamin.taminhamrah.data.remote.models.user.InboxItem
import com.tamin.taminhamrah.data.remote.models.user.InboxPdfItem
import com.tamin.taminhamrah.data.remote.models.user.InboxSizeResponse
import com.tamin.taminhamrah.data.remote.models.user.Permission
import com.tamin.taminhamrah.databinding.FragmentCorrespondenceReportBinding
import com.tamin.taminhamrah.ui.LocalPagingSource
import com.tamin.taminhamrah.ui.PdfViewerActivity
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.appinterface.DialogClickInterface
import com.tamin.taminhamrah.ui.appinterface.MenuInterface
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.ui.dialog.MenuDialogFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.MessageOfRequestDialogFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.manger.DialogManagerMessageOfRequest
import com.tamin.taminhamrah.utils.Utility
import com.tamin.taminhamrah.utils.extentions.createBundle
import com.tamin.taminhamrah.utils.extentions.setupRecycler
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class CorrespondenceFragment :
    BaseFragment<FragmentCorrespondenceReportBinding, CorrespondenceViewModel>(),
    AdapterInterface.OnItemClickListener<InboxItem>,
    AdapterInterface.OnDeleteClickListener<InboxItem>,
    DialogClickInterface.onClickListener {

    override val mViewModel: CorrespondenceViewModel by viewModels()
    lateinit var listAdapter: CorrespondenceAdapter
    var itemid: Int? = null


    override fun getBindingVariable(): Pair<Int, Any?> {
        return Pair(BR.viewModel, mViewModel)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_correspondence_report
    }

    override fun setupObserver() {
        mViewModel.mldCorrespondenceSize.observe(this, ::showCorrespondenceSizeResult)
        mViewModel.mldPDF.observe(this, ::showPDFResult)
        mViewModel.mldIssuedCorrespondenceInquiry.observe(this, ::issuedInquiryLicenseResult)
        mViewModel.mldCancelCorrespondenceInquiry.observe(this, ::cancelInquiryLicenseResult)

        mViewModel.mldDeleteRequest.observe(this, ::showDeleteResult)

    }

    override fun initView() {
        listAdapter = CorrespondenceAdapter().apply {
            onDeleteListener = this@CorrespondenceFragment
            onItemClickListener = this@CorrespondenceFragment
        }

        setupRecycler(viewDataBinding?.recycler, listAdapter)
        setupToolbar(
            viewDataBinding?.appBar,
            viewDataBinding?.appbarBackgroundImage?.imageBackground,
            actionIconRes = R.drawable.ic_search
        )
    }

    override fun getData() {


        collectPaginatedData()

        mViewModel.getCorrespondenceSize()

    }

    private fun collectPaginatedData(
        seenId: Int? = null,
        cancelPermissionItemId: Int? = null,
        issuedPermissionItemId: Int? = null,
        deleteId: Int? = null
    ) {
        this@CorrespondenceFragment.lifecycleScope.launchWhenCreated {
            mViewModel.correspondencePager.collectLatest { pagingData ->
                var index = 0
                val result = pagingData.map {
                    if (seenId == it.id) {
                        it.seen = true
                        listAdapter.notifyItemChanged(index)

                    } else if (cancelPermissionItemId == it.id) {
                        it.permission = null
                        listAdapter.notifyItemChanged(index)

                    } else if (issuedPermissionItemId == it.id) {
                        it.permission = Permission(dateTo = 0)
                        listAdapter.notifyItemChanged(index)

                    }
                    index++
                    it
                }.filter {
                    deleteId != it.id
                }
                listAdapter.submitData(result)

            }
        }
    }

    override fun onClick() {

    }

    private fun showCorrespondenceSizeResult(response: InboxSizeResponse) {
        response.let { initCorrespondenceSizeView(it.usage, it.total) }
    }

    private fun initCorrespondenceSizeView(usage: Float, total: Int) {

        viewDataBinding?.apply {
            val totalWidth = viewTotalSpace.measuredWidth
            val usageHeight = viewTotalSpace.measuredHeight
            var usageWidth = ((totalWidth * usage) / total).toInt()
            if (usageWidth > viewTotalSpace.measuredWidth) {
                usageWidth = totalWidth
                viewUsageSpace.setBackgroundResource(R.drawable.bg_red)
                showAlertDialog(
                    MessageOfRequestDialogFragment.MessageType.INFO,
                    getString(R.string.error_inbox_is_full)
                )
            }
            viewUsageSpace.layoutParams?.width = usageWidth
        }


    }

    private fun showPDFResult(model: InboxPdfItem) {
        //   (requireActivity() as? MainActivity)?.handleResponse(model)
        if (model.isSuccess) {

            val file = Utility.savePdfFile(
                requireContext(),
                model.data?.pdf,
                model.data?.id.toString()
            )

            val bundle = Bundle()
            bundle.putString(PdfViewerActivity.ARG_TITLE, Utility.getToolbarTitle(arguments))
            bundle.putString(PdfViewerActivity.ARG_PDF_FILE_PATH, file.path)
            handlePageDestination(R.id.action_correspondence_to_pdf_viewer, bundle)

            collectPaginatedData(seenId = model.data?.id)

        }

    }

    private fun issuedInquiryLicenseResult(response: InquiryLicenseResponse) {
        if (response.isSuccess) {
            showAlertDialog(
                MessageOfRequestDialogFragment.MessageType.SUCCESS,
                getString(R.string.message_success)
            )
            collectPaginatedData(issuedPermissionItemId = itemid)
        }

    }

    private fun cancelInquiryLicenseResult(response: InquiryLicenseResponse) {
        if (response.isSuccess) {
            showAlertDialog(
                MessageOfRequestDialogFragment.MessageType.SUCCESS,
                getString(R.string.message_success)
            )
            collectPaginatedData(cancelPermissionItemId = itemid)

        }
    }

    private fun showDeleteResult(response: DeleteItemResponse) {
        if (response.isSuccess) {
            showAlertDialog(
                MessageOfRequestDialogFragment.MessageType.SUCCESS,
                getString(R.string.message_success_delete)
            )
            collectPaginatedData(deleteId = itemid)
        }
    }

    override fun onItemClick(item: InboxItem, transitionView: View?, tag: String?) {

        when (tag) {

            getString(R.string.label_show_pdf) -> {
                mViewModel.getPDF(item.id)
            }

            getString(R.string.label_inquiry_license) -> {

                val dialog = MenuDialogFragment.newInstance(menuTitle = "")
                dialog.setMenuListener(object : MenuInterface.OnFetchData {
                    override fun onFetch() {
                        this@CorrespondenceFragment.lifecycleScope.launchWhenCreated {
                            val pager = Pager(
                                config = PagingConfig(Constants.QUERY_PAGE_SIZE_10, 2),
                                pagingSourceFactory = { LocalPagingSource(mViewModel.getLicensePeriodList()) })
                            pager.flow.cachedIn(lifecycleScope).collectLatest { pagingData ->
                                dialog.updateData(pagingData)
                            }
                        }
                    }
                }, object : MenuInterface.OnResult {
                    override fun onResult(resultItem: MenuModel) {
                        val dialogMessage = DialogManagerMessageOfRequest.getInstanceOfDialog()
                        dialogMessage.arguments = createBundle(
                            MessageOfRequestDialogFragment.MessageType.CONFIRM,
                            requireContext().getString(R.string.message_inquery_license_alert_desc),
                            true,
                            getString(R.string.label_inquiry_license),
                            R.string.message_inquery_license_alert
                        )
                        dialogMessage.setDialogClickListener(object :
                            DialogClickInterface.onClickListener {
                            override fun onConfirmClick() {
                                resultItem.id?.let {
                                    mViewModel.issuedCorrespondenceInquiryLicense(
                                        item.id.toString(),
                                        it
                                    )
                                }
                            }
                            override fun onCancelClick() {

                            }
                        })
                        dialogMessage.show(childFragmentManager, "ghghg")
                        itemid = item.id
                    }
                })
                dialog.show(childFragmentManager, "hkjhkj")
            }

            getString(R.string.label_cancel_inquiry_license) -> {

                val dialog = DialogManagerMessageOfRequest.getInstanceOfDialog()
                dialog.arguments = createBundle(
                    MessageOfRequestDialogFragment.MessageType.WARNING,
                    requireContext().getString(R.string.message_cancel_inquery_license),
                    true,
                    getString(R.string.label_calcel_inquiry_license)
                )
                dialog.setDialogClickListener(object : DialogClickInterface.onClickListener {
                    override fun onConfirmClick() {
                        mViewModel.cancelCorrespondenceInquiryLicense(item.id.toString())
                    }

                    override fun onCancelClick() {
                    }
                })
                dialog.show(childFragmentManager, "ghgghjhgjhhg")
                itemid = item.id
            }
        }

    }


    override fun onDelete(item: InboxItem) {
        val dialog = DialogManagerMessageOfRequest.getInstanceOfDialog()
        dialog.arguments = createBundle(
            MessageOfRequestDialogFragment.MessageType.WARNING,
            requireContext().getString(R.string.message_delete_alert),
            true,
            getString(R.string.label_delete)
        )
        dialog.setDialogClickListener(this)
        dialog.show(childFragmentManager, "Alert Dialog inbox fragment for deleted item")
        itemid = item.id
    }

    override fun onConfirmClick() {
        itemid?.let {
            mViewModel.deleteItem(it)
        }
    }

    override fun onCancelClick() {
    }

}