package com.tamin.taminhamrah.ui.home.services.orotezProtez

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.tamin.taminhamrah.Constants.DOCTOR_OROTEZ_IMAGE_TYPE
import com.tamin.taminhamrah.Constants.HEARING_AIDS_IMAGE_TYPE
import com.tamin.taminhamrah.Constants.PURCHASE_INVOICE_OROTEZ_IMAGE_TYPE
import com.tamin.taminhamrah.Constants.QUERY_PAGE_SIZE_10
import com.tamin.taminhamrah.Constants.WARRANTY_HEARING_AIDS_IMAGE_TYPE
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.entity.UploadedImageModel
import com.tamin.taminhamrah.data.remote.models.services.ShortTermOrthosisReq
import com.tamin.taminhamrah.data.remote.models.services.ShortTermOrthosisResponse
import com.tamin.taminhamrah.data.remote.models.services.UploadImageResponse
import com.tamin.taminhamrah.data.remote.models.services.orthosisInfoResponse.InsuredOrthosisInfoResponse
import com.tamin.taminhamrah.data.repository.ServiceRepository
import com.tamin.taminhamrah.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class OrotezProtezViewModel @Inject constructor(private val repository: ServiceRepository) :
    BaseViewModel() {

    val mldUserInfo = MutableLiveData<InsuredOrthosisInfoResponse>()
    val mldUploadImage = MutableLiveData<UploadImageResponse>()
    val mldSendOrthosisRequest = MutableLiveData<ShortTermOrthosisResponse>()

    var tempImageUri: Uri? = null
    var tempImageOriginalUri: Uri? = null

    var  tempImageType: String =""
    var  tempImageName: String =""

    var fileListUploaded : ArrayList<UploadedImageModel> = arrayListOf()
    val dataModel : ShortTermOrthosisReq by lazy { ShortTermOrthosisReq() }

    fun getInsuredOrthosisInfo() {
        viewModelScope.launch {
            mldUserInfo.postValue(callService { repository.getInsuredOrthosisInfo() })
        }
    }

    val getDependantsResponse = createPager(
        repository::getDependantsResponse, QUERY_PAGE_SIZE_10.toString()
    ).flow.cachedIn(viewModelScope)


    fun uploadImage(image: MultipartBody.Part) {
        viewModelScope.launch {
            mldUploadImage.postValue(callService { repository.uploadImage(image) })
        }
    }

    fun saveShortTermOrthosis(requestShortTermOrthosis: ShortTermOrthosisReq) {
        viewModelScope.launch {
            mldSendOrthosisRequest.postValue(callService {
                repository.saveShorttermOrthosis(requestShortTermOrthosis)
            })
        }
    }

    fun getImageTitleList(): ArrayList<MenuModel> {
        val itemList = ArrayList<MenuModel>()
        itemList.add(MenuModel("تصویر نسخه تجویز پزشک", DOCTOR_OROTEZ_IMAGE_TYPE))
        itemList.add(MenuModel("تصویر فاکتور خرید", PURCHASE_INVOICE_OROTEZ_IMAGE_TYPE))
        itemList.add(MenuModel("تصویر نوار گوش (مخصوص سمعک)", HEARING_AIDS_IMAGE_TYPE))
        itemList.add(MenuModel("تصویر گارانتی سمعک", WARRANTY_HEARING_AIDS_IMAGE_TYPE))
        return itemList
    }

}