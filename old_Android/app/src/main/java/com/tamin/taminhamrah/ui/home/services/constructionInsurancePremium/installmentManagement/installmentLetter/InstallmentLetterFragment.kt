package com.tamin.taminhamrah.ui.home.services.constructionInsurancePremium.installmentManagement.installmentLetter

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
import com.tamin.taminhamrah.data.remote.models.services.constructionInsurancePremium.InstallmentLetterListModel
import com.tamin.taminhamrah.databinding.FragmentInstallmentLetterBinding
import com.tamin.taminhamrah.ui.LocalPagingSource
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.appinterface.MenuInterface
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.ui.dialog.MenuDialogFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.MessageOfRequestDialogFragment
import com.tamin.taminhamrah.ui.home.services.constructionInsurancePremium.ConstructionInsurancePremiumViewModel
import com.tamin.taminhamrah.ui.home.services.constructionInsurancePremium.adapter.InstallmentConstructionAdapter
import com.tamin.taminhamrah.utils.Utility
import com.tamin.taminhamrah.utils.extentions.setupRecycler
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class InstallmentLetterFragment :
    BaseFragment<FragmentInstallmentLetterBinding, ConstructionInsurancePremiumViewModel>() {

    //region Variables
    override val mViewModel: ConstructionInsurancePremiumViewModel by viewModels()
    val listAdapter by lazy {
        InstallmentConstructionAdapter()
    }
    val onItemClickListener by lazy {
        object : AdapterInterface.OnItemClickListener<InstallmentLetterListModel> {
            override fun onItemClick(
                item: InstallmentLetterListModel,
                transitionView: View?,
                tag: String?
            ) {
                showDialog(item)
            }
        }
    }
    var branchId = ""
    var workshopId = ""

    //endregion

    //region Base Methods
    override fun getBindingVariable() = Pair(BR.viewModel, mViewModel)

    override fun getLayoutId() = R.layout.fragment_installment_letter

    override fun setupObserver() {
    }

    override fun initView() {
        viewDataBinding?.apply {
            setupRecycler(recyclerRequestInfo, listAdapter)
            listAdapter.onItemClickListener =onItemClickListener
        }
    }

    override fun getData() {
        arguments?.apply {
            branchId = getString(Constants.BRANCH_ID) ?: ""
            workshopId = getString(Constants.WORKSHOP_ID) ?: ""
        }
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            mViewModel.getInstallmentLetterList(workShopId = workshopId, branchId = branchId)
                .collectLatest { pagingData ->
                    listAdapter.submitData(pagingData)
                }
        }
    }

    override fun onClick() {
        viewDataBinding?.apply {
            imageBack.setOnClickListener { requireActivity().onBackPressed() }
        }
    }
    //endregion

    //region Utils
    fun showDialog(info: InstallmentLetterListModel? = null) {
        MenuDialogFragment.newInstance(menuTitle = getString(R.string.select))
            .apply {
                setMenuListener(object : MenuInterface.OnFetchData {
                    override fun onFetch() {
                        viewLifecycleOwner.lifecycleScope.launch {
                            val pager = Pager(
                                config = PagingConfig(Constants.QUERY_PAGE_SIZE_10, 2),
                                pagingSourceFactory = {
                                    LocalPagingSource(mViewModel.getInstallmentListAction())
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
                            when (itemResult.id) {

                                "1" -> {
                                    handlePageDestination(
                                        R.id.action_installment_action_to_installment_management,
                                        Bundle().apply {
                                            putString(
                                                Constants.TOOLBAR_TITLE,
                                                getString(itemResult.titleStringResId)
                                            )
                                            putInt(
                                                Constants.TOOLBAR_ICON_IMAGE,
                                                itemResult.iconRes
                                            )

                                            info?.debitNumber?.let {
                                                putString(
                                                    Constants.BRANCH_ID,
                                                    branchId
                                                )
                                                putString(
                                                    Constants.DEBIT_NUMBER,
                                                    it
                                                )
                                            } ?: showAlertDialog(
                                                MessageOfRequestDialogFragment.MessageType.ERROR,
                                                getString(R.string.error_recive_data)
                                            )
                                        })

                                }
                                "2" -> {
                                    handlePageDestination(
                                        R.id.action_installment_action_to_debit_list_fragment,
                                        Bundle().apply {
                                            putString(
                                                Constants.TOOLBAR_TITLE,
                                                getString(itemResult.titleStringResId)
                                            )
                                            putInt(
                                                Constants.TOOLBAR_ICON_IMAGE,
                                                itemResult.iconRes
                                            )

                                            info?.debitNumber?.let {
                                                putString(
                                                    Constants.BRANCH_ID,
                                                    branchId
                                                )
                                                putString(
                                                    Constants.DEBIT_NUMBER,
                                                    it
                                                )
                                            } ?: showAlertDialog(
                                                MessageOfRequestDialogFragment.MessageType.ERROR,
                                                getString(R.string.error_recive_data)
                                            )
                                        })
                                }

                            }
                        }
                    }
                })
            }.show(childFragmentManager, "ConstructionInsurancePremiumFragment_ShowActionDialog")
        //endregion
    }

}