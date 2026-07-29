package com.tamin.taminhamrah.ui.splash

import androidx.biometric.BiometricManager
import androidx.fragment.app.viewModels
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.BuildConfig
import com.tamin.taminhamrah.Constants.BASE_URL
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.databinding.FragmentSplashBinding
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.utils.isBiometricAvailable
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class SplashFragment : BaseFragment<FragmentSplashBinding, SplashViewModel>() {

    override val mViewModel: SplashViewModel by viewModels()

    override fun getBindingVariable(): Pair<Int, Any?> {
        return Pair(BR.viewModel, mViewModel)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_splash
    }

    override fun setupObserver() {
        mViewModel.mldTimerSplash.observe(this, ::navigateToLogin)
    }

    override fun initView() {
        viewDataBinding?.tvAppVersion?.text = BuildConfig.VERSION_NAME

    }

    override fun getData() {
        mViewModel.splashTimer(2000)
    }

    override fun onClick() {}

    private fun navigateToLogin(result: Boolean) {

        if (BASE_URL.contains("eservices-test")) {
            //TODO: How to get token on test faze
            mViewModel.setToken(
                "Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6InZQYXQzTVNiWlBCZ1pIVVRRMnh2VHlCN3d5VSJ9.eyJleHAiOjE3MDc4MjY4MjAsImF1ZCI6Ijc0MTc2NTNiMzUyODQyMTk2YTA3MzcwYTZjNDYzYjZlIiwiaXNzIjoiaHR0cDovL2lkbS50YW1pbi5pciIsInVybjp0YW1pbjpqd3Q6Y2xhaW06dG9rZW4tdHlwZSI6ImFjY2Vzc190b2tlbiIsInVybjp0YW1pbjpqd3Q6Y2xhaW06dmVyc2lvbiI6IjEuMCIsImNsaWVudF9pZCI6Ijc0MTc2NTNiMzUyODQyMTk2YTA3MzcwYTZjNDYzYjZlIiwic3ViIjoiNDI4MDkyMTgwNiIsImp0aSI6Imlzdk9aYk41VkM2MUlrVXpVSkJTbXciLCJ1cm46dGFtaW46and0OmNsYWltOmdyb3VwcyI6WyJXb3Jrc2hvcCBBZG1pbiIsIldvcmtzaG9wIFN1cHBvcnQgVXNlcnMiLCJXb3Jrc2hvcCBVc2VyIiwiQUxMIFVTRVJTIiwiV0tTUF9TVVBQT1JUIFVTRVIiXSwidXJuOnRhbWluOmp3dDpjbGFpbTpvcmciOiIwMDAwIiwidXJuOnRhbWluOmp3dDpjbGFpbToyZmEiOiJvdHAiLCJpYXQiOjE3MDc4MjMyMjAsIm5iZiI6MTcwNzgyMzEwMH0.H1c-W5uryx1U5P6LdobNY1IXET6lYKHltU2cAqjvl1SMDeuKl3wusnkBxRz9Nlj5QFnNWvqVGXyr9bEDwC38yXGhzJWCeIsOdV4XFcGUuXv6G2X07YwrH2tKg4fQEvDyYgaCl9CiaV07iLmiIttuuoKXIujr7R8o0Il4sV-KkmfrBzXu299XAx9VeV0X8lxQqzzmzYq2noSkBGaD2TLPHJ3WQ4LFD21aVgu3aA2k0zQjwGTGicDP_kfPuCz0n_gSJtgAZeAWx6VrHYSS2AIgBu2nJf-Slwi3Ic0mUfF6H2kkPX8JYkACSA-DQY7G6P2NgfvLYh1hKR0I2MpsnlKVVA"
            )
            handlePageDestination(
                R.id.action_splash_to_biometric,
                finishActivity = true
            )
        } else {
            if (mViewModel.getToken().isNotEmpty()) {
                if (mViewModel.isBiometricEnabled() && requireContext().isBiometricAvailable()) {
                    handlePageDestination(
                        R.id.action_splash_to_biometric,
                        finishActivity = false
                    )
                } else {
                    handlePageDestination(
                        R.id.action_splash_to_home,
                        finishActivity = true
                    )
                }

            } else {
                handlePageDestination(
                    R.id.action_splash_to_login,
                    finishActivity = true
                )
            }
        }
    }

    private fun initService() {
        Timber.tag("UpdateCheck").i("initService()")

    }

    /** This is our function to un-binds this activity from our service.  */
}