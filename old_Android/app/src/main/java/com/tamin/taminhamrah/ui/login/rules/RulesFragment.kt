package com.tamin.taminhamrah.ui.login.rules

import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.viewModels
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.databinding.FragmentRulesBinding
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.ui.login.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RulesFragment : BaseFragment<FragmentRulesBinding, LoginViewModel>() {

    override val mViewModel: LoginViewModel by viewModels()

    override fun getBindingVariable() = Pair(BR.viewModel, mViewModel)

    override fun getLayoutId() = R.layout.fragment_rules

    override fun setupObserver() {
    }

    override fun initView() {
    }

    override fun getData() {
        viewDataBinding?.apply {
            val webSettings: WebSettings = webView.settings
            webSettings.javaScriptEnabled = true
            webSettings.domStorageEnabled = true
            webSettings.allowContentAccess = true
            webSettings.allowFileAccess = true
            // Set WebViewClient to handle URL loading within the WebView

            // Disable the toolbar
            webView.webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    try {
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            val url = Constants.LINK_RULES
            webView.loadUrl(url)
        }
    }

    override fun onClick() {
    }


}