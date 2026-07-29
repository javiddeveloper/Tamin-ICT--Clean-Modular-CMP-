package com.tamin.taminhamrah.ui.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.entity.ProfileModel
import com.tamin.taminhamrah.data.entity.UserInfo
import com.tamin.taminhamrah.data.remote.models.profile.ProfileResponse
import com.tamin.taminhamrah.data.remote.models.profile.asDomainModel
import com.tamin.taminhamrah.data.remote.models.services.CheckUpdateResponse
import com.tamin.taminhamrah.data.remote.models.user.DeviceDetailModel
import com.tamin.taminhamrah.data.remote.models.user.LoginResponse
import com.tamin.taminhamrah.data.repository.LoginRepository
import com.tamin.taminhamrah.enums.EnumUserMode.MODE_EMPLOYER
import com.tamin.taminhamrah.enums.EnumUserMode.MODE_INSURED
import com.tamin.taminhamrah.enums.EnumUserMode.MODE_PENSIONER
import com.tamin.taminhamrah.ui.base.BaseViewModel
import com.tamin.taminhamrah.utils.MultipleLiveData
import com.tamin.taminhamrah.utils.TaminLogger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.UnsupportedEncodingException
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginRepository: LoginRepository
) : BaseViewModel() {

    //    var mldLoginInfo = MutableLiveData<LoginResponse>()
    val mldUserMode = MutableLiveData<UserInfo?>()
    val mldProfile = MutableLiveData<ProfileResponse>()
    val mldCheckUpdate = MutableLiveData<CheckUpdateResponse>()
    val mldShowInfoLoading = MultipleLiveData<Boolean>()
    val mldLoginRes = MutableLiveData<LoginResponse>()


fun hasValidToken(){
    loginRepository.hasValidToken()
}
    fun checkUpdate(accessToken: String) {
        viewModelScope.launch {
            mldCheckUpdate.postValue(callService { loginRepository.checkUpdate(accessToken) })
        }
    }

    fun postDeviceDataModel(deviceDetailModel: DeviceDetailModel) {
        viewModelScope.launch {
            callService { loginRepository.postDeviceDataModel(deviceDetailModel) }
        }
    }

    fun signIn(codeFromServer: String, codeVerifier: String) {
        viewModelScope.launch {
            mldLoginRes.postValue(callService {
                loginRepository.signIn("${Constants.BASE_URL_ACCOUNT}server/v2/token",
                    codeFromServer,
                    codeVerifier
                )
            })
        }
    }

    fun login(nationalCode: String, password: String) {
//        val map = HashMap<String, String>()
//        map["CLASS"] = this.javaClass.simpleName
//        map["METHOD"] = "login"
//        map["nationalCode"] = nationalCode
//        map["password"] = password
//
//          TaminLogger.putLog(map)
        viewModelScope.launch {
            //     mldLoginInfo.postValue(Resource.loading(null))

            //  mldLoadingState.postValue(LoadingState.LOADING)

            //    mldLoadingState.postValue(LoadingState.NOT_LOADING)
            Timber.tag("loginRepository").e("login: CALLED")

            /* mldLoginInfo.postValue(callService {
                 loginRepository.login(
                     nationalCode,
                     password,
                     getHashCode(nationalCode),
                     getAppId(),
                     getRequestCode()
                 )
             })*/

        }

//        viewModelScope.launch {
//
//            loginRepository.testMenu()
//
//        }

    }

    private fun getRequestCode(): String {
        return "1001"
    }

    private fun getAppId(): String {
        return "com.tamin.taminhamrah"
    }

    val String.sha1: String
        get() {
//            return DigestUtils.sha1Hex(this)
            val bytes = MessageDigest.getInstance("SHA1").digest(this.toByteArray(StandardCharsets.UTF_8))
            return convToHex(bytes)

        }

    private fun convToHex(data: ByteArray): String {
        return data.joinToString("") { "%02x".format(it) }
    }

    @Throws(NoSuchAlgorithmException::class, UnsupportedEncodingException::class)
    fun SHA1(text: String): String? {
        val md: MessageDigest = MessageDigest.getInstance("SHA-1")
        var sha1hash = ByteArray(40)
        md.update(
            text.toByteArray(charset(StandardCharsets.ISO_8859_1.displayName())),
            0,
            text.length
        )
        sha1hash = md.digest()
        return convToHex(sha1hash)
    }


    private fun getHashCode(value: String): String {
        var hash = ""
        try {
            hash = (value.substring(4, 6).toInt().toString(16) + getAppId() +
                    value.substring(6, 8).toInt().toString(16) + getRequestCode() +
                    value.substring(8, 10).toInt().toString(16))
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        return hash.sha1.uppercase()
    }

    fun saveLoginInfo(
        accessToken: String,
        expiresIn: Long,
        refreshToken: String
    ) {

        val currentTime = System.currentTimeMillis() / 1000
        val expireTime = currentTime + expiresIn
        loginRepository.saveLoginInfo(
            accessToken,
            expireTime,
            refreshToken
        )

    }

    fun getNationalId() = loginRepository.getNationalCode()
    fun setNationalCode(nationalCode: String) = loginRepository.setNationalCode(nationalCode)

    /* fun saveToken(accessToken: String?) {
         pref.setToken(accessToken)
     }*/
    fun getUserModeData() {

        viewModelScope.launch {
            //  try {
            val userMode = commonRepository.getUserMode()

            val itemList = arrayListOf(
                MenuModel(
                    id = MODE_INSURED.methodValue.toString(),
                    title = MODE_INSURED.methodName,
                    isSelected = userMode == MODE_INSURED.methodName
                ), MenuModel(
                    id = MODE_PENSIONER.methodValue.toString(),
                    title = MODE_PENSIONER.methodName,
                    isSelected = userMode == MODE_PENSIONER.methodName
                ), MenuModel(
                    id = MODE_EMPLOYER.methodValue.toString(),
                    title = MODE_EMPLOYER.methodName,
                    isSelected = userMode == MODE_EMPLOYER.methodName
                )
            )

            val user = UserInfo()
            //   user.username = commonRepository.getUserName()
            user.isEnable = true
            user.imageUrl = getUserAvatar()
            user.modeList.addAll(itemList)
            mldUserMode.postValue(user)
//            } catch (e: Exception) {
//                //handelError(e)
//                mldUserMode.postValue(Resource.error(MessageModel(e.message ?: e.toString(), 0)))
//            }
        }

    }

    fun getUserInfoFromServer(tempToken: String?) {
        viewModelScope.launch {
            //   mldLoginInfo.postValue(Resource.loading(null))
            mldShowInfoLoading.postValue(true)

            val avatarRes = async(Dispatchers.IO) {
                loginRepository.getUserProfileImage(tempToken)
            }
            val profileRes = async(Dispatchers.IO) {
                loginRepository.getProfileInfo( tempToken)
            }

            val avatarResponse = avatarRes.await()

            if (avatarResponse.isSuccess) {
                commonRepository.setUserAvatar(avatarResponse.data)
            }

         //   mldUserAvatar.postValue(avatarResponse)


             profileRes.await().apply {
                  mldShowInfoLoading.postValue(false)
                  setUserInfo(this.asDomainModel())

                  if (isSuccess)
                      mldProfile.postValue(this)
                  else
                      mldErrorState.postValue(this)


              }




        }
    }

    fun setUserInfo(profileModel:ProfileModel){
        commonRepository.setUserInfo(profileModel)
    }
    fun logOut() {
        val map = HashMap<String, String>()
        map["CLASS"] = this.javaClass.simpleName
        map["METHOD"] = "logOut"
        map["condition"] = " Logout and clear Token"
        TaminLogger.putLog(map)
        commonRepository.logOut()
    }

    fun getUserLocalInfo(): ProfileModel {
        return commonRepository.getUserInfo()
    }

    var mldUpdateStatus = MutableLiveData<LoginResponse>()


    fun setCodeVerifier(codeVerifier: String?) {
        loginRepository.setCodeVerifier(codeVerifier)
    }

    fun getCodeVerifier(): String? {
        return loginRepository.getCodeVerifier()
    }


    fun getLoginMenuList(): java.util.ArrayList<MenuModel> {
        val itemList = java.util.ArrayList<MenuModel>()

        /*   itemList.add(
            MenuModel(
                   "وارد حساب کاربری شوید",
                   "1",
                   "ic_my_request_colorful",
                   R.drawable.ic_exit_to_app,
                   "ورود متمرکز سازمان تأمین اجتماعی"
               , textColor = "#3acc6c"
               )
           )*/

   /*     itemList.add(
            MenuModel(
                "استعلام حمایت های درمانی",
                "1",
                "ic_inbox_colorful",
                R.drawable.ic_medical_kit,
                ""
            )
        )*/
        itemList.add(
            MenuModel(
                "سامانه ارتباطات و نظارت مردمی",
                "2",
                "ic_inbox_colorful",
                R.drawable.ic_website,
                ""
            )
        )
        itemList.add(
            MenuModel(
                "مطالعه حریم خصوصی",
                "3",
                "",
                0,
                ""
            )
        )
/*
        itemList.add(
            MenuModel(
                "سامانه تخصصی قوانین و مقررات تأمین اجتماعی",
                "4",
                "ic_doc",
                0,
                ""
            )
        )*/
        return itemList
    }

    /* fun setTempTokenInfo(result: LoginResponse?) {
         loginRepository.setTempTokenInfo(result)
     }

     fun getTempTokenInfo() = loginRepository.getTempTokenInfo()*/
}