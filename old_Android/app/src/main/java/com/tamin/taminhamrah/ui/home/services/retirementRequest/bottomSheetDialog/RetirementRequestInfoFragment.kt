package com.tamin.taminhamrah.ui.home.services.retirementRequest.bottomSheetDialog

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.services.retirementPension.RetirementRequestInfoModel
import com.tamin.taminhamrah.databinding.FragmentRetirementRequestInfoBinding
import com.tamin.taminhamrah.ui.base.BaseBottomSheetDialogFragment
import com.tamin.taminhamrah.ui.base.BaseViewModel
import com.tamin.taminhamrah.ui.dialog.messageOfReques.MessageOfRequestDialogFragment
import com.tamin.taminhamrah.ui.home.services.KeyValueAdapter
import com.tamin.taminhamrah.ui.home.services.retirementRequest.RetirementPensionViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RetirementRequestInfoFragment :
    BaseBottomSheetDialogFragment<FragmentRetirementRequestInfoBinding, BaseViewModel>() {

    companion object {
        fun getInstance(arg: Bundle) = RetirementRequestInfoFragment()
            .apply { arguments = arg }
    }

    //class variables
    override val mViewModelDialog: RetirementPensionViewModel by viewModels()
    val infoAdapter by lazy { KeyValueAdapter() }

    //base methods
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        getData()
    }
    override fun getLayoutId() = R.layout.fragment_retirement_request_info

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
       val data = arguments?.getParcelable(Constants.RETIREMENT_REQUEST_INFO) as? RetirementRequestInfoModel
       if (data==null) {
                if (dialog?.isShowing == true) {
                    showAlertDialog(MessageOfRequestDialogFragment.MessageType.ERROR,
                        getString(R.string.error_recive_data))
                    dismiss()
                }
            } /* else
                infoAdapter.setItems(data.getRequestInf())*/
    }


}