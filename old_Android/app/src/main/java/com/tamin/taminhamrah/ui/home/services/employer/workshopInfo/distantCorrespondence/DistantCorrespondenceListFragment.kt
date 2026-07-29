package com.tamin.taminhamrah.ui.home.services.employer.workshopInfo.distantCorrespondence

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.remote.models.employer.LetterInfo
import com.tamin.taminhamrah.data.remote.models.employer.LetterListResponse
import com.tamin.taminhamrah.data.remote.models.services.GeneralRes
import com.tamin.taminhamrah.databinding.FragmentDistantCorrespondenceListBinding
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.appinterface.DialogClickInterface
import com.tamin.taminhamrah.ui.appinterface.MenuInterface
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.ui.dialog.MenuDialogFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.MessageOfRequestDialogFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.manger.DialogManagerMessageOfRequest
import com.tamin.taminhamrah.utils.Utility
import com.tamin.taminhamrah.utils.extentions.createBundle
import com.tamin.taminhamrah.utils.extentions.setupRecycler
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class DistantCorrespondenceListFragment :
    BaseFragment<FragmentDistantCorrespondenceListBinding, DistantCorrespondenceInfoViewModel>(),
    AdapterInterface.OnItemClickListener<LetterInfo> {

    companion object {
        const val ARG_WORKSHOP_ID = "ARG_WORKSHOP_ID"
        const val ARG_CONTRACT_ROW = "ARG_CONTRACT_ROW"
        const val ARG_BRANCH_CODE = "ARG_BRANCH_CODE"
    }

    private val listAdapter: DistantCorrespondenceAdapter by lazy {
        DistantCorrespondenceAdapter(this)
    }

    private val workshopId by lazy { arguments?.getString(ARG_WORKSHOP_ID) }
    private val contractRow by lazy { arguments?.getString(ARG_CONTRACT_ROW) }
    private val branchCode by lazy { arguments?.getString(ARG_BRANCH_CODE) }

    override val mViewModel: DistantCorrespondenceInfoViewModel by viewModels()

    override fun getBindingVariable(): Pair<Int, Any?> {
        return Pair(BR.viewModel, mViewModel)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_distant_correspondence_list
    }

    override fun setupObserver() {
        mViewModel.mldDeleteRequest.observe(
            this@DistantCorrespondenceListFragment,
            ::onDeleteRequest
        )
        mViewModel.mldApproveRequest.observe(
            this@DistantCorrespondenceListFragment,
            ::onApproveRequest
        )
    }

    private fun onDeleteRequest(result: GeneralRes?) {
        if (result?.isSuccess == true) {

            showAlertDialog(
                MessageOfRequestDialogFragment.MessageType.CONFIRM,
                getString(R.string.message_delete_item_successfully)
            )
            getData()
        }
    }

    private fun onApproveRequest(result: LetterListResponse?) {
        if (result?.isSuccess == true) {

            showAlertDialog(
                MessageOfRequestDialogFragment.MessageType.CONFIRM,
                getString(R.string.message_approve_letter_successfully)
            )
            getData()
        }
    }

    override fun initView() {

        setupRecycler(viewDataBinding?.recycler, listAdapter)

        setupToolbar(
            viewDataBinding?.appBar,
            viewDataBinding?.appbarBackgroundImage?.imageBackground/*,
            actionIconRes = R.drawable.ic_search*/
        )

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        getData()
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun getData() {
        this@DistantCorrespondenceListFragment.lifecycleScope.launchWhenCreated {

            mViewModel.getDistantCorrespondenceFlow(workshopId)
                .collectLatest { pagingData ->
                    listAdapter.submitData(pagingData)
                }
        }
    }

    override fun onClick() {

        viewDataBinding?.apply {
            btnAddNewRequest.setOnClickListener {
                navigateToNewRequestPage(viewOnly = false)

            }
        }
    }

    private fun navigateToNewRequestPage(item: LetterInfo?=null, viewOnly:Boolean=false) {
        val bundle = Bundle()

        bundle.putString(Constants.TOOLBAR_TITLE, getString(R.string.label_register_new_letter))
        bundle.putString(
            Constants.TOOLBAR_SUBTITLE,
            "${getString(R.string.workshop_number)} : ${workshopId ?: "-"}"
        )
        bundle.putString(
            Constants.TOOLBAR_SUB_SUBTITLE,
            "${getString(R.string.label_contract_row)} : ${contractRow ?: "-"}"
        )

        bundle.putInt(
            Constants.TOOLBAR_ICON_IMAGE,
            Utility.getToolbarIconDrawable(arguments)
        )

        bundle.putString(DistantCorrespondenceFragment.ARG_WORKSHOP_CODE, workshopId)
        bundle.putString(DistantCorrespondenceFragment.ARG_BRANCH_CODE, branchCode)
        bundle.putParcelable(DistantCorrespondenceFragment.ARG_LETTER_INFO, item)
        bundle.putBoolean(DistantCorrespondenceFragment.ARG_VIEW_ONLY, viewOnly)
        handlePageDestination(R.id.action_list_to_distant_correspondence, bundle)
    }

    override fun onItemClick(item: LetterInfo, transitionView: View?, tag: String?) {
        MenuDialogFragment.newInstance(true).apply {
            setMenuListener(object : MenuInterface.OnFetchData {
                override fun onFetch() {
                    this@DistantCorrespondenceListFragment.lifecycleScope.launchWhenCreated {
                        mViewModel.getMenuFlow(item.status)
                            .collectLatest { pagingData -> updateData(pagingData) }
                    }
                }
            }, object : MenuInterface.OnResult {
                override fun onResult(menu: MenuModel) {

                    when (menu.id) {
                        "1" -> {
                            navigateToNewRequestPage(item, true)
                        }
                        "2" -> {

                            val dialog = DialogManagerMessageOfRequest.getInstanceOfDialog()
                            dialog.arguments = createBundle(
                                MessageOfRequestDialogFragment.MessageType.WARNING,
                                getString(R.string.message_delete_alert),
                                true,
                                getString(R.string.label_delete)
                            )
                            dialog.setDialogClickListener(object :
                                DialogClickInterface.onClickListener {
                                override fun onConfirmClick() {
                                    mViewModel.deleteExistLetter(item.leterrequestId, workshopId)
                                }

                                override fun onCancelClick() {

                                }


                            })
                            dialog.show(
                                parentFragmentManager,
                                "Alert Dialog inbox fragment for deleted item"
                            )

                        }
                        "3" -> {
                            val dialog = DialogManagerMessageOfRequest.getInstanceOfDialog()
                                dialog.arguments = createBundle(
                                    MessageOfRequestDialogFragment.MessageType.WARNING,
                                    getString(R.string.message_confirm_Letter_status),
                                    true,
                                    getString(R.string.label_confirm)
                                )
                                dialog.setDialogClickListener(object :
                                    DialogClickInterface.onClickListener {
                                    override fun onConfirmClick() {
//                                        mViewModel.confirmLetterStatus(item.leterrequestId, workshopId)
                                    }

                                    override fun onCancelClick() {
                                        mViewModel.approveLetter(item.leterrequestId, workshopId)
                                    }
                                })
                                dialog.show(parentFragmentManager, "lksd;flskdf")
                        }
                    }
                }
            })
        }.show(childFragmentManager, "tyuytuytu")
    }
}