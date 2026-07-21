package com.tamin.taminhamrah.ui.mytamin.inbox

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.remote.models.responses.DeleteItemResponse
import com.tamin.taminhamrah.data.remote.models.responses.InquiryLicenseResponse
import com.tamin.taminhamrah.data.remote.models.user.InboxItem
import com.tamin.taminhamrah.data.remote.models.user.InboxPdfItem
import com.tamin.taminhamrah.data.remote.models.user.InboxSizeResponse
import com.tamin.taminhamrah.databinding.FragmentInboxBinding
import com.tamin.taminhamrah.ui.PdfViewerActivity
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.appinterface.DialogClickInterface
import com.tamin.taminhamrah.ui.appinterface.MenuInterface
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.MessageOfRequestDialogFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.manger.DialogManagerMessageOfRequest
import com.tamin.taminhamrah.utils.Utility
import com.tamin.taminhamrah.utils.extentions.createBundle
import com.tamin.taminhamrah.utils.extentions.setupRecycler
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class InboxFragment :
    BaseFragment<FragmentInboxBinding, InboxViewModel>(),
    AdapterInterface.OnItemClickListener<InboxItem>,
    AdapterInterface.OnDeleteClickListener<InboxItem>,
    DialogClickInterface.onClickListener {
    private lateinit var listAdapter: InboxAdapter
    private var itemid: Int? = null

    override val mViewModel: InboxViewModel by viewModels()

    override fun getBindingVariable(): Pair<Int, Any?> {
        return Pair(BR.viewModel, mViewModel)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_inbox
    }

    override fun setupObserver() {
        mViewModel.mldInboxSize.observe(viewLifecycleOwner, ::showInboxSizeResult)
        mViewModel.mldPDF.observe(viewLifecycleOwner, ::showPDFResult)
        mViewModel.mldDeleteRequest.observe(viewLifecycleOwner, ::showDeleteResult)
        mViewModel.mldInboxInquiry.observe(viewLifecycleOwner, ::showInquiryLicenseResult)
    }

    override fun initView() {
        listAdapter = InboxAdapter().apply {
            onDeleteListener = this@InboxFragment
            onItemClickListener = this@InboxFragment
            onToggleExpandListener = { item ->
                item.id?.let {
                mViewModel.toggleItemExpanded(it)
                }
            }
        }

        setupRecycler(viewDataBinding?.recycler, listAdapter)

        setupToolbar(
            viewDataBinding?.appBar,
            viewDataBinding?.appbarBackgroundImage?.imageBackground,
//            actionIconRes = R.drawable.ic_search
        )

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mViewModel.inboxItems.collectLatest { pagingData ->
                    listAdapter.submitData(pagingData)
                }
            }
        }
    }

    override fun getData() {
        mViewModel.getInboxSize()
    }

    override fun onClick() {
    }


    private fun showInboxSizeResult(response: InboxSizeResponse) {
        response.let { initInboxView(it.usage, it.total) }

    }

    private fun showPDFResult(model: InboxPdfItem) {
        if (model.isSuccess) {
            val file = Utility.savePdfFile(
                requireContext(),
                model.data?.pdf,
                model.data?.id.toString()
            )
            val bundle = Bundle()
            bundle.putString(PdfViewerActivity.ARG_TITLE, Utility.getToolbarTitle(arguments))
            bundle.putString(PdfViewerActivity.ARG_PDF_FILE_PATH, file.path)
            handlePageDestination(R.id.action_inbox_to_pdf_viewer, bundle)
            listAdapter.refresh()
        }
    }


    private fun showDeleteResult(response: DeleteItemResponse) {
        showAlertDialog(
            MessageOfRequestDialogFragment.MessageType.SUCCESS,
            getString(R.string.message_success_delete)
        )
        listAdapter.refresh()
    }

    private fun showInquiryLicenseResult(response: InquiryLicenseResponse) {
        if (response.isSuccess) {
            showAlertDialog(
                MessageOfRequestDialogFragment.MessageType.SUCCESS,
                getString(R.string.message_success)
            )
            listAdapter.refresh()
        }
    }

    private fun initInboxView(usage: Float, total: Int) {

        viewDataBinding?.apply {
            val totalWidth = viewTotalSpace.measuredWidth
            viewTotalSpace.measuredHeight // This line doesn't seem to do anything with the result
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
            viewUsageSpace.requestLayout() // Ensure the view redraws with new width
        }
    }

    override fun onItemClick(item: InboxItem, transitionView: View?, tag: String?) {

        when (tag) {
            getString(R.string.label_show_pdf) -> {
                mViewModel.getPDF(item.id)
            }

            getString(R.string.label_inquiry_license) -> {
                Timber.tag("onDeleteListener").e(" label_inquiry_license ")
                val dialog = InboxInquiryDialogFragment()
                dialog.setListener(object : MenuInterface.OnResult {
                    override fun onResult(itemResult: MenuModel) {
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
                                itemResult.id?.let {
                                    mViewModel.inboxInquiryLicense(
                                        item.id.toString(),
                                        it
                                    )
                                }
                            }

                            override fun onCancelClick() {}
                        })
                        dialogMessage.show(childFragmentManager, "ghghg")
                    }
                })
                dialog.show(childFragmentManager, "hkjhkj")
            }

            getString(R.string.label_cancel_inquiry_license) -> {
                Timber.tag("onDeleteListener").e(" label_cancel_inquiry_license ")
                val dialog = DialogManagerMessageOfRequest.getInstanceOfDialog()
                dialog.arguments = createBundle(
                    MessageOfRequestDialogFragment.MessageType.WARNING,
                    requireContext().getString(R.string.message_cancel_inquery_license),
                    true,
                    getString(R.string.label_calcel_inquiry_license)
                )
                dialog.setDialogClickListener(object : DialogClickInterface.onClickListener {
                    override fun onConfirmClick() {
                        mViewModel.inboxInquiryLicense(item.id.toString())
                    }

                    override fun onCancelClick() {}
                })
                dialog.show(childFragmentManager, "ghgghjhgjhhg")
            }
        }
    }

    override fun onDelete(item: InboxItem) {
        Timber.tag("onDeleteListener").e(" onDelete ")
        this.itemid = item.id
        val dialog = DialogManagerMessageOfRequest.getInstanceOfDialog()
        dialog.arguments = createBundle(
            MessageOfRequestDialogFragment.MessageType.WARNING,
            requireContext().getString(R.string.message_delete_alert),
            true,
            getString(R.string.label_delete)
        )
        dialog.setDialogClickListener(this) // 'this' refers to InboxFragment which implements onClickListener
        dialog.show(childFragmentManager, "Alert Dialog inbox fragment for deleted item")
    }


    override fun onConfirmClick() {
        itemid?.let {
            mViewModel.deleteItem(it)
        }
        itemid = null
    }

    override fun onCancelClick() {
        itemid = null
    }

}
