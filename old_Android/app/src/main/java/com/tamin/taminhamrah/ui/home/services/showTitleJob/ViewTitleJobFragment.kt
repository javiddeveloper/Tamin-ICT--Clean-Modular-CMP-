package com.tamin.taminhamrah.ui.home.services.showTitleJob

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.services.CheckInsuredInfoResponse
import com.tamin.taminhamrah.data.remote.models.services.EnumTypeUser
import com.tamin.taminhamrah.databinding.FragmentViewTitleJobBinding
import com.tamin.taminhamrah.ui.appinterface.DialogClickInterface
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.MessageOfRequestDialogFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.manger.DialogManagerMessageOfRequest
import com.tamin.taminhamrah.utils.extentions.createBundle
import com.tamin.taminhamrah.utils.extentions.setupRecycler
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ViewTitleJobFragment : BaseFragment<FragmentViewTitleJobBinding, ViewTitleJobViewModel>(){

    override val mViewModel: ViewTitleJobViewModel by viewModels()
    private val listAdapter by lazy {ViewTitleJobAdapter()}

    override fun initView() {
        viewDataBinding?.apply {
            setupRecycler(recycler, listAdapter)
            setupToolbar(
                appBar,
                appbarBackgroundImage.imageBackground,
                moreViews = null,
            )
        }
    }

    override fun getData() {
        mViewModel.pensionCheck()
    }

    override fun onClick() {
    }

    override fun getBindingVariable(): Pair<Int, Any?> {
        return Pair(BR.viewModel, mViewModel)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_view_title_job
    }

    override fun setupObserver() {
        mViewModel.mldPensionCheck.observe(this, ::onPensionCheck)
    }

    private fun onPensionCheck(result: CheckInsuredInfoResponse) {
        if (!result.isSuccess) return
        when (result.data?.typeUser) {
            EnumTypeUser.ANONYMOUS.title -> {
                showAlertDialog(MessageOfRequestDialogFragment.MessageType.ERROR,getString(R.string.error_recive_data),dismissType = MessageOfRequestDialogFragment.DismissType.BACK_TO_PREVIOUS)
            }
            EnumTypeUser.PENSIONER.title -> {
                val dialog = DialogManagerMessageOfRequest.getInstanceOfDialog()
                dialog.arguments = createBundle(
                    MessageOfRequestDialogFragment.MessageType.ERROR,
                    result.data?.list?.get(1) ?: getString(R.string.error_active_relation_user_is_pensioner)
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
                viewLifecycleOwner.lifecycleScope.launchWhenCreated {
                    mViewModel.getTitlesJob.collectLatest {
                        listAdapter.submitData(it)
                    }
                }
            }
        }
    }

}