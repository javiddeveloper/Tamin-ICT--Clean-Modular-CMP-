package com.tamin.taminhamrah.ui.home.services.employer.registration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.employer.ConfirmUserResponse
import com.tamin.taminhamrah.data.remote.models.services.GeneralRes
import com.tamin.taminhamrah.data.remote.models.services.workshop.WorkshopNewMember
import com.tamin.taminhamrah.databinding.FragmentWorkshopRecentlyAddedMemberListBinding
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.appinterface.DialogClickInterface
import com.tamin.taminhamrah.ui.appinterface.DialogResultInterface
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.MessageOfRequestDialogFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.manger.DialogManagerMessageOfRequest
import com.tamin.taminhamrah.utils.Utility
import com.tamin.taminhamrah.utils.extentions.createBundle
import com.tamin.taminhamrah.utils.extentions.setupRecycler
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class WRecentlyAddedMemberListFragment :
    BaseFragment<FragmentWorkshopRecentlyAddedMemberListBinding, InsuredRegistrationViewModel>(),
    DialogResultInterface.OnResultListener<Map<String, String>>,
    AdapterInterface.OnItemClickListener<WorkshopNewMember> {

     private val listAdapter: WRecentlyAddedMemberAdapter by lazy{
        WRecentlyAddedMemberAdapter(this)
    }

    override val mViewModel: InsuredRegistrationViewModel by viewModels()

    override fun getBindingVariable(): Pair<Int, Any?> {
        return Pair(BR.viewModel, mViewModel)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_workshop_recently_added_member_list
    }

    override fun setupObserver() {
        mViewModel.mldDeleteUser.observe(this@WRecentlyAddedMemberListFragment, ::onDeleteUser)
        mViewModel.mldConfirmedUser.observe(this@WRecentlyAddedMemberListFragment, ::onConfirmUser)
    }

    private fun onConfirmUser(result: ConfirmUserResponse?) {
        if (result?.isSuccess==true){

            showAlertDialog(
                MessageOfRequestDialogFragment.MessageType.ERROR,
                getString(R.string.message_confirm_user_of_workshop, result.data?.refCode)
            )

            getData()
        }
    }

    private fun onDeleteUser(result: GeneralRes?) {
        if (result?.isSuccess == true) {
            collectData()
        }
    }

    private fun getWorkshopId(): String? {
        return arguments?.getString(mViewModel.ARG_WORKSHOP_ID)
    }

    private fun getOrganizationId(): String? {
        return arguments?.getString(mViewModel.ARG_BRANCH_CODE)
    }

    override fun initView() {

        setupRecycler(viewDataBinding?.recycler, listAdapter)

        setupToolbar(
            viewDataBinding?.appBar,
            viewDataBinding?.appbarBackgroundImage?.imageBackground,
            actionIconRes = R.drawable.ic_search
        )

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        collectData()
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun getData() {
        //collectData()
    }

    private fun collectData(nationalCode: String? = "", statuesType: String? = "") {
        this@WRecentlyAddedMemberListFragment.lifecycleScope.launchWhenCreated {
            getWorkshopId()?.let { workshopId ->
                getOrganizationId()?.let { organizationId ->
                    mViewModel.getWorkshopRecentlyAddedMembers(
                        workshopId,
                        organizationId,
                        nationalCode,
                        statuesType
                    )
                        .collectLatest { pagingData ->
                            listAdapter.submitData(pagingData)
                        }
                }
            }
        }
    }

    override fun onClick() {

        viewDataBinding?.let {
            it.appBar.toolbar.apply {
                imgAction.setOnClickListener {

                    val dialog = WRecentlyAddedMemberSDialogFragment()
                    dialog.setListener(this@WRecentlyAddedMemberListFragment)
                    dialog.show(childFragmentManager, "jfhskljkjllkljl")
                }
            }
            it.btnAddNewMember.setOnClickListener {

                handlePageDestination(
                    R.id.action_recently_added_to_new_member,
                    createNavBundle()
                )
            }
        }
    }

    private fun createNavBundle(item: WorkshopNewMember? = null): Bundle {

        val bundle = Bundle()
        bundle.putString(
            Constants.TOOLBAR_TITLE, getString(R.string.label_add_new_member)
        )
        bundle.putString(Constants.TOOLBAR_SUBTITLE, Utility.getToolbarSubTitle(arguments))
        bundle.putString(Constants.TOOLBAR_SUB_SUBTITLE, Utility.getToolbarSub2(arguments))
        bundle.putString(Constants.TOOLBAR_ICON_IMAGE, Utility.getToolbarIconImage(arguments))
        bundle.putString(InsuredRegistrationFragment.ARG_WORKSHOP_ID, getWorkshopId())
        bundle.putString(InsuredRegistrationFragment.ARG_ORGANIZATION_ID, getOrganizationId())
        bundle.putLong(
            InsuredRegistrationFragment.ARG_PERSONAL_REQUEST_ID,
            item?.personal?.request?.id ?: 0
        )
        return bundle
    }

    override fun onDialogResult(item: Map<String, String>) {
        val nationalCode = item[mViewModel.ARG_NATIONAL_CODE] ?: ""
        val statuesType = item[mViewModel.ARG_REQUEST_STATUS] ?: ""

        collectData(nationalCode, statuesType)
    }

    override fun onItemClick(item: WorkshopNewMember, transitionView: View?, tag: String?) {
        when (tag) {
            getString(R.string.label_ok) -> {

                if (item.personal?.request?.status !== null && item.personal?.request?.status?.requestCode !== null) {
                    showAlertDialog(
                        MessageOfRequestDialogFragment.MessageType.ERROR,
                        getString(R.string.message_error_can_not_edit_request)
                    )
                } else {
                    val dialog = DialogManagerMessageOfRequest.getInstanceOfDialog()
                    dialog.arguments = createBundle(
                        MessageOfRequestDialogFragment.MessageType.WARNING,
                        getString(R.string.label_are_you_sure_to_confirm_this_user), btnCancel = true
                    )
                    dialog.setDialogClickListener(object :
                        DialogClickInterface.onClickListener {
                        override fun onConfirmClick() {
                            mViewModel.confirmRecentlyAddedUser(item.personal?.request?.id)
                        }

                        override fun onCancelClick() {
                            dialog.dismiss()
                        }
                    }
                    )
                    dialog.show(childFragmentManager, "confirmThisUser")
                }
  /*              if (param.item.personal.request.status !== null && param.item.personal.request.status.requestCode !== null) {
                    this.showErrorMessageBox('خطا', 'اطلاعات این درخواست قابل حذف یا اصلاح نمی باشد.');
                    return;
                }
                this.showQuestionBox('پیام سیستم', 'آیا مطمئن هستید؟', () => {
                    this._overlay = this.showOverlay();
                    this.restService.update(Urls.RegRequestPut, param.item.personal.request.id.toString(), {})
                        .then(result => {
                            this.hideOverlay(this._overlay);
                            const massage = 'درخواست شما با کد  ' + param.item.personal.request.refCode + ' در صف بررسی مرکز قرار گرفته است.';
                            this.showInfoMessageBox('پیام سیستم', massage, () => {
                                *//*const model ={workshopId:this.workshopId,
                                organizationId:this.organizationId};*//*
                                this.loadData(null);
                            });
                        })
                    .catch(error => {
                    this.hideOverlay(this._overlay);
                    console.log(error);
                    this.showErrorMessageBox('خطا', error.error.data.message);
                });
                }, () => {
                });

                break;*/

            }
            getString(R.string.label_show_and_edit) -> {
                if (item.personal?.request?.status !== null && item.personal?.request?.status?.requestCode !== null) {
                    showAlertDialog(
                        MessageOfRequestDialogFragment.MessageType.ERROR,
                        getString(R.string.message_error_can_not_edit_request)
                    )
                } else
                    handlePageDestination(
                        R.id.action_recently_added_to_new_member,
                        createNavBundle(item)
                    )
            }
            getString(R.string.label_delete) -> {

                if (item.personal?.request?.status !== null && item.personal?.request?.status?.requestCode !== null) {
                    showAlertDialog(
                        MessageOfRequestDialogFragment.MessageType.ERROR,
                        getString(R.string.message_error_can_not_edit_request)
                    )
                } else {
                    val dialog = DialogManagerMessageOfRequest.getInstanceOfDialog()
                    dialog.arguments = createBundle(
                        MessageOfRequestDialogFragment.MessageType.WARNING,
                        getString(R.string.label_are_you_sure_to_delete_this_user), btnCancel = true
                    )
                    dialog.setDialogClickListener(object :
                        DialogClickInterface.onClickListener {
                        override fun onConfirmClick() {
                            mViewModel.deleteRecentlyAddedUser(item.personal?.id)
                        }

                        override fun onCancelClick() {
                            dialog.dismiss()
                        }
                    }
                    )
                    dialog.show(childFragmentManager, "ExitFromThisPage")
                }
            }
            getString(R.string.label_follow_up) -> {
                handlePageDestination(R.id.action_new_member_to_cartable)
            }
        }
    }


}