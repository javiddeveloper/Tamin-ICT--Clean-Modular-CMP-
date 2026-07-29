package com.tamin.taminhamrah.ui.home.services.employer.payment

import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient

class CustomWebViewClient (private val token: String) : WebViewClient() {

    override fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest?): WebResourceResponse? {
        // Check if this is the request you want to modify, e.g., based on the URL.
      //  if (request?.url.toString().startsWith("https://example.com/api/")) {
            // Modify the request by adding the Bearer token to the headers.
           request?.requestHeaders?.toMutableMap()?.let {modifiedRequest->
                modifiedRequest["Authorization"] = "Bearer $token"
                view?.loadUrl(request.url.toString(), modifiedRequest)
               return null
            }
      //  }
        return super.shouldInterceptRequest(view, request)
    }
}