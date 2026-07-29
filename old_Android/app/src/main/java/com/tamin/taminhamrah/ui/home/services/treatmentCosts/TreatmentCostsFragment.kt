package com.tamin.taminhamrah.ui.home.services.treatmentCosts

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.remote.models.pdfFile.PdfDownloadResponse
import com.tamin.taminhamrah.data.remote.models.services.treatmentServices.costs.SendToInboxTreatmentCosts
import com.tamin.taminhamrah.data.remote.models.services.treatmentServices.costs.TreatmentCostsExpensesModel
import com.tamin.taminhamrah.databinding.FragmentTreatmentCostsBinding
import com.tamin.taminhamrah.ui.LocalPagingSource
import com.tamin.taminhamrah.ui.PdfViewerActivity
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.appinterface.MenuInterface
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.ui.dialog.MenuDialogFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.MessageOfRequestDialogFragment
import com.tamin.taminhamrah.ui.home.services.treatmentCosts.adapter.TreatmentCostsAdapter
import com.tamin.taminhamrah.utils.Utility
import com.tamin.taminhamrah.utils.extentions.setupRecycler
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TreatmentCostsFragment :
    BaseFragment<FragmentTreatmentCostsBinding, TreatmentCostsViewModel>() {
    override val mViewModel: TreatmentCostsViewModel by viewModels()

    val listAdapter by lazy { TreatmentCostsAdapter() }

    val onItemClickListener by lazy {
        object : AdapterInterface.OnItemClickListener<TreatmentCostsExpensesModel> {
            override fun onItemClick(
                item: TreatmentCostsExpensesModel,
                transitionView: View?,
                tag: String?,
            ) {
                  if (item.repId!=0 && item.repId!=null) {
                      showDialog(
                          id = item.repId
                      )
                  } else {
                      showAlertDialog(
                          MessageOfRequestDialogFragment.MessageType.ERROR,
                          getString(R.string.error_recive_data)
                      )
                  }
            }
        }
    }

    override fun getBindingVariable() = Pair(BR.viewModel, mViewModel)

    override fun getLayoutId() = R.layout.fragment_treatment_costs

    override fun setupObserver() {
        mViewModel.mldTreatmentCostsPDF.observe(viewLifecycleOwner, ::onDownloadPdfFile)
        mViewModel.mldSendToInboxTreatmentCosts.observe(viewLifecycleOwner, ::onSendToInbox)
    }

    private fun onSendToInbox(result: SendToInboxTreatmentCosts) {
        if (result.isSuccess) {
            showAlertDialog(
                MessageOfRequestDialogFragment.MessageType.SUCCESS,
                requireContext().getString(R.string.message_success_send_to_inbox_certificate)
            )
        }
    }

    private fun onDownloadPdfFile(result: PdfDownloadResponse?) {
        val file = Utility.writeByteStreamToDisk(
            Utility.getToolbarTitle(arguments),
            requireContext(),
            result?.pdf
        )
        if (file == null) {
            showAlertDialog(
                MessageOfRequestDialogFragment.MessageType.ERROR,
                getString(R.string.error_recive_file)
            )
            return
        }

        handlePageDestination(
            R.id.action_treatmentCostsFragment_to_pdf_viewer,
            bundle = Bundle().apply {
                putString(PdfViewerActivity.ARG_TITLE, Utility.getToolbarTitle(arguments))
                putString(PdfViewerActivity.ARG_PDF_FILE_PATH, file.path)
            })
    }

    override fun initView() {
        viewDataBinding?.apply {
            listAdapter.onItemClickListener = onItemClickListener
            setupRecycler(recycler, listAdapter)

            appBar.toolbar.imgInfo.setOnClickListener {
                mViewModel.saveBoolean(Constants.TapTargetHistoryFragment, false)
                //   setViewForShowGide()
            }

            setupToolbar(
                viewDataBinding?.appBar,
                viewDataBinding?.appbarBackgroundImage?.imageBackground,
                moreViews = null
            )
        }
    }

    override fun getData() {
        this@TreatmentCostsFragment.lifecycleScope.launchWhenCreated {
            mViewModel.getTreatmentCosts.collectLatest {
                listAdapter.submitData(it)
            }
        }
    }

    override fun onClick() {}

    fun showDialog(id: Int) {
        MenuDialogFragment.newInstance(true, menuTitle = getString(R.string.select))
            .apply {
                setMenuListener(object : MenuInterface.OnFetchData {
                    override fun onFetch() {
                        viewLifecycleOwner.lifecycleScope.launch {
                            val pager = Pager(
                                config = PagingConfig(Constants.QUERY_PAGE_SIZE_10, 2),
                                pagingSourceFactory = {
                                    LocalPagingSource(mViewModel.getMainActions())
                                })
                            pager.flow.cachedIn(lifecycleScope).collectLatest {
                                updateData(it)
                            }
                        }
                    }
                }, object : MenuInterface.OnResult {
                    override fun onResult(itemResult: MenuModel) {
                        view?.windowToken?.let { Utility.hideKeyboard(requireContext(), it) }
                        viewLifecycleOwner.lifecycleScope.launch {
                            if (itemResult.id == "1"){
                                mViewModel.getTreatmentCostsPDF(id.toString())
                            }else{
                                mViewModel.sendToInboxTreatmentCosts(id.toString())
                            }
                        }
                    }
                })
            }.show(childFragmentManager, "ShowFilterDialog")
    }

}