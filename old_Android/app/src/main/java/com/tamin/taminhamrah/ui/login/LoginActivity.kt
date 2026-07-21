package com.tamin.taminhamrah.ui.login

import android.os.Bundle
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.Resource
import com.tamin.taminhamrah.databinding.ActivityLoginBinding
import com.tamin.taminhamrah.ui.base.ContainerBaseActivity
import com.tamin.taminhamrah.ui.dialog.messageOfReques.MessageOfRequestDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : ContainerBaseActivity() {
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val rootView = binding.root
        setContentView(rootView)
    }
    fun handleResponse(result: Resource<Any?>?, showError: Boolean = true) {
        when (result?.status) {
            Resource.Status.LOADING -> {
                showLoading(binding.parent)
            }
            Resource.Status.ERROR -> {
                hideLoading()
                if (showError)
                    showAlertDialog(
                        MessageOfRequestDialogFragment.MessageType.ERROR,
                        result.message?.message ?: getString(R.string.message_invalide_error)
                    )
            }
            Resource.Status.NEED_REFRESH_TOKEN -> {
                hideLoading()
                showAlertDialog(
                    MessageOfRequestDialogFragment.MessageType.REFRESHTOKEN,
                    resources.getString(R.string.message_need_to_refresh_token)
                )
            }
            Resource.Status.NEED_NETWORK -> {
                hideLoading()
                result.message?.let {
                    showAlertDialog(MessageOfRequestDialogFragment.MessageType.INFO, it.message)
                }
            }
            Resource.Status.SUCCESS -> {
                hideLoading()

            }

            else -> {}
        }
    }
}