package com.tamin.taminhamrah.ui.home.services.employer.workshopInfo.article16.debtList

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
import com.tamin.taminhamrah.data.remote.models.pdfFile.PdfDownloadResponse
import com.tamin.taminhamrah.data.remote.models.services.workshop.definitiveDebtArticle16.Article16RequestInfoResponse
import com.tamin.taminhamrah.data.remote.models.services.workshop.definitiveDebtArticle16.WorkshopsDebtListModel
import com.tamin.taminhamrah.databinding.FragmentDebtListBinding
import com.tamin.taminhamrah.ui.LocalPagingSource
import com.tamin.taminhamrah.ui.PdfViewerActivity
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.appinterface.MenuInterface
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.ui.dialog.MenuDialogFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.MessageOfRequestDialogFragment
import com.tamin.taminhamrah.ui.home.services.employer.workshopInfo.article16.DefinitiveDebtArticle16ViewModel
import com.tamin.taminhamrah.ui.home.services.employer.workshopInfo.article16.adapter.DebtListAdapter
import com.tamin.taminhamrah.ui.home.services.employer.workshopInfo.article16.model.ActionDebtEnumClass
import com.tamin.taminhamrah.ui.home.services.employer.workshopInfo.article16.model.DialogTypeDebtList
import com.tamin.taminhamrah.utils.ConvertDate
import com.tamin.taminhamrah.utils.Utility
import com.tamin.taminhamrah.utils.extentions.gone
import com.tamin.taminhamrah.utils.extentions.setupRecycler
import com.tamin.taminhamrah.utils.extentions.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import saman.zamani.persiandate.PersianDate

@AndroidEntryPoint
class Article16DebtListFragment :
    BaseFragment<FragmentDebtListBinding, DefinitiveDebtArticle16ViewModel>() {

    //Class Variables
    override val mViewModel: DefinitiveDebtArticle16ViewModel by viewModels()
    var workshopName = ""
    var workshopId = ""
    var branchId = ""
    var seqNumber = ""
    val adapter by lazy {
        DebtListAdapter()
    }

    val onItemClickListener by lazy {
        object : AdapterInterface.OnItemClickListener<WorkshopsDebtListModel> {
            override fun onItemClick(
                item: WorkshopsDebtListModel,
                transitionView: View?,
                tag: String?,
            ) {
                showDialog(type = DialogTypeDebtList.ACTION_DIALOG,
                    status = item.status,
                    item.seqNo,
                    item.dateExecutiveNotification)
                mViewModel.dataModel.detailDebtInfo = item
            }
        }
    }

    //Base Methods
    override fun getBindingVariable() = Pair(BR.viewModel, mViewModel)
    override fun getLayoutId() = R.layout.fragment_debt_list
    override fun initView() {
        setupRecycler(viewDataBinding?.workshopListRecycler, adapter)
        adapter.onItemClickListener = onItemClickListener
    }

    override fun getData() {
        arguments?.apply {
            workshopName = getString(Constants.WORKSHOP_ID) ?: ""
            workshopId = getString(Constants.WORKSHOP_ID) ?: ""
            branchId = getString(Constants.BRANCH_ID) ?: ""

            if (workshopId.isBlank() || branchId.isBlank()) {
                showAlertDialog(MessageOfRequestDialogFragment.MessageType.ERROR,
                    getString(R.string.error_recive_data),
                    dismissType = MessageOfRequestDialogFragment.DismissType.BACK_TO_PREVIOUS)
            } else {
                getDebtListOfWorkShop()
            }
            viewDataBinding?.tvWorkshopName?.text =
                getString(R.string.accountable_debts, workshopName)
        }
    }

    override fun onClick() {
        viewDataBinding?.apply {
            btnOpenSearch.setOnClickListener {
                groupSearch.visible()
                btnOpenSearch.gone()
            }
            btnCloseSearch.setOnClickListener {
                groupSearch.gone()
                btnOpenSearch.visible()
            }
            btnSearch.setOnClickListener {
                view?.windowToken?.let { Utility.hideKeyboard(requireContext(), it) }
                groupSearch.gone()
                btnOpenSearch.visible()
                getDebtListOfWorkShop(debtNumber = inputDebtNumber.getValue(false),
                    agreementRow = inputAgreementRow.getValue(false))
            }
            btnAllItem.setOnClickListener {
                getDebtListOfWorkShop()
            }
            btnFilter.setOnClickListener {
                showDialog(type = DialogTypeDebtList.FILTER_DIALOG)
            }
            imageBack.setOnClickListener {
                backButtonPress()
            }

        }
    }

    override fun setupObserver() {
        mViewModel.mldPdf.observe(this, ::showPDFResult)
        mViewModel.mldExpertsMessageArticle16.observe(this, ::onExpertsMessageResponse)
    }

    //Listeners

    private fun onExpertsMessageResponse(response: Article16RequestInfoResponse) {
        if (response.isSuccess) {
            showAlertDialog(type = MessageOfRequestDialogFragment.MessageType.INFO,
                response.data?.defectDesc ?: getString(R.string.error_recive_data))
        }
    }

    //Utils
    private fun getDebtListOfWorkShop(debtNumber: String? = null, agreementRow: String? = null) {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            mViewModel.getWorkshopsDebtsListPaging(workshopId = workshopId,
                branchId = branchId, debtNumber = debtNumber, agreementRow = agreementRow)
                .collectLatest { pagingData ->
                    mViewModel.debtList.clear()
                    val data = pagingData.map {
                        mViewModel.debtList.add(it)
                        it
                    }
                    adapter.submitData(pagingData = data)
                }
        }
    }

    fun showDialog(
        type: DialogTypeDebtList,
        status: String? = null,
        seqNumber: Long? = null,
        dateExecutiveNotification: String? = null,
    ) {
        MenuDialogFragment.newInstance(true, menuTitle =
        if (type == DialogTypeDebtList.FILTER_DIALOG)
            getString(R.string.select_filter_article16_debt_list)
        else
            getString(R.string.select)
        )
            .apply {
                setMenuListener(object : MenuInterface.OnFetchData {
                    override fun onFetch() {
                        viewLifecycleOwner.lifecycleScope.launch {
                            val list = ArrayList<MenuModel>()
                            when (type) {
                                DialogTypeDebtList.FILTER_DIALOG -> {
                                    list.addAll(mViewModel.getFilterRequestType())
                                }
                                DialogTypeDebtList.ACTION_DIALOG -> {
                                    when {
                                        status != null -> {
                                            list.addAll(mViewModel.getActionList(status))
                                        }
                                        seqNumber == null -> {
                                            list.addAll(mViewModel.getActionList("0"))
                                        }
                                        else -> {
                                            dismiss()
                                        }
                                    }
                                }
                            }
                            val pager = Pager(
                                config = PagingConfig(Constants.QUERY_PAGE_SIZE_10, 2),
                                pagingSourceFactory = {
                                    LocalPagingSource(list)
                                })

                            pager.flow.cachedIn(lifecycleScope).collectLatest {
                                updateData(it)
                            }

                        }
                    }
                }, object : MenuInterface.OnResult {
                    override fun onResult(itemResult: MenuModel) {
                        when (type) {
                            DialogTypeDebtList.FILTER_DIALOG -> {
                                viewLifecycleOwner.lifecycleScope.launch {
                                    mViewModel.createDebtListPaging().collectLatest { pagingData ->
                                        if (itemResult.id == Constants.DEBT_ALL_ITEM) {
                                            adapter.submitData(pagingData = pagingData)
                                        } else {
                                            val data = pagingData.filter { it.status == itemResult.id }
                                            adapter.submitData(pagingData = data)
                                        }
                                    }
                                }
                            }
                            DialogTypeDebtList.ACTION_DIALOG -> {
                                when (itemResult.id) {
                                    ActionDebtEnumClass.INVESTIGATION_DEBTS.id -> {
                                        val currentPersianDate =
                                            ConvertDate.convertTimestampToPersianDate(PersianDate.today().time)
                                        val diffDate =
                                            Utility.differenceBetweenShamsiDate(startDate = dateExecutiveNotification
                                                ?: "0", endDate = currentPersianDate)
                                        if (diffDate.first > 1) {
                                            this@Article16DebtListFragment.showAlertDialog(
                                                MessageOfRequestDialogFragment.MessageType.WARNING,
                                                getString(R.string.desc_expire_date_submit_request))
                                            return
                                        } else {
                                            handlePageDestination(R.id.action_debt_list_to_debt_investigation,
                                                Bundle().apply {
                                                    putString(Constants.WORKSHOP_NAME,
                                                        workshopName)
                                                    putString(Constants.WORKSHOP_ID,
                                                        workshopId)
                                                    putString(Constants.BRANCH_ID,
                                                        branchId)
                                                    putString(Constants.DEBIT_NUMBER,mViewModel.dataModel.detailDebtInfo?.debitNumber)
                                                    putString(Constants.AGREEMENT_ROW,mViewModel.dataModel.detailDebtInfo?.agreementRow)
                                                    putParcelable(Constants.DATA_CLASS,
                                                        mViewModel.dataModel.detailDebtInfo)
                                                })
                                        }
                                    }
                                    ActionDebtEnumClass.SHOW_REQUEST.id -> {
                                        seqNumber?.let {
                                            mViewModel.getDebitObjectionPdf(seqNumber)
                                        }
                                            ?: showAlertDialog(MessageOfRequestDialogFragment.MessageType.ERROR,
                                                getString(R.string.error_recive_data))
                                    }
                                    ActionDebtEnumClass.EXPERT_MESSAGE.id ->{
                                        seqNumber?.let {
                                            mViewModel.getExpertsMessageArticle16(seqNumber)
                                        }
                                            ?: showAlertDialog(MessageOfRequestDialogFragment.MessageType.ERROR,
                                                getString(R.string.error_recive_data))
                                    }
                                    ActionDebtEnumClass.MODIFY_REQUEST.id ->{
                                        handlePageDestination(R.id.action_debt_list_to_debt_investigation,
                                            Bundle().apply {
                                                putString(Constants.WORKSHOP_NAME,
                                                    workshopName)
                                                putString(Constants.WORKSHOP_ID,
                                                    workshopId)
                                                putString(Constants.BRANCH_ID, branchId)

                                                putString(Constants.STATUS_CODE,mViewModel.dataModel.detailDebtInfo?.status)
                                                putParcelable(Constants.DATA_CLASS,
                                                    mViewModel.dataModel.detailDebtInfo)
                                            })
                                    }
                                }
                            }
                        }

                    }
                })
            }.show(childFragmentManager, "ShowFilterDialog")
    }

    private fun showPDFResult(result: PdfDownloadResponse) {
        if (result.isSuccess) {
            val title = getString(R.string.article_16_service)
            val file = Utility.writeByteStreamToDisk(title, requireContext(), result.pdf)
            if (file == null) {
                showAlertDialog(
                    MessageOfRequestDialogFragment.MessageType.ERROR,
                    getString(R.string.error_recive_file)
                )
                return
            }
            handlePageDestination(R.id.action_article16_to_pdf, Bundle().apply {
                putString(PdfViewerActivity.ARG_TITLE, title)
                putString(PdfViewerActivity.ARG_PDF_FILE_PATH, file.path)
            })
        }
    }

    override fun onResume() {
        super.onResume()
        if (adapter.itemCount > 0) {
            // val bundle = arguments
            getData()
        }
    }

}