package com.tamin.taminhamrah.ui.home.services.employer.payment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.PaymentModel
import com.tamin.taminhamrah.databinding.DialogPaymentBinding
import com.tamin.taminhamrah.ui.appinterface.DialogResultInterface.OnResultListener
import com.tamin.taminhamrah.ui.appinterface.MenuInterface
import com.tamin.taminhamrah.ui.base.BaseBottomSheetDialogFragment
import com.tamin.taminhamrah.ui.base.BaseViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PaymentDialogFragment : BaseBottomSheetDialogFragment<DialogPaymentBinding, BaseViewModel>() {

    override val mViewModelDialog: BaseViewModel by viewModels()
    override fun getLayoutId()= R.layout.dialog_payment

    companion object {
        const val ARG_PAYMENT = "ARG_PAYMENT"
    }

    var mListener: OnResultListener<Map<String, String>>? = null

    fun setListener(listener: OnResultListener<Map<String, String>>) {
        mListener = listener
    }

    fun setupObserver() {
    }

    private var onListener: MenuInterface.OnResult? = null

    fun setListener(listener: MenuInterface.OnResult) {
        onListener = listener
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        onClick()

        setupObserver()
    }

    private fun initView() {
        viewBinding?.apply {
            item = getPaymentModel()
        }
    }

    private fun getPaymentModel(): PaymentModel? {
        return arguments?.getParcelable(ARG_PAYMENT) as? PaymentModel
    }

    private fun onClick() {

        viewBinding.apply {
        }
    }
}