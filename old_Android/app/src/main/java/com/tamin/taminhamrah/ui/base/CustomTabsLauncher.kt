package com.tamin.taminhamrah.ui.base

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.browser.customtabs.CustomTabsCallback
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.browser.customtabs.CustomTabsService
import androidx.browser.customtabs.CustomTabsServiceConnection
import androidx.browser.customtabs.CustomTabsSession
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.utils.Utility.openInBrowser
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class CustomTabsLauncher @Inject constructor(@ApplicationContext private val context: Context) : DefaultLifecycleObserver {

    private var mSession: CustomTabsSession? = null
    private var mClient: CustomTabsClient? = null
    private var mConnection: CustomTabsServiceConnection? = null
    private var isBound = false

    fun registerLifecycle(lifecycle: LifecycleOwner) {
        lifecycle.lifecycle.addObserver(this)
    }

    override fun onStart(owner: LifecycleOwner) {
        bindCustomTabsService()
    }

    override fun onStop(owner: LifecycleOwner) {
        unbindCustomTabsService()
    }

    override fun onDestroy(owner: LifecycleOwner) {

    }

    private fun bindCustomTabsService() {
        if (isBound) return

        val packageName = CustomTabsClient.getPackageName(context, null) ?: return

        mConnection = object : CustomTabsServiceConnection() {
            override fun onCustomTabsServiceConnected(name: ComponentName, client: CustomTabsClient) {
                mClient = client
                mClient?.warmup(0)
                mSession = mClient?.newSession(object : CustomTabsCallback() {
                    override fun onRelationshipValidationResult(
                        relation: Int, requestedOrigin: Uri, result: Boolean, extras: Bundle?
                    ) {
                        // Can launch custom tabs intent after session was validated as the same origin.
                    }
                })
            }

            override fun onServiceDisconnected(name: ComponentName) {
                mClient = null
                mSession = null
            }
        }
        isBound = CustomTabsClient.bindCustomTabsService(context, packageName, mConnection!!)
    }

    private fun unbindCustomTabsService() {
        if (isBound && mConnection != null) {
            context.unbindService(mConnection!!)
            isBound = false
            mClient = null
            mSession = null
            mConnection = null
        }
    }

    fun launchUrl(
        url: String?,
        launchInBrowser: Boolean = false,
        configureIntent: ((Intent) -> Unit)? = null
    ) {
        if (url.isNullOrBlank()) return
        if (launchInBrowser || !isBound) {
            context.openInBrowser(url, configureIntent)
        } else {
            launchCustomTab(url, configureIntent)
        }
    }

    private fun launchCustomTab(url: String, configureIntent: ((Intent) -> Unit)?) {
        val uri = url.toUri()
        val intentBuilder = CustomTabsIntent.Builder(mSession)

        // Customizing the Custom Tab to feel more native
        val colorParams = CustomTabColorSchemeParams.Builder()
            .setToolbarColor(ContextCompat.getColor(context, R.color.colorPrimary))
            .build()
        
        intentBuilder.setDefaultColorSchemeParams(colorParams)
        
        //Summarize address bar (Hide title to show only domain)
        intentBuilder.setShowTitle(false)
        
        //Disable Share button
        intentBuilder.setShareState(CustomTabsIntent.SHARE_STATE_OFF)
        
        //Enable URL bar hiding on scroll for a cleaner look
        intentBuilder.setUrlBarHidingEnabled(true)

        //Setting animations for a native feel
        intentBuilder.setStartAnimations(context, R.anim.slide_in_left, R.anim.fade_out)
        intentBuilder.setExitAnimations(context, R.anim.fade_in, R.anim.slide_out_left)

        val customTabsIntent = intentBuilder.build()

        customTabsIntent.intent.apply { configureIntent?.invoke(this) }
        mSession?.validateRelationship(CustomTabsService.RELATION_USE_AS_ORIGIN, uri, null)

        try {
            customTabsIntent.launchUrl(context, uri)
        } catch (e: Exception) {
            context.openInBrowser(url, configureIntent)
        }
    }
}