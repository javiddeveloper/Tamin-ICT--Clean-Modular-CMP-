package com.tamin.taminhamrah.ui.home.services.employer.debt.followUpAndPayment.paidDebtList

import android.view.View
import androidx.fragment.app.viewModels
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.employer.debit.DebtPaidListModel
import com.tamin.taminhamrah.data.remote.models.employer.debit.DebtPaidListResponse
import com.tamin.taminhamrah.databinding.FragmentPaidListBinding
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.MessageOfRequestDialogFragment
import com.tamin.taminhamrah.ui.home.services.employer.debt.adapter.PaidDebtListAdapter
import com.tamin.taminhamrah.ui.home.services.employer.debt.viewModel.InstallmentDebtViewModel
import com.tamin.taminhamrah.utils.UiUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PaidDebtListFragment : BaseFragment<FragmentPaidListBinding, InstallmentDebtViewModel>() {

    //region Variables
    override val mViewModel: InstallmentDebtViewModel by viewModels()
    val listAdapter by lazy { PaidDebtListAdapter() }
    //endregion

    //region BaseMethods
    override fun getBindingVariable() = Pair(BR.viewModel, mViewModel)
    override fun getLayoutId() = R.layout.fragment_paid_list
    override fun setupObserver() { mViewModel.mldDebtPaidList.observe(this, ::debtPaidListListeners) }
    override fun initView() {
        viewDataBinding?.apply {
            recycler.adapter = listAdapter
            if (recycler.itemDecorationCount == 0) {
                recycler.addItemDecoration(UiUtils.VerticalItemMarginDecoration(20,setTopMargin = true))
            }
            setupToolbar(appBar, appbarBackgroundImage.imageBackground)
        }
    }
    override fun getData() {
        val debtNumber = arguments?.getLong(Constants.DEBIT_SERIAL_NUMBER)
        if (debtNumber!=null && debtNumber!=0L) {
            mViewModel.getPaidInfo(debtNumber.toString())
        }else{
            showAlertDialog(MessageOfRequestDialogFragment.MessageType.ERROR,getString(R.string.error_recive_data))
        }
    }
    override fun onClick() {}
    //endregion

    //region Listeners
    private fun debtPaidListListeners(response: DebtPaidListResponse) {
        if (response.isSuccess) {
            if (response.data?.list.isNullOrEmpty()){
                showAlertDialog(MessageOfRequestDialogFragment.MessageType.INFO,getString(R.string.not_exist_info), dismissType = MessageOfRequestDialogFragment.DismissType.BACK_TO_PREVIOUS)
            }
            listAdapter.setItems(response.data?.list,object :AdapterInterface.OnItemClickListener<DebtPaidListModel>{
                override fun onItemClick(
                    item: DebtPaidListModel,
                    transitionView: View?,
                    tag: String?
                ) {

                }
            })
        }
    }
    //endregion

    //region Utils

    //endregion
}