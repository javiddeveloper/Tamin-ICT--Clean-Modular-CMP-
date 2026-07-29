package com.tamin.taminhamrah.ui.laws

import android.os.Bundle
import com.tamin.taminhamrah.data.repository.LoginRepository
import com.tamin.taminhamrah.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LawsViewModel @Inject constructor(
    private val loginRepository: LoginRepository
)  : BaseViewModel() {

    val webViewState = Bundle()

    fun getLawsURL(): String {
        return "https://law.tamin.ir/"
    }
}