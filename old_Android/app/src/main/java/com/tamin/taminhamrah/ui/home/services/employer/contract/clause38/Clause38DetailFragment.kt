package com.tamin.taminhamrah.ui.home.services.employer.contract.clause38

import androidx.fragment.app.viewModels
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.services.contract.Clause38DetailResponse
import com.tamin.taminhamrah.data.remote.models.services.contract.ContractInfoNew
import com.tamin.taminhamrah.databinding.FragmentClause38DetailBinding
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.ui.home.services.KeyValueAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class Clause38DetailFragment :
    BaseFragment<FragmentClause38DetailBinding, Clause38ViewModel>() {

    companion object {
        const val ARG_CONTRACT_ITEM = "ARG_CONTRACT_ITEM"
        const val ARG_MAFASA_SERIAL_NO = "ARG_MAFASA_SERIAL_NO"
    }

    lateinit var listAdapter: KeyValueAdapter

    private val contractInfo by lazy { arguments?.getParcelable(ARG_CONTRACT_ITEM) as? ContractInfoNew }
    private val mafasaSerialNo by lazy { arguments?.getString(ARG_MAFASA_SERIAL_NO) }

    override val mViewModel: Clause38ViewModel by viewModels()

    override fun getBindingVariable(): Pair<Int, Any?> {
        return Pair(BR.viewModel, mViewModel)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_clause38_detail
    }

    override fun setupObserver() {
        mViewModel.mldClause38Detail.observe(this, ::onResult)
    }

    override fun initView() {

        listAdapter = KeyValueAdapter()
        viewDataBinding?.recycler?.getRecycler()?.adapter = listAdapter

        setupToolbar(
            viewDataBinding?.appBar,
            viewDataBinding?.appbarBackgroundImage?.imageBackground
        )
    }

    override fun getData() {
        fetchData()
    }

    private fun fetchData() {
        mViewModel.getClause38Detail(
            contractInfo?.workshop?.workshopId,
            contractInfo?.branch?.code,
            contractInfo?.contractRow,
            mafasaSerialNo
        )
    }

    override fun onClick() {

    }

    private fun onResult(result: Clause38DetailResponse?) {
        if (result?.isSuccess == true) {
            result.data?.list?.get(0)?.let {
                listAdapter.setItems(it.createKeyValue(it))
            }

        }

    }

}