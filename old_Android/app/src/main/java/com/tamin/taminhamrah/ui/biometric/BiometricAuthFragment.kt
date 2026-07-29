package com.tamin.taminhamrah.ui.biometric

import androidx.activity.OnBackPressedCallback
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.databinding.FragmentBiometricAuthBinding
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.utils.biometric.BiometricHelper
import com.tamin.taminhamrah.utils.isBiometricAvailable
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.Executor
import javax.inject.Inject

@AndroidEntryPoint
class BiometricAuthFragment :
    BaseFragment<FragmentBiometricAuthBinding, BiometricAuthViewModel>() {


    @Inject
    lateinit var biometricHelper: BiometricHelper

    override val mViewModel: BiometricAuthViewModel by viewModels()

    override fun getBindingVariable(): Pair<Int, Any?> =
        Pair(BR.viewModel, mViewModel)

    override fun getLayoutId(): Int = R.layout.fragment_biometric_auth

    override fun initView() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    requireActivity().finish()
                }
            }
        )

        setupBiometric()

        viewDataBinding?.fingerPrint?.setOnClickListener { setupBiometric() }
    }

    override fun setupObserver() {
    }

    override fun getData() {
    }

    override fun onClick() {
    }

    private fun setupBiometric() {
        biometricHelper.authenticate(
            fragment = this,
            onSuccess = {
                mViewModel.setBiometricEnabled(true)
                navigateToHome()
            },
            onError = { errorCode, _ ->
                if (errorCode == 0) {
                    mViewModel.setBiometricEnabled(false)
                    navigateToHome()
                }
            }
        )
    }

    private fun navigateToHome() {
        findNavController().navigate(R.id.action_biometricFragment_to_mainActivity)
        requireActivity().finish()
    }
}
