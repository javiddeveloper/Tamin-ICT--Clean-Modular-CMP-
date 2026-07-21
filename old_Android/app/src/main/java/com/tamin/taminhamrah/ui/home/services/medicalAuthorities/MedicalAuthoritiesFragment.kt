package com.tamin.taminhamrah.ui.home.services.medicalAuthorities

import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.map
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.local.models.ToolBarStepperModel
import com.tamin.taminhamrah.data.remote.models.services.medicalAuthorities.MedicalAuthoritiesModel
import com.tamin.taminhamrah.databinding.FragmentMedicalAuthoritiesBinding
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.ui.home.services.medicalAuthorities.adapter.MedicalAuthoritiesAdapter
import com.tamin.taminhamrah.utils.extentions.setupRecycler
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class MedicalAuthoritiesFragment :
    BaseFragment<FragmentMedicalAuthoritiesBinding, MedicalAuthoritiesViewModel>() {
    override val mViewModel: MedicalAuthoritiesViewModel by viewModels()
    private val listAdapter by lazy { MedicalAuthoritiesAdapter() }
    override fun getBindingVariable() = Pair(BR.viewModel, mViewModel)
    override fun getLayoutId() = R.layout.fragment_medical_authorities
    var fullName:String?=null
    var nationalId:String?=null
    var insurance:String?=null
    override fun setupObserver() {}
    override fun initView() {
        viewDataBinding?.apply {
            setupRecycler(recycler, listAdapter)
            appBar.toolbar.imgInfo.setOnClickListener {
                mViewModel.saveBoolean(Constants.TapTargetHistoryFragment, false)
            }
            setupCustomToolbar(
                appBar,
                appbarBackgroundImage.imageBackground,
                moreViews = null,
            )
            appBar.tvSubTitleBirthDate.text = getString(R.string.insurance_num)
        }
    }
    override fun getData() {
        this@MedicalAuthoritiesFragment.lifecycleScope.launchWhenCreated {
            mViewModel.mldConfirmationMedicalAuthorities.collectLatest { pagingDate ->
                val list = ArrayList<MedicalAuthoritiesModel>()
                val result = pagingDate.map {
                    list.add(it)
                    if (fullName.isNullOrEmpty() && nationalId.isNullOrEmpty() && insurance.isNullOrEmpty()) {
                        fullName = "${it.firstName} ${it.lastName}"
                        nationalId = it.nationalCode
                        insurance = it.insuranceNumber
                        viewDataBinding?.appBar?.userInfo = ToolBarStepperModel(
                                userName = fullName ?: "-",
                                nationalID = nationalId ?: "-",
                                accountNum = insurance ?: "-"
                            )
                        }

                    it
                }
                if (list.isEmpty()){
                    viewDataBinding?.appBar?.apply {
                        tvSubTitleBirthDate.isVisible = false
                        tvSubTitleNationalId.isVisible = false
                    }
                }
                listAdapter.submitData(result)
            }
        }
    }
    override fun onClick() {}
}