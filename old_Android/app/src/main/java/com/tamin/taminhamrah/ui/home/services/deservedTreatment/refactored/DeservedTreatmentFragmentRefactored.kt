package com.tamin.taminhamrah.ui.home.services.deservedTreatment.refactored

import android.view.View
import androidx.fragment.app.viewModels
import com.tamin.taminhamrah.data.remote.models.services.ActiveRelation
import com.tamin.taminhamrah.databinding.FragmentDeservedTreatmentBinding
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.ui.home.services.deservedTreatment.DeservedTreatmentViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class DeservedTreatmentFragmentRefactored :
    BaseFragment<FragmentDeservedTreatmentBinding, DeservedTreatmentViewModel>(),
    AdapterInterface.OnItemClickListener<ActiveRelation> {

    //region Variables
    override val mViewModel: DeservedTreatmentViewModel by viewModels()
    //endregion

    //region Base Methods
    override fun getBindingVariable(): Pair<Int, Any?> {
        TODO("Not yet implemented")
    }

    override fun getLayoutId(): Int {
        TODO("Not yet implemented")
    }

    override fun setupObserver() {
        TODO("Not yet implemented")
    }

    override fun initView() {
        TODO("Not yet implemented")
    }

    override fun getData() {
        TODO("Not yet implemented")
    }

    override fun onClick() {
        TODO("Not yet implemented")
    }

    override fun onItemClick(item: ActiveRelation, transitionView: View?, tag: String?) {
        TODO("Not yet implemented")
    }
    //endregion

}