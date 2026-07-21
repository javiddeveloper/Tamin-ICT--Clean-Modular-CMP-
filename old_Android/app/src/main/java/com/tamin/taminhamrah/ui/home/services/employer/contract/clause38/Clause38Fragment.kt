package com.tamin.taminhamrah.ui.home.services.employer.contract.clause38

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.services.contract.Clause38Info
import com.tamin.taminhamrah.data.remote.models.services.contract.ContractInfoNew
import com.tamin.taminhamrah.databinding.FragmentClause38Binding
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.appinterface.DialogResultInterface
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.utils.Utility
import com.tamin.taminhamrah.utils.extentions.setupRecycler
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber

@AndroidEntryPoint
class Clause38Fragment :
    BaseFragment<FragmentClause38Binding, Clause38ViewModel>(),
    AdapterInterface.OnItemClickListener<Clause38Info>,
    DialogResultInterface.OnResultListener<Map<String, String>> {

    companion object {
        const val ARG_CONTRACT_ITEM = "ARG_CONTRACT_ITEM"
    }

    lateinit var listAdapter: Clause38Adapter

    private val contractInfo by lazy { arguments?.getParcelable(ARG_CONTRACT_ITEM) as? ContractInfoNew }

    override val mViewModel: Clause38ViewModel by viewModels()

    override fun getBindingVariable(): Pair<Int, Any?> {
        return Pair(BR.viewModel, mViewModel)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_clause38
    }

    override fun setupObserver() {
        /*   mViewModel.mldWorkshopList.observe(this, {
               it.peekContent()?.let { it1 ->
                   showResult(it1)
               }
           })
   */
    }

    override fun initView() {
        Timber.tag("FragmentInitTest").i("CALLED Init view")
        listAdapter = Clause38Adapter(this)
        setupRecycler(viewDataBinding?.recycler, listAdapter)

        setupToolbar(
            viewDataBinding?.appBar,
            viewDataBinding?.appbarBackgroundImage?.imageBackground
        )
    }

    override fun getData() {
        fetchData()
    }

    private fun fetchData() {
        this@Clause38Fragment.lifecycleScope.launchWhenCreated {
            mViewModel.getClause38List(
                contractInfo?.workshop?.workshopId,
                contractInfo?.branch?.code,
                contractInfo?.contractRow,
                contractInfo?.status,
                contractInfo?.contractNumber
            )
                ?.collectLatest { pagingData ->
                    listAdapter.submitData(pagingData)
                }
        }
    }

    override fun onClick() {

    }

    override fun onItemClick(item: Clause38Info, transitionView: View?, tag: String?) {
        val bundle = Bundle()
        bundle.putString(Constants.TOOLBAR_TITLE, tag)
        bundle.putString(Constants.TOOLBAR_ICON_IMAGE, Utility.getToolbarIconImage(arguments))
        bundle.putParcelable(Clause38DetailFragment.ARG_CONTRACT_ITEM, contractInfo)
        bundle.putString(Clause38DetailFragment.ARG_MAFASA_SERIAL_NO, item.clearanceSerial)

        handlePageDestination(R.id.action_clause38_to_Detail, bundle)

    }

    override fun onDialogResult(item: Map<String, String>) {

    }
}