package com.tamin.taminhamrah.ui.home.services.employer.legalStackHolders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.employer.legalStackHolder.LegalStackHolder
import com.tamin.taminhamrah.data.remote.models.services.GeneralRes
import com.tamin.taminhamrah.databinding.FragmentLegalStackholderListBinding
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.appinterface.DialogClickInterface
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.MessageOfRequestDialogFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.manger.DialogManagerMessageOfRequest
import com.tamin.taminhamrah.utils.Utility
import com.tamin.taminhamrah.utils.extentions.createBundle
import com.tamin.taminhamrah.utils.extentions.setupRecycler
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class AddLegalStackHolderListFragment :
    BaseFragment<FragmentLegalStackholderListBinding, LegalStackHolderViewModel>(),
    AdapterInterface.OnItemClickListener<LegalStackHolder> {

    companion object {
        const val ARG_ITEM_STACK_HOLDER = "ARG_ITEM_STACK_HOLDER"
        const val ARG_TICKET = "ARG_TICKET"
    }

    private val listAdapter: LegalStackHolderAdapter by lazy {
          LegalStackHolderAdapter(this, showMoreInfo = true)
    }

    override val mViewModel: LegalStackHolderViewModel by viewModels()

    override fun getBindingVariable(): Pair<Int, Any?> {
        return Pair(BR.viewModel, mViewModel)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_legal_stackholder_list
    }

    override fun setupObserver() {
        mViewModel.mldDeleteUser.observe(viewLifecycleOwner, ::onDeleteItem)
    }

    private fun onDeleteItem(result: GeneralRes?) {
        if (result?.isSuccess==true)
            getData()
    }

    override fun initView() {

        viewDataBinding?.btnNewAgent?.visibility = View.VISIBLE

        setupRecycler(viewDataBinding?.recycler, listAdapter)

        setupToolbar(
            viewDataBinding?.appBar,
            viewDataBinding?.appbarBackgroundImage?.imageBackground
        )
    }

    override fun getData() {
        this@AddLegalStackHolderListFragment.lifecycleScope.launchWhenCreated {
            mViewModel.getLegalAgentList(
                selectedStackHolder?.workshopId,
                selectedStackHolder?.branchCode
            )
                .collectLatest { pagingData -> listAdapter.submitData(pagingData) }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        getData()
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onClick() {
        viewDataBinding?.btnNewAgent?.setOnClickListener {
            val item = arguments?.getParcelable(ARG_ITEM_STACK_HOLDER) as? LegalStackHolder
            showAddDialog(item, viewDataBinding?.btnNewAgent?.text.toString())
        }
    }

    private fun showAddDialog(item: LegalStackHolder?, title: String?) {
        val bundle = Bundle()
        bundle.putString(
            Constants.TOOLBAR_TITLE,title

        )
        bundle.putString(
            Constants.TOOLBAR_SUBTITLE, "${getString(R.string.label_workshop_name)} : ${item?.workshopName}"

        )
        bundle.putString(
            Constants.TOOLBAR_SUB_SUBTITLE, "${getString(R.string.label_workshop_code)} : ${item?.workshopId}")
        bundle.putString(
            Constants.TOOLBAR_ICON_IMAGE,
            Utility.getToolbarIconImage(arguments)
        )

        bundle.putParcelable(AddLegalStackHolderFragment.ARG_ITEM_STACK_HOLDER, item)
        bundle.putBoolean(AddLegalStackHolderFragment.ARG_IS_SPECIAL_ITEM, item?.special?:false)

        handlePageDestination(R.id.action_list_to_new_stack_holder, bundle)
    }

    override fun onItemClick(item: LegalStackHolder, transitionView: View?, tag: String?) {
        when (tag) {
            getString(R.string.label_edit_info) -> {
                showAddDialog(item, viewDataBinding?.btnNewAgent?.text.toString())
            }
            getString(R.string.label_delete) -> {
                val dialog = DialogManagerMessageOfRequest.getInstanceOfDialog()
                dialog.arguments = createBundle(
                    MessageOfRequestDialogFragment.MessageType.WARNING,
                    getString(R.string.label_are_you_sure_want_delete),
                    btnCancel = true,
                )

                dialog.setDialogClickListener(object : DialogClickInterface.onClickListener {
                    override fun onConfirmClick() {

                        mViewModel.deleteLegalAgent(ticket, item.stakeId)
                    }

                    override fun onCancelClick() {
                    }

                })
                dialog.show(childFragmentManager, "jlkjlklkjlk")
            }
        }
    }

    private val ticket by lazy {
        arguments?.getString(ARG_TICKET)
    }
    private val selectedStackHolder by lazy {
        arguments?.getParcelable(ARG_ITEM_STACK_HOLDER) as? LegalStackHolder
    }
}