package com.tamin.taminhamrah.ui.profile

import android.annotation.SuppressLint
import android.os.CountDownTimer
import android.view.View
import androidx.fragment.app.viewModels
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.Resource
import com.tamin.taminhamrah.data.remote.models.services.GeneralRes
import com.tamin.taminhamrah.data.remote.models.user.EditMobileResponse
import com.tamin.taminhamrah.databinding.FragmentEditProfileBinding
import com.tamin.taminhamrah.ui.MainActivity
import com.tamin.taminhamrah.ui.appinterface.DialogClickInterface
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.MessageOfRequestDialogFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.manger.DialogManagerMessageOfRequest
import com.tamin.taminhamrah.utils.extentions.createBundle
import com.tamin.taminhamrah.utils.extentions.gone
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditProfileFragment :
    BaseFragment<FragmentEditProfileBinding, ProfileViewModel>() {

    private val currentMobile by lazy { mViewModel.getPhoneNumber() }
    override val mViewModel: ProfileViewModel by viewModels()

    private var timer: CountDownTimer? = null
    private var fragmentIsInBackground = false
    private var timerIsFinished = false

    override fun getBindingVariable(): Pair<Int, Any?> {
        return Pair(BR.viewModel, mViewModel)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_edit_profile
    }

    override fun setupObserver() {
//        mViewModel.mldEditProfileResult.observe(this, ::showResult)
//        mViewModel.mldVerifyMobileCodeeResult.observe(this, ::showVerificationResult)
        mViewModel.mldEditMobileRes.observe(this, ::onChangeMobileResult)
        mViewModel.mldVerifyCodeRes.observe(this, ::onVerifyCodeResult)
    }

    override fun initView() {
        viewDataBinding?.apply {
            if (!currentMobile.isNullOrEmpty()) {
                inputMobileCurrent.enableView(false)
                inputMobileCurrent.setTextWidget(currentMobile!!)
            }else inputMobile.gone()

            timer = object : CountDownTimer(1 * 60 * 1000, 1000) {
                @SuppressLint("SetTextI18n")
                override fun onTick(millisUntilFinished: Long) {
                    timerIsFinished = false
                    var seconds = ((millisUntilFinished / 1000).toInt() % 60).toString()
                    var minutes = (millisUntilFinished / (1000 * 60) % 60).toString()

                    if (minutes.length == 1) minutes = "0$minutes"
                    if (seconds.length == 1) seconds = "0$seconds"
                    tvCountDown.text = "$minutes : $seconds"
                }

                override fun onFinish() {

                    timerIsFinished = true
                    if (!fragmentIsInBackground) showTimerFinishedAlert()
                }
            }
        }
        setupToolbar(
            viewDataBinding?.appBar,
            viewDataBinding?.appbarBackgroundImage?.imageBackground
        )
    }

    override fun getData() {

    }

    override fun onClick() {
        viewDataBinding?.apply {

            btnSubmit.setOnClickListener {
                /* if ((inputMobile.getValue().length > 11) || (inputMobile.getValue().take(2) != "09")){
                     showAlertDialog(MessageOfRequestDialogFragment.MessageType.WARNING ,
                         getString(R.string.error_input_number_is_not_valid))
                 } else*/ if (inputMobile.getValue() == currentMobile) {
                showAlertDialog(
                    MessageOfRequestDialogFragment.MessageType.WARNING,
                    getString(R.string.error_input_number_the_same_of_current_number)
                )
            } else {
                mViewModel.changeMobile(inputMobile.getValue())
            }
            }

            btnVerifyCode.setOnClickListener {
                if (inputVerifyCode.getValue().isNotBlank() && inputVerifyCode.getValue()
                        .isNotBlank()
                ) {
                    mViewModel.verifyChangeMobileCode(
                        inputMobile.getValue(),
                        inputVerifyCode.getValue()
                    )
                }
            }

            btnReturn.setOnClickListener {
                setupGroupVisibility(true)
            }
        }

    }

    private fun showResult(result: Resource<Any?>) {
        (requireActivity() as? MainActivity)?.handleResponse(result)
        if (result.status == Resource.Status.SUCCESS) {
            setupGroupVisibility(false)
            /* showAlertDialog(
                 MessageOfRequestDialogFragment.MessageType.INFO,
                 result.message?.message ?: getString(R.string.message_success_change_phone_number)
             )*/
            showAlertDialog(
                MessageOfRequestDialogFragment.MessageType.INFO,
                result.message?.message ?: getString(R.string.desc_send_verification_sms)
            )
        }
    }

    private fun onChangeMobileResult(result: EditMobileResponse) {
        if (result.isSuccess) {
            setupGroupVisibility(false)
            timer?.start()
            showAlertDialog(
                MessageOfRequestDialogFragment.MessageType.INFO,
                /*result.message?.message ?:*/ getString(R.string.desc_send_verification_sms)
            )
        }
    }

    private fun onVerifyCodeResult(result: GeneralRes) {
        if (result.isSuccess) {
            showAlertDialog(
                MessageOfRequestDialogFragment.MessageType.INFO,
                /*result.message?.message ?:*/ getString(R.string.message_success_verify_new_mobil),
                MessageOfRequestDialogFragment.DismissType.BACK_TO_PREVIOUS
            )
        }
    }

    private fun setupGroupVisibility(mobileGroupVisible: Boolean) {
        viewDataBinding?.apply {
            if (!mobileGroupVisible) {
                groupMobile.visibility = View.GONE
                groupVerifyCode.visibility = View.VISIBLE
                inputVerifyCode.setTextWidget("")
            } else {
                groupMobile.visibility = View.VISIBLE
                inputMobile.setTextWidget("")
                groupVerifyCode.visibility = View.GONE
                timer?.cancel()
            }
        }

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

                viewDataBinding?.apply {
                    setupGroupVisibility(true)
                }
            }

            override fun onCancelClick() {
            }
        }
        )
        dialog.show(childFragmentManager, "returnToPrevPage")
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

}