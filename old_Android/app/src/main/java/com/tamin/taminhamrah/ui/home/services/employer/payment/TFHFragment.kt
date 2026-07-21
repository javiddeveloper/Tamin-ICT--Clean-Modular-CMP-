package com.tamin.taminhamrah.ui.home.services.employer.payment

import android.annotation.SuppressLint
import android.net.Uri
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebViewClient
import androidx.fragment.app.viewModels
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.services.payment.PaymentLinkResponse
import com.tamin.taminhamrah.databinding.FragmentTfhBinding
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.ui.home.services.employer.payment.viewmodel.TFHViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TFHFragment : BaseFragment<FragmentTfhBinding, TFHViewModel>() {

    override val mViewModel: TFHViewModel by viewModels()

    override fun getBindingVariable() = Pair(BR.viewModel, mViewModel)

    override fun getLayoutId() = R.layout.fragment_tfh

    override fun setupObserver() {
        mViewModel.mldPaymentLink.observe(this, ::getPaymentLink)
    }

    override fun initView() {
    }

    override fun getData() {
      /*  val ticket = arguments?.getString(PaymentFragment.ARG_PAYMENT_TICKET)
        val nationalId = arguments?.getString(PaymentFragment.ARG_NATIONAL_ID)
        val userType = arguments?.getString(PaymentFragment.ARG_USER_TYPE)
        if (ticket != null && nationalId != null && userType != null) {
            mViewModel.getPaymentLink(
                body = PaymentUrlRequest(
                    enteredNcodeByUser = nationalId,
                    personType = userType
                ),
                ticket = ticket
            )
        } else {
            showAlertDialog(
                MessageOfRequestDialogFragment.MessageType.ERROR,
                getString(R.string.error_payment_token)
            )
        }*/
    }

    override fun onClick() {
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun getPaymentLink(result: PaymentLinkResponse) {
        if (result.isSuccess) {
            val uri = Uri.parse(result.data?.paymentURL)
            /*            mSession?.let {
                            it.validateRelationship(
                                CustomTabsService.RELATION_USE_AS_ORIGIN,
                                uri, null
                            )
                            val intent: CustomTabsIntent? = Utility.constructExtraHeadersIntent(it, mViewModel.getToken())
                            intent?.intent?.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            intent?.launchUrl(requireContext(), uri)


                            mViewModel.saveSystemType(arguments?.getString(PaymentFragment.SYSTEM_TYPE))
                            mViewModel.saveEmployerDebtSerialNumber(arguments?.getString(Constants.DEBIT_SERIAL_NUMBER))
                            callback?.remove()
            //                navigateUp()
                        }*/

            viewDataBinding?.apply {
                // Enable JavaScript (if needed)
                val webSettings: WebSettings = tfhWebView.settings
                webSettings.javaScriptEnabled = true

               // Set WebViewClient to handle URL loading within the WebView
                tfhWebView.webViewClient = WebViewClient()

               // Disable the toolbar
                tfhWebView.webChromeClient = WebChromeClient()

                //load url
                result.data?.paymentURL?.let { tfhWebView.loadUrl(it) }


                mViewModel.saveSystemType(arguments?.getString(PaymentFragment.SYSTEM_TYPE))
                mViewModel.saveEmployerDebtSerialNumber(arguments?.getString(Constants.DEBIT_SERIAL_NUMBER))

            }

        }

    }

}