package com.tamin.taminhamrah.ui.home.services.employer.registration

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.entity.UploadedImageModel
import com.tamin.taminhamrah.data.remote.models.employer.ConfirmUserResponse
import com.tamin.taminhamrah.data.remote.models.employer.DocumentFile
import com.tamin.taminhamrah.data.remote.models.employer.InsuredDoc
import com.tamin.taminhamrah.data.remote.models.employer.InsuredDocsResponse
import com.tamin.taminhamrah.data.remote.models.employer.NewInsuredSummaryResponse
import com.tamin.taminhamrah.data.remote.models.employer.NewInsuredUserInfoReq
import com.tamin.taminhamrah.data.remote.models.employer.NewInsuredUserInfoResponse
import com.tamin.taminhamrah.data.remote.models.employer.NewInsuredUserStatusResponse
import com.tamin.taminhamrah.data.remote.models.employer.Personal
import com.tamin.taminhamrah.data.remote.models.pdfFile.PdfDownloadResponse
import com.tamin.taminhamrah.data.remote.models.services.CityModel
import com.tamin.taminhamrah.data.remote.models.services.GeneralRes
import com.tamin.taminhamrah.data.remote.models.services.UploadImageResponse
import com.tamin.taminhamrah.data.remote.models.services.insuredInspectionPerformed.submit.JobTitleModel
import com.tamin.taminhamrah.data.remote.models.services.workshop.WorkshopNewMember
import com.tamin.taminhamrah.data.repository.ServiceRepository
import com.tamin.taminhamrah.enums.LoadingState
import com.tamin.taminhamrah.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class InsuredRegistrationViewModel @Inject constructor(private val repository: ServiceRepository) :
    BaseViewModel() {

    val ARG_BRANCH_CODE = "branchCode"
    val ARG_ORGANIZATION_ID = "ARG_ORGANIZATION_ID"
    val ARG_WORKSHOP_ID = "workshopId"
    val ARG_REQUEST_STATUS = "ARG_DEBT_ID"
    val ARG_NATIONAL_CODE = "ARG_NATIONAL_CODE"


    val mldUploadImage = MutableLiveData<UploadImageResponse>()
    val mldUserIsNew = MutableLiveData<NewInsuredUserStatusResponse>()
    val mldSummary = MutableLiveData<NewInsuredSummaryResponse>()
    val mldDeleteUser = MutableLiveData<GeneralRes>()
    val mldConfirmedUser = MutableLiveData<ConfirmUserResponse>()

    //user local info before send data to server | data for edit
    var tempUserInfoModel: NewInsuredUserInfoReq = NewInsuredUserInfoReq()

    val mldRecentlyAddedMemberInfo = MutableLiveData<WorkshopNewMember>()
    val mldInsuredUserInfo = MutableLiveData<NewInsuredUserInfoResponse>()
    val mldInsuredDocs = MutableLiveData<InsuredDocsResponse>()
    val mldIPutInsuredDocs = MutableLiveData<GeneralRes>()

    fun deleteRecentlyAddedUser(personalId: Long?) {
        viewModelScope.launch {
            val result = callService { repository.deleteRecentlyAddedUser(personalId) }
            mldDeleteUser.postValue(result)
        }
    }

    fun confirmRecentlyAddedUser(requestId: Long?) {
        viewModelScope.launch {
            val result = callService { repository.confirmRecentlyAddedUser(requestId) }
            mldConfirmedUser.postValue(result)
        }
    }

    fun getSummary(requestId: Long?) {
        viewModelScope.launch {
            val result = callService { repository.getRequestSummary(requestId) }
            mldSummary.postValue(result)
        }
    }

    fun checkUserIsNew(nationalId: String?) {
        viewModelScope.launch {
            val result = callService { repository.checkUserIsNew(nationalId) }
            mldUserIsNew.postValue(result)
        }
    }

    fun postNewInsuredInfo() {
        viewModelScope.launch {
            val result = callService { repository.postNewInsuredInfo(tempUserInfoModel) }
            mldInsuredUserInfo.postValue(result)
        }
    }

    fun updateNewInsuredInfo(requestId: Long?) {
        viewModelScope.launch {
            val result =
                callService { repository.updateNewInsuredInfo(requestId, tempUserInfoModel) }
            mldInsuredUserInfo.postValue(result)
        }
    }

    fun getInsuredRegistrationDocList(personalId: Long?) {
        viewModelScope.launch {
            val result = callService { repository.getInsuredRegistrationDocList(personalId) }
            mldInsuredDocs.postValue(result)
        }
    }

    fun putInsuredRegistrationDocList(
        personalId: Long?,
        imageFileList: ArrayList<UploadedImageModel>?
    ) {

        val list = ArrayList<InsuredDoc>()
        imageFileList?.forEach {
            list.add(
                InsuredDoc(
                    documentFile = DocumentFile(id = it.guid),
                    documentType = it.imageType,
                    personal = Personal(id = personalId)
                )
            )
        }

        viewModelScope.launch {
            val result = callService {
                repository.putInsuredRegistrationDocList(
                    personalId.toString(),
                    list
                )
            }
            mldIPutInsuredDocs.postValue(result)
        }

    }

    fun getCityListFlow(
        cityName: String? = ""
    ): Flow<PagingData<CityModel>> {
        val paramsMap: MutableMap<String, String> = mutableMapOf()
        if (!cityName.isNullOrEmpty())
            paramsMap["cityName"] = cityName

        val result = createPager(repository::getCityList, paramsMap = paramsMap)
        return result.flow.cachedIn(viewModelScope)
    }

    fun getWorkshopRecentlyAddedMembers(
        workshopId: String? = "",
        organizationId: String? = "",
        nationalCode: String?,
        statuesType: String?,
    ): Flow<PagingData<WorkshopNewMember>> {

        val map = HashMap<String, String>()
        if (!workshopId.isNullOrBlank())
            map["workshopId"] = workshopId

        if (!organizationId.isNullOrBlank())
            map["organizationId"] = organizationId

        if (!nationalCode.isNullOrBlank())
            map["personal.nationalId"] = nationalCode

        if (!statuesType.isNullOrBlank())
            map["personal.request.status.requestCode"] = statuesType

        val result = createPager(repository::getWorkshopRecentlyAddedMembers, paramsMap = map)
        return result.flow.cachedIn(viewModelScope)
    }

    fun getStatusList(): ArrayList<MenuModel> {
        val itemList = ArrayList<MenuModel>()

        itemList.add(MenuModel("در انتظار تایید", "0000"))
        itemList.add(MenuModel(" ثبت درخواست", "0004"))
        itemList.add(MenuModel("درخواست نامعتبر", "0006"))
        itemList.add(MenuModel("درحال بررسی-نیاز به رسیدگی شعبه", "0017"))
        itemList.add(MenuModel("درحال بررسی", "0005"))
        itemList.add(MenuModel("مختومه-تایید نهایی", "0018"))
        itemList.add(MenuModel("مختومه-عدم تایید", "0019"))

        return itemList
    }

    fun getRecentlyAddedUser(personalRequestId: Long?) {
        viewModelScope.launch {
            val userInfo = callService { repository.getRecentlyAddedUser(personalRequestId) }
            if (userInfo.isSuccess && userInfo.data?.list?.isNotEmpty() == true) {
                userInfo.data?.list?.get(0)?.let {

                    mldLoadingState.postValue(LoadingState.LOADING)

                    val selectedCityOfBorn = async(Dispatchers.IO) {
                        val map = HashMap<String, String>()
                        map["cityCode"] = it.personal?.cityOfBirthId!!
                        repository.getCityName(map)
                    }
                    val selectedCityOfIssue = async(Dispatchers.IO) {
                        val map = HashMap<String, String>()
                        map["cityCode"] = it.personal?.cityOfIssueId!!
                        repository.getCityName(map)
                    }
                    val selectedJob = async(Dispatchers.IO) {
                        val map = HashMap<String, String>()
                        map["jobCode"] = it.job!!
                        repository.getJobsTitle(map)
                    }

                    val selectedCityOfBornRes = selectedCityOfBorn.await()
                    val selectedCityOfIssueRes = selectedCityOfIssue.await()
                    val selectedJobRes = selectedJob.await()

                    mldLoadingState.postValue(LoadingState.NOT_LOADING)

                    if (!selectedCityOfBornRes.isSuccess)
                        mldErrorState.postValue(selectedCityOfBornRes)
                    else if (!selectedCityOfIssueRes.isSuccess)
                        mldErrorState.postValue(selectedCityOfIssueRes)
                    else if (!selectedJobRes.isSuccess)
                        mldErrorState.postValue(selectedJobRes)
                    else {

                        tempUserInfoModel = it.asDomainModel()
                        tempUserInfoModel.selectedCityOfBirth =
                            selectedCityOfBornRes.data?.list?.get(0)?.cityName
                        tempUserInfoModel.selectedCityOfIssue =
                            selectedCityOfIssueRes.data?.list?.get(0)?.cityName
                        tempUserInfoModel.selectedjob =
                            selectedJobRes.data?.list?.get(0)?.jobDescription

                        selectedCityOfBornRes.data?.list?.get(0)?.cityName
                        selectedCityOfBornRes.data?.list?.get(0)?.cityName
                        selectedJobRes.data?.list?.get(0)?.jobDescription

                        getInsuredRegistrationDocList(tempUserInfoModel.personal.id)

                        mldRecentlyAddedMemberInfo.postValue(userInfo.data?.list?.get(0))
                    }
                }
            }
        }
    }

    fun getJob(jobTitle: String? = null): Flow<PagingData<JobTitleModel>> {
        val array = JsonArray()
        val paramsMap: MutableMap<String, String> = mutableMapOf()
        val jsonObj = JsonObject()
        if (jobTitle != null) {
            jsonObj.addProperty("property", "jobDescription")
            jsonObj.addProperty("value", "*$jobTitle*")
            jsonObj.addProperty("operator", "LIKE")
            array.add(jsonObj)
        } else {
            jsonObj.addProperty("property", "jobDescription")
            jsonObj.addProperty("value", "*")
            jsonObj.addProperty("operator", "LIKE")
            array.add(jsonObj)
        }
        paramsMap[Constants.ARRAY_KEY_FOR_MAP] = array.toString()
        val result = createPager(repository::getJobsTitle, paramsMap = paramsMap)
        return result.flow.cachedIn(viewModelScope)
    }

    fun uploadImage(image: MultipartBody.Part) {
        viewModelScope.launch {
            mldUploadImage.postValue(callService {
                repository.uploadImage(image)
            })
        }
    }

    fun getImageTitleList(): ArrayList<MenuModel> {
        val itemList = ArrayList<MenuModel>()
        itemList.add(MenuModel("تصویر پرسنلی", "01"))
        itemList.add(MenuModel("تصویر اول اظهارنامه", "02"))
        itemList.add(MenuModel("تصویر دوم اظهارنامه", "08"))
        itemList.add(MenuModel("تصویر صفحه اول شناسنامه", "03"))
        itemList.add(MenuModel("تصویر صفحه دوم شناسنامه", "04"))
        itemList.add(MenuModel("تصویر توضیحات شناسنامه", "07"))
        itemList.add(MenuModel("تصویر روی کارت ملی", "05"))
        itemList.add(MenuModel("تصویر پشت کارت ملی", "06"))

        return itemList
    }

    val mldPDfDownload = MutableLiveData<PdfDownloadResponse>()

    fun getRegistrationDeclarationForm() {

        viewModelScope.launch {

            mldPDfDownload.postValue(callService {
                repository.getRegistrationDeclarationForm()
            })
        }
    }

}