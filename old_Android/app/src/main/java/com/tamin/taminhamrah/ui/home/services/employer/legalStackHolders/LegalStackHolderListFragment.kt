package com.tamin.taminhamrah.ui.home.services.employer.legalStackHolders

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.employer.legalStackHolder.LegalStackHolder
import com.tamin.taminhamrah.databinding.FragmentLegalStackholderListBinding
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.appinterface.DialogResultInterface
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.utils.Utility
import com.tamin.taminhamrah.utils.extentions.setupRecycler
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class LegalStackHolderListFragment :
    BaseFragment<FragmentLegalStackholderListBinding, LegalStackHolderViewModel>(),
    AdapterInterface.OnItemClickListener<LegalStackHolder> {

    lateinit var listAdapter: LegalStackHolderAdapter

    override val mViewModel: LegalStackHolderViewModel by viewModels()

    override fun getBindingVariable(): Pair<Int, Any?> {
        return Pair(BR.viewModel, mViewModel)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_legal_stackholder_list
    }

    override fun setupObserver() {

    }

    override fun initView() {

        listAdapter = LegalStackHolderAdapter(this)
        setupRecycler(viewDataBinding?.recycler, listAdapter)

        setupToolbar(
            viewDataBinding?.appBar,
            viewDataBinding?.appbarBackgroundImage?.imageBackground
        )
    }

    override fun getData() {
        collectData()
    }

    private fun collectData(workshopCode: String? = null, branchCode: String? = null) {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {

            mViewModel.getLegalStackHolderList(workshopCode, branchCode)
                .collectLatest { pagingData -> listAdapter.submitData(pagingData) }
        }
    }

    override fun onClick() {

    }

    override fun onItemClick(item: LegalStackHolder, transitionView: View?, tag: String?) {

        val dialog = LegalStackHolderOtpDialog.newInstance(item.nationalId)
        dialog.setListener(object : DialogResultInterface.OnResultListener<String> {
            override fun onDialogResult(ticket: String) {
                val bundle = Bundle()
                bundle.putString(
                    Constants.TOOLBAR_TITLE,
                    "${getString(R.string.label_workshop_name)} : ${item.workshopName}"
                )
                bundle.putString(
                    Constants.TOOLBAR_SUBTITLE,
                    "${getString(R.string.label_workshop_code)} : ${item.workshopId}"
                )
                bundle.putString(
                    Constants.TOOLBAR_SUB_SUBTITLE,
                    "${getString(R.string.label_branch_code)} : ${item.branchCode} "
                )
                bundle.putString(
                    Constants.TOOLBAR_ICON_IMAGE,
                    Utility.getToolbarIconImage(arguments)
                )

                bundle.putParcelable(
                    AddLegalStackHolderListFragment.ARG_ITEM_STACK_HOLDER,
                    item
                )
                bundle.putString(
                    AddLegalStackHolderListFragment.ARG_TICKET,
                    ticket
                )

                handlePageDestination(R.id.action_stack_holder_to_new_item, bundle)
            }
        })
        dialog.show(childFragmentManager, AddLegalStackHolderFragment::javaClass.name)
    }
}