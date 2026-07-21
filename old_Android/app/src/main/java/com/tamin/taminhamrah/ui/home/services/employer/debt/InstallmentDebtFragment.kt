package com.tamin.taminhamrah.ui.home.services.employer.debt

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.services.workshop.WorkshopInfo
import com.tamin.taminhamrah.databinding.FragmentInstallmentDebtBinding
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.ui.home.services.employer.debt.bottomSheet.DebtSearchBottomSheet
import com.tamin.taminhamrah.ui.home.services.employer.debt.viewModel.InstallmentDebtViewModel

import com.tamin.taminhamrah.ui.home.services.employer.workshopInfo.WorkshopInfoAdapter
import com.tamin.taminhamrah.utils.extentions.setupRecycler
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber

@AndroidEntryPoint
class InstallmentDebtFragment :
    BaseFragment<FragmentInstallmentDebtBinding, InstallmentDebtViewModel>() {

    override val mViewModel: InstallmentDebtViewModel by viewModels()
    override fun getBindingVariable() = Pair(BR.viewModel, mViewModel)

    companion object {
        const val WORK_SHOP_TAG = "WORK_SHOP_TAG"

    }

    val listAdapter by lazy { WorkshopInfoAdapter(onWorkshopClickListener) }

    private val onWorkshopClickListener =
        object : AdapterInterface.OnItemClickListener<WorkshopInfo> {
            override fun onItemClick(item: WorkshopInfo, transitionView: View?, tag: String?) {
                /*  WorkshopActionBottomSheet.getInstance(
                    workshopActionClickListener,
                     Bundle().apply { putSerializable(WORK_SHOP_TAG, item) })
                     .show(childFragmentManager, "")*/
                    handlePageDestination(R.id.action_installment_to_debt, Bundle().apply {
                        putInt(Constants.TOOLBAR_ICON_IMAGE, R.drawable.ic_mobile_payment)
                        putString(Constants.TOOLBAR_TITLE, getString(R.string.registration_debt_installment_request))
                        putSerializable(WORK_SHOP_TAG, item)
                    })
            }
        }

   /* val workshopActionClickListener = object : WorkshopActionBottomSheet.WorkshopActionListener {
        override fun onShowDebtClick(item: WorkshopInfo) {
            handlePageDestination(R.id.action_installment_to_debt, Bundle().apply {
                putInt(Constants.TOOLBAR_ICON_IMAGE, R.drawable.ic_mobile_payment)
                putString(Constants.TOOLBAR_TITLE, getString(R.string.registration_debt_installment_request))
                putSerializable(WORK_SHOP_TAG, item)
            })
        }

        override fun onFollowRequestClick(item: WorkshopInfo) {}
    }*/

    override fun getLayoutId() = R.layout.fragment_installment_debt

    override fun setupObserver() {

    }

    override fun initView() {
        setupRecycler(viewDataBinding?.recycler, listAdapter)

        setupToolbar(
            viewDataBinding?.appBar,
            viewDataBinding?.appbarBackgroundImage?.imageBackground,
            actionIconRes = R.drawable.ic_search
        )

        setupHintText()
    }

    private fun setupHintText() {
        viewDataBinding?.apply {
            layoutDesc1.descTxt.text = getText(R.string.desc_installment_1)
            layoutDesc2.descTxt.text = getText(R.string.desc_installment_3)
            layoutDesc3.descTxt.text = getText(R.string.desc_installment_4)
        }
    }

    override fun getData() {
        collectWorkshopsData()
    }

    private fun collectWorkshopsData(workshopId: String = "", contractRow: String = "") {
        lifecycleScope.launchWhenCreated {
            mViewModel.getWorkshopItemsFlow(workshopId, contractRow).collectLatest { data ->
                listAdapter.submitData(data)
            }
        }
    }

    override fun onClick() {
        viewDataBinding?.apply {
            appBar.toolbar.imgAction.setOnClickListener {
                workshopSearchBottomSheet.show(childFragmentManager, "")
            }
            btnActions.setOnClickListener {
                val bundle = Bundle().apply {
                    putInt(Constants.TOOLBAR_ICON_IMAGE, R.drawable.ic_mobile_payment)
                    putString(Constants.TOOLBAR_TITLE, getString(R.string.label_follow_installment))
                }
                handlePageDestination(R.id.action_installment_to_follow_debt, bundle)
            }

        }
    }

    private val workshopSearchBottomSheet by lazy {
        val dialog = DebtSearchBottomSheet(workshopSearchListener)
        dialog.arguments = Bundle().apply {
            putSerializable(
                DebtSearchBottomSheet.DIALOG_TYPE,
                DebtSearchBottomSheet.SearchType.AGREEMENT_ROW
            )
        }
        dialog
    }
    private val workshopSearchListener = object : DebtSearchBottomSheet.WorkshopSearchListener {
        override fun onWorkshopSearchListener(workshopId: String, agreementRow: String) {
            Timber.tag("workshopData")
                .i("Dialog result : workshopId=$workshopId agreementRow=$agreementRow")
            collectWorkshopsData(workshopId, agreementRow)

        }

    }
}



