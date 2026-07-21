package com.tamin.taminhamrah.ui.home.services.inquirePensionStatus

import android.view.View
import android.widget.ScrollView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.map
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.remote.models.services.GeneralRes
import com.tamin.taminhamrah.data.remote.models.services.inquirePensionStatus.InquirePensionStatusModel
import com.tamin.taminhamrah.databinding.FragmentInquirePensionStatusBinding
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.appinterface.DialogClickInterface
import com.tamin.taminhamrah.ui.appinterface.MenuInterface
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.ui.dialog.MenuDialogFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.MessageOfRequestDialogFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.manger.DialogManagerMessageOfRequest
import com.tamin.taminhamrah.ui.home.services.activeRelationInquiry.CertificateToInboxDialogFragment
import com.tamin.taminhamrah.ui.home.services.inquirePensionStatus.adapter.InquirePensionStatusAdapter
import com.tamin.taminhamrah.utils.extentions.createBundle
import com.tamin.taminhamrah.utils.extentions.setupRecycler
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest


@AndroidEntryPoint
class InquirePensionStatusFragment :
    BaseFragment<FragmentInquirePensionStatusBinding, InquirePensionStatusViewModel>(),
    AdapterInterface.OnItemClickListener<Boolean> {

    override val mViewModel: InquirePensionStatusViewModel by viewModels()
    lateinit var listAdapter: InquirePensionStatusAdapter
    var listOfStatusPension = arrayListOf<String>()

    override fun getBindingVariable(): Pair<Int, Any?> {
        return Pair(BR.viewModel, mViewModel)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_inquire_pension_status
    }

    override fun setupObserver() {
        mViewModel.mldCertificateResponse.observe(this, ::showCertificateResult)
    }

    private fun showCertificateResult(result: GeneralRes) {
        if (result.isSuccess) {
            val dialog = DialogManagerMessageOfRequest.getInstanceOfDialog()
            dialog.arguments = createBundle(
                MessageOfRequestDialogFragment.MessageType.SUCCESS,
                getString(R.string.message_success_send_inquire_pension)
            )
            dialog.setDialogClickListener(object : DialogClickInterface.onClickListener {
                override fun onConfirmClick() {
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
        listAdapter = InquirePensionStatusAdapter(this)

        viewDataBinding?.apply {

            setupRecycler(recycler, listAdapter)
            setupToolbar(
                viewDataBinding?.appBar,
                viewDataBinding?.appbarBackgroundImage?.imageBackground,
                moreViews = null
            )
        }
    }


    override fun onClick() {
        viewDataBinding?.apply {

        }
    }

    override fun onItemClick(item: Boolean, transitionView: View?, tag: String?) {
        val bottomSheet = CertificateToInboxInquireDialogFragment.newInstance()
        bottomSheet.show(childFragmentManager, "CertificateBottomSheet")
    }

    /**Because on the eservices site, a non-pension user can also apply
     * for a retirement certificate,so I will not block the send request.*/
    override fun getData() {
        this@InquirePensionStatusFragment.lifecycleScope.launchWhenCreated {
            mViewModel.getResultOfInquirePension.collectLatest { pagingData ->
                val result = pagingData.map { map ->
                    map.statusDesc?.let { listOfStatusPension.add(it) }
                    map
                }
                listAdapter.submitData(result)
            }
        }
    }


}