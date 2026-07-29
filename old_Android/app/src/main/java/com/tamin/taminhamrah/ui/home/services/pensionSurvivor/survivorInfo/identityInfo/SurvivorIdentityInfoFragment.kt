package com.tamin.taminhamrah.ui.home.services.pensionSurvivor.survivorInfo.identityInfo

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.services.pensionSurvivor.SurvivorModel
import com.tamin.taminhamrah.databinding.FragmentSurvivorIdentityInfoBinding
import com.tamin.taminhamrah.ui.base.BaseBottomSheetDialogFragment
import com.tamin.taminhamrah.ui.base.BaseViewModel
import com.tamin.taminhamrah.ui.dialog.messageOfReques.MessageOfRequestDialogFragment
import com.tamin.taminhamrah.ui.home.services.KeyValueAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SurvivorIdentityInfoFragment :
    BaseBottomSheetDialogFragment<FragmentSurvivorIdentityInfoBinding, BaseViewModel>() {

    companion object {
        fun getInstance(arg: Bundle) = SurvivorIdentityInfoFragment().apply { arguments = arg }
    }

    //class variables
    override val mViewModelDialog: BaseViewModel by viewModels()
    val infoAdapter by lazy { KeyValueAdapter() }

    //base methods
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        getData()
    }
    override fun getLayoutId() = R.layout.fragment_survivor_identity_info

    //utils
    private fun initView() {
        viewBinding?.recycler?.apply {
            layoutManager = LinearLayoutManager(requireContext())
            if (itemDecorationCount == 0)
                addItemDecoration(com.tamin.taminhamrah.utils.UiUtils.createDivider(requireContext()))
            adapter = infoAdapter
        }
    }

   private fun getData() {
            val data = arguments?.getParcelable(Constants.IDENTITY_INFO) as? SurvivorModel
            if (data != null)
                infoAdapter.setItems(data.userInfo.getSurvivorIdentityInfo())
            else {
                if (dialog?.isShowing == true) {
                    showAlertDialog(MessageOfRequestDialogFragment.MessageType.ERROR,
                        getString(R.string.error_recive_data))
                    dismiss()
                }
            }
    }


}