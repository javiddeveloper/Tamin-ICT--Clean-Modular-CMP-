package com.tamin.taminhamrah.ui.home.services.activeRelationInquiry

import android.view.View
import android.widget.ScrollView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.map
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.remote.models.services.ActiveRelation
import com.tamin.taminhamrah.data.remote.models.services.CheckInsuredInfoResponse
import com.tamin.taminhamrah.data.remote.models.services.EnumTypeUser
import com.tamin.taminhamrah.data.remote.models.services.GeneralRes
import com.tamin.taminhamrah.databinding.FragmentActiveRelationInquiryBinding
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.appinterface.DialogClickInterface
import com.tamin.taminhamrah.ui.appinterface.MenuInterface
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.ui.dialog.MenuDialogFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.MessageOfRequestDialogFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.manger.DialogManagerMessageOfRequest
import com.tamin.taminhamrah.ui.home.services.activeRelationInquiry.adapter.ActiveRelationAdapter
import com.tamin.taminhamrah.utils.extentions.createBundle
import com.tamin.taminhamrah.utils.extentions.setupRecycler
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber

@AndroidEntryPoint
class ActiveRelationInquiryFragment :
    BaseFragment<FragmentActiveRelationInquiryBinding, ActiveRelationInquiryViewModel>(),
    AdapterInterface.OnItemClickListener<ActiveRelation>, DialogClickInterface.onClickListener {

    lateinit var listAdapter: ActiveRelationAdapter
    override val mViewModel: ActiveRelationInquiryViewModel by viewModels()

    override fun getBindingVariable(): Pair<Int, Any?> {
        return Pair(BR.viewModel, mViewModel)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_active_relation_inquiry
    }

    override fun setupObserver() {
        mViewModel.mldCertificateResponse.observe(this, ::showResult)
        mViewModel.mldPensionCheck.observe(this, ::onPensionCheck)
    }

    private fun showResult(result: GeneralRes) {
        if (result.isSuccess) {
            val dialog = DialogManagerMessageOfRequest.getInstanceOfDialog()
            dialog.arguments = createBundle(
                MessageOfRequestDialogFragment.MessageType.SUCCESS,
                getString(R.string.message_success_send_active_relation_certificate)
            )
            dialog.setDialogClickListener(object : DialogClickInterface.onClickListener {
                override fun onConfirmClick() {
                    //   handlePageDestination(R.id.action_objectionInsuranceHistoryFragment_to_servicesFragment)
                    requireActivity().onBackPressed()
                }

                override fun onCancelClick() {
                }
            }
            )
            dialog.show(childFragmentManager, "showCertificateResult")
        }
    }


    override fun initView() {

        setupToolbar(
            viewDataBinding?.appBar,
            viewDataBinding?.appbarBackgroundImage?.imageBackground/*,
            actionIconRes = R.drawable.ic_search*/
        )

        listAdapter = ActiveRelationAdapter(this)
        setupRecycler(
            viewDataBinding?.recycler,
            listAdapter,
            emptyMessage = getString(R.string.no_active_relation)
        )


    }

    override fun getData() {
        mViewModel.pensionCheck()
    }



    override fun onClick() {}

    override fun onItemClick(item: ActiveRelation, transitionView: View?, tag: String?) {
        mViewModel.selectedItem = item
        val bottomSheet = CertificateToInboxDialogFragment.newInstance()
        bottomSheet.show(childFragmentManager, "CertificateBottomSheet")
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
                    result.data?.list?.get(1)
                        ?: getString(R.string.error_active_relation_user_is_pensioner)
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
                this@ActiveRelationInquiryFragment.lifecycleScope.launchWhenCreated {
                    mViewModel.mldActiveRelation.collectLatest { pagingData ->
                        listAdapter.submitData(pagingData)
                    }
                }
            }
        }
    }

    override fun onConfirmClick() {
        requireActivity().onBackPressed()
    }

    override fun onCancelClick() {
    }
}

