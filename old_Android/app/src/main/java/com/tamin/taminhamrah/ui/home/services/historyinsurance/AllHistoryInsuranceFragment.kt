package com.tamin.taminhamrah.ui.home.services.historyinsurance

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.filter
import androidx.paging.map
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.Constants.TapTargetHistoryFragment
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.pdfFile.PdfDownloadResponse
import com.tamin.taminhamrah.data.remote.models.services.AllHistoryInsuranceResponseModel
import com.tamin.taminhamrah.data.remote.models.services.AllHistoryInsuranceResponseModels
import com.tamin.taminhamrah.data.remote.models.services.CheckInsuredInfoResponse
import com.tamin.taminhamrah.data.remote.models.services.EnumTypeUser
import com.tamin.taminhamrah.databinding.FragmentAllHistoryInsuranceBinding
import com.tamin.taminhamrah.ui.PdfViewerActivity
import com.tamin.taminhamrah.ui.appinterface.ActionAppBarInterface
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.appinterface.DialogClickInterface
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.MessageOfRequestDialogFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.manger.DialogManagerMessageOfRequest
import com.tamin.taminhamrah.utils.Utility
import com.tamin.taminhamrah.utils.extentions.createBundle
import com.tamin.taminhamrah.utils.extentions.setupRecycler
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class AllHistoryInsuranceFragment :
    BaseFragment<FragmentAllHistoryInsuranceBinding, AllHistoryInsuranceViewModel>(),
    AdapterInterface.OnItemClickListener<String>, ActionAppBarInterface.OnActionClickListener {

    var bundleHistory = bundleOf()
    override val mViewModel: AllHistoryInsuranceViewModel by viewModels()
    lateinit var listAdapter: HistoryAdapter
    var allHistory = arrayListOf<AllHistoryInsuranceResponseModel>()

    lateinit var pdfName: String
    override fun getBindingVariable(): Pair<Int, Any?> {
        return Pair(BR.viewModel, mViewModel)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_all_history_insurance
    }

    override fun setupObserver() {
        mViewModel.mldDownloadPDF.observe(this, ::showPDFResult)
        mViewModel.mldPensionCheck.observe(this, ::onPensionCheck)
    }

    private fun onPensionCheck(result: CheckInsuredInfoResponse) {
        if (!result.isSuccess) return
        when (result.data?.typeUser) {
            EnumTypeUser.ANONYMOUS.title -> {
                showAlertDialog(MessageOfRequestDialogFragment.MessageType.ERROR,getString(R.string.error_recive_data),dismissType = MessageOfRequestDialogFragment.DismissType.BACK_TO_PREVIOUS)
            }
            EnumTypeUser.PENSIONER.title -> {
                val dialog = DialogManagerMessageOfRequest.getInstanceOfDialog()
                dialog.arguments = createBundle(
                    MessageOfRequestDialogFragment.MessageType.ERROR,
                    result.data?.list?.get(1) ?: getString(R.string.error_active_relation_user_is_pensioner)
                )
                dialog.setDialogClickListener(object :
                    DialogClickInterface.onClickListener {
                    override fun onConfirmClick() {
                        requireActivity().onBackPressed()
                    }

                    override fun onCancelClick() {
                    }

                })
                dialog.show(childFragmentManager, "Alert Dialog MessageOfRequest")
            }
            EnumTypeUser.INSURED.title -> {
                this@AllHistoryInsuranceFragment.lifecycleScope.launchWhenCreated {
                    mViewModel.getAllHistoryInsurance.collectLatest { pagingData ->
                        val yearMap = mutableSetOf<String>()
                        val yearItem = pagingData.map {
                            allHistory.add(it)
                            it.year
                        }.filter { year ->
                            if (yearMap.contains(year)) {
                                false
                            } else {
                                yearMap.add(year)
                            }
                        }
                        listAdapter.submitData(yearItem)
                    }
                }
            }
        }
    }

    private fun showPDFResult(result: PdfDownloadResponse) {
        if (result.isSuccess) {
            val file = Utility.writeByteStreamToDisk(Utility.getToolbarTitle(arguments), requireContext(), result.pdf)
            if (file == null) {
                showAlertDialog(
                    MessageOfRequestDialogFragment.MessageType.ERROR,
                    getString(R.string.error_recive_file)
                )
                return
            }
            val bundle = Bundle()
            bundle.putString(PdfViewerActivity.ARG_TITLE,Utility.getToolbarTitle(arguments))
            bundle.putString(PdfViewerActivity.ARG_PDF_FILE_PATH, file.path)
            handlePageDestination(R.id.action_all_history_to_pdf_viewer, bundle)
        }
    }

    override fun initView() {
        viewDataBinding?.apply {
            try {
                pdfName = requireActivity().getString(R.string.history_pdf_name) /*+ (mViewModel.getUserName())*/ + ".pdf"

                listAdapter = HistoryAdapter().apply {
                    onItemClickListener = this@AllHistoryInsuranceFragment
                }

               setupRecycler(recycler, listAdapter)

                appBar.toolbar.imgInfo.setOnClickListener {
                    mViewModel.saveBoolean(TapTargetHistoryFragment, false)
                    setViewForShowGide()
                }
            } catch (e: Exception) {
            }
        }
        setupToolbar(
            viewDataBinding?.appBar,
            viewDataBinding?.appbarBackgroundImage?.imageBackground,
            moreViews = null,
            R.drawable.ic_download,
            onActionAppBarClickListener=this,


        )
        setViewForShowGide()
    }

    override fun getData() { mViewModel.pensionCheck() }

    override fun onClick() {}

    override fun onItemClick(item: String, transitionView: View?, tag: String?) {
        val allHistorySameYear = AllHistoryInsuranceResponseModels()
        allHistorySameYear.addAll(allHistory.filter { it.year == item })
        bundleHistory.putParcelableArrayList(Constants.ARRAYLIST, allHistorySameYear)
        val dialog = HistoryDetailFragment()
        dialog.arguments = bundleHistory
        dialog.show(childFragmentManager, "History By Mounth")
    }

    override fun onActionClick() {
        mViewModel.downloadAllHistoryPDF()
    }


    private fun setViewForShowGide() {
        /* try {
             if (!mViewModel.loadBoolean(Constants.TapTargetHistoryFragment)) {
                 val ViewsList = arrayListOf<TapTargetModel>()
                 (viewDataBinding)?.appBar?.toolbar?.imgInfo?.let {
                     ViewsList.add(TapTargetModel(it,R.string.title_img_info_tag_target_view,R.string.detail_info_img_tag_target_view))
                 }
                 this.viewDataBinding?.appBar?.toolbar?.imgAction?.let {
                     ViewsList.add(TapTargetModel(it,R.string.title_img_action_download_history_tag_target_view,R.string.detail_action_download_history_imgaction_tag_target_view))
                 }
                 this.viewDataBinding?.recycler?.let {
                     ViewsList.add(TapTargetModel(it,R.string.title_hisory_tag_target_view,R.string.detail_hisory_tag_target_view,shape = Shape.RECT))
                 }
                 mViewModel.saveBoolean(Constants.TapTargetHistoryFragment, true)

                 ViewsList?.let {
                     showGide(it)
                 }
             }
         } catch (e: Exception) {
             Log.e("showGide: ", e.message.toString())
         }*/
    }

}