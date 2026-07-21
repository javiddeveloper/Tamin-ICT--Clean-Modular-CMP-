package com.tamin.taminhamrah.ui.home.services.occurence

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.tamin.taminhamrah.Constants.QUERY_PAGE_SIZE_10
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.entity.UploadedImageModel
import com.tamin.taminhamrah.data.remote.models.services.GeneralRes
import com.tamin.taminhamrah.data.remote.models.services.UploadImageResponse
import com.tamin.taminhamrah.data.remote.models.services.UserInfoResponse
import com.tamin.taminhamrah.data.remote.models.services.occurrence.AllWorkshopsResponse
import com.tamin.taminhamrah.data.remote.models.services.occurrence.InsuredRelationResponse
import com.tamin.taminhamrah.data.remote.models.services.occurrence.OccurrenceReq
import com.tamin.taminhamrah.data.remote.models.services.occurrence.OccurrenceResponse
import com.tamin.taminhamrah.data.remote.models.services.occurrence.OfficePersonalInfoResponse
import com.tamin.taminhamrah.data.remote.models.services.occurrence.WorkshopSpecificationResponse
import com.tamin.taminhamrah.data.repository.ServiceRepository
import com.tamin.taminhamrah.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class OccurrenceReportViewModel @Inject constructor(private val repository: ServiceRepository) :
    BaseViewModel() {
    val WORKSHOP_INFO_SEPREATOR = "- شعبه"
    val mldUserInfo = MutableLiveData<UserInfoResponse>()
    val mldInsuredRelation = MutableLiveData<InsuredRelationResponse>()
    val mldAllWorkshops = MutableLiveData<AllWorkshopsResponse>()
    val mldAllWorkshopHistory = MutableLiveData<GeneralRes>()
    val mldWorkshopSpecification = MutableLiveData<WorkshopSpecificationResponse>()
    val mldOfficePersonalInfo = MutableLiveData<OfficePersonalInfoResponse>()
    val mldUploadImage = MutableLiveData<UploadImageResponse>()
    val mldResponse = MutableLiveData<OccurrenceResponse>()


    val dataModel : OccurrenceReq by lazy { OccurrenceReq() }

    var tempImageUri: Uri? = null
    var tempImageOriginalUri: Uri? = null
    var tempImageType: String = ""
    var tempImageName: String = ""

    var fileListUploaded: ArrayList<UploadedImageModel> = arrayListOf()


    fun getUserInfo() {
        viewModelScope.launch {
            mldUserInfo.postValue(callService { repository.getUserInfo() })
        }
    }

    fun getInsuredRelation(nationalCode: String?) {
        viewModelScope.launch {
            mldInsuredRelation.postValue(callService { repository.getInsuredRelation(nationalCode) })
        }
    }

    fun getAllWorkshops(nationalCode: String?) {
        viewModelScope.launch {
            mldAllWorkshops.postValue(callService { repository.getAllWorkshops(nationalCode) })
        }
    }

    fun getAllWorkshopHistory(
    ) {
        viewModelScope.launch {
            mldAllWorkshopHistory.postValue(callService {
                repository.getAllWorkshopHistory(
                    dataModel.workshopCode,
                    dataModel.pNationalCode,
                    dataModel.branchCode,
                    dataModel.insuranceID,
                    dataModel.occurrenceDate
                )
            })
        }
    }

    fun getWorkshopAndUserInfo(
        nationalCode: String?,
        birthDate: Long?,
        workshopCode: String?,
        branchCode: String?
    ) {
        viewModelScope.launch {
            val workshopSpecification = async(Dispatchers.IO) {
                callService { repository.getWorkshopSpecification(workshopCode, branchCode) }
            }.await()
            val personalInfo = async(Dispatchers.IO) {
                callService {
                    repository.getOfficePersonalInfo(
                        nationalCode,
                        birthDate,
                        workshopCode,
                        branchCode
                    )
                }
            }.await()

            mldWorkshopSpecification.postValue(workshopSpecification)
            mldOfficePersonalInfo.postValue(personalInfo)
        }
    }

    val marriageFlow = createLocalPager(getMarriage()).flow.cachedIn(viewModelScope)
    private fun getMarriage(): List<MenuModel> {
        val itemList = ArrayList<MenuModel>()
        itemList.add(MenuModel("مجرد", "0"))
        itemList.add(MenuModel("متاهل", "1"))
        return itemList
    }

    val occurrenceResultFlow =
        createLocalPager(getOccurrenceResultList()).flow.cachedIn(viewModelScope)

    private fun getOccurrenceResultList(): List<MenuModel> {
        val itemList = ArrayList<MenuModel>()
        itemList.add(MenuModel("فوت", "0"))
        itemList.add(MenuModel("از کار افتادگی کلی", "1"))
        itemList.add(MenuModel("از کار افتادگی جزئی (درصد کاهش توانایی بین 33% تا 66%)", "2"))
        itemList.add(MenuModel("نقص عضو (درصد کاهش توانایی کمتر از 33%)", "3"))
        itemList.add(MenuModel("درحال استراحت پزشکی", "4"))
        itemList.add(MenuModel("دریافت غرامت پزشکی و بهبودی", "5"))
        itemList.add(MenuModel("هیچکدام", "6"))
        return itemList
    }

    fun getAllWorkshopFlow(list: List<Object>?) = createLocalPager(ArrayList<MenuModel>().apply {
        list?.forEach {
            var workshopString = it.toString().removePrefix("[")
            workshopString = workshopString.removeSuffix("]")
            val workshopInfo = workshopString.split(",")
            add(
                MenuModel(
                    "${workshopInfo.first()}$WORKSHOP_INFO_SEPREATOR${workshopInfo.last()} ",
                    list.indexOf(it).toString()
                )
            )
//            add(MenuModel( it.toString().removePrefix("[").removePrefix("]").replace("null", "- شعبه"),list.indexOf(it).toString()))
        }
    }).flow.cachedIn(viewModelScope)


    fun uploadImage(image: MultipartBody.Part) {
        viewModelScope.launch {
            mldUploadImage.postValue(callService { repository.uploadOccurrenceImage(image) })
        }
    }

    val getDocTypeFlow = createPager(
        repository::getDocumentType, QUERY_PAGE_SIZE_10.toString()
    ).flow.cachedIn(viewModelScope)

    fun sendOccurrenceRequest(request:OccurrenceReq) {
        viewModelScope.launch {
            mldResponse.postValue(callService { repository.sendOccurrenceRequest(request) })
        }
    }





   /* nationalityList = [
    {'value': 1, 'name': 'ایرانی'},
    {'value': 2, 'name': 'غیر ایرانی'},
    ];

    isuTypeList = [
    {'value': 1, 'name': 'نوع اول'},
    {'value': 2, 'name': 'نوع دوم'},
    ];

    maritalStatusList = [
    {'value': 0, 'name': 'مجرد'},
    {'value': 1, 'name': 'متاهل'},
    ];*/

}