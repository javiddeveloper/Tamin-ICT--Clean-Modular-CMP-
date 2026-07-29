package com.tamin.taminhamrah.ui.dialog.messageOfReques

import com.tamin.taminhamrah.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MessageOfRequestDialogViewModel @Inject constructor() :
    BaseViewModel() {

    fun logout() {
       commonRepository.logOut()
        }

}
