package com.tamin.taminhamrah.ui.login.usermode

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tamin.taminhamrah.data.entity.UserInfo
import com.tamin.taminhamrah.data.remote.models.MessageModel
import com.tamin.taminhamrah.data.remote.models.Resource
import com.tamin.taminhamrah.data.repository.LoginRepository
import com.tamin.taminhamrah.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserModeViewModel @Inject constructor(
    private val loginRepository: LoginRepository
) : BaseViewModel() {

    val mldUserMode = MutableLiveData<Resource<UserInfo?>>()
    val mldUserAvatar = MutableLiveData<Resource<String?>>()


    /* fun saveToken(accessToken: String?) {
         pref.setToken(accessToken)
     }*/

    fun getUserProfileImage(tempToken: String?) {

        viewModelScope.launch {
            mldUserAvatar.postValue(Resource.loading(null))
            try {
                val result = loginRepository.getUserProfileImage(tempToken)
                result.let {
                    if (result.isSuccess) {
                        commonRepository.setUserAvatar(result.data)
                    }
//                    mldUserAvatar.postValue(result)
                }

            } catch (e: Exception) {
                //handelError(e)
                mldUserAvatar.postValue(Resource.error(MessageModel(e.message ?: e.toString(), 0)))
            }
        }

    }

}
