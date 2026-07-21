package com.tamin.taminhamrah.ui.home.services.disabilityPension.medicalCommission.registerdRequest

import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.services.disabilityPension.medicalCommission.RegisteredMedicalCommissionModel
import com.tamin.taminhamrah.databinding.FragmentRegisteredRequestBinding
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.ui.home.services.disabilityPension.DisabilityPensionViewModel
import com.tamin.taminhamrah.ui.home.services.disabilityPension.medicalCommission.registerdRequest.adapter.RegisteredRequestAdapter
import com.tamin.taminhamrah.utils.extentions.setupRecycler
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisteredRequestFragment :
    BaseFragment<FragmentRegisteredRequestBinding, DisabilityPensionViewModel>() {

    //Class Variables
    override val mViewModel: DisabilityPensionViewModel by viewModels()
    val adapter by lazy { RegisteredRequestAdapter() }

    val onItemClickListener by lazy {
        object : AdapterInterface.OnItemClickListener<RegisteredMedicalCommissionModel> {
            override fun onItemClick(
                item: RegisteredMedicalCommissionModel,
                transitionView: View?,
                tag: String?,
            ) {

            }
        }
    }

    //Base Methods
    override fun getBindingVariable() = Pair(BR.viewModel, mViewModel)
    override fun getLayoutId() = R.layout.fragment_registered_request
    override fun setupObserver() {
    }


    override fun initView() {
        setupRecycler(viewDataBinding?.workshopListRecycler, adapter = adapter)
        adapter.onItemClickListener = onItemClickListener
        setupToolbar(
            viewDataBinding?.appBar,
            viewDataBinding?.appbarBackgroundImage?.imageBackground
        )
    }

    override fun getData() {
        viewLifecycleOwner.lifecycleScope.launch {

            mViewModel.getRegisteredMedicalCommission().collectLatest {
                adapter.submitData(it)
            }
        }
    }

    override fun onClick() {
        viewDataBinding?.apply {

        }
    }



}