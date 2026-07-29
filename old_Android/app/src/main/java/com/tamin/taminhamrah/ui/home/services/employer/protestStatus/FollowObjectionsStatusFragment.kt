package com.tamin.taminhamrah.ui.home.services.employer.protestStatus

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.Constants.OBJECTION_ID
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.pdfFile.PdfDownloadResponse
import com.tamin.taminhamrah.data.remote.models.services.workshop.WorkShopObjection
import com.tamin.taminhamrah.databinding.FragmentFollowProtestStatusBinding
import com.tamin.taminhamrah.ui.PdfViewerActivity
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.appinterface.DialogResultInterface
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.MessageOfRequestDialogFragment
import com.tamin.taminhamrah.utils.Utility
import com.tamin.taminhamrah.utils.extentions.setupRecycler
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber

@AndroidEntryPoint
class FollowObjectionsStatusFragment :
    BaseFragment<FragmentFollowProtestStatusBinding, FollowObjectionsStatusViewModel>(),
    AdapterInterface.OnItemClickListener<WorkShopObjection>,
    DialogResultInterface.OnResultListener<Map<String, String>>{

    override val mViewModel: FollowObjectionsStatusViewModel by viewModels()
    lateinit var listAdapter: ObjectionsStatusAdapter


    override fun getBindingVariable(): Pair<Int, Any?> {
        return Pair(BR.viewModel, mViewModel)

    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_follow_protest_status
    }

    override fun setupObserver() {
        mViewModel.mldPdf.observe(this, ::showPDFResult)

    }

    override fun initView() {
        Timber.tag("FollowObjection").i("initView: Called")
        listAdapter = ObjectionsStatusAdapter(true, this)
        setupRecycler(viewDataBinding?.recycler, listAdapter)

        setupToolbar(
            viewDataBinding?.appBar,
            viewDataBinding?.appbarBackgroundImage?.imageBackground,
            actionIconRes = R.drawable.ic_search,
        )
    }

    override fun getData() {
       collectData()
    }

    private fun collectData(workshopCode:String?="", objectionNumber:String?="", debitNumber:String?="") {

        this@FollowObjectionsStatusFragment.lifecycleScope.launchWhenCreated {
            mViewModel.getAllObjections(workshopCode, objectionNumber, debitNumber).collectLatest { pagingData ->
                listAdapter.submitData(pagingData)
            }
        }
    }


    override fun onItemClick(item: WorkShopObjection, transitionView: View?, tag: String?) {
        when (tag) {
            getString(R.string.label_show_sms) -> {
                val bundle = Bundle()
                bundle.putString(OBJECTION_ID, item.seqNo?.toString())
                handlePageDestination(R.id.action_messages_objections, bundle)
            }
            getString(R.string.label_show_inspection) -> {
               mViewModel.getDebitObjectionPdf(item.seqNo, item.objectionType)
            }
        }


    }

    override fun onClick() {

        viewDataBinding?.appBar?.toolbar?.imgAction?.setOnClickListener {
            val dialog = ObjectionSearchDialogFragment()
            dialog.setListener(this@FollowObjectionsStatusFragment)
            dialog.show(childFragmentManager, "jfhskljkjllkljl")
        }
    }

    var tempTitle = ""

    private fun showPDFResult(result: PdfDownloadResponse) {
        if (result.isSuccess) {
            val file = Utility.writeByteStreamToDisk(tempTitle, requireContext(), result.pdf)
            if (file == null) {
                showAlertDialog(
                    MessageOfRequestDialogFragment.MessageType.ERROR,
                    getString(R.string.error_recive_file)
                )
                return
            }
            val bundle = Bundle()
            bundle.putString(PdfViewerActivity.ARG_TITLE, tempTitle)
            bundle.putString(PdfViewerActivity.ARG_PDF_FILE_PATH, file.path)
            handlePageDestination(R.id.action_objections_pdf, bundle)

        }
    }

    override fun onDialogResult(item: Map<String, String>) {
        val workshopCode = item[mViewModel.ARG_WORKSHOP_CODE] ?: ""
        val objectionNumber = item[mViewModel.ARG_OBJECTION_NUMBER] ?: ""
        val debitNumber = item[mViewModel.ARG_DEBIT_NUMBER] ?: ""
        collectData(workshopCode, objectionNumber, debitNumber)
    }
}
