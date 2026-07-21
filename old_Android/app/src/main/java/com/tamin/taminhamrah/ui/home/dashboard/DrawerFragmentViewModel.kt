package com.tamin.taminhamrah.ui.home.dashboard

import androidx.lifecycle.MutableLiveData
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.entity.ProfileModel
import com.tamin.taminhamrah.data.remote.models.Resource
import com.tamin.taminhamrah.data.repository.ServiceRepository
import com.tamin.taminhamrah.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DrawerFragmentViewModel @Inject constructor(private val repository: ServiceRepository) :
    BaseViewModel() {
    fun logOut() {
        commonRepository.logOut()
    }

    fun getItemsList(): ArrayList<MenuModel> {
        val itemList = ArrayList<MenuModel>()
        itemList.add(
            MenuModel(
                "سامانه ارتباطات و نظارت مردمی",
                "1",
                iconRes = R.drawable.ic_my_tamin,
                description = "ارتباط با 1420 سازمان تأمین اجتماعی"
            )
        )

        itemList.add(
            MenuModel(
                "اشتراک گذاری",
                "2",
                iconRes = R.drawable.ic_share,
                description = "تأمین من را به دیگران معرفی کن"
            )
        )

        itemList.add(
            MenuModel(
                "تماس با ما",
                "3",
                iconRes = R.drawable.ic_phone,
                description = "ارتباط با سازمان تأمین اجتماعی"
            )
        )

        itemList.add(
            MenuModel(
                "تاریخچه نسخه",
                "4",
                iconRes = R.drawable.ic_history,
                description = "نسخه فعلی و سوابق نسخه های نرم افزار"
            )
        )

        itemList.add(
            MenuModel(
                "خروج",
                "5",
                iconRes = R.drawable.ic_exit_to_app,
                description = "خروج از تأمین من "
            )
        )
        return itemList

    }

    val mldProfile = MutableLiveData<Resource<ProfileModel?>>()
    fun getProfileInfo() {
        mldProfile.postValue(Resource.success(commonRepository.getUserInfo()))
        /*viewModelScope.launch {
            mldProfile.postValue(Resource.loading(null))
            try {
                val result = loginRepository.getProfileInfo(getRequestUrl())
                mldProfile.postValue(result)

              *//*  if (result.isSuccess) {
                    result.data?.nationalCode?.let { fetchEligibilityFromApi(it) }
                }*//*

            } catch (e: Exception) {
                mldProfile.postValue(Resource.error(MessageModel(e.message ?: e.toString(), 0)))
            }
        }*/
    }
}