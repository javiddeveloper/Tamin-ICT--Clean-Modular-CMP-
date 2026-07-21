package com.tamin.taminhamrah.ui.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.google.gson.Gson
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.Constants.BASE_URL
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.entity.ProfileModel
import com.tamin.taminhamrah.data.remote.models.BaseStatus
import com.tamin.taminhamrah.data.remote.models.MessageModel
import com.tamin.taminhamrah.data.remote.models.Resource
import com.tamin.taminhamrah.data.remote.models.profile.RelatedPersonInfo
import com.tamin.taminhamrah.data.remote.models.profile.TaminRelationResponse
import com.tamin.taminhamrah.data.remote.models.profile.asDomainModel
import com.tamin.taminhamrah.data.remote.models.profile.getRelationTitle
import com.tamin.taminhamrah.data.remote.models.services.GeneralRes
import com.tamin.taminhamrah.data.remote.models.services.GeneralStringRes
import com.tamin.taminhamrah.data.remote.models.user.EditMobileResponse
import com.tamin.taminhamrah.data.remote.models.user.EligibilityStatusResponse
import com.tamin.taminhamrah.data.remote.models.user.VerifyMobileReq
import com.tamin.taminhamrah.data.repository.LoginRepository
import com.tamin.taminhamrah.enums.EnumUserMode
import com.tamin.taminhamrah.enums.ServiceStatus
import com.tamin.taminhamrah.ui.base.BaseViewModel
import com.tamin.taminhamrah.utils.ConfigApp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val loginRepository: LoginRepository
) : BaseViewModel() {

    val mldEligibilityResult = MutableLiveData<Resource<EligibilityStatusResponse?>>()
    val mldEditProfileResult = MutableLiveData<Resource<Any?>>()
    val mldVerifyMobileCodeeResult = MutableLiveData<Resource<Boolean?>>()
    val mldFetchRelationInfo = MutableLiveData<TaminRelationResponse>()
    val mldImageRequestResult = MutableLiveData<GeneralRes>()
    val mldRelatedList =
        createPager(loginRepository::getRelatedPersons).flow.cachedIn(viewModelScope)
    val mldSignOut = MutableLiveData<GeneralRes>()

    val mldUserAvatar = MutableLiveData<GeneralStringRes?>()
    val mldProfile = MutableLiveData<ProfileModel>()
    val mldProfileStatus = MutableLiveData<ProfileDataState>()
    val mldEditMobileRes = MutableLiveData<EditMobileResponse>()
    val mldVerifyCodeRes = MutableLiveData<GeneralRes>()

    fun getPhoneNumber()=commonRepository.getUserPhoneNumber()

    fun hasUserInfo(): Boolean {
        val userinfo = commonRepository.getUserInfo()
        if (getUserAvatar().isNullOrBlank() || getUserAvatar() == "null" || userinfo.fullName.isNullOrBlank())
            return false
        else {
            val result = GeneralStringRes(getUserAvatar())
            result.baseStatus = BaseStatus(MessageModel(), ServiceStatus.SUCCESS)

            return true
        }
    }

    fun getUserInfo() {

        viewModelScope.launch {
            mldProfileStatus.postValue(ProfileDataState.LOADING)
            if (getUserAvatar().isNullOrBlank() || getUserAvatar() == "null") {

                mldUserAvatar.postValue(callService {
                    val result = loginRepository.getUserProfileImage("")

                    if (result.isSuccess)
                        commonRepository.setUserAvatar(result.data)

                    result
                })

            } else {
                val result = GeneralStringRes(getUserAvatar())
                result.baseStatus = BaseStatus(MessageModel(), ServiceStatus.SUCCESS)
                mldUserAvatar.postValue(result)
            }

            val userinfo = commonRepository.getUserInfo()

            if (userinfo.fullName.isNullOrBlank() && !BASE_URL.contains("172")) {

                val result = loginRepository.getProfileInfo()
                if (result.isSuccess) {
                    commonRepository.setUserInfo(result.asDomainModel())
                    mldProfile.postValue(result.asDomainModel())
                    mldProfileStatus.postValue(ProfileDataState.FETCHED)

                } else
                    mldProfileStatus.postValue(ProfileDataState.ERROR)


            } else {
                mldProfile.postValue(userinfo)
                mldProfileStatus.postValue(ProfileDataState.FETCHED)

            }
        }

    }

    fun fetchEligibilityFromApi(nationalCode: String) {
        if (isConnected()) {
            viewModelScope.launch {
                mldEligibilityResult.postValue(Resource.loading(null))
                try {
                    val result =
                        loginRepository.getEligibilityTreatment(createUrlRequest(nationalCode))
                    manageResponse(result.data)
                } catch (e: Exception) {
                    mldEligibilityResult.postValue(
                        Resource.error(
                            MessageModel(
                                e.message ?: e.toString(), 0
                            )
                        )
                    )
                }
            }
        } else {
            mldEligibilityResult.postValue(Resource.needNetwork())
        }
    }

//    private fun getRequestUrl(): String {
//        return Constants.URL_PROFILE
//    }

    private fun createUrlRequest(nationalCode: String): String {
        return Constants.BASE_URL_MEDICAL +
                ConfigApp.PostfixUrlEligibility +
                nationalCode
    }

    private fun manageResponse(result: Any?) {
        if (result is String) {
            mldEligibilityResult.postValue(Resource.error(result as? MessageModel))
        } else {
            val map = result as? Map<String, Any>
//            map?.let {
            val eligibilityResult =
                Gson().fromJson(result.toString(), EligibilityStatusResponse::class.java)
//
//                val obj = mapToObject(map, EligibilityStatusResponse::class)
            mldEligibilityResult.postValue(Resource.success((eligibilityResult)))
//            }
        }
    }

    /*    fun changeMobile(mobileStr: String) {
            if (isConnected()) {
                viewModelScope.launch {
                    mldEditProfileResult.postValue(Resource.loading(null))
                    try {
                        val result = loginRepository.editMobile(mobileStr)
                        mldEditProfileResult.postValue(result)
                    } catch (e: Exception) {
                        mldEditProfileResult.postValue(
                            Resource.error(
                                MessageModel(
                                    e.message ?: e.toString(), 0
                                )
                            )
                        )
                    }
                }
            } else {
                mldEditProfileResult.postValue(Resource.needNetwork())
            }
        }*/
    fun changeMobile(mobileStr: String) {
        viewModelScope.launch {
            mldEditMobileRes.postValue(callService {
                loginRepository.editMobile(mobileStr)
            })
        }
    }


    fun verifyChangeMobileCode(mobile: String, verifyCode: String) {

        viewModelScope.launch {
            val result = callService {
                loginRepository.verifyChangeMobileCode(
                    VerifyMobileReq(
                        mobile,
                        verifyCode,
                        mldEditMobileRes.value?.data?.hash ?: ""
                    )
                )
            }
            if (result.isSuccess)
                commonRepository.setPhoneNumber(mobile)

            mldVerifyCodeRes.postValue(result)
        }
    }

    /*fun verifyChangeMobileCode(mobile: String, verifyCode: String) {
        if (isConnected()) {
            viewModelScope.launch {
                mldVerifyMobileCodeeResult.postValue(Resource.loading(null))
                try {
                    val result = loginRepository.verifyChangeMobileCode(mobile, verifyCode)
                    mldVerifyMobileCodeeResult.postValue(result)
                } catch (e: Exception) {
                    mldVerifyMobileCodeeResult.postValue(
                        Resource.error(
                            MessageModel(
                                e.message ?: e.toString(), 0
                            )
                        )
                    )
                }
            }
        } else {
            mldVerifyMobileCodeeResult.postValue(Resource.needNetwork())
        }
    }
*/
    fun aaa() {
        viewModelScope.launch {
            val a = mldRelatedList.last()
        }
    }

    fun fetchRelationInfo() {
        viewModelScope.launch {
            mldFetchRelationInfo.postValue(callService {
                loginRepository.fetchTaminRelation()
            })
        }
    }

    fun sendImageRequest(branchCode: String, serialNumberStr: String) {
        viewModelScope.launch {
            mldImageRequestResult.postValue(callService {
                loginRepository.sendImageRequest(branchCode, serialNumberStr)
            })
        }
    }

    fun revokeRefreshToken() {
        viewModelScope.launch {
            commonRepository.revokeRefreshToken()
        }
    }

    fun logOut() {
        viewModelScope.launch {
            commonRepository.logOut()
            val result = callService {
                loginRepository.signOut()
            }
            mldSignOut.postValue(result)
        }
    }


    fun getItemsList(): ArrayList<MenuModel> {
        val itemList = ArrayList<MenuModel>()
        itemList.add(

            MenuModel(
                "اطلاعات هویتی",
                "1",
                iconRes = R.drawable.ic_user,
                description = "نمایش اطلاعات هویتی و شماره تأمین اجتماعی"
            )
        )
        /*  itemList.add(
              MenuModel(
                  "وضعیت حمایت های درمانی",
                  "2",
                  iconRes = R.drawable.ic_first_aid_kit,
                  description = "استعلام استحقاق درمان برای تاریخ روز"
              )
          )*/
        itemList.add(
            MenuModel(
                "ارتباط فعال با تأمین",
                "3",
                iconRes = R.drawable.ic_network,
                description = "وضعیت ارتباط فعال با تأمین اجتماعی"
            )
        )
        if (getUserMode() == EnumUserMode.MODE_INSURED.methodName ||
            getUserMode() == EnumUserMode.MODE_PENSIONER.methodName
        )
            itemList.add(
                MenuModel(
                    "مشاهده و ثبت افراد تبعی",
                    "4",
                    iconRes = R.drawable.ic_relation,
                    description = "مشاهده و ثبت افراد تبعی توسط بیمه شده اصلی",
                    isNew = false
                )
            )
        itemList.add(
            MenuModel(
                "پرونده الکترونیک من",
                "5",
                iconRes = R.drawable.ic_electronic_file,
                description = "مشاهده مدارک ثبت شده در سیستم",
                isNew = false
            )
        )
        itemList.add(
            MenuModel(
                "شماره حساب بانکی",
                "6",
                iconRes = R.drawable.ic_accounting_colorful,
                description = "استعلام و ثبت شماره حساب های بانکی"
            )
        )
        itemList.add(
            MenuModel(
                "ابطال کفالت",
                "7",
                iconRes = R.drawable.ic_history,
                description = "جهت ابطال کفالت افراد تبعی"
            )
        )
        itemList.add(
            MenuModel(
                "تغییر شماره موبایل",
                "8",
                iconRes = R.drawable.ic_edit_mobile,
                description = "جهت شناسایی شما در اپلیکیشن تأمین من"
            )
        )
        /*itemList.add(
            MenuModel(
                "اشتراک گذاری",
                "6",
                iconRes = R.drawable.ic_share,
                description = "تأمین من را به دیگران معرفی کن"
            )
        )*/

        itemList.add(
            MenuModel(
                title = "تنظیمات",
                id = "9",
                iconRes = R.drawable.ic_settings,
                description = "مدیریت ظاهر و امنیت برنامه"
            )
        )
        itemList.add(
            MenuModel(
                "خروج از حساب کاربری",
                "10",
                iconRes = R.drawable.ic_exit_to_app,
                description =null
            )
        )

        return itemList

    }

    fun getRelatedListAsMenuModel(pagingData: PagingData<RelatedPersonInfo>): PagingData<MenuModel> {
        return pagingData.map {

            val id = it.relationWithTamin?.id
            val fullname =
                "${it.relationWithTamin?.personal?.firstName} ${it.relationWithTamin?.personal?.lastName}"
            val relation = getRelationTitle(
                it.relationWithTamin?.relationWithTamin?.baseTendency?.tendencyCode,
                it.relationWithTamin?.personal?.gender?.genderCode
            )
            val nationalCode = it.relationWithTamin?.personal?.nationalId

            MenuModel(
                "$nationalCode - $fullname - $relation",
                it.id.toString() ?: "",
                description = nationalCode
            )
        }
    }

    enum class ProfileDataState(var message: String) {
        LOADING("در حال دریافت اطلاعت هویتی"), ERROR("خطا در دریافت اطلاعات (برای بازیابی کلیک کنید)"), FETCHED(
            ""
        )
    }

}

