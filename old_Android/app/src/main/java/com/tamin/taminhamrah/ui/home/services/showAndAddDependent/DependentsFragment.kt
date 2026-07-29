package com.tamin.taminhamrah.ui.home.services.showAndAddDependent

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.services.GeneralRes
import com.tamin.taminhamrah.data.remote.models.services.showAndAddDependent.DependentInfoResponse
import com.tamin.taminhamrah.databinding.FragmentShowDependentsBinding
import com.tamin.taminhamrah.ui.appinterface.DialogClickInterface
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.MessageOfRequestDialogFragment.MessageType
import com.tamin.taminhamrah.ui.dialog.messageOfReques.manger.DialogManagerMessageOfRequest
import com.tamin.taminhamrah.ui.home.services.showAndAddDependent.adapters.DependentAdapter
import com.tamin.taminhamrah.utils.UiUtils
import com.tamin.taminhamrah.utils.Utility
import com.tamin.taminhamrah.utils.extentions.createBundle
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DependentsFragment :
    BaseFragment<FragmentShowDependentsBinding, DependentsViewModel>() {

    //Class variables
    override val mViewModel: DependentsViewModel by viewModels()
    private val dependentAdapter by lazy { DependentAdapter() }

    //Base methods
    override fun getBindingVariable() = Pair(BR.viewModel, mViewModel)
    override fun getLayoutId() = R.layout.fragment_show_dependents

    override fun setupObserver() {
        mViewModel.mldDependentInfo.observe(this, ::onDependentInfoResponse)
        mViewModel.mldRefreshDependent.observe(this, ::onRefreshDependentResponse)
    }

    override fun initView() {
        setupToolbar(
            viewDataBinding?.appBar,
            viewDataBinding?.appbarBackgroundImage?.imageBackground,
            moreViews = null
        )

        viewDataBinding?.recycler?.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = dependentAdapter
            if (itemDecorationCount == 0) {
                addItemDecoration(UiUtils.VerticalItemMarginDecoration(40))
            }
        }
    }

    override fun getData() {
        mViewModel.getDependentInfo()
    }

    override fun onClick() {
        viewDataBinding?.btnAddNewPerson?.setOnClickListener {
            handlePageDestination(R.id.action_DependentsFragment_to_AddNewDependentFragment,
                Bundle().apply {
                    putString(Constants.TOOLBAR_TITLE, getString(R.string.add_new_dependent))
                    putInt(Constants.TOOLBAR_ICON_IMAGE, Utility.getToolbarIconDrawable(arguments))
                })
        }

        viewDataBinding?.btnRefresh?.setOnClickListener {
            DialogManagerMessageOfRequest.getInstanceOfDialog().apply {
                arguments = createBundle(
                    MessageType.CONFIRM,
                    this@DependentsFragment.getString(R.string.message_confirm_refresh_dependent),
                    true)
                setDialogClickListener(object : DialogClickInterface.onClickListener {
                    override fun onConfirmClick() {
                        mViewModel.refreshDependent()
                    }

                    override fun onCancelClick() {}
                })
            }.show(childFragmentManager, "ShowAndRegisterDependentsFragment")
        }
    }

    //Listeners
    private fun onDependentInfoResponse(result: DependentInfoResponse) {
        if (result.isSuccess) {
            val list = result.data?.list ?: emptyList()

            if(list.isEmpty()) {
                viewDataBinding?.tvMessage?.visibility = View.VISIBLE
                viewDataBinding?.tvMessage?.text = getString(R.string.not_exist_info)
            }else {
                viewDataBinding?.tvMessage?.visibility = View.GONE
            }
            dependentAdapter.setItems(list)

        }
    }

    private fun onRefreshDependentResponse(result: GeneralRes) {
        if (result.isSuccess)
            showAlertDialog(MessageType.SUCCESS,
                getString(R.string.message_success_refresh_dependent))
    }
}