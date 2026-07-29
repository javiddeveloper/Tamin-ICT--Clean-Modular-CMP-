package com.tamin.taminhamrah.ui.home.services.employer.legalStackHolders

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import androidx.fragment.app.viewModels
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.services.GeneralRes
import com.tamin.taminhamrah.databinding.DialogLegalStackholderOtpBinding
import com.tamin.taminhamrah.ui.appinterface.DialogClickInterface
import com.tamin.taminhamrah.ui.appinterface.DialogResultInterface
import com.tamin.taminhamrah.ui.base.BaseBottomSheetDialogFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.MessageOfRequestDialogFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.manger.DialogManagerMessageOfRequest
import com.tamin.taminhamrah.utils.extentions.createBundle
import com.tamin.taminhamrah.utils.extentions.gone
import com.tamin.taminhamrah.utils.extentions.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LegalStackHolderOtpDialog :
    BaseBottomSheetDialogFragment<DialogLegalStackholderOtpBinding, LegalStackHolderViewModel>() {

    companion object {

        const val ARG_NATIONAL_CODE = "ARG_NATIONAL_CODE"

        fun newInstance(nationalCode: String?): LegalStackHolderOtpDialog {

            val bundle = Bundle()
            bundle.putString(ARG_NATIONAL_CODE, nationalCode)
            val frg = LegalStackHolderOtpDialog()
            frg.arguments = bundle
            return frg
        }
    }

    override val mViewModelDialog: LegalStackHolderViewModel by viewModels()

    private var timer: CountDownTimer? = null
    private var fragmentIsInBackground = false
    private var timerIsFinished = false

    override fun getLayoutId(): Int {
        return R.layout.dialog_legal_stackholder_otp
    }

    private var onListener: DialogResultInterface.OnResultListener<String>? = null

    fun setListener(listener: DialogResultInterface.OnResultListener<String>? ) {
        onListener = listener
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        onClick()
        setupObserver()
    }

    private fun initView() {
        timer = object : CountDownTimer(5 * 60 * 1000, 1000) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                timerIsFinished = false
                var seconds = ((millisUntilFinished / 1000).toInt() % 60).toString()
                var minutes = (millisUntilFinished / (1000 * 60) % 60).toString()

                if (minutes.length == 1) minutes = "0$minutes"
                if (seconds.length == 1) seconds = "0$seconds"
                viewBinding?.tvCountDown?.text = "$minutes : $seconds"
            }

            override fun onFinish() {
                timerIsFinished = true
                if (!fragmentIsInBackground) showTimerFinishedAlert()
            }
        }
    }

    private fun setupObserver() {
        mViewModelDialog.mldTicketResult.observe(viewLifecycleOwner, ::onTicketResult)
        mViewModelDialog.mldVerifyTicketResult.observe(viewLifecycleOwner, ::showVerifyTicketResult)
    }


    private fun onTicketResult(result: GeneralRes?) {
        if (result?.isSuccess == true){
            viewBinding?.labelTitle?.visible()
            timer?.start()
        }


    }

    private fun showVerifyTicketResult(result: GeneralRes?) {
        if (result?.isSuccess == true) {
            viewBinding?.inputVerificationCode?.getValue(false)?.let { onListener?.onDialogResult(it ) }
            dismiss()
        }
    }


    @SuppressLint("SimpleDateFormat")
    private fun onClick() {

        viewBinding?.apply {
            btnSubmit.setOnClickListener {
                if (inputVerificationCode.getValue().isNotBlank()) {
                    viewBinding?.labelTitle?.gone()
                    mViewModelDialog.verifyTicket(
                        inputVerificationCode.getValue()
                    )
                }
            }

            btnAction.setOnClickListener {
                mViewModelDialog.requestTicket()
            }
        }
    }

    override fun onStop() {
        fragmentIsInBackground = true
        super.onStop()
    }

    override fun onStart() {
        super.onStart()

        fragmentIsInBackground = false
        if (timerIsFinished) showTimerFinishedAlert()
    }

    private fun showTimerFinishedAlert() {

        val dialog = DialogManagerMessageOfRequest.getInstanceOfDialog()
        dialog.arguments = createBundle(
            MessageOfRequestDialogFragment.MessageType.ERROR,
            getString(R.string.message_send_verification_code_finished)
        )
        dialog.setDialogClickListener(object :
            DialogClickInterface.onClickListener {
            override fun onConfirmClick() {

                dismiss()
            }

            override fun onCancelClick() {
            }
        }
        )
        dialog.show(childFragmentManager, "returnToPrevPage")

    }
}
