package com.tamin.taminhamrah.ui.home.services.wageandhistory

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.filter
import androidx.paging.map
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.pdfFile.PdfDownloadResponse
import com.tamin.taminhamrah.data.remote.models.services.CheckInsuredInfoResponse
import com.tamin.taminhamrah.data.remote.models.services.EnumTypeUser
import com.tamin.taminhamrah.data.remote.models.services.WageAndHistoryModel
import com.tamin.taminhamrah.data.remote.models.services.WageAndHistoryModels
import com.tamin.taminhamrah.databinding.FragmentWageAndHistoryBinding
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
class WageAndHistoryFragment :
    BaseFragment<FragmentWageAndHistoryBinding, WageAndHistoryViewModel>(),
    AdapterInterface.OnItemClickListener<String>, ActionAppBarInterface.OnActionClickListener {


    var scrollRange = -1
    var isShow = true
    var bundleHistory = bundleOf()
    override val mViewModel: WageAndHistoryViewModel by viewModels()
    lateinit var listAdapter: WageAndHistoryAdapter
    lateinit var pdfName: String
    var pdfChooserIsLuanched = false

    var listOfWageAndHistory = arrayListOf<WageAndHistoryModel>()
    var listOfWageAndHistoryMerge = arrayListOf<WageAndHistoryModel>()

    override fun getBindingVariable(): Pair<Int, Any?> {
        return Pair(BR.viewModel, mViewModel)
    }

    override fun initView() {
        viewDataBinding?.apply {
            listAdapter = WageAndHistoryAdapter().apply {
                onItemClickListener = this@WageAndHistoryFragment
            }
            setupRecycler(recycler, listAdapter)
            appBar.toolbar.imgInfo.setOnClickListener {
                mViewModel.saveBoolean(Constants.TapTargetWageAndHistoryFragment, false)
                setViewForShowGide()
            }
        }
        setupToolbar(
            viewDataBinding?.appBar,
            viewDataBinding?.appbarBackgroundImage?.imageBackground,
            actionIconRes = R.drawable.ic_download,
            onActionAppBarClickListener = this
        )
        setViewForShowGide()
    }

    override fun getData() {
        mViewModel.pensionCheck()
    }

    override fun onClick() {
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_wage_and_history
    }

    override fun setupObserver() {
        mViewModel.mldDownloadPDF.observe(this, ::showPDFResult)
        mViewModel.mldPensionCheck.observe(this, ::onPensionCheck)
    }

    private fun showPDFResult(result: PdfDownloadResponse) {
        if (result.isSuccess) {
            val file = Utility.writeByteStreamToDisk(
                Utility.getToolbarTitle(arguments),
                requireContext(),
                result.pdf
            )
            if (file == null) {
                showAlertDialog(
                    MessageOfRequestDialogFragment.MessageType.ERROR,
                    getString(R.string.error_recive_file)
                )
                return
            }
            val bundle = Bundle()
            bundle.putString(PdfViewerActivity.ARG_TITLE, Utility.getToolbarTitle(arguments))
            bundle.putString(PdfViewerActivity.ARG_PDF_FILE_PATH, file.path)
            handlePageDestination(R.id.action_wage_history_to_pdf_viewer, bundle)
        }
    }


    private fun onPensionCheck(result: CheckInsuredInfoResponse) {
        if (!result.isSuccess) return
        when (result.data?.typeUser) {
            EnumTypeUser.ANONYMOUS.title -> {
                showAlertDialog(
                    MessageOfRequestDialogFragment.MessageType.ERROR,
                    getString(R.string.error_recive_data),
                    dismissType = MessageOfRequestDialogFragment.DismissType.BACK_TO_PREVIOUS
                )
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
                this@WageAndHistoryFragment.lifecycleScope.launchWhenCreated {
                    mViewModel.getWageAndInsuranceHistory.collectLatest { pagingData ->
                        val mergeResult = pagingData.map { newItem ->
                            listOfWageAndHistory.add(newItem)
                            sumDayItems(newItem)
                            newItem
                        }.filter {
                            !it.isDuplicate
                        }
                        listAdapter.submitData(mergeResult)
                    }
                }
            }
        }
    }

    private fun sumDayItems(newItem: WageAndHistoryModel) {
        listOfWageAndHistoryMerge.forEach { oldItem ->
            oldItem.apply {
                if (hisyear == newItem.hisyear) {
                    hismon1 = sumMonth(hismon1, newItem.hismon1)
                    hismon2 = sumMonth(hismon2, newItem.hismon2)
                    hismon3 = sumMonth(hismon3, newItem.hismon3)
                    hismon4 = sumMonth(hismon4, newItem.hismon4)
                    hismon5 = sumMonth(hismon5, newItem.hismon5)
                    hismon6 = sumMonth(hismon6, newItem.hismon6)
                    hismon7 = sumMonth(hismon7, newItem.hismon7)
                    hismon8 = sumMonth(hismon8, newItem.hismon8)
                    hismon9 = sumMonth(hismon9, newItem.hismon9)
                    hismon10 = sumMonth(hismon10, newItem.hismon10)
                    hismon11 = sumMonth(hismon11, newItem.hismon11)
                    hismon12 = sumMonth(hismon12, newItem.hismon12)
                    newItem.isDuplicate = true
                    return@forEach
                }
            }
        }
        listOfWageAndHistoryMerge.add(newItem)
    }


    private fun sumMonth(monthOld: String?, monthNew: String?): String {
        var a = monthOld?.toInt() ?: 0
        val b = monthNew?.toInt() ?: 0
        a += b
        return a.toString()
    }

    fun createToolbarBundle(title: String, desc: String, iconRes: Int): Bundle {
        val bundle = Bundle()
        bundle.putString(Constants.TOOLBAR_TITLE, title)
        bundle.putString(Constants.TOOLBAR_SUBTITLE, desc)
        bundle.putInt(Constants.TOOLBAR_ICON_IMAGE, iconRes)
        return bundle
    }

    override fun onItemClick(item: String, transitionView: View?, tag: String?) {
        val allHistorySameYear = WageAndHistoryModels()
        allHistorySameYear.addAll(listOfWageAndHistory.filter { it.hisyear == item })
        bundleHistory.putParcelableArrayList(Constants.ARRAYLIST, allHistorySameYear)
        val dialog = WageAndHistoryDetailFragment()
        dialog.arguments = bundleHistory
        dialog.show(childFragmentManager, "History By Mounth")
    }

    override fun onActionClick() {
        mViewModel.downloadWageAndHistoryPDF()
    }

    private fun setViewForShowGide() {
        /*  try {
              if (!mViewModel.loadBoolean(Constants.TapTargetWageAndHistoryFragment)) {
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
                  ViewsList?.let {
                          showGide(it)
                   }
                  mViewModel.saveBoolean(Constants.TapTargetWageAndHistoryFragment, true)
              }

          } catch (e: Exception) {
              Log.e("showGide: ", e.message.toString())
          }*/
    }


}
