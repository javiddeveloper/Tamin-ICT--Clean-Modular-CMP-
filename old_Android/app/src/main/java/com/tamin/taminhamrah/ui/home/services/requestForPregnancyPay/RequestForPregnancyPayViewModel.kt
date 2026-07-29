package com.tamin.taminhamrah.ui.home.services.requestForPregnancyPay

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.remote.models.services.UploadImageResponse
import com.tamin.taminhamrah.data.remote.models.services.request_pregnancy_pay.LatestInsuranceInfoResponse
import com.tamin.taminhamrah.data.remote.models.services.request_pregnancy_pay.PregnancyStatusResponse
import com.tamin.taminhamrah.data.remote.models.services.request_pregnancy_pay.PregnancyTypesResponse
import com.tamin.taminhamrah.data.remote.models.services.request_pregnancy_pay.RequestForPregnancyPayReq
import com.tamin.taminhamrah.data.remote.models.services.request_pregnancy_pay.RequestForPregnancyPayResponse
import com.tamin.taminhamrah.data.repository.LoginRepository
import com.tamin.taminhamrah.data.repository.ServiceRepository
import com.tamin.taminhamrah.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class RequestForPregnancyPayViewModel @Inject constructor(
    private val repository: ServiceRepository,
    val loginRepository: LoginRepository
) : BaseViewModel() {

    val mldCheckGender = MutableLiveData<String>()
    val mldLatestInsuranceInfo = MutableLiveData<LatestInsuranceInfoResponse>()
    val mldSendRequestForPregnancyPay = MutableLiveData<RequestForPregnancyPayResponse>()

    fun getLatestInsuranceInfo() {
        viewModelScope.launch {
            val result = callService {
                repository.getLatestInsuranceInfo()
            }
            result.data?.genderCode?.let {
                commonRepository.setGender(it)
                if (it == "02") {
                    mldLatestInsuranceInfo.postValue(result)
                } else {
                    getGender()
                }
            }
        }
    }

    fun getGender() {
        val resultGender = commonRepository.getGender()
        if (!resultGender.isNullOrBlank()) {
            mldCheckGender.postValue(resultGender!!)
            return
        } else {
            getLatestInsuranceInfo()
        }
    }

    val mldUploadImage = MutableLiveData<UploadImageResponse>()
    fun uploadImage(image: MultipartBody.Part) {
        viewModelScope.launch {
            mldUploadImage.postValue(callService {
                repository.uploadImage(image)
            })
        }
    }


    val mldPregnancyStatus = MutableLiveData<PregnancyStatusResponse>()
    fun getPregnancyStatus() {
        viewModelScope.launch {
            val result = callService { repository.getPregnancyStatus() }
            result.let { mldPregnancyStatus.postValue(it) }
        }
    }

    val mldPregnancyType = MutableLiveData<PregnancyTypesResponse>()
    fun getPregnancyType() {
        viewModelScope.launch {
            mldPregnancyType.postValue(callService { repository.getPregnancyType() })
        }
    }

    fun getRequestType(): ArrayList<MenuModel> {
        val itemList = ArrayList<MenuModel>()
        itemList.add(MenuModel("متقاضی استفاده از کمک هزینه بارداری به مدت 6 ماه هستم.", "1"))
        itemList.add(
            MenuModel(
                " متقاضی استفاده از کمک بارداری بیش از 6 ماه تا 9 ماه می باشم.",
                "2"
            )
        )
        itemList.add(
            MenuModel(
                " زایمان 3قلو و بیشتر بوده و متقاضی استفاده تا یک سال می باشم.",
                "3"
            )
        )
        return itemList
    }

    fun getImageTitleList(): ArrayList<MenuModel> {
        val itemList = ArrayList<MenuModel>()
        itemList.add(MenuModel("تصویر استراحت پزشکی", Constants.MEDICAL_REST_IMAGE_TYPE))
        itemList.add(
            MenuModel(
                "تصویر صفحه اول شناسنامه مادر",
                Constants.FIRST_PAGE_ID_CARD_IMAGE_OF_MOTHER_TYPE
            )
        )
        itemList.add(
            MenuModel(
                "تصویر صفحه دوم شناسنامه مادر",
                Constants.SECOND_PAGE_ID_CARD_OF_MOTHER_IMAGE_TYPE
            )
        )
        itemList.add(
            MenuModel(
                "تصویرحکم استخدامی جهت کارکنان دستگاه های  دولتی و دارای آیین نامه خاص",
                Constants.IMAGE_EMPLOYMENT_ORDER_IMAGE_TYPE
            )
        )

        return itemList
    }

    fun sendRequestForPregnancyPay(requestForPregnancyPayReq: RequestForPregnancyPayReq) {
        viewModelScope.launch {
            mldSendRequestForPregnancyPay.postValue(callService {
                repository.sendRequestForPregnancyPay(
                    requestForPregnancyPayReq
                )
            }
          )
        }
    }

}
