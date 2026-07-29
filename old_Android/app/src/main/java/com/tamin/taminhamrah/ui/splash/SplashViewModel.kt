package com.tamin.taminhamrah.ui.splash

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import com.tamin.taminhamrah.data.repository.LoginRepository
import com.tamin.taminhamrah.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val loginRepository: LoginRepository
) : BaseViewModel() {

    val mldTimerSplash = MutableLiveData<Boolean>()

    fun splashTimer(time: Long) {
        Handler(Looper.getMainLooper()).postDelayed({
            mldTimerSplash.postValue(true)
        }, time)
    }

    fun hasValidToken(): Boolean {
        return if (loginRepository.getToken().isBlank()) {
            false
        } else {
            loginRepository.hasValidToken()
        }
    }
}

