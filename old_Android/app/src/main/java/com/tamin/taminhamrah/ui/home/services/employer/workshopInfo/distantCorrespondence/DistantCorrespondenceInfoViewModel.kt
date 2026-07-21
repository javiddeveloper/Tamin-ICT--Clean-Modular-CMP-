package com.tamin.taminhamrah.ui.home.services.employer.workshopInfo.distantCorrespondence

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.entity.UploadedImageModel
import com.tamin.taminhamrah.data.remote.models.employer.LetterInfo
import com.tamin.taminhamrah.data.remote.models.employer.LetterListResponse
import com.tamin.taminhamrah.data.remote.models.employer.LetterRequestDetailCollection
import com.tamin.taminhamrah.data.remote.models.employer.LetterSubject
import com.tamin.taminhamrah.data.remote.models.employer.RegisterLetterRequest
import com.tamin.taminhamrah.data.remote.models.employer.RegisterLetterResponse
import com.tamin.taminhamrah.data.remote.models.services.GeneralRes
import com.tamin.taminhamrah.data.repository.ServiceRepository
import com.tamin.taminhamrah.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class DistantCorrespondenceInfoViewModel @Inject constructor(
    private val repository: ServiceRepository
) : BaseViewModel() {

    val dataModel by lazy { RegisterLetterRequest() }

    val tempDetailCollection by lazy { LetterRequestDetailCollection() }

    val mldUploadImage = MutableLiveData<UploadedImageModel>()
    val mldDownloadImage = MutableLiveData<UploadedImageModel>()

    val mldRegisterRequest = MutableLiveData<RegisterLetterResponse?>()

    val mldDeleteRequest = MutableLiveData<GeneralRes?>()

    val mldApproveRequest = MutableLiveData<LetterListResponse?>()

    fun getImageTitleFlow() = createLocalPager(getDocTitleList()).flow.cachedIn(viewModelScope)

    fun getDocTitleList() = ArrayList<MenuModel>().apply {
        add(MenuModel("مستندات نامه", "1", tag = 1001))
        add(MenuModel("مستندات مدارک الحاقیه", "3", tag = 1002))
        /*  if (dataModel.SubContractor == "1")
              add(MenuModel("مستندات لیست فهرست", "2", tag = 1004))*/
        add(MenuModel("مستندات صورت وضعیت قطعی", "4", tag = 1003))

    }


    fun uploadImage(
        image: MultipartBody.Part,
        requestCode: Int,
        imageUri: Uri,
        fileName: String
    ) {
        viewModelScope.launch {
            val response = callService {
                repository.uploadImage(image)
            }

            if (response.isSuccess) {
                mldUploadImage.postValue(
                    UploadedImageModel(
                        response.guid,
                        imageType = "",
                        imageName = "",
                        imageUri
                    )
                )
            }
        }
    }


    private var letterSubjectFlow: Pager<Int, LetterSubject>? = null
    fun getLetterTitleList(contractNumber: String? = null): Flow<PagingData<LetterSubject>> {
        val paramsMap: MutableMap<String, String> = mutableMapOf()
        paramsMap["contract"] = contractNumber ?: "null"
        if (letterSubjectFlow == null)
            letterSubjectFlow = createPager(
                repository::getLetterSubject,
                queryPageSize = "50",
                paramsMap = paramsMap
            )

        return letterSubjectFlow!!.flow.cachedIn(viewModelScope)
    }

    fun getMonthsFlow() = createLocalPager(
        ArrayList<MenuModel>().apply {
            add(MenuModel("فروردین", "01"))
            add(MenuModel("اردیبهشت", "02"))
            add(MenuModel("خرداد", "03"))
            add(MenuModel("تیر", "04"))
            add(MenuModel("مرداد", "05"))
            add(MenuModel("شهریور", "06"))
            add(MenuModel("مهر", "07"))
            add(MenuModel("آبان", "08"))
            add(MenuModel("آذر", "09"))
            add(MenuModel("دی", "10"))
            add(MenuModel("بهمن", "11"))
            add(MenuModel("اسفند", "12"))

        }
    ).flow.cachedIn(viewModelScope)

    fun getCertificateTypeFlow() = createLocalPager(
        ArrayList<MenuModel>().apply {
            add(MenuModel("درخواست نقل و انتقال", "1"))
            add(MenuModel("درخواست صدورپروانه", "2"))
            add(MenuModel("درخواست تمدید کارت بازرگانی", "3"))
            add(MenuModel("درخواست پرداخت وام", "4"))
            add(MenuModel("درخواست ترهین ملک", "5"))
        }
    ).flow.cachedIn(viewModelScope)

    fun getShutDownTypeFlow() = createLocalPager(
        ArrayList<MenuModel>().apply {
            add(MenuModel("تعطیلی فاقد فعالیت", "1"))
            add(MenuModel("تعطیلی موضوعی", "2"))
            add(MenuModel("تعطیلی فصلی", "3"))
            add(MenuModel("تخریب کامل", "4"))
            add(MenuModel("تخریب و نوسازی", "5"))
        }
    ).flow.cachedIn(viewModelScope)

    fun getMaintenanceCenterFlow() = createLocalPager(
        ArrayList<MenuModel>().apply {
            add(MenuModel("مربی مهد / کارکنان مرکز", "1"))
            add(MenuModel("بهزیستی و توانبخشی", "2"))
            add(MenuModel("تصویر مستندات", "3"))
        }
    ).flow.cachedIn(viewModelScope)

    fun getContractTemStep2Flow() = createLocalPager(
        ArrayList<MenuModel>().apply {
            add(MenuModel("مقاطعه کار", "1"))
            add(MenuModel("واگذارنده", "2"))
            add(
                MenuModel(
                    "قسمتي از مصالح توسط پيمانکار و قسمتي توسط واگذارنده کار تامين شده است",
                    "3"
                )
            )

        }
    ).flow.cachedIn(viewModelScope)

    fun getIncludingTypeFlow() = createLocalPager(
        ArrayList<MenuModel>().apply {
            add(MenuModel("جانباز", "1"))
            add(MenuModel("جانباز با کسر ساعت", "2"))
            add(MenuModel("جانباز بدون کسر ساعت", "3"))
            add(MenuModel("بیمه شده قراردادی", "4"))
            add(MenuModel("بیمه شده پورسانتاژی", "5"))

        }
    ).flow.cachedIn(viewModelScope)

    fun getExemptionTypeFlow() = createLocalPager(
        ArrayList<MenuModel>().apply {
            add(MenuModel("بازنشسته", "1"))
            add(MenuModel("بیمه شده", "2"))
            add(MenuModel("کارفرما", "3"))
            add(MenuModel("همسر یا فرزند کارفرما", "4"))
            add(MenuModel("راننده آژانس دارای سند", "5"))
            add(MenuModel("طبق رای هیات دولت", "6"))
            add(MenuModel("طبق رای هیات دولت", "7"))
            add(MenuModel("هنرآموز", "8"))
        }
    ).flow.cachedIn(viewModelScope)

    fun getPaymentTypeFlow() = createLocalPager(
        ArrayList<MenuModel>().apply {
            add(MenuModel("پرداخت طبق چک انجام شده", "1"))
            add(MenuModel("سایر", "2"))
        }
    ).flow.cachedIn(viewModelScope)

    fun sendDistantCorrespondenceRequest(workshopCode: String?, branchCode: String?) {

        viewModelScope.launch {
            dataModel.brchCode = branchCode
            dataModel.rwshid = workshopCode
            mldRegisterRequest.postValue(callService {
                repository.sendDistantCorrespondenceRequest(
                    dataModel
                )
            })
        }
    }

    fun getDistantCorrespondenceFlow(
        workshopId: String? = ""
    ): Flow<PagingData<LetterInfo>> {

        val map = HashMap<String, String>()
        if (!workshopId.isNullOrBlank())
            map["workshopId"] = workshopId

        val result = createPager(repository::getDistantCorrespondenceList, paramsMap = map)
        return result.flow.cachedIn(viewModelScope)
    }

    fun getMenuFlow(status: String?) = createLocalPager(
        ArrayList<MenuModel>().apply {
            add(MenuModel("نمایش", "1"))
            if (status == "0001") {
                add(MenuModel("حذف", "2"))
                add(MenuModel("تایید", "3"))
            }
        }
    ).flow.cachedIn(viewModelScope)


    fun deleteExistLetter( letterRequestId: Long?, workshopId: String?) {
        viewModelScope.launch {
            val result = callService {
                repository.deleteExistLetter(letterRequestId, workshopId)
            }
            mldDeleteRequest.postValue(result)
        }
    }

    fun approveLetter( letterRequestId: Long?, workshopId: String?) {
        viewModelScope.launch {
            val result = callService {
                repository.approveDistantLetter(letterRequestId, workshopId)
            }
            mldApproveRequest.postValue(result)
        }
    }

    fun getLetterAttachedImage(  guid: String) {
        viewModelScope.launch {
            val result = callService {
                repository.getLetterAttachedImage(guid)
            }
            if (result.isSuccess){
                mldDownloadImage.postValue(
                    UploadedImageModel(
                        result.detail?.guid,
                        imageType = result.detail?.fileType,
                        imageName = result.detail?.fileName,
                        result.uri
                    )
                )
            }
        }
    }

}