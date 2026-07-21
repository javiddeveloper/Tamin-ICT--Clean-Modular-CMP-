package com.tamin.taminhamrah.ui.home.services.constructionInsurancePremium.beneficiaries

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.map
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.Constants.FILE_ID
import com.tamin.taminhamrah.Constants.REQUEST_DATE
import com.tamin.taminhamrah.Constants.REQUEST_ID
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.databinding.FragmentBeneficiariesWorkshopBinding
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.ui.home.services.KeyValueAdapterPaging
import com.tamin.taminhamrah.ui.home.services.constructionInsurancePremium.ConstructionInsurancePremiumViewModel
import com.tamin.taminhamrah.utils.extentions.setupRecycler
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class BeneficiariesWorkshopFragment :
    BaseFragment<FragmentBeneficiariesWorkshopBinding, ConstructionInsurancePremiumViewModel>() {

    //region Variables
    override val mViewModel: ConstructionInsurancePremiumViewModel by viewModels()
    val adapter by lazy { KeyValueAdapterPaging() }
    //endregion

    //region Base Methods
    override fun getBindingVariable() = Pair(BR.viewModel, mViewModel)
    override fun getLayoutId() = R.layout.fragment_beneficiaries_workshop
    override fun setupObserver() {}
    override fun initView() {
        viewDataBinding?.apply {
            setupRecycler(recycler, adapter)
        }
    }


    override fun getData() {
        arguments?.let { arg ->
            val requestId = arg.getLong(REQUEST_ID)
            val requestDate = arg.getString(REQUEST_DATE)
            val fileId = arg.getLong(FILE_ID)

            viewLifecycleOwner.lifecycleScope.launchWhenCreated {
                mViewModel.getBeneficiariesWorkshop(
                    requestNumber = requestId,
                    fileNumber = fileId,
                    requestDate = requestDate
                ).collectLatest { pagingData ->
                    val subItem = pagingData.map { item ->
                        item.getDetailInfo()
                    }
                    adapter.submitData(subItem)
                }
            }
        }

    }


    override fun onClick() {
        viewDataBinding?.imageBack?.setOnClickListener {
            backButtonPress()
        }
    }
    //endregion

    //region Listeners

    //endregion

    //region Utils

        //endregion
}