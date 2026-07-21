package com.tamin.taminhamrah.ui.home.services.employer.completeInfo

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.employer.employerAgreement.EmployerAgreement
import com.tamin.taminhamrah.databinding.FragmentLegalWorkshopListBinding
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.utils.Utility
import com.tamin.taminhamrah.utils.extentions.setupRecycler
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class LegalWorkshopListFragment :
    BaseFragment<FragmentLegalWorkshopListBinding, CompleteInfoViewModel>(),
    AdapterInterface.OnItemClickListener<EmployerAgreement> {

    companion object {
        fun newInstance(bundleInfo: String):LegalWorkshopListFragment{

            val bundle = Bundle()
            bundle.putString(Constants.TOOLBAR_ICON_IMAGE , bundleInfo)
            val frg = LegalWorkshopListFragment()
            frg.arguments=bundle
            return frg
        }
    }

    lateinit var listAdapter: LegalWorkshopAdapter

    override val mViewModel: CompleteInfoViewModel by viewModels()

    override fun getBindingVariable(): Pair<Int, Any?> {
        return Pair(BR.viewModel, mViewModel)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_legal_workshop_list
    }

    override fun setupObserver() {
    }

    override fun initView() {

        listAdapter = LegalWorkshopAdapter(this)
        setupRecycler(viewDataBinding?.recycler, listAdapter)
    }

    override fun getData() {
        collectData()

    }

    private fun collectData(workshopCode: String? = null, branchCode: String? = null) {
        this@LegalWorkshopListFragment.lifecycleScope.launchWhenCreated {

            mViewModel.getEmployerAgreementInfoList(workshopCode, branchCode)
                .collectLatest { pagingData ->
                    //   val result = pagingData.filter { it.workshop?.character?.characterCode == "2" }
                    listAdapter.submitData(pagingData)
                }
        }
    }

    override fun onClick() {
    }

    override fun onItemClick(item: EmployerAgreement, transitionView: View?, tag: String?) {

        view?.windowToken?.let { Utility.hideKeyboard(requireContext(), it) }

        /* childFragmentManager.beginTransaction().replace(
             R.id.fragment_tab,
             CompleteInfoOfLegalWorkshopFragment.newInstance(
                 item.workshop?.workshopId,
                 item.workshop?.branchCode
             )
         ).commit()*/


        val bundle = Bundle()

        bundle.putString(Constants.TOOLBAR_TITLE, getString(R.string.label_complete_legal_workshop_info))
        bundle.putString(Constants.TOOLBAR_SUBTITLE, "${item.workshop?.workshopName} (${getString(R.string.label_workshop_code)}:${item.workshop?.workshopId})")
        bundle.putString(Constants.TOOLBAR_ICON_IMAGE, Utility.getToolbarIconImage(arguments) )
        bundle.putString(CompleteInfoOfLegalWorkshopFragment.ARG_WORKSHOP_ID, item.workshop?.workshopId)
        bundle.putString(CompleteInfoOfLegalWorkshopFragment.ARG_BRANCH_CODE,  item.workshop?.branchCode)

        handlePageDestination(R.id.action_tab_to_legal_workshop, bundle)


       /* CompleteInfoOfLegalWorkshopDialogFragment.newInstance(
            item.workshop?.workshopId,
            item.workshop?.branchCode
        ).show(
            childFragmentManager,
            CompleteInfoOfLegalWorkshopDialogFragment::javaClass.name
        )*/




    }
}