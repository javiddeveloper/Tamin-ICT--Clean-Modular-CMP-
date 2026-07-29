package com.tamin.taminhamrah.ui.settings

import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.local.models.ApplicationThemeEnum
import com.tamin.taminhamrah.databinding.FragmentSettingsBinding
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.utils.biometric.BiometricHelper
import com.tamin.taminhamrah.utils.extentions.visible
import com.tamin.taminhamrah.utils.isBiometricAvailable
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : BaseFragment<FragmentSettingsBinding, SettingsViewModel>() {

    @Inject
    lateinit var biometricHelper: BiometricHelper
    private var targetBiometricState: Boolean = false

    override val mViewModel: SettingsViewModel by viewModels()
    override fun getBindingVariable(): Pair<Int, Any?> = Pair(BR.viewModel, mViewModel)
    override fun getLayoutId(): Int = R.layout.fragment_settings

    override fun initView() {
        if (arguments?.getBoolean("showBackButton") == true) {
            viewDataBinding?.layHeader?.imbBack?.apply {
                visible()
                setOnClickListener { findNavController().popBackStack() }
            }
        }
        setupBiometricFeature()
        setupThemeRow()
    }

    private fun setupBiometricFeature() {
        if (requireContext().isBiometricAvailable()) {
            viewDataBinding?.layoutBiometric?.root?.visibility = View.VISIBLE
            viewDataBinding?.divider3?.visibility = View.VISIBLE
            setupSwitchButton()
        } else {
            viewDataBinding?.layoutBiometric?.root?.visibility = View.GONE
            viewDataBinding?.divider3?.visibility = View.GONE
            if (mViewModel.isBiometricEnabled()) {
                mViewModel.setBiometricEnabled(false)
            }
        }
    }

    private fun handleBiometricFailure() {
        viewDataBinding?.layoutBiometric?.switchDarkMode?.apply {
            if (isChecked != mViewModel.isBiometricEnabled()) {
                isChecked = mViewModel.isBiometricEnabled()
            }
        }
    }

    private fun setupSwitchButton() {
        viewDataBinding?.layoutBiometric?.apply {
            switchDarkMode.setOnCheckedChangeListener { buttonView, isChecked ->
                if (!buttonView.isPressed) return@setOnCheckedChangeListener
                targetBiometricState = isChecked
                buttonView.isChecked = !isChecked
                showBiometricDialog()
            }
        }
    }
    private fun showBiometricDialog() {
        biometricHelper.authenticate(
            fragment = this,
            onSuccess = { _ ->
                mViewModel.setBiometricEnabled(targetBiometricState)
                viewDataBinding?.layoutBiometric?.switchDarkMode?.isChecked = targetBiometricState
            },
            onError = { _, _ ->
                handleBiometricFailure()
            }
        )
    }

    private fun setupThemeRow() {
        viewDataBinding?.apply {
            val savedThemeState = mViewModel.getApplicationTheme()
            val buttonIdToSelect = when (savedThemeState) {
                ApplicationThemeEnum.DAY.state -> R.id.btnThemeLight
                ApplicationThemeEnum.NIGHT.state -> R.id.btnThemeDark
                else -> R.id.btnThemeAuto
            }
            toggleButtonTheme.check(buttonIdToSelect)
            toggleButtonTheme.addOnButtonCheckedListener { group, checkedId, isChecked ->
                if (isChecked) {
                    val (selectedThemeEnum, nightMode) = when (checkedId) {
                        R.id.btnThemeAuto -> Pair(
                            ApplicationThemeEnum.FOLLOW_SYSTEM,
                            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                        )
                        R.id.btnThemeLight -> Pair(
                            ApplicationThemeEnum.DAY,
                            AppCompatDelegate.MODE_NIGHT_NO
                        )
                        R.id.btnThemeDark -> Pair(
                            ApplicationThemeEnum.NIGHT,
                            AppCompatDelegate.MODE_NIGHT_YES
                        )
                        else -> Pair(
                            ApplicationThemeEnum.FOLLOW_SYSTEM,
                            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                        )
                    }
                    mViewModel.setApplicationTheme(selectedThemeEnum)
                    AppCompatDelegate.setDefaultNightMode(nightMode)
                }
            }
        }
    }

    override fun setupObserver() {}

    override fun getData() { viewDataBinding?.layoutBiometric?.switchDarkMode?.isChecked = mViewModel.isBiometricEnabled() }

    override fun onClick() {}
}