package com.tamin.taminhamrah.ui.laws

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.webkit.CookieManager
import android.webkit.JsResult
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.databinding.FragmentLawsBinding
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.utils.extentions.gone
import com.tamin.taminhamrah.utils.extentions.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LawsFragment :
    BaseFragment<FragmentLawsBinding, LawsViewModel>() {

    private val args: LawsFragmentArgs by navArgs()
    override val mViewModel: LawsViewModel by viewModels()

    override fun getBindingVariable(): Pair<Int, Any?> {
        return Pair(BR.viewModel, mViewModel)
    }

    override fun getLayoutId() = R.layout.fragment_laws

    override fun setupObserver() {}

    override fun initView() {
        setupWebView()
        setupOnBackPressed()
        mViewModel.webViewState.let { bundle ->
            if (!bundle.isEmpty) {
                viewDataBinding?.webView?.restoreState(bundle)
            } else {
                viewDataBinding?.webView?.loadUrl(args.webUrl?:mViewModel.getLawsURL())
            }
        }
    }

    override fun getData() {}

    override fun onClick() {
        viewDataBinding?.apply {
            toolbarLayout.imgInfo.gone()

            refreshLayout.setOnRefreshListener {
                reload()
            }
            toolbarLayout.imageBack.setOnClickListener {
                backHandling()
            }
            toolbarLayout.tvToolbarTitle.text = getString(R.string.tamins_laws)
        }
    }

    private fun setupOnBackPressed() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            requireActivity().onBackPressedDispatcher.addCallback(
                viewLifecycleOwner,
                object : androidx.activity.OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                        isEnabled = false
                        backHandling()
                    }
                }
            )
        }
    }

    private fun backHandling() {
        if (!goBack()) {
            requireActivity().onBackPressed()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        CookieManager.getInstance().apply {
            setAcceptCookie(true)
            setAcceptThirdPartyCookies(viewDataBinding?.webView, true)
        }
        viewDataBinding?.webView?.apply {
            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                databaseEnabled = true
                allowFileAccess = true
                allowContentAccess = true
                cacheMode = WebSettings.LOAD_DEFAULT
                useWideViewPort = true
                loadWithOverviewMode = true
                setSupportZoom(false)
                builtInZoomControls = false
                displayZoomControls = false


                allowUniversalAccessFromFileURLs = false
                allowFileAccessFromFileURLs = false
                mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE

                setRenderPriority(WebSettings.RenderPriority.HIGH)
                layoutAlgorithm = WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING
            }

            webViewClient = CustomWebViewClient()

            webChromeClient = CustomWebChromeClient()
        }
    }

    private inner class CustomWebViewClient : WebViewClient() {

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            showLoading()
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            hideLoading()
            viewDataBinding?.webView?.visible()
            viewDataBinding?.refreshLayout?.isRefreshing = false
        }

        override fun onReceivedError(
            view: WebView?,
            request: WebResourceRequest?,
            error: WebResourceError?
        ) {
            super.onReceivedError(view, request, error)
            showError()
        }

        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
        ): Boolean {
            return false
        }
    }

    private inner class CustomWebChromeClient : WebChromeClient() {

        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            super.onProgressChanged(view, newProgress)
        }

        override fun onJsAlert(
            view: WebView?,
            url: String?,
            message: String?,
            result: JsResult?
        ): Boolean {
            return super.onJsAlert(view, url, message, result)
        }
    }


    private fun showError() {
        viewDataBinding?.webView?.gone()
        viewDataBinding?.refreshLayout?.isRefreshing = false
    }

    fun loadUrl(url: String) {
        viewDataBinding?.webView?.loadUrl(url)
    }

    fun goBack(): Boolean {
        return if (viewDataBinding?.webView?.canGoBack() == true) {
            viewDataBinding?.webView?.goBack()
            true
        } else {
            false
        }
    }

    fun reload() {
        viewDataBinding?.webView?.reload()
    }

    override fun onResume() {
        super.onResume()
        viewDataBinding?.webView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        viewDataBinding?.webView?.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewDataBinding?.webView?.saveState(mViewModel.webViewState)
        viewDataBinding?.webView?.destroy()
    }
}
