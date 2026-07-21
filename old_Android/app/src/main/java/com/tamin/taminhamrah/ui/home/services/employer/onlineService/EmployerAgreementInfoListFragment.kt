package com.tamin.taminhamrah.ui.home.services.employer.onlineService

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.employer.ConfirmUserResponse
import com.tamin.taminhamrah.data.remote.models.employer.employerAgreement.EmployerAgreement
import com.tamin.taminhamrah.databinding.FragmentEmployerAgreementInfoBinding
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.appinterface.DialogResultInterface
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.MessageOfRequestDialogFragment
import com.tamin.taminhamrah.utils.Utility
import com.tamin.taminhamrah.utils.extentions.setupRecycler
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class EmployerAgreementInfoListFragment :
    BaseFragment<FragmentEmployerAgreementInfoBinding, EmployerAgreementInfoViewModel>(),
    DialogResultInterface.OnResultListener<Map<String, String>>,
    AdapterInterface.OnItemClickListener<EmployerAgreement> {

    lateinit var listAdapter: EmployerAgreementInfoAdapter

    override val mViewModel: EmployerAgreementInfoViewModel by viewModels()

    override fun getBindingVariable(): Pair<Int, Any?> {
        return Pair(BR.viewModel, mViewModel)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_employer_agreement_info
    }

    override fun setupObserver() {
//        mViewModel.mldDeleteUser.observe(this@EmployerAgreementInfoList, ::onDeleteUser)
//        mViewModel.mldConfirmedUser.observe(this@EmployerAgreementInfoList, ::onConfirmUser)
    }

    private fun onConfirmUser(result: ConfirmUserResponse?) {
        if (result?.isSuccess == true) {

            showAlertDialog(
                MessageOfRequestDialogFragment.MessageType.ERROR,
                getString(R.string.message_confirm_user_of_workshop, result.data?.refCode)
            )

            getData()
        }
    }

    override fun initView() {
        listAdapter = EmployerAgreementInfoAdapter(this)
        setupRecycler(viewDataBinding?.recycler, listAdapter)

        setupToolbar(
            viewDataBinding?.appBar,
            viewDataBinding?.appbarBackgroundImage?.imageBackground,
            actionIconRes = R.drawable.ic_search
        )
    }

    override fun getData() {
        collectData()
    }

    private fun collectData(workshopCode: String? = null, branchCode: String? = null) {
        this@EmployerAgreementInfoListFragment.lifecycleScope.launchWhenCreated {

            mViewModel.getEmployerAgreementInfoList(workshopCode, branchCode)
                .collectLatest { pagingData -> listAdapter.submitData(pagingData) }
        }
    }

    override fun onClick() {

        viewDataBinding?.let {
            it.appBar.toolbar.apply {
                imgAction.setOnClickListener {

                    val dialog = EmployerAgreementSDialogFragment()
                    dialog.setListener(this@EmployerAgreementInfoListFragment)
                    dialog.show(childFragmentManager, "jfhskljkjllkljl")
                }
            }

            it.btnNewRequest.setOnClickListener {
                val bundle = Bundle()
                bundle.putString(Constants.TOOLBAR_TITLE, getString(R.string.label_register_commitment_request_1))
                bundle.putString(Constants.TOOLBAR_ICON_IMAGE, Utility.getToolbarIconImage(arguments))
                handlePageDestination(R.id.action_agreement_info_to_register_agreement, bundle)
            }
        }
    }

    override fun onDialogResult(item: Map<String, String>) {
        val workshopCode = item[mViewModel.ARG_WORKSHOP_ID] ?: ""
        val branchCode = item[mViewModel.ARG_BRANCH_CODE] ?: ""

        collectData(workshopCode, branchCode)
    }

    override fun onItemClick(item: EmployerAgreement, transitionView: View?, tag: String?) {
        val bundle = Bundle()
        bundle.putString(Constants.TOOLBAR_TITLE, getString(R.string.label_workshop_contract_row_list))
        bundle.putString(Constants.TOOLBAR_ICON_IMAGE, Utility.getToolbarIconImage(arguments))
        bundle.putString(mViewModel.ARG_WORKSHOP_ID, item.workshop?.workshopId)
        bundle.putString(mViewModel.ARG_BRANCH_CODE, item.workshop?.branchCode)

        handlePageDestination(
            R.id.action_agreement_info_to_workshop_list,
            bundle
        )
    }
}