package com.tamin.taminhamrah.ui.home.services.constructionInsurancePremium

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.remote.models.services.constructionInsurancePremium.ConstructionFileModel
import com.tamin.taminhamrah.databinding.FragmentConstructionInsurancePremiumBinding
import com.tamin.taminhamrah.ui.LocalPagingSource
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.appinterface.MenuInterface
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.ui.dialog.MenuDialogFragment
import com.tamin.taminhamrah.ui.home.services.constructionInsurancePremium.adapter.ConstructionFilesAdapter
import com.tamin.taminhamrah.utils.Utility
import com.tamin.taminhamrah.utils.extentions.gone
import com.tamin.taminhamrah.utils.extentions.scaleY
import com.tamin.taminhamrah.utils.extentions.setupRecycler
import com.tamin.taminhamrah.utils.extentions.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ConstructionInsurancePremiumFragment :
    BaseFragment<FragmentConstructionInsurancePremiumBinding, ConstructionInsurancePremiumViewModel>() {

    //region Variables
    override val mViewModel: ConstructionInsurancePremiumViewModel by viewModels()
    private val constructionFilesAdapter by lazy { ConstructionFilesAdapter() }
    //endregion

    //region Listeners
    val onItemClickListener by lazy {
        object : AdapterInterface.OnItemClickListener<ConstructionFileModel> {
            override fun onItemClick(
                item: ConstructionFileModel,
                transitionView: View?,
                tag: String?,
            ) {
                showDialog(workshopInfo = item)
            }
        }
    }
    //endregion

    //region Base Methods
    override fun getBindingVariable() = Pair(BR.viewModel, mViewModel)

    override fun getLayoutId() = R.layout.fragment_construction_insurance_premium

    override fun setupObserver() {
    }

    override fun initView() {
        setupRecycler(viewDataBinding?.workshopListRecycler, adapter = constructionFilesAdapter)
        constructionFilesAdapter.onItemClickListener = onItemClickListener
        viewDataBinding?.apply {
            setupToolbar(
                appBar,
                appbarBackgroundImage.imageBackground
            )
            hint1.descTxt.text = this@ConstructionInsurancePremiumFragment.getText(R.string.construction_insurance_hint1)
            hint2.descTxt.text = this@ConstructionInsurancePremiumFragment.getText(R.string.construction_insurance_hint2)
            hint3.descTxt.text = this@ConstructionInsurancePremiumFragment.getText(R.string.construction_insurance_hint3)
            hint4.descTxt.text = this@ConstructionInsurancePremiumFragment.getText(R.string.construction_insurance_hint4)
        }

    }

    override fun getData() {
        getConstructionFiles()
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
                getConstructionFiles(
                    workshopId = inputWorkshopCodeSearch.getValue(false),
                    branchCode = inputBranchCodeSearch.getValue(false),
                    requestNumber = inputRequestNumSearch.getValue(false),
                    fileNumber = inputFileNumSearch.getValue(false)
                )
            }
            btnAllItem.setOnClickListener {
                view?.windowToken?.let { Utility.hideKeyboard(requireContext(), it) }
                groupSearch.gone()
                btnOpenSearch.visible()
                getConstructionFiles()
            }

            btnAllItem.setOnClickListener {
                groupSearch.isVisible = !groupSearch.isVisible
            }
            btnCloseDescription.setOnClickListener {
                btnCloseDescription.scaleY(state = (btnCloseDescription.scaleY == -1f))

                view?.windowToken?.let { Utility.hideKeyboard(requireContext(),it) }
                groupHints.isVisible = !groupHints.isVisible
            }
        }
    }

    //endregion
    //region Utils
    private fun getConstructionFiles(
        workshopId: String? = null,
        branchCode: String? = null,
        requestNumber: String? = null,
        fileNumber: String? = null,
    ) {

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            mViewModel.getConstructionFiles(
                workshopId = workshopId,
                branchCode = branchCode,
                requestNumber = requestNumber,
                fileNumber = fileNumber
            ).collectLatest { pagingData ->
                view?.windowToken?.let { Utility.hideKeyboard(requireContext(), it) }
                viewDataBinding?.rootLayout?.visible()
                constructionFilesAdapter.submitData(pagingData)
            }
        }

    }

    fun showDialog(workshopInfo: ConstructionFileModel? = null) {
        MenuDialogFragment.newInstance(menuTitle = getString(R.string.select))
            .apply {
                setMenuListener(object : MenuInterface.OnFetchData {
                    override fun onFetch() {
                        viewLifecycleOwner.lifecycleScope.launch {
                            val pager = Pager(
                                config = PagingConfig(Constants.QUERY_PAGE_SIZE_10, 2),
                                pagingSourceFactory = {
                                    LocalPagingSource(mViewModel.getCashListAction(workshopInfo?.debitStatusCode == "51"))
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
                                        R.id.action_to_view_detail_request,
                                        Bundle().apply {
                                            putString(
                                                Constants.TOOLBAR_TITLE,
                                                getString(itemResult.titleStringResId)
                                            )
                                            putInt(
                                                Constants.TOOLBAR_ICON_IMAGE,
                                                itemResult.iconRes
                                            )
                                            workshopInfo?.let {
                                                putLong(
                                                    Constants.REQUEST_ID,
                                                    it.requestNumber ?: 0
                                                )
                                                putString(
                                                    Constants.REQUEST_DATE,
                                                    it.requestDate
                                                )
                                                putLong(
                                                    Constants.FILE_ID,
                                                    it.fileNumber ?: 0
                                                )
                                            }
                                        })
                                }

                                "2" -> {
                                    handlePageDestination(
                                        R.id.action_to_payment_sheet,
                                        Bundle().apply {
                                            putString(
                                                Constants.TOOLBAR_TITLE,
                                                getString(itemResult.titleStringResId)
                                            )
                                            putInt(
                                                Constants.TOOLBAR_ICON_IMAGE,
                                                itemResult.iconRes
                                            )
                                            workshopInfo?.let {
                                                putString(
                                                    Constants.BRANCH_ID,
                                                    (it.workshopInfo?.brhCode ?: "").toString()
                                                )
                                                putString(
                                                    Constants.DEBIT_NUMBER,
                                                    (it.debitNumber ?: "").toString()
                                                )
                                                putParcelableArrayList(Constants.REQUEST_INFO,it.getRequestInfoIssuancePaymentSheet())

                                            }
                                        })

                                }
                                "3" -> {
                                    handlePageDestination(
                                        R.id.action_to_installment_management,
                                        Bundle().apply {
                                            putString(
                                                Constants.TOOLBAR_TITLE,
                                                getString(itemResult.titleStringResId)
                                            )
                                            putInt(
                                                Constants.TOOLBAR_ICON_IMAGE,
                                                itemResult.iconRes
                                            )
                                            workshopInfo?.let {
                                                putString(
                                                    Constants.BRANCH_ID,
                                                    (it.workshopInfo?.brhCode ?: "").toString()
                                                )
                                                putString(
                                                    Constants.WORKSHOP_ID,
                                                    (it.workshopInfo?.workshopId ?: "").toString()
                                                )
                                                putParcelableArrayList(Constants.REQUEST_INFO,it.getRequestInfoIssuancePaymentSheet())

                                            }
                                        })

                                }

                                "4" -> {
                                    handlePageDestination(
                                        R.id.action_to_beneficiaries_fragment,
                                        Bundle().apply {
                                            putString(
                                                Constants.TOOLBAR_TITLE,
                                                getString(itemResult.titleStringResId)
                                            )
                                            putInt(
                                                Constants.TOOLBAR_ICON_IMAGE,
                                                itemResult.iconRes
                                            )
                                            workshopInfo?.let {
                                                putLong(
                                                    Constants.REQUEST_ID,
                                                    it.requestNumber ?: 0
                                                )
                                                putString(
                                                    Constants.REQUEST_DATE,
                                                    it.requestDate
                                                )
                                                putLong(
                                                    Constants.FILE_ID,
                                                    it.fileNumber ?: 0
                                                )
                                            }
                                        })
                                }
                            }
                            /*          val bundle = Bundle()
                                      bundle.putString(
                                          Constants.TOOLBAR_TITLE,
                                          getString(WorkshopActionsEnumClass.CORRESPONDENCE_AND_ANNOUNCEMENT_LIST.titleResource)
                                      )
                                      bundle.putInt(
                                          Constants.TOOLBAR_ICON_IMAGE,
                                          item.iconRes
                                      )
                                      handlePageDestination(
                                          R.id.action_workshop_info_to_inbox_fragment,
                                          bundle
                                      )*/

                        }
                    }
                })
            }.show(childFragmentManager, "ConstructionInsurancePremiumFragment_ShowActionDialog")
    }

    //endregion


}