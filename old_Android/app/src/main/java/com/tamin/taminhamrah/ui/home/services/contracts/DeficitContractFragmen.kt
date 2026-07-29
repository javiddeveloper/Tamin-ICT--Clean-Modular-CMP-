package com.tamin.taminhamrah.ui.home.services.contracts

import androidx.fragment.app.viewModels
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.databinding.FragmentShowContractBinding
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.utils.stepperView.StepperLayout
import com.tamin.taminhamrah.utils.stepperView.VerticalStepperItemView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DeficitContractFragment :
    BaseFragment<FragmentShowContractBinding, ContractViewModel>(),
    StepperLayout.NextStepClickListener,
    StepperLayout.PreviousStepClickListener {
    override val mViewModel: ContractViewModel by viewModels()
    override fun getBindingVariable() = Pair(com.tamin.taminhamrah.BR.viewModel, mViewModel)
    override fun getLayoutId() = R.layout.fragment_show_contract
    override fun setupObserver() {}
    override fun initView() {}
    override fun getData() {}
    override fun onClick() {}
    override fun onNextStepClickListener(stepIndex: Int, step: VerticalStepperItemView) {}
    override fun onPreviousStepClickListener(stepIndex: Int, step: VerticalStepperItemView) {}


}
