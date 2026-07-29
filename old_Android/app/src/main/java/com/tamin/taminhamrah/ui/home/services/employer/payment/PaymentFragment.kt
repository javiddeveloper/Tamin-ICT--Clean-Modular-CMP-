package com.tamin.taminhamrah.ui.home.services.employer.payment

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.annotation.Nullable
import androidx.browser.customtabs.CustomTabsCallback
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsIntent
import androidx.browser.customtabs.CustomTabsService
import androidx.browser.customtabs.CustomTabsServiceConnection
import androidx.core.view.isNotEmpty
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import com.google.android.material.appbar.AppBarLayout
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.remote.models.services.payment.PaymentInfo
import com.tamin.taminhamrah.data.remote.models.services.payment.PaymentLinkResponse
import com.tamin.taminhamrah.data.remote.models.services.payment.PaymentUrlRequest
import com.tamin.taminhamrah.databinding.FragmentPaymentBinding
import com.tamin.taminhamrah.ui.appinterface.DialogClickInterface
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.ui.base.CustomTabsLauncher
import com.tamin.taminhamrah.ui.dialog.messageOfReques.MessageOfRequestDialogFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.manger.DialogManagerMessageOfRequest
import com.tamin.taminhamrah.ui.home.services.employer.payment.viewmodel.PaymentViewModel
import com.tamin.taminhamrah.utils.Utility
import com.tamin.taminhamrah.utils.Utility.standardWebFlags
import com.tamin.taminhamrah.utils.ValidationUtil
import com.tamin.taminhamrah.utils.extentions.createBundle
import com.tamin.taminhamrah.utils.extentions.disableButton
import com.tamin.taminhamrah.utils.extentions.enableButton
import com.tamin.taminhamrah.utils.extentions.gone
import dagger.hilt.android.AndroidEntryPoint
import jakarta.inject.Inject
import timber.log.Timber
import kotlin.math.abs

@AndroidEntryPoint
class PaymentFragment :
    BaseFragment<FragmentPaymentBinding, PaymentViewModel>() {
    private var timer: CountDownTimer? = null

    @Inject
    lateinit var webLauncher: CustomTabsLauncher
    private var userType = PaymentUserType.CURRENT_USER.typeCode
    private var enteredNationalId = ""
    private var fragmentIsInBackground = false
    private var timerIsFinished = false
    private var paymentInfo:PaymentInfo?=null

    companion object {
        const val ARG_PAYMENT_INFO = "ARG_PAYMENT_INFO"
        const val SYSTEM_TYPE = "SYSTEM_TYPE"
    }

    override val mViewModel: PaymentViewModel by viewModels()

    override fun getBindingVariable(): Pair<Int, Any?> {
        return Pair(BR.viewModel, mViewModel)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_payment
    }

    override fun setupObserver() {
        mViewModel.mldPaymentLink.observe(this, ::getPaymentLink)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        webLauncher.registerLifecycle(viewLifecycleOwner)
    }

    var callback: OnBackPressedCallback? = null
    override fun initView() {
        viewDataBinding?.apply {

            appBar.appBarView.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
                val maxScroll = appBarLayout.totalScrollRange
                val percentage = abs(verticalOffset).toFloat() / maxScroll.toFloat()
                handleAlphaOnTitle(percentage, appBar.containerAppbarTitle)
                handleToolbarTitleVisibility(
                    percentage,
                    appBar.toolbar.tvToolbarTitle,
                    getString(R.string.label_payment),
                    viewDataBinding?.appbarBackgroundImage?.imageBackground,
                    null
                )
            })
            appBar.toolbar.imgInfo.gone()

            appBar.toolbar.imageBack.setOnClickListener {
                paymentInfo?.ticket?.let {
                    mViewModel.cancelPayment(ticket = it)
                    callback?.remove()
                    requireActivity().onBackPressed()
                }
            }


            btnPayment.disableButton()
            tvDescSelectUserType.descTxt.text = getString(R.string.desc_select_user_type_payment)
            rgUserType.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    R.id.rbCurrentUser -> {
                        inputUserCode.isVisible = false
                        inputUserCodeForeigners.isVisible = false
                        userType = PaymentUserType.CURRENT_USER.typeCode
                        enteredNationalId = mViewModel.getNationalCode()
                        inputUserCode.setTextWidget("")
                        inputUserCodeForeigners.setValueOfText("")
                        inputUserCodeForeigners.setError("")
                        inputUserCode.getLayout().error = ""
                        btnPayment.enableButton()

                    }

                    R.id.rbOtherUser -> {
                        inputUserCode.isVisible = true
                        inputUserCodeForeigners.isVisible = false
                        userType = PaymentUserType.OTHER_USER.typeCode
                        inputUserCode.setTextWidget("")
                        inputUserCode.setHint(getString(R.string.other_user_payment))
                        btnPayment.disableButton()
                        inputUserCodeForeigners.setError("")
                        inputUserCode.getLayout().error = ""
                    }

                    R.id.rbNationalID -> {
                        inputUserCode.isVisible = true
                        inputUserCodeForeigners.isVisible = false
                        userType = PaymentUserType.NATIONAL_ID.typeCode
                        inputUserCode.setTextWidget("")
                        inputUserCode.setHint(getString(R.string.national_id_payment))
                        btnPayment.disableButton()
                        inputUserCodeForeigners.setError("")
                        inputUserCode.getLayout().error = ""
                    }

                    R.id.rbForeigners -> {
                        inputUserCode.isVisible = false
                        inputUserCodeForeigners.isVisible = true
                        userType = PaymentUserType.FOREIGNERS.typeCode
                        inputUserCodeForeigners.setValueOfText("")
                        btnPayment.disableButton()
                        inputUserCodeForeigners.setError("")
                        inputUserCode.getLayout().error = ""
                    }
                }
            }
            inputUserCodeForeigners.getInput().doAfterTextChanged {
                appBar.appBarView.setExpanded(false, true)
                inputUserCodeForeigners.requestFocus()
                nestedScrollView.scrollTo(0, inputUserCodeForeigners.bottom)
                val fda = inputUserCodeForeigners.getValue(false)
                if (inputUserCodeForeigners.isNotEmpty()) {
                    if (fda.length !in 10..16) {
                        btnPayment.disableButton()
                        inputUserCodeForeigners.setError(getString(R.string.foreigners_id_payment_error))
                    } else {
                        enteredNationalId = fda
                        btnPayment.enableButton()
                    }
                }
            }

            inputUserCode.getInput().doAfterTextChanged {
                appBar.appBarView.setExpanded(false, true)
                inputUserCode?.requestFocus()
                nestedScrollView.scrollTo(0, inputUserCode.bottom)

                val nationalId = inputUserCode.getValue(true)
                if (nationalId.isNotEmpty()) {
                    when (userType) {
                        1 -> {

                            val nationalCode = getValueNationalCode(true)
                            if (nationalCode.length == 10 && !inputUserCode.getLayout().isErrorEnabled){
                                enteredNationalId = nationalId
                                btnPayment.enableButton()
                            }
                        }

                        2 -> {
                            if (nationalId.length != 11) {
                                inputUserCode.getLayout().error =
                                    getString(R.string.national_id_payment_error)
                                btnPayment.disableButton()
                            } else {
                                enteredNationalId = nationalId
                                btnPayment.enableButton()
                            }
                        }
                    }
                }
            }
        }

        // This callback will only be called when MyFragment is at least Started.
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Handle the back button event
                paymentInfo?.ticket?.let {
                    mViewModel.cancelPayment(ticket = it)
                    callback?.remove()
                    requireActivity().onBackPressed()
                }
             /*   val dialog = DialogManagerMessageOfRequest.getInstanceOfDialog()
                dialog.arguments = createBundle(
                    MessageOfRequestDialogFragment.MessageType.WARNING,
                    getString(R.string.message_payment_warning),
                    btnCancel = true
                )
                dialog.setDialogClickListener(object : DialogClickInterface.onClickListener {
                    override fun onConfirmClick() {
                        //   handlePageDestination(R.id.action_objectionInsuranceHistoryFragment_to_servicesFragment)

                        callback?.remove()
                        requireActivity().onBackPressed()
                    }

                    override fun onCancelClick() {

                    }
                }
                )
                dialog.show(childFragmentManager, "paymentFragment")*/

            }
        }

        callback?.let {
            requireActivity().onBackPressedDispatcher.addCallback(this, it)
        }

        // The callback can be enabled or disabled here or in handleOnBackPressed()
        viewDataBinding?.apply {
             paymentInfo = if (VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                arguments?.getParcelable(ARG_PAYMENT_INFO, PaymentInfo::class.java)
            else
                arguments?.getParcelable(ARG_PAYMENT_INFO) as? PaymentInfo

            appBar.apply {
                tvValuePaymentId.text = paymentInfo?.paymentId ?: "_"
                tvValuePaymentValue.text = paymentInfo?.getPaymentPreview() ?: "_"
                tvValueReason.text = paymentInfo?.paymentDesc ?: "_"
                imgIcon.apply {
                    visibility = View.VISIBLE
                    setOnClickListener {
                        requireActivity().onBackPressed()
                    }
                }
            }
            paymentInfo?.milliSecondsToExpire?.let {

                timer = object : CountDownTimer(it, 1000) {
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
                        paymentInfo?.ticket?.let {ticket->
                            mViewModel.cancelPayment(ticket)
                        }
                    }
                }
                timer?.start()
            }

            //https://tfh.tamin.ir/api/v1.1/payment/payment-link/26eec086-89b3-4d3e-acb1-8ac6b8f7dd48
            //https://tfh.tamin.ir/api/v1.1/payment/payment-link/{payment-ticket}
            btnPayment.setOnClickListener {
                paymentInfo?.ticket?.let {
                    mViewModel.getPaymentLink(
                        body = PaymentUrlRequest(
                            enteredNcodeByUser = enteredNationalId,
                            personType = userType.toString()
                        ),
                        ticket = it
                    )
                }?: showAlertDialog(
                    MessageOfRequestDialogFragment.MessageType.ERROR,
                    getString(R.string.error_payment_token)
                )
            }
        }
    }

    private fun showTimerFinishedAlert() {
        val dialog = DialogManagerMessageOfRequest.getInstanceOfDialog()
        dialog.arguments = createBundle(
            MessageOfRequestDialogFragment.MessageType.ERROR,
            getString(R.string.message_payment_time_finished)
        )
        dialog.setDialogClickListener(object :
            DialogClickInterface.onClickListener {
            override fun onConfirmClick() {
                //   handlePageDestination(R.id.action_objectionInsuranceHistoryFragment_to_servicesFragment)

                callback?.remove()
                requireActivity().onBackPressed()
            }

            override fun onCancelClick() {
            }
        }
        )
        dialog.show(childFragmentManager, "returnToPrevPage")
    }


    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
    }

    fun openURL(mContext: Context, uri: Uri?) {
        val builder = CustomTabsIntent.Builder()
        builder.setShowTitle(true)
        val customTabsIntent = builder.build()
        val browserIntent = Intent()
            .setAction(Intent.ACTION_VIEW)
            .addCategory(Intent.CATEGORY_BROWSABLE)
            .setDataAndType(Uri.fromParts("http", "", null), "text/plain")

        var possibleBrowsers: List<ResolveInfo>
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            possibleBrowsers = mContext.packageManager
                .queryIntentActivities(browserIntent, PackageManager.MATCH_DEFAULT_ONLY)
            if (possibleBrowsers.isEmpty()) {
                possibleBrowsers = mContext.packageManager
                    .queryIntentActivities(browserIntent, PackageManager.MATCH_ALL)
            }
        } else {
            possibleBrowsers = mContext.packageManager
                .queryIntentActivities(browserIntent, PackageManager.MATCH_DEFAULT_ONLY)
        }
        if (possibleBrowsers.isNotEmpty()) {
            customTabsIntent.intent.setPackage(possibleBrowsers[0].activityInfo.packageName)
            customTabsIntent.launchUrl(mContext, uri!!)
        } else {
            val browserIntent2 = Intent(Intent.ACTION_VIEW, uri)
            mContext.startActivity(browserIntent2)
        }
    }


    override fun getData() {

    }


    override fun onClick() {

        viewDataBinding?.apply {


        }
    }

    fun createToolbarBundle(item: MenuModel): Bundle {
        val bundle = Bundle()
        bundle.putString(Constants.TOOLBAR_TITLE, item.title)
        bundle.putString(Constants.TOOLBAR_SUBTITLE, item.description)
        bundle.putInt(Constants.TOOLBAR_ICON_IMAGE, item.iconRes)
        return bundle
    }

    private fun getPaymentLink(result: PaymentLinkResponse) {
        if (result.isSuccess) {
            mViewModel.saveSystemType(arguments?.getString(SYSTEM_TYPE))
            mViewModel.saveEmployerDebtSerialNumber(arguments?.getString(Constants.DEBIT_SERIAL_NUMBER))
            callback?.remove()

            webLauncher.launchUrl(result.data?.paymentURL) { intent ->
                intent.standardWebFlags()
            }
        }
    }

    override fun onStop() {
        fragmentIsInBackground = true
        super.onStop()
    }

    @Override
    override fun onStart() {
        super.onStart();
        fragmentIsInBackground = false
        if (timerIsFinished) showTimerFinishedAlert()

    }

    fun getValueNationalCode(showError: Boolean = true): String {
        viewDataBinding?.apply {
            val nationalCode =
                ValidationUtil.persianToEnglish(inputUserCode.getValue(false))
            val model = ValidationUtil.nationalCode(requireContext(), nationalCode)

            Timber.tag("ValidationTagDebug")
                .i("getValueNationalCode: status=${model.status}  message=${model.message}   showError=$showError")
            return if (model.status) {
                inputUserCode.setError("")
                inputUserCode.getLayout().isErrorEnabled = false
                nationalCode.trim()
            } else {
                if (showError)
                    inputUserCode.setError(model.message)
                ""
            }
        }
        return ""
    }


}