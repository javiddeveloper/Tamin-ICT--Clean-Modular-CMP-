package com.tamin.taminhamrah.ui.base

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.browser.customtabs.CustomTabsCallback
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsIntent
import androidx.browser.customtabs.CustomTabsService
import androidx.browser.customtabs.CustomTabsServiceConnection
import androidx.browser.customtabs.CustomTabsSession
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.ui.dialog.messageOfReques.MessageOfRequestDialogFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.MessageOfRequestDialogFragment.DismissType
import com.tamin.taminhamrah.ui.dialog.messageOfReques.manger.DialogManagerMessageOfRequest
import com.tamin.taminhamrah.utils.Utility
import com.tamin.taminhamrah.utils.extentions.createBundle
import com.tamin.taminhamrah.utils.extentions.navigateSafe
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

abstract class BaseFragmentMVI<B : ViewBinding, VM : BaseViewModelMVI<*, S, E>, S, E> :
    Fragment() {

    protected var binding: B? = null
        private set

    protected abstract val viewModel: VM

    protected abstract fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): B

    var mSession: CustomTabsSession? = null
    var mConnection: CustomTabsServiceConnection? = null

    fun showAlertDialog(
        type: MessageOfRequestDialogFragment.MessageType,
        desc: String,
        dismissType: DismissType = DismissType.NORMAL,
        titleId: Int? = null
    ) {
        val dialog = DialogManagerMessageOfRequest.getInstanceOfDialog()
        dialog.arguments = createBundle(type, desc, dismissType = dismissType, titleId = titleId)
        dialog.show(childFragmentManager, "Alert Dialog MessageOfRequest")
    }


    protected abstract fun renderState(state: S)

    protected abstract fun handleEvent(event: E)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = inflateBinding(inflater, container)
        return requireNotNull(binding).root
    }

    override fun onStart() {
        super.onStart()
        val callback: CustomTabsCallback = object : CustomTabsCallback() {
            override fun onRelationshipValidationResult(
                relation: Int, requestedOrigin: Uri,
                result: Boolean, @Nullable extras: Bundle?
            ) {
                // Can launch custom tabs intent after session was validated as the same origin.

            }
        }
        mConnection = object : CustomTabsServiceConnection() {
            override fun onCustomTabsServiceConnected(
                name: ComponentName,
                client: CustomTabsClient
            ) {
                // Create session after service connected.
                mSession = client.newSession(callback)
                client.warmup(0)
                // Validate the session as the same origin to allow cross-origin headers.
            }

            override fun onServiceDisconnected(componentName: ComponentName) {}
        }
        val packageName = Utility.getBrowserPackageName(requireContext())
        // Bind the custom tabs service connection.
        packageName?.let {
            CustomTabsClient.bindCustomTabsService(
                requireContext(),
                packageName,
                mConnection!!
            )
        } ?: showAlertDialog(
            MessageOfRequestDialogFragment.MessageType.ERROR,
            getString(R.string.browser_error)
        )

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onViewBindingCreated(view, savedInstanceState)
        observeState()
        observeEvents()
        onViewBindingCreated(view, savedInstanceState)
    }

    protected open fun onViewBindingCreated(view: View, savedInstanceState: Bundle?) {
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collectLatest { state ->
                renderState(state)
            }
        }
    }

    private fun observeEvents() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.events.collectLatest { event ->
                handleEvent(event)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    fun handlePageDestination(
        id: Int,
        bundle: Bundle? = null,
        finishActivity: Boolean = false,
    ) {

        if (finishActivity) {
            requireActivity().finish()
        }

        navigateSafe(id, bundle)
    }

    fun lunchUrl(url: String?, additionalFlag: Boolean = false, launchInBrowser: Boolean = false) {
        if (url.isNullOrBlank() || !Utility.isValidWebUrl(url)) {
            Toast.makeText(context, "آدرس اینترنتی پزشک یافت نشد!", Toast.LENGTH_SHORT).show()
            return
        }

        val uri = Uri.parse(url)
        val packageName = CustomTabsClient.getPackageName(requireContext(), null)
        if (!launchInBrowser && packageName != null) {
            mSession?.let {
                it.validateRelationship(CustomTabsService.RELATION_USE_AS_ORIGIN, uri, null)
                val intent: CustomTabsIntent? =
                    CustomTabsIntent.Builder(it).build()
                intent?.intent?.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                if (additionalFlag)
                    intent?.intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent?.intent?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent?.intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                intent?.launchUrl(requireContext(), uri)
            }
        } else {
            openInBrowser(requireContext(), url)
        }
    }

    private fun openInBrowser(context: Context, url: String?) {
        url?.let {
            val intent = Intent(Intent.ACTION_VIEW, it.toUri()).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }
    }

    override fun onStop() {

        if (mSession != null) {
            mConnection = null
            mSession = null
        }
        super.onStop()
    }
}

