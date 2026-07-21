package com.tamin.taminhamrah.ui.home.services.employer.workshopInfo

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
import com.tamin.taminhamrah.data.remote.models.employer.employerAgreement.EmployerAgreement
import com.tamin.taminhamrah.data.remote.models.services.workshop.WorkshopInfo
import com.tamin.taminhamrah.data.remote.models.services.workshop.definitiveDebtArticle16.WorkshopsDebtListModel
import com.tamin.taminhamrah.data.remote.models.services.workshop.definitiveDebtArticle16.WorkshopsDebtListResponse
import com.tamin.taminhamrah.databinding.FragmentWorkshopListBinding
import com.tamin.taminhamrah.ui.LocalPagingSource
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.appinterface.MenuInterface
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.ui.dialog.MenuDialogFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.MessageOfRequestDialogFragment
import com.tamin.taminhamrah.ui.home.services.employer.workshopInfo.adapter.WorkshopListAdapter
import com.tamin.taminhamrah.ui.home.services.employer.workshopInfo.model.WorkShopDialogTypeEnumClass
import com.tamin.taminhamrah.ui.home.services.employer.workshopInfo.model.WorkshopActionsEnumClass
import com.tamin.taminhamrah.utils.Utility
import com.tamin.taminhamrah.utils.extentions.gone
import com.tamin.taminhamrah.utils.extentions.setupRecycler
import com.tamin.taminhamrah.utils.extentions.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WorkshopInfoFragment :
    BaseFragment<FragmentWorkshopListBinding, WorkshopInfoViewModel>() {

    //Class Variables
    override val mViewModel: WorkshopInfoViewModel by viewModels()
    val adapter by lazy { WorkshopListAdapter() }
    var workshopId = ""
    var branchCode = ""
    var workshopName = ""

    val onItemClickListener by lazy {
        object : AdapterInterface.OnItemClickListener<EmployerAgreement> {
            override fun onItemClick(
                item: EmployerAgreement,
                transitionView: View?,
                tag: String?,
            ) {
                workshopId = item.workshop?.workshopId ?: ""
                branchCode = item.workshop?.branchCode ?: ""
                workshopName = item.workshop?.workshopName ?: ""
                if (workshopId.isNotBlank() && branchCode.isNotBlank()) {
                    showDialog(
                        type = WorkShopDialogTypeEnumClass.ACTION_LIST,
                        workshopInfo = item.workshop
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

    //Base Methods
    override fun getBindingVariable() = Pair(BR.viewModel, mViewModel)
    override fun getLayoutId() = R.layout.fragment_workshop_list
    override fun setupObserver() {
        mViewModel.mldDebtList.observe(this, ::debtListWorkshopResponse)
    }

    override fun initView() {
        setupRecycler(viewDataBinding?.workshopListRecycler, adapter = adapter)
        adapter.onItemClickListener = onItemClickListener
        setupToolbar(
            viewDataBinding?.appBar,
            viewDataBinding?.appbarBackgroundImage?.imageBackground
        )
    }

    override fun getData() {
        getWorkshopList()
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
                getWorkshopList(
                    workshopId = inputWorkshopCodeSearch.getValue(false),
                    branchCode = inputBranchCodeSearch.getValue(false)
                )
            }
            btnAllItem.setOnClickListener {
                getWorkshopList()
            }
            btnFilter.setOnClickListener {
                showDialog(type = WorkShopDialogTypeEnumClass.FILTER_LIST)
            }
        }
    }

    //Listeners
    private fun debtListWorkshopResponse(result: WorkshopsDebtListResponse) {
        if (result.isSuccess) {
            val list = arrayListOf<WorkshopsDebtListModel>()
            list.addAll(result.data?.list ?: emptyList())
            if (list.isEmpty()) {
                showAlertDialog(
                    MessageOfRequestDialogFragment.MessageType.INFO,
                    getString(R.string.not_have_debt_list_for_this_workshop)
                )
            } else {
                handlePageDestination(R.id.action_workshop_info_to_article16_debts_list,
                    Bundle().apply {
                        putString(Constants.WORKSHOP_NAME, workshopName)
                        putString(Constants.WORKSHOP_ID, workshopId)
                        putString(Constants.BRANCH_ID, branchCode)
                    })
            }
        }
    }

    //Utils
    private fun getWorkshopList(
        workshopId: String? = null,
        branchCode: String? = null,
        filterId: String? = null,
    ) {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            mViewModel.getEmployerAgreementInfoList(workshopId, branchCode, filterId)
                .collectLatest { pagingData ->
                    viewDataBinding?.rootLayout?.visible()
                    adapter.submitData(pagingData)
                }
        }
    }

    fun showDialog(workshopInfo: WorkshopInfo? = null, type: WorkShopDialogTypeEnumClass) {
        MenuDialogFragment.newInstance(
            true, menuTitle =
            if (type == WorkShopDialogTypeEnumClass.FILTER_LIST)
                getString(R.string.select_filter_workshop_list)
            else
                getString(R.string.select)
        ).apply {
                setMenuListener(object : MenuInterface.OnFetchData {
                    override fun onFetch() {
                        viewLifecycleOwner.lifecycleScope.launch {
                            val pager = Pager(
                                config = PagingConfig(Constants.QUERY_PAGE_SIZE_10, 2),

                                pagingSourceFactory = {
                                    if (type == WorkShopDialogTypeEnumClass.FILTER_LIST) {
                                        LocalPagingSource(mViewModel.getWorkshopFilterList())
                                    } else {
                                        LocalPagingSource(mViewModel.getMainWorkshopActions())
                                    }
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
                            when (type) {
                                WorkShopDialogTypeEnumClass.FILTER_LIST -> {
                                    getWorkshopList(filterId = itemResult.id)
                                }
                                WorkShopDialogTypeEnumClass.ACTION_LIST -> {
                                    when (itemResult.id) {
                                        WorkshopActionsEnumClass.PAYMENT_SHEET.id -> {
                                            handlePageDestination(
                                                R.id.action_workshopInfo_to_payment_sheet,
                                                setBundleInfo(
                                                    workshopInfo,
                                                    getString(WorkshopActionsEnumClass.PAYMENT_SHEET.titleResource),
                                                    itemResult.iconRes
                                                )
                                            )
                                        }
                                        WorkshopActionsEnumClass.DEBIT_ACCOUNT_TURNOVER_DETAILS.id -> {
                                            handlePageDestination(
                                                R.id.action_workshopInfo_to_debit,
                                                setBundleInfo(
                                                    workshopInfo,
                                                    getString(WorkshopActionsEnumClass.DEBIT_ACCOUNT_TURNOVER_DETAILS.titleResource),
                                                    itemResult.iconRes
                                                )
                                            )
                                        }
                                        WorkshopActionsEnumClass.INQUIRY_DEBITS_WORKSHOP.id -> {
                                            handlePageDestination(
                                                R.id.action_workshopInfo_to_debt_inquiry,
                                                setBundleInfo(
                                                    workshopInfo,
                                                    getString(WorkshopActionsEnumClass.INQUIRY_DEBITS_WORKSHOP.titleResource),
                                                    itemResult.iconRes
                                                )
                                            )
                                        }
                                        WorkshopActionsEnumClass.OBJECTION_TO_DEBIT.id -> {
                                            handlePageDestination(
                                                R.id.action_workshopInfo_to_objectionable_debit,
                                                setBundleInfo(
                                                    workshopInfo,
                                                    getString(WorkshopActionsEnumClass.OBJECTION_TO_DEBIT.titleResource),
                                                    itemResult.iconRes
                                                )
                                            )
                                        }
                                        WorkshopActionsEnumClass.INSURED_ABSENTEE_REGISTRATION.id -> {
                                            handlePageDestination(
                                                R.id.action_workshopInfo_to_recently_added_insured,
                                                setBundleInfo(
                                                    workshopInfo,
                                                    getString(WorkshopActionsEnumClass.INSURED_ABSENTEE_REGISTRATION.titleResource),
                                                    itemResult.iconRes
                                                )
                                            )
                                        }

                                        WorkshopActionsEnumClass.REGISTRATION_DEBIT_ARTICLE16.id -> {
                                            if (workshopId.isNotBlank() && branchCode.isNotBlank()) {
                                                viewLifecycleOwner.lifecycleScope.launch {
                                                    mViewModel.getWorkshopsDebtsList(
                                                        workshopId = workshopId,
                                                        branchCode = branchCode
                                                    )
                                                }
                                            } else {
                                                showAlertDialog(
                                                    MessageOfRequestDialogFragment.MessageType.ERROR,
                                                    getString(R.string.error_recive_data)
                                                )
                                            }
                                        }

                                        WorkshopActionsEnumClass.EMPLOYEES.id -> {
                                            handlePageDestination(
                                                R.id.action_workshopInfo_to_members,
                                                setBundleInfo(
                                                    workshopInfo,
                                                    getString(WorkshopActionsEnumClass.EMPLOYEES.titleResource),
                                                    itemResult.iconRes
                                                )
                                            )
                                        }
                                        WorkshopActionsEnumClass.STACK_HOLDERS.id -> {
                                            handlePageDestination(
                                                R.id.action_workshopInfo_to_stack_holders,
                                                setBundleInfo(
                                                    workshopInfo,
                                                    getString(WorkshopActionsEnumClass.STACK_HOLDERS.titleResource),
                                                    itemResult.iconRes
                                                )
                                            )
                                        }
//                                        WorkshopActionsEnumClass.DISTANT_CORRESPONDENCE.id -> {
//                                            val bundle = Bundle()
//                                            bundle.putString(
//                                                Constants.TOOLBAR_TITLE,
//                                                getString(R.string.label_distant_correspondence_list)
//                                            )
//                                            bundle.putString(
//                                                Constants.TOOLBAR_SUBTITLE,
//                                                "${getString(R.string.label_workshop_name)} : ${workshopInfo?.workshopName}"
//                                            )
//                                            bundle.putInt(
//                                                Constants.TOOLBAR_ICON_IMAGE,
//                                                item.iconRes
//                                            )
//                                            bundle.putString(
//                                                DistantCorrespondenceListFragment.ARG_CONTRACT_ROW,
//                                                workshopInfo?.contractRow
//                                            )
//                                            bundle.putString(
//                                                DistantCorrespondenceListFragment.ARG_WORKSHOP_ID,
//                                                workshopInfo?.workshopId
//                                            )
//                                            bundle.putString(
//                                                DistantCorrespondenceListFragment.ARG_BRANCH_CODE,
//                                                workshopInfo?.branchCode
//                                            )
//                                            handlePageDestination(
//                                                R.id.action_workshop_info_to_letter_list,
//                                                bundle
//                                            )
//                                        }
                                        WorkshopActionsEnumClass.CORRESPONDENCE_AND_ANNOUNCEMENT_LIST.id -> {
                                            val bundle = Bundle()
                                            bundle.putString(
                                                Constants.TOOLBAR_TITLE,
                                                getString(WorkshopActionsEnumClass.CORRESPONDENCE_AND_ANNOUNCEMENT_LIST.titleResource)
                                            )
                                            bundle.putInt(
                                                Constants.TOOLBAR_ICON_IMAGE,
                                                itemResult.iconRes
                                            )
                                            handlePageDestination(
                                                R.id.action_workshop_info_to_inbox_fragment,
                                                bundle
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                })
            }.show(childFragmentManager, "ShowFilterDialog")
    }

    private fun setBundleInfo(info: WorkshopInfo?, menuTitle: String?,iconRes:Int) = Bundle().apply {
        putString(Constants.TOOLBAR_TITLE, menuTitle)
        putString(
            Constants.TOOLBAR_SUBTITLE,
            "${getString(R.string.label_workshop_name)} : ${info?.workshopName} "
        )
        putString(
            Constants.TOOLBAR_SUB_SUBTITLE,
            "${getString(R.string.label_workshop_code)} : ${info?.workshopId} "
        )
        putInt(
            Constants.TOOLBAR_ICON_IMAGE,
            iconRes
        )
//        putString(Constants.TOOLBAR_ICON_IMAGE, Utility.getToolbarIconImage(arguments))
        putString(Constants.WORKSHOP_ID, info?.workshopId)
        putString(Constants.BRANCH_ID, info?.branchCode)
        putString(Constants.WORKSHOP_NAME, info?.workshopName)
    }

}