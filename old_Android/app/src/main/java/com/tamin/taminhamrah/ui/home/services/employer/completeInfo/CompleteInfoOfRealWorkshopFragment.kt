package com.tamin.taminhamrah.ui.home.services.employer.completeInfo

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import androidx.fragment.app.viewModels
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.services.GeneralRes
import com.tamin.taminhamrah.data.remote.models.services.request_pregnancy_pay.CurrentUserResponse
import com.tamin.taminhamrah.databinding.FragmentCompleteRealWorkshopInfoBinding
import com.tamin.taminhamrah.ui.appinterface.DialogClickInterface
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.MessageOfRequestDialogFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.manger.DialogManagerMessageOfRequest
import com.tamin.taminhamrah.utils.extentions.createBundle
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CompleteInfoOfRealWorkshopFragment :
    BaseFragment<FragmentCompleteRealWorkshopInfoBinding, CompleteInfoViewModel>() {

    companion object {
        fun newInstance(bundleInfo: String): CompleteInfoOfRealWorkshopFragment {

            val bundle = Bundle()
            bundle.putString(Constants.TOOLBAR_ICON_IMAGE, bundleInfo)
            val frg = CompleteInfoOfRealWorkshopFragment()
            frg.arguments = bundle
            return frg
        }
    }

    private var timer: CountDownTimer? = null
    private var fragmentIsInBackground = false
    private var timerIsFinished = false

    override val mViewModel: CompleteInfoViewModel by viewModels()

    override fun getBindingVariable(): Pair<Int, Any?> {
        return Pair(BR.viewModel, mViewModel)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_complete_real_workshop_info
    }

    override fun setupObserver() {
        mViewModel.mldUserInfo.observe(viewLifecycleOwner, ::onUserInfo)
        mViewModel.mldTicketResult.observe(viewLifecycleOwner, ::showTicketResult)
        mViewModel.mldVerification.observe(viewLifecycleOwner, ::showVerificationResult)
    }

    private fun onUserInfo(result: CurrentUserResponse?) {
        if (result?.isSuccess == true) {

            if (!result.data?.email.isNullOrBlank()) {
                viewDataBinding?.inputEmail?.setTextWidget(result.data?.email!!)
                viewDataBinding?.inputEmail?.enableView(false)
            }
        }
    }


    private fun showVerificationResult(result: GeneralRes?) {
        if (result?.isSuccess == true) {
            showAlertDialog(
                MessageOfRequestDialogFragment.MessageType.INFO, result.data.toString()
            )
        }
    }

    private fun showTicketResult(result: GeneralRes?) {
        if (result?.isSuccess == true) {
            viewDataBinding?.apply {
                setupUiVisibility(true)
                timer?.start()
            }
        } else {
            viewDataBinding?.apply {
                setupUiVisibility(false)
            }
        }
    }

    private fun setupUiVisibility(showVerifyGroup: Boolean) {
        viewDataBinding?.apply {
            if (showVerifyGroup) {
                groupData.visibility = View.GONE
                groupVerifyTicket.visibility = View.VISIBLE
            } else {
                groupData.visibility = View.VISIBLE
                groupVerifyTicket.visibility = View.GONE
            }
        }
    }

    override fun initView() {

        viewDataBinding?.apply {

            timer = object : CountDownTimer(5 * 60 * 1000, 1000) {
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

            btnSendRequest.setOnClickListener {
                if (!locationSelector.getProvinceId().isNullOrBlank() &&
                    !locationSelector.getCityId().isNullOrBlank() &&
                    !locationSelector.getBranchId().isNullOrBlank() &&
                    mViewModel.mldUserInfo.value?.data != null
                ) {

                    var email = if (!mViewModel.mldUserInfo.value?.data?.mobile.isNullOrBlank()) {
                        mViewModel.mldUserInfo.value?.data?.mobile!!
                    } else {
                        inputEmail.getValue(false)
                    }
                    if (email.isBlank())
                        email = "tamin@tamin.ir"

                    mViewModel.sendCommitmentRequest(
                        mViewModel.mldUserInfo.value?.data?.mobile ?: "0",
                        email
                    )
                }
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
                    setupUiVisibility(false)
                }
            }

            override fun onCancelClick() {
            }
        }
        )
        dialog.show(childFragmentManager, "returnToPrevPage")
    }

    override fun getData() {
        mViewModel.getUserInfo()
    }

    @SuppressLint("SimpleDateFormat")
    override fun onClick() {

        viewDataBinding?.apply {
            btnSendRequest.setOnClickListener {
                if (inputWorkshopCode.getValue().isEmpty())
                    return@setOnClickListener

                if (inputWorkshopCode.getValue().length != 10) {
                    inputWorkshopCode.setError(getString(R.string.error_workshop_code_is_incorrect))
                    return@setOnClickListener
                }

                if (locationSelector.getProvinceId().isNullOrBlank() ||
                    locationSelector.getCityId().isNullOrBlank()||
                    locationSelector.getBranchId().isNullOrBlank())
                    return@setOnClickListener

                mViewModel.mldUserInfo.value?.data?.apply {
                    mViewModel.sendCommitmentRequest(
                        mobile ?: "",
                        email ?: "tamin@tamin.ir"
                    )
                }
            }

            btnSubmit.setOnClickListener {
                if (inputVerificationCode.getValue().isNotBlank()&&!locationSelector.getBranchId().isNullOrBlank())
                    mViewModel.postRealWorkshopVerificationCode(
                        locationSelector.getBranchId()!!,
                        inputWorkshopCode.getValue(),
                        inputVerificationCode.getValue()
                    )
            }

            btnPrevStep.setOnClickListener {
                groupVerifyTicket.visibility = View.GONE
                groupData.visibility = View.VISIBLE
                // setEmailVisibility(mViewModel.mldUserInfo.value?.data?.email)
            }
        }
    }

    /*private fun setEmailVisibility(email: String?) {
        Handler(Looper.getMainLooper()).postDelayed(
            {
                viewDataBinding?.inputEmail?.visibility =
                    if (email.isNullOrBlank()) View.VISIBLE
                    else View.GONE


            },
            500
        )
    }*/

    override fun onStop() {
        fragmentIsInBackground = true
        super.onStop()
    }

    override fun onStart() {
        super.onStart()

        fragmentIsInBackground = false
        if (timerIsFinished) showTimerFinishedAlert()
    }

    enum class DialogType {
        BRANCH_LIST, CITY_LIST, PROVINCES_LIST
    }

}
